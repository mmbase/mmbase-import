/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.sitemanagement;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.*;

import junit.framework.TestCase;

import org.mmbase.bridge.*;
import org.mmbase.core.event.*;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;


public class SiteCacheTest extends TestCase {

   private static final int DEMO_SITE = 1;
   private static final int NEWS_PAGE = 2;
   private static final int VISION_PAGE = 3;
   private static final int SITEMAP_PAGE = 4;
   private static final int NEWS_LATEST_PAGE = 5;
   private static final int NEWS_ARCHIVE_PAGE = 6;

   private static final int NAVREL_ROLE = -2;

   Map<String, PageTree> trees;
   SiteCache siteCache;
   Cloud mockCloud;
   NavigationItemManager mockSiteManager;
   NavigationItemManager mockPageManager;

   @Override
   protected void setUp() throws Exception {
      mockCloud = createMock(Cloud.class);
      mockSiteManager = createMock(NavigationItemManager.class);
      mockPageManager = createMock(NavigationItemManager.class);

      trees = createPageTrees();

      SiteCacheLoader loader = new SiteCacheLoader() {

         @Override
         public Cloud getCloud() {
            return mockCloud;
         }

         @Override
         public int getNavrelRelationNumber() {
            return NAVREL_ROLE;
         }

         @Override
         public Map<String, PageTree> loadPageTreeMap() {
            return trees;
         }
      };
      siteCache = new SiteCache(loader);
   }

   protected Map<String, PageTree> createPageTrees() {
      Map<String, PageTree> newtrees = new HashMap<String, PageTree>();

      PageTree siteTree = new PageTree(1, "demo");
      siteTree.insert(DEMO_SITE, NEWS_PAGE, "news", 1);
      siteTree.insert(DEMO_SITE, VISION_PAGE, "vision", 2);

      siteTree.insert(NEWS_PAGE, NEWS_LATEST_PAGE, "latest", 1);
      siteTree.insert(NEWS_PAGE, NEWS_ARCHIVE_PAGE, "archive", 2);

      newtrees.put("demo", siteTree);
      return newtrees;
   }

   @Override
   protected void tearDown() throws Exception {
      NavigationManager.getNavigationManagers().clear();
   }

   public void testGetPage() {
      expectRegisterNavigationItems();
      replayGlobals();
      registerMockNavigationManagers();

      Integer pageId = siteCache.getPage("demo/news");
      assertEquals(Integer.valueOf(NEWS_PAGE), pageId);
   }

   public void testNewRelation() {
      Node mockNode = createMock(Node.class);
      NodeManager mockNodeManager = createMock(NodeManager.class);

      expectRegisterNavigationItems();
      expectIsChangeTreeEvent();
      expect(mockCloud.getNode(SITEMAP_PAGE)).andReturn(mockNode);
      expect(mockNode.getCloud()).andReturn(mockCloud);
      expect(mockCloud.getNodeManager("object")).andReturn(mockNodeManager);
      expect(mockNode.countRelatedNodes(mockNodeManager, "navrel", "DESTINATION")).andReturn(0);

      expect(mockNode.getStringValue(TreeUtil.PATH_FIELD)).andReturn("demo/sitemap");

      replayGlobals();
      replay(mockNode);
      replay(mockNodeManager);

      registerMockNavigationManagers();

      Integer pageId = siteCache.getPage("demo/sitemap");
      assertEquals(null, pageId);

      RelationEvent newChildEvent = createNewRelation(DEMO_SITE, SITEMAP_PAGE, 2);
      siteCache.notify(newChildEvent);

      Integer newPageId = siteCache.getPage("demo/sitemap");
      assertEquals(Integer.valueOf(SITEMAP_PAGE), newPageId);
   }

