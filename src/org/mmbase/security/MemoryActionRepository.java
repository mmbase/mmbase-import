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
 * @author Michiel Meeuwissen
 * @version $Id: MemoryActionRepository.java,v 1.5 2007-07-26 22:04:23 michiel Exp $
 * @since MMBase-1.9
 */
public class MemoryActionRepository extends ActionRepository {
    private static final Logger log = Logging.getLoggerInstance(MMBaseCop.class);

    private final Map<String, Action> store = new HashMap<String, Action>();

    public MemoryActionRepository() {
    }

    public void load() {
    }

    public void add(Action a) {
        log.info("Adding " + a + " to " + this);
        store.put(a.getName(), a);
    }

    public Action get(String name) {
        return store.get(name);
    }
    public Collection<Action> getActions() {
        return store.values();
    }

}
