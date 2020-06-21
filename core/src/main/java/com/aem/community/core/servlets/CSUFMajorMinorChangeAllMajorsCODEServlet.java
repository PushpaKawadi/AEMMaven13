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
		Constants.SERVICE_DESCRIPTION + "=Major & Minor Change Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/getAllMajorsCode" })
public class CSUFMajorMinorChangeAllMajorsCODEServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFMajorMinorChangeAllMajorsCODEServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;		
		String trnscrDescr = "";

		JSONArray majorMinorChangeDetails = null;
		
		if ((req.getParameter("TrnscrDescr") != null && !req.getParameter("TrnscrDescr").trim().equals(""))) {
			trnscrDescr = req.getParameter("TrnscrDescr"); 			
				
			
			conn = jdbcConnectionService.getDocDBConnection();
		}

		conn = jdbcConnectionService.getDocDBConnection();

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				majorMinorChangeDetails = getStudentInformationDetails(trnscrDescr, conn);
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
	public static JSONArray getStudentInformationDetails(String trnscrDescr,
			Connection conn) throws Exception {

		logger.info("Inside getAllMajorsDetails=" + trnscrDescr);

		ResultSet oRresultSet = null;
		JSONObject instInfo = null;		
		JSONArray jArray = new JSONArray();
		String majorMinorChangeInfoSQL = "";
		Statement oStatement = null;
		try {
				
				majorMinorChangeInfoSQL = CSUFConstants.getAllMajorsAcadPlan;
				logger.info("Updated ALl Major SQL is======"+majorMinorChangeInfoSQL);				
				
				majorMinorChangeInfoSQL = majorMinorChangeInfoSQL.replaceAll("<<PROGRAM>>", trnscrDescr);
			
			try {
				
				oStatement = conn.createStatement();
				oRresultSet = oStatement.executeQuery(majorMinorChangeInfoSQL);
				
			}catch (SQLException ex) {
				majorMinorChangeInfoSQL = null;
				logger.info("SQL Exception==="+ex);
			} 
			
			while (oRresultSet.next()) {
				instInfo = new JSONObject();				

				instInfo.put("ACAD_PLAN", oRresultSet.getString("ACAD_PLAN"));	

				jArray.put(instInfo);
			}
	

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
}
