/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license


*/
package org.mmbase.bridge.remote;

import java.rmi.RemoteException;

/**
 * RemoteBridgeList is a generated interface based on org.mmbase.bridge.BridgeList<br />
 * This interface has almost the same methods names as the BridgeList interface.
 * The interface is created in such way that it can implement java.rmi.Remote.
 * Where needed other return values or parameters are used.
 * @since MMBase-1.9
 */
public interface RemoteBridgeList<E> extends  RemoteList<E> {
  public void setProperty(java.lang.Object arg0,java.lang.Object arg1) throws RemoteException;
  public java.lang.Object getProperty(java.lang.Object arg0) throws RemoteException;
  public java.util.Map<Object, Object> getProperties() throws RemoteException;
  public RemoteBridgeList<E> subList(int arg0,int arg1) throws RemoteException;
  public void sort(java.util.Comparator<? super E> arg0) throws RemoteException;
  public void sort() throws RemoteException;
}
