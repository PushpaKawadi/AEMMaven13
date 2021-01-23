
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
import com.aem.community.util.ConfigManager;
//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Appointment Change Request",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/appointmentChangeRequestUserLookUp" })
public class AppointmentChangeRequestSelfUserLookUpServlet extends SlingSafeMethodsServlet {
    private final static Logger logger = LoggerFactory.getLogger(AppointmentChangeRequestSelfUserLookUpServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection dbConn = null;
		String userID = "";
		JSONArray appointmentChangeRequestDetails = null;
		if (req.getParameter("userID") != null && req.getParameter("userID") != "") {
			userID = req.getParameter("userID");
			dbConn = jdbcConnectionService.getFrmDBConnection();
			logger.info("dbConn==========="+dbConn);
		}

		if (dbConn != null) {
			try {
				logger.info("Connection Success=" + dbConn);
				appointmentChangeRequestDetails = getAppointmentChangeRequestDetails(userID, dbConn, "appointmentChangeRequest");
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(appointmentChangeRequestDetails.toString());
		}
	}

	public static JSONArray getAppointmentChangeRequestDetails(String userID, Connection oConnection, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		JSONObject appointmentChangeRequestSelfDetails;
		JSONArray jArray = new JSONArray();

        String userIDSQL = ConfigManager.getValue("AppointmentChangeRequestUserLookup");
        logger.info("The userID SQL is=" + userIDSQL);

        String lookupFields = ConfigManager.getValue("AppointmentChangeRequestFields");
        logger.info("The user LookUp Fields are=" + lookupFields);

		String[] fields = lookupFields.split(",");

        userIDSQL = userIDSQL.replaceAll("<<getUser_ID>>", userID);
        logger.info("User ID is="+userIDSQL);
		Statement oStatement = null;
		try {

			logger.info("inside try4");
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(userIDSQL);

			while (oRresultSet.next()) {

				appointmentChangeRequestSelfDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					appointmentChangeRequestSelfDetails.put(fields[i], oRresultSet.getString(fields[i]));

				}
				jArray.put(appointmentChangeRequestSelfDetails);
				logger.info("jArray=" + jArray);
			}

		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();

		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();

				}

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

		return jArray;
	}

}
