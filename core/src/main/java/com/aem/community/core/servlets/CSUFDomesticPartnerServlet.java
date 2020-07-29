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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Domestic Partner Dependent Certification Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getDomesticPartnerEmpDetails" })
public class CSUFDomesticPartnerServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFDomesticPartnerServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userId = "";
		JSONArray domesticPartnerDetails = null;
		if (req.getParameter("userId") != null && req.getParameter("userId") != "") {
			userId = req.getParameter("userId");
			logger.info("Got userId =" + userId);
			conn = jdbcConnectionService.getFrmDBConnection();
		}
		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				domesticPartnerDetails = DomesticPartnerDetailsData(userId, conn);

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(domesticPartnerDetails.toString());
		}
	}

	public static JSONArray DomesticPartnerDetailsData(String userId, Connection oConnection) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject domesticPartnerUserDetails;
		JSONArray jArray = new JSONArray();
		String domesticPartnerSQL = CSUFConstants.domesticPartner;
		String lookupFields = CSUFConstants.domesticPartnerLookUpFields;
		String[] fields = lookupFields.split(",");
		domesticPartnerSQL = domesticPartnerSQL.replaceAll("<<getUser_ID>>", userId);
		logger.info("Domestic Partner SQL=" + domesticPartnerSQL);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(domesticPartnerSQL);
			while (oRresultSet.next()) {
				domesticPartnerUserDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					domesticPartnerUserDetails.put(fields[i], oRresultSet.getString(fields[i]));
				}
				if(!domesticPartnerUserDetails.isNull("EMP_USERID")) {
					String empUid = domesticPartnerUserDetails.getString("EMP_USERID");
					String empEmailID = getEmailID(oConnection,empUid);
					domesticPartnerUserDetails.put("EMP_EMAIL_ID", empEmailID);
				}
				logger.info("Domestic Partner User Details=" + domesticPartnerUserDetails);
				jArray.put(domesticPartnerUserDetails);
			}
		} catch (Exception oEx) {
			logger.error("Exception=" + oEx);
			oEx.printStackTrace();
			domesticPartnerUserDetails = null;
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