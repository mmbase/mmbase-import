/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import java.util.Set;
import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @version $Id: Verify.java,v 1.1 2003-05-22 17:14:03 michiel Exp $
 */
public class Verify extends Authorization {
    private static Logger    log = Logging.getLoggerInstance(Verify.class.getName());

    // javadoc inherited
    protected void load() {
    }

    // javadoc inherited
    public void create(UserContext usercontext, int i) {
        User user = (User) usercontext;
        MMObjectNode mmobjectnode = Contexts.getBuilder().setContext(user, i, user.getDefaultContext());
    }

    // javadoc inherited
    public void update(UserContext usercontext, int i)  {
    }


    // javadoc inherited
    public void remove(UserContext usercontext, int i)  {
    }

    // javadoc inherited
    public boolean check(UserContext usercontext, int i, Operation operation)  {
        return Contexts.getBuilder().mayDo((User)usercontext, operation, i);
    }

    // javadoc inherited
    public boolean check(UserContext usercontext, int i, int j, int k, Operation operation)   {
        //log.debug("check if operation: " + operation + " is valid for: " + usercontext + " for node with number # " + i + "(between 2 nodes..)");
        return Contexts.getBuilder().mayDo((User)usercontext, operation, i);
    }

    // javadoc inherited
    public String getContext(UserContext usercontext, int i) throws org.mmbase.security.SecurityException {
        //log.debug("check if we may read the node with # " + i + " nodeid?");
        return Contexts.getBuilder().getContext((User)usercontext, i);
    }


    // javadoc inherited
    public void setContext(UserContext usercontext, int i, String s) throws org.mmbase.security.SecurityException {
        //log.debug("[node #" + i + "] changed to context: " + s + " by [" + usercontext.getIdentifier() + "]");
        Contexts.getBuilder().setContext((User)usercontext, i, s);
    }

    // javadoc inherited
    public Set getPossibleContexts(UserContext usercontext, int i)  throws org.mmbase.security.SecurityException {
        return Contexts.getBuilder().getPossibleContexts((User)usercontext, i);
    }
}
