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
 * dum da di da.. wrap the whole thing !!
 */
public class BasicUser implements User {
    private MMBaseCop securityManager;
    private UserContext usercontext;
    
    BasicUser(MMBaseCop securityManager, UserContext usercontext) {
    	this.securityManager = securityManager;
    	this.usercontext = usercontext;
    }
    
    public String getRank() {
    	return securityManager.getAuthentication().getRank(usercontext).toString();
    }
    
    public String getIdentifier() {
    	return usercontext.getIdentifier();
    }
    
    UserContext getUserContext(){
    	return usercontext;
    }
}
