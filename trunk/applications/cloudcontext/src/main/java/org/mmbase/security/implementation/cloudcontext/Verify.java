/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.bridge.Query;
import org.mmbase.module.core.MMObjectNode;
import java.util.*;
import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Implementation of Authorization. Most implementation is delegated to the Contexts builder.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see    org.mmbase.security.implementation.cloudcontext.builders.Contexts
 */
public class Verify extends Authorization {

    private static final Logger log = Logging.getLoggerInstance(Verify.class);

    protected void load() {
    }


    /**
     * @since MMBase-1.9.1
     */
    public final static Verify getInstance() {
        return (Verify) org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop().getAuthorization();
    }

    /**
     * @since MMBase-1.9.1
     */
    public  ContextProvider getContextProvider() {
        return Contexts.getBuilder().getProvider();
    }


    @Override public void create(UserContext userContext, int nodeId) {
        User user = (User) userContext;
        // odd, getOwnerField is called in BasicNodeManager yet, so I wonder when this is called.
        setContext(userContext, nodeId, user.getOwnerField());
    }

    @Override
    public void update(UserContext userContext, int nodeId)  {
    }


    @Override
    public void remove(UserContext userContext, int nodeId)  {
    }

    @Override
    public boolean check(UserContext userContext, int nodeId, Operation operation)  {
        return getContextProvider().mayDo((User) userContext, getNode(nodeId, true), operation);
    }

    @Override public boolean check(UserContext userContext, int nodeId, int sourceNodeId, int destinationNodeId, Operation operation) {
        // admin bypasses security system
        if (userContext.getRank().getInt() >= Rank.ADMIN_INT) {
            return true;
        }
        return check(userContext, nodeId, operation);
    }



    @Override public String getContext(UserContext userContext, int nodeId) throws org.mmbase.security.SecurityException {
        // userContext ignored
        MMObjectNode contextNode = getContextNode(nodeId, true);
        if (contextNode == null) {
            log.warn("No context node found for node with id " + nodeId);
            return null;
        } else {
            log.debug("Found context node for node with id " + nodeId + " " + contextNode.getNumber());
        }
        return getContextProvider().getContextName(contextNode);
    }

    @Override public void setContext(UserContext user, int nodeId, String context) throws org.mmbase.security.SecurityException {
        getContextProvider().setContext((User) user, getNode(nodeId, true), context);
    }

    @Override public Set<String> getPossibleContexts(UserContext userContext, int nodeId)  throws org.mmbase.security.SecurityException {
        return getContextProvider().getPossibleContexts((User) userContext, getNode(nodeId, true));
    }

    @Override public Set<String> getPossibleContexts(UserContext userContext) throws org.mmbase.security.SecurityException {
        return getContextProvider().getPossibleContexts((User) userContext);
    }

    @Override
    public QueryCheck check(UserContext userContext, Query query, Operation operation) {
        return getContextProvider().check((User) userContext, query, operation);

    }

    @Override public boolean check(UserContext user, Action ac, Parameters parameters) {
        return Actions.getBuilder().check((User) user, ac, parameters);
    }



    /**
     * For a certain node, returns the node representing its 'context'.
     * @param nodeId
     * @param exception If <code>true</code> throw a security exception if a node which such a
     * number could not be found. Otherwise, in that case only log a warning.
     */
    protected MMObjectNode getContextNode(int nodeId, boolean exception) {
        MMObjectNode node = getNode(nodeId, exception);
        MMObjectNode contextNode = getContextProvider().getContextNode(node);
        if (log.isDebugEnabled()) {
            log.debug("Found for " + nodeId + ":" + node.getBuilder().getTableName() + ":" + node.getNumber() + " -> " + contextNode.getBuilder().getTableName() + ":" + contextNode.getNumber());
        }
        return contextNode;
    }

    /**
     * For a certain node number , return the MMObjectNode representing it
     * @param nodeId
     * @param exception If <code>true</code> throw a security exception if a node which such a
     * number could not be found. Otherwise, in that case only log a warning.
     */
    protected MMObjectNode getNode(int nodeId, boolean exception) {
        MMObjectNode node =  getContextProvider().getContextQueries().iterator().next().getBuilder().getNode(nodeId);
        if (node == null) {
            if (exception) {
                throw new SecurityException("node #" + nodeId + " not found");
            } else {
                log.warn("node #" + nodeId + " not found");
            }
        }
        return node;
    }




}
