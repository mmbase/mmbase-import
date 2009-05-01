/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import java.util.*;

/**
 * The defined 'actions' are maintained by the 'action' repository. The security implementation can
 * decide how to persist actions and how to connect rights to it. Every action is identified by two
 * strings: a <em>namespace</em> (which may be <code>null</code>) and it's name. Namespaces are likely to correspond with {@link
 * org.mmbase.framework.Component}s.
 *
 * @see Action
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public abstract class ActionRepository extends Configurable {

    protected static ActionRepository bootstrap = new MemoryActionRepository();

    public static final ActionRepository getInstance() {
        if (bootstrap != null) {
            return bootstrap;
        } else {
            MMBaseCop mmbc = org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop();
            if (mmbc != null) {
                return mmbc.getActionRepository();
            } else {
                return new MemoryActionRepository();
            }
        }
    }


    /**
     * Adds the action to the repository using {@link Action#getNameSpace()} and {@link
     * Action#getName()}.
     */
    public abstract void add(Action a);


    /**
     * All actions of a certain namespace.
     */
    public abstract Map<String, Action> get(String nameSpace);

    /**
     * Shortcut for {@link #get(String)}.{@link java.util.Map#get}.
     */
    public final Action get(String nameSpace, String name) {
        return get(nameSpace).get(name);
    }

    /**
     * All actions managed by this repository
     */
    public abstract Collection<Action> getActions();

}
