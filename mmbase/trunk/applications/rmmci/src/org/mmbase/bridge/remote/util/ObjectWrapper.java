package org.mmbase.bridge.remote.util;

import java.rmi.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.rmi.*;
import org.mmbase.bridge.remote.implementation.*;

/**
 * Util class that performs translations of object to remote objects and 
 * remote bridge node implementations
 **/
public abstract class ObjectWrapper {
    public static Object localToRMIObject(Object o) throws RemoteException {
        if (o == null) {
            return null;
        }
        String className = o.getClass().getName();
        if (className.indexOf("mmbase") == -1) {
            return o;
        }
        Object retval = null;
        if (o instanceof Node) {
            retval = new RemoteNode_Rmi((Node)o);
        }
        //ok .. it an mmbase object
        return retval;
    }

    public static Object rmiObjectToRemoteImplementation(Object o) throws RemoteException {
        if (o == null) {
            return null;
        }
        Object retval = null;
        String className = o.getClass().getName();

        if (className.indexOf("mmbase") != -1) {
            if (o instanceof RemoteNode) {
                retval = new RemoteNode_Impl((RemoteNode)o);
            }
        } else {
            retval = o;
        }
        //System.err.println("wrapped " + className + " to " + retval.getClass().getName());
        return retval;
    }

    public static Object remoteImplementationToRMIObject(Object o) {
        if (o == null) {
            return null;
        }
        String className = o.getClass().getName();
        Object retval = null;
        if (o instanceof MappedObject) {
            retval = ((MappedObject)o).getWrappedObject();
        } else {
            retval = o;
        }
        //System.err.println("wrapped "+ className + " to " + retval.getClass().getName());
        return retval;
    }

    public static Object rmiObjectToLocal(Object o) throws RemoteException {
        if (o == null) {
            return null;
        }
        if (o instanceof ServerMappedObject) {
            return StubToLocalMapper.get(((ServerMappedObject)o).getMapperCode());
        } else {
            return o;
        }
    }
}
