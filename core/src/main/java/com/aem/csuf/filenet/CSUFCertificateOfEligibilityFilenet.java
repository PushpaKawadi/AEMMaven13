package com.aem.csuf.filenet;

import java.io.IOException;
import java.io.InputStream;
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
import com.google.gson.JsonObject;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=DonationDOR",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFCertEligibilityFilenet" })
public class CSUFCertificateOfEligibilityFilenet implements WorkflowProcess {

	private static final Logger log = LoggerFactory
			.getLogger(CSUFPrePerfEvalFilenet.class);

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
		String ssn = null;
		String empId = null;
		String deptID = null;
		String dateInitiated = null;
		String dateComp = null;
		String logUserValue = null;
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

					org.w3c.dom.NodeList nList1 = doc
							.getElementsByTagName("afUnboundData");
					for (int temp1 = 0; temp1 < nList1.getLength(); temp1++) {
						org.w3c.dom.Node nNode1 = nList1.item(temp1);
						org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode1;
						logUserValue = eElement.getElementsByTagName("logUser")
								.item(0).getTextContent();
					}

					org.w3c.dom.NodeList nList = doc
							.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							empId = eElement.getElementsByTagName("EmplID")
									.item(0).getTextContent();
							dateInitiated = eElement
									.getElementsByTagName("DateInitiated")
									.item(0).getTextContent();

							SimpleDateFormat fromDate = new SimpleDateFormat(
									"yyyy-MM-dd");
							SimpleDateFormat toDate = new SimpleDateFormat(
									"MM/dd/yyyy");

							if (dateInitiated != null
									&& !dateInitiated.equals("")) {
								try {
									dateComp = toDate.format(fromDate
											.parse(dateInitiated));
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}

							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							ssn = eElement.getElementsByTagName("SSN").item(0)
									.getTextContent();
							deptID = eElement.getElementsByTagName("DeptID")
									.item(0).getTextContent();
							ssn = eElement.getElementsByTagName("SSN").item(0)
									.getTextContent();

						}
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

			if (filePath.contains("Certificate_Of_Eligibility.pdf")) {
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

		if (encodedPDF != null && empId != null) {
			JsonObject json = new JsonObject();
			json.addProperty("FirstName", firstName);
			json.addProperty("LastName", lastName);
			json.addProperty("CWID", empId);
			json.addProperty("SSN", ssn);
			json.addProperty("DepartmentID", deptID);
			json.addProperty("DocType", "COEFW");
			json.addProperty("InitiatedDate", dateComp);
			json.addProperty("EmpUserID", logUserValue);
			json.addProperty("AttachmentMimeType", "application/pdf");
			json.addProperty("Attachment", encodedPDF);

			log.info("Read Certificate Of Eligibility");
			URL url = null;
			try {
				String filenetUrl = globalConfigService
						.getHRBenefitsFilenetURL();
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
				os.write(json.toString().getBytes("utf-8"));
				os.close();
				con.getResponseCode();

			} catch (IOException e1) {
				log.error("IOException=" + e1.getMessage());
				e1.printStackTrace();
			} finally {
				con.disconnect();
			}

		}

	}
}
