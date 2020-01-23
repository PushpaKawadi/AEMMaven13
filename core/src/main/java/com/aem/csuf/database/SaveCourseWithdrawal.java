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

//import com.adobe.aemfd.docmanager.Document;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Course1",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=Save Student Course DB" })
public class SaveCourseWithdrawal implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(SaveCourseWithdrawal.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;
		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		LinkedHashMap<String, Object> dataMap = null;
		Document doc = null;
		InputStream is = null;
		String firstName = null;
		String lastName = null;
		String major = null;
		String termCode = null;
		String typeOfForm = null;
		String middleName = null;
		String sID = null;
		String expGradDate = null;
		String degreeObjective = null;
		String prgPlan = null;
		String academicPlan = null;
		String telNo = null;
		String emailAddress = null;
		String internationalStudent = null;
		String eipFlag = null;
		Object expGradObj = null;
		String termDesc = null;
		String allCourseWithdrawal = null;
		String course1 = null;
		String schedule1 = null;
		String unit1 = null;
		String instName1 = null;
		String instUID1 = null;
		String instEmail1 = null;
		String chairName1 = null;
		String chairEmail1 = null;
		String chairUID1 = null;
		String course2 = null;
		String schedule2 = null;
		String unit2 = null;
		String instName2 = null;
		String instUID2 = null;
		String instEmail2 = null;
		String chairName2 = null;
		String chairEmail2 = null;
		String chairUID2 = null;
		String course3 = null;
		String schedule3 = null;
		String unit3 = null;
		String instName3 = null;
		String instUID3 = null;
		String instEmail3 = null;
		String chairName3 = null;
		String chairEmail3 = null;
		String chairUID3 = null;
		String course4 = null;
		String schedule4 = null;
		String unit4 = null;
		String instName4 = null;
		String instUID4 = null;
		String instEmail4 = null;
		String chairName4 = null;
		String chairEmail4 = null;
		String chairUID4 = null;
		String course5 = null;
		String schedule5 = null;
		String unit5 = null;
		String instName5 = null;
		String instUID5 = null;
		String instEmail5 = null;
		String chairName5 = null;
		String chairEmail5 = null;
		String chairUID5 = null;

		String course6 = null;
		String schedule6 = null;
		String unit6 = null;
		String instName6 = null;
		String instUID6 = null;
		String instEmail6 = null;
		String chairName6 = null;
		String chairEmail6 = null;
		String chairUID6 = null;

		String course7 = null;
		String schedule7 = null;
		String unit7 = null;
		String instName7 = null;
		String instUID7 = null;
		String instEmail7 = null;
		String chairName7 = null;
		String chairEmail7 = null;
		String chairUID7 = null;

		String course8 = null;
		String schedule8 = null;
		String unit8 = null;
		String instName8 = null;
		String instUID8 = null;
		String instEmail8 = null;
		String chairName8 = null;
		String chairEmail8 = null;
		String chairUID8 = null;

		String course9 = null;
		String schedule9 = null;
		String unit9 = null;
		String instName9 = null;
		String instUID9 = null;
		String instEmail9 = null;
		String chairName9 = null;
		String chairEmail9 = null;
		String chairUID9 = null;

		String course10 = null;
		String schedule10 = null;
		String unit10 = null;
		String instName10 = null;
		String instUID10 = null;
		String instEmail10 = null;
		String chairName10 = null;
		String chairEmail10 = null;
		String chairUID10 = null;
		String course11 = null;
		String schedule11 = null;
		String unit11 = null;
		String instName11 = null;
		String instUID11 = null;
		String instEmail11 = null;
		String chairName11 = null;
		String chairEmail11 = null;
		String chairUID11 = null;
		String course12 = null;
		String schedule12 = null;
		String unit12 = null;
		String instName12 = null;
		String instUID12 = null;
		String instEmail12 = null;
		String chairName12 = null;
		String chairEmail12 = null;
		String chairUID12 = null;
		String nonMedicalPetition1 = null;
		String nonMedicalPetition2 = null;
		String nonMedicalPetition3 = null;
		String medicalPetition1 = null;
		String medicalPetition2 = null;
		String medicalPetition3 = null;
		String medicalPetition4 = null;
		String medicalPetition5 = null;
		String medicalPetition6 = null;
		String nonMedicalStudentCB = null;
		String medicalStudentCB = null;
		String studentSign = null;
		String studentSignDate = null;
		Object studDateObj = null;
		String caseID  = null;
		

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
					org.w3c.dom.NodeList nList1 = doc
							.getElementsByTagName("afUnboundData");
					for (int temp = 0; temp < nList1.getLength(); temp++) {
						org.w3c.dom.Node nNode1 = nList1.item(temp);
						if (nNode1.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element eElement1 = (org.w3c.dom.Element) nNode1;
							 caseID = eElement1
									.getElementsByTagName("caseId").item(0)
									.getTextContent();
							
						}
					}
					
					org.w3c.dom.NodeList nList = doc
							.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);
						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							typeOfForm = eElement
									.getElementsByTagName("typeOfForm").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							middleName = eElement
									.getElementsByTagName("MiddleName").item(0)
									.getTextContent();
							sID = eElement.getElementsByTagName("StudentID")
									.item(0).getTextContent();
							major = eElement.getElementsByTagName("Major")
									.item(0).getTextContent();
							expGradDate = eElement
									.getElementsByTagName("ExpectedGraduDate")
									.item(0).getTextContent();

							if (expGradDate != null && expGradDate != "") {
								Date expGrad = Date.valueOf(expGradDate);
								expGradObj = expGrad;
							}
							degreeObjective = eElement
									.getElementsByTagName("DegreeObjective")
									.item(0).getTextContent();
							prgPlan = eElement
									.getElementsByTagName("ProgramPlan")
									.item(0).getTextContent();
							academicPlan = eElement
									.getElementsByTagName("AcademicPlan")
									.item(0).getTextContent();
							telNo = eElement
									.getElementsByTagName("TelephoneNo")
									.item(0).getTextContent();
							emailAddress = eElement
									.getElementsByTagName("Email").item(0)
									.getTextContent();
							internationalStudent = eElement
									.getElementsByTagName(
											"International_Students").item(0)
									.getTextContent();
							eipFlag = eElement.getElementsByTagName("EIP_Flag")
									.item(0).getTextContent();
							termCode = eElement
									.getElementsByTagName("TermCode").item(0)
									.getTextContent();
							termDesc = eElement
									.getElementsByTagName("TermDesc").item(0)
									.getTextContent();
							allCourseWithdrawal = eElement
									.getElementsByTagName("AllCoursWithdrawRB")
									.item(0).getTextContent();
							course1 = eElement
									.getElementsByTagName("CourseNo1").item(0)
									.getTextContent();
							schedule1 = eElement
									.getElementsByTagName("ScheduleNo1")
									.item(0).getTextContent();
							unit1 = eElement.getElementsByTagName("UnitNo1")
									.item(0).getTextContent();
							instName1 = eElement
									.getElementsByTagName("InstructorName1")
									.item(0).getTextContent();
							instUID1 = eElement
									.getElementsByTagName("InstructorUserID1")
									.item(0).getTextContent();
							instEmail1 = eElement
									.getElementsByTagName("InstructorEmail1")
									.item(0).getTextContent();
							chairName1 = eElement
									.getElementsByTagName("ChairName1").item(0)
									.getTextContent();
							chairEmail1 = eElement
									.getElementsByTagName("ChairEmailID1")
									.item(0).getTextContent();
							chairUID1 = eElement
									.getElementsByTagName("ChairUserID1")
									.item(0).getTextContent();

							course2 = eElement
									.getElementsByTagName("CourseNo2").item(0)
									.getTextContent();
							schedule2 = eElement
									.getElementsByTagName("ScheduleNo2")
									.item(0).getTextContent();
							unit2 = eElement.getElementsByTagName("UnitNo2")
									.item(0).getTextContent();
							instName2 = eElement
									.getElementsByTagName("InstructorName2")
									.item(0).getTextContent();
							instUID2 = eElement
									.getElementsByTagName("InstructorUserID2")
									.item(0).getTextContent();
							instEmail2 = eElement
									.getElementsByTagName("InstructorEmail2")
									.item(0).getTextContent();
							chairName2 = eElement
									.getElementsByTagName("ChairName2").item(0)
									.getTextContent();
							chairEmail2 = eElement
									.getElementsByTagName("ChairEmailID2")
									.item(0).getTextContent();
							chairUID2 = eElement
									.getElementsByTagName("ChairUserID2")
									.item(0).getTextContent();

							course3 = eElement
									.getElementsByTagName("CourseNo3").item(0)
									.getTextContent();
							schedule3 = eElement
									.getElementsByTagName("ScheduleNo3")
									.item(0).getTextContent();
							unit3 = eElement.getElementsByTagName("UnitNo3")
									.item(0).getTextContent();
							instName3 = eElement
									.getElementsByTagName("InstructorName3")
									.item(0).getTextContent();
							instUID3 = eElement
									.getElementsByTagName("InstructorUserID3")
									.item(0).getTextContent();
							instEmail3 = eElement
									.getElementsByTagName("InstructorEmail3")
									.item(0).getTextContent();
							chairName3 = eElement
									.getElementsByTagName("ChairName3").item(0)
									.getTextContent();
							chairEmail3 = eElement
									.getElementsByTagName("ChairEmailID3")
									.item(0).getTextContent();
							chairUID3 = eElement
									.getElementsByTagName("ChairUserID3")
									.item(0).getTextContent();

							course4 = eElement
									.getElementsByTagName("CourseNo4").item(0)
									.getTextContent();
							schedule4 = eElement
									.getElementsByTagName("ScheduleNo4")
									.item(0).getTextContent();
							unit4 = eElement.getElementsByTagName("UnitNo4")
									.item(0).getTextContent();
							instName4 = eElement
									.getElementsByTagName("InstructorName4")
									.item(0).getTextContent();
							instUID4 = eElement
									.getElementsByTagName("InstructorUserID4")
									.item(0).getTextContent();
							instEmail4 = eElement
									.getElementsByTagName("InstructorEmail4")
									.item(0).getTextContent();
							chairName4 = eElement
									.getElementsByTagName("ChairName4").item(0)
									.getTextContent();
							chairEmail4 = eElement
									.getElementsByTagName("ChairEmailID4")
									.item(0).getTextContent();
							chairUID4 = eElement
									.getElementsByTagName("ChairUserID4")
									.item(0).getTextContent();
							course5 = eElement
									.getElementsByTagName("CourseNo5").item(0)
									.getTextContent();
							schedule5 = eElement
									.getElementsByTagName("ScheduleNo5")
									.item(0).getTextContent();
							unit5 = eElement.getElementsByTagName("UnitNo5")
									.item(0).getTextContent();
							instName5 = eElement
									.getElementsByTagName("InstructorName5")
									.item(0).getTextContent();
							instUID5 = eElement
									.getElementsByTagName("InstructorUserID5")
									.item(0).getTextContent();
							instEmail5 = eElement
									.getElementsByTagName("InstructorEmail5")
									.item(0).getTextContent();
							chairName5 = eElement
									.getElementsByTagName("ChairName5").item(0)
									.getTextContent();
							chairEmail5 = eElement
									.getElementsByTagName("ChairEmailID5")
									.item(0).getTextContent();
							chairUID5 = eElement
									.getElementsByTagName("ChairUserID5")
									.item(0).getTextContent();

							course6 = eElement
									.getElementsByTagName("CourseNo6").item(0)
									.getTextContent();
							schedule6 = eElement
									.getElementsByTagName("ScheduleNo6")
									.item(0).getTextContent();
							unit6 = eElement.getElementsByTagName("UnitNo6")
									.item(0).getTextContent();
							instName6 = eElement
									.getElementsByTagName("InstructorName6")
									.item(0).getTextContent();
							instUID6 = eElement
									.getElementsByTagName("InstructorUserID6")
									.item(0).getTextContent();
							instEmail6 = eElement
									.getElementsByTagName("InstructorEmail6")
									.item(0).getTextContent();
							chairName6 = eElement
									.getElementsByTagName("ChairName6").item(0)
									.getTextContent();
							chairEmail6 = eElement
									.getElementsByTagName("ChairEmailID6")
									.item(0).getTextContent();
							chairUID6 = eElement
									.getElementsByTagName("ChairUserID6")
									.item(0).getTextContent();

							course7 = eElement
									.getElementsByTagName("CourseNo7").item(0)
									.getTextContent();
							schedule7 = eElement
									.getElementsByTagName("ScheduleNo7")
									.item(0).getTextContent();
							unit7 = eElement.getElementsByTagName("UnitNo7")
									.item(0).getTextContent();
							instName7 = eElement
									.getElementsByTagName("InstructorName7")
									.item(0).getTextContent();
							instUID7 = eElement
									.getElementsByTagName("InstructorUserID7")
									.item(0).getTextContent();
							instEmail7 = eElement
									.getElementsByTagName("InstructorEmail7")
									.item(0).getTextContent();
							chairName7 = eElement
									.getElementsByTagName("ChairName7").item(0)
									.getTextContent();
							chairEmail7 = eElement
									.getElementsByTagName("ChairEmailID7")
									.item(0).getTextContent();
							chairUID7 = eElement
									.getElementsByTagName("ChairUserID7")
									.item(0).getTextContent();

							course8 = eElement
									.getElementsByTagName("CourseNo8").item(0)
									.getTextContent();
							schedule8 = eElement
									.getElementsByTagName("ScheduleNo8")
									.item(0).getTextContent();
							unit8 = eElement.getElementsByTagName("UnitNo8")
									.item(0).getTextContent();
							instName8 = eElement
									.getElementsByTagName("InstructorName8")
									.item(0).getTextContent();
							instUID8 = eElement
									.getElementsByTagName("InstructorUserID8")
									.item(0).getTextContent();
							instEmail8 = eElement
									.getElementsByTagName("InstructorEmail8")
									.item(0).getTextContent();
							chairName8 = eElement
									.getElementsByTagName("ChairName8").item(0)
									.getTextContent();
							chairEmail8 = eElement
									.getElementsByTagName("ChairEmailID8")
									.item(0).getTextContent();
							chairUID8 = eElement
									.getElementsByTagName("ChairUserID8")
									.item(0).getTextContent();

							course9 = eElement
									.getElementsByTagName("CourseNo9").item(0)
									.getTextContent();
							schedule9 = eElement
									.getElementsByTagName("ScheduleNo9")
									.item(0).getTextContent();
							unit9 = eElement.getElementsByTagName("UnitNo9")
									.item(0).getTextContent();
							instName9 = eElement
									.getElementsByTagName("InstructorName9")
									.item(0).getTextContent();
							instUID9 = eElement
									.getElementsByTagName("InstructorUserID9")
									.item(0).getTextContent();
							instEmail9 = eElement
									.getElementsByTagName("InstructorEmail9")
									.item(0).getTextContent();
							chairName9 = eElement
									.getElementsByTagName("ChairName9").item(0)
									.getTextContent();
							chairEmail9 = eElement
									.getElementsByTagName("ChairEmailID9")
									.item(0).getTextContent();
							chairUID9 = eElement
									.getElementsByTagName("ChairUserID9")
									.item(0).getTextContent();

							course10 = eElement
									.getElementsByTagName("CourseNo10").item(0)
									.getTextContent();
							schedule10 = eElement
									.getElementsByTagName("ScheduleNo10")
									.item(0).getTextContent();
							unit10 = eElement.getElementsByTagName("UnitNo10")
									.item(0).getTextContent();
							instName10 = eElement
									.getElementsByTagName("InstructorName10")
									.item(0).getTextContent();
							instUID10 = eElement
									.getElementsByTagName("InstructorUserID10")
									.item(0).getTextContent();
							instEmail10 = eElement
									.getElementsByTagName("InstructorEmail10")
									.item(0).getTextContent();
							chairName10 = eElement
									.getElementsByTagName("ChairName10")
									.item(0).getTextContent();
							chairEmail10 = eElement
									.getElementsByTagName("ChairEmailID10")
									.item(0).getTextContent();
							chairUID10 = eElement
									.getElementsByTagName("ChairUserID10")
									.item(0).getTextContent();

							course11 = eElement
									.getElementsByTagName("CourseNo11").item(0)
									.getTextContent();
							schedule11 = eElement
									.getElementsByTagName("ScheduleNo11")
									.item(0).getTextContent();
							unit11 = eElement.getElementsByTagName("UnitNo11")
									.item(0).getTextContent();
							instName11 = eElement
									.getElementsByTagName("InstructorName11")
									.item(0).getTextContent();
							instUID11 = eElement
									.getElementsByTagName("InstructorUserID11")
									.item(0).getTextContent();
							instEmail11 = eElement
									.getElementsByTagName("InstructorEmail11")
									.item(0).getTextContent();
							chairName11 = eElement
									.getElementsByTagName("ChairName11")
									.item(0).getTextContent();
							chairEmail11 = eElement
									.getElementsByTagName("ChairEmailID11")
									.item(0).getTextContent();
							chairUID11 = eElement
									.getElementsByTagName("ChairUserID11")
									.item(0).getTextContent();

							course12 = eElement
									.getElementsByTagName("CourseNo12").item(0)
									.getTextContent();
							schedule12 = eElement
									.getElementsByTagName("ScheduleNo12")
									.item(0).getTextContent();
							unit12 = eElement.getElementsByTagName("UnitNo12")
									.item(0).getTextContent();
							instName12 = eElement
									.getElementsByTagName("InstructorName12")
									.item(0).getTextContent();
							instUID12 = eElement
									.getElementsByTagName("InstructorUserID12")
									.item(0).getTextContent();
							instEmail12 = eElement
									.getElementsByTagName("InstructorEmail12")
									.item(0).getTextContent();
							chairName12 = eElement
									.getElementsByTagName("ChairName12")
									.item(0).getTextContent();
							chairEmail12 = eElement
									.getElementsByTagName("ChairEmailID12")
									.item(0).getTextContent();
							chairUID12 = eElement
									.getElementsByTagName("ChairUserID12")
									.item(0).getTextContent();

							nonMedicalPetition1 = eElement
									.getElementsByTagName(
											"StudentPetitionComment").item(0)
									.getTextContent();
							nonMedicalPetition2 = eElement
									.getElementsByTagName(
											"StudentPetitionComment1").item(0)
									.getTextContent();
							nonMedicalPetition3 = eElement
									.getElementsByTagName(
											"StudentPetitionComment2").item(0)
									.getTextContent();

							medicalPetition1 = eElement
									.getElementsByTagName(
											"StudentPetitionMedicalComment")
									.item(0).getTextContent();
							medicalPetition2 = eElement
									.getElementsByTagName(
											"StudentPetitionMedicalComment1")
									.item(0).getTextContent();
							medicalPetition3 = eElement
									.getElementsByTagName(
											"StudentPetitionMedicalComment2")
									.item(0).getTextContent();
							medicalPetition4 = eElement
									.getElementsByTagName(
											"StudentPetitionMedicalComment3")
									.item(0).getTextContent();
							medicalPetition5 = eElement
									.getElementsByTagName(
											"StudentPetitionMedicalComment4")
									.item(0).getTextContent();
							medicalPetition6 = eElement
									.getElementsByTagName(
											"StudentPetitionMedicalComment5")
									.item(0).getTextContent();

							nonMedicalStudentCB = eElement
									.getElementsByTagName("studentCB").item(0)
									.getTextContent();
							medicalStudentCB = eElement
									.getElementsByTagName("studentMedCB")
									.item(0).getTextContent();

							studentSign = eElement
									.getElementsByTagName("StudentSign")
									.item(0).getTextContent();
							studentSignDate = eElement
									.getElementsByTagName("StudentSignDate")
									.item(0).getTextContent();

						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("TYPE_OF_FORM", typeOfForm);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("MIDDLE_NAME", middleName);
					dataMap.put("STUDENT_ID", sID);
					dataMap.put("CASE_ID", caseID);
					dataMap.put("MAJOR", major);
					dataMap.put("EXP_GRAD_DATE", expGradObj);
					dataMap.put("DEGREE_OBJECTIVE", degreeObjective);
					dataMap.put("PROGRAM_PLAN", prgPlan);
					dataMap.put("ACADEMIC_PLAN", academicPlan);
					dataMap.put("PHONE_NO", telNo);
					dataMap.put("EMAIL_ADDRESS", emailAddress);
					dataMap.put("INTERNATIONAL_STUDENT", internationalStudent);
					dataMap.put("EIP_FLAG", eipFlag);
					dataMap.put("TERM_CODE", termCode);
					dataMap.put("TERM_DESC", termDesc);
					dataMap.put("ALL_COURSE_WITHDRAWAL", allCourseWithdrawal);
					dataMap.put("COURSE_NO1", course1);
					dataMap.put("SCHEDULE_NO1", schedule1);
					dataMap.put("UNIT_NO1", unit1);
					dataMap.put("INST_NAME1", instName1);
					dataMap.put("INST_UID1", instUID1);
					dataMap.put("INST_EMAIL1", instEmail1);
					dataMap.put("CHAIR_NAME1", chairName1);
					dataMap.put("CHAIR_EMAIL1", chairEmail1);
					dataMap.put("CHAIR_UID1", chairUID1);
					dataMap.put("COURSE_NO2", course2);
					dataMap.put("SCHEDULE_NO2", schedule2);
					dataMap.put("UNIT_NO2", unit2);
					dataMap.put("INST_NAME2", instName2);
					dataMap.put("INST_UID2", instUID2);
					dataMap.put("INST_EMAIL2", instEmail2);
					dataMap.put("CHAIR_NAME2", chairName2);
					dataMap.put("CHAIR_EMAIL2", chairEmail2);
					dataMap.put("CHAIR_UID2", chairUID2);
					dataMap.put("COURSE_NO3", course3);
					dataMap.put("SCHEDULE_NO3", schedule3);
					dataMap.put("UNIT_NO3", unit3);
					dataMap.put("INST_NAME3", instName3);
					dataMap.put("INST_UID3", instUID3);
					dataMap.put("INST_EMAIL3", instEmail3);
					dataMap.put("CHAIR_NAME3", chairName3);
					dataMap.put("CHAIR_EMAIL3", chairEmail3);
					dataMap.put("CHAIR_UID3", chairUID3);
					dataMap.put("COURSE_NO4", course4);
					dataMap.put("SCHEDULE_NO4", schedule4);
					dataMap.put("UNIT_NO4", unit4);
					dataMap.put("INST_NAME4", instName4);
					dataMap.put("INST_UID4", instUID4);
					dataMap.put("INST_EMAIL4", instEmail4);
					dataMap.put("CHAIR_NAME4", chairName4);
					dataMap.put("CHAIR_EMAIL4", chairEmail4);
					dataMap.put("CHAIR_UID4", chairUID4);
					dataMap.put("COURSE_NO5", course5);
					dataMap.put("SCHEDULE_NO5", schedule5);
					dataMap.put("UNIT_NO5", unit5);
					dataMap.put("INST_NAME5", instName5);
					dataMap.put("INST_UID5", instUID5);
					dataMap.put("INST_EMAIL5", instEmail5);
					dataMap.put("CHAIR_NAME5", chairName5);
					dataMap.put("CHAIR_EMAIL5", chairEmail5);
					dataMap.put("CHAIR_UID5", chairUID5);
					dataMap.put("COURSE_NO6", course6);
					dataMap.put("SCHEDULE_NO6", schedule6);
					dataMap.put("UNIT_NO6", unit6);
					dataMap.put("INST_NAME6", instName6);
					dataMap.put("INST_UID6", instUID6);
					dataMap.put("INST_EMAIL6", instEmail6);
					dataMap.put("CHAIR_NAME6", chairName6);
					dataMap.put("CHAIR_EMAIL6", chairEmail6);
					dataMap.put("CHAIR_UID6", chairUID6);
					dataMap.put("COURSE_NO7", course7);
					dataMap.put("SCHEDULE_NO7", schedule7);
					dataMap.put("UNIT_NO7", unit7);
					dataMap.put("INST_NAME7", instName7);
					dataMap.put("INST_UID7", instUID7);
					dataMap.put("INST_EMAIL7", instEmail7);
					dataMap.put("CHAIR_NAME7", chairName7);
					dataMap.put("CHAIR_EMAIL7", chairEmail7);
					dataMap.put("CHAIR_UID7", chairUID7);
					dataMap.put("COURSE_NO8", course8);
					dataMap.put("SCHEDULE_NO8", schedule8);
					dataMap.put("UNIT_NO8", unit8);
					dataMap.put("INST_NAME8", instName8);
					dataMap.put("INST_UID8", instUID8);
					dataMap.put("INST_EMAIL8", instEmail8);
					dataMap.put("CHAIR_NAME8", chairName8);
					dataMap.put("CHAIR_EMAIL8", chairEmail8);
					dataMap.put("CHAIR_UID8", chairUID8);

					dataMap.put("COURSE_NO9", course9);
					dataMap.put("SCHEDULE_NO9", schedule9);
					dataMap.put("UNIT_NO9", unit9);
					dataMap.put("INST_NAME9", instName9);
					dataMap.put("INST_UID9", instUID9);
					dataMap.put("INST_EMAIL9", instEmail9);
					dataMap.put("CHAIR_NAME9", chairName9);
					dataMap.put("CHAIR_EMAIL9", chairEmail9);
					dataMap.put("CHAIR_UID9", chairUID9);

					dataMap.put("COURSE_NO10", course10);
					dataMap.put("SCHEDULE_NO10", schedule10);
					dataMap.put("UNIT_NO10", unit10);
					dataMap.put("INST_NAME10", instName10);
					dataMap.put("INST_UID10", instUID10);
					dataMap.put("INST_EMAIL10", instEmail10);
					dataMap.put("CHAIR_NAME10", chairName10);
					dataMap.put("CHAIR_EMAIL10", chairEmail10);
					dataMap.put("CHAIR_UID10", chairUID10);

					dataMap.put("COURSE_NO11", course11);
					dataMap.put("SCHEDULE_NO11", schedule11);
					dataMap.put("UNIT_NO11", unit11);
					dataMap.put("INST_NAME11", instName11);
					dataMap.put("INST_UID11", instUID11);
					dataMap.put("INST_EMAIL11", instEmail11);
					dataMap.put("CHAIR_NAME11", chairName11);
					dataMap.put("CHAIR_EMAIL11", chairEmail11);
					dataMap.put("CHAIR_UID11", chairUID11);

					dataMap.put("COURSE_NO12", course12);
					dataMap.put("SCHEDULE_NO12", schedule12);
					dataMap.put("UNIT_NO12", unit12);
					dataMap.put("INST_NAME12", instName12);
					dataMap.put("INST_UID12", instUID12);
					dataMap.put("INST_EMAIL12", instEmail12);
					dataMap.put("CHAIR_NAME12", chairName12);
					dataMap.put("CHAIR_EMAIL12", chairEmail12);
					dataMap.put("CHAIR_UID12", chairUID12);

					dataMap.put("NON_MEDICAL_PETITION1", nonMedicalPetition1);
					dataMap.put("NON_MEDICAL_PETITION2", nonMedicalPetition2);
					dataMap.put("NON_MEDICAL_PETITION3", nonMedicalPetition3);
					dataMap.put("MEDICAL_PETITION1", medicalPetition1);
					dataMap.put("MEDICAL_PETITION2", medicalPetition2);
					dataMap.put("MEDICAL_PETITION3", medicalPetition3);
					dataMap.put("MEDICAL_PETITION4", medicalPetition4);
					dataMap.put("MEDICAL_PETITION5", medicalPetition5);
					dataMap.put("MEDICAL_PETITION6", medicalPetition6);
					dataMap.put("NON_MEDICAL_STUDENT", nonMedicalStudentCB);
					dataMap.put("MEDICAL_STUDENT", medicalStudentCB);
					dataMap.put("STUDENT_SIGN", studentSign);
					
					
					if (studentSignDate != null && studentSignDate != "") {
						Date stuDate = Date.valueOf(studentSignDate);
						studDateObj = stuDate;
					}
					dataMap.put("STUDENT_SIGN_DATE", studDateObj);
					
				} catch (SAXException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				conn = getConnection();
				if (conn != null) {
					log.error("Connection Successfull");
					insertStudentData(conn, dataMap);
				}
			}

		}

	}

	public void insertStudentData(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_STUDENT_COURSE_WITHDRAWAL";
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
}
