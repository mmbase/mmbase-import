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
 * ClassLogin, authentication based on 'class', using &lt;security&gt;/classauthentication.xml or ClassAuthenticationWrapper.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ClassLogin.java,v 1.1 2004-04-19 16:38:59 michiel Exp $
 * @since MMBase-1.8
 */

public class ClassLogin extends ContextLoginModule {
    private static final Logger log = Logging.getLoggerInstance(ClassLogin.class);

    public ContextUserContext login(Map userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException {

        org.mmbase.security.classsecurity.ClassAuthentication.Login li = org.mmbase.security.classsecurity.ClassAuthentication.classCheck("class");
        if (li == null) {
            throw new SecurityException("Class authentication failed  '" + userLoginInfo + "' (class not authorized)");
        }        
        // get username
        String userName = (String) li.getMap().get("username");
        if(userName == null) throw new org.mmbase.security.SecurityException("expected the property 'username' with login");
        
        
        String configValue = getModuleValue(userName);
        if(configValue == null) {
            log.info("user with name:" + userName + " doesnt have a value for this module");
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
