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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Pre Perf Evaluation Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=SCPRStaffMppSave" })
public class SCPRStaffOrMPPDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(SCPRStaffOrMPPDB.class);
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

		String empStatusRB = "";
		String statusTB = "";
		String additionalEmpCB1 = "";
		String additionalEmpCB2 = "";
		String additionalEmpCB3 = "";
		String additionalEmpCB4 = "";
		String fiscalAnotherLocationRB = "";
		String fiscalExtendedEducationRB = "";
		String fiscalAnotherLocationTB = "";
		String fiscalExtendedEducationTB = "";
		String empID = "";
		String unit = "";
		String officeOrRoom = "";
		String extension = "";
		String college = "";
		String empFname = "";
		String empLname = "";
		String empMname = "";
		String hrdeptID = "";
		String dept = "";
		String descOfWork = "";
		String year1 = "";
		String month1 = "";
		String dateWorked1 = "";
		String year2 = "";
		String month2 = "";
		String dateWorked2 = "";
		String year3 = "";
		String month3 = "";
		String dateWorked3 = "";
		String year4 = "";
		String month4 = "";
		String dateWorked4 = "";
		String year5 = "";
		String month5 = "";
		String dateWorked5 = "";
		String year6 = "";
		String month6 = "";
		String dateWorked6 = "";
		String amtPerDay = "";
		String noPayDays = "";
		String totalDue = "";
		String projAccTitle = "";
		String fundingCMSPos = "";
		String acct = "";
		String fundingdept = "";
		String program = "";
		String fund = "";
		String className = "";
		String poNumber = "";
		String projNumber = "";
		String aySalary = "";
		String auxCMSPos = "";
		String empExt = "";
		String empDate = "";
		String formPreparedBy = "";
		String empSign = "";
		String accNumber = "";
		String chairSign = "";
		String chairName = "";
		String chairDate = "";
		String sourceApproverName = "";
		String sourceApproverSign = "";
		String sourceApproverDate = "";
		String deanOrDesignerName = "";
		String deanOrDesignerSign = "";
		String deanOrDesignerDate = "";
		String avpOperationSign = "";
		String avpOperationDate = "";
		String avpAdditionalPay = "";
		String hrName = "";
		String hrSign = "";
		String hrDate = "";

		String workflowInstanceID = "";

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
			workflowInstanceID = workItem.getWorkflow().getId();
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

							empStatusRB = eElement.getElementsByTagName("EmpStatusRB").item(0).getTextContent();
							statusTB = eElement.getElementsByTagName("StatusTB").item(0).getTextContent();
							additionalEmpCB1 = eElement.getElementsByTagName("AdditionalEmpCB1").item(0).getTextContent();
							additionalEmpCB2 = eElement.getElementsByTagName("AdditionalEmpCB2").item(0).getTextContent();
							additionalEmpCB3 = eElement.getElementsByTagName("AdditionalEmpCB3").item(0).getTextContent();
							additionalEmpCB4 = eElement.getElementsByTagName("AdditionalEmpCB4").item(0).getTextContent();
							fiscalAnotherLocationRB = eElement.getElementsByTagName("FiscalAnotherLocationRB").item(0).getTextContent();
							fiscalExtendedEducationRB = eElement.getElementsByTagName("FiscalExtendedEducationRB").item(0).getTextContent();
							fiscalAnotherLocationTB = eElement.getElementsByTagName("FiscalAnotherLocationTB").item(0).getTextContent();
							fiscalExtendedEducationTB = eElement.getElementsByTagName("FiscalExtendedEducationTB").item(0).getTextContent();
							empID = eElement.getElementsByTagName("EmpID").item(0).getTextContent();
							unit = eElement.getElementsByTagName("Unit").item(0).getTextContent();
							officeOrRoom = eElement.getElementsByTagName("OfficeOrRoom").item(0).getTextContent();
							extension = eElement.getElementsByTagName("Extension").item(0).getTextContent();
							college = eElement.getElementsByTagName("College").item(0).getTextContent();
							empFname = eElement.getElementsByTagName("EmpFname").item(0).getTextContent();
							empLname = eElement.getElementsByTagName("EmpLname").item(0).getTextContent();
							empMname = eElement.getElementsByTagName("EmpMname").item(0).getTextContent();
							hrdeptID = eElement.getElementsByTagName("HRDeptID").item(0).getTextContent();
							dept = eElement.getElementsByTagName("Dept").item(0).getTextContent();
							descOfWork = eElement.getElementsByTagName("DescOfWork").item(0).getTextContent();
							year1 = eElement.getElementsByTagName("Year1").item(0).getTextContent();
							month1 = eElement.getElementsByTagName("Month1").item(0).getTextContent();
							dateWorked1 = eElement.getElementsByTagName("DateWorked1").item(0).getTextContent();
							year2 = eElement.getElementsByTagName("Year2").item(0).getTextContent();
							month2 = eElement.getElementsByTagName("Month2").item(0).getTextContent();
							dateWorked2 = eElement.getElementsByTagName("DateWorked2").item(0).getTextContent();
							year3 = eElement.getElementsByTagName("Year3").item(0).getTextContent();
							month3 = eElement.getElementsByTagName("Month3").item(0).getTextContent();
							dateWorked3 = eElement.getElementsByTagName("DateWorked3").item(0).getTextContent();
							year4 = eElement.getElementsByTagName("Year4").item(0).getTextContent();
							month4 = eElement.getElementsByTagName("Month4").item(0).getTextContent();
							dateWorked4 = eElement.getElementsByTagName("DateWorked4").item(0).getTextContent();
							year5 = eElement.getElementsByTagName("Year5").item(0).getTextContent();
							month5 = eElement.getElementsByTagName("Month5").item(0).getTextContent();
							dateWorked5 = eElement.getElementsByTagName("DateWorked5").item(0).getTextContent();
							year6 = eElement.getElementsByTagName("Year6").item(0).getTextContent();
							month6 = eElement.getElementsByTagName("Month6").item(0).getTextContent();
							dateWorked6 = eElement.getElementsByTagName("DateWorked6").item(0).getTextContent();
							amtPerDay = eElement.getElementsByTagName("AmtPerDay").item(0).getTextContent();
							noPayDays = eElement.getElementsByTagName("NoPayDays").item(0).getTextContent();
							totalDue = eElement.getElementsByTagName("TotalDue").item(0).getTextContent();
							projAccTitle = eElement.getElementsByTagName("ProjAccTitle").item(0).getTextContent();
							fundingCMSPos = eElement.getElementsByTagName("FundingCMSPos").item(0).getTextContent();
							acct = eElement.getElementsByTagName("Acct").item(0).getTextContent();
							fundingdept = eElement.getElementsByTagName("FundingDept").item(0).getTextContent();
							program = eElement.getElementsByTagName("Program").item(0).getTextContent();
							fund = eElement.getElementsByTagName("Fund").item(0).getTextContent();
							className = eElement.getElementsByTagName("Class").item(0).getTextContent();
							poNumber = eElement.getElementsByTagName("PONumber").item(0).getTextContent();
							projNumber = eElement.getElementsByTagName("ProjNumber").item(0).getTextContent();
							aySalary = eElement.getElementsByTagName("AYSalary").item(0).getTextContent();
							auxCMSPos = eElement.getElementsByTagName("AuxCMSPos").item(0).getTextContent();
							empExt = eElement.getElementsByTagName("EmpExt").item(0).getTextContent();
							empDate = eElement.getElementsByTagName("EmpDate").item(0).getTextContent();
							formPreparedBy = eElement.getElementsByTagName("FormPreparedBy").item(0).getTextContent();
							empSign = eElement.getElementsByTagName("EmpSign").item(0).getTextContent();
							accNumber = eElement.getElementsByTagName("AccNumber").item(0).getTextContent();
							chairSign = eElement.getElementsByTagName("ChairSign").item(0).getTextContent();
							chairName = eElement.getElementsByTagName("ChairName").item(0).getTextContent();
							chairDate = eElement.getElementsByTagName("ChairDate").item(0).getTextContent();
							sourceApproverName = eElement.getElementsByTagName("SourceApproverName").item(0).getTextContent();
							sourceApproverSign = eElement.getElementsByTagName("SourceApproverSign").item(0).getTextContent();
							sourceApproverDate = eElement.getElementsByTagName("SourceApproverDate").item(0).getTextContent();
							deanOrDesignerName = eElement.getElementsByTagName("DeanOrDesignerName").item(0).getTextContent();
							deanOrDesignerSign = eElement.getElementsByTagName("DeanOrDesignerSign").item(0).getTextContent();
							deanOrDesignerDate = eElement.getElementsByTagName("DeanOrDesignerDate").item(0).getTextContent();
							avpOperationSign = eElement.getElementsByTagName("AVPOperationSign").item(0).getTextContent();
							avpOperationDate = eElement.getElementsByTagName("AVPOperationDate").item(0).getTextContent();
							avpAdditionalPay = eElement.getElementsByTagName("AVPAdditionalPay").item(0).getTextContent();
							hrName = eElement.getElementsByTagName("HRName").item(0).getTextContent();
							hrSign = eElement.getElementsByTagName("HRSign").item(0).getTextContent();
							hrDate = eElement.getElementsByTagName("HRDate").item(0).getTextContent();

						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("EMP_STATUS", empStatusRB);
					dataMap.put("OTHER_STATUS", statusTB);
					dataMap.put("ADD_EMPLOYEMENT_PAY_REQ1", additionalEmpCB1);
					dataMap.put("ADD_EMPLOYEMENT_PAY_REQ2", additionalEmpCB2);
					dataMap.put("ADD_EMPLOYEMENT_PAY_REQ3", additionalEmpCB3);					
					dataMap.put("ADD_EMPLOYEMENT_PAY_REQ4", additionalEmpCB4);
					dataMap.put("WORK_AT_ANOTHER_LOC", fiscalAnotherLocationRB);
					dataMap.put("WORK_FOR_EXT_EDU", fiscalExtendedEducationRB);
					dataMap.put("LIST_WORK_TB", fiscalAnotherLocationTB);
					dataMap.put("LIST_TERM_TB", fiscalExtendedEducationTB);
					dataMap.put("EMP_ID", empID);
					dataMap.put("UNIT", unit);
					dataMap.put("BUILDING", officeOrRoom);
					dataMap.put("EXTENSION", extension);
					dataMap.put("COLLEGE", college);
					dataMap.put("EMP_FNAME", empFname);
					dataMap.put("EMP_LNAME", empLname);
					dataMap.put("EMP_MNAME", empMname);
					dataMap.put("HR_DEPT_ID", hrdeptID);
					dataMap.put("DEPT", dept);
					dataMap.put("DESC_OF_WORK", descOfWork);
					dataMap.put("YEAR1", year1);
					dataMap.put("MONTH1", month1);
					dataMap.put("DATE_WORKED1", dateWorked1);
					dataMap.put("YEAR2", year2);
					dataMap.put("MONTH2", month2);
					dataMap.put("DATE_WORKED2", dateWorked2);
					dataMap.put("YEAR3", year3);
					dataMap.put("MONTH3", month3);
					dataMap.put("DATE_WORKED3", dateWorked3);
					dataMap.put("YEAR4", year4);
					dataMap.put("MONTH4", month4);
					dataMap.put("DATE_WORKED4", dateWorked4);
					dataMap.put("YEAR5", year5);
					dataMap.put("MONTH5", month5);
					dataMap.put("DATE_WORKED5", dateWorked5);
					dataMap.put("YEAR6", year6);
					dataMap.put("MONTH6", month6);
					dataMap.put("DATE_WORKED6", dateWorked6);
					dataMap.put("AMT_PER_DAY", amtPerDay);
					dataMap.put("NO_OF_DAYS", noPayDays);
					dataMap.put("TOTAL_DUE", totalDue);
					dataMap.put("PROJ_TITLE", projAccTitle);
					dataMap.put("FUNDING_CMS_POS", fundingCMSPos);
					dataMap.put("ACCNT", acct);
					dataMap.put("FUNDING_DEPT", fundingdept);
					dataMap.put("PROGRAM", program);
					dataMap.put("FUND", fund);
					dataMap.put("CLASS", className);
					dataMap.put("PO_NO", poNumber);
					dataMap.put("PROJ_NO", projNumber);
					dataMap.put("AY_SALARY", aySalary);
					dataMap.put("AUX_CMS_POS", auxCMSPos);
					dataMap.put("EMP_EXT", empExt);
					Object empSignDateObj = null;
					if (empDate != null && empDate != "") {
						Date empDateNew = Date.valueOf(empDate);
						empSignDateObj = empDateNew;
					}
					dataMap.put("EMP_DATE", empSignDateObj);					
					dataMap.put("FORM_PREPARED_BY", formPreparedBy);
					dataMap.put("EMP_SIGN", empSign);
					dataMap.put("ACC_NUMBER", accNumber);
					dataMap.put("CHAIR_SIGN", chairSign);
					dataMap.put("CHAIR_NAME", chairName);
					Object chairDateObj = null;
					if (chairDate != null && chairDate != "") {
						Date chairDateNew = Date.valueOf(chairDate);
						chairDateObj = chairDateNew;
					}
					dataMap.put("CHAIR_DATE", chairDateObj);
					dataMap.put("FUNDING_SOURCE_APPROVER_NAME", sourceApproverName);
					dataMap.put("FUNDING_SOURCE_APPROVER_SIGN", sourceApproverSign);
					Object sourceApproverDateObj = null;
					if (sourceApproverDate != null && sourceApproverDate != "") {
						Date sourceApproverDateNew = Date.valueOf(sourceApproverDate);
						sourceApproverDateObj = sourceApproverDateNew;
					}
					dataMap.put("FUNDING_SOURCE_APPROVER_DATE", sourceApproverDateObj);
					dataMap.put("DEAN_OR_DESIGNEE_NAME", deanOrDesignerName);
					dataMap.put("DEAN_OR_DESIGNEE_SIGN", deanOrDesignerSign);
					Object deanOrDesignerDateObj = null;
					if (deanOrDesignerDate != null && deanOrDesignerDate != "") {
						Date deanOrDesignerDateNew = Date.valueOf(deanOrDesignerDate);
						deanOrDesignerDateObj = deanOrDesignerDateNew;
					}
					dataMap.put("DEAN_OR_DESIGNEE_DATE", deanOrDesignerDateObj);
					dataMap.put("AVP_OPERATOR_SIGN", avpOperationSign);
					Object avpOperationDateObj = null;
					if (avpOperationDate != null && avpOperationDate != "") {
						Date avpOperationDateNew = Date.valueOf(avpOperationDate);
						avpOperationDateObj = avpOperationDateNew;
					}
					dataMap.put("AVP_OPERATOR_DATE", avpOperationDateObj);
					dataMap.put("AVP_ADDITIONAL_PAY_PERC", avpAdditionalPay);
					dataMap.put("HR_NAME", hrName);
					dataMap.put("HR_SIGN", hrSign);
					Object hrDateObj = null;
					if (hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}
					dataMap.put("HR_DATE", hrDateObj);
					dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstanceID);

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
			String tableName = "AEM_SCPR_STAFF_OR_MPP";
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
				log.info("preparedstmt=" + preparedStmt);
				conn.commit();
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						log.info("preparedstmt closed");
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
