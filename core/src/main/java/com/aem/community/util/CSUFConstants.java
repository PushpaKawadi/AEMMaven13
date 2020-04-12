package com.aem.community.util;

public class CSUFConstants {
	//Start of Dock Notice
	public static final String dockNoticeUserIdSql="Select A.EMPLID, A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = B.EMPLID and A.EMPLID = C.cwid and C.userid = ('<<getUser_ID>>')";
	public static final String dockNoticeFields="EMPLID,FIRST_NAME,LAST_NAME,MIDDLE_NAME,EMPL_RCD,DEPTID,DEPTNAME,SCO_POSITION_NUM,NATIONAL_ID";
	
	public static final String dockNoticeEmpIdSql = "Select A.EMPLID,A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = B.EMPLID and A.EMPLID = '<<Empl_ID>>' and deptid in (select deptid from cmsrda.cms_hr_dept_sec where userid = '<<getUser_ID>>')";
	//End of Dock Notice
}
