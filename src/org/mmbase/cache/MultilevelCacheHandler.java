/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.LRUHashtable;
import org.mmbase.util.StringTagger;


/**
 * This object handles cache multilevel tag cache requests. it removed
 * invalid lines adding listeners to builders used in the multilevel
 * query's

 *
 * @rename MultiLevelCache
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: MultilevelCacheHandler.java,v 1.7 2002-03-30 16:32:43 michiel Exp $
 */
public class MultilevelCacheHandler extends Cache {

    // listeners, keeps a list of entry's per objectmanager
    private Hashtable listeners = new Hashtable();

    // reference to main MMBase class
    private static MMBase mmb;


    private static MultilevelCacheHandler multiCache;

    public static MultilevelCacheHandler getCache() {
        return multiCache;
    }

    static {
        multiCache = new MultilevelCacheHandler(300);
        putCache(multiCache);
    }

    public String getName() {
        return "MultilevelCache";
    }
    public String getDescription() {
        return "Multi-level List Results";
    }

    /**
     * Creates the MultiLevel  Cache.
     */
    private MultilevelCacheHandler(int size) {
        super(size);
    }

    /**
     * @javadoc
     * @todo tagger passed should be the Key, hash should be calculated by entry
     *       types should be a List
     */
    public Object put(Object hash,Object o,Vector types,StringTagger tagger) {
        MultilevelCacheEntry n=new MultilevelCacheEntry(this,hash,o,tagger);
        addListeners(types,n);
        return put(hash,n);
    }

    /**
     * @javadoc
     * @todo pass this in constructor instead
     */
    public static void setMMBase(MMBase m) {
        mmb=m;
    }

    /**
     * @javadoc
     * @badliteral default size of cache should be configurable
     * @todo needs MMbase parameter for initialization
     * @deprecated use getCache
     */
    public static Cache getCache(String name) {
        return getCache();
    }


    /**
     * @javadoc
     */
    public synchronized Object get(Object key) {
        // get the wrapper but return the
        // object
        MultilevelCacheEntry n=(MultilevelCacheEntry)super.get(key);
        if (n==null) {
            return null;
        } else {
            return n.getObject();
        }
    }

    /**
     * @javadoc
     * @todo types should be List
     */
    private void addListeners(Vector types,MultilevelCacheEntry n) {
        Enumeration e=types.elements();
        while (e.hasMoreElements()) {
            String type=(String)e.nextElement();
            char lastchar=type.charAt(type.length()-1);
            if (lastchar>='1' && lastchar<='9') {
                type=type.substring(0,type.length()-1);
            }
            MultilevelSubscribeNode l = (MultilevelSubscribeNode)listeners.get(type);
            if (l == null) {
                l = new MultilevelSubscribeNode(mmb,type);
                listeners.put(type,l);
            }
            l.addCacheEntry(n);
        }
    }

    /**
     * intercept remove of LRU to make sure
     * we remove from all Observers first
     * the clear will call the real remove
     * @javadoc
     * @todo code itself should remove ?
     */
    public synchronized Object remove(Object key) {
        MultilevelCacheEntry n=(MultilevelCacheEntry)super.get(key);
        n.clear();
        return n;
    }

    /**
     * call the real remove in the LRU
     * @javadoc
     * @scope private
     */
    public synchronized void callbackRemove(Object key) {
        super.remove(key);
    }

    /**
     * Create a hashcode for all the parameters in a multilevel request
     * coming from the MMCI.
     * @todo possibly this should be moved to MultiLevelCacheEntry?
     */
    public Integer calcHashMultiLevel(StringTagger tagger) {
        int hash=1;
        Object obj;

        obj=tagger.Values("TYPES");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("SORTED");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("DIR");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Value("WHERE");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("FIELDS");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("NODES");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Value("DISTINCT");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Value("SEARCH");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());

        return new Integer(hash);
    }

}
