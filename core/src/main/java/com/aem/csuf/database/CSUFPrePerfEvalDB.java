package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Pre Perf Evaluation Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=PrePerfEvalSave" })
public class CSUFPrePerfEvalDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFPrePerfEvalDB.class);
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;
	@Reference
	private GlobalConfigService globalConfigService;
	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String empId = "";
		String empRCD = "";
		String reviewPeriodFrom = "";
		String reviewPeriodTo = "";
		String todayDate = "";
		String supervisor = "";
		String firstName = "";
		String lastName = "";
		String deptName = "";
		String deptId = "";
		String evaluationCom1 = "";
		String evaluationCom2 = "";
		String evaluationCom3 = "";
		String evaluationCom4 = "";
		String evaluationCom5 = "";
		String evaluationCom6 = "";
		String evaluationCom7 = "";
		String empCB = "";
		String empSign = "";
		String empDate = "";
		String workflowInstanceID = "";
		
		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		// Get the payload path and iterate the path to find Data.xml, Use
		// Document
		// factory to parse the xml and fetch the required values for the
		// filenet
		// attachment

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			workflowInstanceID = workItem.getWorkflow().getId();
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath);
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
					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							empId = eElement.getElementsByTagName("EmpID").item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EmpRCD").item(0).getTextContent();
							reviewPeriodFrom = eElement.getElementsByTagName("ReviewPeriodFrom").item(0)
									.getTextContent();
							reviewPeriodTo = eElement.getElementsByTagName("ReviewPeriodTo").item(0).getTextContent();
							todayDate = eElement.getElementsByTagName("TodayDate").item(0).getTextContent();
							supervisor = eElement.getElementsByTagName("Supervisor").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();

							lastName = eElement.getElementsByTagName("LastName").item(0).getTextContent();

							deptName = eElement.getElementsByTagName("DeptName").item(0).getTextContent();
							deptId = eElement.getElementsByTagName("DeptID").item(0).getTextContent();
							evaluationCom1 = eElement.getElementsByTagName("Evaluation1").item(0).getTextContent();
							evaluationCom2 = eElement.getElementsByTagName("Evaluation2").item(0).getTextContent();
							evaluationCom3 = eElement.getElementsByTagName("Evaluation3").item(0).getTextContent();
							evaluationCom4 = eElement.getElementsByTagName("Evaluation4").item(0).getTextContent();
							evaluationCom5 = eElement.getElementsByTagName("Evaluation5").item(0).getTextContent();
							evaluationCom6 = eElement.getElementsByTagName("Evaluation6").item(0).getTextContent();
							evaluationCom7 = eElement.getElementsByTagName("Evaluation7").item(0).getTextContent();
							empCB = eElement.getElementsByTagName("EmpCB").item(0).getTextContent();
							empSign = eElement.getElementsByTagName("EmpSign").item(0).getTextContent();
							empDate = eElement.getElementsByTagName("EmpDate").item(0).getTextContent();
							
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMPLID", empId);
					Object todayDtObj = null;
					if (todayDate != null && todayDate != "") {
						Date todayDateNew = Date.valueOf(todayDate);
						todayDtObj = todayDateNew;
					}
					dataMap.put("TODAY_DT", todayDtObj);

					dataMap.put("FIRSTNAME", firstName);
					dataMap.put("LASTNAME", lastName);
					dataMap.put("EMPRCD", empRCD);
					Object reviewPeriodFromObj = null;
					if (reviewPeriodFrom != null && reviewPeriodFrom != "") {
						Date reviewPeriodFromNew = Date.valueOf(reviewPeriodFrom);
						reviewPeriodFromObj = reviewPeriodFromNew;
					}

					dataMap.put("REVIEW_FROM_DT", reviewPeriodFromObj);
					Object reviewPeriodToObj = null;
					if (reviewPeriodTo != null && reviewPeriodTo != "") {
						Date reviewPeriodToNew = Date.valueOf(reviewPeriodTo);
						reviewPeriodToObj = reviewPeriodToNew;
					}
					dataMap.put("REVIEW_TO_DT", reviewPeriodToObj);

					dataMap.put("SUPERVISORNAME", supervisor);
					dataMap.put("DEPARTMENT", deptName);

					dataMap.put("DEPTID", Integer.parseInt(deptId));

					dataMap.put("EVAL_COMMENT1", evaluationCom1);
					dataMap.put("EVAL_COMMENT2", evaluationCom2);
					dataMap.put("EVAL_COMMENT3", evaluationCom3);
					dataMap.put("EVAL_COMMENT4", evaluationCom4);
					dataMap.put("EVAL_COMMENT5", evaluationCom5);
					dataMap.put("EVAL_COMMENT6", evaluationCom6);
					dataMap.put("EVAL_COMMENT7", evaluationCom7);
					dataMap.put("EMPCB", empCB);
					Object empSignDateObj = null;
					if (empDate != null && empDate != "") {
						Date empDateNew = Date.valueOf(empDate);
						empSignDateObj = empDateNew;
					}

					dataMap.put("EMP_SIGN_DATE", empSignDateObj);
					dataMap.put("EMP_SIGN", empSign);
					dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstanceID);

					log.error("Datamap Size=" + dataMap.size());

				} catch (SAXException e) {
					log.error("SAXException=" + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						log.error("IOException=" + e.getMessage());
						e.printStackTrace();
					}

				}

			}
		}
		String dataSourceVal = globalConfigService.getAEMDataSource();
		log.info("DataSourceVal==========" + dataSourceVal);
		conn = jdbcConnectionService.getDBConnection(dataSourceVal);
		if (conn != null) {
			log.error("Connection Successfull");
			insertSPEData(conn, dataMap);
		}
	}

	@Reference
	private DataSourcePool source;

	public void insertSPEData(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_PRE_PERF_EVAL";
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
				preparedStmt.execute();
				log.info("preparedstmt="+preparedStmt);
				conn.commit();
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						log.info("preparedstmt closed");
						conn.close();
					} catch (SQLException e) {
						log.error("SQLException=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
