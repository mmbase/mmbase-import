/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.JspTagException;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is a HashMap, but the keys can contain 'dots', in which case
 * there is searched for HashMaps in the HashMap.
 *
 * @author Michiel Meeuwissen
 **/

public class ContextContainer extends HashMap {
    private static Logger log = Logging.getLoggerInstance(ContextContainer.class.getName());
    private String id;
    private ContextContainer parent;

    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */

    public ContextContainer(String _id, ContextContainer _parent) {
        super();
        id = _id;        
        parent = _parent;
    }

    /**
     * Keys must be Strings, so put(Object, ..) is forbidden in this HashMap!
     *
     */
       
    public Object put(Object key, Object value) {
        throw new RuntimeException("Error, key should be string in ContextContainers!");
    }
    /**
     * Not all Strings can be allowed as keys. Keys are like variable names. 
     *
     */

    public Object put(String key, Object value) throws JspTagException {
        if (key.indexOf('.') != -1) {
            throw new JspTagException("Key may not contain dots");
        }
        return super.put(key, value);
    }
    public boolean containsKey(Object key) {
        throw new RuntimeException("Error, key should be string in ContextContainers!");
    }

    /**
     * This function takes a key, which can contain dots. It returns
     * a structure with a new ContextContainer and the rest of the
     * key. It also returns if it had to 'go down' to find this,
     * because if it has done this, the next time you may not go up
     * again (check the parent).
     *
     */

    private Pair getPair(String key, boolean checkParent) throws JspTagException {
        // checking if key contains a 'dot'.
        int dotpos = key.indexOf('.');
        if (dotpos > 0) {  
            String contextKey = key.substring(0, dotpos);
            // find the Context:
            boolean wentDown = true;
            Object c = simpleGet(contextKey, checkParent);
            if(c == null && checkParent && parent != null) {
                c =  parent.get(key, true); 
                wentDown = false;
            } 
            
            if(c == null) {
                throw new JspTagException("Context '" + contextKey+ "' could not be found.");
            }
            if (! (c instanceof ContextContainer)) {
                throw new JspTagException(contextKey + " is not a Context, but a " + c.getClass().getName());
            }
            String newKey = key.substring(dotpos + 1);
            // and search with that one:
            return new Pair((ContextContainer)c, newKey, wentDown);
        } else {
            return null;
        }

    }
    /**
     * Like containsKey but doesn't check for dots.
     */
    private boolean simpleContainsKey(String key, boolean checkParent) {
        boolean result = super.containsKey(key);
        if (result == false && checkParent && parent != null) {
            result = parent.simpleContainsKey(key, true);
        }
        return result;
    }

    /**
     * Like the containsKey of HashMap.
     * 
     * @param key          The key to search
     * @param checkParent  If this is false, it will only look in the current Container (and below).
     */

    public boolean containsKey(String key, boolean checkParent) throws JspTagException {
        Pair p = getPair(key, checkParent);
        if (p == null) {
            return simpleContainsKey(key, checkParent);
        } else {
            return p.context.containsKey(p.restKey, ! p.wentDown);
        }                
    }
    public boolean containsKey(String key) throws JspTagException {
        return containsKey(key, true);
    }


    /**
     * Like get, but does not try to search dots, because you know already that there aren't.
     */
    private Object simpleGet(String key, boolean checkParent) { // already sure that there is no dot. 
        Object result =  super.get(key);
        if (result == null && checkParent && parent != null) {
            return parent.simpleGet(key, true);
        } 
        return result;
    }

    /**
     * Like get, but you can explicity indicate if to search 'parent' Contextes as well
     */

    public Object get(String key, boolean checkParent) throws JspTagException {
        Pair p = getPair(key, checkParent);
        if (p == null) {
            return simpleGet(key, checkParent);
        } else {     
            return p.context.get(p.restKey, ! p.wentDown);
        }
    }

    public Object get(String key) throws JspTagException {
        return get(key, true);
    }

    public Set keySet() {
        HashSet result = new HashSet(super.keySet());
        if (parent != null) {
            result.addAll(parent.keySet());
        }
        return result;
    }

    /**
     * Container class, to store results of 'getPair' function.
     *
     */

    private class Pair {
        ContextContainer context;
        String           restKey;
        boolean          wentDown;
        Pair(ContextContainer c, String k, boolean w) {
            context = c; restKey = k; wentDown = w;
        }
    }

}
