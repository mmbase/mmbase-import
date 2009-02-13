package com.finalist.cmsc.publish;

import java.util.*;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.cache.CachePolicy;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.remotepublishing.PublishManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.util.ServerUtil;

/**
 * Cleans (deletes) nodes in mmbase when they are expired
 */
public class CleanerModule extends Module implements Runnable {
   /**
    * MMBase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(CleanerModule.class.getName());

   /**
    * The mmbase.
    */
   private MMBase mmb = null;

   private int interval = 3600 * 1000;


   /**
    * @see org.mmbase.module.Module#onload()
    */
   public void onload() {
      // nothing
   }


   /**
    * @see org.mmbase.module.Module#init()
    */
   public void init() {
      mmb = MMBase.getMMBase();
      
      if (ServerUtil.isReadonly()) {
         return;
      }

      // Initialize the module.
      String intervalStr = getInitParameter("interval");
      if (intervalStr == null) {
         throw new IllegalArgumentException("interval");
      }
      else {
         interval = Integer.parseInt(intervalStr) * 1000;
      }

      // Start thread to wait for mmbase to be up and running.
      Thread cleaner = new Thread(this);
      cleaner.setDaemon(true);
      cleaner.start();
   }


   /**
    * Wait for mmbase to be up and running, then execute the tests.
    */
   public void run() {
      // Wait for mmbase to be up & running.
      while (!mmb.getState()) {
         try {
            Thread.sleep(1000);
         }
         catch (InterruptedException e) {
            // nothing
         }
      }

      UnPublishNodeEventListener nodeEventListener = new UnPublishNodeEventListener();
      MMBase.getMMBase().addNodeRelatedEventsListener("object", nodeEventListener);

      while (true) {
         try {
            Thread.sleep(interval);
         }
         catch (InterruptedException e) {
            // nothing
         }

         cleanNodes();
      }
   }


   private void cleanNodes() {
      try {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
         cleanContentElements(cloud);
         cleanAssetElements(cloud);
         cleanPages(cloud);
      }
      catch (Throwable t) {
         log.error("Clean failed " + t.getMessage());
         log.debug(Logging.stackTrace(t));
      }
   }


   private void cleanContentElements(Cloud cloud) {
      NodeQuery elementsQuery = ContentElementUtil.getNodeManager(cloud).createQuery();
      ContentElementUtil.addLifeCycleInverseConstraint(elementsQuery, System.currentTimeMillis());

      elementsQuery.setCachePolicy(CachePolicy.NEVER);

      NodeList queryNodes = ContentElementUtil.getNodeManager(cloud).getList(elementsQuery);
      if (!queryNodes.isEmpty()) {
         NodeIterator ni = queryNodes.nodeIterator();
         while (ni.hasNext()) {
            Node element = ni.nextNode();
            log.info("Deleting expired node (" + element.getNodeManager().getName() + ") " + element.getNumber());
            try {
               List<Node> nodes = new ArrayList<Node>();
               ContentElementUtil.findContentBlockNodes(element, nodes, true, true);

               for (Node deleteNode : nodes) {
                  PublishManager.unLinkNode(deleteNode);
                  deleteNode.delete(false);
               }
            }
            catch (Exception e) {
               // for some reason cleaning will fail because of invalid
               // remotesnodes data
               // just log the stacktrace and don't interrupt cleaning of other
               // contentelements
               log.error(Logging.stackTrace(e));
            }
         }
      }
   }


   private void cleanAssetElements(Cloud cloud) {
      NodeQuery elementsQuery = AssetElementUtil.getNodeManager(cloud).createQuery();
      AssetElementUtil.addLifeCycleInverseConstraint(elementsQuery, System.currentTimeMillis());

      elementsQuery.setCachePolicy(CachePolicy.NEVER);

      NodeList queryNodes = AssetElementUtil.getNodeManager(cloud).getList(elementsQuery);
      if (!queryNodes.isEmpty()) {
         NodeIterator ni = queryNodes.nodeIterator();
         while (ni.hasNext()) {
            Node element = ni.nextNode();
            log.info("Deleting expired node (" + element.getNodeManager().getName() + ") " + element.getNumber());
            try {
               PublishManager.unLinkNode(element);
               element.delete(false);
            }
            catch (Exception e) {
               // for some reason cleaning will fail because of invalid
               // remotesnodes data
               // just log the stacktrace and don't interrupt cleaning of other
               // assetelements
               log.error(Logging.stackTrace(e));
            }
         }
      }
   }


   private void cleanPages(Cloud cloud) {
      NodeQuery elementsQuery = PagesUtil.getNodeManager(cloud).createQuery();
      PagesUtil.addLifeCycleInverseConstraint(elementsQuery, System.currentTimeMillis());

      elementsQuery.setCachePolicy(CachePolicy.NEVER);

      NodeList queryNodes = PagesUtil.getNodeManager(cloud).getList(elementsQuery);
      if (!queryNodes.isEmpty()) {
         NodeIterator ni = queryNodes.nodeIterator();
         while (ni.hasNext()) {
            Node element = ni.nextNode();
            log.info("Deleting expired node (" + element.getNodeManager().getName() + "} " + element.getNumber());
            try {
               deletePage(element);
            }
            catch (Exception e) {
               // for some reason cleaning will fail because of invalid
               // remotesnodes data
               // just log the stacktrace and don't interrupt cleaning of other
               // contentelements
               log.error(Logging.stackTrace(e));
            }
         }
      }
   }


   private void deletePage(Node element) {
      NodeList pages = NavigationUtil.getChildren(element);
      for (Iterator<Node> iter = pages.iterator(); iter.hasNext();) {
         Node childPage = iter.next();
         deletePage(childPage);
      }

      // Deletion of childnodes is done by UnPublishNodeEventListener
      // The listener will delete all nodes which are also found by
      // PagesUtil.findPageNodes(element, nodes, true, true);
      // UnPublishNodeEventListener is triggered by relation deletions

      RelationIterator relations = element.getRelations().relationIterator();
      while (relations.hasNext()) {
         Relation rel = (Relation) relations.next();
         PublishManager.unLinkNode(rel);
         rel.delete(false);
      }
      PublishManager.unLinkNode(element);
      element.delete(false);
   }
}