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
		"sling.servlet.paths=" + "/bin/getAllMajors" })
public class CSUFMajorMinorChangeAllMajorsServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFMajorMinorChangeAllMajorsServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String ACAD_Prog = "";
		String ACAD_Plan_Type = "";

		JSONArray majorMinorChangeDetails = null;
		if (req.getParameter("Acad_Prog") != null
				&& !req.getParameter("Acad_Prog").trim().equals("") && req.getParameter("Acad_Plan_Type") != null) {
			ACAD_Prog = req.getParameter("Acad_Prog");
			ACAD_Plan_Type = req.getParameter("Acad_Plan_Type");
			
			conn = jdbcConnectionService.getDocDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				majorMinorChangeDetails = getStudentInformationDetails(ACAD_Prog, ACAD_Plan_Type, conn);
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
	public static JSONArray getStudentInformationDetails(String ACAD_Prog, String ACAD_Plan_Type,
			Connection conn) throws Exception {

		logger.info("Inside getAllMajorsDetails=" + ACAD_Prog);

		ResultSet oRresultSet = null;
		JSONObject instInfo = new JSONObject();
		JSONArray jArray = new JSONArray();
		String majorMinorChangeInfoSQL = "";
		Statement oStatement = null;
		try {
			
			majorMinorChangeInfoSQL = CSUFConstants.getAllMajors;

			majorMinorChangeInfoSQL = majorMinorChangeInfoSQL.replaceAll(
					"<<ACAD_PROG>>", ACAD_Prog);
			//majorMinorChangeInfoSQL = majorMinorChangeInfoSQL("<<ACAD_PROG>>", ACAD_Prog);
			
			logger.info("get All Majors sql=" + majorMinorChangeInfoSQL);
			oStatement = conn.createStatement();
			oRresultSet = oStatement.executeQuery(majorMinorChangeInfoSQL);
			
			while (oRresultSet.next()) {
				instInfo = new JSONObject();    
				if(  !oRresultSet.getString("DIPLOMA_DESCR").isEmpty()) {
					
				    instInfo.put("Majors", oRresultSet.getString("DIPLOMA_DESCR"));			
							
					jArray.put(instInfo);
					logger.info("JSON Object value="+instInfo);
				}
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
