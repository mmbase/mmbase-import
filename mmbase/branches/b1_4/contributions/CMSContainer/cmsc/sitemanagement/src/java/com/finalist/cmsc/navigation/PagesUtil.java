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
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.publish.Publish;

public class PagesUtil {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(PagesUtil.class.getName());

   private static final String SOURCE = "source";
   private static final String DESTINATION = "DESTINATION";

   public static final String PAGE = "page";
   public static final String LAYOUT = "layout";
   public static final String POPUPINFO = "popupinfo";
   public static final String STYLESHEET = "stylesheet";
   public static final String IMAGES = "images";

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

   public static final String PUBLISHDATE_FIELD = "publishdate";
   public static final String EXPIREDATE_FIELD = "expiredate";
   public static final String USE_EXPIRY_FIELD = "use_expirydate";

   public static final String RELATED = "related";
   public static final String LAYOUTREL = "layoutrel";
   public static final String STYLEREL = "stylerel";
   public static final String NAMEDALLOWEDREL = "namedallowrel";
   public static final String NAMEDREL = "namedrel";

   public static final String POS_FIELD = "pos";


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


   /**
    * Is element of page type
    * 
    * @param node
    *           node to check
    * @return is page
    */
   public static boolean isPageType(Node node) {
      NodeManager nm = node.getNodeManager();
      return isPageType(nm);
   }


