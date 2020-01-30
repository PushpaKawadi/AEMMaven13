
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

import com.aem.community.util.ConfigManager;
//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Dental Plan Enrollment",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/dentalPlanSSNLookUp" })
public class DentalPlanSSNServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(DentalPlanSSNServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String ssn = "";
		JSONArray newPositionManagerDetails = null;
		if (req.getParameter("ssn") != null && req.getParameter("ssn") != "") {
			ssn = req.getParameter("ssn");
			conn = getConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				newPositionManagerDetails = getDentalPlanDetails(ssn, conn, "dentalPlan");
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(newPositionManagerDetails.toString());
		}
	}

	public static JSONArray getDentalPlanDetails(String ssn, Connection oConnection, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		JSONObject newDentalPlanDetails;
		JSONArray jArray = new JSONArray();

        String userIDSQL = ConfigManager.getValue("DentalPlanEnrollmentSSNLookUp");
        logger.info("The userID SQL is=" + userIDSQL);

        String lookupFields = ConfigManager.getValue("DentalPlanEnrollmentFields");
        logger.info("The user LookUp Fields are=" + lookupFields);

		String[] fields = lookupFields.split(",");

        userIDSQL = userIDSQL.replaceAll("<<SSN>>", ssn);
        logger.info("User ID is="+userIDSQL);
		Statement oStatement = null;
		try {

			logger.info("inside try4");
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(userIDSQL);

			while (oRresultSet.next()) {

				newDentalPlanDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					newDentalPlanDetails.put(fields[i], oRresultSet.getString(fields[i]));

				}
				jArray.put(newDentalPlanDetails);
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

	@Reference
	private DataSourcePool source;

	private Connection getConnection() {
		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool right here!
			dataSource = (DataSource) source.getDataSource("frmmgrprod");
			con = dataSource.getConnection();
			logger.info("Connection=" + con);
			return con;

		} catch (Exception e) {
			logger.info("Conn Exception=" + e);
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					logger.info("Conn Exec=");
				}
			} catch (Exception exp) {
				logger.info("Finally Exec=" + exp);
				exp.printStackTrace();
			}
		}
		return null;
	}

}