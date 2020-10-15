package com.aem.csuf.filenet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Date; 
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
import com.google.gson.JsonObject;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=careerDevelopmentPlanSupDocFileNet",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=careerDevelopmentPlanSupDocFileNet" })
public class CareerDevelopmentPlanSupDocFileNet implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CareerDevelopmentPlanSupDocFileNet.class);

	@Reference
	private GlobalConfigService globalConfigService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap processArguments) throws WorkflowException {
		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;
		String firstName = null;
		String lastName = null;
		String encodedPDF = null;
		String empId = null;
		String deptID = null;
		String logUserVal = null;
		String mimeType = null;
		String docEncoded1 = null;
		InputStream sd1 = null;
		String initiatedDate = null;
		String dateComp = null;
		Resource attachment1 = null;
		//Date date = null;
		//SimpleDateFormatter dateFormatter = null;

		String attachmentsPath = "Attachments";
		Resource xmlNode = resolver.getResource(payloadPath);

		// if (xmlNode != null) {
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			String filePath = attachmentXml.getPath();
			if (filePath.contains("Attachments")) {
				String attachmentsFilePath = payloadPath + "/"
						+ attachmentsPath + "/supportDoc1";
				attachment1 = resolver
						.getResource(attachmentsFilePath);
				if (attachment1 != null) {
					Iterator<Resource> attFiles = attachment1.listChildren();
					while (attFiles.hasNext()) {
						Resource supDoc1 = attFiles.next();

						String attDoc1 = supDoc1.getPath().concat(
								"/jcr:content");

						String fileMimeType = supDoc1.getName();
						if (fileMimeType.toLowerCase().endsWith(".jpg")
								|| fileMimeType.toLowerCase().endsWith(".jpeg")) {
							mimeType = "image/jpeg";
						} else if (fileMimeType.toLowerCase().endsWith(".pdf")) {
							mimeType = "application/pdf";
						} else if (fileMimeType.toLowerCase().endsWith(".png")) {
							mimeType = "image/png";
						} else if (fileMimeType.toLowerCase().endsWith(".tiff")) {
							mimeType = "image/tiff";
						} else {
							mimeType = "application/pdf";
						}

						Node subNode1 = resolver.getResource(attDoc1).adaptTo(
								Node.class);

						try {
							sd1 = subNode1.getProperty("jcr:data").getBinary()
									.getStream();
							byte[] bytes = IOUtils.toByteArray(sd1);
							// log.error("bytes="+bytes);
							docEncoded1 = Base64.getEncoder().encodeToString(
									bytes);
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
				log.info("xmlFiles=" + filePath);

				Node subNode = resolver.getResource(filePath).adaptTo(
						Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary()
							.getStream();
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
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
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
					XPath xpath = XPathFactory.newInstance().newXPath();
					try {
						org.w3c.dom.Node empIdNode = (org.w3c.dom.Node) xpath
								.evaluate("//emplID", doc, XPathConstants.NODE);
						empId = empIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath
								.evaluate("//firstName", doc,
										XPathConstants.NODE);
						firstName = fnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath
								.evaluate("//lastName", doc,
										XPathConstants.NODE);
						lastName = lnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node deptNode = (org.w3c.dom.Node) xpath
								.evaluate("//departmentID", doc,
										XPathConstants.NODE);
						deptID = deptNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node logUserNode = (org.w3c.dom.Node) xpath
								.evaluate("//LogUser", doc, XPathConstants.NODE);
						logUserVal = logUserNode.getFirstChild().getNodeValue();
						
						org.w3c.dom.Node initiatedDateNode = (org.w3c.dom.Node) xpath
								.evaluate("//dateInitiated", doc, XPathConstants.NODE);
						initiatedDate = initiatedDateNode.getFirstChild().getNodeValue();
						SimpleDateFormat fromDate = new SimpleDateFormat(
								"yyyy-MM-dd");
						SimpleDateFormat toDate = new SimpleDateFormat(
								"MM/dd/yyyy");

						if (initiatedDate != null
								&& !initiatedDate.equals("")) {
							try {
								dateComp = toDate.format(fromDate
										.parse(initiatedDate));
							} catch (ParseException e) {
								e.printStackTrace();
							}
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
						e.printStackTrace();
					}

				}

			}
		}
		JsonObject json = new JsonObject();
		if(attachment1 != null) {
			log.info("Inside JsonOBject, attache is==========="+attachment1);
			json.addProperty("FirstName", firstName);
			json.addProperty("LastName", lastName);
			json.addProperty("CWID", empId);
			json.addProperty("SSN", "");
			json.addProperty("DepartmentID", deptID);
			json.addProperty("DocType", "CDPSD");
			json.addProperty("InitiatedDate", dateComp);
			json.addProperty("EmpUserID", logUserVal);
			json.addProperty("AttachmentMimeType", mimeType);
			json.addProperty("Attachment", docEncoded1);
		}
		
		//log.info("The JSON STRING="+json.toString());

		log.info("Read Career Development Plan");
		URL url = null;
		try {
			String filenetUrl = globalConfigService.getHRBenefitsFilenetURL();
			url = new URL(filenetUrl);
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
			log.info("ProtocolException=" + e.getMessage());
			e.printStackTrace();
		}
		con.setDoOutput(true);

		try (OutputStream os = con.getOutputStream()) {
			os.write(json.toString().getBytes("utf-8"));
			os.close();
			con.getResponseCode();
			log.debug("Result=" + con.getResponseCode());

		} catch (IOException e1) {
			log.error("IOException=" + e1.getMessage());
			e1.printStackTrace();
		}finally{
			con.disconnect();
		}
	}
}
