package com.aem.community.core.services;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface GlobalConfigService {

	ResourceResolver getResourceResolver() throws LoginException;
	Session getAdminSession();

}