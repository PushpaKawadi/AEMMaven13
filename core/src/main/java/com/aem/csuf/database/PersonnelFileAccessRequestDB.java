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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Peronnel File Access Request Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=PersonalFileAccessRequestDB" })
public class PersonnelFileAccessRequestDB implements WorkflowProcess {

  	private static final Logger log = LoggerFactory.getLogger(PersonnelFileAccessRequestDB.class);

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

		String first_Name = "";
		String last_Name = "";
		String middle_Initials = "";
		String date_Initiated = "";
		String empl_ID = "";
		String campus_Phone = "";
		String cell_Phone = "";
		String dept_ID = "";
		String department = "";
		String email = "";
		String viewPersonnelFile = "";
		String obtainCopies = "";
		String copy1 = "";
		String copy2 = "";
		String copy3 = "";
		String copy4 = "";
		String copy5 = "";
		String copy6 = "";
		String authorizeUnionRep = "";
		String nameOfUnionRep = "";
		String other = "";
		String other1 = ""; 
		String other2 = "";
		String other3 = "";
		String other4 = "";
		String employeeSignature = "";
		String employeeDate = "";
		String hrdiCompletingRequest = "";
		String scheduledReviewDate = "";
		String scheduledReviewTime = "";
		String copiesProvidedDate = "";			
		String copieProvidedTime = "";
		

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
                           						

                            first_Name = eElement.getElementsByTagName("First_Name")
                                    .item(0).getTextContent();                                  

                            last_Name = eElement.getElementsByTagName("Last_Name")
                                    .item(0).getTextContent();                                   

                            middle_Initials = eElement.getElementsByTagName("Middle_Initials")
                                    .item(0).getTextContent();                                 

                            date_Initiated = eElement.getElementsByTagName("Date_Initiated")
                                    .item(0).getTextContent();                           

                            empl_ID = eElement.getElementsByTagName("Empl_ID")
                                    .item(0).getTextContent();   
                            log.info("Pushpa1"); 	

                            campus_Phone = eElement.getElementsByTagName("Campus_Phone")
                                    .item(0).getTextContent();                           

                            cell_Phone = eElement.getElementsByTagName("Cell_Phone")
                                    .item(0).getTextContent();                           

                            dept_ID = eElement.getElementsByTagName("Dept_ID")
                                    .item(0).getTextContent();                          

                            department = eElement.getElementsByTagName("Department")
                                    .item(0).getTextContent();                           

                            email = eElement.getElementsByTagName("Email")
                                    .item(0).getTextContent();    
                            
                            log.info("Pushpa2"); 

                            viewPersonnelFile = eElement.getElementsByTagName("ViewPersonnelFile")
                                    .item(0).getTextContent();                          

                            obtainCopies = eElement.getElementsByTagName("ObtainCopies")
                                    .item(0).getTextContent();                           

                            copy1 = eElement.getElementsByTagName("Copy1")
									.item(0).getTextContent();							

							copy2 = eElement.getElementsByTagName("Copy2")
									.item(0).getTextContent();                           
                            
                            copy3 = eElement.getElementsByTagName("Copy3")
                            .item(0).getTextContent();           
                            
                            log.info("Pushpa3"); 

                            copy4 = eElement.getElementsByTagName("Copy4")
                            .item(0).getTextContent();                              

                            copy5 = eElement.getElementsByTagName("Copy5")
                            .item(0).getTextContent();                            

                             copy6 = eElement.getElementsByTagName("Copy6")
									.item(0).getTextContent();							

							authorizeUnionRep = eElement.getElementsByTagName("AuthorizeUnionRep")
									.item(0).getTextContent();							

							nameOfUnionRep = eElement.getElementsByTagName("NameOfUnionRep")
									.item(0).getTextContent();							

							log.info("Pushpa4"); 
							
							other = eElement.getElementsByTagName("Other")
									.item(0).getTextContent();							
							
							other1 = eElement.getElementsByTagName("Other1")
									.item(0).getTextContent();							
							
							other2 = eElement.getElementsByTagName("Other2")
									.item(0).getTextContent();							
							
							other3 = eElement.getElementsByTagName("Other3")
									.item(0).getTextContent();							
							
							other4 = eElement.getElementsByTagName("Other4")
									.item(0).getTextContent();		
							
							log.info("Pushpa5"); 
							
