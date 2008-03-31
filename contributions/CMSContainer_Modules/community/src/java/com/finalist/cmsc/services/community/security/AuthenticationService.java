/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.security;

import java.util.List;

/**
 * @author Remco Bos
 */
public interface AuthenticationService {

    /**
     * Create an authentication
     *
     * @param userId
     * @param password
     */
    Authentication createAuthentication(String userId, String password);

    /**
     * Update the login information for the authentication (typically called by the user)
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     */
    void updateAuthentication(String userId, String oldPassword, String newPassword);

    /**
     * Set the login information for an authentication (typically called by an admin user)
     *
     * @param userId
     * @param newPassword
     */
    void updateAuthenticationPassword(String userId, String newPassword);


    /**
     * Delete an authentication entry
     * TODO if a authentication is deleted, what should happen with the user's content (in other systems)?
     *
     * @param id
     */
    void deleteAuthentication(Long id);

    /**
     * Check if the given authentication exists
     *
     * @param userId
     * @return boolean returns true if a user exists, otherwise it returns false
     */
    boolean authenticationExists(String userId);

    /**
     * Enable or disable a authentication
     *
     * @param userId
     * @param enabled
     */
    void setAuthenticationEnabled(String userId, boolean enabled);

    /**
     * Is an authentication enabled or disabled?
     *
     * @param userId
     * @return boolean returns true if a user is enabled, otherwise it returns false
     */
    boolean isAuthenticationEnabled(String userId);

    /**
     * Carry out an authentication attempt
     *
     * @param userId
     * @param password
     */
    boolean authenticate(String userId, String password);

    /**
     * Adds an authority to a user
     *
     * @param userId
     * @param authority
     */
    void addAuthorityToUser(String userId, String authority);

    /**
     * Removes an authority from a user
     *
     * @param userId
     * @param authority
     */
    void removeAuthorityFromUser(String userId, String authority);

    /**
     * Finds an authentication object for this userId
     * 
     * @param userId
     * @return authentication
     */
    Authentication findAuthentication(String userId);
    
    /**
     * Finds all authentications
     * 
     * @param authority
     * @return list authentications
     */
    List<Authentication> findAuthenticationsForAuthority(String name);

    /**
     * Finds all authentications
     * @return list authentications
     */
    List<Authentication> findAuthentications();
    
    /**
     * Finds an authentication id for this userId
     * @param userId
     * @return authenticationId
     */ 
	Long getAuthenticationIdForUserId(String userId);

	/**
	 * Find authentication information by Id for a user
	 * @param authenticationId
	 * @return Authentication
	 */
   Authentication getAuthenticationById(Long authenticationId);
}
