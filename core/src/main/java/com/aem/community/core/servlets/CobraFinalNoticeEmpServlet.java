
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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Cobra Final Notice Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getCobraEmpLookup" })
public class CobraFinalNoticeEmpServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CobraFinalNoticeEmpServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		//String userID = "";
		String cwid = "";
		JSONArray cobraFinalDetails = null;
		
		if (req.getParameter("cwid") != null && req.getParameter("cwid") != "") {
			//userID = req.getParameter("userID");
			cwid = req.getParameter("cwid");
			//logger.info("userid =" + userID);
			logger.info("EmpID =" + cwid);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				cobraFinalDetails = getCobraFinalDetails(cwid, conn, "SPE2579");
				logger.info("emplEvalDetails ="+cobraFinalDetails);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(cobraFinalDetails.toString());
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
	public static JSONArray getCobraFinalDetails(String cwid, Connection oConnection, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		//JSONObject employeeEvalDetails = new JSONObject();
		
		JSONObject cobraFinalNoticeDetails;
		JSONArray jArray = new JSONArray();
	
		//String emplIDSQL = ConfigManager.getValue("cobraEmplIDSQL");
		String emplIDSQL = CSUFConstants.cobraEmplIDSQL;
		//String lookupFields = ConfigManager.getValue("cobraLookUpFields");
		String lookupFields = CSUFConstants.cobraLookUpFields;
		
		String[] fields = lookupFields.split(",");
		
		//emplIDSQL = emplIDSQL.replaceAll("<<getUser_ID>>", userID);
		emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);

		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			while (oRresultSet.next()) {
				cobraFinalNoticeDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					cobraFinalNoticeDetails.put(fields[i], oRresultSet.getString(fields[i]));
					logger.info("employeeEvalDetails ="+cobraFinalNoticeDetails);
				}
				
				if(cobraFinalNoticeDetails.length() > 0) {
					String empEmailID = getEmailID(oConnection,cwid);
					cobraFinalNoticeDetails.put("EMP_EMAIL_ID", empEmailID);
					cobraFinalNoticeDetails.put("EMP_EMAIL_ID", "pushpa.kawadi@thoughtfocus.com");
				}
				jArray.put(cobraFinalNoticeDetails);
			}
			logger.info("oRresultSet ="+jArray);
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			cobraFinalNoticeDetails = null;
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

	public static String getEmailID(Connection oConnection,String cwid) throws Exception {
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