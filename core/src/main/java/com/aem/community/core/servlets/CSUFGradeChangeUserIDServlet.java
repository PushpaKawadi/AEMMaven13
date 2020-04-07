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
		"sling.servlet.paths=" + "/bin/getGCLoggedinUserID" })
public class CSUFGradeChangeUserIDServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFGradeChangeUserIDServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String instUserId = "";
		String termDesc = "";

		JSONArray gradChangeDetails = null;
		if (req.getParameter("instUserID") != null
				&& !req.getParameter("instUserID").trim().equals("")) {
			instUserId = req.getParameter("instUserID");
			termDesc = req.getParameter("termDesc");
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				gradChangeDetails = getGradeChangeUserDetails(instUserId, termDesc, conn);
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
	public static JSONArray getGradeChangeUserDetails(String instUserId, String termDesc,
			Connection conn) throws Exception {

		logger.info("Inside getGradeChnageDetails=" + instUserId);

		ResultSet oRresultSet = null;
		JSONObject instInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String studentCourseInfoSQL = "";
		Statement oStatement = null;
		try {
			studentCourseInfoSQL = ConfigManager
					.getValue("gradeChangeLoggedIn");

			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<instr_userid>>", instUserId);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<TERM_DESCR>>", termDesc);
			logger.info("Grade change sql=" + studentCourseInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);

			while (oRresultSet.next()) {
				instInfo = new JSONObject();
				// String crName = null;
				// String clsNbr = null;
				// String result = null;

				instInfo.put("instCwid", oRresultSet.getString("INSTR_CWID"));
				// instInfo.put("class_nbr",
				// oRresultSet.getString("CLASS_NBR"));
				// instInfo.put("crse_name",
				// oRresultSet.getString("CRSE_NAME"));
				instInfo.put("instr_name", oRresultSet.getString("INSTR_NAME"));
				// instInfo.put("inst_userId",
				// oRresultSet.getString("INSTR_USERID"));
				//
				// crName = oRresultSet.getString("CRSE_NAME");
				// clsNbr = oRresultSet.getString("CLASS_NBR");
				// result = clsNbr.concat(" - ").concat(crName);

				// instInfo.put("clsCrs", result);
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
