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
}