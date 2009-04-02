package com.finalist.cmsc.rssfeed;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.rssfeed.beans.om.RssFeed;
import com.finalist.cmsc.rssfeed.publish.RssFeedPublisher;
import com.finalist.cmsc.rssfeed.tree.RssFeedTreeItemRenderer;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;

public class RssFeedNavigationItemManager implements NavigationItemManager {

   private static final Logger log = Logging.getLoggerInstance(RssFeedNavigationItemManager.class);

   private NavigationItemRenderer renderer = new RssFeedNavigationRenderer();

   private NavigationTreeItemRenderer treeRenderer = new RssFeedTreeItemRenderer();

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
      return RssFeedUtil.RSSFEED;
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
      if (!RssFeedUtil.isRssFeedType(node)) {
         log.debug("Node is not a RSS Feed: " + node.getNumber());
         return null;
      }

      RssFeed rssFeed = MMBaseNodeMapper.copyNode(node, RssFeed.class);

      List<String> types = RssFeedUtil.getAllowedTypes(node);
      for (String type : types) {
         rssFeed.addContenttype(type);
      }

      Node contentChannel = RssFeedUtil.getContentChannel(node);
      if (contentChannel != null) {
         rssFeed.setContentChannel(contentChannel.getNumber());
      }

      return rssFeed;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getPublisher(org.mmbase.bridge.Cloud, java.lang.String)
    */
   public Object getPublisher(Cloud cloud, String type) {
      if (type.equals(getTreeManager())) {
         return new RssFeedPublisher(cloud);
      }
      else {
         return null;
      }
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getTreeRenderer()
    */
   public NavigationTreeItemRenderer getTreeRenderer() {
      return treeRenderer;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getItemClass()
    */
   public Class<? extends NavigationItem> getItemClass() {
      return RssFeed.class;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#deleteNode(org.mmbase.bridge.Node)
    */
   public void deleteNode(Node pageNode) {
      pageNode.delete(true);
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#findItemForRelatedNode(org.mmbase.bridge.Node)
    */
   public Node findItemForRelatedNode(Node node) {
      return null;
   }

}
