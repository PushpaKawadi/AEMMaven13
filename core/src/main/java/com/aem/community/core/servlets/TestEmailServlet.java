package com.aem.community.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;

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
		"sling.servlet.paths=" + "/bin/testEmail" })
public class TestEmailServlet extends SlingAllMethodsServlet {

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

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServerException, IOException {
		log.debug("entered TestEmailServlet doGet method");
		PrintWriter out = response.getWriter();

		try {
			String toEmail = request.getParameter("toEmail");
			String ccEmail = request.getParameter("ccEmail");
			String bccEmail = request.getParameter("bccEmail");
			String templatePath = request.getParameter("templatePath");

			EmailServiceVO emailVO = new EmailServiceVO();
			emailVO.setAttachment(null);

			List<String> bccList = new ArrayList<>();
			bccList.add(bccEmail);
			emailVO.setBccAddress(bccList);

			List<String> ccList = new ArrayList<>();
			ccList.add(ccEmail);
			emailVO.setCcAddress(ccList);

			emailVO.addToAddress(toEmail);
			emailVO.setToName("Test Email Recipient");
			emailVO.setFromAddress("ajeet.chhonkar@thoughtfocus.com");
			emailVO.setFromName("Ajeet Chhonkar");
			emailVO.setSubject("Test Email Subject");
			emailVO.setTemplatePath((StringUtils.isNotBlank(templatePath) ? templatePath
					: "/etc/notification/email/recertification/remove-supervisee-email-template.html"));

			emailVO.setUseCQGateway(false);

			log.debug("emailVO : ".concat(emailVO.toString()));

			List<String> emailFailureList = emailService.sendEmail(emailVO);

			if (null != emailFailureList && emailFailureList.size() > 0) {
				out.write("Email sending failed to the recipients: ".concat(emailFailureList.toString()));
			} else if (null != emailFailureList && emailFailureList.size() == 0) {
				out.write("Email sent successfully");
			} else {
				out.write("Email sending failed");
			}
		} catch (Exception e) {
			log.debug("Exception in TestEmailServlet : ".concat(Arrays.toString(e.getStackTrace())));

		}

		log.debug("exit TestEmailServlet doGet method");
	}

}
