/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.io.File;
import java.util.Set;
import org.mmbase.util.FileWatcher;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * This class is a empty implementation of the Authorization, it will only
 * return that operations are valid. To make your own implementation of
 * authorization, you have to extend this class.
 *
 * @author Eduard Witteveen
 * @version $Id: Authorization.java,v 1.15 2003-07-09 07:25:05 michiel Exp $
 */
public abstract class Authorization extends Configurable {
    private static Logger log = Logging.getLoggerInstance(Authorization.class);

    /**
     *	This method could be overrided by an extending class.
     *	It has to be called, when a new Node has been created.
     *	This way, the authentication can create default rights
     *	for this object, depending on the UserContext and generate
     *	logging information.
     *	@param user The UserContext, containing the information
     *	    about the user.
     *	@param nodeid The id of the MMObjectNode, which has just been added to
     *	    the MMBase cloud.
     */
    public abstract void create(UserContext user, int nodeid);

    /**
     *	This method should be overrided by an extending class.
     *	It has to be called, when a Node has been changed.
     *	This way, the authentication can generate log information
     *	for this object, which can be used for accountability
     *	@param user The UserContext, containing the information about the user.
     *	@param nodeid The id of the MMObjectNode, which has just been changed
     *	    in the cloud.
     */
    public abstract void update(UserContext user, int nodeid);

    /**
     *	This method should be overrided by an extending class.
     *	It has to be called, when a Node has been removed from
     *	the cloud.
     *	This way, the authentication can generate log information
     *	for this node, and remove the authorization object which
     *	belongs to this node.
     *	@param user The UserContext, containing the information
     *	    about the user.
     *	@param nodeid The id of the MMObjectNode, which has just been removed
     *	    in the cloud.
     */
    public abstract void remove(UserContext user, int nodeid);

    /**
     *	This method should be overrided by an extending class.
     *	This method checks if an operation is permitted on a certain node done
     *	by a certain user.
     *	@param user The UserContext, containing the information the user.
     *	@param nodeid The id of the MMObjectNode, which has to be checked.
     *                It the action is CREATE then this will be interpreted as a typedef node.
     *	@param operation The operation which will be performed.
     *	@return <code>true</code> if the operation is permitted,
     *	    	<code>false</code> if the operation is not permitted,
     */
    public abstract boolean check(UserContext user, int nodeid, Operation operation);



    /**
     * This method wraps the check-method with the same arguments. The only difference being that it
     * throws on exception if the specified operation is not permitted. 
     *
     * It is wise to override check, and not verify (And I wonder why this method is not simply final).
     *
     * @exception org.mmbase.SecurityException  If the assertion fails
     * @see #check(UserContext, int, Operation)
     */
    public void verify(UserContext user, int nodeid, Operation operation) throws org.mmbase.security.SecurityException {
        if (!check(user, nodeid, operation)) {
            throw new org.mmbase.security.SecurityException("Operation '" + operation + "' on " + nodeid + " was NOT permitted to " + user.getIdentifier());
        }
    }

    /**
     * This method should be overrided by an extending class.
     * This method checks if the creation of a certain relation or changing
     * the source or destination of a certain relation done by a certain
     * user is permitted.
     *
     * @param user      The UserContext, containing the information about the user.
     * @param nodeid    The id of the relation which has to be checked.  If the operation is CREATE
     * then this will be interpreted as the typedef node (extending insrel) for the relation to be
     * created.
     * @param srcnodeid The id of the (new) source node of the relation.
     * @param dstnodeid The id of the (new) destination node of the relation.
     * @param operation The operation which will be performed (CREATE (create
     *                  relation) or CHANGE_RELATION (source and/or destination
     *                  are changed).
     * @return <code>true</code> if the operation is permitted,
     *         <code>false</code> if the operation is not permitted,
     */
    public abstract boolean check(UserContext user, int nodeid, int srcnodeid, int dstnodeid, Operation operation);

    /**
     * This method wraps the check-method with the same arguments. The only difference being that it
     * throws on exception if the specified operation is not permitted. 
     *
     * It is wise to override check, and not verify (And I wonder why this method is not simply final).
     *
     * @exception org.mmbase.SecurityException  If the assertion fails
     * @see #check(UserContext, int, int, int, Operation)
     */
    public void verify(UserContext user, int nodeid, int srcnodeid, int dstnodeid, Operation operation) throws org.mmbase.security.SecurityException {
        if (!check(user, nodeid, srcnodeid, dstnodeid, operation)) {
            throw new org.mmbase.security.SecurityException(
                "Operation '" + operation + "' on " + nodeid + " was NOT permitted to " + user.getIdentifier());
        }
    }

    /**
     *	This method could be overrided by an extending class.
     *	This method returns the context of a specific node.
     *	@param  user 	The UserContext, containing the information about the user.
     *	@param  nodeid  The id of the MMObjectNode, which has to be asserted.
     *	@return the context setting of the node.
     *	@exception org.mmbase.SecurityException If operation is not allowed(needs read rights)
     */
    public abstract String getContext(UserContext user, int nodeid) throws org.mmbase.security.SecurityException;

    /**
     *	This method could be overrided by an extending class.
     *	This method changes the rights on a node, by telling
     *	the authorization that it should use the context which
     *	is defined.
     *	@param user The UserContext, containing the information about the user.
     *	@param nodeid The id of the MMObjectNode, which has to be asserted.
     *	@param context The context which rights the node will get
     *	@exception org.mmbase.SecurityException If operation is not allowed
     *	@exception org.mmbase.SecurityException If context is not known
     */
    public abstract void setContext(UserContext user, int nodeid, String context) throws org.mmbase.security.SecurityException;

    /**
     *	This method could be overrided by an extending class.
     *	This method returns a list of contexts which can be
     *	used to change the node.
     *	@param user The UserContext, containing the information
     *	    about the user.
     *	@param nodeid The id of the MMObjectNode, which has to be asserted.
     *	@return a <code>Set</code> of <code>String</code>s which
     *	    	represent a context in readable form..
     *	@exception org.mmbase.SecurityException maybe
     */
    public abstract Set getPossibleContexts(UserContext user, int nodeid) throws org.mmbase.security.SecurityException ;
}
