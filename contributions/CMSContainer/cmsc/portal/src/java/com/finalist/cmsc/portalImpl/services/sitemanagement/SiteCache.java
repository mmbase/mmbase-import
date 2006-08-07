/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl.services.sitemanagement;

import java.util.*;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.cache.CachePolicy;
import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.portalImpl.services.sitemanagement.tree.PageTree;
import com.finalist.cmsc.portalImpl.services.sitemanagement.tree.PageTreeNode;


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
        List<Node> unfinishedNodes = new ArrayList<Node>();
        
        Cloud cloud = getCloud();

        NodeList sites = SiteUtil.getSites(cloud);
        for (Iterator iter = sites.iterator(); iter.hasNext();) {
           Node siteNode = (Node) iter.next();
            
           Site site = (Site) MMBaseNodeMapper.copyNode(siteNode, Site.class);
           int siteId = site.getId();
           String sitefragment = site.getUrlfragment();
           createTree(siteId, sitefragment);
        }
        
        NodeManager navrel = cloud.getNodeManager(NavigationUtil.NAVREL);
        NodeManager page = cloud.getNodeManager(PagesUtil.PAGE);

        Query q = cloud.createQuery();
        q.addStep(navrel);
        q.addStep(page);
        StepField sourceField = q.addField(NavigationUtil.NAVREL + ".snumber");
        StepField destField = q.addField(NavigationUtil.NAVREL + ".dnumber");
        StepField posField = q.addField(NavigationUtil.NAVREL + ".pos");
        StepField pageNumberField = q.addField(PagesUtil.PAGE + ".number");
        q.addField(PagesUtil.PAGE + "." + PagesUtil.FRAGMENT_FIELD);
        q.setConstraint(q.createConstraint(destField, FieldCompareConstraint.EQUAL, pageNumberField));
        q.addSortOrder(sourceField, SortOrder.ORDER_ASCENDING);
        q.addSortOrder(posField, SortOrder.ORDER_ASCENDING);
        q.setCachePolicy(CachePolicy.NEVER);

        NodeList navrels = cloud.getList(q);
        for (Iterator iter = navrels.iterator(); iter.hasNext();) {
            Node navrelNode = (Node) iter.next();
            
            int sourceNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".snumber");
            int destNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".dnumber");
            int childIndex = navrelNode.getIntValue(NavigationUtil.NAVREL + ".pos");
            String fragment = navrelNode.getStringValue(PagesUtil.PAGE + "." + PagesUtil.FRAGMENT_FIELD);
            
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

            for (Iterator iter = unfinishedNodes.iterator(); iter.hasNext();) {
                Node navrelNode = (Node) iter.next();
    
                int sourceNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".snumber");
                int destNumber = navrelNode.getIntValue(NavigationUtil.NAVREL + ".dnumber");
                int childIndex = navrelNode.getIntValue(NavigationUtil.NAVREL + ".pos");
                String fragment = navrelNode.getStringValue(PagesUtil.PAGE + "." + PagesUtil.FRAGMENT_FIELD);
    
                for (PageTree tree : trees.values()) {
                    PageTreeNode pageTreeNode = tree.insert(sourceNumber, destNumber, fragment, childIndex);
                    if (pageTreeNode != null) {
                        iter.remove();
                        break;
                    }
                }
            }
        }
        
        for (Iterator iter = unfinishedNodes.iterator(); iter.hasNext();) {
            Node navrelNode = (Node) iter.next();
            log.warn("Page treenode not found for navrel: " + navrelNode);
        }
        
        MMBase.getMMBase().addNodeRelatedEventsListener(PagesUtil.PAGE, this);
    }

    private void createTree(int siteId, String sitefragment) {
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


    private PageTreeNode getPageTreeNode(Page findpage) {
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

    public List<Integer> getChildren(Page findpage) {
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
    
    public String getPath(Page page, boolean includeRoot) {
        PageTreeNode pageTreeNode = getPageTreeNode(page);
        if (pageTreeNode != null) {
            return pageTreeNode.getPathStr(includeRoot);
        }
        return null;
    }

    public void notify(RelationEvent event) {
        if (( PagesUtil.PAGE.equals(event.getRelationSourceType()) || SiteUtil.SITE.equals(event.getRelationSourceType()) ) 
                && PagesUtil.PAGE.equals(event.getRelationDestinationType())) {
            switch (event.getType()) {
                case Event.TYPE_CHANGE:
                {
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
                case Event.TYPE_DELETE:
                    {
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
                case Event.TYPE_NEW:
                    {
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

    public void notify(NodeEvent event) {
        Integer key = event.getNodeNumber();
        if (key != null) {
            switch (event.getType()) {
                case Event.TYPE_CHANGE:
                    if (SiteUtil.SITE.equals(event.getBuilderName())) {
                        if(event.getChangedFields().contains(SiteUtil.FRAGMENT_FIELD)) {
                            for (PageTree tree : trees.values()) {
                                if (tree.containsPageTreeNode(key)) {
                                    String newFragment = (String) event.getNewValue(SiteUtil.FRAGMENT_FIELD);
                                    trees.remove(tree.getRoot().getPathStr().toLowerCase());
                                    trees.put(newFragment.toLowerCase(), tree);
                                    tree.replace(key, newFragment);
                                    break;
                                }
                            }
                        }
                    }
                    if (PagesUtil.PAGE.equals(event.getBuilderName())) {
                        if(event.getChangedFields().contains(PagesUtil.FRAGMENT_FIELD)) {
                            for (PageTree tree : trees.values()) {
                                if (tree.containsPageTreeNode(key)) {
                                    String newFragment = (String) event.getNewValue(PagesUtil.FRAGMENT_FIELD);
                                    tree.replace(key, newFragment);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case Event.TYPE_DELETE:
                    if (SiteUtil.SITE.equals(event.getBuilderName())) {
                        for (PageTree tree : trees.values()) {
                            if (tree.containsPageTreeNode(key)) {
                                trees.remove(tree.getRoot().getPathStr().toLowerCase());
                                break;
                            }
                        }
                    }
                    break;
                case Event.TYPE_NEW:
                    if (SiteUtil.SITE.equals(event.getBuilderName())) {
                        String newFragment = (String) event.getNewValue(SiteUtil.FRAGMENT_FIELD);
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
