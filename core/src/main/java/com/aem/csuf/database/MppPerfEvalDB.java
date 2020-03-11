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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=MPP Save in DB", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=MppPerfEvalDB" })
public class MppPerfEvalDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(MppPerfEvalDB.class);

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

		String ratingPeriodFrom = "";
		String ratingPeriodTo = "";
		String empId = "";
		String empRCD = "";
		String cbid = "";
		String classification = "";
		String range = "";
		String evaluationType = "";
		String firstName = "";
		String lastName = "";
		String departmentID = "";
		String departmentName = "";
		String draftDate = "";
		String athleticsEmp = "";
		String evaluatorsName = "";
		String evalCB = "";
		String evalSign = "";
		String evalComments = "";
		String empCB = "";
		String empSign = "";
		String adminCB = "";
		String adminSign = "";
		String hrComments = "";
		String hrCB = "";
		String initials = "";
		String hrDate = "";
		String commentsOthers = "";
		String conceptualSkills = "";
		String interPersonalSkills = "";
		String technicalSkills = "";
		String others = "";
		String overallPerformance = "";
		String overallRating = "";
		String sectionBComments = "";
		String evalName = "";
		String evalDate = "";
		String adminName = "";
		String adminDate = "";
		String adminComment = "";
		String empDate = "";
		String empComments = "";
		String atCritical = "";
		String atOptions = "";
		String supportStmt1 = "";
		String supportStmt2 = "";
		String supportStmt3 = "";
		String supportStmt4 = "";
		String hrCoordinatorSign = "";
		String hrCoordinatorSignDate = "";
		String hrCoordinatorSignComment = "";
		String workflowInstance = "";
		String hrCooCB = "";
		
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
			workflowInstance = workItem.getWorkflow().getId();
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
							initials = eElement.getElementsByTagName("HRDIInitials").item(0).getTextContent();
							hrDate = eElement.getElementsByTagName("HRDIDate").item(0).getTextContent();
							hrComments = eElement.getElementsByTagName("HRDIComment").item(0).getTextContent();
							empId = eElement.getElementsByTagName("EmpID").item(0).getTextContent();
							lastName = eElement.getElementsByTagName("EmpLastName").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("EmpFirstName").item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EmpRCD").item(0).getTextContent();
							cbid = eElement.getElementsByTagName("CBID").item(0).getTextContent();
							classification = eElement.getElementsByTagName("Classification").item(0).getTextContent();
							departmentID = eElement.getElementsByTagName("DeptID").item(0).getTextContent();
							departmentName = eElement.getElementsByTagName("DeptName").item(0).getTextContent();
							range = eElement.getElementsByTagName("Range").item(0).getTextContent();
							evaluatorsName = eElement.getElementsByTagName("EvaluatorName").item(0).getTextContent();
							evaluationType = eElement.getElementsByTagName("EvaluationType").item(0).getTextContent();
							ratingPeriodFrom = eElement.getElementsByTagName("ReviewPeriodFrom").item(0)
									.getTextContent();
							ratingPeriodTo = eElement.getElementsByTagName("ReviewPeriodTo").item(0).getTextContent();
							//adminReview = eElement.getElementsByTagName("AdminReview").item(0).getTextContent();
							athleticsEmp = eElement.getElementsByTagName("AthleticEmp").item(0).getTextContent();
							commentsOthers = eElement.getElementsByTagName("OtherRating").item(0).getTextContent();
							conceptualSkills = eElement.getElementsByTagName("ConceptualSkill").item(0)
									.getTextContent();
							interPersonalSkills = eElement.getElementsByTagName("InterpersonalSkills").item(0)
									.getTextContent();
							technicalSkills = eElement.getElementsByTagName("TechnicalSkills").item(0).getTextContent();
							others = eElement.getElementsByTagName("OtherSkills").item(0).getTextContent();
