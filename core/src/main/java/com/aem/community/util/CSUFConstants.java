package com.aem.community.util;

public class CSUFConstants {
	
	// Start of 10_12_11_12 PayPlan
	public static final String PayPlanUserLookUp = "Select A.FIRST_NAME, A.LAST_NAME, substr(A.WORK_PHONE,7,10) as Extension, B.DEPTNAME, B.DEPTID,  B.EMPL_RCD, B.POSITION_NBR, B.DESCR, B.UNION_CD, ('242' || ' - ' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - ' ||  '00' || (EMPL_RCD+1) ) as SCOPosNum, B.STD_HOURS, B.POSITION_NBR, B.GRADE, B.EMPLID  from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = C.cwid and C.userid = '<<getUser_ID>>' and A.EMPLID = B.EMPLID";
	public static final String PayPlanLookUpFields = "FIRST_NAME,LAST_NAME,Extension,DEPTNAME,DEPTID,EMPL_RCD,POSITION_NBR,DESCR,UNION_CD,SCOPosNum,STD_HOURS,POSITION_NBR,GRADE,EMPLID";	
	// End of 10_12_11_12 PayPlan
	
	// Start of Dock Notice
	public static final String dockNoticeUserIdSql = "Select A.EMPLID, A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = B.EMPLID and A.EMPLID = C.cwid and C.userid = ('<<getUser_ID>>')";
	public static final String dockNoticeFields = "EMPLID,FIRST_NAME,LAST_NAME,MIDDLE_NAME,EMPL_RCD,DEPTID,DEPTNAME,SCO_POSITION_NUM,NATIONAL_ID";

	public static final String dockNoticeEmpIdSql = "Select A.EMPLID,A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.EMPL_RCD, B.DEPTID, B.DEPTNAME, ('242 -' || B.CSU_UNIT || ' - ' || B.JOBCODE || ' - '  || '00' || (B.EMPL_RCD+1) ) as SCO_Position_Num, A.NATIONAL_ID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = B.EMPLID and A.EMPLID = '<<Empl_ID>>' and deptid in (select deptid from cmsrda.cms_hr_dept_sec where userid = '<<getUser_ID>>')";
	// End of Dock Notice

	// Start of SCPR Staff/MPP
	public static final String SCPREmpIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME,C.USERID as EMP_USERID from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B,FUL_EMP_CWID_NT_NAME C where A.EMPLID = Replace('<<Empl_ID>>', '-','') and A.EMPLID = B.EMPLID AND C.CWID = B.EMPLID";
	public static final String SCPREmpIdLookupFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,Extenstion,FUL_COLLEGE_NAME,EMP_USERID";
	public static final String SCPRUserIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.BUILDING, B.CSU_UNIT, B.DEPTID, B.DEPTNAME, A.EMPLID, substr(A.WORK_PHONE, 7, 10) as Extenstion, B.FUL_COLLEGE_NAME,C.USERID as EMP_USERID  from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C where A.EMPLID = C.cwid and C.userid = '<<getUser_ID>>' and A.EMPLID = B.EMPLID AND C.CWID = B.EMPLID";
	public static final String SCPRUserIdLookupfields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,BUILDING,CSU_UNIT,DEPTID,DEPTNAME,EMPLID,Extenstion,FUL_COLLEGE_NAME,EMP_USERID";
	// End of SCPR Staff/MPP

	// Start of MPP Emp Lookup
	public static final String mppEmpIDSQL = "Select FIRST_NAME, LAST_NAME, EMPLID, DEPTID, DEPTNAME, EMPL_RCD, DESCR, UNION_CD, GRADE, Supervisorname,DIVSION,DIVISION_NAME,SupervisorTitle, EMPUSERID FROM HR_STAFF_EVALUATION WHERE EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL AND UNION_CD in ('M80','M98')";
	public static final String mppLookUpFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,EMPL_RCD,DESCR,UNION_CD,GRADE,SupervisorName,DIVSION,DIVISION_NAME,SupervisorTitle,EMPUSERID";
	// End of MPP Emp Lookup

	// Start of Get Staff's Manager/Admin details
	public static final String staffManagerAdminDetailsSQL = "SELECT MANAGER_EMP_USERID , ADMIN_EMP_USERID , ADMIN_EMP_NAME FROM HR_STAFF_EVALUATION WHERE EMPLID = '<<EMP_ID>>' AND DEPTID = '<<DEPT_ID>>' AND UNION_CD='<<UNION_CD>>'";
	public static final String staffManagerAdminDetailsLookUpFields = "MANAGER_EMP_USERID,ADMIN_EMP_USERID,ADMIN_EMP_NAME";
	// End of Get Staff's Manager/Admin details

	// Start of Get Manager/Admin details lookup
	public static final String managerAdminDetailsSQL = "SELECT MANAGER_EMP_USERID,ADMIN_EMP_USERID, ADMIN_EMP_NAME FROM HR_STAFF_EVALUATION WHERE EMPLID = '<<EMP_ID>>' AND DEPTID = '<<DEPT_ID>>' AND UNION_CD in ('M80','M98')";
	public static final String managerAdminDetailsLookUpFields = "MANAGER_EMP_USERID,ADMIN_EMP_USERID,ADMIN_EMP_NAME";
	// End of MPP Get Manager/Admin details lookup

	// Start of self eval get manager details
	public static final String mppManagerSQL = "SELECT MANAGER_EMP_USERID FROM HR_STAFF_EVALUATION WHERE EMPLID='<<EMPL_ID>>' and DEPTID='<<DEPTID>>' AND UNION_CD in ('M80','M98')";
	public static final String mppManagerLookupFields = "MANAGER_EMP_USERID";
	// End of self eval get manager details

