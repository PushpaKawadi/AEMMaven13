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

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Certificate Of Eligibility DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFCertificateEligibilityDB" })
public class CSUFCertificateOfEligibilityDB implements WorkflowProcess {
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	private static final Logger log = LoggerFactory
			.getLogger(CSUFCertificateOfEligibilityDB.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String applicationFeeOnly = "";
		String dateInitiated = "";
		String eligibleRB = "";
		String emplID = "";
		String studentID = "";
		String empStatus = "";
		String firstName = "";
		String lastName = "";
		String phoneExt = "";
		String bargainingUnit = "";
		String deptID = "";
		String deptName = "";
		String undergraduateEmp = "";
		String credentialEmp = "";
		String graduateProfessionalEmp = "";
		String calTechEmp = "";
		String doctorateProgramEmp = "";
		String graduatePostBacEmp = "";
		String emailId = "";
		String attendingCampusEmp = "";
		String classStandingEmp = "";
		String yearEmp = "";
		String semesterEmp = "";
		String humanResourceStaff = "";
		String humanResourceStaffTitle = "";
		String humanResourceDate = "";
		String staffPhone = "";
		String firstNameDep = "";
		String lastNameDep = "";
		String ssn = "";
		String studentIDDep = "";
		String attendingCampusDep = "";
		String depDOB = "";
		String depEmpRelation = "";
		String undergraduateDep = "";
		String credentialDep = "";
		String graduateProfessionalDep = "";
		String calTechDep = "";
		String doctorateProgramDep = "";
		String graduatePostBacDep = "";
		String eligibleRBDep = "";
		String yearDep = "";
		String semesterDep = "";

		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

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
							applicationFeeOnly = eElement
									.getElementsByTagName("ApplicationFeeOnly")
									.item(0).getTextContent();
							dateInitiated = eElement
									.getElementsByTagName("DateInitiated")
									.item(0).getTextContent();
							eligibleRB = eElement
									.getElementsByTagName("EligibleRB").item(0)
									.getTextContent();
							emplID = eElement.getElementsByTagName("EmplID")
									.item(0).getTextContent();
							studentID = eElement
									.getElementsByTagName("StudentID").item(0)
									.getTextContent();
							empStatus = eElement
									.getElementsByTagName("EmpStatus").item(0)
									.getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							phoneExt = eElement
									.getElementsByTagName("PhoneExt").item(0)
									.getTextContent();
							bargainingUnit = eElement
									.getElementsByTagName("BargainingUnit")
									.item(0).getTextContent();
							deptID = eElement.getElementsByTagName("DeptID")
									.item(0).getTextContent();
							undergraduateEmp = eElement
									.getElementsByTagName("Undergraduate")
									.item(0).getTextContent();
							credentialEmp = eElement
									.getElementsByTagName("Credential").item(0)
									.getTextContent();
							graduateProfessionalEmp = eElement
									.getElementsByTagName(
											"GraduateProfessional").item(0)
									.getTextContent();
							calTechEmp = eElement
									.getElementsByTagName("CalTech").item(0)
									.getTextContent();
							doctorateProgramEmp = eElement
									.getElementsByTagName("DoctorateProgram")
									.item(0).getTextContent();
							graduatePostBacEmp = eElement
									.getElementsByTagName("GraduatePostBac")
									.item(0).getTextContent();
							emailId = eElement.getElementsByTagName("Email")
									.item(0).getTextContent();
							attendingCampusEmp = eElement
									.getElementsByTagName("AttendingCampus")
									.item(0).getTextContent();
							deptName = eElement
									.getElementsByTagName("DeptName").item(0)
									.getTextContent();
							classStandingEmp = eElement
									.getElementsByTagName("ClassStanding")
									.item(0).getTextContent();
							yearEmp = eElement.getElementsByTagName("Year")
									.item(0).getTextContent();
							semesterEmp = eElement
									.getElementsByTagName("Semester").item(0)
									.getTextContent();
							humanResourceStaff = eElement
									.getElementsByTagName("HumanResourceStaff")
									.item(0).getTextContent();
							humanResourceStaffTitle = eElement
									.getElementsByTagName(
											"HumanResourceStaffTitle").item(0)
									.getTextContent();
							humanResourceDate = eElement
									.getElementsByTagName("HumanResourceDate")
									.item(0).getTextContent();
							staffPhone = eElement
									.getElementsByTagName("StaffPhone").item(0)
									.getTextContent();

							lastNameDep = eElement
									.getElementsByTagName("DepLastName")
									.item(0).getTextContent();
							firstNameDep = eElement
									.getElementsByTagName("DepFirstName")
									.item(0).getTextContent();
							ssn = eElement.getElementsByTagName("SSN").item(0)
									.getTextContent();
							studentIDDep = eElement
									.getElementsByTagName("StudentID2").item(0)
									.getTextContent();
							attendingCampusDep = eElement
									.getElementsByTagName("DepAttendingCampus")
									.item(0).getTextContent();
							depDOB = eElement.getElementsByTagName("DepDOB")
									.item(0).getTextContent();
							depEmpRelation = eElement
									.getElementsByTagName("DepRelation")
									.item(0).getTextContent();
							graduateProfessionalDep = eElement
									.getElementsByTagName(
											"DepGraduateProfessional").item(0)
									.getTextContent();
							calTechDep = eElement
									.getElementsByTagName("DepCalTech").item(0)
									.getTextContent();
							undergraduateDep = eElement
									.getElementsByTagName("DepUndergraduate")
									.item(0).getTextContent();

							doctorateProgramDep = eElement
									.getElementsByTagName("DepDoctorateProgram")
									.item(0).getTextContent();
							graduatePostBacDep = eElement
									.getElementsByTagName("DepGraduatePostBac")
									.item(0).getTextContent();
							credentialDep = eElement
									.getElementsByTagName("DepCredential")
									.item(0).getTextContent();
							eligibleRBDep = eElement
									.getElementsByTagName("DepEligibleRB")
									.item(0).getTextContent();

							yearDep = eElement.getElementsByTagName("DepYear")
									.item(0).getTextContent();

							semesterDep = eElement
									.getElementsByTagName("DepSemester")
									.item(0).getTextContent();

						}
					}
					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMP_ID", emplID);
					dataMap.put("APPLICATION_FEE_ONLY", applicationFeeOnly);

					Object dateInitiatedObj = null;
					if (dateInitiated != null && dateInitiated != "") {
						Date dateInitNew = Date.valueOf(dateInitiated);
						dateInitiatedObj = dateInitNew;
					}
					dataMap.put("DATE_INITIATED", dateInitiatedObj);
					dataMap.put("EMP_ELIGIBLE", eligibleRB);
					dataMap.put("STUDENT_ID", studentID);
					dataMap.put("EMP_STATUS", empStatus);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("PHONE_EXTENSION", phoneExt);
					dataMap.put("BARGAINING_UNIT", bargainingUnit);
					dataMap.put("DEPTID", deptID);
					dataMap.put("DEPTNAME", deptName);
					dataMap.put("EMP_UNDERGRADUATE", undergraduateEmp);
					dataMap.put("EMP_CREDENTIAL", credentialEmp);
					dataMap.put("EMP_GRADUATE_PROFESSIONAL",
							graduateProfessionalEmp);
					dataMap.put("EMP_CALTECH", calTechEmp);
					dataMap.put("EMP_DOCTORATE_PROGRAM", doctorateProgramEmp);
					dataMap.put("EMP_GRADUATE_POST_BAC", graduatePostBacEmp);
					dataMap.put("EMAIL_ID", emailId);
					dataMap.put("EMP_ATTENDING_CAMPUS", attendingCampusEmp);
					dataMap.put("EMP_CLASS_STANDING", classStandingEmp);
					dataMap.put("EMP_YEAR", yearEmp);
					dataMap.put("EMP_SEMESTER", semesterEmp);
					dataMap.put("HR_STAFF", humanResourceStaff);
					dataMap.put("HR_STAFF_TITLE", humanResourceStaffTitle);

					Object hrDateObj = null;
					if (humanResourceDate != null && humanResourceDate != "") {
						Date hrDateNew = Date.valueOf(humanResourceDate);
						hrDateObj = hrDateNew;
					}
					
					dataMap.put("HR_DATE", hrDateObj);
					dataMap.put("STAFF_PHONE", staffPhone);
					dataMap.put("DEP_LAST_NAME", lastNameDep);
					dataMap.put("DEP_FIRST_NAME", firstNameDep);
					dataMap.put("SSN", ssn);
					dataMap.put("STUDENT_ID2", studentIDDep);
					dataMap.put("DEP_ATTENDING_CAMPUS", attendingCampusDep);
					
					Object depDOBObj = null;
					if (depDOB != null && depDOB != "") {
						Date depDobNew = Date.valueOf(depDOB);
						depDOBObj = depDobNew;
					}
					dataMap.put("DEP_DOB", depDOBObj);
					
					dataMap.put("RELATION_WITH_EMP", depEmpRelation);
					dataMap.put("DEP_UNDERGRADUATE", undergraduateDep);
					dataMap.put("DEP_CREDENTIAL", credentialDep);
					dataMap.put("DEP_GRADUATE_PROFESSIONAL",
							graduateProfessionalDep);
					dataMap.put("DEP_CALTECH", calTechDep);
					dataMap.put("DEP_DOCTORATE_PROGRAM", doctorateProgramDep);
					dataMap.put("DEP_GRADUATE_POST_BAC", graduatePostBacDep);
					dataMap.put("DEP_ELIGIBLE", eligibleRBDep);
					dataMap.put("DEP_YEAR", yearDep);
					dataMap.put("DEP_SEMESTER", semesterDep);

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
			insertCataLeaveDonationData(conn, dataMap);
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
		} /*
		 * finally { try { if (con != null) { log.info("Conn Exec="); } } catch
		 * (Exception exp) { exp.printStackTrace(); } }
		 */
		return null;
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
			String tableName = "CERTIFICATE_OF_ELIGIBILITY";
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
				log.info("Before Prepared Stmt Certificate of eligibility");
				preparedStmt.execute();
				conn.commit();
				log.info("After Prepared Stmt Certificate of eligibility");
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
