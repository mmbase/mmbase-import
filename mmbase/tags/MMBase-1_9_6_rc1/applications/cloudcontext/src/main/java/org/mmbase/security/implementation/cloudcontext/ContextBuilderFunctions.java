/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.security.implementation.cloudcontext.builders.Groups;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.security.Operation;
import org.mmbase.security.UserContext;
import org.mmbase.security.Rank;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.*;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * MMBase-1.9.1
 */
public class ContextBuilderFunctions {

    private static final Logger log = Logging.getLoggerInstance(ContextBuilderFunctions.class);

    public static boolean parentsallow(@Name("node")        Node context,
                                       @Name("grouporuser") Node groupOrUser,
                                       @Name("operation")   Operation operation) {
        try {
            Groups groups = Groups.getBuilder();

            MMObjectNode contextNode = groups.getNode(context.getNumber());
            MMObjectNode groupOrUserNode = groups.getNode(groupOrUser.getNumber());

            Set<MMObjectNode> groupsAndUsers = ((BasicContextProvider) Verify.getInstance().getContextProvider()).getGroupsAndUsers(contextNode, operation); // TODO Casting.
            for (MMObjectNode containingGroup : groupsAndUsers) {
                if (groups.contains(containingGroup, groupOrUserNode)) {
                    return true;
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public static boolean allows(@Name("node") Node context,
                                 @Name("grouporuser") Node groupOrUser,
                                 @Name("operation") Operation operation) {
        Groups groups = Groups.getBuilder();

        MMObjectNode contextNode = groups.getNode(context.getNumber());
        MMObjectNode groupOrUserNode = groups.getNode(groupOrUser.getNumber());
        BasicContextProvider prov = (BasicContextProvider) Verify.getInstance().getContextProvider();
        Collection<MMObjectNode> gau = prov.getGroupsAndUsers(contextNode, operation);
        return gau.contains(groupOrUserNode);
    }

    public static  boolean maygrant(@Name("node") Node  context,
                                    @Name("grouporuser") Node groupOrUser,
                                    @Name("operation") Operation operation,
                                    @Name("user") UserContext user) {
        Groups groups = Groups.getBuilder();

        MMObjectNode contextNode = groups.getNode(context.getNumber());
        MMObjectNode groupOrUserNode = groups.getNode(groupOrUser.getNumber());
        return Verify.getInstance().getContextProvider().mayGrant((User) user, contextNode, groupOrUserNode, operation);

    }

    public static  boolean mayrevoke(@Name("node") Node  context,
                                     @Name("grouporuser") Node groupOrUser,
                                     @Name("operation") Operation operation,
                                     @Name("user") UserContext user) {
        Groups groups = Groups.getBuilder();

        MMObjectNode contextNode = groups.getNode(context.getNumber());
        MMObjectNode groupOrUserNode = groups.getNode(groupOrUser.getNumber());
        return Verify.getInstance().getContextProvider().mayRevoke((User) user, contextNode, groupOrUserNode, operation);

    }

    public static  boolean grant(@Name("node") Node  context,
                                 @Name("grouporuser") Node groupOrUser,
                                 @Name("operation") Operation operation,
                                 @Name("user") UserContext user) {
        Groups groups = Groups.getBuilder();

        MMObjectNode contextNode = groups.getNode(context.getNumber());
        MMObjectNode groupOrUserNode = groups.getNode(groupOrUser.getNumber());
        return Verify.getInstance().getContextProvider().grant((User) user, contextNode, groupOrUserNode, operation);

    }
    public static  boolean revoke(@Name("node") Node  context,
                                  @Name("grouporuser") Node groupOrUser,
                                  @Name("operation") Operation operation,
                                  @Name("user") UserContext user) {
        Groups groups = Groups.getBuilder();

        MMObjectNode contextNode = groups.getNode(context.getNumber());
        MMObjectNode groupOrUserNode = groups.getNode(groupOrUser.getNumber());
        return Verify.getInstance().getContextProvider().revoke((User) user, contextNode, groupOrUserNode, operation);

    }

    /**
     * @since MMBase-1.9.2
     */
    public static boolean may(@Name("node") Node  context,
                              @Name("user") UserContext user,
                              @Name("usertocheck") String usertocheck,
                              @Name("operation") Operation operation) {

        MMObjectNode checkingUser = ((User) user).getNode();
        if (checkingUser == null) {
            throw new SecurityException("Self was not supplied");
        }
        // find the user first, the check if the current user actually has rights on the object
        UserProvider users = Authenticate.getInstance().getUserProvider();
        MMObjectNode userToCheck = users.getUserBuilder().getNode(usertocheck);
        if (userToCheck == null) { // the user is null?
            // I don't know then,
            // yes perhaps?
            return true;
        }

        ContextProvider provider = Verify.getInstance().getContextProvider();

        // admin bypasses security system (maydo(mmobjectnode ... does not check for this)
        if (users.getRank(checkingUser).getInt() < Rank.ADMIN_INT) {
            if ((! provider.mayDoOnContext(checkingUser, provider.getContextNode(userToCheck), Operation.READ, true))) {
                throw new SecurityException("You " + checkingUser + " / " + users.getRank(checkingUser) + " are not allowed to check user '" + userToCheck + "' of context '" + provider.getContextNode(userToCheck) + "' (you have no read rights on that context)");
            }

        }
        Groups groups = Groups.getBuilder();
        MMObjectNode contextNode = groups.getNode(context.getNumber());
        return provider.mayDoOnContext(userToCheck, contextNode, operation, true);
    }
}
