/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.bridge.remote.rmi;

import java.rmi.RemoteException;
import java.util.List;

import org.mmbase.bridge.remote.RemoteList;
import org.mmbase.bridge.remote.util.ObjectWrapper;

/**
 * @javadoc Why is this implemented?
 */
public class RemoteList_Rmi<R, L> extends ServerMappedObject_Rmi<List<L>> implements RemoteList<R> {

    public RemoteList_Rmi(List<L> originalObject) throws RemoteException {
        super(originalObject);
    }

    public R set(int arg0, R arg1) throws RemoteException {
        L local = convertToLocal(arg1);
        L retLocal = getOriginalObject().set(arg0, local);
        R retval = convertToRemote(retLocal);
        return retval;
    }

    public void add(int arg0, R arg1) throws RemoteException {
        L local = convertToLocal(arg1);
        getOriginalObject().add(arg0, local);
    }

    public boolean add(R arg0) throws RemoteException {
        L local = convertToLocal(arg0);
        boolean retval = getOriginalObject().add(local);
        return retval;
    }

    public void clear() {
        getOriginalObject().clear();
    }

    public R get(int arg0) throws RemoteException {
        L local = getOriginalObject().get(arg0);
        R retval = convertToRemote(local);
        return retval;
    }

    public int size() {
        int retval = getOriginalObject().size();
        return retval;
    }

    public boolean remove(java.lang.Object arg0) throws RemoteException {
        Object local = ObjectWrapper.rmiObjectToLocal(arg0);
        boolean retval = getOriginalObject().remove(local);
        return retval;
    }

    public R remove(int arg0) throws RemoteException {
        L local = getOriginalObject().remove(arg0);
        R retval = convertToRemote(local);
        return retval;
    }

    private L convertToLocal(R arg0) throws RemoteException {
        L local = (L) ObjectWrapper.rmiObjectToLocal(arg0);
        return local;
    }

    private R convertToRemote(L local) throws RemoteException {
        R retval = (R) ObjectWrapper.localToRMIObject(local);
        return retval;
    }

}
