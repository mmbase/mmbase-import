/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.cache.Cache;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

/**
 * Groups of users. A group can also contain other groups. Containing
 * is arranged by the 'containsrel' relations type.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Groups.java,v 1.23 2008-12-23 17:30:42 michiel Exp $
 */
public class Groups extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(Groups.class);

    /**
     * @return the MMObjectBuilder mmbasegroups cast to a Groups
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

    public  boolean contains(MMObjectNode containingGroup, MMObjectNode groupOrUser)  {
        return contains(containingGroup, groupOrUser.getNumber());
    }

    /**
     * Checks wether group or user identified by number is contained by group (also indirectly)
     */
    protected boolean contains(MMObjectNode containingGroupNode, int containedObject) {
        return contains(containingGroupNode, containedObject, new HashSet<Integer>());
    }


    /**
     * Checks wether group or user identified by number is contained by group.
     *
     */
    protected boolean contains(MMObjectNode containingGroupNode, int containedObject, Set<Integer> recurse) {
        int containingGroup = containingGroupNode.getNumber();
        String key = "" + containingGroup + "/" + containedObject;
        Cache<String, Boolean> containsCache = Caches.getContainsCache();
        Boolean result = containsCache.get(key);

        if (result == null) {
            int role       = mmb.getRelDef().getNumberByName("contains");
            InsRel insrel =  mmb.getRelDef().getBuilder(role);

            MMObjectBuilder object = mmb.getBuilder("object");
            BasicSearchQuery query = new BasicSearchQuery();
            Step step = query.addStep(object);
            BasicStepField numberStepField = new BasicStepField(step, object. getField("number"));
            BasicFieldValueConstraint numberConstraint = new BasicFieldValueConstraint(numberStepField, new Integer(containedObject));

            BasicRelationStep relationStep = query.addRelationStep(insrel, this);
            relationStep.setDirectionality(RelationStep.DIRECTIONS_SOURCE);

            query.setConstraint(numberConstraint);
            query.addFields(relationStep.getNext());

            List<MMObjectNode> resultList;
            try {
                resultList = storageConnector.getNodes(query, false);
            } catch (SearchQueryException sqe) {
                log.error(sqe.getMessage());
                resultList = new ArrayList<MMObjectNode>();
            }

            Iterator<MMObjectNode> i = resultList.iterator();

            result = Boolean.FALSE;
            while (i.hasNext()) {
                MMObjectNode group = i.next();

                if (group.getNumber() == containingGroup) {
                    log.trace("yes!");
                    result = Boolean.TRUE;
                    break;
                } else if (recurse != null) {
                    log.trace("recursively");
                    Integer  groupNumber = new Integer(group.getNumber());
                    if (! recurse.contains(groupNumber)) {
                        recurse.add(groupNumber);
                        if (contains(containingGroupNode, group.getNumber(), recurse)) {
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

    public SortedSet<Integer> getGroups(int containedObject) {
        SortedSet<Integer> result = new TreeSet<Integer>();
        try {
            Iterator<MMObjectNode>  nodes = getNodes(new NodeSearchQuery(this)).iterator();
            while (nodes.hasNext()) {
                MMObjectNode group = nodes.next();
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
        return n.getStringValue("name") + " (" + n.getNumber() + ")";
    }


    // needed to make SecurityOpeations Cache work?
    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        return o1.getNumber() == o2.getNumber();
    }
    public int hashCode(MMObjectNode o) {
        return 127 * o.getNumber();
    }


}
