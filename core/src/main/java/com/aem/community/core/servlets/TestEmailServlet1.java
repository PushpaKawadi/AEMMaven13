/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.EmailService;
import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.vo.EmailServiceVO;
import com.day.cq.mailer.MailService;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.resourceTypes=" + "AEMMaven13/components/structure/page", "sling.servlet.extensions=" + "txt" })
public class TestEmailServlet1 extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Reference
	private GlobalConfigService globalConfigService;

	@Reference
	private MailService mailService;

	@Reference
	private EmailService emailService;

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.debug("entered TestEmailServlet1 doGet method");
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
			emailVO.setFromAddress("manish.08.hbti@gmail.com");
			emailVO.setFromName("Manish Kumar Singh");
			emailVO.setSubject("Test Email From CSUF AEM Application");
			emailVO.setTemplatePath((StringUtils.isNotBlank(templatePath) ? templatePath
					: "/etc/notification/email/csuf/sample-email-template.html"));

			emailVO.setUseCQGateway(false);
			
			Map<String, String> templateVaribles = new HashMap<>();
			templateVaribles.put("senderEmail", "manish.08.hbti@gmail.com");
			templateVaribles.put("recipientName", emailVO.getToName());	
			
			emailVO.setTemplateVaribles(templateVaribles);

			log.debug("emailVO : ".concat(emailVO.toString()));

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
			log.debug("Exception in TestEmailServlet : ".concat(Arrays.toString(e.getStackTrace())));

		}

		log.debug("exit TestEmailServlet1 doGet method");
	}
}
