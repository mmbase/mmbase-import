/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.security.implementation.cloudcontext.builders.Contexts;
import org.mmbase.security.implementation.cloudcontext.builders.Groups;
import org.mmbase.security.implementation.cloudcontext.builders.Ranks;
import org.mmbase.security.implementation.cloudcontext.builders.RightsRel;


import java.util.*;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.cache.Cache;
import org.mmbase.cache.AggregatedResultCache;
import org.mmbase.util.ChainedList;
import org.mmbase.bridge.Query;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is a basic implemention of {@link ContextProvider} that implements all the methods in a default way.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since  MMBase-1.9.1
 */
public  class BasicContextProvider implements ContextProvider {

    private static final Logger log = Logging.getLoggerInstance(BasicContextProvider.class);


    private final List<NodeSearchQuery> queries;
    private SortedSet<String> all;

    public BasicContextProvider(NodeSearchQuery... q) {
        List<NodeSearchQuery> temp = new ArrayList<NodeSearchQuery>();
        for (NodeSearchQuery query : q) {
            query.setModifiable(false);
            temp.add(query);
        }
        queries = Collections.unmodifiableList(temp);
    }


    public BasicContextProvider(MMObjectBuilder... b) {
        List<NodeSearchQuery> temp = new ArrayList<NodeSearchQuery>();
        for (MMObjectBuilder bul : b) {
            if (bul == null) throw new IllegalArgumentException("Cannot add null to builder list");
            NodeSearchQuery q = new NodeSearchQuery(bul);
            q.setModifiable(false);
            temp.add(q);
        }
        queries = Collections.unmodifiableList(temp);
    }

    public BasicContextProvider(String... b) {
        List<NodeSearchQuery> temp = new ArrayList<NodeSearchQuery>();
        for (String bulName : b) {
            MMObjectBuilder bul = MMBase.getMMBase().getBuilder(bulName);
            if (bul == null) {
                log.warn("Cannot add '" + bulName + "' to builder list (it does not exist)");
            }
            NodeSearchQuery q = new NodeSearchQuery(bul);
            q.setModifiable(false);
            temp.add(q);
        }
        queries = Collections.unmodifiableList(temp);
    }

    protected boolean isAllContextsPossible() {
        return true;
    }
    protected boolean canReadAll() {
        return true;
    }
    protected int getMaxContextsInQuery() {
        return 50;
    }

    protected boolean disableContextChecks() {
        return false;
    }

    /**
     * For the given context- builder return which field contains the 'context'.
     */
    protected String getContextNameField(String table) {
        return "name";
    }


    public Collection<NodeSearchQuery> getContextQueries() {
        return queries;
    }


    public String getContextName(MMObjectNode contextNode) throws SecurityException {
        if (contextNode == null) {
            return null;
        }
        return contextNode.getStringValue(getContextNameField(contextNode.getBuilder().getTableName()));
    }

    public void setContext(User user, MMObjectNode node, String context) {

        // during creation of a node, the context is set twice!
        // (in createNode of BasicNodeManager, but also in create of Authorisation implemetentation called
        // via basiccloud.createSecurityInfo(getNumber());)
        // so not changing must be allowed always.
        if (node.getStringValue("owner").equals(context)) return;

        /*
           mm: I think we should try securing groups as well, so removed this (id did not quit understand it any way)

           if (node.getBuilder() instanceof Groups) {
              node.setValue("owner", "system");
              node.commit();
              return node;
        }
        */
        if (context == null || context.equals("")) {
            //|| context.equals("null"))  {  // dirty work around bug in editwizard
            log.warn("Tried to set context to '" + context + "' WRONG!");
        } else {
            if (!getPossibleContexts(user, node).contains(context)) {
                throw new SecurityException("could not set the context from '" + node.getStringValue("owner") + "' to '" + context + "' for node #" + node.getNumber() + "(context name:" + context + " is not a valid context" + (context == null ? ", but null" : "") + ")");
            }
        }
        node.setValue("owner", context);
        node.commit();
    }


