package org.mmbase.security.implementation;


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

    protected void load() {
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
        case Operation.CREATE_INT:
            // say we may always create, if we are authenticated.
            permitted = !(user.getRank() == Rank.ANONYMOUS);
            break;
        case Operation.LINK_INT:
            // nah, we always except links from other nodes.....
            permitted = !(user.getRank() == Rank.ANONYMOUS);
            break;
        case Operation.READ_INT:
            // nah, we may always view other nodes.,....
            permitted = true;
            break;
        case Operation.REMOVE_INT:
            // same rights as writing, no break
        case Operation.WRITE_INT:
            // dont think so when we are anonymous...
            // we are logged in, check if we may edit this node,....
            if(user.getRank() != Rank.ANONYMOUS) {
                MMObjectNode node = getMMNode(nodeNumber);
                String ownerName = node.getStringValue("owner");
                if(ownerName.equals("bridge")) {
                    // was created by the bridge, we can take this one....
                    log.debug("record was from bridge... hihi we take it...");
                    permitted = true;
                }
                else {
                    log.debug("Owner of checking field is:'" + ownerName +
                              "' and user is '" + user.getIdentifier() + "'");
                    permitted = ownerName.equals(user.getIdentifier());
                }                  
            }
            else {
                // if user is anonymous.....
                permitted = false;
            }
            break;
        default:
            throw new org.mmbase.security.SecurityException("Operation was NOT permitted...");
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
                throw new org.mmbase.security.SecurityException("Operation was NOT permitted...");
            }
        }
    }
}
