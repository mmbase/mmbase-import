/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation;

import java.util.*;

import net.sf.mmapps.commons.bridge.*;
import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



public class PortletUtil {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(PortletUtil.class.getName());
    
    private static final String SOURCE = "source";
    private static final String DESTINATION = "destination";

    public static final String PORTLET = "portlet";
    public static final String PORTLETDEFINITION = "portletdefinition";
    public static final String PORTLETPARAMETER = "portletparameter";
    public static final String NODEPARAMETER = "nodeparameter";
    public static final String VIEW = "view";
    public static final String TYPEDEF = "typedef";
    
    public static final String DEFINITIONREL = "definitionrel";
    public static final String PORTLETREL = "portletrel";
    public static final String PARAMETERREL = "parameterrel";
    public static final String ALLOWREL = "allowrel";
    public static final String VIEWREL = "viewrel";
    
    public final static String TITLE_FIELD = "title";
    public static final String RESOURCE_FIELD = "resource";
    public static final String COLUMNS_FIELD = "columns";
    public static final String ROWS_FIELD = "rows";
    public static final String DEFINITION_FIELD = "definition";
    public static final String VALUE_FIELD = "value";
    public static final String LAYOUTID_FIELD = "name";
    public static final String KEY_FIELD = "key";
    public static final String TYPE_FIELD = "type";

    private static final String SINGLE = "single";
    private static final String MULTIPLE = "multiple";

    private static final String MMBASERANKS = "mmbaseranks";

    private static final String REQUIRESREL = "requiresrel";
    
    private PortletUtil() {
        // utility
    }
    

    public static NodeList getPortlets(Node page) {
        return page.getRelatedNodes(PORTLET, PORTLETREL, DESTINATION);
    }

    public static RelationList getPortletRelations(Node pageNode) {
        NodeManager portletMgr = pageNode.getCloud().getNodeManager(PORTLET);
        RelationList plist = pageNode.getRelations(PORTLETREL, portletMgr, DESTINATION);
        return plist;
    }

    public static NodeList getNodeParameters(Node portlet) {
        return portlet.getRelatedNodes(NODEPARAMETER, PARAMETERREL, DESTINATION);
    }

    public static NodeList getPortletParameters(Node portlet) {
        return portlet.getRelatedNodes(PORTLETPARAMETER, PARAMETERREL, DESTINATION);
    }

    public static NodeList getPortletViews(Node portlet) {
        return portlet.getRelatedNodes(VIEW, VIEWREL, DESTINATION);
    }

    public static Node getDefinition(Node portlet) {
        NodeList definitions = portlet.getRelatedNodes(PORTLETDEFINITION, DEFINITIONREL, DESTINATION);
        if (!definitions.isEmpty()) {
            return definitions.getNode(0);
        }
        return null;   
    }
    
    public static Node getPortletForDefinition(Node definition) {
        NodeList definitions = definition.getRelatedNodes(PORTLET, DEFINITIONREL, SOURCE);
        if (!definitions.isEmpty()) {
            return definitions.getNode(0);
        }
        return null;   
    }

    public static Node getPortletForParameter(Node parameter) {
        NodeList definitions = parameter.getRelatedNodes(PORTLET, PARAMETERREL, SOURCE);
        if (!definitions.isEmpty()) {
            return definitions.getNode(0);
        }
        return null;       }
    
    public static void deletePortlet(Node portlet) {
        log.debug("Delete portlet: "+portlet.getStringValue(TITLE_FIELD));
        deletePortletParameters(portlet);
        portlet.delete(true);
    }
    
    /**
     * Delete all parameters associated with a portlet.
     * @param portlet Portlet whose parameters are to be deleted.
     */
    public static void deletePortletParameters(Node portlet) {
        // Delete all the portlet parameters
        NodeList paramList = getPortletParameters(portlet);
        if (paramList != null) {
            for (int i = 0; i < paramList.size(); i++) {
                Node param = paramList.getNode(i);
                log.debug("Delete portlet param: "+param.getStringValue(KEY_FIELD));
                param.delete(true);
            }
        }
    }

