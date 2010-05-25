/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.bridge.remote.proxy;

import java.rmi.RemoteException;
import java.util.AbstractList;
import java.util.List;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.util.ObjectWrapper;


/**
 * @javadoc
 */
public class RemoteList_Proxy<L, R> extends AbstractList<L> implements List<L>, MappedObject {

    // remote object
    private RemoteList<R> remoteObject;

    public RemoteList_Proxy(RemoteList<R> remoteObject) {
        super();
        this.remoteObject = remoteObject;
    }

    public void add(int index, L o) {
        try {
            R remote = convertToRemote(o);
            remoteObject.add(index, remote);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public boolean add(L o) {
        try {
            R remote = convertToRemote(o);
            boolean retval = remoteObject.add(remote);
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public void clear() {
        try {
            // Calling remote clear will perform better then calling
            // remove for each remote object in the list
            remoteObject.clear();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public L get(int index) {
        try {
            R remote = remoteObject.get(index);
            L retval = convertToLocal(remote);
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public int size() {
        try {
            int retval = remoteObject.size();
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public boolean remove(java.lang.Object o) {
        try {
            Object remote = ObjectWrapper.remoteProxyToRMIObject(o);
            boolean retval = remoteObject.remove(remote);
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public L remove(int index) {
        try {
            R remote = remoteObject.remove(index);
            L retval = convertToLocal(remote);
            return retval;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public L set(int index, L o) {
        try {
            R remotesSet = convertToRemote(o);
            R remoteReturn = remoteObject.set(index, remotesSet);
            L retval = convertToLocal(remoteReturn);
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
            // Calling remote toString will perform better then calling
            // toString for each remote object in the list
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

    public int hashCode() {
        try {
            // Calling remote hashCode will perform better then calling
            // hashCode for each remote object in the list
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

    @Override
    public boolean equals(Object o) {
        // ?? is it possible to check equality on remote objects?
        return super.equals(o);
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

    public RemoteList<R> getWrappedObject() {
        return remoteObject;
    }

    private L convertToLocal(R remote) throws RemoteException {
        L retval = (L) ObjectWrapper.rmiObjectToRemoteProxy(remote);
        return retval;
    }

    private R convertToRemote(L o) {
        R remote = (R) ObjectWrapper.remoteProxyToRMIObject(o);
        return remote;
    }

}
