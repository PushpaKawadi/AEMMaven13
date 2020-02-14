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
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Vision LIFE-LTD Enrollment Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=VisionLifeLtdEnrollmentSave" })
public class CSUFVisionLifeLtdEnrollmentDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFVisionLifeLtdEnrollmentDB.class);
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;
	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String ssn = "";
		String firstName = "";
		String middleName = "";
		String lastName = "";
		String cbid = "";
		String agencyCode = "";
		String unitCode = "";
		String classCode = "";
		String serialCode = "";
		String new1Check = "";
		String delete1Check = "";
		String month1 = "";
		String year1 = "";
		String new2Check = "";
		String delete2Check = "";
		String month2 = "";
		String year2 = "";
		String lifeCode = "";
		String new3Check = "";
		String delete3Check = "";
		String month3 = "";
		String year3 = "";
		String ltdCode = "";
		String new4Check = "";
		String delete4Check = "";
		String month4 = "";
		String year4 = "";
		String remarks = "";
		String formCompletedBy = "";
		String authorizedSign = "";
		String campusName = "";
		String telephoneNumber = "";
		String dateInitiated = "";

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
							ssn = eElement.getElementsByTagName("ssn").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("first_Name").item(0)
									.getTextContent();
							middleName = eElement.getElementsByTagName("middle_Name").item(0).getTextContent();
							lastName = eElement.getElementsByTagName("last_Name").item(0).getTextContent();
							cbid = eElement.getElementsByTagName("cbid").item(0).getTextContent();
							agencyCode = eElement.getElementsByTagName("agency").item(0).getTextContent();
							unitCode = eElement.getElementsByTagName("unit").item(0).getTextContent();
							classCode = eElement.getElementsByTagName("classCode").item(0).getTextContent();
							serialCode = eElement.getElementsByTagName("serial").item(0).getTextContent();
							new1Check = eElement.getElementsByTagName("new1").item(0).getTextContent();
							delete1Check = eElement.getElementsByTagName("delete1").item(0).getTextContent();
							month1 = eElement.getElementsByTagName("month1").item(0).getTextContent();
							year1 = eElement.getElementsByTagName("year1").item(0).getTextContent();
							new2Check = eElement.getElementsByTagName("new2").item(0).getTextContent();
							delete2Check = eElement.getElementsByTagName("delete2").item(0).getTextContent();
							month2 = eElement.getElementsByTagName("month2").item(0).getTextContent();
							year2 = eElement.getElementsByTagName("year2").item(0).getTextContent();
							lifeCode = eElement.getElementsByTagName("lifeCode").item(0).getTextContent();
							new3Check = eElement.getElementsByTagName("new3").item(0).getTextContent();
							delete3Check = eElement.getElementsByTagName("delete3").item(0).getTextContent();
							month3 = eElement.getElementsByTagName("month3").item(0).getTextContent();
							year3 = eElement.getElementsByTagName("year3").item(0)
									.getTextContent();
							ltdCode = eElement.getElementsByTagName("ltdCode").item(0)
									.getTextContent();
							new4Check = eElement.getElementsByTagName("new4").item(0).getTextContent();
							delete4Check = eElement.getElementsByTagName("delete4").item(0)
									.getTextContent();
							month4 = eElement.getElementsByTagName("month4").item(0).getTextContent();
							year4 = eElement.getElementsByTagName("year4").item(0)
									.getTextContent();
							remarks = eElement.getElementsByTagName("remarks").item(0).getTextContent();
							formCompletedBy = eElement.getElementsByTagName("form_Completed_By").item(0)
									.getTextContent();
							authorizedSign = eElement.getElementsByTagName("authorized_Sign").item(0)
									.getTextContent();
							campusName = eElement.getElementsByTagName("campus_Name").item(0)
									.getTextContent();
							telephoneNumber = eElement.getElementsByTagName("telephone_Number").item(0)
									.getTextContent();
							dateInitiated = eElement.getElementsByTagName("date_Initiated").item(0)
									.getTextContent();
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("SSN", ssn);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("MIDDLE_NAME", middleName);
					dataMap.put("CBID", cbid);
					dataMap.put("AGENCY", agencyCode);
					dataMap.put("UNIT", unitCode);
					dataMap.put("CLASSCODE", classCode);
					dataMap.put("SERIAL", serialCode);
					dataMap.put("NEW1", new1Check);
					dataMap.put("DELETE1", delete1Check);
					dataMap.put("MONTH1", month1);
					dataMap.put("YEAR1", year1);
					dataMap.put("NEW2", new2Check);
					dataMap.put("DELETE2", delete2Check);
					dataMap.put("MONTH2", month2);
					dataMap.put("YEAR2", year2);
					dataMap.put("NEW3", new3Check);
					dataMap.put("DELETE3", delete3Check);
					dataMap.put("MONTH3", month3);
					dataMap.put("YEAR3", year3);
					dataMap.put("NEW4", new4Check);
					dataMap.put("DELETE4", delete4Check);
					dataMap.put("MONTH4", month4);
					dataMap.put("YEAR4", year4);
					dataMap.put("LIFECODE", lifeCode);
					dataMap.put("LTDCODE", ltdCode);
					dataMap.put("REMARKS", remarks);
					dataMap.put("FORM_COMPLETED_BY", formCompletedBy);
					dataMap.put("AUTHORIZED_SIGN", authorizedSign);
					dataMap.put("CAMPUS_NAME", campusName);
					dataMap.put("TELEPHONE_NUMBER", telephoneNumber);
					Object dateInitiatedObj = null;
					if (dateInitiated != null && dateInitiated != "") {
						Date dateInitiatedNew = Date.valueOf(dateInitiated);
						dateInitiatedObj = dateInitiatedNew;
					}
					dataMap.put("DATE_INITIATED", dateInitiatedObj);
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
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
			log.error("Connection Successfull");
			insertSPEData(conn, dataMap);
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

	public void insertSPEData(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_VISION_LIFE_LTD_ENROLLMENT";
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
