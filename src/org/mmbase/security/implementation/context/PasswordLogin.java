/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.Rank;
import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class PasswordLogin
 * @javadoc
 *
 * @author Eduard Witteveen
 * @version $Id: PasswordLogin.java,v 1.5 2004-03-08 17:42:31 michiel Exp $
 */

public class PasswordLogin extends ContextLoginModule {
    private static Logger log=Logging.getLoggerInstance(PasswordLogin.class.getName());

    public ContextUserContext login(Map userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException {
        
        // get username
        String username = (String)userLoginInfo.get("username");
        if(username == null) throw new org.mmbase.security.SecurityException("expected the property 'username' with login");
        
        // get password
        String password = (String)userLoginInfo.get("password");
        if(password == null) throw new org.mmbase.security.SecurityException("expected the property 'password' with login");
        
        log.debug("request for user: '"+username+"' with pass: '"+password+"'");
        
        String configValue = getModuleValue(username);
        if(configValue == null) {
            log.info("user with name:" + username + " doesnt have a value for this module");
            return null;
        }
        if(!configValue.equals(password)) {
            log.debug("user with name:" + username + " used pass:" + password+ " but needed :" + configValue);
            log.info("user with name:" + username + " didnt give the right password");
            return null;
        }
        
        Rank rank= getRank(username);
        if(rank == null) {
            log.warn( "expected a rank for user with the name:" + username + ", canceling a valid login due to the fact that the rank attribute wasnt set");
            return null;
            
        }
        return getValidUserContext(username, rank);
    }
}