	// Start of MPP Self eval user lookup
	public static final String MPPUserIDSQL = "select FIRST_NAME,LAST_NAME,UNION_CD,DEPTID,DEPTNAME,EMPL_RCD,DESCR,GRADE,EMPLID,SupervisorName from HR_STAFF_EVALUATION where EMP_USERID = '<<getUser_ID>>' and UNION_CD not in ('R03','R11','E99')";
	public static final String MPPSelfEvalUserIdFields = "FIRST_NAME,LAST_NAME,UNION_CD,DEPTID,DEPTNAME,EMPL_RCD,DESCR,GRADE,EMPLID,SUPERVISORNAME";
	// End of Mpp self eval user lookup

	// Start of Get MPP Self Eval details
	public static final String MPPReviewSQL = "SELECT * FROM AEM_MPP_SELF_EVAL EVAL1 WHERE empid = ('<<empid>>') AND review_period_from=('<<review_period_from>>') AND review_period_to=('<<review_period_to>>') AND deptid=('<<deptid>>') AND UPDATED_DT = (SELECT MAX(UPDATED_DT) FROM AEM_MPP_SELF_EVAL EVAL2 WHERE EVAL1.empid = EVAL2.empid AND EVAL1.review_period_from=EVAL2.review_period_from AND EVAL1.review_period_to=EVAL2.review_period_to AND EVAL1.deptid=EVAL2.deptid)";
	// End of Get Mpp self eval details

	// Start of Certificate Of Eligibility
	public static final String certificateEligibility = "select a.first_name, a.last_name, b.deptname, b.deptid, b.union_cd, substr(Replace(Replace(a.work_phone, '/', ''),'-', ''),7,4) as Extension, c.userid, (case  when CSU_Prob_CD='I' or CSU_Prob_CD= 'J' then 'Permanent' when CSU_Prob_CD='A' or CSU_Prob_CD='B' or CSU_Prob_CD='C' or CSU_Prob_CD='D' or CSU_Prob_CD='E' then 'Probation'  else 'Other' end ) as Status from ful_ecm_pers_vw a, ful_ecm_job_vw b, ful_emp_cwid_nt_name c where a.emplid = b.emplid and a.emplid = c.cwid and a.emplid = '<<Empl_ID>>'";
	public static final String certificateFields = "FIRST_NAME,LAST_NAME,DEPTNAME,DEPTID,UNION_CD,EXTENSION,USERID,STATUS";
	// End of Certificate Of Eligibility

	// Start of Catastrophic Leave Request/Donation
	public static final String catastrophicLeaveRequest = "Select A.FIRST_NAME, A.LAST_NAME,A.EMPLID, B.DEPTNAME, B.DEPTID, B.EMPL_RCD, B.UNION_CD  From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = ('<<getUser_ID>>')";
	public static final String catastrophicFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,EMPL_RCD,UNION_CD";
	// End of Catastrophic Leave Request/Donation

	// Start of Student Performance Evaluation
	public static final String studentPerformanceEval = "Select A.Last_Name, A.First_Name,substr(A.Middle_Name, 1,1) as mid,  B.Descr, B.Hire_dt, B.deptname,  (Select supervisor_name from ful_ecm_reports_vw where b.reports_to = position_nbr) as SupervisorName, (Select extension from ful_ecm_reports_vw where b.reports_to = position_nbr) as Extension from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B WHERE A.EmplID = B.EmplID AND A.EmplID = Replace('<<Empl_ID>>' , '-', '')";
	public static final String studentPerformnceLookUP = "FIRST_NAME,LAST_NAME,MID,DESCR,HIRE_DT,DEPTNAME,SUPERVISORNAME,EXTENSION";
	// End of Student Performance Evaluation

	// Start of Career Development Plan
	public static final String careerDevelopmentPlanUserLookUp = "Select A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTNAME, B.DEPTID, B.EMPL_RCD, B.UNION_CD, B.DESCR  From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = '<<getUser_ID>>'";
	public static final String careerDevelopmentPlanFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,EMPL_RCD,UNION_CD,DESCR";
	// End of Career Development Plan

	// Start of Cobra Final Notice
	public static final String cobraEmplIDSQL = "Select  A.FIRST_NAME, A.LAST_NAME,  A.ADDRESS1, A.CITY, A.STATE, A.POSTAL, B.EMPL_RCD, B.DEPTNAME, B.JOBCODE From  FUL_ECM_PERS_VW A, FUL_ECM_JOB2_VW B Where A.EMPLID = '<<Empl_ID>>' AND B.EMPLID = '<<Empl_ID>>'";
	public static final String cobraLookUpFields = "FIRST_NAME,LAST_NAME,ADDRESS1,CITY,STATE,POSTAL,EMPL_RCD,DEPTNAME,JOBCODE";

	public static final String CobraFinalNoticeBenefitLookUp = "Select DISTINCT (SELECT a.descr from ful_ecm_ben2_vw a where a.plan_type = '10' and a.emplid = '<<Empl_ID>>')as Health, (SELECT ROUND((a.total_covrg_rate * 1.02) ,2) from ful_ecm_ben2_vw a where a.plan_type = '10' and a.emplid = '<<Empl_ID>>')as HealthCovrg, (SELECT a.descr from ful_ecm_ben2_vw a where a.plan_type = '11' and a.emplid = '<<Empl_ID>>')as Dental, (SELECT ROUND((a.total_covrg_rate * 1.02),2) from ful_ecm_ben2_vw a where a.plan_type = '11' and a.emplid = '<<Empl_ID>>')as DentalCovrg, (SELECT a.descr from ful_ecm_ben2_vw a where a.plan_type = '14' and a.emplid = '<<Empl_ID>>')as Vision, (SELECT ROUND((a.total_covrg_rate * 1.02),2) from ful_ecm_ben2_vw a where a.plan_type = '14' and a.emplid = '<<Empl_ID>>')as VisionCovrg, (SELECT a.descr from ful_ecm_fsa_benefit_vw a where a.emplid = '<<Empl_ID>>') as HCRA, (SELECT a.EMPL_CONTRBUTN_AMT from ful_ecm_fsa_benefit_vw a where a.emplid = '<<Empl_ID>>')as HCRACovrg, (SELECT SUBSTR (SYS_CONNECT_BY_PATH (CONCAT(CONCAT(First_Name, ' '),Last_Name) , ', '), 2) DependentChildName FROM (SELECT First_Name , Last_Name, ROW_NUMBER () OVER (ORDER BY Last_Name ) rn,COUNT (*) OVER () cnt FROM FUL_ECM_BEN_VW WHERE EMPLID = '<<Empl_ID>>' AND Relationship = 'SP') WHERE rn = cnt START WITH rn = 1 CONNECT BY rn = PRIOR rn + 1) as SpouseName, (SELECT SUBSTR (SYS_CONNECT_BY_PATH (CONCAT(CONCAT(First_Name, ' '),Last_Name) , ', '), 2) DependentChildName FROM (SELECT First_Name , Last_Name, ROW_NUMBER () OVER (ORDER BY Last_Name ) rn, COUNT (*) OVER () cnt FROM FUL_ECM_BEN_VW WHERE EMPLID = '<<Empl_ID>>' AND Relationship = 'C') WHERE rn = cnt START WITH rn = 1 CONNECT BY rn = PRIOR rn + 1) as DependentChildName from DUAL";
	public static final String CobraFinalNoticeBenefitFields = "Health,HealthCovrg,Dental,DentalCovrg,Vision,VisionCovrg,HCRA,HCRACovrg,SpouseName,DependentChildName";

