/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.mmbase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.mmapps.commons.bridge.RelationUtil;
import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;


public class TreeUtil {


    private static final String DESTINATION = "DESTINATION";
    public final static String OLDPATH_FIELD = "oldpath";
    public final static String PATH_FIELD = "path";
    public final static String LEVEL_FIELD = "level";

    public final static String PATH_SEPARATOR = "/";
    public final static String TITLE_SEPARATOR = " - ";

    public final static String RELATION_RELATED = "related";
    public final static String RELATION_POS_FIELD = "pos";
    
    private TreeUtil() {
        // utility
    }

    public static NodeManager getNodeManager(Cloud cloud, String nodeManagerName) {
        return cloud.getNodeManager(nodeManagerName);
    }

    public static RelationManager getRelationManager(Cloud cloud, String nodeManagerName, String relationName) {
        return cloud.getRelationManager(nodeManagerName, nodeManagerName, relationName);
    }
    
    public static void appendChild(Cloud cloud, String parent, String child, String relationName) {
        Node parentNode = cloud.getNode(parent);
        Node childNode = cloud.getNode(child);

        appendChild(parentNode, childNode, relationName);
    }

    public static void appendChild(Node parentNode, Node childNode, String relationName) {
        String countField = RELATION_POS_FIELD;
        RelationUtil.createCountedRelation(parentNode, childNode, relationName, countField);
    }

    public static Node getParent(Node node, String relationName) {
        String[] treeManagers = new String[] { node.getNodeManager().getName() };
        return getParent(node, treeManagers, relationName);
    }
    
    public static Node getParent(Node node, String[] treeManagers, String relationName) {
        for (int i = 0; i < treeManagers.length; i++) {
            String manager = treeManagers[i];
            
            if (node.getCloud().hasRelationManager(manager, node.getNodeManager().getName(), relationName)) {
                NodeList parentList = node.getRelatedNodes(manager, relationName, "SOURCE");
                if (!parentList.isEmpty()) {
                   return parentList.getNode(0);
                }
            }
        }
        
        return null;
     }

    public static Relation getParentRelation(Node node, String relationName) {
        String[] treeManagers = new String[] { node.getNodeManager().getName() };
        return getParentRelation(node, treeManagers, relationName);
    }
    
    public static Relation getParentRelation(Node node, String[] treeManagers, String relationName) {
        for (int i = 0; i < treeManagers.length; i++) {
            String manager = treeManagers[i];
            
            if (node.getCloud().hasRelationManager(manager, node.getNodeManager().getName(), relationName)) {
                
                RelationList parentList = node.getRelations(relationName, node.getCloud().getNodeManager(manager), "SOURCE");
                if (!parentList.isEmpty()) {
                   return parentList.getRelation(0);
                }
            }
        }
        
        return null;
    }

    public static boolean isParent(Node source, Node dest, String relationName) {
        String[] treeManagers = new String[] { dest.getNodeManager().getName() };
        return isParent(source, dest, treeManagers, relationName);
    }
    
    public static boolean isParent(Node sourceChannel, Node destChannel, String[] treeManagers, String relationName) {
        boolean isParent = false;
        
        // check that the source channel is not a parent of the destination channel
        List pathNodes = getPathToRoot(destChannel, treeManagers, relationName);
        for (Iterator iter = pathNodes.iterator(); iter.hasNext();) {
            Node channel = (Node) iter.next();
            if (channel.getNumber() == sourceChannel.getNumber()) {
                isParent = true;
            }
        }
        return isParent;
    }
    
    
    /**
     * Find path to root
     * @param node - node
     * @param relationName relation between the managers
     * @return List with the path to the root. First item is the root and last is the node
     */
    public static List getPathToRoot(Node node, String relationName) {
        String[] treeManagers = new String[] { node.getNodeManager().getName() };
        return getPathToRoot(node, treeManagers, relationName);
    }
    
    /**
     * Find path to root
     * @param node - node
     * @param treeManagers managers used in the tree
     * @param relationName relation between the managers
     * @return List with the path to the root. First item is the root and last is the node
     */
    public static List<Node> getPathToRoot(Node node, String[] treeManagers, String relationName) {
       List<Node> ret = new ArrayList<Node>();
       Node parent = node;
       while (parent != null) {
          ret.add(parent);
          parent = getParent(parent, treeManagers, relationName);
       }
       Collections.reverse(ret);
       return ret;
    }

