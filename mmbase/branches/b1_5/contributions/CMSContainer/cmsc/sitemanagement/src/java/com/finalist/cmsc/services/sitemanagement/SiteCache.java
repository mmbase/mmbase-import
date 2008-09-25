/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.*;

import org.mmbase.bridge.Node;
import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;
import com.finalist.cmsc.services.sitemanagement.tree.PageTreeNode;

public final class SiteCache implements RelationEventListener, NodeEventListener {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(SiteCache.class);

   private final SiteCacheLoader loader;
   private Map<String, PageTree> trees = new HashMap<String, PageTree>();

   public SiteCache() {
      this(new SiteCacheLoader());
   }

   public SiteCache(SiteCacheLoader loader) {
      this.loader = loader;
      doSetupCache();
      registerListeners();
   }

   public void registerListeners() {
       List<NavigationItemManager> navigationManagers = loader.getNavigationManagers();
       for (NavigationItemManager nim : navigationManagers) {
           if (!nim.isRoot()) {
               String nodeType = nim.getTreeManager();
               MMBase.getMMBase().addNodeRelatedEventsListener(nodeType, this);
           }
       }
   }

   public void doSetupCache() {
       Map<String, PageTree> newtrees = loader.loadPageTreeMap();
       trees = newtrees;
   }

   public void createTree(int siteId, String sitefragment) {
       PageTree siteTree = new PageTree(siteId, sitefragment);
       trees.put(sitefragment.toLowerCase(), siteTree);
    }


   public Integer getSite(String path) {
      List<String> names = PageTree.getPathElements(path);
      PageTree tree = getTree(names.get(0));
      if (tree != null) {
         return tree.getRoot().getPage();
      }
      return null;
   }


   public Integer getPage(String path) {
      PageTreeNode pageTreeNode = getPageTreeNode(path);
      if (pageTreeNode != null) {
         return pageTreeNode.getPage();
      }
      return null;
   }


   private PageTreeNode getPageTreeNode(String path) {
      PageTreeNode pageTreeNode = null;
      List<String> names = PageTree.getPathElements(path);
      PageTree tree = getTree(names.get(0));
      if (tree != null) {
         pageTreeNode = tree.getPath(names);
      }
      return pageTreeNode;
   }


   private PageTreeNode getPageTreeNode(NavigationItem findpage) {
      int id = findpage.getId();
      return getPageTreeNode(id);
   }


   private PageTreeNode getPageTreeNode(int id) {
      for (PageTree tree : trees.values()) {
         PageTreeNode pageTreeNode = tree.getPageTreeNode(id);
         if (pageTreeNode != null) {
            return pageTreeNode;
         }
      }
      return null;
   }


   private PageTree getTree(int id) {
      for (PageTree tree : trees.values()) {
         PageTreeNode pageTreeNode = tree.getPageTreeNode(id);
         if (pageTreeNode != null) {
            return tree;
         }
      }
      return null;
   }


   private PageTree getTree(String name) {
      PageTree tree = null;
      if (trees.containsKey(name.toLowerCase())) {
         tree = trees.get(name.toLowerCase());
      }
      return tree;
   }


   public List<Integer> getSites() {
      List<Integer> siteIds = new ArrayList<Integer>();
      for (PageTree tree : trees.values()) {
         siteIds.add(tree.getRoot().getPage());
      }
      return siteIds;
   }


   public String getSite(NavigationItem page) {
      for (String site : trees.keySet()) {
         PageTree tree = trees.get(site);
         if (tree.containsPageTreeNode(page.getId())) {
            return tree.getRoot().getPathStr();
         }
      }
      return null;
   }


   public List<Integer> getChildren(NavigationItem findpage) {
      List<Integer> pageIds = new ArrayList<Integer>();

      PageTreeNode parent = getPageTreeNode(findpage);
      if (parent != null) {
         for (PageTreeNode treeNode : parent.getChildren()) {
            pageIds.add(treeNode.getPage());
         }
      }
      return pageIds;
   }


   public List<Integer> getItemsForPath(String path) {
      List<Integer> pageIds = new ArrayList<Integer>();

      List<String> names = PageTree.getPathElements(path);
      PageTree tree = getTree(names.get(0));
      if (tree != null) {
         tree.addPages(names, pageIds);
      }
      return pageIds;
   }


   public String getPath(NavigationItem item, boolean includeRoot) {
      PageTreeNode pageTreeNode = getPageTreeNode(item);
      if (pageTreeNode != null) {
         return pageTreeNode.getPathStr(includeRoot);
      }
      return null;
   }


