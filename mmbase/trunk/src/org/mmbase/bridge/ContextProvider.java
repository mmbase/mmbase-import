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
 * @version $Id: ContextProvider.java,v 1.3 2003-02-24 20:41:47 kees Exp $
 * @since MMBase-1.5
 */
public class ContextProvider{
    private final static String DEFAULT_CLOUD_CONTEXT="local";
    private static String defaultCloudContext ;
    /**
     * Factory method to get an instance of a CloudContext. Depending
     * on the uri parameter given the CloudContext might be a local context
     * or a remote context (rmi)
     * @param uri an identifier for the context<BR>
     * possible values:
     * <UL>
     *   <LI>local : will return a local context</LI>
     *   <LI>rmi://hostname:port/contextname : will return a remote context</LI>
     *   <LI>a null parameter: will return a local context
     * </UL>
     * @return a cloud context
     * @throws BridgeException if the cloudcontext was not found
     */
    public static CloudContext getCloudContext(String uri) {
        if (uri == null) uri="";

        if (uri.startsWith("rmi")){
            return RemoteContext.getCloudContext(uri);
        } else if (uri.startsWith("local")){
            return LocalContext.getCloudContext();
        }
	throw new BridgeException("cloudcontext with name {"+ uri +"} is not known to MMBase");
    }

    /**
     *
     **/
    public static CloudContext getDefaultCloudContext(){
	    //first choice.. set the cloud context using system properties
	    if (defaultCloudContext == null){
		    defaultCloudContext = System.getProperty("mmbase.defaultcloudcontext");
	    }
	    if (defaultCloudContext == null){
		    defaultCloudContext = DEFAULT_CLOUD_CONTEXT;
	    }
	    return getCloudContext(defaultCloudContext);
    }
}
