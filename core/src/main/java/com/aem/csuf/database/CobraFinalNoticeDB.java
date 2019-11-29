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

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Cobra Final Notice Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CobraFinalNoticeDB" })
public class CobraFinalNoticeDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CobraFinalNoticeDB.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String firstName = "";
		String lastName = "";
		String empId = "";
		String empRCD = "";		
		String date = "";
		String Address = "";
		String city = "";
		String state = "";
		String zip = "";
		String Plan = "";
		String CobraCoverageDate = "";
		String endOfEmployeementChk = "";
		String endOfEmployeementdate = "";
		String reductionHourschk = "";
		String reductionHourdate = "";
		String DeathOfEmployee = "";
		String EmployeeDeath = "";
		String Divorse = "";
		String dateOfDivorse = "";
		String lossOfDepChild = "";
		String DepChildStatus = "";
		String dissolutionChk = "";
		String dissolutionDate = "";
		String Months = "";
		String EmployeeName1Chk = "";
		String EmployeeName1 = "";
		String spouseNameChk = "";
		String spouseName = "";
		String domesticPartnerNameChk = "";
		String domesticPartnerName = "";
		String dependentChildNameChk = "";
		String dependentChildName = "";
		String childNameChk = "";
		String childName = "";
		String cobraBegin = "";
		String cobraEnd = "";
		String Year = "";
		String healthPlan2 = "";
		String healthPlanPrice = "";
		String dentalPlan2 = "";
		String dentalPlanPrice = "";
		String vision2 = "";
		String visionPrice = "";
		String hcra2 = "";
		String hcraPrice = "";
		String coment1 = "";
		String coment2 = "";
		String comment = "";
		String contact = "";
		String replyBy = "";
		String plan1 = "";
		String dependentName1 = "";
		String dob1 = "";
		String relationshipToEmp1 = "";
		String ssn1 = "";
		String dependentName2 = "";
		String dob2 = "";
		String relationshipToEmp2 = "";
		String ssn2 = "";
		String dependentName3 = "";
		String dob3 = "";
		String relationshipToEmp3 = "";
		String ssn3 = "";
		String dependentName4 = "";
		String dob4 = "";
		String relationshipToEmp4 = "";
		String ssn4 = "";
		String dependentName5 = "";
		String dob5 = "";
		String relationshipToEmp5 = "";
		String ssn5 = "";
		String healthChk = "";
		String healthPlan4 = "";
		String VisionChk = "";
		String vision4 = "";
		String declineChk = "";
		String decline = "";
		String Dentalchk = "";
		String dentalPlan4 = "";
		String health1chk = "";
		String hcra4 = "";
		String medicarePartAYes = "";
		String medicarePartANo = "";
		String medicareentitlement = "";
		String signature = "";
		String electionDate = "";
		String printName = "";
		String relationToIndividual = "";
		String printAdd = "";
		String telNumber = "";
		String meidcalContact = "";
		
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

							empId = eElement.getElementsByTagName("Empl_ID")
									.item(0).getTextContent();
						
							lastName = eElement
									.getElementsByTagName("Last_Name")
									.item(0).getTextContent();
						
							firstName = eElement
									.getElementsByTagName("First_Name")
									.item(0).getTextContent();
							
							empRCD = eElement.getElementsByTagName("EMPL_RCD")
									.item(0).getTextContent();
						
							date = eElement.getElementsByTagName("Date_Initiated")
									.item(0).getTextContent();
							log.info("Date_Initiated Value is: "+date);
							Address = eElement
									.getElementsByTagName("Street_Address").item(0)
									.getTextContent();
							log.info("Street_Address Value is: "+Address);
							city = eElement
									.getElementsByTagName("City").item(0)
									.getTextContent();
							log.info("City Value is: "+city);
							state = eElement
									.getElementsByTagName("State")
									.item(0).getTextContent();
							log.info("State Value is: "+state);
							zip = eElement
									.getElementsByTagName("Zip")
									.item(0).getTextContent();
							log.info("Zip Value is: "+zip);
							Plan = eElement
									.getElementsByTagName("ThePlan")
									.item(0).getTextContent();
							log.info("ThePlan Value is: "+Plan);