//							overallPerformance = eElement.getElementsByTagName("OverallPerformance").item(0)
//									.getTextContent();
							overallRating = eElement.getElementsByTagName("OverallRating").item(0).getTextContent();
							sectionBComments = eElement.getElementsByTagName("SectionB").item(0).getTextContent();
							evalName = eElement.getElementsByTagName("EvaluatorNameSign").item(0).getTextContent();
							evalSign = eElement.getElementsByTagName("EvaluatorSign").item(0).getTextContent();
							evalDate = eElement.getElementsByTagName("EvaluatorDate").item(0).getTextContent();
							evalComments = eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent();
							evalCB = eElement.getElementsByTagName("EvalCB").item(0).getTextContent();
							adminName = eElement.getElementsByTagName("AdminName").item(0).getTextContent();
							adminSign = eElement.getElementsByTagName("AdminSign").item(0).getTextContent();
							adminDate = eElement.getElementsByTagName("AdminDate").item(0).getTextContent();
							adminComment = eElement.getElementsByTagName("AdminComment").item(0).getTextContent();
							adminCB = eElement.getElementsByTagName("AdminCB").item(0).getTextContent();
							empSign = eElement.getElementsByTagName("EmpSign").item(0).getTextContent();
							empDate = eElement.getElementsByTagName("EmpDate").item(0).getTextContent();
							empComments = eElement.getElementsByTagName("EmpComment").item(0).getTextContent();
							empCB = eElement.getElementsByTagName("EmpCB").item(0).getTextContent();
							atCritical = eElement.getElementsByTagName("AthleticEmpImpToPos").item(0).getTextContent();
							atOptions = eElement.getElementsByTagName("AthleticsEmpRating").item(0).getTextContent();
							supportStmt1 = eElement.getElementsByTagName("SupportStatement1").item(0).getTextContent();
							supportStmt2 = eElement.getElementsByTagName("SupportStatement2").item(0).getTextContent();
							supportStmt3 = eElement.getElementsByTagName("SupportStatement3").item(0).getTextContent();
							supportStmt4 = eElement.getElementsByTagName("SupportStatement4").item(0).getTextContent();
							hrCoordinatorSign = eElement.getElementsByTagName("HRCoordinatorSign").item(0)
									.getTextContent();
							hrCoordinatorSignDate = eElement.getElementsByTagName("HRCoordinatorSignDate").item(0)
									.getTextContent();
							hrCoordinatorSignComment = eElement.getElementsByTagName("HRCoordinatorSignComment").item(0)
									.getTextContent();
							hrCB = eElement.getElementsByTagName("HRDICB").item(0)
									.getTextContent();
							hrCooCB = eElement.getElementsByTagName("HRCooCB").item(0).getTextContent();
                    log.info("val save complete");
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					
					dataMap.put("EMPID", empId);
					
					dataMap.put("LASTNAME", lastName);
					
					dataMap.put("FIRSTNAME", firstName);
					
					dataMap.put("CLASSIFICATION", classification);
					
					dataMap.put("EMPRCD", empRCD);
					
					dataMap.put("CBID", cbid);
					
					dataMap.put("DEPTNAME", departmentName);
					
					dataMap.put("RANGE", range);
					
					dataMap.put("DEPTID", departmentID);
					
					Object ratingPeriodFromObj = null;
					if (ratingPeriodFrom != null && ratingPeriodFrom != "") {
						Date ratingPeriodFromNew = Date.valueOf(ratingPeriodFrom);
						ratingPeriodFromObj = ratingPeriodFromNew;
					}
					dataMap.put("REVIEWPERIODFROM",ratingPeriodFromObj);
					Object ratingPeriodToObj = null;
					if (ratingPeriodTo != null && ratingPeriodTo != "") {
						Date ratingPeriodToNew = Date.valueOf(ratingPeriodTo);
						ratingPeriodToObj = ratingPeriodToNew;
					}
					dataMap.put("REVIEWPERIODTO", ratingPeriodToObj);
					dataMap.put("EVALUATORNAME", evaluatorsName);
					dataMap.put("EVALUATIONTYPE", evaluationType);
					dataMap.put("ATHLETICEMP", athleticsEmp);
					dataMap.put("OTHER_RATING", commentsOthers);
					dataMap.put("CONCEPTUALSKILLS", conceptualSkills);
					dataMap.put("INTERPERSONALSKILLS", interPersonalSkills);
					dataMap.put("TECHNICALSKILLS", technicalSkills);
					dataMap.put("OTHERS", others);
				//	dataMap.put("OVERALL_PERFORMANCE", overallPerformance);
					dataMap.put("OVERALLRATING", overallRating);
					dataMap.put("SECTIONBCOMMENTS", sectionBComments);
					dataMap.put("ATHLETICEMP_IMP_TO_POS", atCritical);
					dataMap.put("ATHLETICEMPRATING", atOptions);
					dataMap.put("SUPPORTSTMT1", supportStmt1);
					dataMap.put("SUPPORTSTMT2", supportStmt2);
					dataMap.put("SUPPORTSTMT3", supportStmt3);
					dataMap.put("SUPPORTSTMT4", supportStmt4);
					dataMap.put("EVALUATORNAMESIGN", evalName);
					dataMap.put("EVALUATORSIGN", evalSign);
					log.info("Till Eval Sign");
					Object evalDateObj = null;
					if (evalDate != null && evalDate != "") {
						Date evalDateNew = Date.valueOf(evalDate);
						evalDateObj = evalDateNew;
					}
					//dataMap.put("EVALUATORDATE", null);
					dataMap.put("EVALUATORDATE", evalDateObj);
					dataMap.put("EVALUATORCOMMENT", evalComments);
					log.info("Till eval comment"+evalComments);
					dataMap.put("EVALCB", evalCB);
					dataMap.put("ADMINNAME", adminName);
					dataMap.put("ADMINSIGN", adminSign);
					Object adminDateObj = null;
					if (adminDate != null && adminDate != "") {
						Date adminDateNew = Date.valueOf(adminDate);
						adminDateObj = adminDateNew;
					}
					dataMap.put("ADMINDATE", adminDateObj);
					//dataMap.put("ADMINDATE", null);
					dataMap.put("ADMINCOMMENT", adminComment);
					dataMap.put("ADMINCB", adminCB);
					dataMap.put("HRCOOCB", hrCooCB);
					dataMap.put("HRCOOSIGN", hrCoordinatorSign);
					Object hrCooDateObj = null;
					if (hrCoordinatorSignDate != null && hrCoordinatorSignDate != "") {
						Date hrCooDateNew = Date.valueOf(hrCoordinatorSignDate);
						hrCooDateObj = hrCooDateNew;
					}
					dataMap.put("HRCOODATE", hrCooDateObj);
					dataMap.put("HRCOOCOMMENT", hrCoordinatorSignComment);
					dataMap.put("EMPSIGN", empSign);
					Object empDateObj = null;
					if (empDate != null && empDate != "") {
						Date empDateNew = Date.valueOf(empDate);
						empDateObj = empDateNew;
					}
					dataMap.put("EMPDATE", empDateObj);
					dataMap.put("EMPCOMMENT", empComments);
					dataMap.put("EMPCB", empCB);
					Object hrDateObj = null;
					if (hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}
					dataMap.put("HRDATE", hrDateObj);
					dataMap.put("INITIALS", initials);
					dataMap.put("HRCOMMENT", hrComments);
					dataMap.put("HRCB", hrCB);
					dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstance);
					log.error("put complete");

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
			insertSPEData(conn, dataMap);
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
		}
		/*
		 * finally { try { if (con != null) { log.info("Conn Exec="); } } catch
		 * (Exception exp) { exp.printStackTrace(); } }
		 */
		return null;
	}

	public void insertSPEData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_MPP_PERFORMANCE_EVAL";
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