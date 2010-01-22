/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.remote.rmi;

import java.rmi.RemoteException;
import java.util.ListIterator;

import org.mmbase.bridge.remote.RemoteIterator;
import org.mmbase.bridge.remote.util.ObjectWrapper;

/**
 * @javadoc Why is this implemented?
 */
public class RemoteIterator_Rmi<R, L> extends ServerMappedObject_Rmi<ListIterator<L>> implements RemoteIterator<R> {

    public RemoteIterator_Rmi(ListIterator<L> originalObject) throws RemoteException {
        super(originalObject);
    }

    public boolean hasNext() {
        return getOriginalObject().hasNext();
    }

    public boolean hasPrevious() {
        return getOriginalObject().hasPrevious();
    }

    public R next() throws RemoteException {
        L local = getOriginalObject().next();
        R retval = convertToRemote(local);
        return retval;
    }

    public int nextIndex() {
        return getOriginalObject().nextIndex();
    }

    public R previous() throws RemoteException {
        L local = getOriginalObject().previous();
        R retval = convertToRemote(local);
        return retval;
    }

    public int previousIndex() {
        return getOriginalObject().previousIndex();
    }

    public void remove() {
        getOriginalObject().remove();
    }

    public void set(R o) throws RemoteException {
        L local = convertToLocal(o);
        getOriginalObject().set(local);
    }

    public void add(R o) throws RemoteException {
        L local = convertToLocal(o);
        getOriginalObject().add(local);
    }

    private L convertToLocal(R o) throws RemoteException {
        L local = (L) ObjectWrapper.rmiObjectToLocal(o);
        return local;
    }

    private R convertToRemote(L local) throws RemoteException {
        R retval = (R) ObjectWrapper.localToRMIObject(local);
        return retval;
    }

}
