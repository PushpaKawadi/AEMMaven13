package com.aem.community.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.IntStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
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
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Instructor AND Chair Email_Approve" })

public class Email_Approve implements WorkflowProcess {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private ResourceResolverFactory resolverFactory;

	// Inject a MessageGatewayService
	@Reference
	private MessageGatewayService messageGatewayService;
	
	String courses = null;
	String cwid = null;
	String studentFName = null;
    String sudentLName = null;
    String sectionNumber = null;
    String instEmailAdd = null;
    String instName = null;
    String chairEmailAdd = null;
    String courseName = null;
	

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
					
					 org.w3c.dom.NodeList nListn = doc.getElementsByTagName("afUnboundData");
						for (int temp = 0; temp < nListn.getLength(); temp++) {
							org.w3c.dom.Node nNode1 = nListn.item(temp);
	
							if (nNode1.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						
								org.w3c.dom.Element eElement1 = (org.w3c.dom.Element) nNode1;	
								
                             sectionNumber = eElement1.getElementsByTagName("classNumberList").item(0).getTextContent();
							}
						}

					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);
						
						log.info("THE VALUE OF nLIST is: "+nList.toString());

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

							courses = eElement.getElementsByTagName("CourseNumberList").item(0).getTextContent();
							cwid = eElement.getElementsByTagName("StudentID").item(0).getTextContent();
							studentFName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
							sudentLName = eElement.getElementsByTagName("LastName").item(0).getTextContent();							

							IntStream.range(1, 16).forEach(index -> {
								String indexValue = String.valueOf(index);
								String checkBoxValue = eElement.getElementsByTagName("CB".concat(indexValue)).item(0)
										.getTextContent();
								if (StringUtils.isNotBlank(checkBoxValue) && checkBoxValue.equals("1")) {
									log.info("**********CHECKBOX - ".concat(indexValue).concat("**********"));

									instEmailAdd = eElement
											.getElementsByTagName("InstructorEmail".concat(indexValue)).item(0)
                                            .getTextContent();
                                            log.info("Instructor Email:="+instEmailAdd);

									instName = eElement
											.getElementsByTagName("InstructorLname".concat(indexValue)).item(0)
                                            .getTextContent();
                                            log.info("Instructor LName:="+instName);

									chairEmailAdd = eElement
											.getElementsByTagName("ChairEmailID".concat(indexValue)).item(0)
                                            .getTextContent();
                                            log.info("Chair LName:="+chairEmailAdd);

                                    courseName = eElement
                                            .getElementsByTagName("CourseNo".concat(indexValue)).item(0)
                                            .getTextContent();
                                            log.info("Course No:="+courseName);

                                    sectionNumber = eElement
                                            .getElementsByTagName("ScheduleNo".concat(indexValue)).item(0)
                                            .getTextContent();  
                                            log.info("Section No:="+sectionNumber);      

									try {
										sendEmail(instEmailAdd, instName, chairEmailAdd, courseName, sectionNumber);
									} catch (EmailException e) {
										log.info("Exception from CheckBox : ".concat(indexValue) + e);
										e.printStackTrace();
									}
								}
							});
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

	public void sendEmail(String instructorEmail, String instName, String chairEmail, String CourseName, String sectionNumber) throws EmailException {

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
			
			log.info("Instructor Email in SEND EMAIL Method: " + instructorEmail);
			log.info("Chair Email in SEND EMAIL Method: " + chairEmail);
			log.info("Instructor Name in SEND EMAIL Method: "+instName);
			log.info("Course Number in SEND EMAIL Method: "+ CourseName);			
			
            String Approve = "<!DOCTYPE html><html><head><title></title></head><body>"
                    + "<img src=https://content.screencast.com/users/CSUF-test-account/folders/images/media/459e7631-06d2-469b-8de9-cb4f2b96f223/CSUF_Mailer_logo.gif>"
					+ "</br>Dear Professor " +instName+",</br>" + "</br>"
					+ "The student below has requested to be withdrawn from your course(s) due to medical reasons.  "
					+ "This request has been approved and the student has been withdrawn from the course(s). </br>"
					+ "</br>" + "CWID: " + cwid + "</br>" + "Name: " + studentFName + " " + sudentLName + "</br>"
					+ "Course Name: " + courseName + "</br> Class Number: "+sectionNumber + "</br>" + "</br>" + "</br>"
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

