/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation;

import net.sf.mmapps.commons.bridge.*;
import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreeUtil;


public class PagesUtil {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(PagesUtil.class.getName());
    
    private static final String DESTINATION = "DESTINATION";
    
    public static final String PAGE = "page";
    public static final String LAYOUT = "layout";
    public static final String POPUPINFO = "popupinfo";

    public static final String TITLE_FIELD = "title";
    public static final String FRAGMENT_FIELD = "urlfragment";
    public static final String VISIBLE_FIELD = "inmenu";
    public static final String RESOURCE_FIELD = "resource";
    public static final String NAME_FIELD = "name";
    public static final String SECURE_FIELD = "secure";
    public static final String DESCRIPTION_FIELD = "description";

    public static final String CREATIONDATE_FIELD = "creationdate";
    public static final String LASTMODIFIEDDATE_FIELD = "lastmodifieddate";
    public static final String LASTMODIFIER_FIELD = "lastmodifier";

    
    public static final String RELATED = "related";
    public static final String LAYOUTREL = "layoutrel";
    public static final String STYLEREL = "stylerel";

    private static final String NAMEDALLOWEDREL = "namedallowrel";

    public static final String STYLESHEET = "stylesheet";

    public static NodeManager getNodeManager(Cloud cloud) {
        return TreeUtil.getNodeManager(cloud, PAGE);
    }

    public static Node getPopupinfo(Node pageNode) {
        NodeList popupinfo = pageNode.getRelatedNodes(POPUPINFO, RELATED, DESTINATION);
        if (!popupinfo.isEmpty()) {
            return popupinfo.getNode(0);
        }
        return null;
    }
    
    public static void addPopupinfo(Node pageNode, Node popupinfoNode) {
        RelationUtil.createRelation(pageNode, popupinfoNode, RELATED);
    }
    
    public static boolean isPage(Node node) {
        return PAGE.equals(node.getNodeManager().getName());
    }
    
    /** Is element of page type
     * @param channelNode node to check
     * @return is page
     */
    public static boolean isPageType(Node channelNode) {
        NodeManager nm = channelNode.getNodeManager();
        return isPageType(nm);
    }
    
    /** Is ModeManager of the type page
     * @param nm NodeManager to check
     * @return is page
     */
    public static boolean isPageType(NodeManager nm) {
       try {
          while (!PAGE.equals(nm.getName())) {
             nm = nm.getParent();
          }
          return true;
       }
       catch (NotFoundException nfe) {
          // Ran out of NodeManager parents
       }
       return false;
    }
    
    public static Node createPage(Cloud cloud, String name, String layout) {
        Node layoutNode = findLayoutWithTitle(cloud, layout);
        if (layoutNode == null) {
            throw new IllegalArgumentException("Layout not found with title: " + layout);
        }

        return createPage(cloud, name, null, layoutNode);
    }

    public static Node createPage(Cloud cloud, String name, Node layout) {
        return createPage(cloud, name, null, null, layout);
    }
    
    public static Node createPage(Cloud cloud, String name, String pathname, Node layout) {
        return createPage(cloud, name, pathname, null, layout);
    }
    
    public static Node createPage(Cloud cloud, String name, String pathname, String description, Node layout) {
        Node page = getNodeManager(cloud).createNode();
        page.setStringValue(TITLE_FIELD, name);
        if (!StringUtil.isEmpty(pathname)) {
            page.setStringValue(FRAGMENT_FIELD, pathname);
        }
        if (!StringUtil.isEmpty(description)) {
            page.setStringValue(DESCRIPTION_FIELD, description);
        }
        page.commit();

        addLayout(page, layout);
        linkPortlets(page, layout);
        return page;
    }

    public static void addLayout(Node page, Node layoutNode) {
        if (layoutNode == null) {
            throw new IllegalArgumentException("Layout may not be null");
        }
        RelationUtil.createRelation(page, layoutNode, LAYOUTREL);
    }
    
    public static void addStylesheet(Node page, Node stylesheetNode) {
        if (stylesheetNode == null) {
            throw new IllegalArgumentException("Stylesheet may not be null");
        }
        RelationUtil.createRelation(page, stylesheetNode, STYLEREL);
    }
    
    /**
     * Use this method to remove a page.
     * 
     * @param page
     */
    public static void deletePage(Node page) {
        String pageTitle = page.getStringValue("title");
        log.debug("Delete page: " + pageTitle);

        // Destroy all portlets associated with the page
        NodeList portletList = PortletUtil.getPortlets(page);
        if (portletList != null) {
            for (int i = 0; i < portletList.size(); i++) {
                Node portlet = portletList.getNode(i);
                if (!PortletUtil.isSinglePortlet(portlet)) {
                    PortletUtil.deletePortlet(portlet);
                }
            }
        }
        page.delete(true);
    }

