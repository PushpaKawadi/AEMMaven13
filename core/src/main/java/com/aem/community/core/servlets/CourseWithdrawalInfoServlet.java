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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Student Course Withdrawal Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/getStudentCourseWithdrawalInfo" })
public class CourseWithdrawalInfoServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CourseWithdrawalInfoServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String cwid = "";
		String term = "";
		JSONObject emplEvalDetails = null;
		if (req.getParameter("cwid") != null && !req.getParameter("cwid").trim().equals("")
				&& req.getParameter("term") != null && !req.getParameter("term").trim().equals("")) {
			cwid = req.getParameter("cwid");
			term = req.getParameter("term");
			logger.info("Cwid =" + cwid);
			logger.info("Term =" + term);
			conn = getConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				emplEvalDetails = getStudentCourseWithdrawalInfo(cwid, conn, term);
				// emplEvalDetails = getStudentCourseWithdrawalInfo("802886937",
				// conn, "2185");
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			if (emplEvalDetails != null && !emplEvalDetails.equals("")) {
				response.getWriter().write(emplEvalDetails.toString());
			} else {
				response.getWriter().write("Requested Data Unavailable");
			}
		} else {
			logger.info("Could Not Connect DB");
		}
	}

	@Reference
	private DataSourcePool source;

	private Connection getConnection() {
		logger.info("Inside Get Connection");

		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool right here!
			dataSource = (DataSource) source.getDataSource("docmgrprod");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			// logger.error("Conn Exception=" + e);
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					logger.info("Conn Exec=");
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Executes SQL based on the user id and retrieves lookup information. Used in
	 * Self lookup FEB forms
	 * 
	 * @param cwid
	 *            - CWID of employee
	 * @param oConnection
	 *            - Database connection
	 * @return - JSONObject of key value pairs consisting of the lookup data
	 * @throws Exception
	 */
	public static JSONObject getStudentCourseWithdrawalInfo(String cwid, Connection oConnection, String term)
			throws Exception {

		logger.error("Inside getStudentCourseWithdrawalInfo");

		ResultSet oRresultSet = null;
		JSONObject studentInfo = new JSONObject();
		JSONArray courseInfoArray = new JSONArray();
		JSONObject courseInfo = new JSONObject();

		Statement oStatement = null;
		try {
			String studentCourseInfoSQL = "Select * from AR_COURSE_WITHDRAWAL where CWID = '<<CWID>>' and STRM = '<<TERM>>'";

			// Get current term details
			//Modified by Swathi on 05192020 from line no 149 - 153
			//String[] termInfo = getCurrentTerm(oConnection);

			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll("<<CWID>>", cwid);
			//studentCourseInfoSQL = studentCourseInfoSQL.replaceAll("<<TERM>>", termInfo[0]);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll("<<TERM>>", term);
			
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);

			boolean isStudentInfoSet = false;
			int i = 0;
			while (oRresultSet.next()) {

				if (!isStudentInfoSet) {

					studentInfo.put("CWID", oRresultSet.getString("CWID"));
					studentInfo.put("FNAME", oRresultSet.getString("FNAME"));
					studentInfo.put("LNAME", oRresultSet.getString("LNAME"));
					studentInfo.put("MAJOR_DESCR", oRresultSet.getString("MAJOR_DESCR"));
					studentInfo.put("DEGREE_TYPE", oRresultSet.getString("DEGREE_TYPE"));

					// Hardcoding below values for testing purpose.
					// When SQL integration is complete, please remove hard
					// coded values
					// studentInfo.put("STUDENT_EMAIL", "mshaner@fullerton.edu");
					// studentInfo.put("STUDENT_PHONE", "7457891080");
					studentInfo.put("STUDENT_EMAIL", oRresultSet.getString("STUDENT_EMAIL"));
					studentInfo.put("STUDENT_PHONE", oRresultSet.getString("STUDENT_PHONE"));
					studentInfo.put("INTERNATIONAL_FLAG", oRresultSet.getString("INTERNATIONAL_FLAG"));
					studentInfo.put("ACADEMIC_PLAN", oRresultSet.getString("ACADEMIC_PLAN"));
					studentInfo.put("PROGRAM_PLAN", oRresultSet.getString("PROGRAM_PLAN"));
					studentInfo.put("STRM", oRresultSet.getString("STRM"));
					// studentInfo.put("DESCR TERM_DESCR",oRresultSet.getString("DESCR
					// TERM_DESCR"));
					studentInfo.put("DESCR TERM_DESCR", oRresultSet.getString("TERM_DESCR"));
					studentInfo.put("NURSING_FLAG", oRresultSet.getString("NURSING_FLAG"));
					// studentInfo.put("EXPECTED_GRD_DATE",
					// oRresultSet.getString("EXPECTED_GRAD_DATE"));
					studentInfo.put("TERM_CODE", oRresultSet.getString("STRM"));
					studentInfo.put("TERM_DESCR", oRresultSet.getString("TERM_DESCR"));

					try {
						if (oRresultSet.getString("EIP_FLAG") != null) {
							studentInfo.put("EIP_FLAG", oRresultSet.getString("EIP_FLAG"));
						}
					} catch (Exception e1) {
						logger.error("Failed to get EIP flag." + e1.getMessage());
						e1.getStackTrace();
					}

					isStudentInfoSet = true;

				}

				// Course Info
				courseInfo = new JSONObject();

				courseInfo.put("CLASS_NBR", oRresultSet.getString("CLASS_NBR"));
				courseInfo.put("CRSE_NAME", oRresultSet.getString("CRSE_NAME"));
				courseInfo.put("UNT_TAKEN", oRresultSet.getString("UNT_TAKEN"));
				courseInfo.put("INSTR_NAME", oRresultSet.getString("INSTR_NAME"));
				courseInfo.put("INSTR_CWID", oRresultSet.getString("INSTR_CWID"));
				courseInfo.put("CRSE_ID", oRresultSet.getString("CRSE_ID"));
				courseInfo.put("INSTR_USERID", oRresultSet.getString("INSTR_USERID"));
				courseInfo.put("INSTR_EMAIL", oRresultSet.getString("INSTR_EMAIL"));
				courseInfo.put("CLASS_SECTION", oRresultSet.getString("CLASS_SECTION"));

				String courseID = oRresultSet.getString("CRSE_ID");
				String courseName = oRresultSet.getString("CRSE_NAME");

				logger.info("courseID=" + courseID);
				logger.info("courseName=" + courseName);

				String[] chairInfo = getChairInfo(oConnection, courseID, courseName);
				// String[] chairInfo = getChairInfo(oConnection, courseID);

				courseInfo.put("CHAIR_NAME", chairInfo[1]);
				// courseInfo.put("CHAIR_CWID", "806225686");
				courseInfo.put("CHAIR_USERID", chairInfo[2]);
				courseInfo.put("CHAIR_EMAIL", chairInfo[3]);

				// Hard coding below values for testing purpose.
				// When SQL integration is complete, please remove hard coded
				// values

				// courseInfo.put("INSTR_USERID", "nvadlakunta");
				// courseInfo.put("INSTR_EMAIL", "nvadlakunta@fullerton.edu");

				courseInfoArray.put(i, courseInfo);
				i++;
			}

			if (!studentInfo.isNull("CWID")) {

				// Get the unique Case ID from database sequence
				String caseID = getCaseID(oConnection);
				studentInfo.put("CASEID", caseID);

				// Current Term details added to JSON response
				//Modified by Swathi on 05192020 from line no 247 - 248
				//studentInfo.put("TERM_CODE", termInfo[0]);
				//studentInfo.put("TERM_DESCR", termInfo[1]);

				// Possible values :
				// Yes
				// No
				studentInfo.put("MCBE", "No");

				// Add Courses JSON array to student Info JSON object
				studentInfo.put("COURSES", courseInfoArray);
			} else {
				studentInfo = null;
			}
			logger.info("studentInfo=" + studentInfo);
		} catch (Exception oEx) {
			// logger.error(m_strClassName, methodName, oEx.getMessage());
			// logger.error(m_strClassName, methodName, oEx.getMessage(),oEx);
			studentInfo = null;

		} finally {
			try {
				if (oStatement != null)
					oStatement.close();
				if (oConnection != null) {
					oConnection.close();
				}
			} catch (Exception exp) {
				exp.getStackTrace();

			}
		}

		return studentInfo;
	}

	/**
	 * Gets the term details from the database
	 * 
	 * @param oConnection
	 *            - Database connection
	 * @return - term details as String array. term[0] = term code ex:2185 term[1] =
	 *         term description ex: Summer 2018
	 * @throws Exception
	 */
	private static String[] getCurrentTerm(Connection oConnection) throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;

		String[] term = new String[2];

		try {

			//String sql = "Select distinct STRM,DESCR from SYSADM.PS_TERM_TBL@DBL_CBFULTRS where TERM_BEGIN_DT <= sysdate and TERM_END_DT >=sysdate";
			//Added on 09022020
			String sql = "Select distinct STRM,DESCR from SYSADM.PS_TERM_TBL@DBL_CFULPRD where TERM_BEGIN_DT <= sysdate and TERM_END_DT >=sysdate";
			
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(sql);

			if (oRresultSet.next()) {
				term[0] = oRresultSet.getString("STRM");
				term[1] = oRresultSet.getString("DESCR");
			}
		} catch (Exception oEx) {
			// LogManager.traceErrMsg(m_strClassName, methodName, "Failed to get
			// term details from database. " + oEx.getMessage());
			// LogManager.traceMethodException(m_strClassName, methodName,
			// oEx.getMessage(),oEx);
			throw oEx;

		}
		/*
		 * finally { try { if (oStatement != null) oStatement.close();
		 * oRresultSet.close(); } catch (Exception exp) {
		 * 
		 * } }
		 */

		return term;

	}

	/**
	 * Gets the Unique case ID from Database sequence
	 * 
	 * @param oConnection
	 *            - Database connection
	 * @return - Case ID as string
	 * @throws Exception
	 */
	public static String getCaseID(Connection oConnection) throws Exception {

		// String sClassName = "DBManager";
		// String methodName = "getCaseID";
		// LogManager.traceInfoMsg(sClassName, methodName, "Inside getCaseID");

		ResultSet oRresultSet = null;
		Statement oStatement = null;

		long caseID = 0;

		try {

			String sql = "Select AR_COURSE_WITHDRAWAL_SEQ.nextval as CASEID from dual";
			logger.info("CaseID Function");
			// LogManager.traceInfoMsg(sClassName, methodName, "sql : " + sql);

			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(sql);

			if (oRresultSet.next()) {
				caseID = oRresultSet.getLong("CASEID");
			}
			logger.info("CaseID Function=" + caseID);
		} catch (Exception oEx) {
			// LogManager.traceErrMsg(m_strClassName, methodName, "Failed to
			// Case ID from database. " + oEx.getMessage());
			// LogManager.traceMethodException(m_strClassName, methodName,
			// oEx.getMessage(),oEx);
			throw oEx;

		}
		/*
		 * finally { try { if (oStatement != null) oStatement.close();
		 * oRresultSet.close(); } catch (Exception exp) {
		 * 
		 * } }
		 */

		return String.valueOf(caseID);

	}

	private static String[] getChairInfo(Connection oConnection, String courseId, String courseName) throws Exception {

		ResultSet oRresultSet = null;
		Statement oStatement = null;

		String[] chairVal = new String[4];

		try {
			// String studentCourseInfoSQL = "Select * from AR_COURSE_WITHDRAWL
			// where CWID = '<<CWID>>' and STRM = '2185'";

			String sql = "Select * from AR_Course_Chair_Info where CRSE_ID = '<<CRSE_ID>>' and LOWER(TRIM(CRSE_NAME))=LOWER(TRIM('<<courseName>>'))";

			// String sql = "Select * from AR_Course_Chair_Info where CRSE_ID =
			// '<<CRSE_ID>>'";

			sql = sql.replaceAll("<<CRSE_ID>>", courseId);
			sql = sql.replaceAll("<<courseName>>", courseName);

			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(sql);

			logger.info("oRresultSet Chair=" + oRresultSet);
			if (oRresultSet.next()) {
				chairVal[0] = oRresultSet.getString("CRSE_ID");
				chairVal[1] = oRresultSet.getString("EMPNAME");
				chairVal[2] = oRresultSet.getString("EMP_USERID");
				chairVal[3] = oRresultSet.getString("EMP_EMAIL");
			}
			logger.info("chair2=" + chairVal);
		} catch (Exception oEx) {
			// LogManager.traceErrMsg(m_strClassName, methodName, "Failed to get
			// term details from database. " + oEx.getMessage());
			// LogManager.traceMethodException(m_strClassName, methodName,
			// oEx.getMessage(),oEx);
			throw oEx;

		}
		/*
		 * finally { try { if (oStatement != null) oStatement.close();
		 * oRresultSet.close(); } catch (Exception exp) {
		 * 
		 * } }
		 */

		return chairVal;

	}

}
