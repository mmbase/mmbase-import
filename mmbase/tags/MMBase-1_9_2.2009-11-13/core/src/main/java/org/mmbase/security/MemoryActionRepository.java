/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import java.util.*;

import org.mmbase.util.ChainedIterator;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is the most simple way to store 'actions', namely straightforwardly in memory. Config files may fill
 * this repository on startup.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class MemoryActionRepository extends ActionRepository {
    private static final Logger log = Logging.getLoggerInstance(MemoryActionRepository.class);

    private final Map<String, Map<String, Action>> store = new HashMap<String, Map<String, Action>>();

    public MemoryActionRepository() {
    }

    @Override public void load() {
    }

    @Override public void add(Action a) {
        log.debug("Adding " + a + " to " + this);
        Map<String, Action> map = store.get(a.getNameSpace());
        if (map == null) {
            map = new HashMap<String, Action>();
            store.put(a.getNameSpace(), map);
        }
        map.put(a.getName(), a);
    }
    @Override public Map<String, Action> get(String nameSpace) {
        Map<String, Action> map = store.get(nameSpace);
        if (map == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(map);
        }
    }

    @Override public Collection<Action> getActions() {
        return new AbstractCollection<Action>() {
            int size = -1;
            public int size() {
                if (size == -1) {
                    size = 0;
                    for (Map<String, Action> ns : store.values()) {
                        size += ns.size();
                    }
                }
                return size;
            }
            public Iterator<Action> iterator() {
                ChainedIterator<Action> i = new ChainedIterator<Action>();
                for (Map<String, Action> ns : store.values()) {
                    i.addIterator(ns.values().iterator());
                }
                return i;
            }
        };
    }

}
