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
		Constants.SERVICE_DESCRIPTION + "=MPP Admin Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=MppAdminDistributedDB" })
public class MppAdminDistributedDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(MppAdminDistributedDB.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String empId = "";
		String empRCD = "";
		String cbid = "";
		String firstName = "";
		String lastName = "";
		String departmentID = "";
		String departmentName = "";
		String empSupervisor = "";
		String ratingPeriodFrom = "";
		String ratingPeriodTo = "";
		String evaluation1 = "";
		String evaluation2 = "";
		String assessment1 = "";
		String assessment2 = "";
		String evalSign = "";
		String evalDate = "";
		String evalName = "";
		String assessmentSelection = "";
		String adminName = "";
		String adminSign = "";
		String adminDate = "";
		String vpName = "";
		String vpSign = "";
		String vpDate = "";
		String initials = "";
		String hrDate = "";
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
							departmentID = eElement
									.getElementsByTagName("DeptID").item(0)
									.getTextContent();
							departmentName = eElement
									.getElementsByTagName("DeptName").item(0)
									.getTextContent();

							empSupervisor = eElement
									.getElementsByTagName("EmployeeSupervisor")
									.item(0).getTextContent();

							ratingPeriodFrom = eElement
									.getElementsByTagName("ReviewPeriodFrom")
									.item(0).getTextContent();
							ratingPeriodTo = eElement
									.getElementsByTagName("ReviewPeriodTo")
									.item(0).getTextContent();

							evaluation1 = eElement
									.getElementsByTagName("Evaluation1")
									.item(0).getTextContent();

							evaluation2 = eElement
									.getElementsByTagName("Evaluation2")
									.item(0).getTextContent();

							assessment1 = eElement
									.getElementsByTagName("Assessment1")
									.item(0).getTextContent();

							assessment2 = eElement
									.getElementsByTagName("Assessment2")
									.item(0).getTextContent();

							assessmentSelection = eElement
									.getElementsByTagName("AssementCB").item(0)
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

							adminName = eElement
									.getElementsByTagName("AdminName").item(0)
									.getTextContent();
							adminSign = eElement
									.getElementsByTagName("AdminSign").item(0)
									.getTextContent();
							adminDate = eElement
									.getElementsByTagName("AdminDate").item(0)
									.getTextContent();

							vpName = eElement.getElementsByTagName("VPName")
									.item(0).getTextContent();
							vpSign = eElement.getElementsByTagName("VPSign")
									.item(0).getTextContent();
							vpDate = eElement.getElementsByTagName("VPDate")
									.item(0).getTextContent();

							initials = eElement
									.getElementsByTagName("Initials").item(0)
									.getTextContent();
							hrDate = eElement.getElementsByTagName("HrDate")
									.item(0).getTextContent();
						}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("EMPID", empId);
					dataMap.put("LASTNAME", lastName);
					dataMap.put("FIRSTNAME", firstName);
					dataMap.put("EMPRCD", empRCD);
					dataMap.put("CBID", cbid);
					dataMap.put("DEPTNAME", departmentName);
					dataMap.put("DEPTID", departmentID);
					dataMap.put("EMPLOYEE_SUPERVISOR", empSupervisor);

					dataMap.put("REVIEW_PERIOD_FROM",
							Date.valueOf(ratingPeriodFrom));
					dataMap.put("REVIEW_PERIOD_TO", Date.valueOf(ratingPeriodTo));

					dataMap.put("EVALUATION1", evaluation1);
					dataMap.put("EVALUATION2", evaluation2);
					dataMap.put("ASSESSMENT1", assessment1);
					dataMap.put("ASSESSMENT2", assessment2);
					dataMap.put("ASSEMENT_SELECTION", assessmentSelection);

					dataMap.put("EVALUATOR_NAME", evalName);
					dataMap.put("EVALUATOR_SIGN", evalSign);
					dataMap.put("EVALUATOR_DATE", Date.valueOf(evalDate));
					

					dataMap.put("ADMIN_NAME", adminName);
					dataMap.put("ADMIN_SIGN", adminSign);
					dataMap.put("ADMIN_DATE", Date.valueOf(adminDate));					

					dataMap.put("VPNAME", vpName);
					dataMap.put("VPSIGN", vpSign);

					Object vpDateObj = null;
					if (vpDate != null && vpDate != "") {
						Date vpDateNew = Date.valueOf(vpDate);
						vpDateObj = vpDateNew;
					}
					dataMap.put("VPDATE", vpDateObj);				
					dataMap.put("HR_DATE", Date.valueOf(hrDate));
					dataMap.put("INITIALS", initials);					

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
			String tableName = "AEM_MPP_ADMIN_DISTRIBUTED";
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
