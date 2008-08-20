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
 * @version $Id: MemoryActionRepository.java,v 1.8 2008-08-20 17:07:17 michiel Exp $
 * @since MMBase-1.9
 */
public class MemoryActionRepository extends ActionRepository {
    private static final Logger log = Logging.getLoggerInstance(MemoryActionRepository.class);

    private final Map<String, Map<String, Action>> store = new HashMap<String, Map<String, Action>>();

    public MemoryActionRepository() {
    }

    public void load() {
    }

    public void add(Action a) {
        log.debug("Adding " + a + " to " + this);
        Map<String, Action> map = store.get(a.getNameSpace());
        if (map == null) {
            map = new HashMap<String, Action>();
            store.put(a.getNameSpace(), map);
        }
        map.put(a.getName(), a);
    }
    public Map<String, Action> get(String nameSpace) {
        Map<String, Action> map = store.get(nameSpace);
        if (map == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(map);
        }
    }

    public Action get(String nameSpace, String name) {
        return get(nameSpace).get(name);
    }

    public Collection<Map<String, Action>> getActions() {
        return store.values();
    }

}