   public void testDeleteRelation() {
      Node mockNode = createMock(Node.class);

      expectRegisterNavigationItems();
      expectIsChangeTreeEvent();
      expect(mockCloud.getNode(DEMO_SITE)).andReturn(mockNode);
      expect(mockNode.getStringValue(TreeUtil.PATH_FIELD)).andReturn("demo");

      replayGlobals();
      replay(mockNode);

      Integer pageId = siteCache.getPage("demo/news");
      assertEquals(Integer.valueOf(NEWS_PAGE), pageId);

      registerMockNavigationManagers();

      RelationEvent deleteChildEvent = createDeleteRelation(DEMO_SITE, NEWS_PAGE);
      siteCache.notify(deleteChildEvent);

      Integer deletePageId = siteCache.getPage("demo/sitemap");
      assertEquals(null, deletePageId);
   }

   public void testReorderRelation() {
      expectRegisterNavigationItems();
      expectIsChangeTreeEvent();

      replayGlobals();

      registerMockNavigationManagers();

      NavigationItem siteItem = new NavigationItem();
      siteItem.setId(DEMO_SITE);

      List<Integer> children = siteCache.getChildren(siteItem);
      assertEquals(Integer.valueOf(NEWS_PAGE), children.get(0));
      assertEquals(Integer.valueOf(VISION_PAGE), children.get(1));

      RelationEvent newChildEvent = createChangeRelation(DEMO_SITE, NEWS_PAGE, 3);
      siteCache.notify(newChildEvent);

      List<Integer> newChildren = siteCache.getChildren(siteItem);
      assertEquals(Integer.valueOf(VISION_PAGE), newChildren.get(0));
      assertEquals(Integer.valueOf(NEWS_PAGE), newChildren.get(1));
   }

   public void testMoveRelation() {
      Node mockNode = createMock(Node.class);

      expectRegisterNavigationItems();
      expectIsChangeTreeEvent();
      expectIsChangeTreeEvent();

      expect(mockCloud.getNode(DEMO_SITE)).andReturn(mockNode);
      expect(mockNode.getStringValue(TreeUtil.PATH_FIELD)).andReturn("demo");

      replayGlobals();
      replay(mockNode);

      registerMockNavigationManagers();

      Integer pageId = siteCache.getPage("demo/news");
      assertEquals(Integer.valueOf(NEWS_PAGE), pageId);
      Integer latestPageId = siteCache.getPage("demo/news/latest");
      assertEquals(Integer.valueOf(NEWS_LATEST_PAGE), latestPageId);

      RelationEvent newChildEvent = createNewRelation(VISION_PAGE, NEWS_PAGE, 1);
      siteCache.notify(newChildEvent);
      RelationEvent deleteChildEvent = createDeleteRelation(DEMO_SITE, NEWS_PAGE);
      siteCache.notify(deleteChildEvent);

      Integer newPageId = siteCache.getPage("demo/vision/news");
      assertEquals(Integer.valueOf(NEWS_PAGE), newPageId);
      Integer newLatestPageId = siteCache.getPage("demo/vision/news/latest");
      assertEquals(Integer.valueOf(NEWS_LATEST_PAGE), newLatestPageId);
   }

   public void testMoveRelationInvalidOrder() {
      Node mockSiteNode = createMock(Node.class);
      Node mockNewsNode = createMock(Node.class);
      NodeManager mockNodeManager = createMock(NodeManager.class);

      expectRegisterNavigationItems();
      expectIsChangeTreeEvent();
      expectIsChangeTreeEvent();

      expect(mockCloud.getNode(DEMO_SITE)).andReturn(mockSiteNode);
      expect(mockSiteNode.getStringValue(TreeUtil.PATH_FIELD)).andReturn("demo");

      expect(mockCloud.getNode(NEWS_PAGE)).andReturn(mockNewsNode);
      expect(mockNewsNode.getStringValue(TreeUtil.PATH_FIELD)).andReturn("demo/vision/news");

      expect(mockNewsNode.getCloud()).andReturn(mockCloud);
      expect(mockCloud.getNodeManager("object")).andReturn(mockNodeManager);
      expect(mockNewsNode.countRelatedNodes(mockNodeManager, "navrel", "DESTINATION")).andReturn(2);


      replayGlobals();
      replay(mockSiteNode);
      replay(mockNewsNode);

      registerMockNavigationManagers();
      trees = createPageTreesWithMovedNews();

      Integer pageId = siteCache.getPage("demo/news");
      assertEquals(Integer.valueOf(NEWS_PAGE), pageId);
      Integer latestPageId = siteCache.getPage("demo/news/latest");
      assertEquals(Integer.valueOf(NEWS_LATEST_PAGE), latestPageId);

      RelationEvent deleteChildEvent = createDeleteRelation(DEMO_SITE, NEWS_PAGE);
      siteCache.notify(deleteChildEvent);
      RelationEvent newChildEvent = createNewRelation(VISION_PAGE, NEWS_PAGE, 1);
      siteCache.notify(newChildEvent);

      Integer newPageId = siteCache.getPage("demo/vision/news");
      assertEquals(Integer.valueOf(NEWS_PAGE), newPageId);
      Integer newLatestPageId = siteCache.getPage("demo/vision/news/latest");
      assertEquals(Integer.valueOf(NEWS_LATEST_PAGE), newLatestPageId);
   }

