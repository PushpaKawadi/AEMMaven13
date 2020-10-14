
package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.community.core.services.JDBCConnectionHelperService;
import com.aem.community.util.CSUFConstants;
import com.aem.community.util.CSUFHelper;
import com.aem.community.util.ConfigManager;
//Add the DataSourcePool package
import com.day.commons.datasource.poolservice.DataSourcePool;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Get Manager Details",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getManagerDetails" })
public class CSUFGetManagerDetailsServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFGetManagerDetailsServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String deptid = "";
		String cwid = "";
		String union_cd = "";
		String managerUserId = "";
		String managerEmailId = "";
		JSONObject managerDetails;
		JSONArray jArray = new JSONArray();
		if (req.getParameter("deptid") != null && req.getParameter("deptid") != "" && req.getParameter("cwid") != null
				&& req.getParameter("cwid") != "" && req.getParameter("union_cd") != null
				&& req.getParameter("union_cd") != "") {
			deptid = req.getParameter("deptid");
			cwid = req.getParameter("cwid");
			union_cd = req.getParameter("union_cd");
			logger.info("Got deptid =" + deptid);
			logger.info("Got EmpID =" + cwid);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				managerDetails = new JSONObject();
				logger.info("Connection Success=" + conn);
				managerUserId = CSUFHelper.getManagerDetails(conn, cwid, union_cd, deptid);
				managerEmailId = CSUFHelper.getEmailIDBasedOnUserID(conn, managerUserId);
				managerDetails.put("MANAGER_USER_ID", managerUserId);
				managerDetails.put("MANAGER_EMAIL_ID", managerEmailId);
				jArray.put(managerDetails);
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jArray.toString());
		}

	}

}