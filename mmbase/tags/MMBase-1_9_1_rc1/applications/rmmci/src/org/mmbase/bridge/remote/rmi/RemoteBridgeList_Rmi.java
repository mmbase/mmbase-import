/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license


*/
package org.mmbase.bridge.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;

import org.mmbase.bridge.BridgeList;
import org.mmbase.bridge.remote.RemoteBridgeList;
import org.mmbase.bridge.remote.util.ObjectWrapper;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc Why is this implemented?
 */

public class RemoteBridgeList_Rmi<R, L> extends RemoteList_Rmi<R, L> implements RemoteBridgeList<R>, Unreferenced  {
    private static Logger log = Logging.getLoggerInstance(RemoteBridgeList_Rmi.class);

    public RemoteBridgeList_Rmi(BridgeList<L> originalObject) throws RemoteException{
        super(originalObject);
        log.debug("new RemoteBridgeList_Rmi");
    }
    public void setProperty(java.lang.Object arg0,java.lang.Object arg1) throws RemoteException {
        getOriginalObject().setProperty(ObjectWrapper.rmiObjectToLocal(arg0), ObjectWrapper.rmiObjectToLocal(arg1));
    }

    public java.lang.Object getProperty(java.lang.Object arg0) throws RemoteException {
        java.lang.Object retval = ObjectWrapper.localToRMIObject(getOriginalObject().getProperty(ObjectWrapper.rmiObjectToLocal(arg0)));
        return retval;
    }

    public RemoteBridgeList<R> subList(int arg0,int arg1) throws RemoteException {
        BridgeList<L> list = getOriginalObject().subList(arg0, arg1);
        RemoteBridgeList<R> retval = (RemoteBridgeList<R>) ObjectWrapper.localToRMIObject(list);
        return retval;
    }

    public void sort(java.util.Comparator<? super R> arg0) {
        throw new UnsupportedOperationException(" Method not supported in remote bridge ");
    }

    public void sort() {
        getOriginalObject().sort();
    }

    public BridgeList<L> getOriginalObject() {
        return (BridgeList<L>) super.getOriginalObject();
    }
}
