/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

/**
 * The implemention of 'context' is pluggable, and should be returned by {@link
 * Verify#getContextProvider}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextProvider.java,v 1.4 2009-01-05 10:27:25 michiel Exp $
 * MMBase-1.9.1
 */
public interface ContextProvider {


    Collection<NodeSearchQuery> getContextQueries();

    void setContext(User user, MMObjectNode node, String context);

    String getContext(MMObjectNode node);

    MMObjectNode getContextNode(MMObjectNode node);

    MMObjectNode getContextNode(String context);

    Set<String> getPossibleContexts(User user, MMObjectNode node)  throws SecurityException;

    Set<String> getPossibleContexts(User user)  throws SecurityException;

    boolean mayDo(User user, MMObjectNode nodeId, Operation operation) throws SecurityException;

    boolean mayDoOnContext(MMObjectNode userNode, MMObjectNode contextNode, Operation operation, boolean checkOwnRights);

    /**
     * Whether, or not,  the user  is allowed to grant the security operation to the group or user on the context
     * node.
     */
    boolean mayGrant(User user,  MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);
    /**
     * Whether, or not,  the user  is allowed to revoke the security operation to the group or user on the context
     * node.
     */
    boolean mayRevoke(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);

    boolean grant(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);

    boolean revoke(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);


    Authorization.QueryCheck check(User userContext, org.mmbase.bridge.Query query, Operation operation);

    static class AllowingContexts {
        SortedSet<String> contexts;
        boolean inverse;
        AllowingContexts(SortedSet<String> c, boolean i) {
            contexts = c;
            inverse = i;
        }
        public String toString() {
            return (inverse ? "NOT IN " : "IN ") + contexts;
        }

    }


}
