/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;
import org.mmbase.servlet.BridgeServlet;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.services.search.PageInfo;
import com.finalist.cmsc.services.search.Search;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalURL;

@SuppressWarnings("serial")
public class RedirectServlet extends BridgeServlet {

    @Override
    protected Map getAssociations() {
        Map a = super.getAssociations();
        a.put("content", new Integer(50));
        return a;
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
        QueryParts queryParts = readQuery(request, null);
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
        
        if (PagesUtil.isPage(node)) {
            Page page = SiteManagement.getPage(node.getNumber());
            if (page != null) {
                String link = SiteManagement.getPath(page, !ServerUtil.useServerName());
                redirect = request.getContextPath() + "/" + link;
            }
            
        }
        
        if (ContentElementUtil.isContentElement(node)) {
            PageInfo pageInfo = Search.findDetailPageForContent(node);
            if (pageInfo != null) {
                PortalURL u = new PortalURL(request, pageInfo.getPath());
                u.setRenderParameter(pageInfo.getWindowName(), "elementId", new String[] { String.valueOf(node.getNumber()) } );
                redirect = u.toString();
            }
        }
        
        if (redirect != null) {
            redirect = response.encodeURL(redirect);
            response.sendRedirect(redirect);
        }
        else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No page found");
        }
    }

}
