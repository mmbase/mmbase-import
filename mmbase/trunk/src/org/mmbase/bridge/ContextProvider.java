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
 * @version $Id: ContextProvider.java,v 1.1 2001-11-21 12:30:13 kees Exp $
 * @since MMBase-1.5
 */
public class ContextProvider{
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
     * @throws RuntimeException if anything wrong happends
     */
    public static CloudContext getCloudContext(String uri) {
        if (uri == null) uri="";
        
        if (uri.startsWith("rmi")){
            return RemoteContext.getCloudContext(uri);
        } else if (uri.startsWith("local")){
            return LocalContext.getCloudContext();
        }
        return LocalContext.getCloudContext();
    }
}
