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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Grade Change DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFGradeChangeDB" })
public class GradeChangeDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(GradeChangeDB.class);

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
		String workflowInstanceID = "";
		String term = "";
		String instCwid = "";
		String instName = "";
		String courseName = "";
		String classNumber = "";
		String sectionNumber = "";
		String unitValue = "";
		String courseLevel = "";
		String todayDate = "";
		String massGradeChange = "";
		String instSign = "";
		String instSignDate = "";
		String instComments = "";
		String chairSign = "";
		String chairSignDate = "";
		String chairComments = "";
		String deanSign = "";
		String deanSignDate = "";
		String deanComments = "";
		String recordsSign = "";
		String recordsSignDate = "";
		String recordsComments = "";
		String enrollmentRequestID = "";
		String recordersName = "";
		String cmsUpdateCompleted = "";
		String studentLastName = "";
		String studentFirstName = "";
		String studentID = "";
		String studentMiddleName = "";
		String gradeChangeFrom = "";
		String gradeChangeTo = "";
		String gradeChangeReasons = "";
		String comments = "";
		String rpWorkCompleted = "";

		LinkedHashMap<String, Object> dataMapFormInfo = null;
		LinkedHashMap<String, Object> dataMapStudentInfo = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
			while (xmlFiles.hasNext()) {
				workflowInstanceID = workItem.getWorkflow().getId();
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
						log.error("Exception1 Pushpa=" + e2.getMessage());
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

								term = eElement.getElementsByTagName("Term")
										.item(0).getTextContent();
								
								log.error("term="+term);
								
								instCwid = eElement
										.getElementsByTagName("InstructorCWID")
										.item(0).getTextContent();
								
								log.error("instCwid="+instCwid);
								
								instName = eElement
										.getElementsByTagName(
												"InstructorName").item(0)
										.getTextContent();
								
								courseName = eElement
										.getElementsByTagName("CourseName")
										.item(0).getTextContent();
								
								classNumber = eElement
										.getElementsByTagName("ClassNumber")
										.item(0).getTextContent();
								courseLevel = eElement
										.getElementsByTagName("CourseLevel")
										.item(0).getTextContent();
								
								sectionNumber = eElement
										.getElementsByTagName("SectionNumber")
										.item(0).getTextContent();
								
								todayDate = eElement
										.getElementsByTagName("TodayDate")
										.item(0).getTextContent();

								instSign = eElement
										.getElementsByTagName("InstructorSign")
										.item(0).getTextContent();
								instSignDate = eElement
										.getElementsByTagName("InstructorDate")
										.item(0).getTextContent();
								instComments = eElement
										.getElementsByTagName(
												"InstructorComment").item(0)
										.getTextContent();

								chairSign = eElement
										.getElementsByTagName("ChairSign")
										.item(0).getTextContent();
								chairSignDate = eElement
										.getElementsByTagName("ChairDate")
										.item(0).getTextContent();
								chairComments = eElement
										.getElementsByTagName("ChairComment")
										.item(0).getTextContent();

								deanSign = eElement
										.getElementsByTagName("DeanSign")
										.item(0).getTextContent();
								deanSignDate = eElement
										.getElementsByTagName("DeanDate")
										.item(0).getTextContent();
								deanComments = eElement
										.getElementsByTagName("DeanComment")
										.item(0).getTextContent();

								enrollmentRequestID = eElement
										.getElementsByTagName("EnrollmentReqID")
										.item(0).getTextContent();
								cmsUpdateCompleted = eElement
										.getElementsByTagName("CMSUpdate")
										.item(0).getTextContent();
								recordersName = eElement
										.getElementsByTagName("RecordersName")
										.item(0).getTextContent();
								cmsUpdateCompleted = eElement
										.getElementsByTagName("CMSUpdate")
										.item(0).getTextContent();
								recordsComments = eElement
										.getElementsByTagName(
												"RecordersComments").item(0)
										.getTextContent();
								
								log.error("Here=============================");

								dataMapFormInfo = new LinkedHashMap<String, Object>();
								dataMapFormInfo.put("TERM", term);
								dataMapFormInfo
										.put("INSTRUCTOR_CWID", instCwid);
								dataMapFormInfo
										.put("INSTRUCTOR_NAME", instName);
								dataMapFormInfo.put("COURSE_NAME", courseName);
								dataMapFormInfo
										.put("CLASS_NUMBER", classNumber);
								dataMapFormInfo.put("SECTION_NUMBER",
										sectionNumber);
								dataMapFormInfo
										.put("COURSE_LEVEL", courseLevel);
								
								Object todayDtObj = null;
								if (todayDate != null && todayDate != "") {
									Date todayDateNew = Date.valueOf(todayDate);
									todayDtObj = todayDateNew;
								}
								
								dataMapFormInfo.put("TODAYS_DATE", todayDtObj);
								dataMapFormInfo.put("MASS_GRADE_CHANGE",
										massGradeChange);
								dataMapFormInfo.put("INSTRUCTOR_SIGNATURE",
										instSign);
								
								Object instDateObj = null;
								if (instSignDate != null && instSignDate != "") {
									Date instSignNew = Date.valueOf(instSignDate);
									instDateObj = instSignNew;
								}
								dataMapFormInfo.put("INSTRUCTOR_SIGN_DATE",
										instDateObj);
								dataMapFormInfo.put("INSTRUCTOR_COMMENT",
										instComments);
								
								Object chairDateObj = null;
								if (chairSignDate != null && chairSignDate != "") {
									Date chairSignNew = Date.valueOf(chairSignDate);
									chairDateObj = chairSignNew;
								}
								
								
								dataMapFormInfo.put("CHAIR_SIGNATURE",
										chairSign);
								dataMapFormInfo.put("CHAIR_SIGN_DATE",
										chairDateObj);
								dataMapFormInfo.put("CHAIR_COMMENT",
										chairComments);
								dataMapFormInfo.put("DEAN_SIGNATURE", deanSign);
								
								Object deanDateObj = null;
								if (deanSignDate != null && deanSignDate != "") {
									Date deanSignNew = Date.valueOf(deanSignDate);
									deanDateObj = deanSignNew;
								}
								
								dataMapFormInfo.put("DEAN_SIGN_DATE",
										deanDateObj);
								dataMapFormInfo.put("DEAN_COMMENT",
										deanComments);
								dataMapFormInfo.put("RECORDS_SIGNATURE",
										recordsSign);
								
								Object recordDateObj = null;
								if (recordsSignDate != null && recordsSignDate != "") {
									Date recordSignNew = Date.valueOf(recordsSignDate);
									recordDateObj = recordSignNew;
								}
								
								dataMapFormInfo.put("RECORDS_SIGN_DATE",
										recordDateObj);
								dataMapFormInfo.put("RECORDS_COMMENT",
										recordsComments);

								dataMapFormInfo.put("ENROLLMENT_REQUEST_ID",
										enrollmentRequestID);
								dataMapFormInfo.put("RECORDERS_NAME",
										recordersName);
								dataMapFormInfo.put("CMS_UPDATE_COMPLETED",
										cmsUpdateCompleted);
								dataMapFormInfo.put("WORKFLOW_INSTANCE_ID",
										workflowInstanceID);

								for (int i = 0; i < eElement
										.getElementsByTagName("Row1")
										.getLength(); i++) {
									for (int j = 0; j < eElement
											.getElementsByTagName("Row1")
											.item(i).getChildNodes()
											.getLength() - 1; j++) {

										studentLastName = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(0).getTextContent();

										studentFirstName = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(1).getTextContent();

										studentID = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(2).getTextContent();

										unitValue = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(3).getTextContent();

										gradeChangeFrom = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(4).getTextContent();

										gradeChangeTo = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(5).getTextContent();

										gradeChangeReasons = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(6).getTextContent();

										comments = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(7).getTextContent();

										rpWorkCompleted = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(8).getTextContent();

										break;

									}

									dataMapStudentInfo = new LinkedHashMap<String, Object>();

									dataMapStudentInfo.put("TERM", term);
									dataMapStudentInfo.put("INSTRUCTOR_CWID",
											instCwid);
									dataMapStudentInfo.put("CLASS_NUMBER",
											classNumber);
									dataMapStudentInfo.put("SECTION_NUMBER",
											sectionNumber);
									dataMapStudentInfo.put("TODAYS_DATE",
											todayDate);
									dataMapStudentInfo.put("STUDENT_CWID",
											studentID);
									dataMapStudentInfo.put("MIDDLE_NAME",
											studentMiddleName);
									dataMapStudentInfo.put("FIRST_NAME",
											studentFirstName);
									dataMapStudentInfo.put("LAST_NAME",
											studentLastName);
									dataMapStudentInfo.put("GRADE_CHANGE_FROM",
											gradeChangeFrom);
									dataMapStudentInfo.put("GRADE_CHANGE_TO",
											gradeChangeTo);
									dataMapStudentInfo.put(
											"GRADE_CHANGE_REASON",
											gradeChangeReasons);
									dataMapStudentInfo
											.put("COMMENTS", comments);
									dataMapStudentInfo.put("RP_WORK_COMPLETED",
											rpWorkCompleted);
									dataMapStudentInfo.put(
											"WORKFLOW_INSTANCE_ID",
											workflowInstanceID);

									//insertGCFormData(conn, dataMapFormInfo);

									insertGCStudentData(conn,
											dataMapStudentInfo);
								}
							}
						}

					} catch (SAXException e) {
						log.error("SAXException=" + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						//log.error("Exception1");
						log.error("Exception=" + e.getMessage());
						e.printStackTrace();
					} 
					
					/*finally {
						try {
							is.close();
							conn.close();
						} catch (IOException e) {
							log.error("IOException=" + e.getMessage());
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}*/

				}
			}

			// log.error("Connection Successfull");
			
			insertGCFormData(conn, dataMapFormInfo);
			// insertCataLeaveDonationData(conn,emplId, dateAbsent,hoursAbsent);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */

	public void insertGCStudentData(Connection conn,
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
			String tableName = "AEM_AR_GRADE_CHANGE_STUDENT_INFO";
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
				log.error("Before Prepared stmt GC Student Info");
				preparedStmt.execute();
				conn.commit();
				log.error("After Prepared stmt GC Student Info");
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

	public void insertGCFormData(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_AR_GRADE_CHANGE_FORM";
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
				log.error("Before GC");
				preparedStmt.execute();
				conn.commit();
				log.error("After GC");
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
