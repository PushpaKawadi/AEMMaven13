
package com.aem.community.core.servlets;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.api.security.user.User;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Loggedin User Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/getLoggedUserDetails" })
public class GetLoggedInUserDetails extends SlingSafeMethodsServlet {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	ResourceResolver adminResolver = null;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		JSONObject userDetails = new JSONObject();
		ResourceResolver resolver = req.getResourceResolver();
		Session session = resolver.adaptTo(Session.class);
		String uid = null;
		String uname = null;
		String[] values;
		String username = null;
		final UserManager userManager = resolver.adaptTo(UserManager.class);
		User user = null;
		try {
			user = (User) userManager.getAuthorizable(session.getUserID());
			uid = session.getUserID();
			if(uid.equals("admin")) {
				uname = "Administrator";
			}else {
			uname = session.getNode(user.getPath()).getProperty("rep:fullname").getString();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if((!(uname.equals(null)) && uname != "")){
				if(uname.contains(",")) {
					values = uname.split(",");
					username = (values[1].replaceAll("\\s","")+" "+values[0]);
				}else {
					username = uname;
				}
				
			}else {
				username = uid;
			}
			
			
			userDetails.put("userId", uid);
			userDetails.put("userName", username);
			userDetails.put("Status", "Success");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (session != null) {
			session.logout();
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		logger.info("UserID Value=" + userDetails.toString());
		response.getWriter().write(userDetails.toString());
	}

}