    public static void copyPortlets(Node sourceScreen, Node newScreen) {
        RelationList portletList = getPortletRelations(sourceScreen);
        if (portletList != null) {
            for (int i = 0; i < portletList.size(); i++) {
                Relation portletRelation = portletList.getRelation(i);
                String id = portletRelation.getStringValue(LAYOUTID_FIELD);
                Node portlet = portletRelation.getDestination();
                Node newPortlet = copyPortlet(portlet);
                addPortlet(newScreen, newPortlet, id);
            }
        }
    }

    public static Node copyPortlet(Node sourcePortlet) {
        Node newPortlet = CloneUtil.cloneNode(sourcePortlet);
        NodeList portletParameterList = getPortletParameters(sourcePortlet);
        if (portletParameterList != null) {
            for (int i = 0; i < portletParameterList.size(); i++) {
                Node portletParameter = portletParameterList.getNode(i);
                Node newPortletParameter = copyPortletParameter(portletParameter);
                addPortletParameter(newPortlet, newPortletParameter);
            }
        }
        NodeList nodeParameterList = getNodeParameters(sourcePortlet);
        if (nodeParameterList != null) {
            for (int i = 0; i < nodeParameterList.size(); i++) {
                Node portletParameter = nodeParameterList.getNode(i);
                Node newPortletParameter = copyPortletParameter(portletParameter);
                addPortletParameter(newPortlet, newPortletParameter);
            }
        }

        CloneUtil.cloneRelations(sourcePortlet, newPortlet, VIEWREL, VIEW);
        
        return newPortlet;
    }
    
    public static Node copyPortletParameter(Node portletParameter) {
        return CloneUtil.cloneNode(portletParameter);
    }
    
    public static void addPortlet(Node page, Node portlet, String id) {
        Cloud cloud = page.getCloud();
        RelationManager portletRel = cloud.getRelationManager(PORTLETREL);
        Relation relation = page.createRelation(portlet, portletRel);
        relation.setStringValue(LAYOUTID_FIELD, id);
        relation.commit();
    }

