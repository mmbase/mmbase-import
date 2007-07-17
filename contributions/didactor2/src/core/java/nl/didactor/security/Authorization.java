package nl.didactor.security;

import java.util.*;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.builders.DidactorRel;
import org.mmbase.security.SecurityException;
import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;


/**
 * @javadoc
 * @version $Id: Authorization.java,v 1.6 2007-07-17 14:36:46 michiel Exp $
 */
public class Authorization extends org.mmbase.security.Authorization {

    private static final Logger log = Logging.getLoggerInstance(Authorization.class);

    private static final String EVERYBODY = "everybody";
    private static final Set    possibleContexts = Collections.unmodifiableSet(new HashSet(Arrays.asList( new String[]{EVERYBODY, "owner"})));

    /**
     *	This method does nothing
     */
    protected void load() {
    }

    /**
     *	This method does nothing
     */
    public void create(org.mmbase.security.UserContext user, int nodeid) {
    }

    /**
     *	This method does nothing
     */
    public void update(org.mmbase.security.UserContext user, int nodeid) {
    }

    /**
     */
    public boolean check(org.mmbase.security.UserContext user, int nodeid, Operation operation) {
        if (! (user instanceof nl.didactor.security.UserContext)) {
            return false;
        } else {
            // This is in no way an elaborate implementation

            nl.didactor.security.UserContext uc = (nl.didactor.security.UserContext) user;
            if (operation.equals(Operation.DELETE)) {
                if (uc.getUserNumber() == nodeid) {
                    // you may not delete yourself
                    return false;
                } 
                MMObjectBuilder objectBuilder = MMBase.getMMBase().getBuilder("object");
                MMObjectNode node = objectBuilder.getNode(nodeid);
                if (node == null) {
                    return true;
                }
                if (node.getBuilder().getTableName().equals("people")) {
                    try {
                        UserContext otherUser = new UserContext(node, "check");
                        if (uc.getRank().getInt() < Rank.ADMIN.getInt() && otherUser.getRank().getInt() > uc.getRank().getInt()) {
                            // you may not delete user with equal/higher rank (unless, you are
                            // administrator, then you may delete other administrators)
                            return false;
                        }
                    } catch (Exception e) {
                        // if exception from new UserContext, (propably user withouth roles), then
                        // you may delete correspoding node.
                        return true;
                    }
                }
                
            }
        }
        return true;
    }

    /**
     * This method will call the 'preDelete()' method for the builder to which this node that is deleted belongs. 
     */
    public void verify(org.mmbase.security.UserContext user, int nodeid, Operation operation) throws org.mmbase.security.SecurityException {
        super.verify(user, nodeid, operation);
        if (operation.equals(Operation.DELETE)) {
            MMObjectBuilder objectBuilder = MMBase.getMMBase().getBuilder("object");
            MMObjectNode node = objectBuilder.getNode(nodeid);
            if (node == null) return;
            MMObjectBuilder builder = node.getBuilder();
            if (builder instanceof DidactorBuilder) {
                DidactorBuilder dbuilder = (DidactorBuilder)builder;
                dbuilder.preDelete(node);
            } else if (builder instanceof DidactorRel) {
                DidactorRel dbuilder = (DidactorRel)builder;
                dbuilder.preDelete(node);
            }
        }
    }

    /**
     * This method does nothing
     */
    public void remove(org.mmbase.security.UserContext user, int nodeid) {

    }

    /**
     * Checks that you don't link to roles you don't have yourself. All other relations are permitted.
     */
    public boolean check(org.mmbase.security.UserContext user, int nodeid, int srcNodeid, int dstNodeid, Operation operation) {
        nl.didactor.security.UserContext uc = (nl.didactor.security.UserContext) user;
        if (operation.equals(Operation.CREATE)) {
            if (uc.getRank().getInt() < Rank.ADMIN.getInt()) {
                // you may only give roles, which you have yourself (or, you are administrator)
                MMObjectBuilder objectBuilder = MMBase.getMMBase().getBuilder("object");
                MMObjectNode node = objectBuilder.getNode(dstNodeid);
                if (node.getBuilder().getTableName().equals("roles")) {
                    return uc.getRoles().contains(node.getStringValue("name"));
                }
            }
        }
        return true;
    }



    /**
     * This method does nothing, except from giving a specified string back
     */
    public String getContext(org.mmbase.security.UserContext user, int nodeNumber) throws SecurityException {
        MMObjectBuilder objectBuilder = MMBase.getMMBase().getBuilder("object");
        MMObjectNode node = objectBuilder.getNode(nodeNumber);
        return org.mmbase.util.Casting.toString(node.getValues().get("owner"));
    }

    /**
     * Since this is not authorization, we simply allow every change of context.
     */
    public void setContext(org.mmbase.security.UserContext user, int nodeNumber, String context) throws SecurityException {        
        MMObjectBuilder objectBuilder = MMBase.getMMBase().getBuilder("object");
        MMObjectNode node = objectBuilder.getNode(nodeNumber);
        node.setValue("owner", context);
    }

    /**
     * This method does nothing, except from returning a dummy value
     */
    public Set getPossibleContexts(org.mmbase.security.UserContext user, int nodeid) throws SecurityException {
        return possibleContexts;
    }

    public QueryCheck check(org.mmbase.security.UserContext user, org.mmbase.bridge.Query query, Operation operation) {
        return COMPLETE_CHECK;
    }

}
