/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository;

import java.util.*;

import net.sf.mmapps.commons.bridge.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.security.*;
import com.finalist.cmsc.security.forms.RolesInfo;

public class RepositoryUtil {

    public static final String NAME_FIELD = "name";

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(RepositoryUtil.class.getName());

    private static final String SOURCE = "SOURCE";
    private static final String DESTINATION = "DESTINATION";
    
    public final static String TITLE_FIELD = NAME_FIELD;
    public final static String FRAGMENT_FIELD = "pathfragment";

    public final static String CONTENTCHANNEL = "contentchannel";
    public static final String CONTENTELEMENT = ContentElementUtil.CONTENTELEMENT;
    
    public final static String CHILDREL = "childrel";
    public final static String CONTENTREL = "contentrel";
    public final static String DELETIONREL = "deletionrel";
    public static final String CREATIONREL = "creationrel";
    
    public final static String ALIAS_ROOT = "repository.root";
    public final static String ALIAS_TRASH = "repository.trash";

    
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
        return CONTENTCHANNEL.equals(node.getNodeManager().getName());
    }
    
    /** gets the root number
     * @param cloud - MMbase cloud
     * @return root node number 
     */
    public static String getRoot(Cloud cloud) {
       return getRootNode(cloud).getStringValue("number");
    }

    /** gets the root node
     * @param cloud - MMbase cloud
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

    
    /** gets the Trash number
     * @param cloud - MMbase cloud
     * @return trash node number
     */
   public static String getTrash(Cloud cloud) {
      return getTrashNode(cloud).getStringValue("number");
   }

   /** gets the Trash node
     * @param cloud - MMbase cloud
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
        TreeUtil.appendChild(cloud, parent, child, CHILDREL);
    }
    
    public static void appendChild(Node parentNode, Node childNode) {
        TreeUtil.appendChild(parentNode, childNode, CHILDREL);
    }
    
    public static Node getParent(Node node) {
        return TreeUtil.getParent(node, CHILDREL);
    }
    
    public static Relation getParentRelation(Node node) {
        return TreeUtil.getParentRelation(node, CHILDREL);
    }
    
    public static boolean isParent(Node sourceChannel, Node destChannel) {
        return TreeUtil.isParent(sourceChannel, destChannel, CHILDREL);
    }

    public static String getFragmentFieldname(Node parentNode) {
        return FRAGMENT_FIELD;
    }
    
    /**
     * Find path to root
     * @param node - node
     * @return List with the path to the root. First item is the root and last is the node
     */
    public static List getPathToRoot(Node node) {
        return TreeUtil.getPathToRoot(node, CHILDREL);
    }
    
    /**
     * Creates a string that represents the root path.
     * @param cloud - MMbase cloud
     * @param node - MMbase node
     * @return path to root
     */
    public static String getPathToRootString(Cloud cloud, String node) {
       return TreeUtil.getPathToRootString(cloud.getNode(node), CHILDREL, FRAGMENT_FIELD);
    }
    
    /**
     * Creates a string that represents the root path.
     * @param node - MMbase node
     * @return path to root
     */
    public static String getPathToRootString(Node node) {
       return TreeUtil.getPathToRootString(node, CHILDREL, FRAGMENT_FIELD, true);
    }

    /**
     * Creates a string that represents the root path.
     * @param node - MMbase node
     * @param includeRoot - include the root pathfragment
     * @return path to root
     */
    public static String getPathToRootString(Node node, boolean includeRoot) {
        return TreeUtil.getPathToRootString(node, CHILDREL, FRAGMENT_FIELD, includeRoot);
    }

    /**
     * Creates a string that represents the titles.
     * @param cloud - MMbase cloud
     * @param node - node number
     * @return titles of nodes in path
     */
    public static String getTitlesString(Cloud cloud, String node) {
       return TreeUtil.getTitlesString(cloud, node, CHILDREL, TITLE_FIELD, true);
    }

    /**
     * Creates a string that represents the titles.
     * @param cloud - MMbase cloud
     * @param node - node number
     * @param includeRoot - include the root node
     * @return titles of nodes in path
     */
    public static String getTitlesString(Cloud cloud, String node, boolean includeRoot) {
       return TreeUtil.getTitlesString(cloud.getNode(node), CHILDREL, TITLE_FIELD, includeRoot);
    }

    /**
     * Creates a string that represents the titles.
     * @param node - MMbase node
     * @param includeRoot - include the root node
     * @return titles of nodes in path
     */
    public static String getTitlesString(Node node, boolean includeRoot) {
        return TreeUtil.getTitlesString(node, CHILDREL, TITLE_FIELD, includeRoot);
    }
    
    /**
     * Method that finds the Channel node using a path as input.
     * @param cloud - MMbase cloud
     * @param path - path of channel
     * @return node with channel path
     */
    public static Node getChannelFromPath(Cloud cloud, String path) {
        return TreeUtil.getChannelFromPath(cloud, path, getRootNode(cloud), CHILDREL, FRAGMENT_FIELD, true);
    }

    /**
     * Method that finds the Channel node using a path as input.
     * @param cloud - MMbase cloud
     * @param path - path of channel
     * @param root - node to start search
     * @return node with channel path
     */
    public static Node getChannelFromPath(Cloud cloud, String path, Node root) {
         return TreeUtil.getChannelFromPath(cloud, path, root, CHILDREL, FRAGMENT_FIELD, true);
    }

    /**
     * Method that finds the Channel node using a path as input.
     * @param cloud - MMbase cloud
     * @param path - path of channel
     * @param root - node to start search
     * @param useCache - use path cache
     * @return node with channel path
     */
    public static Node getChannelFromPath(Cloud cloud, String path, Node root, boolean useCache) {
        return TreeUtil.getChannelFromPath(cloud, path, root, CHILDREL, FRAGMENT_FIELD, useCache);
    }
    
    /**
     * Get child channel nodes
     * @param parentNode - parent
     * @return List of children
     */
    public static NodeList getChildren(Node parentNode) {
        return TreeUtil.getChildren(parentNode, CHILDREL);
     }


    public static boolean hasChild(Node parentChannel, String fragment) {
        return TreeUtil.hasChild(parentChannel, fragment, CHILDREL, FRAGMENT_FIELD);
    }

    public static Node getChild(Node parentChannel, String fragment) {
        return TreeUtil.getChild(parentChannel, fragment, CHILDREL, FRAGMENT_FIELD);
    }

    /** Reorder content in channel
     * @param cloud - MMbase cloud
     * @param parentNode - parent
     * @param children - new order 
     */
    public static void reorderContent(Cloud cloud, String parentNode, String children) {
        Node parent = cloud.getNode(parentNode);
        RelationUtil.reorder(parent, children, CONTENTREL, CONTENTELEMENT);
    }

    /** Reorder content in channel
     * @param cloud - MMbase cloud
     * @param parentNode - parent
     * @param children - new order 
     */
    public static void reorderContent(Cloud cloud, String parentNode, String[] children) {
        Node parent = cloud.getNode(parentNode);
        RelationUtil.reorder(parent, children, CONTENTREL, CONTENTELEMENT);
    }


    public static void reorderContent(Cloud cloud, String parentNode, String[] children, int offset) {
        Node parent = cloud.getNode(parentNode);
        RelationUtil.reorder(parent, children, CONTENTREL, CONTENTELEMENT, offset);
    }

    
    /** Get sorted child nodes
     * @param parentNode - parent
     * @return list of sorted children
     */
    public static NodeList getOrderedChildren(Node parentNode) {
        return SearchUtil.findRelatedOrderedNodeList(parentNode, CONTENTCHANNEL, CHILDREL, NAME_FIELD);
     }

    /** Depth of path
     * @param path - path
     * @return level
     */
    public static int getLevel(String path) {
        return TreeUtil.getLevel(path);
    }
    
    /** Get number of children
     * @param parent - parent
     * @return number of children
     */
    public static int getChildCount(Node parent) {
        return TreeUtil.getChildCount(parent, CHILDREL);
    }
    
    /**
     * Create the relation to the creationchannel.
     * @param content - Content Node
     * @param channelNumber - String channel number
     */
    public static void addCreationChannel(Node content, String channelNumber) {
        addCreationChannel(content, content.getCloud().getNode(channelNumber));
    }
    
    /**
     * Create the relation to the creationchannel.
     * @param content - Content Node
     * @param channel - Channel Node
     */
    public static void addCreationChannel(Node content, Node channel) {
       log.debug("Creationchannel " + channel.getNumber());
       RelationManager creationChannel = content.getCloud().getRelationManager(CONTENTELEMENT, CONTENTCHANNEL, CREATIONREL);
       content.createRelation(channel, creationChannel).commit();
    }
    
    /**
     * Check if a contentnode has a creationchannel
     * @param content - Content Node 
     * @return true if the node has a related creation channel
     */
    public static boolean hasCreationChannel(Node content) {
       int count = content.countRelatedNodes(content.getCloud().getNodeManager(CONTENTCHANNEL), CREATIONREL, DESTINATION);
       return count > 0;
    }

    /** Get creation channel
     * @param content - Content Node
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
    
    /** Remove creation relations for the given contentelement
     * @param content A contentelment
     */
    public static void removeCreationRelForContent(Node content) {
       if (!ContentElementUtil.isContentElement(content)) {
          throw new IllegalArgumentException("Only contentelements are allowed.");
       }
       RelationList list = content.getRelations(CREATIONREL);
       for (int i = 0; i < list.size(); i++) {
          Relation creationrel = list.getRelation(i);
          creationrel.delete();
       }
    }
    
    /**
     * Create the relation to the creationchannel.
     * @param content - Content Node
     * @param channelNumber - String channel number
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
           if(!isOrphan) {
              if(isTrash(creationNode)) {
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
           
           if(isOrphan) {
              addCreationChannel(content, channelNode);
           }
           // remove delete relation with this channel, if any still exist
           RepositoryUtil.removeDeletionRels(content, channelNode);
       }
    }

    public static boolean isLinkedToChannel(Node content, Node channelNode) {
        boolean isLinkedToChannel = false;
        NodeList channels = getContentChannels(content);
        for (Iterator iter = channels.iterator(); iter.hasNext();) {
            Node channel = (Node) iter.next();
            if (channelNode.getNumber() == channel.getNumber()) {
                isLinkedToChannel = true;
            }
        }
        return isLinkedToChannel;
    }

    /** Check if a contentnode has a contentchannel
     * @param content - Content Node
     * @return true if the node has a related workflowitem
     */
    public static boolean hasContentChannel(Node content) {
       int count = content.countRelatedNodes(content.getCloud().getNodeManager(CONTENTCHANNEL), CONTENTREL, SOURCE);
       return count > 0;
    }

    public static NodeList getContentChannels(Node content) {
        return content.getRelatedNodes(CONTENTCHANNEL, CONTENTREL, SOURCE);
    }
    
    public static boolean hasCreatedContent(Node channelNode) {
        // check if the content channel has related content elements
        return countCreatedContent(channelNode) != 0;
    }

    public static int countCreatedContent(Node channelNode) {
        return channelNode.countRelatedNodes(channelNode.getCloud().getNodeManager(
             CONTENTELEMENT), CREATIONREL, SOURCE);
    }

    public static NodeList getCreatedElements(Node channel) {
        return channel.getRelatedNodes(CONTENTELEMENT, CREATIONREL, SOURCE);
    }
    
    public static boolean hasLinkedContent(Node channelNode) {
        // check if the content channel has related content elements
        return countLinkedContent(channelNode) != 0;
    }

    public static int countLinkedContent(Node channelNode) {
        int contentCount = channelNode.countRelatedNodes(channelNode.getCloud().getNodeManager(
             CONTENTELEMENT), CONTENTREL, DESTINATION);
        return contentCount;
    }

    public static int countLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumber) {
        NodeQuery query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, offset, maxNumber);
        return Queries.count(query);
    }
    
    public static NodeList getLinkedElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumber) {
        NodeQuery query = createLinkedContentQuery(channel, contenttypes, orderby, direction, useLifecycle, offset, maxNumber);
        return query.getNodeManager().getList(query);
    }

    private static NodeQuery createLinkedContentQuery(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumber) {
        if (orderby == null) {
            orderby = CONTENTREL + ".pos";
        }
        String destinationManager = CONTENTELEMENT;
        
        if (contenttypes != null && contenttypes.size() == 1) {
            destinationManager = contenttypes.get(0);
        }
        
        NodeQuery query = SearchUtil.createRelatedNodeListQuery(channel, destinationManager,
                CONTENTREL, null, null, orderby, direction);

        if (contenttypes != null && contenttypes.size() > 1) {
            SearchUtil.addTypeConstraints(query, contenttypes);
        }

        if (useLifecycle) {
            Long date = new Long(System.currentTimeMillis());
            ContentElementUtil.addLifeCycleConstraint(channel, query, date);
        }
        SearchUtil.addLimitConstraint(query, offset, maxNumber);
        return query;
    }

    public static NodeList getLinkedElements(Node channel) {
        return getLinkedElements(channel, null, null, null, false, -1, -1);
    }
    
    public static void removeContentFromChannel(Node content, Node channelNode) {
        // remove the relations to this content from this channel
        RelationIterator contentRelIter = content.getRelations(CONTENTREL, getNodeManager(content.getCloud()), SOURCE).relationIterator();
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
        RelationIterator contentRelIter = content.getRelations(CONTENTREL, getNodeManager(content.getCloud()), SOURCE).relationIterator();
        while (contentRelIter.hasNext()) {
           Relation relation = contentRelIter.nextRelation();
           RepositoryUtil.addDeletionRelation(content, relation.getSource());
           relation.delete(true);
        }
    }
    
    /**
     * Makes sure the supplied contentelement will have at least one content
     * and one creation channel. If the node has to little, it will relate
     * to the trash contentchannel.
     *
     * @param contentelement The contentelement to repair the channels of.
     */
    public static void repairChannels(Node contentelement) {
       repairCreationChannel(contentelement);
       repairContentChannels(contentelement);
    }

    /**
     * Makes sure the supplied contentelement will have at least one
     * creation channel. If the node has none, it will relate
     * to the trash channel.
     * @param contentelement The contentelement to repair
     */
    public static void repairCreationChannel(Node contentelement) {
       if (!hasCreationChannel(contentelement)) {
          Node trashChannel = getTrashNode(contentelement.getCloud());
          addCreationChannel(contentelement, trashChannel);
          log.debug("Set the creationchannel of node " + contentelement.getNumber() + " to the default channel.");
       }
    }

    /**
     * Makes sure the supplied contentelement will have at least one
     * content channel. If the node has none, it will relate
     * to the trash channel.
     * @param contentelement The contentelement to repair
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
        return contentNode.countRelatedNodes(contentNode.getCloud().getNodeManager(
                CONTENTCHANNEL), DELETIONREL, DESTINATION);
    }
    
    public static NodeList getDeletionChannels(Node contentNode) {
        return contentNode.getRelatedNodes(CONTENTCHANNEL, DELETIONREL, DESTINATION);
    }
    
    /**
     * Create the relation to the deletion relation with this node.
     * 
     * @param contentNode - Content Node
     * @param channelNumber - Channel Node
     */
    public static void addDeletionRelation(Node contentNode, String channelNumber) {
       Node channel = contentNode.getCloud().getNode(channelNumber);
       addDeletionRelation(contentNode, channel);
    }
    
    public static void addDeletionRelation(Node contentNode, Node channel) {
        RelationManager rm = contentNode.getCloud().getRelationManager(
                   CONTENTELEMENT, CONTENTCHANNEL, DELETIONREL);
        contentNode.createRelation(channel, rm).commit();
    }
    
    public static void removeDeletionRels(Node contentNode, String channelNumber) {
       Node channelNode = contentNode.getCloud().getNode(channelNumber);
       removeDeletionRels(contentNode, channelNode);       
    }

    public static void removeDeletionRels(Node contentNode, Node channelNode) {
        RelationIterator relIter = contentNode.getRelations(DELETIONREL, getNodeManager(contentNode.getCloud()), DESTINATION).relationIterator();
           while (relIter.hasNext()) {
              Relation relation = relIter.nextRelation();
              if (relation.getDestination().equals(channelNode) || relation.getSource().equals(channelNode)) {
                 log.info("removing a found deletionrel with:"+channelNode);
                 relation.delete(true);
              }
           }
    }
    
    public static Node createChannel(Cloud cloud, String name) {
        String pathname = name;
        pathname = pathname.replaceAll("\\s", "_");
        pathname = pathname.replaceAll("[^a-zA-Z_0-9_.-]", "");
        return createChannel(cloud, name, pathname);
    }

    public static Node createChannel(Cloud cloud, String name, String pathname) {
        Node channel = getNodeManager(cloud).createNode();
        channel.setStringValue(TITLE_FIELD, name);
        channel.setStringValue(FRAGMENT_FIELD, pathname);
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
        if (!isParent(sourceChannel, destChannel)) {
            Node newChannel = CloneUtil.cloneNode(sourceChannel);
            appendChild(destChannel, newChannel);
            
            NodeList children = getOrderedChildren(sourceChannel);
            for (Iterator iter = children.iterator(); iter.hasNext();) {
                Node childChannel = (Node) iter.next();
                copyChannel(childChannel, newChannel);
            }

            CloneUtil.cloneRelations(sourceChannel, newChannel, CONTENTREL, CONTENTELEMENT);

            return newChannel;
        }
        return null;
    }
    
    /**
     * Get the role for the user for a page
     *
     * @param cloud Cloud with user
     * @param channel get role for this channel
     * @return UserRole - rights of a user
     */
    public static UserRole getRoleForUser(Cloud cloud, int channel) {
        return getRoleForUser(cloud, cloud.getNode(channel), false);
    }

    /**
     * Get the role for the user for a channel
     *
     * @param user Node of user
     * @param channel get role for this channel
     * @return UserRole - rights of a user
     */
    public static UserRole getRoleForUser(Node user, Node channel) {
        return getRoleForUser(user, channel, false);
    }

    /**
     * Get the role for the user for a channel
     *
     * @param cloud Cloud with user
     * @param channel get role for this channel
     * @param rightsInherited inherit rights from parent chennal
     * @return UserRole - rights of a user
     */
    public static UserRole getRoleForUser(Cloud cloud, Node channel, boolean rightsInherited) {
        TreeMap<String,UserRole> channelsWithRole = SecurityUtil.getLoggedInRoleMap(cloud, CONTENTCHANNEL, CHILDREL, FRAGMENT_FIELD);
        return SecurityUtil.getRoleForUser(channel, rightsInherited, channelsWithRole);
    }

    /**
     * Get the role for the user for a channel
     *
     * @param user Node of user
     * @param channel get role for this channel
     * @param rightsInherited inherit rights from parent chennal
     * @return UserRole - rights of a user
     */
    public static UserRole getRoleForUser(Node user, Node channel, boolean rightsInherited) {
       // retrieve a TreeMap where the channels (keys) are ordered on level and path
       TreeMap<String,UserRole> channelsWithRole = SecurityUtil.getNewRolesMap();
       SecurityUtil.fillChannelsWithRole(user, channelsWithRole, CONTENTCHANNEL, CHILDREL, FRAGMENT_FIELD);
       return SecurityUtil.getRoleForUser(channel, rightsInherited, channelsWithRole);
    }

    public static void setGroupRights(Cloud cloud, Node user, Map rights) {
        SecurityUtil.setGroupRights(cloud, user, rights, CONTENTCHANNEL);
    }

    public static List getUsersWithRights(Node channel, Role requiredRole) {
        return SecurityUtil.setGroupRights(channel, requiredRole, CONTENTCHANNEL, CHILDREL);
    }

    public static RepositoryInfo getRepositoryInfo(Cloud cloud) {
        RepositoryInfo info = (RepositoryInfo) cloud.getProperty(RepositoryInfo.class.getName());
        if (info == null) {
            info = new RepositoryInfo();
            cloud.setProperty(RepositoryInfo.class.getName(), info);
            TreeMap<String,UserRole> channelsWithRole = SecurityUtil.getLoggedInRoleMap(cloud, CONTENTCHANNEL, CHILDREL, FRAGMENT_FIELD);
            for (String path : channelsWithRole.keySet()) {
                Node channel = getChannelFromPath(cloud, path);
                info.expand(channel.getNumber());
            }
        }
        return info;
    }
    
    public static RolesInfo getRolesInfo(Cloud cloud, Node group) {
        RolesInfo info = new RolesInfo();
        TreeMap<String,UserRole> channelsWithRole = SecurityUtil.getRoleMap(CONTENTCHANNEL, CHILDREL, FRAGMENT_FIELD, group);
        for (String path : channelsWithRole.keySet()) {
            Node channel = getChannelFromPath(cloud, path);
            info.expand(channel.getNumber());
        }
        return info;
    }

}
