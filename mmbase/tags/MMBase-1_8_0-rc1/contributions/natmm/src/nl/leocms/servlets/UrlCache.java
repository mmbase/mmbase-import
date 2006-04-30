package nl.leocms.servlets;

import java.util.*;
import org.mmbase.cache.oscache.OSCacheImplementation;

public class UrlCache {

  private Map cacheJSPToURL;
  private Map cacheURLToJSP;

  public UrlCache() {
    cacheJSPToURL = new OSCacheImplementation();
    cacheURLToJSP = new OSCacheImplementation();
    // set path explicitly, otherwise java.lang.NullPointerException
	 // at org.mmbase.cache.oscache.OSCacheImplementation.get(OSCacheImplementation.java:135)
    Map config = new HashMap(); 
    config.put("path", nl.mmatch.NatMMConfig.tempDir);
    ((OSCacheImplementation)cacheJSPToURL).config(config);
    ((OSCacheImplementation)cacheURLToJSP).config(config);
  }

  public void flushAll() {
    cacheJSPToURL.clear();
    cacheURLToJSP.clear();
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
