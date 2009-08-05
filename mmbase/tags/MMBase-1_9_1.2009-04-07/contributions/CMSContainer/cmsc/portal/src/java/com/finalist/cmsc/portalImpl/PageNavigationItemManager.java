package com.finalist.cmsc.portalImpl;

import java.util.List;
import java.util.Map;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.navigation.tree.PageTreeItemRenderer;

/**
 * NavigationItemManager implementation for Page types.
 * This NavigationItemManager creates instance of Page class and is a base implementation for subtypes
 */
public class PageNavigationItemManager implements NavigationItemManager {

   private static final Logger log = Logging.getLoggerInstance(PageNavigationItemManager.class);

   private NavigationItemRenderer renderer = new PageNavigationRenderer();

   private NavigationTreeItemRenderer treeRenderer = new PageTreeItemRenderer();

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getRenderer()
    */
   public NavigationItemRenderer getRenderer() {
      return renderer;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getTreeManager()
    */
   public String getTreeManager() {
      return PagesUtil.PAGE;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getRelatedTypes()
    */
   public List<String> getRelatedTypes() {
      return null;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#isRoot()
    */
   public boolean isRoot() {
      return false;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#loadNavigationItem(org.mmbase.bridge.Node)
    */
   public NavigationItem loadNavigationItem(Node node) {
      if (!PagesUtil.isPageType(node)) {
         log.debug("Node is not a Page: " + node.getNumber());
         return null;
      }

      Class<? extends Page> clazz = getPageClass();

      Page page = MMBaseNodeMapper.copyNode(node, clazz);

      RelationList rellist = PortletUtil.getPortletRelations(node);
      RelationIterator r = rellist.relationIterator();
      while (r.hasNext()) {
         Relation relation = r.nextRelation();
         Node relatedPortletNode = relation.getDestination();

         log.debug("portlet='" + relatedPortletNode.getNumber() + "' :"
               + relatedPortletNode.getNodeManager().getName());
         String layoutid = relation.getStringValue(PortletUtil.LAYOUTID_FIELD);
         page.addPortlet(layoutid, relatedPortletNode.getNumber());
      }

      loadLayout(node, page);
      loadStylesheet(node, page);
      loadPageImages(node, page);
      return page;
   }

   protected void loadLayout(Node pageNode, Page page) {
      Node layoutNode = PagesUtil.getLayout(pageNode);
      if (layoutNode != null) {
         page.setLayout(layoutNode.getNumber());
      }
      else {
         log.error("NO LAYOUT");
      }
   }

   protected void loadStylesheet(Node pageNode, Page page) {
      NodeList styleNode = PagesUtil.getStylesheet(pageNode);
      if (!styleNode.isEmpty()) {
         for (NodeIterator iter = styleNode.nodeIterator(); iter.hasNext();) {
            Node stylesheetNode = iter.nextNode();
            page.addStylesheet(stylesheetNode.getNumber());
         }
      }
   }

   protected void loadPageImages(Node pageNode, Page page) {
      Map<String, List<Integer>> pageImages = PagesUtil.getPageImages(pageNode);
      page.setPageImages(pageImages);
   }

   /**
    * publishing of sites and pages is done by the publish module
    *
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getPublisher(org.mmbase.bridge.Cloud, java.lang.String)
    */
   public Object getPublisher(Cloud cloud, String type) {
      return null;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getTreeRenderer()
    */
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

   /**
    * POJO class for the Page type
    * @return POJO class
    */
   protected Class<? extends Page> getPageClass() {
      return Page.class;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getItemClass()
    */
   public Class<? extends NavigationItem> getItemClass() {
      return Page.class;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#deleteNode(org.mmbase.bridge.Node)
    */
   public void deleteNode(Node pageNode) {
      PagesUtil.deletePage(pageNode);
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#findItemForRelatedNode(org.mmbase.bridge.Node)
    */
   public Node findItemForRelatedNode(Node node) {
      return null;
   }

}
