/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import java.util.Map;
import org.mmbase.security.implementation.cloudcontext.builders.*;
import org.mmbase.security.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.SecurityException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @version $Id: Authenticate.java,v 1.2 2003-06-16 13:07:17 michiel Exp $
 */
public class Authenticate extends Authentication {
    private static Logger log = Logging.getLoggerInstance(Authenticate.class.getName());
    private long uniqueNumber;

    /**
     * Constructor. Only initializes an 'unique number' for this security instance, which can be used in
     * 'isValid'.
     */
    public Authenticate() {
        uniqueNumber = System.currentTimeMillis();
    }

    // javadoc inherited
    protected void load() throws SecurityException {
        Users users = Users.getBuilder();
        if (users == null) {
            String msg = "builders for security not installed, if you are trying to install the application belonging to this security, please restart the application after all data has been imported)";
            log.fatal(msg);
            throw new SecurityException(msg);
        }
        if (!users.check()) {
            String msg = "builder mmbaseusers was not configured correctly";
            log.error(msg);
            throw new SecurityException(msg);
        }
    }

    // javadoc inherited
    public UserContext login(String s, Map map, Object aobj[]) throws SecurityException  {
        if (log.isDebugEnabled()) {
            log.trace("login-module: '" + s + "'");
        }
        MMObjectNode node = null;
        Users users = Users.getBuilder();
        if (users == null) {
            String msg = "builders for security not installed, if you are trying to install the application belonging to this security, please restart the application after all data has been imported)";
            log.fatal(msg);
            throw new org.mmbase.security.SecurityException(msg);
        }
        if (s.equals("anonymous")) {
            node = users.getAnonymousUser();
        } else if (s.equals("name/password")) {
            String username = (String)map.get("username");
            String password = (String)map.get("password");
            if(username == null || password == null) {
                throw new SecurityException("expected the property 'username' and 'password' with login");
            }
            node = users.getUser(username, password);
        } else {
            throw new SecurityException("login module with name '" + s + "' not found, only know 'anonymous' and 'name/password' ");
        }
        if (node == null)  return null;
        return new User(node, uniqueNumber);
    }

    
    // javadoc inherited
    public boolean isValid(UserContext usercontext) throws SecurityException {
        if (! (usercontext instanceof User)) {
            log.debug("Changed to other security implementation");
            return false;
        }
        User user = (User) usercontext;
        boolean flag = user.isValid() && user.getKey() == uniqueNumber;
        if (flag) {
            log.debug(user.toString() + " was valid");
        } else if (user.isValid()) {
            log.debug(user.toString() + " was NOT valid (different unique number)");
        } else {
            log.debug(user.toString() + " was NOT valid (node was different)");
        }
        return flag;
    }

}
