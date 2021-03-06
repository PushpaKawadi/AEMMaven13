package com.aem.community.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.day.cq.mailer.MailService;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/testEmailWithEmbeddedImage" })
public class TestEmailWithEmbeddedImageServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Default log. */
	protected final transient Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private transient EmailService emailService;

	@Reference
	private transient GlobalConfigService globalConfigService;

	@Reference
	private transient MailService mailService;

	private static final transient String IMAGE_PATH = "/content/dam/csu/CSUF_Mailer_logo.gif";
    private static final transient String TEMPLATE_PATH = "/etc/notification/email/csufTemplates/sample-email-template1.html";    

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.debug("entered TestEmailWithEmbeddedImageServlet doGet method");
		PrintWriter out = response.getWriter();

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
		emailVO.setFromAddress("manish.08.hbti@gmail.com");
		emailVO.setFromName("Ajeet Singh Chhonkar");
		emailVO.setSubject("Test Email From CSUF AEM Application");
		emailVO.setTemplatePath((StringUtils.isNotBlank(templatePath) ? templatePath : TEMPLATE_PATH));
		emailVO.setStartTLS(true);

		emailVO.setUseCQGateway(false);

		Map<String, String> templateVaribles = new HashMap<>();
		templateVaribles.put("senderEmail", "manish.08.hbti@gmail.com");
		templateVaribles.put("recipientName", emailVO.getToName());
		emailVO.setTemplateVaribles(templateVaribles);
		
		// If an image needs to be embedded with email body
		emailVO.setEmbeddedImage(true);
		emailVO.setEmbeddedImagePath(IMAGE_PATH);
		emailVO.setEmbeddedImageDescription("CSUF Logo");

		List<String> emailFailureList = emailService.sendEmail(emailVO);

		if (null != emailFailureList && !emailFailureList.isEmpty()) {
			out.write("Email sending failed to the recipients: ".concat(emailFailureList.toString()));
		} else if (null != emailFailureList && emailFailureList.isEmpty()) {
			out.write("Email sent successfully to ".concat(toEmail));
		} else {
			out.write("Email sending failed");
		}

		log.debug("exit TestEmailWithEmbeddedImageServlet doGet method");
	}
}
