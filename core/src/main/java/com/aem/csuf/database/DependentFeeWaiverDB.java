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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Dependent Fee Waiver Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=DependentFeeWaiverDB" })
public class DependentFeeWaiverDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(DependentFeeWaiverDB.class);
	
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

		String firstName = "";
		String lastName = "";
		String empID = "";
		String departmentID = "";
		String extension = "";
		String bargainingUnit = "";
		String jobCode = "";
		String fullTime = "";
		String partTime = "";
		String tenure = "";
		String perm = "";
		String probationary = "";
		String others = "";
		String termStatus = "";
		String leavesYes = "";
		String leavesNo = "";
		String applicantFirstName = "";
		String applicantLastName = "";
		String applicantStudentID = "";
		String applicantAddress = "";
		String applicantCity = "";
		String applicantState = "";
		String applicantDateOfBirth = "";
		String applicantEmailAddress = "";
		String applicantPhone = "";
		String alternatePhone = "";
		String relationToEmployee = "";
		String studentType = "";
		String classStanding = "";
		String semster = "";
		String campusOfEnrollment = "";
		String degreeProgram = "";
		String yearSemester = "";
		String courseTitle1 = "";
		String courseTitle2 = "";
		String onlineCourse1Yes = "";
		String onlineCourse2Yes = "";
		String onlineCourse1No = "";
		String onlineCourse2No = "";
		String employeeComments = "";
		String initials = "";
		String employeeSignature = "";
		String employeeDate = "";
		String hrComments = "";
		String feeWaiverGranted = "";
		String feeWaiverDenied = "";
		String term = "";
		String edde = "";
		String hrSignature = "";
		String hrDate = "";

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

							lastName = eElement.getElementsByTagName("lastName").item(0).getTextContent();
							log.info("lastName Value is: " + lastName);

							firstName = eElement.getElementsByTagName("firstName").item(0).getTextContent();
							log.info("firstName Value is: " + firstName);

							empID = eElement.getElementsByTagName("empID").item(0).getTextContent();
							log.info("empID Value is: " + empID);

							departmentID = eElement.getElementsByTagName("departmentID").item(0).getTextContent();
							log.info("departmentID Value is: " + departmentID);

							extension = eElement.getElementsByTagName("extension").item(0).getTextContent();
							log.info("extension Value is: " + extension);

							bargainingUnit = eElement.getElementsByTagName("bargainingUnit").item(0).getTextContent();
							log.info("bargainingUnit Value is: " + bargainingUnit);

							jobCode = eElement.getElementsByTagName("jobCode").item(0).getTextContent();
							log.info("jobCode Value is: " + jobCode);

							fullTime = eElement.getElementsByTagName("fullTime").item(0).getTextContent();
							log.info("fullTime Value is: " + fullTime);

							partTime = eElement.getElementsByTagName("partTime").item(0).getTextContent();
							log.info("partTime Value is: " + partTime);

							tenure = eElement.getElementsByTagName("tenure").item(0).getTextContent();
							log.info("tenure Value is: " + tenure);

							perm = eElement.getElementsByTagName("perm").item(0).getTextContent();
							log.info("perm Value is: " + perm);

							probationary = eElement.getElementsByTagName("probationary").item(0).getTextContent();
							log.info("probationary Value is: " + probationary);

							others = eElement.getElementsByTagName("others").item(0).getTextContent();
							log.info("others Value is: " + others);

							termStatus = eElement.getElementsByTagName("temStatus").item(0).getTextContent();
							log.info("termStatus Value is: " + termStatus);

							leavesYes = eElement.getElementsByTagName("leavesYes").item(0).getTextContent();
							log.info("leavesYes Value is: " + leavesYes);

							leavesNo = eElement.getElementsByTagName("leavesNo").item(0).getTextContent();
							log.info("leavesNo Value is: " + leavesNo);

							applicantFirstName = eElement.getElementsByTagName("applicantFirstName")
									.item(0).getTextContent();
							log.info("applicantFirstName Value is: " + applicantFirstName);

							applicantLastName = eElement.getElementsByTagName("applicantLastName")
									.item(0).getTextContent();
							log.info("applicantLastName Value is: " + applicantLastName);

							 applicantStudentID = eElement.getElementsByTagName("applicantStudentID")
							 .item(0).getTextContent();
							 log.info("applicantStudentID Value is: " + applicantStudentID);
							 
							 applicantAddress = eElement.getElementsByTagName("applicantAddress")
							 .item(0).getTextContent();
							 log.info("applicantEmailAddress Value is: "+applicantAddress);
							 
							 applicantCity = eElement.getElementsByTagName("applicantCity")
							 .item(0).getTextContent();
							 log.info("applicantPhone Value is: "+applicantCity);

							 applicantState = eElement.getElementsByTagName("applicantState")
							 .item(0).getTextContent();
							 log.info("alternatePhone Value is: "+applicantState);
							 
							 applicantDateOfBirth = eElement.getElementsByTagName("applicantDateOfBirth")
							 .item(0).getTextContent();
							 log.info("applicantDateOfBirth Value is: "+applicantDateOfBirth);

							 applicantEmailAddress = eElement.getElementsByTagName("applicantEmailAddress")
							 .item(0).getTextContent();
							 log.info("applicantAddress Value is: " + applicantAddress);

							 applicantPhone = eElement.getElementsByTagName("applicantPhone")
							 .item(0).getTextContent();
							 log.info("applicantCity Value is: " + applicantPhone);

							 alternatePhone = eElement.getElementsByTagName("alternatePhone")
							 .item(0).getTextContent();
							 log.info("applicantState Value is: " + alternatePhone);

							 relationToEmployee = eElement.getElementsByTagName("relationToEmployee")
							 .item(0).getTextContent();
							 log.info("relationToEmployee Value is: "+relationToEmployee);

							 studentType = eElement.getElementsByTagName("studentType")
							 .item(0).getTextContent();
							 log.info("studentType Value is: "+studentType);

							 classStanding = eElement.getElementsByTagName("classStanding")
							 .item(0).getTextContent();
							 log.info("classStanding Value is: "+classStanding);

							 semster = eElement.getElementsByTagName("semster")
							 .item(0).getTextContent();
							 log.info("semster Value is: "+semster);

							 campusOfEnrollment = eElement.getElementsByTagName("campusOfEnrollment")
							 .item(0).getTextContent();
							 log.info("campusOfEnrollment Value is: " + campusOfEnrollment);

							 degreeProgram = eElement.getElementsByTagName("degreeProgram")
							 .item(0).getTextContent();
							 log.info("degreeProgram Value is: " + degreeProgram);

							 yearSemester = eElement.getElementsByTagName("yearSemester")
							 .item(0).getTextContent();
							 log.info("yearSemester Value is: " + yearSemester);

							courseTitle1 = eElement.getElementsByTagName("courseTitle1").item(0).getTextContent();
							log.info("courseTitle1 Value is: " + courseTitle1);

							onlineCourse1Yes = eElement.getElementsByTagName("onlineCourse1Yes").item(0)
									.getTextContent();
							log.info("onlineCourse1Yes Value is: " + onlineCourse1Yes);

