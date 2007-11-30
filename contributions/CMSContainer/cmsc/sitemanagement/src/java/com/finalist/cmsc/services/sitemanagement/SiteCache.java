/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Query;
import org.mmbase.cache.CachePolicy;
import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.core.event.RelationEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.storage.search.StepField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;
import com.finalist.cmsc.services.sitemanagement.tree.PageTreeNode;

public class SiteCache implements RelationEventListener, NodeEventListener {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(SiteCache.class.getName());

   private CloudProvider cloudProvider;
   private Map<String, PageTree> trees = new HashMap<String, PageTree>();


   public SiteCache() {
      this.cloudProvider = CloudProviderFactory.getCloudProvider();
      doSetupCache();
   }


   public void doSetupCache() {
       Cloud cloud = getCloud();
       List<NavigationItemManager> navigationManagers = NavigationManager.getNavigationManagers();
       for (NavigationItemManager nim : navigationManagers) {
           if (nim.isRoot()) {
               String nodeType = nim.getTreeManager();
               String fragmentField = NavigationUtil.getFragmentFieldname(nodeType);
               loadTrees(cloud, nodeType, fragmentField);
           }
       }

       for (NavigationItemManager nim : navigationManagers) {
           if (!nim.isRoot()) {
               String nodeType = nim.getTreeManager();
               String fragmentField = NavigationUtil.getFragmentFieldname(nodeType);
               loadNavigationItems(cloud, nodeType, fragmentField);
           }
       }
   }
   
   private void loadTrees(Cloud cloud, String nodeType, String fragmentField) {
       NodeManager sitesManager = cloud.getNodeManager(nodeType);
       NodeList sites = sitesManager.getList(sitesManager.createQuery());
       for (Iterator<Node> iter = sites.iterator(); iter.hasNext();) {
          Node siteNode = iter.next();
           
          int siteId = siteNode.getNumber();
          String sitefragment = siteNode.getStringValue(fragmentField);
          createTree(siteId, sitefragment);
       }

   }


   @SuppressWarnings("unchecked")
   private void loadNavigationItems(Cloud cloud, String nodeType, String fragmentField) {
      List<Node> unfinishedNodes = new ArrayList<Node>();

      NodeManager navrel = cloud.getNodeManager(NavigationUtil.NAVREL);
      NodeManager page = cloud.getNodeManager(nodeType);

      Query q = cloud.createQuery();
      q.addStep(navrel);
      q.addStep(page);
      // q.removeFields(false);

      StepField sourceField = q.addField(NavigationUtil.NAVREL + ".snumber");
      StepField destField = q.addField(NavigationUtil.NAVREL + ".dnumber");
      StepField posField = q.addField(NavigationUtil.NAVREL + ".pos");
      StepField pageNumberField = q.addField(nodeType + ".number");

      q.addField(nodeType + "." + fragmentField);
      q.setConstraint(q.createConstraint(destField, FieldCompareConstraint.EQUAL, pageNumberField));
      q.addSortOrder(sourceField, SortOrder.ORDER_ASCENDING);
      q.addSortOrder(posField, SortOrder.ORDER_ASCENDING);
      q.setCachePolicy(CachePolicy.NEVER);

      NodeList navrels = cloud.getList(q);
      for (Iterator<Node> iter = navrels.iterator(); iter.hasNext();) {
         Node navrelNode = iter.next();

         int sourceNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".snumber");
         int destNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".dnumber");
         int childIndex = navrelNode.getIntValue(NavigationUtil.NAVREL + ".pos");
         String fragment = navrelNode.getStringValue(nodeType + "." + fragmentField);

         boolean parentNotFound = true;
         for (PageTree tree : trees.values()) {
            PageTreeNode pageTreeNode = tree.insert(sourceNumber, destNumber, fragment, childIndex);
            if (pageTreeNode != null) {
               parentNotFound = false;
               break;
            }
         }
         if (parentNotFound) {
            unfinishedNodes.add(navrelNode);
         }
      }
      int oldUnfinishedSize = unfinishedNodes.size() + 1;
      while (oldUnfinishedSize > unfinishedNodes.size()) {
         oldUnfinishedSize = unfinishedNodes.size();

         for (Iterator<Node> iter = unfinishedNodes.iterator(); iter.hasNext();) {
            Node navrelNode = iter.next();

            int sourceNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".snumber");
            int destNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".dnumber");
            int childIndex = navrelNode.getIntValue(NavigationUtil.NAVREL + ".pos");
            String fragment = navrelNode.getStringValue(nodeType + "." + fragmentField);

            for (PageTree tree : trees.values()) {
               PageTreeNode pageTreeNode = tree.insert(sourceNumber, destNumber, fragment, childIndex);
               if (pageTreeNode != null) {
                  iter.remove();
                  break;
               }
            }
         }
      }

      for (Iterator<Node> iter = unfinishedNodes.iterator(); iter.hasNext();) {
         Node navrelNode = iter.next();
         log.warn("Page treenode not found for navrel: " + navrelNode);
      }

      MMBase.getMMBase().addNodeRelatedEventsListener(nodeType, this);
   }


   public void createTree(int siteId, String sitefragment) {
      PageTree siteTree = new PageTree(siteId, sitefragment);
      trees.put(sitefragment.toLowerCase(), siteTree);
   }


   protected Cloud getAdminCloud() {
      return cloudProvider.getAdminCloud();
   }


   protected Cloud getCloud() {
      return cloudProvider.getAnonymousCloud();
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


   public List<Integer> getPagesForPath(String path) {
      List<Integer> pageIds = new ArrayList<Integer>();

      List<String> names = PageTree.getPathElements(path);
      PageTree tree = getTree(names.get(0));
      if (tree != null) {
         tree.addPages(names, pageIds);
      }
      return pageIds;
   }


   public String getPath(NavigationItem page, boolean includeRoot) {
      PageTreeNode pageTreeNode = getPageTreeNode(page);
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
               Node sourceNode = getCloud().getNode(sourceNumber);
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
                  Node destNode = getCloud().getNode(destinationNumber);
                  String path = destNode.getStringValue(TreeUtil.PATH_FIELD);
                  List<String> names = PageTree.getPathElements(path);
                  PageTree tree = getTree(names.get(0));
                  if (tree != null) {
                     tree.insert(path, destinationNumber, childIndex);
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
      boolean sourceIsTreeType = false;
      boolean destinationIsTreeType = false;

      for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
         String treeManager = manager.getTreeManager();
         if (treeManager.equals(event.getRelationSourceType())) {
            sourceIsTreeType = true;
         }
         if (treeManager.equals(event.getRelationDestinationType())) {
            destinationIsTreeType = true;
         }
      }
      return sourceIsTreeType && destinationIsTreeType;
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
                      for (PageTree tree : trees.values()) {
                          if (tree.containsPageTreeNode(key)) {
                              trees.remove(tree.getRoot().getPathStr().toLowerCase());
                              trees.put(newFragment.toLowerCase(), tree);
                              tree.replace(key, newFragment);
                          }
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