   protected Map<String, PageTree> createPageTreesWithMovedNews() {
      Map<String, PageTree> newtrees = new HashMap<String, PageTree>();

      PageTree siteTree = new PageTree(1, "demo");
      siteTree.insert(DEMO_SITE, VISION_PAGE, "vision", 2);
      siteTree.insert(VISION_PAGE, NEWS_PAGE, "news", 1);

      siteTree.insert(NEWS_PAGE, NEWS_LATEST_PAGE, "latest", 1);
      siteTree.insert(NEWS_PAGE, NEWS_ARCHIVE_PAGE, "archive", 2);

      newtrees.put("demo", siteTree);
      return newtrees;
   }


   private void replayGlobals() {
      replay(mockCloud);
      replay(mockSiteManager);
      replay(mockPageManager);
   }

   private void registerMockNavigationManagers() {
      NavigationManager.registerNavigationManager(mockSiteManager);
      NavigationManager.registerNavigationManager(mockPageManager);
   }

   private void expectRegisterNavigationItems() {
      expect(mockPageManager.isRoot()).andReturn(false);
      expect(mockSiteManager.isRoot()).andReturn(true);
   }

   private void expectIsChangeTreeEvent() {
      expect(mockPageManager.getTreeManager()).andReturn("page");
      expect(mockSiteManager.getTreeManager()).andReturn("site");

      expect(mockPageManager.getTreeManager()).andReturn("page");
      expect(mockSiteManager.getTreeManager()).andReturn("site");
   }

   private RelationEvent createChangeRelation(int sourceNr, int destNr, int childIndex) {
      Map<String,Object> newValues = new HashMap<String,Object>();
      newValues.put(TreeUtil.RELATION_POS_FIELD, childIndex);

      NodeEvent newChildNodeEvent = new NodeEvent("localhost", "navrel", 100, null, newValues , Event.TYPE_CHANGE);
      RelationEvent newChildEvent = new RelationEvent(newChildNodeEvent, sourceNr, destNr, "site", "page", NAVREL_ROLE);
      return newChildEvent;
   }


   private RelationEvent createNewRelation(int sourceNr, int destNr, int childIndex) {
      Map<String,Object> newValues = new HashMap<String,Object>();
      newValues.put(TreeUtil.RELATION_POS_FIELD, childIndex);

      NodeEvent newChildNodeEvent = new NodeEvent("localhost", "navrel", 100, null, newValues , Event.TYPE_NEW);
      RelationEvent newChildEvent = new RelationEvent(newChildNodeEvent, sourceNr, destNr, "site", "page", NAVREL_ROLE);
      return newChildEvent;
   }

   private RelationEvent createDeleteRelation(int sourceNr, int destNr) {
      NodeEvent newChildNodeEvent = new NodeEvent("localhost", "navrel", 100, null, null , Event.TYPE_DELETE);
      RelationEvent newChildEvent = new RelationEvent(newChildNodeEvent, sourceNr, destNr, "site", "page", NAVREL_ROLE);
      return newChildEvent;
   }

}
