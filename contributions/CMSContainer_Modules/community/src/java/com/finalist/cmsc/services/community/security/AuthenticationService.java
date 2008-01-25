/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.security;

/**
 * @author Remco Bos
 */
public interface AuthenticationService {

    /**
     * Create an authentication
     *
     * @param userName
     * @param password
     */
    void createAuthentication(String userName, String password);

    /**
     * Update the login information for the authentication (typically called by the user)
     *
     * @param userName
     * @param oldPassword
     * @param newPassword
     */
    void updateAuthentication(String userName, String oldPassword, String newPassword);

    /**
     * Set the login information for an authentication (typically called by an admin user)
     *
     * @param userName
     * @param newPassword
     */
    void updateAuthenticationPassword(String userName, String newPassword);


    /**
     * Delete an authentication entry
     * TODO if a authentication is deleted, what should happen with the user's content (in other systems)?
     *
     * @param userName
     */
    void deleteAuthentication(String userName);

    /**
     * Check if the given authentication exists
     *
     * @param userName
     * @return boolean returns true if a user exists, otherwise it returns false
     */
    boolean authenticationExists(String userName);

    /**
     * Enable or disable a authentication
     *
     * @param userName
     * @param enabled
     */
    void setAuthenticationEnabled(String userName, boolean enabled);

    /**
     * Is an authentication enabled or disabled?
     *
     * @param userName
     * @return boolean returns true if a user is enabled, otherwise it returns false
     */
    boolean isAuthenticationEnabled(String userName);

    /**
     * Carry out an authentication attempt
     *
     * @param userName
     * @param password
     */
    boolean authenticate(String userName, String password);

    /**
     * Adds an authority to a user
     *
     * @param userName
     * @param authority
     */
    void addAuthorityToUser(String userName, String authority);

    /**
     * Removes an authority from a user
     *
     * @param userName
     * @param authority
     */
    void removeAuthorityFromUser(String userName, String authority);

    /**
     * Finds an authentication object for this userName
     * 
     * @param userName
     * @return authentication
     */
    Authentication findAuthentication(String userName);
}
