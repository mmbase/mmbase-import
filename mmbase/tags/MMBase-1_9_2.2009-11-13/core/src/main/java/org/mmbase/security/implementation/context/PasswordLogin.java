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
 * @version $Id$
 */

public class PasswordLogin extends ContextLoginModule {
    private static final Logger log = Logging.getLoggerInstance(PasswordLogin.class);

    public ContextUserContext login(Map<String, ?> userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException {

        // get userName
        String userName = (String)userLoginInfo.get("username");
        if(userName == null) throw new org.mmbase.security.SecurityException("expected the property 'username' with login");

        // get password
        String password = (String)userLoginInfo.get("password");
        if(password == null) throw new org.mmbase.security.SecurityException("expected the property 'password' with login");

        log.debug("request for user: '"+userName+"' with pass: '"+password+"'");

        org.w3c.dom.Element node = getAccount(userName);
        if(node == null) {
            log.info("user with name:" + userName + " doesnt have a value for this module");
            return null;
        }
        org.w3c.dom.Element identify = (org.w3c.dom.Element) node.getElementsByTagName("identify").item(0);
        String configPassword = org.mmbase.util.xml.DocumentReader.getNodeTextValue(identify);
        if(!configPassword.equals(password)) {
            log.debug("user with name:" + userName + " used pass:" + password + " but needed :" + configPassword);
            log.info("user with name:" + userName + " didnt give the right password");
            return null;
        }

        Rank rank= getRank(userName);
        if(rank == null) {
            log.warn( "expected a rank for user with the name:" + userName + ", canceling a valid login due to the fact that the rank attribute wasnt set");
            return null;

        }
        return getValidUserContext(userName, rank);
    }
}
