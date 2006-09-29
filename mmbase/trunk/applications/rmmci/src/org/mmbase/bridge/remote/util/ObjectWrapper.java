package org.mmbase.bridge.remote.util;

import java.util.*;
import java.rmi.*;
import java.util.Vector;

import org.mmbase.bridge.remote.*;

import org.mmbase.util.logging.*;
/**
 * Utility class that performs translations of object to remote objects and
 * remote bridge node implementations.
 **/
public abstract class ObjectWrapper {
    private static Logger log = Logging.getLoggerInstance(ObjectWrapper.class);

    /*
     * Called to create a RMI object based on a "local" object.
     */
    public static Object localToRMIObject(Object o) throws RemoteException {
        Object retval = o;
        if (o != null) {
            Object mapObject = ObjectWrapperHelper.localToRMIObject(o);
            if (mapObject != null) {
                retval = mapObject;
            } else if (o instanceof SortedSet) {
                SortedSet in = (SortedSet)o;
                log.debug("convert treeset from "+ in);
                SortedSet set = new TreeSet();
                Iterator i = in.iterator();
                while(i.hasNext()){
                    set.add(localToRMIObject(i.next()));
                }
                log.debug("convert treeset to "+ set);
                retval = set;
            } else if (o instanceof List) {
                List source = (List)o;
                List list = new Vector();
                for (int x = 0; x < source.size(); x++) {
                    list.add(localToRMIObject(source.get(x)));
                }
                retval = list;
            }
        }
        return retval;
    }

    /*
     *
     */
    public static Object rmiObjectToRemoteImplementation(Object o) throws RemoteException {
        Object retval = o;
        if (o != null) {
            if (o instanceof ServerMappedObject) {
                Object mapObject = ObjectWrapperHelper.rmiObjectToRemoteImplementation(o);
                if (mapObject != null) {
                    retval = mapObject;
                }
            } else if (o instanceof SortedSet) {
                SortedSet in = (SortedSet)o;
                System.err.println("convert treeset from "+ in);
                SortedSet set = new TreeSet();
                Iterator i = in.iterator();
                while(i.hasNext()){
                    set.add(rmiObjectToRemoteImplementation(i.next()));
                }
                System.err.println("convert treeset to"+ in);
                retval = set;
            } else if (o instanceof List) {
                List source = (List)o;
                List list = new ArrayList();
                for (int x = 0; x < source.size(); x++) {
                    list.add(rmiObjectToRemoteImplementation(source.get(x)));
                }
                retval = list;
            }
        }
        return retval;
    }

    public static Object remoteImplementationToRMIObject(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof MappedObject) {
            return ((MappedObject)o).getWrappedObject();
        } else {
            return o;
        }
    }

    public static Object rmiObjectToLocal(Object o) throws RemoteException {
        if (o == null) {
            return null;
        } else if (o instanceof ServerMappedObject) {
            return StubToLocalMapper.get(((ServerMappedObject)o).getMapperCode());
        } else {
            return o;
        }
    }
}
