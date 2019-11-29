
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
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getEmployeeLookupUnit4" })
public class EmployeeEvaluationUnit4Servlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFEmployeeLookUpServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String userID = "";
		String cwid = "";
		// JSONObject emplEvalDetails = null;
		JSONArray emplEvalDetails = null;
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

		String lookupFields = "FIRST_NAME,LAST_NAME,DEPTID,DEPTNAME,EMPL_RCD,EMPLID,DESCR,GRADE,SupervisorName,SupervisorTitle,UNION_CD,EMPUSERID,ADMINUSERID,ADMINFULLNAME";

		// String emplIDSQL = "SELECT A.FIRST_NAME, A.LAST_NAME, B.DEPTID,
		// B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, B.UNION_CD,"
		// + " D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS
		// SupervisorTitle, E.USERID AS EMPUSERID,(Select USERID from
		// cmsrda.ful_emp_cwid_nt_name "
		// + "where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR
		// = (select REPORTS_TO from FUL_ECM_JOB_VW "
		// + "where emplid='<<Empl_ID>>'))) as MANAGERUSERID, (Select USERID
		// from cmsrda.ful_emp_cwid_nt_name "
		// + "where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR
		// = (Select REPORTS_TO from FUL_ECM_JOB_VW where "
		// + "EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR =
		// (select REPORTS_TO from FUL_ECM_JOB_VW where
		// emplid='<<Empl_ID>>'))))) as ADMINUSERID, (Select (FNAME || ' ' ||
		// LNAME) from cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID
		// from FUL_ECM_JOB_VW where POSITION_NBR = (Select REPORTS_TO from
		// FUL_ECM_JOB_VW where EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where
		// POSITION_NBR = (select REPORTS_TO from FUL_ECM_JOB_VW where
		// emplid='<<Empl_ID>>'))))) as ADMINFULLNAME FROM FUL_ECM_JOB_VW B LEFT
		// JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN
		// FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT "
		// + "JOIN cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID WHERE
		// B.EMPLID = '<<Empl_ID>>'";

		String emplIDSQL = "SELECT A.FIRST_NAME, A.LAST_NAME, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD,A.EMPLID, B.DESCR, B.GRADE, B.UNION_CD, D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS SupervisorTitle, E.USERID AS EMPUSERID,(Select USERID from cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR in (select REPORTS_TO from FUL_ECM_JOB_VW where emplid='<<Empl_ID>>'))) as MANAGERUSERID, (Select USERID from cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (Select REPORTS_TO from FUL_ECM_JOB_VW where EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR in (select REPORTS_TO from FUL_ECM_JOB_VW where emplid='<<Empl_ID>>'))))) as ADMINUSERID, (Select (FNAME || ' ' || LNAME)  from cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR in (Select REPORTS_TO from FUL_ECM_JOB_VW where EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR in (select REPORTS_TO from FUL_ECM_JOB_VW where emplid='<<Empl_ID>>'))))) as ADMINFULLNAME FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID  WHERE B.EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL";

		// String emplIDSQL ="SELECT A.FIRST_NAME, A.LAST_NAME, B.DEPTID,
		// B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, B.UNION_CD,
		// D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS
		// SupervisorTitle, E.USERID AS EMPUSERID,(Select USERID from
		// cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID from
		// FUL_ECM_JOB_VW where POSITION_NBR in (select REPORTS_TO from
		// FUL_ECM_JOB_VW where emplid='<<Empl_ID>>'))) as MANAGERUSERID,
		// (Select USERID from cmsrda.ful_emp_cwid_nt_name where CWID = (Select
		// EMPLID from FUL_ECM_JOB_VW where POSITION_NBR = (Select REPORTS_TO
		// from FUL_ECM_JOB_VW where EMPLID =(Select EMPLID from FUL_ECM_JOB_VW
		// where POSITION_NBR in (select REPORTS_TO from FUL_ECM_JOB_VW where
		// emplid='<<Empl_ID>>'))))) as ADMINUSERID, (Select (FNAME || ' ' ||
		// LNAME) from cmsrda.ful_emp_cwid_nt_name where CWID = (Select EMPLID
		// from FUL_ECM_JOB_VW where POSITION_NBR in (Select REPORTS_TO from
		// FUL_ECM_JOB_VW where EMPLID =(Select EMPLID from FUL_ECM_JOB_VW where
		// POSITION_NBR in (select REPORTS_TO from FUL_ECM_JOB_VW where
		// emplid='<<Empl_ID>>'))))) as ADMINFULLNAME FROM FUL_ECM_JOB_VW B LEFT
		// JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN
		// FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN
		// cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID WHERE B.EMPLID =
		// '<<Empl_ID>>'";

		String[] fields = lookupFields.split(",");

		// userIDSQL.replaceAll("<<getUser_ID>>",userID);
		emplIDSQL = emplIDSQL.replaceAll("<<getUser_ID>>", userID);
		emplIDSQL = emplIDSQL.replaceAll("<<Empl_ID>>", cwid);

		// logger.error("emplIDSQL ="+emplIDSQL);

		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			// logger.error("oRresultSet ="+oRresultSet);

			// if (oRresultSet.next()) {
			// while (oRresultSet.next()) {
			// for (int i = 0; i < fields.length; i++) {
			// employeeEvalDetails.put(fields[i],
			// oRresultSet.getString(fields[i]));
			// }
			// }

			while (oRresultSet.next()) {

				employeeEvalDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					employeeEvalDetails.put(fields[i], oRresultSet.getString(fields[i]));
					logger.error("employeeEvalDetails =" + employeeEvalDetails);
				}
				jArray.put(employeeEvalDetails);
			}
			logger.error("oRresultSet=" + jArray);
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