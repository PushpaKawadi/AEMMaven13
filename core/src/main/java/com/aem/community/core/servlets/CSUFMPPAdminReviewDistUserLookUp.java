
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

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=New Position Manager Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/mppAdminReviewEmpLookUp" })
public class CSUFMPPAdminReviewDistUserLookUp extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFMPPAdminReviewDistUserLookUp.class);
	private static final long serialVersionUID = 1L;

	public void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;

		String userID = "";
		String cwid = "";
		// JSONObject emplEvalDetails = null;
		JSONArray newMppAdminReviewDetails = null;
		if (req.getParameter("userID") != null
				&& req.getParameter("userID") != ""
				&& req.getParameter("cwid") != null
				&& req.getParameter("cwid") != "") {
			userID = req.getParameter("userID");
			cwid = req.getParameter("cwid");
			logger.info("userid =" + userID);
			logger.info("EmpID =" + cwid);
			conn = getConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				newMppAdminReviewDetails = getMppAdminReviewDistDetails(cwid, conn, userID, "mppAdminReviewDist");

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(newMppAdminReviewDetails.toString());
		}
	}

	public static JSONArray getMppAdminReviewDistDetails(String cwid,
			Connection oConnection, String userID, String docType)
			throws Exception {
		ResultSet oRresultSet = null;
		JSONObject newMPPAdminReviewDistDetails;
		JSONArray jArray = new JSONArray();
		String emplIDSQL = ConfigManager.getValue("MPPAdminReviewDistUserLookUp");
		
		String lookupFields = ConfigManager.getValue("MPPAdminReviewDistFields");
		
		String[] fields = lookupFields.split(",");
		emplIDSQL = emplIDSQL.replaceAll("<<getUser_ID>>", userID);
		
		emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);
		logger.info("THE SQL QUERY IS: "+emplIDSQL);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			while (oRresultSet.next()) {
				newMPPAdminReviewDistDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					
					newMPPAdminReviewDistDetails.put(fields[i],
							oRresultSet.getString(fields[i]));
				}
				jArray.put(newMPPAdminReviewDistDetails);
			}
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			newMPPAdminReviewDistDetails = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();
					logger.info("Connection closed");

				}
			} catch (Exception e) {
				logger.error("Exception in MPPAdminReviewLookUpServlet="
						+ e.getMessage());
				e.getStackTrace();
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