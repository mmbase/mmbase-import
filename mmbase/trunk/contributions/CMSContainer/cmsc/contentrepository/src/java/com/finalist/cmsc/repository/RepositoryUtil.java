/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.mmapps.commons.bridge.CloneUtil;
import net.sf.mmapps.commons.bridge.NodeFieldComparator;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.FieldValueDateConstraint;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueDateConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.richtext.RichText;
import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.security.forms.RolesInfo;

public final class RepositoryUtil {

   private static final String DESTINATION_LOWER = "destination";

   public static final String NAME_FIELD = "name";

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(RepositoryUtil.class.getName());

   private static final String SOURCE = "SOURCE";
   private static final String DESTINATION = "DESTINATION";

   public static final String TITLE_FIELD = NAME_FIELD;
   public static final String FRAGMENT_FIELD = "pathfragment";

   public static final String CONTENTCHANNEL = "contentchannel";
   public static final String COLLECTIONCHANNEL = "collectionchannel";
   public static final String CONTENTELEMENT = ContentElementUtil.CONTENTELEMENT;
   public static final String ASSETELEMENT = AssetElementUtil.ASSETELEMENT;

   public static final String CHILDREL = "childrel";
   public static final String COLLECTIONREL = "collectionrel";
   public static final String CONTENTREL = "contentrel";
   public static final String DELETIONREL = "deletionrel";
   public static final String CREATIONREL = "creationrel";

   public static final String ALIAS_ROOT = "repository.root";
   public static final String ALIAS_TRASH = "repository.trash";

   /**
    * The first key in this LinkedMap is the root nodemanager of the tree.
    */
   private static LinkedHashMap<String, String> treeManagers = new LinkedHashMap<String, String>();

   /**
    * Return node managers names and fields which are involved in the navigation tree The key is the nodemanager name.
    * The value is the field for the path fragment The first element is the root node manager in the tree.
    * 
    * @return Map with nodemanager names and fields
    */
   public static LinkedHashMap<String, String> getTreeManagers() {
      return treeManagers;
   }

   /**
    * This method is used on startup of MMBase to fill the information about treeManagers and path fragment fields This
    * method is synchronized on the class (static method) to make sure only one managers is added at the same time.
    * 
    * @param manager
    *           name of nodemanager which is used in the tree
    * @param fragmentFieldname
    *           name of field which is used in the path of a tree item
    * @param root
    *           This nodemanager maintains the nodes which are root tree items
    */
   public static synchronized void registerTreeManager(String manager, String fragmentFieldname, boolean root) {
      if (root) {
         LinkedHashMap<String, String> tempManagers = new LinkedHashMap<String, String>(treeManagers);
         treeManagers.clear();
         treeManagers.put(manager, fragmentFieldname);
         treeManagers.putAll(tempManagers);
      } else {
         treeManagers.put(manager, fragmentFieldname);
      }
   }

   private RepositoryUtil() {
      // utility
   }

   public static NodeManager getNodeManager(Cloud cloud) {
      return TreeUtil.getNodeManager(cloud, CONTENTCHANNEL);
   }

   public static RelationManager getRelationManager(Cloud cloud) {
      return TreeUtil.getRelationManager(cloud, CONTENTCHANNEL, CHILDREL);
   }

   public static boolean isChannel(Node node) {
      return isContentChannel(node) || isCollectionChannel(node);
   }

   public static boolean isChannel(String node) {
      Node channel = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNode(node);
      return isChannel(channel);
   }

   public static boolean isContentChannel(Node node) {
      return CONTENTCHANNEL.equals(node.getNodeManager().getName());
   }

   public static boolean isContentChannel(String node) {
      Node channel = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNode(node);
      return isContentChannel(channel);
   }

   public static boolean isCollectionChannel(Node node) {
      return COLLECTIONCHANNEL.equals(node.getNodeManager().getName());
   }

   public static boolean isCollectionChannel(String node) {
      Node channel = CloudProviderFactory.getCloudProvider().getAnonymousCloud().getNode(node);
      return isCollectionChannel(channel);
   }

   public static NodeList getCollectionChannels(Node contentchannel) {
      return contentchannel.getRelatedNodes(COLLECTIONCHANNEL, null, null);
   }

   /**
    * gets the root number
    * 
    * @param cloud
    *           - MMbase cloud
    * @return root node number
    */
   public static String getRoot(Cloud cloud) {
      return getRootNode(cloud).getStringValue("number");
   }

   /**
    * gets the root node
    * 
    * @param cloud
    *           - MMbase cloud
    * @return root node
    */
   public static Node getRootNode(Cloud cloud) {
      return cloud.getNodeByAlias(ALIAS_ROOT);
   }

   public static boolean isRoot(Node node) {
      return node.getNumber() == getRootNode(node.getCloud()).getNumber();
   }

   public static boolean isRoot(Cloud cloud, String number) {
      if (ALIAS_ROOT.equals(number)) {
         return true;
      }
      return number.equals(String.valueOf(getRootNode(cloud).getNumber()));
   }

   public static boolean isRoot(Cloud cloud, int number) {
      return number == getRootNode(cloud).getNumber();
   }

   /**
    * gets the Trash number
    * 
    * @param cloud
    *           - MMbase cloud
    * @return trash node number
    */
   public static String getTrash(Cloud cloud) {
      return getTrashNode(cloud).getStringValue("number");
   }

   /**
    * gets the Trash node
    * 
    * @param cloud
    *           - MMbase cloud
    * @return trash node
    */
   public static Node getTrashNode(Cloud cloud) {
      return cloud.getNodeByAlias(ALIAS_TRASH);
   }

   public static boolean isTrash(Node node) {
      return node.getNumber() == getTrashNode(node.getCloud()).getNumber();
   }

   public static boolean isTrash(Cloud cloud, String number) {
      if (ALIAS_TRASH.equals(number)) {
         return true;
      }
      return number.equals(String.valueOf(getTrashNode(cloud).getNumber()));
   }

   public static boolean isTrash(Cloud cloud, int number) {
      return number == getTrashNode(cloud).getNumber();
   }

   public static void appendChild(Cloud cloud, String parent, String child) {
      Node parentNode = cloud.getNode(parent);
      Node childNode = cloud.getNode(child);
      appendChild(parentNode, childNode);
   }

   public static void appendChild(Node parentNode, Node childNode) {
      TreeUtil.uniqueChild(parentNode, childNode, treeManagers, CHILDREL);
      TreeUtil.appendChild(parentNode, childNode, CHILDREL);
   }

   public static Node getParent(Node node) {
      return TreeUtil.getParent(node, treeManagers, CHILDREL);
   }

   public static Relation getParentRelation(Node node) {
      return TreeUtil.getParentRelation(node, TreeUtil.convertToList(treeManagers), CHILDREL);
   }

   /**
    * check that the source item is not a parent of the destination item
    * 
    * @param sourceChannel
    *           - source item
    * @param destChannel
    *           - destination item
    * @return <code>true</code> when the source item is a parent
    */
   public static boolean isParent(Node sourceChannel, Node destChannel) {
      return TreeUtil.isParent(sourceChannel, destChannel, TreeUtil.convertToList(treeManagers), CHILDREL);
   }

