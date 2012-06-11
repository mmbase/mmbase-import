/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.remote;

import java.rmi.RemoteException;

public interface RemoteIterator<E> extends ServerMappedObject {

    boolean hasNext() throws RemoteException;
    E next() throws RemoteException;
    boolean hasPrevious() throws RemoteException;
    E previous() throws RemoteException;
    int nextIndex() throws RemoteException;
    int previousIndex() throws RemoteException;
    void remove() throws RemoteException;
    void set(E o) throws RemoteException;
    void add(E o) throws RemoteException;
}
