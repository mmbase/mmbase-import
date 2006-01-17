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

/**
 * Implementation of UserContext (the security presentation of a User).
 * Most implementation is delegated to the Users builder.
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: User.java,v 1.18 2006-01-17 21:28:18 michiel Exp $
 * @see    org.mmbase.security.implementation.cloudcontext.builders.Users
 */
public class User extends BasicUser implements MMBaseObserver {

    private static final long serialVersionUID = 1;

    protected MMObjectNode node;
    protected long key;

    /**
     * @javadoc
     */
    protected User(MMObjectNode n, long l, String app) {
        super(app);
        node = n;
        key = l;
//        Adding local observers seems like a plan, but unfortunately there is no way to unregister
//        a user that got out of use. This results in a nasty memoryleak and, eventually,
//        bad to almost stand-still perfromance when you craete new users...
//
//        Users.getBuilder().addLocalObserver(this);
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
    protected long getKey() {
        return key;
    }

    /**
     * @javadoc
     */
    public boolean isValidNode() {
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

    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(number, ctype);
    }

    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(number, ctype);
    }

    private boolean nodeChanged(String number, String ctype) {
        if ((node != null) && (node.getNumber() == Integer.parseInt(number))) {
            if (ctype.equals("d")) {
                node = null; // invalidate
            } else if (ctype.equals("c")) {
                node = Users.getBuilder().getNode(number);
            }
        }
        return true;
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        final int number = in.readInt();
        key = in.readLong();
        org.mmbase.util.ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    org.mmbase.bridge.LocalContext.getCloudContext().assertUp();
                    node = Users.getBuilder().getNode(number);
                }
            });
    }


    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.writeInt(node.getNumber());
        out.writeLong(key);
    }

}
