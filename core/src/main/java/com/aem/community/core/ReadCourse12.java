package com.aem.community.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
//import com.adobe.aemfd.docmanager.Document;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.community.core.services.GlobalConfigService;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Save Course12", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=Save Course12" })
public class ReadCourse12 implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(ReadCourse12.class);

	@Reference
	private GlobalConfigService globalConfigService;
	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;
		String firstName = null;
		String lastName = null;
		String encodedPDF = null;
		String studentID = null;
		String caseID = null;
		String major = null;
		String termCode = null;
		String termDescription = null;
		String typeOfForm = null;
		String WithdrawalType = null;
		String chairVal = null;
		String withdrawalDecision = null;
		String instUID = "";
		String chairUID ="";

		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			String filePath = attachmentXml.getPath();

			if (filePath.contains("Data12.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
				} catch (ValueFormatException e2) {
					log.error("ValueFormatException in readcourse12=" + e2.getMessage());
					e2.printStackTrace();
				} catch (PathNotFoundException e2) {
					log.error("PathNotFoundException in readcourse12=" + e2.getMessage());
					e2.printStackTrace();
				} catch (RepositoryException e2) {
					log.error("RepositoryException in readcourse12=" + e2.getMessage());
					e2.printStackTrace();
				}

				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = null;
					try {
						dBuilder = dbFactory.newDocumentBuilder();
					} catch (ParserConfigurationException e1) {
						log.error("ParserConfigurationException in readcourse12=" + e1.getMessage());
						e1.printStackTrace();
					}
					try {
						doc = dBuilder.parse(is);
					} catch (IOException e1) {
						log.error("IOException in readcourse12=" + e1.getMessage());
						e1.printStackTrace();
					}
					XPath xpath = XPathFactory.newInstance().newXPath();
					try {
						
						org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath.evaluate("//FirstName", doc,
								XPathConstants.NODE);
						firstName = fnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath.evaluate("//LastName", doc,
								XPathConstants.NODE);
						lastName = lnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node sIDNode = (org.w3c.dom.Node) xpath.evaluate("//StudentID", doc,
								XPathConstants.NODE);
						studentID = sIDNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node caseId = (org.w3c.dom.Node) xpath.evaluate("//caseId", doc,
								XPathConstants.NODE);
						caseID = caseId.getFirstChild().getNodeValue();

						org.w3c.dom.Node majorVal = (org.w3c.dom.Node) xpath.evaluate("//Major", doc,
								XPathConstants.NODE);
						major = majorVal.getFirstChild().getNodeValue();

						org.w3c.dom.Node termCodeVal = (org.w3c.dom.Node) xpath.evaluate("//TermCode", doc,
								XPathConstants.NODE);
						termCode = termCodeVal.getFirstChild().getNodeValue();

						org.w3c.dom.Node termDescVal = (org.w3c.dom.Node) xpath.evaluate("//TermDesc", doc,
								XPathConstants.NODE);
						termDescription = termDescVal.getFirstChild().getNodeValue();
						
						org.w3c.dom.Node typeFormVal = (org.w3c.dom.Node) xpath.evaluate("//typeOfForm", doc,
								XPathConstants.NODE);
						typeOfForm = typeFormVal.getFirstChild().getNodeValue();
						
						if(typeOfForm.equals("1")){
							WithdrawalType = "Non-Medical";
						}else{
							WithdrawalType = "Medical";
						}
						org.w3c.dom.Node chairWithdrawlVal = (org.w3c.dom.Node) xpath.evaluate("//RecommendChair", doc,XPathConstants.NODE);
						chairVal = chairWithdrawlVal.getFirstChild().getNodeValue();

						if (chairVal.equals("1")) {
							withdrawalDecision = "Approval";
						} else {
							withdrawalDecision = "Denial";
						}
						org.w3c.dom.Node instUIDVal = (org.w3c.dom.Node) xpath.evaluate("//InstructorUserID12", doc,
								XPathConstants.NODE);
						instUID = instUIDVal.getFirstChild().getNodeValue();
						
						org.w3c.dom.Node chairUIDVal = (org.w3c.dom.Node) xpath.evaluate("//ChairUserID12", doc,
								XPathConstants.NODE);
						chairUID = chairUIDVal.getFirstChild().getNodeValue();

					} catch (XPathExpressionException e) {
						log.error("XPathExpressionException in readcourse12=" + e.getMessage());
						e.printStackTrace();
					}
				} catch (SAXException e) {
					log.error("SAXException in readcourse12=" + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						log.error("IOException in readcourse12=" + e.getMessage());
						e.printStackTrace();
					}

				}

			}

			if (filePath.contains("Student_Course_Withdrawal_C12.pdf")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);
				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
					try {
						byte[] bytes = IOUtils.toByteArray(is);
						encodedPDF = Base64.getEncoder().encodeToString(bytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (ValueFormatException e) {
					log.error("ValueFormatException in readcourse12=" + e.getMessage());
					e.printStackTrace();
				} catch (PathNotFoundException e) {
					log.error("PathNotFoundException in readcourse12=" + e.getMessage());
					e.printStackTrace();
				} catch (RepositoryException e) {
					log.error("RepositoryException in readcourse12=" + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						log.error("IOException in readcourse12=" + e.getMessage());
						e.printStackTrace();
					}

				}
			}
		}
		String jsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \"" + lastName + "\"," + "\"withdrawalDecision\": \"" + withdrawalDecision + "\","
				+ "\"CWID\": \"" + studentID + "\"," + "\"CaseID\": \"" + caseID + "\"," + "\"Major\": \"" + major
				+ "\"," + "\"TermCode\": \"" + termCode + "\"," + "\"TermDescription\": \"" + termDescription + "\"," + "\"chairUID\": \"" + chairUID + "\"," + "\"instUID\": \"" + instUID + "\","
				+ "\"Attachment\": \"" + encodedPDF + "\"," + "\"AttachmentType\": " + "\"FinalDOR\"" + ","
				+ "\"AttachmentMimeType\": " + "\"application/pdf\"" +","+ "\"WithdrawalType\": \"" + WithdrawalType + "\"}";

		if (encodedPDF != null && lastName != null && firstName != null) {
			log.info("Save Course 12 pdf");
			URL url = null;
			try {
				String filenetUrl = globalConfigService.getFilenetURL();
				url = new URL(filenetUrl);
				//url = new URL("http://erpicn521tst.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addCourseWithdrawalDocuments");
								
				//url = new URL("http://erpicn521prd01.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addCourseWithdrawalDocuments");
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) url.openConnection();
				log.info("Con=" + con);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json");

			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {
				// byte[] input = jsonInputString.getBytes("utf-8");
				os.write(jsonString.getBytes("utf-8"));
				os.close();
				con.getResponseCode();
				
			} catch (IOException e1) {
				log.error("IOException in readcourse12=" + e1.getMessage());
				e1.printStackTrace();
			}
			try {
				con.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
