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
		"chooser.label=Dock notice dynamic participant chooser" })

public class CSUFDockNoticeParticipantChooser implements ParticipantStepChooser {
	private static final Logger logger = LoggerFactory.getLogger(CSUFDockNoticeParticipantChooser.class);

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
					
				if (valStr1.equals("ToSupervisor")) {  
					for (Map.Entry<String, Object> entry5 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry5.getKey().matches("managerUserID")) {
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