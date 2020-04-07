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

@Component(property = { Constants.SERVICE_DESCRIPTION + "=SPECustodialDOR", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=SPECustodialDOR" })
public class CSUFSPECUSTODIANFilenet implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFSPECUSTODIANFilenet.class);
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
		String firstName = "";
		String lastName = "";
		String encodedPDF = "";
		String empId = "";
		String cbid = "";
		String deptId = "";
		String overallRating = "";
		String evaluationType = "";
		String empUserId = "";
		String managerUserId = "";
		String hrCoordId = "";
		String administratorId = "";
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();

		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			String filePath = attachmentXml.getPath();
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
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
								.evaluate("//EmpID", doc, XPathConstants.NODE);
						empId = empIdNode.getFirstChild().getNodeValue();
						log.info("empId =" + empId);
						org.w3c.dom.Node fnNode = (org.w3c.dom.Node) xpath
								.evaluate("//StaffFirstName", doc,
										XPathConstants.NODE);
						firstName = fnNode.getFirstChild().getNodeValue();
						log.info("firstName =" + firstName);
						org.w3c.dom.Node lnNode = (org.w3c.dom.Node) xpath
								.evaluate("//StaffLastName", doc,
										XPathConstants.NODE);
						lastName = lnNode.getFirstChild().getNodeValue();
						log.info("StaffLastName =" + lastName);					
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
						e.printStackTrace();
					}

				}

			}
			// Payload path contains the PDF, get the inputstream, convert to
			// Base encoder

			if (filePath.contains("Staff_Performance_Evaluation_Unit_Custodial_Dist.pdf")) {
				log.info("filePath =" + filePath);
				filePath = attachmentXml.getPath().concat("/jcr:content");
				Node subNode = resolver.getResource(filePath).adaptTo(
						Node.class);
				try {
					is = subNode.getProperty("jcr:data").getBinary()
							.getStream();
					try {
						byte[] bytes = IOUtils.toByteArray(is);
						encodedPDF = Base64.getEncoder().encodeToString(bytes);
						
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
		String jsonString = "{" + "\"FirstName\": \"" + firstName + "\"," + "\"LastName\": \"" + lastName + "\","
				+ "\"CWID\": \"" + empId + "\"," + "\"AttachmentType\": " + "\"FinalSPECustodialDOR\"" + ","
				+ "\"AttachmentMimeType\": " + "\"application/pdf\"" + "," + "\"Attachment\":\"" + encodedPDF + "\","
				+ "\"CBID\": \"" + cbid + "\"," + "\"DepartmentID\": \"" + deptId + "\"," + "\"DocType\":" + "\"SPECUST\""
				+ "," + "\"EndMonth\":" + "\"04\"" + "," + "\"EndYear\":" + "\"2020\"" + "," + "\"OverallRating\":\""
				+ overallRating + "\"," + "\"EvaluationType\":\"" + evaluationType + "\"," + "\"StartMonth\":"
				+ "\"04\"" + "," + "\"StartYear\":" + "\"2019\"" + "," + "\"EmpUserID\":\"" + empUserId + "\","
				+ "\"ManagerUserID\":\"" + managerUserId + "\"," + "\"HRCoordUserID\":\"" + hrCoordId + "\","
				+ "\"AppropriateAdminUserID\":\"" + administratorId + "\"}";
	
		if (encodedPDF != null && lastName != null && firstName != null) {
			log.info("Read SPE Custodial DOR");
			URL url = null;
			try {
				String filenetUrl = globalConfigService.getStaffEvalFilenetURL();
				url = new URL(filenetUrl);
				//log.error("Json String:" + jsonString.toString());

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
