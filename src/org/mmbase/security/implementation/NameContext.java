package org.mmbase.security.implementation;

import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.SecurityException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This UserContext class provides a storage for the authentication
 *  and authorization, so that information can be shared.
 *  This class is NOT a container class for client related stuff, altrough
 *  this is possible.
 */
public class NameContext extends UserContext {
    private static Logger log = Logging.getLoggerInstance(NameContext.class.getName());

    private String ident = null;
    private Rank rank= null;

    public NameContext(Rank rank) {
        this.rank = rank;
    }

    public String getIdentifier() {
        if(ident == null) throw new SecurityException("Dont know who we are, was not set by the system. This is required");
	return ident;
    }

    public Rank getRank() {
        if(rank == null) throw new SecurityException("Dont know who we are, was not set by the system. This is required");
	return rank;
    }

    void setIdentifier(String ident) {
        this.ident = ident;
    }
}
