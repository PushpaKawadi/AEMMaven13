package com.aem.community.core.services.impl;

import java.sql.Connection;
import javax.sql.DataSource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;


@Component(service = JDBCConnectionHelperService.class)
public class JDBCConnectionHelperServiceImpl implements JDBCConnectionHelperService {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private DataSourcePool source;

	// Returns a connection using the configured DataSourcePool
	@Override
	public Connection getFrmDBConnection() {
		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool
			dataSource = (DataSource) source.getDataSource("frmmgrprod");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			log.error(e.getMessage() + " Exception ");
		}
		return null;
	}

	@Override
	public Connection getDocDBConnection() {
		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool
			dataSource = (DataSource) source.getDataSource("docmgrprod");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			log.error(e.getMessage() + " Exception ");
		}
		return null;
	}
	
	@Override
	public Connection getAemDEVDBConnection() {
		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool
			dataSource = (DataSource) source.getDataSource("AEMDBDEV");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			log.error(e.getMessage() + " Exception ");
		}
		return null;
	}
}
