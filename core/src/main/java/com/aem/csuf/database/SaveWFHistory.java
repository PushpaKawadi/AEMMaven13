package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import com.day.commons.datasource.poolservice.DataSourcePool;
/**
 * 
 * @author 103499
 *
 */
@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Course1",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=WF History" })
public class SaveWFHistory implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(SaveWFHistory.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;
		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();

		String paramsValue = ((String) processArguments.get("PROCESS_ARGS",
				"string")).toString();
		log.info("params value=======" + paramsValue);
		String[] parameters1 = paramsValue.split(",");
		log.info("parameters1=" + parameters1);
		String param1 = parameters1[0];
		String param2 = parameters1[1];
		log.error("Param1=" + param1);
		log.error("Param12=" + param2);

		String filename = param2.split("\\.")[0];
		String extension = param2.split("\\.")[1];

		String fileIndex = filename.substring(filename.indexOf("Data") + 4,
				filename.length());
		log.info("fileNameExtract=" + fileIndex);
		LinkedHashMap<String, Object> dataMap = null;

		Document doc = null;
		InputStream is = null;
		String firstName = null;
		String lastName = null;
		String major = "";
		String termCode = null;
		String typeOfForm = null;

		String sID = "";
		String termDesc = "";
		String allCourseWithdrawal = "";
		String course = "";
		String schedule = "";
		String instName = "";
		String instUID = "";
		String chairName = "";
		String chairUID = "";
		String caseID = "";
		String stepResponse = "";
		String stepType = "";
		String formType = "";
		String courseWithdrawalType = "";
		String instApprovalStatus = "";
		String chairApprovalStatus = "";
		String approvalStatus = "";
		String stepName = "";
		String arscComments = "";
		String instComments = "";
		String chairComments = "";
		String comments = "";
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		
		
		while (xmlFiles.hasNext()) {
			String workflowID = workItem.getId();
			String workID = workflowID
					.substring(workflowID.lastIndexOf("/") + 1);

			String contentPath = workItem.getContentPath();
			java.util.Date workflowStartTime = workItem.getProgressBeginTime();
			log.error("Pushpa Process begin="+workflowStartTime);
			
			java.util.Date timeStarted = workItem.getTimeStarted();
			log.error("Pushpa timeStarted="+timeStarted);
			
			
			java.util.Date stepStartTime = workItem.getWorkflow()
					.getTimeStarted();
			log.error("Pushpa stepStart="+workflowStartTime);
			
			java.sql.Date stepStartDate = new java.sql.Date(stepStartTime.getTime());
			
			
			String workflowInitiator = workItem.getWorkflow().getInitiator();
			String currentAssignee = workItem.getCurrentAssignee();
			java.util.Date stepCompleteTime = workItem.getTimeEnded();
			
			log.error("Pushpa stepCompleteTime="+stepCompleteTime);
			//java.sql.Date stepEndDate = new java.sql.Date(stepCompleteTime.getTime());
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
			// if (filePath.contains("Data.xml")) {
			if (filePath.contains(param2)) {
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
							course = eElement
									.getElementsByTagName(
											"CourseNo".concat(fileIndex))
									.item(0).getTextContent();
							schedule = eElement
									.getElementsByTagName(
											"ScheduleNo".concat(fileIndex))
									.item(0).getTextContent();
							instName = eElement
									.getElementsByTagName(
											"InstructorName".concat(fileIndex))
									.item(0).getTextContent();
							instUID = eElement
									.getElementsByTagName(
											"InstructorUserID"
													.concat(fileIndex)).item(0)
									.getTextContent();
							chairName = eElement
									.getElementsByTagName(
											"ChairName".concat(fileIndex))
									.item(0).getTextContent();
							chairUID = eElement
									.getElementsByTagName(
											"ChairUserID".concat(fileIndex))
									.item(0).getTextContent();
							instApprovalStatus = eElement
									.getElementsByTagName("RecommendInstructor")
									.item(0).getTextContent();
							instComments = eElement
									.getElementsByTagName("InstructorComment")
									.item(0).getTextContent();
							chairApprovalStatus = eElement
									.getElementsByTagName("RecommendChair")
									.item(0).getTextContent();
							chairComments = eElement
									.getElementsByTagName("ChairComment")
									.item(0).getTextContent();
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
				if (param1.equalsIgnoreCase("Before Instructor Review")) {
					stepType = "STEPSTART";
					stepResponse = "Send To Instructor";
					stepName = "Instructor Review";
					currentAssignee = instUID;
				}
				if (param1.equalsIgnoreCase("After Instructor Review")) {
					stepType = "STEPEND";
					currentAssignee = instUID;
					stepName = "Instructor Review";
					if (instApprovalStatus.equals("1")) {
						approvalStatus = "Approved";
					} else {
						approvalStatus = "Denied";
					}
					comments = instComments;
				}
				if (param1.equalsIgnoreCase("Before Chair Review")) {
					stepType = "STEPSTART";
					stepName = "Chair Review";
					currentAssignee = chairUID;
				}
				if (param1.equalsIgnoreCase("After Chair Review")) {
					stepType = "STEPEND";
					currentAssignee = chairUID;
					stepName = "Chair Review";
					if (chairApprovalStatus.equals("1")) {
						approvalStatus = "Approved";
					} else {
						approvalStatus = "Denied";
					}
					comments = chairComments;
				}
				if (param1.equalsIgnoreCase("Before Admin Review")) {
					stepType = "STEPSTART";
					stepName = "ARSC Review";
					currentAssignee = "ARSC-Reviewers";
				}
				if (param1.equalsIgnoreCase("After Admin Review")) {
					stepType = "STEPEND";
					currentAssignee = "ARSC-Reviewers";
					stepName = "ARSC Review";
					if (chairApprovalStatus.equals("1")) {
						approvalStatus = "Approved";
					} else {
						approvalStatus = "Denied";
					}
					comments = arscComments;
				}

				dataMap = new LinkedHashMap<String, Object>();
				dataMap.put("WORKFLOW_ID", workID);
				dataMap.put("WORKFLOW_PAYLOAD", contentPath);
				dataMap.put("CASE_ID", caseID);
				dataMap.put("CWID", sID);
				stepStartTime = null;
				dataMap.put("WORKFLOW_START_TIME", stepStartTime);
				dataMap.put("STEP_START_TIME", stepStartDate);
				dataMap.put("WORKFLOW_INITIATOR", workflowInitiator);
				dataMap.put("ASSIGNEE", currentAssignee);
				stepCompleteTime = null;
				dataMap.put("STEP_COMPLETE_TIME", stepCompleteTime);
				dataMap.put("STEP_TYPE", stepType);
				dataMap.put("STEP_RESPONSE", stepResponse);
				dataMap.put("STEP_NAME", stepName); // /
				dataMap.put("COURSE_NUMBER", course);
				dataMap.put("INSTRUCTOR_NAME", instName);
				dataMap.put("STUDENT_FIRST_NAME", firstName);
				dataMap.put("STUDENT_LAST_NAME", lastName);
				dataMap.put("TERM", termCode);
				dataMap.put("MAJOR", major);
				dataMap.put("CLASS_NUMBER", schedule);
				dataMap.put("TERM_DESCRIPTION", termDesc);
				dataMap.put("TERM_WITHDRAWAL", courseWithdrawalType);
				dataMap.put("WITHDRAWAL_TYPE", formType);
				dataMap.put("CHAIR_NAME", chairName);
				dataMap.put("APPROVAL_STATUS", approvalStatus);
				dataMap.put("COMMENTS", comments);
				// datamap.put("WORKFLOW_COMPLETE_TIME", stepEndTime);
				conn = getConnection();
				if (conn != null) {
					log.info("Connection Successfull");
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
				log.error("Before insert workflow history");
				preparedStmt.execute();
				conn.commit();
				log.error("End insert workflow history");
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
