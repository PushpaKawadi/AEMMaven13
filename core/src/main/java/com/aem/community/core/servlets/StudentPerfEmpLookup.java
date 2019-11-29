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

//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Staff Evaluation Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getStudentPerf" })
public class StudentPerfEmpLookup extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(StudentPerfEmpLookup.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userID = "";
		String cwid = "";
		// JSONObject emplEvalDetails = null;
		JSONArray emplEvalDetails = null;
		// if (req.getParameter("userID") != null && req.getParameter("userID")
		// != "" && req.getParameter("cwid") != null
		// && req.getParameter("cwid") != "") {
		if (req.getParameter("cwid") != null && req.getParameter("cwid") != "") {
			userID = req.getParameter("userID");
			cwid = req.getParameter("cwid");
			logger.info("userid =" + userID);
			logger.info("EmpID =" + cwid);
			conn = getConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				emplEvalDetails = getEmployeeEvalDetails(cwid, conn, userID, "SPE2579");
				logger.info("emplEvalDetails =" + emplEvalDetails);

			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(emplEvalDetails.toString());
		}
	}

	public static JSONArray getEmployeeEvalDetails(String cwid, Connection oConnection, String userID, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		// JSONObject employeeEvalDetails = new JSONObject();

		JSONObject employeeEvalDetails;
		JSONArray jArray = new JSONArray();

		String lookupFields = "FIRST_NAME,LAST_NAME,MID,DESCR,HIRE_DT,DEPTNAME,SUPERVISORNAME,EXTENSION";
		String emplIDSQL = "Select A.Last_Name, A.First_Name,substr(A.Middle_Name, 1,1) as mid,  B.Descr, B.Hire_dt, B.deptname,  (Select supervisor_name from ful_ecm_reports_vw where b.reports_to = position_nbr) as SupervisorName, (Select extension from ful_ecm_reports_vw where b.reports_to = position_nbr) as Extension from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B WHERE A.EmplID = B.EmplID AND A.EmplID = Replace('<<Empl_ID>>' , '-', '')";
		String[] fields = lookupFields.split(",");

		// userIDSQL.replaceAll("<<getUser_ID>>",userID);
		emplIDSQL = emplIDSQL.replaceAll("<<getUser_ID>>", userID);
		emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);

		// logger.error("emplIDSQL ="+emplIDSQL);

		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			while (oRresultSet.next()) {
				employeeEvalDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					employeeEvalDetails.put(fields[i], oRresultSet.getString(fields[i]));
					
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

				}
				// oStatement.close();
				// oRresultSet.close();
			} catch (Exception exp) {

			}
		}

		// return employeeEvalDetails;
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