package com.finalist.util.version;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.jar.Attributes;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VersionUtil {

   private static final String CMSC_PORTAL_START = "/WEB-INF/lib/cmsc-portal-";
   // we check against the editwizard jar, because otherwise we will get a version starting with email-1. etc
   private static final String MMBASE_START = "/WEB-INF/lib/mmbase-editwizard-"; 
   private static final String END = ".jar";
   
   private static Log log;
    private static String aplicationVersion;
    private static String cmscVersion;
    private static String mmbaseVersion;
    
    protected static Log getLogger() {
        if (log == null) {
            log = LogFactory.getLog(VersionUtil.class);
        }
        return log;
    }

    public static synchronized String getCmscVersion(ServletContext servletContext) {
       if(cmscVersion == null) {
          cmscVersion = getJarVersion(servletContext, CMSC_PORTAL_START);
       }
       return cmscVersion;
    }

    public static synchronized String getMmbaseVersion(ServletContext servletContext) {
       if(mmbaseVersion == null) {
          mmbaseVersion = getJarVersion(servletContext, MMBASE_START);
       }
       return mmbaseVersion;
    }

   private static String getJarVersion(ServletContext servletContext, String start) {
      Set<String> paths = servletContext.getResourcePaths("/WEB-INF/lib");
      for(String path:paths) {
         if(path.startsWith(start)) {
            return path.substring(start.length(), path.length()-END.length());
         }
      }
      return "unknown";
   }

	public static synchronized String getApplicationVersion(ServletContext servletContext) {
		if(aplicationVersion == null) {
			try {
				java.net.URL manifestURL;
				manifestURL = servletContext.getResource("/META-INF/MANIFEST.MF");
				
				java.util.jar.Manifest mf = new java.util.jar.Manifest(manifestURL.openStream());
				java.util.Map<String, java.util.jar.Attributes> entries = mf.getEntries();
            aplicationVersion = "unknown";
				for(java.util.Iterator<Attributes> i = entries.values().iterator(); i.hasNext();) {
					java.util.jar.Attributes attributes = i.next();
					String implementationVersion = attributes.getValue("Implementation-Version");
					if(implementationVersion != null) {
                  aplicationVersion = implementationVersion;
					}
				}
			} catch (MalformedURLException e) {
				getLogger().error("Unable to get application version.", e);
				return "Unable to get application version";
			} catch (IOException e) {
				getLogger().error("Unable to get application version.", e);
				return "Unable to get application version";
			}
		}
		return aplicationVersion;
	}
}
