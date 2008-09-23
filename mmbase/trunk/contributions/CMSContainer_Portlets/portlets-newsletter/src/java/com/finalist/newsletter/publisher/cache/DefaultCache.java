package com.finalist.newsletter.publisher.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author nikko yin
 * implement the interface ICache
 */
public class DefaultCache implements ICache {

   private static final int FreshTimerIntervalSeconds = 1;
   private Map < String , CacheInfo > datas;
   private long time = 1800;
   private Timer timer;

   /**
    * synchronized cache when instance it
    * flush when every second
    */
   public DefaultCache() {
      datas = Collections.synchronizedMap(new HashMap < String , CacheInfo > ());
      TimerTask task = new CacheFreshTask(this);
      timer = new Timer("Cache_Timer", true);
      timer.scheduleAtFixedRate(task, 1000, FreshTimerIntervalSeconds * 1000);
   }

   public DefaultCache(long time) {
      this();
      this.time = time;
   }

   /** add
    * @see com.finalist.newsletter.publisher.cache.ICache#add(java.lang.Object, java.lang.Object)
    */
   public void add(Object key, Object value) {
      add(key, value, time);
   }

  
   /**add
    * @see com.finalist.newsletter.publisher.cache.ICache#add(java.lang.Object, java.lang.Object, long)
    */
   public void add(Object key, Object value, long slidingExpiration) {
      if (slidingExpiration != 0) {
         CacheInfo ci = new CacheInfo(value, slidingExpiration);
         datas.put((String) key, ci);
      }
   }

   /** contains
    * @see com.finalist.newsletter.publisher.cache.ICache#contains(java.lang.Object)
    */
   public boolean contains(Object key) {
      if (datas.containsKey(key)) {
         return true;
      }
      return false;
   }

   
   /** get
    * @see com.finalist.newsletter.publisher.cache.ICache#get(java.lang.Object)
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

   /** remove
    * @see com.finalist.newsletter.publisher.cache.ICache#remove(java.lang.Object)
    */
   public void remove(Object key) {
      datas.remove(key);
   }

   
   /** removeAll
    * @see com.finalist.newsletter.publisher.cache.ICache#removeAll()
    */
   public void removeAll() {
   }

   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public Map < String , CacheInfo > getDatas() {
      return datas;
   }
}
