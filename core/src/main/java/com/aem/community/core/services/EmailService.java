package com.aem.community.core.services;

import java.util.List;

import com.aem.community.core.services.vo.*;

/**
 * A service interface for sending a generic template based Email Notification
 * 
 */
@org.osgi.annotation.versioning.ProviderType
public interface EmailService {

    /**
     * Construct an email based on a template and send it to one or more
     * recipients.
     * 
     * @param emailBean 
     * 
     * @return failureList containing list recipient's String addresses for which email sent failed
     */
    List<String> sendEmail(EmailServiceVO emailBean);
}