	public static final String cobraFinalDependentNameLookUp = "Select (A.FIRST_NAME || ' ' || A.LAST_NAME) as Name, A.BIRTHDATE, 'self' as Relationship, A.NATIONAL_ID from FUL_ECM_PERS_VW A where A.EMPLID = Replace('<<Empl_ID>>', '-', '') AND A.FIRST_NAME like (decode(trim('<<DependentName>>'),'',' ', trim('<<DependentName>>')) || '%') union Select  (A.FIRST_NAME || ' ' || A.LAST_NAME)as Name, A.BIRTHDATE, A.RELATIONSHIP, A.NATIONAL_ID From  FUL_ECM_BEN_VW A, FUL_ECM_PERS_VW B Where A.EMPLID = B.EMPLID AND B.EMPLID = Replace('<<Empl_ID>>', '-', '') AND A.FIRST_NAME like (decode(trim('<<DependentName>>'),'',' ', trim('<<DependentName>>')) || '%')";
	public static final String cobraFinalDependentNameFields = "Name,Relationship,BIRTHDATE,NATIONAL_ID";
	// End of Cobra Final Notice

	// Start of Dependent Fee Waiver
	public static final String dependentFeeWaiverEmpLookUp = "Select A.FIRST_NAME, A.LAST_NAME, B.DEPTNAME,  B.DEPTID, B.UNION_CD, substr(A.WORK_PHONE,7,10) as Extension, B.JOBCODE, (case FULL_PART_TIME when 'F' then '1' else '0' end) as FullTime, (case FULL_PART_TIME when 'P' then '1' else '0' end) as PartTime, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD = 'R03' then '1' else '0' end) as Tenure, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD <> 'R03' then '1' else '0' end) as Perm, (case when CSU_PROB_CD ='A' or CSU_PROB_CD = 'B' or  CSU_PROB_CD = 'C' or CSU_PROB_CD = 'D' or CSU_PROB_CD = 'E' then '1' else '0' end) as Prob, (case when CSU_PROB_CD =  'N' or CSU_PROB_CD = 'P' or CSU_PROB_CD = 'Q' or CSU_PROB_CD = 'T'  then '1' else '0' end) as Other, (case Reg_Temp when 'T' then '1' else '0' end) as Temp, (case Reg_Temp when 'T' then replace(expected_end_date, '/','') end ) as EndDate, (case when Empl_Status = 'L' or Empl_Status = 'P' then '1' else '0' end) as LeaveYes, (case when Empl_Status = 'L' or Empl_Status = 'P' then '0' else '1' end) as LeaveNo from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B where A.EMPLID = B.EMPLID and A.EMPLID = Replace('<<Empl_ID>>','-','')";
	public static final String dependentFeeWaiverEmpLookUpFields = "FIRST_NAME,LAST_NAME,DEPTNAME,DEPTID,UNION_CD,Extension,JOBCODE,FullTime,PartTime,Tenure,Perm,Prob,Other,Temp,EndDate,LeaveYes,LeaveNo";

	public static final String dependentFeeApplicantNameLookup = "Select a.last_name, a.birthdate, a.address1, a.city, a.state,(Select d.emplid from ful_ecm_ben_vw c, ful_ecm_pers_vw d where c.FIRST_NAME like (decode(trim('<<Applicant_First_Name>>'),'',' ', trim('<<Applicant_First_Name>>')) || '%') and c.EMPLID = Replace('<<Empl_ID>>', '-', '') and c.emplid != d.emplid and c.last_name = d.last_name and c.first_name = d.first_name and c.birthdate = d.birthdate) as AppCWID from ful_ecm_ben_vw a, ful_ecm_pers_vw b where A.EMPLID = Replace('<<Empl_ID>>', '-', '') and a.emplid = b.emplid and a.FIRST_NAME like (decode(trim('<<Applicant_First_Name>>'),'',' ', trim('<<Applicant_First_Name>>')) || '%')";
	public static final String dependentFeeApplicantNameLookupFields = "last_name,birthdate,address1,city,state";

