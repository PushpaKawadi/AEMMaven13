package com.aem.community.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.EmailService;
import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.vo.EmailServiceVO;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/testEmail2" })
public class TestEmailServlet2 extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private EmailService emailService;

	@Reference
	private GlobalConfigService globalConfigService;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.debug("entered TestEmailServlet2 doGet method");
		PrintWriter out = response.getWriter();

		try {

			String toEmail = request.getParameter("toEmail");
			String ccEmail = request.getParameter("ccEmail");
			//String bccEmail = request.getParameter("bccEmail");
			String templatePath = request.getParameter("templatePath");

			EmailServiceVO emailVO = new EmailServiceVO();
			//emailVO.setAttachment(null);

			//List<String> bccList = new ArrayList<>();
			// bccList.add(bccEmail);
			// emailVO.setBccAddress(bccList);

			// List<String> ccList = new ArrayList<>();
			// ccList.add(ccEmail);
			// emailVO.setCcAddress(ccList);

			emailVO.addToAddress(StringUtils.isNotBlank(toEmail) ? toEmail : "ajeet.chhonkar@thoughtfocus.com");

			emailVO.addCcAddress(StringUtils.isNotBlank(ccEmail) ? ccEmail : "singhaj326@hotmail.com");

			emailVO.setToName("Test Email Recipient");
			emailVO.setFromAddress("arscworkflow@fullerton.edu");
			emailVO.setFromName("Manish Kumar Singh");
			emailVO.setSubject("Test Email From CSUF AEM Application");
			emailVO.setTemplatePath((StringUtils.isNotBlank(templatePath) ? templatePath
					: "/etc/notification/email/csuf/email-approve.txt"));

			emailVO.setUseCQGateway(false);
			
			Map<String, String> templateVaribles = new HashMap<>();
			templateVaribles.put("senderEmail", "csuf@fullerton.edu");
			templateVaribles.put("recipientName", emailVO.getToName());	
			
			emailVO.setTemplateVaribles(templateVaribles);

			log.debug("emailVO : ".concat(emailVO.toString()));

				
			// log.info("Instructor Email in SEND EMAIL Method: " + instructorEmail);
			// log.info("Chair Email in SEND EMAIL Method: " + chairEmail);
			// log.info("Instructor Name in SEND EMAIL Method: "+instName);
			// log.info("Course Number in SEND EMAIL Method: "+ CourseName);

			List<String> emailFailureList = emailService.sendEmail(emailVO);

			if (null != emailFailureList && emailFailureList.size() > 0) {
				out.write("Email sending failed to the recipients: ".concat(emailFailureList.toString()));
			} else if (null != emailFailureList && emailFailureList.size() == 0) {
				out.write("Email sent successfully to ".concat(toEmail));
			} else {
				out.write("Email sending failed");
			}

			//out.write("hello");
		} catch (Exception e) {
			log.debug("Exception in TestEmailServlet2 : ".concat(Arrays.toString(e.getStackTrace())));

		}

		log.debug("exit TestEmailServlet2 doGet method");
	}

}
