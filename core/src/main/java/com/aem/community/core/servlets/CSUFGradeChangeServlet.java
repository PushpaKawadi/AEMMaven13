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
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;

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
		"sling.servlet.paths=" + "/bin/getGradeChangeDetails" })
public class CSUFGradeChangeServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFGradeChangeServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String cwid = "";
		String instCwid = "";
		String courseName= "";
		String termDesc = "";
		String sectionNo = "";
		String classNo = "";

		JSONArray gradChangeDetails = null;
		if (req.getParameter("instCwid") != null
				&& !req.getParameter("instCwid").trim().equals("")) {
			//strm = req.getParameter("strm");
			//courseId = req.getParameter("courseId");
			//instUserId = req.getParameter("instUserid");
			
			termDesc = req.getParameter("termDesc");
			courseName = req.getParameter("courseName");
			instCwid = req.getParameter("instCwid");
			sectionNo = req.getParameter("classSection");
			classNo = req.getParameter("classNumber");
			cwid = req.getParameter("cwid");
			
			logger.error("termDesc="+termDesc);
			logger.error("courseName="+courseName);
			logger.error("instCwid="+instCwid);
			logger.error("sectionNo="+sectionNo);
			logger.error("classNo="+classNo);
			logger.error("cwid="+cwid);
			
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				gradChangeDetails = getGradeChnageDetails(termDesc, courseName,
						sectionNo, classNo, instCwid, cwid, conn);
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
	public static JSONArray getGradeChnageDetails(String termDesc, String courseName,
			String sectionNo, String classNo, String instCwid, String cwid,
			Connection conn) throws Exception {

		logger.info("Inside getGradeChnageDetails=" + cwid);
		
		ResultSet oRresultSet = null;
		JSONObject studentInfo;
		JSONArray jArray = new JSONArray();
		String studentCourseInfoSQL = "";
		Statement oStatement = null;
		try {
			if (!cwid.equals("null") && !cwid.equals("")) {
//				studentCourseInfoSQL = ConfigManager
//						.getValue("gradeChangeSingleStudent");
				studentCourseInfoSQL = CSUFConstants.gradeChangeSingleStudent;
				studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
						"<<cwid>>", cwid);
			} else {
//				studentCourseInfoSQL = ConfigManager
//						.getValue("gradeChangeBulk");
				studentCourseInfoSQL =CSUFConstants.gradeChangeBulk;
			}

			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll("<<TERM_DESCR>>",
					termDesc);
//			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
//					"<<courseId>>", courseName);
//			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
//					"<<classNo>>", classNo);
//			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
//					"<<sectionNo>>", sectionNo);
//			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
//					"<<instUserId>>", instCwid);
			
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<CRSE_NAME>>", courseName);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<classNo>>", classNo);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<sectionNo>>", sectionNo);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll(
					"<<instCwid>>", instCwid);

			//String lookupFields = ConfigManager.getValue("gradeChangeFields");
			String lookupFields = CSUFConstants.gradeChangeFields;
			String[] fields = lookupFields.split(",");
			logger.info("Grade change sql=" + studentCourseInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);
			while (oRresultSet.next()) {
				studentInfo = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					studentInfo
							.put(fields[i], oRresultSet.getString(fields[i]));
				}
				jArray.put(studentInfo);
			}
			logger.info("studentInfo=" + jArray);
		} catch (Exception oEx) {
			studentInfo = null;

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
