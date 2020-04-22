package com.aem.community.util;

public class CSUFConstants {
	// Start of Dock Notice
	public static final String dockNoticeUserIdSql = "Select A.EMPLID, A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = B.EMPLID and A.EMPLID = C.cwid and C.userid = ('<<getUser_ID>>')";
	public static final String dockNoticeFields = "EMPLID,FIRST_NAME,LAST_NAME,MIDDLE_NAME,EMPL_RCD,DEPTID,DEPTNAME,SCO_POSITION_NUM,NATIONAL_ID";

	public static final String dockNoticeEmpIdSql = "Select A.EMPLID,A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = B.EMPLID and A.EMPLID = '<<Empl_ID>>' and deptid in (select deptid from cmsrda.cms_hr_dept_sec where userid = '<<getUser_ID>>')";
	// End of Dock Notice
	
	// Start of SCPR Staff/MPP
	public static final String SCPREmpIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = Replace('<<Empl_ID>>', '-','') and A.EMPLID = B.EMPLID";
	public static final String SCPREmpIdLookupFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,Extenstion,FUL_COLLEGE_NAME";
	public static final String SCPRUserIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, A.EMPLID, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME  from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = C.cwid and C.userid = '<<getUser_ID>>' and A.EMPLID = B.EMPLID";
	public static final String SCPRUserIdLookupfields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,EMPLID,Extenstion,FUL_COLLEGE_NAME";
	// End of SCPR Staff/MPP

	// Start of MPP Emp Lookup
	public static final String mppEmpIDSQL = "Select A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTID, B.DEPTNAME, B.EMPL_RCD, B.DESCR, B.UNION_CD, B.GRADE, D.SUPERVISOR_NAME as Supervisorname, B.FUL_DIVISION as DIVSION,B.FUL_DIVISION_NAME as DIVISION_NAME,D.WORKING_TITLE AS SupervisorTitle, E.USERID AS EMPUSERID FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN ful_emp_cwid_nt_name E ON E.CWID = A.EMPLID WHERE B.EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL";
	public static final String mppLookUpFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,EMPL_RCD,DESCR,UNION_CD,GRADE,SupervisorName,DIVSION,DIVISION_NAME,SupervisorTitle,EMPUSERID";
	// End of MPP Emp Lookup

	// Start of Get Manager/Admin details lookup
	public static final String managerAdminDetailsSQL = "SELECT DISTINCT (SELECT USERID FROM cmsrda.ful_emp_cwid_nt_name WHERE CWID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE emplid='<<EMP_ID>>' AND deptid  = '<<DEPT_ID>>' ) ) ) AS MANAGERUSERID, (SELECT USERID FROM cmsrda.ful_emp_cwid_nt_name WHERE CWID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE EMPLID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE emplid='<<EMP_ID>>' AND deptid  = '<<DEPT_ID>>' ) ) ) ) ) AS ADMINUSERID, (SELECT (FNAME || ' ' || LNAME) FROM cmsrda.ful_emp_cwid_nt_name WHERE CWID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE EMPLID IN (SELECT EMPLID FROM FUL_ECM_JOB_VW WHERE POSITION_NBR IN (SELECT REPORTS_TO FROM FUL_ECM_JOB_VW WHERE emplid='<<EMP_ID>>' AND deptid  = '<<DEPT_ID>>' ) ) ) ) ) AS ADMINFULLNAME FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN ful_emp_cwid_nt_name E ON E.CWID = A.EMPLID WHERE B.EMPLID = '<<EMP_ID>>'";
	public static final String managerAdminDetailsLookUpFields = "MANAGERUSERID,ADMINUSERID,ADMINFULLNAME";
	// End of MPP Get Manager/Admin details lookup

	// Start of self eval get manager details
	public static final String mppManagerSQL = "Select USERID from ful_emp_cwid_nt_name where CWID = (Select EMPLID from FUL_ECM_JOB_VW where POSITION_NBR in (select REPORTS_TO from FUL_ECM_JOB_VW where emplid='<<EMPL_ID>>' and deptid='<<DEPTID>>'))";
	public static final String mppManagerLookupFields = "USERID";
	// End of self eval get manager details

	// Start of MPP Self eval user lookup
	public static final String MPPUserIDSQL = "Select A.FIRST_NAME, A.LAST_NAME, B.UNION_CD,B.DEPTID, B.DEPTNAME,  B.EMPL_RCD, B.DESCR, B.GRADE, A.EMPLID, (Select supervisor_name from ful_ecm_reports_vw where b.reports_to = position_nbr) as SupervisorName FROM FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C  where A.EMPLID = C.cwid and C.userid = '<<getUser_ID>>' and A.EMPLID = B.EMPLID";
	public static final String MPPSelfEvalUserIdFields = "FIRST_NAME,LAST_NAME,UNION_CD,DEPTID,DEPTNAME,EMPL_RCD,DESCR,GRADE,EMPLID,SUPERVISORNAME";
	// End of Mpp self eval user lookup
	
