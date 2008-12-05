/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MalformedObjectNameException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.module.Module;

import com.finalist.cmsc.module.luceusmodule.luceus.Indexer;
import com.finalist.cmsc.repository.ContentElementUtil;

/**
 * @author Freek Punt
 * @author Wouter Heijke
 */
public class LuceusModule extends Module {
   static Log log = LogFactory.getLog(LuceusModule.class);

   private Indexer indexer;

   private String serverUrl;

   private String repositoryName = "cmsc";

   private boolean doSecondaryWithPrimary = true;

   private boolean doSecondaryAsPrimary = true;

   private LinkedBlockingQueue<QueuedUpdate> in;

   private boolean doAttachments = true;

   private boolean doImages = false;

   private boolean doUrls = false;

   private boolean doListeners = true;

   private int execs = 2;

   private int updateStart = 60;

   private int updateInterval = 120;

   private int updateQueueSize = 100;

   private List<String> excludeTypes = new ArrayList<String>();

   private CustomContentHandler customContentHandler;

   private Set<String> secondaryCache = Collections.synchronizedSet(new HashSet<String>());


   public void init() {
      loadInitParameters("com/luceus");

      String userRepositoryName = getInitParameter("repository-name");
      if (userRepositoryName != null) {
         repositoryName = userRepositoryName;
      }
      else {
         log.info("repository-name property not set, using defaults");
      }

      String userIndexerUrl = getInitParameter("server-url");
      if (userIndexerUrl != null) {
         serverUrl = userIndexerUrl;
      }
      else {
         log.info("server-url property not set, using defaults");
      }

      String userSecondaryWithPrimary = getInitParameter("secondary-with-primary");
      if (userSecondaryWithPrimary != null) {
         if (userSecondaryWithPrimary.equalsIgnoreCase("true")) {
            doSecondaryWithPrimary = true;
         }
         else {
            doSecondaryWithPrimary = false;
         }
      }
      else {
         log.info("secondary-with-primary property not set, using defaults");
      }

      String userSecondaryAsPrimary = getInitParameter("secondary-as-primary");
      if (userSecondaryAsPrimary != null) {
         if (userSecondaryAsPrimary.equalsIgnoreCase("true")) {
            doSecondaryAsPrimary = true;
         }
         else {
            doSecondaryAsPrimary = false;
         }
      }
      else {
         log.info("secondary-as-primary property not set, using defaults");
      }

      String userDoAttachments = getInitParameter("index-attachments");
      if (userDoAttachments != null) {
         if (userDoAttachments.equalsIgnoreCase("true")) {
            doAttachments = true;
         }
         else {
            doAttachments = false;
         }
      }
      else {
         log.info("index-attachments property not set, using defaults");
      }

      String userDoImages = getInitParameter("index-images");
      if (userDoImages != null) {
         if (userDoImages.equalsIgnoreCase("true")) {
            doImages = true;
         }
         else {
            doImages = false;
         }
      }
      else {
         log.info("index-images property not set, using defaults");
      }

      String userDoUrls = getInitParameter("index-urls");
      if (userDoUrls != null) {
         if (userDoUrls.equalsIgnoreCase("true")) {
            doUrls = true;
         }
         else {
            doUrls = false;
         }
      }
      else {
         log.info("index-urls property not set, using defaults");
      }

      String userExecs = getInitParameter("update-processes");
      if (userExecs != null) {
         execs = Integer.parseInt(userExecs);
      }
      else {
         log.info("update-processes property not set, using defaults");
      }

      String userUpdateStart = getInitParameter("update-start");
      if (userUpdateStart != null) {
         updateStart = Integer.parseInt(userUpdateStart);
      }
      else {
         log.info("update-start property not set, using defaults");
      }

      String userUpdateInterval = getInitParameter("update-interval");
      if (userUpdateInterval != null) {
         updateInterval = Integer.parseInt(userUpdateInterval);
      }
      else {
         log.info("update-interval property not set, using defaults");
      }

      String userUpdateQueueSize = getInitParameter("update-queue-size");
      if (userUpdateQueueSize != null) {
         updateQueueSize = Integer.parseInt(userUpdateQueueSize);
      }
      else {
         log.info("update-queue-size property not set, using defaults");
      }

      String userDoListeners = getInitParameter("disable-listeners");
      if (userDoListeners != null) {
         if (userDoListeners.equalsIgnoreCase("true")) {
            doListeners = false;
         }
         else {
            doListeners = true;
         }
      }

      String userExcludeTypes = getInitParameter("exclude-types");
      if (userExcludeTypes != null) {
         StringTokenizer tokenizer = new StringTokenizer(userExcludeTypes, ", \t\n\r\f");
         while (tokenizer.hasMoreTokens()) {
            String type = tokenizer.nextToken();
            excludeTypes.add(type);
         }
      }

      // read customhandlerclass and create instance
      String customContentHandlerClassname = getInitParameter("custom-content-handler-classname");
      if (customContentHandlerClassname != null) {
         try {
            customContentHandler = (CustomContentHandler) Class.forName(customContentHandlerClassname).newInstance();
         }
         catch (Exception e) {
            log.warn("Unable to create CustomContentHandler! (" + e.getMessage() + ")");
         }
      }

      in = new LinkedBlockingQueue<QueuedUpdate>(updateQueueSize);

      if (doListeners) {
         new PageEventListener(this);
         new ContentElementEventListener(this);
         new NodeParameterEventListener(this);
         new SecondaryContentEventListener(this);
         if (customContentHandler != null) {
            customContentHandler.registerListeners(this);
         }
      }

      ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(execs);
      for (int i = 0; i < execs; i++) {
         exec.scheduleAtFixedRate(new IndexUpdateTask(this, in, i + 1), updateStart, updateInterval, TimeUnit.SECONDS);
      }
   }


