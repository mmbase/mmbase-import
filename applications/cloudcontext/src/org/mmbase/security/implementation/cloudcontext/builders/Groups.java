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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Groups of users. A group can also contain other groups. Containing
 * is arranged by the 'containsrel' relations type.
 * 
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Groups.java,v 1.2 2003-05-23 12:05:13 michiel Exp $
 * @see ContainsRel
 */
public class Groups extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Groups.class.getName());

    // javadoc inherited
    public boolean init() {
        mmb.addLocalObserver(getTableName(), CacheInvalidator.getInstance());
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
        log.debug("Checking if user " + user + " is contained by group " + group + "(" + group.getNumber() + ")");
        return contains(group,  user.getNode().getNumber());
    }
    
    /**
     * Checks wether group or user identified by number is contained by group.
     */
    protected boolean contains(MMObjectNode group, int number)  {
        if (log.isDebugEnabled()) {
            log.debug("Checking if user/group " + number + " is contained by group " + group + "(" + group.getNumber() + ")");
        }
        int role       = mmb.getRelDef().getNumberByName("contains");
        InsRel insrel =  mmb.getRelDef().getBuilder(role);
        Enumeration e  = insrel.getRelations(number, getObjectType(), role);
        while(e.hasMoreElements()) {
            MMObjectNode relation    = (MMObjectNode) e.nextElement();
            int source = relation.getIntValue("snumber");
            //assert(source.parent instanceof Groups);

            if (source  == number) continue; // only search 'up', so number must represent destination.

            if (group.getNumber() == source) { // the found source is the requested group, we found it!
                log.trace("yes!");
                return true;
            } else { // recursively call on groups
                log.trace("recursively");
                if (contains(group, source)) {
                    return true;
                }
            }
        }
        log.trace("no!");
        return false;
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

    public String toString(MMObjectNode n) {
        return n.getStringValue("name");
    }

}
