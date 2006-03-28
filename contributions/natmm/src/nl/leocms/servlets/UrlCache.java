package nl.leocms.servlets;

import java.util.*;

public class UrlCache {

  private Map cacheJSPToURL;
  private Map cacheURLToJSP;

  public UrlCache() {
    cacheJSPToURL = new HashMap();
    cacheURLToJSP = new HashMap();
  }

  public void flushAll() {
    // this can lead to a: java.util.ConcurrentModificationException
    for (Iterator it=cacheJSPToURL.keySet().iterator();it.hasNext();) {
      cacheJSPToURL.remove(it.next());
    }
    for (Iterator it=cacheURLToJSP.keySet().iterator();it.hasNext();) {
      cacheURLToJSP.remove(it.next());
    }
  }

  public String getJSPEntry(String requestedURL) {
    return (String)cacheURLToJSP.get(requestedURL);
  }

  public String getURLEntry(String requestedURL) {
    return (String)cacheJSPToURL.get(requestedURL);
  }

  public void putInCache(String jspURL, String processedURL) {
    cacheJSPToURL.put(jspURL, processedURL);
    cacheURLToJSP.put(processedURL, jspURL);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator it=cacheJSPToURL.keySet().iterator();it.hasNext();) {
      String key = (String)it.next();
      String value = (String)cacheJSPToURL.get(key);
      sb.append(key).append(" - ").append(value).append("\n");
    }
    return sb.toString();
  }

}
