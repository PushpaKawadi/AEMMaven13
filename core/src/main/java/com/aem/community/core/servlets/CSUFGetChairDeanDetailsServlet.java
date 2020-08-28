package com.aem.community.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
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

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Chair Dean Info Lookup Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/getChairDeanInfo" })
public class CSUFGetChairDeanDetailsServlet extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory.getLogger(CSUFGetChairDeanDetailsServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	public void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		String deptId = "";
		JSONArray chairDeanInfoDetails = null;
		if (req.getParameter("dept_id") != null && req.getParameter("dept_id") != "") {
			deptId = req.getParameter("dept_id");
			logger.info("Got dept_id =" + deptId);
			conn = jdbcConnectionService.getDocDBConnection();
		}
		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				chairDeanInfoDetails = ChairDeanInfoDetailsGetData(conn, deptId);

			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(chairDeanInfoDetails.toString());
		}
	}

	public static JSONArray ChairDeanInfoDetailsGetData(Connection oConnection, String dept_id) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject chairDeanInfoObj;
		JSONArray jArray = new JSONArray();
		String chairDeanInfoSQL = CSUFConstants.getChairDeanInfoSQL;
		chairDeanInfoSQL = chairDeanInfoSQL.replaceAll("<<dept_id>>", dept_id);
		logger.info("ChairInfo SQL=" + chairDeanInfoSQL);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(chairDeanInfoSQL);
			while (oRresultSet.next()) {
				chairDeanInfoObj = new JSONObject();
				String chairUid = oRresultSet.getString("CHAIR_USERID");
				chairDeanInfoObj.put("CHAIR_USERID", chairUid);
				String chairName = oRresultSet.getString("CHAIR_NAME");
				chairDeanInfoObj.put("CHAIR_NAME", chairName);
				String chairEmail = oRresultSet.getString("CHAIR_EMAIL");
				chairDeanInfoObj.put("CHAIR_EMAIL", chairEmail);
				
				String deanUid = oRresultSet.getString("DEAN_USERID");
				chairDeanInfoObj.put("DEAN_USERID", deanUid);
				String deanName = oRresultSet.getString("DEAN_NAME");
				chairDeanInfoObj.put("DEAN_NAME", deanName);
				String deanEmail = oRresultSet.getString("DEAN_EMAIL");
				chairDeanInfoObj.put("DEAN_EMAIL", deanEmail);			
				
				jArray.put(chairDeanInfoObj);
			}
		} catch (Exception oEx) {
			logger.error("Exception=" + oEx);
			oEx.printStackTrace();
			chairDeanInfoObj = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();
					logger.info("Connection closed");
				}
			} catch (Exception e) {
				logger.error("Exception in CSUF=" + e.getMessage());
				e.getStackTrace();
			}
		}
		return jArray;
	}
}