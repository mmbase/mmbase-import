/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.basic;

import java.util.HashMap;
import java.io.File;

import org.mmbase.util.ExtendedProperties;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class OwnerAuthorization extends Authorization {
    private static Logger log=Logging.getLoggerInstance(OwnerAuthorization.class.getName()); 
    
    private static org.mmbase.module.core.MMObjectBuilder builder = null;

    private MMObjectNode getMMNode(int n) {
        if(builder == null) {
            org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase)org.mmbase.module.Module.getModule("MMBASEROOT");
            builder =  mmb.getMMObject("typedef");
            if(builder == null) throw new org.mmbase.security.SecurityException("builder not found");
        }
        MMObjectNode node = builder.getNode(n);
        if(node == null) throw new org.mmbase.security.SecurityException("node not found");
        return node;
    }

    public void load() {
        if ( ! configFile.exists() ) {
            log.error("file: '"+configFile+"' did not exist.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' did not exist.");
        }
        if ( ! configFile.isFile() ) {
            log.error("file: '"+configFile+"' is not a file.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' is not a file.");
        }
        if ( ! configFile.canRead() ) {
            log.error("file: '"+configFile+"' is not readable.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' is not readable.");
        }
        log.debug("file for accounts loaded");
    }

    public void create(UserContext user, int nodeNumber) {
        if(manager.getActive()) { // else don't touch.
            MMObjectNode node = getMMNode(nodeNumber);
            node.setValue("owner", user.getIdentifier());
            node.commit();
        }
    }

    public void update(UserContext user, int nodeNumber) {
        if(manager.getActive()) {
            MMObjectNode node = getMMNode(nodeNumber);
            node.setValue("owner", user.getIdentifier());
            node.commit();
        }
    }

    public void remove(UserContext user, int node) {
    }

    public boolean check(UserContext user, int nodeNumber, Operation operation) {        
        // if we don't do security, then we are allowed to do everything.
        if (!manager.getActive()) {
            log.trace("security is not active. permitting operation");
            return true; 
        }

        if (log.isDebugEnabled()) {
            log.trace("checking user: " + user.getIdentifier() + " operation: " + operation + " node: " + nodeNumber);
        }

        boolean permitted = false;

        // if we are admin, then everything is permitted as well....
        if(user.getRank() == Rank.ADMIN) {
            log.debug("user admin has always all rights..");
            return true;
        }

        switch(operation.getInt()) {
    	    // say we may always create, if we are authenticated.	
            case Operation.CREATE_INT:
	    // nah, we always except links from other nodes.....		
            case Operation.LINK_INT:            	
            	permitted = !(user.getRank() == Rank.ANONYMOUS);
            	break;
    	    // nah, we may always view other nodes.,....		
            case Operation.READ_INT:         
            	permitted = true;
            	break;
            // same rights as writing, no break
	    case Operation.DELETE_INT:
            // dont think so when we are anonymous...
            case Operation.WRITE_INT:
            case Operation.CHANGECONTEXT_INT:
            	// we are logged in, check if we may edit this node,....
            	if(user.getRank() != Rank.ANONYMOUS) {
                    MMObjectNode node = getMMNode(nodeNumber);
                    String ownerName = node.getStringValue("owner");
    	    	    log.debug("Owner of checking field is:'" + ownerName + "' and user is '" + user.getIdentifier() + "'");
                    permitted = ownerName.equals(user.getIdentifier());
                }                  
            	else {
                    // if user is anonymous.....
                    permitted = false;
            	}
            	break;
            default:
            	throw new org.mmbase.security.SecurityException("Operation was NOT permitted, OPERATION UNKNOWN????");
        }   
         
        if (permitted) {            
            log.trace("operation was permitted");
        } else {
            log.info(" user: " + user.getIdentifier() + " operation: " + operation + " node: " + nodeNumber  + "   operation was NOT permitted");
        }
        return permitted;
    }

    public void assert(UserContext user, int node, Operation operation) 
        throws org.mmbase.security.SecurityException {
        // hmm, we can use check :)
        if(manager.getActive()){
            if (!check(user, node, operation)) {               
                throw new org.mmbase.security.SecurityException(
                    "Operation '" + operation + "' on " + node + " was NOT permitted to " + user.getIdentifier());
            }
        }
    }
    
    public String getContext(UserContext user, int nodeNumber) throws org.mmbase.security.SecurityException {    	
	assert(user, nodeNumber, Operation.READ);

    	// and get the value...		
	MMObjectNode node = getMMNode(nodeNumber);	
	return node.getStringValue("owner");
    }

    /** 
     * This method does nothing, except from checking if the setContext was valid..
     */        
    public void setContext(UserContext user, int nodeNumber, String context) throws org.mmbase.security.SecurityException {
	// check if is a valid context for us..
	java.util.HashSet possible = getPossibleContexts(user, nodeNumber);
	if(!possible.contains(context)) {
	    String msg = "could not set the context to "+context+" for node #"+nodeNumber+" by user: " +user+"not a valid context";
	    log.error(msg);
	    throw new org.mmbase.security.SecurityException(msg);
    	}
	
	// check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
	assert(user, nodeNumber, Operation.CHANGECONTEXT);
	
	// well now really set it...
	MMObjectNode node = getMMNode(nodeNumber);
    	node.setValue("owner", user.getIdentifier());
        node.commit();	
	log.info("changed context settings of node #"+nodeNumber+" to context: "+context+ " by user: " +user);		
    }
    
    /** 
     * This method does nothing, except from returning a dummy value
     */        
    public java.util.HashSet getPossibleContexts(UserContext user, int nodeNumber) throws org.mmbase.security.SecurityException {
        ExtendedProperties reader = new ExtendedProperties();

        log.debug("reading accounts from " + configFile);
        java.util.Hashtable accounts = reader.readProperties(configFile.getAbsolutePath());

        if (accounts == null) {
            log.error("Could not find accounts!");
        }

        // return a list of the users possible..
	java.util.HashSet set = new java.util.HashSet(accounts.keySet());
	log.debug("returning possible contexts, amount of entries is:" + set.size() + " the following entries where found:" );
	// for(java.util.Iterator i= set.iterator(); i.hasNext(); log.debug("\t"+i.next()) );    	    
	return set;
    }    
}
