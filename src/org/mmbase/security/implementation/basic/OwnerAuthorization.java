/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.basic;

import org.mmbase.bridge.Query;
import org.mmbase.util.ExtendedProperties;

import org.mmbase.module.core.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

/**
 * A very simple Authorization implementation
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 * @version $Id: OwnerAuthorization.java,v 1.10 2003-09-26 19:41:17 michiel Exp $
 */
public class OwnerAuthorization extends Authorization {

    private static final Logger log = Logging.getLoggerInstance(OwnerAuthorization.class);

    private static MMObjectBuilder builder = null; // only to get Nodes from

    private MMObjectNode getMMNode(int n) {
        if(builder == null) {
            MMBase mmb = MMBase.getMMBase();
            builder =  mmb.getMMObject("typedef"); // only because it always exists
            if(builder == null) throw new SecurityException("Builder 'typedef'not found.");
        }
        MMObjectNode node = builder.getNode(n);
        if(node == null) throw new SecurityException("Node '" + n + "' not found");
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
                if (log.isDebugEnabled()) {
                    log.debug("Owner of checking field is:'" + ownerName + "' and user is '" + user.getIdentifier() + "'");
                }
                permitted = ownerName.equals(user.getIdentifier());
            }
            else {
                // if user is anonymous.....
                permitted = false;
            }
            break;
        default:
            throw new SecurityException("Operation '" + operation + "' on node '" + nodeNumber + "' was NOT permitted to user '" + user + "' (Operation unknown?)");
        }
        
        if (log.isDebugEnabled()) {
            if (permitted) {
                log.trace("operation was permitted");
            } else {
                log.debug(" user: " + user.getIdentifier() + " operation: " + operation + " node: " + nodeNumber  + "   operation was NOT permitted");
            }
        }
        return permitted;
    }

    public boolean check(UserContext user, int nodeNumber, int srcNodeNumber, int dstNodeNumber, Operation operation) {
        if(manager.getActive()){
            if (user.getRank() == Rank.ANONYMOUS) {
                if (log.isDebugEnabled()) {
                    log.debug(" user: " + user.getIdentifier() + " operation: " + operation + " node: " + nodeNumber  + "   operation was NOT permitted");
                }
                return false;
            }
        }
        return true;
    }

    public String getContext(UserContext user, int nodeNumber) throws SecurityException {
        verify(user, nodeNumber, Operation.READ);        
        // and get the value...
        MMObjectNode node = getMMNode(nodeNumber);
        return node.getStringValue("owner");
    }

    /**
     * This method does nothing, except from checking if the setContext was valid..
     */
    public void setContext(UserContext user, int nodeNumber, String context) throws SecurityException {
        // check if is a valid context for us..
        Set possible = getPossibleContexts(user, nodeNumber);
        if(!possible.contains(context)) {
            throw new SecurityException("could not set the context to "+context+" for node #"+nodeNumber+" by user: " +user+"not a valid context");
        }
        
        // check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
        verify(user, nodeNumber, Operation.CHANGECONTEXT);
        
        // well now really set it...
        MMObjectNode node = getMMNode(nodeNumber);
        node.setValue("owner", user.getIdentifier());
        node.commit();
        if (log.isServiceEnabled()) {
            log.service("changed context settings of node #" + nodeNumber + " to context: " + context + " by user: " + user);
        }
    }

    /**
     * Returns a list of all users in accounts.properties
     */
    public Set getPossibleContexts(UserContext user, int nodeNumber) throws org.mmbase.security.SecurityException {
        ExtendedProperties reader = new ExtendedProperties();

        if (log.isDebugEnabled()) {
            log.debug("reading accounts from " + configFile);
        }
        java.util.Hashtable accounts = reader.readProperties(configFile.getAbsolutePath());

        if (accounts == null) {
            log.error("Could not find accounts!");
        }

        // return a list of the users possible..
        Set set = accounts.keySet();
        if (log.isDebugEnabled()) {
            log.debug("returning possible contexts, amount of entries is:" + set.size() + " the following entries where found:" );
            // for(java.util.Iterator i= set.iterator(); i.hasNext(); log.debug("\t"+i.next()) );
        }
        return set;
    }

    public QueryCheck check(UserContext user, Query query, Operation operation) {
        if(user.getRank().getInt() >= Rank.ADMIN.getInt()) {
            return COMPLETE_CHECK;
        }
        if(operation == Operation.READ) {
            return COMPLETE_CHECK;
        } else {
            return NO_CHECK;
        }
    }
}
