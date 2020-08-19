package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
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
import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.aem.community.util.DBUtil;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Confirmation Ticket DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFConfTicketDB" })
public class CSUFConfirmationTicketDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CSUFConfirmationTicketDB.class);
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Reference
	private GlobalConfigService globalConfigService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String fwsAllocation = "";
		String awardYear = "";
		String posNo = "";
		String officeCont = "";
		String supName = "";
		String cwid = "";
		String emplRcd = "";
		String effectiveDate = "";
		String currentDate = "";
		String appointmentEndDate = "";
		String lastName = "";
		String middleName = "";
		String firstName = "";
		String action = "";
		String actionReason = "";
		String departmentCode = "";
		String department = "";
		String cmsPositionNumber = "";
		String agency = "";
		String reportingUnit = "";
		String classCode = "";
		String serialNumber = "";
		String jobTitle = "";
		String compensationRate = "";
		String authDeptSign = "";
		String authDeptName = "";
		String authDeptDate = "";
		String authDeptComments = "";
		String authDeptCB = "";
		String financialDeptCB = "";
		String financeDeptSign = "";
		String financeDeptName = "";
		String financialDate = "";
		String financialComments = "";
		String payrollDeptCB = "";
		String payrollDeptSign = "";
		String payrollDeptName = "";
		String payrollDate = "";
		String payrollComments = "";

		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		String wfInstanceID = workItem.getWorkflow().getId();
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

							fwsAllocation = eElement
									.getElementsByTagName("FWSAllocation")
									.item(0).getTextContent();
							awardYear = eElement
									.getElementsByTagName("AwardYear").item(0)
									.getTextContent();
							posNo = eElement.getElementsByTagName("PositionNo")
									.item(0).getTextContent();
							officeCont = eElement
									.getElementsByTagName("OfficeContact")
									.item(0).getTextContent();
							supName = eElement
									.getElementsByTagName("SupervisorName")
									.item(0).getTextContent();
							cwid = eElement.getElementsByTagName("CWID")
									.item(0).getTextContent();
							emplRcd = eElement.getElementsByTagName("EmplRcd")
									.item(0).getTextContent();
							effectiveDate = eElement
									.getElementsByTagName("EffectiveDate")
									.item(0).getTextContent();

							currentDate = eElement
									.getElementsByTagName("CurrentDate")
									.item(0).getTextContent();

							appointmentEndDate = eElement
									.getElementsByTagName("AppointmentEndDate")
									.item(0).getTextContent();

							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							middleName = eElement
									.getElementsByTagName("MiddleName").item(0)
									.getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();

							action = eElement.getElementsByTagName("Action")
									.item(0).getTextContent();
							actionReason = eElement
									.getElementsByTagName("ActionReason")
									.item(0).getTextContent();
							departmentCode = eElement
									.getElementsByTagName("DepartmentCode")
									.item(0).getTextContent();
							department = eElement
									.getElementsByTagName("Department").item(0)
									.getTextContent();
							cmsPositionNumber = eElement
									.getElementsByTagName("CMSPositionNumber")
									.item(0).getTextContent();
							agency = eElement.getElementsByTagName("Agency")
									.item(0).getTextContent();
							reportingUnit = eElement
									.getElementsByTagName("ReportingUnit")
									.item(0).getTextContent();
							classCode = eElement
									.getElementsByTagName("ClassCode").item(0)
									.getTextContent();
							serialNumber = eElement
									.getElementsByTagName("SerialNumber")
									.item(0).getTextContent();
							jobTitle = eElement
									.getElementsByTagName("JobTitle").item(0)
									.getTextContent();
							compensationRate = eElement
									.getElementsByTagName("CompensationRate")
									.item(0).getTextContent();
							authDeptSign = eElement
									.getElementsByTagName("AuthDeptSign")
									.item(0).getTextContent();
							authDeptName = eElement
									.getElementsByTagName("AuthDeptName")
									.item(0).getTextContent();
							authDeptDate = eElement
									.getElementsByTagName("AuthDeptDate")
									.item(0).getTextContent();
							authDeptComments = eElement
									.getElementsByTagName("AuthDeptComments")
									.item(0).getTextContent();
							authDeptCB = eElement
									.getElementsByTagName("AuthDeptCB").item(0)
									.getTextContent();

							financialDeptCB = eElement
									.getElementsByTagName("FinancialDeptCB")
									.item(0).getTextContent();
							financeDeptSign = eElement
									.getElementsByTagName("FinanceDeptSign")
									.item(0).getTextContent();
							financeDeptName = eElement
									.getElementsByTagName("FinanceDeptName")
									.item(0).getTextContent();
							financialDate = eElement
									.getElementsByTagName("FinancialDate")
									.item(0).getTextContent();
							financialComments = eElement
									.getElementsByTagName("FinancialComments")
									.item(0).getTextContent();

							payrollDeptCB = eElement
									.getElementsByTagName("PayrollDeptCB")
									.item(0).getTextContent();
							payrollDeptSign = eElement
									.getElementsByTagName("PayrollDeptSign")
									.item(0).getTextContent();

							payrollDeptName = eElement
									.getElementsByTagName("PayrollDeptName")
									.item(0).getTextContent();

							payrollDate = eElement
									.getElementsByTagName("PayrollDate")
									.item(0).getTextContent();

							payrollComments = eElement
									.getElementsByTagName("PayrollComments")
									.item(0).getTextContent();

						}
					}
					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("CWID", cwid);
					dataMap.put("FWS_ALLOCATION", fwsAllocation);
					dataMap.put("AWARD_YEAR", awardYear);
					dataMap.put("POSITION_NUMBER", posNo);
					dataMap.put("OFFICE_CONTACT", officeCont);
					dataMap.put("SUPERVISOR_NAME", supName);
					dataMap.put("EMPL_RCD", emplRcd);
					Object effectiveDateObj = null;
					if (effectiveDate != null && effectiveDate != "") {
						Date effectiveDateNew = Date.valueOf(effectiveDate);
						effectiveDateObj = effectiveDateNew;
					}
					dataMap.put("EFFECTIVE_DATE", effectiveDateObj);
					Object currentDateObj = null;
					if (currentDate != null && currentDate != "") {
						Date currentDateNew = Date.valueOf(currentDate);
						currentDateObj = currentDateNew;
					}
					dataMap.put("CURRENT_DATE", currentDateObj);

					Object appointmentEndDateObj = null;
					if (appointmentEndDate != null && appointmentEndDate != "") {
						Date appointmentEndDateNew = Date
								.valueOf(appointmentEndDate);
						appointmentEndDateObj = appointmentEndDateNew;
					}

					dataMap.put("APPOINTMENT_END_DATE", appointmentEndDateObj);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("MIDDLE_NAME", middleName);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("ACTION", action);
					dataMap.put("ACTION_REASON", actionReason);
					dataMap.put("DEPARTMENT_CODE", departmentCode);
					dataMap.put("DEPARTMENT", department);
					dataMap.put("CMSPOSITION_NUMBER", cmsPositionNumber);
					dataMap.put("AGENCY", agency);
					dataMap.put("REPORTING_UNIT", reportingUnit);
					dataMap.put("CLASS_CODE", classCode);
					dataMap.put("SERIAL_NUMBER", serialNumber);
					dataMap.put("JOB_TITLE", jobTitle);
					dataMap.put("COMPENSATION_RATE", compensationRate);
					dataMap.put("AUTH_DEPT_SIGN", authDeptSign);
					dataMap.put("AUTH_DEPT_NAME", authDeptName);
					dataMap.put("AUTH_DEPT_COMMENTS", authDeptComments);
					dataMap.put("AUTH_DEPT_CB", authDeptCB);
					Object authDeptDateObj = null;
					if (authDeptDate != null && authDeptDate != "") {
						Date authDeptDateNew = Date.valueOf(authDeptDate);
						authDeptDateObj = authDeptDateNew;
					}
					dataMap.put("AUTH_DEPT_DATE", authDeptDateObj);

					dataMap.put("FINANCE_DEPT_SIGN", financeDeptSign);
					dataMap.put("FINANCE_DEPT_NAME", financeDeptName);
					dataMap.put("FINANCIAL_COMMENTS", financialComments);
					dataMap.put("FINANCIAL_DEPT_CB", financialDeptCB);
					Object financialDateObj = null;
					if (financialDate != null && financialDate != "") {
						Date financialDateNew = Date.valueOf(financialDate);
						financialDateObj = financialDateNew;
					}
					dataMap.put("FINANCIAL_DATE", financialDateObj);

					dataMap.put("PAYROLL_DEPT_SIGN", payrollDeptSign);
					dataMap.put("PAYROLL_DEPT_NAME", payrollDeptName);
					dataMap.put("PAYROLL_COMMENTS", payrollComments);
					dataMap.put("PAYROLL_DEPT_CB", payrollDeptCB);
					Object payrollDateObj = null;
					if (payrollDate != null && payrollDate != "") {
						Date payrollDateNew = Date.valueOf(payrollDate);
						payrollDateObj = payrollDateNew;
					}
					dataMap.put("PAYROLL_DATE", payrollDateObj);
					dataMap.put("WORKFLOW_INSTANCE_ID", wfInstanceID);

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
		String dataSourceVal = globalConfigService.getAEMDataSource();
		log.info("DataSourceVal==========" + dataSourceVal);
		conn = jdbcConnectionService.getDBConnection(dataSourceVal);
		log.info("Connection==========" + conn);
		// conn = jdbcConnectionService.getDBConnection(datasourceName)
		if (conn != null) {
			insertCataLeaveDonationData(conn, dataMap);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */
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
			String tableName = "aem_confirmation_ticket";
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
				}
			} catch (SQLException e) {
				log.error("SQLException=" + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}

			try {
				log.info("Before Prepared Stmt Confirmation Ticket");
				preparedStmt.execute();
				conn.commit();
				log.info("After Prepared Stmt Confirmation Ticket");
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				String errMessage = e1.getMessage();
				e1.printStackTrace();
				DBUtil dbUtil = new DBUtil();
				String tableNameAudit = "AEM_AUDIT_TRACE";
				LinkedHashMap<String, Object> dataMapAuditTrail = new LinkedHashMap<String, Object>();
				Timestamp auditStTime = new java.sql.Timestamp(
						System.currentTimeMillis());
				dataMapAuditTrail.put("EVENT_TYPE", "Database");
				dataMapAuditTrail.put("AUDIT_TIME", auditStTime);
				dataMapAuditTrail.put("FILENET_URL", "");
				dataMapAuditTrail.put("DATA_PROCESSED", "0");
				dataMapAuditTrail.put("FILENET_JSON", "");
				dataMapAuditTrail.put("FORM_NAME",
						"Confirmation Ticket");
				dataMapAuditTrail.put("TABLE_NAME",
						"aem_confirmation_ticket");
				dataMapAuditTrail.put("ERROR_DESC", errMessage);
				dbUtil.insertAutitTrace(conn, dataMapAuditTrail, tableNameAudit);

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
						log.error("Exception=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
