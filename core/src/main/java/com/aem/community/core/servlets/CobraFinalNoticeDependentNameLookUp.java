
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
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getCobraDependentNameLookup" })
public class CobraFinalNoticeDependentNameLookUp extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CobraFinalNoticeDependentNameLookUp.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String dependentName = "";
		String cwid = "";
		JSONArray cobraFinalDetails = null;
		
		if ((req.getParameter("cwid") != null && req.getParameter("cwid") != "") 
			&& (req.getParameter("dependentName") != null && req.getParameter("dependentName") != "")){
			dependentName = req.getParameter("dependentName");
			cwid = req.getParameter("cwid");
			logger.info("dependentName =" + dependentName);
			logger.info("EmpID =" + cwid);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				cobraFinalDetails = getCobraFinalDetails(cwid, dependentName, conn, "COBRAFINALNOTICE");
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
	public static JSONArray getCobraFinalDetails(String cwid, String dependentName, Connection oConnection, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		//JSONObject employeeEvalDetails = new JSONObject();
		
		JSONObject cobraFinalNoticeDetails;
		JSONArray jArray = new JSONArray();
	
		//String emplIDSQL = ConfigManager.getValue("cobraFinalDependentNameLookUp");
		String emplIDSQL = CSUFConstants.cobraFinalDependentNameLookUp;
		logger.info("Cobra Dependent LookUp="+emplIDSQL);
		//String lookupFields = ConfigManager.getValue("cobraFinalDependentNameFields");
		String lookupFields = CSUFConstants.cobraFinalDependentNameFields;
		logger.info("Cobra Dependent LookUp fields="+lookupFields);
		
		String[] fields = lookupFields.split(",");
		
		emplIDSQL = emplIDSQL.replaceAll("<<DependentName>>", dependentName);
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
			logger.error("Conn Exception=" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					logger.info("Conn available=");
				}
			} catch (Exception exp) {
				logger.info("Finally Exec=" + exp);
				exp.printStackTrace();
			}
		}
		return null;
	}

}