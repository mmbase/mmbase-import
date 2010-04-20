/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A UserContext object is the result of an authentication, on which authorization can be
 * based. Normally your authorization/authentication implementation will also provide an extension
 * to this class.
 *
 * This default implementation is the most simple one, actually implementing 'no authorization'
 * (because the rank is fixed to 'administrator').
 *
 * This class is <em>not</em> necessarily also the container class for the client's credentials,
 * although this is possible.
 *
 * @author Eduard Witteveen
 * @version $Id$
 */
public class BasicUser implements UserContext, Comparable<Object> {
    private static final long serialVersionUID = -2999918034738004174L;
    private static final Logger log = Logging.getLoggerInstance(BasicUser.class);

    protected final String authenticationType;
    private final String identifier;
    protected final Authentication authentication;

    public BasicUser(Authentication auth, String authenticationType, String identifier) {
        this.authenticationType = authenticationType;
        this.identifier = identifier;
        this.authentication = auth;
    }

    /**
     * Gets the unique identifier for this user. This should be unique
     * for every different user on the system.
     *
     * @return A unique identifier for this user.
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the owner field value of new objects created by this user. The default implementation
     * returns the user's identifier. This can be changed if the authorization implementation does
     * not attribute rights to users directly ('context' implementations).
     * @return A possible value for the owner field
     */
    @Override
    public String getOwnerField() {
        return getIdentifier();
    }

    /**
     * Gets the rank of this user.
     * @return the user rank
     */
    @Override
    public Rank getRank() throws org.mmbase.security.SecurityException {
        // we need the highest rank.. to fool the security checks that we are allowed...
        return Rank.ADMIN;
    }

    /**
     * Gets a string representation of this user context (for debugging)
     * @return a string describing the usercontext
     */
    @Override
    public String toString() {
        try {
            return getIdentifier() + " (" + getRank() + ")";
        } catch (Throwable t) {
            return getIdentifier();
        }
    }

    @Override
    public boolean isValid() {
        try {
            if (authentication == null) {
                log.warn("No authentication object in " + this);
                return false;
            } else {
                return authentication.isValid(this);
            }
        } catch (Exception e) {
            log.warn(e);
            return false;
        }
    }

    @Override
    public String getAuthenticationType() {
        return authenticationType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BasicUser) {
            BasicUser ou = (BasicUser) o;
            return
                (authenticationType == null ? ou.authenticationType == null : authenticationType.equals(ou.authenticationType)) &&
                (getIdentifier() == null ? ou.getIdentifier() == null : getIdentifier().equals(ou.getIdentifier())) &&
                (getRank() == null ? ou.getRank() == null : getRank().equals(ou.getRank()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = org.mmbase.util.HashCodeUtil.hashCode(result, authenticationType);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof UserContext) {
            UserContext uc = (UserContext) o;
            int result = getRank().compareTo(uc.getRank());
            if (result != 0) return result;
            return getIdentifier().compareTo(uc.getIdentifier());
        } else {
            return getIdentifier().compareTo(org.mmbase.util.Casting.toString(o));
        }
    }

}
