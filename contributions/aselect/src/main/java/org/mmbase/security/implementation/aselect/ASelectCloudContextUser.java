/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.aselect;

import org.mmbase.security.SecurityException;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.security.implementation.cloudcontext.builders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import java.util.*;


/**
 * CloudContext compatible User object.
 *
 * @author Michiel Meeuwissen (Publieke Omroep)
 *
 * @version $Id$
 * @since  MMBase-1.8
 * @see ASelectAuthentication
 */

public class ASelectCloudContextUser extends org.mmbase.security.implementation.cloudcontext.User {

    private static final long serialVersionUID = 1;

    private static final Logger log = Logging.getLoggerInstance(ASelectCloudContextUser.class);

    protected ASelectCloudContextUser(MMObjectNode node, long number, String app) {
        super(node, number, app);
    }

    private String rank = null;

    // constructor, perhaps needs more argumetns
    protected ASelectCloudContextUser(String userName, long number, String app, String rank) {
        super(getUser(userName, rank), number, app);
        if (! "".equals(rank)) {
            this.rank = rank;
        }
    }

        // javadoc inherited
    public Rank getRank() throws SecurityException {
        if (rank != null) {
            Rank r =  Rank.getRank(rank);
            if (r != null) {
                rank = r.toString();
                return r;
            } else {
                log.error("Rank member was unknown '" + rank + "'");
            }
        }
        return super.getRank();
    }

    protected static MMObjectNode getUser(String userName, String rank) {
        Users users = Users.getBuilder();
        MMObjectNode node;
        if (userName == null) {
            node = users.getUserByRank(rank, userName);
            if (node == null ) {
                return null;
            }
            log.debug("Class authentication to rank " + rank + " found node " + node);
        } else {
            node = users.getUser(userName);
        }
        if (node == null) {
            // Since the user is authenticated by A-Select, the mmbase-users object should exist.
            // So if not, create it, this is the first time this user logs in.

            log.service("No user found for user '" + userName + "', creating one");
            node = users.getNewNode(userName);
            node.setValue("username", userName);
            node.insert(userName);


            Ranks ranks = Ranks.getBuilder();
            MMObjectNode rankNode = ranks.getRankNode(Rank.BASICUSER);

            log.service("Ranking user with rank node " + rankNode);

            MMObjectNode relationNode = MMBase.getMMBase().getInsRel().getNewNode(userName);

            relationNode.setValue("snumber", node.getNumber());
            relationNode.setValue("dnumber", rankNode.getNumber());
            relationNode.setValue("rnumber", MMBase.getMMBase().getRelDef().getNumberByName("rank"));
            relationNode.setValue("dir", 2);

            if (relationNode.insert(userName) ==  -1) {
                throw new RuntimeException("Could not relate rank-node to user-node");
            }

            Groups groups = Groups.getBuilder();
            MMObjectNode defaultGroup = groups.getNode("mayreadallgroup");
            if (defaultGroup == null) {
                log.warn("No group with alias 'mayreadallgroup' found, cannot grant rights to new user");
            } else {
                relationNode = MMBase.getMMBase().getInsRel().getNewNode(userName);

                relationNode.setValue("snumber", defaultGroup.getNumber());
                relationNode.setValue("dnumber", node.getNumber());
                relationNode.setValue("rnumber", MMBase.getMMBase().getRelDef().getNumberByName("contains"));
                relationNode.setValue("dir", 2);

                if (relationNode.insert(userName) ==  -1) {
                    throw new RuntimeException("Could not relate group-node to user-node");
                }
                log.service("Grouping user " + node.getNumber() + " with group node " + defaultGroup.getNumber() + " " + relationNode);
            }

        }
        return node;
    }

    /**
     * Only overriden because of changed scope. (ASelectAuthentication is a friend).
     */
    protected long getKey() {
        return super.getKey();
    }

}
