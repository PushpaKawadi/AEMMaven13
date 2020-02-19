package com.aem.web.models.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
		"process.label=Read Metadata Value and Set Element in XML" })
public class ReadMetadataMap implements WorkflowProcess {
	private static final Logger log = LoggerFactory.getLogger(ReadMetadataMap.class);

//	public ReadMetadataMap() {
//	}

	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2)
			throws com.adobe.granite.workflow.WorkflowException {
		//System.out.println("The process arguments are " + ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		InputStream xmlDataStream = null;
		log.info("Start of Read Metadata Value and Set Element in XML");
		log.info("The string I got was ..." + ((String) arg2.get("PROCESS_ARGS", "string")).toString());
		String params = ((String) arg2.get("PROCESS_ARGS", "string")).toString();
		
		String[] parameters = params.split(",");
		String nodeName = parameters[0];
		String keyObtained = parameters[1];
		String value = "false";
		String valStr = "";
		for (Map.Entry<String, Object> entry : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
			if (entry.getKey().matches(keyObtained)) {
				valStr = entry.getValue().toString();
				value = valStr;
			}
		}		
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		log.info("The payload  in HandleCMSubmission is " + workItem.getWorkflowData().getPayload().toString());

		String dataFilePath = payloadPath + "/Data.xml/jcr:content";
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
			log.error("PathNotFoundException in Read Metadata Value and Set Element in XML" + e1.getMessage());
			e1.printStackTrace();
		} catch (RepositoryException e1) {
			log.error("RepositoryException in Read Metadata Value and Set Element in XML" + e1.getMessage());
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
			
			log.info("End of Read Metadata Value and Set Element in XML");
		} catch (Exception e) {

			log.error("Got error in Read Metadata Value and Set Element in XML" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (xmlDataStream != null) {
				try {
					xmlDataStream.close();
				} catch (IOException e) {
					log.error("Unable to close inputstream in Read Metadata Value and Set Element in XML" + e.getMessage());
					e.printStackTrace();
				}
			}

		}
	}
}
