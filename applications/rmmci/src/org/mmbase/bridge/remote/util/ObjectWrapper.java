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
    public static Object localToRMIObject(Object o, int port) throws RemoteException {
        //if the object is null -> return null
        if (o == null) {
            return null;
        }
        if (o.getClass().getName().indexOf("mmbase") == -1 && o instanceof SortedSet) {

            SortedSet in = (SortedSet)o;
            log.debug("convert treeset from "+ in);
            SortedSet set = new TreeSet();
            Iterator i = in.iterator();
            while(i.hasNext()){
                set.add(localToRMIObject(i.next(), port));
            }
            log.debug("convert treeset to "+ set);
            return set;
        }

        if (o.getClass().getName().indexOf("mmbase") == -1 && o instanceof List) {
            List source = (List)o;
            List list = new Vector();
            for (int x = 0; x < source.size(); x++) {
                list.add(localToRMIObject(source.get(x), port));
            }
            return list;
        }

        String className = o.getClass().getName();

        if (className.indexOf("mmbase") == -1) {
            //if it is not a mmbase class we don't care and hope the object is serialisable
            return o;
        }

        //it's an mmbase object
        Object retval = ObjectWrapperHelper.localToRMIObject(o, port);

        if (retval == null) {
            log.warn("please add a wrapper for objects of type " + className);
        } else {
            log.debug(o.getClass().getName() + " -> " + retval.getClass().getName());
        }
        return retval;
    }

    /*
     *
     */
    public static Object rmiObjectToRemoteImplementation(Object o) throws RemoteException {
        if (o == null) {
            return null;
        }

        if (o.getClass().getName().indexOf("mmbase") == -1 && o instanceof SortedSet) {
            SortedSet in = (SortedSet)o;
            System.err.println("convert treeset from "+ in);
            SortedSet set = new TreeSet();
            Iterator i = in.iterator();
            while(i.hasNext()){
                set.add(rmiObjectToRemoteImplementation(i.next()));
            }
            System.err.println("convert treeset to"+ in);
            return set;
        }

        if (o.getClass().getName().indexOf("mmbase") == -1 && o instanceof List) {
            List source = (List)o;
            List list = new ArrayList();
            for (int x = 0; x < source.size(); x++) {
                list.add(rmiObjectToRemoteImplementation(source.get(x)));
            }
            return list;
        }

        Object retval = null;
        String className = o.getClass().getName();

        if (className.indexOf("mmbase") != -1) {
            retval = ObjectWrapperHelper.rmiObjectToRemoteImplementation(o);
            if (retval == null) {
                log.warn("please add a  wrapper for objects of type " + className);
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
