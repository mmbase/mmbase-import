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
 * @version $Id: BasicUser.java,v 1.6 2003-11-10 16:47:14 michiel Exp $
 */
public class BasicUser implements User {
    private MMBaseCop securityManager;
    private UserContext usercontext;

    private String      authenticationType;

    BasicUser(MMBaseCop securityManager, UserContext usercontext, String authenticationType) {
        this.securityManager = securityManager;
        this.usercontext = usercontext;
        this.authenticationType = authenticationType;
    }

    public String getRank() {
        if(!securityManager.getAuthentication().isValid(usercontext)) throw new org.mmbase.security.SecurityException("usercontext invalid");
        return usercontext.getRank().toString();
    }

    public String getIdentifier() {
        if(!securityManager.getAuthentication().isValid(usercontext)) throw new org.mmbase.security.SecurityException("usercontext invalid");
        return usercontext.getIdentifier();
    }

    UserContext getUserContext() {
        // if(!securityManager.getAuthentication().isValid(usercontext)) throw new org.mmbase.security.SecurityException("usercontext invalid");
        return usercontext;
    }

    public boolean isValid() {
        return securityManager.getAuthentication().isValid(usercontext);
    }


    public String getOwnerField() {
        return usercontext.getOwnerField();
    }

    public String getAuthenticationType() {
        return authenticationType;
    }
}