	public static final String DependentFeeWaiverUserLookUp = "Select A.FIRST_NAME, A.LAST_NAME,A.EMPLID, B.DEPTNAME,  B.DEPTID, B.UNION_CD, substr(A.WORK_PHONE,7,10) as Extension, B.JOBCODE, (case FULL_PART_TIME when 'F' then '1' else '0' end) as FullTime, (case FULL_PART_TIME when 'P' then '1' else '0' end) as PartTime, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD = 'R03'  then '1' else '0' end) as Tenure, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD <> 'R03' then '1' else '0' end) as Perm, (case when CSU_PROB_CD ='A' or CSU_PROB_CD = 'B' or  CSU_PROB_CD = 'C' or CSU_PROB_CD = 'D' or CSU_PROB_CD = 'E' then '1' else '0' end) as Prob, (case when CSU_PROB_CD =  'N' or CSU_PROB_CD = 'P' or CSU_PROB_CD = 'Q' or CSU_PROB_CD = 'T' then '1' else '0' end) as Other, (case Reg_Temp when 'T' then '1' else '0' end) as Temp, (case Reg_Temp when 'T' then replace(expected_end_date, '/','') end ) as EndDate, (case when Empl_Status = 'L' or Empl_Status = 'P' then '1' else '0' end) as LeaveYes, (case when Empl_Status = 'L' or Empl_Status = 'P' then '0' else '1' end) as LeaveNo From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = '<<getUser_ID>>'";
	public static final String DependentFeeWaiverUserLookUpFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,UNION_CD,Extension,JOBCODE,FullTime,PartTime,Tenure,Perm,Prob,Other,Temp,EndDate,LeaveYes,LeaveNo";
	// End of Dependent Fee Waiver

	// Start of Dental Plan Enrollment
	public static final String DentalPlanEnrollmentSSNLookUp = "Select A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, A.ADDRESS1, A.CITY, A.STATE, A.POSTAL, (case A.SEX when 'M' then '1' else '0' end) as Male, (case A.SEX when 'F' then '1' else '0' end) as Female, (case A.MAR_STATUS when 'M' then '1' else '0' end) as Married, (case A.MAR_STATUS when 'U' then '1' else '0' end) as Single, B.DEPTNAME as DeptName, B.JOBCODE as JobCode From FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where  A.NATIONAL_ID = Replace('<<SSN>>', '-','') AND A.EMPLID = B.EMPLID";
	public static final String DentalPlanEnrollmentFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,ADDRESS1,CITY,STATE,POSTAL,Male,Female,Married,Single,DeptName,JobCode";
	// Start of Dental Plan Enrollment

	// Start of Initial Cobra Lookup
	public static final String initialCobraEmpLookUp = "Select A.FIRST_NAME, A.LAST_NAME, B.EMPL_RCD, (SELECT (C.FIRST_NAME || '   ' || C.LAST_NAME) FROM FUL_ECM_BEN_VW C WHERE C.EMPLID = '<<Empl_ID>>' AND C.RELATIONSHIP = 'SP') as PartnerName From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where  A.EMPLID = '<<Empl_ID>>'  AND A.EMPLID = B.EMPLID";
	public static final String initialCobraEmpLookUpfields = "FIRST_NAME,LAST_NAME,EMPL_RCD,PartnerName";
	// End of Initial Cobra Lookup

	// Start of Short App Emp Fee Waiver
	public static final String shortAppEmpFeeWaiver = "SELECT A.FIRST_NAME, A.MIDDLE_NAME, A.LAST_NAME, A.NATIONAL_ID, A.ADDRESS1, A.ADDRESS2, A.CITY, A.STATE, A.POSTAL, (case SEX when 'M' then '1' else '0' end) as Male, (case SEX when 'F' then '1' else '0' end) as Female, A.EMPLID, A.BIRTHDATE, A.HOME_PHONE, C.USERID FROM FUL_ECM_PERS_VW A, FUL_EMP_CWID_NT_NAME C WHERE C.USERID = '<<getUser_ID>>' and A.EMPLID = C.CWID";
	public static final String shortAppEmpFeeWaiverFields = "FIRST_NAME,MIDDLE_NAME,LAST_NAME,NATIONAL_ID,ADDRESS1,ADDRESS2,CITY,STATE,POSTAL,MALE,FEMALE,EMPLID,BIRTHDATE,HOME_PHONE,USERID";
	// End of Short App Emp Fee Waiver

	// Start of Vision LIFE_LTD Lookup
	public static final String visionLifeSQL = "Select  A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.UNION_CD, B.CSU_UNIT, B.JOBCODE, B.EMPL_RCD+1 AS Serial, B.CSU_SCO_AGENCY, B.DEPTNAME, C.USERID as EMP_USERID From FUL_EMP_CWID_NT_NAME C, FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where A.NATIONAL_ID = Replace('<<SSN>>','-','') AND B.EMPLID = A.EMPLID and C.CWID = B.EMPLID";
	public static final String lookupFieldsVisionLife = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,UNION_CD,CSU_UNIT,JOBCODE,Serial,CSU_SCO_AGENCY,DEPTNAME,EMP_USERID";
	// End of Vision LIFE_LTD Lookup
	
