/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 *  This User interface defines the storage for the authentication
 *  and authorization, so that information can be shared.
 *  This interface is NOT a container class for client related stuff, altrough 
 *  this is possible. Notice that after the login on the cloud it is not 
 *  certain that you will receive the same User interface back !
 */
public interface User {
    /**
     *	gets a property of the User, this is used to get the 
     *	information from the User.
     * @param key   The string which represents the key which has to
     *	    	    be set with the value in the properties.
     * @param value The Object which has to be linked on the key.
     * @return      value
     */
    public Object put(String key, Object value);

    /**
     *	gets a property of the User, this is used to get the 
     *	information from the User.
     * @param key   The string which represents the key which has to
     *	    	    be looked up in the properties.
     * @return      null, if the value wasn't set, otherwise the Object.
     */
    public Object get(String key);


    /**
     *  Get the unique identifier for this user. This should be unique 
     *  for every different user inside a cloud.
     */
    public String getIdentifier();
}
