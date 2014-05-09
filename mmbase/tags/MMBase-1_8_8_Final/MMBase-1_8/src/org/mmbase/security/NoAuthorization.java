/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.*;

/**
 * This class is used when no authorization is configured. Everything is allowed.
 *
 * @author Eduard Witteveen
 * @version $Id$
 */
final public class NoAuthorization extends Authorization {

    // This defined the 'context' used everywhere where one is needed.
    private static final String EVERYBODY = "everybody";
    private static final Set    possibleContexts = Collections.unmodifiableSet(new HashSet(Arrays.asList( new String[]{EVERYBODY, NoAuthentication.userContext.getOwnerField()})));

    /**
     *	This method does nothing
     */
    protected void load() {
    }

    /**
     *	This method does nothing
     */
    public void create(UserContext user, int nodeid) {
    }

    /**
     *	This method does nothing
     */
    public void update(UserContext user, int nodeid) {
    }

    /**
     * This method does nothing
     */
    public void remove(UserContext user, int nodeid) {
    }

    /**
     * No authorization means that everyting is allowed
     * @return true
     */
    public boolean check(UserContext user, int nodeid, Operation operation) {
        return true;
    }

    /**
     * This method does nothing
     */
    public void verify(UserContext user, int nodeid, Operation operation) throws org.mmbase.security.SecurityException {
    }

    /**
     * No authorization means that everyting is allowed
     * @return true
     */
    public boolean check(UserContext user, int nodeid, int srcNodeid, int dstNodeid, Operation operation) {
        return true;
    }

    /**
     * This method does nothing
     */
    public void verify(UserContext user, int nodeid, int srcNodeid, int dstNodeid, Operation operation) throws SecurityException {
    }


    /**
     * This method does nothing, except from giving a specified string back
     */
    public String getContext(UserContext user, int nodeid) throws SecurityException {
        return EVERYBODY;
    }

    /**
     * Since this is not authorization, we simply allow every change of context.
     */
    public void setContext(UserContext user, int nodeid, String context) throws SecurityException {        
        //if(!EVERYBODY.equals(context)) throw new SecurityException("unknown context");
    }

    /**
     * This method does nothing, except from returning a dummy value
     */
    public Set getPossibleContexts(UserContext user, int nodeid) throws SecurityException {
        return possibleContexts;
    }

    public QueryCheck check(UserContext user, org.mmbase.bridge.Query query, Operation operation) {
        return COMPLETE_CHECK;
    }

}
