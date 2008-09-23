package com.finalist.newsletter.publisher.cache;

/**
 * @author nikko
 * 
 */
public interface ICache {
   public static int FOREVER = -1;

   /**
    * @param key used to find value
    * @param value store the infobean
    */
   public void add(Object key, Object value);

   /**
    * @param key used to find value
    * @param value store the infobean
    * @param slidingExpiration the life of infobean
    */
   public void add(Object key, Object value, long slidingExpiration);

   /**
    * @param key remove 
    */
   public void remove(Object key);

   /**
    * remove all exited infobean in the cache
    */
   public void removeAll();

   /**
    * @param key
    * @return Object
    */
   public Object get(Object key);

   /**
    * @param key
    * @return boolean
    */
   public boolean contains(Object key);

}