	//Start of Get MPP Self Eval details
	public static final String MPPReviewSQL = "SELECT * FROM AEM_MPP_SELF_EVAL EVAL1 WHERE empid = ('<<empid>>') AND review_period_from=('<<review_period_from>>') AND review_period_to=('<<review_period_to>>') AND deptid=('<<deptid>>') AND UPDATED_DT = (SELECT MAX(UPDATED_DT) FROM AEM_MPP_SELF_EVAL EVAL2 WHERE EVAL1.empid = EVAL2.empid AND EVAL1.review_period_from=EVAL2.review_period_from AND EVAL1.review_period_to=EVAL2.review_period_to AND EVAL1.deptid=EVAL2.deptid)";
	//End of Get Mpp self eval details
	
	//Start of Certificate Of Eligibility
	public static final String certificateEligibility="select a.first_name, a.last_name, b.deptname, b.deptid, b.union_cd, substr(Replace(Replace(a.work_phone, '/', ''),'-', ''),7,4) as Extension, c.userid, (case  when CSU_Prob_CD='I' or CSU_Prob_CD= 'J' then 'Permanent' when CSU_Prob_CD='A' or CSU_Prob_CD='B' or CSU_Prob_CD='C' or CSU_Prob_CD='D' or CSU_Prob_CD='E' then 'Probation'  else 'Other' end ) as Status from ful_ecm_pers_vw a, ful_ecm_job_vw b, ful_emp_cwid_nt_name c where a.emplid = b.emplid and a.emplid = c.cwid and a.emplid = '<<Empl_ID>>'";
    public static final String certificateFields="FIRST_NAME,LAST_NAME,DEPTNAME,DEPTID,UNION_CD,EXTENSION,USERID,STATUS";
   //End of Certificate Of Eligibility
    
   //Start of Catastrophic Leave Request/Donation
    public static final String catastrophicLeaveRequest="Select A.FIRST_NAME, A.LAST_NAME,A.EMPLID, B.DEPTNAME, B.DEPTID, B.EMPL_RCD, B.UNION_CD  From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = ('<<getUser_ID>>')";
    public static final String catastrophicFields="FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,EMPL_RCD,UNION_CD";
  //End of Catastrophic Leave Request/Donation
    
  //Start of Student Performance Evaluation
    public static final String studentPerformanceEval="Select A.Last_Name, A.First_Name,substr(A.Middle_Name, 1,1) as mid,  B.Descr, B.Hire_dt, B.deptname,  (Select supervisor_name from ful_ecm_reports_vw where b.reports_to = position_nbr) as SupervisorName, (Select extension from ful_ecm_reports_vw where b.reports_to = position_nbr) as Extension from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B WHERE A.EmplID = B.EmplID AND A.EmplID = Replace('<<Empl_ID>>' , '-', '')";
    public static final String studentPerformnceLookUP="FIRST_NAME,LAST_NAME,MID,DESCR,HIRE_DT,DEPTNAME,SUPERVISORNAME,EXTENSION";
  //End of Student Performance Evaluation
	
  //Start of Career Development Plan
    public static final String careerDevelopmentPlanUserLookUp="Select A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTNAME, B.DEPTID, B.EMPL_RCD, B.UNION_CD, B.DESCR  From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = '<<getUser_ID>>'";
    public static final String careerDevelopmentPlanFields="FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,EMPL_RCD,UNION_CD,DESCR";
  //End of Career Development Plan
    
  //Start of Cobra Final Notice
    public static final String cobraEmplIDSQL="Select  A.FIRST_NAME, A.LAST_NAME,  A.ADDRESS1, A.CITY, A.STATE, A.POSTAL, B.EMPL_RCD, B.DEPTNAME, B.JOBCODE From  FUL_ECM_PERS_VW A, FUL_ECM_JOB2_VW B Where A.EMPLID = '<<Empl_ID>>' AND B.EMPLID = '<<Empl_ID>>'";
    public static final String cobraLookUpFields="FIRST_NAME,LAST_NAME,ADDRESS1,CITY,STATE,POSTAL,EMPL_RCD,DEPTNAME,JOBCODE";

