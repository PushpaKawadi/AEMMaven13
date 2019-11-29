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
   property = {"service.description=Delay processing of workflow", "service.vendor=Adobe Systems", "process.label=Delay Step"}
)
public class DelayStep implements WorkflowProcess {
   private static final Logger log = LoggerFactory.getLogger(GetValueinDataXml.class);

   public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arg2) throws WorkflowException {
	  
	  log.info("In DelayStep Class. Execute method");
	  
	  /*
	  try {
		Thread.sleep(4000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		log.error("In DelayStep Class. Execute method.Exception while delaying the process: " + e.getMessage());
		e.printStackTrace();
	}
	*/
			  	           
   }
}
    