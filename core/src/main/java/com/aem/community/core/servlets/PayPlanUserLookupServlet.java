
package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.sql.DataSource;

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
//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=10_12_11_12_PayPlan",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/payPlanUserLookUpServlet" })
public class PayPlanUserLookupServlet extends SlingSafeMethodsServlet {
    private final static Logger logger = LoggerFactory.getLogger(PayPlanUserLookupServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userID = "";
		//String cwid = "";
		JSONArray PayPlanDetails = null;
		
		if (req.getParameter("userID") != null && req.getParameter("userID") != "") {
			userID = req.getParameter("userID");			
			logger.info("userID =" + userID);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				PayPlanDetails = getPayPlanDetails(userID, conn, "payPlan");
				logger.info("emplFeeWaiverDetails ="+PayPlanDetails);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(PayPlanDetails.toString());
		}
	}
/**
 * 
 * @param cwid
 * @param oConnection
 * @param docType
 * @return
 * @throws Exception
 */
	public static JSONArray getPayPlanDetails(String userID, Connection oConnection, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		//JSONObject employeeEvalDetails = new JSONObject();
		
		JSONObject payPlanJSONDetails;
		JSONArray jArray = new JSONArray();
	
		//String emplIDSQL = ConfigManager.getValue("DependentFeeWaiverUserLookUp");		
		String emplIDSQL = CSUFConstants.PayPlanUserLookUp;
		logger.info("Dependent User Lookup"+emplIDSQL);
		
		//String lookupFields = ConfigManager.getValue("DependentFeeWaiverUserLookUpFields");
		String lookupFields = CSUFConstants.PayPlanLookUpFields;
		logger.info("Dependent User Lookup Fields"+lookupFields); 
		
		String[] fields = lookupFields.split(",");
		
		emplIDSQL = emplIDSQL.replaceAll("<<getUser_ID>>", userID);
		logger.info("SQL Comman is= "+emplIDSQL);
		//emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);

		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			while (oRresultSet.next()) {
				payPlanJSONDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					payPlanJSONDetails.put(fields[i], oRresultSet.getString(fields[i]));
					logger.info("employeeWaiverDetails ="+payPlanJSONDetails);
				}
				if (!payPlanJSONDetails.isNull("EMPLID")) {
					String cwid = payPlanJSONDetails.getString("EMPLID");
					String empEmailID = getEmailID(oConnection, cwid);
					payPlanJSONDetails.put("EMP_EMAIL_ID", empEmailID);
				}
				logger.info("Emp Details =" + jArray);
				jArray.put(payPlanJSONDetails);
			}
			logger.info("oRresultSet ="+jArray);
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			payPlanJSONDetails = null;
		} finally {
			try {
				if (oConnection != null){
					oConnection.close();
					
				}
				// oStatement.close();
				// oRresultSet.close();
			} catch (Exception exp) {

			}
		}

		//return employeeEvalDetails;
		return jArray;
	}

	public static String getEmailID(Connection oConnection, String cwid) throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		String empEmail = "";
		try {

			String getEmailSql = CSUFConstants.getEmailAddressCwidLookup;
			getEmailSql = getEmailSql.replaceAll("<<Emp_ID>>", cwid);
			oStatement = oConnection.createStatement();

			oRresultSet = oStatement.executeQuery(getEmailSql);
			if (oRresultSet.next()) {
				empEmail = oRresultSet.getString("EMAILID");
			}
			logger.info("Get Email Function=" + empEmail);
		} catch (Exception oEx) {
			throw oEx;
		}
		return empEmail;
	}

}