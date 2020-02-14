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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Student Perf Evaluation Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=StudentPerfEvalSave" })
public class CSUFStudentPerfEvalDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFStudentPerfEvalDB.class);
	
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

		String empId = "";
		String empRCD = "";
		String posClassification = "";
		String fromDate = "";
		String toDate = "";
		String dateInitiated = "";
		String choiceList = "";
		String supervisor = "";
		String firstName = "";
		String lastName = "";
		String midName = "";
		String hireDate = "";
		String deptName = "";
		String room = "";
		String extension = "";
		String attendanceRating = "";
		String punctualityRating = "";
		String neatnessRating = "";
		String ipSkillRating1 = "";
		String ipSkillRating2 = "";
		String ipSkillRating3 = "";
		String quickLearnRating = "";
		String jobResposibilityRating = "";
		String workCompletionRating = "";
		String followRating = "";
		String conscientiousRating = "";
		String co_OperateRating = "";
		String correctionRating = "";
		String resposiblerating = "";
		String overallRatingComment = "";
		String recomMeritSalaryInc = "";
		String dontRecommendSalaryInc = "";
		String recomThreeMonthExt = "";
		String studEmpReEvalDate = "";
//		String supervisorSign = "";
//		String supervisorDate = "";
//		String deptSign = "";
//		String deptSignDate = "";
//		String studentEmpSign = "";
//		String studentEmpDate = "";
		String effectiveDate = "";

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
							empId = eElement.getElementsByTagName("EmpId").item(0).getTextContent();
							posClassification = eElement.getElementsByTagName("PositionClassification").item(0)
									.getTextContent();
							fromDate = eElement.getElementsByTagName("FromDate").item(0).getTextContent();
							toDate = eElement.getElementsByTagName("ToDate").item(0).getTextContent();
							dateInitiated = eElement.getElementsByTagName("DateInitiated").item(0).getTextContent();
							choiceList = eElement.getElementsByTagName("ChoiceList").item(0).getTextContent();
							supervisor = eElement.getElementsByTagName("Supervisorname").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
							lastName = eElement.getElementsByTagName("Lastname").item(0).getTextContent();
							midName = eElement.getElementsByTagName("MiddleName").item(0).getTextContent();
							hireDate = eElement.getElementsByTagName("HireDate").item(0).getTextContent();
							deptName = eElement.getElementsByTagName("Department").item(0).getTextContent();
							room = eElement.getElementsByTagName("Room").item(0).getTextContent();
							extension = eElement.getElementsByTagName("Extension").item(0).getTextContent();
							attendanceRating = eElement.getElementsByTagName("Attendance").item(0).getTextContent();
							punctualityRating = eElement.getElementsByTagName("Punctuality").item(0).getTextContent();
							neatnessRating = eElement.getElementsByTagName("Neatness").item(0).getTextContent();
							ipSkillRating1 = eElement.getElementsByTagName("WithPublic").item(0).getTextContent();
							ipSkillRating2 = eElement.getElementsByTagName("WithSupervisor").item(0).getTextContent();
							ipSkillRating3 = eElement.getElementsByTagName("WithCoWorker").item(0).getTextContent();
							quickLearnRating = eElement.getElementsByTagName("LearnQuick").item(0).getTextContent();
							jobResposibilityRating = eElement.getElementsByTagName("JobResponsibility").item(0)
									.getTextContent();
							workCompletionRating = eElement.getElementsByTagName("quanityWorkComplete").item(0)
									.getTextContent();
							followRating = eElement.getElementsByTagName("FollowDirection").item(0).getTextContent();
							conscientiousRating = eElement.getElementsByTagName("Conscientious").item(0)
									.getTextContent();
							co_OperateRating = eElement.getElementsByTagName("Co-operativie").item(0).getTextContent();
							correctionRating = eElement.getElementsByTagName("AcceptCorrection").item(0)
									.getTextContent();
							resposiblerating = eElement.getElementsByTagName("Responsible").item(0).getTextContent();
							overallRatingComment = eElement.getElementsByTagName("OverallComment").item(0)
									.getTextContent();
							recomMeritSalaryInc = eElement.getElementsByTagName("RecomMeritSalaryInc").item(0)
									.getTextContent();
							dontRecommendSalaryInc = eElement.getElementsByTagName("DontRecommendSalaryInc").item(0)
									.getTextContent();
							recomThreeMonthExt = eElement.getElementsByTagName("RecomThreeMonthExt").item(0)
									.getTextContent();
							studEmpReEvalDate = eElement.getElementsByTagName("StudEmpReEvalDate").item(0)
									.getTextContent();
