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


@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION +"=Personal File Access Request Servlet", 
		"sling.service.method=" + HttpConstants.METHOD_POST, "sling.servlet.paths="+ "/bin/getPersonalFileAccessRequestUserLookUp"})
public class PersonalFileAccessRequestUserLookUp extends SlingSafeMethodsServlet{
	
	private static final Logger log = LoggerFactory.getLogger(PersonalFileAccessRequestUserLookUp.class);
	private static final long serialVersionUID = 1L;
	
	@Reference 
	private JDBCConnectionHelperService jdbcConnectionService;
	
	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response) throws ServletException, IOException{
		
		Connection conn = null;
		String userID = "";
		
		JSONArray personalFileAccessRequest = null;
		
		if (req.getParameter("userID") != null && req.getParameter("userID") != "") {
			userID = req.getParameter("userID");
			
			conn = jdbcConnectionService.getFrmDBConnection();
		}					
		
		if(conn != null) {
			try {
				personalFileAccessRequest = getPersonalFileAccessRequestDetails(userID, conn, "personalFileAccessRequest");
			
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			//set JSON in string
			response.getWriter().write(personalFileAccessRequest.toString());
		}
		
	}
	
	public static JSONArray getPersonalFileAccessRequestDetails(String userID, Connection oConnection, String docType) {
		
		ResultSet oRresultSet = null;
		
		JSONObject fileAccessRequestDetails = null;
		JSONArray jArray = new JSONArray();
		
		String userIDSQL = CSUFConstants.personalFileAccessRequestUserLookUp;
		//log.info("User Lookup SQL=="+userIDSQL);
		
		String lookupFields = CSUFConstants.personalFileAccessRequestUserLookUpFields;
		//log.info("Personal File Access Request Lookup Fields=="+lookupFields);
		
		String[] fields = lookupFields.split(",");
		
		userIDSQL = userIDSQL.replaceAll("<<getUser_ID>>", userID);
		//log.info("SQL Comman is= "+userIDSQL);
		
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(userIDSQL);
			while (oRresultSet.next()) {
				fileAccessRequestDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					fileAccessRequestDetails.put(fields[i], oRresultSet.getString(fields[i]));
					//log.info("employeeWaiverDetails ="+fileAccessRequestDetails);
				}
				jArray.put(fileAccessRequestDetails);
			}
			log.info("oRresultSet ="+jArray);
		} catch (Exception oEx) {
			log.info("Exception=" + oEx);
			oEx.printStackTrace();
			fileAccessRequestDetails = null;
		} finally {
			try {
				if (oConnection != null){
					oConnection.close();
					
				}
				
			} catch (Exception exp) {

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
			log.info("Connection=" + con);
			return con;

		} catch (Exception e) {
			log.error("Conn Exception=" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					log.info("Conn available=");
				}
			} catch (Exception exp) {
				log.info("Finally Exec=" + exp);
				exp.printStackTrace();
			}
		}
		return null;
	}	

}



















