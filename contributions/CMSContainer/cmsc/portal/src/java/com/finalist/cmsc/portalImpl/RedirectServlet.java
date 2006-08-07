/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
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
        QueryParts queryParts = readQuery(request, response);
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
            NodeList channels = RepositoryUtil.getContentChannels(node);
            channels.add(node);
            
            Cloud cloud = getAnonymousCloud();
            NodeManager parameterManager = cloud.getNodeManager(PortletUtil.NODEPARAMETER);
            NodeManager portletManager = cloud.getNodeManager(PortletUtil.PORTLET);
            NodeManager pageManager = cloud.getNodeManager(PagesUtil.PAGE);

            Query query = cloud.createQuery();
            Step parameterStep = query.addStep(parameterManager);
            RelationStep step2 = query.addRelationStep(portletManager, PortletUtil.PARAMETERREL, "SOURCE");
            RelationStep step4 = query.addRelationStep(pageManager, PortletUtil.PORTLETREL, "SOURCE");
            Step pageStep = step4.getNext();

            query.addField(parameterStep, parameterManager.getField(PortletUtil.KEY_FIELD));
            query.addField(parameterStep, parameterManager.getField(PortletUtil.VALUE_FIELD));
            query.addField(step4, cloud.getRelationManager(PortletUtil.PORTLETREL).getField(PortletUtil.LAYOUTID_FIELD));
            query.addField(pageStep, pageManager.getField("number"));
            
            SearchUtil.addNodesConstraints(query, parameterManager.getField(PortletUtil.VALUE_FIELD), channels);
            
            NodeList pages = cloud.getList(query);
            if (!pages.isEmpty()) {
                Node pageQueryNode = null;
                if (pages.size() == 1) {
                    pageQueryNode = pages.getNode(0);
                }
                else {
                    for (Iterator iter = pages.iterator(); iter.hasNext();) {
                        Node pageNode = (Node) iter.next();
                        String key = pageNode.getStringValue(PortletUtil.NODEPARAMETER + "." + PortletUtil.KEY_FIELD);
                        if ("contentelement".equals(key)) {
                            pageQueryNode = pageNode;
                        }
                    }
                    if (pageQueryNode == null) {
                        pageQueryNode = pages.getNode(0);
                    }
                }

                if (pageQueryNode != null) {
                    Page page = SiteManagement.getPage(pageQueryNode.getIntValue(PagesUtil.PAGE + ".number"));
                    if (page != null) {
                        String link = SiteManagement.getPath(page, !ServerUtil.useServerName());
                        PortalURL u = new PortalURL(request, link);

                        String portletWindowName = pageQueryNode.getStringValue(PortletUtil.PORTLETREL + "." + PortletUtil.LAYOUTID_FIELD);
                        u.setRenderParameter(portletWindowName, "elementId", new String[] { String.valueOf(node.getNumber()) } );
                        
                        redirect = u.toString();
                    }
                }
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
