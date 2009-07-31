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
                SortedSet<?> in = (SortedSet<?>)o;
                log.debug("convert treeset from "+ in);
                SortedSet<Object> set = new TreeSet<Object>();
                for (Object object : in) {
                    set.add(localToRMIObject(object));
                }
                log.debug("convert treeset to "+ set);
                retval = set;
            } else if (o instanceof List) {
                List<?> source = (List<?>)o;
                List<Object> list = new Vector<Object>();
                for (Object object : source) {
                    list.add(localToRMIObject(object));
                }
                retval = list;
            }
        }
        return retval;
    }

    /*
     *
     */
    public static Object rmiObjectToRemoteProxy(Object o) throws RemoteException {
        Object retval = o;
        if (o != null) {
            if (o instanceof ServerMappedObject) {
                Object mapObject = ObjectWrapperHelper.rmiObjectToRemoteProxy(o);
                if (mapObject != null) {
                    retval = mapObject;
                }
            } else if (o instanceof SortedSet) {
                SortedSet<?> in = (SortedSet<?>)o;
                SortedSet<Object> set = new TreeSet<Object>();
                for (Object object : in) {
                    set.add(rmiObjectToRemoteProxy(object));
                }
                retval = set;
            } else if (o instanceof List) {
                List<?> source = (List<?>)o;
                List<Object> list = new ArrayList<Object>();
                for (Object object : source) {
                    list.add(rmiObjectToRemoteProxy(object));
                }
                retval = list;
            }
        }
        return retval;
    }

    public static Object remoteProxyToRMIObject(Object o) {
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
