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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=MPP Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=MppPerfEvalDB" })
public class MppPerfEvalDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(MppPerfEvalDB.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
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
		String adminReview = "";
		String commentsOthers = "";
		String conceptualSkills1 = "";
		String conceptualSkills2 = "";
		String conceptualSkills3 = "";
		String conceptualSkills4 = "";
		String conceptualSkills5 = "";
		String conceptualSkills6 = "";
		String interPersonalSkills1 = "";
		String interPersonalSkills2 = "";
		String interPersonalSkills3 = "";
		String interPersonalSkills4 = "";
		String interPersonalSkills5 = "";
		String interPersonalSkills6 = "";
		String technicalSkills1 = "";
		String technicalSkills2 = "";
		String technicalSkills3 = "";
		String technicalSkills4 = "";
		String technicalSkills5 = "";
		String technicalSkills6 = "";
		String others1 = "";
		String others2 = "";
		String others3 = "";
		String others4 = "";
		String others5 = "";
		String others6 = "";
		String overallPerformance1 = "";
		String overallPerformance2 = "";
		String overallPerformance3 = "";
		String overallPerformance4 = "";
		String overallPerformance5 = "";
		String overallPerformance6 = "";
		String overallRating = "";
		String sectionBComments = "";
		String evalName = "";
		String evalDate = "";
		String adminName = "";
		String adminDate = "";
		String adminComment = "";
		String vpName = "";
		String vpSign = "";
		String vpDate = "";
		String vpComment = "";
		String vpCB = "";
		String empDate = "";
		String empComments = "";
		String atCritical = "";
		String atOptions = "";
		String supportStmt1 = "";
		String supportStmt2 = "";
		String supportStmt3 = "";
		String supportStmt4 = "";
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
							initials = eElement
									.getElementsByTagName("Initials").item(0)
									.getTextContent();
							hrDate = eElement.getElementsByTagName("HRDate")
									.item(0).getTextContent();
							hrComments = eElement
									.getElementsByTagName("HRComment").item(0)
									.getTextContent();
							empId = eElement.getElementsByTagName("EmpID")
									.item(0).getTextContent();
							lastName = eElement
									.getElementsByTagName("EmpLastName")
									.item(0).getTextContent();
							firstName = eElement
									.getElementsByTagName("EmpFirstNAme")
									.item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EmpRCD")
									.item(0).getTextContent();
							cbid = eElement.getElementsByTagName("CBID")
									.item(0).getTextContent();
							classification = eElement
									.getElementsByTagName("Classification")
									.item(0).getTextContent();
							departmentID = eElement
									.getElementsByTagName("DeptID").item(0)
									.getTextContent();
							departmentName = eElement
									.getElementsByTagName("DeptName").item(0)
									.getTextContent();
							range = eElement.getElementsByTagName("Range")
									.item(0).getTextContent();
							evaluatorsName = eElement
									.getElementsByTagName("EvaluatorName")
									.item(0).getTextContent();
							evaluationType = eElement
									.getElementsByTagName("EvaluationType")
									.item(0).getTextContent();
							ratingPeriodFrom = eElement
									.getElementsByTagName("ReviewPeriodFrom")
									.item(0).getTextContent();
							ratingPeriodTo = eElement
									.getElementsByTagName("ReviewPeriodTo")
									.item(0).getTextContent();
							adminReview = eElement
									.getElementsByTagName("AdminReview")
									.item(0).getTextContent();
							athleticsEmp = eElement
									.getElementsByTagName("AthleticEmp")
									.item(0).getTextContent();

							commentsOthers = eElement
									.getElementsByTagName("CommentsOthers")
									.item(0).getTextContent();
							conceptualSkills1 = eElement
									.getElementsByTagName("CS1").item(0)
									.getTextContent();
							conceptualSkills2 = eElement
									.getElementsByTagName("CS2").item(0)
									.getTextContent();
							conceptualSkills3 = eElement
									.getElementsByTagName("CS3").item(0)
									.getTextContent();
							conceptualSkills4 = eElement
									.getElementsByTagName("CS4").item(0)
									.getTextContent();
							conceptualSkills5 = eElement
									.getElementsByTagName("CS5").item(0)
									.getTextContent();
							conceptualSkills6 = eElement
									.getElementsByTagName("CS6").item(0)
									.getTextContent();

							interPersonalSkills1 = eElement
									.getElementsByTagName("IS1").item(0)
									.getTextContent();
							interPersonalSkills2 = eElement
									.getElementsByTagName("IS2").item(0)
									.getTextContent();
							interPersonalSkills3 = eElement
									.getElementsByTagName("IS3").item(0)
									.getTextContent();
							interPersonalSkills4 = eElement
									.getElementsByTagName("IS4").item(0)
									.getTextContent();
							interPersonalSkills5 = eElement
									.getElementsByTagName("IS5").item(0)
									.getTextContent();
							interPersonalSkills6 = eElement
									.getElementsByTagName("IS6").item(0)
									.getTextContent();

							technicalSkills1 = eElement
									.getElementsByTagName("TS1").item(0)
									.getTextContent();
							technicalSkills2 = eElement
									.getElementsByTagName("TS2").item(0)
									.getTextContent();
							technicalSkills3 = eElement
									.getElementsByTagName("TS3").item(0)
									.getTextContent();
							technicalSkills4 = eElement
									.getElementsByTagName("TS4").item(0)
									.getTextContent();
							technicalSkills5 = eElement
									.getElementsByTagName("TS5").item(0)
									.getTextContent();
							technicalSkills6 = eElement
									.getElementsByTagName("TS6").item(0)
									.getTextContent();

							others1 = eElement.getElementsByTagName("others1")
									.item(0).getTextContent();
							others2 = eElement.getElementsByTagName("others2")
									.item(0).getTextContent();
							others3 = eElement.getElementsByTagName("others3")
									.item(0).getTextContent();
							others4 = eElement.getElementsByTagName("others4")
									.item(0).getTextContent();
							others5 = eElement.getElementsByTagName("others5")
									.item(0).getTextContent();
							others6 = eElement.getElementsByTagName("others6")
									.item(0).getTextContent();

							overallPerformance1 = eElement
									.getElementsByTagName("OP1").item(0)
									.getTextContent();
							overallPerformance2 = eElement
									.getElementsByTagName("OP2").item(0)
									.getTextContent();
							overallPerformance3 = eElement
									.getElementsByTagName("OP3").item(0)
									.getTextContent();
							overallPerformance4 = eElement
									.getElementsByTagName("OP4").item(0)
									.getTextContent();
							overallPerformance5 = eElement
									.getElementsByTagName("OP5").item(0)
									.getTextContent();
							overallPerformance6 = eElement
									.getElementsByTagName("OP6").item(0)
									.getTextContent();

							overallRating = eElement
									.getElementsByTagName("OverallRating")
									.item(0).getTextContent();
							sectionBComments = eElement
									.getElementsByTagName("SectionB").item(0)
									.getTextContent();

							evalName = eElement
									.getElementsByTagName("EvaluatorNameSign")
									.item(0).getTextContent();

							evalSign = eElement
									.getElementsByTagName("EvaluatorSign")
									.item(0).getTextContent();

							evalDate = eElement
									.getElementsByTagName("EvaluatorDate")
									.item(0).getTextContent();
							evalComments = eElement
									.getElementsByTagName("EvaluatorComment")
									.item(0).getTextContent();
							evalCB = eElement.getElementsByTagName("EvalCB")
									.item(0).getTextContent();

							adminName = eElement
									.getElementsByTagName("AdminName").item(0)
									.getTextContent();
							adminSign = eElement
									.getElementsByTagName("AdminSign").item(0)
									.getTextContent();
							adminDate = eElement
									.getElementsByTagName("AdminDate").item(0)
									.getTextContent();
							adminComment = eElement
									.getElementsByTagName("AdminComment")
									.item(0).getTextContent();
							adminCB = eElement.getElementsByTagName("AdminCB")
									.item(0).getTextContent();

							vpName = eElement.getElementsByTagName("VPName")
									.item(0).getTextContent();
							vpSign = eElement.getElementsByTagName("VPSign")
									.item(0).getTextContent();
							vpDate = eElement.getElementsByTagName("VPDate")
									.item(0).getTextContent();
							vpComment = eElement
									.getElementsByTagName("VPComment").item(0)
									.getTextContent();
							vpCB = eElement.getElementsByTagName("VPCB")
									.item(0).getTextContent();

							empSign = eElement.getElementsByTagName("EmpSign")
									.item(0).getTextContent();
							empDate = eElement.getElementsByTagName("EmpDate")
									.item(0).getTextContent();

							empComments = eElement
									.getElementsByTagName("EmpComment").item(0)
									.getTextContent();
							empCB = eElement.getElementsByTagName("EmpCB")
									.item(0).getTextContent();

							atCritical = eElement
									.getElementsByTagName("AtCritical").item(0)
									.getTextContent();

							atOptions = eElement
									.getElementsByTagName("AtOptions").item(0)
									.getTextContent();

							supportStmt1 = eElement
									.getElementsByTagName("SupportDoc1")
									.item(0).getTextContent();
							supportStmt2 = eElement
									.getElementsByTagName("SupportDoc2")
									.item(0).getTextContent();
							supportStmt3 = eElement
									.getElementsByTagName("SupportDoc3")
									.item(0).getTextContent();
							supportStmt4 = eElement
									.getElementsByTagName("SupportDoc4")
									.item(0).getTextContent();

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

					dataMap.put("REVIEWPERIODFROM",
							Date.valueOf(ratingPeriodFrom));
					dataMap.put("REVIEWPERIODTO", Date.valueOf(ratingPeriodTo));
					dataMap.put("EVALUATORNAME", evaluatorsName);
					dataMap.put("EVALUATIONTYPE", evaluationType);
					dataMap.put("ADMINREVIEW", adminReview);
					dataMap.put("ATHLETICEMP", athleticsEmp);
					dataMap.put("COMMENTSOTHERS", commentsOthers);

					dataMap.put("CONCEPTUALSKILLS1", conceptualSkills1);
					dataMap.put("CONCEPTUALSKILLS2", conceptualSkills2);
					dataMap.put("CONCEPTUALSKILLS3", conceptualSkills3);
					dataMap.put("CONCEPTUALSKILLS4", conceptualSkills4);
					dataMap.put("CONCEPTUALSKILLS5", conceptualSkills5);
					dataMap.put("CONCEPTUALSKILLS6", conceptualSkills6);

					dataMap.put("INTERPERSONALSKILLS1", interPersonalSkills1);
					dataMap.put("INTERPERSONALSKILLS2", interPersonalSkills2);
					dataMap.put("INTERPERSONALSKILLS3", interPersonalSkills3);
					dataMap.put("INTERPERSONALSKILLS4", interPersonalSkills4);
					dataMap.put("INTERPERSONALSKILLS5", interPersonalSkills5);
					dataMap.put("INTERPERSONALSKILLS6", interPersonalSkills6);

					dataMap.put("TECHNICALSKILLS1", technicalSkills1);
					dataMap.put("TECHNICALSKILLS2", technicalSkills2);
					dataMap.put("TECHNICALSKILLS3", technicalSkills3);
					dataMap.put("TECHNICALSKILLS4", technicalSkills4);
					dataMap.put("TECHNICALSKILLS5", technicalSkills5);
					dataMap.put("TECHNICALSKILLS6", technicalSkills6);

					dataMap.put("OTHERS1", others1);
					dataMap.put("OTHERS2", others2);
					dataMap.put("OTHERS3", others3);
					dataMap.put("OTHERS4", others4);
					dataMap.put("OTHERS5", others5);
					dataMap.put("OTHERS6", others6);

					dataMap.put("OVERALL_PERFORMANCE1", overallPerformance1);
					dataMap.put("OVERALL_PERFORMANCE2", overallPerformance2);
					dataMap.put("OVERALL_PERFORMANCE3", overallPerformance3);
					dataMap.put("OVERALL_PERFORMANCE4", overallPerformance4);

					dataMap.put("OVERALL_PERFORMANCE5", overallPerformance5);
					dataMap.put("OVERALL_PERFORMANCE6", overallPerformance6);

					dataMap.put("OVERALLRATING", overallRating);
					dataMap.put("SECTIONBCOMMENTS", sectionBComments);

					dataMap.put("ATCRITICAL", atCritical);
					dataMap.put("ATOPTIONS", atOptions);

					dataMap.put("SUPPORTSTMT1", supportStmt1);
					dataMap.put("SUPPORTSTMT2", supportStmt2);
					dataMap.put("SUPPORTSTMT3", supportStmt3);
					dataMap.put("SUPPORTSTMT4", supportStmt4);

					dataMap.put("EVALUATORNAMESIGN", evalName);
					dataMap.put("EVALUATORSIGN", evalSign);
					dataMap.put("EVALUATORDATE", Date.valueOf(evalDate));
					dataMap.put("EVALUATORCOMMENT", evalComments);
					dataMap.put("EVALCB", evalCB);

					dataMap.put("ADMINNAME", adminName);
					dataMap.put("ADMINSIGN", adminSign);
					dataMap.put("ADMINDATE", Date.valueOf(adminDate));
					dataMap.put("ADMINCOMMENT", adminComment);
					dataMap.put("ADMINCB", adminCB);

					dataMap.put("VPNAME", vpName);
					dataMap.put("VPSIGN", vpSign);

					Object vpDateObj = null;
					if (vpDate != null && vpDate != "") {
						Date vpDateNew = Date.valueOf(draftDate);
						vpDateObj = vpDateNew;
					}

					dataMap.put("VPDATE", vpDateObj);
					dataMap.put("VPCOMMENT", vpComment);
					dataMap.put("VPCB", vpCB);

					dataMap.put("EMPSIGN", empSign);
					dataMap.put("EMPDATE", empDate);
					dataMap.put("EMPCOMMENT", empComments);
					dataMap.put("EMPCB", empCB);

					dataMap.put("HRDate", Date.valueOf(hrDate));
					dataMap.put("INITIALS", initials);
					dataMap.put("HRCOMMENT", hrComments);
					dataMap.put("HRCB", hrCB);

					log.error("Here111");

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
		} /*
		 * finally { try { if (con != null) { log.info("Conn Exec="); } } catch
		 * (Exception exp) { exp.printStackTrace(); } }
		 */
		return null;
	}

	public void insertSPEData(Connection conn,
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
			String tableName = "AEM_MPP_PERF_EVAL";
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