   public static String getFragmentFieldname(Node parentNode) {
      return TreeUtil.getFragmentFieldname(parentNode.getNodeManager().getName(), treeManagers);
   }

   /**
    * Find path to root
    * 
    * @param node
    *           - node
    * @return List with the path to the root. First item is the root and last is the node
    */
   public static List<Node> getPathToRoot(Node node) {
      return TreeUtil.getPathToRoot(node, treeManagers, CHILDREL);
   }

   /**
    * Creates a string that represents the root path.
    * 
    * @param cloud
    *           - MMbase cloud
    * @param node
    *           - MMbase node
    * @return path to root
    */
   public static String getPathToRootString(Cloud cloud, String node) {
      return getPathToRootString(cloud.getNode(node));
   }

   /**
    * Creates a string that represents the root path.
    * 
    * @param node
    *           - MMbase node
    * @return path to root
    */
   public static String getPathToRootString(Node node) {
      return getPathToRootString(node, true);
   }

   /**
    * Creates a string that represents the root path.
    * 
    * @param node
    *           - MMbase node
    * @param includeRoot
    *           - include the root pathfragment
    * @return path to root
    */
   public static String getPathToRootString(Node node, boolean includeRoot) {
      return TreeUtil.getPathToRootString(node, treeManagers, CHILDREL, includeRoot);
   }

   /**
    * Creates a string that represents the titles.
    * 
    * @param cloud
    *           - MMbase cloud
    * @param node
    *           - node number
    * @return titles of nodes in path
    */
   public static String getTitlesString(Cloud cloud, String node) {
      return getTitlesString(cloud, node, true);
   }

   /**
    * Creates a string that represents the titles.
    * 
    * @param cloud
    *           - MMbase cloud
    * @param node
    *           - node number
    * @param includeRoot
    *           - include the root node
    * @return titles of nodes in path
    */
   public static String getTitlesString(Cloud cloud, String node, boolean includeRoot) {
      return getTitlesString(cloud.getNode(node), includeRoot);
   }

   /**
    * Creates a string that represents the titles.
    * 
    * @param node
    *           - MMbase node
    * @param includeRoot
    *           - include the root node
    * @return titles of nodes in path
    */
   public static String getTitlesString(Node node, boolean includeRoot) {
      return TreeUtil.getTitlesString(node, treeManagers, CHILDREL, TITLE_FIELD, includeRoot);
   }

   /**
    * Method that finds the Channel node using a path as input.
    * 
    * @param cloud
    *           - MMbase cloud
    * @param path
    *           - path of channel
    * @return node with channel path
    */
   public static Node getChannelFromPath(Cloud cloud, String path) {
      return getChannelFromPath(cloud, path, getRootNode(cloud));
   }

   /**
    * Method that finds the Channel node using a path as input.
    * 
    * @param cloud
    *           - MMbase cloud
    * @param path
    *           - path of channel
    * @param root
    *           - node to start search
    * @return node with channel path
    */
   public static Node getChannelFromPath(Cloud cloud, String path, Node root) {
      return getChannelFromPath(cloud, path, root, true);
   }

   /**
    * Method that finds the Channel node using a path as input.
    * 
    * @param cloud
    *           - MMbase cloud
    * @param path
    *           - path of channel
    * @param root
    *           - node to start search
    * @param useCache
    *           - use path cache
    * @return node with channel path
    */
   public static Node getChannelFromPath(Cloud cloud, String path, Node root, boolean useCache) {
      Node node = TreeUtil.getTreeItemFromPath(cloud, path, root, treeManagers, CHILDREL, useCache);
      return node;
   }

   /**
    * Get child channel nodes
    * 
    * @param parentNode
    *           - parent
    * @return List of children
    */
   public static NodeList getChildren(Node parentNode) {
      return TreeUtil.getChildren(parentNode, TreeUtil.convertToList(treeManagers), CHILDREL);
   }

   public static boolean hasChild(Node parentChannel, String fragment) {
      return TreeUtil.hasChild(parentChannel, fragment, treeManagers, CHILDREL);
   }

   public static Node getChild(Node parentChannel, String fragment) {
      return TreeUtil.getChild(parentChannel, fragment, treeManagers, CHILDREL);
   }

   /**
    * Reorder content in channel
    * 
    * @param cloud
    *           - MMbase cloud
    * @param parentNode
    *           - parent
    * @param children
    *           - new order
    * @return changed numbers
    */
   public static List<Integer> reorderContent(Cloud cloud, String parentNode, String children) {
      Node parent = cloud.getNode(parentNode);
      return RelationUtil.reorder(parent, children, CONTENTREL, CONTENTELEMENT);
   }

   /**
    * Reorder content in channel
    * 
    * @param cloud
    *           - MMbase cloud
    * @param parentNode
    *           - parent
    * @param children
    *           - new order
    * @return changed numbers
    */
   public static List<Integer> reorderContent(Cloud cloud, String parentNode, String[] children) {
      Node parent = cloud.getNode(parentNode);
      return RelationUtil.reorder(parent, children, CONTENTREL, CONTENTELEMENT);
   }

   /**
    * Reorder content in channel
    * 
    * @param cloud
    *           - MMbase cloud
    * @param parentNode
    *           - parent
    * @param children
    *           - new order
    * @param offset
    *           - start reorder from this index
    * @return changed numbers
    */
   public static List<Integer> reorderContent(Cloud cloud, String parentNode, String[] children, int offset) {
      Node parent = cloud.getNode(parentNode);
      return RelationUtil.reorder(parent, children, CONTENTREL, CONTENTELEMENT, offset);
   }

   /**
    * Get sorted ContentChannel child nodes
    * 
    * @param parentNode
    *           - parent
    * @return list of sorted children
    */
   public static NodeList getContentChannelOrderedChildren(Node parentNode) {
      return SearchUtil.findRelatedOrderedNodeList(parentNode, CONTENTCHANNEL, CHILDREL, NAME_FIELD);
   }

   /**
    * Get sorted child nodes
    * 
    * @param parentNode
    *           - parent
    * @return list of sorted children
    */
   public static NodeList getOrderedChildren(Node parentNode) {
      NodeList channels = SearchUtil.findRelatedNodeList(parentNode, "object", CHILDREL);
      Collections.sort(channels, new NodeFieldComparator(NAME_FIELD));
      return channels;
   }

   /**
    * Depth of path
    * 
    * @param path
    *           - path
    * @return level
    */
   public static int getLevel(String path) {
      return TreeUtil.getLevel(path);
   }

   /**
    * Get number of children
    * 
    * @param parent
    *           - parent
    * @return number of children
    */
   public static int getChildCount(Node parent) {
      return TreeUtil.getChildCount(parent, "object", CHILDREL);
   }

   /**
    * Get number of ContentChannel children
    * 
    * @param parent
    *           - parent
    * @return number of children
    */
   public static int getContentChannelChildCount(Node parent) {
      return TreeUtil.getChildCount(parent, CONTENTCHANNEL, CHILDREL);
   }

