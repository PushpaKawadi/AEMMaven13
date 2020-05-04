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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Employee Fee Waiver Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=EmployeeFeeWaiverDB" })
public class EmployeeFeeWaiverDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(EmployeeFeeWaiverDB.class);
	
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
		String empId = "";
		String departmentName = "";
		String extension = "";
		String bargainingUnit = "";
		String jobCode = "";
		String jobType = "";
		String probationStatus = "";
		String temporary = "";
		String enddate = "";
		String onLeaveCheck = "";
		String semester = "";
		String year = "";
		String classStanding = "";
		String studentType = "";
		String campus_Attending = "";
		String studentId = "";
		String approvedPlan = "";
		String major = "";
		String courseInfoComment = "";
		String courseTitle1 = "";
		String wr_Cd1 = "";
		String onlineCourse1 = "";
		String unit1 = "";
		String day1 = "";
		String hours1 = "";
		String releaseTime1 = "";
		String courseTitle2 = "";			
		String wr_Cd2 = "";
		String onlineCourse2 = "";	
		String unit2 = "";
		String day2 = "";
		String hours2 = "";
		String releaseTime2 = "";
		String comment1 = "";
		String gradProfBusStatement = "";
		String authorizationInitials = "";
		String empSign = "";
		String dateInitiated = "";
		String SemesterCode1 = "";
		String ClassStandingCode1 = "";
		String deptChairPrintName = "";
		String deptChairSign = "";
		String deptchairSignedDate = "";
		String collegeDeanPrintName = "";
		String collegeDeanSign = "";
		String collegeDeanSignedDate = "";
		String provostPrintName = "";
		String provostSign = "";
		String provostDate = "";
		String releaseTimeCheck = "";
		String WorkScheduleChangeCheck = "";
		String daysAndTimes = "";
		String supervisorPrintname = "";
		String supervisorSign = "";
		String supervisorSignedDate = "";
		String administratorPrintName = "";
		String administratorSign = "";
		String administratorSignedDate = "";
		String hrComments = "";
		String feeWaiverGranted = "";
		String feeWaiverDenied = "";
		String term = "";
		String EDDE = "";
		String hrSign = "";
		String hrSigneddate = "";

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

							firstName = eElement.getElementsByTagName("firstName").item(0).getTextContent();
							log.info("firstName Value is: " + firstName);

							lastName = eElement.getElementsByTagName("lastName").item(0).getTextContent();
							log.info("lastName Value is: " + lastName);

							empId = eElement.getElementsByTagName("empId").item(0).getTextContent();
							log.info("empId Value is: " + empId);

							departmentName = eElement.getElementsByTagName("departmentName").item(0).getTextContent();
							log.info("departmentName Value is: " + departmentName);

							extension = eElement.getElementsByTagName("extension").item(0).getTextContent();
							log.info("extension Value is: " + extension);

							bargainingUnit = eElement.getElementsByTagName("bargainingUnit").item(0).getTextContent();
							log.info("bargainingUnit Value is: " + bargainingUnit);

							jobCode = eElement.getElementsByTagName("jobCode").item(0).getTextContent();
							log.info("jobCode Value is: " + jobCode);

							jobType = eElement.getElementsByTagName("jobType").item(0).getTextContent();
							log.info("jobType Value is: " + jobType);

							probationStatus = eElement.getElementsByTagName("probationStatus").item(0).getTextContent();
							log.info("probationStatus Value is: " + probationStatus);

							temporary = eElement.getElementsByTagName("temporary").item(0).getTextContent();
							log.info("temporary Value is: " + temporary);

							enddate = eElement.getElementsByTagName("enddate").item(0).getTextContent();
							log.info("enddate Value is: " + enddate);

							onLeaveCheck = eElement.getElementsByTagName("onLeaveCheck").item(0).getTextContent();
							log.info("onLeaveCheck Value is: " + onLeaveCheck);							

							semester = eElement.getElementsByTagName("semester")
									.item(0).getTextContent();
							log.info("semester Value is: " + semester);

							year = eElement.getElementsByTagName("year")
									.item(0).getTextContent();
							log.info("year Value is: " + year);

							classStanding = eElement.getElementsByTagName("classStanding")
							 .item(0).getTextContent();
							 log.info("classStanding Value is: " + classStanding);
							 
							 studentType = eElement.getElementsByTagName("studentType")
							 .item(0).getTextContent();
							 log.info("studentType Value is: "+studentType);
							 
							 campus_Attending = eElement.getElementsByTagName("campus_Attending")
							 .item(0).getTextContent();
							 log.info("campus_Attending Value is: "+campus_Attending	);

							 studentId = eElement.getElementsByTagName("studentId")
							 .item(0).getTextContent();
							 log.info("studentId Value is: "+studentId);
							 
							 approvedPlan = eElement.getElementsByTagName("approvedPlan")
							 .item(0).getTextContent();
							 log.info("approvedPlan Value is: "+approvedPlan);

							 major = eElement.getElementsByTagName("major")
							 .item(0).getTextContent();
							 log.info("major Value is: " + major);

							 courseInfoComment = eElement.getElementsByTagName("courseInfoComment")
							 .item(0).getTextContent();
							 log.info("courseInfoComment Value is: " + courseInfoComment);

							 courseTitle1 = eElement.getElementsByTagName("courseTitle1")
							 .item(0).getTextContent();
							 log.info("courseTitle1 Value is: "+courseTitle1);

							 wr_Cd1 = eElement.getElementsByTagName("wr_Cd1")
							 .item(0).getTextContent();
							 log.info("wr_Cd1 Value is: "+wr_Cd1);

							 onlineCourse1 = eElement.getElementsByTagName("onlineCourse1")
							 .item(0).getTextContent();
							 log.info("onlineCourse1 Value is: "+onlineCourse1);

							 unit1 = eElement.getElementsByTagName("unit1")
							 .item(0).getTextContent();
							 log.info("unit1 Value is: "+unit1);

							 day1 = eElement.getElementsByTagName("day1")
							 .item(0).getTextContent();
							 log.info("day1 Value is: " + day1);

							 hours1 = eElement.getElementsByTagName("hours1")
							 .item(0).getTextContent();
							 log.info("hours1 Value is: " + hours1);

							 releaseTime1 = eElement.getElementsByTagName("releaseTime1")
							 .item(0).getTextContent();
							 log.info("releaseTime1 Value is: " + releaseTime1);

							 courseTitle2 = eElement.getElementsByTagName("courseTitle2").item(0).getTextContent();
							log.info("courseTitle2 Value is: " + courseTitle2);

							wr_Cd2 = eElement.getElementsByTagName("wr_Cd2").item(0)
									.getTextContent();
							log.info("wr_Cd2 Value is: " + wr_Cd2);

							onlineCourse2 = eElement.getElementsByTagName("onlineCourse2").item(0).getTextContent();
							log.info("onlineCourse2 Value is: " + onlineCourse2);

							unit2 = eElement.getElementsByTagName("unit2").item(0)
									.getTextContent();
							log.info("unit2 Value is: " + unit2);

							day2 = eElement.getElementsByTagName("day2").item(0)
									.getTextContent();
							log.info("day2 Value is: " + day2);
							
							hours2 = eElement.getElementsByTagName("hours2").item(0).getTextContent();
							log.info("hours2 Value is: " + hours2);

							releaseTime2 = eElement.getElementsByTagName("releaseTime2").item(0).getTextContent();
							log.info("releaseTime2 Value is: " + releaseTime2);

							comment1 = eElement.getElementsByTagName("comment1").item(0).getTextContent();
							log.info("comment1 Value is: " + comment1);					

							gradProfBusStatement = eElement.getElementsByTagName("gradProfBusStatement").item(0).getTextContent();
							log.info("gradProfBusStatement Value is: " + gradProfBusStatement);

							authorizationInitials = eElement.getElementsByTagName("authorizationInitials").item(0).getTextContent();
							log.info("authorizationInitials Value is: " + authorizationInitials);

							empSign = eElement.getElementsByTagName("empSign").item(0).getTextContent();
							log.info("empSign Value is: " + empSign);

							dateInitiated = eElement.getElementsByTagName("dateInitiated").item(0)
									.getTextContent();
							log.info("dateInitiated Value is: " + dateInitiated);

							deptChairPrintName = eElement.getElementsByTagName("deptChairPrintName").item(0).getTextContent();
							log.info("deptChairPrintName Value is: " + deptChairPrintName);

							deptChairSign = eElement.getElementsByTagName("deptChairSign").item(0)
									.getTextContent();
							log.info("deptChairSign Value is: " + deptChairSign);

							deptchairSignedDate = eElement.getElementsByTagName("deptchairSignedDate").item(0).getTextContent();
							log.info("deptchairSignedDate Value is: " + deptchairSignedDate);
							
							collegeDeanPrintName = eElement.getElementsByTagName("collegeDeanPrintName").item(0).getTextContent();
							log.info("collegeDeanPrintName Value is: " + collegeDeanPrintName);					

							collegeDeanSign = eElement.getElementsByTagName("collegeDeanSign").item(0).getTextContent();
							log.info("collegeDeanSign Value is: " + collegeDeanSign);

							collegeDeanSignedDate = eElement.getElementsByTagName("collegeDeanSignedDate").item(0).getTextContent();
							log.info("collegeDeanSignedDate Value is: " + collegeDeanSignedDate);

							provostPrintName = eElement.getElementsByTagName("provostPrintName").item(0).getTextContent();
							log.info("provostPrintName Value is: " + provostPrintName);

							provostSign = eElement.getElementsByTagName("provostSign").item(0)
									.getTextContent();
							log.info("provostSign Value is: " + provostSign);

							provostDate = eElement.getElementsByTagName("provostDate").item(0).getTextContent();
							log.info("provostDate Value is: " + provostDate);

							releaseTimeCheck = eElement.getElementsByTagName("releaseTimeCheck").item(0)
									.getTextContent();
							log.info("releaseTimeCheck Value is: " + releaseTimeCheck);

							WorkScheduleChangeCheck = eElement.getElementsByTagName("WorkScheduleChangeCheck").item(0).getTextContent();
							log.info("WorkScheduleChangeCheck Value is: " + WorkScheduleChangeCheck);
							
							daysAndTimes = eElement.getElementsByTagName("daysAndTimes").item(0).getTextContent();
							log.info("daysAndTimes Value is: " + daysAndTimes);

							supervisorPrintname = eElement.getElementsByTagName("supervisorPrintname").item(0).getTextContent();
							log.info("supervisorPrintname Value is: " + supervisorPrintname);

							supervisorSign = eElement.getElementsByTagName("supervisorSign").item(0)
									.getTextContent();
							log.info("supervisorSign Value is: " + supervisorSign);

							supervisorSignedDate = eElement.getElementsByTagName("supervisorSignedDate").item(0).getTextContent();
							log.info("supervisorSignedDate Value is: " + supervisorSignedDate);

							administratorPrintName = eElement.getElementsByTagName("administratorPrintName").item(0)
									.getTextContent();
							log.info("administratorPrintName Value is: " + administratorPrintName);

							administratorSign = eElement.getElementsByTagName("administratorSign").item(0).getTextContent();
							log.info("administratorSign Value is: " + administratorSign);
							
							administratorSignedDate = eElement.getElementsByTagName("administratorSignedDate").item(0).getTextContent();
							log.info("administratorSignedDate Value is: " + administratorSignedDate);

							hrComments = eElement.getElementsByTagName("hrComments").item(0)
									.getTextContent();
							log.info("hrComments Value is: " + hrComments);

							feeWaiverGranted = eElement.getElementsByTagName("feeWaiverGranted").item(0).getTextContent();
							log.info("feeWaiverGranted Value is: " + feeWaiverGranted);

							feeWaiverDenied = eElement.getElementsByTagName("feeWaiverDenied").item(0)
									.getTextContent();
							log.info("feeWaiverDenied Value is: " + feeWaiverDenied);

							term = eElement.getElementsByTagName("term").item(0).getTextContent();
							log.info("term Value is: " + term);
							
							EDDE = eElement.getElementsByTagName("EDDE").item(0).getTextContent();
							log.info("EDDE Value is: " + EDDE);

							hrSign = eElement.getElementsByTagName("hrSign").item(0)
									.getTextContent();
							log.info("hrSign Value is: " + hrSign);

							hrSigneddate = eElement.getElementsByTagName("hrSigneddate").item(0).getTextContent();
							log.info("hrSigneddate Value is: " + hrSigneddate);

						}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("EMPL_ID", empId);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("DEPARTMENT_NAME", departmentName);
					dataMap.put("EXTENSION", extension);
					dataMap.put("BARGAINING_UNIT", bargainingUnit);
					dataMap.put("JOB_CODE", jobCode);
					dataMap.put("JOB_TYPE", jobType);					
					dataMap.put("PROBATION_STATUS", probationStatus);					
					dataMap.put("TEMPORARY", temporary);
					
					Object enddateObj = null;
					if (enddate != null && enddate != "") {
						Date enddateNew = Date.valueOf(enddate);
						enddateObj = enddateNew;
					}
					dataMap.put("END_DATE", enddateObj);
					
					dataMap.put("ON_LEAVE_CHECK", onLeaveCheck);
					dataMap.put("SEMESTER", semester);
					dataMap.put("YEAR", year);
					dataMap.put("CLASS_STANDING", classStanding);
					dataMap.put("STUDENT_TYPE", studentType);
					dataMap.put("CAMPUS_ATTENDING", campus_Attending);					
					dataMap.put("STUDENT_ID", studentId);
					dataMap.put("APPROVED_PLAN", approvedPlan);
					dataMap.put("MAJOR", major);
					dataMap.put("COURSE_INFO_COMMENT", courseInfoComment);
					dataMap.put("COURSE_TITLE1", courseTitle1);
					dataMap.put("WR_CD1", wr_Cd1);
					dataMap.put("ONLINE_COURSE1", onlineCourse1);
					dataMap.put("UNIT1", unit1);
					dataMap.put("DAY1", day1);
					dataMap.put("HOURS1", hours1);
					dataMap.put("RELEASE_TIME1", releaseTime1);		
					dataMap.put("COURSE_TITLE2", courseTitle2);
					dataMap.put("WR_CD2", wr_Cd2);					
					dataMap.put("ONLINE_COURSE2", onlineCourse2);
					dataMap.put("UNIT2", unit2);					
					dataMap.put("DAY2", day2);
					dataMap.put("HOURS2", hours2);
					dataMap.put("RELEASE_TIME2", releaseTime2);
					dataMap.put("COMMENT1", comment1);
					dataMap.put("GRADPROFBUS_STATEMENT", gradProfBusStatement);
					dataMap.put("AUTHORIZATION_INITIALS", authorizationInitials);
					dataMap.put("EMP_SIGN", empSign);
					
					Object dateInitiatedObj = null;
					if (dateInitiated != null && dateInitiated != "") {
						Date dateInitiatedNew = Date.valueOf(dateInitiated);
						dateInitiatedObj = dateInitiatedNew;
					}
					dataMap.put("DATE_NITIATED", dateInitiatedObj);				
					dataMap.put("DEPT_CHAIR_PRINT_NAME", deptChairPrintName);
					dataMap.put("DEPT_CHAIR_SIGN", deptChairSign);	
					
					Object deptchairSignedDateObj = null;
					if (deptchairSignedDate != null && deptchairSignedDate != "") {
						Date deptchairSignedDateNew = Date.valueOf(deptchairSignedDate);
						deptchairSignedDateObj = deptchairSignedDateNew;
					}
					dataMap.put("DEPT_CHAIR_SIGNED_DATE", deptchairSignedDateObj);
					
					dataMap.put("COLLEGE_DEAN_PRINT_NAME", collegeDeanPrintName);					
					dataMap.put("COLLEGE_DEAN_SIGN", collegeDeanSign);
					
					Object collegeDeanSignedDateObj = null;
					if (collegeDeanSignedDate != null && collegeDeanSignedDate != "") {
						Date collegeDeanSignedDateNew = Date.valueOf(collegeDeanSignedDate);
						collegeDeanSignedDateObj = collegeDeanSignedDateNew;
					}
					dataMap.put("COLLEGE_DEAN_SIGNE_DATE", collegeDeanSignedDateObj);					
					dataMap.put("PROVOST_PRINT_NAME", provostPrintName);
					dataMap.put("PROVOST_SIGN", provostSign);
					
					Object provostDateObj = null;
					if (provostDate != null && provostDate != "") {
						Date provostDateNew = Date.valueOf(provostDate);
						provostDateObj = provostDateNew;
					}
					dataMap.put("PROVOST_DATE", provostDateObj);					
					dataMap.put("RELEASE_TIME_CHECK", releaseTimeCheck);
					dataMap.put("WORK_SCHEDULE_CHANGE_CHECK", WorkScheduleChangeCheck);
					dataMap.put("DAYS_AND_TIMES", daysAndTimes);					
					dataMap.put("SUPERVISOR_PRINT_NAME", supervisorPrintname);
					dataMap.put("SUPERVISOR_SIGN", supervisorSign);
					
					Object supervisorSignedDateObj = null;
					if (supervisorSignedDate != null && supervisorSignedDate != "") {
						Date supervisorSignedDateNew = Date.valueOf(supervisorSignedDate);
						supervisorSignedDateObj = supervisorSignedDateNew;
					}
					dataMap.put("SUPERVISOR_SIGNED_DATE", supervisorSignedDateObj);					
					dataMap.put("ADMINISTRATOR_PRINT_NAME", administratorPrintName);
					dataMap.put("ADMINISTRATOR_SIGN", administratorSign);
					
					Object administratorSignedDateObj = null;
					if (administratorSignedDate != null && administratorSignedDate != "") {
						Date administratorSignedDateNew = Date.valueOf(administratorSignedDate);
						administratorSignedDateObj = administratorSignedDateNew;
					}
					dataMap.put("ADMINISTRATOR_SIGNED_DATE", administratorSignedDateObj);
					
					dataMap.put("HR_COMMENTS", hrComments);
					dataMap.put("FEE_WAIVER_GRANTED", feeWaiverGranted);
					dataMap.put("FEE_WAIVER_DENIED", feeWaiverDenied);
					dataMap.put("TERM", term);					
					dataMap.put("EDDE", EDDE);
					dataMap.put("HR_SIGN", hrSign);
					
					Object hrSigneddateObj = null;
					if (hrSigneddate != null && hrSigneddate != "") {
						Date hrSigneddateNew = Date.valueOf(hrSigneddate);
						hrSigneddateObj = hrSigneddateNew;
					}
					dataMap.put("HR_SIGNED_DATE", hrSigneddateObj);
					
					
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
			insertEmployeeFeeWaiverData(conn, dataMap);
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

	public void insertEmployeeFeeWaiverData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_EMPLOYEE_FEE_WAIVER";
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
