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
 * StubToLocalMapper is a utitity class that helps
 * a Stub to find it's Local implementation
 * @author Kees Jongenburger
 * @version $Id: StubToLocalMapper.java,v 1.3.2.1 2002-04-10 07:06:09 kees Exp $
 */
public class StubToLocalMapper{
    static private Logger log = Logging.getLoggerInstance(StubToLocalMapper.class.getName());
    /**
     * private data member to keep track of mapperCode/object combinations
     */
    private static Hashtable hash = new Hashtable();
    private static Hashtable refcount = new Hashtable();
    
    /**
     * add an object to the mapper
     * @param object the object to add to the mapper
     * @return a string that can later be used to find
     * back the object or remove it (MapperCode)
     */
    public static String add(Object object){
        if (object != null){
            String mapperCode = null;
	    if (object instanceof Node){
	    	Node node = (Node)object;
	        mapperCode = "node:" + node.getNodeManager().getName() + "-" + node.getNumber();
	    } else if (object instanceof Relation){
	    	Relation rel = (Relation)object;
	        mapperCode = "relation:" + rel.getNodeManager().getName() + "-" + rel.getNumber();
	    } else if (object instanceof NodeManager){
	    	NodeManager nodeManager = (NodeManager)object;
	        mapperCode = "nodemanager:" + nodeManager.getName() + "-" + nodeManager.hashCode();
	    } else {
	    	mapperCode = "" + object;
	    }
	    
            log.debug("add=("+ mapperCode +")");
	    int rcount = increaseRefCount(mapperCode);
	    if (rcount ==1){
               hash.put(mapperCode,object);
               log.debug("add=("+ mapperCode +")");
	    } else {
               log.debug("increace=("+ mapperCode +")("+ rcount +")");
	    }
            return mapperCode;
        }
        return "";
    }

    private static int increaseRefCount(String mapperCode){
    	Integer count = (Integer)refcount.get(mapperCode);
	if (count == null){
		refcount.put(mapperCode,new Integer(1));
		return 1;
	} else {
		refcount.put(mapperCode,new Integer(count.intValue() + 1));
		return count.intValue() + 1;
	}

    }

    private static int decreaseRefCount(String mapperCode){
    	Integer count = (Integer)refcount.get(mapperCode);
	if (count == null){
		log.warn("refcount entry not found for(" + mapperCode + ")");
		return 0;
	}
	int c = count.intValue();

	if (c ==1 ){
		refcount.remove(mapperCode);
		return 0;
	} else {
		refcount.put(mapperCode,new Integer(c  - 1));
		return c -1;
	}
    }
    
    /**
     * get an object based on its MapperCode
     * @param mapperCode the Mappercode of the object
     * @return the required object or null if there was no such object
     */
    public static Object get(String mapperCode){
        log.debug("access=("+ mapperCode +")");

        Object o = hash.get(mapperCode);
	if (o ==null){
		log.warn("hash.get("  + mapperCode +") has null value");
	}
        return o;
    }
    
    /**
     * remove an entry in the StubToLocal mapper
     * @param mapperCode the MapperCode of the object to be removed
     * @return if the entry was removed because refcount was 0
     */
    public static boolean remove(String mapperCode){
        if (mapperCode != null && ! mapperCode.equals("")){
	    int rcount = decreaseRefCount(mapperCode);
	    if (rcount ==0){
		    log.debug("remove=("+ mapperCode +")");
		    hash.remove(mapperCode);
		    return true;
	    } else {
		    log.debug("keep=("+ mapperCode +") refcount=("+ rcount +")");
		    return false;
	    }
        }
	return false;
    }
}
