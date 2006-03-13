package nl.leocms.servlets;

import java.util.*;

public class UrlCache {

  private Map cache;

  public UrlCache() {
    cache = new HashMap();
  }


  public void flushAll() {
    for (Iterator it=cache.keySet().iterator();it.hasNext();) {
      cache.remove(it.next());
    }
  }

  public String getCacheEntry(String requestedURL) {
    return (String)cache.get(requestedURL);
  }

  public void putInCache(String requestedURL, String processedURL) {
    cache.put(requestedURL, processedURL);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator it=cache.keySet().iterator();it.hasNext();) {
      String key = (String)it.next();
      String value = (String)cache.get(key);
      sb.append(key).append(" - ").append(value).append("\n");
    }
    return sb.toString();
  }

}
