/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.remote.util;

import java.util.*;
/**
 * StubToLocalMapper is a utitity class that helps
 * a Stub to find it's Local implementation
 * @author Kees Jongenburger
 * @version $Id: StubToLocalMapper.java,v 1.1 2001-11-29 11:59:58 kees Exp $
 */
public class StubToLocalMapper{
    /**
     * pricate data member to keep track of hashCode/object combinations
     */
    private static Hashtable hash = new Hashtable();
    
    /**
     * add an object to the mapper
     * @param object the object to add to the mapper
     * @return a string that can later be used to find
     * back the object or remove it (MapperCode)
     */
    public static String add(Object object){
        hash.put("" + object.hashCode(),object);
        return "" + object.hashCode();
    }
    
    /**
     * get an object based on its MapperCode
     * @param mapperCode the Mappercode of the object
     * @return the required object or null if there was no such object
     */
    public static Object get(String mapperCode){
        return hash.get(mapperCode);
    }
    
    /**
     * remove an entry in the StubToLocal mapper
     * @param mapperCode the MapperCode of the object to be removed
     */
    public static void remove(String mapperCode){
        hash.remove(mapperCode);
    }
}
