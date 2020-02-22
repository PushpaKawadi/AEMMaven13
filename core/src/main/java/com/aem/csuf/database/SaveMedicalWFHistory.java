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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Course1",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=WF Medical History" })
public class SaveMedicalWFHistory implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(SaveMedicalWFHistory.class);

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();

		String paramsValue = ((String) processArguments.get("PROCESS_ARGS",
				"string")).toString();
		log.error("paramsnew=======" + paramsValue); // After Instructor Review
		LinkedHashMap<String, Object> dataMap = null;

		Document doc = null;
		InputStream is = null;
		String firstName = null;
		String lastName = null;
		String major = "";
		String termCode = null;
		String typeOfForm = null;
		String sID = null;
		String termDesc = null;
		String allCourseWithdrawal = null;
		String caseID = null;
		String stepResponse = "";
		String stepType = "";
		String formType = "";
		String courseWithdrawalType = "";
		String approvalStatus = "";
		String stepName = "";
		String arscComments = "";
		String medicalApprovalStatus = "";
		String comments = "";
		String medicalComments = "";
		Timestamp stepCompleteTime = null;
		Timestamp wfCompleteTime = null;

		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		String wfInstanceID = workItem.getWorkflow().getId(); // Workflow
																// instance id
		log.info("Workflow InstanceID =="+wfInstanceID);
		String workflowID = workItem.getId(); // workitem id
		log.info("WorkflowID=="+workflowID);

		String wId = workflowID.replace("VolatileWorkItem_", "/workItems/");
		String workItemID = "";
		log.info("Workflow item Id==" + wId);
		if (paramsValue.equalsIgnoreCase("Before Medical Review")
				|| paramsValue.equalsIgnoreCase("Before Admin Review")) {
			log.info("param1 before step=" + paramsValue);
			String firstStr = wId.substring(0, wId.indexOf('_'));
			String secString = wId
					.substring(wId.indexOf('_') + 1, wId.length());
			String t1 = firstStr.replaceAll("[^0-9]+", "");
			int a1 = Integer.parseInt(t1);
			a1++; // Process step is one step behind the Assign task, so
					// increment it.
			firstStr = firstStr.replaceAll(t1, String.valueOf(a1));
			workItemID = wfInstanceID.concat(firstStr).concat("_")
					.concat(secString);
			log.info("Final workItemID ==" + workItemID);
		}

		if (paramsValue.equalsIgnoreCase("After Medical Review")
				|| paramsValue.equalsIgnoreCase("After Admin Review")) {
			log.info("param1 after step=" + paramsValue);
			String firstStr = wId.substring(0, wId.indexOf('_'));
			String secString = wId
					.substring(wId.indexOf('_') + 1, wId.length());
			String t1 = firstStr.replaceAll("[^0-9]+", "");
			int a1 = Integer.parseInt(t1);
			a1--;// Process step is one step ahead the Assign task, so decrement
					// it.
			firstStr = firstStr.replaceAll(t1, String.valueOf(a1));
			workItemID = wfInstanceID.concat(firstStr).concat("_")
					.concat(secString);
			log.info("Final workItemID==" + workItemID);
		}

		while (xmlFiles.hasNext()) {

			String contentPath = workItem.getContentPath();

			Timestamp workflowStartTime = new java.sql.Timestamp(workItem
					.getTimeStarted().getTime());
			Timestamp stepStartTime = new java.sql.Timestamp(
					System.currentTimeMillis());

			String workflowInitiator = workItem.getWorkflow().getInitiator();
			String currentAssignee = workItem.getCurrentAssignee();
			for (Map.Entry<String, Object> entry : workItem.getWorkflowData()
					.getMetaDataMap().entrySet()) {
				if (entry.getKey().matches("actionTaken")) {
					stepResponse = entry.getValue().toString();
				}
			}
		
			Resource attachmentXml = xmlFiles.next();
			// log.info("xmlFiles inside ");
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath);
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("xmlFiles=" + filePath);
				Node subNode = resolver.getResource(filePath).adaptTo(
						Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary()
							.getStream();
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
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
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
					org.w3c.dom.NodeList nList1 = doc
							.getElementsByTagName("afUnboundData");
					for (int temp = 0; temp < nList1.getLength(); temp++) {
						org.w3c.dom.Node nNode1 = nList1.item(temp);
						if (nNode1.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element eElement1 = (org.w3c.dom.Element) nNode1;
							caseID = eElement1.getElementsByTagName("caseId")
									.item(0).getTextContent();

						}
					}

					org.w3c.dom.NodeList nList = doc
							.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);
						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							typeOfForm = eElement
									.getElementsByTagName("typeOfForm").item(0)
									.getTextContent();

							if (typeOfForm.equals("1")) {
								formType = "Non-Medical";
							} else {
								formType = "Medical";
							}

							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							sID = eElement.getElementsByTagName("StudentID")
									.item(0).getTextContent();
							major = eElement.getElementsByTagName("Major")
									.item(0).getTextContent();
							termCode = eElement
									.getElementsByTagName("TermCode").item(0)
									.getTextContent();
							termDesc = eElement
									.getElementsByTagName("TermDesc").item(0)
									.getTextContent();
							allCourseWithdrawal = eElement
									.getElementsByTagName("AllCoursWithdrawRB")
									.item(0).getTextContent();
							if (allCourseWithdrawal.equals("1")) {
								courseWithdrawalType = "Yes";
							} else {
								courseWithdrawalType = "No";
							}
							medicalApprovalStatus = eElement
									.getElementsByTagName("RecommendMedical")
									.item(0).getTextContent();
							medicalComments = eElement
									.getElementsByTagName(
											"MedicalReviewComment").item(0)
									.getTextContent();
							arscComments = eElement
									.getElementsByTagName("ARSCComment")
									.item(0).getTextContent();

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

				if (paramsValue.equalsIgnoreCase("Before Admin Review")) {
					stepType = "STEPSTART";
					stepName = "ARSC Review";
					currentAssignee = "ARSC-Reviewers";
					stepCompleteTime = null;
					workflowStartTime = null;
					wfCompleteTime = null;
				}
				if (paramsValue.equalsIgnoreCase("After Admin Review")) {
					stepType = "STEPEND";
					currentAssignee = "ARSC-Reviewers";
					stepName = "ARSC Review";
					if (medicalApprovalStatus.equals("1")) {
						approvalStatus = "Approved";
					} else {
						approvalStatus = "Denied";
					}
					comments = arscComments;
					stepCompleteTime = new java.sql.Timestamp(
							System.currentTimeMillis());
					workflowStartTime = null;
					wfCompleteTime = new java.sql.Timestamp(
							System.currentTimeMillis());
					stepStartTime = null;
				}
				if (paramsValue.equalsIgnoreCase("Before Medical Review")) {
					stepType = "STEPSTART";
					stepName = "Medical Review";
					stepResponse = "Send To Medical Reviewers";
					currentAssignee = "Medical-Reviewers";
					stepCompleteTime = null;
					wfCompleteTime = null;
				}
				if (paramsValue.equalsIgnoreCase("After Medical Review")) {
					stepType = "STEPEND";
					currentAssignee = "Medical-Reviewers";
					stepName = "Medical Review";
					if (medicalApprovalStatus.equals("1")) {
						approvalStatus = "Approved";
					} else {
						approvalStatus = "Denied";
					}
					comments = medicalComments;
					workflowStartTime = null;
					stepCompleteTime = new java.sql.Timestamp(
							System.currentTimeMillis());
					wfCompleteTime = null;
					stepStartTime = null;
				}

				dataMap = new LinkedHashMap<String, Object>();
				dataMap.put("WORKITEM_ID", workItemID);
				dataMap.put("WORKFLOW_PAYLOAD", contentPath);
				dataMap.put("CASE_ID", caseID);
				dataMap.put("CWID", sID);
				// dataMap.put("WORKFLOW_START_TIME", workflowStartTime);
				dataMap.put("STEP_START_TIME", stepStartTime);
				dataMap.put("WORKFLOW_INITIATOR", workflowInitiator);
				dataMap.put("ASSIGNEE", currentAssignee);
				dataMap.put("STEP_COMPLETE_TIME", stepCompleteTime);
				dataMap.put("STEP_TYPE", stepType);
				dataMap.put("STEP_RESPONSE", stepResponse);
				dataMap.put("STEP_NAME", stepName);
				dataMap.put("COURSE_NUMBER", null);
				dataMap.put("INSTRUCTOR_NAME", null);
				dataMap.put("STUDENT_FIRST_NAME", firstName);
				dataMap.put("STUDENT_LAST_NAME", lastName);
				dataMap.put("TERM", termCode);
				dataMap.put("MAJOR", major);
				dataMap.put("CLASS_NUMBER", null);
				dataMap.put("TERM_DESCRIPTION", termDesc);
				dataMap.put("TERM_WITHDRAWAL", courseWithdrawalType);
				dataMap.put("WITHDRAWAL_TYPE", formType);
				dataMap.put("CHAIR_NAME", null);
				dataMap.put("APPROVAL_STATUS", approvalStatus);
				dataMap.put("COMMENTS", comments);
				dataMap.put("WORKFLOW_INSTANCE_ID", wfInstanceID);
				// dataMap.put("WORKFLOW_COMPLETE_TIME", wfCompleteTime);

				// conn = jdbcConnectionService.getAemDEVDBConnection();
				conn = getConnection();
				if (conn != null) {
					log.error("Connection Successfull");
					insertWFHistory(conn, dataMap);
				}
			}

		}

	}

	public void insertWFHistory(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_SCW_WF_HISTORY";
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
			log.error("SQL=" + sql.toString());
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
			// Inject the DataSourcePool right here!
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
