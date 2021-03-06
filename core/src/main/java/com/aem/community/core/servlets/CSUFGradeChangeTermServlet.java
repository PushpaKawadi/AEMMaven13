package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.JDBCConnectionHelperService;
import com.aem.community.util.CSUFConstants;
import com.aem.community.util.ConfigManager;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Grade Change Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/getGCTermServlet" })
public class CSUFGradeChangeTermServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFGradeChangeTermServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String instCwid = "";
		String termDesc = "";

		JSONArray gradChangeDetails = null;
		if (req.getParameter("instCwid") != null
				&& !req.getParameter("instCwid").trim().equals("")) {
			instCwid = req.getParameter("instCwid");
			termDesc = req.getParameter("termDesc");
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				gradChangeDetails = getGradeChangeTermDetails(instCwid,
						termDesc, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			if (gradChangeDetails != null && !gradChangeDetails.equals("")) {
				response.getWriter().write(gradChangeDetails.toString());
			} else {
				logger.info("Data not available");
				response.getWriter().write("Requested Data Unavailable");
			}
		} else {
			logger.info("Could Not Connect DB");
		}
	}

	/**
	 * Executes SQL based on the user id and retrieves lookup information. Used
	 * in Self lookup FEB forms
	 * 
	 * @param cwid
	 *            - CWID of employee
	 * @param oConnection
	 *            - Database connection
	 * @return - JSONObject of key value pairs consisting of the lookup data
	 * @throws Exception
	 */
	public static JSONArray getGradeChangeTermDetails(String instCwid,
			String termDesc, Connection conn) throws Exception {

		logger.info("Inside getGradeChnageDetails=" + instCwid);

		ResultSet oRresultSet = null;
		JSONObject instInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String studentCourseInfoSQL = "";
		Statement oStatement = null;
		try {
			//studentCourseInfoSQL = ConfigManager.getValue("gradeChangeTerm");
			studentCourseInfoSQL = CSUFConstants.gradeChangeTerm;

			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<INSTR_CWID>>", instCwid);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<TERM_DESCR>>", termDesc);

			logger.info("Grade change sql=" + studentCourseInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);
			
			while (oRresultSet.next()) {
				instInfo = new JSONObject();
				String crName = null;
				String clsNbr = null;
				String clsSection = null;
				String clsCrsResult = null;
				String clsCrsSecResult = null;

				instInfo.put("crse_name", oRresultSet.getString("CRSE_NAME"));
				instInfo.put("class_nbr", oRresultSet.getString("CLASS_NBR"));
				instInfo.put("instCwid", oRresultSet.getString("INSTR_CWID"));
				instInfo.put("inst_userId",	oRresultSet.getString("INSTR_USERID"));
				instInfo.put("class_section", oRresultSet.getString("CLASS_SECTION"));
				instInfo.put("course_level", oRresultSet.getString("COURSE_LEVEL"));
				instInfo.put("instr_name", oRresultSet.getString("INSTR_NAME"));
				instInfo.put("department_code", oRresultSet.getString("DEPT_CD"));

				crName = oRresultSet.getString("CRSE_NAME");
				clsNbr = oRresultSet.getString("CLASS_NBR");
				clsSection = oRresultSet.getString("CLASS_SECTION");
				clsCrsResult = clsNbr.concat(" - ").concat(crName);
				clsCrsSecResult = clsNbr.concat(" - ").concat(crName).concat(" - ").concat(clsSection);
				
				instInfo.put("clsCrs", clsCrsResult);
				instInfo.put("clsCrsSec", clsCrsSecResult);
				jArray.put(instInfo);
			}

		} catch (Exception oEx) {
			instInfo = null;

		} finally {
			try {
				if (oStatement != null)
					oStatement.close();
				oRresultSet.close();
				if (conn != null) {
					conn.close();
				}
			} catch (Exception exp) {
				exp.getStackTrace();

			}
		}
		return jArray;
	}
}
