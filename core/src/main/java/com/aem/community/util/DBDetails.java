package com.aem.community.util;

import java.sql.Connection;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;

@Component

public class DBDetails {
	private final static Logger logger = LoggerFactory
			.getLogger(DBDetails.class);

	@Reference
	public static DataSourcePool source;

	public static Connection getConnection(String dataSourceName) {
		DataSource dataSource = null;
		Connection con = null;
		logger.error("Method dataSource=");
		try {
			// Inject the DataSourcePool right here!
			logger.error("Inside Try Method");
			dataSource = (DataSource) source.getDataSource("frmmgrprod");
			logger.error("dataSource="+dataSource);
			con = dataSource.getConnection();
			logger.info("Connection Anagha=" + con);
			//return con;

		} catch (Exception e) {
			logger.info("Conn Exception Anagha=" + e.getMessage());
			e.printStackTrace();
		} 
//		finally {
//			try {
//				if (con != null) {
//					logger.info("Conn Exec=");
//				}
//			} catch (Exception exp) {
//				logger.error("Finally Exec=" + exp);
//				exp.printStackTrace();
//			}
//		}
		return con;
	}

//	public static void closeConnection(Connection dbConn) throws Exception {
//		if (dbConn != null && !dbConn.isClosed()) {
//			dbConn.close();
//		}
//	}

}
