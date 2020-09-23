package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
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
		Constants.SERVICE_DESCRIPTION + "=Personnel Notice DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=PersonnelActionNoticeDB" })
public class CSUFPersonnelActionNoticeDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CSUFPersonnelActionNoticeDB.class);

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
		String emplID = "";
		String firstName = "";
		String lastName = "";
		String middleInitial = "";
		String newCMSNo = "";
		String empRCD = "";
		String currentCMSNo = "";
		String classificationChange = "";
		String demotion = "";
		String equlityIncrease = "";
		String extensionofAppointment = "";
		String inRangeProgression = "";
		String reassignment = "";
		String returnReassignment = "";
		String salaryStipend = "";
		String salaryStipendValue = "";
		String serviceSalaryIncrease = "";
		String approved = "";
		String denied = "";
		String others = "";
		String othersComments = "";
		String effectiveDateAction = "";
		String endingDate = "";
		String newEndingDate = "";
		String currentMPP = "";
		String currentPerm = "";
		String currentProb = "";
		String currentTemp = "";
		String currentAgency = "";
		String currentReptUnit = "";
		String currentClassCode = "";
		String currentSerialNo = "";
		String currentFSLAStatus = "";
		String currentCBID = "";
		String currentDept = "";
		String currentDeptID = "";
		String currentMPPSupName = "";
		String currentDivision = "";
		String currentCollege = "";
		String currentClassificationTitle = "";
		String currentWorkingTitle = "";
		String currentFTMonthlySalary = "";
		String currentActualSalary = "";
		String currentFrequency = "";
		String currentTimeBase = "";
		String currentRangeCode = "";
		String currentAnniversaryDate = "";
		String currentProbEndDate = "";
		String currentStep = "";
		String currentMppJobCode = "";

		String newMPP = "";
		String newPerm = "";
		String newProb = "";
		String newTemp = "";
		String newAgency = "";
		String newReptUnit = "";
		String newClassCode = "";
		String newSerialNo = "";
		String newFSLAStatus = "";
		String newDept = "";
		String newDeptID = "";
		String newMPPSupName = "";
		String newCBID = "";
		String newCollege = "";
		String newClassificationTitle = "";
		String newWorkingTitle = "";
		String newDivision = "";
		String newFTMonthlySalary = "";
		String newActualSalary = "";
		String newFrequency = "";
		String newTimeBase = "";
		String newAnniversaryDate = "";
		String newProbEndDate = "";
		String newStep = "";
		String newRangeCode = "";
		String newMppJobCode = "";
		String hrName = "";
		String hrDate = "";
		String hrComments = "";
		String payrollComments = "";
		String payrollName = "";
		String payrollDate = "";

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
							emplID = eElement.getElementsByTagName("EmplID")
									.item(0).getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							middleInitial = eElement
									.getElementsByTagName("MiddleInitial")
									.item(0).getTextContent();
							newCMSNo = eElement
									.getElementsByTagName("NewCMSNo").item(0)
									.getTextContent();
							empRCD = eElement.getElementsByTagName("EmpRCD")
									.item(0).getTextContent();
							currentCMSNo = eElement
									.getElementsByTagName("CurrentCMSNo")
									.item(0).getTextContent();
							classificationChange = eElement
									.getElementsByTagName(
											"ClassificationChange").item(0)
									.getTextContent();
							demotion = eElement
									.getElementsByTagName("Demotion").item(0)
									.getTextContent();
							equlityIncrease = eElement
									.getElementsByTagName("EqulityIncrease")
									.item(0).getTextContent();
							extensionofAppointment = eElement
									.getElementsByTagName(
											"ExtensionofAppointment").item(0)
									.getTextContent();
							inRangeProgression = eElement
									.getElementsByTagName("InRangeProgression")
									.item(0).getTextContent();
							reassignment = eElement
									.getElementsByTagName("Reassignment")
									.item(0).getTextContent();
							returnReassignment = eElement
									.getElementsByTagName("ReturnReassignment")
									.item(0).getTextContent();
							salaryStipend = eElement
									.getElementsByTagName("SalaryStipend")
									.item(0).getTextContent();
							salaryStipendValue = eElement
									.getElementsByTagName("SalaryStipendValue")
									.item(0).getTextContent();
							serviceSalaryIncrease = eElement
									.getElementsByTagName(
											"ServiceSalaryIncrease").item(0)
									.getTextContent();
							approved = eElement
									.getElementsByTagName("Approved").item(0)
									.getTextContent();
							denied = eElement.getElementsByTagName("Denied")
									.item(0).getTextContent();
							others = eElement.getElementsByTagName("Others")
									.item(0).getTextContent();
							othersComments = eElement
									.getElementsByTagName("OthersComments")
									.item(0).getTextContent();
							effectiveDateAction = eElement
									.getElementsByTagName("EffectiveDateAction")
									.item(0).getTextContent();
							endingDate = eElement
									.getElementsByTagName("EndingDate").item(0)
									.getTextContent();
							newEndingDate = eElement
									.getElementsByTagName("NewEndingDate")
									.item(0).getTextContent();

							// Current Assignment
							currentMPP = eElement
									.getElementsByTagName("CurrentMPP").item(0)
									.getTextContent();
							currentPerm = eElement
									.getElementsByTagName("CurrentPerm")
									.item(0).getTextContent();
							currentProb = eElement
									.getElementsByTagName("CurrentProb")
									.item(0).getTextContent();
							currentTemp = eElement
									.getElementsByTagName("CurrentTemp")
									.item(0).getTextContent();
							currentAgency = eElement
									.getElementsByTagName("CurrentAgency")
									.item(0).getTextContent();
							currentReptUnit = eElement
									.getElementsByTagName("CurrentReptUnit")
									.item(0).getTextContent();
							currentClassCode = eElement
									.getElementsByTagName("CurrentClassCode")
									.item(0).getTextContent();
							currentSerialNo = eElement
									.getElementsByTagName("CurrentSerialNo")
									.item(0).getTextContent();
							currentFSLAStatus = eElement
									.getElementsByTagName("CurrentFSLAStatus")
									.item(0).getTextContent();
							currentCBID = eElement
									.getElementsByTagName("CurrentCBID")
									.item(0).getTextContent();
							currentDept = eElement
									.getElementsByTagName("CurrentDept")
									.item(0).getTextContent();
							currentDeptID = eElement
									.getElementsByTagName("CurrentDeptID")
									.item(0).getTextContent();
							currentMPPSupName = eElement
									.getElementsByTagName("CurrentMPPSupName")
									.item(0).getTextContent();
							currentDivision = eElement
									.getElementsByTagName("CurrentDivision")
									.item(0).getTextContent();
							currentCollege = eElement
									.getElementsByTagName("CurrentCollege")
									.item(0).getTextContent();
							currentClassificationTitle = eElement
									.getElementsByTagName(
											"CurrentClassificationTitle")
									.item(0).getTextContent();
							currentWorkingTitle = eElement
									.getElementsByTagName("CurrentWorkingTitle")
									.item(0).getTextContent();
							currentFTMonthlySalary = eElement
									.getElementsByTagName(
											"CurrentFTMonthlySalary").item(0)
									.getTextContent();
							currentActualSalary = eElement
									.getElementsByTagName("CurrentActualSalary")
									.item(0).getTextContent();
							currentFrequency = eElement
									.getElementsByTagName("CurrentFrequency")
									.item(0).getTextContent();
							currentTimeBase = eElement
									.getElementsByTagName("CurrentTimeBase")
									.item(0).getTextContent();
							currentRangeCode = eElement
									.getElementsByTagName("CurrentRangeCode")
									.item(0).getTextContent();
							currentAnniversaryDate = eElement
									.getElementsByTagName(
											"CurrentAnniversaryDate").item(0)
									.getTextContent();
							currentProbEndDate = eElement
									.getElementsByTagName("CurrentProbEndDate")
									.item(0).getTextContent();
							currentStep = eElement
									.getElementsByTagName("CurrentStep")
									.item(0).getTextContent();
							currentMppJobCode = eElement
									.getElementsByTagName("CurrentMppJobCode")
									.item(0).getTextContent();

							newMPP = eElement.getElementsByTagName("NewMPP")
									.item(0).getTextContent();
							newPerm = eElement.getElementsByTagName("NewPerm")
									.item(0).getTextContent();
							newProb = eElement.getElementsByTagName("NewProb")
									.item(0).getTextContent();
							newTemp = eElement.getElementsByTagName("NewTemp")
									.item(0).getTextContent();
							newAgency = eElement
									.getElementsByTagName("NewAgency").item(0)
									.getTextContent();
							newReptUnit = eElement
									.getElementsByTagName("NewReptUnit")
									.item(0).getTextContent();
							newClassCode = eElement
									.getElementsByTagName("NewClassCode")
									.item(0).getTextContent();
							newSerialNo = eElement
									.getElementsByTagName("NewSerialNo")
									.item(0).getTextContent();
							newFSLAStatus = eElement
									.getElementsByTagName("NewFSLAStatus")
									.item(0).getTextContent();
							newDept = eElement.getElementsByTagName("NewDept")
									.item(0).getTextContent();
							newDeptID = eElement
									.getElementsByTagName("NewDeptID").item(0)
									.getTextContent();
							newMPPSupName = eElement
									.getElementsByTagName("NewMPPSupName")
									.item(0).getTextContent();
							newCBID = eElement.getElementsByTagName("NewCBID")
									.item(0).getTextContent();
							newCollege = eElement
									.getElementsByTagName("NewCollege").item(0)
									.getTextContent();
							newClassificationTitle = eElement
									.getElementsByTagName(
											"NewClassificationTitle").item(0)
									.getTextContent();
							newWorkingTitle = eElement
									.getElementsByTagName("NewWorkingTitle")
									.item(0).getTextContent();
							newDivision = eElement
									.getElementsByTagName("NewDivision")
									.item(0).getTextContent();
							newFTMonthlySalary = eElement
									.getElementsByTagName("NewFTMonthlySalary")
									.item(0).getTextContent();
							newActualSalary = eElement
									.getElementsByTagName("NewActualSalary")
									.item(0).getTextContent();
							newFrequency = eElement
									.getElementsByTagName("NewFrequency")
									.item(0).getTextContent();
							newTimeBase = eElement
									.getElementsByTagName("NewTimeBase")
									.item(0).getTextContent();
							newAnniversaryDate = eElement
									.getElementsByTagName("NewAnniversaryDate")
									.item(0).getTextContent();
							newProbEndDate = eElement
									.getElementsByTagName("NewProbEndDate")
									.item(0).getTextContent();
							newStep = eElement.getElementsByTagName("NewStep")
									.item(0).getTextContent();
							newRangeCode = eElement
									.getElementsByTagName("NewRangeCode")
									.item(0).getTextContent();
							newMppJobCode = eElement
									.getElementsByTagName("NewMppJobCode")
									.item(0).getTextContent();
							hrName = eElement.getElementsByTagName("HRName")
									.item(0).getTextContent();
							hrDate = eElement.getElementsByTagName("HRDate")
									.item(0).getTextContent();
							hrComments = eElement
									.getElementsByTagName("HRComments").item(0)
									.getTextContent();
							payrollComments = eElement
									.getElementsByTagName("PayrollComments")
									.item(0).getTextContent();
							payrollName = eElement
									.getElementsByTagName("PayrollName")
									.item(0).getTextContent();
							payrollDate = eElement
									.getElementsByTagName("PayrollDate")
									.item(0).getTextContent();

						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMPL_ID", emplID);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("MIDDLE_INITIAL", middleInitial);
					dataMap.put("NEW_CMS_NO", newCMSNo);
					dataMap.put("EMP_RCD", empRCD);
					dataMap.put("CURRENT_CMS_NO", currentCMSNo);
					dataMap.put("CLASSIFICATION_CHANGE", classificationChange);
					dataMap.put("DEMOTION", demotion);
					dataMap.put("EQULITY_INCREASE", equlityIncrease);
					dataMap.put("EXTENSION_OF_APPOINTMENT",
							extensionofAppointment);
					dataMap.put("IN_RANGE_PROGRESSION", inRangeProgression);
					dataMap.put("REASSIGNMENT", reassignment);
					dataMap.put("RETURN_REASSIGNMENT", returnReassignment);
					dataMap.put("SALARY_STIPEND", salaryStipend);
					dataMap.put("SALARY_STIPEND_VALUE", salaryStipendValue);
					dataMap.put("SERVICE_SALARY_INCREASE",
							serviceSalaryIncrease);
					dataMap.put("APPROVED", approved);
					dataMap.put("DENIED", denied);
					dataMap.put("OTHERS_CB", others);
					dataMap.put("OTHERS_COMMENTS", othersComments);

					Object effDateObj = null;
					if (effectiveDateAction != null
							&& effectiveDateAction != "") {
						Date dateQualifingEventNew = Date
								.valueOf(effectiveDateAction);
						effDateObj = dateQualifingEventNew;
					}
					dataMap.put("EFFECTIVE_DATE_ACTION", effDateObj);

					Object endingDateObj = null;
					if (endingDate != null && endingDate != "") {
						Date endingDateObjNew = Date.valueOf(endingDate);
						endingDateObj = endingDateObjNew;
					}
					dataMap.put("ENDING_DATE", endingDateObj);

					Object newDateObj = null;
					if (newEndingDate != null && newEndingDate != "") {
						Date newDate1 = Date.valueOf(newEndingDate);
						newDateObj = newDate1;
					}
					dataMap.put("NEW_ENDING_DATE", newDateObj);

					dataMap.put("CURRENT_MPP", currentMPP);
					dataMap.put("CURRENT_PERM", currentPerm);
					dataMap.put("CURRENT_PROB", currentProb);
					dataMap.put("CURRENT_TEMP", currentTemp);
					dataMap.put("CURRENT_AGENCY", currentAgency);
					dataMap.put("CURRENT_REPT_UNIT", currentReptUnit);
					dataMap.put("CURRENT_CLASS_CODE", currentClassCode);

					dataMap.put("CURRENT_SERIAL_NO", currentSerialNo);
					dataMap.put("CURRENT_FSLA_STATUS", currentFSLAStatus);
					dataMap.put("CURRENT_CBID", currentCBID);
					dataMap.put("CURRENT_DEPT", currentDept);
					dataMap.put("CURRENT_DEPTID", currentDeptID);
					dataMap.put("CURRENT_MPP_SUP_NAME", currentMPPSupName);

					dataMap.put("CURRENT_DIVISION", currentDivision);
					dataMap.put("CURRENT_COLLEGE", currentCollege);
					dataMap.put("CURRENT_CLASSIFICATION_TITLE",
							currentClassificationTitle);
					dataMap.put("CURRENT_WORKING_TITLE", currentWorkingTitle);
					dataMap.put("CURRENT_FT_MONTHLY_SALARY",
							currentFTMonthlySalary);
					dataMap.put("CURRENT_ACTUAL_SALARY", currentActualSalary);
					dataMap.put("CURRENT_FREQUENCY", currentFrequency);
					dataMap.put("CURRENT_TIMEBASE", currentTimeBase);
					dataMap.put("CURRENT_RANGE_CODE", currentRangeCode);
					dataMap.put("CURRENT_STEP", currentStep);
					dataMap.put("CURRENT_ANNIVERSARY_DATE",
							currentAnniversaryDate);
					dataMap.put("CURRENT_MPP_JOB_CODE", currentMppJobCode);
					Object currentProbEndDateObj = null;
					if (currentProbEndDate != null && currentProbEndDate != "") {
						Date currentProbEndDateNew = Date
								.valueOf(currentProbEndDate);
						currentProbEndDateObj = currentProbEndDateNew;
					}
					dataMap.put("CURRENT_PROB_END_DATE", currentProbEndDateObj);

					dataMap.put("NEW_MPP", newMPP);
					dataMap.put("NEW_PERM", newPerm);
					dataMap.put("NEW_PROB", newProb);
					dataMap.put("NEW_TEMP", newTemp);
					dataMap.put("NEW_AGENCY", newAgency);
					dataMap.put("NEW_REPT_UNIT", newReptUnit);
					dataMap.put("NEW_CLASS_CODE", newClassCode);
					dataMap.put("NEW_SERIAL_NO", newSerialNo);
					dataMap.put("NEW_FSLA_STATUS", newFSLAStatus);
					dataMap.put("NEW_DEPT", newDept);
					dataMap.put("NEW_DEPT_ID", newDeptID);
					dataMap.put("NEW_MPP_SUP_NAME", newMPPSupName);
					dataMap.put("NEW_CBID", newCBID);
					dataMap.put("NEW_COLLEGE", newCollege);
					dataMap.put("NEW_CLASSIFICATION_TITLE",
							newClassificationTitle);
					dataMap.put("NEW_WORKING_TITLE", newWorkingTitle);
					dataMap.put("NEW_DIVISION", newDivision);
					dataMap.put("NEW_FT_MONTHLY_SALARY", newFTMonthlySalary);
					dataMap.put("NEW_ACTUAL_SALARY", newActualSalary);

					dataMap.put("NEW_FREQUENCY", newFrequency);
					dataMap.put("NEW_TIMEBASE", newTimeBase);
					dataMap.put("NEW_ANNIVERSARY_DATE", newAnniversaryDate);

					Object newProbEndDateObj = null;
					if (newProbEndDate != null && newProbEndDate != "") {
						Date newProbEndDateNew = Date.valueOf(newProbEndDate);
						newProbEndDateObj = newProbEndDateNew;
					}
					dataMap.put("NEW_PROB_END_DATE", newProbEndDateObj);
					dataMap.put("NEW_STEP", newStep);
					dataMap.put("NEW_RANGE_CODE", newRangeCode);
					dataMap.put("NEW_MPP_JOB_CODE", newMppJobCode);
					dataMap.put("HR_NAME", hrName);
					Object hrDateObj = null;
					if (hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}
					dataMap.put("HR_DATE", hrDateObj);
					dataMap.put("HR_COMMENTS", hrComments);

					Object payrollDateObj = null;
					if (payrollDate != null && payrollDate != "") {
						Date payrollDateNew = Date.valueOf(payrollDate);
						payrollDateObj = payrollDateNew;
					}
					dataMap.put("PAYROLL_DATE", payrollDateObj);
					dataMap.put("PAYROLL_COMMENTS", payrollComments);
					dataMap.put("PAYROLL_NAME", payrollName);
					dataMap.put("WORKFLOW_INSTANCE_ID", wfInstanceID);
				} catch (SAXException e) {
					log.error("SAXException=" + e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					log.error("Exception1");
					log.error("Exception=" + Arrays.toString(e.getStackTrace()));
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
			insertPersonnelActionNotice(conn, dataMap);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */
	public void insertPersonnelActionNotice(Connection conn,
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
			String tableName = "AEM_PERSONNEL_ACTION_NOTICE";
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
				log.info("Before Prepared Stmt PERSONNEL ACTION NOTICE");
				preparedStmt.execute();
				conn.commit();
				log.info("After Prepared Stmt PERSONNEL ACTION NOTICE");
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
