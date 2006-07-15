/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.*;

/**
 * This UserContext class provides a storage for the authentication
 * and authorization, so that information can be shared.
 * This class is NOT a container class for client related stuff, altrough
 * this is possible.
 *
 * @author Eduard Witteveen
 * @version $Id: ContextUserContext.java,v 1.10 2006-02-13 18:19:55 michiel Exp $
 */
public class ContextUserContext extends BasicUser implements java.io.Serializable {

    private static final long serialVersionUID = 1;

    private String  username;
    private Rank    rank;
    private long    key;
    /** The SecurityManager, who (eventually) created this instance */
    protected MMBaseCop manager;

    public ContextUserContext(String username, Rank rank, long key, MMBaseCop manager, String app) {
        super(app);
        this.rank = rank;
        this.username = username;
        this.key = key;
        this.manager=manager;
    }

    public String getIdentifier() {
        return username;
    }

    public String getOwnerField() {
        if (manager == null) {
            manager = org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop();
        }
        return ((ContextAuthorization)manager.getAuthorization()).getDefaultContext(this);
    }

    public Rank getRank() {
        return rank;
    }

    long getKey() {
        return key;
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        username = in.readUTF();
        rank = (Rank)in.readObject();
        key = in.readLong();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.writeUTF(username);
        out.writeObject(rank);
        out.writeLong(key);
    }

    public boolean equals(Object o) {
        if (o instanceof ContextUserContext) {
            ContextUserContext ou = (ContextUserContext) o;
            return super.equals(o) && key == ou.key;
        } else {
            return false;
        }
    }


}
