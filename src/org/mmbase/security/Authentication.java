/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.Map;
import java.io.File;

import org.mmbase.util.FileWatcher;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is a empty implementation of the Authentication, it will only
 *  return that the authentication succeeded.
 *
 *  To make your own implementation of authorization, you have to extend this class.
 *
 * @author Eduard Witteveen
 * @version $Id: Authentication.java,v 1.17 2003-07-09 07:25:05 michiel Exp $
 */
public abstract class Authentication extends Configurable {
    private static Logger log = Logging.getLoggerInstance(Authentication.class);

    /**
     *  This method will verify the login, and give a UserContext back if everything
     *  was valid
     *	@param manager The class that created this instance.
     *	@param configPath The url which contains the config information for.
     *	                  the authorization.
     *	@param parameters a list of optional parameters, may also be null
     *	@return <code>null</code When not valid a (maybe new) UserContext when valid.
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
