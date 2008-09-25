package com.finalist.cmsc.navigation;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;

/**
 * Navigation manager implementations provide the sitemanagement system with information about a
 * particular NavigationItem type.
 */
public interface NavigationItemManager {

   /**
    * Name of the NavigationItem type
    * @return NavigationItem type
    */
   String getTreeManager();


   /**
    * Related types which are loaded inside the NavigationItem POJO
    * @return list of related types
    */
   List<String> getRelatedTypes();


   /**
    * Is this NavigationItem type the root type of the tree
    * @return <code>true</code> when root type
    */
   boolean isRoot();

   /**
    * POJO class for the NavigationItem type
    * @return POJO class
    */
   Class<? extends NavigationItem> getItemClass();


   /**
    * Delete Navigation node including related nodes from mmbase
    * @param node node to delete
    */
   void deleteNode(Node node);


   // PORTAL

   /**
    * A NavigationItemRenderer knows how to render mark-up for this type
    * @return NavigationItemRenderer
    */
   NavigationItemRenderer getRenderer();

   // SITEMANAGEMENT

   /**
    * Load NavigationItem instance based on provided node
    * @param node navigation node
    * @return NavigationItem instance
    */
   NavigationItem loadNavigationItem(Node node);


   /**
    * Find NavigationItem for a node of a related type
    * @param node the related node
    * @return NavigationItem node
    */
   Node findItemForRelatedNode(Node node);

   // EDITORS

   /**
    * A NavigationTreeItemRenderer is used to render an item in the tree in the editors
    * @return NavigationTreeItemRenderer
    */
   NavigationTreeItemRenderer getTreeRenderer();

   // PUBLISH SERVICE

   /**
    * An object which extends the Publisher and provides information about which nodes should be published
    * @param cloud Cloud which the publisher can use to lookup nodes to publish
    * @param type type for which the publisher is requested
    * @return publisher instance
    */
   Object getPublisher(Cloud cloud, String type);

}
