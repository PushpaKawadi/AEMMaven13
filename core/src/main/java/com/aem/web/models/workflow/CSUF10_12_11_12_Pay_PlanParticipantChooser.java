package com.aem.web.models.workflow;

import java.util.Map;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = ParticipantStepChooser.class, property = {
		"chooser.label=10/12 11/12 Pay Plan dynamic participant chooser" })

public class CSUF10_12_11_12_Pay_PlanParticipantChooser implements ParticipantStepChooser {
	private static final Logger logger = LoggerFactory.getLogger(CSUF10_12_11_12_Pay_PlanParticipantChooser.class);

	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		logger.info(
				"################ Inside the Employee Fee Waiver GetParticipant ##########################");
		String participant = "";
		Workflow wf = workItem.getWorkflow();
		logger.info("Stage value==" + wf.getWorkflowData().getMetaDataMap().get("stage"));
		
		String valStr1;
		String valStr2;
		for (Map.Entry<String, Object> entry1 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
			logger.info("Key = " + entry1.getKey() + ", Value = " + entry1.getValue());
			if (entry1.getKey().matches("stage")) {
				valStr1 = entry1.getValue().toString();
				
				if (valStr1.equals("ToManager")) {  
					for (Map.Entry<String, Object> entry3 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry3.getKey().matches("managerUserId")) {
							valStr2 = entry3.getValue().toString();
							participant = valStr2;
						}
					}
				}
				if (valStr1.equals("ToAdmin")) {
					for (Map.Entry<String, Object> entry4 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry4.getKey().matches("adminUserId")) {
							valStr2 = entry4.getValue().toString();
							participant = valStr2;
						}
					}
				}	
				if (valStr1.equals("ToVP")) {  
					for (Map.Entry<String, Object> entry5 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry5.getKey().matches("VPruserId")) {
							valStr2 = entry5.getValue().toString();
							participant = valStr2;
						}
					}
				}				
			}
		}

		logger.info("####### Participant : " + participant + " ##############");
		return participant;
	}
}