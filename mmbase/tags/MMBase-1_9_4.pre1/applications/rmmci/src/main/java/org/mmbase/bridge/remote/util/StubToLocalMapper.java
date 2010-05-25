/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.remote.util;

import java.util.*;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * StubToLocalMapper is a utitity class that helps a Stub to find it's Local implementation.
 * @author Kees Jongenburger
 * @version $Id$
 **/
public class StubToLocalMapper {
    static private final Logger log = Logging.getLoggerInstance(StubToLocalMapper.class);
    /**
     * private data member to keep track of mapperCode/object combinations
     **/
    private static final Map<String, Object> hash     = new Hashtable<String, Object>();
    private static final Map<String, Integer> refcount = new Hashtable<String, Integer>();

    /**
     * Add an object to the mapper.
     * @param object the object to add to the mapper
     * @return a string that can later be used to find back the object or remove it (MapperCode)
     **/
    public static String add(Object object) {
        if (object != null) {

            String mapperCode = null;
            if (object instanceof Node) {
                Node node = (Node)object;
                mapperCode = "node:" + node.getNodeManager().getName() + "->" + node.getNumber();
            } else if (object instanceof Cloud) {
                Cloud cloud = (Cloud)object;
                mapperCode = "cloud:" + cloud.getName();
            } else if (object instanceof Relation) {
                Relation rel = (Relation)object;
                mapperCode = "relation:" + rel.getNodeManager().getName() + "->" + rel.getNumber();
            } else if (object instanceof RelationManager) {
                RelationManager relationManager = (RelationManager)object;
                mapperCode = "relationmanager:" + relationManager.getName() + "->" + relationManager.getNumber();
            } else if (object instanceof NodeManager) {
                NodeManager nodeManager = (NodeManager)object;
                mapperCode = "nodemanager:" + nodeManager.getName() + "->" + nodeManager.getNumber();
            } else {
                mapperCode = "" + object;
            }

            //code needed to support transactions
            //the generated key (like node:nodemanagername->nodeNumber) is currently wrong
            //since multiple different instances of the node might exist
            //the real fix is to add the BasicCloud.getAccount() to the key
            //but this requires a different code for every type of object(to find the key)
            //so first we will as a temp fix try to not also check if the object is in the hash
            //is so and they are not the same instance we create a new hash entry
            if (hash.get(mapperCode) != null) {
                //if there is a hash entry but the objects are not equal
                if (hash.get(mapperCode) != object) {
                    for (int counter = 1; true; counter++) {
                        String newMapperCode = mapperCode + "{" + counter + "}";
                        if (!hash.containsKey(newMapperCode) || hash.get(newMapperCode) == object) {
                            mapperCode = newMapperCode;
                            break;
                        }
                    }
                }
            }

            log.debug("add=(" + mapperCode + ")");
            int rcount = increaseRefCount(mapperCode);
            if (rcount == 1) {
                hash.put(mapperCode, object);
                log.debug("add=(" + mapperCode + ")");
            } else {
                log.debug("increace=(" + mapperCode + ")(" + rcount + ")");
            }
            return mapperCode;
        }
        return "";
    }

    /**
     * Increase the counter of references to a certain mapper code.
     * @param mapperCode the mapper to for wich we do reference counting
     * @return the amount of references known at this point
     **/
    private static int increaseRefCount(String mapperCode) {
        Integer count = refcount.get(mapperCode);
        if (count == null) {
            refcount.put(mapperCode, 1);
            return 1;
        } else {
            refcount.put(mapperCode, count.intValue() + 1);
            return count.intValue() + 1;
        }

    }

    /**
     * Decrease the counter of references to a certain mapper code.
     * @param mapperCode the mapper code for with we do reference counting
     * @return the number of references we have for the mapper code
     **/
    private static int decreaseRefCount(String mapperCode) {
        Integer count = refcount.get(mapperCode);
        if (count == null) {
            log.warn("refcount entry not found for(" + mapperCode + ")", new Exception());
            return 0;
        }
        int c = count.intValue();

        if (c == 1) {
            refcount.remove(mapperCode);
            return 0;
        } else {
            refcount.put(mapperCode, c - 1);
            return c - 1;
        }
    }

    /**
     * Get an object based on its mapper code.
     * @param mapperCode the Mappercode of the object
     * @return the required object or <code>null</code> if there was no such object
     **/
    public static Object get(String mapperCode) {
        log.debug("access=(" + mapperCode + ")");
        Object o = hash.get(mapperCode);
        return o;
    }

    /**
     * Remove an entry in the StubToLocal mapper.
     * The entry is only removed if there are no other references to the entry.
     * @param mapperCode the MapperCode of the object to be removed
     * @return <code>true</code> if the entry was removed
     **/
    public static boolean remove(String mapperCode) {
        if (mapperCode != null && !mapperCode.equals("")) {
            int rcount = decreaseRefCount(mapperCode);
            if (rcount == 0) {
                log.debug("remove=(" + mapperCode + ")");
                hash.remove(mapperCode);
                return true;
            } else {
                log.debug("keep=(" + mapperCode + ") refcount=(" + rcount + ")");
                return false;
            }
        }
        return false;
    }
}