//							CobraCoverageDate = eElement
//									.getElementsByTagName("EndOfCobraCoverageDate")
//									.item(0).getTextContent();
//
//							endOfEmployeementChk = eElement
//									.getElementsByTagName("EndOfEmploymentChk")
//									.item(0).getTextContent();
//							endOfEmployeementdate = eElement
//									.getElementsByTagName("EndOfEmploymentDate")
//									.item(0).getTextContent();
//
//							reductionHourschk = eElement
//									.getElementsByTagName("ReductionHoursChk")
//									.item(0).getTextContent();
//
//							reductionHourdate = eElement
//									.getElementsByTagName("ReductionHoursDate")
//									.item(0).getTextContent();
//
//							DeathOfEmployee = eElement
//									.getElementsByTagName("DeathOfEmployeeChk").item(0)
//									.getTextContent();
//
//							EmployeeDeath = eElement
//									.getElementsByTagName("DeathOfEmployeeDate")
//									.item(0).getTextContent();
//							Divorse = eElement
//									.getElementsByTagName("DivorseChk")
//									.item(0).getTextContent();
//							dateOfDivorse = eElement
//									.getElementsByTagName("DivorceDate")
//									.item(0).getTextContent();
//							lossOfDepChild = eElement.getElementsByTagName("LossOfDepChildStatusChk")
//									.item(0).getTextContent();
//
//							DepChildStatus = eElement
//									.getElementsByTagName("LossOfDeptChildStatusDate").item(0)
//									.getTextContent();
//							dissolutionChk = eElement
//									.getElementsByTagName("DissolutionChk").item(0)
//									.getTextContent();
//							dissolutionDate = eElement
//									.getElementsByTagName("DissolutionDate").item(0)
//									.getTextContent();
//
//							Months = eElement.getElementsByTagName("months")
//									.item(0).getTextContent();
//
//							EmployeeName1Chk = eElement.getElementsByTagName("EmployeeNameChk")
//									.item(0).getTextContent();
//							EmployeeName1 = eElement.getElementsByTagName("EmployeeName")
//									.item(0).getTextContent();
//							spouseNameChk = eElement.getElementsByTagName("SpouseNameChk")
//									.item(0).getTextContent();
//
//							spouseName = eElement.getElementsByTagName("SpouseName")
//									.item(0).getTextContent();
//
//							domesticPartnerNameChk = eElement.getElementsByTagName("RegisteredDomesticPartnerNameChk")
//									.item(0).getTextContent();
//							domesticPartnerName = eElement
//									.getElementsByTagName("RegisteredDomesticPartnerName").item(0)
//									.getTextContent();
//							dependentChildNameChk = eElement.getElementsByTagName("DependentChildNameChk")
//									.item(0).getTextContent();
//							dependentChildName = eElement.getElementsByTagName("DependentChildName")
//									.item(0).getTextContent();
//							childNameChk = eElement.getElementsByTagName("ChildNameChk")
//									.item(0).getTextContent();
//							childName = eElement.getElementsByTagName("ChildName")
//									.item(0).getTextContent();
//							cobraBegin = eElement.getElementsByTagName("CobraBegin")
//									.item(0).getTextContent();
//							cobraEnd = eElement.getElementsByTagName("CobraEnds")
//									.item(0).getTextContent();
//							Year = eElement.getElementsByTagName("HCRAYear")
//									.item(0).getTextContent();
//							healthPlan2 = eElement.getElementsByTagName("HealthPlan2")
//									.item(0).getTextContent();
//							healthPlanPrice = eElement.getElementsByTagName("HealthPlanPrice")
//									.item(0).getTextContent();
//							dentalPlan2 = eElement.getElementsByTagName("DentalPlan2")
//									.item(0).getTextContent();
//							dentalPlanPrice = eElement.getElementsByTagName("DentalPlanPrice")
//									.item(0).getTextContent();
//							vision2 = eElement.getElementsByTagName("Vision2")
//									.item(0).getTextContent();
//							visionPrice = eElement.getElementsByTagName("VisionPrice")
//									.item(0).getTextContent();
//							hcra2 = eElement.getElementsByTagName("HCRA2")
//									.item(0).getTextContent();
//							hcraPrice = eElement.getElementsByTagName("HCRAPrice")
//									.item(0).getTextContent();
//							coment1 = eElement.getElementsByTagName("comment1")
//									.item(0).getTextContent();							
//							coment2 = eElement.getElementsByTagName("comment2")
//									.item(0).getTextContent();
//							comment = eElement.getElementsByTagName("Comments")
//									.item(0).getTextContent();
//							contact = eElement.getElementsByTagName("Contacts")
//									.item(0).getTextContent();
//							replyBy = eElement.getElementsByTagName("ReplyBy")
//									.item(0).getTextContent();
//							plan1 = eElement.getElementsByTagName("ThePlan1")
//									.item(0).getTextContent();
//							dependentName1 = eElement.getElementsByTagName("DependentName1")
//									.item(0).getTextContent();
//							dob1 = eElement.getElementsByTagName("DOB1")
//									.item(0).getTextContent();
//							relationshipToEmp1 = eElement.getElementsByTagName("RelationshipToEmp1")
//									.item(0).getTextContent();
//							ssn1 = eElement.getElementsByTagName("SSN1")
//									.item(0).getTextContent();
//							dependentName2 = eElement.getElementsByTagName("DependentName2")
//									.item(0).getTextContent();
//							dob2 = eElement.getElementsByTagName("DOB2")
//									.item(0).getTextContent();
//							relationshipToEmp2 = eElement.getElementsByTagName("RelationshipToEmp2")
//									.item(0).getTextContent();
//							ssn2 = eElement.getElementsByTagName("SSN2")
//									.item(0).getTextContent();
//							dependentName3 = eElement.getElementsByTagName("DependentName3")
//									.item(0).getTextContent();
//							dob3 = eElement.getElementsByTagName("DOB3")
//									.item(0).getTextContent();
//							relationshipToEmp3 = eElement.getElementsByTagName("RelationshipToEmp3")
//									.item(0).getTextContent();
//							ssn3 = eElement.getElementsByTagName("SSN3")
//									.item(0).getTextContent();
//							dependentName4 = eElement.getElementsByTagName("DependentName4")
//									.item(0).getTextContent();
//							dob4 = eElement.getElementsByTagName("DOB4")
//									.item(0).getTextContent();
//							relationshipToEmp4 = eElement.getElementsByTagName("RelationshipToEmp4")
//									.item(0).getTextContent();
//							ssn4 = eElement.getElementsByTagName("SSN4")
//									.item(0).getTextContent();
//							dependentName5 = eElement.getElementsByTagName("DependentName5")
//									.item(0).getTextContent();
//							dob5 = eElement.getElementsByTagName("DOB5")
//									.item(0).getTextContent();
//							relationshipToEmp5 = eElement.getElementsByTagName("RelationshipToEmp5")
//									.item(0).getTextContent();
//							ssn5 = eElement.getElementsByTagName("SSN5")
//									.item(0).getTextContent();
//							healthChk = eElement.getElementsByTagName("chkHealth")
//									.item(0).getTextContent();
//							healthPlan4 = eElement.getElementsByTagName("HealthPlan4")
//									.item(0).getTextContent();
//							VisionChk = eElement.getElementsByTagName("chkVision")
//									.item(0).getTextContent();
//							vision4 = eElement.getElementsByTagName("Vision4")
//									.item(0).getTextContent();
//							declineChk = eElement.getElementsByTagName("chkDecline")
//									.item(0).getTextContent();
//							decline = eElement.getElementsByTagName("Decline")
//									.item(0).getTextContent();
//							Dentalchk = eElement.getElementsByTagName("chkDental")
//									.item(0).getTextContent();
//							dentalPlan4 = eElement.getElementsByTagName("DentalPlan4")
//									.item(0).getTextContent();
//							health1chk = eElement.getElementsByTagName("chkHealth1")
//									.item(0).getTextContent();
//							hcra4 = eElement.getElementsByTagName("HCRA4")
//									.item(0).getTextContent();
//							medicarePartAYes = eElement.getElementsByTagName("MedicarePartAEntitledYes")
//									.item(0).getTextContent();
//							medicarePartANo = eElement.getElementsByTagName("MedicarePartAEntitledNo")
//									.item(0).getTextContent();
//							medicareentitlement = eElement.getElementsByTagName("MedicareEntitlement")
//									.item(0).getTextContent();
//							signature = eElement.getElementsByTagName("Signature")
//									.item(0).getTextContent();
//							electionDate = eElement.getElementsByTagName("ElectionDate")
//									.item(0).getTextContent();
//							printName = eElement.getElementsByTagName("PrintName")
//									.item(0).getTextContent();
//							relationToIndividual = eElement.getElementsByTagName("RelationshipToIndividual")
//									.item(0).getTextContent();
//							printAdd = eElement.getElementsByTagName("PrintAddress")
//									.item(0).getTextContent();
//							telNumber = eElement.getElementsByTagName("TelephoneNumber")
//									.item(0).getTextContent();							
//							meidcalContact = eElement.getElementsByTagName("MedicalContactInfo")
//									.item(0).getTextContent();							
						}					
					}	

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("EMPL_ID", empId);
				
					dataMap.put("LAST_NAME", lastName);
				
					dataMap.put("FIRST_NAME", firstName);
			
					dataMap.put("EMPL_RCD", empRCD);
	
