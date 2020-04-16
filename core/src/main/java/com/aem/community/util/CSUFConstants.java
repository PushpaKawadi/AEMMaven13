package com.aem.community.util;

public class CSUFConstants {
	//Start of Dock Notice
	public static final String dockNoticeUserIdSql="Select A.EMPLID, A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = B.EMPLID and A.EMPLID = C.cwid and C.userid = ('<<getUser_ID>>')";
	public static final String dockNoticeFields="EMPLID,FIRST_NAME,LAST_NAME,MIDDLE_NAME,EMPL_RCD,DEPTID,DEPTNAME,SCO_POSITION_NUM,NATIONAL_ID";
	
	public static final String dockNoticeEmpIdSql = "Select A.EMPLID,A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = B.EMPLID and A.EMPLID = '<<Empl_ID>>' and deptid in (select deptid from cmsrda.cms_hr_dept_sec where userid = '<<getUser_ID>>')";
	//End of Dock Notice
	//Start of SCPR Staff/MPP
	public static final String SCPREmpIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = Replace('<<Empl_ID>>', '-','') and A.EMPLID = B.EMPLID";
	public static final String SCPREmpIdLookupFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,Extenstion,FUL_COLLEGE_NAME";
	public static final String SCPRUserIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, A.EMPLID, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME  from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = C.cwid and C.userid = '<<getUser_ID>>' and A.EMPLID = B.EMPLID"; 
	public static final String SCPRUserIdLookupfields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,EMPLID,Extenstion,FUL_COLLEGE_NAME";
	//End of SCPR Staff/MPP
	
	//Start of MPP Emp Lookup
	public static final String mppEmpIDSQL = "Select A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTID, B.DEPTNAME, B.EMPL_RCD, B.DESCR, B.UNION_CD, B.GRADE, D.SUPERVISOR_NAME as Supervisorname, B.FUL_DIVISION as DIVSION,B.FUL_DIVISION_NAME as DIVISION_NAME,D.WORKING_TITLE AS SupervisorTitle, E.USERID AS EMPUSERID FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN ful_emp_cwid_nt_name E ON E.CWID = A.EMPLID WHERE B.EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL";
	public static final String mppLookUpFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,EMPL_RCD,DESCR,UNION_CD,GRADE,SupervisorName,DIVSION,DIVISION_NAME,SupervisorTitle,EMPUSERID";
	//End of MPP Emp Lookup
	
	//Start of Get Manager/Admin details lookup
		public static final String managerAdminDetailsSQL = "SELECT DISTINCT (SELECT USERID FROM cmsrda.ful_emp_cwid_nt_name WHERE CWID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE emplid='<<EMP_ID>>' AND deptid  = '<<DEPT_ID>>' ) ) ) AS MANAGERUSERID, (SELECT USERID FROM cmsrda.ful_emp_cwid_nt_name WHERE CWID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE EMPLID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE emplid='<<EMP_ID>>' AND deptid  = '<<DEPT_ID>>' ) ) ) ) ) AS ADMINUSERID, (SELECT (FNAME || ' ' || LNAME) FROM cmsrda.ful_emp_cwid_nt_name WHERE CWID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE EMPLID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE emplid='<<EMP_ID>>' AND deptid  = '<<DEPT_ID>>' ) ) ) ) ) AS ADMINFULLNAME FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN ful_emp_cwid_nt_name E ON E.CWID = A.EMPLID WHERE B.EMPLID = '<<EMP_ID>>'";
		public static final String managerAdminDetailsLookUpFields = "MANAGERUSERID,ADMINUSERID,ADMINFULLNAME";
	//End of MPP Get Manager/Admin details lookup
}
