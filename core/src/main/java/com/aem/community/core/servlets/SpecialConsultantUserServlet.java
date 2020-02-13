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
//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Special Consultant User Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getSplUserLookup" })
public class SpecialConsultantUserServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(SpecialConsultantUserServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userID = "";
		JSONArray emplEvalDetails = null;
		if (req.getParameter("userID") != null && req.getParameter("userID") != "") {
			userID = req.getParameter("userID");
			logger.info("userid =" + userID);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				emplEvalDetails = getUserIDDetailsNew(userID, conn, "SPE2579");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(emplEvalDetails.toString());
		}
	}

	public static JSONArray getUserIDDetailsNew(String userID, Connection oConnection, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		JSONObject employeeEvalDetails;
		JSONArray jArray = new JSONArray();

		Statement oStatement = null;
		try {

			// String userIDSQL = ConfigManager.getValue(docType+"USERIDSQL");

//			String userIDSQL = "SELECT A.FIRST_NAME, A.LAST_NAME, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, "
//					+ "B.GRADE, B.UNION_CD, D.SUPERVISOR_NAME AS SupervisorName, A.EMPLID, D.WORKING_TITLE AS SupervisorTitle "
//					+ "FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_EMP_CWID_NT_NAME C ON C.CWID = B.EMPLID LEFT "
//					+ "JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO WHERE C.USERID = '<<getUser_ID>>'";
			// String lookupFields = ConfigManager.getValue(docType +
			// "USERIDLOOKUPFIELDS");
			String userIDSQL ="Select A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, A.EMPLID, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME  from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = C.cwid and C.userid = '<<getUser_ID>>' and A.EMPLID = B.EMPLID";

			String lookupFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,EMPLID,EXTENSTION,FUL_COLLEGE_NAME";
			String[] fields = lookupFields.split(",");

			userIDSQL = userIDSQL.replaceAll("<<getUser_ID>>", userID);

			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(userIDSQL);
			logger.info("userIDSQL="+userIDSQL);

			// if (oRresultSet.next()) {
			while (oRresultSet.next()) {
				logger.info("oRresultSet="+oRresultSet);

				employeeEvalDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					employeeEvalDetails.put(fields[i], oRresultSet.getString(fields[i]));

				}
				jArray.put(employeeEvalDetails);
				logger.info("jArray=" + jArray);
			}

		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();

		} finally {
			try {
				if (oConnection != null){
					oConnection.close();
					
				}
//				if (oStatement != null){
//					oStatement.close();
//					
//				}
//				if (oRresultSet != null){
//					oRresultSet.close();
//					
//				}
				//oStatement.close();
				//oRresultSet.close();
			} catch (Exception exp) {
				logger.info("Finally=" + exp);
				exp.printStackTrace();
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