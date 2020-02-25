package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Workflow Instance History",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Save Workflow Instance History" })
public class SaveInstanceHistory implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(SaveInstanceHistory.class);

//	@Reference
//	private JDBCConnectionHelperService jdbcConnectionService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		Connection conn = null;

		String paramsValue = ((String) processArguments.get("PROCESS_ARGS", "string")).toString();
		log.info("params value=======" + paramsValue);

		String param = paramsValue;

		log.info("parameter" + param);
		String workflowInstance = "";
		String payloadPath = "";
		String workflowInitiator = "";
		String workflowName = "";
		String workflowTitle = "";
		String workflowVersion = "";
		String workflowStatus = "";
		Timestamp workflowCompleteTime = null;
		Timestamp workflowStartTime = null;
		LinkedHashMap<String, Object> dataMap = null;

		if (param.equalsIgnoreCase("Start of the Workflow Instance")) {
			log.info("inside start");
			workflowInstance = workItem.getWorkflow().getId();
			payloadPath = workItem.getWorkflowData().getPayload().toString();
			workflowStartTime = new java.sql.Timestamp(workItem.getTimeStarted().getTime());
			workflowInitiator = workItem.getWorkflow().getInitiator();
			workflowName = workItem.getWorkflow().getWorkflowModel().getId();
			workflowTitle = workItem.getWorkflow().getWorkflowModel().getTitle();
			workflowVersion = workItem.getWorkflow().getWorkflowModel().getVersion();
			workflowStatus = workItem.getWorkflow().getState();
			log.info("workflow instance id=" + workflowInstance);
			log.info("payload path=" + payloadPath);
			log.info("workflow start time=" + workflowStartTime);
			log.info("workflow initiator=" + workflowInitiator);
			log.info("workflow model name=" + workflowName);
			log.info("workflow model title" + workflowTitle);
			log.info("workflow model version" + workflowVersion);
			log.info("workflow status" + workflowStatus);
			dataMap = new LinkedHashMap<String, Object>();
			dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstance);
			dataMap.put("WORKFLOW_PAYLOAD", payloadPath);
			dataMap.put("WORKFLOW_MODEL_NAME", workflowName);
			dataMap.put("WORKFLOW_START_TIME", workflowStartTime);
			dataMap.put("WORKFLOW_INITIATOR", workflowInitiator);
			dataMap.put("WORKFLOW_TITLE", workflowTitle);
			dataMap.put("WORKFLOW_COMPLETE_TIME", workflowCompleteTime);
			dataMap.put("WORKFLOW_STATUS", workflowStatus);
			dataMap.put("WORKFLOW_VERSION", Float.parseFloat(workflowVersion));
			// conn = jdbcConnectionService.getAemDEVDBConnection();
			conn = getConnection();
			if (conn != null) {
				log.info("Connection Successfull");
				insertWFInstanceHistory(conn, dataMap);
			}

		}
		if (param.equalsIgnoreCase("End of the Workflow Instance")) {
			workflowCompleteTime = new java.sql.Timestamp(System.currentTimeMillis());
			workflowStatus = "COMPLETED";
			workflowInstance = workItem.getWorkflow().getId();
			log.info("	" + workflowCompleteTime);
			log.info("workflow complete status" + workflowStatus);
			conn = getConnection();
			if (conn != null) {
				log.info("Connection Successfull");
				updateWFInstanceHistory(conn, workflowInstance, workflowCompleteTime, workflowStatus);
			}

		}

	}

	public void insertWFInstanceHistory(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_WORKFLOW_INSTANCE_HISTORY";
			StringBuilder sql = new StringBuilder("INSERT INTO  ").append(tableName).append(" (");
			StringBuilder placeholders = new StringBuilder();
			for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
				sql.append(iter.next());
				placeholders.append("?");
				if (iter.hasNext()) {
					sql.append(",");
					placeholders.append(",");
				}
			}
			sql.append(") VALUES (").append(placeholders).append(")");
			log.info("SQL=" + sql.toString());
			try {
				preparedStmt = conn.prepareStatement(sql.toString());
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			int i = 0;
			log.info("Datamap values=" + dataMap.values());
			for (Object value : dataMap.values()) {
				try {
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
				} catch (SQLException e) {
					log.error("SQLException=" + e.getMessage());
					e.printStackTrace();
				}
			}
			try {
				log.info("SQL statement=" + preparedStmt);
				log.info("Before insert instance history");
				preparedStmt.execute();
				conn.commit();

			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						conn.close();
					} catch (SQLException e) {
						log.error("SQLException=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void updateWFInstanceHistory(Connection conn, String instanceId, Timestamp completedDate, String status) {
		// String methodName = "updateEvent";
		PreparedStatement preparedStmt = null;

		if (conn != null) {
			String updateEventQuery = "UPDATE AEM_WORKFLOW_INSTANCE_HISTORY SET WORKFLOW_COMPLETE_TIME=? , WORKFLOW_STATUS=?  where WORKFLOW_INSTANCE_ID = '"
					+ instanceId + "'";
			log.info("SQL=" + updateEventQuery);
			try {

				preparedStmt = conn.prepareStatement(updateEventQuery);

				preparedStmt.setTimestamp(1, completedDate);

				preparedStmt.setString(2, status);

				preparedStmt.execute();

				conn.commit();

			} catch (SQLException e) {

				e.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						conn.close();
					} catch (SQLException e) {

						e.printStackTrace();
					}
				}

			}
		}
	}

	@Reference
	private DataSourcePool source;

	private Connection getConnection() {
		log.info("Inside Get Connection");

		DataSource dataSource = null;
		Connection con = null;
		try {
			dataSource = (DataSource) source.getDataSource("AEMDBDEV");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			log.error("Conn Exception=" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					log.info("Conn Exec=");
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return null;
	}
}
