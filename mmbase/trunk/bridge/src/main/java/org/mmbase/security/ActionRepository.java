/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import java.util.*;
import org.w3c.dom.*;
import org.mmbase.util.xml.Instantiator;

import org.mmbase.util.logging.*;

/**
 * The defined 'actions' are maintained by the {@link Action} repository. The security implementation can
 * decide how to persist actions and how to connect rights to it, and hence is responsible for
 * implementation this.
 *
 * Every action is identified by two
 * strings: a <em>namespace</em> (which may be <code>null</code>) and it's name. Namespaces are likely to correspond with {@link
 * org.mmbase.framework.Component}s.
 *
 * <pre>
    Action action = ActionRepository.getInstance().get("core", "viewsource");
    Parameters params = action.createParameters();
    // perhaps supply some parameters, not needed in this case (it may be null then too).
    if (cloud.check(cloud.getUser(), action, parameters)) { // checks if current user may view the source
       /// show the source..
       ...
    } else {
       return "not allowed to view the source";
    }
    </pre>
 *
 * @see Action
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public abstract class ActionRepository extends Configurable {
    private static final Logger log = Logging.getLoggerInstance(ActionRepository.class);

    protected static ActionRepository bootstrap = new MemoryActionRepository();

    /**
     * Returns the ActionRepository associated with the current MMBase's {@link MMBaseCop}. Or if
     * that doesn't provide one, returns a {@link MemoryActionRepository}.
     */
    public static ActionRepository getInstance() {
        if (bootstrap != null) {
            return bootstrap;
        } else {
            MMBaseCop mmbc = null; // TODO org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop();
            if (mmbc != null) {
                return mmbc.getActionRepository();
            } else {
                return new MemoryActionRepository();
            }
        }
    }

    /**
     * @since MMBase-1.9.2
     */
    public void fillFromXml(Element el, String name) {
        NodeList actionElements = el.getElementsByTagName("action");
        for (int i = 0; i < actionElements.getLength(); i++) {
            try {
                Element element = (Element) actionElements.item(i);
                String actionName = element.getAttribute("name");
                String rank = element.getAttribute("rank");
                Object c = Instantiator.getInstanceWithSubElement(element);
                Action a;
                if (c != null) {
                    if (! "".equals(rank)) {
                        log.warn("Rank attribute ignored");
                    }
                    a = new Action(name, actionName, (ActionChecker) c);
                } else {
                    if ("".equals(rank)) { rank = "basic user"; }
                    a = new Action(name, actionName, new ActionChecker.Rank(rank));
                }
                a.getDescription().fillFromXml("description", element);
                log.service("Registering action " + a);
                add(a);
            } catch (Exception e) {
                log.error(e);
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
