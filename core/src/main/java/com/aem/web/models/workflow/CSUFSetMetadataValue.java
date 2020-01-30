package com.aem.web.models.workflow;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = { "service.description=Set Meatadata Value", "service.vendor=Adobe Systems",
		"process.label=CSUF Set value of metadata" })
public class CSUFSetMetadataValue implements WorkflowProcess {
	private static final Logger log = LoggerFactory.getLogger(CSUFSetMetadataValue.class);

	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
			throws com.adobe.granite.workflow.WorkflowException {
        
		String params = ((String) args.get("PROCESS_ARGS", "string")).toString();
		String[] parameters = params.split(",");
		String[] paramObtained = parameters;
		int paramLength = parameters.length;
        if(paramLength != 0) {
        	for (int i = 0; i < paramLength; ++i) {
			String items = paramObtained[i];
			String[] itemsArray = items.split("=");
			String valueObtained = itemsArray[1];
			String keyObtained = itemsArray[0];
			try {
			MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
			wfd.put(keyObtained, valueObtained);
			log.info(" Metadata is set.... and  key , value is : " + keyObtained + ":" + valueObtained);
			   		}catch(Exception e){
        			e.printStackTrace();
        		}
		}
        }
	}
	
}

