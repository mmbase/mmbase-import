/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 * An action is something which an authenticated user may want to do, but which is not directly
 * associated with MMBase nodes. Actions are e.g. provided by components (and can be added to
 * component XML's).
 *
 * @author Michiel Meeuwissen
 * @version $Id: Action.java,v 1.2 2007-07-25 07:17:40 michiel Exp $
 * @since MMBase-1.9
 */
public class Action implements java.io.Serializable {
    protected final String name;
    protected final ActionChecker defaultChecker;
    public Action(String n, ActionChecker c) {
        name = n;
        defaultChecker = c;
    }
    /**
     * Every action needs to do a proposal on how to check it. The security implementation may
     * override this. But since components can freely define new actions, which may not be
     * anticipated by  the authorization implementation, the action itself must provide some basic
     * checker (e.g. an instance of {@link ActionChecker.Rank}. 
     */
    public ActionChecker getDefault() {
        return defaultChecker;
    }
    public String getName() {
        return name;
    }
    
}
