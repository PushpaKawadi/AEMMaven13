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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Direct Pay Dental Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getDirectPayDentalEmpDetails" })
public class CSUFDirectPayDentalServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFDirectPayDentalServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String ssn = "";
		JSONArray directPayDentalDetails = null;
		if (req.getParameter("ssn") != null && req.getParameter("ssn") != "") {
			ssn = req.getParameter("ssn");
			logger.info("Got SSN =" + ssn);
			conn = jdbcConnectionService.getFrmDBConnection();
		}
		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				directPayDentalDetails = getEmployeeTransferData(ssn, conn);

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(directPayDentalDetails.toString());
		}
	}

	public static JSONArray getEmployeeTransferData(String ssn, Connection oConnection) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject directPayDentalUserDetails;
		JSONArray jArray = new JSONArray();
		String directPayDentalSQL = CSUFConstants.directPayDental;
		String lookupFields = CSUFConstants.directPayDentalLookUpFields;
		String[] fields = lookupFields.split(",");
		directPayDentalSQL = directPayDentalSQL.replaceAll("<<SSN>>", ssn);
		logger.info("Direct Pay Dental SQL=" + directPayDentalSQL);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(directPayDentalSQL);
			while (oRresultSet.next()) {
				directPayDentalUserDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					directPayDentalUserDetails.put(fields[i], oRresultSet.getString(fields[i]));
				}
				if(!directPayDentalUserDetails.isNull("EMP_USERID")) {
					String empUid = directPayDentalUserDetails.getString("EMP_USERID");
					String empEmailID = getEmailID(oConnection,empUid);
					directPayDentalUserDetails.put("EMP_EMAIL_ID", empEmailID);
				}
				logger.info("Direct Pay Dental User Details=" + directPayDentalUserDetails);
				jArray.put(directPayDentalUserDetails);
			}
		} catch (Exception oEx) {
			logger.error("Exception=" + oEx);
			oEx.printStackTrace();
			directPayDentalUserDetails = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();
					logger.info("Connection closed");
				}
			} catch (Exception e) {
				logger.error("Exception in CSUF=" + e.getMessage());
				e.getStackTrace();
			}
		}
		return jArray;
	}
	public static String getEmailID(Connection oConnection,String uid) throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		String empEmail = "";
			try {

			String getEmailSql = CSUFConstants.getEmailAddressUserIdLookup;
			getEmailSql = getEmailSql.replaceAll("<<UID>>", uid);
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