package com.aem.community.util;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
			logger.info("Get Email Function=" + empEmail);
		} catch (Exception oEx) {
			throw oEx;
		}
		return empEmail;
	}
}
