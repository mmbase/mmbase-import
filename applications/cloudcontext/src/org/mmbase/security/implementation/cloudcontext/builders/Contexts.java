/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.security.SecurityException;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.cache.Cache;
import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Representation of a 'context', which can be read as a valid value
 * of the 'owner' field of any object in MMBase. Rights are
 * distributed using this thing. This is part of cloud context
 * security, so the 'context' values need to be present in the cloud.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Contexts.java,v 1.2 2003-05-23 12:05:13 michiel Exp $
 */
public class Contexts extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Contexts.class.getName());
    private boolean readall = true;

    protected static Cache contextCache = new Cache(30) {
            public String getName()        { return "ContextCache"; }
            public String getDescription() { return "Links owner field to Contexts MMObjectNodes"; }
        };


    /**
     * @javadoc
     */
    static final String DEFAULT_CONTEXT = "admin";

    /**
     * @javadoc
     */
    public boolean init() {
        String s = (String) getInitParameters().get("readall");
        readall = "true".equals(s);

        contextCache.putCache();
        CacheInvalidator.getInstance().addCache(contextCache);
        mmb.addLocalObserver(getTableName(), CacheInvalidator.getInstance());
        mmb.addRemoteObserver(getTableName(), CacheInvalidator.getInstance());

        return super.init();
    }

    /**
     * @javadoc
     */
    public static Contexts getBuilder() {
        return (Contexts) MMBase.getMMBase().getBuilder("mmbasecontexts");
    }

    /**
     * @javadoc
     */
    public boolean mayDo(User user, Operation operation, int nodeId) throws SecurityException {
        // admin bypasses security system
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            log.debug("admin may do everything");
            return true;
        }


        // retrieve the node
        MMObjectNode node = getNode(nodeId);

        if (node == null) {
            throw new SecurityException("node #" + nodeId + " not found");
        }

        if (readall && operation == Operation.READ) {
            log.debug("Allowing all read operiations, so read on " + nodeId + " is permitted");
            return true;
        }

        MMObjectBuilder builder = node.getBuilder();
        // if this is a group node, then you may do anything on it, if you are member of the group.
        // should that be?
        if (builder instanceof Groups) {
            return Groups.getBuilder().contains(node, user);
        }

        // when it is our user node, and you are this user, you may do anything on it (change password)
        if (builder instanceof Users) {
            if (user.equals(node) && (operation == Operation.READ || operation == Operation.WRITE)) {
                if (log.isDebugEnabled()) {
                    log.debug("May always " + operation + " on own user node: " + nodeId);
                }
                return true;
            }
        }

        // retrieve all the groups in which the context of this node is.

        MMObjectNode contextNode = getContextNode(node); // the mmbasecontext node associated with this node
        Iterator iter = RightsRel.getBuilder().getGroups(contextNode, operation).iterator();        

        // now checking if this user is in one of these groups.
        while (iter.hasNext()) {
            MMObjectNode group = (MMObjectNode) iter.next();            
            log.trace("checking group " + group);
            if(Groups.getBuilder().contains(group, user)) { 
                if (log.isDebugEnabled()) {
                    log.debug("May " + operation + " on node: " + nodeId + " of context " + contextNode);
                }
                return true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("May not " + operation + " on node: " + nodeId + " of context " + contextNode);
        }
        return false;
    }

    /**
     * @return The MMObjectNode presenting the context of the given node.
     */
    private final MMObjectNode getContextNode(MMObjectNode node)  {
        String s = node.getStringValue("owner");
        
        MMObjectNode context = (MMObjectNode) contextCache.get(s);
        if (context == null) {

            Enumeration enumeration = searchWithWhere(" name = '" + s + "' ");
            if (enumeration.hasMoreElements()) {
                context =  (MMObjectNode) enumeration.nextElement();
            } else {
                // log.warn("context with name '" + s + "' not found, using default: '" + "admin" + "'");
                enumeration = searchWithWhere(" name = 'admin' ");
                if (!enumeration.hasMoreElements()) {
                    throw new SecurityException("no context with name 'admin' defined! This one is needed as default");
                }
                context = (MMObjectNode) enumeration.nextElement();
            }
            contextCache.put(s, context);
        }
        return context;
    }

    /**
     * @javadoc
     */
    public String getContext(User user, int i)  throws SecurityException {
        MMObjectNode node = getNode(i);
        if (node == null) {
            throw new SecurityException("node #" + i + " not found");
        }
        if (node.getBuilder() instanceof Groups) {
            return "unused";
        }
        return getContextNode(node).getStringValue("name");
    }

    /**
     * @javadoc
     */
    public MMObjectNode setContext(User user, int i, String s) throws SecurityException {
        MMObjectNode node = getNode(i);
        if (node == null) {
            throw new SecurityException("node #" + i + " not found");
        }
        if (node.getBuilder() instanceof Groups) { 
            node.setValue("owner", "system");
            node.commit();
            return node;
        }
        if (!getPossibleContexts(user, i).contains(s)) {
            throw new SecurityException("could not set the context to " + s + " for node #" + i + "(context name:" + s + " is not a valid context)");
        }
        node.setValue("owner", s);
        node.commit();
        return node;
    }

    /**
     * @javadoc
     */
    public Set getPossibleContexts(User user, int i) throws SecurityException {
        MMObjectNode node = getNode(i);
        if (node == null) {
            throw new SecurityException("node #" + i + " not found");
        }
        if (node.getBuilder() instanceof Groups) { 
            return new HashSet();
        }
        Enumeration enumeration = search(null); 
        Set hashSet = new HashSet();
        while (enumeration.hasMoreElements()) {
            MMObjectNode node1 = (MMObjectNode) enumeration.nextElement();
            if (mayDo(user, Operation.READ, node1.getNumber())) {
                hashSet.add(node1.getStringValue("name"));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("context with name:" + node1.getStringValue("name") + " could not be added to possible contexes, since we had no read rights");
                }
            }
        }
        return hashSet;
    }

    public String toString(MMObjectNode n) {
        return n.getStringValue("name");
    }

}
