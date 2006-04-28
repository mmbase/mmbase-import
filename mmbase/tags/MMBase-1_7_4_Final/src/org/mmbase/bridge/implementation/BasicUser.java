/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.User;
import org.mmbase.security.UserContext;
import org.mmbase.security.MMBaseCop;

/**
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: BasicUser.java,v 1.7 2003-11-16 13:32:57 michiel Exp $
 */
public class BasicUser implements User {
    private MMBaseCop securityManager;
    private UserContext userContext;

    private String      authenticationType;

    BasicUser(MMBaseCop securityManager, UserContext userContext, String authenticationType) {
        this.securityManager = securityManager;
        this.userContext = userContext;
        this.authenticationType = authenticationType;
    }

    public String getRank() {
        if(!securityManager.getAuthentication().isValid(userContext)) throw new org.mmbase.security.SecurityException("userContext invalid");
        return userContext.getRank().toString();
    }

    public String getIdentifier() {
        if(!securityManager.getAuthentication().isValid(userContext)) throw new org.mmbase.security.SecurityException("userContext invalid");
        return userContext.getIdentifier();
    }

    UserContext getUserContext() {
        // if(!securityManager.getAuthentication().isValid(userContext)) throw new org.mmbase.security.SecurityException("userContext invalid");
        return userContext;
    }

    public boolean isValid() {
        return securityManager.getAuthentication().isValid(userContext);
    }


    public String getOwnerField() {
        return userContext.getOwnerField();
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    /**
     * @since MMBase-1.7
     */
    public String toString() {
        return userContext.getIdentifier() + "/" +  userContext.getRank().toString() + (isValid() ? "" : " (invalid)");
    }
}
