package com.aem.csuf.filenet;

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
import com.aem.community.util.ConfigManager;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=DonationDOR", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFCataLeaveDonationFilenet" })
public class CSUFCataLeaveDonationFilenet implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFPrePerfEvalFilenet.class);

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
		//String rating = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		// Get the payload path and iterate the path to find Data.xml, Use
		// Document
		// factory to parse the xml and fetch the required values for the
		// filenet
		// attachment
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
						org.w3c.dom.Node empIdNode = (org.w3c.dom.Node) xpath.evaluate("//EmpId", doc,
								XPathConstants.NODE);
						empId = empIdNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath.evaluate("//FirstName", doc,
								XPathConstants.NODE);
						firstName = fnNode.getFirstChild().getNodeValue();

						org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath.evaluate("//LastName", doc,
								XPathConstants.NODE);
						lastName = lnNode.getFirstChild().getNodeValue();

//						org.w3c.dom.Node ratingNode = (org.w3c.dom.Node) xpath.evaluate("//OverallRating", doc,
//								XPathConstants.NODE);
//						rating = ratingNode.getFirstChild().getNodeValue();

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

			if (filePath.contains("Catastrophic_Leave_Donation.pdf")) {
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

		// Create the JSON with the required parameter from Data.xml, encoded
		// Base 64 to
		// the Filenet rest call to save the document
		String jsonString = "{" + "\"CWID\": \"" + empId + "\"," + "\"AttachmentType\": " + "\"CatastrophicLeaveRequestDOR\"" + "," + "\"AttachmentMimeType\": " + "\"application/pdf\"" + ","
				+ "\"EncodedPDF\":\"" + encodedPDF + "\"}";
		// log.error("lastName="+lastName);
		// log.error("firstName="+firstName);
		// log.error("empId="+empId);
		// log.error("Rating="+rating);
		//log.error("Json String:" + jsonString.toString());

		// log.error("encodedPDF="+encodedPDF);
		if (encodedPDF != null && empId != null) {
			log.info("Read Catastrophic ");
			URL url = null;
			try {
				String filenetUrl = ConfigManager.getValue("filenetUrl");
				url = new URL(filenetUrl);
				// url = new URL("");

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
				os.write(jsonString.getBytes("utf-8"));
				os.close();
				con.getResponseCode();

			} catch (IOException e1) {
				log.error("IOException=" + e1.getMessage());
				e1.printStackTrace();
			}

		}

	}
}
