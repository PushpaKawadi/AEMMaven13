package com.aem.csuf.filenet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.nio.file.Files;
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
//import com.aem.community.util.ConfigManager;
import com.aem.community.core.services.GlobalConfigService;


@Component(property = { Constants.SERVICE_DESCRIPTION + "=Read Pre Perf Eval Support Doc",
		Constants.SERVICE_VENDOR + "=Thoughtfocus-CSUF", "process.label" + "=Read Pre Perf Eval Support Doc" })
public class ReadPrePerfEvalSupDocs implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(ReadPrePerfEvalSupDocs.class);
	@Reference
	private GlobalConfigService globalConfigService;
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();

		Document doc = null;
		InputStream is = null;
		String firstName = "";
		String lastName = "";
		String encodedPDF = "";
		String empId = "";
		String rating = "";
		String cbid = "";
		String depId = "";
		String empUserID = "";
		String managerUserID = "";
		String attachmentMimeType = "";
		String reviewPeriodFrom = null;
		String reviewPeriodTo = null;
		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		    while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			String filePath = attachmentXml.getPath();
			
			if (filePath.contains("Attachments")) {
				Iterator<Resource> fileList = xmlNode.listChildren();

				  while (fileList.hasNext()) {
					Resource xmlFile = fileList.next();
					String xmlPath = xmlFile.getPath();
					if (xmlPath.contains("Data.xml")) {

						xmlPath = xmlFile.getPath().concat("/jcr:content");
						
						Node dataSubNode = resolver.getResource(xmlPath).adaptTo(Node.class);

						try {
							is = dataSubNode.getProperty("jcr:data").getBinary().getStream();
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
						

						try {
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder dBuilder = null;
							try {
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
								org.w3c.dom.Node empIdNode = (org.w3c.dom.Node) xpath
										.evaluate("//EmpID", doc, XPathConstants.NODE);
								empId = empIdNode.getFirstChild().getNodeValue();

								org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath
										.evaluate("//FirstName", doc,
												XPathConstants.NODE);
								firstName = fnNode.getFirstChild().getNodeValue();

								org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath
										.evaluate("//LastName", doc,
												XPathConstants.NODE);
								lastName = lnNode.getFirstChild().getNodeValue();

								org.w3c.dom.Node depVal = (org.w3c.dom.Node) xpath
										.evaluate("//DeptID", doc, XPathConstants.NODE);
								depId = depVal.getFirstChild().getNodeValue();

								org.w3c.dom.Node logVal = (org.w3c.dom.Node) xpath
										.evaluate("//EmpUserID", doc, XPathConstants.NODE);
								empUserID = logVal.getFirstChild().getNodeValue();

								org.w3c.dom.Node managerUid = (org.w3c.dom.Node) xpath
										.evaluate("//ManagerUserID", doc, XPathConstants.NODE);
								managerUserID = managerUid.getFirstChild().getNodeValue();
								org.w3c.dom.Node reviewPeriodFromNode = (org.w3c.dom.Node) xpath.evaluate("//ReviewPeriodFrom",
										doc, XPathConstants.NODE);
								reviewPeriodFrom = reviewPeriodFromNode.getFirstChild().getNodeValue();

								org.w3c.dom.Node reviewPeriodToNode = (org.w3c.dom.Node) xpath.evaluate("//ReviewPeriodTo", doc,
										XPathConstants.NODE);
								reviewPeriodTo = reviewPeriodToNode.getFirstChild().getNodeValue();
								
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
				String attachmentsPath = "Attachments";
				
				String AttachmentsFilePath = payloadPath + "/" + attachmentsPath;
				 
				Resource attachments = resolver.getResource(AttachmentsFilePath);
				
				if (attachments != null) {
					Iterator<Resource> attachmentFiles = attachments.listChildren();
					
					while (attachmentFiles.hasNext()) {
						Resource attachmentSupDoc = attachmentFiles.next();
						if(attachmentSupDoc.getResourceType().equalsIgnoreCase("sling:Folder")) {
							Iterator<Resource> innerAttachmentFiles = attachmentSupDoc.listChildren();
							while (innerAttachmentFiles.hasNext()) {
							Resource innerAttachmentSupDoc = innerAttachmentFiles.next();
							Path attachmentSource = Paths.get(innerAttachmentSupDoc.getPath());
							String attachmentDoc = innerAttachmentSupDoc.getPath().concat("/jcr:content");
							Node attachmentSubNode = resolver.getResource(attachmentDoc).adaptTo(Node.class);
							try {
								attachmentMimeType = Files.probeContentType(attachmentSource);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								is = attachmentSubNode.getProperty("jcr:data").getBinary().getStream();
								byte[] bytes = IOUtils.toByteArray(is);
								encodedPDF = Base64.getEncoder().encodeToString(bytes);
								String fromYear = reviewPeriodFrom.substring(0, 4);
								String fromMonth = reviewPeriodFrom.substring(5, 7);
								String endYear = reviewPeriodTo.substring(0, 4);
								String endMonth = reviewPeriodTo.substring(5, 7);
								String jsonString = "{" + "\"FirstName\": \"" + firstName + "\","
										+ "\"LastName\": \"" + lastName + "\"," + "\"CWID\": \""
										+ empId + "\"," + "\"OverallRating\": \"" + "" + "\","
										+ "\"EvaluationType\": \"" + "" + "\","
										+ "\"AttachmentType\": " + "\"PrePerfEvalSupDoc\"" + ","
										+ "\"AttachmentMimeType\": \"" + attachmentMimeType + "\","
										+ "\"CBID\": \"" + "" + "\"," + "\"DepartmentID\": \""
										+ depId + "\"," + "\"DocType\":" + "\"STAFFSESD\"" + ","
										+ "\"EndMonth\":\"" + endMonth + "\"," + "\"EndYear\":\""
										+ endYear + "\"," + "\"StartMonth\":\"" + fromMonth + "\","
										+ "\"StartYear\":\"" + fromYear + "\"," + "\"EmpUserID\":\""
										+ empUserID + "\"," + "\"ManagerUserID\":\"" + managerUserID + "\","
										+ "\"HRCoordUserID\":\"" + "" + "\","
										+ "\"AppropriateAdminUserID\":\"" + "" + "\","
										+ "\"Attachment\":\"" + encodedPDF + "\"}";

								if (encodedPDF != null && lastName != null && firstName != null) {
									log.error("Read Pre Perf Eval suppoting doc");
									URL url = null;
									try {
										String filenetUrl = globalConfigService.getStaffEvalFilenetURL();
										url = new URL(filenetUrl);
										//log.info("jsonString=" + jsonString);
									} catch (MalformedURLException e) {
										e.printStackTrace();
									}
									HttpURLConnection con = null;
									try {
										con = (HttpURLConnection) url.openConnection();
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
										os.write(jsonString.getBytes("utf-8"));
										os.close();
										con.getResponseCode();
										log.error("Res=" + con.getResponseCode());
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
									try {
										con.getInputStream();
										log.error("is=" + con.getInputStream());
									} catch (IOException e) {
										e.printStackTrace();
									}

								}
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
									is.close();
								} catch (IOException e) {
									e.printStackTrace();
								}

							}
							}
						}
							
					}
				}
			}


		}

	}
}
