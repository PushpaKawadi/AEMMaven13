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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Dock Notice DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFDockNoticeDB" })
public class DockNoticeDB implements WorkflowProcess {

	int parentTable = 0;

	private static final Logger log = LoggerFactory
			.getLogger(DockNoticeDB.class);

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;
		String timeKeeper = "";
		String middleName = "";
		String dockDate = "";
		String ssn = "";
		String positionNumber = "";
		String firstName = "";
		String lastName = "";
		String emplId = "";
		String empRCD = "";
		String departmentID = "";
		String departmentName = "";
		String payPeriodYear = "";
		String payPeriodMonth = "";
		String dateAbsent = "";
		String hoursAbsent = "";
		String supervisorSignature = "";
		String supervisorDate = "";
		String workflowInstanceID = "";

		LinkedHashMap<String, Object> dataMapFormInfo = null;
		LinkedHashMap<String, Object> dataMapAbsentInfo = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
			while (xmlFiles.hasNext()) {
				workflowInstanceID = workItem.getWorkflow().getId();
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

								departmentID = eElement
										.getElementsByTagName("DeptID").item(0)
										.getTextContent();
								departmentName = eElement
										.getElementsByTagName("DeptName")
										.item(0).getTextContent();
								timeKeeper = eElement
										.getElementsByTagName("TimeKeeper")
										.item(0).getTextContent();
								dockDate = eElement
										.getElementsByTagName("DockDate")
										.item(0).getTextContent();
								
								
								lastName = eElement
										.getElementsByTagName("LastName")
										.item(0).getTextContent();
								firstName = eElement
										.getElementsByTagName("FirstName")
										.item(0).getTextContent();
								middleName = eElement
										.getElementsByTagName("MiddleName")
										.item(0).getTextContent();

								ssn = eElement.getElementsByTagName("SSN")
										.item(0).getTextContent();
								positionNumber = eElement
										.getElementsByTagName("PositionNumber")
										.item(0).getTextContent();
								payPeriodMonth = eElement
										.getElementsByTagName("PayPeriodMonth")
										.item(0).getTextContent();
								payPeriodYear = eElement
										.getElementsByTagName("PayPeriodYear")
										.item(0).getTextContent();
								
								emplId = eElement
										.getElementsByTagName("EmplID")
										.item(0).getTextContent();

								supervisorSignature = eElement
										.getElementsByTagName(
												"SupervisorSignature").item(0)
										.getTextContent();

								dataMapFormInfo = new LinkedHashMap<String, Object>();
								dataMapFormInfo.put("EMPL_ID", emplId);
								dataMapFormInfo.put("FIRST_NAME", firstName);
								dataMapFormInfo.put("LAST_NAME", lastName);
								dataMapFormInfo.put("MIDDLE_NAME", middleName);
								dataMapFormInfo.put("DEPT_ID", departmentID);
								dataMapFormInfo
										.put("DEPT_NAME", departmentName);
								dataMapFormInfo.put("TIME_KEEPER", timeKeeper);

								Object dockDateObj = null;
								if (dockDate != null && dockDate != "") {
									Date dockDateNew = Date.valueOf(dockDate);
									dockDateObj = dockDateNew;
								}
								dataMapFormInfo.put("DOCK_DATE", dockDateObj);

								dataMapFormInfo.put("SSN", ssn);
								dataMapFormInfo.put("POSITION_NUMBER",
										positionNumber);
								dataMapFormInfo.put("EMPL_RCD", empRCD);
								dataMapFormInfo.put("PAY_PERIOD_MONTH",
										payPeriodMonth);
								dataMapFormInfo.put("PAY_PERIOD_YEAR",
										payPeriodYear);
								dataMapFormInfo.put("SUPERVISOR_SIGNATURE",
										supervisorSignature);
								
								Object supDateObj = null;
								if (supervisorDate != null
										&& supervisorDate != "") {
									Date supDateNew = Date
											.valueOf(supervisorDate);
									supDateObj = supDateNew;
								}
								dataMapFormInfo.put("SUPERVISOR_DATE",
										supDateObj);

								dataMapFormInfo.put("WORKFLOW_INSTANCE_ID",
										workflowInstanceID);
								
								for (int i = 0; i < eElement
										.getElementsByTagName("AbsentDetails")
										.getLength(); i++) {
									for (int j = 0; j < eElement
											.getElementsByTagName(
													"AbsentDetails").item(i)
											.getChildNodes().getLength() - 1; j++) {

										dateAbsent = eElement
												.getElementsByTagName(
														"AbsentDetails")
												.item(i).getChildNodes()
												.item(0).getTextContent();
										
										Object dateAbsentObj = null;
										if (dateAbsent != null
												&& dateAbsent != "") {
											Date dateAbsentNew = Date
													.valueOf(dateAbsent);
											dateAbsentObj = dateAbsentNew;
										}

										hoursAbsent = eElement
												.getElementsByTagName(
														"AbsentDetails")
												.item(i).getChildNodes()
												.item(1).getTextContent();
										

										dataMapAbsentInfo = new LinkedHashMap<String, Object>();

										dataMapAbsentInfo.put("EMPL_ID", emplId);
										dataMapAbsentInfo.put("ABSENT_DATE",
												dateAbsentObj);
										dataMapAbsentInfo.put("ABSENT_HOURS",
												hoursAbsent);
										dataMapAbsentInfo.put(
												"WORKFLOW_INSTANCE_ID",
												workflowInstanceID);
										
										
										if (parentTable == 0) {
											insertDockForm(conn,
													dataMapFormInfo);
										}

										if (parentTable == 1) {
										insertDockAbsentForm(conn,
												dataMapAbsentInfo);
										}
										break;

									}

									// log.error("Here");
									// insertDockStudentInfo(conn, emplId,
									// dateAbsent, hoursAbsent);

								}

							}
						}

					} catch (SAXException e) {
						log.error("SAXException=" + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error("Exception1");
						log.error("Exception=" + e.getMessage());
						e.printStackTrace();
					} finally {
						try {
							parentTable = 0;
							log.info("Resetting parentTable in finally block==" + parentTable);
							is.close();
							conn.close();
						} catch (IOException e) {
							log.error("IOException=" + e.getMessage());
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			}

			// log.error("Connection Successfull");
			// insertCataLeaveDonationData(conn,emplId, dateAbsent,hoursAbsent);
		}
	}

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */
	public void insertDockAbsent(Connection conn, String empID,
			String dateAbsent, String hoursAbsent) {
		log.error("empID=" + empID);
		log.error("dateAbsent=" + dateAbsent);
		log.error("hoursAbsent=" + hoursAbsent);
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

			String inserteventQuery = "insert into Dock_Notice (EMPID,ABSENT_DATE,ABSENT_HOURS) values (?,?,?)";
			try {
				preparedStmt = conn.prepareStatement(inserteventQuery);
				log.error("preparedStmt=" + preparedStmt);
				preparedStmt.setString(1, empID);
				preparedStmt.setDate(2, Date.valueOf(dateAbsent));
				preparedStmt.setInt(3, Integer.valueOf(hoursAbsent));
				log.error("Befor");
				preparedStmt.execute();
				log.error("After");
				conn.commit();
			} catch (SQLException e2) {
				log.error("aaa=" + e2.getMessage());
				e2.printStackTrace();
			} catch (Exception e2) {
				log.error("e2=" + e2.getMessage());
				e2.printStackTrace();
			}

			/*
			 * finally { if (preparedStmt != null) { try { preparedStmt.close();
			 * conn.close(); } catch (SQLException e) {
			 * log.error("SQLException=" + e.getMessage()); e.printStackTrace();
			 * } catch (Exception e) { log.error("Exception=" + e.getMessage());
			 * e.printStackTrace(); } } }
			 */
		}
	}

	public void insertDockForm(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_DOCK_NOTICE_FORM";
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
				log.error("Before Dock Notice");
				preparedStmt.execute();
				conn.commit();
				parentTable = 1;
				log.error("parentTable=" + parentTable);
				log.error("After Dock Notice");
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						log.info("preparedstmt closed");
						// conn.close();
					} catch (SQLException e) {
						log.error("SQLException=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void insertDockAbsentForm(Connection conn,
			LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		log.error("Pushpa=" + dataMap);
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
			String tableName = "AEM_DOCK_NOTICE_ABSENT_INFO";
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
				log.error("Exception4");
				log.error("Exception=" + e.getMessage());
				e.printStackTrace();
			}
			try {
				log.error("Before Prepared stmt GC Student Info");
				preparedStmt.execute();
				conn.commit();
				log.error("After Prepared stmt GC Student Info");
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
						// conn.close();
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