//					dataMap.put("DATE_INITIATED", Date.valueOf(date));
//					log.info(date+"Added into the database");
					dataMap.put("STREET_ADDRESS", Address);
					log.info(Address+"Added into the database");
					dataMap.put("CITY", city);
					log.info(city+"Added into the database");
					dataMap.put("STATE", state);
					log.info(state+"Added into the database");
					dataMap.put("ZIP", zip);
					log.info(zip+"Added into the database");
//					dataMap.put("THE_PLAN", Plan);
//					dataMap.put("END_OF_COBRA_COVERAGE_DATE", Date.valueOf(CobraCoverageDate));
//					dataMap.put("END_OF_EMPLOYMENT_CHK", endOfEmployeementChk);
//					dataMap.put("END_OF_EMPLOYMENT_DATE", Date.valueOf(endOfEmployeementdate));
//					dataMap.put("REDUCTION_HOURS_CHK", reductionHourschk);
//					dataMap.put("REDUCTION_HOURS_DATE", Date.valueOf(reductionHourdate));
//					dataMap.put("DEATH_OF_EMPLOYEE_CHK", DeathOfEmployee);
//					dataMap.put("DEATH_OF_EMPLOYEE_DATE", Date.valueOf(EmployeeDeath));
//					dataMap.put("DIVORSE_CHK", Date.valueOf(Divorse));
//					dataMap.put("DIVORSE_DATE", Date.valueOf(dateOfDivorse));
//					dataMap.put("LOSS_OF_DEPCHILD_STATUS_CHK", lossOfDepChild);
//					dataMap.put("LOSS_OF_DEPCHILD_STATUS_DATE", Date.valueOf(DepChildStatus));
//					dataMap.put("DISSOLUTION_CHK", dissolutionChk);
//					dataMap.put("DISSOLUTION_DATE", Date.valueOf(dissolutionDate));
//					dataMap.put("MONTHS", Months);
//					dataMap.put("EMPLOYEE_NAME_CHK", EmployeeName1Chk);
//					dataMap.put("EMPLOYEE_NAME_NAME", EmployeeName1);
//					dataMap.put("SPOUSE_NAME_CHK", spouseNameChk);
//					dataMap.put("SPOUSE_NAME", spouseName);
//					dataMap.put("REGISTERED_DOMESTIC_PARTNER_NAME_CHK", domesticPartnerNameChk);
//					dataMap.put("REDUCTION_HOURS_NAME", domesticPartnerName);
//					dataMap.put("DEPENDENT_CHILD_NAME_CHK", dependentChildNameChk);
//					dataMap.put("DEPENDENT_CHILD_NAME", dependentChildName);
//					dataMap.put("CHILD_NAME_CHK", childNameChk);
//					dataMap.put("CHILD_NAME", childName);
//					dataMap.put("COBRA_BEGIN", Date.valueOf(cobraBegin));
//					dataMap.put("COBRA_ENDS", Date.valueOf(cobraEnd));
//					dataMap.put("HCRA_YEAR", Year);
//					dataMap.put("HEALTH_PLAN2", healthPlan2);
//					dataMap.put("HEALTH_PLAN_PRICE", healthPlanPrice);
//					dataMap.put("DENTAL_PLAN2", dentalPlan2);
//					dataMap.put("DENTAL_PLAN_PRICE", dentalPlanPrice);
//					dataMap.put("VISION2", vision2);
//					dataMap.put("VISION_PRICE", visionPrice);
//					dataMap.put("HCRA2", hcra2);
//					dataMap.put("HCRA_PRICE", hcraPrice);
//					dataMap.put("COMMENT1", coment1);
//					dataMap.put("COMMENT2", coment2);
//					dataMap.put("COMMENTS", comment);
//					dataMap.put("CONTACTS", contact);
//					dataMap.put("REPLY_BY", replyBy);
//					dataMap.put("THE_PLAN1", plan1);
//					dataMap.put("DEPENDENT_NAME1", dependentName1);
//					dataMap.put("DOB1", Date.valueOf(dob1));
//					dataMap.put("RELATIONSHIP_TO_EMP2", relationshipToEmp1);
//					dataMap.put("SSN1", ssn1);
//					dataMap.put("DEPENDENT_NAME2", dependentName2);
//					dataMap.put("DOB2", Date.valueOf(dob2));
//					dataMap.put("RELATIONSHIP_TO_EMP2", relationshipToEmp2);
//					dataMap.put("SSN2", ssn2);
//					dataMap.put("DEPENDENT_NAME3", dependentName3);
//					dataMap.put("DOB3", Date.valueOf(dob3));
//					dataMap.put("RELATIONSHIP_TO_EMP3", relationshipToEmp3);
//					dataMap.put("SSN3", ssn3);
//					dataMap.put("DEPENDENT_NAME4", dependentName4);
//					dataMap.put("DOB4", Date.valueOf(dob4));
//					dataMap.put("RELATIONSHIP_TO_EMP4", relationshipToEmp4);
//					dataMap.put("SSN4", ssn4);
//					dataMap.put("DEPENDENT_NAME5", dependentName5);
//					dataMap.put("DOB5", Date.valueOf(dob5));
//					dataMap.put("RELATIONSHIP_TO_EMP5", relationshipToEmp5);
//					dataMap.put("SSN5", ssn5);
//					dataMap.put("CHK_HEALTH", healthChk);
//					dataMap.put("HEALTH_PLAN4", healthPlan4);
//					dataMap.put("CHK_VISION", VisionChk);
//					dataMap.put("VISION4", vision4);
//					dataMap.put("CHK_DECLINE", declineChk);
//					dataMap.put("DECLINE", decline);
//					dataMap.put("CHK_DENTAL", Dentalchk);
//					dataMap.put("DETANL_PLAN4", dentalPlan4);
//					dataMap.put("CHK_HEALTH1", health1chk);
//					dataMap.put("HCRA4", hcra4);					
//					dataMap.put("MEDICARE_PART_ENTITLED_YES_CHK", medicarePartAYes);
//					dataMap.put("MEDICARE_PART_ENTITLED_NO_CHK", medicarePartANo);
//					dataMap.put("MEDICARE_ENTITLEMENT", medicareentitlement);
//					dataMap.put("SIGNATURE", signature);
//					dataMap.put("ELECTION_DATE", Date.valueOf(electionDate));
//					dataMap.put("PRINT_NAME", printName);
//					dataMap.put("RELATIONSHIP_TO_INDIVIDUAL", relationToIndividual);
//					dataMap.put("PRINT_ADDRESS", printAdd);
//					dataMap.put("TELEPHONE_NUMBER", telNumber);
//					dataMap.put("MEDICAL_CONTACT_INFO", meidcalContact);
					

//					Object vpDateObj = null;
//					if (vpDate != null && vpDate != "") {
//						Date vpDateNew = Date.valueOf(vpDate);
//						vpDateObj = vpDateNew;
//					}
//					dataMap.put("VPDATE", vpDateObj);
//					dataMap.put("VPCB", vpCB);
//					dataMap.put("HR_DATE", Date.valueOf(hrDate));
//					dataMap.put("INITIALS", initials);
//					dataMap.put("HRCB", hrCB);

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
			insertCobraFinalNoticeData(conn, dataMap);
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

	public void insertCobraFinalNoticeData(Connection conn,
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
			String tableName = "AEM_COBRA_FINAL_NOTICE";
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