   protected synchronized Indexer getIndexer() {
      if (indexer == null) {
         try {
            indexer = new Indexer(repositoryName, serverUrl);
         }
         catch (MalformedObjectNameException e) {
            log.error("Luceus indexer url is malformed");
         }
         catch (NullPointerException e) {
            log.error("Luceus indexer url is empty");
         }
         catch (IOException e) {
            log.error("Unable to connect to Luceus indexer");
         }
      }
      return indexer;
   }


   private void addToQueue(QueuedUpdate newUpdate) {
      log.debug("now queued:" + in.size());
      try {
         if (!in.contains(newUpdate)) {
            in.put(newUpdate);
         }
         else {
            log.debug("item '" + newUpdate.getNodeNumber() + "' exists in queue");
         }
      }
      catch (InterruptedException e) {
         log.warn("Unable to add item to queue!");
      }
   }


   public void eraseIndex() {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_ERASE_INDEX));
   }
   

   public void deleteChannelContentIndex(int channel, int contentelement) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_DELETE_CHANNELCONTENT_INDEX, channel, contentelement));
   }


   public void deletePageContentIndex(int page, int contentelement) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_DELETE_PAGECONTENT_INDEX, page, contentelement));
   }


   public void deleteContentIndex(int nodeNumber) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_DELETE_CONTENT_INDEX, nodeNumber));
   }


   public void deletePageIndex(int nodeNumber) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_DELETE_PAGE_INDEX, nodeNumber));
   }


   // aka fullindex
   public void createContentIndex(Node node) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_CREATE_CONTENT_INDEX, node.getNumber()));
   }


   public void updateContentIndex(Node node) {
      updateContentIndex(node.getNumber());
   }


   public void updateContentIndex(int nodeNumber) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_UPDATE_CONTENT_INDEX, nodeNumber));
   }


   public void updateContentChannelIndex(int nodeNumber) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_UPDATE_CONTENT_CHANNEL_INDEX, nodeNumber));
   }


   public void updatePageIndex(int nodeNumber) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_UPDATE_PAGE_INDEX, nodeNumber));
   }


   public void updateSecondaryContentIndex(int nodeNumber) {
      addToQueue(new QueuedUpdate(QueuedUpdate.METHOD_UPDATE_SECONDARYCONTENT_INDEX, nodeNumber));
   }


   public Cloud getAnonymousCloud() {
      return CloudProviderFactory.getCloudProvider().getAnonymousCloud();
   }


   public boolean excludeType(String name) {
      return excludeTypes.contains(name);
   }

   private class FullIndexTimerTask extends TimerTask {

      private boolean erase = false;
      
      private String nodemanager = null;

      public FullIndexTimerTask(boolean erase, String nodemanager) {
         this.erase = erase;
         this.nodemanager = nodemanager;
      }


      public void run() {
         log.info("===>fullIndex starting<==");
         Cloud cloud = getAnonymousCloud();

         NodeManager nm = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
         
         //Optionally use a specific Nodemanager to (re)index
         if (nodemanager != null) {
            log.info("===>only doing a fullIndex on nodemanager " + nodemanager + "<==");
            try {
               nm = cloud.getNodeManager(nodemanager);
            } catch (NotFoundException e) {
               log.error("===>Help, nodemanager '" + nodemanager + "' could not be found!<===");
               log.info("===>fullIndex aborted<==");
               return;
            }
         }
         
         if (erase) {
            log.info("===>erasing index<==");
            eraseIndex();
         }
         
         NodeQuery q = nm.createQuery();

         // use this iterator because we can have many data to process
         HugeNodeListIterator iterator = new HugeNodeListIterator(q);
         while (iterator.hasNext()) {
            Node currentNode = iterator.nextNode();
            if (erase) {
               createContentIndex(currentNode);
            }
            else {
               updateContentIndex(currentNode);
            }
         }

         log.info("===>fullIndex done<==");
      }
   }

   private class EraseIndexTimerTask extends TimerTask {
      public void run() {
         log.info("===>eraseIndex starting<==");
         eraseIndex();
         log.info("===>eraseIndex done<==");
      }
   }


   public void startFullIndex(boolean erase, String nodemanager) {
      Thread runOnce = new Thread(new FullIndexTimerTask(erase, nodemanager));
      runOnce.setDaemon(true);
      runOnce.start();
   }


   public void startEraseIndex() {
      Thread runOnce = new Thread(new EraseIndexTimerTask());
      runOnce.setDaemon(true);
      runOnce.start();
   }


   public String getRepositoryName() {
      return repositoryName;
   }


   public String getServerUrl() {
      return serverUrl;
   }


   public boolean isDoAttachments() {
      return doAttachments;
   }


   public boolean isDoImages() {
      return doImages;
   }


   public boolean isDoSecondaryAsPrimary() {
      return doSecondaryAsPrimary;
   }


   public boolean isDoSecondaryWithPrimary() {
      return doSecondaryWithPrimary;
   }


   public boolean isDoUrls() {
      return doUrls;
   }


   public CustomContentHandler getCustomContentHandler() {
      return customContentHandler;
   }


   public boolean hasProcessedSecondary(String scId) {
      return secondaryCache.contains(scId);
   }


   public void processSecondary(String scId) {
      secondaryCache.add(scId);
   }


   public void clearProcessedSecondary() {
      secondaryCache.clear();
   }

}
