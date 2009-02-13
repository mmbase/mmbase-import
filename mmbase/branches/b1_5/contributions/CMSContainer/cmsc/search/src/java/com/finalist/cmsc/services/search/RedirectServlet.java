/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

import java.io.IOException;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.bridge.Node;
import org.mmbase.servlet.BridgeServlet;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

@SuppressWarnings("serial")
public class RedirectServlet extends BridgeServlet {

    private boolean forwardRequest = false;

    @Override
    protected Map<String, Integer> getAssociations() {
        Map<String, Integer> a = super.getAssociations();
        a.put("content", Integer.valueOf(50));
        return a;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String forwardRequest = config.getInitParameter("forwardRequest");
        this.forwardRequest = Boolean.valueOf(forwardRequest);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doRedirect(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doRedirect(request, response);
    }

    private void doRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        QueryParts queryParts = readQueryFromRequestURI(request, null);
        Node node = getNode(queryParts);
        if (node == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No redirect possible");
            return;
        }
        String redirect = null;

        String managerName = node.getNodeManager().getName();
        if (ResourcesUtil.URLS.equals(managerName)) {
            redirect = node.getStringValue("url");
        }
        if (ResourcesUtil.ATTACHMENTS.equals(managerName)) {
            redirect = ResourcesUtil.getServletPath(node, node.getStringValue("number"));
        }
        if (ResourcesUtil.IMAGES.equals(managerName)) {
            redirect = ResourcesUtil.getServletPath(node, node.getStringValue("number"));
        }

        /* *****************************
         * All types above this comment should all be redirected and not forwarded with a dispatcher
         * *****************************
         */
        if (redirect != null) {
            response.sendRedirect(redirect);
            return;
        }

        if (PagesUtil.isPageType(node)) {
            NavigationItem item = SiteManagement.getNavigationItem(node.getNumber());
            if (item != null) {
                redirect = getPortalUrl(request, item);
            }
        }

        if (ContentElementUtil.isContentElement(node)) {
            PageInfo pageInfo = null;
            if (ServerUtil.useServerName()) {
                pageInfo = Search.findDetailPageForContent(node, request.getServerName());
            }
            else {
                pageInfo = Search.findDetailPageForContent(node);
            }

            if (pageInfo != null) {
                PortalURL u = new PortalURL(pageInfo.getHost(), request, pageInfo.getPath());
                String elementId = String.valueOf(node.getNumber());
                // When contentelement and the same number then it is a contentportlet
                if (! ( "contentelement".equals(pageInfo.getParametername())
                        && elementId.equals(pageInfo.getParametervalue()) ) ) {
                    u.setRenderParameter(pageInfo.getWindowName(), "elementId", new String[] { elementId } );
                }

                for (Map.Entry<String, String> urlParam : pageInfo.getUrlParameters().entrySet()) {
                    u.setRenderParameter(pageInfo.getWindowName(), urlParam.getKey(), new String[] { urlParam.getValue() } );
                }
                redirect = u.toString();
            }
        }

        if (redirect != null) {
            if (this.forwardRequest) {
                if (redirect.indexOf("://") > -1 && ServerUtil.useServerName()) {
                    // not a valid forward dispatch url, but it might be converted to one.
                    String currentHost = request.getServerName();
                    int hostIndex = redirect.indexOf("://" + currentHost);
                    if (hostIndex > -1) {
                        // The same host as the contenturl. strip servername and port
                        int firstSlash = redirect.indexOf("/", hostIndex + "://".length());
                        if (firstSlash > -1) {
                           redirect = redirect.substring(firstSlash);
                        }
                        else {
                           // firstSlash is -1 when there is a contentportlet on the homepage
                           // and servername is true
                           response.sendRedirect(redirect);
                           return;
                        }
                    }
                    else {
                        // can not convert so just redirect.
                        response.sendRedirect(redirect);
                        return;
                    }
                }
                if (!"/".equals(request.getContextPath())) {
                    if (redirect.startsWith(request.getContextPath() + "/")) {
                        redirect = redirect.substring( request.getContextPath().length());
                    }
                }

            	redirect = response.encodeURL(redirect);
                try {
                    RequestDispatcher rd = super.getServletContext().getNamedDispatcher(PortalConstants.CMSC_PORTAL_SERVLET);
                    HttpServletRequest internalRequest = new InternalRedirectHttpServletRequest(request, redirect);
                    PortalEnvironment internalEnv = new PortalEnvironment(internalRequest, response);
                    rd.forward(internalRequest, response);
                }
                catch (ServletException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
            else {
            	response.sendRedirect(redirect);
        	}
        }
        else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No page found");
        }
    }

    private String getPortalUrl(HttpServletRequest request, NavigationItem item) {
        String host = null;
        if(ServerUtil.useServerName()) {
           host = SiteManagement.getSite(item);
        }

        String link = SiteManagement.getPath(item, !ServerUtil.useServerName());
        PortalURL u = new PortalURL(host, request, link);
        return u.toString();
    }

    class InternalRedirectHttpServletRequest extends HttpServletRequestWrapper {

        private String pagePath;

        public InternalRedirectHttpServletRequest(HttpServletRequest request, String pagePath) {
            super(request);
            this.pagePath = pagePath;
        }

        @Override
        public String getServletPath() {
            return pagePath;
        }
        
        @Override
        public StringBuffer getRequestURL() {
          String webapp = HttpUtil.getWebappUri(this);
          return new StringBuffer(webapp).append(pagePath.substring(1));
        }
    }
}
