/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.remote;

import java.rmi.RemoteException;


public interface RemoteList<E> extends ServerMappedObject {

    public E get(int index) throws RemoteException;
    public int size() throws RemoteException;

    public boolean remove(java.lang.Object o) throws RemoteException;
    public E remove(int index) throws RemoteException;
    public E set(int arg0,E arg1) throws RemoteException;
    public void add(int index ,E o) throws RemoteException;
    public boolean add(E o) throws RemoteException;
    public void clear() throws RemoteException;
}