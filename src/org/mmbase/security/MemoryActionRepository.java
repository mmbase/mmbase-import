/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import java.util.*;

/**
 * @author Michiel Meeuwissen
 * @version $Id: MemoryActionRepository.java,v 1.1 2007-07-25 06:47:11 michiel Exp $
 * @since MMBase-1.9
 */
public class MemoryActionRepository extends ActionRepository {
    
    private final Map<String, Action> store = new HashMap<String, Action>();

    public MemoryActionRepository() {
    }
    
    public void load() {
    }

    public void addAction(Action a) {
        store.put(a.getName(), a);
    }

    public Action getAction(String name) {
        return store.get(name);
    }
}