							employeeSignature = eElement.getElementsByTagName("EmployeeSignature")
									.item(0).getTextContent();							
							
							employeeDate = eElement.getElementsByTagName("EmployeeDate")
									.item(0).getTextContent();							
							
							/*hrdiCompletingRequest = eElement.getElementsByTagName("HRDICompletingRequest")
									.item(0).getTextContent();*/					
							
							scheduledReviewDate = eElement.getElementsByTagName("ScheduledReviewDate")
									.item(0).getTextContent();												
							
							scheduledReviewTime = eElement.getElementsByTagName("ScheduledReviewTime")
									.item(0).getTextContent();							
							
							copiesProvidedDate = eElement.getElementsByTagName("CopiesProvidedDate")
									.item(0).getTextContent();
														
							copieProvidedTime = eElement.getElementsByTagName("CopiesProvidedTime")
									.item(0).getTextContent();	
							log.info("Pushpa6"); 
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					
					dataMap.put("FIRST_NAME", first_Name);
                    dataMap.put("LAST_NAME", last_Name);
                    dataMap.put("MIDDLE_INITIALS", middle_Initials);
                    
                    Object date_InitiatedObj= null;
                    if(date_Initiated != null && date_Initiated != "") {
						Date date_InitiatedNew = Date.valueOf(date_Initiated);
						date_InitiatedObj = date_InitiatedNew;
					}
					dataMap.put("DATE_INITIATED", date_InitiatedObj);
					dataMap.put("EMPL_ID", empl_ID);
					dataMap.put("CAMPUS_PHONE", campus_Phone);
					dataMap.put("CELL_PHONE", cell_Phone);
                    dataMap.put("DEPT_ID", dept_ID);	
                    dataMap.put("DEPARTMENT", department);
					dataMap.put("EMAIL", email);					
					dataMap.put("VIEW_PERSONNEL_FILE", viewPersonnelFile);					
					dataMap.put("OBTAIN_COPIES", obtainCopies);					
					dataMap.put("COPY1", copy1);					
                    dataMap.put("COPY2", copy2);
                    dataMap.put("COPY3", copy3);					
					dataMap.put("COPY4", copy4);					
					dataMap.put("COPY5", copy5);		
					dataMap.put("COPY6", copy6);
                    dataMap.put("AUTHORIZE_UNION_REP", authorizeUnionRep);	
                    dataMap.put("NAME_OF_UNION_REP", nameOfUnionRep); 
                    dataMap.put("OTHER", other);                                      
                    dataMap.put("OTHER1", other1);
                    dataMap.put("OTHER2", other2);                
                    dataMap.put("OTHER3", other3);    
					dataMap.put("OTHER4", other4);
                    dataMap.put("EMPLOYEE_SIGNATURE", employeeSignature); 
                    
                    Object employeeDateObj= null;
                    if(employeeDate != null && employeeDate != "") {
						Date employeeDateNew = Date.valueOf(employeeDate);
						employeeDateObj = employeeDateNew;
					}	
					dataMap.put("EMPLOYEE_DATE", employeeDateObj);					
					dataMap.put("HRDI_COMPLETING_REQUEST", "");
					
					Object scheduledReviewDateObj= null;
                    if(scheduledReviewDate != null && scheduledReviewDate != "") {
						Date scheduledReviewDateNew = Date.valueOf(scheduledReviewDate);
						scheduledReviewDateObj = scheduledReviewDateNew;
					}
                    dataMap.put("SCHEDULED_REVIEW_DATE", scheduledReviewDateObj);                
                    dataMap.put("SCHEDULED_REVIEW_TIME", scheduledReviewTime);

                    Object copiesProvidedDateObj= null;
                    if(copiesProvidedDate != null && copiesProvidedDate != "") {
						Date copiesProvidedDateNew = Date.valueOf(copiesProvidedDate);
						copiesProvidedDateObj = copiesProvidedDateNew;
					}
					dataMap.put("COPIES_PROVIDED_DATE", copiesProvidedDateObj);
                    dataMap.put("COPIES_PROVIDED_TIME", copieProvidedTime);                   
					


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
			insertCareerDevelopmentData(conn, dataMap);
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

	public void insertCareerDevelopmentData(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		log.error("conn=" + conn);
		log.error("dataMap==="+dataMap.size());
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
			String tableName = "AEM_PERSONNEL_FILE_ACCESS_REQ";
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
