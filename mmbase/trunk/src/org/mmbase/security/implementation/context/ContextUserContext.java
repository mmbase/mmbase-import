/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This UserContext class provides a storage for the authentication
 * and authorization, so that information can be shared.
 * This class is NOT a container class for client related stuff, altrough
 * this is possible.
 *
 * @author Eduard Witteveen
 * @version $Id: ContextUserContext.java,v 1.4 2002-06-07 12:57:01 pierre Exp $
 */
public class ContextUserContext extends UserContext {
    private static Logger log = Logging.getLoggerInstance(ContextUserContext.class.getName());
    private String  username;
    private Rank    rank;
    private long    key;
    /** The SecurityManager, who (eventually) created this instance */
    protected MMBaseCop manager;

    public ContextUserContext(String username, Rank rank, long key, MMBaseCop manager) {
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

}
