package com.aem.web.models.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Binary;
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
		"process.label=Set Value of Element in Submitted XML" })
public class SetValueinXml implements WorkflowProcess {
	private static final Logger log = LoggerFactory.getLogger(SetValueinXml.class);

	public SetValueinXml() {
		
	}

	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2)
			throws com.adobe.granite.workflow.WorkflowException {
		System.out.println("The process arguments are " + ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		InputStream xmlDataStream = null;
		log.info("Start of SetValueinXml");
		log.info("The string I got was ..." + ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		String params = ((String) arg2.get("PROCESS_ARGS", "string")).toString();

		String[] parameters = params.split(",");
		String xmlFileName = "/" + parameters[0] + "/";
		String nodeName = parameters[1];
		String value = parameters[2];
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		log.info("The payload  in HandleCMSubmission is " + workItem.getWorkflowData().getPayload().toString());

		String dataFilePath = payloadPath + xmlFileName + "jcr:content";
		System.out.println("The datafilepath is " + dataFilePath);
		log.info("The datafilepath is " + dataFilePath);
		Session session = (Session) workflowSession.adaptTo(Session.class);
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document xmlDocument = null;
		javax.jcr.Node xmlDataNode = null;
		try {
			xmlDataNode = session.getNode(dataFilePath);
		} catch (PathNotFoundException e1) {
			log.error("PathNotFoundException in SetValueinXml" + e1.getMessage());
			e1.printStackTrace();
			
		} catch (RepositoryException e1) {
			log.error("RepositoryException in SetValueinXml" + e1.getMessage());
			e1.printStackTrace();
			
		}
		try {
			xmlDataStream = xmlDataNode.getProperty("jcr:data").getBinary().getStream();
			log.info("Got InputStream.... and the size available is ..." + xmlDataStream.available());
			XPath xPath = XPathFactory.newInstance().newXPath();
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			xmlDocument = builder.parse(xmlDataStream);
			org.w3c.dom.Node node = (org.w3c.dom.Node) xPath.compile(nodeName).evaluate(xmlDocument,
					XPathConstants.NODE);
			log.info("%%%%Bingo Getting node text content" + node.getTextContent());
			node.setTextContent(value);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult outputTarget = new StreamResult(outputStream);
			TransformerFactory.newInstance().newTransformer().transform(source, outputTarget);
			InputStream is1 = new ByteArrayInputStream(outputStream.toByteArray());
			Binary binary = session.getValueFactory().createBinary(is1);
			xmlDataNode.setProperty("jcr:data", binary);
			
			/*
			 //Added for Debugging
	         if(session.hasPendingChanges()) {
	        	 log.info("*******************Session has pending changes in SetValueinXml *************");
	        	 Thread.sleep(1000);
	        	 session.refresh(true);
	        	 session.save();
	        	 session.refresh(false);
	         } else {
	        	 session.save();
	        	 session.refresh(false);
	         }
	         */
	         
	         
			//session.refresh(true);
			//session.save();
			log.info("End of SetValueinXml");
		} catch (Exception e) {

			log.error("Got error in SetValueinXml" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (xmlDataStream != null) {
				try {
					xmlDataStream.close();
				} catch (IOException e) {
					log.error("Unable to close inputstream in SetValueinXml" + e.getMessage());
					e.printStackTrace();
				}
			}

		}
	}
}
