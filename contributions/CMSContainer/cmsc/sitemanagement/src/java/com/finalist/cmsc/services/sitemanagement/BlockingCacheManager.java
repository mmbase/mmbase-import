package com.finalist.cmsc.services.sitemanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BlockingCacheManager {
   private static final Log LOG = LogFactory.getLog(BlockingCacheManager.class.getName());

   /**
    * A custom cache manager, in case the user does not
    * want to use the default cache manager when
    * creating custom caches.
    */
   private static CacheManager manager;

   /**
    * A map of BlockingCaches, keyed by cache name
    */
   protected final Map caches;


   /**
    * Empty Constructor
    */
   public BlockingCacheManager() {
       caches = new HashMap();
   }

   /**
    * Constructor that assigns the cache manager to use when
    * creating caches.
    */
   public BlockingCacheManager(CacheManager mgr) {
       manager = mgr;
       caches = new HashMap();
   }

   /**
    * Creates a cache.
    */
   public BlockingCache getCache(final String name) throws CacheException {
       // Lookup the cache
       BlockingCache blockingCache = (BlockingCache) caches.get(name);
       if (blockingCache != null) {
           return blockingCache;
       }

       // Create the cache
       synchronized (this) {
           if (manager == null) {
               blockingCache = new BlockingCache(new CacheManager().getCache(name));
           } else {
               blockingCache = new BlockingCache(manager.getCache(name));
           }

           caches.put(name, blockingCache);
           return blockingCache;
       }
   }

   /**
    * Drops the contents of all caches.
    */
   public void clearAll() throws CacheException {
       final List cacheList = getCaches();
       if (LOG.isDebugEnabled()) {
           LOG.debug("Removing all blocking caches");
       }
       for (int i = 0; i < cacheList.size(); i++) {
           final BlockingCache cache = (BlockingCache) cacheList.get(i);
           cache.removeAll();
       }
   }

   /**
    * Drops the contents of a named cache.
    */
   public void clear(final String name) throws CacheException {
       final BlockingCache blockingCache = (BlockingCache) getCache(name);
       if (LOG.isDebugEnabled()) {
           LOG.debug("Clearing " + name);
       }
       blockingCache.removeAll();
   }

   /**
    * Returns the EHCache Cache Manager used in creating Blocking Caches.
    *
    * @return The cache manager
    */
   protected CacheManager getCacheManager() {
       return manager;
   }

   /**
    * Sets the EHCache Cache Manager used in creating blocking caches.
    *
    * @param mgr The new manager to use
    */
   protected void setCacheManager(CacheManager mgr) {
       manager = mgr;
   }

   /**
    * Builds the set of caches.
    * Returns a copy so that the monitor can be released.
    */
   private synchronized List getCaches() {
       final ArrayList blockingCaches = new ArrayList();
       blockingCaches.addAll(this.caches.values());
       return blockingCaches;
   }
}
