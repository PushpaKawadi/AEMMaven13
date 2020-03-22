package com.aem.community.core.servlets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
//import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import org.osgi.framework.Constants;
//import org.osgi.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.adobe.granite.taskmanagement.Filter;
import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.day.cq.wcm.foundation.List;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "= Get Support Docs", "sling.servlet.paths=/bin/user/getSelfEvalSupDoc" })
public class GetMPPSelfEvalSupDoc extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		 WorkflowSession wfSession = request.getResourceResolver().adaptTo(WorkflowSession.class);
		
		ResourceResolver resolver = wfSession.adaptTo(ResourceResolver.class);
		String id = "";
		String encodedFile = "";
		String payloadPath = "";
		String attachmentMimeType = "";
		Document doc = null;
		InputStream is = null;
		if (request.getParameter("instanceId") != null && request.getParameter("instanceId") != "" ) {
			id = request.getParameter("instanceId");
		}
		try {
			payloadPath = (String) wfSession.getWorkflow("id").getWorkflowData().getPayload();
			Resource xmlNode = resolver.getResource(payloadPath);
			Iterator<Resource> xmlFiles = xmlNode.listChildren();

			while (xmlFiles.hasNext()) {
				Resource attachmentXml = xmlFiles.next();
				// log.error("xmlFiles inside ");
				String filePath = attachmentXml.getPath();
				if (filePath.contains("Attachments")) {
					String attachmentsPath = "Attachments";
					// String attachmentsFilePath = payloadPath + "/" +
					// attachmentsPath + "/supportDoc1";
					String AttachmentsFilePath = payloadPath + "/" + attachmentsPath;

					Resource attachments = resolver.getResource(AttachmentsFilePath);

					if (attachments != null) {
						Iterator<Resource> attachmentFiles = attachments.listChildren();

						while (attachmentFiles.hasNext()) {
							Resource attachmentSupDoc = attachmentFiles.next();
							if (attachmentSupDoc.getResourceType().equalsIgnoreCase("sling:Folder")) {
								Iterator<Resource> innerAttachmentFiles = attachmentSupDoc.listChildren();
								while (innerAttachmentFiles.hasNext()) {
									Resource innerAttachmentSupDoc = innerAttachmentFiles.next();
									// log.info("name of doc inside="+innerAttachmentSupDoc);
									Path attachmentSource = Paths.get(innerAttachmentSupDoc.getPath());
									String attachmentDoc = innerAttachmentSupDoc.getPath().concat("/jcr:content");
									Node attachmentSubNode = resolver.getResource(attachmentDoc).adaptTo(Node.class);
									try {
										attachmentMimeType = Files.probeContentType(attachmentSource);
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									try {
										is = attachmentSubNode.getProperty("jcr:data").getBinary().getStream();
										byte[] bytes = IOUtils.toByteArray(is);
										// log.error("bytes="+bytes);
										encodedFile = Base64.getEncoder().encodeToString(bytes);

										
										if (encodedFile != null) {
											logger.error("Read Self Eval suppoting doc");
											Base64.Decoder decoder = Base64.getDecoder();
											byte[] decodedByteArray = decoder.decode(encodedFile);
											
											response.setContentType(attachmentMimeType);
											response.setCharacterEncoding("UTF-8");
											response.setContentLength(decodedByteArray.length);
											response.getOutputStream().write(decodedByteArray); 
																						
										}
									} catch (ValueFormatException e) {
										e.printStackTrace();
									} catch (PathNotFoundException e) {
										e.printStackTrace();
									} catch (RepositoryException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} finally {
										try {
											is.close();
										} catch (IOException e) {
											e.printStackTrace();
										}

									}
								}
							}

						}
					}
				}
			}
		} catch (WorkflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
					
					