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
 * @version $Id: ActionRepository.java,v 1.5 2007-07-26 22:04:23 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class ActionRepository extends Configurable {

    protected static ActionRepository bootstrap = new MemoryActionRepository();
    public static final ActionRepository getInstance() {
        if (bootstrap != null) {
            return bootstrap;
        } else {
            return org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop().getActionRepository();
        }
    }

    public abstract void add(Action a);

    public abstract Action get(String name);

    public abstract Collection<Action> getActions();
}
