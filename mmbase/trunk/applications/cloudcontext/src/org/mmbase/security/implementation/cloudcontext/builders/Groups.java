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
 * @version $Id: Groups.java,v 1.13 2003-11-27 08:32:22 pierre Exp $
 * @see ContainsRel
 */
public class Groups extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Groups.class);


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
        return contains(group,  user.getNode());
    }

    protected boolean contains(MMObjectNode containingGroup, MMObjectNode groupOrUser)  {
        return contains(containingGroup, groupOrUser.getNumber());
    }

    /**
     * Checks wether group or user identified by number is contained by group (also indirectly)
     */
    protected boolean contains(MMObjectNode containingGroupNode, int containedObject) {
        return contains(containingGroupNode, containedObject, new HashSet());
    }

    /**
     * Checks wether group or user identified by number is contained by group.
     *
     */
    protected boolean contains(MMObjectNode containingGroupNode, int containedObject, Set recurse) {
        int containingGroup = containingGroupNode.getNumber();
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
                } else if (recurse != null) { // recursively call on groups
                    log.trace("recursively");
                    if (! recurse.contains(new Integer(source))) {
                        recurse.add(new Integer(source));
                        if (contains(containingGroupNode, source, recurse)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                }
            }
            containsCache.put(key, result);
        }
        return result.booleanValue();
    }

    /**
     * Returns all groups, which are (directly or indirectly) containing the given object (user/group)
     */

    public SortedSet getGroups(int containedObject) {
        SortedSet result = new TreeSet();
        try {
            Iterator  nodes = getNodes(new NodeSearchQuery(this)).iterator();
            while (nodes.hasNext()) {
                MMObjectNode group = (MMObjectNode) nodes.next();
                if (contains(group, containedObject)) {
                    result.add(new Integer(group.getNumber()));
                } else {
                }
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

    public void setDefaults(MMObjectNode node) {
        setUniqueValue(node, "name", "group");
    }


    public String toString(MMObjectNode n) {
        return n.getStringValue("name");
    }


    // needed to make SecurityOpeations Cache work?
    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        return o1.getNumber() == o2.getNumber();
    }


}
