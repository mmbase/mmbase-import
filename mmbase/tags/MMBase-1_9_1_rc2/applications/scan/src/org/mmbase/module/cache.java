/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;

import org.mmbase.util.LRUHashtable;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Simple file cache system that can be used by any servlet
 *
 * @application cache [utility, implementation]
 * @javadoc
 * @move org.mmbase.cache.implementation
 * @rename Cache
 * @author  $Author: nklasens $
 * @version $Id$
 */
public class cache extends Module implements cacheInterface {

    // logging
    private static Logger log = Logging.getLoggerInstance(cache.class.getName());

    /**
     * @javadoc
     */
    private int MaxLines=1000;
    /**
     * @javadoc
     */
    private int MaxSize=100*1024;
    /**
     * @javadoc
     */
    private boolean active=true;
    /**
     * @javadoc
     * @scope private
     */
    boolean state_up = false;
    /**
     * @javadoc
     * @scope private
     */
    int hits,miss;
    /**
     * @javadoc
     * @scope private
     */
    LRUHashtable<Object, Object> lines = new LRUHashtable<Object, Object>( MaxLines );

    /**
     * Simple file cache system that can be used by any servlet
     */
    public cache() {
    }

    /** @duplicate */
    public void onload() {
    }

    /**
     * @javadoc
     */
    public void reload() {
        readParams();
        if( MaxLines > 0 ) lines = new LRUHashtable<Object, Object>( MaxLines );
    }

    /** @duplicate */
    public void shutdown() {
    }

    /**
     * Old interface to the inner table, will be removed soon
     * @deprecated-now direct access to lines seems undesirable and is implementation-dependent
     */
    public LRUHashtable<Object, Object> lines() {
        return lines;
    }

    /**
     * Try to get a cacheline from cache, returns null if not found
     * @javadoc
     */
    public cacheline get(Object wanted) {
        if (!active) return(null);
        cacheline o=(cacheline)lines.get(wanted);
        if (o==null) {
            //if (log.isDebugEnabled()) log.debug("WOW CACHE MIS = "+wanted);
            miss++;
        } else {
            //if (log.isDebugEnabled()) log.debug("WOW CACHE HIT = "+wanted);
            hits++;
        }
        return o;
    }

    /**
     * Try to put a cacheline in cache, returns old one if available
     * In all other cases returns null.
     * @javadoc
     */
    public cacheline put(Object key,Object value) {
        if (!active) return(null);
        // check if there is still room in the cache
        // there is room so look at the cacheline and check size
        cacheline line=(cacheline)value;
        // if size is to big ignore the entry
        if (line.filesize<MaxSize) {
            // cacheline is oke place it in cache
            return (cacheline)lines.put(key,value);
        } else {
            // cacheline to big
            return null;
        }
    }

    /**
     * Clear the whole cache in one go
     */
    public boolean clear() {
        lines.clear();
        return false;
    }

    /**
     * Remove the entry identified by key from the cache
     * @javadoc
     */
    public cacheline remove(Object key) {
        return (cacheline)lines.remove(key);
    }

    /**
     * @javadoc
     */
    public void init() {
        if (!state_up) {
            state_up=true;
        }
        readParams();
        if( MaxLines > 0 ) lines = new LRUHashtable<Object, Object>( MaxLines );
    }

    /**
     * @javadoc
     */
    public void unload() {
    }

    /**
     * @javadoc
     */
    public Map<String, String> getStates() {
        setState("Hits",""+hits);
        setState("Misses",""+miss);
        if (hits!=0 && miss!=0) {
            setState("Cache hits %",""+((hits+miss)*100)/hits);
            setState("Cache misses %",""+((hits+miss)*100)/miss);
        }
        setState("Number cachelines",""+lines.size());
        cacheline line;
        int size=0;
        for (Enumeration<Object> t=lines.elements();t.hasMoreElements();) {
            line=(cacheline)t.nextElement();
            size+=line.filesize;
        }
        setState("Cache Size (in kb)",""+(size+1)/1024);
        return super.getStates();
    }

    public Map<String, String> state() {
        return getStates();
    }

    /**
     * @javadoc
     */
    void readParams() {
        String tmp = null;
        try
        {
            tmp=getInitParameter("MaxLines");
            if (tmp!=null) MaxLines=Integer.parseInt(tmp);
            tmp=getInitParameter("MaxSize");
            if (tmp!=null) MaxSize=Integer.parseInt(tmp)*1024;
            tmp=getInitParameter("Active");
        } catch (NumberFormatException e ) {
            log.error("readParams(): " + e ) ;
        }

        if (tmp!=null && (tmp.equals("yes") || tmp.equals("Yes"))) {
            active=true;
        } else {
            active=false;

        }
    }

    /**
     * @duplicate Should be handled with a standard method in Module that
     *            uses the configuration files.
     */
    public String getModuleInfo() {
        return "this module provides cache function for http requests";
    }
}
