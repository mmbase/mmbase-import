/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.implementation.basic;

import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class AdminLoginModule
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: AdminLoginModule.java,v 1.4 2002-06-07 12:56:56 pierre Exp $
 */
public class AdminLoginModule implements LoginModule {
    private String key;
    private static Logger log=Logging.getLoggerInstance(AdminLoginModule.class.getName());

    public void load(Map properties) {
        key = (String)properties.get("key");
        if(key == null) {
            log.error("property name 'key' was not specified, but is needed for operation of this class");
            throw new org.mmbase.security.SecurityException("property name 'key' was not specified, but is needed for operation of this class");
        }
    }

    public boolean login(NameContext user, Map loginInfo,  Object[] parameters) {
        if(!loginInfo.containsKey("key")) throw new org.mmbase.security.SecurityException("key 'key' not found in login information");
        if(key.equals(loginInfo.get("key"))) {
            log.info("admin login..");
            // set the identifier
            user.setIdentifier("admin");
            return true;
        }
        else {
            log.info("admin login failed..");
            return false;
        }
    }
}
