package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

//import com.adobe.aemfd.docmanager.Document;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.community.util.ConfigManager;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Course1",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=Save Student Course DB" })
public class SaveCourseWithdrawal implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(SaveCourseWithdrawal.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;
		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		LinkedHashMap<String, Object> dataMap = null;
		Document doc = null;
		InputStream is = null;
		String firstName = null;
		String lastName = null;
		String encodedPDF = null;
		String studentID = null;
		String caseID = null;
		String major = null;
		String termCode = null;
		String termDescription = null;
		String typeOfForm = null;
		String WithdrawalType = null;
		String middleName = null;
		String sID = null;
		String expGradDate = null;
		String degreeObjective = null;
		String prgPlan = null;
		String academicPlan = null;
		String telNo = null;
		String emailAddress = null;
		String internationalStudent = null;
		String eipFlag = null;
		Object expGradObj = null;

		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
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
							typeOfForm = eElement
									.getElementsByTagName("typeOfForm").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							middleName = eElement
									.getElementsByTagName("MiddleName").item(0)
									.getTextContent();
							sID = eElement.getElementsByTagName("StudentID")
									.item(0).getTextContent();
							major = eElement.getElementsByTagName("Major")
									.item(0).getTextContent();
							expGradDate = eElement
									.getElementsByTagName("ExpectedGraduDate")
									.item(0).getTextContent();

							if (expGradDate != null && expGradDate != "") {
								Date expGrad = Date.valueOf(expGradDate);
								expGradObj = expGrad;
							}
							degreeObjective = eElement
									.getElementsByTagName("DegreeObjective")
									.item(0).getTextContent();
							prgPlan = eElement
									.getElementsByTagName("ProgramPlan")
									.item(0).getTextContent();
							academicPlan = eElement
									.getElementsByTagName("AcademicPlan")
									.item(0).getTextContent();
							telNo = eElement
									.getElementsByTagName("TelephoneNo")
									.item(0).getTextContent();
							emailAddress = eElement
									.getElementsByTagName("Email").item(0)
									.getTextContent();
							internationalStudent = eElement
									.getElementsByTagName(
											"International_Students").item(0)
									.getTextContent();
							eipFlag = eElement.getElementsByTagName("EIP_Flag")
									.item(0).getTextContent();
							// lastDateAttended =
							// eElement.getElementsByTagName("LastdateAttended").item(0).getTextContent();
							// grade =
							// eElement.getElementsByTagName("Grade").item(0).getTextContent();
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("TYPE_OF_FORM", typeOfForm);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("MIDDLE_NAME", middleName);
					dataMap.put("STUDENT_ID", sID);
					dataMap.put("MAJOR", major);
					dataMap.put("EXP_GRAD_DATE", expGradObj);
					dataMap.put("DEGREE_OBJECTIVE", degreeObjective);
					dataMap.put("PROGRAM_PLAN", prgPlan);
					dataMap.put("ACADEMIC_PLAN", academicPlan);
					dataMap.put("PHONE_NO", telNo);
					dataMap.put("EMAIL_ADDRESS", emailAddress);
					dataMap.put("INTERNATIONAL_STUDENT", internationalStudent);
					dataMap.put("EIP_FLAG", eipFlag);
				} catch (SAXException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				conn = getConnection();
				if (conn != null) {
					log.error("Connection Successfull");
					insertStudentData(conn, dataMap);
				}
			}

		}

	}

	public void insertStudentData(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_STUDENT_COURSE_WITHDRAWAL";
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
}
