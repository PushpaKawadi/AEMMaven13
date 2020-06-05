package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		"sling.servlet.paths=" + "/bin/getMinorAndSignatureServlet" })
public class CSUFMajorMinorChangeMinorAndSignatureServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFMajorMinorChangeMinorAndSignatureServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String userId = "";
		String acadProg = "";
		String acadPlanType ="";
		String minor = "";
		
		logger.info("Inside doGET Method");
		
		JSONArray minorDetails = null;
		if ((req.getParameter("AcadProg") != null && !req.getParameter("AcadProg").trim().equals(""))) {
			userId = req.getParameter("userID"); 			
			acadProg = req.getParameter("AcadProg");			
			acadPlanType = req.getParameter("AcadPlanType");			
			minor = req.getParameter("Minor");	
			
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				minorDetails = getminorDetails(userId, acadProg, acadPlanType, minor, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			if (minorDetails != null && !minorDetails.equals("")) {
				response.getWriter().write(minorDetails.toString());
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
	public static JSONArray getminorDetails(String userId, String acadProg, String acadPlanType, String minor,
			Connection conn) throws Exception {

		logger.info("Inside getGradeChangeDetails");			

		ResultSet oRresultSet = null;
		JSONObject minorInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String minorInfoSQL = "";
		Statement oStatement = null;
		try {			
				if(minor != null && !minor.equals("")) {
					minorInfoSQL = CSUFConstants.getMinorSignature;						

					minorInfoSQL = minorInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);								
					minorInfoSQL = minorInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);																	
					minorInfoSQL = minorInfoSQL.replaceAll("<<DIPLOMA_DESCR>>", minor);
					logger.info("SQL for get Minor Signature = " + minorInfoSQL);
					
				}else if(userId != null && !userId.equals("")) {
					
					minorInfoSQL = CSUFConstants.getCurrentMinors;					
					
					minorInfoSQL = minorInfoSQL.replaceAll("<<getUser_ID>>", userId);
					minorInfoSQL = minorInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);					
					minorInfoSQL = minorInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);	
					logger.info("SQL for get Current Minor = " + minorInfoSQL);
					
				}else {
					
					minorInfoSQL = CSUFConstants.getAllMinors;				
										
					minorInfoSQL = minorInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);					
					minorInfoSQL = minorInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);
					logger.info("SQL for get All Minor = " + minorInfoSQL);
				}
			
			try {
				
				oStatement = conn.createStatement();
				oRresultSet = oStatement.executeQuery(minorInfoSQL);
				
			}catch (SQLException ex) {
				minorInfoSQL = null;
				logger.info("SQL Exception==="+ex);
			} 		

			while (oRresultSet.next()) {
			
				minorInfo = new JSONObject();
				if(minor == null && userId == null) {
					
					minorInfo.put("All_Minor", oRresultSet.getString("DIPLOMA_DESCR"));
					
				}else if(minor == null ){
					
					minorInfo.put("Current_Minor", oRresultSet.getString("DIPLOMA_DESCR"));					
					
				}else {
					minorInfo.put("DeptID", oRresultSet.getString("DEPTID"));
					minorInfo.put("DeptName", oRresultSet.getString("DEPTNAME"));
					minorInfo.put("FullCollegeName", oRresultSet.getString("FUL_COLLEGE_NAME"));
					minorInfo.put("ChairUserID", oRresultSet.getString("CHAIR_USERID"));
					minorInfo.put("ChairEmpName", oRresultSet.getString("CHAIR_EMPNAME"));
					minorInfo.put("ChairEmplID", oRresultSet.getString("CHAIR_EMPLID"));
					minorInfo.put("ChairEmpEmail", oRresultSet.getString("CHAIR_EMAIL"));
				}
				
				jArray.put(minorInfo);
			}

		} catch (Exception oEx) {
			minorInfo = null;
			logger.info("Exception==="+oEx);

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
