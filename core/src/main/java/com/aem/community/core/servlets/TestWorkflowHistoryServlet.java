package com.aem.community.core.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.Workflow;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Workflow History Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/fetchWorkflowHistory" })
public class TestWorkflowHistoryServlet extends SlingSafeMethodsServlet {
	private static final Logger logger = LoggerFactory.getLogger(TestWorkflowHistoryServlet.class);
	private static final long serialVersionUID = 1L;

	@Reference
	private transient ResourceResolverFactory resolverFactory;

	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		// Gson gson = new GsonBuilder().setPrettyPrinting().create();
		WorkflowSession graniteWorkflowSession = request.getResourceResolver().adaptTo(WorkflowSession.class);
		response.getWriter().println("Started listing history items\n");
		try {
			Workflow[] workflow = graniteWorkflowSession.getAllWorkflows();
			for (Workflow workflowInstance : workflow) {
				if (workflowInstance.getWorkflowModel().getTitle().equalsIgnoreCase("Student Course Withdrawal")) {
					List<HistoryItem> items = graniteWorkflowSession.getHistory(workflowInstance);
					// String json = gson.toJson(items); // converts to json
					items.forEach(item -> {
						try {
							Date a = item.getDate();
							response.getWriter().println(item.getDate());
							response.getWriter().println(item.getAction());
							response.getWriter().println(item.getWorkItem().getCurrentAssignee());
							response.getWriter().println(item.getWorkItem().getProgressBeginTime());
							response.getWriter().println(item.getWorkItem().getContentPath());
							response.getWriter().println(item.getWorkItem().getMetaDataMap().toString());
							
							//item.getWorkItem().getNode().
							response.getWriter().print("");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				}
			}
			response.getWriter().println("Completed listing history items");
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
	}
}
