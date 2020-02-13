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
//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Vision Life Ltd Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getVisionLifeLtdLookup" })
public class CSUFVisionLifeLtd extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFVisionLifeLtd.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		String ssn = "";

		// JSONObject emplEvalDetails = null;
		JSONArray visionDetails = null;
		if (req.getParameter("ssn") != null && req.getParameter("ssn") != "") {
			ssn = req.getParameter("ssn");

			logger.info("ssn =" + ssn);

			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				visionDetails = getvisionDetails(conn, ssn, "SPE2579");

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(visionDetails.toString());
		}
	}

	public static JSONArray getvisionDetails(Connection oConnection, String ssn, String docType) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject visionLifeDetails;
		JSONArray jArray = new JSONArray();
		String visionLifeSQL = ConfigManager.getValue("visionLifeSQL");
		
		String lookupFields = ConfigManager.getValue("lookupFieldsVisionLife");
		
		String[] fields = lookupFields.split(",");
		visionLifeSQL = visionLifeSQL.replaceAll("<<SSN>>", ssn);

		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(visionLifeSQL);
			while (oRresultSet.next()) {
				visionLifeDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					//logger.info("fied="+fields[i]);
					visionLifeDetails.put(fields[i], oRresultSet.getString(fields[i]));
				}
				jArray.put(visionLifeDetails);
			}
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			visionLifeDetails = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();
					logger.info("Connection closed");

				}
			} catch (Exception e) {
				logger.error("Exception in CSUFvisionLifeServlet=" + e.getMessage());
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