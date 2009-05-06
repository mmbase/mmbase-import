/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloud;

import org.mmbase.security.*;
import org.mmbase.bridge.Query;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

/**
 * Simple authorization implemenation for 'cloud' security implemenation based only on an mmbasusers
 * builder. Read-rights on everything for everybody. User named 'admin' has rank administrator and
 * may do everything. Normal users have no rights on a limited set of 'admin' builders. Normal users may do everything on their 'own' nodes, and 
 * on nodes with the owner field '[shared]'.
 *
 * @author Eduard Witteveen
 * @version $Id: Verify.java,v 1.2 2007-06-21 15:50:27 nklasens Exp $
 */
public class Verify extends Authorization {
    private static final Logger log = Logging.getLoggerInstance(Verify.class);
    private static final Set<String> adminBuilders = new HashSet<String>();

    protected void load() {
	adminBuilders.add("typedef");
	adminBuilders.add("syncnodes");
	adminBuilders.add("mmservers");
	adminBuilders.add("icaches");
	adminBuilders.add("versions");
	adminBuilders.add("typerel");
	adminBuilders.add("reldef");
	adminBuilders.add("daymarks");
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
	  well the following rules apply.....
	  - everyone may read everything
	  - anonymous may do nothing further..
	  - basic user do anything to nodes which belong to him...
	  - may see/change it's own mmbase-user builder-node
	  - admin may do everything....
	*/
	// everyone may read everything....
	if(operation == Operation.READ) return true;

	// anonoymous may do nothing further...
	if(user.getRank() == Rank.ANONYMOUS) return false;

	// link operation may always be done by basic users..
	if(operation == Operation.CHANGE_RELATION) return true;

	MMObjectNode node = getMMNode(nodeid);

	String username = user.getIdentifier();
	String builder = node.getName();
	Rank rank = user.getRank();
        if (log.isDebugEnabled()) {
            log.debug("[node #" + nodeid + "] check by [" + user.getIdentifier() + "] (" + rank + ")for operation [" + operation + "]");
        }

	// which situation do we have? security or not security objects..
	// onlything that we have to lookout for are:
	//- we are creating a new user
	//- we are changing behaviour of a user....
	if(builder.equals("mmbaseusers")) {
	    // look at our node..
	    if(node.getStringValue("username").equals(username)){
		if(operation == Operation.WRITE) return true;
		if(operation == Operation.DELETE) return false;
	    }
	    // further nothing allowed, unless we are the admin..
	    return rank == Rank.ADMIN;
	} else if(operation != Operation.CREATE && adminBuilders.contains(builder)) {
	    // most core builders cant be used by basic users...
	    return rank == Rank.ADMIN;
	}
	else {
	    // admin may do everything else..
	    if(rank == Rank.ADMIN) return true;

	    if(operation == Operation.CREATE) {
		String buildername = node.getStringValue("name");
		if(buildername.equals("mmbaseusers")) {
		    return false;
		}
		else if (adminBuilders.contains(buildername)) {
		    return false;
		}
		return true;
	    }

	    // change context and change node itselve only allowed for the owner...
	    if(operation == Operation.WRITE || operation == Operation.CHANGE_CONTEXT || operation == Operation.DELETE) {
		// look if this is a valid context...
		String context = node.getStringValue("owner");
		if(!getPossibleContexts(user, nodeid).contains(context)) {
		    log.warn("context with name:'" + context + "' not found as user, granting the user the rights for operation:" + operation + " on node #" + nodeid);
		    return true;
		}
		return context.equals(username) || context.equals(SHARED_CONTEXT_ID);
	    }
	    // basic users may do everything further...
	    return true;
	}
    }

    private static String SHARED_CONTEXT_ID = "[shared]";

    public boolean check(UserContext user, int nodeid, int srcnodeid, int dstnodeid, Operation operation) {
	// link is always permitted!
	if(user.getRank() == Rank.ANONYMOUS) return false;
	return true;
    }

    public String getContext(UserContext user, int nodeid) throws org.mmbase.security.SecurityException {
	// check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
	verify(user, nodeid, Operation.READ);

	// and get the value...
	MMObjectNode node = getMMNode(nodeid);
	return node.getStringValue("owner");
    }

    public void setContext(UserContext user, int nodeid, String context) throws org.mmbase.security.SecurityException {
	// check if is a valid context for us..
	if(!getPossibleContexts(user, nodeid).contains(context)) {
	    throw new org.mmbase.security.SecurityException("could not set the context to " + context + " for node #" + nodeid + " by user: " + user);
	}
	// check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
	verify(user, nodeid, Operation.CHANGE_CONTEXT);

	// well now really set it...
	MMObjectNode node = getMMNode(nodeid);
	node.setValue("owner", context);
	node.commit();
	log.info("[node #"+nodeid+"] context set ["+user.getIdentifier()+"]");
    }

    public Set<String> getPossibleContexts(UserContext user, int nodeid) throws org.mmbase.security.SecurityException {
	// retrieve all the users....
	MMBase mmb = MMBase.getMMBase();
	UserBuilder builder =  (UserBuilder) mmb.getBuilder("mmbaseusers");
	Enumeration<MMObjectNode> e = builder.search(null);
	Set<String> contexts = new HashSet<String>();
	while(e.hasMoreElements()) {
	    contexts.add(e.nextElement().getStringValue("username"));
	}
	contexts.add(SHARED_CONTEXT_ID);
        contexts.add(builder.getNode(nodeid).getStringValue("owner"));
	return contexts;
    }

    private static MMObjectBuilder builder = null;
    private MMObjectNode getMMNode(int n) {
	if(builder == null) {
            MMBase mmb = MMBase.getMMBase();
	    builder =  mmb.getBuilder("typedef");
	    if(builder == null) {
		throw new org.mmbase.security.SecurityException("builder typedef not found");
	    }
	}
	MMObjectNode node = builder.getNode(n);
	if(node == null) {
	    throw new org.mmbase.security.SecurityException("node not found");
	}
	return node;
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
