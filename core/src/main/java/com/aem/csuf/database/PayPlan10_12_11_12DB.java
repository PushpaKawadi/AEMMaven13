//package com.aem.csuf.database;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//
//import javax.jcr.Node;
//import javax.jcr.PathNotFoundException;
//import javax.jcr.RepositoryException;
//import javax.jcr.ValueFormatException;
//import javax.sql.DataSource;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.apache.sling.api.resource.Resource;
//import org.apache.sling.api.resource.ResourceResolver;
//import org.osgi.framework.Constants;
//import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.Reference;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.xml.sax.SAXException;
//
//import com.adobe.granite.workflow.WorkflowException;
//import com.adobe.granite.workflow.WorkflowSession;
//import com.adobe.granite.workflow.exec.WorkItem;
//import com.adobe.granite.workflow.exec.WorkflowProcess;
//import com.adobe.granite.workflow.metadata.MetaDataMap;
////import com.aem.csuf.filenet.PayPlan10_12_11_12Filenet;
//import com.day.commons.datasource.poolservice.DataSourcePool;
//
//@Component(property = { Constants.SERVICE_DESCRIPTION + "=PayPlan10_12_11_12 Save in DB",
//		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=PayPlan10_12_11_12DB" })
//public class PayPlan10_12_11_12DB implements WorkflowProcess {
//
//	private static final Logger log = LoggerFactory.getLogger(PayPlan10_12_11_12DB.class);
//
//	@Override
//	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
//			throws WorkflowException {
//		Connection conn = null;
//
//		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
//		String payloadPath = workItem.getWorkflowData().getPayload().toString();
//		Document doc = null;
//		InputStream is = null;
//		
//		String empId = "";
//		String firstName = "";
//		String lastName = "";		
//		String empRCD = "";
//		String extension = "";
//		String scoPositionNumber = "";
//		String timeBase = "";
//		String statusMenu  = "";
//		String cbid  = "";
//		String classifications = "";
//		String grade = "";
//		String cmsPositionNumber = "";
//		String departmentName = "";
//		String departmentID = "";
//		String payPlanCHK = "";
//		String effectiveDate  = "";
//		String planSelected = "";
//		String name1 = "";
//		String monthOff1 = "";
//		String monthOff2 = "";
//		String employeeSignature = "";
//		String empDate = "";
//		String onCycle = "";
//		String offCycle = "";
//		String currentMonthSalary = "";
//		String adjustedSalary = "";
//		String dateDiscussed  = "";
//		String payPlan11 = "";
//		String payPlan10 = "";
//		String startDate = "";
//		String monthSal = "";
//		String daysToWork = "";
//		String annualSalary = "";
//		String possibleWorkDays = "";
//		String monthSal1 = "";
//		String monthsToWork = "";
//		String projectedEarnedSalary = "";
//		String annualSalary1 = "";
//		String projectedEarnedSalary1 = "";
//		String settlementAmount = "";
//		String firstMonthOff  = "";
//		String secondMonthOff = "";
//		String approvalRecommendedYes = "";
//		String appropriateAdminName = "";
//		String date1 = "";
//		String approvedGrantedYes = "";					
//		String vpSignature = "";
//		String date2 = "";
//
//		LinkedHashMap<String, Object> dataMap = null;
//
//		Resource xmlNode = resolver.getResource(payloadPath);
//
//		Iterator<Resource> xmlFiles = xmlNode.listChildren();
//		// Get the payload path and iterate the path to find Data.xml, Use
//		// Document
//		// factory to parse the xml and fetch the required values for the
//		// filenet
//		// attachment
//		while (xmlFiles.hasNext()) {
//			Resource attachmentXml = xmlFiles.next();
//			log.info("xmlFiles inside ");
//			String filePath = attachmentXml.getPath();
//
//			log.info("filePath for PayPlan_10_12_11_12=" + filePath);
//			if (filePath.contains("Data.xml")) {
//				filePath = attachmentXml.getPath().concat("/jcr:content");
//				log.info("xmlFiles for PayPlan_10_12_11_12=" + filePath);
//				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);
//				try {
//					is = subNode.getProperty("jcr:data").getBinary().getStream();
//				} catch (ValueFormatException e2) {
//					log.error("Exception1 for PayPlan_10_12_11_12=" + e2.getMessage());
//					e2.printStackTrace();
//				} catch (PathNotFoundException e2) {
//					log.error("Exception2 for PayPlan_10_12_11_12=" + e2.getMessage());
//					e2.printStackTrace();
//				} catch (RepositoryException e2) {
//					log.error("Exception3 for PayPlan_10_12_11_12=" + e2.getMessage());
//					e2.printStackTrace();
//				}
//
//				try {
//					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//					DocumentBuilder dBuilder = null;
//					try {
//						dBuilder = dbFactory.newDocumentBuilder();
//					} catch (ParserConfigurationException e1) {
//						log.info("ParserConfigurationException=" + e1);
//						e1.printStackTrace();
//					}
//					try {
//						doc = dBuilder.parse(is);
//					} catch (IOException e1) {
//						log.info("IOException=" + e1);
//						e1.printStackTrace();
//					}
//					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
//					for (int temp = 0; temp < nList.getLength(); temp++) {
//						org.w3c.dom.Node nNode = nList.item(temp);
//
//						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
//
//							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
//
//							empId = eElement.getElementsByTagName("empl_ID")
//									.item(0).getTextContent();
//									log.info("empId Value is: " + empId);
//
//							firstName = eElement.getElementsByTagName("first_Name")
//									.item(0).getTextContent();
//									log.info("first_NameValue is: " + firstName);
//
//							lastName = eElement.getElementsByTagName("last_Name")
//									.item(0).getTextContent();
//									log.info("last_Name Value is: " + lastName);							
//
//							empRCD = eElement.getElementsByTagName("empl_RCD")
//									.item(0).getTextContent();
//									log.info("empRCD Value is: " + empRCD);
//
//							extension = eElement.getElementsByTagName("extension")
//									.item(0).getTextContent();
//									log.info("extension Value is: " + extension);
//
//							scoPositionNumber = eElement.getElementsByTagName("scoPositionNumber")
//									.item(0).getTextContent();
//									log.info("scoPositionNumber Value is: " + scoPositionNumber);
//
//							timeBase = eElement.getElementsByTagName("timebase")
//									.item(0).getTextContent();
//									log.info("timeBase Value is: " + timeBase);
//
//							statusMenu = eElement.getElementsByTagName("statusMenu")
//									.item(0).getTextContent();
//									log.info("statusMenu Value is: " + statusMenu);
//
//							cbid = eElement.getElementsByTagName("cbid")
//									.item(0).getTextContent();
//									log.info("cbid Value is: " + cbid);
//
//							classifications = eElement.getElementsByTagName("classification")
//									.item(0).getTextContent();
//							log.info("classification Value is: " + classifications);
//
//							grade = eElement.getElementsByTagName("grade")
//									.item(0).getTextContent();
//							log.info("grade Value is: " + grade);		
//
//							cmsPositionNumber = eElement.getElementsByTagName("cmsPositionNumber")
//									.item(0).getTextContent();
//							log.info("cmsPositionNumber Value is: " + cmsPositionNumber);		
//
//							departmentName = eElement.getElementsByTagName("departmentName")
//									.item(0).getTextContent();
//							log.info("departmentName Value is: " + departmentName);
//
//							departmentID = eElement.getElementsByTagName("departmentID")
//									.item(0).getTextContent();
//							log.info("departmentID Value is: " + departmentID);
//
//							payPlanCHK = eElement.getElementsByTagName("payPlanCHK")
//							.item(0).getTextContent();
//						log.info("payPlanCHK Value is: " + payPlanCHK);
//
//							effectiveDate = eElement.getElementsByTagName("effectiveDate")
//									.item(0).getTextContent();
//							log.info("effectiveDate Value is: " + effectiveDate);
//
//							planSelected = eElement.getElementsByTagName("planSelected")
//									.item(0).getTextContent();
//							log.info("planSelected Value is: " + planSelected);
//
//							name1 = eElement.getElementsByTagName("name1")
//									.item(0).getTextContent();
//							log.info("name1 Value is: " + name1);
//
//							monthOff1 = eElement.getElementsByTagName("monthOff1")
//							.item(0).getTextContent();
//							log.info("monthOff1 Value is: " + monthOff1);
//
//							monthOff2 = eElement.getElementsByTagName("monthOff2")
//							.item(0).getTextContent();
//							log.info("monthOff2 Value is: " + monthOff2);
//
//							employeeSignature = eElement.getElementsByTagName("employeeSignature")
//							.item(0).getTextContent();
//							log.info("employeeSignature Value is: " + employeeSignature);
//
//							empDate = eElement.getElementsByTagName("empDate")
//							.item(0).getTextContent();
//							log.info("empDate Value is: " + empDate);
//
//							onCycle = eElement.getElementsByTagName("onCycle")
//							.item(0).getTextContent();
//							log.info("OnCycle Value is: " + onCycle);
//
//							offCycle = eElement.getElementsByTagName("offCycle")
//							.item(0).getTextContent();
//							log.info("offCycle Value is: " + offCycle);
//
//							currentMonthSalary = eElement.getElementsByTagName("currentMonthlySalary")
//							.item(0).getTextContent();
//							log.info("currentMonthSalary Value is: " + currentMonthSalary);
//
//							adjustedSalary = eElement.getElementsByTagName("adjustedSalary")
//							.item(0).getTextContent();
//							log.info("adjustedSalary Value is: " + adjustedSalary);
//			
//							dateDiscussed = eElement.getElementsByTagName("dateDiscussed")
//							.item(0).getTextContent();
//							log.info("dateDiscussed Value is: " + dateDiscussed);
//
//							payPlan10 = eElement.getElementsByTagName("payPlan10")
//							.item(0).getTextContent();
//							log.info("payPlan10 Value is: " + payPlan10);
//							
//							payPlan11 = eElement.getElementsByTagName("payPlan11")
//							.item(0).getTextContent();
//							log.info("payPlan11 Value is: " + payPlan11);
//
//							startDate = eElement.getElementsByTagName("startDate")
//							.item(0).getTextContent();
//							log.info("StartDate Value is: " + startDate);
//							
//							monthSal = eElement.getElementsByTagName("monthSal")
//							.item(0).getTextContent();
//							log.info("monthSal Value is: " + monthSal);
//							
//							daysToWork = eElement.getElementsByTagName("daystowork")
//							.item(0).getTextContent();
//							log.info("daystowork Value is: " + daysToWork);
//							
//							annualSalary = eElement.getElementsByTagName("annualSalary")
//							.item(0).getTextContent();
//							log.info("annualSalary Value is: " + annualSalary);
//
//							possibleWorkDays = eElement.getElementsByTagName("possibleworkdays")
//							.item(0).getTextContent();
//							log.info("possibleWorkDays Value is: " + possibleWorkDays);
//
//							monthSal1 = eElement.getElementsByTagName("monthSal1")
//							.item(0).getTextContent();
//							log.info("monthSal1 Value is: " + monthSal1);
//						
//							monthsToWork = eElement.getElementsByTagName("monthstowork")
//							.item(0).getTextContent();
//							log.info("monthsToWork Value is: " + monthsToWork);
//
//							projectedEarnedSalary = eElement.getElementsByTagName("projectedEarnedSalary")
//							.item(0).getTextContent();
//							log.info("projectedEarnedSalary Value is: " + projectedEarnedSalary);
//
//							annualSalary1 = eElement.getElementsByTagName("annualSalary1")
//							.item(0).getTextContent();
//							log.info("annualSalary1 Value is: " + annualSalary1);	
//							
//							projectedEarnedSalary1 = eElement.getElementsByTagName("projectedEarnedSalary1")
//							.item(0).getTextContent();
//							log.info("projectedEarnedSalary1 Value is: " + projectedEarnedSalary1);
//						
//							settlementAmount = eElement.getElementsByTagName("settlementAmount")
//							.item(0).getTextContent();
//							log.info("settlementAmount Value is: " + settlementAmount);
//
//							firstMonthOff = eElement.getElementsByTagName("firstMonthOff")
//							.item(0).getTextContent();
//							log.info("firstMonthOff Value is: " + firstMonthOff);
//
//							
//							secondMonthOff = eElement.getElementsByTagName("secondMonthOff")
//							.item(0).getTextContent();
//							log.info("secondMonthOff Value is: " + secondMonthOff);
//
//							approvalRecommendedYes = eElement.getElementsByTagName("approvalRecommendedYes")
//							.item(0).getTextContent();
//							log.info("approvalRecommendedYes Value is: " + approvalRecommendedYes);
//
//							appropriateAdminName = eElement.getElementsByTagName("appropriateAdminName")
//							.item(0).getTextContent();
//							log.info("appropriateAdminName Value is: " + appropriateAdminName);
//
//							date1 = eElement.getElementsByTagName("date1")
//							.item(0).getTextContent();
//							log.info("date1 Value is: " + date1);
//
//							approvedGrantedYes = eElement.getElementsByTagName("approvalGrantedYes")
//							.item(0).getTextContent();
//							log.info("approvalGrantedYes Value is: " + approvedGrantedYes);
//
//							vpSignature = eElement.getElementsByTagName("vpSignature")
//							.item(0).getTextContent();
//							log.info("vpSignature Value is: " + vpSignature);
//										
//							date2 = eElement.getElementsByTagName("date2")
//							.item(0).getTextContent();
//							log.info("date2 Value is: " + date2);
//												
//						}
//					}
//
//					dataMap = new LinkedHashMap<String, Object>();
//
//
//					dataMap.put("EMPL_ID", empId);
//					dataMap.put("FIRST_NAME", lastName);
//					dataMap.put("LAST_NAME", firstName);
//					dataMap.put("EMPL_RCD", empRCD);
//					dataMap.put("EXTENSION", extension);
//					dataMap.put("SCO_POSITION_NUMBER", scoPositionNumber);
//					dataMap.put("TIMEBASE", timeBase);
//					dataMap.put("STATUS_MENU", statusMenu);
//					dataMap.put("CBID", cbid);
//					dataMap.put("CLASSIFICATIONS", classifications);
//					dataMap.put("GRADE", grade);					
//					dataMap.put("CMS_POSITION_NUMBER", cmsPositionNumber);
//					dataMap.put("DEPARTMENT_NAME", departmentName);					
//					dataMap.put("DEPARTMENT_ID", departmentID);	
//					dataMap.put("PAYPLAN_CHK", payPlanCHK);				
//
//					Object effectiveDateObj= null;
//					if(effectiveDate != null && effectiveDate != "") {
//						Date effectiveDateNew = Date.valueOf(effectiveDate);
//						effectiveDateObj = effectiveDateNew;
//					}
//					dataMap.put("EFFECTIVE_DATE", effectiveDateObj);
//					dataMap.put("PLAN_SELECTED", planSelected);
//					dataMap.put("NAME1", name1);
//					dataMap.put("MONTHOFF1", monthOff1);
//					dataMap.put("MONTHOFF2", monthOff2);	
//					dataMap.put("EMPLOYEE_SIGNATURE", employeeSignature);
//
//					Object empDateObj= null;
//					if(empDate != null && empDate != "") {
//						Date empDateNew = Date.valueOf(empDate);
//						empDateObj = empDateNew;
//					}
//					dataMap.put("EMP_DATE", empDateObj);
//<<<<<<< HEAD
//					dataMap.put("ADMIN_SIGN", adminSign);
//					dataMap.put("APPROVAL_RECOMMENDED_YES", approvalRecommendedYes);
//					dataMap.put("APPROPRIATE_ADMIN_NAME", appropriateAdminName);
//
//					Object date1Obj = null;
//					if (date1 != null && date1 != "") {
//						Date date1New = Date.valueOf(date1);
//						date1Obj = date1New;
//					}
//					dataMap.put("DATE1", date1Obj);
//					dataMap.put("APPROVAL_GRANTED_YES", approvalGrantedYes);
//					dataMap.put("VP_SIGNATURE", vpSignature);
//
//					Object date2Obj = null;
//					if (date2 != null && date2 != "") {
//						Date date2New = Date.valueOf(date2);
//						date2Obj = date2New;
//					}
//					dataMap.put("DATE2", date2Obj);
//
//					Object oncycleObj = null;
//					if (oncycle != null && oncycle != "") {
//						Date oncycleNew = Date.valueOf(oncycle);
//						oncycleObj = oncycleNew;
//					}
//					dataMap.put("ON_CYCLE", oncycleObj);
//
//					Object offcycleObj = null;
//					if (offcycle != null && offcycle != "") {
//						Date offcycleNew = Date.valueOf(offcycle);
//						offcycleObj = offcycleNew;
//					}
//					dataMap.put("OFF_CYCLE", offcycleObj);
//					dataMap.put("CURRENT_MONTHLY_SALARY", currentMonthSalary);
//					dataMap.put("ADJUSTED_SALARY", adjustedSalary);
//
//					Object dateDiscussedObj = null;
//					if (dateDiscussed != null && dateDiscussed != "") {
//=======
//					dataMap.put("ON_CYCLE", onCycle);
//					dataMap.put("OFF_CYCLE", offCycle);
//					dataMap.put("CURRENT_MONTHLY_SALARY", currentMonthSalary);
//					dataMap.put("ADJUSTED_SALARY", adjustedSalary);
//
//					Object dateDiscussedObj= null;
//					if(dateDiscussed != null && dateDiscussed != "") {
//>>>>>>> 986669274a715be47dd5e805ec0e463380721f7e
//						Date dateDiscussedNew = Date.valueOf(dateDiscussed);
//						dateDiscussedObj = dateDiscussedNew;
//					}
//					dataMap.put("DATE_DISCUSSED", dateDiscussedObj);
//<<<<<<< HEAD
//					dataMap.put("PAYPLAN10", payPlan10);
//=======
//						dataMap.put("PAYPLAN10", payPlan10);
//>>>>>>> 986669274a715be47dd5e805ec0e463380721f7e
//					dataMap.put("PAYPLAN11", payPlan11);
//
//					Object dstartDateObj= null;
//					if(startDate != null && startDate != "") {
//						Date startDateNew = Date.valueOf(dateDiscussed);
//						dstartDateObj = startDateNew;
//					}
//					dataMap.put("START_DATE", dstartDateObj);
//					dataMap.put("MONTH_SAL", monthSal);
//					dataMap.put("DAYS_TO_WORK", daysToWork);
//					dataMap.put("ANNUAL_SALARY", annualSalary);
//					dataMap.put("POSSIBLE_WORK_DAYS", possibleWorkDays);
//					dataMap.put("MONTH_SAL1", monthSal1);
//					dataMap.put("MONTHS_TO_WORK", monthsToWork);
//					dataMap.put("PROJECTED_EARNED_SALARY", projectedEarnedSalary);
//					dataMap.put("ANNUAL_SALARY1", annualSalary1);
//					dataMap.put("PROJECTED_EARNED_SALARY1", projectedEarnedSalary1);
//					dataMap.put("SETTLEMENT_AMOUNT", settlementAmount);
//					dataMap.put("FIRST_MONTH_OFF", firstMonthOff);
//					dataMap.put("SECOND_MONTH_OFF", secondMonthOff);
//					dataMap.put("APPROVAL_RECOMMENDED_YES", approvalRecommendedYes);
//					dataMap.put("APPROPRIATE_ADMIN_NAME", appropriateAdminName);
//
//					Object date1Obj= null;
//					if(date1 != null && date1 != "") {
//						Date date1New = Date.valueOf(date1);
//						date1Obj = date1New;
//					}
//					dataMap.put("DATE1", date1Obj);
//					dataMap.put("APPROVAL_GRANTED_YES", approvedGrantedYes);
//					dataMap.put("VP_SIGNATURE", vpSignature);
//
//					Object date2Obj= null;
//					if(date2 != null && date2 != "") {
//						Date date2New = Date.valueOf(date1);
//						date2Obj = date2New;
//					}
//					dataMap.put("DATE2", date2Obj);
//
//
//				} catch (SAXException e) {
//					log.error("SAXException=" + e.getMessage());
//					e.printStackTrace();
//				} catch (Exception e) {
//					log.error("Exception1");
//					log.error("Exception=" + e.getMessage());
//					e.printStackTrace();
//				} finally {
//					try {
//						is.close();
//					} catch (IOException e) {
//						log.error("IOException=" + e.getMessage());
//						e.printStackTrace();
//					}
//
//				}
//
//			}
//		}
//		conn = getConnection();
//		if (conn != null) {
//			log.error("Connection Successfull");
//			insertPayPlanData(conn, dataMap);
//		}
//	}
//
//	@Reference
//	private DataSourcePool source;
//
//	private Connection getConnection() {
//		log.info("Inside Get Connection");
//
//		DataSource dataSource = null;
//		Connection con = null;
//		try {
//			// Inject the DataSourcePool right here!
//			dataSource = (DataSource) source.getDataSource("AEMDBDEV");
//			con = dataSource.getConnection();
//			return con;
//
//		} catch (Exception e) {
//			log.error("Conn Exception=" + e.getMessage());
//			e.printStackTrace();
//		} /*
//			 * finally { try { if (con != null) { log.info("Conn Exec="); } } catch
//			 * (Exception exp) { exp.printStackTrace(); } }
//			 */
//		return null;
//	}
//
//	public void insertPayPlanData(Connection conn, LinkedHashMap<String, Object> dataMap) {
//		PreparedStatement preparedStmt = null;
//		log.error("conn=" + conn);
//		if (conn != null) {
//			try {
//				conn.setAutoCommit(false);
//			} catch (SQLException e1) {
//				log.error("SQLException=" + e1.getMessage());
//				e1.printStackTrace();
//			} catch (Exception e) {
//				log.error("Exception=" + e.getMessage());
//				e.printStackTrace();
//			}
//			String tableName = "AEM_PayPlan";
//			log.info("Table Name is: "+tableName);
//			StringBuilder sql = new StringBuilder("INSERT INTO  ").append(tableName).append(" (");
//			log.info("THE SQL QUERY IS="+sql);
//			StringBuilder placeholders = new StringBuilder();
//			for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
//				sql.append(iter.next());
//				placeholders.append("?");
//				if (iter.hasNext()) {
//					sql.append(",");
//					placeholders.append(",");
//				}
//			}
//			sql.append(") VALUES (").append(placeholders).append(")");
//			log.error("SQL=" + sql.toString());
//			try {
//				preparedStmt = conn.prepareStatement(sql.toString());
//			} catch (SQLException e1) {
//				log.error("Exception3");
//				log.error("SQLException=" + e1.getMessage());
//				e1.printStackTrace();
//			} catch (Exception e) {
//				log.error("Exception2");
//				log.error("Exception=" + e.getMessage());
//				e.printStackTrace();
//			}
//			int i = 0;
//			log.info("Datamap values=" + dataMap.values());
//			try {
//				for (Object value : dataMap.values()) {
//					if (value instanceof Date) {
//						log.error("Date=" + value);
//						preparedStmt.setDate(++i, (Date) value);
//					} else if (value instanceof Integer) {
//						log.error("Integ=" + value);
//						preparedStmt.setInt(++i, (Integer) value);
//					} else {
//						log.error("Else=" + value);
//						if (value != "" && value != null) {
//							preparedStmt.setString(++i, value.toString());
//						} else {
//							preparedStmt.setString(++i, null);
//						}
//					}
//					log.info("The Vlaue is=" + value);
//				}
//			} catch (SQLException e) {
//				log.error("SQLException=" + e.getMessage());
//				e.printStackTrace();
//			} catch (Exception e) {
//				log.error("Exception4");
//				log.error("Exception=" + e.getMessage());
//				e.printStackTrace();
//			}
//			try {
//				log.error("Before Prepared stmt");
//				preparedStmt.execute();
//				conn.commit();
//				log.error("After Prepared stmt");
//			} catch (SQLException e1) {
//				log.error("SQLException=" + e1.getMessage());
//				e1.printStackTrace();
//			} catch (Exception e) {
//				log.error("Exception5");
//				log.error("Exception=" + e.getMessage());
//				e.printStackTrace();
//			} finally {
//				if (preparedStmt != null) {
//					try {
//						preparedStmt.close();
//						conn.close();
//					} catch (SQLException e) {
//						log.error("SQLException=" + e.getMessage());
//						e.printStackTrace();
//					} catch (Exception e) {
//						log.error("Exception7");
//						log.error("Exception=" + e.getMessage());
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
//}
