/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.*;
import javax.servlet.*;
import org.mmbase.module.core.*;

/**
 * This interface represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types. It also contains some maintenance code - code
 * to create new nodes, en code to query objects belonging to the same manager.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (which is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface NodeManager {

	public final static int ORDER_CREATE = 0;
	public final static int ORDER_EDIT = 1;
	public final static int ORDER_LIST = 2;
	public final static int ORDER_SEARCH = 3;

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
     * Returns the name of this node manager. This name is a unique name.
     *
     * @return the name of this node manager.
     */
    public String getName();
 	
    /**
     * Returns the descriptive name of this node manager. This name will be in
     * the default language (defined in mmbaseroot.xml).
     *
     * @return the descriptive name of this node manager
     */
    public String getGUIName();

    /** 
     * Returns the description of this node manager.
     *
     * @return the description of this node manager
     */
    public String getDescription();

    /**
     * Returns a list of all fields defined for this node manager.
     *
     * @return a list of all fields defined for this node manager
     */
    public FieldList getFields();

	/**
	 * Retrieve a subset of field types of this NodeManager, depending on a given order.
	 *
	 * @param order the order in which to list the fields
	 * @return a <code>List</code> of <code>FieldType</code> objects
	 */
	public FieldList getFields(int order);

    /**
     * Returns the field with the specified name.
     *
     * @param name  the name of the field to be returned
     * @return      the field with the requested name
     */
    public Field getField(String name);
	
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
     * "email NOT LIKE '%.org'" (indication the email should end with .org)
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
     * <a href="http://w3.one.net/~jhoffman/sqltut.htm">this one</a>. 
     *
     * @param constraints   Contraints to prevent nodes from being
     *                      included in the resulting list which would normally
     *                      by included or <code>null</code> if no contraints
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
    public NodeList getList(String constraints, String orderby,
                            String directions);

	/**
	 * Retrieve info from a node manager based on a command string.
	 * Similar to the $MOD command in SCAN.
	 * @param command the info to obtain, i.e. "USER-OS".
	 */
	public String getInfo(String command);

	/**
	 * Retrieve info from a node manager based on a command string
	 * Similar to the $MOD command in SCAN.
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param req the Request item to use for obtaining user information. For backward compatibility.
	 * @param resp the Response item to use for redirecting users. For backward compatibility.
	 */
	public String getInfo(String command, ServletRequest req,  ServletResponse resp);
	
	/**
	 * Retrieve info (as a list of virtual nodes) from a node manager based on a command string.
	 * Similar to the LIST command in SCAN.
	 * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param parameters a hashtable containing the named parameters of the list.
	 */
	public NodeList getList(String command, Hashtable parameters);

	/**
	 * Retrieve info from a node manager based on a command string
	 * Similar to the LIST command in SCAN.
	 * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param parameters a hashtable containing the named parameters of the list.
	 * @param req the Request item to use for obtaining user information. For backward compatibility.
	 * @param resp the Response item to use for redirecting users. For backward compatibility.
	 */
	public NodeList getList(String command, Hashtable parameters, ServletRequest req, ServletResponse resp);
	
    /**
     * Check if the current user may create a new node of this type.
     *
     * @return  Check if the current user may create a new node of this type.
     */
    public boolean mayCreateNode();

	

}
