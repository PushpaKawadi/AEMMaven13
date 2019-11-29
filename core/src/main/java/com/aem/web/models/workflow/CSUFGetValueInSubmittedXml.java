package com.aem.web.models.workflow;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = { "service.description=Get Value",
		"service.vendor=Adobe Systems",
		"process.label=CSUF Get Value of Element in Submitted Xml" })
public class CSUFGetValueInSubmittedXml implements WorkflowProcess {
	private static final Logger log = LoggerFactory
			.getLogger(CSUFGetValueInSubmittedXml.class);

	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap arg2) throws WorkflowException {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document xmlDocument = null;
		InputStream xmlDataStream = null;
		log.info("Start of GetValueinXml");
		log.info("The string I got was ..."
				+ ((String) arg2.get("PROCESS_ARGS", "string")).toString());

		ResourceResolver resolver = workflowSession
				.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();

		// String payloadPath =
		// workItem.getWorkflowData().getPayload().toString();
		log.info("The payload  in HandleCMSubmission is "
				+ workItem.getWorkflowData().getPayload().toString());

		Resource xmlNode = resolver.getResource(payloadPath);

		String paramsnew = ((String) arg2.get("PROCESS_ARGS", "string"))
				.toString();
		String[] parameters1 = paramsnew.split(",");
		String xmlFileName1 = "/" + parameters1[0] + "/";
		String dataFilePath = payloadPath + xmlFileName1 + "jcr:content";

		// String dataFilePathNew = payloadPath + xmlFileName1;
		// /var/fd/dashboard/payload/server0/2019-09-12/5ZM6HGXYRKU2KQY5BUNXDCKURY_7/Data9.xml
		//String dataFilePathNew = xmlFileName1;
		// Session session = (Session)workflowSession.adaptTo(Session.class);
		// DocumentBuilderFactory factory = null;
		// DocumentBuilder builder = null;
		// Document xmlDocument = null;
		//Node xmlDataNode = null;

		// String dataFilePathNew = payloadPath + xmlFileName1;
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		//log.info("dataFilePathNew is " + dataFilePathNew);
		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			// log.info("xmlFiles inside ");
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath); // /var/fd/dashboard/payload/server0/2019-09-12/5ZM6HGXYRKU2KQY5BUNXDCKURY_7/Data1.xml
			// if (filePath.contains(dataFilePathNew)) {

			// if (filePath.contains("Data.xml")) {

			filePath = attachmentXml.getPath().concat("/jcr:content");
			filePath = dataFilePath;
			log.error("Focus=" + filePath);
			// log.info("xmlFiles=" + filePath);
			// var/fd/dashboard/payload/server0/2019-08-07_3/523TS2EV2Q2XKMLHUNVXUQKTJU_6/Data.xml
			Node subNode = resolver.getResource(filePath).adaptTo(Node.class);

			try {
				xmlDataStream = subNode.getProperty("jcr:data").getBinary()
						.getStream();

				XPath xPath = XPathFactory.newInstance().newXPath();
				factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				xmlDocument = builder.parse(xmlDataStream);
				String params = ((String) arg2.get("PROCESS_ARGS", "string"))
						.toString();
				String[] parameters = params.split(",");
				String[] var15 = parameters;
				int var16 = parameters.length;

				for (int var17 = 1; var17 < var16; ++var17) {
					String items = var15[var17];
					String[] itemsArray = items.split("=");
					String nodeNameone = itemsArray[1];
					String valueone = itemsArray[0];
					org.w3c.dom.Node node = (org.w3c.dom.Node) xPath.compile(
							nodeNameone).evaluate(xmlDocument,
							XPathConstants.NODE);
					log.info("%%%%Bingo Getting node text content"
							+ node.getTextContent());
					String textValue = node.getTextContent();
					MetaDataMap wfd = workItem.getWorkflow().getWorkflowData()
							.getMetaDataMap();
					wfd.put(valueone, textValue);
					log.info(" Metadata is set.... and  key , value is : "
							+ valueone + ":" + textValue);
					log.info("**************************************");
				}

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
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					xmlDataStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			// }

		}

	}
}