	// Start of Grade Change
	public static final String gradeChangeSingleStudent = "Select * from AR_GRADE_FORM where TERM_DESCR = '<<TERM_DESCR>>' and CRSE_NAME ='<<CRSE_NAME>>' and class_nbr ='<<classNo>>' and class_section ='<<sectionNo>>' and INSTR_CWID ='<<instCwid>>' and cwid ='<<cwid>>'";
	public static final String gradeChangeBulk = "Select * from AR_GRADE_FORM where TERM_DESCR = '<<TERM_DESCR>>' and CRSE_NAME ='<<CRSE_NAME>>' and class_nbr ='<<classNo>>' and class_section ='<<sectionNo>>' and INSTR_CWID ='<<instCwid>>'";
	public static final String gradeChangeFields = "CWID,FNAME,LNAME,MNAME,STDNT_CAR_NBR,EFFDT,EFFSEQ,ACAD_PROG,INSTITUTION,TERM_DESCR,STRM,STDNT_ENRL_STATUS,CLASS_NBR,CRSE_ID,CRSE_NAME,CLASS_SECTION,SCHEDULE_NBR,COURSE_LEVEL,UNT_TAKEN,CURRENT_GRADE,DEPT_CD,COLLEGE,MAJOR_CODE,MAJOR_DESCR,MAJOR_TYPE,DEGREE_TYPE,INSTR_NAME,INSTR_EMAIL,INSTR_USERID,INSTR_CWID,CHAIR_USERID,CHAIR_NAME,CHAIR_CWID,CHAIR_EMAIL,DEAN_USERID,DEAN_NAME,DEAN_CWID,DEAN_EMAIL,ASS_DEAN_USERID,ASS_DEAN_NAME,ASS_DEAN_CWID,ASS_DEAN_EMAIL,STUDENT_EMAIL,STUDENT_USERID";
	public static final String gradeChangeUserDetails = "Select distinct CRSE_NAME, INSTR_CWID, INSTR_NAME, CLASS_NBR,INSTR_USERID from AR_GRADE_FORM where LOWER(instr_userid) = LOWER('<<instr_userid>>') and STRM = '<<STRM>>'";
	public static final String gradeChangeClassDetails = "select distinct * from AR_GRADE_FORM where CRSE_NAME = '<<CRSE_NAME>>' and LOWER(instr_userid) = LOWER('<<instr_userid>>') and TERM_DESCR = '<<TERM_DESCR>>'";
	public static final String gradeChangeSchemeDetails = "select distinct CRSE_ID,GRADING_BASIS,GRADING_SCHEME from AR_GRADE_FORM where TERM_DESCR = '<<TERM_DESCR>>' and CRSE_ID='<<CRSE_ID>>'";
	public static final String gradeChangeToDetails = "select distinct CRSE_GRADE_INPUT,DESCR from AR_GRADE_ROSTER where CRSE_ID='<<CRSE_ID>>' and GRADING_BASIS='<<GRADING_BASIS>>' and GRADING_SCHEME='<<GRADING_SCHEME>>' order by CRSE_GRADE_INPUT";
	public static final String gradeChangeCwidDetails = "Select distinct INSTR_USERID, INSTR_NAME from AR_GRADE_FORM where INSTR_CWID= '<<INSTR_CWID>>' and TERM_DESCR='<<TERM_DESCR>>'";
	public static final String gradeChangeLoggedIn = "SELECT DISTINCT INSTR_CWID, INSTR_NAME FROM AR_GRADE_FORM where LOWER(instr_userid) = LOWER('<<instr_userid>>') and TERM_DESCR='<<TERM_DESCR>>'";
	public static final String gradeChangeTerm = "SELECT DISTINCT CRSE_NAME, CLASS_NBR, INSTR_CWID,INSTR_USERID,CLASS_SECTION, COURSE_LEVEL, INSTR_NAME, DEPT_CD FROM AR_GRADE_FORM where INSTR_CWID='<<INSTR_CWID>>' AND TERM_DESCR='<<TERM_DESCR>>'";
	public static final String gradeChangeClassSection = "SELECT DISTINCT CLASS_SECTION, CRSE_NAME FROM AR_GRADE_FORM where INSTR_CWID='<<INSTR_CWID>>' AND TERM_DESCR='<<TERM_DESCR>>' AND CLASS_NBR='<<CLASS_NBR>>'";
	// End of Grade Change

	// Start of Employee Fee Waiver User Lookup
	public static final String employeeFeeWaiverUserLookUp = "Select A.FIRST_NAME, A.LAST_NAME,A.EMPLID, B.DEPTNAME,  B.DEPTID, B.UNION_CD, substr(A.WORK_PHONE,7,10) as Extension, B.JOBCODE, (case FULL_PART_TIME when 'F' then '1' else '0' end) as FullTime,  (case FULL_PART_TIME when 'P' then '1' else '0' end) as PartTime, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD = 'R03'  then '1' else '0' end) as Tenure, (case when (CSU_PROB_CD = 'I' or CSU_PROB_CD = 'J') and UNION_CD <> 'R03' then '1' else '0' end) as Perm, (case when CSU_PROB_CD ='A' or CSU_PROB_CD = 'B' or  CSU_PROB_CD = 'C' or CSU_PROB_CD = 'D' or CSU_PROB_CD = 'E' then '1' else '0' end) as Prob, (case when CSU_PROB_CD =  'N' or CSU_PROB_CD = 'P' or CSU_PROB_CD = 'Q' or CSU_PROB_CD = 'T'  then '1' else '0' end) as Other, (case Reg_Temp when 'T' then '1' else '0' end) as Temp, (case Reg_Temp when 'T' then replace(expected_end_date, '/','') end ) as EndDate, (case when Empl_Status = 'L' or Empl_Status = 'P' then '1' else '0' end) as LeaveYes, (case when Empl_Status = 'L' or Empl_Status = 'P' then '0' else '1' end) as LeaveNo From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = '<<getUser_ID>>'";
	public static final String employeeFeeWaiverUserLookUpFields = "FIRST_NAME,LAST_NAME,EMPLID,DEPTNAME,DEPTID,UNION_CD,JOBCODE,Extension,FullTime,PartTime,Tenure,Perm,Prob,Temp,EndDate,LeaveYes,OtherLeaveYes,LeaveNo";
	// End of Employee Fee Waiver User Lookup

	// Start of Start of Get Pre Perf Eval Data
	public static final String PrePerfReviewSQL = "select * from aem_pre_perf_eval eval1 where emplid = ('<<empid>>') and review_from_dt=('<<review_period_from>>') and review_to_dt=('<<review_period_to>>') and deptid=('<<deptid>>') and UPDATED_DT = (SELECT MAX(UPDATED_DT) FROM aem_pre_perf_eval eval2 WHERE eval1.emplid = eval2.emplid AND eval1.review_from_dt=eval2.review_from_dt AND eval1.review_to_dt=eval2.review_to_dt and eval1.deptid=eval2.deptid)";
	// End of Start of Get Pre Perf Eval Data

	// Start of User-id lookup
	public static final String lookupFieldsUserIdLookup = "FIRST_NAME,LAST_NAME,DEPTID,DEPTNAME,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,UNION_CD,EMPLID";
	public static final String userIDSQL = "select FIRST_NAME,LAST_NAME,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,UNION_CD,SupervisorName,EMPLID,SupervisorTitle from HR_STAFF_EVALUATION where EMP_USERID = '<<getUser_ID>>' AND UNION_CD not in ('R03','R11','E99','M80','M98')";
	// End of User-id lookup