    public static final String CobraFinalNoticeBenefitLookUp="Select DISTINCT (SELECT a.descr from ful_ecm_ben2_vw a where a.plan_type = '10' and a.emplid = '<<Empl_ID>>')as Health, (SELECT ROUND((a.total_covrg_rate * 1.02) ,2) from ful_ecm_ben2_vw a where a.plan_type = '10' and a.emplid = '<<Empl_ID>>')as HealthCovrg, (SELECT a.descr from ful_ecm_ben2_vw a where a.plan_type = '11' and a.emplid = '<<Empl_ID>>')as Dental, (SELECT ROUND((a.total_covrg_rate * 1.02),2) from ful_ecm_ben2_vw a where a.plan_type = '11' and a.emplid = '<<Empl_ID>>')as DentalCovrg, (SELECT a.descr from ful_ecm_ben2_vw a where a.plan_type = '14' and a.emplid = '<<Empl_ID>>')as Vision, (SELECT ROUND((a.total_covrg_rate * 1.02),2) from ful_ecm_ben2_vw a where a.plan_type = '14' and a.emplid = '<<Empl_ID>>')as VisionCovrg, (SELECT a.descr from ful_ecm_fsa_benefit_vw a where a.emplid = '<<Empl_ID>>') as HCRA, (SELECT a.EMPL_CONTRBUTN_AMT from ful_ecm_fsa_benefit_vw a where a.emplid = '<<Empl_ID>>')as HCRACovrg, (SELECT SUBSTR (SYS_CONNECT_BY_PATH (CONCAT(CONCAT(First_Name, ' '),Last_Name) , ', '), 2) DependentChildName FROM (SELECT First_Name , Last_Name, ROW_NUMBER () OVER (ORDER BY Last_Name ) rn,COUNT (*) OVER () cnt FROM FUL_ECM_BEN_VW WHERE EMPLID = '<<Empl_ID>>' AND Relationship = 'SP') WHERE rn = cnt START WITH rn = 1 CONNECT BY rn = PRIOR rn + 1) as SpouseName, (SELECT SUBSTR (SYS_CONNECT_BY_PATH (CONCAT(CONCAT(First_Name, ' '),Last_Name) , ', '), 2) DependentChildName FROM (SELECT First_Name , Last_Name, ROW_NUMBER () OVER (ORDER BY Last_Name ) rn, COUNT (*) OVER () cnt FROM FUL_ECM_BEN_VW WHERE EMPLID = '<<Empl_ID>>' AND Relationship = 'C') WHERE rn = cnt START WITH rn = 1 CONNECT BY rn = PRIOR rn + 1) as DependentChildName from DUAL";
    public static final String CobraFinalNoticeBenefitFields="Health,HealthCovrg,Dental,DentalCovrg,Vision,VisionCovrg,HCRA,HCRACovrg,SpouseName,DependentChildName";

    public static final String cobraFinalDependentNameLookUp="Select (A.FIRST_NAME || ' ' || A.LAST_NAME) as Name, A.BIRTHDATE, 'self' as Relationship, A.NATIONAL_ID from FUL_ECM_PERS_VW A where A.EMPLID = Replace('<<Empl_ID>>', '-', '') AND A.FIRST_NAME like (decode(trim('<<DependentName>>'),'',' ', trim('<<DependentName>>')) || '%') union Select  (A.FIRST_NAME || ' ' || A.LAST_NAME)as Name, A.BIRTHDATE, A.RELATIONSHIP, A.NATIONAL_ID From  FUL_ECM_BEN_VW A, FUL_ECM_PERS_VW B Where A.EMPLID = B.EMPLID AND B.EMPLID = Replace('<<Empl_ID>>', '-', '') AND A.FIRST_NAME like (decode(trim('<<DependentName>>'),'',' ', trim('<<DependentName>>')) || '%')";
    public static final String cobraFinalDependentNameFields="Name,Relationship,BIRTHDATE,NATIONAL_ID";
  //End of Cobra Final Notice  
    
  //Start of Dependent Fee Waiver
    public static final String dependentFeeWaiverEmpLookUp="Select A.FIRST_NAME, A.LAST_NAME, B.DEPTNAME,  B.DEPTID, B.UNION_CD, substr(A.WORK_PHONE,7,10) as Extension, B.JOBCODE, (case FULL_PART_TIME when 'F' then '1' else '0' end) as FullTime, (case FULL_PART_TIME when 'P' then '1' else '0' end) as PartTime, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD = 'R03' then '1' else '0' end) as Tenure, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD <> 'R03' then '1' else '0' end) as Perm, (case when CSU_PROB_CD ='A' or CSU_PROB_CD = 'B' or  CSU_PROB_CD = 'C' or CSU_PROB_CD = 'D' or CSU_PROB_CD = 'E' then '1' else '0' end) as Prob, (case when CSU_PROB_CD =  'N' or CSU_PROB_CD = 'P' or CSU_PROB_CD = 'Q' or CSU_PROB_CD = 'T'  then '1' else '0' end) as Other, (case Reg_Temp when 'T' then '1' else '0' end) as Temp, (case Reg_Temp when 'T' then replace(expected_end_date, '/','') end ) as EndDate, (case when Empl_Status = 'L' or Empl_Status = 'P' then '1' else '0' end) as LeaveYes, (case when Empl_Status = 'L' or Empl_Status = 'P' then '0' else '1' end) as LeaveNo from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = B.EMPLID and A.EMPLID = Replace('<<Empl_ID>>','-','')";
    public static final	String dependentFeeWaiverEmpLookUpFields="FIRST_NAME,LAST_NAME,DEPTNAME,DEPTID,UNION_CD,Extension,JOBCODE,FullTime,PartTime,Tenure,Perm,Prob,Other,Temp,EndDate,LeaveYes,LeaveNo";

