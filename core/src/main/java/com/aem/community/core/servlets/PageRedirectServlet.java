package com.aem.community.core.servlets;

import com.day.cq.wcm.api.WCMMode;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Redirection Servlet",
                "sling.servlet.extensions=" + "html",
                "sling.servlet.selectors=" + "redirect",
                "sling.servlet.resourceTypes=" + "cq/Page",
                Constants.SERVICE_RANKING + "=700"
        })
public class PageRedirectServlet
        extends SlingSafeMethodsServlet {

    private Set<String> excludedResourceTypes;
    private static Logger LOG = LoggerFactory.getLogger(PageRedirectServlet.class);
    private static final String WCM_MODE_PARAM = "wcmmode";

    @Activate
    protected void activate(Map<String, Object> properties) {
        String[] excludedResourceTypesArray = PropertiesUtil.toStringArray(properties.get("excluded.resource.types"));
        this.excludedResourceTypes = new HashSet();
        if (!ArrayUtils.isEmpty(excludedResourceTypesArray)) {
            Collections.addAll(this.excludedResourceTypes, excludedResourceTypesArray);
        }
    }

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        Resource resource = request.getResource();
        Resource contentResource = resource.getChild("jcr:content");
        if (contentResource != null) {
            String redirectTarget = getRedirectTarget(contentResource);
            String redirectType = getRedirectType(contentResource);
            if ((isRedirectRequest(request, redirectTarget)) && (!isExcludedResourceType(contentResource))) {
                if (!isExternalRedirect(redirectTarget)) {
                    redirectTarget = resource.getResourceResolver().map(request, redirectTarget) + ".html";
                }
                redirectTarget = appendWcmModeQueryParameter(request, redirectTarget);
                LOG.debug("Redirecting page {} to target {}", resource.getPath(), redirectTarget);
                if (redirectType.equals("301")) {
                    response.setStatus(301);
                    response.setHeader("Location", redirectTarget);
                } else
                    response.sendRedirect(redirectTarget);
                return;
            }
        }
        RequestDispatcherOptions requestDispatcherOptions = new RequestDispatcherOptions();
        String selectorString = request.getRequestPathInfo().getSelectorString();
        selectorString = StringUtils.replace(selectorString, "redirect", "");
        requestDispatcherOptions.setReplaceSelectors(selectorString);

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(contentResource, requestDispatcherOptions);
        if (requestDispatcher != null) {
            requestDispatcher.include(request, response);
        }
    }

    private boolean isExcludedResourceType(Resource contentResource) {
        for (String excludedResourceType : this.excludedResourceTypes) {
            if (contentResource.isResourceType(excludedResourceType)) {
                return true;
            }
        }
        return false;
    }

    private String appendWcmModeQueryParameter(SlingHttpServletRequest request, String redirectTarget) {
        if (isModeDisabledChangeRequest(request)) {
            redirectTarget = redirectTarget + (redirectTarget.contains("?") ? "&" : "?") + "wcmmode" + "=disabled";
        }
        return redirectTarget;
    }

    private boolean isModeDisabledChangeRequest(SlingHttpServletRequest request) {
        boolean isModeChangeRequest = false;
        String modeChange = request.getParameter("wcmmode");
        if (StringUtils.equalsIgnoreCase(modeChange, WCMMode.DISABLED.name())) {
            isModeChangeRequest = true;
        }
        return isModeChangeRequest;
    }

    private boolean isExternalRedirect(String redirectTarget) {
        boolean externalRedirect = false;
        try {
            URL url = new URL(redirectTarget);
            String protocol = url.getProtocol();
            if (StringUtils.isNotBlank(protocol)) {
                externalRedirect = true;
            }
        } catch (MalformedURLException e) {
            return false;
        }
        return externalRedirect;
    }

    private String getRedirectTarget(Resource resource) {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        String redirectTarget = "";
        if (valueMap != null) {
            redirectTarget = (String) valueMap.get("cq:redirectTarget", "");
        }
        return redirectTarget;
    }

    private String getRedirectType(Resource resource) {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        String redirectTarget = "";
        if (valueMap != null) {
            redirectTarget = valueMap.get("redirectType", "");
        }
        return redirectTarget;
    }

    private boolean isRedirectRequest(SlingHttpServletRequest request, String redirectTarget) {
        WCMMode wcmMode = WCMMode.fromRequest(request);
        return (StringUtils.isNotEmpty(redirectTarget)) && (wcmMode.equals(WCMMode.DISABLED));
    }
}