/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import java.util.*;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.cache.Cache;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.storage.search.implementation.*;

/**
 * Groups of users. A group can also contain other groups. Containing
 * is arranged by the 'containsrel' relations type.
 * 
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Groups.java,v 1.6 2003-08-11 13:31:14 michiel Exp $
 * @see ContainsRel
 */
public class Groups extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Groups.class);

    public final static Argument[] ALLOW_ARGUMENTS = {
        new Argument("context",   String.class),
        new Argument("operation", String.class)
    };

    protected static Cache containsCache = new Cache(200) {
            public String getName()        { return "CCS:ContainedBy"; }
            public String getDescription() { return "group + group/user --> boolean"; }
        };


    // javadoc inherited
    public boolean init() {
        containsCache.putCache();
        CacheInvalidator.getInstance().addCache(containsCache);
        mmb.addLocalObserver(getTableName(),  CacheInvalidator.getInstance());
        mmb.addRemoteObserver(getTableName(), CacheInvalidator.getInstance());
        return super.init();
    }


    /**
     * @return the MMObjectBuilder mmbasegroups casted to a Groups
     */
    public static Groups getBuilder() {
        return (Groups) MMBase.getMMBase().getBuilder("mmbasegroups");
    }

    /**
     * Checks wether a a certain user is part of a certain group, either directly or indirectly.
     * @todo This could perhaps be just as logicly be implemented in Users rather than Groups (and groups becomes Dummy).
     */
    public boolean contains(MMObjectNode group, User user)  {
        if (log.isDebugEnabled()) {
            log.debug("Checking if user " + user + " is contained by group " + group + "(" + group.getNumber() + ")");
        }
        return contains(group,  user.getNode());
    }
    
    protected boolean contains(MMObjectNode containingGroup, MMObjectNode groupOrUser)  {
        return contains(containingGroup, groupOrUser.getNumber());
    }

    /**
     * Checks wether group or user identified by number is contained by group (also indirectly)
     */
    protected boolean contains(MMObjectNode containingGroupNode, int containedObject) {
        return contains(containingGroupNode, containedObject, true);
    }

    /**
     * Checks wether group or user identified by number is contained by group.
     */
    protected boolean contains(MMObjectNode containingGroupNode, int containedObject, boolean recurse) {
        int containingGroup = containingGroupNode.getNumber();
        if (log.isDebugEnabled()) {
            log.debug("Checking if user/group " + containedObject + " is contained by group " + containingGroupNode + "(" + containingGroup + ")");
        }
        String key = "" + containingGroup + "/" + containedObject;
        Boolean result = (Boolean) containsCache.get(key);

        if (result == null) {
            int role       = mmb.getRelDef().getNumberByName("contains");
            InsRel insrel =  mmb.getRelDef().getBuilder(role);
            Enumeration e  = insrel.getRelations(containedObject, getObjectType(), role);
            result = Boolean.FALSE;
            while(e.hasMoreElements()) {
                MMObjectNode relation    = (MMObjectNode) e.nextElement();
                int source = relation.getIntValue("snumber");
                //assert(source.parent instanceof Groups);               
                if (source  == containedObject) continue; // only search 'up', so number must represent destination.
                
                if (containingGroup == source) { // the found source is the requested group, we found it!
                    log.trace("yes!");
                    result = Boolean.TRUE;
                    break;
                } else if (recurse) { // recursively call on groups
                    log.trace("recursively");
                    if (contains(containingGroupNode, source)) {
                        result = Boolean.TRUE;
                        break;
                    }
                }
            }
            containsCache.put(key, result);
        }
        return result.booleanValue();
    }


    public SortedSet getGroups(int containedObject) {
        SortedSet result = new TreeSet();
        try {
            Iterator  nodes = getNodes(new NodeSearchQuery(this)).iterator();
            while (nodes.hasNext()) {
                MMObjectNode group = (MMObjectNode) nodes.next();
                if (contains(group, containedObject, false)) result.add(new Integer(group.getNumber()));
            }
        } catch (org.mmbase.storage.search.SearchQueryException sqe) {
            log.error(sqe.toString());
        }
        return result;
    }

    /**
     * unused, ie untested
    public void addGroup(User user, MMObjectNode group, Verify verify) {
        for (Enumeration enumeration = user.getNode().getRelations(); enumeration.hasMoreElements();) {
            MMObjectNode relation = getNode(((MMObjectNode)enumeration.nextElement()).getNumber());
            if (relation.parent instanceof ContainsRel) {
                MMObjectNode source = relation.getNodeValue("snumber");
                MMObjectNode destination = relation.getNodeValue("dnumber");
                if (destination.getNumber() == user.getNode().getNumber() && source.getBuilder() == this) {
                    MMObjectNode newRelation = ContainsRel.getBuilder().grantRightsTo(group, source);
                    verify.create(user, newRelation.getNumber());
                }
            }
        }
    }
     */


    /**
     * Wether users of the given group may do operation on a node of given context.
     * @return boolean
     */    
    protected boolean allows(MMObjectNode groupNode, String context, Operation operation) {
        Contexts contexts = Contexts.getBuilder();
        Set groups = contexts.getGroups(context, operation);
        return groups.contains(groupNode);
    }

    /**
     * Wether users of the given group may do operation on a node of given context, because
     * (one of) the parents of this group allow it.
     * 
     * @return boolean
     */    
    protected boolean parentsAllow(MMObjectNode groupNode, String context, Operation operation) {

        Contexts contexts = Contexts.getBuilder();
        Set groups = contexts.getGroups(context, operation);
        Iterator i = groups.iterator();
        while (i.hasNext()) {
            MMObjectNode containingGroup = (MMObjectNode) i.next();
            if (contains(containingGroup, groupNode)) return true;
        }
        return false;
    }

    /**
     * Grand/revoke all
     * @untested
     */
    protected void grantAll(MMObjectNode groupNode, List contexts, Operation operation, User  user) {
        SortedSet allContexts = Contexts.getBuilder().getAllContexts();
        Iterator i = allContexts.iterator();
        while (i.hasNext()) {
            String context = (String) i.next();
            if (contexts.contains(context)) {
                grant(groupNode, context, operation, user);
            } else {
                revoke(groupNode, context, operation, user);
            }
        }        
    }

    /**
     * @untested
     */

    protected boolean revoke(MMObjectNode groupNode, String context, Operation operation, User  user) {
        if (!allows(groupNode, context, operation)) return true; // already disallowed
        if (mayRevoke(groupNode, context, operation, user)) {
            Contexts contexts = Contexts.getBuilder();
            MMObjectNode contextNode = contexts.getContextNode(context);
            NodeSearchQuery q = new NodeSearchQuery(RightsRel.getBuilder());
            BasicStepField snumber = q.getField(contexts.getField("snumber"));
            BasicStepField dnumber = q.getField(contexts.getField("dnumber"));
            BasicFieldValueConstraint c1 = new BasicFieldValueConstraint(snumber, new Integer(contextNode.getNumber()));
            BasicFieldValueConstraint c2 = new BasicFieldValueConstraint(dnumber, new Integer(groupNode.getNumber()));           
            BasicCompositeConstraint cons = new BasicCompositeConstraint(BasicCompositeConstraint.LOGICAL_AND);
            cons.addChild(c1);
            cons.addChild(c2);
            q.setConstraint(cons);
            try {
                Iterator i = contexts.getNodes(q).iterator();
                while (i.hasNext()) {
                    MMObjectNode right = (MMObjectNode) i.next();
                    RightsRel.getBuilder().removeNode(right);
                }
            } catch (Exception sqe) {
                log.error(sqe.toString());
                return false;
            }
            return true;
        }
        return false;
    }
    


    /**
     * @untested
     */
    protected boolean grant(MMObjectNode groupNode, String context, Operation operation, User  user) {
        if (allows(groupNode, context, operation)) return true; // already allowed
        // create a relation..
        if (mayGrant(groupNode, context, operation, user)) {
            RightsRel rightsRel = RightsRel.getBuilder();
            Contexts contexts = Contexts.getBuilder();
            MMObjectNode contextNode = contexts.getContextNode(context);
            return rightsRel.getNewNode(user.getOwnerField(), contextNode.getNumber(), groupNode.getNumber()) > 0;
        } else {
            return false;
        }
    }


    /**
     * @untested
     */

    protected boolean mayRevoke(MMObjectNode groupNode, String context, Operation operation, User user) {
        if (user.getRank().getInt() >= Rank.ADMIN.getInt()) return true; // admin may do everything
        if (! contains(groupNode, user.getNode().getNumber()) || user.getRank().getInt() <= Rank.BASICUSER.getInt()) return false; // must be 'high rank' member of group 
        return true;
    }



    /**
     * @untested
     */
    protected boolean mayGrant(MMObjectNode groupNode, String context, Operation operation, User user) {
        if (user.getRank().getInt() >= Rank.ADMIN.getInt()) return true; // admin may do everything

        if (! contains(groupNode, user.getNode().getNumber()) || user.getRank().getInt() <= Rank.BASICUSER.getInt()) return false; // must be 'high rank' member of group 
        Contexts contexts = Contexts.getBuilder();
        MMObjectNode contextNode = contexts.getContextNode(context);
        return contexts.mayDo(user, contextNode, operation); // you need to have the right yourself to grant it.
    }

    protected Object executeFunction(MMObjectNode node, String function, List args) {
        log.debug("executefunction of abstractservletbuilder");
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, function, empty);
            info.put("allow",        "" + ALLOW_ARGUMENTS + " Wether operation may be done by members of this group");
            info.put("parentsallow", "" + ALLOW_ARGUMENTS + " Wether operation may be done by members of this group, also because of parents");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals("allows")) {
            Arguments a;
            if (args instanceof Arguments) {
                a = (Arguments) args;
            } else {
                a = new Arguments(ALLOW_ARGUMENTS, args);
            }
            if (allows(node, (String) a.get("context"), Operation.getOperation((String) a.get("operation")))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (function.equals("parentsallow")) {
            Arguments a;
            if (args instanceof Arguments) {
                a = (Arguments) args;
            } else {
                a = new Arguments(ALLOW_ARGUMENTS, args);
            }
            if (parentsAllow(node, (String) a.get("context"), Operation.getOperation((String) a.get("operation")))) {
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


    // needed to make SecurityOpeations Cache work?
    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        return o1.getNumber() == o2.getNumber();
    }


}