	// Start of MPP Get HR COO Details
	public static final String mppHRCooSQL = "select * from AEM_EVAL_HR_COORDINATORS where division=('<<division>>')";
	// End of MPP Get HR COO Details

	// Start of Personnel Action Plan
	public static final String personnelActionPlan = "Select A.FIRST_NAME, A.LAST_NAME, A.MIDDLE_NAME, B.CSU_SCO_AGENCY, B.CSU_UNIT, B.JOBCODE, B.EMPL_RCD+1 as SERIAL_NO, B.DEPTNAME, B.DEPTID,  B.EMPL_RCD, B.POSITION_NBR, B.DESCR, B.UNION_CD, B.FUL_DIVISION_NAME,  B.FUL_COLLEGE_NAME,  B.STD_HOURS, (CASE B.UNION_CD when 'M80' then B.DESCR1 else '' end) as DESCR1, B.CSU_ANNI_MONTH, B.CSU_ANNI_YEAR,(case B.FLSA_STATUS when 'X' then '1' else '0' end) as FLSAExmp, (case B.FLSA_STATUS when 'N' then '1' else '0' end) as FLSANon, B.GRADE, B.MONTHLY_RT, C.SUPERVISOR_NAME, (SELECT(b1.CSU_MPP_JOB_FAMILY  ||  b1.CSU_MPP_JOB_FUNC || b1.CSU_MPP_RPT_CAT) FROM ful_ecm_job_vw a1, ful_ecm_post_data_vw b1 WHERE a1.position_nbr = b1.position_nbr and a1.emplid = a.emplid) As MppJobcode,b.Expected_End_Date, b.fte from FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_ECM_REPORTS_VW C where A.EMPLID = '<<Empl_ID>>' and A.EMPLID = B.EMPLID  and B.REPORTS_TO = C.POSITION_NBR";
	public static final String personnelActionPlanFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,CSU_SCO_AGENCY,CSU_UNIT,JOBCODE,SERIAL_NO,DEPTNAME,DEPTID,EMPL_RCD,POSITION_NBR,DESCR,UNION_CD,FUL_DIVISION_NAME,FUL_COLLEGE_NAME,STD_HOURS,DESCR1,CSU_ANNI_MONTH,CSU_ANNI_YEAR,FLSAEXMP,FLSANON,GRADE,MONTHLY_RT,SUPERVISOR_NAME,MPPJOBCODE,EXPECTED_END_DATE,FTE";
	// End of Personnel Action Plan

	// Start of Personal File Access Request Form
	public static final String personalFileAccessRequestUserLookUp = "select a.first_name, a.last_name, substr(a.middle_name,1,1) as Middle_Initial, a.emplid, a.work_phone, b.deptid, b.deptname from ful_ecm_pers_vw a, ful_ecm_job_vw b, ful_emp_cwid_nt_name c where a.emplid = b.emplid  and a.emplid = c.cwid and c.userid = '<<getUser_ID>>'";
	public static final String personalFileAccessRequestUserLookUpFields = "first_name,last_name,Middle_Initial,emplid,work_phone,deptid,deptname";
	// Start of Personal File Access Request Form

	// Start of Cobra Enroll Delta
	public static final String CobraEnrollDeltaSSNLookUp = "Select A.FIRST_NAME, A.LAST_NAME, A.BIRTHDATE, A.ADDRESS1, A.CITY, A.STATE, A.POSTAL, A.HOME_PHONE, B.DEPTNAME, B.JOBCODE From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where  A.NATIONAL_ID = Replace('<<SSN>>', '-', '') and A.EMPLID = B.EMPLID";
	public static final String CobraEnrollDeltaSSNLookUpFields = "FIRST_NAME,LAST_NAME,BIRTHDATE,ADDRESS1,CITY,STATE,POSTAL,HOME_PHONE,DEPTNAME,JOBCODE";
	// End of Cobra Enroll Delta

	// Start of VSP Cobra
	public static final String vspCobra = "Select B.LAST_NAME, B.FIRST_NAME, B.MIDDLE_NAME, B.BIRTHDATE,  B.ADDRESS1, B.CITY, B.STATE, B.POSTAL, 'N/A' as DeptName, 'N/A' as Jobcode From FUL_ECM_BEN_VW B Where  B.NATIONAL_ID = Replace('<<Applicant_SSN>>','-','') union Select B.LAST_NAME, B.FIRST_NAME, B.MIDDLE_NAME, B.BIRTHDATE,  B.ADDRESS1, B.CITY, B.STATE, B.POSTAL, A.DEPTNAME, A.JOBCODE From FUL_ECM_PERS_VW B, FUL_ECM_JOB_VW A Where  B.NATIONAL_ID = Replace('<<Applicant_SSN>>', '-','') AND A.EMPLID = B.EMPLID";
	public static final String vspCobraLookUpFields = "LAST_NAME,FIRST_NAME,MIDDLE_NAME,BIRTHDATE,ADDRESS1,CITY,STATE,POSTAL,DEPTNAME,JOBCODE";
	// End of VSP Cobra
	
	//Start of Evaluation Emp Lookup	
	public static final String emplIDSQL = "SELECT A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS SupervisorTitle,B.FUL_DIVISION as DIVSION, B.FUL_DIVISION_NAME as DIVISION_NAME, E.USERID AS EMPUSERID FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID WHERE B.EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL";
	public static final String lookupFieldsEmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Emp Lookup
	
