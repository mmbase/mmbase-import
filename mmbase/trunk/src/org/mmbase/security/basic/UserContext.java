package org.mmbase.security.basic;

import java.util.HashMap;
import org.mmbase.security.Rank;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This UserContext class provides a storage for the authentication
 *  and authorization, so that information can be shared.
 *  This class is NOT a container class for client related stuff, altrough 
 *  this is possible.
 */
public class UserContext extends org.mmbase.security.UserContext {
    private static Logger log = Logging.getLoggerInstance(UserContext.class.getName()); 

    private String name;

    public UserContext(String name) {
        log.debug("constructor");
        this.name = name;
    }
    
    /**
     *  Get the unique identifier for this user. This should be unique 
     *  for every different user on the system.
     */
    public String getIdentifier() {
	return name;
    }

    /**
     *  Get the rank of this user. 
     *	@return     the rank of this user.          
     */
    public Rank getRank() {
    	if(getIdentifier().equals("")) return Rank.ANONYMOUS;
	if(getIdentifier().equals("admin")) return Rank.ADMIN;
	return Rank.BASICUSER;
    }    
}
