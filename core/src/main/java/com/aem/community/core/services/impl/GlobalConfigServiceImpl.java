package com.aem.community.core.services.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.GlobalConfigService;

@Component(service = GlobalConfigService.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Global Config Service" })

public class GlobalConfigServiceImpl implements GlobalConfigService {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String SUB_SERVICE_NAME = "datawrite";

	private ResourceResolver resolver = null;

	private Session session = null;

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public ResourceResolver getResourceResolver() throws LoginException {
		return resolver = resolverFactory.getServiceResourceResolver(
				Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, (Object) SUB_SERVICE_NAME));
	}

	@Override
	public Session getAdminSession() {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE_NAME);
		try {
			resolver = resolverFactory.getServiceResourceResolver(param);

			session = resolver.adaptTo(Session.class);
			if (null != session) {
				return session;
			} else
				throw new Exception("Fatal Exception:: GlobalConfigServiceImpl getAdminSession : session is null");
		} catch (Exception e) {
		}
		return null;
	}
}
