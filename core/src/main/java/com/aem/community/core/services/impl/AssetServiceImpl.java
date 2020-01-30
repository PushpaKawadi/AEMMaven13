package com.aem.community.core.services.impl;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.AssetService;
import com.aem.community.core.services.GlobalConfigService;

@Component(service = AssetService.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Generic Asset Handler Service" })
public class AssetServiceImpl implements AssetService {

	@Reference
	private transient GlobalConfigService globalConfigService;
	
	private static final Logger log = LoggerFactory.getLogger(AssetServiceImpl.class);

	@Override
	public InputStream readCRXAsset(String assetPath) {
		Session session = null;
		try {
			session = globalConfigService.getAdminSession();
			Node jcnode = session.getNode(assetPath);
			return jcnode.getProperty("jcr:data").getBinary().getStream();
		} catch (Exception e) {
			log.error(ArrayUtils.toString(e.getStackTrace()));
		} finally {
			if (null != session && session.isLive()) {
				session.logout();
			}
		}
		return null;
	}
}
