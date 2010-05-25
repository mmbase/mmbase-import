/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.bridge.remote.proxy;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.util.ObjectWrapper;


/**
 * @javadoc
 */
public class MappedObject_Proxy<O extends ServerMappedObject> implements MappedObject {

    private O remoteObject;

    public MappedObject_Proxy(O remoteObject) {
        this.remoteObject = remoteObject;
    }

    public String getMapperCode() {
        String code = null;
        try {
            code = remoteObject.getMapperCode();
        }
        catch (Exception e) {
        }
        return code;
    }

    public O getWrappedObject() {
        return remoteObject;
    }

    public int hashCode() {
        try {
            int retval = remoteObject.wrapped_hashCode();
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public java.lang.String toString() {
        try {
            java.lang.String retval = remoteObject.wrapped_toString();
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public boolean equals(java.lang.Object arg0) {
        try {
            boolean retval = remoteObject.wrapped_equals(ObjectWrapper.remoteProxyToRMIObject(arg0));
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

}
