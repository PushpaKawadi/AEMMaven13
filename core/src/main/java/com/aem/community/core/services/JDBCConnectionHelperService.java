package com.aem.community.core.services;

import java.sql.Connection;

/**
 * @author 105876
 *
 */
public interface JDBCConnectionHelperService {
	Connection getFrmDBConnection();
	Connection getDocDBConnection();
	Connection getAemDEVDBConnection();
	Connection getAemProdDBConnection();
	Connection getDBConnection(String datasourceName);
}
