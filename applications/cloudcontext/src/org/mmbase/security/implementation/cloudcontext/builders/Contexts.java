/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.security.SecurityException;
import org.mmbase.security.Authorization;
import java.util.*;
import org.mmbase.bridge.Query;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.cache.Cache;
import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Representation of a 'context', which can be read as a valid value of the 'owner' field of any
 * object in MMBase. Rights are distributed using this thing. This is part of cloud context
 * security, so the 'context' values need to be present in the cloud.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Contexts.java,v 1.14 2003-08-13 10:39:18 michiel Exp $
 * @see    org.mmbase.security.implementation.cloudcontext.Verify; 
 * @see    org.mmbase.security.Authorization; 
 */
public class Contexts extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Contexts.class);

    /**
     *
     * @javadoc
     */
    static final String DEFAULT_CONTEXT = "default";  // default used to be 'admin', but does that make sense?


    static final int DEFAULT_MAX_CONTEXTS_IN_QUERY = 50;


    protected static Cache contextCache = new Cache(30) { // 30 'contexts' (organisations or so)
            public String getName()        { return "CCS:ContextCache"; }
            public String getDescription() { return "Links owner field to Contexts MMObjectNodes"; }
        };


    protected static Cache allowingContextsCache = new Cache(200) { // 200 users.
            public String getName()        { return "CCS:AllowingContextsCache"; }
            public String getDescription() { return "Links user id to a set of contexts"; }
        };
    protected static class OperationsCache extends Cache {
        OperationsCache() {
            super(100);
        }
        public String getName()        { return "CCS:SecurityOperations"; }
        public String getDescription() { return "The groups associated with a security operation";}
        
        public Object put(MMObjectNode context, Operation op, Set groups) {
            return super.put(op.toString() + context.getNumber(), groups);
        }
        public Set get(MMObjectNode context, Operation op) {
            return (Set) super.get(op.toString() + context.getNumber());
        }
        
    };

    protected static OperationsCache operationsCache = new OperationsCache();



    protected static Map  invalidableObjects = new HashMap();

    private boolean readAll = false;
    private int     maxContextsInQuery = DEFAULT_MAX_CONTEXTS_IN_QUERY;


    /**
     * @javadoc
     */
    public boolean init() {
        String s = (String) getInitParameters().get("readall");
        readAll = "true".equals(s);

        s = (String) getInitParameters().get("maxcontextsinquery");
        if (! "".equals(s) && s != null) {
            maxContextsInQuery = Integer.parseInt(s);
        }

        contextCache.putCache();
        allowingContextsCache.putCache();
        operationsCache.putCache();

        CacheInvalidator.getInstance().addCache(operationsCache);
        CacheInvalidator.getInstance().addCache(contextCache);
        CacheInvalidator.getInstance().addCache(allowingContextsCache);
        CacheInvalidator.getInstance().addCache(invalidableObjects);
        mmb.addLocalObserver(getTableName(), CacheInvalidator.getInstance());
        mmb.addRemoteObserver(getTableName(), CacheInvalidator.getInstance());

        return super.init();
    }

    /**
     * Staticly receives the MMObjectBuilder instance (casted to Contexts). A utility function.
     */
    public static Contexts getBuilder() {
        return (Contexts) MMBase.getMMBase().getBuilder("mmbasecontexts");
    }


    /**
     * Implements check function with same arguments of Authorisation security implementation.     
     * @see Verify#check(user, nodeId, sourceNodeI, destinationNodeI, operation);
     */

    public boolean mayDo(User user, int nodeId, int sourceNodeId, int destinationNodeId, Operation operation) throws SecurityException {
        // admin bypasses security system
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            log.debug("admin may do everything");
            return true;
        }        
        

        // retrieve the nodes
        MMObjectNode source      = getNode(sourceNodeId);
        MMObjectNode destination = getNode(destinationNodeId);

        if  ( (source.parent instanceof Users) && (destination.parent instanceof Ranks)) {
            if (user.getNode().equals(source)) {
                log.debug("Cannot change own rank");
                return false;
            }

            if (Ranks.getBuilder().getRank(destination).getInt() > user.getRank().getInt()) {
                log.debug("May not increase rank of other user to rank higher than own rank");
                return false;
            }
            if (operation == Operation.CREATE) { 
                List ranks =  source.getRelatedNodes("mmbaseranks", "rank", ClusterBuilder.SEARCH_DESTINATION);
                if (ranks.size() > 0) {
                    log.debug("May only create relation to rank if there are none now.");
                    return false;
                }
            }
        }

        if ( (source.parent instanceof Contexts) && (destination.parent instanceof Groups)) {
            if (operation == Operation.CREATE) {
                return Groups.getBuilder().mayGrant(destination, source.getStringValue("name"), Operation.READ, user.getNode());
            }
            
        }

        return mayDo(user, nodeId, operation);

    }


    /**
     * Implements check function with same arguments of Authorisation security implementation
     * @see Verify#check(user, nodeId, operation);
     */
    public boolean mayDo(User user, int nodeId, Operation operation) throws SecurityException {

        // retrieve the node
        MMObjectNode node       = getNode(nodeId);

        MMObjectBuilder builder = null;
        if (node != null) { // perhaps a node of inactive type
            builder = node.getBuilder();
        }

        // may never unlink relation with own rank
        if (operation == Operation.DELETE && builder instanceof InsRel) {
            MMObjectNode source      = getNode(node.getIntValue("snumber"));
            MMObjectNode destination = getNode(node.getIntValue("dnumber"));
            if (source.parent instanceof Users && destination.parent instanceof Ranks) {
                if (user.getNode().equals(source)) {
                    log.debug("May not unlink rank with own user object");
                    return false;
                }
                
            }                                          
        }

        // admin bypasses security system
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            log.debug("admin may do everything");
            return true;
        }


        if (node == null) {
            log.warn("node #" + nodeId + " not found");
            return false;
        }

        if (readAll && operation == Operation.READ) {
            log.debug("Allowing all read operiations, so read on " + nodeId + " is permitted");
            return true;
        }

        // if this is a group node, then you may do anything on it, if you are member of the group,
        // and you rank is higher then 'basic user'.
        if (builder instanceof Groups) {
            boolean res = Groups.getBuilder().contains(node, user);  // members may see the group
            if (operation != Operation.READ) {
                return res && user.getRank().getInt() > Rank.BASICUSER.getInt();
            } else {
                return res;
            }
        }

        // when it is our user node, and you are this user, you may do anything on it (change password)
        if (builder instanceof Users) {
            if (user.getNode().equals(node) && (operation == Operation.READ || operation == Operation.WRITE)) {
                if (log.isDebugEnabled()) {
                    log.debug("May always " + operation + " on own user node: " + nodeId);
                }
                return true;
            }
        }

        // retrieve all the groups in which the context of this node is.

        MMObjectNode contextNode = getContextNode(node); // the mmbasecontext node associated with this node

        return mayDo(user, contextNode, operation);
    }

    /**
     * Returns wether user may do operation on a node with given context. 
     */
    protected boolean mayDo(User user, MMObjectNode contextNode, Operation operation) {

        return mayDo(user.getNode(), contextNode, operation);
    }

    protected boolean mayDo(MMObjectNode user, MMObjectNode contextNode, Operation operation) {

        Iterator iter = getGroups(contextNode, operation).iterator();        

        // now checking if this user is in one of these groups.
        while (iter.hasNext()) {
            MMObjectNode group = (MMObjectNode) iter.next();            
            log.trace("checking group " + group);
            if(Groups.getBuilder().contains(group, user)) { 
                if (log.isDebugEnabled()) {
                    log.debug("User " + user.getStringValue("username") + " may " + operation + " according to context " + contextNode);
                }
                return true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("User " + user.getStringValue("username") + " may not " + operation + " according to context " + contextNode);
        }
        return false;

    }


    /**
     * Returns a Set (of Strings) of all existing contexts
     */
    protected SortedSet getAllContexts() {
        SortedSet all = (SortedSet) invalidableObjects.get("ALL"); 
        if (all == null) {
            Enumeration enumeration = search(null);  // list all  Contextes simply..        
            all = new TreeSet();
            while (enumeration.hasMoreElements()) {
                MMObjectNode context = (MMObjectNode) enumeration.nextElement();            
                all.add(context.getStringValue("name"));
            }
            
            invalidableObjects.put("ALL", all);
        }
        return all;
    }

    /**
     * Returns a Set (of Strings) of all existing contexts for which the given operation is not allowed for the given user.
     */
    protected SortedSet getDisallowingContexts(User user, Operation operation) {
        if (operation != Operation.READ) throw new UnsupportedOperationException("Currently only implemented for READ");
        SortedSet set = new TreeSet();
        Iterator i = getAllContexts().iterator();
        while (i.hasNext()) {
            String context = (String) i.next();
            MMObjectNode contextNode = getContextNode(context);
            if (! mayDo(user, contextNode, operation)) {
                set.add(context);
            }
        }
        return set;
    }


    /**
     * Implements check function with same arguments of Authorisation security implementation
     * @see Verify#check(user, query, operation);
     */

    public Authorization.QueryCheck check(User userContext, Query query, Operation operation) {
        if (userContext.getRank().getInt() >= Rank.ADMIN_INT) {
            return Authorization.COMPLETE_CHECK;
        } else {
            if (operation == Operation.READ && readAll) {
                return Authorization.COMPLETE_CHECK;
            } else if (operation == Operation.READ) {

                AllowingContexts ac = (AllowingContexts) allowingContextsCache.get(userContext.getIdentifier());
                if (ac == null) {
                    // smart stuff for query-modification
                    SortedSet disallowing = getDisallowingContexts(userContext, operation);
                    SortedSet contexts;
                    boolean   inverse;
                    if (log.isDebugEnabled()) {
                        log.debug("disallowing: " + disallowing + " all " + getAllContexts());
                    }

                    // searching which is 'smallest': disallowing contexts, or allowing contexts.
                    if (disallowing.size() < (getAllContexts().size() / 2)) {
                        contexts = disallowing;
                        inverse = true;
                    } else {
                        contexts  = new TreeSet(getAllContexts());
                        contexts.removeAll(disallowing);
                        inverse = false;
                    }
                    ac = new AllowingContexts(contexts, inverse);
                    allowingContextsCache.put(userContext.getIdentifier(), ac);
                }
                if (ac.contexts.size() == 0) {
                    if (ac.inverse) {
                        return Authorization.COMPLETE_CHECK;
                    } else {
                        // may read nothing, simply making the query result nothing: number = -1 
                        Constraint mayNothing = query.createConstraint(query.createStepField((Step) query.getSteps().get(0), "number"), new Integer(-1));
                        return new Authorization.QueryCheck(true, mayNothing);
                    }
                }
                
                List steps = query.getSteps();
                if (steps.size() * ac.contexts.size() < maxContextsInQuery) { 
                    Iterator i = steps.iterator();
                    Constraint constraint = null;
                    while (i.hasNext()) {
                        Step step = (Step) i.next();
                        Constraint newConstraint = null;

                        if (step.getTableName().equals("mmbasegroups")) {
                            newConstraint = query.createConstraint(query.createStepField(step, "number"), userContext.getGroups()); // must be member of group to see group
                            if(operation != Operation.READ) { //
                                if (userContext.getRank().getInt() <= Rank.BASICUSER.getInt()) { // may no nothing, simply making the query result nothing: number = -1 
                                    Constraint mayNothing = query.createConstraint(query.createStepField(step, "number"), new Integer(-1));
                                    return new Authorization.QueryCheck(true, mayNothing);
                                }
                            }
                        } else {
                            StepField field = query.createStepField(step, "owner");
                            newConstraint = query.createConstraint(field, ac.contexts);
                            if (ac.inverse) query.setInverse(newConstraint, true);
                            if (step.getTableName().equals("mmbaseusers")) { // anybody may see own node
                                Users users = Users.getBuilder();
                                Constraint own = query.createConstraint(query.createStepField(step, "number"),
                                                                        new Integer(users.getUser(userContext.getIdentifier()).getNumber()));
                                newConstraint = query.createConstraint(newConstraint, CompositeConstraint.LOGICAL_OR, own);
                            }
                        }


                        if (constraint == null) {
                            constraint = newConstraint;
                        } else {
                            constraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
                        } 
                    }
                    return new Authorization.QueryCheck(true, constraint);
                } else { // query would grow too large
                    return Authorization.NO_CHECK;
                }

            } else {
                //not checking for READ: never mind, this is only used for read checks any way
                return Authorization.NO_CHECK;
            }
        }
    }


    /**
     * @return The MMObjectNode presenting the context of the given node.
     */
    private final MMObjectNode getContextNode(MMObjectNode node)  {
        String s = node.getStringValue("owner");
        return getContextNode(s);

    }

    /**
     * @return a  Set of all groups which allow the given operation (not recursively).
     */
    protected  Set getGroups(MMObjectNode contextNode, Operation operation) {        
        
        Set found = operationsCache.get(contextNode, operation);
        if (found == null) {
            found = new HashSet();
            for(Enumeration enumeration = contextNode.getRelations(); enumeration.hasMoreElements();) {
                // needed to get the correct type of builder!!
                MMObjectNode relation = getNode(((MMObjectNode) enumeration.nextElement()).getNumber());
                if (relation.parent instanceof RightsRel) {
                    String nodeOperation = relation.getStringValue(RightsRel.OPERATION_FIELD);
                    if (nodeOperation.equals(operation.toString()) || nodeOperation.equals("all")) {
                        int source      = relation.getIntValue("snumber");
                        MMObjectNode destination = relation.getNodeValue("dnumber");
                        if (source == contextNode.getNumber()) {
                            if (log.isDebugEnabled()) {
                                log.debug("found group # " + destination.getNumber() + " for operation" + operation + "(because " + nodeOperation + ")");
                            }
                            found.add(destination);
                        } else {
                            log.warn("source was not the same as out contextNode");
                        }
                    }  
                } 
            }
            if (log.isDebugEnabled()) {
                log.debug("found groups for operation " + operation + " " + found);
            }
            operationsCache.put(contextNode, operation, found);
        }
        return found;
    }

    protected  Set getGroups(String context, Operation operation) {
        return getGroups(getContextNode(context), operation);
    }

    protected final MMObjectNode getContextNode(String context) {
        MMObjectNode contextNode = (MMObjectNode) contextCache.get(context);
        if (contextNode == null && ! contextCache.contains(context)) {           
            try {
                NodeSearchQuery query = new NodeSearchQuery(this);
                BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(query.getField(getField("name")), context);
                query.setConstraint(constraint);
                Iterator i = getNodes(query).iterator();
                if (i.hasNext()) {
                    contextNode = (MMObjectNode)i.next();
                } else {
                    if (! DEFAULT_CONTEXT.equals(context)) {
                        log.warn("Could not find context '" + context + "' using default context '" + DEFAULT_CONTEXT + "'");
                        contextNode = getContextNode(DEFAULT_CONTEXT);
                        if (contextNode == null) {
                            log.error("Could not find default context '" + DEFAULT_CONTEXT + "'.");
                        }
                    }
                }
            } catch (SearchQueryException sqe) {
                log.error(sqe.toString());
                contextNode = null;
                
            }
            contextCache.put(context, contextNode);
        }
        return contextNode;

    }

   

    /**
     * Returns this Context node as a String (so the name field)
     */
    public String getContext(User user, int nodeId)  throws SecurityException {
        MMObjectNode node = getNode(nodeId);
        if (node == null) {
            throw new SecurityException("node #" + nodeId + " not found");
        }
        if (node.getBuilder() instanceof Groups) {
            return "unused"; // confusing
        }
        return getContextNode(node).getStringValue("name");
    }

    /**
     * Sets the context of a node to a certain String Value
     * @param user The user doing this.
     * @param nodeId The number of the node which' context must be changed
     * @param context The String describing the desired new context
     * @return The MMObjectNode
     */
    public MMObjectNode setContext(User user, int nodeId, String context) throws SecurityException {
        MMObjectNode node = getNode(nodeId);

        // during creation of a node, the context is set twice!
        // (in createNode of BasicNodeManager, but also in create of Authorisation implemetentation called
        // via basicclou .createSecurityInfo(getNumber());)
        // so not changing must be allowed always.
        if (node.getStringValue("owner").equals(context)) return node;

        if (node == null) {
            throw new SecurityException("node #" + nodeId + " not found");
        }
        if (node.getBuilder() instanceof Groups) { 
            node.setValue("owner", "system");
            node.commit();
            return node;
        }
        if (!getPossibleContexts(user, nodeId).contains(context)) {
            throw new SecurityException("could not set the context from '" + node.getStringValue("owner") + "' to '" + context + "' for node #" + nodeId + "(context name:" + context + " is not a valid context)");
        }
        node.setValue("owner", context);
        node.commit();
        return node;
    }

    /**
     * Wraps getPossibleContexts of Authorisation implementation Verify.
     * @see Verify#getPossibleContexts
     * @todo Perhaps we need a possibleContextCache.
     */
    public Set getPossibleContexts(User user, int nodeId) throws SecurityException {
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            log.debug("admin may do everything");           
            return getAllContexts();
        }        

        MMObjectNode node = getNode(nodeId);
        if (node == null) {
            throw new SecurityException("node #" + nodeId + " not found");
        }
        if (node.getBuilder() instanceof Groups) { 
            return new HashSet();  // why?
        }
        
        List possibleContexts = getContextNode(node).getRelatedNodes("mmbasecontexts", "allowed", ClusterBuilder.SEARCH_DESTINATION);

        Set hashSet = new HashSet();
        Iterator i = possibleContexts.iterator();
        while (i.hasNext()) {
            MMObjectNode context = (MMObjectNode) i.next();
            if (mayDo(user, context.getNumber(), Operation.READ )) {
                hashSet.add(context.getStringValue("name"));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("context with name:" + context.getStringValue("name") + " could not be added to possible contexes, since we had no read rights");
                }
            }
        }

        return hashSet;
    }

    public String toString(MMObjectNode n) {
        return n.getStringValue("name");
    }

    private static class AllowingContexts {
        SortedSet contexts;
        boolean inverse;
        AllowingContexts(SortedSet c, boolean i) {
            contexts = c;
            inverse = i;
        }
        public String toString() {
            return (inverse ? "NOT IN " : "IN ") + contexts;
        }
        
    }



}
