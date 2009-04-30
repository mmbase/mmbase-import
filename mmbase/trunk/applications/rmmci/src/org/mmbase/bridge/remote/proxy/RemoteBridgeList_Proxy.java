/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license


*/
package org.mmbase.bridge.remote.proxy;

import java.util.Collections;

import org.mmbase.bridge.*;
import org.mmbase.bridge.remote.*;

import org.mmbase.bridge.remote.util.*;

/**
 * @javadoc
 */

public class RemoteBridgeList_Proxy<L extends Comparable<? super L>,E> extends RemoteList_Proxy<L,E> implements BridgeList<L>, MappedObject  {

  public RemoteBridgeList_Proxy(RemoteBridgeList<E> remoteObject) {
    super(remoteObject);
  }

  public void setProperty(java.lang.Object arg0,java.lang.Object arg1) {
    try {
        getWrappedObject().setProperty(ObjectWrapper.remoteProxyToRMIObject(arg0), ObjectWrapper.remoteProxyToRMIObject(arg1));
    } catch (RuntimeException e) {
      throw e ;
    } catch(Exception e) {
      throw new BridgeException(e.getMessage(), e);
    }
  }

  public java.lang.Object getProperty(java.lang.Object arg0) {
    try {
      java.lang.Object retval = ObjectWrapper.rmiObjectToRemoteProxy(getWrappedObject().getProperty(ObjectWrapper.remoteProxyToRMIObject(arg0)));
      return retval;
    } catch (RuntimeException e) {
      throw e ;
    } catch(Exception e) {
      throw new BridgeException(e.getMessage(), e);
    }
  }

  public org.mmbase.bridge.BridgeList<L> subList(int arg0,int arg1) {
    try {
      RemoteBridgeList<E> list = getWrappedObject().subList(arg0, arg1);
      org.mmbase.bridge.BridgeList<L> retval = (org.mmbase.bridge.BridgeList<L>) ObjectWrapper.rmiObjectToRemoteProxy(list);
      return retval;
    } catch (RuntimeException e) {
      throw e ;
    } catch(Exception e) {
      throw new BridgeException(e.getMessage(), e);
    }
  }

  public void sort(java.util.Comparator<? super L> arg0) {
     // This is heavy for RMI. many remote compareTo calls will happen.
     Collections.sort(this, arg0);
  }

  public void sort() {
    try {
      // Let the server compare the items in the list.
      // only one call to the server
      getWrappedObject().sort();
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new BridgeException(e.getMessage(), e);
    }
  }

  public RemoteBridgeList<E> getWrappedObject() {
    return (RemoteBridgeList<E>) super.getWrappedObject();
  }

}
