/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.rmi.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;

// import org.mmbase.bridge.remote.RemoteCloudContext;

/**
 * This, despite its name, is not actually a CloudContext, it only provides the static method to obtain one.
 *
 * @author Kees Jongenburger <keesj@framfab.nl>
 * @version $Id$
 * @since MMBase-1.5
 */
public final class RemoteContext {

    private RemoteContext() {
        throw new IllegalArgumentException("This class has no instances");
    }
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
            Class<?> clazz = Class.forName("org.mmbase.bridge.remote.proxy.UriRemoteCloudContext_Proxy");
            Constructor<?> constr =  clazz.getConstructor(Class.forName("org.mmbase.bridge.remote.RemoteCloudContext"), String.class);
            return (CloudContext) constr.newInstance(remoteCloudContext, uri);
            //new RemoteCloudContext_Impl(remoteCloudContext);
        } catch (MalformedURLException mue) {
            String message = mue.getMessage();
            if (message != null && message.indexOf("no protocol") > -1) {
                throw new RuntimeException("This exception maybe occured, because the servlet container is " +
                                           "installed in a directory with spaces.\n" +
                                           "The java.rmi.server.RMIClassLoader loads classes from network locations " +
                                           "(one or more URLS) for marschalling and unmarschalling parameters and return values. " +
                                           "The RMIClassLoader uses a codebase where to load the classes. The codebase is a string " +
                                           "with URLs separated by spaces.\n" +
                                           "Error message: " + mue.getMessage());
            }
            throw new BridgeException("While connecting to " + uri + ": " + mue.getMessage(), mue);
        } catch (Exception e){
            throw new BridgeException("While connecting to " + uri + ": " + e.getClass() + " " +  e.getMessage(), e);
        }
    }
    public static void main(String[] argv) {
        getCloudContext(argv[0]);
    }
}