	/*//Start of Major_Minor Change
	public static final String studentPersonalInformationCWID = "select DISTINCT STUDENT_ID,STUDENT_EMAIL,STUDENT_FNAME,STUDENT_LNAME,STUDENT_PHONE,STUDENT_USERID,ACAD_PROG,TERM_DESCR,ACAD_YEAR from AR_CSU_STDNT_PROG_DATA where STUDENT_ID='<<CWID>>' and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE in ('MAJ','PRP')  and PLAN_RANK ='1'";
	public static final String studentPersonalInformation = "select DISTINCT STUDENT_ID,STUDENT_EMAIL,STUDENT_FNAME,STUDENT_LNAME,STUDENT_PHONE,STUDENT_USERID,ACAD_PROG,TERM_DESCR,ACAD_YEAR from AR_CSU_STDNT_PROG_DATA where UPPER(STUDENT_USERID) = UPPER('<<getUser_ID>>') and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE in ('MAJ','PRP')  and PLAN_RANK ='1'";
	public static final String getMajorsDetails = "select * from AR_CSU_STDNT_PLAN where UPPER(STUDENT_USERID) = UPPER('<<getUser_ID>>') and ACAD_PROG='<<ACAD_PROG>>' and ACAD_PLAN_TYPE='<<ACAD_PLAN_TYPE>>'";
	public static final String getCurrentMajorDetailsUpdated = "select distinct ACAD_PLAN,PROGRAMS from AR_CSU_STDNT_PROG_DATA where STUDENT_USERID='<<getUser_ID>>' and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE in ('MAJ','PRP') and PLAN_RANK='1'";	
	public static final String getAllMajorsUpdated = "select distinct ACAD_PLAN, PROGRAMS from AR_CSU_STDNT_PROG where DESCR like '%1MJ%' ORDER BY PROGRAMS ASC";
	public static final String getAllMajorsAcadPlan = "select distinct ACAD_PLAN from AR_CSU_STDNT_PROG where DESCR like '%1MJ%' and PROGRAMS='<<PROGRAM>>'";
	public static final String getAllAdditionalMajors = "select distinct ACAD_PLAN, PROGRAMS from AR_CSU_STDNT_PROG where DESCR like '%2MJ%' ORDER BY PROGRAMS ASC";
	public static final String getAllAdditionalMajorsAcadPlan = "select distinct ACAD_PLAN from AR_CSU_STDNT_PROG where DESCR like '%2MJ%' and PROGRAMS='<<PROGRAM>>'";
	public static final String getCurrentAdditionalMajors = "select distinct ACAD_PLAN,PROGRAMS from AR_CSU_STDNT_PROG_DATA where STUDENT_USERID='<<getUser_ID>>' and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE in ('MAJ','PRP') and PLAN_RANK>='2'";
	public static final String getCurrentAdditionalMajorsAcadPlan = "select distinct ACAD_PLAN from AR_CSU_STDNT_PROG_DATA where STUDENT_USERID='<<getUser_ID>>' and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE='MAJ' and PLAN_RANK>='2' and PROGRAMS='<<PROGRAM>>'";
	public static final String getAllMinorsUpdated = "Select ACAD_PLAN, PROGRAMS from AR_CSU_STDNT_PROG where ACAD_PLAN_TYPE='MIN' ORDER BY PROGRAMS ASC";
	public static final String getCurrentMinorsUpdated = "select distinct ACAD_PLAN,PROGRAMS from AR_CSU_STDNT_PROG_DATA where STUDENT_USERID='<<getUser_ID>>' and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE='MIN'";
	public static final String getAllMinorAcadPlan = "Select ACAD_PLAN from AR_CSU_STDNT_PROG where ACAD_PLAN_TYPE='MIN'and PROGRAMS='<<PROGRAM>>'";
	public static final String getCurrentMinorAcadPlan = "select distinct ACAD_PLAN from AR_CSU_STDNT_PROG_DATA where PROGRAMS='<<PROGRAM>>'";
	public static final String getMinorChairDetails = "select distinct DEPTID,DEPTNAME,CHAIR_USERID,CHAIR_NAME,CHAIR_EMPLID,CHAIR_EMAIL from AR_CSU_STDNT_PROG_DATA where PROGRAMS='<<PROGRAM>>'";
	public static final String getAllCertificates = "Select ACAD_PLAN, PROGRAMS from AR_CSU_STDNT_PROG where ACAD_PLAN_TYPE='CER' ORDER BY PROGRAMS ASC";
	public static final String getCurrentCertificates = "select distinct ACAD_PLAN,PROGRAMS from AR_CSU_STDNT_PROG_DATA where STUDENT_USERID='<<getUser_ID>>' and ACAD_CAREER='UGRD' and ACAD_PLAN_TYPE='CER'";
	public static final String getAllCertificateAcadPlan = "Select ACAD_PLAN  from AR_CSU_STDNT_PROG where ACAD_PLAN_TYPE='CER' and PROGRAMS='<<PROGRAM>>'";
	public static final String getCurrentCertificateAcadPlan = "select distinct ACAD_PLAN from AR_CSU_STDNT_PROG_DATA where PROGRAMS='<<PROGRAM>>'";
	public static final String getAllChairDetials = "select distinct CHAIR_EMPLID,CHAIR_EMPNAME,CHAIR_USERID,CHAIR_EMAIL,DEPTID,DEPTNAME from AR_CSU_STDNT_PROG where PROGRAMS='<<PROGRAM>>'";
	//End of Major_Minor Change */
	
	//Start of Evaluation Unit 4 Emp Lookup		
	//public static final String staffEvalUnit4EmplIDSQL = "Select A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, (Select supervisor_name from ful_ecm_reports_vw where b.reports_to = position_nbr) as SupervisorName, (Select Working_Title from ful_ecm_reports_vw where b.reports_to = position_nbr) as SupervisorTitle, B.FUL_DIVISION as DIVSION, B.FUL_DIVISION_NAME as DIVISION_NAME, E.USERID AS EMPUSERID FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID WHERE A.EMPLID = Replace('<<Empl_ID>>', '-', '') and A.EMPLID = B.EMPLID AND B.UNION_CD='R04'";
	//Modified on 07012020
	public static final String staffEvalUnit4EmplIDSQL = "select FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID	from HR_STAFF_EVALUATION where EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL AND UNION_CD='R04'";
	public static final String staffEvalUnit4LookupFieldsEmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";		
	//End of Evaluation Unit 4  Emp Lookup
	
