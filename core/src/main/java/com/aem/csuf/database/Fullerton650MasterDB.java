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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Fullerton 650 Master Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Fullerton650MasterDB" })
public class Fullerton650MasterDB implements WorkflowProcess {

  	private static final Logger log = LoggerFactory.getLogger(Fullerton650MasterDB.class);

  	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {

                log.info("Inside Method");
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;
		
		String emailAddressList = null;
		String batchNumber = null;
		String payPeriod = null;
		String recordB = null;
		String recordBR = null;
		String recordL = null;
		String recordLR = null;
		String recordV = null;
		String recordVR = null;
		String fileNameBR = null;
		String fileNameBR1 = null;
		String fileNameL = null;
		String fileNameLR = null;
		String fileNameV = null;
		String fileNameVR = null;
		String authorizeSiganture = null;
		String dateInitiated = null;

		LinkedHashMap<String, Object> dataMap = null;

		Resource xmlNode = resolver.getResource(payloadPath);

		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		// Get the payload path and iterate the path to find Data.xml, Use
		// Document
		// factory to parse the xml and fetch the required values for the
		// filenet
        // attachment
        log.info("Welcome!!");

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

                        log.info("Inside For & Outside IF"); 

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            log.info("Inside IF"); 
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
                            log.info("Before Fields"); 	                    		

                    		emailAddressList = eElement.getElementsByTagName("emailAddressList")
                                    .item(0).getTextContent();
                                    log.info("firstName Value is: " + emailAddressList);

                            batchNumber = eElement.getElementsByTagName("emailAddressList")
                                    .item(0).getTextContent();
                                    log.info("lastName Value is: " + batchNumber);

                            payPeriod = eElement.getElementsByTagName("payPeriod")
                                    .item(0).getTextContent();
                                    log.info("empID Value is: " + payPeriod);

                            recordB = eElement.getElementsByTagName("recordB")
                                    .item(0).getTextContent();
                            log.info("departmentName Value is: " + recordB);

                            recordBR = eElement.getElementsByTagName("recordBR")
                                    .item(0).getTextContent();
                            log.info("departmentID Value is: " + recordBR);

                            recordL = eElement.getElementsByTagName("recordL")
                                    .item(0).getTextContent();
                            log.info("emplRCD Value is: " + recordL);
                    	
                    		recordLR = eElement.getElementsByTagName("recordLR")
                                    .item(0).getTextContent();
                            log.info("classification Value is: " + recordLR);

                            recordV = eElement.getElementsByTagName("recordV")
                                    .item(0).getTextContent();
                            log.info("cbid Value is: " + recordV);

                            recordVR = eElement.getElementsByTagName("recordVR")
                                    .item(0).getTextContent();
                            log.info("dateInitiated Value is: " + recordVR);

                            fileNameBR = eElement.getElementsByTagName("FileNameBR")
                                    .item(0).getTextContent();
                            log.info("shortTermsGoal Value is: " + fileNameBR);

                            fileNameBR1 = eElement.getElementsByTagName("FileNameBR1")
                                    .item(0).getTextContent();
                            log.info("longTermGoals Value is: " + fileNameBR);

                            fileNameL = eElement.getElementsByTagName("FileNameL")
                                    .item(0).getTextContent();
                            log.info("education Value is: " + fileNameL);

                            fileNameLR = eElement.getElementsByTagName("FileNameLR")
									.item(0).getTextContent();
							log.info("training Value is: " + fileNameLR);

							fileNameV = eElement.getElementsByTagName("FileNameV")
									.item(0).getTextContent();
                            log.info("workExperience1 Value is: " + fileNameV);
                            
                            fileNameVR = eElement.getElementsByTagName("FileNameVR")
                            .item(0).getTextContent();
                            log.info("education Value is: " + fileNameVR);

                            authorizeSiganture = eElement.getElementsByTagName("authorizedSiganture")
                            .item(0).getTextContent();
                                log.info("training Value is: " + authorizeSiganture);

                                dateInitiated = eElement.getElementsByTagName("dateInitiated")
                            .item(0).getTextContent();
                             log.info("workExperience1 Value is: " + dateInitiated);													
						}
					}

					dataMap = new LinkedHashMap<String, Object>();	
			        
					dataMap.put("EMAIL_ADDRESS_LIST", emailAddressList);
                    dataMap.put("BATCH_NUMBER", batchNumber);
                    
                    Object payPeriodObj= null;
                    if(payPeriod != null && payPeriod != "") {
						Date payPeriodNew = Date.valueOf(payPeriod);
						payPeriodObj = payPeriodNew;
					}	
                    dataMap.put("PAY_PERIOD", payPeriodObj);										
					dataMap.put("RECORD_B", recordB);
					dataMap.put("RECORD_BR", recordBR);
					dataMap.put("RECORD_L", recordL);
					dataMap.put("RECORD_LR", recordLR);
                    dataMap.put("RECORD_V", recordV);
					dataMap.put("RECORD_VR", recordVR);	
					dataMap.put("FILE_NAME_BR", fileNameBR);
					dataMap.put("FILE_NAME_BR1", fileNameBR1);					
					dataMap.put("FILE_NAME_L", fileNameL);					
					dataMap.put("FILE_NAME_LR", fileNameLR);					
                    dataMap.put("FILE_NAME_V", fileNameV);
                    dataMap.put("FILE_NAME_VR", fileNameVR);		
                    dataMap.put("AUTHORIZED_SIGNATURE", authorizeSiganture);
                    
                    Object dateInitiatedObj= null;
                    if(dateInitiated != null && dateInitiated != "") {
						Date dateInitiatedNew = Date.valueOf(dateInitiated);
						dateInitiatedObj = dateInitiatedNew;
					}	
                    dataMap.put("DATE_INITIATED", dateInitiatedObj);

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
		conn = jdbcConnectionService.getAemDEVDBConnection();
		if (conn != null) {
			log.error("Connection Successfull");
			insertFullertonMasterData(conn, dataMap);
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

	public void insertFullertonMasterData(Connection conn, LinkedHashMap<String, Object> dataMap) {
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
			String tableName = "AEM_FULLERTON_650_MASTER";
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
