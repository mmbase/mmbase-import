/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import java.util.*;
import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBaseObserver;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Implementation of UserContext (the security presentation of a User).
 * Most implementation is delegated to the Users builder.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: User.java,v 1.11 2004-01-19 17:27:24 michiel Exp $
 * @see    org.mmbase.security.implementation.cloudcontext.builders.Users
 */
public class User extends UserContext implements MMBaseObserver {
    private static final Logger log = Logging.getLoggerInstance(User.class);
    protected MMObjectNode node;
    private long key;

    /**
     * @javadoc
     */
    User(MMObjectNode n, long l) {
        node = n;
        key = l;
        Users.getBuilder().addLocalObserver(this);
    }

    // javadoc inherited
    public String getIdentifier()  {
        if (node == null) {
            return "anonymous";
        } else {
            return Users.getBuilder().getUserName(node);
        }
    }


    // javadoc inherited
    public Rank getRank() throws SecurityException {
        if (node == null) {
            return Rank.ANONYMOUS;
        } else {
            return Users.getBuilder().getRank(node);
        }
    }

    // javadoc inherited
    public String getOwnerField() {
        if (node == null) {
            return "system";
        } else {
            return Users.getBuilder().getDefaultContext(node);
        }
    }


    /**
     * @javadoc
     */
    long getKey() {
        return key;
    }

    /**
     * @javadoc
     */
    boolean isValid() {
        return (node == null) || Users.getBuilder().isValid(node);
    }


    public SortedSet getGroups() {
        return Groups.getBuilder().getGroups(node.getNumber());
    }


    /**
     * An MMObjectNode (of type 'mmbaseusers') is associated with this User object.
     * This function returns it.
     */
    public MMObjectNode getNode() {
        if (node == null) throw new SecurityException("Account has been removed.");
        return node;
    }

    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        return nodeChanged(number,ctype);
    }

    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        return nodeChanged(number,ctype);
    }

    private boolean nodeChanged(String number,String ctype) {
        if ((node!=null) && (node.getNumber()==Integer.parseInt(number))) {
            if (ctype.equals("d")) {
                node = null; // invalidate
            }
            if (ctype.equals("c")) {
                node = Users.getBuilder().getNode(number);
            }
        }
        return true;
    }

}
