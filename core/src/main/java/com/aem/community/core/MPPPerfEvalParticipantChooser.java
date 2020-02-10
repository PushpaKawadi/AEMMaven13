package com.aem.community.core;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ParticipantStepChooser.class, property = {
		"chooser.label=MPP Performance Evaluation dynamic participant chooser" })

public class MPPPerfEvalParticipantChooser implements ParticipantStepChooser {
	private static final Logger logger = LoggerFactory.getLogger(MPPPerfEvalParticipantChooser.class);

	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		logger.info(
				"################ Inside the MPPPerfEvalParticipantChooser GetParticipant ##########################");
		String participant = "";
		Workflow wf = workItem.getWorkflow();
		logger.info("Stage value==" + wf.getWorkflowData().getMetaDataMap().get("stage"));
		
		String valStr1;
		String valStr2;
		for (Map.Entry<String, Object> entry1 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
			logger.info("Key = " + entry1.getKey() + ", Value = " + entry1.getValue());
			if (entry1.getKey().matches("stage")) {
				valStr1 = entry1.getValue().toString();
				if (valStr1.equals("ToManager") || valStr1.equals("ToManagerAcknowledge")
						|| valStr1.equals("ToManagerAcknowledge") || valStr1.equals("ToManagerAcknowledge")
						|| valStr1.equals("ToManagerAcknowledge") || valStr1.equals("ToManagerFinalAcknowledge")
						|| valStr1.equals("ToManagerHRDI") || valStr1.equals("ToManagerFinalAcknowledge")
						|| valStr1.equals("ToManagerAcknowledgeOnExpire")) {
					for (Map.Entry<String, Object> entry2 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry2.getKey().matches("managerUserId")) {
							valStr2 = entry2.getValue().toString();
							participant = valStr2;
						}
					}
				}
				if (valStr1.equals("ToEmployee") || valStr1.equals("ToEmployeeAck") || valStr1.equals("ToEmployeeAckOnExpire")) {  
					for (Map.Entry<String, Object> entry3 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry3.getKey().matches("empUserId")) {
							valStr2 = entry3.getValue().toString();
							participant = valStr2;
						}
					}
				}
				if (valStr1.equals("ToHRCoo")) {
					for (Map.Entry<String, Object> entry4 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry4.getKey().matches("reviewerUserId")) {
							valStr2 = entry4.getValue().toString();
							participant = valStr2;
						}
					}
				}if (valStr1.equals("ToAdmin")) {
					for (Map.Entry<String, Object> entry5 : workItem.getWorkflowData().getMetaDataMap().entrySet()) {
						if (entry5.getKey().matches("adminUID")) {
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