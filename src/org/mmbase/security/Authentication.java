package org.mmbase.security;

import java.util.HashMap;
import java.io.File;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is a empty implementation of the Authentication, it will only 
 *  return that the authentication succeeded. 
 *  To make your own implementation of
 *  authorization, you have to extend this class.
 */
public abstract class Authentication {
    private static Logger log=Logging.getLoggerInstance(Authentication.class.getName()); 

    /** The SecurityManager, who created this instance */
    protected MMBaseCop manager;
    
    /** The absolute file which is the config file */
    protected File configFile;

    /** 
     *	The method which sets the settings of this class. This method is 
     *	shouldn't be overrided.
     *	This class will set the member variables of this class and then
     *	call the member function load();
     *	@param manager The class that created this instance.
     *	@param configPath The url which contains the config information for.     
     *	    the authorization.
     */        
    public final void load(MMBaseCop manager, String configPath) {
    	log.debug("Calling load() with as config file:" + configPath);
     	this.manager = manager;
	if(configPath != null) this.configFile = new File(configPath).getAbsoluteFile();
	load();
    }

    /** 
     *	This method could be overrided by an extending class. 
     *	It should set the settings for this class, and when needed 
     *	retrieve them from the file at location configPath.
     */        
    protected abstract void load();

    /** 
     *	The method which sets the settings of this class. This method is 
     *	shouldn't be overrided.
     *	This class will set the member variables of this class and then
     *	call the member function load();
     *	@param manager The class that created this instance.
     *	@param configPath The url which contains the config information for.     
     *	    the authorization.
     *	@param parameters a list of optional parameters, may also be null
     *	@return <code>null</code When not valid
     *	    	a (maybe new) UserContext When valid
     *	@exception org.mmbase.security.SecurityException When something strang happend
     */        
    public abstract UserContext login(String application, HashMap loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException;

    /** 
     *	The method returns the Rank of a UserContext
     *	@param usercontext The UserContext of which we want to know the rights
     *	@return <code>true</code> when valid, otherwise <code>false</code>
     *	@exception org.mmbase.security.SecurityException When something strang happend
     */        
    public abstract boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException;   
}
