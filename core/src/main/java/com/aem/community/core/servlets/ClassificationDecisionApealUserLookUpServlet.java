//package com.aem.community.core.servlets;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import javax.servlet.Servlet;
//import javax.servlet.ServletException;
//import javax.sql.DataSource;
//import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.api.SlingHttpServletResponse;
//import org.apache.sling.api.servlets.HttpConstants;
//import org.apache.sling.api.servlets.SlingAllMethodsServlet;
//import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.osgi.framework.Constants;
//import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.Reference;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.aem.community.util.ConfigManager;
////Add the DataSourcePool package
//import com.day.commons.datasource.poolservice.DataSourcePool;
//
///**
// * Servlet that writes some sample content into the response. It is mounted for
// * all resources of a specific Sling resource type. The
// * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
// * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
// */
//
//@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Career Development Plan",
//		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/classificationDecisionApealUserLookUp" })
//public class ClassificationDecisionApealUserLookUpServlet extends SlingSafeMethodsServlet{
//
//    private final static Logger logger = LoggerFactory.getLogger(ClassificationDecisionApealUserLookUpServlet.class);
//	private static final long serialVersionUID = 1L;
//
//	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse response)
//			throws ServletException, IOException {
//		Connection conn = null;
//		String userID = "";
//		JSONArray classificationDecisionAppealRequestDetails = null;
//		if (req.getParameter("userID") != null && req.getParameter("userID") != "") {
//			userID = req.getParameter("userID");
//			conn = getConnection();
//		}
//
//		if (conn != null) {
//			try {
//				logger.info("Connection Success=" + conn);
//				classificationDecisionAppealRequestDetails = getClassificationDecisionApealDetails(userID, conn, "classificationDecisionAppeal");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			response.setContentType("application/json");
//			response.setCharacterEncoding("UTF-8");
//			// Set JSON in String
//			response.getWriter().write(classificationDecisionAppealRequestDetails.toString());
//		}
//	}
//
//	public static JSONArray getClassificationDecisionApealDetails(String userID, Connection oConnection, String docType)
//			throws Exception {
//
//		ResultSet oRresultSet = null;
//		JSONObject classificationDecisionApealDetails;
//		JSONArray jArray = new JSONArray();
//
//        String userIDSQL = ConfigManager.getValue("classificationDecisionUserLookUp");
//        logger.info("The userID SQL is=" + userIDSQL);
//
//        String lookupFields = ConfigManager.getValue("classificationDecisionFields");
//        logger.info("The user LookUp Fields are=" + lookupFields);
//
//		String[] fields = lookupFields.split(",");
//
//        userIDSQL = userIDSQL.replaceAll("<<getUser_ID>>", userID);
//        logger.info("User ID is="+userIDSQL);
//		Statement oStatement = null;
//		try {
//
//			logger.info("inside try4");
//			oStatement = oConnection.createStatement();
//			oRresultSet = oStatement.executeQuery(userIDSQL);
//
//			while (oRresultSet.next()) {
//
//				classificationDecisionApealDetails = new JSONObject();
//				for (int i = 0; i < fields.length; i++) {
//					classificationDecisionApealDetails.put(fields[i], oRresultSet.getString(fields[i]));
//
//				}
//				jArray.put(classificationDecisionApealDetails);
//				logger.info("jArray=" + jArray);
//			}
//
//		} catch (Exception oEx) {
//			logger.info("Exception=" + oEx);
//			oEx.printStackTrace();
//
//		} finally {
//			try {
//				if (oConnection != null) {
//					oConnection.close();
//
//				}
//
//			} catch (Exception exp) {
//				exp.printStackTrace();
//			}
//		}
//
//		return jArray;
//	}
//
//	@Reference
//	private DataSourcePool source;
//
//	private Connection getConnection() {
//		DataSource dataSource = null;
//		Connection con = null;
//		try {
//			// Inject the DataSourcePool right here!
//			dataSource = (DataSource) source.getDataSource("frmmgrprod");
//			con = dataSource.getConnection();
//			logger.info("Connection=" + con);
//			return con;
//
//		} catch (Exception e) {
//			logger.info("Conn Exception=" + e);
//			e.printStackTrace();
//		} finally {
//			try {
//				if (con != null) {
//					logger.info("Conn Exec=");
//				}
//			} catch (Exception exp) {
//				logger.info("Finally Exec=" + exp);
//				exp.printStackTrace();
//			}
//		}
//		return null;
//	}
//
//}
