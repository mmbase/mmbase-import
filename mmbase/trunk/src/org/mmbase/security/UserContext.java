/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 * The UserContext class is the result of an authentication, on wich the authorization can be
 * based. Normally your authorization/authentication implementation will also provide an extension
 * to this class.
 *
 * This default implementation is the most simple one, actually implementing 'no authorization'.
 *
 * This class is <em>not</em> necessarily also the container class for the client's credentials,
 * although this is possible.
 *
 * @author Eduard Witteveen
 * @version $Id: UserContext.java,v 1.13 2003-07-09 07:16:16 michiel Exp $
 */
public class UserContext {

    /**
     * Get the unique identifier for this user. This should be unique
     * for every different user on the system.
     *
     * @return A unique identifier for this user.
     */
    public String getIdentifier() {
        return "anonymous";
    }

    /**
     * Get the owner field value of new objects created by this user. The default implementation
     * returns the user's identifier. This can be changed if the authorization implementation does
     * not attribute rights to users directly ('context' implementations).
     * @return A possible value for the owner field
     */
    public String getOwnerField() {
        return getIdentifier();
    }

    /**
     * Gets the rank of this user.
     * @return the user rank
     */
    public Rank getRank() throws org.mmbase.security.SecurityException {
        // we need the highest rank.. to fool the security checks that we are allowed...
        return Rank.ADMIN;
    }

    /**
     * Get a string respresentation of this user context (for debugging)
     * @return a string describing the usercontext
     */
    public String toString() {
        return getIdentifier() + " (" + getRank() + ")";
    }

}
