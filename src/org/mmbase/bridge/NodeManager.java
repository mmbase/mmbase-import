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

    /**
    * Creates a new initialized node.
    * The returned node will not be visible in the cloud until the commit() method is called on this node.
    * @return the new <code>Node</code>
    */
    public Node createNode();
 	
 	/**
     * Retrieves the Cloud to which this manager belongs
     */
    public Cloud getCloud();

	/**
     * Retrieve the identifying name of the NodeManager
     */
    public String getName();
 	
	/**
     * Retrieve the descriptive name of the NodeManager (in the default language defined in mmbaseroot.xml)
     */
    public String getGUIName();

	/** 
	 * Retrieve the description of the NodeManager.
	 */
	public String getDescription();

	/**
	 * Retrieve all field types of this NodeManager.
	 * @return a <code>List</code> of <code>FieldType</code> objects
	 */
	public FieldTypeList getFieldTypes();

	/**
	 * Retrieve the field type for a given fieldname.
	 * @param fieldName name of the field to retrieve
	 * @return the requested <code>FieldType</code>
	 */
	public FieldType getFieldType(String fieldName);
	
	/**
     * Search nodes beloingin to this NodeManager.
     * @param where The contraint. this is in essence a SQL where clause.
     *      Examples: "email IS NOT NULL", "lastname='admin' OR lastname = 'sa'"
     * @param order the fieldname on which you want to sort.
     *      Examples: 'lastname', 'number'
     * @param direction indicates whether the sort is ascending (true) or descending (false).
     * @return a <code>List</code> of found nodes
     */
    public NodeList getList(String where, String sorted, boolean direction);

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

}
