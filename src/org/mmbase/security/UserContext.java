/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 * This UserContext class provides a storage for the authentication
 * and authorization, so that information can be shared.
 * This class is NOT a container class for client related stuff, altrough
 * this is possible.
 */
public class UserContext {

    /**
     * Get the unique identifier for this user. This should be unique
     * for every different user on the system.
     * @return a unique identifier for this user.
     */
    public String getIdentifier() {
        return "anonymous";
    }

    /**
     * Get the identifier to be used as the owner field value of
     * objects created by this user.
     * @return an identifier for the owner field
     */
    public String getOwnerField() {
        return getIdentifier();
    }

    /**
     * Get the rank of this user.
     * @return the user rank
     */
    public Rank getRank() throws org.mmbase.security.SecurityException {
        // we need the highest rank.. to fool the security checks that we are allowed...
        return Rank.ADMIN;
    }

    /**
     * Get the string respresnetation of the user context.
     * @return a string describing the usercontext
     */
    public String toString() {
        return getIdentifier() + " (" + getRank() + ")";
    }

}
