package com.finalist.util.version;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.Attributes;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VersionUtil {

   private static final String CMSC_PORTAL_START = "cmsc-portal";
   // we check against the editwizard jar, because otherwise we will get a
   // version starting with email-1. etc
   private static final String MMBASE_START = "mmbase";

   private static Log log;
   private static String applicationVersion;
   private static String cmscVersion;
   private static String mmbaseVersion;
   private static Map<String, String> libVersions;


   protected static Log getLogger() {
      if (log == null) {
         log = LogFactory.getLog(VersionUtil.class);
      }
      return log;
   }


   public static synchronized String getCmscVersion(ServletContext servletContext) {
      if (cmscVersion == null) {
         if (libVersions == null) {
            initLibVersions(servletContext);
         }
         cmscVersion = libVersions.get(CMSC_PORTAL_START);
      }
      return cmscVersion;
   }


   public static synchronized String getMmbaseVersion(ServletContext servletContext) {
      if (mmbaseVersion == null) {
         if (libVersions == null) {
            initLibVersions(servletContext);
         }
         mmbaseVersion = libVersions.get(MMBASE_START);
      }
      return mmbaseVersion;
   }


   private static synchronized void initLibVersions(ServletContext servletContext) {

      Set<String> paths = servletContext.getResourcePaths("/WEB-INF/lib");
      libVersions = new TreeMap<String, String>();
      for (String path : paths) {

         int start = path.lastIndexOf("/") + 1;
         int end = path.lastIndexOf("-");

         if (start != -1 && end != -1 && end > start) {
            if (path.charAt(end - 1) >= '0' && path.charAt(end - 1) <= '9') {
               end = path.lastIndexOf("-", end - 1);
            }

            String lib = path.substring(start, end);
            String version = path.substring(end + 1, path.lastIndexOf("."));

            libVersions.put(lib, version);
         }
      }

   }


   public static Map<String, String> getLibVersions(ServletContext servletContext) {
      if (libVersions == null) {
         initLibVersions(servletContext);
      }
      return libVersions;
   }


   public static synchronized String getApplicationVersion(ServletContext servletContext) {
      if (applicationVersion == null) {
         try {
            java.net.URL manifestURL;
            manifestURL = servletContext.getResource("/META-INF/MANIFEST.MF");

            java.util.jar.Manifest mf = new java.util.jar.Manifest(manifestURL.openStream());
            java.util.Map<String, java.util.jar.Attributes> entries = mf.getEntries();
            applicationVersion = "unknown";
            for (java.util.Iterator<Attributes> i = entries.values().iterator(); i.hasNext();) {
               java.util.jar.Attributes attributes = i.next();
               String implementationVersion = attributes.getValue("Implementation-Version");
               if (implementationVersion != null) {
                  applicationVersion = implementationVersion;
               }
            }
         }
         catch (MalformedURLException e) {
            getLogger().error("Unable to get application version.", e);
            return "Unable to get application version";
         }
         catch (IOException e) {
            getLogger().error("Unable to get application version.", e);
            return "Unable to get application version";
         }
      }
      return applicationVersion;
   }

}
