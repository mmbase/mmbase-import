/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.rmi.*;
import java.lang.reflect.*;

// import org.mmbase.bridge.remote.RemoteCloudContext;

/**
 * @javadoc
 * @author Kees Jongenburger <keesj@framfab.nl>
 * @version $Id: RemoteContext.java,v 1.5 2003-03-07 09:31:00 pierre Exp $
 * @since MMBase-1.5
 */
public class RemoteContext {

    /**
     * Connect to a remote cloudcontext. The name of the context
     * depends on configurations found in mmbaseroot.xml (host) and
     * rmmci.xml for port and context name
     * @todo should throw a Bridge Exception (?)
     * @param uri rmi uri like rmi://www.mmbase.org:1111/remotecontext
     * @return the remote cloud context named remotecontext
     * @throws RuntimeException if anything goes wrong
     */
    public static CloudContext getCloudContext(String uri) {
        try {

            Object remoteCloudContext= Naming.lookup(uri);
            try {
                Class clazz = Class.forName("org.mmbase.bridge.remote.implementation.RemoteCloudContext_Impl");
                Constructor constr =  clazz.getConstructor(new Class [] { Class.forName("org.mmbase.bridge.remote.RemoteCloudContext") });
                return (CloudContext) constr.newInstance(new Object[] { remoteCloudContext } );
                //new RemoteCloudContext_Impl(remoteCloudContext);
            } catch (ClassNotFoundException e) {
                return null;
            } catch (NoSuchMethodException e) {
                return null;
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    public static void main(String[] argv) {
        getCloudContext(argv[0]);
    }
}
