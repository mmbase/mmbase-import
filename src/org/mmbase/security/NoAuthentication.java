/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import org.mmbase.security.SecurityException;
import java.util.Map;

/**
 * This class is used when no authentication is configured. Every credential is rewarded with a
 * UserContext object. So every attempt to log in will succeed.
 *
 * @author Eduard Witteveen
 * @version $Id: NoAuthentication.java,v 1.7 2003-11-26 20:50:49 michiel Exp $
 * @see UserContext
 */
final public class NoAuthentication extends Authentication {

    static final UserContext userContext = new UserContext(); 
    // package because NoAuthorization uses it to get the one 'possible context' (which is of course the 'getOwnerField' of the only possible user)
    // (this is assuming that NoAuthentication is used too, but if not so, that does not matter)

    /**
     *	This method does nothing
     */
    protected void load() {
    }


    /**
     * Returns always the same object (an user 'anonymous'with rank 'administrator'')
     * @see UserContext
     */
    public UserContext login(String application, Map loginInfo, Object[] parameters) throws SecurityException {
        return userContext;
    }

    /**
     * Users remain valid always. 
     * @return true
     */
    public boolean isValid(UserContext usercontext) throws SecurityException {
        return true;
    }
}
