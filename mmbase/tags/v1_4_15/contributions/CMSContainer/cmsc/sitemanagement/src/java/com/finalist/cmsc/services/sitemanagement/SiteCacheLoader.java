/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.sitemanagement;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.cache.CachePolicy;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.storage.search.StepField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;
import com.finalist.cmsc.services.sitemanagement.tree.PageTreeNode;


public class SiteCacheLoader {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(SiteCacheLoader.class.getName());
    
    public Map<String, PageTree> loadPageTreeMap(Cloud cloud) {
        Map<String, PageTree> newtrees = new HashMap<String, PageTree>();
        
        List<NavigationItemManager> navigationManagers = NavigationManager.getNavigationManagers();
        for (NavigationItemManager nim : navigationManagers) {
            if (nim.isRoot()) {
                String nodeType = nim.getTreeManager();
                String fragmentField = NavigationUtil.getFragmentFieldname(nodeType);
                loadTrees(newtrees, cloud, nodeType, fragmentField);
            }
        }

        Map<Integer,String> itemUrlFragments = new HashMap<Integer, String>();
        
        for (NavigationItemManager nim : navigationManagers) {
            if (!nim.isRoot()) {
                String nodeType = nim.getTreeManager();
                String fragmentField = NavigationUtil.getFragmentFieldname(nodeType);
                loadNavigationItems(cloud, nodeType, fragmentField, itemUrlFragments);
            }
        }
        
        loadTreeStructure(newtrees, cloud, itemUrlFragments);
        return newtrees;
    }
    
    @SuppressWarnings("unchecked")
    private void loadTrees(Map<String, PageTree> newtrees, Cloud cloud, String nodeType, String fragmentField) {
        NodeManager sitesManager = cloud.getNodeManager(nodeType);
        NodeList sites = sitesManager.getList(sitesManager.createQuery());
        for (Iterator<Node> iter = sites.iterator(); iter.hasNext();) {
           Node siteNode = iter.next();
           int siteId = siteNode.getNumber();
           String sitefragment = siteNode.getStringValue(fragmentField);
           createTree(newtrees, siteId, sitefragment);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadNavigationItems(Cloud cloud, String nodeType, String fragmentField, Map<Integer, String> itemUrlFragments) {
        NodeManager manager = cloud.getNodeManager(nodeType);

        NodeQuery q = manager.createQuery();
        List<String> types = new ArrayList<String>();
        types.add(manager.getName());
        SearchUtil.addTypeConstraints(q, types);
        q.setCachePolicy(CachePolicy.NEVER);
        
        for (NodeIterator iter = new HugeNodeListIterator(q); iter.hasNext();) {
            Node navNode = iter.nextNode();
            int number = navNode.getNumber();
            String urlfragment = navNode.getStringValue(fragmentField);
            itemUrlFragments.put(number, urlfragment);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadTreeStructure(Map<String, PageTree> newtrees, Cloud cloud, Map<Integer,String> itemUrlFragments) {
       List<Node> unfinishedNodes = new ArrayList<Node>();

       NodeManager navrel = cloud.getNodeManager(NavigationUtil.NAVREL);

       Query q = cloud.createQuery();
       q.addStep(navrel);

       StepField sourceField = q.addField(NavigationUtil.NAVREL + ".snumber");
       StepField posField = q.addField(NavigationUtil.NAVREL + ".pos");

       q.addSortOrder(sourceField, SortOrder.ORDER_ASCENDING);
       q.addSortOrder(posField, SortOrder.ORDER_ASCENDING);
       q.setCachePolicy(CachePolicy.NEVER);

       for (NodeIterator iter = new HugeNodeListIterator(q); iter.hasNext();) {
          Node navrelNode = iter.nextNode();

          int sourceNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".snumber");
          int destNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".dnumber");
          int childIndex = navrelNode.getIntValue(NavigationUtil.NAVREL + ".pos");
          String fragment = itemUrlFragments.get(destNumber);
          
          boolean parentNotFound = true;
          for (PageTree tree : newtrees.values()) {
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
             String fragment = itemUrlFragments.get(destNumber);

             for (PageTree tree : newtrees.values()) {
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
    }


    public void createTree(Map<String, PageTree> newtrees, int siteId, String sitefragment) {
       PageTree siteTree = new PageTree(siteId, sitefragment);
       newtrees.put(sitefragment.toLowerCase(), siteTree);
    }

}
