/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.HashMap;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class does nothing
 */
public class NoAuthentication extends Authentication {
    private static Logger log=Logging.getLoggerInstance(NoAuthentication.class.getName()); 

    /** 
     *	This method does nothing
     */        
    protected void load() {
    }


    /** 
     * this method does nothing..
     */        
    public UserContext login(String application, HashMap loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
    	return new UserContext();
    }
    
    /** 
     * this method does nothing..
     */        
    public boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException {
    	return true;
    }
}
