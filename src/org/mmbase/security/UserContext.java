package org.mmbase.security;

/**
 *  This UserContext class provides a storage for the authentication
 *  and authorization, so that information can be shared.
 *  This class is NOT a container class for client related stuff, altrough 
 *  this is possible.
 */
public class UserContext implements org.mmbase.bridge.User {
    /**
     *  Get the unique identifier for this user. This should be unique 
     *  for every different user on the system.
     *	@return     a unique identifier for this user.     
     */
    public String getIdentifier() {
	return "anonymous";
    }
    
    /**
     *  Get the rank of this user. 
     *	@return     the rank of this user.          
     */
    public Rank getRank() {
	return Rank.ANONYMOUS;
    }    
}
