package com.finalist.cmsc.favorites.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

public class FavoritesUtil {

   /**
    * Read all the favorites from the cloud for the given user
    * 
    * @param user
    * @return A list of Favorite objects
    */
   public static List<Favorite> getUserFavorites(String user) {
      ArrayList<Favorite> result = new ArrayList<Favorite>();

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager nodeManager = cloud.getNodeManager("favorite");
      NodeList nodeList = nodeManager.getList("user = '" + user + "'", null, null);
      for (NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         result.add(new Favorite(node.getNumber(), node.getStringValue("name"), node.getStringValue("url")));
      }

      return result;
   }


   /**
    * Removes the favorite only when the user of this favorite is the given
    * user.
    * 
    * @param user
    * @param number
    */
   public static void removeFavorite(String user, int number) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node node = cloud.getNode(number);
      if (node != null && node.getStringValue("user").equals(user)) {
         node.delete();
      }
   }


   /**
    * Create a favorite for a certain user
    * 
    * @param user
    * @param name
    * @param url
    */
   public static void addFavorite(String user, String name, String url) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager nodeManager = cloud.getNodeManager("favorite");
      Node newNode = nodeManager.createNode();
      newNode.setStringValue("user", user);
      newNode.setStringValue("name", name);
      newNode.setStringValue("url", url);
      newNode.commit();
   }

}
