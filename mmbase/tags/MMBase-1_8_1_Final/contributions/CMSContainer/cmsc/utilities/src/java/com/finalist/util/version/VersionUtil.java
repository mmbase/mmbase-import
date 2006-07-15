package com.finalist.util.version;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VersionUtil {

    private static Log log;
    private static String version;
    
    protected static Log getLogger() {
        if (log == null) {
            log = LogFactory.getLog(VersionUtil.class);
        }
        return log;
    }
	
	public static synchronized String getVersion(ServletContext servletContext) {
		if(version == null) {
			try {
				java.net.URL manifestURL;
				manifestURL = servletContext.getResource("/META-INF/MANIFEST.MF");
				
				java.util.jar.Manifest mf = new java.util.jar.Manifest(manifestURL.openStream());
				java.util.Map<String, java.util.jar.Attributes> entries = mf.getEntries();
				version = "unknown";
				for(java.util.Iterator i = entries.values().iterator(); i.hasNext();) {
					java.util.jar.Attributes attributes = (java.util.jar.Attributes)i.next();
					String implementationVersion = attributes.getValue("Implementation-Version");
					if(implementationVersion != null) {
						version = implementationVersion;
					}
				}
			} catch (MalformedURLException e) {
				getLogger().error("Unable to get version.", e);
				return "Unable to get version";
			} catch (IOException e) {
				getLogger().error("Unable to get version.", e);
				return "Unable to get version";
			}
		}
		return version;
	}
}
