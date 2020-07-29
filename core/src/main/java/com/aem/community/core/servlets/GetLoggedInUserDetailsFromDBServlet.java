/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
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

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Logged In User Details From DB Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/getLoggedInUserDetailsFromDB" })
public class GetLoggedInUserDetailsFromDBServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(GetLoggedInUserDetailsFromDBServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		ResourceResolver resolver = req.getResourceResolver();
		Session session = resolver.adaptTo(Session.class);		
		Connection conn = null;
		String userID = session.getUserID();
		JSONArray userDetails = null;
		if (userID != null && userID != "") {
			logger.info("userid =" + userID);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				userDetails = getUserDetails(conn, userID);

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(userDetails.toString());
		}
	}

	public static JSONArray getUserDetails(Connection oConnection, String userID) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject loggedinUserNameDetails;
		JSONObject loggedinUserDetails;
		loggedinUserDetails = new JSONObject();
		JSONArray jArray = new JSONArray();
		String loggedInUserSQL = CSUFConstants.getLoggedInUserDetailsFromDB;
		String lookupFields = CSUFConstants.loggedInUserDetailsLookupFields;
		String[] fields = lookupFields.split(",");
		loggedInUserSQL = loggedInUserSQL.replaceAll("<<get_user_id>>", userID);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(loggedInUserSQL);
			while (oRresultSet.next()) {
				loggedinUserNameDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					loggedinUserNameDetails.put(fields[i], oRresultSet.getString(fields[i]));	
					}
				if (!loggedinUserNameDetails.isNull("FNAME") && !loggedinUserNameDetails.isNull("LNAME")) {					
					String fullName = loggedinUserNameDetails.getString("FNAME").concat(" ".concat(loggedinUserNameDetails.getString("LNAME")));
					loggedinUserDetails.put("FULL_NAME", fullName);
				}
				jArray.put(loggedinUserDetails);
			}
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			loggedinUserDetails = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();
					logger.info("Connection closed");

				}
			} catch (Exception e) {
				logger.error("Exception in CSUFEmployeeLookUpServlet=" + e.getMessage());
				e.getStackTrace();
			}
		}
		return jArray;
	}

}