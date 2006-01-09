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
 * @version $Id: ContextUserContext.java,v 1.7 2006-01-09 12:18:49 johannes Exp $
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
        return ((ContextAuthorization)manager.getAuthorization()).getDefaultContext(this);
    }

    public Rank getRank() {
        return rank;
    }

    long getKey() {
        return key;
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        this.username = (String)in.readObject();
        this.rank = (Rank)in.readObject();
        this.key = in.readLong();
        this.manager = org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.writeObject(username);
        out.writeObject(rank);
        out.writeLong(key);
    }


}
