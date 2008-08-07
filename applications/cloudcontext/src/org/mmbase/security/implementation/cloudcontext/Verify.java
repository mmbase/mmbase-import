/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.bridge.Query;
import java.util.Set;
import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.security.*;
import org.mmbase.util.functions.*;

/**
 * Implementation of Authorization. Most implementation is delegated to the Contexts builder.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Verify.java,v 1.14 2008-08-07 20:01:24 michiel Exp $
 * @see    org.mmbase.security.implementation.cloudcontext.builders.Contexts
 */
public class Verify extends Authorization {

    protected void load() {
    }


    @Override
    public void create(UserContext userContext, int nodeId) {
        User user = (User) userContext;
        // odd, getOwnerField is called in BasicNodeManager yet, so I wonder when this is called.
        Contexts.getBuilder().setContext(user, nodeId, user.getOwnerField());
    }

    @Override
    public void update(UserContext userContext, int nodeId)  {
    }


    @Override
    public void remove(UserContext userContext, int nodeId)  {
    }

    @Override
    public boolean check(UserContext userContext, int nodeId, Operation operation)  {
        return Contexts.getBuilder().mayDo((User) userContext, nodeId, operation);
    }

    @Override
    public boolean check(UserContext userContext, int nodeId, int sourceNodeId, int destinationNodeId, Operation operation) {
        //log.debug("check if operation: " + operation + " is valid for: " + usercontext + " for node with number # " + i + "(between 2 nodes..)");
        return Contexts.getBuilder().mayDo((User) userContext, nodeId, sourceNodeId, destinationNodeId, operation);
    }

    @Override
    public String getContext(UserContext userContext, int nodeId) throws org.mmbase.security.SecurityException {
        //log.debug("check if we may read the node with # " + i + " nodeid?");
        return Contexts.getBuilder().getContext((User) userContext, nodeId);
    }

    @Override
    public void setContext(UserContext userContext, int nodeId, String context) throws org.mmbase.security.SecurityException {
        //log.debug("[node #" + i + "] changed to context: " + s + " by [" + usercontext.getIdentifier() + "]");
        Contexts.getBuilder().setContext((User) userContext, nodeId, context);
    }

    @Override
    public Set<String> getPossibleContexts(UserContext userContext, int nodeId)  throws org.mmbase.security.SecurityException {
        return Contexts.getBuilder().getPossibleContexts((User) userContext, nodeId);
    }
    @Override
    public Set<String> getPossibleContexts(UserContext userContext) throws org.mmbase.security.SecurityException {
        return Contexts.getBuilder().getPossibleContexts((User) userContext);
    }

    @Override
    public QueryCheck check(UserContext userContext, Query query, Operation operation) {
        return Contexts.getBuilder().check((User) userContext, query, operation);

    }

    @Override
    public boolean check(UserContext user, Action ac, Parameters parameters) {
        return Actions.getBuilder().check((User) user, ac, parameters);
    }
}
