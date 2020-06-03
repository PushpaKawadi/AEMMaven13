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
		"sling.servlet.paths=" + "/bin/getCertificateAndSignatureServlet" })
public class CSUFMajorMinorChangeCertificateAndSignatureServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFMajorMinorChangeCertificateAndSignatureServlet.class);
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
		String certificate = "";
		
		logger.info("Inside doGET Method");
		
		JSONArray degreeDetails = null;
		if ((req.getParameter("AcadProg") != null && !req.getParameter("AcadProg").trim().equals(""))) {
			userId = req.getParameter("userID"); 			
			acadProg = req.getParameter("AcadProg");			
			acadPlanType = req.getParameter("AcadPlanType");			
			certificate = req.getParameter("Certificate");	
			
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				degreeDetails = getCurrentConcentrationSignatureDetails(userId, acadProg, acadPlanType, certificate, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			if (degreeDetails != null && !degreeDetails.equals("")) {
				response.getWriter().write(degreeDetails.toString());
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
	public static JSONArray getCurrentConcentrationSignatureDetails(String userId, String acadProg, String acadPlanType, String certificate,
			Connection conn) throws Exception {

		logger.info("Inside getGradeChangeDetails");			

		ResultSet oRresultSet = null;
		JSONObject concentrationInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String concentrationInfoSQL = "";
		Statement oStatement = null;
		try {			
//				if(certificate != null && !certificate.equals("")) {
//					concentrationInfoSQL = CSUFConstants.getMinorSignature;						
//
//					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);								
//					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);																	
//					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<DIPLOMA_DESCR>>", certificate);
//					logger.info("SQL for get Minor Signature = " + concentrationInfoSQL);
//					
//				}else if(userId != null && !userId.equals("")) {
//					
//					concentrationInfoSQL = CSUFConstants.getCurrentMinors;					
//					
//					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<getUser_ID>>", userId);
//					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);					
//					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);	
//					logger.info("SQL for get Current Minor = " + concentrationInfoSQL);
//					
//				}else {
					
					concentrationInfoSQL = CSUFConstants.getAllCertificate;				
										
					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);					
					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);
					logger.info("SQL for get All Certificate = " + concentrationInfoSQL);
				//}
			
			try {
				
				oStatement = conn.createStatement();
				oRresultSet = oStatement.executeQuery(concentrationInfoSQL);
				
			}catch (SQLException ex) {
				concentrationInfoSQL = null;
				logger.info("SQL Exception==="+ex);
			} 		

			while (oRresultSet.next()) {
			
				concentrationInfo = new JSONObject();
				//if(certificate == null && userId == null) {
					
					concentrationInfo.put("All_Certificate", oRresultSet.getString("TRNSCR_DESCR"));
					
//				}else if(certificate == null ){
//					
//					concentrationInfo.put("Current_Minor", oRresultSet.getString("DIPLOMA_DESCR"));					
//					
//				}else {
//					concentrationInfo.put("DeptID", oRresultSet.getString("DEPTID"));
//					concentrationInfo.put("DeptName", oRresultSet.getString("DEPTNAME"));
//					concentrationInfo.put("FullCollegeName", oRresultSet.getString("FUL_COLLEGE_NAME"));
//					concentrationInfo.put("ChairUserID", oRresultSet.getString("CHAIR_USERID"));
//					concentrationInfo.put("ChairEmpName", oRresultSet.getString("CHAIR_EMPNAME"));
//					concentrationInfo.put("ChairEmplID", oRresultSet.getString("CHAIR_EMPLID"));
//					concentrationInfo.put("ChairEmpEmail", oRresultSet.getString("CHAIR_EMAIL"));
//				}
				
				jArray.put(concentrationInfo);
			}

		} catch (Exception oEx) {
			concentrationInfo = null;
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
