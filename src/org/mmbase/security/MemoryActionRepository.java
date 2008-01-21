/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is the most simple way to store 'actions', namely simply in memory. Config files may fill
 * this repository on startup.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MemoryActionRepository.java,v 1.6 2008-01-21 15:25:28 michiel Exp $
 * @since MMBase-1.9
 */
public class MemoryActionRepository extends ActionRepository {
    private static final Logger log = Logging.getLoggerInstance(MMBaseCop.class);

    private static class Key  {
        private final String nameSpace;
        private final String name;
        Key(String ns, String n) {
            nameSpace = ns;
            name = n;
        }
        public int hashCode() {
            return name.hashCode();
        }
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key k = (Key) o;
                return k.name.equals(name) && (k.nameSpace == null ? nameSpace == null : k.nameSpace.equals(nameSpace));
            } else {
                return false;
            }
        }
    }

    private final Map<Key, Action> store = new HashMap<Key, Action>();

    public MemoryActionRepository() {
    }

    public void load() {
    }

    public void add(Action a) {
        log.info("Adding " + a + " to " + this);
        store.put(new Key(a.getNameSpace(), a.getName()), a);
    }

    public Action get(String nameSpace, String name) {
        return store.get(new Key(nameSpace, name));
    }
    public Collection<Action> getActions() {
        return store.values();
    }

}
