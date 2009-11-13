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
 * Override the getCloud-methods, to make sure the returned objects have a correct getCloudContext,
 * namely this object again.
 *
 * This makes is possible to check whether a Cloud is remote, by checking the URI of its
 * cloud-context.
 *
 * @version $Id$
 * @author Michiel Meeuwissen
 */
public class UriRemoteCloudContext_Proxy extends RemoteCloudContext_Proxy {

    private final String uri;
    public UriRemoteCloudContext_Proxy(RemoteCloudContext remoteObject, String uri) {
        super(remoteObject);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    protected RemoteCloud getRemote(Cloud cloud) {
        return ((RemoteCloud_Proxy) cloud).getWrappedObject();
    }

    @Override
    public Cloud getCloud(String name) {
        return new UriRemoteCloud_Proxy(this, getRemote(super.getCloud(name)));
    }


    @Override
    public Cloud getCloud(String name, String authenticationType, java.util.Map<String, ?> loginInfo) throws NotFoundException {
        return new UriRemoteCloud_Proxy(this, getRemote(super.getCloud(name, authenticationType, loginInfo)));
    }
    @Override
    public Cloud getCloud(String name, org.mmbase.security.UserContext user) throws NotFoundException {
        return new UriRemoteCloud_Proxy(this, getRemote(super.getCloud(name, user)));
    }





}