    public static final String dependentFeeApplicantNameLookup="Select a.last_name, a.birthdate, a.address1, a.city, a.state,(Select d.emplid from ful_ecm_ben_vw c, ful_ecm_pers_vw d where c.FIRST_NAME like (decode(trim('<<Applicant_First_Name>>'),'',' ', trim('<<Applicant_First_Name>>')) || '%') and c.EMPLID = Replace('<<Empl_ID>>', '-', '') and c.emplid != d.emplid and c.last_name = d.last_name and c.first_name = d.first_name and c.birthdate = d.birthdate) as AppCWID from ful_ecm_ben_vw a, ful_ecm_pers_vw b where A.EMPLID = Replace('<<Empl_ID>>', '-', '') and a.emplid = b.emplid and a.FIRST_NAME like (decode(trim('<<Applicant_First_Name>>'),'',' ', trim('<<Applicant_First_Name>>')) || '%')";
    public static final	String dependentFeeApplicantNameLookupFields="last_name,birthdate,address1,city,state";

    public static final String DependentFeeWaiverUserLookUp="Select A.FIRST_NAME, A.LAST_NAME,A.EMPLID, B.DEPTNAME,  B.DEPTID, B.UNION_CD, substr(A.WORK_PHONE,7,10) as Extension, B.JOBCODE, (case FULL_PART_TIME when 'F' then '1' else '0' end) as FullTime,(case FULL_PART_TIME when 'P' then '1' else '0' end) as PartTime, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD = 'R03'  then '1' else '0' end) as Tenure, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD <> 'R03' then '1' else '0' end) as Perm, (case when CSU_PROB_CD ='A' or CSU_PROB_CD = 'B' or  CSU_PROB_CD = 'C' or CSU_PROB_CD = 'D' or CSU_PROB_CD = 'E' then '1' else '0' end) as Prob, (case when CSU_PROB_CD =  'N' or CSU_PROB_CD = 'P' or CSU_PROB_CD = 'Q' or CSU_PROB_CD = 'T' then '1' else '0' end) as Other, (case Reg_Temp when 'T' then '1' else '0' end) as Temp, (case Reg_Temp when 'T' then replace(expected_end_date, '/','') end ) as EndDate, (case when Empl_Status = 'L' or Empl_Status = 'P' then '1' else '0' end) as LeaveYes, (case when Empl_Status = 'L' or Empl_Status = 'P' then '0' else '1' end) as LeaveNo From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = '<<getUser_ID>>'";
    public static final String DependentFeeWaiverUserLookUpFields="FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,UNION_CD,JOBCODE,Extension,FullTime,PartTime,Tenure,Perm,Prob,Other,Temp,EndDate,LeaveYes,LeaveNo";
  //End of Dependent Fee Waiver  
    
  //Start of Dental Plan Enrollment
    public static final String DentalPlanEnrollmentSSNLookUp="Select A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.ADDRESS1, A.CITY, A.STATE, A.POSTAL, (case A.SEX when 'M' then '1' else '0' end) as Male, (case A.SEX when 'F' then '1' else '0' end) as Female, (case A.MAR_STATUS when 'M' then '1' else '0' end) as Married, (case A.MAR_STATUS when 'U' then '1' else '0' end) as Single, B.DEPTNAME as DeptName, B.JOBCODE as JobCode From FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where  A.NATIONAL_ID = Replace('<<SSN>>', '-','') AND A.EMPLID = B.EMPLID";
    public static final String DentalPlanEnrollmentFields="FIRST_NAME,LAST_NAME,MIDDLE_NAME,ADDRESS1,CITY,STATE,POSTAL,Male,Female,Married,Single,DeptName,JobCode";
  //Start of Dental Plan Enrollment  
  //Start of Vision LIFE_LTD Lookup
  	public static final String visionLifeSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.UNION_CD, B.CSU_UNIT, B.JOBCODE, B.EMPL_RCD+1 AS Serial, B.CSU_SCO_AGENCY, B.DEPTNAME From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where A.NATIONAL_ID = Replace('<<SSN>>','-','') AND B.EMPLID = A.EMPLID";
  	public static final String lookupFieldsVisionLife = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,UNION_CD,CSU_UNIT,JOBCODE,Serial,CSU_SCO_AGENCY,DEPTNAME";
  	//End of Vision LIFE_LTD Lookup
}
