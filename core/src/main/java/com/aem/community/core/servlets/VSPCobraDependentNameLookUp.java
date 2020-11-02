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
//Add the DataSourcePool package

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=Cobra Final Notice Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/getVSPCobraNameLookup" })
public class VSPCobraDependentNameLookUp extends SlingSafeMethodsServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(VSPCobraDependentNameLookUp.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private JDBCConnectionHelperService jdbcConnectionService;

	protected void doGet(SlingHttpServletRequest req,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection conn = null;
		String dependentName = "";
		String cwid = "";
		JSONArray cobraFinalDetails = null;

		if ((req.getParameter("cwid") != null && req.getParameter("cwid") != "")
				&& (req.getParameter("dependentName") != null && req
						.getParameter("dependentName") != "")) {
			dependentName = req.getParameter("dependentName");
			cwid = req.getParameter("cwid");
			logger.info("dependentName =" + dependentName);
			logger.info("EmpID =" + cwid);
			conn = jdbcConnectionService.getFrmDBConnection();
		}

		if (conn != null) {
			try {
				logger.info("Connection Success=" + conn);
				cobraFinalDetails = getVSPCobraDependantDetails(cwid,
						dependentName, conn);
				logger.info("emplEvalDetails =" + cobraFinalDetails);

			} catch (Exception e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Set JSON in String
			response.getWriter().write(cobraFinalDetails.toString());
		}
	}

	/**
	 * 
	 * @param cwid
	 * @param oConnection
	 * @param docType
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getVSPCobraDependantDetails(String cwid,
			String dependentName, Connection oConnection) throws Exception {
		ResultSet oRresultSet = null;
		JSONObject cobraFinalNoticeDetails;
		JSONArray jArray = new JSONArray();

		String emplIDSQL = CSUFConstants.vspCobraDepLookup;
		String lookupFields = CSUFConstants.vspCobraDepFields;

		String[] fields = lookupFields.split(",");

		emplIDSQL = emplIDSQL.replaceAll("<<DependentName>>", dependentName);
		emplIDSQL = emplIDSQL.replaceAll("<<SSN>>", cwid);
		Statement oStatement = null;
		try {
			oStatement = oConnection.createStatement();
			oRresultSet = oStatement.executeQuery(emplIDSQL);
			while (oRresultSet.next()) {
				cobraFinalNoticeDetails = new JSONObject();
				for (int i = 0; i < fields.length; i++) {
					cobraFinalNoticeDetails.put(fields[i],
							oRresultSet.getString(fields[i]));
					logger.info("employeeEvalDetails ="
							+ cobraFinalNoticeDetails);
				}
				jArray.put(cobraFinalNoticeDetails);
			}
			logger.info("oRresultSet =" + jArray);
		} catch (Exception oEx) {
			logger.info("Exception=" + oEx);
			oEx.printStackTrace();
			cobraFinalNoticeDetails = null;
		} finally {
			try {
				if (oConnection != null) {
					oConnection.close();

				}

			} catch (Exception exp) {
				logger.info("Exception=" + exp.getMessage());

			}
		}

		return jArray;
	}

}