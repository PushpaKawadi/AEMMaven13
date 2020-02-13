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
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Catastrophic Leave Request",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/getCatastrophicLeaveRequest" })
public class CSUFCatastrophicLeaveRequestServlet extends
		SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFCatastrophicLeaveRequestServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		//String dataSourceName = ConfigManager.getValue("dbFrmMgrProd");
		String userID = "";
		// JSONObject emplEvalDetails = null;
		JSONArray emplEvalDetails = null;
		if (req.getParameter("userID") != null
				&& req.getParameter("userID") != "") {
			userID = req.getParameter("userID");
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				emplEvalDetails = getCataLeaveRequestUserDetails(conn, userID,
						"SPE2579");

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(emplEvalDetails.toString());
		}
	}

	public static JSONArray getCataLeaveRequestUserDetails(
			Connection oConnection, String userID, String docType)
			throws Exception {
		ResultSet oRresultSet = null;
		JSONObject employeeEvalDetails;
		JSONArray jArray = new JSONArray();
		
		String sqlQuery = ConfigManager.getValue("catastrophicLeaveRequest");
		logger.info("sqlQuery="+sqlQuery);
		String lookupFields = ConfigManager.getValue("catastrophicFields");
		logger.info("lookupFields="+lookupFields);
		String[] fields = lookupFields.split(",");
		sqlQuery = sqlQuery.replaceAll("<<getUser_ID>>", userID);
		// emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);
		logger.info("emplIDSQL="+sqlQuery);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(sqlQuery);
			while (oRresultSet.next()) {
				employeeEvalDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					employeeEvalDetails.put(fields[i],
							oRresultSet.getString(fields[i]));
				}
				jArray.put(employeeEvalDetails);
			}
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			employeeEvalDetails = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();
					logger.info("Connection closed");

				}
			} catch (Exception e) {
				logger.error("Exception in CSUFCatastrophicLeaveRequestServlet="
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