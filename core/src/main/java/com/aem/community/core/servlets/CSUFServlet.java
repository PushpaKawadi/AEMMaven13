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
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Staff Evaluation Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getEmployeeDetails" })
public class CSUFServlet extends SlingSafeMethodsServlet {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		Statement oStatement = null;
		conn = getConnection();
		// String cwid="";
		// if(req.getParameter("cwid") != null && req.getParameter("cwid") != ""){
		// cwid = req.getParameter("cwid");
		// logger.info("cwid ="+cwid);
		// }
		// cwid = req.getParameter("cwid");
		// logger.info("cwid outdis="+cwid);

		logger.info("Focus CSUF=" + conn);
		if (conn != null) {
			logger.info("Connection Successfull=" + conn);
			ResultSet oRresultSet = null;
			String firstName = null;
			String lastName = null;
			String userId = null;
			String deptId = null;
			String deptName = null;
			String emplRcd = null;
			String classification = null;
			String range = null;
			String supervisorName = null;
			String supervisorTitle = null;
			String cbid = null;
			JSONObject employeeEvalDetails = new JSONObject();
			String cwid = "806225686";

			String sSQLQuery = "SELECT A.FIRST_NAME, A.LAST_NAME, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, B.UNION_CD, D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS SupervisorTitle "
					+ "FROM FUL_ECM_JOB_VW B " + "LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID "
					+ "LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO " + "WHERE B.EMPLID = '" + cwid
					+ "'";

			try {
				oStatement = conn.createStatement();
				logger.info("oStatement=" + oStatement);
				oRresultSet = oStatement.executeQuery(sSQLQuery);
				if (oRresultSet.next()) {
					logger.info("Resultset=" + oRresultSet);
					firstName = oRresultSet.getString("FIRST_NAME");
					lastName = oRresultSet.getString("LAST_NAME");
					deptId = oRresultSet.getString("DEPTID");
					deptName = oRresultSet.getString("DEPTNAME");
					emplRcd = oRresultSet.getString("EMPL_RCD");
					classification = oRresultSet.getString("DESCR");
					range = oRresultSet.getString("GRADE");
					supervisorName = oRresultSet.getString("SupervisorName");
					supervisorTitle = oRresultSet.getString("SupervisorTitle");
					cbid = oRresultSet.getString("UNION_CD");

					employeeEvalDetails.put("FirstName", firstName);
					employeeEvalDetails.put("LastName", lastName);
					employeeEvalDetails.put("DeptID", deptId);
					employeeEvalDetails.put("DeptName", deptName);
					employeeEvalDetails.put("EmplRCD", emplRcd);
					employeeEvalDetails.put("Classification", classification);
					employeeEvalDetails.put("Range", range);
					employeeEvalDetails.put("SupervisorName", supervisorName);
					employeeEvalDetails.put("SupervisorTitle", supervisorTitle);
					employeeEvalDetails.put("CBID", cbid);
				}
				// Set the content type JSON
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				// Set JSON in String
				response.getWriter().write(employeeEvalDetails.toString());
			} catch (Exception oEx) {
				logger.info("Exception frmmgrprod=" + oEx);
				oEx.printStackTrace();
			} finally {
				logger.info("Finally Exec frmmgrprod");
				try {
					if (conn != null) {
						conn.close();

					}
					// oStatement.close();
					// oRresultSet.close();
				} catch (Exception exp) {
					logger.info("Finally Exec frmmgrprod=" + exp);
					exp.printStackTrace();

				}
			}
		}
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