	//Start of Evaluation Cust Emp Lookup	
	public static final String staffEvalCustEmplIDSQL = "SELECT A.FIRST_NAME, A.LAST_NAME, A.EMPLID, B.DEPTID, B.DEPTNAME, B.UNION_CD, B.EMPL_RCD, B.DESCR, B.GRADE, D.SUPERVISOR_NAME AS SupervisorName, D.WORKING_TITLE AS SupervisorTitle,B.FUL_DIVISION as DIVSION, B.FUL_DIVISION_NAME as DIVISION_NAME, E.USERID AS EMPUSERID FROM FUL_ECM_JOB_VW B LEFT JOIN FUL_ECM_PERS_VW A ON A.EMPLID = B.EMPLID LEFT JOIN FUL_ECM_REPORTS_VW D ON D.POSITION_NBR = B.REPORTS_TO LEFT JOIN cmsrda.ful_emp_cwid_nt_name E on A.EMPLID = E.CWID WHERE B.EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL and B.UNION_CD='R05'";
	public static final String lookupFieldsCustEmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Cust Emp Lookup
	
	//Start of Evaluation Conf Emp Lookup	
	public static final String staffEvalConfEmplIDSQL = "select FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID from HR_STAFF_EVALUATION WHERE EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL and UNION_CD='C99'";
	public static final String lookupFieldsConfEmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Conf Emp Lookup
	
	//Start of Evaluation Unit1 Emp Lookup	
	public static final String staffEvalUnit1EmplIDSQL = "select FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID from HR_STAFF_EVALUATION WHERE EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL and	UNION_CD='R01'";
	public static final String lookupFieldsUnit1EmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Unit1 Emp Lookup
	
	//Start of Evaluation Unit2579 Emp Lookup	
	public static final String staffEvalUnit2579EmplIDSQL = "select FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID from HR_STAFF_EVALUATION WHERE EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL and UNION_CD in ('R02','R05','R07','R09')";
	public static final String lookupFieldsUnit2579EmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Unit2579 Emp Lookup
	
	//Start of Evaluation Unit6 Emp Lookup	
	public static final String staffEvalUnit6EmplIDSQL = "select FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID from HR_STAFF_EVALUATION WHERE EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL and UNION_CD ='R06'";
	public static final String lookupFieldsUnit6EmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Unit6 Emp Lookup
	
	//Start of Evaluation Unit8 Emp Lookup	
	public static final String staffEvalUnit8EmplIDSQL = "select FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID from HR_STAFF_EVALUATION WHERE EMPLID = '<<Empl_ID>>' AND ISEVALUSER('<<getUser_ID>>') IS NOT NULL and UNION_CD ='R08'";
	public static final String lookupFieldsUnit8EmpLookup = "FIRST_NAME,LAST_NAME,EMPLID,DEPTID,DEPTNAME,UNION_CD,EMPL_RCD,DESCR,GRADE,SupervisorName,SupervisorTitle,DIVSION,DIVISION_NAME,EMPUSERID";	
	//End of Evaluation Unit8 Emp Lookup
	
	//Start of Get Email Lookup
	public static final String getEmailAddressCwidLookup = "select EMAILID from EMPL_ACT_DIR_DATA where EMPLOYEEID = '<<Emp_ID>>'";
	public static final String getEmailAddressUserIdLookup = "select EMAILID from EMPL_ACT_DIR_DATA where USERID = '<<UID>>'";
	//End of Get Email Lookup
	
	//Start of Get Logged In User Details from DB SQL
	public static final String getLoggedInUserDetailsFromDB = "SELECT FNAME, LNAME from cmsrda.ful_emp_cwid_nt_name where USERID = '<<get_user_id>>'";
	public static final String loggedInUserDetailsLookupFields = "FNAME,LNAME";
	
	//End of Get Logged In User Details from DB SQL
	
	// Start of Direct Pay Dental
	public static final String directPayDental = "Select A.FIRST_NAME, A.LAST_NAME,  A.MIDDLE_NAME,   B.JOBCODE,  A.CITY, A.STATE, A.POSTAL, A.HOME_PHONE,  B.UNION_CD, B.DEPTNAME,  A.ADDRESS1, C.USERID as EMP_USERID From FUL_EMP_CWID_NT_NAME C , FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B Where  A.NATIONAL_ID = Replace('<<SSN>>','-','') AND A.EMPLID = B.EMPLID and C.CWID = B.EMPLID";
	public static final String directPayDentalLookUpFields = "FIRST_NAME,LAST_NAME,MIDDLE_NAME,JOBCODE,CITY,STATE,POSTAL,HOME_PHONE,UNION_CD,DEPTNAME,ADDRESS1,EMP_USERID";
	// End of Direct Pay Dental

	// Start of Domestic Partner
	public static final String domesticPartner = "Select A.FIRST_NAME, A.LAST_NAME,A.NATIONAL_ID, B.DEPTNAME, B.JOBCODE,C.USERID as EMP_USERID From  FUL_ECM_PERS_VW A, FUL_ECM_JOB_VW B, FUL_EMP_CWID_NT_NAME C Where  A.EMPLID = B.EMPLID AND A.emplid = C.cwid AND C.userid = '<<getUser_ID>>' AND C.CWID = B.EMPLID";
	public static final String domesticPartnerLookUpFields = "FIRST_NAME,LAST_NAME,NATIONAL_ID,DEPTNAME,JOBCODE,EMP_USERID";
	// End of Domestic Partner
	
	// Start of Short App Emp Fee Waiver
	public static final String confirmationTicketEmp = "select * from HR_STUDENT_WORKER_DATA where CLASS_CODE ='<<CLASS_CODE>>' and cwid='<<CWID>>'";
	public static final String confTicketLookupFields = "CWID,EMPL_RCD,EFFECTIVE_DATE,CURRENT_DATE,APPOINTMENT_END_DATE,FULL_NAME,LAST_NAME,FIRST_NAME,MIDDLE_NAME,ACTION,ACTION_REASON,DEPARTMENT_CODE,DEPARTMENT,CMS_POSITION_NUMBER,AGENCY,REPORTING_UNIT,CLASS_CODE,SERIAL_NUMBER,JOB_TITLE,COMPENSATION_RATE";
	// End of Short App Emp Fee Waiver
}