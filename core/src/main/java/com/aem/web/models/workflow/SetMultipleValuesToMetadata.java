package com.aem.web.models.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

@Component(
   property = {"service.description=Get Value", "service.vendor=Adobe Systems", "process.label=Get Multiple Value of Element in Xml"}
)
public class SetMultipleValuesToMetadata implements WorkflowProcess {
   private static final Logger log = LoggerFactory.getLogger(SetMultipleValuesToMetadata.class);

   public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2) throws WorkflowException {
	  InputStream xmlDataStream = null;

	  log.info("Start of GetValueinDataXml");
      log.info("The string I got was ..." + ((String)arg2.get("PROCESS_ARGS", "string")).toString());
      String payloadPath = workItem.getWorkflowData().getPayload().toString();
      log.info("The payload  in HandleCMSubmission is " + workItem.getWorkflowData().getPayload().toString());
      String dataFilePath = payloadPath + "/Data.xml/jcr:content";
      Session session = (Session)workflowSession.adaptTo(Session.class);
      DocumentBuilderFactory factory = null;
      DocumentBuilder builder = null;
      Document xmlDocument = null;
      Node xmlDataNode = null;

      try {
         xmlDataNode = session.getNode(dataFilePath);
      } catch (PathNotFoundException var25) {
    	  log.error("PathNotFoundException in GetValueinDataXml=" + var25.getMessage());
          var25.printStackTrace();
      } catch (RepositoryException var26) {
    	  log.error("RepositoryException in GetValueinDataXml=" + var26.getMessage());
          var26.printStackTrace();
      }

      try {
         xmlDataStream = xmlDataNode.getProperty("jcr:data").getBinary().getStream();
         log.info("Got InputStream.... and the size available is ..." + xmlDataStream.available());
         XPath xPath = XPathFactory.newInstance().newXPath();
         factory = DocumentBuilderFactory.newInstance();
         builder = factory.newDocumentBuilder();
         xmlDocument = builder.parse(xmlDataStream);
         String params = ((String)arg2.get("PROCESS_ARGS", "string")).toString();
         String[] parameters = params.split("=");
 		 String nodeName = parameters[0];
 		 String[] value = parameters[1].split(",");
         int i = value.length;
         log.info(" value of i : " + i);
         String textValue = "";
         for(int j = 0; j < i; ++j) { 
        	 String valueOne = value[j];
        	 org.w3c.dom.Node node = (org.w3c.dom.Node)xPath.compile(valueOne).evaluate(xmlDocument, XPathConstants.NODE);
        	 textValue = textValue.concat(", ").concat(node.getTextContent());            
            //log.info("%%%%Bingo Getting node text content" + node.getTextContent());
            
         }
        
         
         MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
         wfd.put(nodeName,textValue);
         log.info(" Metadata is set.... and  key , value is : " + nodeName + ":" + textValue);
         log.info("**************************************");
         
         log.info("End of GetValueinDataXml");
      } catch (Exception var27) {
         log.error("Got error in GetValueinDataXml" + var27.getMessage());
         var27.printStackTrace();
      }finally {
        	if(xmlDataStream != null){
          		try {
      				xmlDataStream.close();
      			} catch (IOException e) {
      				 log.error("Unable to close inputstream in GetValueinDataXml" + e.getMessage());
    				 e.printStackTrace();
      				
      			}
          	}
        	 
        }

   }
}
    