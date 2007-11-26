package com.finalist.cmsc.portalImpl;

import java.util.Map;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.services.sitemanagement.SiteCache;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;

public class PageNavigationItemManager implements NavigationItemManager {

   private static Logger log = Logging.getLoggerInstance(PageNavigationItemManager.class.getName());

   private NavigationItemRenderer renderer = new PageNavigationRenderer();


   public Object getChild(Node parentNode, int index) {
      NodeList pages = NavigationUtil.getOrderedChildren(parentNode);
      if (pages.size() > index) {
         return pages.get(index);
      }
      return null;
   }


   public int getChildCount(Node parent) {
      return NavigationUtil.getChildCount(parent);
   }


   public NavigationItem getNavigationItem(String path) {
      NavigationItem navigationItem = SiteManagement.getNavigationItemFromPath(path);
      if (navigationItem instanceof Page) {
         return navigationItem;
      }
      return null;
   }


   public NavigationItem getNavigationItem(int number) {
      NavigationItem item = SiteManagement.getNavigationItem(number);
      if (item instanceof Page) {
         return item;
      }
      return null;
   }


   public NavigationItemRenderer getRenderer() {
      return renderer;
   }


   public String getFragementFieldname() {
      return PagesUtil.FRAGMENT_FIELD;
   }


   public String getTreeManager() {
      return PagesUtil.PAGE;
   }


   public void loadNavigationItems(SiteCache cache, Cloud cloud) {
      cache.loadNavigationItems(cloud, getTreeManager());
   }


   public void updateCache(Map<String, PageTree> trees, Integer key, String newFragment) {
      for (PageTree tree : trees.values()) {
         if (tree.containsPageTreeNode(key)) {
            tree.replace(key, newFragment);
         }
      }
   }


   public NavigationItem loadNavigationItem(Integer key, Node node) {
      if (node == null || !PagesUtil.isPageType(node)) {
         log.debug("Page not found: " + key);
         return null;
      }

      Page page = null;
      if (SiteUtil.isSite(node)) {
         page = (Page) MMBaseNodeMapper.copyNode(node, Site.class);
      }
      else {
         page = (Page) MMBaseNodeMapper.copyNode(node, Page.class);
      }

      RelationList rellist = PortletUtil.getPortletRelations(node);
      RelationIterator r = rellist.relationIterator();
      while (r.hasNext()) {
         Relation relation = r.nextRelation();
         Node relatedPortletNode = relation.getDestination();

         log
               .debug("portlet='" + relatedPortletNode.getNumber() + "' :"
                     + relatedPortletNode.getNodeManager().getName());
         String layoutid = relation.getStringValue(PortletUtil.LAYOUTID_FIELD);
         page.addPortlet(layoutid, relatedPortletNode.getNumber());
      }

      loadLayout(node, page);
      loadStylesheet(node, page);
      loadPageImages(node, page);
      return page;
   }


   private void loadLayout(Node pageNode, Page page) {
      Node layoutNode = PagesUtil.getLayout(pageNode);
      if (layoutNode != null) {
         page.setLayout(layoutNode.getNumber());
      }
      else {
         log.error("NO LAYOUT");
      }
   }


   private void loadStylesheet(Node pageNode, Page page) {
      NodeList styleNode = PagesUtil.getStylesheet(pageNode);
      if (!styleNode.isEmpty()) {
         for (NodeIterator iter = styleNode.nodeIterator(); iter.hasNext();) {
            Node stylesheetNode = iter.nextNode();
            page.addStylesheet(stylesheetNode.getNumber());
         }
      }
   }


   private void loadPageImages(Node pageNode, Page page) {
      RelationList relations = pageNode.getRelations(null, "images");
      for (RelationIterator iter = relations.relationIterator(); iter.hasNext();) {
         Relation relation = iter.nextRelation();
         String name = relation.getStringValue("name");

         // this is a bit of a hack, but saves on the loading of the actual node
         String image = "" + relation.getStringValue("dnumber");
         page.addPageImage(name, image);
      }
   }


   /**
    * publishing of sites and pages is done by the publish module
    */
   public Object getPublisher(Cloud cloud, String type) {
      return null;
   }

}
