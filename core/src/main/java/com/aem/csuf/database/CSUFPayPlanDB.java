package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
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
import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=10-12/11-12 PayPlan Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=PayPlanDB" })
public class CSUFPayPlanDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFPayPlanDB.class);

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Reference
	private GlobalConfigService globalConfigService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		log.info("Inside the class");
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String empl_ID = null;
		String first_Name = null;
		String last_Name = null; 
		String empl_RCD = null; 
		String extension = null; 
		String scoPositionNumber = null; 
		String timebase = null; 
		String statusMenu = null; 
		String cbid = null; 
		String classification = null; 
		String grade = null; 
		String cmsPositionNumber = null; 
		String departmentName = null; 
		String departmentID = null; 
		String payPlanCHK = null; 
		String effectiveDate = null; 
		String planSelected = null; 
		String work11 = null;
		String work10 = null;
		String work5 = null;
		String variation = null;
		String variationField = null;
		String monthOff1 = null; 
		String monthOff2 = null; 
		String employeeSignature = null; 
		String empDate = null;    
		String onCycle = null; 
		String offCycle = null; 
		String currentMonthlySalary = null; 
		String adjustedSalary = null; 
		String dateDiscussed = null; 	
		String payPlan10 = null; 
		String payPlan11 = null; 
		String startDate = null; 
		String monthSal = null; 
		String daystowork = null; 
		String annualSalary = null; 
		String possibleworkdays = null;        
		String monthSal1 = null; 
		String monthstowork = null; 
		String projectedEarnedSalary = null; 
		String annualSalary1 = null; 
		String projectedEarnedSalary1 = null; 
		String settlementAmount = null; 
		String firstMonthOff = null; 
		String secondMonthOff = null;	
		String approvalRecommendedYes = null; 
		String appropriateAdminName = null; 
		String date1 = null; 
		String approvalGrantedYes = null; 
		String vpSignature = null; 
		String date2 = null; 
		 

		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		
		String dataSourceVal = globalConfigService.getAEMDataSource();
		log.info("DataSourceVal==========" + dataSourceVal);
		conn = jdbcConnectionService.getDBConnection(dataSourceVal);
		log.info("Connection==========" + conn);

		if (conn != null) {
			while (xmlFiles.hasNext()) {
				Resource attachmentXml = xmlFiles.next();
				// log.info("xmlFiles inside ");
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
								
								empl_ID = eElement.getElementsByTagName("empl_ID").item(0).getTextContent();

								first_Name = eElement.getElementsByTagName("first_Name").item(0).getTextContent();

								last_Name = eElement.getElementsByTagName("last_Name").item(0)
										.getTextContent();

								empl_RCD = eElement.getElementsByTagName("empl_RCD").item(0)
										.getTextContent();
								
								extension = eElement.getElementsByTagName("extension").item(0)
										.getTextContent();

								scoPositionNumber = eElement.getElementsByTagName("scoPositionNumber").item(0).getTextContent();

								timebase = eElement.getElementsByTagName("timebase").item(0).getTextContent();

								statusMenu = eElement.getElementsByTagName("statusMenu").item(0)
										.getTextContent();

								cbid = eElement.getElementsByTagName("cbid").item(0)
										.getTextContent();

								classification = eElement.getElementsByTagName("classification").item(0).getTextContent();

								grade = eElement.getElementsByTagName("grade").item(0)
										.getTextContent();

								cmsPositionNumber = eElement.getElementsByTagName("cmsPositionNumber").item(0)
										.getTextContent();

								departmentName = eElement.getElementsByTagName("departmentName").item(0)
										.getTextContent();

								departmentID = eElement.getElementsByTagName("departmentID").item(0).getTextContent();

								payPlanCHK = eElement.getElementsByTagName("payPlanCHK").item(0)
										.getTextContent();

								effectiveDate = eElement.getElementsByTagName("effectiveDate").item(0)
										.getTextContent();

								work11 = eElement.getElementsByTagName("work11CHK").item(0).getTextContent();
								
								work10 = eElement.getElementsByTagName("work10CHK").item(0).getTextContent();
								
								work5 = eElement.getElementsByTagName("work5CHK").item(0).getTextContent();
								
								variation = eElement.getElementsByTagName("variationCHK").item(0).getTextContent();
								
								variationField = eElement.getElementsByTagName("variationField").item(0).getTextContent();

								monthOff1 = eElement.getElementsByTagName("monthOff1").item(0)
										.getTextContent();

								monthOff2 = eElement.getElementsByTagName("monthOff2").item(0)
										.getTextContent();

								employeeSignature = eElement.getElementsByTagName("employeeSignature").item(0)
										.getTextContent();
								
								empDate = eElement.getElementsByTagName("empDate").item(0)
										.getTextContent();

								onCycle = eElement.getElementsByTagName("onCycle").item(0)
										.getTextContent();

								offCycle = eElement.getElementsByTagName("offCycle").item(0)
										.getTextContent();

								currentMonthlySalary = eElement.getElementsByTagName("currentMonthlySalary").item(0)
										.getTextContent();

								adjustedSalary = eElement.getElementsByTagName("adjustedSalary").item(0)
										.getTextContent();

								dateDiscussed = eElement.getElementsByTagName("dateDiscussed").item(0)
										.getTextContent();

								payPlan10 = eElement.getElementsByTagName("payPlan10").item(0).getTextContent();

								payPlan11 = eElement.getElementsByTagName("payPlan11").item(0).getTextContent();

								startDate = eElement.getElementsByTagName("startDate").item(0).getTextContent();

								monthSal = eElement.getElementsByTagName("monthSal").item(0).getTextContent();

								daystowork = eElement.getElementsByTagName("daystowork").item(0)
										.getTextContent();

								annualSalary = eElement.getElementsByTagName("annualSalary").item(0)
										.getTextContent();

								possibleworkdays = eElement.getElementsByTagName("possibleworkdays").item(0)
										.getTextContent();

								monthSal1 = eElement.getElementsByTagName("monthSal1").item(0)
										.getTextContent();

								monthstowork = eElement.getElementsByTagName("monthstowork").item(0).getTextContent();

								projectedEarnedSalary = eElement.getElementsByTagName("projectedEarnedSalary").item(0).getTextContent();

								annualSalary1 = eElement.getElementsByTagName("annualSalary1").item(0).getTextContent();

								projectedEarnedSalary1 = eElement.getElementsByTagName("projectedEarnedSalary1").item(0).getTextContent();

								settlementAmount = eElement.getElementsByTagName("settlementAmount").item(0).getTextContent();

								firstMonthOff = eElement.getElementsByTagName("firstMonthOff").item(0)
										.getTextContent();

								secondMonthOff = eElement.getElementsByTagName("secondMonthOff").item(0)
										.getTextContent();

								

															}
						}
						dataMap = new LinkedHashMap<String, Object>();
						
						dataMap.put("EMPL_ID", empl_ID);
						dataMap.put("FIRST_NAME", first_Name);
						dataMap.put("LAST_NAME", last_Name);
						dataMap.put("EMPL_RCD", empl_RCD);
						dataMap.put("EXTENSION", extension);
						dataMap.put("SCOPOSITIONNUMBER", scoPositionNumber);
						dataMap.put("TIME_BASE", timebase);
						dataMap.put("STATUS_MENU", statusMenu);
						dataMap.put("CBID", cbid);
						dataMap.put("CLASSIFICATION", classification);
						dataMap.put("GRADE", grade);
						dataMap.put("CMS_POSITION_NUMBER", cmsPositionNumber);
						dataMap.put("DEPARTMENT_NAME", departmentName);
						dataMap.put("DEPARTMENT_ID", departmentID);
						dataMap.put("PAY_PLAN_CHK", payPlanCHK);

						Object effectiveDateObj = null;
						if (effectiveDate != null && effectiveDate != "") {
							Date effectiveDateNew = Date.valueOf(effectiveDate);
							effectiveDateObj = effectiveDateNew;
						}
						dataMap.put("EFFECTIVE_DATE", effectiveDateObj);						
						dataMap.put("WORK11_CHK", work11);
						dataMap.put("WORK10_CHK", work10);
						dataMap.put("WORK5_CHK", work5);
						dataMap.put("VARIATION_CHK", variation);
						dataMap.put("VARIATION", variationField);
						dataMap.put("MONTH_OFF1", monthOff1);
						dataMap.put("MONTH_OFF2", monthOff2);
						dataMap.put("EMPLOYEE_SIGNATURE", employeeSignature);
						
						Object empDateObj = null;
						if (empDate != null && empDate != "") {
							Date empDateNew = Date.valueOf(empDate);
							empDateObj = empDateNew;
						}
						dataMap.put("EMP_DATE", empDateObj);
						dataMap.put("ON_CYCLE", onCycle);
						dataMap.put("OFF_CYCLE", offCycle);
						dataMap.put("CURRENT_MONTHLY_SALARY", currentMonthlySalary);						
						dataMap.put("ADJUSTED_SALARY", adjustedSalary);

						Object dateDiscussedObj = null;
						if (dateDiscussed != null && dateDiscussed != "") {
							Date dateDiscussedNew = Date.valueOf(dateDiscussed);
							dateDiscussedObj = dateDiscussedNew;
						}
						dataMap.put("DATE_DISCUSSED", dateDiscussedObj);
						dataMap.put("PAY_PLAN10", payPlan10);
						dataMap.put("PAY_PLAN11", payPlan11);
						
						Object startDateObj = null;
						if (startDate != null && startDate != "") {
							Date startDateNew = Date.valueOf(startDate);
							startDateObj = startDateNew;
						}
						dataMap.put("START_DATE", startDateObj);
						dataMap.put("MONTH_SAL", monthSal);
						dataMap.put("DAYS_TO_WORK", daystowork);
						dataMap.put("ANNUAL_SALARY", annualSalary);
						dataMap.put("POSSIBLE_WORK_DAYS", possibleworkdays);
						dataMap.put("MONTH_SAL1", monthSal1);
						dataMap.put("MONTHS_TO_WORK", monthstowork);
						dataMap.put("PROJECTED_EARNED_SALARY", projectedEarnedSalary);						
						dataMap.put("ANNUAL_SALARY1", annualSalary1);
						dataMap.put("PROJECTED_EARNED_SALARY1", projectedEarnedSalary1);
						dataMap.put("SETTLEMENT_AMOUNT", settlementAmount);
						dataMap.put("FIRST_MONTH_OFF", firstMonthOff);
						dataMap.put("SECOND_MONTH_OFF", secondMonthOff);								

						log.error("Connection Successfull");
						insertPayPlanData(conn, dataMap);

					} catch (SAXException e) {
						log.error("SAXException=" + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error(Arrays.toString(e.getStackTrace()));
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
		}
	}
	
	public void insertPayPlanData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_PAY_PLAN";
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
					log.info("The Vlaue is=" + value);
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
				log.error("Before Pay Plan Prepared stmt");
				preparedStmt.execute();
				conn.commit();
				log.error("After Pay Plan Prepared stmt");
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