    /**
     * Returns a Set (of Strings) of all existing contexts
     */
    protected SortedSet<String> getAllContexts() {
        if (all == null) {
            try {
                all = new TreeSet<String>();
                for (NodeSearchQuery q : getContextQueries()) {
                    MMObjectBuilder contextBuilder = MMBase.getMMBase().getBuilder(q.getSteps().get(0).getTableName());
                    Iterator<MMObjectNode> i = contextBuilder.getNodes(q).iterator();  // list all  Contextes simply..
                    String nameField = getContextNameField(q.getBuilder().getTableName());
                    log.debug("Using " + MMBase.getMMBase().getSearchQueryHandler().createSqlString(q) + " for all context");
                    while (i.hasNext()) {
                        MMObjectNode context = i.next();
                        all.add(context.getStringValue(nameField));
                    }
                }
                //invalidableObjects.put("ALL", Collections.unmodifiableSortedSet(all));
            } catch (SearchQueryException sqe) {
                log.error(sqe.getMessage(), sqe);
            }
        }
        return all;
    }


    /**
     * Returns a Set (of Strings) of all existing contexts for which the given operation is not allowed for the given user.
     */
    protected SortedSet<String> getDisallowingContexts(User user, Operation operation) {
        if (operation != Operation.READ) throw new UnsupportedOperationException("Currently only implemented for READ");
        SortedSet<String> set = new TreeSet<String>();
        if (! canReadAll()) {
            Iterator<String> i = getAllContexts().iterator();
            while (i.hasNext()) {
                String context = i.next();
                MMObjectNode contextNode = getContextNode(context);
                if (! mayDo(user, contextNode, operation)) {
                    set.add(context);
                }
            }
        }

        return Collections.unmodifiableSortedSet(set);
    }

    protected SortedSet<String> getAllowingContexts(User user, Operation operation) {
        if (operation != Operation.READ) throw new UnsupportedOperationException("Currently only implemented for READ");
        if (canReadAll()) { return getAllContexts(); }

        SortedSet<String> set = new TreeSet<String>(getAllContexts());
        set.removeAll(getDisallowingContexts(user, operation));

        return Collections.unmodifiableSortedSet(set);

    }



    /**
     * @return The MMObjectNode presenting the context of the given node.
     */
    public  MMObjectNode getContextNode(MMObjectNode node)  {
        if (node == null) return null;
        String s = node.getStringValue("owner");
        return getContextNode(s);

    }


    public MMObjectNode getContextNode(String context) {
        Cache<String,MMObjectNode> contextCache = Caches.getContextCache();
        MMObjectNode contextNode = contextCache.get(context);
        if (contextNode == null && ! contextCache.contains(context)) {
            for (NodeSearchQuery query : getContextQueries()) {
                try {
                    MMObjectBuilder contextBuilder = query.getBuilder();
                    query = (NodeSearchQuery) query.clone();
                    String nameField = getContextNameField(contextBuilder.getTableName());
                    BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(query.getField(contextBuilder.getField(nameField)), context);
                    query.setConstraint(constraint);
                    Iterator<MMObjectNode> i = contextBuilder.getNodes(query).iterator();
                    if (i.hasNext()) {
                        contextNode = i.next();
                        break;
                    }
                } catch (SearchQueryException sqe) {
                    log.error(sqe.toString());
                }
            }
            if (contextNode == null) {
                if (! Contexts.DEFAULT_CONTEXT.equals(context)) {
                    log.warn("Could not find context '" + context + "' using default context '" + Contexts.DEFAULT_CONTEXT + "'");
                    contextNode = getContextNode(Contexts.DEFAULT_CONTEXT);
                    if (contextNode == null) {
                        log.error("Could not find default context '" + Contexts.DEFAULT_CONTEXT + "'.");
                    }
                }
            }
            contextCache.put(context, contextNode);
        }
        return contextNode;

    }


    public Set<String> getPossibleContexts(User user, MMObjectNode node)  throws org.mmbase.security.SecurityException {
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            // admin may do everything
            return getAllContexts();
        }
        if (node.getBuilder() instanceof Groups) {
            return new TreeSet<String>();  // why?
        }

