package com.finalist.cmsc.navigation;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.tree.SiteTreeItemExtensionRenderer;

public class SiteCopyNavigationItemManager implements NavigationItemManager {

   private NavigationItemRenderer renderer = new SiteCopyNavigationRenderer();

   private NavigationTreeItemRenderer treeRenderer = new SiteTreeItemExtensionRenderer();

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
      return SiteCopyUtil.TREEMANAGER;
   }

  

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#isRoot()
    */
   public boolean isRoot() {
      return false;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getTreeRenderer()
    */
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

   public void deleteNode(Node node) {
      // TODO Auto-generated method stub
      
   }

   public Node findItemForRelatedNode(Node node) {
      // TODO Auto-generated method stub
      return null;
   }

   public Class<? extends NavigationItem> getItemClass() {
      // TODO Auto-generated method stub
      return null;
   }

   public Object getPublisher(Cloud cloud, String type) {
      // TODO Auto-generated method stub
      return null;
   }

   public List<String> getRelatedTypes() {
      // TODO Auto-generated method stub
      return null;
   }

   public NavigationItem loadNavigationItem(Node node) {
      // TODO Auto-generated method stub
      return null;
   }

  
}
