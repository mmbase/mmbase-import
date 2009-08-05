/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license


*/
package org.mmbase.bridge.remote.proxy;

import org.mmbase.bridge.remote.*;
public class UriRemoteCloudContext_Proxy extends RemoteCloudContext_Proxy {

    private final String uri;
    public UriRemoteCloudContext_Proxy(RemoteCloudContext remoteObject, String uri) {
        super(remoteObject);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

}
