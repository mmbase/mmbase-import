/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.mmbase;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;

import com.finalist.cmsc.util.EncodingUtil;


public final class TreeUtil {


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

   public static void uniqueChild(Node parentNode, Node newChildNode, LinkedHashMap<String, String> treeManagers, String relationName) {
      String fragmentFieldname = getFragmentFieldname(newChildNode, treeManagers);
      String fragment = newChildNode.getStringValue(fragmentFieldname);

      NodeList children = getChildren(parentNode, relationName);
      int startsWithIndex = 0;
      boolean foundExistingChildFragment = false;
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node child = iter.next();
         String fragmentfield = getFragmentFieldname(child, treeManagers);
         String value = child.getStringValue(fragmentfield);
         if (value.equals(fragment)) {
            foundExistingChildFragment = true;
         }
         else {
            if (value.startsWith(fragment + "_")) {
               startsWithIndex++;
            }
         }
      }
      if (foundExistingChildFragment) {
         newChildNode.setStringValue(fragmentFieldname, fragment + "_" + (startsWithIndex+1));
         newChildNode.commit();
      }
   }
   
   public static Node getParent(Node node, String relationName) {
      List<String> treeManagers = new ArrayList<String>();
      treeManagers.add(node.getNodeManager().getName());
      return getParent(node, treeManagers, relationName);
   }

   public static Node getParent(Node node, LinkedHashMap<String, String> treeManagers, String relationName) {
      return getParent(node, convertToList(treeManagers), relationName);
   }

   public static Node getParent(Node node, List<String> treeManagers, String relationName) {
      for (String manager : treeManagers) {
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
      List<String> treeManagers = new ArrayList<String>();
      treeManagers.add(node.getNodeManager().getName());
      return getParentRelation(node, treeManagers, relationName);
   }

   public static Relation getParentRelation(Node node, List<String> treeManagers, String relationName) {
      for (String manager : treeManagers) {
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
      List<String> treeManagers = new ArrayList<String>();
      treeManagers.add(dest.getNodeManager().getName());
      return isParent(source, dest, treeManagers, relationName);
   }

   public static boolean isParent(Node sourceItem, Node destItem, List<String> treeManagers, String relationName) {
      boolean isParent = false;

      // check that the source item is not a parent of the destination item
      List<Node> pathNodes = getPathToRoot(destItem, treeManagers, relationName);
      for (Node item : pathNodes) {
         if (item.getNumber() == sourceItem.getNumber()) {
            isParent = true;
         }
      }
      return isParent;
   }


   /**
    * Find path to root
    *
    * @param node         - node
    * @param relationName relation between the managers
    * @return List with the path to the root. First item is the root and last is the node
    */
   public static List<Node> getPathToRoot(Node node, String relationName) {
      List<String> treeManagers = new ArrayList<String>();
      treeManagers.add(node.getNodeManager().getName());
      return getPathToRoot(node, treeManagers, relationName);
   }

   /**
    * Find path to root
    *
    * @param node         - node
    * @param treeManagers managers used in the tree
    * @param relationName relation between the managers
    * @return List with the path to the root. First item is the root and last is the node
    */
   public static List<Node> getPathToRoot(Node node, LinkedHashMap<String, String> treeManagers, String relationName) {
      return getPathToRoot(node, convertToList(treeManagers), relationName);
   }

   public static List<Node> getPathToRoot(Node node, List<String> treeManagers, String relationName) {
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
    *
    * @param cloud             - MMbase cloud
    * @param node              - MMbase node
    * @param relationName      - relation between the managers
    * @param fragmentFieldname - fieldname the path is constructed with
    * @return String with the path to the root.
    */
   public static String getPathToRootString(Cloud cloud, String node, String relationName, String fragmentFieldname) {
      return getPathToRootString(cloud.getNode(node), relationName, fragmentFieldname);
   }

   /**
    * Creates a string that represents the root path.
    *
    * @param node              - MMBase node
    * @param relationName      - relation between the managers
    * @param fragmentFieldname - fieldname the path is constructed with
    * @return String with the path to the root.
    */
   public static String getPathToRootString(Node node, String relationName, String fragmentFieldname) {
      return getPathToRootString(node, relationName, fragmentFieldname, true);
   }

   /**
    * Creates a string that represents the root path.
    *
    * @param node              - MMBase node
    * @param relationName      - relation between the managers
    * @param fragmentFieldname - fieldname the path is constructed with
    * @param includeRoot       - include root to the string
    * @return String with the path to the root.
    */
   public static String getPathToRootString(Node node, String relationName, String fragmentFieldname, boolean includeRoot) {
      LinkedHashMap<String, String> treeManagers = createTreeManagers(node, fragmentFieldname);
      return getPathToRootString(node, treeManagers, relationName, includeRoot);
   }

   /**
    * Creates a string that represents the root path.
    *
    * @param node         - MMBase node
    * @param treeManagers - managers used in the tree
    * @param relationName - relation between the managers
    * @param includeRoot  - include root to the string
    * @return String with the path to the root.
    */
   public static String getPathToRootString(Node node, LinkedHashMap<String, String> treeManagers, String relationName, boolean includeRoot) {
      String pathStr = TreePathCache.getPathStringFromCache(getRootManager(treeManagers), node.getNumber());
      if (pathStr == null) {
         pathStr = getPathToRootStringWithoutCache(node, treeManagers, relationName);
         if (StringUtils.isNotEmpty(pathStr)) {
            TreePathCache.addToCache(getRootManager(treeManagers), pathStr, node.getNumber());
         }
      }

      if (includeRoot) {
         return pathStr;
      } else {
         int pathSepIndex = pathStr.indexOf(PATH_SEPARATOR);
         if (pathSepIndex > -1) {
            return pathStr.substring(pathSepIndex + PATH_SEPARATOR.length());
         }
         return "";
      }
   }

   public static String getPathToRootStringWithoutCache(Node node, LinkedHashMap<String, String> treeManagers, String relationName) {
      String pathStr = "";
      List<Node> path = getPathToRoot(node, convertToList(treeManagers), relationName);
      for (Iterator<Node> i = path.iterator(); i.hasNext();) {
         Node n = i.next();

         String fragmentFieldname = getFragmentFieldname(n, treeManagers);

         pathStr += n.getStringValue(fragmentFieldname);

         if (i.hasNext()) {
            pathStr += PATH_SEPARATOR;
         }
      }
      return pathStr;
   }

   public static String[] getPathElementsToRoot(Node node, LinkedHashMap<String, String> treeManagers, String relationName, boolean includeRoot) {
      String path = getPathToRootString(node, treeManagers, relationName, includeRoot);
      return path.split(PATH_SEPARATOR);
   }

   public static String getFragmentFieldname(Node node, LinkedHashMap<String, String> treeManagers) {
      return getFragmentFieldname(node.getNodeManager().getName(), treeManagers);
   }
   
   public static String getFragmentFieldname(String nManagerName, LinkedHashMap<String, String> treeManagers) {
      return treeManagers.get(nManagerName);
   }

   /**
    * Creates a string that represents the titles.
    *
    * @param cloud          - MMbase cloud
    * @param node           - node number
    * @param relationName   - relation between the managers
    * @param titleFieldname - fieldname the titles is constructed with
    * @return titles of nodes in path
    */
   public static String getTitlesString(Cloud cloud, String node, String relationName, String titleFieldname) {
      return getTitlesString(cloud, node, relationName, titleFieldname, true);
   }

   /**
    * Creates a string that represents the titles.
    *
    * @param cloud          - MMbase cloud
    * @param node           - node number
    * @param relationName   - relation between the managers
    * @param titleFieldname - fieldname the titles is constructed with
    * @param includeRoot    - include root to the string
    * @return titles of nodes in path
    */
   public static String getTitlesString(Cloud cloud, String node, String relationName, String titleFieldname, boolean includeRoot) {
      return getTitlesString(cloud.getNode(node), relationName, titleFieldname, includeRoot);
   }

   /**
    * Creates a string that represents the titles.
    *
    * @param node           - node number
    * @param relationName   - relation between the managers
    * @param titleFieldname - fieldname the titles is constructed with
    * @param includeRoot    - include root to the string
    * @return titles of nodes in path
    */
   public static String getTitlesString(Node node, String relationName, String titleFieldname, boolean includeRoot) {
      List<String> treeManagers = createTreeManagers(node);
      return getTitlesString(node, treeManagers, relationName, titleFieldname, includeRoot);
   }

   /**
    * Creates a string that represents the titles.
    *
    * @param node           - node number
    * @param treeManagers   - managers used in the tree
    * @param relationName   - relation between the managers
    * @param titleFieldname - fieldname the titles is constructed with
    * @param includeRoot    - include root to the string
    * @return titles of nodes in path
    */
   public static String getTitlesString(Node node, LinkedHashMap<String, String> treeManagers, String relationName, String titleFieldname, boolean includeRoot) {
      return getTitlesString(node, convertToList(treeManagers), relationName, titleFieldname, includeRoot);
   }

   public static String getTitlesString(Node node, List<String> treeManagers, String relationName, String titleFieldname, boolean includeRoot) {
      String result = "";
      List<Node> path = getPathToRoot(node, treeManagers, relationName);
      for (int i = 0; i < path.size(); i++) {
         Node n = path.get(i);
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
    * Method that finds the tree item node using a path as input.
    *
    * @param cloud             - MMbase cloud
    * @param path              - path of tree
    * @param root              - root node to start the search from
    * @param relationName      - relation between the managers
    * @param fragmentFieldname - fieldname the path is constructed with
    * @return node with tree path
    */
   public static Node getTreeItemFromPath(Cloud cloud, String path, Node root,
                                          String relationName, String fragmentFieldname) {
      return getTreeItemFromPath(cloud, path, root, relationName, fragmentFieldname, true);
   }

   /**
    * Method that finds the tree item node using a path as input.
    *
    * @param cloud             - MMbase cloud
    * @param path              - path of tree
    * @param root              - root node to start the search from
    * @param relationName      - relation between the managers
    * @param fragmentFieldname - fieldname the path is constructed with
    * @param useCache          - use the path cache
    * @return node with tree path
    */
   public static Node getTreeItemFromPath(Cloud cloud, String path, Node root, String relationName,
                                          String fragmentFieldname, boolean useCache) {
      LinkedHashMap<String, String> treeManagers = createTreeManagers(root, fragmentFieldname);
      return getTreeItemFromPath(cloud, path, root, treeManagers, relationName, useCache);
   }

   /**
    * Method that finds the tree item node using a path as input.
    *
    * @param cloud        - MMbase cloud
    * @param path         - path of tree
    * @param root         - root node to start the search from
    * @param treeManagers - managers used in the tree
    * @param relationName - relation between the managers
    * @param useCache     - use the path cache
    * @return node with tree path
    */
   public static Node getTreeItemFromPath(Cloud cloud, String path, Node root, LinkedHashMap<String, String> treeManagers,
                                          String relationName, boolean useCache) {

      if (useCache) {
         String rootManager = getRootManager(treeManagers);
         Integer nodenr = TreePathCache.getTreeItemFromCache(rootManager, path);
         if (nodenr != null && nodenr.intValue() > 0) {
            return cloud.getNode(nodenr.intValue());
         }
      }

      String fragmentFieldname = getFragmentFieldname(root, treeManagers);

      String[] fragments = getPathFragments(path);
      if (root.getStringValue(fragmentFieldname).equals(path)) {
         if (useCache) {
            TreePathCache.addToCache(getRootManager(treeManagers), path, root.getNumber());
         }
         return root;
      }
      Node item = getTreeItemFromPath(cloud, fragments, root, treeManagers, relationName, 0);
      if (item != null) {
         item = cloud.getNode(item.getNumber());
         if (useCache) {
            TreePathCache.addToCache(getRootManager(treeManagers), item.getStringValue(PATH_FIELD), item.getNumber());
         }
      }
      return item;
   }

   public static String[] getPathFragments(String path) {
      String[] fragments = path.split(PATH_SEPARATOR);
      return fragments;
   }

   private static Node getTreeItemFromPath(Cloud cloud, String[] fragments, Node root, LinkedHashMap<String, String> treeManagers,
                                           String relationName, int level) {

      String fragmentFieldname = getFragmentFieldname(root, treeManagers);

      String field = root.getStringValue(fragmentFieldname);

      if (fragments[level].equalsIgnoreCase(field) && level == fragments.length - 1) {
         return root;
      } else {
         if (level != fragments.length - 1) {
            NodeList nl = getChildren(root, convertToList(treeManagers), relationName);
            NodeIterator nli = nl.nodeIterator();
            while (nli.hasNext()) {
               Node element = nli.nextNode();
               String elementFieldname = getFragmentFieldname(element, treeManagers);

               field = element.getStringValue(elementFieldname);
               if (fragments[level + 1].equalsIgnoreCase(field)) {
                  return getTreeItemFromPath(cloud, fragments, element, treeManagers, relationName, level + 1);
               }
            }
         }
      }
      return null;
   }

   public static NodeList getChildren(Node parentNode, List<String> treeManagers, String relationName) {
      NodeList list = parentNode.getCloud().createNodeList();
      for (String manager : treeManagers) {
         if (parentNode.getCloud().hasRelationManager(parentNode.getNodeManager().getName(), manager, relationName)) {
            NodeList childrenList = parentNode.getRelatedNodes(manager, relationName, DESTINATION);
            if (!childrenList.isEmpty()) {
               list.addAll(childrenList);
            }
         }
      }
      return list;
   }

   public static NodeList getChildren(Node parentNode, String relationName) {
      return parentNode.getRelatedNodes("object", relationName, DESTINATION);
   }

   public static NodeList getChildren(Node parentNode, String nodeManager, String relationName) {
      return parentNode.getRelatedNodes(nodeManager, relationName, DESTINATION);
   }

   public static int getLevel(String path) {
      if (StringUtils.isEmpty(path.trim())) {
         return 0;
      }
      String[] fragements = path.split(PATH_SEPARATOR);
      return fragements.length;
   }

   public static int getChildCount(Node parent, String relationName) {
      return getChildCount(parent, parent.getNodeManager(), relationName);
   }

   public static int getChildCount(Node parent, List<String> treeManagers, String relationName) {
      return getChildCount(parent, parent.getCloud().getNodeManager(getRootManager(treeManagers)), relationName);
   }

   public static int getChildCount(Node parent, String nodeManager, String relationName) {
      return getChildCount(parent, parent.getCloud().getNodeManager(nodeManager), relationName);
   }

   public static int getChildCount(Node parent, NodeManager nodeManager, String relationName) {
      return parent.countRelatedNodes(nodeManager, relationName, DESTINATION);
   }

   public static boolean hasChild(Node parent, String fragment, String relationName, String fragmentfield) {
      LinkedHashMap<String, String> treeManagers = createTreeManagers(parent, fragmentfield);
      return hasChild(parent, fragment, treeManagers, relationName);
   }

   public static boolean hasChild(Node parent, String fragment, LinkedHashMap<String, String> treeManagers,
                                  String relationName) {
      NodeList children = getChildren(parent, relationName);
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node child = iter.next();
         String fragmentfield = getFragmentFieldname(child, treeManagers);
         String value = child.getStringValue(fragmentfield);
         if (value.equals(fragment)) {
            return true;
         }
      }

      return false;
   }

   public static Node getChild(Node parent, String fragment, String relationName, String fragmentfield) {
      LinkedHashMap<String, String> treeManagers = createTreeManagers(parent, fragmentfield);
      return getChild(parent, fragment, treeManagers, relationName);
   }

   public static Node getChild(Node parentItem, String fragment, LinkedHashMap<String, String> treeManagers,
                               String relationName) {
      NodeList children = getChildren(parentItem, relationName);
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node child = iter.next();
         String fragmentfield = getFragmentFieldname(child, treeManagers);
         String value = child.getStringValue(fragmentfield);
         if (value.equals(fragment)) {
            return child;
         }
      }
      return null;
   }

   public static String convertToFragment(String name) {

      //todo make a common solution for urlgragment  generation.
      name = replaceChineseCharacter(name);

      String pathFragment = EncodingUtil.convertNonAscii(name);
      pathFragment = pathFragment.replaceAll("\\s", "_");
      while (pathFragment.length() > 1 && pathFragment.substring(0, 1).matches("[_.-]")) {
         pathFragment = pathFragment.substring(1, pathFragment.length());
      }
      pathFragment = pathFragment.replaceAll("[^a-zA-Z_0-9_.-]", "");
      pathFragment = pathFragment.toLowerCase();
      return pathFragment;
   }

   private static String replaceChineseCharacter(String input) {
      Pattern pa = Pattern.compile("[\u4E00-\u9FA0]", Pattern.CANON_EQ);
      Matcher m = pa.matcher(input);
      if (m.find()) {
         return RandomStringUtils.randomAlphabetic(30);
      }

      return input;
   }


   public static List<String> createTreeManagers(Node node) {
      List<String> treeManagers = new ArrayList<String>();
      treeManagers.add(node.getNodeManager().getName());
      return treeManagers;
   }


   public static LinkedHashMap<String, String> createTreeManagers(Node node, String fragmentFieldname) {
      LinkedHashMap<String, String> treeManagers = new LinkedHashMap<String, String>();
      treeManagers.put(node.getNodeManager().getName(), fragmentFieldname);
      return treeManagers;
   }

   public static String getRootManager(List<String> treeManagers) {
      return treeManagers.get(0);
   }

   public static String getRootManager(LinkedHashMap<String, String> treeManagers) {
      return treeManagers.keySet().iterator().next();
   }

   public static List<String> convertToList(LinkedHashMap<String, String> treeManagers) {
      return new ArrayList<String>(treeManagers.keySet());
   }
}
