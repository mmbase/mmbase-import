/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is a abstract implementation of the Authentication.
 *
 *  To make your own implementation of authentication, you have to extend this class.
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen (javadocs)
 * @version $Id: Authentication.java,v 1.20 2003-09-26 17:41:10 michiel Exp $
 */
public abstract class Authentication extends Configurable {


    private static final Logger log = Logging.getLoggerInstance(Authentication.class);

    /**
     *  This method will verify the login, and give a UserContext back if the login procedure was successful.
     *	@param application A String that further specifies the login method (one implementation could handle more then one methods)
     *                     A typical value might be 'username/password'.
     *
     *	@param loginInfo   A Map containing the credentials or other objects which might be used to obtain them (e.g. request/response objects).
     *                     It might also be 'null', in which case your implementation normally should return the 'anonymous' user (or null, if
     *                     no such user can be defined).
     *
     *	@param parameters  A list of optional parameters, may also (and will often) be null.
     *
     *	@return <code>null</code if no valid credentials were supplied,  a (perhaps new) UserContext if login succeeded.
     *
     *	@exception org.mmbase.security.SecurityException When something strang happend
     */
    public abstract UserContext login(String application, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException;

    /**
     *	The method returns wether the UserContext has become invalid for some reason (change in security config?)
     *	@param usercontext The UserContext of which we want to know the rights
     *	@return <code>true</code> when valid, otherwise <code>false</code>
     *	@exception org.mmbase.security.SecurityException When something strang happend
     */
    public abstract boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException;
}
