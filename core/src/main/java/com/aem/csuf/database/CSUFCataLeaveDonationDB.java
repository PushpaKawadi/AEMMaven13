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
import com.aem.community.util.DBUtil;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Catastrophic Leave Donation DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFCataLeaveDonationDB" })
public class CSUFCataLeaveDonationDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CSUFCataLeaveDonationDB.class);
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Reference
	private GlobalConfigService globalConfigService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String firstName = "";
		String lastName = "";
		String emplId = "";
		String empRCD = "";
		String departmentID = "";
		String departmentName = "";
		String donationDate = "";
		String timeOfDay = "";
		String campusExt = "";
		String sickLeaveHours = "";
		String vacationHours = "";
		String donarCatagory = "";
		String signature = "";

		LinkedHashMap<String, Object> dataMap = null;
		LinkedHashMap<String, Object> dataMapAuditTrail = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		String wfInstanceID = workItem.getWorkflow().getId();
		while (xmlFiles.hasNext()) {
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
					org.w3c.dom.NodeList nList = doc
							.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							emplId = eElement.getElementsByTagName("EmplID")
									.item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EmplRCD")
									.item(0).getTextContent();
							departmentName = eElement
									.getElementsByTagName("Department").item(0)
									.getTextContent();
							departmentID = eElement
									.getElementsByTagName("DeptID").item(0)
									.getTextContent();
							donationDate = eElement
									.getElementsByTagName("DonationDate")
									.item(0).getTextContent();
							timeOfDay = eElement
									.getElementsByTagName("TimeOfDay").item(0)
									.getTextContent();
							campusExt = eElement
									.getElementsByTagName("CampExt").item(0)
									.getTextContent();
							sickLeaveHours = eElement
									.getElementsByTagName("sl_hours").item(0)
									.getTextContent();
							vacationHours = eElement
									.getElementsByTagName("vac_hrs").item(0)
									.getTextContent();
							donarCatagory = eElement
									.getElementsByTagName("DonarCatagory")
									.item(0).getTextContent();
							signature = eElement
									.getElementsByTagName("Signature").item(0)
									.getTextContent();

						}
					}
					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMPID", emplId);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("EMP_RCD", empRCD);
					dataMap.put("DEPT_NAME", departmentName);
					dataMap.put("DEPT_ID", departmentID);
					Object donationDateObj = null;
					if (donationDate != null && donationDate != "") {
						Date donDateNew = Date.valueOf(donationDate);
						donationDateObj = donDateNew;
					}
					dataMap.put("DONATION_DATE", donationDateObj);
					dataMap.put("TIME_OF_DAY", timeOfDay);
					dataMap.put("CAMPUS_EXTENTION", campusExt);
					dataMap.put("SICK_LEAVE_CREDITS", sickLeaveHours);
					dataMap.put("VACATION_CREDITS", vacationHours);
					dataMap.put("DONAR_CATAGORY", donarCatagory);
					dataMap.put("SIGNATURE", signature);
					// dataMap.put("WORKFLOW_INSTANCE_ID", wfInstanceID);
				} catch (SAXException e) {
					log.error("SAXException=" + e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					log.error("Exception1");
					log.error("Exception=" + e.getMessage());
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
		log.info("Connection==========" + conn);
		// conn = jdbcConnectionService.getDBConnection(datasourceName)
		if (conn != null) {
			insertCataLeaveDonationData(conn, dataMap);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */
	public void insertCataLeaveDonationData(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		log.error("conn=" + conn);
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}
			String tableName = "CATASTROPHIC_LEAVE_DONATION";
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
			} catch (Exception e) {
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}
			int i = 0;
			log.info("Datamap values=" + dataMap.values());

			try {
				for (Object value : dataMap.values()) {
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
				}
			} catch (SQLException e) {
				log.error("SQLException=" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}

			try {
				log.info("Before Prepared Stmt Catastrophic Leave Donation");
				preparedStmt.execute();
				conn.commit();
				log.info("After Prepared Stmt Catastrophic Leave Donation");
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				String errMessage = e1.getMessage();
				e1.printStackTrace();
				DBUtil dbUtil = new DBUtil();
				String tableNameAudit = "AEM_AUDIT_TRACE";
				LinkedHashMap<String, Object> dataMapAuditTrail = new LinkedHashMap<String, Object>();
				Timestamp auditStTime = new java.sql.Timestamp(System.currentTimeMillis());
				dataMapAuditTrail.put("EVENT_TYPE", "Database");
				dataMapAuditTrail.put("AUDIT_TIME", auditStTime);
				dataMapAuditTrail.put("FILENET_URL", "");
				dataMapAuditTrail.put("DATA_PROCESSED", "0");
				dataMapAuditTrail.put("FILENET_JSON", "");
				dataMapAuditTrail.put("FORM_NAME", "Catastrophic Leave Donation");
				dataMapAuditTrail.put("TABLE_NAME", "catastrophic_leave_donation");
				dataMapAuditTrail.put("SQL_ERROR_DESC", errMessage);
				dbUtil.insertAutitTrace(conn, dataMapAuditTrail, tableNameAudit);
				
			} catch (Exception e) {
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						conn.close();
					} catch (SQLException e) {
						log.error("SQLException=" + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error("Exception=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