    /**
     * Creates a string that represents the root path.
     * @param cloud - MMbase cloud
     * @param node - MMbase node
     * @param relationName - relation between the managers
     * @param fragmentFieldname - fieldname the path is constructed with
     * @return String with the path to the root.
     */
    public static String getPathToRootString(Cloud cloud, String node, String relationName, String fragmentFieldname) {
       return getPathToRootString(cloud.getNode(node), relationName, fragmentFieldname);
    }
    
    /**
     * Creates a string that represents the root path.
     * @param node - MMBase node
     * @param relationName - relation between the managers
     * @param fragmentFieldname - fieldname the path is constructed with
     * @return String with the path to the root.
     */
    public static String getPathToRootString(Node node, String relationName, String fragmentFieldname) {
       return getPathToRootString(node, relationName, fragmentFieldname, true);
    }

    /**
     * Creates a string that represents the root path.
     * @param node - MMBase node
     * @param relationName - relation between the managers
     * @param fragmentFieldname - fieldname the path is constructed with
     * @param includeRoot - include root to the string
     * @return String with the path to the root.
     */
    public static String getPathToRootString(Node node, String relationName, String fragmentFieldname, boolean includeRoot) {
        String[] treeManagers = new String[] { node.getNodeManager().getName() };
        String[] fragmentFieldnames = new String[] { fragmentFieldname };
        return getPathToRootString(node, treeManagers, relationName, fragmentFieldnames, includeRoot);
    }
    
    
    /**
     * Creates a string that represents the root path.
     * @param node - MMBase node
     * @param treeManagers - managers used in the tree
     * @param relationName - relation between the managers
     * @param fragmentFieldnames - fieldnames the path is constructed with
     * @param includeRoot - include root to the string
     * @return String with the path to the root.
     */
    public static String getPathToRootString(Node node, String[] treeManagers, String relationName, String[] fragmentFieldnames, boolean includeRoot) {
       String pathStr = TreePathCache.getPathStringFromCache(treeManagers[0], node.getNumber());
       if (pathStr == null) {
          pathStr = "";
          List path = getPathToRoot(node, treeManagers, relationName) ;
          for (Iterator i = path.iterator(); i.hasNext(); ) {
             Node n = (Node) i.next();
             
             String nManagerName = n.getNodeManager().getName();
             String fragmentFieldname = getFragmentFieldname(nManagerName, treeManagers, fragmentFieldnames);
             
             pathStr += n.getStringValue(fragmentFieldname);
             
             if (i.hasNext()) {
                pathStr += PATH_SEPARATOR;
             }
          }
          if (!StringUtil.isEmpty(pathStr)) {
              TreePathCache.addToCache(treeManagers[0], pathStr, node.getNumber());
          }
       }
       
       if (includeRoot) {
          return pathStr;
       }
       else {
          int pathSepIndex = pathStr.indexOf(PATH_SEPARATOR);
          if (pathSepIndex > -1) {
             return pathStr.substring(pathSepIndex + PATH_SEPARATOR.length());
          }
          return "";
       }
    }

    public static String getFragmentFieldname(String nManagerName, String[] treeManagers, String[] fragmentFieldnames) {
        String fragmentFieldname = null;
         for (int j = 0; j < treeManagers.length; j++) {
            String treeManager = treeManagers[j];
            if (treeManager.equals(nManagerName)) {
                fragmentFieldname = fragmentFieldnames[j];
                break;
            }
        }
        return fragmentFieldname;
    }
    
    /**
     * Creates a string that represents the titles.
     * @param cloud - MMbase cloud
     * @param node - node number
     * @param relationName - relation between the managers
     * @param titleFieldname - fieldname the titles is constructed with
     * @return titles of nodes in path
     */
    public static String getTitlesString(Cloud cloud, String node, String relationName, String titleFieldname) {
       return getTitlesString(cloud, node, relationName, titleFieldname, true);
    }

