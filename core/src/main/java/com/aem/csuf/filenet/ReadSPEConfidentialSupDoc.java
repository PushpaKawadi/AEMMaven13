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


@Component(property = { Constants.SERVICE_DESCRIPTION + "=Read Confidential Support Doc",
		Constants.SERVICE_VENDOR + "=Thoughtfocus-CSUF", "process.label" + "=Read SPE Confidential Support Doc" })
public class ReadSPEConfidentialSupDoc implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(ReadSPEConfidentialSupDoc.class);
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
		String empId = null;
		String cbid = null;
		String deptId = null;
		String overallRating = null;
		String evaluationType = null;
		String empUserId = null;
		String managerUserId = null;
		String hrCoordId = null;
		String administratorId = null;
		String attachmentMimeType = "";
		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			String filePath = attachmentXml.getPath();
			if (filePath.contains("Data.xml")) {

				filePath = attachmentXml.getPath().concat("/jcr:content");
				
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
						log.info("Xpath");
						org.w3c.dom.Node empIdNode = (org.w3c.dom.Node) xpath
								.evaluate("//EmpID", doc, XPathConstants.NODE);
						empId = empIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath
								.evaluate("//StaffFirstName", doc,
										XPathConstants.NODE);
						firstName = fnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath
								.evaluate("//StaffLastName", doc,
										XPathConstants.NODE);
						lastName = lnNode.getFirstChild().getNodeValue();
											
						org.w3c.dom.Node cbidNode = (org.w3c.dom.Node) xpath
								.evaluate("//CBID", doc, XPathConstants.NODE);
						cbid = cbidNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node deptIdNode = (org.w3c.dom.Node) xpath
								.evaluate("//Department_ID", doc,
										XPathConstants.NODE);
						deptId = deptIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node overallRatingNode = (org.w3c.dom.Node) xpath
								.evaluate("//OverallRating", doc,
										XPathConstants.NODE);
						overallRating = overallRatingNode.getFirstChild().getNodeValue();
						
						org.w3c.dom.Node evaluationTypeNode = (org.w3c.dom.Node) xpath
								.evaluate("//EvaluationType", doc, XPathConstants.NODE);
						evaluationType = evaluationTypeNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node empUserIdNode = (org.w3c.dom.Node) xpath
								.evaluate("//EmpUserID", doc,
										XPathConstants.NODE);
						empUserId = empUserIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node managerUserIdNode = (org.w3c.dom.Node) xpath
								.evaluate("//ManagerUserID", doc,
										XPathConstants.NODE);
						managerUserId = managerUserIdNode.getFirstChild().getNodeValue();
						
						org.w3c.dom.Node hrCoordIdNode = (org.w3c.dom.Node) xpath
						.evaluate("//HrCoordId", doc,
								XPathConstants.NODE);
						hrCoordId = hrCoordIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node administratorIdNode = (org.w3c.dom.Node) xpath
						.evaluate("//AdminUserID", doc,
								XPathConstants.NODE);
						administratorId = administratorIdNode.getFirstChild().getNodeValue();
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
			if (filePath.contains("Attachments")) {
				String attachmentsPath = "Attachments";
				// String attachmentsFilePath = payloadPath + "/" +
				// attachmentsPath + "/supportDoc1";
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

								String jsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \"" + lastName + "\"," + "\"CWID\": \"" 	+ empId + "\"," + "\"AttachmentType\": " + "\"SPEConfidentialSupDoc\"" + "," + "\"AttachmentMimeType\": \"" + attachmentMimeType + "\"," + "\"Attachment\":\"" + encodedPDF + "\"," + "\"CBID\": \"" + cbid + "\"," + "\"DepartmentID\": \"" + deptId + "\"," + "\"DocType\":" + "\"SPE99SD\"" + ","  + "\"EndMonth\":" + "\"04\"" + "," + "\"EndYear\":" + "\"2020\"" + "," + "\"OverallRating\":\"" + overallRating + "\"," + "\"EvaluationType\":\"" + evaluationType + "\"," + "\"StartMonth\":" + "\"04\"" + "," + "\"StartYear\":" + "\"2019\"" + "," + "\"EmpUserID\":\"" + empUserId + "\"," + "\"ManagerUserID\":\"" + managerUserId + "\"," + "\"HRCoordUserID\":\"" + hrCoordId + "\"," + "\"AppropriateAdminUserID\":\"" + administratorId + "\"}";

								if (encodedPDF != null && lastName != null && firstName != null) {
									log.error("Read inner suppoting doc");
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
										// byte[] input = jsonInputString.getBytes("utf-8");
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
						}else {
						Path attachmentSource = Paths.get(attachmentSupDoc.getPath());
						String attachmentDoc = attachmentSupDoc.getPath().concat("/jcr:content");
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
							// log.error("bytes="+bytes);
							encodedPDF = Base64.getEncoder().encodeToString(bytes);

							String jsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \"" + lastName + "\"," + "\"CWID\": \"" 	+ empId + "\"," + "\"AttachmentType\": " + "\"SPEConfidentialSupDoc\"" + "," + "\"AttachmentMimeType\": \"" + attachmentMimeType + "\"," + "\"Attachment\":\"" + encodedPDF + "\"," + "\"CBID\": \"" + cbid + "\"," + "\"DepartmentID\": \"" + deptId + "\"," + "\"DocType\":" + "\"SPE99SD\"" + ","  + "\"EndMonth\":" + "\"04\"" + "," + "\"EndYear\":" + "\"2020\"" + "," + "\"OverallRating\":\"" + overallRating + "\"," + "\"EvaluationType\":\"" + evaluationType + "\"," + "\"StartMonth\":" + "\"04\"" + "," + "\"StartYear\":" + "\"2019\"" + "," + "\"EmpUserID\":\"" + empUserId + "\"," + "\"ManagerUserID\":\"" + managerUserId + "\"," + "\"HRCoordUserID\":\"" + hrCoordId + "\"," + "\"AppropriateAdminUserID\":\"" + administratorId + "\"}";

							if (encodedPDF != null && lastName != null && firstName != null) {
								log.error("Read outer suppoting doc");
								URL url = null;
								try {
									String filenetUrl = globalConfigService.getStaffEvalFilenetURL();
									url = new URL(filenetUrl);
									
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
									// byte[] input = jsonInputString.getBytes("utf-8");
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
