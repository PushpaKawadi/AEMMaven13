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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Career Development Plan Save in DB",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=CareerDevelopmentPlanDB" })

public class ClassificationDecisionApealDB implements WorkflowProcess{

private static final Logger log = LoggerFactory.getLogger(CareerDevelopmentPlanDB.class);

    
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {

                log.info("Inside Method");
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

        String empID = "";
		String firstName = "";
        String lastName = "";
        String dateInitiated = "";
        String departmentName = "";		
		String departmentID = "";
		String emplRCD = "";
		String classification = "";
		String cbid = "";
		String shortTermsGoal = "";
		String longTermGoals = "";
		String education = "";
		String training = "";
		String workExperience = "";
		String education1 = "";
		String training1 = "";
		String workExperience1 = "";
		String comment = "";
		String employeeSignature = "";
		String employeeDate = "";
		String discussionCHK = "";
		String discussedDate = "";
		String supervisorSignature = "";
		String supervisorDate = "";
		String careerAdvise = "";	
		String analystSignature = "";
		String analystDate = "";

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

                            firstName = eElement.getElementsByTagName("firstName")
                                    .item(0).getTextContent();
                                    log.info("firstName Value is: " + firstName);

                            lastName = eElement.getElementsByTagName("lastName")
                                    .item(0).getTextContent();
                                    log.info("lastName Value is: " + lastName);

                            empID = eElement.getElementsByTagName("emplID")
                                    .item(0).getTextContent();
                                    log.info("empID Value is: " + empID);

                            departmentName = eElement.getElementsByTagName("departmentName")
                                    .item(0).getTextContent();
                            log.info("departmentName Value is: " + departmentName);

                            departmentID = eElement.getElementsByTagName("departmentID")
                                    .item(0).getTextContent();
                            log.info("departmentID Value is: " + departmentID);

                            emplRCD = eElement.getElementsByTagName("emplRCD")
                                    .item(0).getTextContent();
                            log.info("emplRCD Value is: " + emplRCD);

                            classification = eElement.getElementsByTagName("classification")
                                    .item(0).getTextContent();
                            log.info("classification Value is: " + classification);

                            cbid = eElement.getElementsByTagName("cbid")
                                    .item(0).getTextContent();
                            log.info("cbid Value is: " + cbid);

                            dateInitiated = eElement.getElementsByTagName("dateInitiated")
                                    .item(0).getTextContent();
                            log.info("dateInitiated Value is: " + dateInitiated);

                            shortTermsGoal = eElement.getElementsByTagName("shortTermsGoal")
                                    .item(0).getTextContent();
                            log.info("shortTermsGoal Value is: " + shortTermsGoal);

                            longTermGoals = eElement.getElementsByTagName("longTermGoals")
                                    .item(0).getTextContent();
                            log.info("longTermGoals Value is: " + longTermGoals);

                            education = eElement.getElementsByTagName("education")
                                    .item(0).getTextContent();
                            log.info("education Value is: " + education);

                            training = eElement.getElementsByTagName("training")
									.item(0).getTextContent();
							log.info("training Value is: " + training);

							workExperience = eElement.getElementsByTagName("workExperience1")
									.item(0).getTextContent();
                            log.info("workExperience1 Value is: " + workExperience1);
                            
                            education1 = eElement.getElementsByTagName("education")
                            .item(0).getTextContent();
                            log.info("education Value is: " + education);

                            training1 = eElement.getElementsByTagName("training")
                            .item(0).getTextContent();
                                log.info("training Value is: " + training);

                            workExperience1 = eElement.getElementsByTagName("workExperience1")
                            .item(0).getTextContent();
                             log.info("workExperience1 Value is: " + workExperience1);

							comment = eElement.getElementsByTagName("comment")
									.item(0).getTextContent();
							log.info("comment Value is: " + comment);

							employeeSignature = eElement.getElementsByTagName("employeeSignature")
									.item(0).getTextContent();
							log.info("employeeSignature Value is: " + employeeSignature);

							employeeDate = eElement.getElementsByTagName("employeeDate")
									.item(0).getTextContent();
							log.info("employeeDate Value is: " + employeeDate);

							discussionCHK = eElement.getElementsByTagName("discussionCHK")
									.item(0).getTextContent();
							log.info("discussionCHK Value is: " + discussionCHK);
							
							discussedDate = eElement.getElementsByTagName("discussedDate")
									.item(0).getTextContent();
							log.info("discussedDate Value is: " + discussedDate);
							
							supervisorSignature = eElement.getElementsByTagName("supervisorSignature")
									.item(0).getTextContent();
							log.info("supervisorSignature Value is: " + supervisorSignature);
							
							supervisorDate = eElement.getElementsByTagName("supervisorDate")
									.item(0).getTextContent();
							log.info("supervisorDate Value is: " + supervisorDate);
							
							careerAdvise = eElement.getElementsByTagName("careerAdviseYes")
									.item(0).getTextContent();
							log.info("careerAdviseYes Value is: " + careerAdvise);					
							
							analystSignature = eElement.getElementsByTagName("analystSignature")
									.item(0).getTextContent();
							log.info("analystSignature Value is: "+analystSignature);
							
							analystDate = eElement.getElementsByTagName("analystDate")
									.item(0).getTextContent();
							log.info("analystDate Value is: "+analystDate);								
						}
					}

					dataMap = new LinkedHashMap<String, Object>();
					
					dataMap.put("FIRST_NAME", firstName);
                    dataMap.put("LAST_NAME", lastName);
                    dataMap.put("EMP_ID", empID);										
					dataMap.put("DEPARTMENT_NAME", departmentName);
					dataMap.put("DEPARTMENT_ID", departmentID);
					dataMap.put("EMPL_RCD", emplRCD);
					dataMap.put("CLASSIFICATION1", classification);
                    dataMap.put("CBID", cbid);

                    Object dateInitiatedObj= null;
                    if(dateInitiated != null && dateInitiated != "") {
						Date dateInitiatedNew = Date.valueOf(dateInitiated);
						dateInitiatedObj = dateInitiatedNew;
					}	
                    dataMap.put("DATE_INITIATED", dateInitiatedObj);

					dataMap.put("SHORT_TERM_GOAL", shortTermsGoal);					
					dataMap.put("LONG_TERM_GOAL", longTermGoals);					
					dataMap.put("EDUCATION", education);					
					dataMap.put("TRAINING", training);					
                    dataMap.put("WORK_EXPERIENCE", workExperience);
                    dataMap.put("EDUCATION1", education1);					
					dataMap.put("TRAINING1", training1);					
					dataMap.put("WORK_EXPERIENCE1", workExperience1);		
					dataMap.put("COMMENTS", comment);
                    dataMap.put("EMPLOYEE_SIGNATURE", employeeSignature);
                    
                    Object employeeDateObj= null;
                    if(employeeDate != null && employeeDate != "") {
						Date employeeDateNew = Date.valueOf(employeeDate);
						employeeDateObj = employeeDateNew;
					}	
                    dataMap.put("EMPLOYEE_DATE", employeeDateObj);    

                    dataMap.put("DISCUSSION_CHK", discussionCHK);
                    
                    Object discussedDateObj= null;
                    if(discussedDate != null && discussedDate != "") {
						Date discussedDateNew = Date.valueOf(discussedDate);
						discussedDateObj = discussedDateNew;
					}	
                    dataMap.put("DISCUSSED_DATE", discussedDateObj); 

                    dataMap.put("SUPERVISOR_SIGNATURE", supervisorSignature);
                    
                    Object supervisorDateObj= null;
                    if(supervisorDate != null && supervisorDate != "") {
						Date asupervisorDateNew = Date.valueOf(supervisorDate);
						supervisorDateObj = asupervisorDateNew;
					}	
                    dataMap.put("SUPERVISOR_DATE", supervisorDateObj);    

					dataMap.put("CAREER_ADVISE", careerAdvise);
                    dataMap.put("ANALYST_SIGNATURE", analystSignature);
                    
                    Object analystDateObj= null;
                    if(analystDate != null && analystDate != "") {
						Date aanalystDateNew = Date.valueOf(analystDate);
						analystDateObj = aanalystDateNew;
					}	
					dataMap.put("ANALYST_DATE", analystDateObj);


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
			String tableName = "AEM_CAREER_DEVELOPMENT_PLAN";
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