   /**
    * Create the relation to the creationchannel.
    * 
    * @param content
    *           - Content Node
    * @param channelNumber
    *           - String channel number
    */
   public static void addCreationChannel(Node content, String channelNumber) {
      addCreationChannel(content, content.getCloud().getNode(channelNumber));
   }

   /**
    * Create the relation to the creationchannel.
    * 
    * @param content
    *           - Content Node
    * @param channel
    *           - Channel Node
    */
   public static void addCreationChannel(Node content, Node channel) {
      log.debug("Creationchannel " + channel.getNumber());
      RelationManager creationChannel = content.getCloud().getRelationManager(CONTENTELEMENT, CONTENTCHANNEL,
            CREATIONREL);
      content.createRelation(channel, creationChannel).commit();
   }

   /**
    * Check if a contentnode has a creationchannel
    * 
    * @param content
    *           - Content Node
    * @return true if the node has a related creation channel
    */
   public static boolean hasCreationChannel(Node content) {
      int count = content
            .countRelatedNodes(content.getCloud().getNodeManager(CONTENTCHANNEL), CREATIONREL, DESTINATION);
      return count > 0;
   }
   /**
    * Check if a contentnode has a creationchannel
    * 
    * @param content
    *           - Content Node
    * @return true if the node has a related creation channel
    */
   public static boolean hasCreationChannel(Node content,Node channel) {
      if(!isChannel(channel)) {
         return false;
      }
      NodeList channels = content.getRelatedNodes(content.getCloud().getNodeManager(CONTENTCHANNEL), CREATIONREL, DESTINATION);
      if(channels == null || channels.size()  < 1) {
         return false;
      }
      Node parentChannel = channels.getNode(0);
      if(parentChannel.getNumber() == channel.getNumber()) {
         return true;
      }
      return false;
   }
   /**
    * Get creation channel
    * 
    * @param content
    *           - Content Node
    * @return Creation channel
    */
   public static Node getCreationChannel(Node content) {
      NodeList list = content.getRelatedNodes(CONTENTCHANNEL, CREATIONREL, DESTINATION);
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }

   public static boolean isCreationChannel(Node content, Node channelNode) {
      Node creationNode = getCreationChannel(content);
      return (creationNode != null) && (creationNode.getNumber() == channelNode.getNumber());
   }

   /**
    * Remove creation relations for the given contentelement
    * 
    * @param content
    *           A contentelment
    */
   public static void removeCreationRelForContent(Node content) {
      if (!ContentElementUtil.isContentElement(content)) {
         throw new IllegalArgumentException("Only contentelements are allowed.");
      }
      RelationList list = content.getRelations(CREATIONREL, null, DESTINATION);
      for (int i = 0; i < list.size(); i++) {
         Relation creationrel = list.getRelation(i);
         creationrel.delete();
      }
   }

   /**
    * Remove creation relations for the given assetelement
    * 
    * @param content
    *           A assetelment
    */
   public static void removeCreationRelForAsset(Node asset) {
      if (!AssetElementUtil.isAssetElement(asset)) {
         throw new IllegalArgumentException("Only assetelements are allowed.");
      }
      RelationList list = asset.getRelations(CREATIONREL, null, DESTINATION);
      for (int i = 0; i < list.size(); i++) {
         Relation creationrel = list.getRelation(i);
         creationrel.delete();
      }
   }

   /**
    * Create the relation to the creationchannel.
    * 
    * @param content
    *           - Content Node
    * @param channelNumber
    *           - String channel number
    */
   public static void addContentToChannel(Node content, String channelNumber) {
      Node channelNode = content.getCloud().getNode(channelNumber);
      addContentToChannel(content, channelNode);
   }

   public static void addContentToChannel(Node content, Node channelNode) {
      if (!isLinkedToChannel(content, channelNode)) {
         Cloud cloud = content.getCloud();

         // set the creationchannel if it does not have one (it was an orphan)
         // or if the creationchannel is the trash channel
         Node creationNode = getCreationChannel(content);
         boolean isOrphan = (creationNode == null);
         if (!isOrphan) {
            if (isTrash(creationNode)) {
               isOrphan = true;
               removeCreationRelForContent(content);
               removeContentFromChannel(content, getTrashNode(cloud));
            }
         }

         NodeManager nm = cloud.getNodeManager(CONTENTELEMENT);
         RelationManager rm = cloud.getRelationManager(CONTENTREL);

         int childCount = channelNode.countRelatedNodes(nm, CONTENTREL, DESTINATION);

         Relation relation = channelNode.createRelation(content, rm);
         relation.setIntValue(TreeUtil.RELATION_POS_FIELD, childCount + 1);
         relation.commit();

         if (isOrphan) {
            addCreationChannel(content, channelNode);
         }
         // remove delete relation with this channel, if any still exist
         RepositoryUtil.removeDeletionRels(content, channelNode);
      }
   }

   /**
    * Create the relation to the creationchannel.
    * 
    * @param asset
    *           - Asset Node
    * @param channelNumber
    *           - String channel number
    */
   public static void addAssetToChannel(Node asset, String channelNumber) {
      Node channelNode = asset.getCloud().getNode(channelNumber);
      addAssetToChannel(asset, channelNode);
   }

   public static void addAssetToChannel(Node asset, Node channelNode) {
      Cloud cloud = asset.getCloud();

      // set the creationchannel if it does not have one (it was an orphan)
      // or if the creationchannel is the trash channel
      Node creationNode = getCreationChannel(asset);
      boolean isOrphan = (creationNode == null);
      if (!isOrphan) {
         if (isTrash(creationNode)) {
            isOrphan = true;
            removeCreationRelForAsset(asset);
         }
      }

      if (isOrphan) {
         RelationManager creationChannel = cloud.getRelationManager(ASSETELEMENT, CONTENTCHANNEL, CREATIONREL);
         asset.createRelation(channelNode, creationChannel).commit();
      }
      // remove delete relation with this channel, if any still exist
      RepositoryUtil.removeDeletionRels(asset, channelNode);
   }

