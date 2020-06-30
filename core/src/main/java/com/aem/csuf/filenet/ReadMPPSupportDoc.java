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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Read MPP Support Doc",
		Constants.SERVICE_VENDOR + "=Thoughtfocus-CSUF", "process.label" + "=Read MPP Support Doc" })
public class ReadMPPSupportDoc implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(ReadMPPSupportDoc.class);
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
		String reviewPeriodFrom = null;
		String reviewPeriodTo = null;
		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.error("xmlFiles inside ");
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
				// log.error("Test="+is.available());

				// Document doc = null;

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
					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
						empId = eElement.getElementsByTagName("EmpID").item(0).getTextContent();
						firstName = eElement.getElementsByTagName("EmpFirstName").item(0).getTextContent();
						lastName = eElement.getElementsByTagName("EmpLastName").item(0).getTextContent();
						cbid = eElement.getElementsByTagName("CBID").item(0).getTextContent();
						deptId = eElement.getElementsByTagName("DeptID").item(0).getTextContent();
						overallRating = eElement.getElementsByTagName("OverallRating").item(0).getTextContent();
						evaluationType = eElement.getElementsByTagName("EvaluationType").item(0).getTextContent();
						empUserId = eElement.getElementsByTagName("EmpUserID").item(0).getTextContent();
						managerUserId = eElement.getElementsByTagName("ManagerUserID").item(0).getTextContent();
						hrCoordId = eElement.getElementsByTagName("HrCoordId").item(0).getTextContent();
						administratorId = eElement.getElementsByTagName("AdminUserID").item(0).getTextContent();
						reviewPeriodFrom = eElement.getElementsByTagName("ReviewPeriodFrom").item(0).getTextContent();
						reviewPeriodTo = eElement.getElementsByTagName("ReviewPeriodTo").item(0).getTextContent();
						}
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
						if (attachmentSupDoc.getResourceType().equalsIgnoreCase("sling:Folder")) {
							Iterator<Resource> innerAttachmentFiles = attachmentSupDoc.listChildren();
							while (innerAttachmentFiles.hasNext()) {
								Resource innerAttachmentSupDoc = innerAttachmentFiles.next();
								// log.info("name of doc inside="+innerAttachmentSupDoc);
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
									// log.error("bytes="+bytes);
									encodedPDF = Base64.getEncoder().encodeToString(bytes);
									String fromYear = reviewPeriodFrom.substring(0, 4);
									String fromMonth = reviewPeriodFrom.substring(5, 7);
									String endYear = reviewPeriodTo.substring(0, 4);
									String endMonth = reviewPeriodTo.substring(5, 7);
									String jsonString = "{" + "\"FirstName\": \"" + firstName + "\","
											+ "\"LastName\": \"" + lastName + "\"," + "\"CWID\": \"" + empId + "\","
											+ "\"AttachmentType\": " + "\"FinalMPPPerfEvalDOR\"" + ","
											+ "\"AttachmentMimeType\": \"" + attachmentMimeType + "\","
											+ "\"Attachment\":\"" + encodedPDF + "\"," + "\"CBID\": \"" + cbid + "\","
											+ "\"DepartmentID\": \"" + deptId + "\"," + "\"DocType\":" + "\"MPPPESD\""
											+ "," + "\"EndMonth\":\"" + endMonth + "\"," + "\"EndYear\":\"" + endYear
											+ "\"," + "\"OverallRating\":\"" + overallRating + "\","
											+ "\"EvaluationType\":\"" + evaluationType + "\"," + "\"StartMonth\":\""
											+ fromMonth + "\"," + "\"StartYear\":\"" + fromYear + "\","
											+ "\"EmpUserID\":\"" + empUserId + "\"," + "\"ManagerUserID\":\""
											+ managerUserId + "\"," + "\"HRCoordUserID\":\"" + hrCoordId + "\","
											+ "\"AppropriateAdminUserID\":\"" + administratorId + "\"}";

									if (encodedPDF != null && lastName != null && firstName != null) {
										log.error("Read inner suppoting doc");
										URL url = null;
										try {
											String filenetUrl = globalConfigService.getMppFilenetURL();
											url = new URL(filenetUrl);
											// log.info("jsonString=" + jsonString);
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
						} else {
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

								String fromYear = reviewPeriodFrom.substring(0, 4);
								String fromMonth = reviewPeriodFrom.substring(5, 7);
								String endYear = reviewPeriodTo.substring(0, 4);
								String endMonth = reviewPeriodTo.substring(5, 7);
								String jsonString = "{" + "\"FirstName\": \"" + firstName + "\","
										+ "\"LastName\": \"" + lastName + "\"," + "\"CWID\": \"" + empId + "\","
										+ "\"AttachmentType\": " + "\"FinalMPPPerfEvalDOR\"" + ","
										+ "\"AttachmentMimeType\": \"" + attachmentMimeType + "\","
										+ "\"Attachment\":\"" + encodedPDF + "\"," + "\"CBID\": \"" + cbid + "\","
										+ "\"DepartmentID\": \"" + deptId + "\"," + "\"DocType\":" + "\"MPPPESD\""
										+ "," + "\"EndMonth\":\"" + endMonth + "\"," + "\"EndYear\":\"" + endYear
										+ "\"," + "\"OverallRating\":\"" + overallRating + "\","
										+ "\"EvaluationType\":\"" + evaluationType + "\"," + "\"StartMonth\":\""
										+ fromMonth + "\"," + "\"StartYear\":\"" + fromYear + "\","
										+ "\"EmpUserID\":\"" + empUserId + "\"," + "\"ManagerUserID\":\""
										+ managerUserId + "\"," + "\"HRCoordUserID\":\"" + hrCoordId + "\","
										+ "\"AppropriateAdminUserID\":\"" + administratorId + "\"}";

								if (encodedPDF != null && lastName != null && firstName != null) {
									log.error("Read outer suppoting doc");
									URL url = null;
									try {
										String filenetUrl = globalConfigService.getMppFilenetURL();
										url = new URL(filenetUrl);

										// log.info("jsonString=" + jsonString);
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
