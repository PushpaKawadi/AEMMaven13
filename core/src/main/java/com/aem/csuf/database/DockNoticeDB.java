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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Dock Notice DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFDockNoticeDB" })
public class DockNoticeDB implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(DockNoticeDB.class);

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
		String donationDate = "";
		String timeOfDay = "";
		String campusExt = "";
		String sickLeaveHours = "";
		String vacationHours = "";
		String donarCatagory = "";
		String signature = "";

		String dateAbsent = "";
		String hoursAbsent = "";

		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		conn = getConnection();
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

								emplId = eElement
										.getElementsByTagName("EmplID").item(0)
										.getTextContent();

								for (int i = 0; i < eElement
										.getElementsByTagName("AbsentDetails")
										.getLength(); i++) {
									for (int j = 0; j < eElement
											.getElementsByTagName(
													"AbsentDetails").item(i)
											.getChildNodes().getLength() - 1; j++) { // 2

										dateAbsent = eElement
												.getElementsByTagName(
														"AbsentDetails")
												.item(i).getChildNodes()
												.item(0).getTextContent();

										hoursAbsent = eElement
												.getElementsByTagName(
														"AbsentDetails")
												.item(i).getChildNodes()
												.item(1).getTextContent();

									}

									log.error("Here");
									insertCataLeaveDonationData(conn, emplId,
											dateAbsent, hoursAbsent);
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
	public void insertCataLeaveDonationData(Connection conn, String empID,
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
			
			/*finally {
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
			}*/
		}
	}
}