   /**
    * Is ModeManager of the type page
    * 
    * @param nm
    *           NodeManager to check
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


   public static boolean isPageType(String type) {
      NodeManager nm = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNodeManager(type);
      return isPageType(nm);
   }


   public static Node createPage(Cloud cloud, String name, String layout) {
      Node layoutNode = findLayoutWithTitle(cloud, layout);
      if (layoutNode == null) {
         throw new IllegalArgumentException("Layout not found with title: " + layout);
      }

      return createPage(cloud, name, null, layoutNode);
   }

   public static Node createPage(Cloud cloud, String name, String layout, String managerName) {
       Node layoutNode = findLayoutWithTitle(cloud, layout);
       if (layoutNode == null) {
          throw new IllegalArgumentException("Layout not found with title: " + layout);
       }

       return createPage(cloud, name, null, layoutNode, managerName);
   }

   public static Node createPage(Cloud cloud, String name, Node layout) {
      return createPage(cloud, name, null, null, layout);
   }

   public static Node createPage(Cloud cloud, String name, Node layout, String managerName) {
       return createPage(cloud, name, null, null, layout, managerName);
   }

   public static Node createPage(Cloud cloud, String name, String pathname, Node layout) {
      return createPage(cloud, name, pathname, null, layout);
   }

   public static Node createPage(Cloud cloud, String name, String pathname, Node layout, String managerName) {
       return createPage(cloud, name, pathname, null, layout, managerName);
   }

   public static Node createPage(Cloud cloud, String name, String pathname, String description, Node layout) {
      return createPage(cloud, name, pathname, description, layout, PAGE);
   }

   public static Node createPage(Cloud cloud, String name, String pathname,
        String description, Node layout, String managerName) {
      Node page = TreeUtil.getNodeManager(cloud, managerName).createNode();
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


   public static void replaceLayout(Node page, Node newLayout) {
      if (newLayout == null) {
         throw new IllegalArgumentException("Layout may not be null");
      }
      RelationList layoutrels = page.getRelations(LAYOUTREL);
      for (Iterator<Relation> iterator = layoutrels.iterator(); iterator.hasNext();) {
         Relation rel = iterator.next();
         rel.delete(true);
      }
      addLayout(page, newLayout);
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

      if (Workflow.hasWorkflow(page)) {
         Workflow.remove(page);
      }
      if (Publish.isPublished(page)) {
         Publish.unpublish(page);
      }

      // Destroy all portlets associated with the page
      NodeList portletList = PortletUtil.getPortlets(page);
      if (portletList != null) {
         for (int i = 0; i < portletList.size(); i++) {
            Node portlet = portletList.getNode(i);
            if (portlet != null) {
               if (!PortletUtil.isSinglePortlet(portlet)) {
                  PortletUtil.deletePortlet(portlet);
               }
            }
         }
      }

      page.delete(true);
   }


   public static Node copyPage(Node sourcePage) {
      Node newPage = CloneUtil.cloneNode(sourcePage);
      return copyPageRelations(sourcePage, newPage);
   }


   public static Node copyPageRelations(Node sourcePage, Node newPage) {
      CloneUtil.cloneRelations(sourcePage, newPage, LAYOUTREL, LAYOUT);
      CloneUtil.cloneRelations(sourcePage, newPage, STYLEREL, STYLESHEET);
      CloneUtil.cloneRelations(sourcePage, newPage, NAMEDREL, IMAGES);
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


   public static boolean isStylesheet(Node node) {
      return STYLESHEET.equals(node.getNodeManager().getName());
   }


   public static NodeList getStylesheet(Node pageNode) {
      return SearchUtil.findRelatedOrderedNodeList(pageNode, STYLESHEET, STYLEREL, STYLEREL + "." + POS_FIELD);
   }


   public static NodeList getLayouts(Cloud cloud) {
      return SearchUtil.findOrderedNodeList(cloud, LAYOUT, TITLE_FIELD);
   }


   public static boolean isLayout(Node node) {
      return LAYOUT.equals(node.getNodeManager().getName());
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

    public static Map<String, List<Integer>> getPageImages(Node pageNode) {
        Map<String,List<Integer>> pageImages = new HashMap<String,List<Integer>>();
        RelationList relations = PagesUtil.getImages(pageNode);
        for(RelationIterator iter = relations.relationIterator(); iter.hasNext();) {
            Relation relation = iter.nextRelation();
            String name = relation.getStringValue(NAME_FIELD);
             
            // this is a bit of a hack, but saves on the loading of the actual node
            int image = relation.getIntValue("dnumber");
             
            List<Integer> images = pageImages.get(name);
            if (images == null) {
                images = new ArrayList<Integer>();
                pageImages.put(name, images);
            }
            images.add(image);
        }
        return pageImages;
    }

    public static RelationList getImages(Node pageNode) {
        RelationList namedrels = pageNode.getRelations(NAMEDREL, pageNode.getCloud().getNodeManager(IMAGES), DESTINATION);
        Collections.sort(namedrels, new NodeFieldComparator(NAME_FIELD));
        return namedrels;
    }
    
   public static Node copyPopupinfo(Node popupinfo) {
      return CloneUtil.cloneNode(popupinfo);
   }


   public static RelationList getAllowedNamedRelations(Node layoutNode) {
      return SearchUtil.findRelations(layoutNode, PortletUtil.PORTLETDEFINITION, NAMEDALLOWEDREL, NAME_FIELD, "UP");
   }


   public static void addAllowedNamedRelation(Node layoutNode, Node definitionNode, String position) {
      Relation relation = RelationUtil.createRelation(layoutNode, definitionNode, NAMEDALLOWEDREL);
      relation.setStringValue(NAME_FIELD, position);
      relation.commit();
   }


   public static void linkPortlets(Node newPage, Node layoutNode) {
      RelationList namedRelations = PagesUtil.getAllowedNamedRelations(layoutNode);
      if (!namedRelations.isEmpty()) {
         Map<String, List<Node>> defpositions = new HashMap<String, List<Node>>();
         for (Iterator<Relation> iter = namedRelations.iterator(); iter.hasNext();) {
            Relation relation = iter.next();
            String name = relation.getStringValue(PagesUtil.NAME_FIELD);
            Node definition = relation.getDestination();
            String[] names = name.split(",");
            for (String element : names) {
               String position = element.trim();
               List<Node> definitions;
               if (defpositions.containsKey(position)) {
                  definitions = defpositions.get(position);
               }
               else {
                  definitions = new ArrayList<Node>();
                  defpositions.put(position, definitions);
               }
               definitions.add(definition);
            }
         }

         for (Map.Entry<String, List<Node>> defpos : defpositions.entrySet()) {
            String name = defpos.getKey();
            List<Node> definitions = defpos.getValue();
            if (definitions.size() == 1) {
               Node definition = definitions.get(0);
               if (PortletUtil.isSingleDefinition(definition)) {
                  Node portlet = PortletUtil.getPortletForDefinition(definition);
                  if (portlet != null) {
                     PortletUtil.addPortlet(newPage, portlet, name);
                  }
                  else {
                     throw new NullPointerException("Single portletdefinition does not have a portlet instance");
                  }
               }
            }
         }
      }
   }


   public static void removePortlets(Node page, boolean singleOnly) {
      RelationList portletrels = PortletUtil.getPortletRelations(page);
      for (Iterator<Relation> iterator = portletrels.iterator(); iterator.hasNext();) {
         Relation rel = iterator.next();
         Node portlet = rel.getDestination();
         if (PortletUtil.isSinglePortlet(portlet)) {
            rel.delete(true);
         }
         else {
            if (!singleOnly) {
               rel.delete(true);
               PortletUtil.deletePortlet(portlet);
            }
         }
      }
   }


   public static Node getPage(Node portlet) {
      if (!PortletUtil.isSinglePortlet(portlet)) {
         NodeList pages = portlet.getRelatedNodes(PAGE, PortletUtil.PORTLETREL, SOURCE);
         if (!pages.isEmpty()) {
            return pages.getNode(0);
         }
      }
      return null;
   }


   public static void addNotExpiredConstraint(Node channel, NodeQuery query, long date) {
      NodeManager pageManager = channel.getCloud().getNodeManager(PAGE);

      Constraint useExpire = getUseExpireConstraint(query, pageManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, pageManager, true);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, expirydate);
      SearchUtil.addConstraint(query, composite);
   }


   public static void addLifeCycleConstraint(NodeQuery query, long date) {
      NodeManager pageManager = query.getCloud().getNodeManager(PAGE);

      Constraint useExpire = getUseExpireConstraint(query, pageManager, Boolean.FALSE);
      Constraint expirydate = getExpireConstraint(query, date, pageManager, true);
      Constraint publishdate = getPublishConstraint(query, date, pageManager, false);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_AND, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_OR, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }


   public static void addLifeCycleInverseConstraint(NodeQuery query, long date) {
      NodeManager pageManager = query.getCloud().getNodeManager(PAGE);

      Constraint useExpire = getUseExpireConstraint(query, pageManager, Boolean.TRUE);
      Constraint expirydate = getExpireConstraint(query, date, pageManager, false);
      Constraint publishdate = getPublishConstraint(query, date, pageManager, true);

      Constraint lifecycleComposite = query.createConstraint(expirydate, CompositeConstraint.LOGICAL_OR, publishdate);

      Constraint composite = query.createConstraint(useExpire, CompositeConstraint.LOGICAL_AND, lifecycleComposite);
      SearchUtil.addConstraint(query, composite);
   }


   public static Constraint getUseExpireConstraint(NodeQuery query, NodeManager pageManager, Boolean value) {
      Field useExpireField = pageManager.getField(USE_EXPIRY_FIELD);
      Constraint useExpire = query.createConstraint(query.getStepField(useExpireField), FieldCompareConstraint.EQUAL,
            value);
      return useExpire;
   }


   public static Constraint getExpireConstraint(NodeQuery query, long date, NodeManager pageManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field expireField = pageManager.getField(EXPIREDATE_FIELD);
      Object expireDateObj = (expireField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);
      Constraint expirydate = query.createConstraint(query.getStepField(expireField), operator, expireDateObj);
      return expirydate;
   }


   public static Constraint getPublishConstraint(NodeQuery query, long date, NodeManager pageManager, boolean greater) {
      int operator = (greater ? FieldCompareConstraint.GREATER_EQUAL : FieldCompareConstraint.LESS_EQUAL);

      Field publishField = pageManager.getField(PUBLISHDATE_FIELD);
      Object publishDateObj = (publishField.getType() == Field.TYPE_DATETIME) ? new Date(date) : new Long(date);
      Constraint publishdate = query.createConstraint(query.getStepField(publishField), operator, publishDateObj);
      return publishdate;
   }


   public static void findPageNodes(Node node, List<Node> nodes, boolean withRelation, boolean remove) {
      if (!remove) {
         if (!nodes.contains(node)) {
            nodes.add(node);
         }
      }

      NodeIterator childs = node.getRelatedNodes("object", null, DESTINATION).nodeIterator();
      while (childs.hasNext()) {
         Node childNode = childs.nextNode();
         if (PortletUtil.isPortlet(childNode)) {
            if (!PortletUtil.isSinglePortlet(childNode)) {
               findPageNodes(childNode, nodes, withRelation, remove);
            }
         }
         else {
            if (PortletUtil.isParameter(childNode)) {
               findPageNodes(childNode, nodes, withRelation, remove);
            }
         }
      }

      if (withRelation) {
         RelationIterator relations = node.getRelations().relationIterator();
         while (relations.hasNext()) {
            Relation rel = (Relation) relations.next();
            if (!nodes.contains(rel)) {
               nodes.add(rel);
            }
         }
      }
      if (remove) {
         if (!nodes.contains(node)) {
            nodes.add(node);
         }
      }
   }

}
