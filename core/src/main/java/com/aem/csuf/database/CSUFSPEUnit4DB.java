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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=SPE Save in DB", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=SPEUnit4Save" })
public class CSUFSPEUnit4DB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFSPEUnit4DB.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;
		String draftDate = "";
		String empId = "";
		String empRCD = "";
		String cbid = "";
		String evaluationType = "";
		String firstName = "";
		String lastName = "";
		String classification = "";
		String range = "";
		String departmentName = "";
		String departmentID = "";
		String evaluatorsName = "";
		String evaluatorsTitle = "";
		String ratingPeriodFrom = "";
		String ratingPeriodTo = "";
		String qualityRating = "";
		String quantityRating = "";
		String judgementRating = "";
		String contributionRating = "";
		String jobStrengthComment = "";
		String comment = "";
		String progressComment = "";
		String imprComment = "";
		String programComment = "";
		String probEmpl = "";
		String overallRating = "";
		String evalCB = "";
		String evalSign = "";
		String evalComments = "";
		String evalSignDate = "";
		String empCB = "";
		String empSign = "";
		String empSignDate = "";
		String empComment = "";
		String waivePeriod = "";
		String reviewerCB = "";
		String reviewerSignDate = "";
		String reviewerName = "";
		String reviewerTitle = "";
		String reviewerSign = "";
		String reviewerComments = "";
		String hrCB = "";
		String hrDate = "";
		String hrComments = "";
		String initials = "";

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
			log.info("xmlFiles inside ");
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
							draftDate = eElement.getElementsByTagName("DraftDateGiven").item(0).getTextContent();
							empId = eElement.getElementsByTagName("EmpId").item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EmpRCD").item(0).getTextContent();
							cbid = eElement.getElementsByTagName("CBID").item(0).getTextContent();
							evaluationType = eElement.getElementsByTagName("EvaluationType").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
							lastName = eElement.getElementsByTagName("LastName").item(0).getTextContent();
							classification = eElement.getElementsByTagName("Classification").item(0).getTextContent();
							range = eElement.getElementsByTagName("Range").item(0).getTextContent();
							departmentName = eElement.getElementsByTagName("DepartmentName").item(0).getTextContent();
							departmentID = eElement.getElementsByTagName("DepartmentID").item(0).getTextContent();
							evaluatorsName = eElement.getElementsByTagName("EvalName").item(0).getTextContent();
							evaluatorsTitle = eElement.getElementsByTagName("EvalTitle").item(0).getTextContent();
							ratingPeriodFrom = eElement.getElementsByTagName("RatingPeriodFrom").item(0).getTextContent();
							ratingPeriodTo = eElement.getElementsByTagName("RatingPeriodTo").item(0).getTextContent();
							qualityRating = eElement.getElementsByTagName("Quality").item(0).getTextContent();
							quantityRating = eElement.getElementsByTagName("Quantity").item(0).getTextContent();
							contributionRating = eElement.getElementsByTagName("ContributionCampus").item(0)
									.getTextContent();
							jobStrengthComment = eElement.getElementsByTagName("JobStrengthComment").item(0)
									.getTextContent();
							comment = eElement.getElementsByTagName("SectiotnBComment").item(0).getTextContent();
							progressComment = eElement.getElementsByTagName("SectionCProgressComment").item(0).getTextContent();
							imprComment = eElement.getElementsByTagName("SectionDImprovementComment").item(0).getTextContent();
							programComment = eElement.getElementsByTagName("SectionEImprovementComment").item(0).getTextContent();
							probEmpl = eElement.getElementsByTagName("ProbEmployee").item(0).getTextContent();
							overallRating = eElement.getElementsByTagName("OverallRating").item(0).getTextContent();
							evalCB = eElement.getElementsByTagName("EvalCB").item(0).getTextContent();
							evalSign = eElement.getElementsByTagName("EvalSign").item(0).getTextContent();
							evalComments = eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent();
							evalSignDate = eElement.getElementsByTagName("EvaluationDate").item(0).getTextContent();
							empCB = eElement.getElementsByTagName("EmpCB").item(0).getTextContent();
							waivePeriod = eElement.getElementsByTagName("WaiveReviewPeriod").item(0).getTextContent();
							empSignDate = eElement.getElementsByTagName("EmpSignDate").item(0).getTextContent();
							empComment = eElement.getElementsByTagName("EmployeeComment").item(0).getTextContent();
							empSign = eElement.getElementsByTagName("EmpSign").item(0).getTextContent();
							reviewerCB = eElement.getElementsByTagName("ReviewerCB").item(0).getTextContent();
							reviewerSignDate = eElement.getElementsByTagName("ReviewerSignDate").item(0)
									.getTextContent();
							reviewerName = eElement.getElementsByTagName("ReviewerName").item(0)
									.getTextContent();
							reviewerTitle = eElement.getElementsByTagName("ReviewerTitle").item(0).getTextContent();
							reviewerSign = eElement.getElementsByTagName("ReviewerSign").item(0).getTextContent();
							reviewerComments = eElement.getElementsByTagName("ReviewerComment").item(0)
									.getTextContent();
							hrCB = eElement.getElementsByTagName("HRCB").item(0).getTextContent();
							hrDate = eElement.getElementsByTagName("HrDate").item(0).getTextContent();
							hrComments = eElement.getElementsByTagName("HRComment").item(0).getTextContent();
							initials = eElement.getElementsByTagName("Initials").item(0).getTextContent();

						}
					}
					Object draftDateGivenObj = null;
					if (draftDate != null && draftDate != "") {
						Date draftDateNew = Date.valueOf(draftDate);
						draftDateGivenObj = draftDateNew;
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("DRAFTDATE", draftDateGivenObj);
					dataMap.put("EMPLID", empId);
					dataMap.put("EMPRCD", empRCD);
					dataMap.put("CBID", cbid);
					dataMap.put("EVALUATIONTYPE", evaluationType);
					dataMap.put("FIRSTNAME", firstName);
					dataMap.put("LASTNAME", lastName);
					dataMap.put("CLASSIFICATION", classification);
					dataMap.put("EMPRANGE", range);
					dataMap.put("DEPARTMENT", departmentName);
					// Integer deptartmentId = Integer.parseInt(departmentID);
					// Object deptId = deptartmentId;
					dataMap.put("DEPARTMENTID", Integer.parseInt(departmentID));
					dataMap.put("EVALUATORSNAME", evaluatorsName);
					dataMap.put("EVALUATORSTITLE", evaluatorsTitle);
					Object reviewPeriodFromObj = null;
					if (ratingPeriodFrom != null && ratingPeriodFrom != "") {
						Date reviewPeriodFromNew = Date.valueOf(ratingPeriodFrom);
						reviewPeriodFromObj = reviewPeriodFromNew;
					}
					dataMap.put("REVIEWPERIODFROM", reviewPeriodFromObj);
					Object reviewPeriodToObj = null;
					if (ratingPeriodTo != null && ratingPeriodTo != "") {
						Date reviewPeriodToNew = Date.valueOf(ratingPeriodTo);
						reviewPeriodToObj = reviewPeriodToNew;
					}
					dataMap.put("REVIEWPERIODTO", reviewPeriodToObj);
					dataMap.put("QUALITY", qualityRating);
					dataMap.put("QUANTITY", quantityRating);
					dataMap.put("PROFESSIONALJUDGEMENT", judgementRating);
					dataMap.put("CONTRIBUTIONCAMPUS", contributionRating);
					dataMap.put("JOBSTRENGTHCOMMENT", jobStrengthComment);
					dataMap.put("COMMENTS", comment);
					dataMap.put("PROGRESSCOMMENTS", progressComment);
					dataMap.put("IMPROVEMENTSCOMMENT", imprComment);
					dataMap.put("GOALSPROGRAMSCOMMENT", programComment);
					dataMap.put("PROBEMPRB", probEmpl);
					dataMap.put("OVERALLRATING", overallRating);
					dataMap.put("EVAL_DECL_CB", evalCB);
					dataMap.put("EVALUATORSSIGNATURE", evalSign);
					dataMap.put("EVALCOMMENTS", evalComments);
					Object evalDateObj = null;
					if (evalSignDate != null && evalSignDate != "") {
						Date evalDateNew = Date.valueOf(evalSignDate);
						evalDateObj = evalDateNew;
					}

					dataMap.put("EVALSIGNDATE", evalDateObj);
					dataMap.put("EMP_DECL_CB", empCB);
					dataMap.put("WAIVEPERIOD", waivePeriod);

					Object empSignObj = null;
					if (empSignDate != null && empSignDate != "") {
						Date empDateNew = Date.valueOf(empSignDate);
						empSignObj = empDateNew;
					}
					dataMap.put("EMPSIGNDATE", empSignObj);
					dataMap.put("EMPCOMMENT", empComment);
					dataMap.put("EMPSIGN", empSign);
					
					dataMap.put("REVIEWER_DECL_CB", reviewerCB);
					Object reviewerSignObj = null;
					if (reviewerSignDate != null && reviewerSignDate != "") {
						Date reviewerDateNew = Date.valueOf(reviewerSignDate);
						reviewerSignObj = reviewerDateNew;
					}
					dataMap.put("REVIEWERSIGNDATE", reviewerSignObj);
					dataMap.put("REVIEWERNAME", reviewerName);
					dataMap.put("REVIEWERTITLE", reviewerTitle);
					dataMap.put("REVIEWEERSIGNATURE", reviewerSign);
					dataMap.put("REVIEWERCOMMENT",reviewerComments);
					dataMap.put("HR_DECL_CB", hrCB);
					Object hrDateObj = null;
					if (hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}
					dataMap.put("HRDATE", hrDateObj);
					dataMap.put("HRCOMMENT", hrComments);
					dataMap.put("HRINITIALS", initials);
					
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
			String tableName = "AEM_STAFF_PERF_EVAL_UNIT4";
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
