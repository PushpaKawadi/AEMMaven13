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
		Constants.SERVICE_DESCRIPTION + "=PayPlan10_12_11_12 Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=PayPlan10_12_11_12DB" })
public class PayPlan10_12_11_12DB implements WorkflowProcess{
	
	private static final Logger log = LoggerFactory
			.getLogger(PayPlan10_12_11_12DB.class);

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
		String empId = "";
		String empRCD = "";		
		String extension = "";
		String scoPositionNumber = "";
		String timeBase = "";
		String statusMenu = "";
		String cbid = "";
		String classification = "";
		String grade = "";
		String cmsPosition = "";
		String departmentName = "";
		String departmentID = "";
		String payPlan = "";
		String planSelected = "";
		String name1 = "";
		String monthOff1 = "";
		String monthOff2 = "";
		String empDate = "";
		String adminSign = "";
		String approvalRecommendedYes = "";
		String appropriateAdminName = "";
		String date1 = "";
		String approvalGrantedYes = "";
		String vpSignature = "";
		String date2 = "";
		String oncycle = "";
		String offcycle = "";
		String currentMonthSalary = "";
		String adjustedSalary = "";
		String dateDiscussed = "";
		String payPlan10 = "";
		String payPlan11 = "";
		String startDate = "";
		String monthSal = "";
		String daysToWork = "";
		String possibleWorkDays = "";
		String annualSalary = "";
		String monthSal1 = "";
		String monthsToWork = "";
		String projectedEarnedSalary = "";
		String annualSalary1 = "";
		String projectedEarnedSalary1 = "";
		String settlementAmount = "";
		String MonthOff1st = "";
		String MonthOff2nd = "";
		
		
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

							empId = eElement.getElementsByTagName("Empl_ID")
									.item(0).getTextContent();
							lastName = eElement
									.getElementsByTagName("Last_Name")
									.item(0).getTextContent();
							firstName = eElement
									.getElementsByTagName("First_Name")
									.item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("Empl_RCD")
									.item(0).getTextContent();
							extension = eElement.getElementsByTagName("Extension")
									.item(0).getTextContent();
							scoPositionNumber = eElement
									.getElementsByTagName("SCO_Position_Number").item(0)
									.getTextContent();
							timeBase = eElement
									.getElementsByTagName("Timebase").item(0)
									.getTextContent();

							statusMenu = eElement
									.getElementsByTagName("StatusMenu")
									.item(0).getTextContent();

							cbid = eElement
									.getElementsByTagName("CBID")
									.item(0).getTextContent();
							classification = eElement
									.getElementsByTagName("Classification")
									.item(0).getTextContent();

							grade = eElement
									.getElementsByTagName("Grade")
									.item(0).getTextContent();

							cmsPosition = eElement
									.getElementsByTagName("CMS_Position_Number")
									.item(0).getTextContent();
							departmentName = eElement
									.getElementsByTagName("Department_Name")
									.item(0).getTextContent();

							departmentID = eElement
									.getElementsByTagName("Department_ID")
									.item(0).getTextContent();

							payPlan = eElement
									.getElementsByTagName("10/12 OR 11/12 Request")
									.item(0).getTextContent();

							planSelected = eElement
									.getElementsByTagName("Plan_Selected").item(0)
									.getTextContent();

							name1 = eElement
									.getElementsByTagName("Name1")
									.item(0).getTextContent();
							monthOff1 = eElement
									.getElementsByTagName("MonthOff1")
									.item(0).getTextContent();
							monthOff2 = eElement
									.getElementsByTagName("MonthOff2")
									.item(0).getTextContent();
							empDate = eElement.getElementsByTagName("EmpDate")
									.item(0).getTextContent();

							adminSign = eElement
									.getElementsByTagName("AdminSign").item(0)
									.getTextContent();
							approvalRecommendedYes = eElement
									.getElementsByTagName("ApprovalRecommendedYes").item(0)
									.getTextContent();
							appropriateAdminName = eElement
									.getElementsByTagName("Appropriate_Admin_Name").item(0)
									.getTextContent();

							date1 = eElement.getElementsByTagName("Date1")
									.item(0).getTextContent();

							approvalGrantedYes = eElement.getElementsByTagName("ApprovalGrantedYes")
									.item(0).getTextContent();
							vpSignature = eElement.getElementsByTagName("VP_Signature")
									.item(0).getTextContent();
							date2	 = eElement.getElementsByTagName("Date2")
									.item(0).getTextContent();

							oncycle = eElement.getElementsByTagName("OnCycle")
									.item(0).getTextContent();

