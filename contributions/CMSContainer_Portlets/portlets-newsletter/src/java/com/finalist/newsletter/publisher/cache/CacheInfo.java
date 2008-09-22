package com.finalist.newsletter.publisher.cache;

/**
 * @author nikko yin
 */
public class CacheInfo {

   private Object obj;
   private long secondsRemain;
   private long cacheSeconds;

   // construct CacheInfoBean
   public CacheInfo(Object obj, long cacheSeconds) {
      this.obj = obj;
      this.secondsRemain = cacheSeconds;
      this.cacheSeconds = cacheSeconds;
   }

   // getObjInfoBean
   public Object getObj() {
      return obj;
   }

   // getSecondsRemain
   public long getSecondsRemain() {
      return secondsRemain;
   }

   // getTotalSeconds
   public long getTotalSeconds() {
      return cacheSeconds;
   }

   /**
    * setSecondsRemain
    * 
    * @param null
    */
   public void setSecondsRemain(long secondsRemain) {
      this.secondsRemain = secondsRemain;
   }

}