    public static boolean hasPortlet(Node page, String id) {
        RelationList portletrels = getPortletRelations(page);
        for (Iterator iter = portletrels.iterator(); iter.hasNext();) {
            Relation portletrel = (Relation) iter.next();
            String value = portletrel.getStringValue(LAYOUTID_FIELD);
            if (value.equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    public static void addPortletParameter(Node portlet, Node portletParameter) {
        RelationUtil.createRelation(portlet, portletParameter, PARAMETERREL);
    }

    public static void addPortletView(Node portlet, Node portletParameter) {
        RelationUtil.createRelation(portlet, portletParameter, VIEWREL);
    }
    
    public static void addPortletDefinition(Node portlet, Node definition) {
        RelationUtil.createRelation(portlet, definition, DEFINITIONREL);
    }
    
    public static void updatePortletParameter(Cloud cloud, String portletId, String key, String value) {
        Node portlet = cloud.getNode(portletId);
        updatePortletParameter(portlet, key, value);
    }

    public static void updatePortletParameter(Node portlet, String key, String value) {
        NodeList plist = SearchUtil.findRelatedNodeList(portlet, PORTLETPARAMETER, PARAMETERREL, KEY_FIELD, key);
        if (!plist.isEmpty()) {
            Node foundNode = plist.getNode(0);
            if (StringUtil.isEmptyOrWhitespace(value)) {
                foundNode.delete(true);
            }
            else {
                log.debug("updating node:" + foundNode.getNumber());
                foundNode.setStringValue(VALUE_FIELD, value);
                foundNode.commit();
            }
        } else {
            if (!StringUtil.isEmptyOrWhitespace(value)) {
                log.debug("creating node for node:" + portlet.getNumber());
                Node newNode = createPortletParameter(portlet.getCloud(), key, value);
                addPortletParameter(portlet, newNode);
            }
        }
    }
    
    public static void updatePortletParameter(Cloud cloud, String portletId, String key, Node value) {
        Node portlet = cloud.getNode(portletId);
        updatePortletParameter(portlet, key, value);
    }
    
    public static void updatePortletParameter(Node portlet, String key, Node value) {
        NodeList plist = SearchUtil.findRelatedNodeList(portlet, NODEPARAMETER, PARAMETERREL, KEY_FIELD, key);
        if (!plist.isEmpty()) {
            Node foundNode = plist.getNode(0);
            log.debug("updating node:" + foundNode.getNumber());
            foundNode.setNodeValue(VALUE_FIELD, value);
            foundNode.commit();
        } else {
            log.debug("creating node for node:" + portlet.getNumber());
            Node newNode = createNodeParameter(portlet.getCloud(), key, value);
            addPortletParameter(portlet, newNode);
        }
    }
    
    public static Node createPortletParameter(Cloud cloud, String key, String value) {
        NodeManager paramMgr = cloud.getNodeManager(PORTLETPARAMETER);
        Node newNode = paramMgr.createNode();
        newNode.setStringValue(KEY_FIELD, key);
        newNode.setStringValue(VALUE_FIELD, value);
        newNode.commit();
        return newNode;
    }
    
    public static Node createNodeParameter(Cloud cloud, String key, Node value) {
        NodeManager paramMgr = cloud.getNodeManager(NODEPARAMETER);
        Node newNode = paramMgr.createNode();
        newNode.setStringValue(KEY_FIELD, key);
        newNode.setNodeValue(VALUE_FIELD, value);
        newNode.commit();
        return newNode;
    }

    public static void updatePortletView(Cloud cloud, String portletId, String viewId) {
        Node portlet = cloud.getNode(portletId);
        updatePortletView(cloud, portlet, viewId);
    }

    public static void updatePortletView(Cloud cloud, Node portlet, String viewId) {
        boolean makeView = false;
        NodeManager viewMgr = portlet.getCloud().getNodeManager(VIEW);

        // change or create a view
        if (viewId != null) {
            RelationList vlist = portlet.getRelations(VIEWREL, viewMgr, DESTINATION);
            if (vlist.size() > 0) {
                Relation paramRelation = (Relation) vlist.get(0);
                Node viewNode = paramRelation.getDestination();
                if (viewNode.getNumber() == Integer.parseInt(viewId)) {
                    log.debug("views are the same, no changes!");
                } else {
                    log.debug("view is different, remove ('" + viewNode.getNumber() + "')!");
                    paramRelation.delete(true);
                    makeView = true;
                }

            } else {
                log.debug("no view so make it");
                makeView = true;
            }

            if (makeView) {
                Node viewNode = cloud.getNode(viewId);
                addPortletView(portlet, viewNode);
            }
        }
    }

    public static Node createPortlet(Cloud cloud, String portletName, String definitionId, String viewId) {
        Node viewNode = null;
        if (!StringUtil.isEmpty(viewId)) {
            viewNode = cloud.getNode(viewId);
        }
        Node definitionNode = null;
        if (!StringUtil.isEmpty(definitionId)) {
            definitionNode = cloud.getNode(definitionId);
        }

        return createPortlet(cloud, portletName, definitionNode, viewNode);
    }

    public static Node createPortlet(Cloud cloud, String portletName, Node definitionNode, Node viewNode, Map parameters) {
        Node portlet = createPortlet(cloud, portletName, definitionNode, viewNode);
        if (parameters != null) {
            for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    Node parameter = createPortletParameter(cloud, key, (String) value);
                    addPortletParameter(portlet, parameter);
                }
                if (value instanceof Node) {
                    Node parameter = createNodeParameter(cloud, key, (Node) value);
                    addPortletParameter(portlet, parameter);
                }
            }
        }
        
        return portlet;
    }


    public static Node createPortlet(Cloud cloud, String portletName, Node definitionNode, Node viewNode) {
        NodeManager portletMgr = cloud.getNodeManager(PORTLET);
        Node newNode = portletMgr.createNode();
        newNode.setStringValue(TITLE_FIELD, portletName);
        newNode.commit();

        if (definitionNode != null) {
            addPortletDefinition(newNode, definitionNode);
        }
        else {
            throw new IllegalArgumentException("Defintion node is required"); 
        }
        
        if (viewNode != null) {
            addPortletView(newNode, viewNode);
        }
        return newNode;
    }
    
    public static void setPagePortlet(Cloud cloud, String pageId, String defId, String layoutId) {
        Node pltdef = cloud.getNode(defId);
        Node plt = getPortletForDefinition(pltdef);
        setPagePortlet(cloud, pageId, plt, layoutId);
    }

    public static void setPagePortlet(Cloud cloud, String pageId, Node portlet, String layoutId) {
        log.debug("page='" + pageId + "' portlet='" + portlet.getNumber() + "' layoutId='" + layoutId + "'");
        Node page = cloud.getNode(pageId);
        addPortlet(page, portlet, layoutId);
    }
    
    public static void deletePagePortlet(Cloud cloud, int pageId, int portletId, String id) {
        log.debug("page:'" + pageId + "' portlet:'" + portletId + "' id:'"+id+"'");
        Node pageNode = cloud.getNode(pageId);
        RelationList plist = getPortletRelations(pageNode);
        for (int i = 0; i < plist.size(); i++) {
            Relation portletRelation = (Relation) plist.get(i);
            Node portletNode = portletRelation.getDestination();
            if (portletNode.getNumber() == portletId) {
                if (portletRelation.getStringValue(LAYOUTID_FIELD).equals(id)) {
                    log.debug("Portlet:'" + portletId + "' removed from page");
                    portletRelation.delete(true);
                    Node definition = PortletUtil.getDefinition(portletNode);
                    if (MULTIPLE.equals(definition.getStringValue(TYPE_FIELD))) {
                        deletePortlet(portletNode);
                    }
                }
            }
        }
    }
    
    public static Node createView(Cloud cloud, String title, String resource) {
        NodeManager viewMgr = cloud.getNodeManager(VIEW);
        Node view = viewMgr.createNode();
        view.setStringValue(TITLE_FIELD, title);
        view.setStringValue(RESOURCE_FIELD, resource);
        view.commit();
        return view;
    }

    public static Node findDefinitionWithTitle(Cloud cloud, String definitionName) {
        return SearchUtil.findNode(cloud, PORTLETDEFINITION, TITLE_FIELD, definitionName);
    }
    
    public static Node findViewWithTitle(Cloud cloud, String view) {
        return SearchUtil.findNode(cloud, VIEW, TITLE_FIELD, view);
    }

    public static Node findViewWithResource(Cloud cloud, String view) {
        return SearchUtil.findNode(cloud, VIEW, RESOURCE_FIELD, view);
    }

    public static NodeList getAllowedViews(Node definitionNode) {
        return definitionNode.getRelatedNodes(VIEW, ALLOWREL, DESTINATION);
    }

    public static boolean isSingleDefinition(Node definition) {
        return SINGLE.equals(definition.getStringValue(PortletUtil.TYPE_FIELD));
    }

    public static boolean isSinglePortlet(Node portlet) {
        Node definition = getDefinition(portlet);
        return isSingleDefinition(definition);
    }


    public static Node getRank(Node definitionNode) {
        NodeList ranks = definitionNode.getRelatedNodes(MMBASERANKS, REQUIRESREL, DESTINATION);
        if (!ranks.isEmpty()) {
            return ranks.getNode(0);
        }
        return null;
    }


    public static List<String> getAllowedTypes(Node node) {
        List<String> types = new ArrayList<String>();
        NodeList typedefs = node.getRelatedNodes(TYPEDEF, ALLOWREL, DESTINATION);
        for (Iterator iter = typedefs.iterator(); iter.hasNext();) {
            Node typedef = (Node) iter.next();
            types.add(typedef.getStringValue("name"));
        }
        return types;
    }
}
