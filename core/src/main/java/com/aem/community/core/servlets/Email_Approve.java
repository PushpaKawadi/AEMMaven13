package com.aem.community.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
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
import com.aem.community.core.services.EmailService;
import com.aem.community.core.services.vo.EmailServiceVO;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Email To Instructor And Chair",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Instructor and Chair Custom Email_Approve" })

public class Email_Approve implements WorkflowProcess {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String EMAIL_TEMPLATE_PATH = "/etc/notification/email/csuf/email-approve.html";
    // "/etc/notification/email/csuf/sample-template.html";
    //
    
	private static final String EMAIL_FROM_ADDRESS = "csuf@fullerton.edu";

	@Reference
	private EmailService emailService;

	// String courses = null;
	String cwid = null;
	String studentFName = null;
	String sudentLName = null;

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

						log.info("THE VALUE OF nLIST is: " + nList.toString());

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

							// courses =
							// eElement.getElementsByTagName("CourseNumberList").item(0).getTextContent();
							cwid = eElement.getElementsByTagName("StudentID").item(0).getTextContent();
							studentFName = eElement.getElementsByTagName("FirstName").item(0).getTextContent();
							sudentLName = eElement.getElementsByTagName("LastName").item(0).getTextContent();
							// sectionNumber = eElement.getElementsByTagName("")

							IntStream.range(1, 16).forEach(index -> {
								String indexValue = String.valueOf(index);
								String checkBoxValue = eElement.getElementsByTagName("CB".concat(indexValue)).item(0)
										.getTextContent();
								if (StringUtils.isNotBlank(checkBoxValue) && checkBoxValue.equals("1")) {
									log.info("**********CHECKBOX - ".concat(indexValue).concat("**********"));

									String instEmailAdd = eElement
											.getElementsByTagName("InstructorEmail".concat(indexValue)).item(0)
											.getTextContent();

									String instName = eElement
											.getElementsByTagName("InstructorLname".concat(indexValue)).item(0)
											.getTextContent();

									String chairEmailAdd = eElement
											.getElementsByTagName("ChairEmailID".concat(indexValue)).item(0)
											.getTextContent();

									String courseName = eElement.getElementsByTagName("CourseNo".concat(indexValue))
											.item(0).getTextContent();

									try {
										sendEmail(instEmailAdd, chairEmailAdd, courseName, instName);
									} catch (EmailException e) {
										log.info("Exception from CheckBox : ".concat(indexValue) + e);
										e.printStackTrace();
									}
								}
							});
						}
					}
				}
				catch (SAXException e) {
					log.error("SAXException: " + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (Exception e) {
						log.error("Exception from Finally group: " + e.getMessage());
						e.printStackTrace();
					}
				}

			}
		}

	}

	private void sendEmail(String instructorEmail, String chairEmail, String courseName, String instName)
			throws EmailException {

		try {
         
			log.debug("inside sendEmail Method =========== Custom Email Approve");

			EmailServiceVO emailVO = new EmailServiceVO();
            emailVO.setTemplatePath(EMAIL_TEMPLATE_PATH);
//             emailVO.setTemplatePath((StringUtils.isNotBlank(templatePath) ? templatePath
//  					: "/etc/notification/email/csuf/sample-email-template.html"));
			emailVO.setSubject("Student Course Withdrawallllllll: ".concat(cwid));
			emailVO.setFromAddress(EMAIL_FROM_ADDRESS);
			emailVO.addToAddress(instructorEmail);
			if (StringUtils.isNotBlank(chairEmail)) {
				emailVO.addCcAddress(chairEmail);
			}

			// String Approve = "<!DOCTYPE html><html><head><title></title></head><body>" + "</br>Dear Professor "
			// 		+ instName + ",</br>" + "</br>"
			// 		+ "The student below has requested to be withdrawn from your course(s) due to medical reasons.  "
			// 		+ "This request has been approved and the student has been withdrawn from the course(s). </br>"
			// 		+ "</br>" + "CWID: " + cwid + "</br>" + "Name: " + studentFName + " " + sudentLName + "</br>"
			// 		+ "Course Name: " + courseName + "</br>" + "</br>"
			// 		+ "Should you have any questions, please contact Enrollment Services at (657)-278-5202." + "</br>"
			// 		+ "</br> Sincerely, </br>" + "Registration and Records" + "</html>";

			emailVO.setUseCQGateway(false);

			Map<String, String> templateVaribles = new HashMap<>();
			templateVaribles.put("instName", instName);
			templateVaribles.put("cwid", cwid);
			templateVaribles.put("studentFName", studentFName);
			templateVaribles.put("sudentLName", sudentLName);
			templateVaribles.put("courseName", courseName);
			emailVO.setTemplateVaribles(templateVaribles);

			log.debug("emailVO : ".concat(emailVO.toString()));

			List<String> emailFailureList = emailService.sendEmail(emailVO);

			if (null != emailFailureList && emailFailureList.size() > 0) {
				log.debug("Email sending failed to the recipients: ".concat(emailFailureList.toString()));
			} else if (null != emailFailureList && emailFailureList.size() == 0) {
				log.debug("Email sent successfully to ".concat(instructorEmail));
			} else {
				log.debug("Email sending failed");
			}

		} catch (Exception e) {
			log.error("Exception from sendEmail Method: " + e.getMessage());
			e.printStackTrace();
		}
	}
}