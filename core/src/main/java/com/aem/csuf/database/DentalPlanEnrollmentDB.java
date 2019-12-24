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
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Dental Plan Enrollment Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=DentalPlanEnrollmentDB" })
public class DentalPlanEnrollmentDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(DentalPlanEnrollmentDB.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String action = "";
		String fName = "";
		String middleName = "";
		String lName = "";
		String address = "";
		String city = "";
		String state = "";
		String zip = "";
		String permanentEmp = "";
		String sex = "";
		String marritalStatus = "";
		String socialSecNo = "";
		String spouseSocialSecNo = "";
		String nameOfDentalPlan = "";
		String facilityNo = "";
		String actionCode1 = "";
		String enrolledPersonName1 = "";
		String dob1 = "";
		String familyRelationship1 = "";
		String actionCode2 = "";
		String enrolledPersonName2 = "";
		String dob2 = "";
		String familyRelationship2 = "";
		String actionCode3 = "";
		String enrolledPersonName3 = "";
		String dob3 = "";
		String familyRelationship3 = "";
		String actionCode4 = "";
		String enrolledPersonName4 = "";
		String dob4 = "";
		String familyRelationship4 = "";
		String actionCode5 = "";
		String enrolledPersonName5 = "";
		String dob5 = "";
		String familyRelationship5 = "";
		String actionCode6 = "";
		String enrolledPersonName6 = "";
		String dob6 = "";
		String familyRelationship6 = "";
		String actionCode7 = "";
		String enrolledPersonName7 = "";
		String dob7 = "";
		String familyRelationship7 = "";
		String actionCode8 = "";
		String enrolledPersonName8 = "";
		String dob8 = "";
		String familyRelationship8 = "";
		String priorDentalPlanName = "";
		String checkCB1 = "";
		String checkCB2 = "";
		String checkCB3 = "";
		String empSignature = "";
		String empSignedDate = "";
		String empDEDCode1 = "";
		String empDEDCode2 = "";
		String dentalOrgCode = "";
		String partyCode = "";
		String payPeriod = "";
		String stateShareAmount = "";
		String empDeductionAmount = "";
		String empDesignation = "";
		String bargaininUnit = "";
		String totalPremiumAmount = "";
		String priorEmpDEDCode2 = "";
		String priorEmpDEDCode1 = "";
		String priorDentalOrgCode = "";
		String priorPartyCode = "";
		String permittingEventDate = "";
		String permittingEventCode = "";
		String electiveDateOfAction = "";
		String agencyCode = "";
		String unitCode = "";
		String agencyName1 = "";
		String agencyName2 = "";
		String remarks = "";
		String authorisedAgencySignature = "";
		String telephoneNumber = "";
		String dateRecievedInEmpOffice = "";

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

			log.info("filePath of Dental Plan=" + filePath);
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("xmlFiles of Dental Plan=" + filePath);
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

							action = eElement.getElementsByTagName("action")
									.item(0).getTextContent();
									log.info("actionType Value is: " + action);

							fName = eElement.getElementsByTagName("Fname")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + fName);

							middleName = eElement.getElementsByTagName("MiddleName")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + middleName);

							lName = eElement.getElementsByTagName("Lname")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + lName);

							address = eElement.getElementsByTagName("Address")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + address);

							city = eElement.getElementsByTagName("City")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + city);

							state = eElement.getElementsByTagName("State")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + state);

							zip = eElement.getElementsByTagName("Zip")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + zip);

							permanentEmp = eElement.getElementsByTagName("PermanentEmp")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + permanentEmp);

							sex = eElement.getElementsByTagName("Sex")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + sex);

							marritalStatus = eElement.getElementsByTagName("MarritalStatus")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + marritalStatus);

							socialSecNo = eElement.getElementsByTagName("SocialSecNo")
									.item(0).getTextContent();
									log.info("reductionHourdate Value is: " + socialSecNo);

							spouseSocialSecNo = eElement.getElementsByTagName("SpouseSocialSecNo")
									.item(0).getTextContent();
							log.info("reductionHourdate Value is: " + spouseSocialSecNo);

							nameOfDentalPlan = eElement.getElementsByTagName("NameOfDentalPlan")
									.item(0).getTextContent();
							log.info("reductionHourdate Value is: " + nameOfDentalPlan);

							facilityNo = eElement.getElementsByTagName("FacilityNo")
									.item(0).getTextContent();
						//	log.info("DeathOfEmployee Value is: " + DeathOfEmployee);

							actionCode1 = eElement.getElementsByTagName("ActionCode1")
									.item(0).getTextContent();
							//log.info("EmployeeDeath Value is: " + EmployeeDeath);

							enrolledPersonName1 = eElement.getElementsByTagName("EnrolledPersonName1")
									.item(0).getTextContent();
						//	log.info("Divorse Value is: " + Divorse);

							dob1 = eElement.getElementsByTagName("DOB1")
									.item(0).getTextContent();
						//	log.info("dateOfDivorse Value is: " + dateOfDivorse);
							
							familyRelationship1 = eElement.getElementsByTagName("FamilyRelationship1")
									.item(0).getTextContent();
							//log.info("lossOfDepChild Value is: " + lossOfDepChild);
							
							actionCode2 = eElement.getElementsByTagName("ActionCode2")
									.item(0).getTextContent();
						//	log.info("DepChildStatus Value is: " + DepChildStatus);
							
						enrolledPersonName2 = eElement.getElementsByTagName("EnrolledPersonName2")
									.item(0).getTextContent();
						//	log.info("dissolutionChk Value is: " + dissolutionChk);
							
							dob2 = eElement.getElementsByTagName("DOB2")
									.item(0).getTextContent();
						//	log.info("dissolutionDate Value is: " + dissolutionDate);
							
							familyRelationship2 = eElement.getElementsByTagName("FamilyRelationship2")
									.item(0).getTextContent();
						//	log.info("Months Value is: "+Months);
							
							actionCode3 = eElement.getElementsByTagName("ActionCode3")
									.item(0).getTextContent();
							//log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							enrolledPersonName3 = eElement.getElementsByTagName("EnrolledPersonName3")
									.item(0).getTextContent();
							//log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							dob3 = eElement.getElementsByTagName("DOB3")
									.item(0).getTextContent();
							//log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							familyRelationship3 = eElement.getElementsByTagName("FamilyRelationship3")
									.item(0).getTextContent();
							//log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							actionCode4 = eElement.getElementsByTagName("ActionCode4")
									.item(0).getTextContent();
						//	log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							enrolledPersonName4 = eElement.getElementsByTagName("EnrolledPersonName4")
									.item(0).getTextContent();
							//log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							dob4 = eElement.getElementsByTagName("DOB4")
									.item(0).getTextContent();
						//	log.info("ThePlan Value is: "+endOfEmployeementdate);
							
							familyRelationship4 = eElement.getElementsByTagName("FamilyRelationship4")
									.item(0).getTextContent();
							actionCode5 = eElement.getElementsByTagName("ActionCode5")
									.item(0).getTextContent();
							enrolledPersonName5 = eElement.getElementsByTagName("EnrolledPersonName5")
									.item(0).getTextContent();
							dob5 = eElement.getElementsByTagName("DOB5")
									.item(0).getTextContent();
							familyRelationship5 = eElement.getElementsByTagName("FamilyRelationship5")
									.item(0).getTextContent();
							actionCode6 = eElement.getElementsByTagName("ActionCode6")
									.item(0).getTextContent();
							enrolledPersonName6 = eElement.getElementsByTagName("EnrolledPersonName6")
									.item(0).getTextContent();
							dob6 = eElement.getElementsByTagName("DOB6")
									.item(0).getTextContent();
							familyRelationship6 = eElement.getElementsByTagName("FamilyRelationship6")
									.item(0).getTextContent();
							actionCode7 = eElement.getElementsByTagName("ActionCode7")
									.item(0).getTextContent();
							enrolledPersonName7 = eElement.getElementsByTagName("EnrolledPersonName7")
									.item(0).getTextContent();
							dob7 = eElement.getElementsByTagName("DOB7")
									.item(0).getTextContent();
							familyRelationship7 = eElement.getElementsByTagName("FamilyRelationship7")
									.item(0).getTextContent();
							actionCode8 = eElement.getElementsByTagName("ActionCode8")
									.item(0).getTextContent();
							enrolledPersonName8 = eElement.getElementsByTagName("EnrolledPersonName8")
									.item(0).getTextContent();							
							dob8 = eElement.getElementsByTagName("DOB8")
									.item(0).getTextContent();
							familyRelationship8 = eElement.getElementsByTagName("FamilyRelationship8")
									.item(0).getTextContent();
							priorDentalPlanName = eElement.getElementsByTagName("PriorDentalPlanName")
									.item(0).getTextContent();
							checkCB1 = eElement.getElementsByTagName("CheckCB1")
									.item(0).getTextContent();
							checkCB2 = eElement.getElementsByTagName("CheckCB2")
									.item(0).getTextContent();
							checkCB3 = eElement.getElementsByTagName("CheckCB3")
									.item(0).getTextContent();
							empSignature = eElement.getElementsByTagName("EmpSignature")
									.item(0).getTextContent();
							empSignedDate = eElement.getElementsByTagName("EmpSignedDate")
									.item(0).getTextContent();
							empDEDCode1 = eElement.getElementsByTagName("EmpDEDCode1")
									.item(0).getTextContent();
							empDEDCode2 = eElement.getElementsByTagName("EmpDEDCode2")
									.item(0).getTextContent();
							dentalOrgCode = eElement.getElementsByTagName("DentalOrgCode")
									.item(0).getTextContent();
							partyCode = eElement.getElementsByTagName("PartyCode")
									.item(0).getTextContent();
							payPeriod = eElement.getElementsByTagName("PayPeriod")
									.item(0).getTextContent();
							stateShareAmount = eElement.getElementsByTagName("StateShareAmount")
									.item(0).getTextContent();
							empDeductionAmount = eElement.getElementsByTagName("EmpDeductionAmount")
									.item(0).getTextContent();
							empDesignation = eElement.getElementsByTagName("EmpDesignation")
							.item(0).getTextContent();
							bargaininUnit = eElement.getElementsByTagName("BargaininUnit")
									.item(0).getTextContent();
							totalPremiumAmount = eElement.getElementsByTagName("TotalPremiumAmount")
									.item(0).getTextContent();
							priorEmpDEDCode2 = eElement.getElementsByTagName("PriorEmpDEDCode2")
									.item(0).getTextContent();
							priorEmpDEDCode1 = eElement.getElementsByTagName("PriorEmpDEDCode1")
									.item(0).getTextContent();
							priorDentalOrgCode = eElement.getElementsByTagName("PriorDentalOrgCode")
									.item(0).getTextContent();
							priorPartyCode = eElement.getElementsByTagName("PriorPartyCode")
									.item(0).getTextContent();
							permittingEventDate	= eElement.getElementsByTagName("PermittingEventDate")
							.item(0).getTextContent();	
							permittingEventCode = eElement.getElementsByTagName("PermittingEventCode")
									.item(0).getTextContent();
							electiveDateOfAction = eElement.getElementsByTagName("ElectiveDateOfAction")
									.item(0).getTextContent();
							agencyCode = eElement.getElementsByTagName("AgencyCode")
									.item(0).getTextContent();
							unitCode = eElement.getElementsByTagName("UnitCode")
									.item(0).getTextContent();
							agencyName1 = eElement.getElementsByTagName("AgencyName1")
									.item(0).getTextContent();
							agencyName2 = eElement.getElementsByTagName("AgencyName2")
									.item(0).getTextContent();
							remarks = eElement.getElementsByTagName("Remarks")
									.item(0).getTextContent();
							authorisedAgencySignature = eElement.getElementsByTagName("AuthorisedAgencySignature")
									.item(0).getTextContent();
							telephoneNumber = eElement.getElementsByTagName("TelephoneNumber")
									.item(0).getTextContent();
							dateRecievedInEmpOffice = eElement.getElementsByTagName("DateRecievedInEmpOffice")
									.item(0).getTextContent();
						}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("TYPE_OF_ACTION", action);
					dataMap.put("FNAME", fName);
					dataMap.put("MIDDLENAME", middleName);
					dataMap.put("LNAME", lName);
					dataMap.put("ADDRESS", address);
					dataMap.put("CITY", city);
					dataMap.put("STATE", state);
					dataMap.put("ZIP", zip);
					dataMap.put("PERMANENT_EMP", permanentEmp);
					dataMap.put("SEX", sex);					
					dataMap.put("MARRITAL_STATUS", marritalStatus);
					dataMap.put("SOCIAL_SEC_NO", socialSecNo);					
					dataMap.put("SPOUSE_SOCIAL_SEC_NO", spouseSocialSecNo);		
					dataMap.put("NAME_OF_DENAL_PLAN", nameOfDentalPlan);
					dataMap.put("FACILITY_NO", facilityNo);
					dataMap.put("ACTION_CODE1", actionCode1);
					dataMap.put("ENROLLED_PERSON_NAME1", enrolledPersonName1);
					
					Object dob1Obj= null;
					if(dob1 != null && dob1 != "") {
						Date dob1New = Date.valueOf(dob1);
						dob1Obj = dob1New;
					}
					dataMap.put("DOB1", dob1Obj);
					dataMap.put("FAMILY_RELATIONSHIP1", familyRelationship1);
					
					dataMap.put("ACTION_CODE2", actionCode2);
					dataMap.put("ENROLLED_PERSON_NAME2", enrolledPersonName2);
					
					Object dob2Obj= null;
					if(dob2 != null && dob2 != "") {
						Date dob2New = Date.valueOf(dob2);
						dob2Obj = dob2New;
					}
					dataMap.put("DOB2", dob2Obj);
					dataMap.put("FAMILY_RELATIONSHIP2", familyRelationship2);
					dataMap.put("ACTION_CODE3", actionCode3);
					dataMap.put("ENROLLED_PERSON_NAME3", enrolledPersonName3);

					Object dob3Obj= null;
					if(dob3 != null && dob3 != "") {
						Date dob3New = Date.valueOf(dob3);
						dob3Obj = dob3New;
					}
					dataMap.put("DOB3", dob3Obj);
					dataMap.put("FAMILY_RELATIONSHIP3", familyRelationship3);
					dataMap.put("ACTION_CODE4", actionCode4);
					dataMap.put("ENROLLED_PERSON_NAME4", enrolledPersonName4);

					Object dob4Obj= null;
					if(dob4 != null && dob4 != "") {
						Date dob4New = Date.valueOf(dob4);
						dob4Obj = dob4New;
					}
					dataMap.put("DOB4", dob4Obj);
					dataMap.put("FAMILY_RELATIONSHIP4", familyRelationship4);
					dataMap.put("ACTION_CODE5", actionCode5);
					dataMap.put("ENROLLED_PERSON_NAME5", enrolledPersonName5);
					
					Object dob5Obj= null;
					if(dob5 != null && dob5 != "") {
						Date dob5New = Date.valueOf(dob5);
						dob5Obj = dob5New;
					}
					dataMap.put("DOB5", dob5Obj);
				
					dataMap.put("FAMILY_RELATIONSHIP5", familyRelationship5);
					dataMap.put("ACTION_CODE6", actionCode6);
					dataMap.put("ENROLLED_PERSON_NAME6", enrolledPersonName6);

					Object dob6Obj= null;
					if(dob6 != null && dob6 != "") {
						Date dob6New = Date.valueOf(dob6);
						dob6Obj = dob6New;
					}
					dataMap.put("DOB6", dob6Obj);
					dataMap.put("FAMILY_RELATIONSHIP6", familyRelationship6);
					dataMap.put("ACTION_CODE7", actionCode7);
					dataMap.put("ENROLLED_PERSON_NAME7", enrolledPersonName7);

					Object dob7Obj= null;
					if(dob7 != null && dob7 != "") {
						Date dob7New = Date.valueOf(dob7);
						dob7Obj = dob7New;
					}
					dataMap.put("DOB7", dob7Obj);
					dataMap.put("FAMILY_RELATIONSHIP7", familyRelationship7);
					dataMap.put("ACTION_CODE8", actionCode8);
					dataMap.put("ENROLLED_PERSON_NAME8", enrolledPersonName8);

					Object dob8Obj= null;
					if(dob8 != null && dob8 != "") {
						Date dob8New = Date.valueOf(dob8);
						dob8Obj = dob8New;
					}
					dataMap.put("DOB8", dob8Obj);
					dataMap.put("FAMILY_RELATIONSHIP8", familyRelationship8);
					dataMap.put("PRIOR_DENTAL_PLAN_NAME", priorDentalPlanName);
					dataMap.put("CB1", checkCB1);
					dataMap.put("CB2", checkCB2);
					dataMap.put("CB3", checkCB3);
			
					dataMap.put("EMP_SIGNATURE", empSignature);

					Object empSignedDateObj= null;
					if(empSignedDate != null && empSignedDate != "") {
						Date empSignedDateNew = Date.valueOf(empSignedDate);
						empSignedDateObj = empSignedDateNew;
					}
					dataMap.put("EMP_SIGNED_DATE", empSignedDateObj);
					dataMap.put("EMP_DED_CODE1", empDEDCode1);
					dataMap.put("EMP_DED_CODE2", empDEDCode2);
					dataMap.put("DENTAL_ORG_CODE", dentalOrgCode);
					dataMap.put("PARTY_CODE", partyCode);
					dataMap.put("PAY_PERIOD", payPeriod);
					dataMap.put("STATE_SHARE_AMOUNT", stateShareAmount);
					dataMap.put("EMP_DEDUCTION_AMOUNT", empDeductionAmount);
					dataMap.put("EMP_DESIGNATION", empDesignation);
					dataMap.put("BARGAIN_IN_UNIT", bargaininUnit);
					dataMap.put("TOTAL_PREMIUM_AMOUNT", totalPremiumAmount);
					dataMap.put("PRIOR_EMP_DED_CODE1", priorEmpDEDCode1);
					dataMap.put("PRIOR_EMP_DED_CODE2", priorEmpDEDCode2);
					dataMap.put("PRIOR_DENTAL_ORG_CODE", priorDentalOrgCode);
					dataMap.put("PRIOR_PARTY_CODE", priorPartyCode);

					Object permittingEventDateObj= null;
					if(permittingEventDate != null && permittingEventDate != "") {
						Date permittingEventDateNew = Date.valueOf(permittingEventDate);
						permittingEventDateObj = permittingEventDateNew;
					}
					dataMap.put("PERMITTING_EVENT_DATE", permittingEventDateObj);
					dataMap.put("PERMITTING_EVENT_CODE", permittingEventCode);

					Object delectiveDateOfActionObj= null;
					if(electiveDateOfAction != null && electiveDateOfAction != "") {
						Date delectiveDateOfActionNew = Date.valueOf(electiveDateOfAction);
						delectiveDateOfActionObj = delectiveDateOfActionNew;
					}
					dataMap.put("ELECTIVE_DATE_OF_ACTION", delectiveDateOfActionObj);
					dataMap.put("AGENCY_CODE", agencyCode);
					dataMap.put("UNIT_CODE", unitCode);
					dataMap.put("AGENCY_NAME1", agencyName1);
					dataMap.put("AGENCY_NAME2", agencyName2);
					dataMap.put("REMARK", remarks);
					dataMap.put("AUTHORISED_AGENCY_SIGNATURE", authorisedAgencySignature);
					dataMap.put("TELEPHONE_NUMBER", telephoneNumber);

					Object dateRecievedInEmpOfficeObj= null;
					if(dateRecievedInEmpOffice != null && dateRecievedInEmpOffice != "") {
						Date dateRecievedInEmpOfficeNew = Date.valueOf(dateRecievedInEmpOffice);
						dateRecievedInEmpOfficeObj = dateRecievedInEmpOfficeNew;
					}
					dataMap.put("DATE_RECEIVED_IN_EMP_OFFICE", dateRecievedInEmpOfficeObj);

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
		conn = getConnection();
		if (conn != null) {
			log.error("Connection Successfull");
			insertDentalPlanData(conn, dataMap);
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

	public void insertDentalPlanData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_DENTAL_PLAN_ENROLLMENT";
			StringBuilder sql = new StringBuilder("INSERT INTO  ").append(tableName).append(" (");
			log.info("The SQL COMMAN IS=" + sql);
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