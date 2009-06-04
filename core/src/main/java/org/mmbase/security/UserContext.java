/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security;

/**
 * This User interface defines the storage for the authentication
 * and authorization, so that information can be shared.
 * This interface is NOT a container class for client related stuff, altrough
 * this is possible. Notice that after the login on the cloud it is not
 * certain that you will receive the same User object back !
 *
 * @author Eduard Witteveen
 * @version $Id$
 */
public interface UserContext extends java.io.Serializable {
    /**
     *  Get the unique identifier for this user. This should be unique
     *  for every different user inside a cloud.
     *	@return an unique id for the current user
     */
    public String getIdentifier();

    /**
     *  Get the rank of this user.
     *	@return the rank of this user
     */
    public Rank getRank();

    /**
     *  Is valid
     *	@return <code>true</code> if the user is still valid.
     *      	<code>false</code> if the user is expired..
     */
    public boolean isValid();


    /**
     * Return the default owner field value for new nodes created by this user.
     * @return owner field value
     *
     * @since MMBase-1.7
     */
    public String getOwnerField();

    /**
     * Returns the original authentication type as specified in getCloud
     * @return authentication type
     *
     * @since MMBase-1.7
     * @see   org.mmbase.bridge.CloudContext#getCloud
     */
    public String getAuthenticationType();

}
