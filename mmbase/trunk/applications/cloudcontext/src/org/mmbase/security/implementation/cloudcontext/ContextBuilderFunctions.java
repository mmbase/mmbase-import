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
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.*;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextBuilderFunctions.java,v 1.3 2009-01-05 12:43:23 michiel Exp $
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
            Iterator<MMObjectNode> i = groupsAndUsers.iterator();
            while (i.hasNext()) {
                MMObjectNode containingGroup = i.next();
                if (groups.contains(containingGroup, groupOrUserNode)) return true;
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
}
