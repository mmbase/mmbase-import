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
 * @version $Id: StubToLocalMapper.java,v 1.4 2002-03-04 16:30:27 kees Exp $
 */
public class StubToLocalMapper{
    static private Logger log = Logging.getLoggerInstance(StubToLocalMapper.class.getName());

    /**
     * private data member to keep track of mapperCode/object combinations
     */
    private static Hashtable hash = new Hashtable();
    
    /**
     * add an object to the mapper
     * @param object the object to add to the mapper
     * @return a string that can later be used to find
     * back the object or remove it (MapperCode)
     */
    public static String add(Object object){
        if (object != null){
            String mapperCode = "" + object.hashCode();
	    if (log.isDebugEnabled()){
	    	StringBuffer sb = new StringBuffer();
		sb.append("StubToLocalMapper.add{mapperCode=("+ mapperCode +")}");
		sb.append(getObjectInfo(object));
		sb.append(" SIZE=" + hash.size());
            	log.debug(sb.toString());
	    }
            hash.put(mapperCode,object);
            return mapperCode;
        }
        return "";
    }
    
    /**
     * get an object based on its MapperCode
     * @param mapperCode the Mappercode of the object
     * @return the required object or null if there was no such object
     */
    public static Object get(String mapperCode){
        log.debug("accessing an object of StubToLocalMapper with mapperCode=("+ mapperCode +")");
        return hash.get(mapperCode);
    }
    
    /**
     * @param object the object to acquire information about
     * @return a String containing information about the 
     **/
    private static String getObjectInfo(Object object){
	if (object instanceof Node){
	    Node node = (Node)object;
	    return "node (type,number)=("+node.getNodeManager().getName() +","+ node.getNumber() +")";
	} else if (object instanceof NodeManager){
	    NodeManager nodeManager = (NodeManager)object;
	    return "nodemanager (name)=("+nodeManager.getName() +")";
	} else if (object instanceof NodeList){
	    NodeList nodeList = (NodeList)object;
	    return "nodelist (size)=("+nodeList.size() +")";
	} else if (object instanceof Cloud){
	    Cloud cloud = (Cloud)object;
	    return "cloud (name)=("+cloud.getName() +")";
	} else if (object instanceof CloudContext){
	    CloudContext cloudContext = (CloudContext)object;
	    return "cloudcontext";
	} else if (object instanceof Module){
	    Module module = (Module)object;
	    return "module (name)("+ module.getName() +")";
	} else {
	    return object.getClass().getName();
	}
    }

    /**
     * remove an entry in the StubToLocal mapper
     * @param mapperCode the MapperCode of the object to be removed
     */
    public static void remove(String mapperCode){
        if (mapperCode != null && ! mapperCode.equals("")){
	    if (log.isDebugEnabled()){
	        Object object = hash.get(mapperCode);
	    	StringBuffer sb = new StringBuffer();
		sb.append("StubToLocalMapper.remove{mapperCode=("+ mapperCode +")}");
		sb.append(getObjectInfo(object));
		sb.append(" SIZE=" + hash.size());
            	log.debug(sb.toString());
	    }
            hash.remove(mapperCode);
        }
    }
}
