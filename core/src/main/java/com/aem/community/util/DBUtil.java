package com.aem.community.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {
	private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

	public void insertAutitTrace(Connection conn,
			LinkedHashMap<String, Object> dataMap, String tableName) {
		PreparedStatement preparedStmt = null;
		logger.error("conn=" + conn);
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				logger.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
				logger.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}
			// String tableName = tableName;
			StringBuilder sql = new StringBuilder("INSERT INTO  ").append(
					tableName).append(" (");
			StringBuilder placeholders = new StringBuilder();
			for (Iterator<String> iter = dataMap.keySet().iterator(); iter
					.hasNext();) {
				sql.append(iter.next());
				placeholders.append("?");
				if (iter.hasNext()) {
					sql.append(",");
					placeholders.append(",");
				}
			}
			sql.append(") VALUES (").append(placeholders).append(")");
			logger.error("SQL=" + sql.toString());
			try {
				preparedStmt = conn.prepareStatement(sql.toString());
			} catch (SQLException e1) {
				logger.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
				logger.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}
			int i = 0;
			// logger.info("Datamap values=" + dataMap.values());

			try {
				for (Object value : dataMap.values()) {
					if (value instanceof Date) {
						preparedStmt.setDate(++i, (Date) value);
					} else if (value instanceof Timestamp) {
						preparedStmt.setTimestamp(++i, (Timestamp) value);
					} else if (value instanceof Integer) {
						preparedStmt.setInt(++i, (Integer) value);
					} else if (value instanceof Float) {
						preparedStmt.setFloat(++i, (Float) value);
					} else {
						if (value != "" && value != null) {
							preparedStmt.setString(++i, value.toString());
						} else {
							preparedStmt.setString(++i, null);
						}
					}
				}
			} catch (SQLException e) {
				logger.error("SQLException=" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logger.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}

			try {
				logger.info("Before Audit Trace");
				preparedStmt.execute();
				conn.commit();
				logger.info("After Audit Trace");
			} catch (SQLException e1) {
				logger.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
				logger.error("Exception=" + e.getMessage());
				e.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						conn.close();
					} catch (SQLException e) {
						logger.error("SQLException=" + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						logger.error("Exception=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
