package com.aem.csuf.filenet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.aem.community.util.ConfigManager;
import com.google.gson.JsonObject;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=SCPRFacultyDOR", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=SCPRFacultyDOR" })
public class CSUFSCPRFacultyFilenet implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFSCPRFacultyFilenet.class);

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
		String deptID = null;
		String empUserVal = null;
		String dateInitiated = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.info("xmlFiles inside ");
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath);
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("xmlFiles=" + filePath);

				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
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
					XPath xpath = XPathFactory.newInstance().newXPath();
					try {
						org.w3c.dom.Node empIdNode = (org.w3c.dom.Node) xpath.evaluate("//EmpID", doc,
								XPathConstants.NODE);
						empId = empIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath.evaluate("//EmpFname", doc,
								XPathConstants.NODE);
						firstName = fnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath.evaluate("//EmpLname", doc,
								XPathConstants.NODE);
						lastName = lnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node logUserNode = (org.w3c.dom.Node) xpath.evaluate("//empUserId", doc,
								XPathConstants.NODE);
						empUserVal = logUserNode.getFirstChild().getNodeValue();
						org.w3c.dom.Node deptIdNode = (org.w3c.dom.Node) xpath.evaluate("//HRDeptID", doc,
								XPathConstants.NODE);
						deptID = deptIdNode.getFirstChild().getNodeValue();
						
						org.w3c.dom.Node dateInitiatedNode = (org.w3c.dom.Node) xpath.evaluate("//EmpDate", doc,
								XPathConstants.NODE);
						dateInitiated = dateInitiatedNode.getFirstChild().getNodeValue();

						SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat toDate = new SimpleDateFormat("MM/dd/yyyy");

						if (dateInitiated != null && !dateInitiated.equals("")) {
							try {
								dateInitiated = toDate.format(fromDate.parse(dateInitiated));
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
			// Payload path contains the PDF, get the inputstream, convert to
			// Base encoder

			if (filePath.contains("Special_Consultant_Pay_Request_Faculty.pdf")) {
				log.info("filePath =" + filePath);
				filePath = attachmentXml.getPath().concat("/jcr:content");
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);
				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
					try {
						byte[] bytes = IOUtils.toByteArray(is);
						encodedPDF = Base64.getEncoder().encodeToString(bytes);
						// log.info("encodedPDF="+encodedPDF);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ValueFormatException e) {
					log.error("ValueFormatException=" + e.getMessage());
					e.printStackTrace();
				} catch (PathNotFoundException e) {
					log.error("PathNotFoundException=" + e.getMessage());
					e.printStackTrace();
				} catch (RepositoryException e) {
					log.error("RepositoryException=" + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						log.error("IOException=" + e.getMessage());
						e.printStackTrace();
					}

				}
			}
		}
		JsonObject json = new JsonObject();	
		
		json.addProperty("InitiatedDate", dateInitiated);
		json.addProperty("DepartmentID", deptID);
		json.addProperty("DocType", "SPCON");
		json.addProperty("CWID", empId);
		json.addProperty("FirstName", firstName);
		json.addProperty("LastName", lastName);
		json.addProperty("SSN", "");
		json.addProperty("EmpUserID", empUserVal);
		json.addProperty("AttachmentMimeType", "application/pdf");
		json.addProperty("Attachment", encodedPDF);

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
			log.info("Read SCPR Faculty DOR");
			os.close();
			int responseCode = con.getResponseCode();
			log.debug("Result=" + con.getResponseCode());
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				log.info("Response from Filenet=" + response.toString());
				if (response.toString().contains("Failed")) {
					log.info("Response code=" + response.toString());
				}
		}

		} catch (IOException e1) {
			log.error("IOException=" + e1.getMessage());
			e1.printStackTrace();
		} finally {
			con.disconnect();
		}
	}
}
