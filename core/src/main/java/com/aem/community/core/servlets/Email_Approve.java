package com.aem.community.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
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
//MessageServiceGateway API
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Email To Instructor And Chair",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Instructor and Chair Email_Approve" })

public class Email_Approve implements WorkflowProcess {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private ResourceResolverFactory resolverFactory;

	// Inject a MessageGatewayService
	@Reference
	private MessageGatewayService messageGatewayService;	

	String caseId = null;
	String Courses = null;
	String cwid = null;
	String StudentFName = null;
	String SudentLName = null;
	String sectionNumber = null;

	String CourseName = null;
	String CourseName1 = null;
	String CourseName2 = null;
	String CourseName3 = null;
	String CourseName4 = null;
	String CourseName5 = null;
	String CourseName6 = null;
	String CourseName7 = null;
	String CourseName8 = null;
	String CourseName9 = null;
	String CourseName10 = null;
	String CourseName11 = null;
	String CourseName12 = null;
	String CourseName13 = null;
	String CourseName14 = null;
	String CourseName15 = null;

	String instEmailAdd1 = null;
	String instEmailAdd2 = null;
	String instEmailAdd3 = null;
	String instEmailAdd4 = null;
	String instEmailAdd5 = null;
	String instEmailAdd6 = null;
	String instEmailAdd7 = null;
	String instEmailAdd8 = null;
	String instEmailAdd9 = null;
	String instEmailAdd10 = null;
	String instEmailAdd11 = null;
	String instEmailAdd12 = null;
	String instEmailAdd13 = null;
	String instEmailAdd14 = null;
	String instEmailAdd15 = null;
	
	String instName1 = null;
	String instName2 = null;
	String instName3 = null;
	String instName4 = null;
	String instName5 = null;
	String instName6 = null;
	String instName7 = null;
	String instName8 = null;
	String instName9 = null;
	String instName10 = null;
	String instName11 = null;
	String instName12 = null;
	String instName13 = null;
	String instName14 = null;
	String instName15 = null;

	String chairEmailAdd1 = null;
	String chairEmailAdd2 = null;
	String chairEmailAdd3 = null;
	String chairEmailAdd4 = null;
	String chairEmailAdd5 = null;
	String chairEmailAdd6 = null;
	String chairEmailAdd7 = null;
	String chairEmailAdd8 = null;
	String chairEmailAdd9 = null;
	String chairEmailAdd10 = null;
	String chairEmailAdd11 = null;
	String chairEmailAdd12 = null;
	String chairEmailAdd13 = null;
	String chairEmailAdd14 = null;
	String chairEmailAdd15 = null;

	public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
		log.info("Custom Email Start");

		ResourceResolver resolver = wfsession.adaptTo(ResourceResolver.class);
		String payloadPath = item.getWorkflowData().getPayload().toString();

		Document doc = null;
		InputStream is = null;

		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		log.info("xmlFiles is " + xmlFiles);
		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.error("xmlFiles inside ");

			String filePath = attachmentXml.getPath();

			// log.error("filePath= "+filePath);
			if (filePath.contains("Data.xml")) {
				log.info("FileName=" + filePath);

				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("Content=" + filePath);

				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
				} catch (ValueFormatException e2) {
					log.info("Exception1=" + e2);
					e2.printStackTrace();
				} catch (PathNotFoundException e2) {
					log.info("Exception2=" + e2);
					e2.printStackTrace();
				} catch (RepositoryException e2) {
					log.info("Exception3=" + e2);
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
					// XPath xpath = XPathFactory.newInstance().newXPath();
					doc.getDocumentElement().normalize();

					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);
						
						log.info("THE VALUE OF nLIST is: "+nList.toString());

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

							Courses = eElement.getElementsByTagName("CourseNumberList").item(0).getTextContent();
							cwid = eElement.getElementsByTagName("StudentID").item(0).getTextContent();
							StudentFName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
							SudentLName = eElement.getElementsByTagName("LastName").item(0).getTextContent();
							//sectionNumber = eElement.getElementsByTagName("")
							

