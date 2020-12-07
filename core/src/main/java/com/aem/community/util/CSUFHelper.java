package com.aem.community.util;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSUFHelper {
	private final static Logger logger = LoggerFactory
			.getLogger(CSUFHelper.class);

	public static String getEmailID(Connection oConnection, String cwid)
			throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		String empEmail = "";
		try {

			String getEmailSql = CSUFConstants.getEmailAddressCwidLookup;
			getEmailSql = getEmailSql.replaceAll("<<Emp_ID>>", cwid);
			oStatement = oConnection.createStatement();

			oRresultSet = oStatement.executeQuery(getEmailSql);
			if (oRresultSet.next()) {
				empEmail = oRresultSet.getString("EMAILID");
			}
			logger.info("Get getEmailID Function=" + empEmail);
		} catch (Exception oEx) {
			throw oEx;
		}
		return empEmail;
	}
	public static JSONObject getManagerDetails(Connection oConnection, String empID,String union_cd, String deptId )
			throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		JSONObject managerUserArray = new JSONObject();
		try {

			String getManagerDetailsSql = CSUFConstants.getManagerDetails;
			getManagerDetailsSql = getManagerDetailsSql.replaceAll("<<EMP_ID>>", empID);
			getManagerDetailsSql = getManagerDetailsSql.replaceAll("<<DEPT_ID>>", deptId);
			getManagerDetailsSql = getManagerDetailsSql.replaceAll("<<UNION_CD>>", union_cd);
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(getManagerDetailsSql);
			if (oRresultSet.next()) {
				managerUserArray.put("MANAGER_EMP_USERID",oRresultSet.getString("MANAGER_EMP_USERID"));
				managerUserArray.put("MANAGER_NAME",oRresultSet.getString("SUPERVISORNAME"));
			}
			logger.info("Get Email Function=" + managerUserArray);
		} catch (Exception oEx) {
			throw oEx;
		}
		return managerUserArray;
	}
	
	public static String getEmailIDBasedOnUserID(Connection oConnection, String userId)
			throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		String empEmail = "";
		try {

			String getEmailSql = CSUFConstants.getEmailAddressUserIdLookup;
			getEmailSql = getEmailSql.replaceAll("<<UID>>", userId);
			oStatement = oConnection.createStatement();

			oRresultSet = oStatement.executeQuery(getEmailSql);
			if (oRresultSet.next()) {
				empEmail = oRresultSet.getString("EMAILID");
			}
			logger.info("Get getEmailIDBasedOnUserID Function=" + empEmail);
		} catch (Exception oEx) {
			throw oEx;
		}
		return empEmail;
	}
	
	public static JSONObject getEmployeeDetails(Connection oConnection, String cwid)
			throws Exception {
		ResultSet oRresultSet = null;
		Statement oStatement = null;
		JSONObject empUserArray = new JSONObject();
		try {

			String getEmpDetailsSql = CSUFConstants.getEmployeeDetails;
			getEmpDetailsSql = getEmpDetailsSql.replaceAll("<<EMP_ID>>", cwid);
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(getEmpDetailsSql);
			if (oRresultSet.next()) {
				empUserArray.put("EMP_USERID",oRresultSet.getString("EMP_USERID"));
				empUserArray.put("EMP_NAME",oRresultSet.getString("EMP_NAME"));
			}
			logger.info("Get Employee Details Function=" + empUserArray);
		} catch (Exception oEx) {
			throw oEx;
		}
		return empUserArray;
	}
}
