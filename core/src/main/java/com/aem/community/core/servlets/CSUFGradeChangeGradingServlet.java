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
import org.json.JSONException;
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
		"sling.servlet.paths=" + "/bin/getGCGradingServlet" })
public class CSUFGradeChangeGradingServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFGradeChangeGradingServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String courseId = "";
		String gradingBasis = "";
		String gradingScheme = "";

		JSONArray gradChangeDetails = null;
		if (req.getParameter("courseId") != null
				&& !req.getParameter("courseId").trim().equals("")) {
			// classNo = req.getParameter("classNbr");
			courseId = req.getParameter("courseId");
			gradingBasis = req.getParameter("gradingBasis");
			gradingScheme = req.getParameter("gradingScheme");
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				gradChangeDetails = getGradeChangeGrading(courseId,
						gradingBasis, gradingScheme, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			if (gradChangeDetails != null && !gradChangeDetails.equals("")) {
				logger.info("Grading To Array Lenght ="
						+ gradChangeDetails.length());
				JSONObject gradingInfo = new JSONObject();
				JSONArray gradingArray = new JSONArray();
				if (gradChangeDetails.length() == 0) {
					try {
						gradingArray.put("WU");
						gradingArray.put("D+");
						gradingArray.put("A+");
						gradingArray.put("D-");
						gradingArray.put("F");
						gradingArray.put("A");
						gradingArray.put("WE");
						gradingArray.put("RD");
						gradingArray.put("A-");
						gradingArray.put("C");
						gradingArray.put("C-");
						gradingArray.put("B+");
						gradingArray.put("B");
						gradingArray.put("IC");
						gradingArray.put("RP");
						gradingArray.put("D");
						gradingArray.put("W");
						gradingArray.put("B-");
						gradingArray.put("I");
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						gradingInfo.put("Grade To", gradingArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					response.getWriter().write(gradingInfo.toString());
				} else {
					response.getWriter().write(gradChangeDetails.toString());
				}
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
	public static JSONArray getGradeChangeGrading(String courseId,
			String gradingBasis, String gradingScheme, Connection conn)
			throws Exception {

		logger.info("Inside getGradeChangeGrading=" + courseId);

		ResultSet oRresultSet = null;
		JSONObject instInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String studentCourseInfoSQL = "";
		Statement oStatement = null;
		try {
//			studentCourseInfoSQL = ConfigManager
//					.getValue("gradeChangeToDetails");
			studentCourseInfoSQL = CSUFConstants.gradeChangeToDetails;

			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<CRSE_ID>>", courseId);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<GRADING_BASIS>>", gradingBasis);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<GRADING_SCHEME>>", gradingScheme);

			logger.info("Grade change TO sql=" + studentCourseInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);
			while (oRresultSet.next()) {
				instInfo = new JSONObject();
				instInfo.put("CRSE_GRADE_INPUT",
						oRresultSet.getString("CRSE_GRADE_INPUT"));
				instInfo.put("DESCR", oRresultSet.getString("DESCR"));
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