							String cb1 = eElement.getElementsByTagName("CB1").item(0).getTextContent();
							String cb2 = eElement.getElementsByTagName("CB2").item(0).getTextContent();
							String cb3 = eElement.getElementsByTagName("CB3").item(0).getTextContent();
							String cb4 = eElement.getElementsByTagName("CB4").item(0).getTextContent();
							String cb5 = eElement.getElementsByTagName("CB5").item(0).getTextContent();
							String cb6 = eElement.getElementsByTagName("CB6").item(0).getTextContent();
							String cb7 = eElement.getElementsByTagName("CB7").item(0).getTextContent();
							String cb8 = eElement.getElementsByTagName("CB8").item(0).getTextContent();
							String cb9 = eElement.getElementsByTagName("CB9").item(0).getTextContent();
							String cb10 = eElement.getElementsByTagName("CB10").item(0).getTextContent();
							String cb11 = eElement.getElementsByTagName("CB11").item(0).getTextContent();
							String cb12 = eElement.getElementsByTagName("CB12").item(0).getTextContent();
							String cb13 = eElement.getElementsByTagName("CB13").item(0).getTextContent();
							String cb14 = eElement.getElementsByTagName("CB14").item(0).getTextContent();
							String cb15 = eElement.getElementsByTagName("CB15").item(0).getTextContent();

