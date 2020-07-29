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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Domestic Partner Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=DomesticPartnerSave" })
public class CSUFDomesticPartnerDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFDomesticPartnerDB.class);
	
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
		String certifyCB1 = "";
		String tax_year = "";
		String name_of_employee = "";
		String name_of_domestic_partner = "";
		String certifyCB2 = "";
		String tax_year2 = "";
		String name_of_employee1 = "";
		String name_of_domestic_partner1 = "";
		String emp_signature = "";
		String ssn = "";
		String first_Name = "";
		String last_Name = "";
		String campus = "";
		String date_Initiated = "";
		String campus_representative = "";
		String telephone_no = "";
		String date_Approved = "";
		String workflowInstance = "";
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
			workflowInstance = workItem.getWorkflow().getId();
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
							certifyCB1 = eElement.getElementsByTagName("certifyCB1").item(0).getTextContent();
							tax_year = eElement.getElementsByTagName("tax_year").item(0)
									.getTextContent();
							name_of_employee = eElement.getElementsByTagName("name_of_employee").item(0).getTextContent();
							name_of_domestic_partner = eElement.getElementsByTagName("name_of_domestic_partner").item(0).getTextContent();
							certifyCB2 = eElement.getElementsByTagName("certifyCB2").item(0).getTextContent();
							tax_year2 = eElement.getElementsByTagName("tax_year2").item(0).getTextContent();
							name_of_employee1 = eElement.getElementsByTagName("name_of_employee1").item(0).getTextContent();
							name_of_domestic_partner1 = eElement.getElementsByTagName("name_of_domestic_partner1").item(0).getTextContent();
							emp_signature = eElement.getElementsByTagName("emp_signature").item(0).getTextContent();
							ssn = eElement.getElementsByTagName("SSN").item(0).getTextContent();
							first_Name = eElement.getElementsByTagName("First_Name").item(0).getTextContent();
							last_Name = eElement.getElementsByTagName("Last_Name").item(0).getTextContent();
							campus = eElement.getElementsByTagName("campus").item(0).getTextContent();
							date_Initiated = eElement.getElementsByTagName("Date_Initiated").item(0).getTextContent();
							campus_representative = eElement.getElementsByTagName("campus_representative").item(0).getTextContent();
							telephone_no = eElement.getElementsByTagName("Telephone_no").item(0).getTextContent();
							date_Approved = eElement.getElementsByTagName("Date_Approved").item(0).getTextContent();
													
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					
					dataMap.put("CERTIFY_CB1", certifyCB1);
					dataMap.put("TAX_YEAR1", tax_year);
					dataMap.put("EMP_NAME1", name_of_employee);
					dataMap.put("DOMESTIC_PARTNER_NAME1", name_of_domestic_partner);
					dataMap.put("CERTIFY_CB2", certifyCB2);
					dataMap.put("TAX_YEAR2", tax_year2);
					dataMap.put("EMP_NAME2", name_of_employee1);					
					dataMap.put("DOMESTIC_PARTNER_NAME2", name_of_domestic_partner1);
					dataMap.put("EMP_SIGN", emp_signature);
					dataMap.put("SSN", ssn);					
					dataMap.put("FIRST_NAME", first_Name);
					dataMap.put("LAST_NAME", last_Name);
					dataMap.put("CAMPUS", campus);
					Object date_InitiatedObj = null;
					if (date_Initiated != null && date_Initiated != "") {
						Date date_InitiatedNew = Date.valueOf(date_Initiated);
						date_InitiatedObj = date_InitiatedNew;
					}
					dataMap.put("DATE_INITIATED", date_InitiatedObj);
					dataMap.put("CAMPUS_REPRESENTATIVE", campus_representative);
					dataMap.put("TELE_NO", telephone_no);
					Object date_ApprovedObj = null;
					if (date_Approved != null && date_Approved != "") {
						Date date_ApprovedNew = Date.valueOf(date_Approved);
						date_ApprovedObj = date_ApprovedNew;
					}
					dataMap.put("DATE_APPROVED", date_ApprovedObj);					
									
					dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstance);
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
			String tableName = "AEM_DOMESTIC_PARTNER_DPND_CERT";
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
}
