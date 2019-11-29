package com.aem.web.models.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = { "service.description=Set Value",
		"service.vendor=Adobe Systems",
		"process.label=CSUF Set Value of Element in XML" })
public class CSUFSetValueinDataXml implements WorkflowProcess {
	private static final Logger log = LoggerFactory
			.getLogger(CSUFSetValueinDataXml.class);

	public CSUFSetValueinDataXml() {
	}

	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap arg2)
			throws com.adobe.granite.workflow.WorkflowException {
		System.out.println("The process arguments are "
				+ ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		InputStream xmlDataStream = null;
		InputStream is1 = null;
		log.info("Start of SetValueinDataXml");
		log.info("The string I got was ..."
				+ ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		String params = ((String) arg2.get("PROCESS_ARGS", "string"))
				.toString();

		String[] parameters = params.split(",");
		String nodeName = parameters[0];
		String value = parameters[1];
		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		Session session = resolver.adaptTo(Session.class);

		// Session session = (Session) workflowSession.adaptTo(Session.class);

		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		log.info("The payload  in HandleCMSubmission is "
				+ workItem.getWorkflowData().getPayload().toString());

		String dataFilePath = payloadPath + "/Data.xml/jcr:content";
		System.out.println("The datafilepath is " + dataFilePath);
		log.info("The datafilepath is " + dataFilePath);
		// Session session = (Session) workflowSession.adaptTo(Session.class);
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document xmlDocument = null;
		javax.jcr.Node xmlDataNode = null;

		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.info("xmlFiles inside ");
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath);
			if (filePath.contains("Data.xml")) {

				filePath = attachmentXml.getPath().concat("/jcr:content");
				// log.info("xmlFiles=" + filePath);
				// var/fd/dashboard/payload/server0/2019-08-07_3/523TS2EV2Q2XKMLHUNVXUQKTJU_6/Data.xml
				Node subNode = resolver.getResource(filePath).adaptTo(
						Node.class);

				try {
					xmlDataStream = subNode.getProperty("jcr:data").getBinary()
							.getStream();
					log.info("Got InputStream.... and the size available is ..."
							+ xmlDataStream.available());
					XPath xPath = XPathFactory.newInstance().newXPath();
					factory = DocumentBuilderFactory.newInstance();
					builder = factory.newDocumentBuilder();
					xmlDocument = builder.parse(xmlDataStream);
					org.w3c.dom.Node node = null;
					try {
						node = (org.w3c.dom.Node) xPath.compile(nodeName)
								.evaluate(xmlDocument, XPathConstants.NODE);
					} catch (XPathExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info("%%%%Bingo Getting node text content"
							+ node.getTextContent());
					node.setTextContent(value);
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					DOMSource source = new DOMSource(xmlDocument);
					StreamResult outputTarget = new StreamResult(outputStream);
					TransformerFactory.newInstance().newTransformer()
							.transform(source, outputTarget);
					is1 = new ByteArrayInputStream(outputStream.toByteArray());

					Binary binary = session.getValueFactory().createBinary(is1);

					xmlDataNode.setProperty("jcr:data", binary);

				} catch (ValueFormatException e2) {
					log.info("Exception1=" + e2);
					e2.printStackTrace();
				} catch (PathNotFoundException e2) {
					log.info("Exception2=" + e2);
					e2.printStackTrace();
				} catch (RepositoryException e2) {
					log.info("Exception3=" + e2);
					e2.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					if (xmlDataStream != null || is1 != null) {
						try {
							xmlDataStream.close();
							is1.close();
						} catch (IOException e) {
							log.error("Unable to close inputstream in SetValueinDataXml"
									+ e.getMessage());
							e.printStackTrace();
						}
					}

				}

			}

		}

	}
}
