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
		Constants.SERVICE_DESCRIPTION + "=NewPositionManager Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=NewPositionManagerDB" })
public class NewPositionManagerDB implements WorkflowProcess{

	private static final Logger log = LoggerFactory
			.getLogger(NewPositionManagerDB.class);
	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2) throws WorkflowException {
	
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String empId = "";
		String firstName = "";
		String lastName = "";
		String empRCD = "";
		String scoPosition = "";
		String cmsPosition = "";
		String classification = "";
		String range = "";
		String department = "";
		String deptID = "";
		String workingTitle = "";
		String appointmentType = "";
		String timeBaseRB = "";
		String cbid = "";
		String dateInitiated = "";
		String supervisor = "";
		String appropriateAdmin = "";
		String explanation1 = "";
		String explanation2 = "";
		String explanation3 = "";
		String explanation4 = "";
		String explanation5 = "";
		String explanation6 = "";
		String explanation7 = "";
		String explanation8 = "";
		String explanation9 = "";
		String complianceYes = "";
		String complianceNo = "";
		String decisionYes = "";
		String decisionNo = "";
		String bendingRB = "";
		String walkingRB = "";
		String squattingRB = "";
		String unEvenWalkRB = "";
		String crawlingRB = "";
		String liftingRB = "";
		String kneelingRB = "";
		String climbingRB = "";
		String graspingRB = "";
		String pushingRB = "";
		String movementsRB = "";
		String reachingOverheadRB = "";
		String discriminateColrsRB = "";
		String auditoryRequirements = "";
		String balancing = "";
		String wearingRespiratorRB = "";
		String sittingRB = "";
		String driverRB = "";
		String standingRB = "";
		String deptHead = "";
		String deptHeadDate = "";
		String incumbent = "";
		String incumbentDate = "";
		String adminDate = "";
		String deptHiringManager = "";
		String deptDate = "";
		String vp = "";
		String vpDate = "";
		String reqNo = "";
		String initials = "";
		String hrDate = "";
		String approvedClassification = "";		
		String mppCode = "";
		String hrTitle = "";
		String readCheck = "";
		
		
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

			log.info("filePath NewPosition= " + filePath);
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("xmlFiles NewPosition=" + filePath);
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

							log.info("Inside Data.xml");
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

							empId = eElement.getElementsByTagName("EmplID").item(0)
									.getTextContent();
							log.info("The value of empID="+empId);
							
							firstName = eElement.getElementsByTagName("IncumbentFirstName")
									.item(0).getTextContent();
							log.info("The value of First Name="+firstName);

							lastName = eElement.getElementsByTagName("IncumbentLastName")
									.item(0).getTextContent();
							log.info("The value of Last Name="+lastName);
							
							empRCD = eElement.getElementsByTagName("EmplRCD")
									.item(0).getTextContent();
									log.info("The value of empRCD="+empRCD);
							
							scoPosition = eElement.getElementsByTagName("SCOPosition")
									.item(0).getTextContent();
									log.info("The value of scoPosition="+scoPosition);

							cmsPosition = eElement.getElementsByTagName("CMSPosition")
									.item(0).getTextContent();
									log.info("The value of cmsPosition="+cmsPosition);

							classification = eElement.getElementsByTagName("Classification")
									.item(0).getTextContent();
									log.info("The value of classification="+classification);


							range = eElement.getElementsByTagName("Range")
									.item(0).getTextContent();
									log.info("The value of range="+range);

							department = eElement.getElementsByTagName("Department")
									.item(0).getTextContent();
									log.info("The value of department="+department);

							deptID = eElement.getElementsByTagName("DeptID").item(0)
									.getTextContent();
									log.info("The value of deptID="+deptID);

							workingTitle = eElement.getElementsByTagName("WorkingTitle")
									.item(0).getTextContent();
									log.info("The value of workingTitle="+workingTitle);
							
							appointmentType = eElement.getElementsByTagName("AppointmentType")
									.item(0).getTextContent();
									log.info("The value AppointmentType="+appointmentType);
							
							timeBaseRB = eElement.getElementsByTagName("TimebaseRB").item(0)
									.getTextContent();
									log.info("The value TimeBase="+timeBaseRB);
							
							cbid = eElement.getElementsByTagName("CBID")
									.item(0).getTextContent();
									log.info("The value of CBID"+cbid);

							dateInitiated = eElement.getElementsByTagName("DateInitiated").item(0)
									.getTextContent();
							
							supervisor = eElement.getElementsByTagName("ManagementSupervisor")
									.item(0).getTextContent();

							appropriateAdmin = eElement.getElementsByTagName("AppropriateAdministrator").item(0)
									.getTextContent();

							explanation1 = eElement.getElementsByTagName("Explanation1")
									.item(0).getTextContent();

							explanation2 = eElement.getElementsByTagName("Explanation2")
									.item(0).getTextContent();
							explanation3 = eElement.getElementsByTagName("Explanation3")
									.item(0).getTextContent();
							explanation4	 = eElement.getElementsByTagName("Explanation4")
									.item(0).getTextContent();

							explanation5 = eElement.getElementsByTagName("Explanation5")
									.item(0).getTextContent();

							explanation6 = eElement.getElementsByTagName("Explanation6")
									.item(0).getTextContent();

							explanation7 = eElement.getElementsByTagName("Explanation7").item(0)
									.getTextContent();

							explanation8 = eElement.getElementsByTagName("Explanation8")
									.item(0).getTextContent();

							explanation9 = eElement.getElementsByTagName("Explanation9")
									.item(0).getTextContent();

							complianceYes = eElement.getElementsByTagName("ComplianceYes")
									.item(0).getTextContent();

							complianceNo = eElement.getElementsByTagName("ComplianceNo")
									.item(0).getTextContent();

							decisionYes = eElement.getElementsByTagName("DecisionYes")
									.item(0).getTextContent();

							decisionNo = eElement.getElementsByTagName("DecisionNo")
									.item(0).getTextContent();

							bendingRB = eElement.getElementsByTagName("BendingBR")
									.item(0).getTextContent();

							walkingRB = eElement.getElementsByTagName("WalkingRB")
									.item(0).getTextContent();

							squattingRB = eElement.getElementsByTagName("SquattingRB")
									.item(0).getTextContent();

							unEvenWalkRB = eElement.getElementsByTagName("UnevenWalkRB")
									.item(0).getTextContent();

							crawlingRB = eElement.getElementsByTagName("CrawlingRB")
									.item(0).getTextContent();

							liftingRB = eElement.getElementsByTagName("LiftingRB")
									.item(0).getTextContent();

							kneelingRB = eElement.getElementsByTagName("KneelingRB")
									.item(0).getTextContent();

							climbingRB = eElement.getElementsByTagName("ClimbingRB")
									.item(0).getTextContent();

							graspingRB = eElement.getElementsByTagName("GraspingRB")
									.item(0).getTextContent();

							pushingRB = eElement.getElementsByTagName("PushingRB")
									.item(0).getTextContent();

							movementsRB = eElement.getElementsByTagName("MovementsRB")
									.item(0).getTextContent();

							reachingOverheadRB = eElement.getElementsByTagName("ReachingOverheadRB")
									.item(0).getTextContent();

							discriminateColrsRB = eElement.getElementsByTagName("DiscriminateColorsRB")
									.item(0).getTextContent();

							auditoryRequirements = eElement.getElementsByTagName("AuditoryRequirements")
									.item(0).getTextContent();

							balancing = eElement.getElementsByTagName("Balancing")
									.item(0).getTextContent();

							wearingRespiratorRB = eElement.getElementsByTagName("WearingRespiratorRB")
									.item(0).getTextContent();

							sittingRB = eElement.getElementsByTagName("SittingRB")
									.item(0).getTextContent();

							driverRB = eElement.getElementsByTagName("DriverRB")
									.item(0).getTextContent();
									
							standingRB = eElement.getElementsByTagName("StandingRB")
									.item(0).getTextContent();

							incumbent = eElement.getElementsByTagName("Incumbent")
									.item(0).getTextContent();

							incumbentDate = eElement.getElementsByTagName("IncumbentDate")
									.item(0).getTextContent();

							deptHiringManager = eElement.getElementsByTagName("DeptHiringManager")
									.item(0).getTextContent();

							deptDate = eElement.getElementsByTagName("DeptDate")
									.item(0).getTextContent();

							deptHead = eElement.getElementsByTagName("DeptHead")
									.item(0).getTextContent();	

							deptHeadDate = eElement.getElementsByTagName("DeptHeadDate")
									.item(0).getTextContent();

							adminDate = eElement.getElementsByTagName("AdminDate")
									.item(0).getTextContent();

							vp = eElement.getElementsByTagName("VP")
									.item(0).getTextContent();

							vpDate = eElement.getElementsByTagName("VPDate")
									.item(0).getTextContent();	

									reqNo = eElement.getElementsByTagName("ReqNo")
									.item(0).getTextContent();	

							initials = eElement.getElementsByTagName("Initials")
									.item(0).getTextContent();	

							hrDate = eElement.getElementsByTagName("HRDate")
									.item(0).getTextContent();	

							approvedClassification = eElement.getElementsByTagName("ApprovedClassification")
									.item(0).getTextContent();	

							mppCode = eElement.getElementsByTagName("MPPCode")
									.item(0).getTextContent();	

							hrTitle = eElement.getElementsByTagName("HRWorkingTitle").item(0)
									.getTextContent();
							
							readCheck = eElement.getElementsByTagName("ReadCheck")
									.item(0).getTextContent();
//							
							}
					}

					dataMap = new LinkedHashMap<String, Object>();
			
					dataMap.put("EMP_ID", empId);		
					dataMap.put("INCUMBENT_FIRST_NAME", firstName);
					dataMap.put("INCUMBENT_LAST_NAME", lastName);
					dataMap.put("EMP_RCD", empRCD);
					dataMap.put("SCO_POSITION", scoPosition);
					dataMap.put("CMS_POSITION", cmsPosition);
					dataMap.put("CLASSIFICATION", classification);
					dataMap.put("RANGE", range);
					dataMap.put("DEPARTMENT", department);
					dataMap.put("DEPT_ID", deptID);
					dataMap.put("WORKING_TITLE", workingTitle);
					dataMap.put("APPOINTMENT_TYPE", appointmentType);
					dataMap.put("TIME_BASE_RB", timeBaseRB);
					dataMap.put("CBID", cbid);
					
					Object dateInitiatedObj= null;
					if(dateInitiated != null && dateInitiated != "") {
						Date dateInitiatedNew = Date.valueOf(dateInitiated);
						dateInitiatedObj = dateInitiatedNew;
					}
					dataMap.put("DATE_INITIATED", dateInitiatedObj);
					dataMap.put("MANAGEMENT_SUPERVISOR", supervisor);
					dataMap.put("APPROPRIATE_ADMINISTRATOR", appropriateAdmin);
					dataMap.put("EXPLANATION1", explanation1);
					dataMap.put("EXPLANATION2", explanation2);
					dataMap.put("EXPLANATION3", explanation3);
					dataMap.put("EXPLANATION4", explanation4);
					dataMap.put("EXPLANATION5", explanation5);
					dataMap.put("EXPLANATION6", explanation6);
					dataMap.put("EXPLANATION7", explanation7);
					dataMap.put("EXPLANATION8", explanation8);
					dataMap.put("EXPLANATION9", explanation9);
					dataMap.put("COMPLIANCE_YES", complianceYes);
					dataMap.put("COMPLIANCE_NO", complianceNo);
					dataMap.put("DECISION_Yes", decisionYes);
					dataMap.put("DECISION_NO", decisionNo);
					dataMap.put("BENDING_RB", bendingRB);
					dataMap.put("WALKING_RB", walkingRB);
					dataMap.put("SQUATTING_RB", squattingRB);
					dataMap.put("UNEVEN_WALK_RB", unEvenWalkRB);
					dataMap.put("CRAWLING_RB", crawlingRB);
					dataMap.put("LIFITING_RB", liftingRB);
					dataMap.put("KNEELING_RB", kneelingRB);
					dataMap.put("CLIMBING_RB", climbingRB);
					dataMap.put("GRASPING_RB", graspingRB);
					dataMap.put("PUSHING_RB", pushingRB);
					dataMap.put("MOVEMENTS_RB", movementsRB);
					dataMap.put("REACHING_OVERHEAD_RB", reachingOverheadRB);
					dataMap.put("DISCRIMINATE_COLORS_RB", discriminateColrsRB);
					dataMap.put("AUDITORY_REQUIREMENTS", auditoryRequirements);
					dataMap.put("BALANCING_RB", balancing);
					dataMap.put("WEARING_RESPIRATOR_RB", wearingRespiratorRB);
					dataMap.put("SITTING_RB", sittingRB);
					dataMap.put("DRIVER_RB", driverRB);
					dataMap.put("STANDING_RB", standingRB);
					dataMap.put("INCUMBENT", incumbent);
					
					Object incumbentDateObj= null;
					if(incumbentDate != null && incumbentDate != "") {
						Date incumbentDateNew = Date.valueOf(incumbentDate);
						incumbentDateObj = incumbentDateNew;
					}
					dataMap.put("INCUMBENT_DATE", incumbentDateObj);	
					dataMap.put("DEPT_HIRING_MANAGER", deptHiringManager);
					
					Object deptDateObj= null;
					if(deptDate != null && deptDate != "") {
						Date deptDateNew = Date.valueOf(deptDate);
						deptDateObj = deptDateNew;
					}
					dataMap.put("DEPT_DATE", deptDateObj);					
					dataMap.put("DEPT_HEAD", deptHead);
					
					Object deptHeadDateObj= null;
					if(deptHeadDate != null && deptHeadDate != "") {
						Date deptHeadDateNew = Date.valueOf(deptHeadDate);
						deptHeadDateObj = deptHeadDateNew;
					}
					dataMap.put("DEPT_HEAD_DATE", deptHeadDateObj);

					
					Object adminDateObj= null;
					if(adminDate != null && adminDate != "") {
						Date adminDateNew = Date.valueOf(adminDate);
						adminDateObj = adminDateNew;
					}
					dataMap.put("ADMIN_DATE", adminDateObj);
					dataMap.put("VP", vp);
					
					Object vpDateObj= null;
					if(vpDate != null && vpDate != "") {
						Date vpDateNew = Date.valueOf(vpDate);
						vpDateObj = vpDateNew;
					}
					dataMap.put("VP_DATE", vpDateObj);
					dataMap.put("REQ_NO", reqNo);					
					dataMap.put("INITIALS", initials);

					Object hrDateObj= null;
					if(hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}
					dataMap.put("HR_DATE", hrDateObj);
					dataMap.put("APPROVED_CLASSIFICATION", approvedClassification);
					dataMap.put("MPP_CODE", mppCode);
					dataMap.put("HR_WORKING_TITLE", hrTitle);
					dataMap.put("READ_CHECK", readCheck);


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
			insertNewPostionManagerData(conn, dataMap);
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

	public void insertNewPostionManagerData(Connection conn,
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
			String tableName = "AEM_NEW_POSITION_MANAGERS";
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