							if (cb1.equals("1")) {
								log.info("**********CHECKBOX - 1**********");
								
								instEmailAdd1 = eElement.getElementsByTagName("InstructorEmail1").item(0)
										.getTextContent();
								
								instName1 = eElement.getElementsByTagName("InstructorLname1").item(0)
										.getTextContent();
								
								chairEmailAdd1 = eElement.getElementsByTagName("ChairEmailID1").item(0)
										.getTextContent();
																
								CourseName1 = eElement.getElementsByTagName("CourseNo1").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd1, chairEmailAdd1, CourseName1, instName1);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 1: "+e);
									e.printStackTrace();
								}

							}
							if (cb2.equals("1")) {
								log.info("**********CHECKBOX - 2**********");
								
								instEmailAdd2 = eElement.getElementsByTagName("InstructorEmail2").item(0)
										.getTextContent();
								
								instName2 = eElement.getElementsByTagName("InstructorLname2").item(0)
										.getTextContent();
								
								
								chairEmailAdd2 = eElement.getElementsByTagName("ChairEmailID2").item(0)
										.getTextContent();					
								
								CourseName2 = eElement.getElementsByTagName("CourseNo2").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd2, chairEmailAdd2, CourseName2, instName2);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 2: "+e);
									e.printStackTrace();
								}
							}

							if (cb3.equals("1")) {
								log.info("**********CHECKBOX - 3**********");
								
								instEmailAdd3 = eElement.getElementsByTagName("InstructorEmail3").item(0)
										.getTextContent();
								
								instName3 = eElement.getElementsByTagName("InstructorLname3").item(0)
										.getTextContent();
								
								chairEmailAdd3 = eElement.getElementsByTagName("ChairEmailID3").item(0)
										.getTextContent();			
								
								CourseName3 = eElement.getElementsByTagName("CourseNo3").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd3, chairEmailAdd3, CourseName3, instName3);
									

								} catch (EmailException e) {
									log.info("Exception from CheckBox 3: "+e);
									e.printStackTrace();
								}
							}

							if (cb4.equals("1")) {
								log.info("**********CHECKBOX - 4**********");
								
								instEmailAdd4 = eElement.getElementsByTagName("InstructorEmail4").item(0)
										.getTextContent();

								instName4 = eElement.getElementsByTagName("InstructorLname4").item(0)
										.getTextContent();
								
								
								chairEmailAdd4 = eElement.getElementsByTagName("ChairEmailID4").item(0)
										.getTextContent();								
								
								
								CourseName4 = eElement.getElementsByTagName("CourseNo4").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd4, chairEmailAdd4, CourseName4, instName4);
								
								} catch (EmailException e) {
									log.info("Exception from CheckBox45: "+e);
									e.printStackTrace();
								}
							}

						if (cb5.equals("1")) {
								log.info("**********CHECKBOX - 5**********");
								
								instEmailAdd5 = eElement.getElementsByTagName("InstructorEmail5").item(0)
										.getTextContent();
								
								instName5 = eElement.getElementsByTagName("InstructorLname5").item(0)
										.getTextContent();
								
								chairEmailAdd5 = eElement.getElementsByTagName("ChairEmailID5").item(0)
										.getTextContent();								
								
								CourseName5 = eElement.getElementsByTagName("CourseNo5").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd5, chairEmailAdd5, CourseName5, instName5);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 5: "+e);
									e.printStackTrace();
								}
							}

							if (cb6.equals("1")) {
								log.info("**********CHECKBOX - 6**********");
								
								instEmailAdd6 = eElement.getElementsByTagName("InstructorEmail6").item(0)
										.getTextContent();
								
								instName6 = eElement.getElementsByTagName("InstructorLname6").item(0)
										.getTextContent();
								
								chairEmailAdd6 = eElement.getElementsByTagName("ChairEmailID6").item(0)
										.getTextContent();
																
								CourseName6 = eElement.getElementsByTagName("CourseNo6").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd6, chairEmailAdd6, CourseName6, instName6);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 6: "+e);
									e.printStackTrace();
								}
							}

							if (cb7.equals("1")) {
								log.info("**********CHECKBOX - 7**********");
								instEmailAdd7 = eElement.getElementsByTagName("InstructorEmail7").item(0)
										.getTextContent();
								
								instName7 = eElement.getElementsByTagName("InstructorLname7").item(0)
										.getTextContent();
								
								chairEmailAdd7 = eElement.getElementsByTagName("ChairEmailID7").item(0)
										.getTextContent();
								
								CourseName7 = eElement.getElementsByTagName("CourseNo7").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd7, chairEmailAdd7, CourseName7, instName7);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 7: "+e);
									e.printStackTrace();
								}
							}

							if (cb8.equals("1")) {
								log.info("**********CHECKBOX - 8**********");
								instEmailAdd8 = eElement.getElementsByTagName("InstructorEmail8").item(0)
										.getTextContent();
								
								instName8 = eElement.getElementsByTagName("InstructorLname8").item(0)
										.getTextContent();
								
								chairEmailAdd8 = eElement.getElementsByTagName("ChairEmailID8").item(0)
										.getTextContent();
								
								CourseName8 = eElement.getElementsByTagName("CourseNo8").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd8, chairEmailAdd8, CourseName8, instName8);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 8: "+e);
									e.printStackTrace();
								}
							}

							if (cb9.equals("1")) {
								log.info("**********CHECKBOX - 9**********");
								instEmailAdd9 = eElement.getElementsByTagName("InstructorEmail9").item(0)
										.getTextContent();
								
								instName9 = eElement.getElementsByTagName("InstructorLname9").item(0)
										.getTextContent();
								
								chairEmailAdd9 = eElement.getElementsByTagName("ChairEmailID9").item(0)
										.getTextContent();
								
								CourseName9 = eElement.getElementsByTagName("CourseNo9").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd9, chairEmailAdd9, CourseName9, instName9);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 9: "+e);
									e.printStackTrace();
								}
							}

							if (cb10.equals("1")) {
								log.info("**********CHECKBOX - 10**********");
								instEmailAdd10 = eElement.getElementsByTagName("InstructorEmail10").item(0)
										.getTextContent();
								
								instName10 = eElement.getElementsByTagName("InstructorLname10").item(0)
										.getTextContent();
								
								chairEmailAdd10 = eElement.getElementsByTagName("ChairEmailID10").item(0)
										.getTextContent();
								
								CourseName10 = eElement.getElementsByTagName("CourseNo10").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd10, chairEmailAdd10, CourseName10, instName10);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 10: "+e);
									e.printStackTrace();
								}
							}

							if (cb11.equals("1")) {
								log.info("**********CHECKBOX - 11**********");
								instEmailAdd11 = eElement.getElementsByTagName("InstructorEmail11").item(0)
										.getTextContent();
								
								instName11 = eElement.getElementsByTagName("InstructorLname11").item(0)
										.getTextContent();
								
								chairEmailAdd11 = eElement.getElementsByTagName("ChairEmailID11").item(0)
										.getTextContent();
								
								CourseName11 = eElement.getElementsByTagName("CourseNo11").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd11, chairEmailAdd11, CourseName11, instName11);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 11: "+e);
									e.printStackTrace();
								}
							}

							if (cb12.equals("1")) {
								log.info("**********CHECKBOX - 12**********");
								instEmailAdd12 = eElement.getElementsByTagName("InstructorEmail12").item(0)
										.getTextContent();
								
								instName12 = eElement.getElementsByTagName("InstructorLname12").item(0)
										.getTextContent();
							
								chairEmailAdd12 = eElement.getElementsByTagName("ChairEmailID12").item(0)
										.getTextContent();
								
								CourseName12 = eElement.getElementsByTagName("CourseNo12").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd12, chairEmailAdd12, CourseName12, instName12);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 12: "+e);
									e.printStackTrace();
								}
							}

							if (cb13.equals("1")) {
								log.info("**********CHECKBOX - 13**********");
								instEmailAdd13 = eElement.getElementsByTagName("InstructorEmail13").item(0)
										.getTextContent();
								
								instName13 = eElement.getElementsByTagName("InstructorLname13").item(0)
										.getTextContent();
								
								chairEmailAdd13 = eElement.getElementsByTagName("ChairEmailID13").item(0)
										.getTextContent();
								
								CourseName13 = eElement.getElementsByTagName("CourseNo13").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd13, chairEmailAdd13, CourseName13, instName13);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 13: "+e);
									e.printStackTrace();
								}
							}

							if (cb14.equals("1")) {
								log.info("**********CHECKBOX - 14**********");
								instEmailAdd14 = eElement.getElementsByTagName("InstructorEmail14").item(0)
										.getTextContent();
								
								instName14 = eElement.getElementsByTagName("InstructorLname14").item(0)
										.getTextContent();
								
								chairEmailAdd14 = eElement.getElementsByTagName("ChairEmailID14").item(0)
										.getTextContent();
								
								CourseName14 = eElement.getElementsByTagName("CourseNo14").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd14, chairEmailAdd14, CourseName14, instName14);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 14: "+e);
									e.printStackTrace();
								}
							}

							if (cb15.equals("1")) {
								log.info("**********CHECKBOX - 15**********");
								instEmailAdd15 = eElement.getElementsByTagName("InstructorEmail15").item(0)
										.getTextContent();
								
								instName15 = eElement.getElementsByTagName("InstructorLname15").item(0)
										.getTextContent();
								
								chairEmailAdd15 = eElement.getElementsByTagName("ChairEmailID15").item(0)
										.getTextContent();
								
								CourseName15 = eElement.getElementsByTagName("CourseNo15").item(0).getTextContent();

								try {
									sendEmail(instEmailAdd15, chairEmailAdd15, CourseName15, instName15);

								} catch (EmailException e) {
									log.info("Exception from CheckBox 15: "+e);
									e.printStackTrace();
								}
							}
						}
					}

				} catch (SAXException e) {
					log.error("SAXException: "+e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						log.error("Exception from Finally group: "+e.getMessage());
						e.printStackTrace();
					}
				}

			}
		}
	}

	public void sendEmail(String instructorEmail, String chairEmail, String CourseName, String instName) throws EmailException {

		try {
			
			log.info("Inside the send Email Method");
			
			// Declare a MessageGateway service
			MessageGateway<Email> messageGateway;
			
			// Set up the Email message
			Email email = new SimpleEmail();

			email.setSubject("Student Course Withdrawal: " + cwid);
			
			email.setFrom("csuf@fullerton.edu");
			email.addTo(instructorEmail);
			
			try {
				if (!chairEmail.equals("")) {					
					email.addCc(chairEmail);
				}
			}
			catch (EmailException e) {
				log.info(e.toString());
				e.printStackTrace();
			}	
					
			
			String Approve = "<!DOCTYPE html><html><head><title></title></head><body>"
					+ "</br>Dear Professor " +instName+",</br>" + "</br>"
					+ "The student below has requested to be withdrawn from your course(s) due to medical reasons.  "
					+ "This request has been approved and the student has been withdrawn from the course(s). </br>"
					+ "</br>" + "CWID: " + cwid + "</br>" + "Name: " + StudentFName + " " + SudentLName + "</br>"
					+ "Course Name: " + CourseName + "</br>" + "</br>"
					+ "Should you have any questions, please contact Enrollment Services at (657)-278-5202." + "</br>"
					+ "</br> Sincerely, </br>" + "Registration and Records" + "</html>";

			email.setContent(Approve, "text/html");

			// Inject a MessageGateway Service and send the message
			messageGateway = messageGatewayService.getGateway(Email.class);

			messageGateway.send((Email) email);

		} catch (Exception e) {
			log.error("Exception from sendEmail Method: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
