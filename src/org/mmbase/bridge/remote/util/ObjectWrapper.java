package org.mmbase.bridge.remote.util;

import java.rmi.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.rmi.*;
import org.mmbase.bridge.remote.implementation.*;

/**
 * Util class that perfomrs translations of object to remote objects and 
 * remote bridge node implementations
 **/
public abstract class ObjectWrapper{
    public static Object localToRMIObject(Object o) throws RemoteException{
        if (o == null){
            return null;
        }
        String className = o.getClass().getName();
        if (className.indexOf("mmbase") == -1){
            return o;
        }
        Object retval  = null;
        if (o instanceof Node){
            retval =  new RemoteNode_Rmi((Node)o);
        }
        //ok .. it an mmbase object
        return retval;
    }
    
    public static Object rmiObjectToRemoteImplementation(Object o) throws RemoteException{
        if (o == null){
            return null;
        }
        String className = o.getClass().getName();
        if (className.indexOf("mmbase") == -1){
            return o;
        }
        Object retval  = null;
        if (o instanceof RemoteNode){
            retval =  new RemoteNode_Impl((RemoteNode)o);
        }
        return retval;
    }
    
    public static Object remoteImplementationToRMIObject(Object o){
        if (o == null){
            return null;
        }
        if (o instanceof MappedObject){
            return ((MappedObject)o).getWrappedObject();
        } else {
            return o;
        }
    }
    
    public static Object rmiObjectToLocal(Object o) throws RemoteException{
        if (o == null){
            return null;
        }
        if (o instanceof ServerMappedObject){
            return StubToLocalMapper.get(((ServerMappedObject)o).getMapperCode());
        } else {
            return o;
        }
    }
}
