/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBaseObserver;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @version $Id: User.java,v 1.1 2003-05-22 17:14:03 michiel Exp $
 */
public class User extends UserContext implements MMBaseObserver {
    private static Logger log = Logging.getLoggerInstance(User.class.getName());
    private MMObjectNode node;
    private long key;

    /**
     * @javadoc
     */
    User(MMObjectNode mmobjectnode, long l) {
        node = null;
        key = 0L;
        node = mmobjectnode;
        key = l;
        Users.getBuilder().addLocalObserver(this);
    }

    /**
     * @javadoc
     */
    public String getIdentifier()  {
        if (node==null) {
            return "anonymous";
        } else {
            return Users.getBuilder().getUserName(node);
        }
    }

    /**
     * @javadoc
     */
    public Rank getRank() throws SecurityException {
        if (node==null) {
            return Rank.ANONYMOUS;
        } else {
            return Users.getBuilder().getRank(node);
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
        return (node!=null) && Users.getBuilder().isValid(node);
    }

    /**
     * Wether this User equals some object. 
     */
    public boolean equals(Object object) {
        if (!(object instanceof MMObjectNode)) return super.equals(object);
        MMObjectNode otherNode = (MMObjectNode) object;
        return (otherNode != null) && (node.getNumber() == otherNode.getNumber());
    }

    /**
     * @javadoc
     */
    String getDefaultContext() {
        if (node==null) {
            return "";
        } else {
            return Users.getBuilder().getDefaultContext(node);
        }
    }

    /**
     * An MMObjectNode (of type 'mmbaseusers') is associated with this User object.
     * This function returns it.
     */
    public MMObjectNode getNode() {
        if (node==null) throw new SecurityException("Account has been removed.");
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
