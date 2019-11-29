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
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getEmployeeUnit8Details" })
public class EmployeeEvaluationUnit8Servlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(EmployeeEvaluationUnit8Servlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userID = "";
		String cwid = "";
		JSONObject emplEvalDetails = null;
		if (req.getParameter("userID") != null && req.getParameter("userID") != "" && req.getParameter("cwid") != null
				&& req.getParameter("cwid") != "") {
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
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(emplEvalDetails.toString());
		}
	}

	public static JSONObject getEmployeeEvalDetails(String cwid, Connection oConnection, String userID, String docType)
			throws Exception {

		ResultSet oRresultSet = null;
		JSONObject employeeEvalDetails = new JSONObject();
		String lookupFields = "FIRST_NAME,LAST_NAME,DEPTID,DEPTNAME,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,UNION_CD,EMPUSERID,ADMINUSERID,ADMINFULLNAME";

		String emplIDSQL = "SELECT A.FIRST_NAME, A.LAST_NAME, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, B.UNION_CD,"
				+ " D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS SupervisorTitle, E.USERID AS EMPUSERID,(Select USERID from cmsrda.ful_emp_cwid_nt_name "
				+ "where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (select REPORTS_TO from FUL_ECM_JOB_VW "
				+ "where emplid='<<Empl_ID>>'))) as MANAGERUSERID, (Select USERID from cmsrda.ful_emp_cwid_nt_name "
				+ "where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (Select REPORTS_TO from FUL_ECM_JOB_VW where "
				+ "EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (select REPORTS_TO from FUL_ECM_JOB_VW where emplid='<<Empl_ID>>'))))) as ADMINUSERID, (Select (FNAME || ' ' || LNAME)  from cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (Select REPORTS_TO from FUL_ECM_JOB_VW where EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (select REPORTS_TO from FUL_ECM_JOB_VW where emplid='<<Empl_ID>>'))))) as ADMINFULLNAME FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT "
				+ "JOIN cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID  WHERE B.EMPLID = '<<Empl_ID>>'";
		
//		String emplIDSQL = "Select A.FIRST_NAME, A.LAST_NAME, B.DEPTID, B.DEPTNAME,  B.EMPL_RCD, B.DESCR, B.GRADE,  "
//				+ "(Select SUPERVISOR_NAME from ful_ecm_reports_vw where b.reports_to = position_nbr) as SUPERVISOR_NAME,  "
//				+ "(Select WORKING_TITLE from ful_ecm_reports_vw where reports_to = position_nbr) as WORKING_TITLE FROM FUL_ECM_PERS_VW A,"
//				+ " FUL_ECM_JOB_VW B WHERE A.EMPLID = Replace('806225686', '-', '') and A.EMPLID = B.EMPLID";
		String[] fields = lookupFields.split(",");

		// userIDSQL.replaceAll("<<getUser_ID>>",userID);
		emplIDSQL = emplIDSQL.replaceAll("<<getUser_ID>>", userID);
		emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);

		// LogManager.traceInfoMsg(sClassName, methodName, "Employee ID SQL : "
		// + emplIDSQL);

		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			if (oRresultSet.next()) {
				for (int i = 0; i < fields.length; i++) {
					employeeEvalDetails.put(fields[i], oRresultSet.getString(fields[i]));
				}
			}

		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			employeeEvalDetails = null;
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

		return employeeEvalDetails;
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