//							onlineCourse1No = eElement.getElementsByTagName("onlineCourse1No").item(0).getTextContent();
//							log.info("onlineCourse1No Value is: " + onlineCourse1No);

							courseTitle2 = eElement.getElementsByTagName("courseTitle2").item(0).getTextContent();
							log.info("courseTitle2 Value is: " + courseTitle2);

							onlineCourse2Yes = eElement.getElementsByTagName("onlineCourse2Yes").item(0)
									.getTextContent();
							log.info("onlineCourse2Yes Value is: " + onlineCourse2Yes);

//							onlineCourse2No = eElement.getElementsByTagName("onlineCourse2No").item(0).getTextContent();
//							log.info("onlineCourse2No Value is: " + onlineCourse2No);

							feeWaiverGranted = eElement.getElementsByTagName("feeWaiverGranted").item(0)
									.getTextContent();
							log.info("feeWaiverGranted Value is: " + feeWaiverGranted);
							
							term = eElement.getElementsByTagName("term").item(0).getTextContent();
							log.info("term Value is: " + term);

							edde = eElement.getElementsByTagName("edde").item(0).getTextContent();
							log.info("edde Value is: " + edde);

							feeWaiverDenied = eElement.getElementsByTagName("feeWaiverDenied").item(0).getTextContent();
							log.info("feeWaiverDenied Value is: " + feeWaiverDenied);					

							hrComments = eElement.getElementsByTagName("hrComments").item(0).getTextContent();
							log.info("hrComments Value is: " + hrComments);

							hrSignature = eElement.getElementsByTagName("hrSignature").item(0).getTextContent();
							log.info("hrSignature Value is: " + hrSignature);

							hrDate = eElement.getElementsByTagName("hrDate").item(0).getTextContent();
							log.info("hrDate Value is: " + hrDate);

							employeeComments = eElement.getElementsByTagName("employeeComments").item(0)
									.getTextContent();
							log.info("employeeComments Value is: " + employeeComments);

							initials = eElement.getElementsByTagName("initials").item(0).getTextContent();
							log.info("initials Value is: " + initials);

							employeeSignature = eElement.getElementsByTagName("employeeSignature").item(0)
									.getTextContent();
							log.info("employeeSignature Value is: " + employeeSignature);

							employeeDate = eElement.getElementsByTagName("employeeDate").item(0).getTextContent();
							log.info("employeeDate Value is: " + employeeDate);

						}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("EMP_ID", empID);
					dataMap.put("DEPARTMENT_NAME", departmentID);
					dataMap.put("EXTENSION", extension);
					dataMap.put("BARGAINING_UNIT", bargainingUnit);
					dataMap.put("JOB_CODE", jobCode);
					dataMap.put("FULL_TIME", fullTime);
					dataMap.put("PART_TIME", partTime);
					dataMap.put("TENURE", tenure);
					dataMap.put("PERM", perm);
					dataMap.put("PROBATIONARY", probationary);
					dataMap.put("OTHER", others);
					dataMap.put("TERM_STATUS", termStatus);
					dataMap.put("LEAVES_YES", leavesYes);
					dataMap.put("LEAVES_NO", leavesNo);
					
					dataMap.put("APPLICANT_FIRST_NAME", applicantFirstName);
					dataMap.put("APPLICANT_LAST_NAME", applicantLastName);
					dataMap.put("APPLICANT_STUDENT_ID", applicantStudentID);
					dataMap.put("APPLICANT_ADDRESS", applicantAddress);
					dataMap.put("APPLICANT_CITY", applicantCity);
					dataMap.put("APPLICANT_STATE", applicantState);
					Object applicantDateOfBirthObj = null;
					if (applicantDateOfBirth != null && applicantDateOfBirth != "") {
						Date applicantDateOfBirthNew = Date.valueOf(applicantDateOfBirth);
						applicantDateOfBirthObj = applicantDateOfBirthNew;
					}
					dataMap.put("APPLICANT_DATE_OF_BIRTH", applicantDateOfBirthObj);
					dataMap.put("APPLICANT_EMAIL_ADDRESS", applicantEmailAddress);
					dataMap.put("APPLICANT_HOME_PHONE", applicantPhone);
					dataMap.put("APPLICANT_ALTERNATE_PHONE", alternatePhone);
					dataMap.put("RELATION_TO_EMPLOYEE", relationToEmployee);
					dataMap.put("STUDENT_TYPE", studentType);
					dataMap.put("CLASS_STANDING", classStanding);
					dataMap.put("SEMESTER", semster);
					dataMap.put("CAMPUS_OF_ENROLLMENT", campusOfEnrollment);
					dataMap.put("DEGREE_PROGRAM", degreeProgram);
					dataMap.put("YEAR_SEMESTER", yearSemester);				

					dataMap.put("COURSE_TITLE1", courseTitle1);
					dataMap.put("ONLINE_COURSE1_YES", onlineCourse1Yes);
					//dataMap.put("ONLINE_COURSE1_NO", onlineCourse2No);
					dataMap.put("COURSE_TITLE2", courseTitle2);
					dataMap.put("ONLINE_COURSE2_YES", onlineCourse2Yes);
					//dataMap.put("ONLINE_COURSE2_NO", onlineCourse2No);
					dataMap.put("FEE_WAIVER_GRANTED", feeWaiverGranted);
					dataMap.put("FEE_WAIVER_DENIED", feeWaiverDenied);
					dataMap.put("TERM", term);
					dataMap.put("EDDE", edde);
					dataMap.put("HR_COMMENT", hrComments);
					dataMap.put("HR_SIGNATURE", hrSignature);

					Object hrDateObj = null;
					if (hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}
					dataMap.put("HR_DATE", hrDateObj);
					dataMap.put("EMPLOYEE_COMMENT", employeeComments);
					dataMap.put("INITIALS", initials);
					dataMap.put("EMPLOYEE_SIGNATURE", employeeSignature);

					Object employeeDateObj = null;
					if (employeeDate != null && employeeDate != "") {
						Date employeeDateNew = Date.valueOf(employeeDate);
						employeeDateObj = employeeDateNew;
					}
					dataMap.put("EMPLOYEE_DATE", employeeDateObj);

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
			insertDependentFeeWaiverData(conn, dataMap);
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

	public void insertDependentFeeWaiverData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_DEPENDENT_FEE_WAIVER";
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
				log.error("Before Prepared stmt");
				preparedStmt.execute();
				conn.commit();
				log.error("After Prepared stmt");
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