    /**
     * Creates a string that represents the titles.
     * @param cloud - MMbase cloud
     * @param node - node number
     * @param relationName - relation between the managers
     * @param titleFieldname - fieldname the titles is constructed with
     * @param includeRoot - include root to the string
     * @return titles of nodes in path
     */
    public static String getTitlesString(Cloud cloud, String node, String relationName, String titleFieldname, boolean includeRoot) {
       return getTitlesString(cloud.getNode(node), relationName, titleFieldname, includeRoot);
    }

    /**
     * Creates a string that represents the titles.
     * @param node - node number
     * @param relationName - relation between the managers
     * @param titleFieldname - fieldname the titles is constructed with
     * @param includeRoot - include root to the string
     * @return titles of nodes in path
     */
    public static String getTitlesString(Node node, String relationName, String titleFieldname, boolean includeRoot) {
        String[] treeManagers = new String[] { node.getNodeManager().getName() };
        return getTitlesString(node, treeManagers ,relationName, titleFieldname, includeRoot);
    }
    
    /**
     * Creates a string that represents the titles.
     * @param node - node number
     * @param treeManagers - managers used in the tree
     * @param relationName - relation between the managers
     * @param titleFieldname - fieldname the titles is constructed with
     * @param includeRoot - include root to the string
     * @return titles of nodes in path
     */
    public static String getTitlesString(Node node, String[] treeManagers, String relationName, String titleFieldname, boolean includeRoot) {
       String result = "";
       List path = getPathToRoot(node, treeManagers, relationName);
       for (int i = 0; i < path.size(); i++) {
           Node n = (Node) path.get(i);
           if (i == path.size() - 1 && !includeRoot) {
               continue;
           }
           result += n.getStringValue(titleFieldname);
           if (i < path.size() - 1) {
              result += TITLE_SEPARATOR;
           }
       }
       return result;
    }

    
    /**
     * Method that finds the Channel node using a path as input.
     * @param cloud - MMbase cloud
     * @param path - path of channel
     * @param root - root node to start the search from
     * @param relationName - relation between the managers
     * @param fragmentFieldname - fieldname the path is constructed with
     * @return node with channel path
     */
    public static Node getChannelFromPath(Cloud cloud, String path, Node root,
            String relationName, String fragmentFieldname) {
         return getChannelFromPath(cloud, path, root, relationName, fragmentFieldname, true);
    }
    
    /**
     * Method that finds the Channel node using a path as input.
     * @param cloud - MMbase cloud
     * @param path - path of channel
     * @param root - root node to start the search from
     * @param relationName - relation between the managers
     * @param fragmentFieldname - fieldname the path is constructed with
     * @param useCache - use the path cache
     * @return node with channel path
     */
    public static Node getChannelFromPath(Cloud cloud, String path, Node root, String relationName,
            String fragmentFieldname, boolean useCache) {
        String[] treeManagers = new String[] { root.getNodeManager().getName() } ;
        String[] fragmentFieldnames = new String[] { fragmentFieldname };
        return getChannelFromPath(cloud, path, root, treeManagers, relationName, fragmentFieldnames, useCache);
    }
    
    /**
     * Method that finds the Channel node using a path as input.
     * @param cloud - MMbase cloud
     * @param path - path of channel
     * @param root - root node to start the search from
     * @param treeManagers - managers used in the tree
     * @param relationName - relation between the managers
     * @param fragmentFieldnames - fieldnames the path is constructed with
     * @param useCache - use the path cache
     * @return node with channel path
     */
    public static Node getChannelFromPath(Cloud cloud, String path, Node root, String[] treeManagers, 
            String relationName, String[] fragmentFieldnames, boolean useCache) {
     
       if (useCache) {
         Integer nodenr = TreePathCache.getChannelFromCache(treeManagers[0], path);
         if (nodenr != null && nodenr.intValue() > 0) {
             return cloud.getNode(nodenr.intValue());
         }
       }

       String nManagerName = root.getNodeManager().getName();
       String fragmentFieldname = getFragmentFieldname(nManagerName, treeManagers, fragmentFieldnames);
       
       String[] fragments = path.split(PATH_SEPARATOR);
       if (root.getStringValue(fragmentFieldname).equals(path)) {
          if (useCache) {
              TreePathCache.addToCache(treeManagers[0], path, root.getNumber());
          }
          return root;
       }
       Node channel = getChannelFromPath(cloud, fragments, root, treeManagers, relationName, fragmentFieldnames, 0);
       if (channel != null) {
         channel = cloud.getNode(channel.getNumber());
         if (useCache) {
             TreePathCache.addToCache(treeManagers[0], channel.getStringValue(PATH_FIELD), channel.getNumber());
         }
       }
       return channel;
    }

