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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import com.aem.community.util.ConfigManager;
//Add the DataSourcePool package

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Pre Prerformance Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getPrePerfDBDetails" })
public class CSUFPrePerfDBServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFPrePerfDBServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		String empID = "";
		String reviewPeriodTo ="";
		String reviewPeriodFrom ="";
		String deptID ="";
		JSONArray emplEvalDetails = null;
		
		Connection dbConn = jdbcConnectionService.getAemDEVDBConnection();
		logger.info("dbConn==========="+dbConn);
		
		if (req.getParameter("empID") != null && req.getParameter("empID") != "" ) {
			empID = req.getParameter("empID");
			reviewPeriodTo = req.getParameter("reviewPeriodTo");
			reviewPeriodFrom = req.getParameter("reviewPeriodFrom");
			deptID = req.getParameter("deptID");
			
		}

		if (dbConn != null) {
			try {
				logger.error("Connection Success=" + dbConn);
				emplEvalDetails = getUserIDDetailsNew(empID,reviewPeriodTo,reviewPeriodFrom,deptID, dbConn, "SPE2579");
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(emplEvalDetails.toString());
		}
	}

	public static JSONArray getUserIDDetailsNew(String empID,String reviewPeriodTo,String reviewPeriodFrom,String deptID ,Connection oConnection, String docType)
			throws Exception {
		
		ResultSet oRresultSet = null;
		JSONObject employeeEvalDetails;
		JSONArray jArray = new JSONArray();
		//String userIDSQL = "select * from aem_mpp_self_eval where empid='899943393' and review_period_from='16-APR-19' and review_period_to='15-APR-20' and deptid='10100'";
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat reqFormat = new SimpleDateFormat("dd-MMM-yy");
		String userIDSQL = ConfigManager.getValue("PrePerfReviewSQL");
		
		userIDSQL = userIDSQL.replaceAll("<<emplid>>", empID);
		logger.info("Empid="+userIDSQL);
		
		//userIDSQL = userIDSQL.replaceAll("<<review_from_dt>>", reqFormat.format(fromUser.parse(reviewPeriodFrom)));
		userIDSQL = userIDSQL.replaceAll("<<review_from_dt>>", reviewPeriodFrom);
		logger.info("From="+userIDSQL);
		
		//userIDSQL = userIDSQL.replaceAll("<<review_to_dt>>", reqFormat.format(fromUser.parse(reviewPeriodTo)));
		userIDSQL = userIDSQL.replaceAll("<<review_to_dt>>", reviewPeriodTo);
		logger.info("To="+userIDSQL);
		
		userIDSQL = userIDSQL.replaceAll("<<deptid>>", deptID);
		logger.info("Dept="+userIDSQL);
		
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(userIDSQL);
			while (oRresultSet.next()) {
				employeeEvalDetails = new JSONObject();
				employeeEvalDetails.put("evalComment1", oRresultSet.getString("EVAL_COMMENT1"));
				employeeEvalDetails.put("evalComment2", oRresultSet.getString("EVAL_COMMENT2"));
				employeeEvalDetails.put("evalComment3", oRresultSet.getString("EVAL_COMMENT3"));
				employeeEvalDetails.put("evalComment4", oRresultSet.getString("EVAL_COMMENT4"));
				employeeEvalDetails.put("evalComment5", oRresultSet.getString("EVAL_COMMENT5"));
				employeeEvalDetails.put("evalComment6", oRresultSet.getString("EVAL_COMMENT6"));
				employeeEvalDetails.put("evalComment7", oRresultSet.getString("EVAL_COMMENT7"));
				//employeeEvalDetails.put("instanceId", oRresultSet.getString("WORKFLOW_INSTANCE_ID"));
				jArray.put(employeeEvalDetails);
			}

		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();

		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();

				}

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

		return jArray;
	}
}