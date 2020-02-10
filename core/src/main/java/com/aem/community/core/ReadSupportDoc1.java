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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Read Support Doc1",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Read Support Doc1" })
public class ReadSupportDoc1 implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(ReadSupportDoc1.class);
	

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

		InputStream sd1 = null;
		String docEncoded1 = null;

		String studentID = null;
		String caseID = null;
		String major = null;
		String termCode = null;
		String termDescription = null;
		String typeOfForm = null;
		String WithdrawalType = null;
		String mimeType= null;
		String withdrawalDecision = "";
		String instUID = "";
		String chairUID ="";

		String attachmentsPath = "attachments";
		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		
		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.error("xmlFiles inside ");
			String filePath = attachmentXml.getPath();
			if (filePath.contains("attachments")) {
				

				String attachmentsFilePath = payloadPath + "/" + attachmentsPath + "/supportDoc1";
				Resource attachment1 = resolver.getResource(attachmentsFilePath);
				if (attachment1 != null) {
					Iterator<Resource> attFiles = attachment1.listChildren();
					while (attFiles.hasNext()) {
						Resource supDoc1 = attFiles.next();
						

						//MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

						// only by file name
						//String mimeType = mimeTypesMap.getContentType(supDoc1.getPath());
						//log.error("Mime Type =" + mimeType);

						String attDoc1 = supDoc1.getPath().concat("/jcr:content");
						
						String fileMimeType = supDoc1.getName();
						if (fileMimeType.toLowerCase().endsWith(".jpg") || fileMimeType.toLowerCase().endsWith(".jpeg")) {
							mimeType = "image/jpeg";
						} else if (fileMimeType.toLowerCase().endsWith(".pdf")){
							mimeType = "application/pdf";
						}else if(fileMimeType.toLowerCase().endsWith(".png")){
							mimeType = "image/png";
						}
					   else if(fileMimeType.toLowerCase().endsWith(".tiff")){
							mimeType = "image/tiff";
						}else {
							mimeType = "application/pdf";
						}
						
						Node subNode1 = resolver.getResource(attDoc1).adaptTo(Node.class);

						try {
							sd1 = subNode1.getProperty("jcr:data").getBinary().getStream();
							byte[] bytes = IOUtils.toByteArray(sd1);
							// log.error("bytes="+bytes);
							docEncoded1 = Base64.getEncoder().encodeToString(bytes);
						} catch (ValueFormatException e) {
							e.printStackTrace();
						} catch (PathNotFoundException e) {
							e.printStackTrace();
						} catch (RepositoryException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								sd1.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}
				}
			}

			// log.error("filePath= "+filePath);
			if (filePath.contains("Data.xml")) {
				

				filePath = attachmentXml.getPath().concat("/jcr:content");
				/// var/fd/dashboard/payload/server0/2019-08-07_3/523TS2EV2Q2XKMLHUNVXUQKTJU_6/Data.xml
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);
				

				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
				} catch (ValueFormatException e2) {
					log.error("Exception1=" + e2);
					e2.printStackTrace();
				} catch (PathNotFoundException e2) {
					log.error("Exception2=" + e2);
					e2.printStackTrace();
				} catch (RepositoryException e2) {
					log.error("Exception3=" + e2);
					e2.printStackTrace();
				}
				// log.error("Test="+is.available());

				// Document doc = null;

				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = null;
					try {
						log.info("Inside try");
						dBuilder = dbFactory.newDocumentBuilder();
					} catch (ParserConfigurationException e1) {
						log.error("ParserConfigurationException=" + e1);
						e1.printStackTrace();
					}
					try {
						log.info("Inside try2");
						doc = dBuilder.parse(is);
					} catch (IOException e1) {
						log.error("IOException=" + e1);
						e1.printStackTrace();
					}
					XPath xpath = XPathFactory.newInstance().newXPath();
					try {
						log.info("Xpath");
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
					} catch (XPathExpressionException e) {
						e.printStackTrace();
					}
				} catch (SAXException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
					}

				}
			}
		}

			// String jsonString = "{" +"\"SupDoc1\": " + docEncoded1 + "," +
			// "\"SupDoc2\": " + docEncoded2 + "}";

			// String jsonString = "{" +"\"SupDoc1\": " + docEncoded1 + "," +
			// "\"lastName\": " + lastName + "," + "\"firstName\": " + firstName
//			// +"}";
//			String jsonString = "{" + "\"FirstName\": " + firstName + "," + "\"LastName\": " + lastName + ","
//					+ "\"CWID\": " + studentID + "," + "\"CaseID\": " + caseID + "," + "\"Major\": " + major + ","
//					+ "\"TermCode\": " + termCode + "," + "\"TermDescription\": " + termDescription + ","
//					+ "\"Attachment\": " + docEncoded1 + "," + "\"AttachmentType\": " + "SupportingDocument" +  " \"AttachmentMimeType\": " + "application/pdf" +"}";
			
			String jsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \"" + lastName + "\"," +"\"withdrawalDecision\": \"" + withdrawalDecision + "\"," + "\"chairUID\": \"" + chairUID + "\"," + "\"instUID\": \"" + instUID + "\","
					+ "\"CWID\": \"" + studentID + "\"," + "\"CaseID\": \"" + caseID + "\"," + "\"Major\": \"" + major + "\","
					+ "\"TermCode\": \"" + termCode + "\"," + "\"TermDescription\": \"" + termDescription + "\","
					+ "\"Attachment\": \"" + docEncoded1 + "\"," + "\"AttachmentType\": " + "\"SupportingDocument\"" + ","+ "\"AttachmentMimeType\": \"" + mimeType +"\"," + "\"WithdrawalType\": \"" + WithdrawalType + "\"}";
			

//			String jsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \"" + lastName + "\","
//					+ "\"CWID\": \"" + studentID + "\"," + "\"CaseID\": \"" + caseID + "\"," + "\"Major\": \"" + major + "\","
//					+ "\"TermCode\": \"" + termCode + "\"," + "\"TermDescription\": \"" + termDescription + "\","
//					+a "\"Attachment\": \"" + encodedPDF + "\"," + "\"AttachmentType\": " + "\"FinalDOR\"" + ","+ "\"AttachmentMimeType\": " + "\"application/pdf\"" + ","+ "\"WithdrawalType\": \"" + WithdrawalType + "\"}";
			 
			
			if (docEncoded1 != null && lastName != null && firstName != null) {
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
					log.info("Connection=" + con);
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

				// byte[] outputBytes = rootJsonObject.getBytes("UTF-8");
				// OutputStream os = con.getOutputStream();
				// os.write(outputBytes);

				try (OutputStream os = con.getOutputStream()) {
					// byte[] input = jsonInputString.getBytes("utf-8");
					os.write(jsonString.getBytes("utf-8"));
					os.close();
					con.getResponseCode();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// try {
				// //con.getOutputStream().write(data.getBytes("UTF-8"));
				// con.getResponseCode();
				// log.error("Res ="+con.getResponseCode());
				//
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
//				try {
//					con.getInputStream();
//					log.error("Here1=" + con.getInputStream());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

			}

		
	}
}
