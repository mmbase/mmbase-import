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
import org.mmbase.util.*;

/**
 * Representation of a 'context', which can be read as a valid value of the 'owner' field of any
 * object in MMBase. Rights are distributed using this thing. This is part of cloud context
 * security, so the 'context' values need to be present in the cloud.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Contexts.java,v 1.26 2003-11-19 16:41:00 michiel Exp $
 * @see    org.mmbase.security.implementation.cloudcontext.Verify
 * @see    org.mmbase.security.Authorization
 */
public class Contexts extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(Contexts.class);

    /**
     *
     * @javadoc
     */
    static final String DEFAULT_CONTEXT = "default";  // default used to be 'admin', but does that make sense?
    static final int DEFAULT_MAX_CONTEXTS_IN_QUERY = 50;

    public final static Argument[] ALLOW_ARGUMENTS = {
        new Argument("grouporuser", String.class),
        new Argument("operation", String.class)
    };


    public final static Argument[] GRANT_ARGUMENTS = {
        new Argument("grouporuser",  String.class),
        new Argument("operation", String.class),
        new Argument("user", org.mmbase.bridge.User.class),

    };



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
    private boolean allContextsPossible = true; // if you want to use security for workflow, then you want this to be false


    private int     maxContextsInQuery = DEFAULT_MAX_CONTEXTS_IN_QUERY;


    /**
     * @javadoc
     */
    public boolean init() {
        String s = (String) getInitParameters().get("readall");
        readAll = "true".equals(s);

        s = (String) getInitParameters().get("allcontextspossible");
        allContextsPossible = ! "false".equals(s);

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

        // interesting right-righs are implemented in mayDo.

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

        // admin bypasses security system
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            log.debug("admin may do everything, besides deleting itself");
            if (user.getNode() != null && user.getNode().getNumber() == nodeId && operation == Operation.DELETE) return false;
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



        // security-implmentation related issues
        if (builder instanceof InsRel) {
            MMObjectNode source      = getNode(node.getIntValue("snumber"));
            MMObjectNode destination = getNode(node.getIntValue("dnumber"));

            if (source.parent instanceof Users && destination.parent instanceof Ranks) {

                // forbid hackery
                if (operation == Operation.WRITE || operation == Operation.CHANGE_RELATION) {
                    return false;
                }
                if (operation == Operation.DELETE || operation == Operation.CREATE) {

                    // only 'high rank' user may change rank at all:
                    if(user.getRank().getInt() <= Rank.BASICUSER.getInt()) return false;

                    // may never unlink/link relation with own rank
                    if (user.getNode() != null && user.getNode().equals(source)) {
                        log.debug("May not unlink rank with own user object");
                        return false;
                    }

                    // may not change rank of higher rank users
                    if (user.getRank().getInt() <= destination.getIntValue("rank")) {
                        return false;
                    }
                }
                // otherwise: ok:
                return true;
            }
            if (builder instanceof RightsRel) {
                // forbid hackery
                if (operation == Operation.WRITE || operation == Operation.CHANGE_RELATION) {
                    return false;
                }
                if (operation == Operation.CREATE) {
                    return mayGrant(source, destination, Operation.getOperation(node.getStringValue("operation")), user.getNode());
                }
                if (operation == Operation.DELETE) {
                    return mayRevoke(source, destination, Operation.getOperation(node.getStringValue("operation")), user.getNode());
                }
            }
        }


        // if this is a group node, then you may do anything on it, if you are member of the group,
        // and you rank is higher then 'basic user'.
        /*
        if (builder instanceof Groups) {
            boolean res = Groups.getBuilder().contains(node, user);  // members may see the group
            if (operation != Operation.READ) {
                return res && user.getRank().getInt() > Rank.BASICUSER.getInt();
            } else {
                return res;
            }
        }
        */

        // when it is our user node, and you are this user, you may do anything on it (change password)
        if (isOwnNode(user, node)) {
            if ((operation == Operation.READ || operation == Operation.WRITE)) {
                if (log.isDebugEnabled()) {
                    log.debug("May always " + operation + " on own user node: " + nodeId);
                }
                return true;
            }
            if (operation == Operation.DELETE || operation == Operation.CHANGECONTEXT) {
                    // may not delete/give away own user.
                    return false;
            }
        }


        MMObjectNode contextNode = getContextNode(node); // the mmbasecontext node associated with this node

        return mayDo(user, contextNode, operation);
    }

    /**
     * Returns wether the given node is an 'own' node. It should return true if the node is representing the mmbaseusers object which represents the current user.
     * Extensions could e.g. also implement returning true for the associated people node.
     */
    protected boolean isOwnNode(User user, MMObjectNode node) {
        MMObjectNode userNode = user.getNode();
        return  (userNode != null && userNode.getBuilder() instanceof Users && userNode.equals(node));
    }

    /**
     * Returns wether user may do operation on a node with given context.
     */
    protected boolean mayDo(User user, MMObjectNode contextNode, Operation operation) {

        return mayDo(user.getNode(), contextNode, operation, true);
    }

    protected boolean mayDo(MMObjectNode user, MMObjectNode contextNode, Operation operation, boolean checkOwnRights) {

        Set groupsAndUsers = getGroupsAndUsers(contextNode, operation);

        if (checkOwnRights) {
            if (groupsAndUsers.contains(user)) return true;
        }

        Iterator iter = groupsAndUsers.iterator();
        // now checking if this user is in one of these groups.
        while (iter.hasNext()) {
            MMObjectNode group = (MMObjectNode) iter.next();
            if (! (group.parent instanceof Groups)) continue;
            if (log.isDebugEnabled()) log.trace("checking group " + group);
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
            try {
                Iterator i = getNodes(new NodeSearchQuery(this)).iterator();  // list all  Contextes simply..
                all = new TreeSet();
                while (i.hasNext()) {
                    MMObjectNode context = (MMObjectNode) i.next();
                    all.add(context.getStringValue("name"));
                }
                
                invalidableObjects.put("ALL", all);
            } catch (SearchQueryException sqe) {
                log.error(sqe + Logging.stackTrace(sqe));
            }
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

                List steps = query.getSteps();
                Constraint constraint = null;

                // constraints on security objects
                {
                    Iterator i = steps.iterator();
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
                        } else if (step.getTableName().equals("mmbaseranks")) { // higher ranks are none of your businuess (especially usefull for editors)
                            newConstraint = query.createConstraint(query.createStepField(step, "rank"), FieldCompareConstraint.LESS_EQUAL, new Integer(userContext.getRank().getInt()));
                        } else {
                            continue;
                        }

                        if (constraint == null) {
                            constraint = newConstraint;
                        } else {
                            constraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
                        }

                    }
                }

                if (ac.contexts.size() == 0) {
                    if (ac.inverse) {
                        if (constraint == null) {
                            return Authorization.COMPLETE_CHECK;
                        } else {
                            return new Authorization.QueryCheck(true, constraint);
                        }
                    } else {
                        // may read nothing, simply making the query result nothing: number = -1
                        Constraint mayNothing = query.createConstraint(query.createStepField((Step) query.getSteps().get(0), "number"), new Integer(-1));
                        return new Authorization.QueryCheck(true, mayNothing);
                    }
                }


                if (steps.size() * ac.contexts.size() < maxContextsInQuery) {
                    Iterator i = steps.iterator();
                    while (i.hasNext()) {
                        Step step = (Step) i.next();
                        Constraint newConstraint = null;
                        StepField field = query.createStepField(step, "owner");
                        newConstraint = query.createConstraint(field, ac.contexts);
                        if (ac.inverse) query.setInverse(newConstraint, true);

                        if (step.getTableName().equals("mmbaseusers")) { // anybody may see own node
                            Users users = Users.getBuilder();
                            Constraint own = query.createConstraint(query.createStepField(step, "number"),
                                                                    new Integer(users.getUser(userContext.getIdentifier()).getNumber()));
                            newConstraint = query.createConstraint(newConstraint, CompositeConstraint.LOGICAL_OR, own);
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
     * @return a  Set of all groups/users which allow the given operation (not recursively).
     */
    protected  Set getGroupsAndUsers(MMObjectNode contextNode, Operation operation) {
        Set found = operationsCache.get(contextNode, operation);
        if (log.isDebugEnabled()) {
            log.debug("found " + found  + " for " + contextNode + "/" + operation);
        }
        if (found == null) {
            found = new HashSet();
            for(Enumeration enumeration = contextNode.getRelations(); enumeration.hasMoreElements();) {
                MMObjectNode originalRelation = null;
                try {
                    originalRelation = (MMObjectNode) enumeration.nextElement();
                    // needed to get the correct type of builder!!
                    MMObjectNode relation = getNode(originalRelation.getNumber());
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
                                log.warn("source of " + relation + " was not the same as contextNode " + contextNode + " but " + relation.getNodeValue("snumber"));
                                log.warn(Logging.stackTrace());
                            }
                        }
                    }
                } catch (RuntimeException rte) {
                    // ignore the cited excedption
                    log.warn("Error with " + originalRelation +  Logging.stackTrace(rte, 5));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("found groups for operation " + operation + " " + found);
            }
            operationsCache.put(contextNode, operation, found);
        }

        return found;
    }

    /*
    protected  Set getGroups(String context, Operation operation) {
        return getGroups(getContextNode(context), operation);
    }
    */

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

        /*
           mm: I think we should trying securing groups as well, to removed this (id did not quit understand it any way)a

        if (node.getBuilder() instanceof Groups) {
            node.setValue("owner", "system");
            node.commit();
            return node;
        }
        */
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

        List possibleContexts;
        if (allContextsPossible) {
            try {
                possibleContexts = getNodes(new NodeSearchQuery(this));
            } catch (SearchQueryException sqe) {
                throw new SecurityException(sqe);
            }
        } else {
            possibleContexts = getContextNode(node).getRelatedNodes("mmbasecontexts", "allowed", ClusterBuilder.SEARCH_DESTINATION);
        }

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


    //********************************************************************************
    // EDIT FUNCTIONS
    //********************************************************************************

    /**
     * Wether users of the given group may do operation on a node of given context (so no following)
     * @return boolean
     */
    protected boolean allows(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        return getGroupsAndUsers(contextNode, operation).contains(groupOrUserNode);
    }

    /**
     * Wether users of the given group may do operation on a node of given context, because
     * (one of) the parents of this group allow it.
     *
     * @return boolean
     */
    protected boolean parentsAllow(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        if (log.isDebugEnabled()) {
            log.debug("parents allow for " + contextNode + " " + groupOrUserNode + " " + operation);
        }
        try {
            Groups groups = Groups.getBuilder();

            Set groupsAndUsers = getGroupsAndUsers(contextNode, operation);
            Iterator i = groupsAndUsers.iterator();
            while (i.hasNext()) {
                MMObjectNode containingGroup = (MMObjectNode) i.next();
                if (groups.contains(containingGroup, groupOrUserNode)) return true;
            }
        } catch (Throwable e) {
            log.error(Logging.stackTrace(e));
        }
        return false;
    }

    /**
     */
    protected boolean mayGrant(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, MMObjectNode user) {
        Users users = Users.getBuilder();
        if (users.getRank(user).getInt() >= Rank.ADMIN.getInt()) return true; // admin may do everything
        Groups groups = Groups.getBuilder();

        if (groupOrUserNode.parent instanceof Groups) {
            if (! groups.contains(groupOrUserNode, user.getNumber()) || users.getRank(user).getInt() <= Rank.BASICUSER.getInt()) return false; // must be 'high rank' member of group
        } else {
            if (groupOrUserNode.equals(user)) return false; // if not admin, you may not grant yourself rights. (and for admin it is not necessary)
            if (users.getRank(groupOrUserNode).getInt() >= users.getRank(user).getInt()) return false; // may not grant to users of higher/equal rank than yourself
        }
        return mayDo(user, contextNode, operation, true); // you need to have the right yourself to grant it.
    }



    /**
     */
    protected boolean grant(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, MMObjectNode user) {
        if (allows(contextNode, groupOrUserNode, operation)) return true; // already allowed
        // create a relation..
        if (mayGrant(contextNode, groupOrUserNode, operation, user)) {
            if (log.isServiceEnabled()) {
                log.service("Granting right " + operation + " on context " + contextNode  + " to group/user " + groupOrUserNode + " by " + user);
            }
            RightsRel rightsRel = RightsRel.getBuilder();
            MMObjectNode ownerContextNode = user.getNodeValue("defaultcontext");
            String ownerString;
            if (ownerContextNode != null) {
                ownerString = ownerContextNode.getStringValue("name");
            } else {
                ownerString = DEFAULT_CONTEXT;
            }
            MMObjectNode newRight = rightsRel.getNewNode(ownerString, contextNode.getNumber(), groupOrUserNode.getNumber(), operation);
            boolean res = newRight.insert(ownerString) > 0;
            if (! res) {
                log.error("Failed to grant " + newRight);
            } else {
                log.service("Granted " + newRight);
            }
            return res;

        } else {
            log.service("Granting right " + operation + " on context " + contextNode  + " to group/user " + groupOrUserNode + " by " + user + " failed because it it not allowed");
            return false;
        }
    }


    /**
     * Makes sure unique values and not-null's are filed
     */
    public void setDefaults(MMObjectNode node) {
        setUniqueValue(node, "name", "context");
    }





    /**
     * @untested
     */

    protected boolean mayRevoke(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, MMObjectNode user) {
        Users users = Users.getBuilder();
        if (users.getRank(user).getInt() >= Rank.ADMIN.getInt()) return true; // admin may do everything
        if (groupOrUserNode.parent instanceof Groups) {
            if (! Groups.getBuilder().contains(groupOrUserNode, user.getNumber()) || users.getRank(user).getInt() <= Rank.BASICUSER.getInt()) return false; // must be 'high rank' member of group
        } else {
            if (groupOrUserNode.equals(user)) return false; // if not admin, you may not revoke yourself rights. (and for admin it does not make sense
            if (users.getRank(groupOrUserNode).getInt() >= users.getRank(user).getInt()) return false; // may not revoke from users of higher/equal rank than yourself
        }
        return mayDo(user, contextNode, operation, true); // you need to have the right yourself to revoke it (otherwise you could not grant it back)
    }



    /**
     */

    protected boolean revoke(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, MMObjectNode user) {
        Users users = Users.getBuilder();
        if (!allows(contextNode, groupOrUserNode, operation)) return true; // already disallowed

        if (mayRevoke(contextNode, groupOrUserNode, operation, user)) {
            if (log.isServiceEnabled()) {
                log.service("Revoking right " + operation + " on context " + contextNode + " to group " + groupOrUserNode + " by " + user);
            }
            RightsRel rights   = RightsRel.getBuilder();
            NodeSearchQuery q = new NodeSearchQuery(rights);
            BasicStepField snumber = q.getField(rights.getField("snumber"));
            BasicStepField dnumber = q.getField(rights.getField("dnumber"));
            BasicFieldValueConstraint c1 = new BasicFieldValueConstraint(snumber, new Integer(contextNode.getNumber()));
            BasicFieldValueConstraint c2 = new BasicFieldValueConstraint(dnumber, new Integer(groupOrUserNode.getNumber()));
            BasicCompositeConstraint cons = new BasicCompositeConstraint(BasicCompositeConstraint.LOGICAL_AND);
            cons.addChild(c1);
            cons.addChild(c2);
            q.setConstraint(cons);
            try {
                List r = rights.getNodes(q);
                Iterator i = r.iterator();
                while (i.hasNext()) {
                    MMObjectNode right = (MMObjectNode) i.next();
                    rights.removeNode(right);
                }
            } catch (Exception sqe) {
                log.error(sqe.toString());
                return false;
            }
            return true;
        } else {
            log.service("Revoking right " + operation + " on context " + contextNode + " to group/user " + groupOrUserNode + " by " + user + " failed because it it not allowed");
            return false;
        }

    }

    /**
     * util
     */
    protected MMObjectNode getUserNode(org.mmbase.bridge.User bridgeUser) {
        Users users = Users.getBuilder();
        return users.getUser(bridgeUser.getIdentifier());
    }

    protected MMObjectNode getGroupOrUserNode(Arguments a) {
        MMObjectNode groupOrUser = getNode(a.getString("grouporuser"));
        if (groupOrUser == null) throw new IllegalArgumentException("There is no node with id '" + a.get("grouporuser") + "'");
        if (! (groupOrUser.parent instanceof Groups || groupOrUser.parent instanceof Users)) {
            throw new IllegalArgumentException("Node '" + a.get("grouporuser") + "' does not represent a group or a user");
        }
        return groupOrUser;
    }



    protected Object executeFunction(MMObjectNode node, String function, List args) {
        log.debug("executefunction of abstractservletbuilder");
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, function, empty);
            info.put("allow",        "" + ALLOW_ARGUMENTS + " Wether operation may be done according to this context");
            info.put("parentsallow", "" + ALLOW_ARGUMENTS + " Wether operation may be done by members of this group, also because of parents");
            info.put("grant",        "" + GRANT_ARGUMENTS + " Grant a right");
            info.put("revoke",       "" + GRANT_ARGUMENTS + " Revoke a right");
            info.put("maygrant",     "" + GRANT_ARGUMENTS + " Check if user may grant a right");
            info.put("mayrevoke",    "" + GRANT_ARGUMENTS + " Check if user may revoke a right");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals("allows")) {
            Arguments a = Arguments.get(GRANT_ARGUMENTS, args);  // 'ALLOW' argument would be more logical, but don't when because of the extra argument (practical can use several functions with same arguments list)
            if (allows(node, getNode(a.getString("grouporuser")), Operation.getOperation(a.getString("operation")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("parentsallow")) {   // 'ALLOW' argument would be more logical, but don't when because of the extra argument (practical can use several functions with same arguments list)
            Arguments a = Arguments.get(GRANT_ARGUMENTS, args);
            if (parentsAllow(node, getGroupOrUserNode(a), Operation.getOperation(a.getString("operation")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("grant")) {
            Arguments a = Arguments.get(GRANT_ARGUMENTS, args);
            if (grant(node, getGroupOrUserNode(a), Operation.getOperation(a.getString("operation")), getUserNode((org.mmbase.bridge.User) a.get("user")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("revoke")) {
            Arguments a = Arguments.get(GRANT_ARGUMENTS, args);
            if (revoke(node, getGroupOrUserNode(a), Operation.getOperation(a.getString("operation")), getUserNode((org.mmbase.bridge.User) a.get("user")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("maygrant")) {
            Arguments a = Arguments.get(GRANT_ARGUMENTS, args);
            if (mayGrant(node, getGroupOrUserNode(a), Operation.getOperation(a.getString("operation")), getUserNode((org.mmbase.bridge.User) a.get("user")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("mayrevoke")) {
            Arguments a = Arguments.get(GRANT_ARGUMENTS, args);
            if (mayRevoke(node, getGroupOrUserNode(a), Operation.getOperation(a.getString("operation")), getUserNode((org.mmbase.bridge.User) a.get("user")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }

        } else {
            return super.executeFunction(node, function, args);
        }
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