    private static Node getChannelFromPath(Cloud cloud, String[] fragments, Node root, String[] treeManagers,
            String relationName, String[] fragmentFieldnames, int level) {
        
        String nManagerName = root.getNodeManager().getName();
        String fragmentFieldname = getFragmentFieldname(nManagerName, treeManagers, fragmentFieldnames);

       String field = root.getStringValue(fragmentFieldname);
       
       if (fragments[level].equalsIgnoreCase(field) && level==fragments.length-1) {
         return root;
       }
       else {
            if (level != fragments.length - 1) {
                NodeList nl = getChildren(root, treeManagers, relationName);
                NodeIterator nli = nl.nodeIterator();
                while (nli.hasNext()) {
                    Node element = nli.nextNode();

                    field = element.getStringValue(fragmentFieldname);
                    if (fragments[level + 1].equalsIgnoreCase(field)) { 
                        return getChannelFromPath(cloud, fragments, element, treeManagers, relationName, fragmentFieldnames, level + 1); 
                    }
                }
            }
        }
       return null;
    }

    public static NodeList getChildren(Node parentNode, String[] treeManagers, String relationName) {
        for (int i = 0; i < treeManagers.length; i++) {
            String manager = treeManagers[i];
            
            if (parentNode.getCloud().hasRelationManager(parentNode.getNodeManager().getName(), manager, relationName)) {
                NodeList childerenList = parentNode.getRelatedNodes(manager, relationName, DESTINATION);
                if (!childerenList.isEmpty()) {
                   return childerenList;
                }
                if (i == treeManagers.length -1) {
                    return childerenList;
                }
            }
        }
        return null;
    }
    
    public static NodeList getChildren(Node parentNode, String relationName) {
        return parentNode.getRelatedNodes(parentNode.getNodeManager().getName(), relationName, DESTINATION);
    }

    public static NodeList getChildren(Node parentNode, String nodeManager, String relationName) {
        return parentNode.getRelatedNodes(nodeManager, relationName, DESTINATION);
    }

    public static int getLevel(String path) {
        if (StringUtil.isEmpty(path.trim())) { 
            return 0; 
        }
        String[] fragements = path.split(PATH_SEPARATOR);
        return fragements.length;
    }
    
    public static int getChildCount(Node parent, String relationName) {
       return getChildCount(parent, parent.getNodeManager(), relationName);
    }

    public static int getChildCount(Node parent, String[] treeManagers, String relationName) {
        return getChildCount(parent, parent.getCloud().getNodeManager(treeManagers[0]), relationName);
    }

    public static int getChildCount(Node parent, String nodeManager, String relationName) {
        return getChildCount(parent, parent.getCloud().getNodeManager(nodeManager), relationName);
    }

    public static int getChildCount(Node parent, NodeManager nodeManager, String relationName) {
        return parent.countRelatedNodes(nodeManager, relationName, DESTINATION);
    }

    public static boolean hasChild(Node parentChannel, String fragment, String relationName, String fragmentfield) {
        NodeList children = getChildren(parentChannel, relationName);
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Node child = (Node) iter.next();
            String value = child.getStringValue(fragmentfield);
            if (value.equals(fragment)) {
                return true;
            }
        }

        return false;
    }

    public static Node getChild(Node parentChannel, String fragment, String relationName, String fragmentfield) {
        NodeList children = getChildren(parentChannel, relationName);
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Node child = (Node) iter.next();
            String value = child.getStringValue(fragmentfield);
            if (value.equals(fragment)) {
                return child;
            }
        }
        return null;
    }
}
