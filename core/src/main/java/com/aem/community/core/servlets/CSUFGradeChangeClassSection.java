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
		"sling.servlet.paths=" + "/bin/getGCClassSection" })
public class CSUFGradeChangeClassSection extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFGradeChangeClassSection.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String classNBR = "";
		String instCwid = "";
		String termDesc = "";
		
		JSONArray gradChangeDetails = null;
		if (req.getParameter("instCwid") != null && !req.getParameter("instCwid").trim().equals("") && req.getParameter("termDesc") != null 
				&& req.getParameter("classNumber") != null) {
			instCwid = req.getParameter("instCwid");
			termDesc = req.getParameter("termDesc");
			classNBR = req.getParameter("classNumber");

			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				gradChangeDetails = getGradeChangeClassNumber(instCwid, termDesc, classNBR, conn);
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
	public static JSONArray getGradeChangeClassNumber(String instCwid, String termDesc, String classNBR, Connection conn) throws Exception {

		logger.info("Inside getGradeChangeDetails & Class NBR value is=" + classNBR);

		ResultSet oRresultSet = null;
		JSONObject instInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String studentCourseInfoSQL = "";
		Statement oStatement = null;
		try {

			studentCourseInfoSQL = CSUFConstants.gradeChangeClassSection;
			
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<INSTR_CWID>>", instCwid);
			
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<TERM_DESCR>>", termDesc);

			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<CLASS_NBR>>", classNBR);

			
			logger.info("Grade change sql=" + studentCourseInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);
			while (oRresultSet.next()) {
				instInfo = new JSONObject();		
				
				instInfo.put("classSection", oRresultSet.getString("CLASS_SECTION"));
				instInfo.put("className", oRresultSet.getString("CRSE_NAME"));
				
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
