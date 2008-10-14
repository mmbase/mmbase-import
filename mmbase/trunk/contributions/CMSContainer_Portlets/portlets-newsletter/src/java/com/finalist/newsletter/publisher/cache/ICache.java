package com.finalist.newsletter.publisher.cache;

/**
 * @author nikko
 */
public interface ICache {
   /**
    * FOREVER is the flag mark the cache life
    */
   public static int FOREVER = -1;

   /**
    * @param key   used to find value
    * @param value store the infoBean
    */
   public void add(Object key, Object value);

   /**
    * @param key               used to find value
    * @param value             store the infoBean
    * @param slidingExpiration the life of infoBean
    */
   public void add(Object key, Object value, long slidingExpiration);

   /**
    * @param key remove
    */
   public void remove(Object key);

   /**
    * remove all exited infoBean in the cache
    */
   public void removeAll();

   /**
    * @param key used get the value
    * @return Object is the infoBean
    */
   public Object get(Object key);

   /**
    * @param key used mark the infoBean
    * @return boolean see it contains
    */
   public boolean contains(Object key);

}
