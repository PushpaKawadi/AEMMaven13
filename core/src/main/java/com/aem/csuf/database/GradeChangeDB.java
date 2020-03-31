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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Grade Change DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFGradeChangeDB" })
public class GradeChangeDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(GradeChangeDB.class);

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



		String studentLastName = "";
		String studentFirstName = "";
		String studentID ="";
		String gradeChangeFrom ="";
		String gradeChangeTo = "";
		String reasonsToChange ="";
		String description ="";

		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
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

								// instID = eElement
								// .getElementsByTagName("InstructorID").item(0)
								// .getTextContent();

								for (int i = 0; i < eElement
										.getElementsByTagName("Row1")
										.getLength(); i++) {
									for (int j = 0; j < eElement
											.getElementsByTagName("Row1")
											.item(i).getChildNodes()
											.getLength() - 1; j++) {

										studentLastName = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(0).getTextContent();

										studentFirstName = eElement
												.getElementsByTagName("Row1")
												.item(i).getChildNodes()
												.item(1).getTextContent();
										
//										studentID = eElement
//												.getElementsByTagName("Row1")
//												.item(i).getChildNodes()
//												.item(2).getTextContent();
//										
//										gradeChangeFrom = eElement
//												.getElementsByTagName("Row1")
//												.item(i).getChildNodes()
//												.item(3).getTextContent();
//										
//										gradeChangeTo = eElement
//												.getElementsByTagName("Row1")
//												.item(i).getChildNodes()
//												.item(4).getTextContent();
//										
//										gradeChangeTo = eElement
//												.getElementsByTagName("Row1")
//												.item(i).getChildNodes()
//												.item(5).getTextContent();
//
//										reasonsToChange = eElement
//												.getElementsByTagName("Row1")
//												.item(i).getChildNodes()
//												.item(6).getTextContent();
//										
//										description = eElement
//												.getElementsByTagName("Row1")
//												.item(i).getChildNodes()
//												.item(7).getTextContent();
										
										
										break;

									}
									dataMap = new LinkedHashMap<String, Object>();
									dataMap.put("STUDENT_FIRST_NAME",
											studentFirstName);
									dataMap.put("STUDENT_LAST_NAME",
											studentLastName);
									
									insertGCData(conn,dataMap);

									// insertGradeChange(conn,
									// studentFirstName, studentFirstName);
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

	/**
	 * 
	 * @param conn
	 * @param dataMap
	 */
	public void insertGradeChangeOld(Connection conn, String studentFirstName,
			String studentLastName) {
		// log.error("empID=" + empID);
		log.error("First=" + studentFirstName);
		log.error("Last=" + studentLastName);
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

			String inserteventQuery = "insert into AEM_GRADE_CHANGE (STUDENT_FIRST_NAME,STUDENT_LAST_NAME) values (?,?)";
			try {
				preparedStmt = conn.prepareStatement(inserteventQuery);
				log.error("preparedStmt=" + preparedStmt);
				preparedStmt.setString(1, studentFirstName);
				preparedStmt.setString(2, studentLastName);
				log.error("Before GC");
				preparedStmt.execute();
				log.error("After GC");
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

	public void insertGCData(Connection conn,
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
			String tableName = "AEM_GRADE_CHANGE";
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
				log.error("Before Prepared stmt GC");
				preparedStmt.execute();
				conn.commit();
				log.error("After Prepared stmt GC");
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
