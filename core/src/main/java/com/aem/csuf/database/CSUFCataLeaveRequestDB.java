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
		Constants.SERVICE_DESCRIPTION + "=Catastrophic Leave Request DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFCataLeaveRequestDB" })
public class CSUFCataLeaveRequestDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CSUFCataLeaveRequestDB.class);

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
		String emplId = "";
		String empRCD = "";
		String departmentID = "";
		String departmentName = "";
		String bargainingUnit = "";
		String illnessType = "";
		String leaveType = "";
		String lenghtOfIllness = "";
		String estimatedSickLeave = "";
		String empSign = "";
		String eligibleCB = "";
		String vacationCredits = "";
		String allCredits = "";
		String nonEligibleCB = "";
		String reasons = "";
		String staffUnitSign = "";
		String staffUnitDate = "";
		String unit3Sign = "";
		String unit3Date = "";
		String empRepresentative = "";
		String department = "";
		String payroll = "";
		LinkedHashMap<String, Object> dataMap = null;
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
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							emplId = eElement.getElementsByTagName("EMPLID")
									.item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EMPLRCD")
									.item(0).getTextContent();
							departmentName = eElement
									.getElementsByTagName("DepartmentName")
									.item(0).getTextContent();
							departmentID = eElement
									.getElementsByTagName("DepartmentID")
									.item(0).getTextContent();
							bargainingUnit = eElement
									.getElementsByTagName("BargainingUnit")
									.item(0).getTextContent();
							illnessType = eElement
									.getElementsByTagName("IllnessType")
									.item(0).getTextContent();
							leaveType = eElement
									.getElementsByTagName("LeaveType").item(0)
									.getTextContent();
							lenghtOfIllness = eElement
									.getElementsByTagName("LenghtIllness")
									.item(0).getTextContent();
							estimatedSickLeave = eElement
									.getElementsByTagName("EstimatedSickLeave")
									.item(0).getTextContent();
							empSign = eElement.getElementsByTagName("EmpSign")
									.item(0).getTextContent();
							eligibleCB = eElement
									.getElementsByTagName("eligibleCB").item(0)
									.getTextContent();
							vacationCredits = eElement
									.getElementsByTagName("vacationCredits")
									.item(0).getTextContent();
							allCredits = eElement
									.getElementsByTagName("allCredits").item(0)
									.getTextContent();
							nonEligibleCB = eElement
									.getElementsByTagName("nonEligibleCB")
									.item(0).getTextContent();
							reasons = eElement.getElementsByTagName("Reasons")
									.item(0).getTextContent();
							staffUnitSign = eElement
									.getElementsByTagName("StaffUnitSign")
									.item(0).getTextContent();
							staffUnitDate = eElement
									.getElementsByTagName("StaffUnitDate")
									.item(0).getTextContent();
							unit3Sign = eElement
									.getElementsByTagName("Unit3Sign").item(0)
									.getTextContent();
							unit3Date = eElement
									.getElementsByTagName("Unit3Date").item(0)
									.getTextContent();
							empRepresentative = eElement
									.getElementsByTagName("EmpRepresentative")
									.item(0).getTextContent();
							department = eElement
									.getElementsByTagName("Department").item(0)
									.getTextContent();
							payroll = eElement.getElementsByTagName("Payroll")
									.item(0).getTextContent();
						}
					}
					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMPID", emplId);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("EMP_RCD", empRCD);
					dataMap.put("DEPT_NAME", departmentName);
					dataMap.put("DEPT_ID", departmentID);
					dataMap.put("BARGAINING_UNIT", bargainingUnit);
					dataMap.put("ILLNESS_TYPE", illnessType);
					dataMap.put("LEAVE_TYPE", leaveType);
					dataMap.put("LENGHT_OF_ILLNESS", lenghtOfIllness);
					dataMap.put("ESTIMATED_SICK_LEAVE", estimatedSickLeave);
					dataMap.put("EMP_SIGN", empSign);
					dataMap.put("ELIGIBLE", eligibleCB);
					dataMap.put("VACATION_CREDITS", vacationCredits);
					dataMap.put("ALL_CREDITS", allCredits);
					dataMap.put("NON_ELIGIBLE", nonEligibleCB);
					dataMap.put("REASONS_FOR_NON_ELIGIBLE", reasons);
					dataMap.put("STAFF_SIGN", staffUnitSign);
					
					Object staffDateObj = null;
					if (staffUnitDate != null && staffUnitDate != "") {
						Date staffDateNew = Date.valueOf(staffUnitDate);
						staffDateObj = staffDateNew;
					}
					dataMap.put("STAFF_DATE", staffDateObj);
					dataMap.put("UNIT3_SIGN", unit3Sign);
					
					Object unit3DateObj = null;
					if (unit3Date != null && unit3Date != "") {
						Date unitDateNew = Date.valueOf(unit3Date);
						unit3DateObj = unitDateNew;
					}
					dataMap.put("UNIT3_DATE",unit3DateObj);
					dataMap.put("EMP_REPRESENTATIVE", empRepresentative);
					dataMap.put("DEPARTMENT", department);
					dataMap.put("PAYROLL", payroll);
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
			insertCataLeaveDonationData(conn, dataMap);
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

	public void insertCataLeaveDonationData(Connection conn,
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
			String tableName = "CATASTROPHIC_LEAVE_DONATION";
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
			} catch (Exception e) {
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
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}

			try {
				log.info("Before Prepared stmt");
				preparedStmt.execute();
				conn.commit();
				log.info("After Prepared stmt");
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} catch (Exception e) {
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