   public static boolean isLinkedToChannel(Node content, Node channelNode) {
      boolean isLinkedToChannel = false;
      NodeList channels = getContentChannelsForContent(content);
      for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
         Node channel = iter.next();
         if (channelNode.getNumber() == channel.getNumber()) {
            isLinkedToChannel = true;
         }
      }
      return isLinkedToChannel;
   }

   /**
    * Check if a contentnode has a contentchannel
    * 
    * @param content
    *           - Content Node
    * @return true if the node has a related workflowitem
    */
   public static boolean hasContentChannel(Node content) {
      int count = content.countRelatedNodes(content.getCloud().getNodeManager(CONTENTCHANNEL), CONTENTREL, SOURCE);
      return count > 0;
   }

   public static NodeList getContentChannelsForContent(Node node) {
      return node.getRelatedNodes(CONTENTCHANNEL, CONTENTREL, SOURCE);
   }

   public static NodeList getContentChannelsForCollection(Node node) {
      return node.getRelatedNodes(CONTENTCHANNEL, COLLECTIONREL, DESTINATION);
   }

   public static boolean hasCreatedContent(Node channelNode) {
      // check if the content channel has related content elements
      return countCreatedContent(channelNode) != 0;
   }

   public static int countCreatedContent(Node channelNode) {
      return channelNode.countRelatedNodes(channelNode.getCloud().getNodeManager(CONTENTELEMENT), CREATIONREL, SOURCE);
   }

   public static NodeList getCreatedElements(Node channel) {
      return channel.getRelatedNodes(CONTENTELEMENT, CREATIONREL, SOURCE);
   }

   public static NodeList getCreatedAssetElements(Node channel) {
      return channel.getRelatedNodes(ASSETELEMENT, CREATIONREL, SOURCE);
   }

   public static NodeList getCreatedAssets(Node channel) {
      return channel.getRelatedNodes(ASSETELEMENT, CREATIONREL, SOURCE);
   }

   public static NodeList getCreatedAssets(Node channel, String assettype) {
      return channel.getRelatedNodes(assettype, CREATIONREL, SOURCE);
   }

   public static boolean hasLinkedContent(Node channelNode) {
      // check if the content channel has related content elements
      return countLinkedContent(channelNode) != 0;
   }

   public static int countLinkedContent(Node channelNode) {
      int contentCount = channelNode.countRelatedNodes(channelNode.getCloud().getNodeManager(CONTENTELEMENT),
            CONTENTREL, DESTINATION);
      return contentCount;
   }

   public static int countLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, int offset, int maxNumber, int year, int month, int day) {
      NodeQuery query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, null, offset,
            maxNumber, year, month, day);
      return Queries.count(query);
   }

   public static int countLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
      NodeQuery query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, archive,
            offset, maxNumber, year, month, day);
      return Queries.count(query);
   }

   public static int countCreatedAsset(Node channelNode) {
      int contentCount = channelNode.countRelatedNodes(channelNode.getCloud().getNodeManager(ASSETELEMENT),
            CREATIONREL, SOURCE);
      return contentCount;
   }

   public static int countCreatedAsset(Node channel, List<String> assettypes, String orderby, String direction,
         boolean useLifecycle, int offset, int maxNumber, int year, int month, int day) {
      NodeQuery query = createCreatedAssetQuery(channel, assettypes, orderby, direction, useLifecycle, null, offset,
            maxNumber, year, month, day);
      return Queries.count(query);
   }

   public static int countCreatedAsset(Node channel, List<String> assettypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
      NodeQuery query = createCreatedAssetQuery(channel, assettypes, orderby, direction, useLifecycle, archive, offset,
            maxNumber, year, month, day);
      return Queries.count(query);
   }

   public static NodeList getLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, int offset, int maxNumber, int year, int month, int day) {
      NodeList elements = getLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, null, offset,
            maxNumber, year, month, day);
      return elements;
   }

   public static NodeList getLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
      NodeList elements = getLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset,
            maxNumber, year, month, day, null);
      return elements;
   }

   public static NodeList getLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day,
         HashMap<String, Object> extraParameters) {
      NodeQuery query;
      NodeList elements;
      // CMSC-1313 Sorting on TYPE should not sort of the otype value but the title of the type
      if(orderby != null && "otype".equals(orderby)){
         query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, archive,
               SearchQuery.DEFAULT_OFFSET, SearchQuery.DEFAULT_MAX_NUMBER, year, month, day, extraParameters);
         elements = query.getNodeManager().getList(query);
         boolean reverse = false;
         if ("DOWN".equalsIgnoreCase(direction)) {
            reverse = true;
         }
         Collections.sort(elements, new NodeGUITypeComparator(query.getCloud().getLocale(), reverse));
         int toIndex = elements.size()<(offset+maxNumber)? elements.size(): (offset+maxNumber);
         elements = elements.subNodeList(offset, toIndex);
      }else {
         query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, archive,
            offset, maxNumber, year, month, day, extraParameters);
         elements = query.getNodeManager().getList(query);
      }
      return elements;
   }

   public static NodeQuery createLinkedContentQuery(Node channel, List<String> contenttypes, String orderby,
         String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
      return createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, archive, offset,
            maxNumber, year, month, day, null);
   }

   public static NodeQuery createLinkedContentQuery(Node channel, List<String> contenttypes, String orderby,
         String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month,
         int day, HashMap<String, Object> extraParameters) {
      String destinationManager = CONTENTELEMENT;

      if (contenttypes != null && contenttypes.size() == 1) {
         destinationManager = contenttypes.get(0);
      }

      NodeQuery query;
      if (isContentChannel(channel)) {
         if (orderby == null) {
            orderby = CONTENTREL + ".pos";
         }
         query = SearchUtil.createRelatedNodeListQuery(channel, destinationManager, CONTENTREL, null, null, orderby,
               direction);
      } else {
         if (orderby == null) {
            orderby = CONTENTREL + ".pos";
         }

         NodeList contentchannels = SearchUtil.findRelatedNodeList(channel, CONTENTCHANNEL, COLLECTIONREL);
         if (contentchannels.isEmpty()) {
            throw new IllegalArgumentException("contentchannels or collectionchannel is empty; should be at least one.");
         }
         query = SearchUtil.createRelatedNodeListQuery(contentchannels, destinationManager, CONTENTREL);
         SearchUtil.addFeatures(query, contentchannels.getNode(0), destinationManager, CONTENTREL, null, null, orderby,
               direction);
      }

      if (contenttypes != null && contenttypes.size() > 1) {
         SearchUtil.addTypeConstraints(query, contenttypes);
      }

      // Precision of now is based on minutes.
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Long date = cal.getTimeInMillis();

      if (useLifecycle) {
         ContentElementUtil.addLifeCycleConstraint(query, date);
      }
      if (StringUtils.isNotEmpty(archive)) {
         ContentElementUtil.addArchiveConstraint(channel, query, date, archive);
      }

      if (year != -1 || month != -1 || day != -1) {
         Field field = query.getCloud().getNodeManager("contentelement").getField("publishdate");
         StepField basicStepField = query.getStepField(field);
         if (year != -1) {
            SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(year),
                  FieldValueDateConstraint.YEAR));
         }
         if (month != -1) {
            SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(month),
                  FieldValueDateConstraint.MONTH));
         }
         if (day != -1) {
            SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(day),
                  FieldValueDateConstraint.DAY_OF_MONTH));
         }
      }

      if (extraParameters != null) {
         for (String key : extraParameters.keySet()) {
            Object value = extraParameters.get(key);
            Field field = query.getCloud().getNodeManager("contentelement").getField(key);
            StepField basicStepField = query.getStepField(field);
            SearchUtil.addConstraint(query, new BasicFieldValueConstraint(basicStepField, value));
         }
      }

      SearchUtil.addLimitConstraint(query, offset, maxNumber);
      return query;
   }

   public static NodeList getCreatedAssets(Node channel, List<String> assettypes, String orderby, String direction,
         boolean useLifecycle, int offset, int maxNumber, int year, int month, int day) {
      NodeList elements = getCreatedAssets(channel, assettypes, orderby, direction, useLifecycle, null, offset,
            maxNumber, year, month, day);
      return elements;
   }

   public static NodeList getCreatedAssets(Node channel, List<String> assettypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
      NodeList elements = getCreatedAssets(channel, assettypes, orderby, direction, useLifecycle, archive, offset,
            maxNumber, year, month, day, null);
      return elements;
   }

   public static NodeList getCreatedAssets(Node channel, List<String> assettypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day,
         HashMap<String, Object> extraParameters) {
      NodeQuery query;
      NodeList elements;
      // CMSC-1313 Sorting on TYPE should not sort of the otype value but the title of the type
      if(orderby != null && "otype".equals(orderby)){
         query = createCreatedAssetQuery(channel, assettypes, orderby, direction, useLifecycle, archive,
               SearchQuery.DEFAULT_OFFSET, SearchQuery.DEFAULT_MAX_NUMBER, year, month, day, extraParameters);
         elements = query.getNodeManager().getList(query);
         boolean reverse = false;
         if ("DOWN".equalsIgnoreCase(direction)) {
            reverse = true;
         }
         Collections.sort(elements, new NodeGUITypeComparator(query.getCloud().getLocale(), reverse));
         int toIndex = elements.size()<(offset+maxNumber)? elements.size(): (offset+maxNumber);
         elements = elements.subNodeList(offset, toIndex);
      }else {
         query = createCreatedAssetQuery(channel, assettypes, orderby, direction, useLifecycle, archive,
            offset, maxNumber, year, month, day, extraParameters);
         elements = query.getNodeManager().getList(query);
      }
      return elements;
   }

   public static NodeQuery createCreatedAssetQuery(Node channel, List<String> assettypes, String orderby,
         String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
      return createCreatedAssetQuery(channel, assettypes, orderby, direction, useLifecycle, archive, offset, maxNumber,
            year, month, day, null);
   }

   public static NodeQuery createCreatedAssetQuery(Node channel, List<String> assettypes, String orderby,
         String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month,
         int day, HashMap<String, Object> extraParameters) {
      String sourceManager = ASSETELEMENT;

      if (assettypes != null && assettypes.size() == 1) {
         sourceManager = assettypes.get(0);
      }

      NodeQuery query;
      if (isContentChannel(channel)) {
         query = SearchUtil.createRelatedNodeListQuery(channel, sourceManager, CREATIONREL, null, null, orderby,
               direction, SOURCE);
      } else {
         NodeList contentchannels = SearchUtil.findRelatedNodeList(channel, CONTENTCHANNEL, COLLECTIONREL);
         if (contentchannels.isEmpty()) {
            throw new IllegalArgumentException("contentchannels or collectionchannel is empty; should be at least one.");
         }
         query = SearchUtil.createRelatedNodeListQuery(contentchannels, sourceManager, CREATIONREL);
         SearchUtil.addFeatures(query, contentchannels.getNode(0), sourceManager, CREATIONREL, null, null, orderby,
               direction);
      }

      if (assettypes != null && assettypes.size() > 1) {
         SearchUtil.addTypeConstraints(query, assettypes);
      }

      // Precision of now is based on minutes.
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Long date = cal.getTimeInMillis();

      if (useLifecycle) {
         AssetElementUtil.addLifeCycleConstraint(query, date);
      }
      if (StringUtils.isNotEmpty(archive)) {
         AssetElementUtil.addArchiveConstraint(channel, query, date, archive);
      }

      if (year != -1 || month != -1 || day != -1) {
         Field field = query.getCloud().getNodeManager("assetelement").getField("publishdate");
         StepField basicStepField = query.getStepField(field);
         if (year != -1) {
            SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(year),
                  FieldValueDateConstraint.YEAR));
         }
         if (month != -1) {
            SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(month),
                  FieldValueDateConstraint.MONTH));
         }
         if (day != -1) {
            SearchUtil.addConstraint(query, new BasicFieldValueDateConstraint(basicStepField, Integer.valueOf(day),
                  FieldValueDateConstraint.DAY_OF_MONTH));
         }
      }

      if (extraParameters != null) {
         for (String key : extraParameters.keySet()) {
            Object value = extraParameters.get(key);
            Field field = query.getCloud().getNodeManager("assetelement").getField(key);
            StepField basicStepField = query.getStepField(field);
            SearchUtil.addConstraint(query, new BasicFieldValueConstraint(basicStepField, value));
         }
      }

      SearchUtil.addLimitConstraint(query, offset, maxNumber);
      return query;
   }

   public static NodeList getLinkedElements(Node channel) {
      return getLinkedElements(channel, null, null, null, false, -1, -1, -1, -1, -1);
   }

   public static void removeContentFromChannel(Node content, Node channelNode) {
      // remove the relations to this content from this channel
      RelationIterator contentRelIter = content.getRelations(CONTENTREL, getNodeManager(content.getCloud()), SOURCE)
            .relationIterator();
      while (contentRelIter.hasNext()) {
         Relation relation = contentRelIter.nextRelation();
         if (relation.getDestination().equals(channelNode) || relation.getSource().equals(channelNode)) {
            relation.delete(true);
            break;
         }
      }
      RepositoryUtil.addDeletionRelation(content, channelNode);
   }

   public static void removeContentFromAllChannels(Node content) {
      RelationIterator contentRelIter = content.getRelations(CONTENTREL, getNodeManager(content.getCloud()), SOURCE)
            .relationIterator();
      while (contentRelIter.hasNext()) {
         Relation relation = contentRelIter.nextRelation();
         RepositoryUtil.addDeletionRelation(content, relation.getSource());
         relation.delete(true);
      }
   }

   /**
    * Makes sure the supplied contentelement will have at least one content and one creation channel. If the node has to
    * little, it will relate to the trash contentchannel.
    * 
    * @param contentelement
    *           The contentelement to repair the channels of.
    */
   public static void repairChannels(Node contentelement) {
      repairCreationChannel(contentelement);
      repairContentChannels(contentelement);
   }

   /**
    * Makes sure the supplied contentelement will have at least one creation channel. If the node has none, it will
    * relate to the trash channel.
    * 
    * @param contentelement
    *           The contentelement to repair
    */
   public static void repairCreationChannel(Node contentelement) {
      if (!hasCreationChannel(contentelement)) {
         Node trashChannel = getTrashNode(contentelement.getCloud());
         addCreationChannel(contentelement, trashChannel);
         log.debug("Set the creationchannel of node " + contentelement.getNumber() + " to the default channel.");
      }
   }

   /**
    * Makes sure the supplied contentelement will have at least one content channel. If the node has none, it will
    * relate to the trash channel.
    * 
    * @param contentelement
    *           The contentelement to repair
    */
   public static void repairContentChannels(Node contentelement) {
      if (!hasContentChannel(contentelement)) {
         Node trashChannel = getTrashNode(contentelement.getCloud());
         addContentToChannel(contentelement, trashChannel.getStringValue("number"));
         log.debug("Set a contentchannel of node " + contentelement.getNumber() + " to the default channel.");
      }
   }

   public static boolean hasDeletionChannels(Node contentNode) {
      // check if the content channel has related content elements
      return countDeletionChannels(contentNode) != 0;
   }

   public static int countDeletionChannels(Node contentNode) {
      return contentNode.countRelatedNodes(contentNode.getCloud().getNodeManager(CONTENTCHANNEL), DELETIONREL,
            DESTINATION);
   }

   public static NodeList getDeletionChannels(Node elementNode) {
      return elementNode.getRelatedNodes(CONTENTCHANNEL, DELETIONREL, DESTINATION);
   }

   /**
    * Create the relation to the deletion relation with this node.
    * 
    * @param contentNode
    *           - Content Node
    * @param channelNumber
    *           - Channel Node
    */
   public static void addDeletionRelation(Node contentNode, String channelNumber) {
      Node channel = contentNode.getCloud().getNode(channelNumber);
      addDeletionRelation(contentNode, channel);
   }

   public static void addDeletionRelation(Node contentNode, Node channel) {
      RelationManager rm = contentNode.getCloud().getRelationManager(CONTENTELEMENT, CONTENTCHANNEL, DELETIONREL);
      contentNode.createRelation(channel, rm).commit();
   }

   public static void addAssetDeletionRelation(Node AssetNode, Node channel) {
      RelationManager rm = AssetNode.getCloud().getRelationManager(ASSETELEMENT, CONTENTCHANNEL, DELETIONREL);
      AssetNode.createRelation(channel, rm).commit();
   }

   public static void removeDeletionRels(Node contentNode, String channelNumber) {
      Node channelNode = contentNode.getCloud().getNode(channelNumber);
      removeDeletionRels(contentNode, channelNode);
   }

   public static void removeDeletionRels(Node contentNode, Node channelNode) {
      RelationIterator relIter = contentNode.getRelations(DELETIONREL, getNodeManager(contentNode.getCloud()),
            DESTINATION).relationIterator();
      while (relIter.hasNext()) {
         Relation relation = relIter.nextRelation();
         if (relation.getDestination().equals(channelNode) || relation.getSource().equals(channelNode)) {
            log.info("removing a found deletionrel with:" + channelNode);
            relation.delete(true);
         }
      }
   }

   public static Node createContentChannelPath(Cloud cloud, String path) {
      String[] fragments = TreeUtil.getPathFragments(path);
      Node parentChannel = getRootNode(cloud);
      String rootPathFragment = parentChannel.getStringValue(FRAGMENT_FIELD);
      if (!rootPathFragment.equals(fragments[0])) {
         throw new IllegalArgumentException("path does not start with root pathfragment (" + rootPathFragment + ")");
      }

      for (int i = 1; i < fragments.length; i++) {
         String fragment = fragments[i];

         if (!hasChild(parentChannel, fragment)) {
            Node contentChannel = RepositoryUtil.createChannel(cloud, fragment, fragment);
            RepositoryUtil.appendChild(parentChannel, contentChannel);
            parentChannel = contentChannel;
         } else {
            parentChannel = RepositoryUtil.getChild(parentChannel, fragment);
         }
      }
      return parentChannel;
   }

   public static Node createChannel(Cloud cloud, String name) {
      return createChannel(cloud, name, null);
   }

   public static Node createChannel(Cloud cloud, String name, String pathname) {
      Node channel = getNodeManager(cloud).createNode();
      channel.setStringValue(TITLE_FIELD, name);
      if (StringUtils.isNotEmpty(pathname)) {
         channel.setStringValue(FRAGMENT_FIELD, pathname);
      }
      channel.commit();
      return channel;
   }

   public static void moveChannel(Node sourceChannel, Node destChannel) {
      if (!isParent(sourceChannel, destChannel)) {
         Relation parentRelation = getParentRelation(sourceChannel);
         appendChild(destChannel, sourceChannel);
         parentRelation.delete();
      }
   }

  public static Node copyChannel(Node sourceChannel, Node destChannel) {
      List<Integer> channelList = new ArrayList<Integer>();
      iterateChannels(sourceChannel,channelList);
      StringBuilder output = new StringBuilder().append(" -Start: ");
      Map<Integer, Integer> copiedNodes = new HashMap<Integer, Integer>();

      Node newNode = copyChannel(sourceChannel,destChannel,channelList,copiedNodes,output);
      output.append("<br/><br/>copiedNodes has #" + copiedNodes.size() + ":<br/>" + copiedNodes.toString());
      if(log.isDebugEnabled()) {
         log.debug("#################:"+output.toString());
      }
       String cloneCopy = PropertiesUtil.getProperty("clonecopy");
      if("true".equalsIgnoreCase(cloneCopy)) {
         copyContentElements(sourceChannel,destChannel,channelList,copiedNodes,output);
      }
      return newNode;
   }

      public static Node copyContentElements(Node sourceChannel, Node destChannel, List<Integer> channelList,Map<Integer, Integer> copiedNodes ,StringBuilder output) {
      if (!isParent(sourceChannel, destChannel)) {
         Object newChannelNumber = copiedNodes.get(sourceChannel.getNumber());
         
         if( newChannelNumber != null) {
            Node newChannel = sourceChannel.getCloud().getNode((Integer)newChannelNumber);
            cloneRelatedNodes(sourceChannel, newChannel,copiedNodes,output,channelList);
            NodeList children = getOrderedChildren(sourceChannel);
            for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
               Node childChannel = iter.next();
               copyContentElements(childChannel, newChannel,channelList,copiedNodes,output);
            }
         }
      }
      return null;
   }
   public static Node copyChannel(Node sourceChannel, Node destChannel, List<Integer> channelList,Map<Integer, Integer> copiedNodes ,StringBuilder output) {
      if (!isParent(sourceChannel, destChannel)) {
         Node newChannel = CloneUtil.cloneNode(sourceChannel);
         appendChild(destChannel, newChannel);
         copiedNodes.put(sourceChannel.getNumber(), newChannel.getNumber());
         NodeList children = getOrderedChildren(sourceChannel);
         for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
            Node childChannel = iter.next();
            copyChannel(childChannel, newChannel,channelList,copiedNodes,output);
         }
         String cloneCopy = PropertiesUtil.getProperty("clonecopy");
         if("true".equalsIgnoreCase(cloneCopy)) {
            cloneAssetNodes(sourceChannel,newChannel,copiedNodes,output);
         }
         else {
            CloneUtil.cloneRelations(sourceChannel, newChannel, CONTENTREL, CONTENTELEMENT);
         }
         return newChannel;
      }
      return null;
   }

   /**
    * Get the role for the user for a page
    * 
    * @param cloud
    *           Cloud with user
    * @param channel
    *           get role for this channel
    * @return UserRole - rights of a user
    */
   public static UserRole getRole(Cloud cloud, int channel) {
      return getRole(cloud, cloud.getNode(channel), false);
   }

   /**
    * Get the role for the user for a channel
    * 
    * @param group
    *           Node of group
    * @param channel
    *           get role for this channel
    * @return UserRole - rights of a user
    */
   public static UserRole getRole(Node group, Node channel) {
      return getRole(group, channel, false);
   }

   /**
    * Get the role for the user for a channel
    * 
    * @param cloud
    *           Cloud with user
    * @param channel
    *           get role for this channel
    * @param rightsInherited
    *           inherit rights from parent chennal
    * @return UserRole - rights of a user
    */
   public static UserRole getRole(Cloud cloud, Node channel, boolean rightsInherited) {
      TreeMap<String, UserRole> channelsWithRole = SecurityUtil.getLoggedInRoleMap(cloud, treeManagers, CHILDREL);
      return SecurityUtil.getRole(channel, rightsInherited, channelsWithRole);
   }

   /**
    * Get the role for the user for a channel
    * 
    * @param group
    *           Node of group
    * @param channel
    *           get role for this channel
    * @param rightsInherited
    *           inherit rights from parent chennal
    * @return UserRole - rights of a user
    */
   public static UserRole getRole(Node group, Node channel, boolean rightsInherited) {
      // retrieve a TreeMap where the channels (keys) are ordered on level and path
      TreeMap<String, UserRole> channelsWithRole = SecurityUtil.getNewRolesMap();
      SecurityUtil.fillChannelsWithRole(group, channelsWithRole, treeManagers, CHILDREL);
      return SecurityUtil.getRole(channel, rightsInherited, channelsWithRole);
   }

   public static void setGroupRights(Cloud cloud, Node user, Map<Integer, UserRole> rights) {
      SecurityUtil.setGroupRights(cloud, user, rights, TreeUtil.convertToList(treeManagers));
   }

   public static List<Node> getUsersWithRights(Node channel, Role requiredRole) {
      return SecurityUtil.getUsersWithRights(channel, requiredRole, TreeUtil.convertToList(treeManagers), CHILDREL);
   }

   public static RepositoryInfo getRepositoryInfo(Cloud cloud) {
      RepositoryInfo info = (RepositoryInfo) cloud.getProperty(RepositoryInfo.class.getName());
      if (info == null) {
         info = new RepositoryInfo();
         cloud.setProperty(RepositoryInfo.class.getName(), info);
         TreeMap<String, UserRole> channelsWithRole = SecurityUtil.getLoggedInRoleMap(cloud, treeManagers, CHILDREL);

         for (Map.Entry<String, UserRole> entry : channelsWithRole.entrySet()) {
            UserRole role = entry.getValue();
            if (!Role.NONE.equals(role.getRole())) {
               String path = entry.getKey();
               Node channel = getChannelFromPath(cloud, path);
               if (channel != null) {
                  if (isRoot(channel)) {
                     info.expand(channel.getNumber());
                  } else {
                     List<Node> pathNodes = getPathToRoot(channel);
                     for (Node pathNode : pathNodes) {
                        info.expand(pathNode.getNumber());
                     }
                  }
               }
            }
         }
      }
      return info;
   }

   public static RolesInfo getRolesInfo(Cloud cloud, Node group) {
      RolesInfo info = new RolesInfo();
      TreeMap<String, UserRole> channelsWithRole = SecurityUtil.getRoleMap(treeManagers, CHILDREL, group);
      for (String path : channelsWithRole.keySet()) {
         Node channel = getChannelFromPath(cloud, path);
         info.expand(channel.getNumber());
      }
      return info;
   }

   /**
    * This is the method for a USER, the old ones want a GROUP... (even although the are called getRoleForUser(..)
    * 
    * @param channel
    *           channel to get role for
    * @param user
    *           user to get role for
    * @return User Role
    */
   public static UserRole getUserRole(Node channel, Node user) {
      TreeMap<String, UserRole> pagesWithRole = SecurityUtil.getNewRolesMap();
      SecurityUtil.getUserRoleMap(user, treeManagers, CHILDREL, pagesWithRole);
      return SecurityUtil.getRole(channel, true, pagesWithRole);
   }

   /**
    * Check if the role has rights on the Recyclebin
    * 
    * @param cloud
    *           Cloud
    * @param roleName
    *           specified roleName
    * @return boolean
    */
   public static boolean hasRecyclebinRights(Cloud cloud, String roleName) {
      Node node = getTrashNode(cloud);
      roleName = roleName.toLowerCase();

      UserRole role = RepositoryUtil.getRole(cloud, node, true);

      if (role != null && roleName.equals(role.getRole().getName())) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * get all Content Channels
    * 
    * @return content channel list
    */
   public static NodeList getAllContentChannels(Cloud cloud) {
      NodeList contentChannels = getContentChannelOrderedChildren(getRootNode(cloud));
      contentChannels.add(0, getRootNode(cloud));
      return contentChannels;
   }
   /**   Remove a asset element from a channel
    *   
    * @param asset
    * @param channelNode
    */
   public static void removeAssetFromChannel(Node asset, Node channelNode) {
      RepositoryUtil.addDeletionRelation(asset, channelNode);
   }
   public static int countLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day,int maxDays){
      NodeQuery query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day);
      if(maxDays > 0){
         SearchUtil.addDayConstraint(query, query.getNodeManager(), ContentElementUtil.CREATIONDATE_FIELD, "-" + maxDays);
      }
      return Queries.count(query);
   }
   public static NodeList getLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day, int maxDays) {
      NodeQuery query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day);
      if(maxDays > 0){
         SearchUtil.addDayConstraint(query, query.getNodeManager(), ContentElementUtil.CREATIONDATE_FIELD, "-" + maxDays);
      }
      return query.getNodeManager().getList(query);
   }
   /**
    *    Clone nodes related with a node or channel
    * @param sourceNode source channel 
    * @param destNode  destination node
    * @param copiedNodes   map used for the mapping number-pair
    * @param output
    * @param channels  the current channel tree list
    */
   public static void cloneRelatedNodes(Node sourceNode,Node destNode,Map<Integer, Integer> copiedNodes ,StringBuilder output,List<Integer> channels) {
      List<Relation> relations = sourceNode.getRelations(null, null, DESTINATION_LOWER);
     
      for (Relation rel : relations) {
         if(rel == null) {
            output.append("skipped  " + rel + "; ");
            continue; //Skip contentchannels and collection channels. 
         }
         if (! rel.isRelation()) {
            output.append("skipped  " + rel + "; ");
            continue; //Skip contentchannels and collection channels.  
         }
         RelationManager relManager = rel.getRelationManager();
         if (relManager == null) {
            //When a relation exists, but it is not defined in the typedef, the relManager could not be found and resolves in null
            //Apparently, there is a typedef missing in the MMBase typedef table. Check the numbers in the logfile and add the relation to fix it!
            //You need to cleanrun the script again (from previous database) or fix these listed nodes by hand!
            output.append("strangely not found relManager for " + rel.getNumber() + ", please check destNode(" + sourceNode.getNumber() + "|" + destNode.getNumber() + ") (might need to add a relation in typedef table!);");
            continue;
         }
         
         if (isChannel(rel.getDestination()) || 
               relManager.getName().equalsIgnoreCase("deletionrel")
//               || relManager.getName().equalsIgnoreCase("creationrel")
               ) {
            output.append("skipped  " + relManager.getName() + "; ");
            continue; //Skip contentchannels and collection channels.
         } 
         else if (rel.getNodeManager().getName().equals(ContentElementUtil.OWNERREL)) {
            CloneUtil.cloneRelations(sourceNode, destNode, ContentElementUtil.OWNERREL, SecurityUtil.USER);
            output.append(ContentElementUtil.OWNERREL + " copied;");
         }
         else if (!isRelatedWithCurrentChannelTree(rel.getDestination(),channels)) {
            output.append("skipped  " + relManager.getName() + "; ");
            continue; //Skip nodes not in the current channel tree. 
         }
         else 
         {            output.append(" into main ");
            //*** Start cloning the node from sourceChild -> destChild
            //If the related node should be cloned, dive into the node and deepcopy it
            Node sourceChild = rel.getDestination();
            boolean cloned = false;
            
            //Only clone node, when it hasn't been cloned before.
            Node destChild;
            if (copiedNodes.get(sourceChild.getNumber()) == null) { 
               destChild = cloneNode(sourceChild,copiedNodes);
               copiedNodes.put(Integer.valueOf(sourceChild.getNumber()),Integer.valueOf(destChild.getNumber()));
               cloned = true;
               //Logging
               if (destChild.getNodeManager().hasField("name")) {
                //  output.append(destChild.getFieldValue("name") + " of ");
               }
               output.append(rel.getNodeManager().getName() + " cloned (" + sourceNode.getNumber() + "|" + destChild.getNumber() + ");");
            } else {
               //Retrieve the already cloned node from the Map
               destChild = sourceNode.getCloud().getNode(copiedNodes.get(sourceChild.getNumber()));
               output.append(rel.getNodeManager().getName() + " reused clone (" + sourceChild.getNumber() + "|" + destChild.getNumber() + ");");
            }
            //*** End cloning node
        
            //Create a new relation between the new node and its parent
            if(!"imageinlinerel".equalsIgnoreCase(rel.getNodeManager().getName()) && !"inlinerel".equalsIgnoreCase(rel.getNodeManager().getName())) {
               Relation destRel = destNode.createRelation(destChild, relManager);
               String relName = destRel.getNodeManager().getName();
               if (relName.equalsIgnoreCase("posrel") || 
                     relName.equalsIgnoreCase("contentrel") ||
                     relName.equalsIgnoreCase("childrel") || 
                     relName.equalsIgnoreCase("detailimagerel")) {
                  destRel.setIntValue("pos", rel.getIntValue("pos"));
               }
               destRel.commit(); 
               output.append("[newRel:" + destNode.getNumber() + "," + relName + "];");
            }


            
            //Creation channels are skipped at copying relations, so do it by hand.
            if (hasCreationChannel(sourceChild,sourceNode) && isChannel(destNode)) {
               addCreationChannel(destChild, destNode);
               output.append("added creationrel to " + destChild.getNumber() + ";");
            }
                        //If no clone was needed, but reused an existing clone, the relations are fine already..continue!
            if (!cloned) continue;
            //If destChild is an image, also change title
            if (destChild.getNodeManager().getName().equalsIgnoreCase("images")) {
               destChild.setStringValue("title", destChild.getStringValue("title") + "-North");
               destChild.commit();
            }

            
//            if (destChild.getNodeManager().getName().equalsIgnoreCase("subject")) {
               //Now go deeper into the tree
               cloneRelatedNodes(sourceChild, destChild, copiedNodes, output,channels);
//            }
         }
      }
   }
   /**
    *    Clone a node 
    * @param localNode
    * @param copiedNodes   number-pairs
    * @param channels
    * @return
    */
   public static Node cloneNode(Node localNode,Map<Integer, Integer> copiedNodes) {
      if (isRelation(localNode)) {
         return CloneUtil.cloneRelation(localNode);
      }
      else {
        NodeManager localNodeManager = localNode.getNodeManager();
        NodeManager nodeManager = localNode.getCloud().getNodeManager(localNodeManager.getName());
        Node newNode = nodeManager.createNode();
        newNode.commit();
        FieldIterator fields = localNodeManager.getFields().fieldIterator();
        while (fields.hasNext()) {
           Field field = fields.nextField();
           String fieldName = field.getName();
           
           if (field.getState() == Field.STATE_PERSISTENT) {
               if (!(fieldName.equals("owner") || fieldName.equals("number") ||
                     fieldName.equals("otype") ||
                     (fieldName.indexOf("_") == 0))) {
                  cloneNodeField(localNode, newNode, field,copiedNodes);
               }
           }
        }
        newNode.commit();

        return newNode;
      }
   }
   
   /**
    * cloneNodeField copies node fields from one node to an other
    * 
    * @param sourceNode
    *           the source node
    * @param destinationNode
    *           destination node
    * @param field
    *           the field to clone
    */
   public static void cloneNodeField(Node sourceNode, Node destinationNode, Field field,Map<Integer, Integer> copiedNodes) {
      String fieldName = field.getName();

      if (destinationNode.getNodeManager().hasField(fieldName) == true) {
         Field sourceField = sourceNode.getNodeManager().getField(fieldName);
         if (sourceField.getState() != Field.STATE_SYSTEM && !sourceField.isVirtual()) {
          destinationNode.setValueWithoutProcess(fieldName, 
                strip(sourceNode,destinationNode,field,copiedNodes));
         }
      }
   }
   
   /**
    *  To strip the Node in richtext and that not in the current channel tree
    * @param sourceNode
    * @param field
    * @param copiedNodes
    * @param channels
    * @return
    */
   public static Object strip(Node sourceNode, Node destinationNode,Field field,Map<Integer, Integer> copiedNodes) {
      return RichText.stripLinkAndImage(sourceNode, destinationNode,field, copiedNodes);
   }
   /**
    * quick test to see if node is a relation by testing fieldnames
    * @param node Possible relation
    * @return <code>true</code> when relation fields present
    */
   public static boolean isRelation(Node node) {
      FieldIterator fi = node.getNodeManager().getFields().fieldIterator();
      int count = 0;

      while (fi.hasNext()) {
         String name = fi.nextField().getName();

         if (name.equals("rnumber") || name.equals("snumber") ||
               name.equals("dnumber")) {
            count++;
         }
      }

      if (count == 3) {
         return true;
      }

      return false;
   }
   public static void iterateChannels(Node sourceChannel, List<Integer> channelList) {
      NodeList children = RepositoryUtil.getOrderedChildren(sourceChannel);  
      channelList.add(sourceChannel.getNumber());
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node childChannel = iter.next();
         iterateChannels(childChannel, channelList);
      }
   }
   
   /**
    * check the node if it is in the current channel tree
    * @param sourceNode
    * @param channels
    * @return
    */
   public static boolean isRelatedWithCurrentChannelTree(Node sourceNode,List<Integer> channels) {

         Node creationChannel = getCreationChannel(sourceNode);
         if(creationChannel != null && channels.contains(creationChannel.getNumber())) {
            return true;
         }     
      return false;
   }
   
   /**
    *  Clone related assets according to the channel
    * @param sourceChannel   source channel 
    * @param newChannel      destination channel
    * @param copiedNodes     Nodes have been copied 
    * @param output
    */
   public static void cloneAssetNodes(Node sourceChannel,Node newChannel,Map<Integer, Integer> copiedNodes ,StringBuilder output) {
      
      List<Relation> relations = sourceChannel.getRelations("creationrel", sourceChannel.getCloud().getNodeManager("assetelement"), SOURCE);
      for (Relation rel : relations) {
         Node sourceChild = rel.getSource();
         Node destChild = CloneUtil.cloneNode(sourceChild);
         copiedNodes.put(sourceChild.getNumber(), destChild.getNumber());
         addAssetToChannel(destChild,newChannel);
      }
   }
   
}