   public void notify(RelationEvent event) {
      if (isChangeTreeEvent(event)) {
         switch (event.getType()) {
            case Event.TYPE_CHANGE: {
               if (log.isDebugEnabled()) {
                  log.debug("change " + event);
                  for (Object field : event.getNodeEvent().getChangedFields()) {
                     log.debug("changed relation field " + field);
                  }
               }
               if (event.getNodeEvent().getChangedFields().contains(TreeUtil.RELATION_POS_FIELD)) {
                  int destinationNumber = event.getRelationDestinationNumber();
                  int childIndex = (Integer) event.getNodeEvent().getNewValue(TreeUtil.RELATION_POS_FIELD);

                  for (PageTree tree : trees.values()) {
                     PageTreeNode pageTreeNode = tree.getPageTreeNode(destinationNumber);
                     if (pageTreeNode != null) {
                        tree.move(pageTreeNode, childIndex);
                        break;
                     }
                  }
               }
               break;
            }
            case Event.TYPE_DELETE: {
               if (log.isDebugEnabled()) {
                  log.debug("delete " + event);
               }
               int sourceNumber = event.getRelationSourceNumber();
               int destinationNumber = event.getRelationDestinationNumber();
               Node sourceNode = loader.getCloud().getNode(sourceNumber);
               String path = sourceNode.getStringValue(TreeUtil.PATH_FIELD);

               List<String> names = PageTree.getPathElements(path);
               PageTree tree = getTree(names.get(0));
               if (tree != null) {
                  tree.remove(path, destinationNumber);
               }
            }
               break;
            case Event.TYPE_NEW: {
               if (log.isDebugEnabled()) {
                  log.debug("new " + event);
               }
               int destinationNumber = event.getRelationDestinationNumber();
               PageTreeNode destTreeNode = getPageTreeNode(destinationNumber);
               int childIndex = (Integer) event.getNodeEvent().getNewValue(TreeUtil.RELATION_POS_FIELD);
               if (destTreeNode == null) {
                  // create new PageYreeNode
                  Node destNode = loader.getCloud().getNode(destinationNumber);

                  if (NavigationUtil.getChildCount(destNode) == 0) {
                     String path = destNode.getStringValue(TreeUtil.PATH_FIELD);
                     List<String> names = PageTree.getPathElements(path);
                     PageTree tree = getTree(names.get(0));
                     if (tree != null) {
                        tree.insert(path, destinationNumber, childIndex);
                     }
                  }
                  else {
                     // this is NOT a new navigation item. SiteCache is invalid.
                     // fastest way to recover (minimal number of queries) is a reset of the cache.
                     doSetupCache();
                  }
               }
               else {
                  // move PageTreeNode
                  int sourceNumber = event.getRelationSourceNumber();

                  PageTree destTree = getTree(destinationNumber);
                  PageTree sourceTree = getTree(sourceNumber);
                  if (sourceTree == destTree) {
                     destTree.move(destTreeNode, sourceNumber, childIndex);
                  }
                  else {
                     destTree.move(destTreeNode, sourceTree, sourceNumber, childIndex);
                  }
               }
            }
               break;
            case NodeEvent.TYPE_RELATION_CHANGE:
               break;
            default:
               break;
         }
      }
   }


   private boolean isChangeTreeEvent(RelationEvent event) {
      int relationNumber = loader.getNavrelRelationNumber();
      if (event.getRole() == relationNumber) {
          boolean sourceIsTreeType = NavigationManager.getNavigationManager(event.getRelationSourceType()) != null;
          boolean destinationIsTreeType = NavigationManager.getNavigationManager(event.getRelationDestinationType()) != null;
          return sourceIsTreeType && destinationIsTreeType;
      }
      return false;
   }

   public void notify(NodeEvent event) {
      Integer key = event.getNodeNumber();
      if (key != null) {
        String nodeType = event.getBuilderName();
        NavigationItemManager navigationManager = NavigationManager.getNavigationManager(nodeType);
        switch (event.getType()) {
            case Event.TYPE_CHANGE:
              String fragmentField = NavigationUtil.getFragmentFieldname(nodeType);
              if (event.getChangedFields().contains(fragmentField)) {
                  String newFragment = (String) event.getNewValue(fragmentField);
                  if (navigationManager.isRoot()) {

                     //Search for keys to change
                     ArrayList<PageTree> addList = new ArrayList<PageTree>();
                     for (PageTree tree : trees.values()) {
                          if (tree.containsPageTreeNode(key)) {
                             addList.add(tree);
                        }
                     }

                     //Now change the 'trees' Map (otherwise the Map gets corrupted)
                     for (PageTree tree : addList) {
                        trees.remove(tree.getRoot().getPathStr().toLowerCase());
                        trees.put(newFragment.toLowerCase(), tree);
                        tree.replace(key, newFragment);
                     }
                  }
                  else {
                      for (PageTree tree : trees.values()) {
                          if (tree.containsPageTreeNode(key)) {
                              tree.replace(key, newFragment);
                          }
                      }
                  }
              }
               break;
            case Event.TYPE_DELETE:
               if (navigationManager.isRoot()) {
                  for (PageTree tree : trees.values()) {
                     if (tree.containsPageTreeNode(key)) {
                        trees.remove(tree.getRoot().getPathStr().toLowerCase());
                        break;
                     }
                  }
               }
               break;
            case Event.TYPE_NEW:
               if (navigationManager.isRoot()) {
                  String rootFragmentField = NavigationUtil.getFragmentFieldname(nodeType);
                  String newFragment = (String) event.getNewValue(rootFragmentField);
                  createTree(event.getNodeNumber(), newFragment);
               }
               break;
            case NodeEvent.TYPE_RELATION_CHANGE:
               break;
            default:
               break;
         }
      }
   }
}
