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


import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
   property = {"service.description=Get Value", "service.vendor=Adobe Systems", "process.label=CSUF Get Value of Element in Xml"}
)
public class CSUFGetValueinDataXml implements WorkflowProcess {
   private static final Logger log = LoggerFactory.getLogger(CSUFGetValueinDataXml.class);

	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2)
			throws com.adobe.granite.workflow.WorkflowException {

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document xmlDocument = null;
		InputStream is = null;

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
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);

				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
					XPath xPath = XPathFactory.newInstance().newXPath();
					factory = DocumentBuilderFactory.newInstance();
					builder = factory.newDocumentBuilder();
					try {
						xmlDocument = builder.parse(is);
					} catch (SAXException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String params = ((String) arg2.get("PROCESS_ARGS", "string")).toString();
					String[] parameters = params.split(",");
					String[] var15 = parameters;
					int var16 = parameters.length;

					for (int var17 = 0; var17 < var16; ++var17) {
						String items = var15[var17];
						String[] itemsArray = items.split("=");
						String nodeNameone = itemsArray[1];
						String valueone = itemsArray[0];
						org.w3c.dom.Node node;
						try {
							node = (org.w3c.dom.Node) xPath.compile(nodeNameone).evaluate(xmlDocument,
									XPathConstants.NODE);
							String textValue = node.getTextContent();
							MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
							wfd.put(valueone, textValue);
							log.info(" Metadata is set.... and  key , value is : " + valueone + ":" + textValue);
							log.info("**************************************");
						} catch (XPathExpressionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// log.info("%%%%Bingo Getting node text content" +
						// node.getTextContent());

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
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}
}
    