    public static Node copyPage(Node sourcePage) {
        Node newPage = CloneUtil.cloneNode(sourcePage);
        CloneUtil.cloneRelations(sourcePage, newPage, LAYOUTREL, LAYOUT);
        PortletUtil.copyPortlets(sourcePage, newPage);

        Node popupinfo = getPopupinfo(sourcePage);
        if (popupinfo != null) {
            Node newPopupinfo = copyPopupinfo(popupinfo);
            addPopupinfo(newPage, newPopupinfo);
        }
        return newPage;
    }


    public static Node createLayout(Cloud cloud, String title, String resource) {
        NodeManager layoutMgr = cloud.getNodeManager(LAYOUT);
        Node layout = layoutMgr.createNode();
        layout.setStringValue(TITLE_FIELD, title);
        layout.setStringValue(RESOURCE_FIELD, resource);
        layout.commit();
        return layout;
    }
    
    public static Node createStylesheet(Cloud cloud, String title, String resource) {
        NodeManager stylesheetMgr = cloud.getNodeManager(STYLESHEET);
        Node stylesheet = stylesheetMgr.createNode();
        stylesheet.setStringValue(TITLE_FIELD, title);
        stylesheet.setStringValue(RESOURCE_FIELD, resource);
        stylesheet.commit();
        return stylesheet;
    }
    
    public static NodeList getStylesheet(Node pageNode) {
        return pageNode.getRelatedNodes(STYLESHEET, STYLEREL, DESTINATION);
    }
    
    public static Node getLayout(Node pageNode) {
        NodeList layouts = pageNode.getRelatedNodes(LAYOUT, LAYOUTREL, DESTINATION);
        if (!layouts.isEmpty()) {
            return layouts.getNode(0);
        }
        return null;
    }
    
    public static Node findLayoutWithTitle(Cloud cloud, String layout) {
        return SearchUtil.findNode(cloud, LAYOUT, TITLE_FIELD, layout);
    }
    
    public static Node findLayoutWithResource(Cloud cloud, String layout) {
        return SearchUtil.findNode(cloud, LAYOUT, RESOURCE_FIELD, layout);
    }
    
    public static Node findStylesheetWithTitle(Cloud cloud, String layout) {
        return SearchUtil.findNode(cloud, STYLESHEET, TITLE_FIELD, layout);
    }
    
    public static Node findStylesheetWithResource(Cloud cloud, String layout) {
        return SearchUtil.findNode(cloud, STYLESHEET, RESOURCE_FIELD, layout);
    }
    
    public static Node copyPopupinfo(Node popupinfo) {
        return CloneUtil.cloneNode(popupinfo);
    }

    public static RelationList getAllowedNamedRelations(Node layoutNode) {
        return SearchUtil.findRelations(layoutNode, PortletUtil.PORTLETDEFINITION,
                    NAMEDALLOWEDREL, NAME_FIELD, "UP");
    }
    
    public static NodeList getAllowedDefintions(Node layoutNode, String name) {
        return SearchUtil.findRelatedNodeList(layoutNode, PortletUtil.PORTLETDEFINITION,
                                              NAMEDALLOWEDREL, NAME_FIELD, name);
    }
    
    public static void addAllowedNamedRelation(Node layoutNode, Node definitionNode, String position) {
        Relation relation = RelationUtil.createRelation(layoutNode, definitionNode, NAMEDALLOWEDREL);
        relation.setStringValue(NAME_FIELD, position);
        relation.commit();
    }
    
    public static void linkPortlets(Node newPage, Node layoutNode) {
        RelationList namedRelations = PagesUtil.getAllowedNamedRelations(layoutNode);
        if (!namedRelations.isEmpty()) {
            Relation previousRelation = namedRelations.getRelation(0);
            String previoueName = previousRelation.getStringValue(PagesUtil.NAME_FIELD);
            int count = 1;
            
            for (int i = 1; i < namedRelations.size(); i++) {
                Relation relation = namedRelations.getRelation(i);
                String name = relation.getStringValue(PagesUtil.NAME_FIELD);
                if (previoueName.equals(name)) {
                    count++;
                }
                else {
                    if (count == 1) {
                        Node definition = previousRelation.getDestination();
                        if (PortletUtil.isSingleDefinition(definition)) {
                            Node portlet = PortletUtil.getPortletForDefinition(definition);
                            if (portlet != null) {
                                PortletUtil.addPortlet(newPage, portlet, previoueName);
                            }
                            else {
                                throw new NullPointerException("Single portletdefinition does not have a portlet instance");
                            }
                        }
                    }
                    else {
                        count = 1;
                    }
                    previousRelation = relation;
                    previoueName = name;
                }
            }
            if (count == 1) {
                Node definition = previousRelation.getDestination();
                if (PortletUtil.isSingleDefinition(definition)) {
                    Node portlet = PortletUtil.getPortletForDefinition(definition);
                    if (portlet != null) {
                        PortletUtil.addPortlet(newPage, portlet, previoueName);
                    }
                    else {
                        throw new NullPointerException("Single portletdefinition does not have a portlet instance");
                    }
                }
            }
        }
    }

}
