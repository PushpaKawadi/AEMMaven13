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
import com.adobe.granite.workflow.model.WorkflowModel;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Workflow History",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Save Workflow History" })
public class SaveWorkflowHistory implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(SaveWorkflowHistory.class);

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {

		Connection conn = null;
		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);

		String payloadPath = workItem.getWorkflowData().getPayload().toString();

		String paramsValue = ((String) processArguments.get("PROCESS_ARGS", "string")).toString();
		log.info("params value=" + paramsValue);
		String[] parameters1 = paramsValue.split(",");
		log.info("parameters1=" + parameters1);
		String param1 = parameters1[0];
		String param2 = parameters1[1];
		log.info("Param1=" + param1);
		log.info("Param12=" + param2);
		String actionTaken = "";
		actionTaken = param2;
		String param = paramsValue;
		LinkedHashMap<String, Object> dataMap = null;

		Document doc = null;
		InputStream is = null;
		String workflowInstance = "";

		String workflowInitiator = "";
		String workflowName = "";
		String workItemId = "";
		String caseId = "";
		String cwid = "";
		Timestamp stepCompleteTime = null;
		Timestamp stepStartTime = null;
		String assignee = "";
		String stepType = "";
		String stepResponse = "";
		String stepName = "";
		String comments = "";
		String sendBackResponse = "";
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		String wfInstanceID = workItem.getWorkflow().getId(); // Workflow instance id
		log.info("Workflow InstanceID ==" + wfInstanceID);
		String workflowID = workItem.getId(); // workitem id
		log.info("WorkflowID==" + workflowID);

		String wId = workflowID.replace("VolatileWorkItem_", "/workItems/");
		// String workItemID = "";
		log.info("WorkItem Id==" + wId);
		while (xmlFiles.hasNext()) {

			workflowInstance = workItem.getWorkflow().getId();
			payloadPath = workItem.getWorkflowData().getPayload().toString();
			for (Map.Entry<String, Object> entry : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
				if (entry.getKey().matches(actionTaken)) {
					for (Map.Entry<String, Object> entry1 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry1.getKey().matches("empAction")) {
							if (entry1.getValue().toString().equals("disagree")) {
								sendBackResponse = "Disagree";
								if (param1.equalsIgnoreCase("End of the Workflow Assign Step")) {
								entry1.setValue("");
								}
							} else {
								sendBackResponse = "";
							}
						}
					}
					if (sendBackResponse.equals("Disagree")) {
						stepResponse = sendBackResponse;
						
					} else {
						stepResponse = entry.getValue().toString();
					}
				}
			}
			workflowName = workItem.getWorkflow().getWorkflowModel().getId();
			stepStartTime = new java.sql.Timestamp(System.currentTimeMillis());
			workflowInitiator = workItem.getWorkflow().getInitiator();
			Resource attachmentXml = xmlFiles.next();
			log.info("xmlFiles inside ");
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath);
			// if (filePath.contains("Data.xml")) {
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("xmlFiles=" + filePath);
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
				} catch (ValueFormatException e2) {
					log.error("Exception1=" + e2.getMessage());
					e2.printStackTrace();
				} catch (PathNotFoundException e2) {
					log.error("Exception2=" + e2.getMessage());
					e2.printStackTrace();
				} catch (RepositoryException e2) {
					log.error("Exception3=" + e2.getMessage());
					e2.printStackTrace();
				}

				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = null;
					try {
						dBuilder = dbFactory.newDocumentBuilder();
					} catch (ParserConfigurationException e1) {
						log.info("ParserConfigurationException=" + e1);
						e1.printStackTrace();
					}
					try {
						doc = dBuilder.parse(is);
					} catch (IOException e1) {
						log.info("IOException=" + e1);
						e1.printStackTrace();
					}
					org.w3c.dom.NodeList nList1 = doc.getElementsByTagName("afUnboundData");
					for (int temp = 0; temp < nList1.getLength(); temp++) {
						org.w3c.dom.Node nNode1 = nList1.item(temp);
						if (nNode1.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element eElement1 = (org.w3c.dom.Element) nNode1;
							if (eElement1.hasAttribute("caseId")) {
								caseId = eElement1.getElementsByTagName("caseId").item(0).getTextContent();

							} else {
								caseId = "";
							}

						}
					}

					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);
						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							cwid = eElement.getElementsByTagName("EmpID").item(0).getTextContent();
							String stage = eElement.getElementsByTagName("StageIndicator").item(0).getTextContent();
							if (stage.equals("ToManager")) {
								assignee = eElement.getElementsByTagName("ManagerUserID").item(0).getTextContent();
								stepName = "Manager Review";

								if (!(eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("EvaluatorComment").item(0)
											.getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToManagerAcknowledge")) {
								assignee = eElement.getElementsByTagName("ManagerUserID").item(0).getTextContent();
								stepName = "Evaluation Final - Manager";
								if (!(eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("EvaluatorComment").item(0)
											.getTextContent();
								} else {
									comments = null;
								}

							}
							if (stage.equals("ToManagerFinalAcknowledge")) {
								assignee = eElement.getElementsByTagName("ManagerUserID").item(0).getTextContent();
								stepName = "Manager Final Evaluation";
								if (!(eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("EvaluatorComment").item(0)
											.getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToManagerHRDI")) {
								assignee = eElement.getElementsByTagName("ManagerUserID").item(0).getTextContent();
								stepName = "Manager HRDI Changes";
								if (!(eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("EvaluatorComment").item(0)
											.getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToHRCoo")) {
								assignee = eElement.getElementsByTagName("HrCoordId").item(0).getTextContent();
								stepName = "HR Coordinator";
								if (!(eElement.getElementsByTagName("HRCoordinatorSignComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("HRCoordinatorSignComment").item(0)
											.getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToEmployee")) {
								assignee = eElement.getElementsByTagName("EmpUserID").item(0).getTextContent();
								stepName = "Employee Review";
								if (!(eElement.getElementsByTagName("EmpComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("EmpComment").item(0).getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToEmployeeAck")) {
								assignee = eElement.getElementsByTagName("EmpUserID").item(0).getTextContent();
								stepName = "Employee Acknowledgement";
								if (!(eElement.getElementsByTagName("EmpComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("EmpComment").item(0).getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToAdmin")) {
								assignee = eElement.getElementsByTagName("AdminUserID").item(0).getTextContent();
								stepResponse = "Send To Appropriate Administrator";
								stepName = "Appropriate Administrator Review";
								if (!(eElement.getElementsByTagName("AdminComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("AdminComment").item(0).getTextContent();
								} else {
									comments = null;
								}
							}
							if (stage.equals("ToHRDI")) {
								// assignee =
								// eElement.getElementsByTagName("EmpUserID").item(0).getTextContent();
								assignee = "HR-Reviewers";
								stepName = "HRDI Review";
								if (!(eElement.getElementsByTagName("HRDIComment").item(0).getTextContent()
										.equals(null))) {
									comments = eElement.getElementsByTagName("HRDIComment").item(0).getTextContent();
								} else {
									comments = null;
								}
							}

						}
					}

				} catch (SAXException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

				if (param1.equalsIgnoreCase("Start of the Workflow Assign Step")) {
					stepCompleteTime = null;
					comments = null;
					stepType = "STEPSTART";
					String firstStr = wId.substring(0, wId.indexOf('_'));
					String secString = wId.substring(wId.indexOf('_') + 1, wId.length());
					String t1 = firstStr.replaceAll("[^0-9]+", "");
					int a1 = Integer.parseInt(t1);
					a1++; // Process step is one step behind the Assign task, so increment it.
					firstStr = firstStr.replaceAll(t1, String.valueOf(a1));
					workItemId = workflowInstance.concat(firstStr).concat("_").concat(secString);
					log.info("Final WorkItemID ==" + workItemId);
				}
				if (param1.equalsIgnoreCase("End of the Workflow Assign Step")) {
					stepCompleteTime = new java.sql.Timestamp(System.currentTimeMillis());
					stepType = "STEPEND";

					String firstStr = wId.substring(0, wId.indexOf('_'));
					String secString = wId.substring(wId.indexOf('_') + 1, wId.length());
					String t1 = firstStr.replaceAll("[^0-9]+", "");
					int a1 = Integer.parseInt(t1);
					a1--; // Process step is one step behind the Assign task, so increment it.
					firstStr = firstStr.replaceAll(t1, String.valueOf(a1));
					workItemId = workflowInstance.concat(firstStr).concat("_").concat(secString);
					log.info("Final WorkItemID ==" + workItemId);
				}

				dataMap = new LinkedHashMap<String, Object>();
				dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstance);
				dataMap.put("WORKITEM_ID", workItemId);
				dataMap.put("WORKFLOW_PAYLOAD", payloadPath);
				dataMap.put("WORKFLOW_MODEL_NAME", workflowName);
				dataMap.put("CASE_ID", caseId);
				dataMap.put("CWID", cwid);
				dataMap.put("STEP_START_TIME", stepStartTime);
				dataMap.put("WORKFLOW_INITIATOR", workflowInitiator);
				dataMap.put("ASSIGNEE", assignee);
				dataMap.put("STEP_COMPLETE_TIME", stepCompleteTime);
				dataMap.put("STEP_TYPE", stepType);
				dataMap.put("STEP_RESPONSE", stepResponse);
				dataMap.put("STEP_NAME", stepName);
				dataMap.put("COMMENTS", comments);
				conn = jdbcConnectionService.getAemDEVDBConnection();
				// conn = getConnection();
				if (conn != null) {
					log.info("Connection Successfull");
					insertWFHistory(conn, dataMap);
				}
			}

		}

	}

	public void insertWFHistory(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_WORKFLOW_HISTORY";
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
				log.info("Before insert workflow history");
				preparedStmt.execute();
				conn.commit();
				log.info("End insert workflow history");
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
