package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
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
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Staff Reminder Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/sendStaffEvalReminderEmail" })
public class CSUFStaffPerfEvalReminderServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFStaffPerfEvalReminderServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private MessageGatewayService messageGatewayService;

	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		String emailAddress = null;
		String professorName = null;
		Connection conn = null;
		WorkflowSession graniteWorkflowSession = request.getResourceResolver().adaptTo(WorkflowSession.class);
		response.getWriter().write("Started Sending Email Reminder \n");
		try {

			WorkItem[] workItems = graniteWorkflowSession.getActiveWorkItems();
			for (WorkItem wItem : workItems) {
				if (wItem.getWorkflow().getWorkflowModel().getTitle()
						.contains("Staff Performance")) {
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
					if (noOfDays >= 0) {

						String currAssignee = wItem.getCurrentAssignee();
						try {
							if (currAssignee.equalsIgnoreCase("admin")
									|| currAssignee.equalsIgnoreCase("administrator")
									|| currAssignee.equalsIgnoreCase("workflow-administrators")) {
								//Modify email address for prod release 
								emailAddress = "swathi.thoughtfocus@gmail.com";
							}else {
								emailAddress = wItem.getWorkflow().getMetaDataMap().get("employeeEmail").toString();
								logger.info("Current emailAddress===============================" + emailAddress);
								//Comment line number 79 for prod release 
								emailAddress = "swathi.kumari@thoughtfocus.com";
							}
						} catch (Exception ex) {
							logger.error("Exception while setting current assignee=" + ex.getMessage());
							ex.printStackTrace();
						}				
						
						if (wItem.getNode().getTitle().trim().matches("Employee Review")) {
							logger.info("The recepient is=============================== " + emailAddress);
							try {
								String fName = "";
								String lName = "";
								String timeline = "";
								String cba = "";
								lName = wItem.getWorkflow().getMetaDataMap().get("StaffLastName").toString();
								fName = wItem.getWorkflow().getMetaDataMap().get("StaffFirstName").toString();
								timeline = wItem.getWorkflow().getMetaDataMap().get("timeline").toString();
								cba = timeline.substring(timeline.length() - 1);
								if (cba.equals("W")) {
									cba = "working";
								}
								if (cba.equals("C")) {
									cba = "calendar";
								}

								timeline = timeline.replaceAll("([A-Z])", "") + " " + cba;
								professorName = fName.concat(" ".concat(lName));
								logger.info("professorName=" + professorName);
								String emailSubject = "REMINDER: "+wItem.getWorkflow().getWorkflowModel().getTitle();
								MessageGateway<Email> messageGateway;
								Email email = new SimpleEmail();
								email.setFrom("csuf@fullerton.edu");
								email.addTo(emailAddress);
								//Modify BCC address for prod release
								email.addBcc("swathi.thoughtfocus@gmail.com");
								email.setSubject(emailSubject);
								String emailBody = "<!DOCTYPE html><html><head><title></title></head><body>"
										+ "<img src=https://content.screencast.com/users/CSUF-test-account/folders/images/media/976d1a51-9d37-45e2-ad8b-311222535c18/CSUF_HRDI_Email_Logo.png>"
										+ "<h3 style=\"text-align: center;\">Evaluation Pending Review</h3>"
										+ "<p>Dear " + professorName + ",</p>"
										+ "<p>You have a performance evaluation pending for your review and acknowledgment.</p>"
										+ "<p><b>How to Review and Acknowledge the evaluation</b></p>" + "<ul>"
										+ "<li>Click here to <a href=\"https://aemformsprd.fullerton.edu/aem/inbox.html\" target=\"_blank\" style=\"color: black;font-weight: bold;\">View AEM Evaluation Form</a></li>"
										+ "<li>Or, log on to the <a href=\"https://my.fullerton.edu/Portal\" target=\"_blank\" style=\"color: blue;font-size : 16px;\">CSUF portal</a> and go to Titan Online. Search for AEM or Adobe in the search box on the left-hand side of the campus main portal page. Then click the link for Adobe Experience Manager (AEM) Inbox, Or click this <a href=\"https://aemformsprd.fullerton.edu/aem/inbox.html\" target=\"_blank\" style=\"color: blue;font-size : 16px;\">link</a> to be taken directly to the AEM Inbox</li>"
										+ "<li>Select the form from the AEM inbox and then click Open</li>"
										+ "<li>Review the form and add Comment (optional) under <b>Signature and Acknowledgement</b> section. Click Agree or Disagree (at the left-hand top of the page) to move the form through the workflow</li>"
										+ "<li>Please complete the review within "
										+ timeline + " days</li>"
										+ "<li>For more instructions on how to review your evaluation, please review the <a href=\"https://csuf.screenstepslive.com/s/12867/m/90548/l/1244991-mpp-performance-evaluation-form?token=4bngD-EZoL_6Sl6-2nhhiQLLChZZyPx6\" target=\"_blank\" style=\"color: black;font-size : 16px;font-style:italic;font-weight: bold; \">Staff Evaluation handbook</a></li>"
										+ "</ul>"
										+ "<p>Should you have any questions about this process in terms of procedures, please contact Labor and employee relations at HRDIevaluations@fullerton.edu.</p>"
										+ "<p>Please contact the IT Help Desk if you have any technical issue at 657-278-7777 or helpdesk@fullerton.edu.</p>"
										+ "<p>Sincerely,</p>" + "<p>Registration and Records</p>"
										+ "<p><span style=\"font-size: 12px; font-weight: normal; font-style: italic; color: #919191;\">This is an automatically generated email. Please do not reply to this email.</span></p>"
										+ "</body>" + "</html>";
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

}
