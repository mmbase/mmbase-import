package com.finalist.newsletter.publisher.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author nikko yin
 *         implement the interface ICache
 */
public class DefaultCache implements ICache {

   private static final int FreshTimerIntervalSeconds = 1;
   private Map<String, CacheInfo> datas;
   private long time = 1800;
   private Timer timer;

   /**
    * synchronized cache when instance it
    * flush when every second
    */
   public DefaultCache() {
      datas = Collections.synchronizedMap(new HashMap<String, CacheInfo>());
      TimerTask task = new CacheFreshTask(this);
      timer = new Timer("Cache_Timer", true);
      timer.scheduleAtFixedRate(task, 1000, FreshTimerIntervalSeconds * 1000);
   }

   public DefaultCache(long time) {
      this();
      this.time = time;
   }

   /**
    * @param key   used to find value
    * @param value store the infoBean
    */
   public void add(Object key, Object value) {
      add(key, value, time);
   }


   /**
    * @param key               used to find value
    * @param value             store the infoBean
    * @param slidingExpiration the life of infoBean
    */
   public void add(Object key, Object value, long slidingExpiration) {
      if (slidingExpiration != 0) {
         CacheInfo ci = new CacheInfo(value, slidingExpiration);
         datas.put((String) key, ci);
      }
   }

   /**
    * @param key used mark the infoBean
    * @return boolean see it contains
    */
   public boolean contains(Object key) {
      if (datas.containsKey(key)) {
         return true;
      }
      return false;
   }


   /**
    * @param key used get the value
    * @return Object is the infoBean
    */
   public Object get(Object key) {
      if (datas.containsKey(key)) {
         CacheInfo ci = datas.get(key);
         // cahce'life will refresh when it's invoke ;)
         ci.setSecondsRemain(ci.getTotalSeconds());
         return ci.getObj();
      }
      return null;
   }

   /**
    * @param key remove
    */
   public void remove(Object key) {
      datas.remove(key);
   }


   /**
    * remove all exited infoBean in the cache
    */
   public void removeAll() {
   }

   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public Map<String, CacheInfo> getDatas() {
      return datas;
   }
}
