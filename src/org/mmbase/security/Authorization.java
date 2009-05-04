/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.Set;
import java.util.HashSet;

import org.mmbase.bridge.Query;
import org.mmbase.storage.search.Constraint;

/**
 * The abstract implementation of the Authorization. To make your own implementation of
 * authorization, you have to extend this class, and implement the abstract methods.
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class Authorization extends Configurable {

    /**
     *	This method should be overrided by an extending class.
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
     * @exception SecurityException  If the assertion fails
     * @see #check(UserContext, int, Operation)
     */
    public void verify(UserContext user, int nodeid, Operation operation) throws SecurityException {
        if (!check(user, nodeid, operation)) {
            throw new SecurityException("Operation '" + operation + "' on " + nodeid + " was NOT permitted to " + user.getIdentifier());
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
     * @exception SecurityException  If the assertion fails
     * @see #check(UserContext, int, int, int, Operation)
     */
    public void verify(UserContext user, int nodeid, int srcnodeid, int dstnodeid, Operation operation) throws SecurityException {
        if (!check(user, nodeid, srcnodeid, dstnodeid, operation)) {
            throw new SecurityException("Operation '" + operation + "' on " + nodeid + " was NOT permitted to " + user.getIdentifier());
        }
    }

    /**
     *	This method could be overrided by an extending class.
     *	This method returns the context of a specific node.
     *	@param  user 	The UserContext, containing the information about the user.
     *	@param  nodeid  The id of the MMObjectNode, which has to be asserted.
     *	@return the context setting of the node.
     *	@exception SecurityException If operation is not allowed(needs read rights)
     */
    public abstract String getContext(UserContext user, int nodeid) throws SecurityException;

    /**
     *	This method could be overrided by an extending class.
     *	This method changes the rights on a node, by telling
     *	the authorization that it should use the context which
     *	is defined.
     *	@param user The UserContext, containing the information about the user.
     *	@param nodeid The id of the MMObjectNode, which has to be asserted.
     *	@param context The context which rights the node will get
     *	@exception SecurityException If operation is not allowed
     *	@exception SecurityException If context is not known
     */
    public abstract void setContext(UserContext user, int nodeid, String context) throws SecurityException;

    /**
     *	This method could be overrided by an extending class.
     *	This method returns a list of contexts which can be
     *	used to change the node.
     *	@param user The UserContext, containing the information
     *	    about the user.
     *	@param nodeid The id of the MMObjectNode, which has to be asserted.
     *	@return a <code>Set</code> of <code>String</code>s which
     *	    	represent a context in readable form..
     *	@exception SecurityException
     */
    public abstract Set getPossibleContexts(UserContext user, int nodeid) throws SecurityException ;


    /**
     *	This method could be overrided by an extending class.
     *	This method returns a list of contexts availabel to a user when creating or searching for an object.
     *  The default implementation returns only the user's own default context.
     *	@param user The UserContext, containing the information
     *	    about the user.
     *	@return a <code>Set</code> of <code>String</code>s which
     *	    	represent a context in readable form..
     *	@exception SecurityException
     */
     public Set getPossibleContexts(UserContext user) throws SecurityException {
         Set contexts = new HashSet();
         contexts.add(user.getOwnerField());
         return contexts;
     }

    /**
     * Checks rights on a query. This means that the query is explored and (if possible) a
     * constraint for it is constructed, which, if appied to the query, makes it return only
     * checked results for the given user.
     *
     * Of course, this will normally only be implemented for the  'READ' operation.
     *
     * The constraint is <em>not</em> applied automaticly. This has to be done by using BasicQuery.setSecurityConstraint().
     *
     * @param user  The UserContext, for which the query must be considered
     * @param query The query to be explored
     * @return      A {@link QueryCheck} structure (containing whether the constriant is sufficient, and the
     *              new constraint or null).
     *
     * @since MMBase-1.7
     */

    public QueryCheck check(UserContext user, Query query, Operation operation) {
        return NO_CHECK;
    }


    /**
     * Constant which can be used as a result for the check query function. It means: 'No extra
     * contraints to be added, and the query's result will have to be postprocessed for security.
     *
     * @since MMBase-1.7
     */
    public static final QueryCheck NO_CHECK       = new QueryCheck(false, null);

    /**
     * Constant which can be used as a result for the check query function. It means: 'No extra
     * contraints to be added, but the query's result will <em>not</em> have to be postprocessed for
     * security. This means that there are no restrictions on the given operation at all (normally:
     * 'read' is permit to everybody).
     *
     * @since MMBase-1.7
     */
    public static final QueryCheck COMPLETE_CHECK = new QueryCheck(true,  null);


    /**
     * Defines the result of a security check on a query. Such a result has two members: A
     * 'Constraint' which has to be added to the query and a boolean which sais if the query (with
     * the given Constraint) has now been fully checked and that it's result does not need further
     * postprocessing.
     *
     * @since MMBase-1.7
     */

    public static class QueryCheck {
        Constraint constraint;
        boolean    check;
        public QueryCheck(boolean ch, Constraint co) {
            check = ch; constraint = co;
        }
        /**
         * Whether the contained result completely checks for security.
         */
        public boolean isChecked() {
            return check;
        }
        /**
         * The stored constraint. This can be null if no constraint was needed (if checked), or no helpfull
         * constraint could be constructed (if not checked).
         */
        public Constraint getConstraint() {
            return constraint;
        }
        /**
         * {@inheritDoc}
         * Used for debugging.
         * @since MMBase-1.8
         */
        public String toString() {
            return (check ? "CHECKED: " : "NOT CHECKED: ") + constraint;
        }


    }
}
