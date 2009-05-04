/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

/**
 * Main class to aquire CloudContexts
 * @author Kees Jongenburger
 * @version $Id$
 * @since MMBase-1.5
 */
public class ContextProvider {
   /**
    * When no system property mmbase.defaultcloudcontext is set
    * the default cloud context is the context returned when 
    * DEFAULT_CLOUD_CONTEXT_NAME is fed to getCloudContext(String)<BR>
    * DEFAULT_CLOUD_CONTEXT_NAME="local"
    **/

    public final static String DEFAULT_CLOUD_CONTEXT_NAME = "local";
    private static String defaultCloudContextName ;

    /**
     * Factory method to get an instance of a CloudContext. Depending
     * on the uri parameter given the CloudContext might be a local context
     * or a remote context (rmi)
     * @param uri an identifier for the context<br />
     * possible values:
     * <ul>
     *   <li>local : will return a local context</li>
     *   <li>rmi://hostname:port/contextname : will return a remote context</li>
     *   <li>a null parameter: will return the default context </li>
     * </ul>
     * @return a cloud context
     * @throws BridgeException if the cloudcontext was not found
     */
    public static CloudContext getCloudContext(String uri) {
        if (uri == null || (uri != null && uri.trim().length() == 0)){
            uri = getDefaultCloudContextName();
	}
        
        if (uri.startsWith("rmi")){
            return RemoteContext.getCloudContext(uri);
        } else if (uri.startsWith("local")){
            return LocalContext.getCloudContext();
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
             defaultCloudContextName = System.getProperty("mmbase.defaultcloudcontext");
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
        String uri = System.getProperty("mmbase.defaultcloudcontext");
        if (uri != null) {
            return getCloudContext(uri);
        }

        try {        
            return getCloudContext(getDefaultCloudContextName());
        } catch (NotFoundException nfe) {
            return getCloudContext("rmi://127.0.0.1:1111/remotecontext");
        } catch (BridgeException be) {
            throw new BridgeException(be.getMessage() + " You may want to specify -Dmmbase.defaultcloudcontext=<URI>", be);
        }
    }

}
