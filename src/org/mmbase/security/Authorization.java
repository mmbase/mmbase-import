package org.mmbase.security;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 *  This class is a empty implementation of the Authorization, it will only 
 *  return that operations are valid. To make your own implementation of
 *  authorization, you have to extend this class.
 */
public class Authorization {
    private static Logger log=Logging.getLoggerInstance(Authorization.class.getName()); 
    
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
     *	This method could be overrided by an extending class. 
     *	It has to be called, when a new Node has been created.
     *	This way, the authentication can create default rights
     *	for this object, depending on the UserContext and generate
     *	logging information.
     *	@param user The UserContext, containing the information 
     *	    about the user.
     *	@param node The MMObjectNode, which has just been added to
     *	    the MMBase cloud.
     */        
    public void create(UserContext user, MMObjectNode node) {
    }
    
    /** 
     *	This method could be overrided by an extending class. 
     *	It has to be called, when a Node has been changed.
     *	This way, the authentication can generate log information
     *	for this object, which can be used for accountability
     *	@param user The UserContext, containing the information 
     *	    about the user.
     *	@param node The MMObjectNode, which has just been changed
     *	    in the cloud.
     */        
    public void update(UserContext user, MMObjectNode node) {
    }
    
    /** 
     *	This method could be overrided by an extending class. 
     *	It has to be called, when a Node has been removed from 
     *	the cloud.
     *	This way, the authentication can generate log information
     *	for this node, and remove the authorization object which
     *	belongs to this node.
     *	@param user The UserContext, containing the information 
     *	    about the user.
     *	@param node The MMObjectNode, which has just been removed
     *	    in the cloud.
     */        
    public void remove(UserContext user, MMObjectNode node) {
    }
    
    /** 
     *	This method could be overrided by an extending class.     
     *	This method checks if an operation is permitted on a certain node done
     *	by a certain user.
     *	@param user The UserContext, containing the information 
     *	    about the user.
     *	@param node The MMObjectNode, which has to be checked.
     *	@param operation The operation which will be performed.
     *	@return <code>true</code> if the operation is permitted,
     *	    	<code>false</code> if the operation is not permitted,     
     */        
    public boolean check(UserContext user, MMObjectNode node, Operation operation) {
    	return true;
    }
    
    /** 
     *	This method could be overrided by an extending class.     
     *	This method asserts that an operation is permitted on a 
     *	certain node done by a certain user. If not, a exception is thrown
     *	@param user The UserContext, containing the information 
     *	    about the user.
     *	@param node The MMObjectNode, which has to be asserted.
     *	@param operation The operation which will be performed.
     *	@exception org.mmbase.SecurityException 
     *	    If the assertion fails
     */        
    public void assert(UserContext user, MMObjectNode node, Operation operation) 
    	throws org.mmbase.security.SecurityException 
    {
    }
}
