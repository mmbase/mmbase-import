/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.io.Serializable;

import org.mmbase.bridge.*;
import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public abstract class MMBaseCacheEntryFactory implements CacheEntryFactory, NodeEventListener, RelationEventListener {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(MMBaseCacheEntryFactory.class.getName());

   private CloudProvider cloudProvider;
   private SelfPopulatingCache cache;
   private String nodeType;


   public MMBaseCacheEntryFactory(String nodeType) {
      this.cloudProvider = CloudProviderFactory.getCloudProvider();
      this.nodeType = nodeType;

      registerListener(nodeType);
   }


   protected abstract Serializable loadEntry(Serializable key) throws Exception;


   protected void registerListener(String nodeType) {
      MMBase.getMMBase().addNodeRelatedEventsListener(nodeType, this);
   }


   public Serializable createEntry(Serializable key) throws Exception {
      return loadEntry(key);
   }


   protected Node getNode(Serializable key) {
      if (key == null) {
         return null;
      }
      try {
         if (key instanceof Integer) {
            Integer keyInt = (Integer) key;
            return getCloud().getNode(keyInt.intValue());
         }
         if (key instanceof String) {
            return getCloud().getNode((String) key);
         }
         return getCloud().getNode(key.toString());
      }
      catch (NotFoundException nfe) {
         return null;
      }
   }


   protected Cloud getAdminCloud() {
      Cloud cloud = cloudProvider.getAdminCloud();
      return cloud;
   }


   protected Cloud getCloud() {
      Cloud cloud = cloudProvider.getAnonymousCloud();
      return cloud;
   }


   public void cacheToRefresh(SelfPopulatingCache cache) {
      this.cache = cache;
   }


   /**
    * Refreshes a single entry in a SelfPopulatingCache. The old entry is
    * discarded and then requested, causing it to be populated. Note: Used by
    * tests only, do not use in production.
    * 
    * @param key
    *           cache key entry to refresh
    */
   public void refreshEntry(final Serializable key) {
      try {
         if (cache.getKeys().contains(key)) {
            cache.put(key, null);
            cache.get(key);
         }
      }
      catch (Exception e) {
         log.debug("Failed to refresh " + key + ":" + e.getMessage(), e);
      }
   }


   public void deleteEntry(final Serializable key) {
      try {
         cache.put(key, null);
      }
      catch (Exception e) {
         log.debug("Failed to delete " + key + ":" + e.getMessage(), e);
      }
   }


   public void notify(NodeEvent event) {
      if (isNodeEvent(event)) {
         Integer key = getKey(event);
         if (key != null) {
            switch (event.getType()) {
               case Event.TYPE_CHANGE:
                  refreshEntry(key);
                  break;
               case Event.TYPE_DELETE:
                  deleteEntry(key);
                  break;
               case Event.TYPE_NEW:
                  break;
               case NodeEvent.TYPE_RELATION_CHANGE:
                  break;
               default:
                  break;
            }
         }
      }
   }


   public void notify(RelationEvent event) {
      if (isRelationEvent(event)) {
         Integer key = getKey(event);
         if (key != null) {
            switch (event.getType()) {
               case Event.TYPE_CHANGE:
                  refreshEntry(key);
                  break;
               case Event.TYPE_DELETE:
                  refreshEntry(key);
                  break;
               case Event.TYPE_NEW:
                  refreshEntry(key);
                  break;
               case NodeEvent.TYPE_RELATION_CHANGE:
                  refreshEntry(key);
                  break;
               default:
                  break;
            }
         }
      }
   }


   protected Integer getKey(NodeEvent event) {
      return new Integer(event.getNodeNumber());
   }


   protected Integer getKey(RelationEvent event) {
      return new Integer(event.getRelationSourceNumber());
   }


   protected boolean isNodeEvent(NodeEvent event) {
      return isNodeEvent(event, nodeType);
   }


   protected boolean isRelationEvent(RelationEvent event) {
      return isRelationEvent(event, nodeType);
   }


   protected boolean isNodeEvent(NodeEvent event, String nodeType) {
      return event.getBuilderName().equals(nodeType);
   }


   protected boolean isRelationEvent(RelationEvent event, String nodeType) {
      return event.getRelationSourceType().equals(nodeType) && !"object".equals(event.getRelationDestinationType())
            && !event.getRelationDestinationType().equals(nodeType);
   }

}
