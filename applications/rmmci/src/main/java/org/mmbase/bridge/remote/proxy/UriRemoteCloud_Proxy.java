/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license


*/
package org.mmbase.bridge.remote.proxy;

import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.*;

/**
 * Fixes  {@link getCloudContext} (should be remote again);
 * @version $Id$
 * @author Michiel Meeuwissen
 */
public class UriRemoteCloud_Proxy extends RemoteCloud_Proxy {

    private final CloudContext cc;
    public UriRemoteCloud_Proxy(UriRemoteCloudContext_Proxy c, RemoteCloud cloud) {
        super(cloud);
        cc = c;
    }
    @Override
    public CloudContext getCloudContext() {
        return cc;
    }

}
