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

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Short Emp Fee Waiver DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFShortAppEmpFeeWaiverDB" })
public class CSUFShortAppEmpFeeWaiverDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CSUFShortAppEmpFeeWaiverDB.class);

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

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
		String semester = "";
		String year = "";
		String program = "";
		String attendedCSUFBefore = "";
		String csufStatus = "";
		String emplID = "";
		String middleName = "";
		String birthDate = "";
		String gender = "";
		String ssnNo = "";
		String stNumber = "";
		String apt = "";
		String state = "";
		String city = "";
		String zipPostalCode = "";
		String phone = "";
		String email = "";
		String countryPR = "";
		String degreeReceived = "";
		String signature = "";
		String classLevel = "";
		String signedDate = "";
		String facultyStaffVerify = "";
		String userId = "";
		LinkedHashMap<String, Object> dataMap = null;
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
							semester = eElement
									.getElementsByTagName("Semester").item(0)
									.getTextContent();
							year = eElement.getElementsByTagName("Year")
									.item(0).getTextContent();
							program = eElement.getElementsByTagName("Program")
									.item(0).getTextContent();
							attendedCSUFBefore = eElement
									.getElementsByTagName("AttendedCSUFBefore")
									.item(0).getTextContent();
							csufStatus = eElement
									.getElementsByTagName("CSUFStatus").item(0)
									.getTextContent();
							emplID = eElement.getElementsByTagName("EmplID")
									.item(0).getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							middleName = eElement
									.getElementsByTagName("MiddleName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							birthDate = eElement
									.getElementsByTagName("BirthDate").item(0)
									.getTextContent();
							gender = eElement.getElementsByTagName("Gender")
									.item(0).getTextContent();
							ssnNo = eElement.getElementsByTagName("SSN")
									.item(0).getTextContent();
							stNumber = eElement
									.getElementsByTagName("StreetNumber")
									.item(0).getTextContent();
							apt = eElement.getElementsByTagName("Apt").item(0)
									.getTextContent();
							state = eElement.getElementsByTagName("State")
									.item(0).getTextContent();
							city = eElement.getElementsByTagName("City")
									.item(0).getTextContent();
							zipPostalCode = eElement
									.getElementsByTagName("ZipPostalCode")
									.item(0).getTextContent();
							phone = eElement.getElementsByTagName("Phone")
									.item(0).getTextContent();
							email = eElement.getElementsByTagName("Email")
									.item(0).getTextContent();
							countryPR = eElement
									.getElementsByTagName("CountryPR").item(0)
									.getTextContent();
							degreeReceived = eElement
									.getElementsByTagName("DegreeReceived")
									.item(0).getTextContent();
							signature = eElement
									.getElementsByTagName("Signature").item(0)
									.getTextContent();
							classLevel = eElement
									.getElementsByTagName("ClassLevel").item(0)
									.getTextContent();
							signedDate = eElement
									.getElementsByTagName("SignedDate").item(0)
									.getTextContent();
							facultyStaffVerify = eElement
									.getElementsByTagName("FacultyStaffVerify")
									.item(0).getTextContent();
							userId = eElement.getElementsByTagName("userID")
									.item(0).getTextContent();

						}
					}
					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMPL_ID", emplID);
					dataMap.put("SEMESTER", semester);
					dataMap.put("YEAR", year);
					dataMap.put("PROGRAM", program);
					dataMap.put("ATTENDED_CSUF_BEFORE", attendedCSUFBefore);
					dataMap.put("CSUF_STATUS", csufStatus);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("MIDDLE_NAME", middleName);
					dataMap.put("LAST_NAME", lastName);
					Object birthDateObj = null;
					if (birthDate != null && birthDate != "") {
						Date birthDateNew = Date.valueOf(birthDate);
						birthDateObj = birthDateNew;
					}
					dataMap.put("BIRTH_DATE", birthDateObj);
					dataMap.put("GENDER", gender);
					dataMap.put("SSN", ssnNo);
					dataMap.put("USER_ID", userId);
					dataMap.put("STREET_NUMBER", stNumber);
					dataMap.put("APT", apt);
					dataMap.put("STATE", state);
					dataMap.put("CITY", city);
					dataMap.put("ZIP_POSTAL_CODE", zipPostalCode);
					dataMap.put("PHONE_NO", phone);
					dataMap.put("EMAIL_ADDRESS", email);
					dataMap.put("COUNTRY_PERMANENT_RESIDENCE", countryPR);
					dataMap.put("INSTITUTION_DEGREE_RECEIVED", degreeReceived);
					dataMap.put("CLASS_LEVEL", classLevel);
					dataMap.put("SIGNATURE", signature);

					Object signedDateObj = null;
					if (signedDate != null && signedDate != "") {
						Date signedDateNew = Date.valueOf(signedDate);
						signedDateObj = signedDateNew;
					}
					dataMap.put("SIGNED_DATE", signedDateObj);
					dataMap.put("FACULTY_STAFF_VERIFY", facultyStaffVerify);
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
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
			log.error("Connection Successfull");
			insertShortEmpDB(conn, dataMap);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */
	public void insertShortEmpDB(Connection conn,
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
			String tableName = "AEM_SHORT_EMP_FEE_WAIVER_FORM";
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
						log.error("Date=" + value);
						preparedStmt.setDate(++i, (Date) value);
					} else if (value instanceof Integer) {
						log.error("Integ=" + value);
						preparedStmt.setInt(++i, (Integer) value);
					} else {
						log.error("Else=" + value);
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
				log.info("Before Prepared Stmt Short Employee Fee Waiver");
				preparedStmt.execute();
				conn.commit();
				log.info("After Prepared Stmt Short Employee Fee Waiver");
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
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
