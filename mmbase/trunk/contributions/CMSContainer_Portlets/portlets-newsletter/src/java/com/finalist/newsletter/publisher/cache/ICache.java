package com.finalist.newsletter.publisher.cache;

public interface ICache {
   public static int Forever = -1;

   public void add(Object key, Object value);

   public void add(Object key, Object value, long slidingExpiration);

   public void remove(Object key);

   public void removeAll();

   public Object get(Object key);

   public boolean contains(Object key);

}
