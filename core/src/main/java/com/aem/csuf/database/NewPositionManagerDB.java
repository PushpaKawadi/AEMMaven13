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

		String reqNo = "";
		String initials = "";
		String hrDate = "";
		String approvedClassification = "";		
		String mppCode = "";
		String hrTitle = "";
		String empId = "";
		String firstName = "";
		String lastName = "";
		String empRCD = "";
		String cmsPosition = "";
		String classification = "";
		String scoPosition = "";
		String range = "";
		String department = "";
		String deptID = "";
		String cbid = "";
		String workingTitle = "";
		String appointmentType = "";
		String supervisor = "";
		String dateInitiated = "";
		String timeBaseRB = "";
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

							reqNo = eElement.getElementsByTagName("ReqNo")
									.item(0).getTextContent();
							initials = eElement
									.getElementsByTagName("Initials")
									.item(0).getTextContent();
							hrDate = eElement
									.getElementsByTagName("HRDate")
									.item(0).getTextContent();
							approvedClassification = eElement.getElementsByTagName("ApprovedClassification")
									.item(0).getTextContent();
							mppCode = eElement.getElementsByTagName("MPPCode")
									.item(0).getTextContent();
							hrTitle = eElement
									.getElementsByTagName("HRWorkingTitle").item(0)
									.getTextContent();
							empId = eElement
									.getElementsByTagName("EmplID").item(0)
									.getTextContent();

							firstName = eElement
									.getElementsByTagName("IncumbentFirstName")
									.item(0).getTextContent();

							lastName = eElement
									.getElementsByTagName("IncumbentLastName")
									.item(0).getTextContent();
							empRCD = eElement
									.getElementsByTagName("EmplRCD")
									.item(0).getTextContent();

							cmsPosition = eElement
									.getElementsByTagName("CMSPosition")
									.item(0).getTextContent();

							classification = eElement
									.getElementsByTagName("Classification")
									.item(0).getTextContent();
							scoPosition = eElement
									.getElementsByTagName("SCOPosition")
									.item(0).getTextContent();

							range = eElement
									.getElementsByTagName("Range")
									.item(0).getTextContent();

							department = eElement
									.getElementsByTagName("Department")
									.item(0).getTextContent();

							deptID = eElement
									.getElementsByTagName("DeptID").item(0)
									.getTextContent();

							cbid = eElement
									.getElementsByTagName("CBID")
									.item(0).getTextContent();
							workingTitle = eElement
									.getElementsByTagName("WorkingTitle")
									.item(0).getTextContent();
							appointmentType = eElement
									.getElementsByTagName("AppointmentType")
									.item(0).getTextContent();
							supervisor = eElement.getElementsByTagName("ManagementSupervisor")
									.item(0).getTextContent();

							dateInitiated = eElement
									.getElementsByTagName("DateInitiated").item(0)
									.getTextContent();
							timeBaseRB = eElement
									.getElementsByTagName("TimebaseRB").item(0)
									.getTextContent();
							appropriateAdmin = eElement
									.getElementsByTagName("AppropriateAdministrator").item(0)
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
							explanation7 = eElement
									.getElementsByTagName("Explanation7").item(0)
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
							deptHead = eElement.getElementsByTagName("DeptHead")
									.item(0).getTextContent();							
							deptHeadDate = eElement.getElementsByTagName("DeptHeadDate")
									.item(0).getTextContent();
							incumbent = eElement.getElementsByTagName("Incumbent")
									.item(0).getTextContent();
							incumbentDate = eElement.getElementsByTagName("IncumbentDate")
									.item(0).getTextContent();
							adminDate = eElement.getElementsByTagName("AdminDate")
									.item(0).getTextContent();
							deptHiringManager = eElement.getElementsByTagName("DeptHiringManager")
									.item(0).getTextContent();
							deptDate = eElement.getElementsByTagName("DeptDate")
									.item(0).getTextContent();
							vp = eElement.getElementsByTagName("VP")
									.item(0).getTextContent();
							vpDate = eElement.getElementsByTagName("VPDate")
									.item(0).getTextContent();							
							readCheck = eElement.getElementsByTagName("ReadCheck")
									.item(0).getTextContent();
							
							}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("EREQ_NO", reqNo);
					dataMap.put("INITIALS", initials);
					dataMap.put("HR_DATE", Date.valueOf(hrDate));
					dataMap.put("EMPL_RCD", approvedClassification);
					dataMap.put("EXTENSION", mppCode);
					dataMap.put("SCO_POSITION_NUMBER", hrTitle);
					dataMap.put("TIMEBASE", empId);
					dataMap.put("STATUS_MENU", firstName);
					dataMap.put("CBID", lastName);
					dataMap.put("CLASSIFICATION", empRCD);
					dataMap.put("GRADE", cmsPosition);
					dataMap.put("CMS_POSITION_NUMBER", classification);
					dataMap.put("DEPARTMENT_NAME", scoPosition);
					dataMap.put("DEPARTMENT_ID", range);
					dataMap.put("10_12or11_12_REQUEST", department);
					dataMap.put("PLAN_SELECTED", deptID);
					dataMap.put("NAME1", cbid);
					dataMap.put("MONTHOFF1", workingTitle);
					dataMap.put("MONTHOFF2", appointmentType);
					dataMap.put("EMP_DATE", supervisor);
					dataMap.put("ADMIN_SIGN", Date.valueOf(dateInitiated));
					dataMap.put("APPROVAL_RECOMMENDED_YES", timeBaseRB);
					dataMap.put("APPROPRIATE_ADMIN_NAME", appropriateAdmin);
					dataMap.put("DATE1", explanation1);
					dataMap.put("APPROVAL_GRANTED_YES", explanation2);
					dataMap.put("VP_SIGNATURE", explanation3);
					dataMap.put("DATE2", explanation4);
					dataMap.put("ON_CYCLE", explanation5);
					dataMap.put("OFF_CYCLE", explanation6);
					dataMap.put("CURRENT_MONTHLY_SALARY", explanation7);
					dataMap.put("ADJUSTED_SALARY", explanation8);
					dataMap.put("DATE_DISCUSSED", explanation9);
					dataMap.put("PAYPLAN10", complianceYes);
					dataMap.put("PAYPLAN11", complianceNo);
					dataMap.put("START_DATE", decisionYes);
					dataMap.put("MONTH_SAL", decisionNo);
					dataMap.put("DAYS_TO_WORK", bendingRB);
					dataMap.put("POSSIBLE_WORK_DAYS", walkingRB);
					dataMap.put("ANNUAL_SALARY", squattingRB);
					dataMap.put("MONTH_SAL1", crawlingRB);
					dataMap.put("MONTHS_TO_WORK", liftingRB);
					dataMap.put("PROJECTED_EARNED_SALARY", kneelingRB);
					dataMap.put("ANNUAL_SALARY1", climbingRB);
					dataMap.put("PROJECTED_EARNED_SALARY1", graspingRB);
					dataMap.put("SETTLEMENT_AMOUNT", pushingRB);
					dataMap.put("1ST_MONTH_OFF", movementsRB);
					dataMap.put("2ND_MONTH_OFF", reachingOverheadRB);
					dataMap.put("START_DATE", discriminateColrsRB);
					dataMap.put("MONTH_SAL", auditoryRequirements);
					dataMap.put("DAYS_TO_WORK", balancing);
					dataMap.put("POSSIBLE_WORK_DAYS", wearingRespiratorRB);
					dataMap.put("ANNUAL_SALARY", sittingRB);
					dataMap.put("MONTH_SAL1", driverRB);
					dataMap.put("MONTHS_TO_WORK", standingRB);
					dataMap.put("PROJECTED_EARNED_SALARY", deptHead);
					dataMap.put("ANNUAL_SALARY1", deptHeadDate);
					dataMap.put("PROJECTED_EARNED_SALARY1", incumbent);
					dataMap.put("SETTLEMENT_AMOUNT", incumbentDate);
					dataMap.put("1ST_MONTH_OFF", adminDate);
					dataMap.put("2ND_MONTH_OFF", deptHiringManager);
					dataMap.put("PROJECTED_EARNED_SALARY1", deptDate);
					dataMap.put("SETTLEMENT_AMOUNT", vp);
					dataMap.put("1ST_MONTH_OFF", vpDate);
					dataMap.put("2ND_MONTH_OFF", readCheck);
					

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
			insertMppAdminData(conn, dataMap);
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

	public void insertMppAdminData(Connection conn,
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
			String tableName = "NewPositionManagerDB";
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