        if (isAllContextsPossible()) {
            return getAllowingContexts(user, Operation.READ);
        } else {
            List<MMObjectNode> possibleContexts = getContextNode(node).getRelatedNodes("mmbasecontexts", "allowed", RelationStep.DIRECTIONS_DESTINATION);
            SortedSet<String> set = new TreeSet<String>();
            Iterator<MMObjectNode> i = possibleContexts.iterator();
            while (i.hasNext()) {
                MMObjectNode context = i.next();
                String contextField = getContextNameField(context.getBuilder().getTableName());
                if (mayDo(user, context, Operation.READ )) {
                    set.add(context.getStringValue(contextField));
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("context with name:" + context.getStringValue(contextField) + " could not be added to possible contexes, since we had no read rights");
                    }
                }
            }
            return set;
        }
    }

    public Set<String> getPossibleContexts(User user)  throws org.mmbase.security.SecurityException {
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            // admin may do everything
            return getAllContexts();
        } else {
            return getAllowingContexts(user, Operation.READ);
        }
    }

    public boolean mayDo(User user, MMObjectNode node, Operation operation) {
        MMObjectBuilder builder = null;
        if (node != null) { // perhaps a node of inactive type
            builder = node.getBuilder();
        }

        if (operation == Operation.DELETE) {

            if (user.getNode() != null && user.getNode().getNumber() == node.getNumber() && operation == Operation.DELETE) {
                return false; // nobody may delete own node
            }

            if (builder instanceof Contexts) {
                try {
                    MMObjectBuilder users = Authenticate.getInstance().getUserProvider().getUserBuilder();
                    if (users.hasField("defaultcontext")) {
                        MMBase mmb = MMBase.getMMBase();
                        BasicSearchQuery query = new BasicSearchQuery(true);
                        Step step = query.addStep(users);
                        BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(new BasicStepField(step, users.getField("defaultcontext")), Integer.valueOf(node.getNumber()));
                        query.setConstraint(constraint);
                        BasicAggregatedField baf = query.addAggregatedField(query.getSteps().get(0), users.getField("defaultcontext"), AggregatedField.AGGREGATION_TYPE_COUNT);
                        baf.setAlias("count");

                        AggregatedResultCache cache = AggregatedResultCache.getCache();
                        List<MMObjectNode> resultList = cache.get(query);
                        if (resultList == null) {
                            ResultBuilder resultBuilder = new ResultBuilder(mmb, query);
                            resultList = mmb.getSearchQueryHandler().getNodes(query, resultBuilder);
                            cache.put(query, resultList);
                        }

                        ResultNode result = (ResultNode) resultList.get(0);
                        int count = result.getIntValue("count");
                        if (count > 0) return false;

                        // perhaps should also return false if there are still nodes with this context?
                        // this check is not done in editors, but perhaps it should be bit harder!
                    }
                } catch (SearchQueryException sqe) {
                    // leave to rest of impl.
                }
            }

        }

        // admin bypasses security system
        if (user.getRank().getInt() >= Rank.ADMIN_INT) {
            return true;
        }


        if (node == null) {
            return false;
        }

        if (canReadAll() && operation == Operation.READ) {
            return true;
        }



        // security-implementation related issues
        if (builder instanceof InsRel) {
            MMObjectNode source      = getNode(node.getIntValue("snumber"));
            MMObjectNode destination = getNode(node.getIntValue("dnumber"));

            if (Authenticate.getInstance().getUserBuilder().getClass().isInstance(source.getBuilder())
                    && Ranks.class.isInstance(destination.getBuilder())) {
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
                    return mayGrant(user, source, destination, Operation.getOperation(node.getStringValue("operation")));
                }
                if (operation == Operation.DELETE) {
                    return mayRevoke(user, source, destination, Operation.getOperation(node.getStringValue("operation")));
                }
            }
            if (source.getBuilder() instanceof Groups && Authenticate.getInstance().getUserBuilder().getClass().isInstance(destination.getBuilder()) && operation != Operation.READ) {
                if (getNode(node.getIntValue("rnumber")).getStringValue("sname").equals("contains")) {
                    // may not change groups of higher rank users
                    Rank destRank = Authenticate.getInstance().getUserProvider().getRank(destination);
                    if (user.getRank().getInt() <= destRank.getInt()) {
                        return false;
                    }
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

        UserProvider up = Authenticate.getInstance().getUserProvider();
        // when it is our user node, and you are this user, you may do anything on it (change password)
        if (up.isOwnNode(user, node)) {
            if ((operation == Operation.READ || operation == Operation.WRITE)) {
                if (log.isDebugEnabled()) {
                    log.debug("May always " + operation + " on own user node: " + node.getNumber());
                }
                return true;
            }
            if (operation == Operation.DELETE || operation == Operation.CHANGE_CONTEXT) {
                // may not delete/give away own user.
                return false;
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("According to " + up + " " + node.getNumber() + " is not an own node");
            }
        }


        MMObjectNode contextNode = getContextNode(node); // the mmbasecontext node associated with this node
        if (contextNode == null) {
            log.warn("Did not find context node for " + node);
            return false;
        }
        return mayDoOnContext(user, contextNode, operation, true);
    }

    protected boolean mayDoOnContext(User user, MMObjectNode contextNode, Operation operation, boolean checkOwnRights) {
        return mayDoOnContext(user.getNode(), contextNode, operation, checkOwnRights);
    }

    public boolean mayDoOnContext(MMObjectNode userNode, MMObjectNode contextNode,
                                  Operation operation, boolean checkOwnRights) {
        if (disableContextChecks()) {
            return true;
        }

        Set<MMObjectNode> groupsAndUsers = getGroupsAndUsers(contextNode, operation);

        if (checkOwnRights) {
            if (groupsAndUsers.contains(userNode)) return true;
        }


        for (MMObjectNode group : groupsAndUsers) {
            if (! (group.getBuilder() instanceof Groups)) continue;
            if (log.isTraceEnabled()) {
                log.trace("checking group " + group);
            }

            if(Groups.getBuilder().contains(group, userNode)) {
                if (log.isDebugEnabled()) {
                    log.debug("User " + userNode.getStringValue("username") + " may " + operation + " according to context " + contextNode);
                }
                return true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("User " + userNode.getStringValue("username") + " may not " + operation + " according to context " + contextNode);
        }
        return false;

    }

    public boolean mayGrant(User user,  MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        if (user.getRank().getInt() >= Rank.ADMIN.getInt()) {
            return true; // admin may do everything
        }

        Groups groups = Groups.getBuilder();

        if (groupOrUserNode.getBuilder() instanceof Groups) {
            if (! groups.contains(groupOrUserNode, user.getNode()) || user.getRank().getInt() <= Rank.BASICUSER.getInt()) {
                return false; // must be 'high rank' member of group
            }
        } else {
            if (groupOrUserNode.equals(user.getNode())) {
                return false; // if not admin, you may not grant yourself rights. (and for admin it  is not necessary)
            }
            UserProvider users = Authenticate.getInstance().getUserProvider();
            if (users.getRank(groupOrUserNode).getInt() >= user.getRank().getInt()) {
                return false; // may not grant to users of higher/equal rank than yourself
            }
        }
        return mayDoOnContext(user, contextNode, operation, true); // you need to have the right yourself to grant it.
    }
    /**
     * @todo untested
     */
    public boolean mayRevoke(User user,  MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        if (user.getRank().getInt() >= Rank.ADMIN.getInt()) {
            return true; // admin may do everything
        }
        if (groupOrUserNode.getBuilder() instanceof Groups) {
            if (! Groups.getBuilder().contains(groupOrUserNode, user.getNode()) || user.getRank().getInt() <= Rank.BASICUSER.getInt()) {
                return false; // must be 'high rank' member of group
            }
        } else {
            if (groupOrUserNode.equals(user.getNode())) {
                return false; // if not admin, you may not revoke yourself rights. (and for admin it does not make sense
            }
            UserProvider users = Authenticate.getInstance().getUserProvider();
            if (users.getRank(groupOrUserNode).getInt() >= user.getRank().getInt()) {
                return false; // may not revoke from users of higher/equal rank than yourself
            }
        }
        return mayDoOnContext(user, contextNode, operation, true); // you need to have the right yourself to revoke it (otherwise you could not grant it back)
    }


    /**
     * @return a  Set of all groups and users which are allowed for the given operation (not recursively).
     */
    public  Set<MMObjectNode> getGroupsAndUsers(MMObjectNode contextNode, Operation operation) {
        Set<MMObjectNode> found = Caches.getOperationsCache().get(contextNode, operation);
        if (found == null) {
            found = new HashSet<MMObjectNode>();

            MMObjectBuilder users = Authenticate.getInstance().getUserProvider().getUserBuilder();
            found.addAll(getGroupsOrUsers(contextNode, operation, users));
            found.addAll(getGroupsOrUsers(contextNode, operation, Groups.getBuilder()));
            Caches.getOperationsCache().put(contextNode, operation, found);
        }
        return found;
    }


    /**
     *
     * @return A Collection of groups or users which are allowed for the given operation (not recursively)
     */

    protected Collection<MMObjectNode> getGroupsOrUsers(MMObjectNode contextNode, Operation operation, MMObjectBuilder groupsOrUsers) {
        InsRel rights = RightsRel.getBuilder();

        ChainedList<MMObjectNode> result = new ChainedList<MMObjectNode>();

        for (NodeSearchQuery q : getContextQueries()) {

            MMObjectBuilder contextBuilder = q.getBuilder();
            BasicSearchQuery query = new BasicSearchQuery(q, BasicSearchQuery.COPY_NORMAL);
            Step step = query.getSteps().get(0);
            BasicStepField numberStepField = new BasicStepField(step, contextBuilder.getField("number"));
            BasicFieldValueConstraint numberConstraint = new BasicFieldValueConstraint(numberStepField, Integer.valueOf(contextNode.getNumber()));

            BasicRelationStep relationStep = query.addRelationStep(rights, groupsOrUsers);
            relationStep.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);

            BasicStepField  operationStepField = new BasicStepField(relationStep, rights.getField("operation"));
            BasicFieldValueConstraint operationConstraint =  new BasicFieldValueConstraint(operationStepField, operation.toString());

            BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            constraint.addChild(numberConstraint);
            constraint.addChild(operationConstraint);

            query.setConstraint(constraint);
            query.removeFields();
            query.addFields(relationStep.getNext());

            try {
                result.addList(groupsOrUsers.getStorageConnector().getNodes(query, false));
            } catch (SearchQueryException sqe) {
                log.error(sqe.getMessage());
            }
        }
        return result;

    }

    public Authorization.QueryCheck check(User userContext, Query query, Operation operation) {
        if (userContext.getRank().getInt() >= Rank.ADMIN_INT) {
            return Authorization.COMPLETE_CHECK;
        } else {
            if (operation == Operation.READ && (canReadAll() || disableContextChecks())) {
                return Authorization.COMPLETE_CHECK;
            } else if (operation == Operation.READ) {
                Cache<String, ContextProvider.AllowingContexts> allowingContextsCache = Caches.getAllowingContextsCache();
                AllowingContexts ac = allowingContextsCache.get(userContext.getIdentifier());
                if (ac == null) {
                    // smart stuff for query-modification
                    SortedSet<String> disallowing = getDisallowingContexts(userContext, operation);
                    SortedSet<String> contexts;
                    boolean   inverse;
                    if (log.isDebugEnabled()) {
                        log.debug("disallowing: " + disallowing + " all " + getAllContexts());
                    }

                    // searching which is 'smallest': disallowing contexts, or allowing contexts.
                    if (disallowing.size() < (getAllContexts().size() / 2)) {
                        contexts = disallowing;
                        inverse = true;
                    } else {
                        contexts  = new TreeSet<String>(getAllContexts());
                        contexts.removeAll(disallowing);
                        inverse = false;
                    }
                    ac = new AllowingContexts(contexts, inverse);
                    allowingContextsCache.put(userContext.getIdentifier(), ac);
                }

                List<Step> steps = query.getSteps();
                Constraint constraint = null;

                // constraints on security objects
                {
                    Iterator<Step> i = steps.iterator();
                    while (i.hasNext()) {
                        Step step = i.next();
                        Constraint newConstraint = null;
                        if (step.getTableName().equals("mmbasegroups")) {
                            newConstraint = query.createConstraint(query.createStepField(step, "number"), userContext.getGroups()); // must be member of group to see group
                            if(operation != Operation.READ) { //
                                if (userContext.getRank().getInt() <= Rank.BASICUSER.getInt()) { // may no nothing, simply making the query result nothing: number = -1
                                    Constraint mayNothing = query.createConstraint(query.createStepField(step, "number"), Integer.valueOf(-1));
                                    return new Authorization.QueryCheck(true, mayNothing);
                                }
                            }
                        } else if (step.getTableName().equals("mmbaseranks")) { // higher ranks are none of your businuess (especially usefull for editors)
                            newConstraint = query.createConstraint(query.createStepField(step, "rank"), FieldCompareConstraint.LESS_EQUAL, Integer.valueOf(userContext.getRank().getInt()));
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
                        Constraint mayNothing = query.createConstraint(query.createStepField(query.getSteps().get(0), "number"), Integer.valueOf(-1));
                        return new Authorization.QueryCheck(true, mayNothing);
                    }
                }


                if (steps.size() * ac.contexts.size() < getMaxContextsInQuery()) {
                    Iterator<Step> i = steps.iterator();
                    while (i.hasNext()) {
                        Step step = i.next();
                        StepField field = query.createStepField(step, "owner");
                        Constraint newConstraint = query.createConstraint(field, ac.contexts);
                        if (ac.inverse) query.setInverse(newConstraint, true);

                        UserProvider users = Authenticate.getInstance().getUserProvider();

                        if (step.getTableName().equals(users.getUserBuilder().getTableName())) { // anybody may see own node
                            Constraint own = query.createConstraint(query.createStepField(step, "number"),
                                                                    Integer.valueOf(users.getUser(userContext.getIdentifier()).getNumber()));
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
                //not checking for other operations than READ: never mind, this is only used for read checks any way
                return Authorization.NO_CHECK;
            }
        }
    }



    protected boolean allows(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        return getGroupsAndUsers(contextNode, operation).contains(groupOrUserNode);
    }

    public  boolean grant(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        if (allows(contextNode, groupOrUserNode, operation)) {
            return true; // already allowed
        }

        // create a relation..
        if (mayGrant(user, contextNode, groupOrUserNode, operation)) {
            if (log.isServiceEnabled()) {
                log.service("Granting right " + operation + " on context " + contextNode  + " to group/user " + groupOrUserNode + " by " + user);
            }
            RightsRel rightsRel = RightsRel.getBuilder();
            MMObjectNode ownerContextNode = user.getNode().getNodeValue("defaultcontext");
            String ownerString;
            if (ownerContextNode != null) {
                ownerString = ownerContextNode.getStringValue("name");
            } else {
                ownerString = Contexts.DEFAULT_CONTEXT;
            }
            MMObjectNode newRight = rightsRel.getNewNode(ownerString, contextNode.getNumber(), groupOrUserNode.getNumber(), operation);
            boolean res = newRight.insert(ownerString) > 0;
            if (! res) {
                log.error("Failed to grant " + newRight);
            } else {
                log.debug("Granted " + newRight);
            }
            return res;

        } else {
            log.service("Granting right " + operation + " on context " + contextNode  + " to group/user " + groupOrUserNode + " by " + user + " failed because it it not allowed");
            return false;
        }
    }
    public boolean revoke(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation) {
        if (!allows(contextNode, groupOrUserNode, operation)) {
            return true; // already disallowed
        }

        if (mayRevoke(user, contextNode, groupOrUserNode, operation)) {
            if (log.isServiceEnabled()) {
                log.service("Revoking right " + operation + " on context " + contextNode + " to group " + groupOrUserNode + " by " + user);
            }
            RightsRel rights   = RightsRel.getBuilder();
            NodeSearchQuery q = new NodeSearchQuery(rights);
            BasicStepField snumber = q.getField(rights.getField("snumber"));
            BasicStepField dnumber = q.getField(rights.getField("dnumber"));
            BasicStepField op      = q.getField(rights.getField("operation"));
            BasicFieldValueConstraint c1  = new BasicFieldValueConstraint(snumber, Integer.valueOf(contextNode.getNumber()));
            BasicFieldValueConstraint c2  = new BasicFieldValueConstraint(dnumber, Integer.valueOf(groupOrUserNode.getNumber()));
            BasicFieldValueConstraint c3  = new BasicFieldValueConstraint(op, operation.toString());
            BasicCompositeConstraint cons = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
            cons.addChild(c1);
            cons.addChild(c2);
            cons.addChild(c3);
            q.setConstraint(cons);
            try {
                List<MMObjectNode> r = rights.getNodes(q);
                Iterator<MMObjectNode> i = r.iterator();
                while (i.hasNext()) {
                    MMObjectNode right = i.next();
                    rights.removeNode(right);
                }
            } catch (Exception sqe) {
                log.error(sqe.toString(), sqe);
                return false;
            }
            return true;
        } else {
            log.service("Revoking right " + operation + " on context " + contextNode + " to group/user " + groupOrUserNode + " by " + user + " failed because it it not allowed");
            return false;
        }
    }


    private MMObjectNode getNode(int i) {
        return queries.get(0).getBuilder().getNode(i);
    }


}