							offcycle = eElement.getElementsByTagName("OffCycle")
									.item(0).getTextContent();
							currentMonthSalary = eElement
									.getElementsByTagName("CurrentMonthlySalary").item(0)
									.getTextContent();
							adjustedSalary = eElement.getElementsByTagName("AdjustedSalary")
									.item(0).getTextContent();
							dateDiscussed = eElement.getElementsByTagName("DateDiscussed")
									.item(0).getTextContent();
							payPlan10 = eElement.getElementsByTagName("PayPlan10")
									.item(0).getTextContent();
							payPlan11 = eElement.getElementsByTagName("PayPlan11")
									.item(0).getTextContent();
							startDate = eElement.getElementsByTagName("StartDate")
									.item(0).getTextContent();
							monthSal = eElement.getElementsByTagName("Month_Sal")
									.item(0).getTextContent();
							daysToWork = eElement.getElementsByTagName("Month_Sal")
									.item(0).getTextContent();
							possibleWorkDays = eElement.getElementsByTagName("Possible_work_days")
									.item(0).getTextContent();
							annualSalary = eElement.getElementsByTagName("Possible_work_days")
									.item(0).getTextContent();
							monthSal1 = eElement.getElementsByTagName("Month_Sal1")
									.item(0).getTextContent();
							monthsToWork = eElement.getElementsByTagName("Months_to_work")
									.item(0).getTextContent();
							projectedEarnedSalary = eElement.getElementsByTagName("Projected_Earned_Salary")
									.item(0).getTextContent();
							annualSalary1 = eElement.getElementsByTagName("AnnualSalary1")
									.item(0).getTextContent();
							projectedEarnedSalary1 = eElement.getElementsByTagName("Projected_Earned_Salary1")
									.item(0).getTextContent();
							settlementAmount = eElement.getElementsByTagName("Settlement_Amount")
									.item(0).getTextContent();
							MonthOff1st = eElement.getElementsByTagName("1st_Month_off")
									.item(0).getTextContent();							
							MonthOff2nd = eElement.getElementsByTagName("2nd_Month_offment2")
									.item(0).getTextContent();
							
							}
					}

					dataMap = new LinkedHashMap<String, Object>();

					dataMap.put("EMPL_ID", empId);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("EMPL_RCD", empRCD);
					dataMap.put("EXTENSION", extension);
					dataMap.put("SCO_POSITION_NUMBER", scoPositionNumber);
					dataMap.put("TIMEBASE", timeBase);
					dataMap.put("STATUS_MENU", statusMenu);
					dataMap.put("CBID", cbid);
					dataMap.put("CLASSIFICATION", classification);
					dataMap.put("GRADE", grade);
					dataMap.put("CMS_POSITION_NUMBER", cmsPosition);
					dataMap.put("DEPARTMENT_NAME", departmentName);
					dataMap.put("DEPARTMENT_ID", departmentID);
					dataMap.put("10_12or11_12_REQUEST", payPlan);
					dataMap.put("PLAN_SELECTED", planSelected);
					dataMap.put("NAME1", name1);
					dataMap.put("MONTHOFF1", monthOff1);
					dataMap.put("MONTHOFF2", monthOff2);
					
					Object empDateObj = null;
					if (empDate != null && empDate != "") {
						Date empDateNew = Date.valueOf(empDate);
						empDateObj = empDateNew;
					}
					dataMap.put("EMP_DATE", empDateObj);
					dataMap.put("ADMIN_SIGN", adminSign);
					dataMap.put("APPROVAL_RECOMMENDED_YES", approvalRecommendedYes);
					dataMap.put("APPROPRIATE_ADMIN_NAME", appropriateAdminName);

					Object date1Obj = null;
					if (date1 != null && date1 != "") {
						Date date1New = Date.valueOf(date1);
						date1Obj = date1New;
					}
					dataMap.put("DATE1", date1Obj);
					dataMap.put("APPROVAL_GRANTED_YES", approvalGrantedYes);
					dataMap.put("VP_SIGNATURE", vpSignature);

					Object date2Obj = null;
					if (date2 != null && date2 != "") {
						Date date2New = Date.valueOf(date2);
						date2Obj = date2New;
					}
					dataMap.put("DATE2", date2Obj);

					Object oncycleObj = null;
					if (oncycle != null && oncycle != "") {
						Date oncycleNew = Date.valueOf(oncycle);
						oncycleObj = oncycleNew;
					}
					dataMap.put("ON_CYCLE", oncycleObj);

					Object offcycleObj = null;
					if (offcycle != null && offcycle != "") {
						Date offcycleNew = Date.valueOf(offcycle);
						offcycleObj = offcycleNew;
					}
					dataMap.put("OFF_CYCLE", offcycleObj);
					dataMap.put("CURRENT_MONTHLY_SALARY", currentMonthSalary);
					dataMap.put("ADJUSTED_SALARY", adjustedSalary);

					Object dateDiscussedObj = null;
					if (dateDiscussed != null && dateDiscussed != "") {
						Date dateDiscussedNew = Date.valueOf(dateDiscussed);
						dateDiscussedObj = dateDiscussedNew;
					}
					dataMap.put("DATE_DISCUSSED", dateDiscussedObj);
					dataMap.put("PAYPLAN10", payPlan10);
					dataMap.put("PAYPLAN11", payPlan11);
					
					Object startDateObj = null;
					if (startDate != null && startDate != "") {
						Date startDateNew = Date.valueOf(startDate);
						startDateObj = startDateNew;
					}
					dataMap.put("START_DATE", startDateObj);
					dataMap.put("MONTH_SAL", monthSal);
					dataMap.put("DAYS_TO_WORK", daysToWork);
					dataMap.put("POSSIBLE_WORK_DAYS", possibleWorkDays);
					dataMap.put("ANNUAL_SALARY", annualSalary);
					dataMap.put("MONTH_SAL1", monthSal1);
					dataMap.put("MONTHS_TO_WORK", monthsToWork);
					dataMap.put("PROJECTED_EARNED_SALARY", projectedEarnedSalary);
					dataMap.put("ANNUAL_SALARY1", annualSalary1);
					dataMap.put("PROJECTED_EARNED_SALARY1", projectedEarnedSalary1);
					dataMap.put("SETTLEMENT_AMOUNT", settlementAmount);
					dataMap.put("1ST_MONTH_OFF", MonthOff1st);
					dataMap.put("2ND_MONTH_OFF", MonthOff2nd);


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
			insertPayPlan10_12_11_12Data(conn, dataMap);
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

	public void insertPayPlan10_12_11_12Data(Connection conn,
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
			String tableName = "AEM_10_12_11_12PayPlan";
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
