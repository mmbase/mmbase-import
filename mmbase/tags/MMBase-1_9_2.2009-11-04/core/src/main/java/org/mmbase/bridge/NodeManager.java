/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.Map;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;


/**
 * <p>
 * This interface represents a node's type information object. This is also commonly - because of
 * legacy -  referred to as 'builder'.</p>
 *
 * <p>It contains all the field and attribute information, as well as GUI data for editors and
 * some information on derived and deriving types. It also contains some maintenance code - code
 * to create new nodes, and code to query objects belonging to the same manager.</p>
 *
 * <p> Node types are normally maintained through use of config files (and not in the database), in so
 * called 'builder xmls'.</p>
 *
  <p>* A NodeManager does however extend {@link Node}, because an entry for each node manager is stored
 * in the 'typedef' NodeManager. The number {@link Node#getNumber} of the NodeManager is the 'otype'
 * field of the Nodes of that type.</p>

 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface NodeManager extends Node {

    /**
     * A constant for use with {@link #getFields(int)}, meaning `all fields, even those which are
     * not stored.
     */
    public final static int ORDER_NONE = -1;
    /**
     * A constant for use with {@link #getFields(int)}, meaning `all fields, in storage order (so
     * which are in storage'.
     */
    public final static int ORDER_CREATE = 0;
    /**
     * A constant for use with {@link #getFields(int)}, meaning all fields which a user may want to
     * fill when creating or editing this node. That are normally all nodes without the `automatic'
     * ones like `number' and `otype'.
     */
    public final static int ORDER_EDIT = 1;
    /**
     * A constant for use with {@link #getFields(int)}. When presenting a Node in some list overview
     * then less essential fields can be left out, to get a more concise presentation of the node.
     */
    public final static int ORDER_LIST = 2;
    /**
     * A constant for use with {@link #getFields(int)} On some fields, like binary fields (e.g. images) it makes no sense searching. These are left
     * out among the `search' fields.
     */
    public final static int ORDER_SEARCH = 3;

    public final static int GUI_SINGULAR = 1;
    public final static int GUI_PLURAL = 2;

    /**
    * Creates a new node. The returned node will not be visible in the cloud
    * until the commit() method is called on this node. Until then it will have
    * a temporary node number.
    *
    * @return the new <code>Node</code>
    */
    public Node createNode();

    /**
     * Returns the cloud to which this manager belongs.
     *
     * @return the cloud to which this manager belongs
     */
    public Cloud getCloud();

    /**
     * Retrieve the parent of this NodeManager (the Nodemanager that this nodemanager extends from)
     * @return the NodeManager's parent
     * @throws NotFoundException if no parent exists (i.e. this nodeManager is "object")
     */
    public NodeManager getParent() throws NotFoundException;

    /**
     * Retrieve a  list of descendant nodemanagers (the nodemanagers that - possibly indirectly - extend from this nodemanager)
     * @return a list of NodeManagers
     * @since MMBase-1.7
     */
    public NodeManagerList getDescendants();

    /**
     * Returns the name of this node manager. This name is a unique name.
     *
     * @return the name of this node manager.
     */
    public String getName();

    /**
     * Retrieve a property of the node manager.
     * @param name the name of the property
     * @return the property value (null if not given)
     * @since  MMBase-1.7
     */
    public String getProperty(String name);

    /**
     * Retrieve a copy of the node manager's properties
     * @return a map of node manager properties
     * @since  MMBase-1.7
     */
    public Map<String, String> getProperties();

    /**
     * Returns the descriptive name of this node manager. This name will be in
     * the language of the current cloud  (defined in cloud.getLocale()).
     *
     * @return the descriptive name of this node manager
     */
    public String getGUIName();

    /**
     * Returns the descriptive name of this node manager. This name will be in
     * the language of the current cloud  (defined in cloud.getLocale()).
     *
     * @since MMBase-1.6
     * @param plurality the plurality (number of objects) for which to return a description
     *                  ({@link #GUI_SINGULAR} or {@link #GUI_PLURAL})
     * @return the descriptive name of this node manager
     */
    public String getGUIName(int plurality);

    /**
     * Returns the descriptive name of this node manager ina a specified language.
     *
     * @since MMBase-1.6
     * @param plurality the plurality (number of objects) for which to return a description
     *                  ({@link #GUI_SINGULAR} or {@link #GUI_PLURAL})
     * @param locale the locale that determines the language for the GUI name
     * @return the descriptive name of this node manager
     */
    public String getGUIName(int plurality, Locale locale);

    /**
     * Returns the description of this node manager.
     *
     * @return the description of this node manager
     */
    public String getDescription();

    /**
     * Returns the description of this node manager in a specified language.
     *
     * @param locale the locale that determines the language for the description
     * @return the description of this node manager
     * @since MMBase-1.7
     */
    public String getDescription(Locale locale);

    /**
     * Returns a list of all fields defined for this node manager.
     *
     * @return a list of all fields defined for this node manager
     */
    public FieldList getFields();

    /**
     * Retrieve a subset of field types of this NodeManager, depending on a given order. The order
     * is a integer constant which symbolizes `none', `create', `edit', `list' or `search'. These last three one may recognize
     * from builder XML's. `none' means `all fields'. The actual integer contants are present as the
     * ORDER contants in this interface.
     *
     * @param order the order in which to list the fields
     * @return a <code>FieldList</code> object, which is a specialized <code>List</code> of {@link Field} objects.
     * @see   #ORDER_NONE
     * @see   #ORDER_CREATE
     * @see   #ORDER_EDIT
     * @see   #ORDER_LIST
     * @see   #ORDER_SEARCH
     */
    public FieldList getFields(int order);

    /**
     * Returns the field with the specified name.
     *
     * @param name  the name of the field to be returned
     * @return      the field with the requested name
     * @throws NotFoundException is the field does not exist
     */
    public Field getField(String name) throws NotFoundException;

    /**
     * Tests whether the field with the specified name exists in this nodemanager.
     *
     * @since MMBase-1.6
     * @param fieldName  the name of the field to be returned
     * @return      <code>true</code> if the field with the requested name exists
     */
    public boolean hasField(String fieldName);

    /**
     * Returns a list of nodes belonging to this node manager. Constraints can
     * be given to exclude nodes from the returned list. These constraints
     * follow the syntax of the SQL where clause. It's a good practice to use
     * uppercase letters for the operators and lowercase letters for the
     * fieldnames. Example constraints are:
     *
     * <pre>
     * "number = 100" (!=, <, >, <= and >= can also be used)
     * "name = 'admin'",
     * "email IS NULL" (indicating the email field is empty)
     * "email LIKE '%.org'" (indication the email should end with .org)
     * "number BETWEEN 99 AND 101"
     * "name IN ('admin', 'anonymous')"
     * </pre>
     *
     * The NOT operator can be used to get the opposite result like:
     *
     * <pre>
     * "NOT (number = 100)"
     * "NOT (name = 'admin')",
     * "email IS NOT NULL"
     * "email NOT LIKE '%.org'" (indication the email should not end with .org)
     * "number NOT BETWEEN 99 AND 101"
     * "name NOT IN ('admin', 'anonymous')"
     * </pre>
     *
     * Some special functions (not part of standard SQL, but most databases
     * support them) can be used like:
     *
     * <pre>
     * "LOWER(name) = 'admin'" (to also allow 'Admin' to be selected)
     * "LENGTH(name) > 5" (to only select names longer then 5 characters)
     * </pre>
     *
     * Constraints can be linked together using AND and OR:
     *
     * <pre>
     * "((number=100) OR (name='admin') AND email LIKE '%.org')"
     * </pre>
     *
     * The single quote can be escaped using it twice for every single
     * occurence:
     *
     * <pre>
     * "name='aaa''bbb'" (if we want to find the string aaa'bbb)
     * </pre>
     *
     * For more info consult a SQL tutorial like
     * <a href="http://hea-www.harvard.edu/MST/simul/software/docs/pkg/pgsql/sqltut/sqltut.htm">Jim Hoffman's introduction to Structured Query Language</a>.
     *
     * @param constraints   Constraints to prevent nodes from being
     *                      included in the resulting list which would normally
     *                      by included or <code>null</code> if no constraints
     *                      should be applied .
     * @param orderby       A comma separated list of field names on which the
     *                      returned list should be sorted or <code>null</code>
     *                      if the order of the returned virtual nodes doesn't
     *                      matter.
     * @param directions    A comma separated list of values indicating wether
     *                      to sort up (ascending) or down (descending) on the
     *                      corresponding field in the <code>orderby</code>
     *                      parameter or <code>null</code> if sorting on all
     *                      fields should be up.
     *                      The value DOWN (case insensitive) indicates
     *                      that sorting on the corresponding field should be
     *                      down, all other values (including the
     *                      empty value) indicate that sorting on the
     *                      corresponding field should be up.
     *                      If the number of values found in this parameter are
     *                      less than the number of fields in the
     *                      <code>orderby</code> parameter, all fields that
     *                      don't have a corresponding direction value are
     *                      sorted according to the last specified direction
     *                      value.
     * @return              a list of nodes belonging to this node manager
     */
    public NodeList getList(String constraints, String orderby, String directions);



    /**
     * Creates a query for this NodeNanager. The nodemanager is added as a step, and also all (non
     * byte array) fields are added.  The query can be used  by getList of Cloud.
     *
     * You can not add steps to this NodeQuery.
     * @return query for this NodeNanager
     *
     * @since MMBase-1.7
     * @see #getList(NodeQuery)
     * @see Cloud#createNodeQuery
     */
    public NodeQuery createQuery();

    /**
     * Executes a query and returns the result as nodes of this NodeManager (or of extensions)
     * @param query query to execute
     * @return list of nodes
     *
     * @since MMBase-1.7
     */
    public NodeList getList(NodeQuery query);


    /**
     * Retrieve info from a node manager based on a command string.
     * Similar to the $MOD command in SCAN.
     * @param command the info to obtain, i.e. "USER-OS".
     * @return info from a node manager
     */
    public String getInfo(String command);

    /**
     * Retrieve info from a node manager based on a command string
     * Similar to the $MOD command in SCAN.
     * @param command the info to obtain, i.e. "USER-OS".
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     * @return info from a node manager
     */
    public String getInfo(String command, ServletRequest req,  ServletResponse resp);

    /**
     * Retrieve all relation managers that can be used to create relations for objects of this nodemanager.
     * @return the relation manager list
     * @since MMBase-1.6
     */
    public RelationManagerList getAllowedRelations();

    /**
     * Retrieve all relation managers that can be used to create relations for objects from this nodemanager,
     * to the specified manager, using the specified role and direction.
     * @param nodeManager the name of the nodemanger with which to make a relation, can be null
     * @param role the role with which to make a relation, can be null
     * @param direction the search direction ("source", "destination", "both"), can be null
     * @return the relation manager list
     * @since MMBase-1.6
     */
    public RelationManagerList getAllowedRelations(String nodeManager, String role, String direction);

    /**
     * Retrieve all relation managers that can be used to create relations for objects from this nodemanager,
     * to the specified manager, using the specified role and direction.
     * @param nodeManager the nodemanger with which to make a relation, can be null
     * @param role the role with which to make a relation, can be null
     * @param direction the search direction ("source", "destination", "both"), can be null
     * @return the relation manager list
     * @since MMBase-1.6
     */
    public RelationManagerList getAllowedRelations(NodeManager nodeManager, String role, String direction);

    /**
     * Retrieve info (as a list of virtual nodes) from a node manager based on a command string.
     * Similar to the LIST command in SCAN.
     * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
     * @param command the info to obtain, i.e. "USER-OS".
     * @param parameters a hashtable containing the named parameters of the list.
     * @return info from a node manager (as a list of virtual nodes)
     */
    public NodeList getList(String command, Map parameters);

    /**
     * Retrieve info from a node manager based on a command string
     * Similar to the LIST command in SCAN.
     * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
     * @param command the info to obtain, i.e. "USER-OS".
     * @param parameters a hashtable containing the named parameters of the list.
     * @param req the Request item to use for obtaining user information. For backward compatibility.
     * @param resp the Response item to use for redirecting users. For backward compatibility.
     * @return info from a node manager (as a list of virtual nodes)
     */
    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp);

    /**
     * Check if the current user may create a new node of this type.
     *
     * @return  Check if the current user may create a new node of this type.
     */
    public boolean mayCreateNode();

    /**
     * Returns a new, empty field list for this nodemanager
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public FieldList createFieldList();

    /**
     * Returns a new, empty node list for this nodemanager
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public NodeList createNodeList();

    /**
     * Returns a new, empty relation list for this nodemanager
     *
     * @return  The empty list
     * @since   MMBase-1.8
     */
    public RelationList createRelationList();

}
