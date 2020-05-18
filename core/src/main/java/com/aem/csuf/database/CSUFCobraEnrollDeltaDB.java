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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Cobra Enroll Delta Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=CobraEnrollDeltaDB" })
public class CSUFCobraEnrollDeltaDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFCobraEnrollDeltaDB.class);

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Reference
	private GlobalConfigService globalConfigService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		log.info("Inside the class");
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String issuingDate = "";
		String effectiveDate = "";
		String employeeFirstName = "";
		String employeeLastName = "";
		String employeeSSN = "";
		String employeeBirthDate = "";
		String spouseFirstName = "";
		String spouseLastName = "";
		String spouseSSN = "";
		String spouseBirthDate = "";
		String childFirstName1 = "";
		String childLastName1 = "";
		String childSSN1 = "";
		String childBirthDate1 = "";
		String childFirstName2 = "";
		String childFirstName3 = "";
		String childFirstName4 = "";
		String childFirstName5 = "";
		String childLastName2 = "";
		String childLastName3 = "";
		String childLastName4 = "";
		String childLastName5 = "";
		String childSSN2 = "";
		String childSSN3 = "";
		String childSSN4 = "";
		String childSSN5 = "";
		String childBirthDate2 = "";
		String childBirthDate3 = "";
		String childBirthDate4 = "";
		String childBirthDate5 = "";
		String address = "";
		String city = "";
		String state = "";
		String zip = "";
		String phoneNumber = "";
		String lastDayWorkedDate = "";
		String deathOfEmployeeDate = "";
		String divorseDate = "";
		String spouseMedicaDate = "";
		String dependentChildDate = "";
		String employeeCharge = "";
		String employeeOneCharge = "";
		String employeeTwoCharge = "";
		String noCoveragePlan = "";
		String continueCoveragePlan = "";
		String myselfOnly = "";
		String myselfAndDependents = "";
		String dependentsOnly = "";
		String name1 = "";
		String name2 = "";
		String name3 = "";
		String name4 = "";
		String name5 = "";
		String name6 = "";
		String employeeSignature = "";
		String employeeDate = "";
		String nameOfIndividual = "";
		String groupName = "";
		String title = "";
		String deltaGroupNumber = "";
		String employerSignature = "";
		String employerSignatureDate = "";

		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		// Get the payload path and iterate the path to find Data.xml, Use
		// Document
		// factory to parse the xml and fetch the required values for the
		// filenet
		// attachment

		String dataSourceVal = globalConfigService.getAEMDataSource();
		log.info("DataSourceVal==========" + dataSourceVal);
		conn = jdbcConnectionService.getDBConnection(dataSourceVal);
		log.info("Connection==========" + conn);

		if (conn != null) {
			while (xmlFiles.hasNext()) {
				Resource attachmentXml = xmlFiles.next();
				// log.info("xmlFiles inside ");
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

								issuingDate = eElement.getElementsByTagName("IssuingDate").item(0).getTextContent();

								effectiveDate = eElement.getElementsByTagName("EffectiveDate").item(0).getTextContent();

								employeeFirstName = eElement.getElementsByTagName("EmployeeFirstName").item(0)
										.getTextContent();

								employeeLastName = eElement.getElementsByTagName("EmployeeLastName").item(0)
										.getTextContent();

								employeeSSN = eElement.getElementsByTagName("EmployeeSSN").item(0).getTextContent();

								employeeBirthDate = eElement.getElementsByTagName("EmployeeBirthDate").item(0)
										.getTextContent();

								spouseFirstName = eElement.getElementsByTagName("SpouseFirstName").item(0)
										.getTextContent();

								spouseLastName = eElement.getElementsByTagName("SpouseLastName").item(0)
										.getTextContent();

								spouseSSN = eElement.getElementsByTagName("SpouseSSN").item(0).getTextContent();

								spouseBirthDate = eElement.getElementsByTagName("SpouseBirthDate").item(0)
										.getTextContent();

								childFirstName1 = eElement.getElementsByTagName("ChildFirstName1").item(0)
										.getTextContent();

								childLastName1 = eElement.getElementsByTagName("ChildLastName1").item(0)
										.getTextContent();

								childSSN1 = eElement.getElementsByTagName("ChildSSN1").item(0).getTextContent();

								childBirthDate1 = eElement.getElementsByTagName("ChildBirthDate1").item(0)
										.getTextContent();

								childFirstName2 = eElement.getElementsByTagName("ChildFirstName2").item(0)
										.getTextContent();

								childFirstName3 = eElement.getElementsByTagName("ChildFirstName3").item(0)
										.getTextContent();

								childFirstName4 = eElement.getElementsByTagName("ChildFirstName4").item(0)
										.getTextContent();

								childFirstName5 = eElement.getElementsByTagName("ChildFirstName5").item(0)
										.getTextContent();

								childLastName2 = eElement.getElementsByTagName("ChildLastName2").item(0)
										.getTextContent();

								childLastName3 = eElement.getElementsByTagName("ChildLastName3").item(0)
										.getTextContent();

								childLastName4 = eElement.getElementsByTagName("ChildLastName4").item(0)
										.getTextContent();

								childLastName5 = eElement.getElementsByTagName("ChildLastName5").item(0)
										.getTextContent();

								childSSN2 = eElement.getElementsByTagName("ChildSSN2").item(0).getTextContent();

								childSSN3 = eElement.getElementsByTagName("ChildSSN3").item(0).getTextContent();

								childSSN4 = eElement.getElementsByTagName("ChildSSN4").item(0).getTextContent();

								childSSN5 = eElement.getElementsByTagName("ChildSSN5").item(0).getTextContent();

								childBirthDate2 = eElement.getElementsByTagName("ChildBirthDate2").item(0)
										.getTextContent();

								childBirthDate3 = eElement.getElementsByTagName("ChildBirthDate3").item(0)
										.getTextContent();

								childBirthDate4 = eElement.getElementsByTagName("ChildBirthDate4").item(0)
										.getTextContent();

								childBirthDate5 = eElement.getElementsByTagName("ChildBirthDate5").item(0)
										.getTextContent();

								address = eElement.getElementsByTagName("Address").item(0).getTextContent();

								city = eElement.getElementsByTagName("City").item(0).getTextContent();

								state = eElement.getElementsByTagName("State").item(0).getTextContent();

								zip = eElement.getElementsByTagName("Zip").item(0).getTextContent();

								phoneNumber = eElement.getElementsByTagName("PhoneNumber").item(0).getTextContent();

								lastDayWorkedDate = eElement.getElementsByTagName("LastDayWorkedDate").item(0)
										.getTextContent();

								deathOfEmployeeDate = eElement.getElementsByTagName("DeathOfEmployeeDate").item(0)
										.getTextContent();

								divorseDate = eElement.getElementsByTagName("DivorseDate").item(0).getTextContent();

								spouseMedicaDate = eElement.getElementsByTagName("SpouseMedicaDate").item(0)
										.getTextContent();

								dependentChildDate = eElement.getElementsByTagName("DependentChildDate").item(0)
										.getTextContent();

								employeeCharge = eElement.getElementsByTagName("EmployeeCharge").item(0)
										.getTextContent();

								employeeOneCharge = eElement.getElementsByTagName("EmployeeOneCharge").item(0)
										.getTextContent();

								employeeTwoCharge = eElement.getElementsByTagName("EmployeeTwoCharge").item(0)
										.getTextContent();

								noCoveragePlan = eElement.getElementsByTagName("NoCoveragePlan").item(0)
										.getTextContent();

								continueCoveragePlan = eElement.getElementsByTagName("ContinueCoveragePlan").item(0)
										.getTextContent();

								myselfOnly = eElement.getElementsByTagName("MyselfOnly").item(0).getTextContent();

								myselfAndDependents = eElement.getElementsByTagName("MyselfAndDependents").item(0)
										.getTextContent();

								dependentsOnly = eElement.getElementsByTagName("DependentsOnly").item(0)
										.getTextContent();

								name1 = eElement.getElementsByTagName("Name1").item(0).getTextContent();

								name2 = eElement.getElementsByTagName("Name2").item(0).getTextContent();

								name3 = eElement.getElementsByTagName("Name3").item(0).getTextContent();

								name4 = eElement.getElementsByTagName("Name4").item(0).getTextContent();

								name5 = eElement.getElementsByTagName("Name5").item(0).getTextContent();

								name6 = eElement.getElementsByTagName("Name6").item(0).getTextContent();

								employeeSignature = eElement.getElementsByTagName("EmployeeSignature").item(0)
										.getTextContent();

								employeeDate = eElement.getElementsByTagName("EmployeeDate").item(0).getTextContent();

								nameOfIndividual = eElement.getElementsByTagName("NameOfIndividual").item(0)
										.getTextContent();

								groupName = eElement.getElementsByTagName("GroupName").item(0).getTextContent();

								title = eElement.getElementsByTagName("Title").item(0).getTextContent();

								deltaGroupNumber = eElement.getElementsByTagName("DeltaGroupdNumber").item(0)
										.getTextContent();

								employerSignature = eElement.getElementsByTagName("EmployerSignature").item(0)
										.getTextContent();

								employerSignatureDate = eElement.getElementsByTagName("EmployerDate").item(0)
										.getTextContent();

							}
						}
						dataMap = new LinkedHashMap<String, Object>();

						Object issuingDateObj = null;
						if (issuingDate != null && issuingDate != "") {
							Date issuingDateNew = Date.valueOf(issuingDate);
							issuingDateObj = issuingDateNew;
						}
						dataMap.put("ISSUING_DATE", issuingDateObj);

						Object effectiveDateObj = null;
						if (effectiveDate != null && effectiveDate != "") {
							Date effectiveDateNew = Date.valueOf(effectiveDate);
							effectiveDateObj = effectiveDateNew;
						}
						dataMap.put("EFFECTIVE_DATE", effectiveDateObj);
						dataMap.put("EMPLOYEE_FIRST_NAME", employeeFirstName);
						dataMap.put("EMPLOYEE_LAST_NAME", employeeLastName);
						dataMap.put("EMPLOYEE_SSN", employeeSSN);

						Object employeeBirthDateObj = null;
						if (employeeBirthDate != null && employeeBirthDate != "") {
							Date employeeBirthDateNew = Date.valueOf(employeeBirthDate);
							employeeBirthDateObj = employeeBirthDateNew;
						}
						dataMap.put("EMPLOYEE_BIRTH_DATE", employeeBirthDateObj);
						dataMap.put("SPOUSE_FIRST_NAME", spouseFirstName);
						dataMap.put("SPOUSE_LAST_NAME", spouseLastName);
						dataMap.put("SPOUSE_SSN", spouseSSN);

						Object spouseBirthDateObj = null;
						if (spouseBirthDate != null && spouseBirthDate != "") {
							Date spouseBirthDateNew = Date.valueOf(spouseBirthDate);
							spouseBirthDateObj = spouseBirthDateNew;
						}
						dataMap.put("SPOUSE_BIRTH_DATE", spouseBirthDateObj);
						dataMap.put("CHILD_FIRST_NAME1", childFirstName1);
						dataMap.put("CHILD_LAST_NAME1", childLastName1);
						dataMap.put("CHILD_SSN1", childSSN1);

						Object schildBirthDate1Obj = null;
						if (childBirthDate1 != null && childBirthDate1 != "") {
							Date childBirthDate1New = Date.valueOf(childBirthDate1);
							schildBirthDate1Obj = childBirthDate1New;
						}
						dataMap.put("CHILD_BIRTH_DATE1", schildBirthDate1Obj);
						dataMap.put("CHILD_FIRST_NAME2", childFirstName2);
						dataMap.put("CHILD_LAST_NAME2", childLastName2);
						dataMap.put("CHILD_SSN2", childSSN2);

						Object childBirthDate2Obj = null;
						if (childBirthDate2 != null && childBirthDate2 != "") {
							Date childBirthDate2New = Date.valueOf(childBirthDate2);
							childBirthDate2Obj = childBirthDate2New;
						}
						dataMap.put("CHILD_BIRTHDATE2", childBirthDate2Obj);
						dataMap.put("CHILD_FIRST_NAME3", childFirstName3);
						dataMap.put("CHILD_LAST_NAME3", childLastName3);
						dataMap.put("CHILD_SSN3", childSSN3);

						Object childBirthDate3Obj = null;
						if (childBirthDate3 != null && childBirthDate3 != "") {
							Date supervisorSignedDateNew = Date.valueOf(childBirthDate3);
							childBirthDate3Obj = supervisorSignedDateNew;
						}
						dataMap.put("CHILD_BIRTHDATE3", childBirthDate3Obj);
						dataMap.put("CHILD_FIRST_NAME4", childFirstName4);
						dataMap.put("CHILD_LAST_NAME4", childLastName4);
						dataMap.put("CHILD_SSN4", childSSN4);

						Object childBirthDate4Obj = null;
						if (childBirthDate4 != null && childBirthDate4 != "") {
							Date childBirthDate4New = Date.valueOf(childBirthDate4);
							childBirthDate4Obj = childBirthDate4New;
						}
						dataMap.put("CHILD_BIRTHDATE4", childBirthDate4Obj);
						dataMap.put("CHILD_FIRST_NAME5", childFirstName5);
						dataMap.put("CHILD_LAST_NAME5", childLastName5);
						dataMap.put("CHILD_SSN5", childSSN5);

						Object childBirthDate5Obj = null;
						if (childBirthDate5 != null && childBirthDate5 != "") {
							Date schildBirthDate5New = Date.valueOf(childBirthDate5);
							childBirthDate5Obj = schildBirthDate5New;
						}
						dataMap.put("CHILD_BIRTHDATE5", childBirthDate5Obj);
						dataMap.put("ADDRESS", address);
						dataMap.put("CITY", city);
						dataMap.put("STATE", state);
						dataMap.put("ZIP", zip);
						dataMap.put("PHONE_NUMBER", phoneNumber);

						Object lastDayWorkedDateObj = null;
						if (lastDayWorkedDate != null && lastDayWorkedDate != "") {
							Date lastDayWorkedDateNew = Date.valueOf(lastDayWorkedDate);
							lastDayWorkedDateObj = lastDayWorkedDateNew;
						}
						dataMap.put("LAST_DAY_WORKED_DATE", lastDayWorkedDateObj);

						Object deathOfEmployeeDateObj = null;
						if (deathOfEmployeeDate != null && deathOfEmployeeDate != "") {
							Date deathOfEmployeeDateNew = Date.valueOf(deathOfEmployeeDate);
							deathOfEmployeeDateObj = deathOfEmployeeDateNew;
						}
						dataMap.put("DEATH_OF_EMPLOYEE_DATE", deathOfEmployeeDateObj);

						Object divorseDateObj = null;
						if (divorseDate != null && divorseDate != "") {
							Date divorseDateNew = Date.valueOf(divorseDate);
							divorseDateObj = divorseDateNew;
						}
						dataMap.put("DIVORSE_DATE", divorseDateObj);

						Object spouseMedicaDateObj = null;
						if (spouseMedicaDate != null && spouseMedicaDate != "") {
							Date spouseMedicaDateNew = Date.valueOf(spouseMedicaDate);
							spouseMedicaDateObj = spouseMedicaDateNew;
						}
						dataMap.put("SPOUSE_MEDICA_DATE", spouseMedicaDateObj);

						Object dependentChildDateObj = null;
						if (dependentChildDate != null && dependentChildDate != "") {
							Date dependentChildDateNew = Date.valueOf(dependentChildDate);
							dependentChildDateObj = dependentChildDateNew;
						}
						dataMap.put("DEPENDENT_CHILD_DATE", dependentChildDateObj);
						dataMap.put("EMPLOYEE_CHARGE", employeeCharge);
						dataMap.put("EMPLOYEE_ONE_CHARGE", employeeOneCharge);
						dataMap.put("EMPLOYEE_TWO_CHARGE", employeeTwoCharge);
						dataMap.put("NO_COVERAGE_PLAN", noCoveragePlan);
						dataMap.put("CONTINUE_COVERAGE_PLAN", continueCoveragePlan);
						dataMap.put("MY_SELF_ONLY", myselfOnly);
						dataMap.put("MY_SELF_AND_DEPENDENTS", myselfAndDependents);
						dataMap.put("DEPENDENTS_ONLY", dependentsOnly);
						dataMap.put("NAME1", name1);
						dataMap.put("NAME2", name2);
						dataMap.put("NAME3", name3);
						dataMap.put("NAME4", name4);
						dataMap.put("NAME5", name5);
						dataMap.put("NAME6", name6);
						dataMap.put("EMPLOYEE_SIGNATURE", employeeSignature);

						Object employeeDateObj = null;
						if (employeeDate != null && employeeDate != "") {
							Date employeeDateNew = Date.valueOf(employeeDate);
							employeeDateObj = employeeDateNew;
						}
						dataMap.put("EMPLOYEE_DATE", employeeDateObj);
						dataMap.put("NAME_OF_INDIVIDUAL", nameOfIndividual);
						dataMap.put("GROUP_NAME", groupName);
						dataMap.put("TITLE", title);
						dataMap.put("DELTA_GROUP_NUMBER", deltaGroupNumber);
						dataMap.put("EMPLOYER_SIGNATURE", employerSignature);

						Object employerSignatureDateObj = null;
						if (employerSignatureDate != null && employerSignatureDate != "") {
							Date employerSignatureDateNew = Date.valueOf(employerSignatureDate);
							employerSignatureDateObj = employerSignatureDateNew;
						}
						dataMap.put("EMPLOYER_SIGNATURE_DATE", employerSignatureDateObj);

						log.error("Connection Successfull");
						insertCobraEnrollDeltaData(conn, dataMap);

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
		}
	}
	
	public void insertCobraEnrollDeltaData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_COBRA_ENROLL_DELTA";
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
				log.error("Exception3");
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
				log.error("Exception2");
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
					log.info("The Vlaue is=" + value);
				}
			} catch (SQLException e) {
				log.error("SQLException=" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				log.error("Exception4");
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}
			try {
				log.error("Before Cobra Enroll Delta Prepared stmt");
				preparedStmt.execute();
				conn.commit();
				log.error("After Cobra Enroll Delta Prepared stmt");
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
				log.error("Exception5");
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
						log.error("Exception7");
						log.error("Exception=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
