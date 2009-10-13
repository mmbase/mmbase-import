/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.logging.*;



/**
 * Main class to aquire CloudContexts
 * @author Kees Jongenburger
 * @version $Id$
 * @since MMBase-1.5
 */
public class ContextProvider {
    private static final Logger log = Logging.getLoggerInstance(ContextProvider.class);
   /**
    * When no system property mmbase.defaultcloudcontext is set
    * the default cloud context is the context returned when
    * DEFAULT_CLOUD_CONTEXT_NAME is fed to getCloudContext(String)<BR>
    * DEFAULT_CLOUD_CONTEXT_NAME="local"
    **/

    public final static String DEFAULT_CLOUD_CONTEXT_NAME = "local";
    private static String defaultCloudContextName ;

    /**
     * @since MMBase-1.9.2
     */
    private static final List<Resolver> resolvers = new CopyOnWriteArrayList<Resolver>();

    static {
        for (URL url : ResourceLoader.getConfigurationRoot().getResourceList("contextproviders")) {
            try {
                URLConnection uc = url.openConnection();
                if (uc.getDoInput()) {
                    InputStream is = uc.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = reader.readLine();
                    while (line != null) {
                        line = line.trim();
                        if (line.length() > 0 && ! line.startsWith("#")) {
                            try {
                                Resolver resolver = (Resolver) Class.forName(line).newInstance();
                                resolvers.add(resolver);
                            } catch (Exception e) {
                                log.error("During parsing of " + url + ": " + line + ":" + e.getMessage(), e);
                            }
                        }
                        line = reader.readLine();
                    }
                }
            } catch (Exception e) {
                log.error("During parsing of " + url + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Factory method to get an instance of a CloudContext. Depending
     * on the uri parameter given the CloudContext might be a local context,
     * a remote context (rmi), or a CloudContext representing some other bridge implementation.
     * @param uri an identifier for the context<br />
     * possible values are defined by {@link #getResolvers}, but probably include:
     *
     * <ul>
     *   <li>local : will return a local context</li>
     *   <li>rmi://hostname:port/contextname : will return a remote context (only if the rmmci or rmmci-client jar is available)</li>
     *   <li>a null parameter: will return the default context. See {@link #getDefaultCloudContext} </li>
     * </ul>
     * The actual list can be found in the admin pages (a view on {@link #getResolvers}.
     *
     * @return a cloud context
     * @throws BridgeException if the cloudcontext was not found
     */
    public static CloudContext getCloudContext(String uri) {
        if (uri == null || uri.trim().length() == 0) {
            uri = getDefaultCloudContextName();
	}

        for (Resolver resolver : resolvers) {
            CloudContext cc = resolver.resolve(uri);
            if (cc != null) {
                return cc;
            }
        }
	throw new BridgeException("cloudcontext with name {" + uri + "} is not known to MMBase");
    }

    /**
     * @since MMBase-1.7
     * @return the name of the cloud context to be used as default
     **/
     public static String getDefaultCloudContextName() {
         //first choice.. set the cloud context using system properties
         if (defaultCloudContextName == null) {
             try {
                 defaultCloudContextName = System.getProperty("mmbase.defaultcloudcontext");
             } catch (SecurityException se) {
                 log.info(se);
             }
         }
         if (defaultCloudContextName == null) {
             defaultCloudContextName = DEFAULT_CLOUD_CONTEXT_NAME;
         }
         return defaultCloudContextName;
     }

    /**
     * @since MMBase-1.7
     * @return the default cloud context This is the local cloud if mmbase is running or could be started (with mmbase.config system property),
     *         Otherwise a default rmmci cloud, or specified with mmbase.defaultcloudtext.property
     **/
    public static CloudContext getDefaultCloudContext() {
        try {
            String uri = System.getProperty("mmbase.defaultcloudcontext");
            if (uri != null) {
                return getCloudContext(uri);
            }
        } catch (SecurityException se) {
            log.debug(se);
        }

        try {
            return getCloudContext(getDefaultCloudContextName());
        } catch (NotFoundException nfe) {
            return getCloudContext("rmi://127.0.0.1:1111/remotecontext");
        } catch (BridgeException be) {
            throw new BridgeException(be.getMessage() + " You may want to specify -Dmmbase.defaultcloudcontext=<URI>", be);
        }
    }

    /**
     * Returns the list of {@link Resolver}s that is used in the implementation of {@link
     * #getCloudContext(String)}.  The contents of this list are defined by the resource
     * org.mmbase.config.contextproviders. A plain text resource just simply stating the
     * Resolver-classes. E.g. the RMMCI-jar provides this resource too, to add itself to this list.
     *
     * @since MMBase-1.9.2
     */
    public static List<Resolver> getResolvers() {
        return Collections.unmodifiableList(resolvers);
    }

    private ContextProvider() {
        // no instances
    }




    /**
     * A Resolver resolves a URI-string to a CloudContext object.
     */
    public static abstract class Resolver {
        protected final LocalizedString description = new LocalizedString(toString());
        {
            description.setBundle("org.mmbase.bridge.resources.contextproviders");
        }

        public abstract CloudContext resolve(String uri);
        public LocalizedString getDescription() {
            return description;
        }
    }

    public static class LocalResolver extends Resolver {
        @Override
        public CloudContext resolve(String uri) {
            if (uri.equals("local")){
                return LocalContext.getCloudContext();
            } else {
                return null;
            }
        }
        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof LocalResolver;
        }
        @Override
        public String toString() {
            return "local";
        }
    }


}
