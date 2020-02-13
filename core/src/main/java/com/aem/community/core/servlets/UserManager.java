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

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.GlobalConfigService;
import com.aem.community.core.services.JDBCConnectionHelperService;
import com.aem.community.util.ConfigManager;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Loggedin User Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/getLoggedUserId" })
public class UserManager extends SlingSafeMethodsServlet {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	
	@Reference
	private GlobalConfigService globalService;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		JSONObject userValues = null;
		try {
			userValues = getCurrentUserId(req);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		logger.info("UserID Value=" + userValues.toString());
		response.getWriter().write(userValues.toString());
	}

	public JSONObject getCurrentUserId(SlingHttpServletRequest request) throws JSONException {
		JSONObject userDetails = new JSONObject();
		ResourceResolver resolver = request.getResourceResolver();
		Session session = resolver.adaptTo(Session.class);
		//Session session = globalService.getAdminSession();
		String userId = session.getUserID();
		//logger.info("userDetails=" + userId);
		userDetails.put("userId", userId);
		userDetails.put("Status", "Success");
		if(session != null){
			session.logout();
		}
		return userDetails;

	}

}