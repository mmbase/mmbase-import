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
	 String tempdir = System.getProperty("java.io.tmpdir");
	 if ( !(tempdir.endsWith("/") || tempdir.endsWith("\\")) ) {
		 tempdir += System.getProperty("file.separator");
	 }
    Map config = new HashMap(); 
    config.put("path", tempdir);
    ((OSCacheImplementation)cacheJSPToURL).config(config);
    ((OSCacheImplementation)cacheURLToJSP).config(config);
  }

  public void flushAll() {
    cacheJSPToURL.clear();
    cacheURLToJSP.clear();
  }

  public void putJSPEntry(String jspURL, String processedURL) {
    cacheURLToJSP.put(processedURL, jspURL);
  }

  public String getJSPEntry(String processedURL) {
    return (String)cacheURLToJSP.get(processedURL);
  }

  public void putURLEntry(String jspURL, String processedURL) {
    cacheJSPToURL.put(jspURL, processedURL);
  }

  public String getURLEntry(String jspURL) {
    return (String)cacheJSPToURL.get(jspURL);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("JSP to URL Cache\n");
    for (Iterator it=cacheJSPToURL.keySet().iterator();it.hasNext();) {
      String key = (String)it.next();
      String value = (String)cacheJSPToURL.get(key);
      sb.append(key).append(" - ").append(value).append("\n");
    }
    sb.append("URL to JSP Cache\n");
    for (Iterator it=cacheURLToJSP.keySet().iterator();it.hasNext();) {
      String key = (String)it.next();
      String value = (String)cacheURLToJSP.get(key);
      sb.append(key).append(" - ").append(value).append("\n");
    }
    return sb.toString();
  }

}
