package org.mmbase.security;

import java.util.HashMap;

/**
 *  This UserContext class provides a storage for the authentication
 *  and authorization, so that information can be shared.
 *  This class is NOT a container class for client related stuff, altrough 
 *  this is possible.
 */
public class UserContext {
    /** Container for login information */
    private HashMap properties= new HashMap();
    
    /**
     *  Conststuctor, to create a new UserContext, which can be used
     *	to be filled with user related stuff. 
     *	(key='name' value='admin' , key='password' value='admin2k')
     */
    public UserContext() {
    	super();
    }

    /**
     *	gets a property of the UserContext, this is used to get the 
     *	information from the UserContext.
     * @param key   The string which represents the key which has to
     *	    	    be set with the value in the properties.
     * @param value The Object which has to be linked on the key.
     * @return      value
     */
    public Object put(String key, Object value) {
    	return properties.put(key, value);
    }

    /**
     *	gets a property of the UserContext, this is used to get the 
     *	information from the UserContext.
     * @param key   The string which represents the key which has to
     *	    	    be looked up in the properties.
     * @return      null, if the value wasn't set, otherwise the Object.
     */
    public Object get(String key) {
    	return properties.get(key);
    }


    /**
     *  Get the unique identifier for this user. This should be unique 
     *  for every different user on the system.
     */
    public String getIdentifier() {
	return "nobody";
    }
}
