/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.rmi.*;
import java.rmi.registry.*;

import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.rmi.*;
import org.mmbase.bridge.remote.implementation.*;

import org.mmbase.bridge.*;

/**
 * @javadoc
 * @author Kees Jongenburger <keesj@framfab.nl>
 * @version $Id: RemoteContext.java,v 1.2 2002-01-31 10:05:08 pierre Exp $
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
     * @throw RuntimeException if anything goes wrong
     */
    public static CloudContext getCloudContext(String uri) {
        try {
            RemoteCloudContext remoteCloudContext= (RemoteCloudContext)Naming.lookup(uri);
            return new RemoteCloudContext_Impl(remoteCloudContext);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
