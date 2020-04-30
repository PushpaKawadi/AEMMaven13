package com.aem.community.core.config;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Global Configuration", description = "CSUF Global Configuration Service")
public @interface GlobalConfig {
	
	@AttributeDefinition(name = "Filenet URL", description = "Filenet URL", type = AttributeType.STRING, defaultValue = "http://erpicn521tst.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addCourseWithdrawalDocuments")
	String filenet_URL();	

	@AttributeDefinition(name = "dbFrmMgrProd", description = "Employee Database Name", type = AttributeType.STRING, defaultValue = "frmmgrprod")
	String db_Frm_Mgr_Prod();
	
	@AttributeDefinition(name = "dbDocMgrProd", description = "Student Database Name", type = AttributeType.STRING, defaultValue = "docmgrprod")
	String db_Doc_Mgr_Prod();
	
	@AttributeDefinition(name = "dbAemDev", description = "AEM Dev DB", type = AttributeType.STRING, defaultValue = "AEMDBDEV")
	String db_Aem_Dev();
	
	@AttributeDefinition(name = "MPP Filenet URL", description = "MPP Filenet URL", type = AttributeType.STRING, defaultValue = "http://erpicn521tst.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addCourseWithdrawalDocuments")
	String mpp_filenet_URL();
	
	@AttributeDefinition(name = "Staff Eval Filenet URL", description = "Staff Eval Filenet URL", type = AttributeType.STRING, defaultValue = "http://erpicn521tst.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addStaffEvalDocuments")
	String staff_eval_filenet_URL();
	
	@AttributeDefinition(name = "HR Benefits Filenet URL", description = "HR Benefits Filenet URL", type = AttributeType.STRING, defaultValue = "http://erpicn521tst.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addHRIntExtFeeWaiverBenefitsDocuments")
	String hr_Benefits_Filenet_URL();
	
//	@AttributeDefinition(name = "Grade Change Filenet URL", description = "Grade ChangeFilenet URL", type = AttributeType.STRING, defaultValue = "http://erpicn521tst.fullerton.edu:9080/CSUFAEMServices/rest/AEMService/addGradeChangeDocuments")
//	String grade_Change_Filenet_URL();
	
	@AttributeDefinition(name = "dbAemProd", description = "AEM Dev Prod", type = AttributeType.STRING, defaultValue = "AEMDBPRD")
	String db_Aem_Prod_DB();
}