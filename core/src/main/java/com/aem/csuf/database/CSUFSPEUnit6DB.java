package com.aem.csuf.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.day.commons.datasource.poolservice.DataSourcePool;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=SPE  unit 6 Save in DB", Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "=SPEUnit6Save" })
public class CSUFSPEUnit6DB implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(CSUFSPEUnit6DB.class);
	
	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;
	
	@Reference
	private GlobalConfigService globalConfigService;
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
			throws WorkflowException {
		Connection conn = null;

		ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		String payloadPath = workItem.getWorkflowData().getPayload().toString();
		Document doc = null;
		InputStream is = null;

		String ratingPeriodFrom = "";
		String ratingPeriodTo = "";
		String empId = "";
		String empRCD = "";
		String cbid = "";
		String classification = "";
		String range = "";
		String evaluationType = "";
		String firstName = "";
		String lastName = "";
		String departmentID = "";
		String departmentName = "";
		String staffposdesc = "";
		String evaluatorsName = "";
		String evaluatorsTitle = "";
		String qualityRB = "";
		String quality1 = "";
		String quality2 = "";
		String quality3 = "";
		String quality4 = "";
		String quality5 = "";
		String quantityRB = "";
		String quantity1 = "";
		String quantity2 = "";
		String quantity3 = "";
		String quantity4 = "";
		String quantity5 = "";
		String oralCommRB = "";
		String oralComm1 = "";
		String oralComm2 = "";
		String oralComm3 = "";
		String oralComm4 = "";
		String oralComm5 = "";
		String interpersonalSkillsRB = "";
		String interpersonalSkills1 = "";
		String interpersonalSkills2 = "";
		String interpersonalSkills3 = "";
		String interpersonalSkills4 = "";
		String interpersonalSkills5 = "";
		String initiativeRB = "";
		String initiative1 = "";
		String initiative2 = "";
		String initiative3 = "";
		String initiative4 = "";
		String initiative5 = "";
		String serviceOrientationRB = "";
		String serviceOrientation1 = "";
		String serviceOrientation2 = "";
		String serviceOrientation3 = "";
		String serviceOrientation4 = "";
		String serviceOrientation5 = "";
		String adaptabilityRB = "";
		String adaptability1 = "";
		String adaptability2 = "";
		String adaptability3 = "";
		String adaptability4 = "";
		String adaptability5 = "";
		String jobKnowledgeRB = "";
		String jobKnowledge1 = "";
		String jobKnowledge2 = "";
		String jobKnowledge3 = "";
		String jobKnowledge4 = "";
		String jobKnowledge5 = "";
		String dependReliRB = "";
		String dependReli1 = "";
		String dependReli2 = "";
		String dependReli3 = "";
		String dependReli4 = "";
		String dependReli5 = "";
		String writtenCommRB = "";
		String writtenComm1 = "";
		String writtenComm2 = "";
		String writtenComm3 = "";
		String writtenComm4 = "";
		String writtenComm5 = "";
		String probSolvingRB = "";
		String probSolving1 = "";
		String probSolving2 = "";
		String probSolving3 = "";
		String probSolving4 = "";
		String probSolving5 = "";
		String leadingOthersRB = "";
		String leadingOthers1 = "";
		String leadingOthers2 = "";
		String leadingOthers3 = "";
		String leadingOthers4 = "";
		String leadingOthers5 = "";
		String acceptingRB = "";
		String accepting1 = "";
		String accepting2 = "";
		String accepting3 = "";
		String accepting4 = "";
		String accepting5 = "";
		
		String addCriteriaCom1 = "";
	
		String addCriteriaRating1 = "";
		String addCriteriaRating2 = "";
		String addCriteriaRating3 = "";
		String addCriteriaRating4 = "";
		String addCriteriaRating5 = "";
		
		String criteriaComment1 = "";
		String criteriaComment2 = "";
		String criteriaComment3 = "";
		String criteriaComment4 = "";
		String criteriaComment5 = "";
		
		String overallRating = "";
		String supportFactorComments1 = "";
		String supportFactorComments2 = "";
		String performanceGoalComment1 = "";
		String performanceGoalComment2 = "";
		String goal1 = "";
		String goal2 = "";
		String goal3 = "";
		String goal4 = "";
		String goal5 = "";
		String evalCB = "";
		String evalPrintedName = "";
		String evalSign = "";
		String evalSignDate = "";
		String evalComments = "";
		String empCB = "";
		String empSign = "";
		String empSignDate = "";
		String empComment = "";
		String directorCB = "";
		String directorSign = "";
		String directorDate = "";
		String directorName = "";
		String directorComment = "";
		String adminCB = "";
		String adminPrintedName = "";
		String adminSign = "";
		String adminSignDate = "";
		String adminComments = "";
		String hrComments = "";
		String hrOverallRate = "";
		String hrCB = "";
		String initials = "";
		String hrDate = "";
		String addCriteriaSelection1 = "";
		
		String hrCoordinatorSign = "";
		String hrCoordinatorSignDate = "";
		String hrCoordinatorSignComment = "";
		String workflowInstance = "";
		String hrCooCB = "";
		String division = "";
		String division_name = "";
		LinkedHashMap<String, Object> dataMap = null;
		Resource xmlNode = resolver.getResource(payloadPath);
		Iterator<Resource> xmlFiles = xmlNode.listChildren();
		// Get the payload path and iterate the path to find Data.xml, Use
		// Document
		// factory to parse the xml and fetch the required values for the
		// filenet
		// attachment
		while (xmlFiles.hasNext()) {
			Resource attachmentXml = xmlFiles.next();
			workflowInstance = workItem.getWorkflow().getId();
			String filePath = attachmentXml.getPath();

			log.info("filePath= " + filePath);
			if (filePath.contains("Data.xml")) {
				filePath = attachmentXml.getPath().concat("/jcr:content");
				log.info("xmlFiles=" + filePath);
				Node subNode = resolver.getResource(filePath).adaptTo(Node.class);
				try {
					is = subNode.getProperty("jcr:data").getBinary().getStream();
				} catch (ValueFormatException e2) {
					log.error("Exception1=" + e2.getMessage());
					e2.printStackTrace();
				} catch (PathNotFoundException e2) {
					log.error("Exception2=" + e2.getMessage());
					e2.printStackTrace();
				} catch (RepositoryException e2) {
					log.error("Exception3=" + e2.getMessage());
					e2.printStackTrace();
				}

				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = null;
					try {
						dBuilder = dbFactory.newDocumentBuilder();
					} catch (ParserConfigurationException e1) {
						log.info("ParserConfigurationException=" + e1);
						e1.printStackTrace();
					}
					try {
						doc = dBuilder.parse(is);
					} catch (IOException e1) {
						log.info("IOException=" + e1);
						e1.printStackTrace();
					}
					org.w3c.dom.NodeList nList = doc.getElementsByTagName("afBoundData");
					for (int temp = 0; temp < nList.getLength(); temp++) {
						org.w3c.dom.Node nNode = nList.item(temp);

						if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
							initials = eElement.getElementsByTagName("HRDIInitials").item(0).getTextContent();
							hrDate = eElement.getElementsByTagName("HRDIDate").item(0).getTextContent();
							ratingPeriodFrom = eElement.getElementsByTagName("ReviewPeriodFrom").item(0)
									.getTextContent();
							ratingPeriodTo = eElement.getElementsByTagName("ReviewPeriodTo").item(0).getTextContent();
							empId = eElement.getElementsByTagName("EmpID").item(0).getTextContent();
							empRCD = eElement.getElementsByTagName("EmpRCD").item(0).getTextContent();
							cbid = eElement.getElementsByTagName("CBID").item(0).getTextContent();
							classification = eElement.getElementsByTagName("Classification").item(0).getTextContent();
							range = eElement.getElementsByTagName("Range").item(0).getTextContent();
							evaluationType = eElement.getElementsByTagName("EvaluationType").item(0).getTextContent();
							firstName = eElement.getElementsByTagName("StaffFirstName").item(0).getTextContent();
							lastName = eElement.getElementsByTagName("StaffLastName").item(0).getTextContent();
							departmentID = eElement.getElementsByTagName("Department_ID").item(0).getTextContent();
							departmentName = eElement.getElementsByTagName("Department").item(0).getTextContent();
							staffposdesc = eElement.getElementsByTagName("Staffposdesc").item(0).getTextContent();
							evaluatorsName = eElement.getElementsByTagName("EvaluatorName").item(0).getTextContent();
							evaluatorsTitle = eElement.getElementsByTagName("EvaluatorsTitle").item(0).getTextContent();
							qualityRB = eElement.getElementsByTagName("Quality").item(0).getTextContent();
							quality1 = eElement.getElementsByTagName("quality1").item(0).getTextContent();
							quality2 = eElement.getElementsByTagName("quality2").item(0).getTextContent();
							quality3 = eElement.getElementsByTagName("quality3").item(0).getTextContent();
							quality4 = eElement.getElementsByTagName("quality4").item(0).getTextContent();
							quality5 = eElement.getElementsByTagName("quality5").item(0).getTextContent();
							quantityRB = eElement.getElementsByTagName("Quantity").item(0).getTextContent();
							quantity1 = eElement.getElementsByTagName("Quantity1").item(0).getTextContent();
							quantity2 = eElement.getElementsByTagName("Quantity2").item(0).getTextContent();
							quantity3 = eElement.getElementsByTagName("Quantity3").item(0).getTextContent();
							quantity4 = eElement.getElementsByTagName("Quantity4").item(0).getTextContent();
							quantity5 = eElement.getElementsByTagName("Quantity5").item(0).getTextContent();
							oralCommRB = eElement.getElementsByTagName("OralComm").item(0).getTextContent();
							oralComm1 = eElement.getElementsByTagName("OC1").item(0).getTextContent();
							oralComm2 = eElement.getElementsByTagName("OC2").item(0).getTextContent();
							oralComm3 = eElement.getElementsByTagName("OC3").item(0).getTextContent();
							oralComm4 = eElement.getElementsByTagName("OC4").item(0).getTextContent();
							oralComm5 = eElement.getElementsByTagName("OC5").item(0).getTextContent();
							interpersonalSkillsRB = eElement.getElementsByTagName("InterpersonalSkills").item(0)
									.getTextContent();
							interpersonalSkills1 = eElement.getElementsByTagName("IPSkill1").item(0).getTextContent();
							interpersonalSkills2 = eElement.getElementsByTagName("IPSkill2").item(0).getTextContent();
							interpersonalSkills3 = eElement.getElementsByTagName("IPSkill3").item(0).getTextContent();
							interpersonalSkills4 = eElement.getElementsByTagName("IPSkill4").item(0).getTextContent();
							interpersonalSkills5 = eElement.getElementsByTagName("IPSkill5").item(0).getTextContent();
							initiativeRB = eElement.getElementsByTagName("Initiative").item(0).getTextContent();
							initiative1 = eElement.getElementsByTagName("Initiative1").item(0).getTextContent();
							initiative2 = eElement.getElementsByTagName("Initiative2").item(0).getTextContent();
							initiative3 = eElement.getElementsByTagName("Initiative3").item(0).getTextContent();
							initiative4 = eElement.getElementsByTagName("Initiative4").item(0).getTextContent();
							initiative5 = eElement.getElementsByTagName("Initiative5").item(0).getTextContent();
							serviceOrientationRB = eElement.getElementsByTagName("ServiceOrientation").item(0)
									.getTextContent();
							serviceOrientation1 = eElement.getElementsByTagName("SC1").item(0).getTextContent();
							serviceOrientation2 = eElement.getElementsByTagName("SC2").item(0).getTextContent();
							serviceOrientation3 = eElement.getElementsByTagName("SC3").item(0).getTextContent();
							serviceOrientation4 = eElement.getElementsByTagName("SC4").item(0).getTextContent();
							serviceOrientation5 = eElement.getElementsByTagName("SC5").item(0).getTextContent();
							adaptabilityRB = eElement.getElementsByTagName("Adaptability").item(0).getTextContent();
							adaptability1 = eElement.getElementsByTagName("Adaptability1").item(0).getTextContent();
							adaptability2 = eElement.getElementsByTagName("Adaptability2").item(0).getTextContent();
							adaptability3 = eElement.getElementsByTagName("Adaptability3").item(0).getTextContent();
							adaptability4 = eElement.getElementsByTagName("Adaptability4").item(0).getTextContent();
							adaptability5 = eElement.getElementsByTagName("Adaptability5").item(0).getTextContent();
							jobKnowledgeRB = eElement.getElementsByTagName("JobKnowledge").item(0).getTextContent();
							jobKnowledge1 = eElement.getElementsByTagName("JK1").item(0).getTextContent();
							jobKnowledge2 = eElement.getElementsByTagName("JK2").item(0).getTextContent();
							jobKnowledge3 = eElement.getElementsByTagName("JK3").item(0).getTextContent();
							jobKnowledge4 = eElement.getElementsByTagName("JK4").item(0).getTextContent();
							jobKnowledge5 = eElement.getElementsByTagName("JK5").item(0).getTextContent();
							dependReliRB = eElement.getElementsByTagName("DependReli").item(0).getTextContent();
							dependReli1 = eElement.getElementsByTagName("DR1").item(0).getTextContent();
							dependReli2 = eElement.getElementsByTagName("DR2").item(0).getTextContent();
							dependReli3 = eElement.getElementsByTagName("DR3").item(0).getTextContent();
							dependReli4 = eElement.getElementsByTagName("DR4").item(0).getTextContent();
							dependReli5 = eElement.getElementsByTagName("DR5").item(0).getTextContent();
							writtenCommRB = eElement.getElementsByTagName("WrittenComm").item(0).getTextContent();
							writtenComm1 = eElement.getElementsByTagName("WC1").item(0).getTextContent();
							writtenComm2 = eElement.getElementsByTagName("WC2").item(0).getTextContent();
							writtenComm3 = eElement.getElementsByTagName("WC3").item(0).getTextContent();
							writtenComm4 = eElement.getElementsByTagName("WC4").item(0).getTextContent();
							writtenComm5 = eElement.getElementsByTagName("WC5").item(0).getTextContent();
							probSolvingRB = eElement.getElementsByTagName("ProbSolving").item(0).getTextContent();
							probSolving1 = eElement.getElementsByTagName("ProbSol1").item(0).getTextContent();
							probSolving2 = eElement.getElementsByTagName("ProbSol2").item(0).getTextContent();
							probSolving3 = eElement.getElementsByTagName("ProbSol3").item(0).getTextContent();
							probSolving4 = eElement.getElementsByTagName("ProbSol4").item(0).getTextContent();
							probSolving5 = eElement.getElementsByTagName("ProbSol5").item(0).getTextContent();
							leadingOthersRB = eElement.getElementsByTagName("LeadingOthers").item(0).getTextContent();
							leadingOthers1 = eElement.getElementsByTagName("LeadOthers1").item(0).getTextContent();
							leadingOthers2 = eElement.getElementsByTagName("LeadOthers2").item(0).getTextContent();
							leadingOthers3 = eElement.getElementsByTagName("LeadOthers3").item(0).getTextContent();
							leadingOthers4 = eElement.getElementsByTagName("LeadOthers4").item(0).getTextContent();
							leadingOthers5 = eElement.getElementsByTagName("LeadOthers5").item(0).getTextContent();
							acceptingRB = eElement.getElementsByTagName("Accepting").item(0).getTextContent();
							accepting1 = eElement.getElementsByTagName("Accepting1").item(0).getTextContent();
							accepting2 = eElement.getElementsByTagName("Accepting2").item(0).getTextContent();
							accepting3 = eElement.getElementsByTagName("Accepting3").item(0).getTextContent();
							accepting4 = eElement.getElementsByTagName("Accepting4").item(0).getTextContent();
							accepting5 = eElement.getElementsByTagName("Accepting5").item(0).getTextContent();
							
							addCriteriaSelection1 = eElement.getElementsByTagName("AddCriteriaImpToPos1").item(0)
									.getTextContent();
							
							addCriteriaCom1 = eElement.getElementsByTagName("AddCriteria1").item(0).getTextContent();
							
							addCriteriaRating1 = eElement.getElementsByTagName("Additional1").item(0).getTextContent();
							addCriteriaRating2 = eElement.getElementsByTagName("Additional2").item(0).getTextContent();
							addCriteriaRating3 = eElement.getElementsByTagName("Additional3").item(0).getTextContent();
							addCriteriaRating4 = eElement.getElementsByTagName("Additional4").item(0).getTextContent();
							addCriteriaRating5 = eElement.getElementsByTagName("Additional5").item(0).getTextContent();
							
							criteriaComment1 = eElement.getElementsByTagName("AdditionalCriteria1").item(0)
									.getTextContent();
							criteriaComment2 = eElement.getElementsByTagName("AdditionalCriteria2").item(0)
									.getTextContent();
							criteriaComment3 = eElement.getElementsByTagName("AdditionalCriteria3").item(0)
									.getTextContent();
							criteriaComment4 = eElement.getElementsByTagName("AdditionalCriteria4").item(0)
									.getTextContent();
							criteriaComment5 = eElement.getElementsByTagName("AdditionalCriteria5").item(0)
									.getTextContent();
							
							overallRating = eElement.getElementsByTagName("OverallRating").item(0).getTextContent();
							supportFactorComments1 = eElement.getElementsByTagName("supportFactorComments1").item(0)
									.getTextContent();
							supportFactorComments2 = eElement.getElementsByTagName("supportFactorComments2").item(0)
									.getTextContent();
							performanceGoalComment1 = eElement.getElementsByTagName("performanceGoalComment1").item(0)
									.getTextContent();
							performanceGoalComment2 = eElement.getElementsByTagName("performanceGoalComment2").item(0)
									.getTextContent();
							goal1 = eElement.getElementsByTagName("goalOne").item(0).getTextContent();
							goal2 = eElement.getElementsByTagName("goalTwo").item(0).getTextContent();
							goal3 = eElement.getElementsByTagName("goalThree").item(0).getTextContent();
							goal4 = eElement.getElementsByTagName("goalFour").item(0).getTextContent();
							goal5 = eElement.getElementsByTagName("goalFive").item(0).getTextContent();
							
							evalCB = eElement.getElementsByTagName("EvalCB").item(0).getTextContent();
							evalPrintedName = eElement.getElementsByTagName("EvaluatorNameSign").item(0)
									.getTextContent();
							evalSign = eElement.getElementsByTagName("EvaluatorSign").item(0).getTextContent();
							evalSignDate = eElement.getElementsByTagName("EvaluatorDate").item(0).getTextContent();
							evalComments = eElement.getElementsByTagName("EvaluatorComment").item(0).getTextContent();
							empCB = eElement.getElementsByTagName("EmpCB").item(0).getTextContent();
							empSign = eElement.getElementsByTagName("EmpSign").item(0).getTextContent();
							empSignDate = eElement.getElementsByTagName("EmpDate").item(0).getTextContent();
							empComment = eElement.getElementsByTagName("EmpComment").item(0).getTextContent();
							adminCB = eElement.getElementsByTagName("AdminCB").item(0).getTextContent();
							adminPrintedName = eElement.getElementsByTagName("AdminName").item(0)
									.getTextContent();
							adminSign = eElement.getElementsByTagName("AdminSign").item(0)
									.getTextContent();
							adminSignDate = eElement.getElementsByTagName("AdminDate").item(0).getTextContent();
							adminComments = eElement.getElementsByTagName("AdminComment").item(0).getTextContent();
							hrCoordinatorSign = eElement.getElementsByTagName("HRCoordinatorSign").item(0)
									.getTextContent();
							hrCoordinatorSignDate = eElement.getElementsByTagName("HRCoordinatorSignDate").item(0)
									.getTextContent();
							hrCoordinatorSignComment = eElement.getElementsByTagName("HRCoordinatorSignComment").item(0)
									.getTextContent();
							
							hrCB = eElement.getElementsByTagName("HRDICB").item(0)
									.getTextContent();
							hrCooCB = eElement.getElementsByTagName("HRCooCB").item(0).getTextContent();
							hrComments = eElement.getElementsByTagName("HRDIComment").item(0).getTextContent();
							hrOverallRate = eElement.getElementsByTagName("HRDIOverallRate").item(0).getTextContent();
							division = eElement.getElementsByTagName("division").item(0).getTextContent();
							division_name = eElement.getElementsByTagName("divisionName").item(0).getTextContent();

						}
					}
					

					dataMap = new LinkedHashMap<String, Object>();
					
					dataMap.put("STAFFPOSDESC", staffposdesc);
					dataMap.put("EMPLID", empId);
					dataMap.put("EMPRCD", empRCD);
					dataMap.put("CBID", cbid);
					dataMap.put("EVALUATION_TYPE", evaluationType);
					dataMap.put("FIRST_NAME", firstName);
					dataMap.put("LAST_NAME", lastName);
					dataMap.put("CLASSIFICATION", classification);
					dataMap.put("EMPRANGE", range);
					dataMap.put("DEPARTMENT", departmentName);
					dataMap.put("DEPARTMENT_ID", Integer.parseInt(departmentID));
					dataMap.put("EVALUATORS_NAME", evaluatorsName);
					dataMap.put("EVALUATORS_TITLE", evaluatorsTitle);
					Object reviewPeriodFromObj = null;
					if (ratingPeriodFrom != null && ratingPeriodFrom != "") {
						Date reviewPeriodFromNew = Date.valueOf(ratingPeriodFrom);
						reviewPeriodFromObj = reviewPeriodFromNew;
					}
					dataMap.put("REVIEWPERIOD_FROM", reviewPeriodFromObj);
					Object reviewPeriodToObj = null;
					if (ratingPeriodTo != null && ratingPeriodTo != "") {
						Date reviewPeriodToNew = Date.valueOf(ratingPeriodTo);
						reviewPeriodToObj = reviewPeriodToNew;
					}
					dataMap.put("REVIEWPERIOD_TO", reviewPeriodToObj);

					dataMap.put("QUALITY", qualityRB);
					dataMap.put("QUALITY_RATING_1", quality1);
					dataMap.put("QUALITY_RATING_2", quality2);
					dataMap.put("QUALITY_RATING_3", quality3);
					dataMap.put("QUALITY_RATING_4", quality4);
					dataMap.put("QUALITY_RATING_5", quality5);

					dataMap.put("QUANTITY", quantityRB);
					dataMap.put("QUANTITY_RATING_1", quantity1);
					dataMap.put("QUANTITY_RATING_2", quantity2);
					dataMap.put("QUANTITY_RATING_3", quantity3);
					dataMap.put("QUANTITY_RATING_4", quantity4);
					dataMap.put("QUANTITY_RATING_5", quantity5);

					dataMap.put("ORALCOMM", oralCommRB);
					dataMap.put("OC_RATING_1", oralComm1);
					dataMap.put("OC_RATING_2", oralComm2);
					dataMap.put("OC_RATING_3", oralComm3);
					dataMap.put("OC_RATING_4", oralComm4);
					dataMap.put("OC_RATING_5", oralComm5);

					dataMap.put("INTERPERSONALSKILLS", interpersonalSkillsRB);
					dataMap.put("IPSKILL_RATING_1", interpersonalSkills1);
					dataMap.put("IPSKILL_RATING_2", interpersonalSkills2);
					dataMap.put("IPSKILL_RATING_3", interpersonalSkills3);
					dataMap.put("IPSKILL_RATING_4", interpersonalSkills4);
					dataMap.put("IPSKILL_RATING_5", interpersonalSkills5);

					dataMap.put("INITIATIVE", initiativeRB);
					dataMap.put("INITIATIVE_RATING_1", initiative1);
					dataMap.put("INITIATIVE_RATING_2", initiative2);
					dataMap.put("INITIATIVE_RATING_3", initiative3);
					dataMap.put("INITIATIVE_RATING_4", initiative4);
					dataMap.put("INITIATIVE_RATING_5", initiative5);

					dataMap.put("SERVICEORIENTATION", serviceOrientationRB);
					dataMap.put("SO1_RATING_1", serviceOrientation1);
					dataMap.put("SO2_RATING_2", serviceOrientation2);
					dataMap.put("SO3_RATING_3", serviceOrientation3);
					dataMap.put("SO4_RATING_4", serviceOrientation4);
					dataMap.put("SO5_RATING_5", serviceOrientation5);

					dataMap.put("ADAPTABILITY", adaptabilityRB);
					dataMap.put("ADAPTABILITY_RATING_1", adaptability1);
					dataMap.put("ADAPTABILITY_RATING_2", adaptability2);
					dataMap.put("ADAPTABILITY_RATING_3", adaptability3);
					dataMap.put("ADAPTABILITY_RATING_4", adaptability4);
					dataMap.put("ADAPTABILITY_RATING_5", adaptability5);

					dataMap.put("JOBKNOWLEDGE", jobKnowledgeRB);
					dataMap.put("JK1_RATING_1", jobKnowledge1);
					dataMap.put("JK2_RATING_2", jobKnowledge2);
					dataMap.put("JK3_RATING_3", jobKnowledge3);
					dataMap.put("JK4_RATING_4", jobKnowledge4);
					dataMap.put("JK5_RATING_5", jobKnowledge5);

					dataMap.put("DEPENDRELI", dependReliRB);
					dataMap.put("DR1_RATING_1", dependReli1);
					dataMap.put("DR2_RATING_2", dependReli2);
					dataMap.put("DR3_RATING_3", dependReli3);
					dataMap.put("DR4_RATING_4", dependReli4);
					dataMap.put("DR5_RATING_5", dependReli5);

					dataMap.put("WRITTENCOMM", writtenCommRB);
					dataMap.put("WC1_RATING_1", writtenComm1);
					dataMap.put("WC2_RATING_2", writtenComm2);
					dataMap.put("WC3_RATING_3", writtenComm3);
					dataMap.put("WC4_RATING_4", writtenComm4);
					dataMap.put("WC5_RATING_5", writtenComm5);

					dataMap.put("PROBSOLVING", probSolvingRB);
					dataMap.put("PROBSOL_RATING_1", probSolving1);
					dataMap.put("PROBSOL_RATING_2", probSolving2);
					dataMap.put("PROBSOL_RATING_3", probSolving3);
					dataMap.put("PROBSOL_RATING_4", probSolving4);
					dataMap.put("PROBSOL_RATING_5", probSolving5);

					dataMap.put("LEADINGOTHERS", leadingOthersRB);
					dataMap.put("LEADOTHERS_RATING_1", leadingOthers1);
					dataMap.put("LEADOTHERS_RATING_2", leadingOthers2);
					dataMap.put("LEADOTHERS_RATING_3", leadingOthers3);
					dataMap.put("LEADOTHERS_RATING_4", leadingOthers4);
					dataMap.put("LEADOTHERS_RATING_5", leadingOthers5);

					dataMap.put("ACCEPTING", acceptingRB);
					dataMap.put("ACCEPTING_RATING_1", accepting1);
					dataMap.put("ACCEPTING_RATING_2", accepting2);
					dataMap.put("ACCEPTING_RATING_3", accepting3);
					dataMap.put("ACCEPTING_RATING_4", accepting4);
					dataMap.put("ACCEPTING_RATING_5", accepting5);

					

					dataMap.put("ADDCRITERIA_1", addCriteriaCom1);
					dataMap.put("ADDITIONALCRITERIA1", addCriteriaSelection1);
					dataMap.put("ADDCRITERIA_RATING1", addCriteriaRating1);
					dataMap.put("ADDCRITERIA_RATING2", addCriteriaRating2);
					dataMap.put("ADDCRITERIA_RATING3", addCriteriaRating3);
					dataMap.put("ADDCRITERIA_RATING4", addCriteriaRating4);
					dataMap.put("ADDCRITERIA_RATING5", addCriteriaRating5);
					dataMap.put("ADDCRITERIA_COMMENT_1", criteriaComment1);
					dataMap.put("ADDCRITERIA_COMMENT_2", criteriaComment2);
					dataMap.put("ADDCRITERIA_COMMENT_3", criteriaComment3);
					dataMap.put("ADDCRITERIA_COMMENT_4", criteriaComment4);
					dataMap.put("ADDCRITERIA_COMMENT_5", criteriaComment5);
					
					dataMap.put("OVERALLRATING", overallRating);

					dataMap.put("SUPPORTFACTOR_COMMENTS1", supportFactorComments1);
					dataMap.put("SUPPORTFACTOR_COMMENTS2", supportFactorComments2);
					dataMap.put("PERFORMANCE_GOAL_COMMENT1", performanceGoalComment1);
					dataMap.put("PERFORMANCE_GOAL_COMMENT2", performanceGoalComment2);
					dataMap.put("PERFORMANCE_GOAL_ONE", goal1);
					dataMap.put("PERFORMANCE_GOAL_TWO", goal2);
					dataMap.put("PERFORMANCE_GOAL_THREE", goal3);
					dataMap.put("PERFORMANCE_GOAL_FOUR", goal4);
					dataMap.put("PERFORMANCE_GOAL_FIVE", goal5);

					dataMap.put("EVAL_DECL_CB", evalCB);
					dataMap.put("EVALUATORS_PRINTED_NAME", evalPrintedName);
					dataMap.put("EVALUATORS_SIGNATURE", evalSign);
					dataMap.put("EVAL_COMMENTS", evalComments);

					Object evalDateObj = null;
					if (evalSignDate != null && evalSignDate != "") {
						Date evalDateNew = Date.valueOf(evalSignDate);
						evalDateObj = evalDateNew;
					}

					dataMap.put("EVAL_SIGNED_DATE", evalDateObj);
					dataMap.put("EMP_DECL_CB", empCB);
					Object empSignObj = null;
					if (empSignDate != null && empSignDate != "") {
						Date empDateNew = Date.valueOf(empSignDate);
						empSignObj = empDateNew;
					}
					dataMap.put("EMP_SIGNED_DATE", empSignObj);
					dataMap.put("EMP_COMMENT", empComment);
					dataMap.put("EMPSIGN", empSign);
					dataMap.put("ADMIN_DECL_CB", adminCB);

					Object adminSignObj = null;
					if (adminSignDate != null && adminSignDate != "") {
						Date adminDateNew = Date.valueOf(adminSignDate);
						adminSignObj = adminDateNew;
					}
					dataMap.put("ADMIN_SIGNED_DATE", adminSignObj);
					dataMap.put("ADMIN_COMMENT", adminComments);
					dataMap.put("ADMIN_SIGNATURE", adminSign);
					dataMap.put("ADMIN_PRINTED_NAME", adminPrintedName);
					
										
					dataMap.put("HRCOOCB", hrCooCB);
					dataMap.put("HRCOO_SIGN", hrCoordinatorSign);
					Object hrCooDateObj = null;
					if (hrCoordinatorSignDate != null && hrCoordinatorSignDate != "") {
						Date hrCooDateNew = Date.valueOf(hrCoordinatorSignDate);
						hrCooDateObj = hrCooDateNew;
					}
					dataMap.put("HRCOO_SIGNED_DATE", hrCooDateObj);
					dataMap.put("HRCOO_COMMENT", hrCoordinatorSignComment);
					dataMap.put("HR_DECL_CB", hrCB);

					Object hrDateObj = null;
					if (hrDate != null && hrDate != "") {
						Date hrDateNew = Date.valueOf(hrDate);
						hrDateObj = hrDateNew;
					}

					dataMap.put("HR_SIGNED_DATE", hrDateObj);
					dataMap.put("HR_COMMENT", hrComments);
					dataMap.put("HR_INITIALS", initials);
					dataMap.put("HR_OVERALL_RATING", hrOverallRate);
					dataMap.put("WORKFLOW_INSTANCE_ID", workflowInstance);
					dataMap.put("DIVISION", division);
					dataMap.put("DIVISION_NAME", division_name);
					log.error("put complete");
					log.error("Datamap Size=" + dataMap.size());

				} catch (SAXException e) {
					log.error("SAXException=" + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						log.error("IOException=" + e.getMessage());
						e.printStackTrace();
					}

				}

			}
		}
		String dataSourceVal = globalConfigService.getAEMDataSource();
		log.info("DataSourceVal==========" + dataSourceVal);
		conn = jdbcConnectionService.getDBConnection(dataSourceVal);
		if (conn != null) {
			log.error("Connection Successfull");
			insertSPEData(conn, dataMap);
		}
	}

	@Reference
	private DataSourcePool source;

	private Connection getConnection() {
		log.info("Inside Get Connection");

		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool right here!
			dataSource = (DataSource) source.getDataSource("AEMDBDEV");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			log.error("Conn Exception=" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					log.info("Conn Exec=");
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		return null;
	}

	public void insertSPEData(Connection conn, LinkedHashMap<String, Object> dataMap) {
		PreparedStatement preparedStmt = null;
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			String tableName = "AEM_STAFF_PERF_EVAL_UNIT6";
			StringBuilder sql = new StringBuilder("INSERT INTO  ").append(tableName).append(" (");
			StringBuilder placeholders = new StringBuilder();
			for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
				sql.append(iter.next());
				placeholders.append("?");
				if (iter.hasNext()) {
					sql.append(",");
					placeholders.append(",");
				}
			}
			sql.append(") VALUES (").append(placeholders).append(")");
			log.error("SQL=" + sql.toString());
			try {
				preparedStmt = conn.prepareStatement(sql.toString());
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			}
			int i = 0;
			log.info("Datamap values=" + dataMap.values());
			for (Object value : dataMap.values()) {
				try {
					if (value instanceof Date) {
						preparedStmt.setDate(++i, (Date) value);
					} else if (value instanceof Integer) {
						preparedStmt.setInt(++i, (Integer) value);
					} else {
						if (value != "" && value != null) {
							preparedStmt.setString(++i, value.toString());
						} else {
							preparedStmt.setString(++i, null);
						}
					}
				} catch (SQLException e) {
					log.error("SQLException=" + e.getMessage());
					e.printStackTrace();
				}
			}
			try {
				preparedStmt.execute();
				conn.commit();
			} catch (SQLException e1) {
				log.error("SQLException=" + e1.getMessage());
				e1.printStackTrace();
			} finally {
				if (preparedStmt != null) {
					try {
						preparedStmt.close();
						conn.close();
					} catch (SQLException e) {
						log.error("SQLException=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
}
