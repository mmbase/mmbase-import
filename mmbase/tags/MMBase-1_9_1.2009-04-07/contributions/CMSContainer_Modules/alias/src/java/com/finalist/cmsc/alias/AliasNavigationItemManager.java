package com.finalist.cmsc.alias;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.alias.beans.om.Alias;
import com.finalist.cmsc.alias.publish.AliasPublisher;
import com.finalist.cmsc.alias.tree.AliasTreeItemRenderer;
import com.finalist.cmsc.alias.util.AliasUtil;
import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.*;

public class AliasNavigationItemManager implements NavigationItemManager {

   private static final Logger log = Logging.getLoggerInstance(AliasNavigationItemManager.class);

   private NavigationItemRenderer renderer = new AliasNavigationRenderer();

   private NavigationTreeItemRenderer treeRenderer = new AliasTreeItemRenderer();

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
      return AliasUtil.ALIAS;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getRelatedTypes()
    */
   public List<String> getRelatedTypes() {
      List<String> relatedTypes = new ArrayList<String>();
      relatedTypes.add(ResourcesUtil.URLS);
      return relatedTypes;
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
      if (!AliasUtil.isAliasType(node)) {
         log.debug("Node is not an Alias: " + node.getNumber());
         return null;
      }

      Alias alias = MMBaseNodeMapper.copyNode(node, Alias.class);

      Node page = AliasUtil.getPage(node);
      if (page != null) {
         alias.setPage(page.getNumber());
      }
      else {
         String externalUrl = AliasUtil.getUrlStr(node);
         if (StringUtils.isNotEmpty(externalUrl)) {
            alias.setUrl(externalUrl);
         }
      }

      return alias;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#getPublisher(org.mmbase.bridge.Cloud,
    *      java.lang.String)
    */
   public Object getPublisher(Cloud cloud, String type) {
      if (type.equals(getTreeManager())) {
         return new AliasPublisher(cloud);
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
      return Alias.class;
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#deleteNode(org.mmbase.bridge.Node)
    */
   public void deleteNode(Node pageNode) {
      pageNode.delete(true); // Also delete related items
   }

   /**
    * @see com.finalist.cmsc.navigation.NavigationItemManager#findItemForRelatedNode(org.mmbase.bridge.Node)
    */
   public Node findItemForRelatedNode(Node node) {
      NodeList aliases = node.getRelatedNodes(AliasUtil.ALIAS, "related", "source");
      if (!aliases.isEmpty()) {
         return aliases.getNode(0);
      }
      return null;
   }
}
