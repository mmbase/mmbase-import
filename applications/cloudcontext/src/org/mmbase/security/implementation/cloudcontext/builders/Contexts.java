/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import java.util.*;

import org.mmbase.bridge.Query;
import org.mmbase.module.core.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.util.functions.*;
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
 * @version $Id: Contexts.java,v 1.66 2009-04-07 07:59:31 nklasens Exp $
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
    private boolean disableContextChecks = false;

    private int     maxContextsInQuery = DEFAULT_MAX_CONTEXTS_IN_QUERY;


    private ContextProvider provider;

    protected ContextProvider createProvider() {
        return new BasicContextProvider(Contexts.this) {
            @Override  protected boolean isAllContextsPossible() {
                return Contexts.this.allContextsPossible;
            }
            @Override  protected boolean canReadAll() {
                return Contexts.this.readAll;
            }

            @Override  protected int getMaxContextsInQuery() {
                return Contexts.this.maxContextsInQuery;
            }
            @Override  protected boolean disableContextChecks() {
                return Contexts.this.disableContextChecks;
            }
        };
    }

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

        s = getInitParameters().get("disableContextChecks");
        if (! "".equals(s) && s != null) {
            disableContextChecks = "true".equals(s);
        }
        provider = createProvider();

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


    protected boolean isOwnNode(User user, MMObjectNode node) {
        return Authenticate.getInstance().getUserProvider().isOwnNode(user, node);
    }


    protected boolean mayDo(MMObjectNode user, MMObjectNode contextNode, Operation operation, boolean checkOwnRights) {
        return provider.mayDoOnContext(user, contextNode, operation, checkOwnRights);
    }



    /**
     * Implements check function with same arguments of Authorisation security implementation
     * @see Verify#check(UserContext, Query, Operation)
     */

    public Authorization.QueryCheck check(User userContext, Query query, Operation operation) {
        return provider.check(userContext, query, operation);
    }

    public final MMObjectNode getDefaultContextNode() {
        return getProvider().getContextNode(DEFAULT_CONTEXT);
     }

    //********************************************************************************
    // EDIT FUNCTIONS
    //********************************************************************************



    /**
     * Makes sure unique values and not-null's are filed
     */
    public void setDefaults(MMObjectNode node) {
        setUniqueValue(node, "name", "context");
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
            info.put("may",          "" + MAY_PARAMETERS + " Checks a right for another user than yourself");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
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
