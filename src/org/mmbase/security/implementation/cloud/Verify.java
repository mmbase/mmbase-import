/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/**
 * Security from within MMBase
 * @author Eduard Witteveen
 */
package org.mmbase.security.implementation.cloud;

import org.mmbase.security.Authorization;
import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.Operation;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/** authorization based on a config*/
public class Verify extends Authorization {
    private static Logger   log=Logging.getLoggerInstance(Verify.class.getName()); 

    protected void load() {
    }
	
    public void create(UserContext user, int nodeid) {
        // when we have a create, the user id is set to it..
	// scary way, to do it with identifier !!
	setContext(user, nodeid, user.getIdentifier());
	log.info("[node #"+nodeid+"] created by ["+user.getIdentifier()+"]");
    }
	
    public void update(UserContext user, int nodeid) {
        log.info("[node #"+nodeid+"] updated by ["+user.getIdentifier()+"]");
    }
	
    public void remove(UserContext user, int nodeid) {
	log.info("[node #"+nodeid+"] removed by ["+user.getIdentifier()+"]");
    }
	
    public boolean check(UserContext user, int nodeid, Operation operation) {
	/* 
	well the following rulez apply.....
		- anonymous may see everything(except the mmbaseusers-builder), further nothing....
		- basic user do anything to nodes which belong to him...
		- may see/change it's own mmbase-user builder-node
		- admin may do everything....			
	*/
	log.debug("[node #"+nodeid+"] check by ["+user.getIdentifier()+"] for operation ["+operation+"]");		
    	MMObjectNode node = getMMNode(nodeid);
	String username = user.getIdentifier();
	String builder = node.getName();
	Rank rank = user.getRank();

	// which situation do we have? security or not security objects..
	if(builder.equals("mmbaseusers")) {
	    // security related objects
	    if(rank == Rank.ADMIN) {
		// admin may see all security nodes, create, change, delete them.. (maybe someone lost its password), except it's own account
		if(username.equals(node.getStringValue("username")) && Operation.DELETE == operation) {
		    // user may not delete it's own account
		    return false;
		}
		// furthermore admin may do everyting...
		return true;
	    }
	    else if(rank == Rank.BASICUSER) {
		// when we are ourselve...
		if(username.equals(node.getStringValue("username"))) {
		    // we may only see / modify it...
		    if (Operation.READ == operation || Operation.WRITE == operation) {
			// only return true/false when we are the owner...
			return true;
		    }
		}
	    }
	    // well anonymous may not see everyting....
	    return false;
	}
	else {
	    // normal objects
	    // do we have to hide the builder "mmbaseusers" for admin?
	    if(rank == Rank.ADMIN) {
		return true;
	    }
	    else if (rank == Rank.BASICUSER) {
                // we may not create new users...
                if(node.parent.getTableName().equals("typedef") && node.getStringValue("name").equals("mmbaseusers") && Operation.CREATE == operation)  {
                    log.debug("tried to create a new user, not allowed..");
                    return false;
                }                
		if(username.equals(node.getStringValue("username"))) {
		    // if we are the owner, we may change stuff....
		    return true;
		}
		else {                    
		    // if we aint the owner, we only may read / create /link it...
		    return (Operation.READ == operation || Operation.CREATE == operation);
		}
	    }
	    return Operation.READ == operation;
	}
    }
	
    public void assert(UserContext user, int nodeid, Operation operation) throws org.mmbase.security.SecurityException {
    	if (!check(user, nodeid, operation) ) {
	    String msg = "Operation '" + operation + "' on " + nodeid + " was NOT permitted to " + user.getIdentifier();
	    log.error(msg);
	    throw new org.mmbase.security.SecurityException(msg);
	}	
    }
	
    public boolean check(UserContext user, int nodeid, int srcnodeid, int dstnodeid, Operation operation) {
	Rank rank = user.getRank();
	if(rank == Rank.ADMIN) {
	    return true;
	}
	else if (rank == Rank.BASICUSER) {
	    if(operation == Operation.CREATE) {
		return true;
	    }
	    if(operation == Operation.CHANGE_RELATION) {
		MMObjectNode node = getMMNode(nodeid);
		String username = user.getIdentifier();			
		if(username.equals(node.getStringValue("username"))) {
		    return true;
		}
	    }
	}
	return false;
    }
	
    public void assert(UserContext user, int nodeid, int srcnodeid, int dstnodeid, Operation operation) throws org.mmbase.security.SecurityException {
        if (!check(user, nodeid, srcnodeid, dstnodeid, operation) ) {
	    String msg = "Operation '" + operation + "' on " + nodeid + " with src " + srcnodeid + "  and dest " + dstnodeid + " was NOT permitted to " + user.getIdentifier();
	    log.error(msg);
	    throw new org.mmbase.security.SecurityException(msg);
        }
    }	
	
    public String getContext(UserContext user, int nodeid) throws org.mmbase.security.SecurityException {
        // check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
	assert(user, nodeid, Operation.READ);

    	// and get the value...		
	MMObjectNode node = getMMNode(nodeid);	
	return node.getStringValue("owner");
    }
	
    public void setContext(UserContext user, int nodeid, String context) throws org.mmbase.security.SecurityException {
	// check if is a valid context for us..
	if(!getPossibleContexts(user, nodeid).contains(context)) {
	    String msg = "could not set the context to "+context+" for node #"+nodeid+" by user: " +user;
	    log.error(msg);
	    throw new org.mmbase.security.SecurityException(msg);
        }
		
        // check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
	assert(user, nodeid, Operation.CHANGECONTEXT);
	
	// well now really set it...
	MMObjectNode node = getMMNode(nodeid);
    	node.setValue("owner", context);
	node.commit();
	log.info("[node #"+nodeid+"] context set ["+user.getIdentifier()+"]");		
    }
	
    public java.util.HashSet getPossibleContexts(UserContext user, int nodeid) throws org.mmbase.security.SecurityException {
	// retrieve all the users....
	org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase)org.mmbase.module.Module.getModule("mmbaseroot");
        builder =  (UserBuilder)mmb.getMMObject("mmbaseusers");		
	java.util.Enumeration e = builder.search("WHERE 42=42 ");
	java.util.HashSet contexts = new java.util.HashSet();
	while(e.hasMoreElements()) {
	    contexts.add(((MMObjectNode) e.nextElement()).getStringValue("username"));			
        }		
	return contexts;
    }
	
    private static org.mmbase.module.core.MMObjectBuilder builder = null;   
    private MMObjectNode getMMNode(int n) {
        if(builder == null) {
      	    org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase)org.mmbase.module.Module.getModule("mmbaseroot");
            builder =  mmb.getMMObject("typedef");
            if(builder == null) {
	        String msg = "builder typedef not found";
		log.error(msg);
	    	throw new org.mmbase.security.SecurityException(msg);
            }
        }
        MMObjectNode node = builder.getNode(n);
        if(node == null) {
	    String msg = "node not found";
	    log.error(msg);
	    throw new org.mmbase.security.SecurityException(msg);
        }
        return node;
    }    	
}
