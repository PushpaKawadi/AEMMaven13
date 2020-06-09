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
		"sling.servlet.paths=" + "/bin/getCurrentConcentration" })
public class CSUFMajorMinorChangeGetCurrentConcentrationAndSignatureServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFMajorMinorChangeGetCurrentConcentrationAndSignatureServlet.class);
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
		
		logger.info("Inside doGET Method");
		
		JSONArray degreeDetails = null;
		if ((req.getParameter("AcadProg") != null && !req.getParameter("AcadProg").trim().equals(""))) {
			userId = req.getParameter("userID"); 			
			acadProg = req.getParameter("AcadProg");			
			acadPlanType = req.getParameter("AcadPlanType");				
			
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				degreeDetails = getCurrentConcentrationSignatureDetails(userId, acadProg, acadPlanType, conn);
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
	public static JSONArray getCurrentConcentrationSignatureDetails(String userId, String acadProg, String acadPlanType,
			Connection conn) throws Exception {

		logger.info("Inside getGradeChnageDetails");		

		ResultSet oRresultSet = null;
		JSONObject concentrationInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String concentrationInfoSQL = "";
		Statement oStatement = null;
		try {	
					concentrationInfoSQL = CSUFConstants.getCurrentConcentration;					
					
					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<getUser_ID>>", userId);
					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PROG>>", acadProg);					
					concentrationInfoSQL = concentrationInfoSQL.replaceAll("<<ACAD_PLAN_TYPE>>", acadPlanType);							
			
			try {
				
				oStatement = conn.createStatement();
				oRresultSet = oStatement.executeQuery(concentrationInfoSQL);
				
			}catch (SQLException ex) {
				concentrationInfoSQL = null;
				logger.info("SQL Exception==="+ex);
			} 		

			while (oRresultSet.next()) {
			
				concentrationInfo = new JSONObject();				
					
				concentrationInfo.put("Current_Concentration", oRresultSet.getString("CONCENTRATION"));
				
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
