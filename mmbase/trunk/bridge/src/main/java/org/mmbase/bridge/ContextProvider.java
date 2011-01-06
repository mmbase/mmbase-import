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
import org.mmbase.util.*;
import org.mmbase.util.logging.*;



/**
 * This is the main factory to acquire certain {@link CloudContext}s. The most basic method is
 * {@link #getCloudContext(String)}.
 *
 * The method {@link #getResolvers} can be used to inspect what kind of CloudContexts are currently
 * available. Normally there is one named 'local' which represents the MMBase in the current
 * application. CloudContexts are pluggable. An extra jar may also provide one or more CloudContext
 * resolvers (e.g. the rmmci jar would do that).
 *
 * There is also a method {@link #getDefaultCloudContext()}. This returns
 * <code>getCloudContext('local')</code>, unless a System property
 * <code>mmbase.defaultcloudcontext</code> defines another URI (e.g. an rmmci implementation).
 *
 * If your code does not have any user interaction, you'll typically be able to start like this:
 * <pre>
    Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
    // Now start doing things with the Cloud.
 </pre>
 * This acquires a cloud on the 'default context' authenticated with {@link
 * org.mmbase.security.classsecurity.ClassAuthentication}. During testing the default cloud context
 * can e.g. be a {@link org.mmbase.bridge.mock.MockCloudContext} or a {@link
 * org.mmbase.bridge.RemoteCloudContext}.
 *
 * Though discouraged, it is sometimes also possible to directly access the wanted CloudContext
 * implementation, and not use ContextProvider at all. It depends on that implementation how
 * precisely that would go. E.g. if you're absolutely certain that your code must only work on the
 * local MMBase, you may proceed like so:
 <pre>
    Cloud cloud = LocalContext.getCloudContext().getCloud("mmbase", "class", null);
 </pre>
 * Another example, which is acceptable in test cases:
 <pre>
    Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
 </pre>
 *
 *
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

    /**
     * @since MMBase-1.9.2
     */
    private static final List<Resolver> resolvers = new CopyOnWriteArrayList<Resolver>();

    static {
        try {
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
                                    if (resolvers.contains(resolver)) {
                                        log.warn("Already resolving with " + resolver + " (" + url + ")");
                                    } else {
                                        log.info("Found " + resolver + " (" + url + ")");
                                        resolvers.add(resolver);
                                    }
                                } catch (Exception e) {
                                    log.error("During parsing of " + url + ": " + line + ":" + e.getMessage(), e);
                                }
                            }
                            line = reader.readLine();
                        }
                    }
                } catch (Throwable e) {
                    log.error("During parsing of " + url + ": " + e.getMessage(), e);
                }
            }

            Casting.setHelper(new BridgeCaster());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
     *   <li>mock:localhost : Will return a {@link org.mmbase.bridge.mock.MockCloudContext}.</li>
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
	throw new NotFoundException("cloudcontext with name '" + uri + "' is not known to MMBase " + resolvers);
    }

    /**
     * The uri defined as default (using the 'mmbase.defaultcloudcontext' system property) or
     * 'local'. The return value can be used in {@link #getCloudContext}, and is used in {@link #getDefaultCloudContext}.
     * @since MMBase-1.7
     * @return the name of the cloud context to be used as default
     **/
     public static String getDefaultCloudContextName() {
         //first choice.. set the cloud context using system properties

         String defaultCloudContextName = null;
         try {
             defaultCloudContextName = System.getProperty("mmbase.defaultcloudcontext");
         } catch (SecurityException se) {
             log.info(se);
         }
         if (defaultCloudContextName == null) {

             // Let's try wether DEFAULT_CLOUD_CONTEXT_NAME is indeed useable.
             for (Resolver resolver : resolvers) {
                 CloudContext cc = resolver.resolve(DEFAULT_CLOUD_CONTEXT_NAME);
                 if (cc != null) {
                     defaultCloudContextName = DEFAULT_CLOUD_CONTEXT_NAME;
                     break;
                 }
             }
             // No? Fall back to a mock implementation. E.g. rmmci-client will have a 'mock' mmbase as default and fall back.
             if (defaultCloudContextName == null) {
                 defaultCloudContextName = "mock:local";
             }

         }
         return defaultCloudContextName;
     }

    /**
     * Returns the 'default' cloud context. This could be <code>getCloudContext('local')</code>, or
     * <code>getCloudContext('rmi://127.0.0.1:1111/remotecontext</code> (as a fall back).
     *
     * @since MMBase-1.7
     * @return the default cloud context This is the local cloud if mmbase is running or could be started (with mmbase.config system property),
     *         Otherwise a default rmmci cloud, or specified with the mmbase.defaultcloudtext system
     *         property
     */
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
     * A Resolver resolves an URI-string (the argument of {@link #getCloudContext(String)} to a CloudContext object.
     */
    public static abstract class Resolver {
        protected final LocalizedString description = new LocalizedString(toString());
        {
            description.setBundle("org.mmbase.bridge.resources.contextproviders");
        }
        /**
         * Resolve an 'uri' string to an actual CloudContext instance.
         * @return A CloudContext or <code>null</code> if this specific Resolver doesn't understand
         * the uri.
         */
        public abstract CloudContext resolve(String uri);

        /**
         * The syntax of the uri and goal of the resuling CloudContext may be explained in this
         * description.
         */
        public LocalizedString getDescription() {
            return description;
        }
    }


}
