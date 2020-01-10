package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Reminder Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/sendReminderEmail" })
public class ReminderServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(ReminderServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private MessageGatewayService messageGatewayService;

	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		String emailAddress = null;
		String professorName = null;
		Connection conn = null;
		WorkflowSession graniteWorkflowSession = request.getResourceResolver().adaptTo(WorkflowSession.class);
		response.getWriter().write("Started Sending Email Reminder \n");
		try {
			conn = getConnection();
			if (conn != null) {
				WorkItem[] workItems = graniteWorkflowSession.getActiveWorkItems();
				for (WorkItem wItem : workItems) {
					if (wItem.getWorkflow().getWorkflowModel().getTitle()
							.equalsIgnoreCase("Student Course Withdrawal")) {
						for (Map.Entry<String, Object> entry : wItem.getWorkflowData().getMetaDataMap().entrySet()) {
							if (entry.getKey().matches("CWID")) {
								logger.error("CWID---------------------=" + conn);
								String cwid = entry.getValue().toString();
								String pattern = "yyyy-MM-dd";
								SimpleDateFormat format = new SimpleDateFormat(pattern);
								String today = format.format(new Date());
								Date dateToday = format.parse(today);

								Date progressDate = wItem.getProgressBeginTime();
								String prgDate = format.format(progressDate);
								Date progressToday = format.parse(prgDate);

								long difference = dateToday.getTime() - progressToday.getTime();
								float daysBetween = (difference / (1000 * 60 * 60 * 24));
								int noOfDays = (int) daysBetween;
								//String noOfDays = ConfigManager.getValue("emailReminderDays");
								//int days = Integer.valueOf(noOfDays);
								if (noOfDays > 6) {
									//String userGuide = "<a href=\"https://csuf.screenstepslive.com/s/12867/m/90548/l/1136007-faculty-withdrawal?token=aXghvkrtakWvPBGKAFxVh-J6KxAJcjjZ\" target=\"_blank\" style=\"color: blue;font-size : 20px;\">
									String link = "<a href=\"http://erpobistg:4502/aem/inbox\" target=\"_blank\">View Form</a>";
									
									String userGuide = "<a href=\"https://csuf.screenstepslive.com/s/12867/m/90548/l/1136007-faculty-withdrawal?token=aXghvkrtakWvPBGKAFxVh-J6KxAJcjjZ\" target=\"_blank\">User Guide</a>";

									String currAssignee = wItem.getCurrentAssignee();
									String emailAssignee = currAssignee.concat("@exchange.fullerton.edu");
									emailAddress = "pushpa.kawadi@thoughtfocus.com";
									if (wItem.getNode().getTitle().trim().matches("Instructor Review")) {
										try {
											professorName = getInstructorName(cwid, conn, currAssignee);
											logger.info("inName=" + professorName);
											MessageGateway<Email> messageGateway;
											Email email = new SimpleEmail();
											email.setFrom("csuf@fullerton.edu");
											email.addTo(emailAddress);
											email.addCc("yashovardhan.jayaram@thoughtfocus.com");
											email.addBcc("swathi.kumari@thoughtfocus.com");

											email.setSubject("Reminder: Student Course Withdrawal "
													+ wItem.getWorkflowData().getMetaDataMap().get("CWID"));
											String emailBody = "Dear " + professorName + ",</br>" + "</br>"
													+ "Please review the course withdrawal request from a student in one of your classes by clicking on the link below. Once you have made a decision to approve or deny the request, it will continue through the workflow process to the Department Chair.</br>" + "</br>"
													+ "Should you have any questions about this process in terms of procedures, please contact Enrollment Services at (657)-278-5202.</br>" + "</br>"
													+ "Please contact the IT Help Desk if you have any issues reviewing the withdrawal form at 657-278-7777 or helpdesk@fullerton.edu</br>" + "</br>"
													+ "For instructions on completing this request, please review the faculty and staff user guide:</br>"+userGuide+"</br>" + "</br>"
													+ "We appreciate your timely attention to this matter</br>"
													+ link + "</br>" + "</br> Sincerely, </br>"
													+ "Registration and Records</br>" + "</br>";
													//+"This is an automatically generated email. Please do not reply to this email</br>" + "</br>";

											email.setContent(emailBody, "text/html");
											messageGateway = messageGatewayService.getGateway(Email.class);
											messageGateway.send((Email) email);

										} catch (Exception e) {
											logger.error("Exception in Reminder Email=" + e.getMessage());
											e.printStackTrace();
										}
									}
									if (wItem.getNode().getTitle().trim().matches("Chair Review")) {
										try {
											logger.info("Here in execute method");
											professorName = getChairName(conn, currAssignee);
											MessageGateway<Email> messageGateway;
											Email email = new SimpleEmail();
											email.setFrom("csuf@fullerton.edu");
											email.addTo(emailAddress);
											email.addCc("yashovardhan.jayaram@thoughtfocus.com");
											email.addBcc("swathi.kumari@thoughtfocus.com");
											email.setSubject("Reminder: Student Course Withdrawal "
													+ wItem.getWorkflowData().getMetaDataMap().get("CWID"));
											String emailBody = "Dear Dr." + professorName + ",</br>" + "</br>"
													+ "Please review the course withdrawal request by clicking the link below. It has already been reviewed by the instructor of the class. Once you have made a decision to approve or deny the request, the form will continue through the workflow process to the Records and Registration Area (ARSC). There, an email will be sent to the student as to the final decision.</br>" + "</br>"
													+ "Should you have any questions about this process in terms of procedures, please contact Enrollment Services at (657)-278-5202.</br>"+"</br>"
													+ "Please contact the IT Help Desk if you have any technical issue at 657-278-7777 or helpdesk@fullerton.edu</br>" + "</br>"
													+ "For instructions on completing this request, please review the faculty and staff user guide:</br>"+userGuide+"</br>" + "</br>"
													+ "We appreciate your timely attention to this matter</br>"
													+ link +"</br>" + "</br> Sincerely, </br>"
													+ "Registration and Records</br>" + "</br>";
													//+"This is an automatically generated email. Please do not reply to this email</br>" + "</br>";

											email.setContent(emailBody, "text/html");
											messageGateway = messageGatewayService.getGateway(Email.class);
											messageGateway.send((Email) email);

										} catch (Exception e) {
											logger.error("Exception in Reminder Email=" + e.getMessage());
											e.printStackTrace();
										}
									}
								}
							}

						} // if condition
					}
				}
			}
			response.getWriter().write("Completed Sending Email Reminder");
		} catch (Exception e) {
			logger.error("Exception=" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("SQL Exception=" + e.getMessage());
					e.printStackTrace();
				}
			}
			graniteWorkflowSession.logout();
		}

	}

	public String getInstructorName(String cwid, Connection oConnection, String instUserId) throws Exception {

		logger.error("Inside getInstructorName=" + instUserId);

		ResultSet oRresultSet = null;
		String instName = null;
		Statement oStatement = null;
		try {
			// String studentCourseInfoSQL = "Select * from AR_COURSE_WITHDRAWAL
			// where LOWER(student_userid) = LOWER('<<userId>>') and STRM =
			// '2197'";

			String studentCourseInfoSQL = "Select distinct instr_name from AR_COURSE_WITHDRAWAL where CWID = '<<CWID>>' and LOWER(instr_userid)=LOWER('<<instr_userid>>') and STRM = '2197'";
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll("<<CWID>>", cwid);
			studentCourseInfoSQL = studentCourseInfoSQL.replaceAll("<<instr_userid>>", instUserId);
			logger.error("Stmt=" + studentCourseInfoSQL);

			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(studentCourseInfoSQL);
			logger.error("oRresultSet=" + oRresultSet);
			// logger.error("oConnection=" + oConnection);
			while (oRresultSet.next()) {
				logger.error("Result=" + oRresultSet.getString("instr_name"));
				instName = oRresultSet.getString("instr_name");
				instName = (instName).substring(0, (instName).indexOf(","));
				return instName;
			}

		} catch (Exception oEx) {
			logger.error("Error=" + oEx.getMessage());
			oEx.printStackTrace();

		}
		/*
		 * finally { try { if (oStatement != null) oStatement.close();
		 * oRresultSet.close(); if (oConnection != null) { oConnection.close();
		 * } } catch (Exception exp) { exp.getStackTrace();
		 * 
		 * } }
		 */

		return instName;
	}

	public String getChairName(Connection oConnection, String EMP_USERID) throws Exception {

		logger.error("Inside getChairName=" + EMP_USERID);

		ResultSet oRresultSet = null;
		String instName = null;
		Statement oStatement = null;
		try {
			// String studentCourseInfoSQL = "Select * from AR_COURSE_WITHDRAWAL
			// where LOWER(student_userid) = LOWER('<<userId>>') and STRM =
			// '2197'";
			String chairInfo = "Select distinct EMPNAME from AR_Course_Chair_Info where  LOWER(EMP_USERID)=LOWER('<<EMP_USERID>>')";
			chairInfo = chairInfo.replaceAll("<<EMP_USERID>>", EMP_USERID);
			logger.error("Stmt=" + chairInfo);

			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(chairInfo);
			logger.error("oRresultSet=" + oRresultSet);
			// logger.error("oConnection=" + oConnection);
			while (oRresultSet.next()) {
				logger.error("Result=" + oRresultSet.getString("EMPNAME"));
				instName = oRresultSet.getString("EMPNAME");
				instName = (instName).substring(0, (instName).indexOf(","));
				return instName;
			}

		} catch (Exception oEx) {
			logger.error("Error=" + oEx.getMessage());
			oEx.printStackTrace();

		}
		return instName;
	}

	@Reference
	private DataSourcePool source;

	private Connection getConnection() {
		logger.info("Inside Get Connection");

		DataSource dataSource = null;
		Connection con = null;
		try {
			// Inject the DataSourcePool right here!
			dataSource = (DataSource) source.getDataSource("docmgrprod");
			con = dataSource.getConnection();
			return con;

		} catch (Exception e) {
			logger.error("Conn Exception=" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
