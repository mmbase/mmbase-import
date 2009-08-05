package com.finalist.newsletter.publisher.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author nikko yin
 */
public class CacheFactory {

   private static ICache cache = null;
   private static final Log logger = LogFactory.getLog(CacheFactory.class);

   /**
    * @param caches this is a map
    * @return interface
    */
   public static ICache getCacheInstance(Class caches) {
      if (cache == null) {
         try {
            cache = (ICache) caches.newInstance();
         } catch (InstantiationException e) {
            logger.debug("the point cache is error,caches'paramater must be an ICache'instance");

         } catch (IllegalAccessException e) {
            logger.debug("the point cache is error,caches'paramater must be an ICache'instance");
         }
      }
      return cache;
   }

   /**
    * get time
    *
    * @param null
    * @return ICache
    */
   public static ICache getDefaultCache() {
      return getDefaultCache(1800);
   }

   /**
    * getDefaultCache
    *
    * @param time which control the life of cache
    * @return ICache
    */
   public static ICache getDefaultCache(long time) {
      if (cache == null) {
         cache = new DefaultCache(time);
      } else if (!(cache instanceof DefaultCache)) {
         cache = new DefaultCache(time);
      }
      return cache;
   }

}
