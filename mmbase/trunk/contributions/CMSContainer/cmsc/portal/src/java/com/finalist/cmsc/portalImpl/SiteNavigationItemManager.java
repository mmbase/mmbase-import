package com.finalist.cmsc.portalImpl;

import java.util.Iterator;
import java.util.Map;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.services.sitemanagement.SiteCache;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;

public class SiteNavigationItemManager extends PageNavigationItemManager {
   private SiteNavigationRenderer renderer = new SiteNavigationRenderer();


   public NavigationItemRenderer getRenderer() {
      return renderer;
   }


   public NavigationItem getNavigationItem(String path) {
      NavigationItem navigationItem = SiteManagement.getNavigationItemFromPath(path);
      if (navigationItem instanceof Site) {
         return navigationItem;
      }
      return null;
   }


   public NavigationItem getNavigationItem(int number) {
      NavigationItem item = SiteManagement.getNavigationItem(number);
      if (item instanceof Site) {
         return item;
      }
      return null;
   }


   /**
    * make sure that pages do not get counted twice
    */
   public int getChildCount(Node parent) {
      return 0;
   }


   public String getFragementFieldname() {
      return SiteUtil.FRAGMENT_FIELD;
   }


   public String getTreeManager() {
      return SiteUtil.SITE;
   }


   @SuppressWarnings("unchecked")
   public void loadNavigationItems(SiteCache cache, Cloud cloud) {
      NodeList sites = SiteUtil.getSites(cloud);
      for (Iterator<Node> iter = sites.iterator(); iter.hasNext();) {
         Node siteNode = iter.next();

         Site site = (Site) MMBaseNodeMapper.copyNode(siteNode, Site.class);
         int siteId = site.getId();
         String sitefragment = site.getUrlfragment();
         cache.createTree(siteId, sitefragment);
      }
   }


   public void updateCache(Map<String, PageTree> trees, Integer key, String newFragment) {
      for (PageTree tree : trees.values()) {
         if (tree.containsPageTreeNode(key)) {
            trees.remove(tree.getRoot().getPathStr().toLowerCase());
            trees.put(newFragment.toLowerCase(), tree);
            tree.replace(key, newFragment);
         }
      }
   }
}
