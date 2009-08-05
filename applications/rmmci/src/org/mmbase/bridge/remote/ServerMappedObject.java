package org.mmbase.bridge.remote;

import java.rmi.*;
/**
 * MMBase bridge classes are wrapped in a bridge.remote.rmi object
 * on creation of such an object a ref is kept in the StubToLocalMapper.
 * this class makes it possible to make a transation between the original
 * object and the stub
 * @author Kees Jongenburger <keesj@dds.nl>
 **/
public interface ServerMappedObject extends Remote {
    /**
     * @return a value that when fed to the StubToLocalMapper returns the origirnal object
     **/
    public String getMapperCode() throws RemoteException;

    public int wrapped_hashCode() throws RemoteException;
    public boolean wrapped_equals(java.lang.Object arg0) throws RemoteException;
    public java.lang.String wrapped_toString() throws RemoteException;

}

