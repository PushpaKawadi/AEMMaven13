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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Dental Plan Enrollment Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=DentalPlanEnrollmentDB" })
public class DentalPlanEnrollmentDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(DentalPlanEnrollmentDB.class);

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

		String action1 = "";
		String action2 = "";
		String action3 = "";
		String action4 = "";
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
		String priorDentalPlanName = "";
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

							action1 = eElement.getElementsByTagName("typeOfAction1").item(0).getTextContent();
							log.info("typeOfAction1 Value is: " + action1);

							action2 = eElement.getElementsByTagName("typeOfAction2").item(0).getTextContent();
							log.info("typeOfAction1 Value is: " + action1);

							action3 = eElement.getElementsByTagName("typeOfAction3").item(0).getTextContent();
							log.info("typeOfAction1 Value is: " + action1);

							action4 = eElement.getElementsByTagName("typeOfAction4").item(0).getTextContent();
							log.info("typeOfAction1 Value is: " + action1);

							fName = eElement.getElementsByTagName("fname").item(0).getTextContent();

							middleName = eElement.getElementsByTagName("middleName").item(0).getTextContent();

							lName = eElement.getElementsByTagName("lname").item(0).getTextContent();

							address = eElement.getElementsByTagName("address").item(0).getTextContent();

							city = eElement.getElementsByTagName("City").item(0).getTextContent();

							state = eElement.getElementsByTagName("State").item(0).getTextContent();

							zip = eElement.getElementsByTagName("zip").item(0).getTextContent();

							permanentEmp = eElement.getElementsByTagName("permanentEmp").item(0).getTextContent();
							log.info("permanentEmp Value is: " + permanentEmp);

							sex = eElement.getElementsByTagName("sex").item(0).getTextContent();

							marritalStatus = eElement.getElementsByTagName("marritalStatus").item(0).getTextContent();

							socialSecNo = eElement.getElementsByTagName("socialSecNo").item(0).getTextContent();

							spouseSocialSecNo = eElement.getElementsByTagName("spouseSocialSecNo").item(0)
									.getTextContent();

							nameOfDentalPlan = eElement.getElementsByTagName("nameOfDentalPlan").item(0)
									.getTextContent();

							facilityNo = eElement.getElementsByTagName("facilityNo").item(0).getTextContent();

							actionCode1 = eElement.getElementsByTagName("actionCode1").item(0).getTextContent();

							enrolledPersonName1 = eElement.getElementsByTagName("enrolledPersonName1").item(0)
									.getTextContent();

							dob1 = eElement.getElementsByTagName("dob1").item(0).getTextContent();

							familyRelationship1 = eElement.getElementsByTagName("familyRelationship1").item(0)
									.getTextContent();

							actionCode2 = eElement.getElementsByTagName("actionCode2").item(0).getTextContent();

							enrolledPersonName2 = eElement.getElementsByTagName("enrolledPersonName2").item(0)
									.getTextContent();

							dob2 = eElement.getElementsByTagName("dob2").item(0).getTextContent();

							familyRelationship2 = eElement.getElementsByTagName("familyRelationship2").item(0)
									.getTextContent();

							actionCode3 = eElement.getElementsByTagName("actionCode3").item(0).getTextContent();

							enrolledPersonName3 = eElement.getElementsByTagName("enrolledPersonName3").item(0)
									.getTextContent();

							dob3 = eElement.getElementsByTagName("dob3").item(0).getTextContent();

							familyRelationship3 = eElement.getElementsByTagName("familyRelationship3").item(0)
									.getTextContent();

							actionCode4 = eElement.getElementsByTagName("actionCode4").item(0).getTextContent();

							enrolledPersonName4 = eElement.getElementsByTagName("enrolledPersonName4").item(0)
									.getTextContent();

							dob4 = eElement.getElementsByTagName("dob4").item(0).getTextContent();

							familyRelationship4 = eElement.getElementsByTagName("familyRelationship4").item(0)
									.getTextContent();

							actionCode5 = eElement.getElementsByTagName("actionCode5").item(0).getTextContent();
							enrolledPersonName5 = eElement.getElementsByTagName("enrolledPersonName5").item(0)
									.getTextContent();
							dob5 = eElement.getElementsByTagName("dob5").item(0).getTextContent();
							familyRelationship5 = eElement.getElementsByTagName("familyRelationship5").item(0)
									.getTextContent();
							actionCode6 = eElement.getElementsByTagName("actionCode6").item(0).getTextContent();
							enrolledPersonName6 = eElement.getElementsByTagName("enrolledPersonName6").item(0)
									.getTextContent();
							dob6 = eElement.getElementsByTagName("dob6").item(0).getTextContent();
							familyRelationship6 = eElement.getElementsByTagName("familyRelationship6").item(0)
									.getTextContent();
							actionCode7 = eElement.getElementsByTagName("actionCode7").item(0).getTextContent();
							enrolledPersonName7 = eElement.getElementsByTagName("enrolledPersonName7").item(0)
									.getTextContent();
							dob7 = eElement.getElementsByTagName("dob7").item(0).getTextContent();
							familyRelationship7 = eElement.getElementsByTagName("familyRelationship7").item(0)
									.getTextContent();
							actionCode8 = eElement.getElementsByTagName("actionCode8").item(0).getTextContent();
							enrolledPersonName8 = eElement.getElementsByTagName("enrolledPersonName8").item(0)
									.getTextContent();
							dob8 = eElement.getElementsByTagName("dob8").item(0).getTextContent();
							familyRelationship8 = eElement.getElementsByTagName("familyRelationship8").item(0)
									.getTextContent();
							priorDentalPlanName = eElement.getElementsByTagName("priorDentalPlanName").item(0)
									.getTextContent();
							checkCB1 = eElement.getElementsByTagName("checkCB1").item(0).getTextContent();
							checkCB2 = eElement.getElementsByTagName("checkCB2").item(0).getTextContent();
							checkCB3 = eElement.getElementsByTagName("checkCB3").item(0).getTextContent();
							empSignature = eElement.getElementsByTagName("empSignature").item(0).getTextContent();
							empSignedDate = eElement.getElementsByTagName("empSignedDate").item(0).getTextContent();
							empDEDCode1 = eElement.getElementsByTagName("empDEDCode1").item(0).getTextContent();
							empDEDCode2 = eElement.getElementsByTagName("empDEDCode2").item(0).getTextContent();
							dentalOrgCode = eElement.getElementsByTagName("dentalOrgCode").item(0).getTextContent();
							partyCode = eElement.getElementsByTagName("partyCode").item(0).getTextContent();
							payPeriod = eElement.getElementsByTagName("payPeriod").item(0).getTextContent();
							stateShareAmount = eElement.getElementsByTagName("stateShareAmount").item(0)
									.getTextContent();
							empDeductionAmount = eElement.getElementsByTagName("empDeductionAmount").item(0)
									.getTextContent();
							empDesignation = eElement.getElementsByTagName("empDesignation").item(0).getTextContent();
							bargaininUnit = eElement.getElementsByTagName("bargaininUnit").item(0).getTextContent();
							totalPremiumAmount = eElement.getElementsByTagName("totalPremiumAmount").item(0)
									.getTextContent();
							priorEmpDEDCode2 = eElement.getElementsByTagName("priorEmpDEDCode2").item(0)
									.getTextContent();
							priorEmpDEDCode1 = eElement.getElementsByTagName("priorEmpDEDCode1").item(0)
									.getTextContent();
							priorDentalOrgCode = eElement.getElementsByTagName("priorDentalOrgCode").item(0)
									.getTextContent();
							priorPartyCode = eElement.getElementsByTagName("priorPartyCode").item(0).getTextContent();
							permittingEventDate = eElement.getElementsByTagName("permittingEventDate").item(0)
									.getTextContent();
							permittingEventCode = eElement.getElementsByTagName("permittingEventCode").item(0)
									.getTextContent();
							electiveDateOfAction = eElement.getElementsByTagName("electiveDateOfAction").item(0)
									.getTextContent();
							agencyCode = eElement.getElementsByTagName("agencyCode").item(0).getTextContent();
							unitCode = eElement.getElementsByTagName("unitCode").item(0).getTextContent();

							agencyName1 = eElement.getElementsByTagName("agencyName1").item(0).getTextContent();

							agencyName2 = eElement.getElementsByTagName("agencyName2").item(0).getTextContent();

							remarks = eElement.getElementsByTagName("remarks").item(0).getTextContent();

							authorisedAgencySignature = eElement.getElementsByTagName("authorisedAgencySignature")
									.item(0).getTextContent();
							telephoneNumber = eElement.getElementsByTagName("telephoneNumber").item(0).getTextContent();

							dateRecievedInEmpOffice = eElement.getElementsByTagName("dateRecievedInEmpOffice").item(0)
									.getTextContent();
						}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("TYPE_OF_ACTION1", action1);
					dataMap.put("TYPE_OF_ACTION2", action2);
					dataMap.put("TYPE_OF_ACTION3", action3);
					dataMap.put("TYPE_OF_ACTION4", action4);
					dataMap.put("FNAME", fName);
					dataMap.put("MIDDLENAME", middleName);
					dataMap.put("LNAME", lName);
					dataMap.put("ADDRESS", address);
					dataMap.put("CITY", city);
					dataMap.put("STATE", state);
					dataMap.put("ZIP", zip);
					dataMap.put("SEX", sex);
					dataMap.put("MARITAL_STATU", marritalStatus);
					dataMap.put("SOCIAL_SEC_NO", socialSecNo);
					dataMap.put("SPOUSE_SOCIAL_SEC_NO", spouseSocialSecNo);
					dataMap.put("PERMANENT_EMP", permanentEmp);
					dataMap.put("NAME_OF_DENAL_PLAN", nameOfDentalPlan);
					dataMap.put("FACILITY_NO", facilityNo);
					dataMap.put("ACTION_CODE1", actionCode1);
					dataMap.put("ENROLLED_PERSON_NAME1", enrolledPersonName1);

					Object dob1Obj = null;
					if (dob1 != null && dob1 != "") {
						Date dob1New = Date.valueOf(dob1);
						dob1Obj = dob1New;
					}
					dataMap.put("DOB1", dob1Obj);
					dataMap.put("FAMILY_RELATIONSHIP1", familyRelationship1);
					dataMap.put("ACTION_CODE2", actionCode2);
					dataMap.put("ENROLLED_PERSON_NAME2", enrolledPersonName2);

					Object dob2Obj = null;
					if (dob2 != null && dob2 != "") {
						Date dob2New = Date.valueOf(dob2);
						dob2Obj = dob2New;
					}
					dataMap.put("DOB2", dob2Obj);
					dataMap.put("FAMILY_RELATIONSHIP2", familyRelationship2);
					dataMap.put("ACTION_CODE3", actionCode3);
					dataMap.put("ENROLLED_PERSON_NAME3", enrolledPersonName3);

					Object dob3Obj = null;
					if (dob3 != null && dob3 != "") {
						Date dob3New = Date.valueOf(dob3);
						dob1Obj = dob3New;
					}
					dataMap.put("DOB3", dob3Obj);
					dataMap.put("FAMILY_RELATIONSHIP3", familyRelationship3);
					dataMap.put("ACTION_CODE4", actionCode4);
					dataMap.put("ENROLLED_PERSON_NAME4", enrolledPersonName4);

					Object dob4Obj = null;
					if (dob4 != null && dob4 != "") {
						Date dob4New = Date.valueOf(dob4);
						dob4Obj = dob4New;
					}
					dataMap.put("DOB4", dob4Obj);
					dataMap.put("FAMILY_RELATIONSHIP4", familyRelationship4);
					dataMap.put("ACTION_CODE5", actionCode5);
					dataMap.put("ENROLLED_PERSON_NAME5", enrolledPersonName5);

					Object dob5Obj = null;
					if (dob5 != null && dob5 != "") {
						Date dob5New = Date.valueOf(dob5);
						dob5Obj = dob5New;
					}
					dataMap.put("DOB5", dob5Obj);
					dataMap.put("FAMILY_RELATIONSHIP5", familyRelationship5);
					dataMap.put("ACTION_CODE6", actionCode6);
					dataMap.put("ENROLLED_PERSON_NAME6", enrolledPersonName6);

					Object dob6Obj = null;
					if (dob6 != null && dob6 != "") {
						Date dob6New = Date.valueOf(dob6);
						dob6Obj = dob6New;
					}
					dataMap.put("DOB6", dob6Obj);
					dataMap.put("FAMILY_RELATIONSHIP6", familyRelationship6);
					dataMap.put("ACTION_CODE7", actionCode7);
					dataMap.put("ENROLLED_PERSON_NAME7", enrolledPersonName7);

					Object dob7Obj = null;
					if (dob7 != null && dob7 != "") {
						Date dob7New = Date.valueOf(dob7);
						dob7Obj = dob7New;
					}
					dataMap.put("DOB7", dob7Obj);
					dataMap.put("FAMILY_RELATIONSHIP7", familyRelationship7);
					dataMap.put("ACTION_CODE8", actionCode8);
					dataMap.put("ENROLLED_PERSON_NAME8", enrolledPersonName8);

					Object dob8Obj = null;
					if (dob8 != null && dob8 != "") {
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

					Object empSignedDateObj = null;
					if (empSignedDate != null && empSignedDate != "") {
						Date empSignedDateNew = Date.valueOf(empSignedDate);
						empSignedDateObj = empSignedDateNew;
					}
					dataMap.put("EMP_SIGNED_DATE", empSignedDateObj);
					dataMap.put("EMP_DED_CODE1", empDEDCode1);
					dataMap.put("EMP_DED_CODE2", empDEDCode2);
					dataMap.put("DENTAL_ORG_CODE", dentalOrgCode);
					dataMap.put("PARTY_CODE", partyCode);

					Object payPeriodObj = null;
					if (payPeriod != null && payPeriod != "") {
						Date epayPeriodNew = Date.valueOf(payPeriod);
						payPeriodObj = epayPeriodNew;
					}
					dataMap.put("PAY_PERIOD", payPeriodObj);
					dataMap.put("STATE_SHARE_AMOUNT", stateShareAmount);
					dataMap.put("EMP_DEDUCTION_AMOUNT", empDeductionAmount);
					dataMap.put("EMP_DESIGNATION", empDesignation);
					dataMap.put("BARGAIN_IN_UNIT", bargaininUnit);
					dataMap.put("TOTAL_PREMIUM_AMOUNT", totalPremiumAmount);
					dataMap.put("PRIOR_EMP_DED_CODE1", priorEmpDEDCode1);
					dataMap.put("PRIOR_EMP_DED_CODE2", priorEmpDEDCode2);
					dataMap.put("PRIOR_DENTAL_ORG_CODE", priorDentalOrgCode);
					dataMap.put("PRIOR_PARTY_CODE", priorPartyCode);

					Object permittingEventDateObj = null;
					if (permittingEventDate != null && permittingEventDate != "") {
						Date permittingEventDateNew = Date.valueOf(permittingEventDate);
						permittingEventDateObj = permittingEventDateNew;
					}
					dataMap.put("PERMITTING_EVENT_DATE", permittingEventDateObj);
					dataMap.put("PERMITTING_EVENT_CODE", permittingEventCode);

					Object electiveDateOfActionObj = null;
					if (electiveDateOfAction != null && electiveDateOfAction != "") {
						Date electiveDateOfActionNew = Date.valueOf(electiveDateOfAction);
						electiveDateOfActionObj = electiveDateOfActionNew;
					}
					dataMap.put("ELECTIVE_DATE_OF_ACTION", electiveDateOfActionObj);
					dataMap.put("AGENCY_CODE", agencyCode);
					dataMap.put("UNIT_CODE", unitCode);
					dataMap.put("AGENCY_NAME1", agencyName1);
					dataMap.put("AGENCY_NAME2", agencyName2);
					dataMap.put("REMARK", remarks);
					dataMap.put("AUTHORISED_AGENCY_SIGNATURE", authorisedAgencySignature);
					dataMap.put("TELEPHONE_NUMBER", telephoneNumber);

					Object dateRecievedInEmpOfficeObj = null;
					if (dateRecievedInEmpOffice != null && dateRecievedInEmpOffice != "") {
						Date dateRecievedInEmpOfficeNew = Date.valueOf(dateRecievedInEmpOffice);
						dateRecievedInEmpOfficeObj = dateRecievedInEmpOfficeNew;
					}
					dataMap.put("DATE_RECEIVED_IN_EMP_OFFICE", dateRecievedInEmpOfficeObj);

				} catch (SAXException e) {
					log.error("SAXException=" + Arrays.toString(e.getStackTrace()));
					e.printStackTrace();
				} catch (Exception e) {
					log.error("Exception1");
					log.error("Exception=" + Arrays.toString(e.getStackTrace()));
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						log.error("IOException=" + Arrays.toString(e.getStackTrace()));
						e.printStackTrace();
					}

				}

			}
		}
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
			log.error("Connection Successfull");
			insertDentalPlanEnrollmentDetails(conn, dataMap);
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
			log.error("Conn Exception=" + Arrays.toString(e.getStackTrace()));
			e.printStackTrace();
		} /*
			 * finally { try { if (con != null) { log.info("Conn Exec="); } } catch
			 * (Exception exp) { exp.printStackTrace(); } }
			 */
		return null;
	}

	public void insertDentalPlanEnrollmentDetails(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		log.error("conn=" + conn);
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + Arrays.toString(e1.getStackTrace()));
				e1.printStackTrace();
			} catch (Exception e) {
				log.error("Exception=" + Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			}
			String tableName = "AEM_DENTAL_PLAN_ENROLLMENT";
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
				log.error("SQLException=" + Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			} catch (Exception e) {
				log.error("Exception4");
				log.error("Exception=" + Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			}
			try {
				log.error("Before Prepared stmt");
				preparedStmt.execute();
				conn.commit();
				log.error("After Prepared stmt");
			} catch (SQLException e1) {
				log.error("SQLException=" + Arrays.toString(e1.getStackTrace()));
				e1.printStackTrace();
			} catch (Exception e) {
				log.error("Exception5");
				log.error("Exception=" + Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						conn.close();
					} catch (SQLException e) {
						log.error("SQLException=" + Arrays.toString(e.getStackTrace()));
						e.printStackTrace();
					} catch (Exception e) {
						log.error("Exception7");
						log.error("Exception=" + Arrays.toString(e.getStackTrace()));
						e.printStackTrace();
					}
				}
			}
		}
	}
}
