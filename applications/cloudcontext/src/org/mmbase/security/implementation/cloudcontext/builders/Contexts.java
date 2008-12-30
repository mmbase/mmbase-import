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
import org.mmbase.bridge.Query;

import java.util.*;

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.cache.Cache;
import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;
import org.mmbase.cache.AggregatedResultCache;

/**
 * Representation of a 'context', which can be read as a valid value of the 'owner' field of any
 * object in MMBase. Rights are distributed using this thing. This is part of cloud context
 * security, so the 'context' values need to be present in the cloud.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Contexts.java,v 1.63 2008-12-30 17:49:44 michiel Exp $
 * @see    org.mmbase.security.implementation.cloudcontext.Verify
 * @see    org.mmbase.security.Authorization
 */
public class Contexts extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(Contexts.class);

    /**
     *
     * @javadoc
     */
    public static final String DEFAULT_CONTEXT = "default";  // default used to be 'admin', but does that make sense?
    static final int DEFAULT_MAX_CONTEXTS_IN_QUERY = 50;
    public final static Parameter<String> PARAMETER_OPERATION   = new Parameter<String>("operation", String.class);
    public final static Parameter<String> PARAMETER_GROUPORUSER = new Parameter<String>("grouporuser", String.class);

    private final static Parameter[] ALLOWS_PARAMETERS = {
        PARAMETER_GROUPORUSER,
        PARAMETER_OPERATION
    };


    public final static Parameter[] GRANT_PARAMETERS = {
        PARAMETER_GROUPORUSER,
        PARAMETER_OPERATION,
        Parameter.USER
    };

    public final static Parameter[] REVOKE_PARAMETERS    = GRANT_PARAMETERS;
    public final static Parameter[] MAYREVOKE_PARAMETERS = REVOKE_PARAMETERS;


    public final static Parameter[] MAY_PARAMETERS = {
        Parameter.USER,
        new Parameter<String>("usertocheck",  String.class),
        PARAMETER_OPERATION

    };




    /**
     * Things which must be cleared when some security objects change, can all be collected in this map
     */

    //protected static Map<String,SortedSet<String>>  invalidableObjects = new HashMap<String,SortedSet<String>>();



    private boolean readAll = false;
    private boolean allContextsPossible = true; // if you want to use security for workflow, then you want this to be false


    private int     maxContextsInQuery = DEFAULT_MAX_CONTEXTS_IN_QUERY;


    private BasicContextProvider provider = new BasicContextProvider(Contexts.this) {
            @Override  protected boolean isAllContextsPossible() {
                return Contexts.this.allContextsPossible;
            }
            @Override  protected boolean canReadAll() {
                return Contexts.this.readAll;
            }

            @Override  protected int getMaxContextsInQuery() {
                return Contexts.this.maxContextsInQuery;
            }
        };


    /**
     * @javadoc
     */
    public boolean init() {
        String s = getInitParameters().get("readall");
        readAll = "true".equals(s);

        s = getInitParameters().get("allcontextspossible");
        allContextsPossible = ! "false".equals(s);

        s = getInitParameters().get("maxcontextsinquery");
        if (! "".equals(s) && s != null) {
            maxContextsInQuery = Integer.parseInt(s);
        }


        return super.init();
    }

    /**
     * Staticly receives the MMObjectBuilder instance (cast to Contexts). A utility function.
     */
    public static Contexts getBuilder() {
        return (Contexts) MMBase.getMMBase().getBuilder("mmbasecontexts");
    }


    public ContextProvider getProvider() {
        return provider;
    }




    /**
     * Implements check function with same arguments of Authorisation security implementation
     * @see Verify#check(UserContext, int, Operation)
     */
    private  boolean mayDo(User user, int nodeId, Operation operation) throws SecurityException {
        // retrieve the node
        MMObjectNode node       = getNode(nodeId);
        return provider.mayDo(user, node, operation);
    }


    protected boolean isOwnNode(User user, MMObjectNode node) {
        return Authenticate.getInstance().getUserProvider().isOwnNode(user, node);
    }

    /**
     * Returns whether user may do operation on a node with given context.
     */

    protected boolean mayDo(User user, MMObjectNode contextNode, Operation operation) {
        return mayDo(user.getNode(), contextNode, operation, true);
    }

    protected boolean mayDo(MMObjectNode user, MMObjectNode contextNode, Operation operation, boolean checkOwnRights) {

        Set<MMObjectNode> groupsAndUsers = provider.getGroupsAndUsers(contextNode, operation);

        if (checkOwnRights) {
            if (groupsAndUsers.contains(user)) return true;
        }

        Iterator<MMObjectNode> iter = groupsAndUsers.iterator();
        // now checking if this user is in one of these groups.
        while (iter.hasNext()) {
            MMObjectNode group = iter.next();
            if (! (group.getBuilder() instanceof Groups)) continue;
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
     * Implements check function with same arguments of Authorisation security implementation
     * @see Verify#check(UserContext, Query, Operation)
     */

    public Authorization.QueryCheck check(User userContext, Query query, Operation operation) {
        return provider.check(userContext, query, operation);
    }



    //********************************************************************************
    // EDIT FUNCTIONS
    //********************************************************************************


    /**
     * @javadoc
     */
    protected boolean grant(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, User user) {
        return provider.grant(user, contextNode, groupOrUserNode, operation);
    }


    /**
     * Makes sure unique values and not-null's are filed
     */
    public void setDefaults(MMObjectNode node) {
        setUniqueValue(node, "name", "context");
    }





    /**
     */
    protected boolean mayRevoke(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, User user) {
        return provider.mayRevoke(user, contextNode, groupOrUserNode, operation);
    }



    /**
     * @javadoc
     */
    protected boolean revoke(MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation, User user) {
        return provider.revoke(user, contextNode, groupOrUserNode, operation);
    }

    /**
     * @javadoc
     */
    protected MMObjectNode getUserNode(UserContext user) {
        return ((User) user).getNode();
    }

    protected MMObjectNode getGroupOrUserNode(Parameters a) {
        MMObjectNode groupOrUser = getNode(a.getString(PARAMETER_GROUPORUSER));
        if (groupOrUser == null) throw new IllegalArgumentException("There is no node with id '" + a.get(PARAMETER_GROUPORUSER) + "'");

        MMObjectBuilder parent = groupOrUser.getBuilder();
        MMObjectBuilder userBuilder = Authenticate.getInstance().getUserProvider().getUserBuilder();
        if (! (parent instanceof Groups || userBuilder.getClass().isInstance(parent))) {
            throw new IllegalArgumentException("Node '" + a.get(PARAMETER_GROUPORUSER) + "' does not represent a group or a user");
        }
        return groupOrUser;
    }

    protected Object executeFunction(MMObjectNode node, String function, List<?> args) {
        if (log.isDebugEnabled()) {
            log.trace("executefunction of contexts " + function + " " + args);
        }
        if (function.equals("info")) {
            List<Object> empty = new ArrayList<Object>();
            Map<String,String> info = (Map<String,String>) super.executeFunction(node, function, empty);
            info.put("grant",        "" + GRANT_PARAMETERS + " Grant a right");
            info.put("revoke",       "" + REVOKE_PARAMETERS + " Revoke a right");
            info.put("mayrevoke",    "" + MAYREVOKE_PARAMETERS + " Check if user may revoke a right");
            info.put("may",          "" + MAY_PARAMETERS + " Checks a right for another user than yourself");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals("grant")) {
            Parameters a = Functions.buildParameters(GRANT_PARAMETERS, args);
            return grant(node, getGroupOrUserNode(a), Operation.getOperation(a.getString(PARAMETER_OPERATION)), (User) a.get("user"));
        } else if (function.equals("revoke")) {
            Parameters a = Functions.buildParameters(REVOKE_PARAMETERS, args);
            return revoke(node, getGroupOrUserNode(a), Operation.getOperation(a.getString(PARAMETER_OPERATION)), (User) a.get("user"));
        } else if (function.equals("mayrevoke")) {
            Parameters a = Functions.buildParameters(MAYREVOKE_PARAMETERS, args);
            if (mayRevoke(node, getGroupOrUserNode(a), Operation.getOperation(a.getString(PARAMETER_OPERATION)), (User) a.get("user"))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("may")) {
            Parameters a = Functions.buildParameters(MAY_PARAMETERS, args);
            MMObjectNode checkingUser = getUserNode(a.get(Parameter.USER));
            if (checkingUser == null) {
                throw new SecurityException("Self was not supplied");
            }
            // find the user first, the check if the current user actually has rights on the object
            UserProvider users = Authenticate.getInstance().getUserProvider();
            MMObjectNode userToCheck = users.getUserBuilder().getNode(a.getString("usertocheck"));
            if (userToCheck == null) { // the user is null?
                // I don't know then,
                // yes perhaps?
                return Boolean.TRUE;
            }

            // admin bypasses security system (maydo(mmobjectnode ... does not check for this)
            if (users.getRank(checkingUser).getInt() < Rank.ADMIN_INT) {
                if ((! mayDo(checkingUser, provider.getContextNode(userToCheck), Operation.READ, true))) {
                    throw new SecurityException("You " + checkingUser + " / " + users.getRank(checkingUser) + " are not allowed to check user '" + userToCheck + "' of context '" + provider.getContextNode(userToCheck) + "' (you have no read rights on that context)");
                }

            }
            // MMObjectNode contextNode = getContextNode(node);

            if (mayDo(userToCheck, node, Operation.getOperation(a.getString(PARAMETER_OPERATION)), true)) {
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





}
