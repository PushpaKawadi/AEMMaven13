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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=MPP Evaluation Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getMppManagerIdLookup" })
public class MPPManagerLookupServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(MPPManagerLookupServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		String emplId = "";
		String deptid = "";
		// JSONObject mppManagerDetails = null;
		JSONArray mppManagerDetails = null;
		if (req.getParameter("empId") != null) {
			emplId = req.getParameter("empId");
			deptid = req.getParameter("deptid");
			logger.info("emplId =" + emplId);
			logger.info("deptid =" + deptid);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				mppManagerDetails = getMppEvalManagerDetails(emplId, deptid, conn, "SPE2579");

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(mppManagerDetails.toString());
		}
	}

	public static JSONArray getMppEvalManagerDetails(String emplId, String deptid, Connection oConnection,
			String docType) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject mppEvalManagerDetails;
		JSONArray jArray = new JSONArray();
		String mppManagerIDSQL = CSUFConstants.mppManagerSQL;
		// String lookupFields = ConfigManager.getValue("lookupFields");
		String lookupFields = CSUFConstants.mppManagerLookupFields;
		String[] fields = lookupFields.split(",");

		mppManagerIDSQL = mppManagerIDSQL.replaceAll("<<EMPL_ID>>", emplId);
		mppManagerIDSQL = mppManagerIDSQL.replaceAll("<<DEPTID>>", deptid);
		logger.info("mppManagerIDSQL =" + mppManagerIDSQL);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(mppManagerIDSQL);
			while (oRresultSet.next()) {
				mppEvalManagerDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					mppEvalManagerDetails.put(fields[i], oRresultSet.getString(fields[i]));
				}
				if(!mppEvalManagerDetails.isNull("MANAGER_EMP_USERID")) {
					String managerUid = mppEvalManagerDetails.getString("MANAGER_EMP_USERID");
					String managerEmailID = getEmailID(oConnection,managerUid);
					mppEvalManagerDetails.put("MANAGER_EMAIL_ID", managerEmailID);
				}
				jArray.put(mppEvalManagerDetails);
			}
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			mppEvalManagerDetails = null;
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
	public static String getEmailID(Connection oConnection,String uid) throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		String empEmail = "";
			try {

			String getEmailSql = CSUFConstants.getEmailAddressUserIdLookup;
			getEmailSql = getEmailSql.replaceAll("<<UID>>", uid);
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

	@Reference
	private DataSourcePool source;

}