package org.mmbase.security;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 *  This class is a empty implementation of the Authentication, it will only 
 *  return that the authentication succeeded. 
 *  To make your own implementation of
 *  authorization, you have to extend this class.
 */
public class Authentication {
    private static Logger log=Logging.getLoggerInstance(Authentication.class.getName()); 

    /** The SecurityManager, who created this instance */
    protected SecurityManager manager;
    
    /** The url where the configfile is located */      
    protected String configUrl;

    /** 
     *	The method which sets the settings of this class. This method is 
     *	shouldn't be overrided.
     *	This class will set the member variables of this class and then
     *	call the member function load();
     *	@param manager The class that created this instance.
     *	@param configUrl The url which contains the config information for.     
     *	    the authorization.
     */        
    public final void load(SecurityManager manager, String configUrl) {
    	log.debug("Calling load() with configUrl:" + configUrl);
     	this.manager = manager;
	this.configUrl = configUrl;
	load();
    }

    /** 
     *	This method could be overrided by an extending class. 
     *	It should set the settings for this class, and when needed 
     *	retrieve them from the file at location configUrl.
     */        
    protected void load() {
    }


    /** 
     *	The method which sets the settings of this class. This method is 
     *	shouldn't be overrided.
     *	This class will set the member variables of this class and then
     *	call the member function load();
     *	@param manager The class that created this instance.
     *	@param configUrl The url which contains the config information for.     
     *	    the authorization.
     *	@return <code>true</code> When valid
     *	    	<code>false</code When not valid
     *	@exception org.mmbase.security.SecurityException When something strang happend     
     */        
    public boolean login(String application, UserContext userContext) throws org.mmbase.security.SecurityException {
    	return login(application, userContext, null);
    }

    /** 
     *	The method which sets the settings of this class. This method is 
     *	shouldn't be overrided.
     *	This class will set the member variables of this class and then
     *	call the member function load();
     *	@param manager The class that created this instance.
     *	@param configUrl The url which contains the config information for.     
     *	    the authorization.
     *	@param parameters a list of optional parameters
     *	@return <code>true</code> When valid
     *	    	<code>false</code When not valid
     *	@exception org.mmbase.security.SecurityException When something strang happend
     */        
    public boolean login(String application, UserContext userContext, Object[] parameters) 
    	throws org.mmbase.security.SecurityException 
    {
    	return true;
    }
}
