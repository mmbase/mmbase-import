package nl.didactor.security;

import java.util.*;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.builders.DidactorRel;
import org.mmbase.security.SecurityException;
import org.mmbase.security.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

/**
 * This class is based on the NoAuthorization class from MMBase.
 *
 * @author Eduard Witteveen
 * @version $Id: Authorization.java,v 1.1 2005-12-02 10:21:39 jverelst Exp $
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
     * No authorization means that everyting is allowed
     * @return true
     */
    public boolean check(org.mmbase.security.UserContext user, int nodeid, Operation operation) {
        return true;
    }

    /**
     * This method will call the 'preDelete()' method for the builder to which this node that is deleted belongs. 
     */
    public void verify(org.mmbase.security.UserContext user, int nodeid, Operation operation) throws org.mmbase.security.SecurityException {
        if (operation.equals(Operation.DELETE)) {
            MMObjectBuilder objectBuilder = org.mmbase.module.core.MMBase.getMMBase().getBuilder("object");
            MMObjectNode node = objectBuilder.getNode(nodeid);
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
     * No authorization means that everyting is allowed
     * @return true
     */
    public boolean check(org.mmbase.security.UserContext user, int nodeid, int srcNodeid, int dstNodeid, Operation operation) {
        return true;
    }

    /**
     * This method does nothing
     */
    public void verify(org.mmbase.security.UserContext user, int nodeid, int srcNodeid, int dstNodeid, Operation operation) throws SecurityException {
    }


    /**
     * This method does nothing, except from giving a specified string back
     */
    public String getContext(org.mmbase.security.UserContext user, int nodeid) throws SecurityException {
        return EVERYBODY;
    }

    /**
     * Since this is not authorization, we simply allow every change of context.
     */
    public void setContext(org.mmbase.security.UserContext user, int nodeid, String context) throws SecurityException {        
        //if(!EVERYBODY.equals(context)) throw new SecurityException("unknown context");
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
