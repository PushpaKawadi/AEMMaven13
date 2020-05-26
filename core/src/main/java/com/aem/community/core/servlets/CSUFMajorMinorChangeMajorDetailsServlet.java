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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Major & Minor Change Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/getMajorsDetails" })
public class CSUFMajorMinorChangeMajorDetailsServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFMajorMinorChangeMajorDetailsServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userId = "";
		String acadProg = "";
		String acadProgType = "";

		JSONArray majorMinorChangeDetails = null;
		if (req.getParameter("userID") != null && !req.getParameter("userID").trim().equals("")) {
			userId = req.getParameter("userID");
			
			acadProg = req.getParameter("acadProg");
			acadProgType = req.getParameter("acadProgType");

			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				majorMinorChangeDetails = getStudentInformationDetails(userId, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			if (majorMinorChangeDetails != null && !majorMinorChangeDetails.equals("")) {
				response.getWriter().write(majorMinorChangeDetails.toString());
			} else {
				logger.info("Data not available");
				response.getWriter().write("Requested Data Unavailable");
			}
		} else {
			logger.info("Could Not Connect DB");
		}
	}

	/**
	 * Executes SQL based on the user id and retrieves lookup information. Used in
	 * Self lookup FEB forms
	 * 
	 * @param cwid        - CWID of employee
	 * @param oConnection - Database connection
	 * @return - JSONObject of key value pairs consisting of the lookup data
	 * @throws Exception
	 */
	public static JSONArray getStudentInformationDetails(String userId, Connection conn) throws Exception {

		logger.info("Inside getMajorMinorChangeDetails=" + userId);

		ResultSet oRresultSet = null;
		
		JSONObject instInfo = new JSONObject();
		
		JSONArray jArray = new JSONArray();
		
		String majorMinorChangeInfoSQL = "";		
		Statement oStatement = null;
		
		try {

			majorMinorChangeInfoSQL = CSUFConstants.getMajorsDetails;	
			majorMinorChangeInfoSQL = majorMinorChangeInfoSQL.replaceAll("<<getUser_ID>>", userId);

			logger.info("MajorMinor change sql=" + majorMinorChangeInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(majorMinorChangeInfoSQL);			

			while (oRresultSet.next()) {
				instInfo = new JSONObject();

				instInfo.put("ACAD_CAREER", oRresultSet.getString("ACAD_CAREER"));
				instInfo.put("ACAD_PLAN", oRresultSet.getString("ACAD_PLAN"));
				instInfo.put("ACAD_SUB_PLAN", oRresultSet.getString("ACAD_SUB_PLAN"));
				instInfo.put("PLAN_SEQUENCE", oRresultSet.getString("PLAN_SEQUENCE"));
				instInfo.put("REQ_TERM", oRresultSet.getString("REQ_TERM"));
				instInfo.put("DESCR", oRresultSet.getString("DESCR"));
				instInfo.put("DESCRSHORT", oRresultSet.getString("DESCRSHORT"));
				instInfo.put("ACAD_PROG", oRresultSet.getString("ACAD_PROG"));
				instInfo.put("DEGREE", oRresultSet.getString("DEGREE"));
				instInfo.put("ACAD_PLAN_TYPE", oRresultSet.getString("ACAD_PLAN_TYPE"));
				instInfo.put("ACAD_SUBPLAN_TYPE", oRresultSet.getString("ACAD_SUBPLAN_TYPE"));
				instInfo.put("DIPLOMA_DESCR", oRresultSet.getString("DIPLOMA_DESCR"));
				instInfo.put("CONCENTRATION", oRresultSet.getString("CONCENTRATION"));
				instInfo.put("EMP_DESCR", oRresultSet.getString("EMP_DESCR"));
				instInfo.put("ACAD_ORG", oRresultSet.getString("ACAD_ORG"));
				instInfo.put("DEPT_DESCR", oRresultSet.getString("DEPT_DESCR"));
				instInfo.put("DEPT_DESCRSHORT", oRresultSet.getString("DEPT_DESCRSHORT"));
				instInfo.put("PLAN_RANK", oRresultSet.getString("PLAN_RANK"));
//				instInfo.put("DEPTID", oRresultSet.getString("DEPTID"));
//				instInfo.put("DEPTNAME", oRresultSet.getString("DEPTNAME"));
//				instInfo.put("FUL_COLLEGE", oRresultSet.getString("FUL_COLLEGE"));
//				instInfo.put("FUL_COLLEGE_NAME", oRresultSet.getString("FUL_COLLEGE_NAME"));
//				instInfo.put("CHAIR_EMPLID", oRresultSet.getString("CHAIR_EMPLID"));
//				instInfo.put("CHAIR_EMPNAME", oRresultSet.getString("CHAIR_EMPNAME"));
//				instInfo.put("CHAIR_USERID", oRresultSet.getString("CHAIR_USERID"));
//				instInfo.put("CHAIR_EMAIL", oRresultSet.getString("CHAIR_EMAIL"));

				String currentMajorInfo = getCurrentMajorDetails(userId, conn);
				instInfo.put("Current_Major", currentMajorInfo);				

				jArray.put(instInfo);
			}
			logger.info("Jarray value is==" + jArray);

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

	public static String getCurrentMajorDetails(String userId, Connection conn) throws Exception {

		logger.info("Inside getCurrentMajorDetails=" + userId);

		ResultSet oRresultSet = null;
		Statement oStatement = null;
		String diplomaDescr = null;
		String majorMinorChangeInfoSQL = "";

		try {

			majorMinorChangeInfoSQL = CSUFConstants.getCurrentMajorDetails;
			majorMinorChangeInfoSQL = majorMinorChangeInfoSQL.replaceAll("<<getUser_ID>>", userId);
			logger.info("MajorMinor change Major List sql=" + majorMinorChangeInfoSQL);
			
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(majorMinorChangeInfoSQL);

			while (oRresultSet.next()) {
				diplomaDescr = oRresultSet.getString("DIPLOMA_DESCR");
			}

		} catch (Exception oEx) {

		}
		logger.info("Returned Result =" + diplomaDescr);
		return diplomaDescr;

	}	
}
