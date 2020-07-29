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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Direct Pay Dental Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=DirectPayDentalSave" })
public class CSUFDirectPayDentalDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFDirectPayDentalDB.class);
	
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

		String streetAddress = "";
		String SSN = "";
		String middleName  = "";
		String firstName = "";
		String homePhone = "";
		String city = "";
		String lastName = "";
		String state = "";
		String zipCode = "";
		String empDate = "";
		String employeeSignature  = "";
		String policyNumber1 = "";
		String policyNumber2 = "";
		String quaterlyAmt = "";
		String eachQuater = "";
		String mothlyValue = "";
		String empDate1 = "";
		String employeeSignature1 = "";
		String typeOfAbsence = "";
		String fromDateofAbsence = "";
		String toDateofAbsence = "";
		String lastPayPeriodPermium = "";
		String empPayThrough = "";
		String empPayMonths = "";
		String campus = "";
		String address = "";
		String agencyCode = "";
		String empBarganingUnitCode = "";
		String signOfBenefitOfficer = "";
		String phoneNumber = "";
		String benefitOfficerDate = "";
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
							streetAddress = eElement.getElementsByTagName("StreetAddress").item(0).getTextContent();
							SSN = eElement.getElementsByTagName("SSN").item(0)
									.getTextContent();
							middleName = eElement.getElementsByTagName("MiddleName").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
							homePhone = eElement.getElementsByTagName("HomePhone").item(0).getTextContent();
							city = eElement.getElementsByTagName("City").item(0).getTextContent();
							lastName = eElement.getElementsByTagName("LastName").item(0).getTextContent();
							state = eElement.getElementsByTagName("State").item(0).getTextContent();
							zipCode = eElement.getElementsByTagName("ZipCode").item(0).getTextContent();
							empDate = eElement.getElementsByTagName("EmpDate").item(0).getTextContent();
							employeeSignature = eElement.getElementsByTagName("EmployeeSignature").item(0).getTextContent();
							policyNumber1 = eElement.getElementsByTagName("PolicyNumber1").item(0).getTextContent();
							policyNumber2 = eElement.getElementsByTagName("PolicyNumber2").item(0).getTextContent();
							quaterlyAmt = eElement.getElementsByTagName("QuaterlyAmt").item(0).getTextContent();
							eachQuater = eElement.getElementsByTagName("EachQuater").item(0).getTextContent();
							mothlyValue = eElement.getElementsByTagName("MothlyValue").item(0).getTextContent();
							empDate1 = eElement.getElementsByTagName("EmpDate1").item(0).getTextContent();
							employeeSignature1 = eElement.getElementsByTagName("EmployeeSignature1").item(0).getTextContent();
							typeOfAbsence = eElement.getElementsByTagName("TypeOfAbsence").item(0).getTextContent();
							fromDateofAbsence = eElement.getElementsByTagName("FromDateofAbsence").item(0).getTextContent();
							toDateofAbsence = eElement.getElementsByTagName("ToDateofAbsence").item(0).getTextContent();
							lastPayPeriodPermium = eElement.getElementsByTagName("LastPayPeriodPermium").item(0)
									.getTextContent();
							empPayThrough = eElement.getElementsByTagName("EmpPayThrough").item(0)
									.getTextContent();
							empPayMonths = eElement.getElementsByTagName("EmpPayMonths").item(0).getTextContent();
							campus = eElement.getElementsByTagName("Campus").item(0)
									.getTextContent();
							address = eElement.getElementsByTagName("Address").item(0).getTextContent();
							agencyCode = eElement.getElementsByTagName("AgencyCode").item(0)
									.getTextContent();
							empBarganingUnitCode = eElement.getElementsByTagName("EmpBarganingUnitCode").item(0).getTextContent();
							signOfBenefitOfficer = eElement.getElementsByTagName("SignOfBenefitOfficer").item(0)
									.getTextContent();
							phoneNumber = eElement.getElementsByTagName("PhoneNumber").item(0)
									.getTextContent();
							benefitOfficerDate = eElement.getElementsByTagName("BenefitOfficerDate").item(0)
									.getTextContent();							
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					
					dataMap.put("SSN", SSN);
					dataMap.put("STREET_ADDRESS", streetAddress);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("MID_NAME", middleName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("HOME_PHONE", homePhone);
					dataMap.put("CITY", city);
					
					dataMap.put("STATE", state);
					dataMap.put("ZIP_CODE", zipCode);
					Object empDateObj = null;
					if (empDate != null && empDate != "") {
						Date empDateNew = Date.valueOf(empDate);
						empDateObj = empDateNew;
					}
					dataMap.put("EMP_DATE1", empDateObj);					
					dataMap.put("EMP_SIGN", employeeSignature);
					dataMap.put("POLICY_NO1", policyNumber1);
					dataMap.put("POLICY_NO2", policyNumber2);
					dataMap.put("QUARTERLY_AMT", quaterlyAmt);
					dataMap.put("EACH_QUARTER", eachQuater);
					dataMap.put("MONTHLY_VALUE", mothlyValue);
					Object empDate1Obj = null;
					if (empDate1 != null && empDate1 != "") {
						Date empDate1New = Date.valueOf(empDate1);
						empDate1Obj = empDate1New;
					}
					dataMap.put("EMP_DATE2", empDate1Obj);					
					dataMap.put("EMP_SIGN1", employeeSignature1);
					dataMap.put("TYPE_OF_ABSENCE", typeOfAbsence);
					Object fromDateofAbsenceObj = null;
					if (fromDateofAbsence != null && fromDateofAbsence != "") {
						Date fromDateofAbsenceNew = Date.valueOf(fromDateofAbsence);
						fromDateofAbsenceObj = fromDateofAbsenceNew;
					}
					dataMap.put("FROM_DT_ABSENCE", fromDateofAbsenceObj);
					Object toDateofAbsenceObj = null;
					if (toDateofAbsence != null && toDateofAbsence != "") {
						Date toDateofAbsenceNew = Date.valueOf(toDateofAbsence);
						toDateofAbsenceObj = toDateofAbsenceNew;
					}
					dataMap.put("TO_DT_ABSENCE", toDateofAbsenceObj);
					dataMap.put("LAST_PAY_PERIOD", lastPayPeriodPermium);
					dataMap.put("EMP_PAY_THROUGH", empPayThrough);
					dataMap.put("EMP_PAY_MONTHS", empPayMonths);
					dataMap.put("CAMPUS", campus);
					dataMap.put("ADDRESS", address);
					dataMap.put("AGENCY_CODE", agencyCode);
					dataMap.put("EMP_CBID", empBarganingUnitCode);
					dataMap.put("BENEFIT_OFFICER_SIGN", signOfBenefitOfficer);
					dataMap.put("PHONE_NO", phoneNumber);
					Object benefitOfficerDateObj = null;
					if (benefitOfficerDate != null && benefitOfficerDate != "") {
						Date benefitOfficerDateNew = Date.valueOf(benefitOfficerDate);
						benefitOfficerDateObj = benefitOfficerDateNew;
					}
					dataMap.put("BENEFIT_OFFICER_DATE", benefitOfficerDateObj);					
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
			String tableName = "AEM_DIRECT_PAY_DENTAL";
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