//							supervisorSign = eElement.getElementsByTagName("SupervisorSign").item(0).getTextContent();
//							supervisorDate = eElement.getElementsByTagName("SupervisorDate").item(0).getTextContent();
//							DeptSign = eElement.getElementsByTagName("DeptSign").item(0).getTextContent();
//							DeptSignDate = eElement.getElementsByTagName("DeptSignDate").item(0).getTextContent();
//							studentEmpSign = eElement.getElementsByTagName("StudentEmpSign").item(0).getTextContent();
//							studentEmpDate = eElement.getElementsByTagName("StudentEmpdate").item(0).getTextContent();
							effectiveDate = eElement.getElementsByTagName("EffectiveDate").item(0).getTextContent();
						}
					}

					dataMap = new LinkedHashMap<String, Object>();

					Object initiatedDtObj = null;
					if (dateInitiated != null && dateInitiated != "") {
						Date initiatedDateNew = Date.valueOf(dateInitiated);
						initiatedDtObj = initiatedDateNew;
					}
					dataMap.put("INITIATED_DT", initiatedDtObj);
					dataMap.put("CHOICELIST", choiceList);
					dataMap.put("EMPLID", empId);
					dataMap.put("FIRSTNAME", firstName);
					dataMap.put("LASTNAME", lastName);
					dataMap.put("MIDNAME", midName);
					dataMap.put("CLASSIFICATION", posClassification);
					Object hiredateObj = null;
					if (hireDate != null && hireDate != "") {
						Date hireDateNew = Date.valueOf(hireDate);
						hiredateObj = hireDateNew;
					}
					dataMap.put("HIRE_DT", hiredateObj);
					Object fromDtObj = null;
					if (fromDate != null && fromDate != "") {
						Date fromDtNew = Date.valueOf(fromDate);
						fromDtObj = fromDtNew;
					}
					dataMap.put("FROM_DT", fromDtObj);
					Object toDtObj = null;
					if (toDate != null && toDate != "") {
						Date toDtNew = Date.valueOf(toDate);
						toDtObj = toDtNew;
					}
					dataMap.put("TO_DT", toDtObj);
					dataMap.put("SUPERVISORNAME", supervisor);
					dataMap.put("DEPARTMENT", deptName);

					dataMap.put("ROOM", room);

					dataMap.put("EXTENSION", extension);
					dataMap.put("ATTENDANCE_RATING", attendanceRating);
					dataMap.put("PUNCTUALITY_RATING", punctualityRating);
					dataMap.put("NEATNESS_RATING", neatnessRating);
					dataMap.put("WITHPUBLIC_RATING", ipSkillRating1);
					dataMap.put("WITHSUPERVISOR_RATING", ipSkillRating2);
					dataMap.put("WITHCOWORKER_RATING", ipSkillRating3);
					dataMap.put("JOBRESPONSIBILITY_RATING", jobResposibilityRating);
					dataMap.put("LEARNQUICK_RATING", quickLearnRating);
					dataMap.put("QUANITYOFWORK_RATING", workCompletionRating);
					dataMap.put("FOLLOWDIRECTION_RATING", followRating);
					dataMap.put("CONSCIENTIOUS_RATING", conscientiousRating);
					dataMap.put("CO_OPERATIVITY_RATING", co_OperateRating);
					dataMap.put("ACCEPTCORRECTION_RATING", correctionRating);
					dataMap.put("RESPONSIBLE_RATING", resposiblerating);
					dataMap.put("OVERALLCOMMENT", overallRatingComment);
					dataMap.put("RECOM_INC", recomMeritSalaryInc);
					dataMap.put("DONOT_RECOM_INC", dontRecommendSalaryInc);
					dataMap.put("RECOM_EXTENSION", recomThreeMonthExt);
					Object effectiveDtObj = null;
					if (effectiveDate != null && effectiveDate != "") {
						Date effectiveDateNew = Date.valueOf(effectiveDate);
						effectiveDtObj = effectiveDateNew;
					}
					dataMap.put("EFFECTIVE_DATE", effectiveDtObj);
					Object reEvalDtObj = null;
					if (studEmpReEvalDate != null && studEmpReEvalDate != "") {
						Date reEvalDateNew = Date.valueOf(studEmpReEvalDate);
						reEvalDtObj = reEvalDateNew;
					}

					dataMap.put("RE_EVAL_DATE", reEvalDtObj);

					log.error("Datamap Size=" + dataMap.size());

				} catch (SAXException e) {
					log.error("SAXException=" + e.getMessage());
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

	public void insertSPEData(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_STUDENT_PERF_EVAL";
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
}
