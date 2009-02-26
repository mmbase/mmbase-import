package com.finalist.newsletter.publisher.cache;

/**
 * @author nikko yin
 *         CacheInfo is infoBean has getter and setter
 */
public class CacheInfo {

   private Object obj;
   private long secondsRemain;
   private long cacheSeconds;

   public CacheInfo(Object obj, long cacheSeconds) {
      this.obj = obj;
      this.secondsRemain = cacheSeconds;
      this.cacheSeconds = cacheSeconds;
   }

   public Object getObj() {
      return obj;
   }

   public long getSecondsRemain() {
      return secondsRemain;
   }

   public long getTotalSeconds() {
      return cacheSeconds;
   }

   public void setSecondsRemain(long secondsRemain) {
      this.secondsRemain = secondsRemain;
   }

}
