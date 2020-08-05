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
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.aem.community.util.DBUtil;
import com.google.gson.JsonObject;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=ShortAppDOR", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=CSUFShortAppEmpFeeWaiverFilenet" })
public class CSUFShortAppEmpFeeWaiverFilenet implements WorkflowProcess {
	
	@Reference
	private GlobalConfigService globalConfigService;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;
	
	private static final Logger log = LoggerFactory.getLogger(CSUFShortAppEmpFeeWaiverFilenet.class);

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
		String ssnNo = "";
		String deptID = "";
		String logUserVal = "";
		String signedDate ="";
		String dateComp = "";
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		Connection conn = null;
		LinkedHashMap<String, Object> dataMap = null;

		String dataSourceVal = globalConfigService.getAEMDataSource();
		conn = jdbcConnectionService.getDBConnection(dataSourceVal);

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
					org.w3c.dom.NodeList nList = doc
							.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							
							empId = eElement.getElementsByTagName("EmplID")
									.item(0).getTextContent();
							firstName = eElement
									.getElementsByTagName("FirstName").item(0)
									.getTextContent();
							lastName = eElement
									.getElementsByTagName("LastName").item(0)
									.getTextContent();
							logUserVal = eElement.getElementsByTagName("userID")
									.item(0).getTextContent();
							ssnNo = eElement.getElementsByTagName("SSN")
									.item(0).getTextContent();
							signedDate = eElement.getElementsByTagName("SignedDate")
									.item(0).getTextContent();
							SimpleDateFormat fromDate = new SimpleDateFormat(
									"yyyy-MM-dd");
							SimpleDateFormat toDate = new SimpleDateFormat(
									"MM/dd/yyyy");
							if (signedDate != null
									&& !signedDate.equals("")) {
								try {
									dateComp = toDate.format(fromDate
											.parse(signedDate));
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}

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

			if (filePath.contains("Short_Application_Employee_Fee_Waiver.pdf")) {
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

		
		if (encodedPDF != null && empId != null) {
			JsonObject json = new JsonObject();
			json.addProperty("FirstName", firstName);
			json.addProperty("LastName", lastName);
			json.addProperty("CWID", empId);
			json.addProperty("SSN", ssnNo);
			json.addProperty("DepartmentID", "");
			json.addProperty("DocType", "SAEFW");
			json.addProperty("InitiatedDate", dateComp);
			json.addProperty("EmpUserID", logUserVal);
			json.addProperty("AttachmentMimeType", "application/pdf");
			json.addProperty("Attachment", encodedPDF);
			String filenetUrl ="";
			log.error("Short App Emp=" +json.toString());
			URL url = null;
			try {
				filenetUrl = globalConfigService.getHRBenefitsFilenetURL();
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
				int responseCode = con.getResponseCode();
				log.info("POST Response Code to Filenet :: " + responseCode);
				if (responseCode == HttpURLConnection.HTTP_OK) { 
					BufferedReader in = new BufferedReader(new InputStreamReader(
							con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					log.info("Response from Filenet============="+response.toString()); //{5E77A58F-EA81-4A33-8C35-B2DA3FD55AA4}
					
					
					DBUtil dbUtil = new DBUtil();
					String tableName = "AEM_AUDIT_TRACE";
					
					dataMap = new LinkedHashMap<String, Object>();
					
					Timestamp auditStTime = new java.sql.Timestamp(System.currentTimeMillis());
					
					dataMap.put("EVENT_TYPE", "Filenet");
					dataMap.put("AUDIT_TIME", auditStTime);
					dataMap.put("FILENET_URL", filenetUrl);
					dataMap.put("DATA_PROCESSED", "0");
					dataMap.put("FILENET_JSON", json.toString());
					dataMap.put("FORM_NAME", "Short App Emp Fee Waiver");
					
					dbUtil.insertAutitTrace(conn, dataMap, tableName);
				}
			} catch (IOException e1) {
				log.error("IOException=" + e1.getMessage());
				e1.printStackTrace();
			} finally{
				con.disconnect();
			}

		}

	}
}
