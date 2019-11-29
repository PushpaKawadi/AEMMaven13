package com.aem.web.models.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = { "service.description=Set Value", "service.vendor=Adobe Systems",
		"process.label=New Payload XML11" })
public class NewPayloadModified11 implements WorkflowProcess {
	private static final Logger log = LoggerFactory.getLogger(NewPayloadModified11.class);

	public NewPayloadModified11() {
	}

	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2)
			throws com.adobe.granite.workflow.WorkflowException {
		System.out.println("The process arguments are " + ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		InputStream xmlDataStream = null;
		InputStream is1 = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		log.info("Start of NewPayloadModified11");
		log.info("The string I got was ..." + ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		String params = ((String) arg2.get("PROCESS_ARGS", "string")).toString();

		String[] parameters = params.split(",");
		String OldXmlFileName = "/" + parameters[0] + "/";
		log.info("NewPayloadModified11 OldXmlFileName=" + OldXmlFileName); // /Data.xml/
		String newXmlFileName = parameters[1];
		log.info("NewPayloadModified11 newXmlFileName=" + newXmlFileName); // Data1.xml
		String nodeName = parameters[2];
		log.info("NewPayloadModified11 nodeName=" + nodeName); // afData/afBoundData/form1/Instructor1
		String value = parameters[3];
		log.info("NewPayloadModified11 value=" + value); // instructorone
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		log.info("The payload  in HandleCMSubmission is " + payloadPath); /// var/fd/dashboard/payload/server0/2019-09-02/XXS4H3IMFPQVYBXDEVS4WG4IHY_3

		String dataFilePath = payloadPath + OldXmlFileName + "jcr:content";
		// String newdataFilePath = payloadPath + newXmlFileName +
		// "jcr:content";
		// System.out.println("The datafilepath is " + dataFilePath);
		log.info("The datafilepath is " + dataFilePath); /// var/fd/dashboard/payload/server0/2019-09-02/XXS4H3IMFPQVYBXDEVS4WG4IHY_3/Data.xml/jcr:content
		Session session = (Session) workflowSession.adaptTo(Session.class);
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document xmlDocument = null;
		javax.jcr.Node xmlDataNode = null;
		try {
			xmlDataNode = session.getNode(dataFilePath);
		} catch (PathNotFoundException e1) {
			log.error("PathNotFoundException in NewPayloadModified11=" + e1.getMessage());
			e1.printStackTrace();
		} catch (RepositoryException e1) {
			log.error("RepositoryException in NewPayloadModified11=" + e1.getMessage());
			e1.printStackTrace();
		} catch (Exception e) {
			log.error("Exception in NewPayloadModified11=" + e.getMessage());
			e.printStackTrace();
		}
		try {
			xmlDataStream = xmlDataNode.getProperty("jcr:data").getBinary().getStream();
			log.info("Got InputStream.... and the size available is ..." + xmlDataStream.available()); // 5539
			XPath xPath = XPathFactory.newInstance().newXPath();
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			xmlDocument = builder.parse(xmlDataStream);
			org.w3c.dom.Node node = (org.w3c.dom.Node) xPath.compile(nodeName).evaluate(xmlDocument,
					XPathConstants.NODE);
			log.info("%%%%Bingo Getting node text content" + node.getTextContent());
			node.setTextContent(value);
			// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult outputTarget = new StreamResult(outputStream);
			TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
			is1 = new ByteArrayInputStream(outputStream.toByteArray());

			// Node copy
			try {

				// boolean nodeCheck = session.nodeExists(payloadPath);

				Node node1 = session.getNode(payloadPath); // Get the client lib
															// node in which to
															// write the posted
															// file
				javax.jcr.ValueFactory valueFactory = session.getValueFactory();
				javax.jcr.Binary contentValue = valueFactory.createBinary(is1);

				String newPath = payloadPath.concat("newXmlFileName").concat("jcr:content");
				Node fileNode = node1.addNode(newXmlFileName, "nt:file");
				boolean newNodeCheck = session.nodeExists(newPath);
				if (newNodeCheck == true) {
					log.info("Node Exists=" + newNodeCheck);
				} else {
					fileNode.addMixin("mix:referenceable");
					Node resNode = fileNode.addNode("jcr:content", "nt:resource");

					log.info("Node Name=" + resNode.getName());
					log.info("Node Path=" + resNode.getPath());
					log.info("Nodes =" + resNode.getNodes());

					resNode.setProperty("jcr:mimeType", "application/octet-stream");
					resNode.setProperty("jcr:data", contentValue);

					/*
					 //Added for Debugging
			         if(session.hasPendingChanges()) {
			        	 log.info("*******************Session has pending changes in NewPayloadModified11 *************");
			        	 Thread.sleep(1000);
			        	 session.refresh(true);
			        	 session.save();
			        	 session.refresh(false);
			         } else {
			        	 session.save();
			        	 session.refresh(false);
			         }
			         */
			         
					// log.info("Node Content=" + contentValue);
					//session.refresh(true);
					//session.save();

				}
				// node1.
			} catch (RepositoryException re) {
				log.error("RepositoryException in NewPayloadModified10=" + re.getMessage());
				re.printStackTrace();
			}
			// Ends
			log.info("End of NewPayloadModified11");

		} catch (ItemExistsException i) {
			log.error("ItemExistsException in NewPayloadModified11" + i.getMessage());
			i.printStackTrace();
		} catch (PathNotFoundException i) {
			log.error("PathNotFoundException in NewPayloadModified11" + i.getMessage());
			i.printStackTrace();
		} catch (Exception e) {
			log.error("Got error in NewPayloadModified11" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (is1 != null || xmlDataStream != null) {
				try {
					is1.close();
					xmlDataStream.close();
					outputStream.close();
				} catch (IOException e) {
					log.error("Unable to close inputstream in NewPayloadModified11" + e.getMessage());
					e.printStackTrace();

				}
			}

		}
	}

}
