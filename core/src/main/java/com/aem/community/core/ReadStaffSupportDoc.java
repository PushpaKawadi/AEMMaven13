package com.aem.community.core;

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
import com.aem.community.util.ConfigManager;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Read Staff Support Doc",
		Constants.SERVICE_VENDOR + "=Adobe Systems", "process.label" + "=Read Staff Support Doc" })
public class ReadStaffSupportDoc implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(ReadStaffSupportDoc.class);
	private String mimeType;

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
		String docEncoded2 = null;
		String studentID = null;
		String caseID = null;
		String major = null;
		String termCode = null;
		String termDescription = null;
		String typeOfForm = null;
		String WithdrawalType = null;

		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.error("xmlFiles inside ");
			String filePath = attachmentXml.getPath();
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

						if (typeOfForm.equals("1")) {
							WithdrawalType = "Non-Medical";
						} else {
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
			if (filePath.contains("InstructorTaskAttachments")) {
				String instructorAttachmentsPath = "InstructorTaskAttachments";
				// String attachmentsFilePath = payloadPath + "/" +
				// attachmentsPath + "/supportDoc1";
				String instructorAttachmentsFilePath = payloadPath + "/" + instructorAttachmentsPath;
				// log.info("length="+attachmentsFilePath.length());
				Resource instructorAttachments = resolver.getResource(instructorAttachmentsFilePath);

				if (instructorAttachments != null) {
					Iterator<Resource> instructorAttFiles = instructorAttachments.listChildren();
					while (instructorAttFiles.hasNext()) {
						Resource instructorSupDoc = instructorAttFiles.next();
						Path InstructorSource = Paths.get(instructorSupDoc.getPath());
						String instructorMimeType = "";
						try {
							instructorMimeType = Files.probeContentType(InstructorSource);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// log.error("Mime Type =" + instructorMimeType);

						String instructorAttDoc = instructorSupDoc.getPath().concat("/jcr:content");
						Node instructorSubNode = resolver.getResource(instructorAttDoc).adaptTo(Node.class);

						try {
							sd1 = instructorSubNode.getProperty("jcr:data").getBinary().getStream();
							byte[] bytes = IOUtils.toByteArray(sd1);
							// log.error("bytes="+bytes);
							docEncoded1 = Base64.getEncoder().encodeToString(bytes);

							String instructorJsonString = "{" + "\"FirstName\": \"" + firstName + "\","
									+ "\"LastName\": \"" + lastName + "\"," + "\"CWID\": \"" + studentID + "\","
									+ "\"CaseID\": \"" + caseID + "\"," + "\"Major\": \"" + major + "\","
									+ "\"TermCode\": \"" + termCode + "\"," + "\"TermDescription\": \""
									+ termDescription + "\"," + "\"Attachment\": \"" + docEncoded1 + "\","
									+ "\"AttachmentType\": " + "\"SupportingDocument\"" + ","
									+ "\"AttachmentMimeType\": " + instructorMimeType + "," + "\"WithdrawalType\": \""
									+ WithdrawalType + "\"}";

							if (docEncoded1 != null && lastName != null && firstName != null) {
								log.error("Read Instructor doc");
								URL url = null;
								try {
									String filenetUrl = ConfigManager.getValue("filenetUrl");
									url = new URL(filenetUrl);
									//log.info("instructorJsonString=" + instructorJsonString);
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
									os.write(instructorJsonString.getBytes("utf-8"));
									os.close();
									con.getResponseCode();
									log.error("Res=" + con.getResponseCode());
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
								try {
									con.getInputStream();
									log.error("Here1=" + con.getInputStream());
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
								sd1.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}
				}
			}

			if (filePath.contains("ChairTaskAttachments")) {
				String chairAttachmentsPath = "ChairTaskAttachments";
				// String attachmentsFilePath = payloadPath + "/" +
				// attachmentsPath + "/supportDoc1";
				String chairAttachmentsFilePath = payloadPath + "/" + chairAttachmentsPath;
				// log.info("length="+attachmentsFilePath.length());
				Resource chairAttachments = resolver.getResource(chairAttachmentsFilePath);

				if (chairAttachments != null) {
					Iterator<Resource> chairAttFiles = chairAttachments.listChildren();
					while (chairAttFiles.hasNext()) {
						Resource chairSupDoc = chairAttFiles.next();

						Path chairSource = Paths.get(chairSupDoc.getPath());

						String chairMimeType = "";
						try {
							chairMimeType = Files.probeContentType(chairSource);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// log.error("Mime Type =" + chairMimeType);

						String chairAttDoc = chairSupDoc.getPath().concat("/jcr:content");
						Node chairSubNode = resolver.getResource(chairAttDoc).adaptTo(Node.class);

						try {
							sd1 = chairSubNode.getProperty("jcr:data").getBinary().getStream();
							byte[] bytes = IOUtils.toByteArray(sd1);
							// log.error("bytes="+bytes);
							docEncoded2 = Base64.getEncoder().encodeToString(bytes);

							String chairJsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \""
									+ lastName + "\"," + "\"CWID\": \"" + studentID + "\"," + "\"CaseID\": \"" + caseID
									+ "\"," + "\"Major\": \"" + major + "\"," + "\"TermCode\": \"" + termCode + "\","
									+ "\"TermDescription\": \"" + termDescription + "\"," + "\"Attachment\": \""
									+ docEncoded2 + "\"," + "\"AttachmentType\": " + "\"SupportingDocument\"" + ","
									+ "\"AttachmentMimeType\": " + chairMimeType + "," + "\"WithdrawalType\": \""
									+ WithdrawalType + "\"}";

							if (docEncoded2 != null && lastName != null && firstName != null) {
								log.error("Read Chair doc");
								URL url = null;
								try {
									String filenetUrl = ConfigManager.getValue("filenetUrl");
									url = new URL(filenetUrl);
									//log.info("chairJsonString=" + chairJsonString);
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
									os.write(chairJsonString.getBytes("utf-8"));
									os.close();
									con.getResponseCode();
									log.error("Res=" + con.getResponseCode());
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
								try {
									con.getInputStream();
									log.error("Here1=" + con.getInputStream());
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
								sd1.close();
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
