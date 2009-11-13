/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import org.mmbase.module.core.*;
import org.mmbase.util.*;
import java.util.*;


/**
 * This object handles cache multilevel tag cache requests.
 *
 * @javadoc
 * @application SCAN
 * @deprecated use org.mmbase.cache.MultilevelCacheHandler
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class MultilevelCacheHandler extends LRUHashtable {

        private Hashtable<String, MultilevelSubscribeNode> listeners = new Hashtable<String, MultilevelSubscribeNode>();
        private MMBase mmb;

        public MultilevelCacheHandler(MMBase mmb,int size) {
                super(size);
                this.mmb=mmb;

        }

        public Object put(Object hash,Object o,Vector types,StringTagger tagger) {
                MultilevelCacheEntry n=new MultilevelCacheEntry(this,hash,o,tagger);
                addListeners(types,n);
                return(put(hash,n));
        }


        public synchronized Object get(Object key) {
                // get the wrapper but return the
                // object
                MultilevelCacheEntry n=(MultilevelCacheEntry)super.get(key);
                if (n==null) {
                        return(null);
                } else {
                        return(n.getObject());
                }
        }

        private void addListeners(Vector types,MultilevelCacheEntry n) {
                Enumeration e=types.elements();
                while (e.hasMoreElements()) {
                        String type=(String)e.nextElement();
                        char lastchar=type.charAt(type.length()-1);
                        if (lastchar>='1' && lastchar<='9') {
                                type=type.substring(0,type.length()-1);
                        }
                        MultilevelSubscribeNode l=listeners.get(type);
                        if (l==null) {
                                l=new MultilevelSubscribeNode(mmb,type);
                                listeners.put(type,l);
                        }
                        l.addCacheEntry(n);
                }
        }


        // intercept remove of LRU to make sure
        // we remove from all Observers first
        // the clear will call the real remove
        public synchronized Object remove(Object key) {
                MultilevelCacheEntry n=(MultilevelCacheEntry)super.get(key);
                n.clear();
                return(n);
        }

        // call the real remove in the LRU
        public synchronized void callbackRemove(Object key) {
                super.remove(key);
        }
}
