/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
// import org.mmbase.security.*;
import java.util.*;
import javax.servlet.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This class represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types. It also contains some maintenance code - code
 * to create new nodes, en code to query objects belonging to the same manager.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (which is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicNodeManager implements NodeManager {

    // Cloud for this nodetype
    protected BasicCloud cloud=null;

    // builder on which the type is based
    protected MMObjectBuilder builder=null;

    // field types
    protected Hashtable fieldTypes = new Hashtable();

    // empty constructor for overriding constructors
    BasicNodeManager() {
    }

    // constructor for creating a Manager for specific cloud
    BasicNodeManager(MMObjectBuilder builder, Cloud cloud) {
        init(builder, cloud);
    }

    protected void init(MMObjectBuilder builder, Cloud cloud) {
        this.cloud=(BasicCloud)cloud;
        this.builder=builder;
        if (!builder.tableName.equals("multirelations")) {
    	    for(Iterator i=builder.getFields().iterator(); i.hasNext();){
	            FieldDefs f=(FieldDefs)i.next();
	            Field ft= new BasicField(f,this);
	            fieldTypes.put(ft.getName(),ft);
    	    }
        }
    }

    /**
    * Creates a new initialized node.
    * The returned node will not be visible in the cloud until the commit() method is called on this node.
    * @return the new <code>Node</code>
    */
    public Node createNode() {
//        cloud.assert(Operation.CREATE,builder.oType);
        // create object as a temporary node
        int id = cloud.uniqueId();
        System.out.println();
        String currentObjectContext = BasicCloudContext.tmpObjectManager.createTmpNode(builder.getTableName(), cloud.getAccount(), ""+id);
        // if we are in a transaction, add the node to the transaction;
        if (cloud instanceof BasicTransaction) {
            ((BasicTransaction)cloud).add(currentObjectContext);
        }
        MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(cloud.getAccount(), ""+id);
        // set the owner to userName instead of account
//        node.setValue("owner",cloud.getUserName());
        node.setValue("owner","bridge");
        return new BasicNode(node, this, id);
    }

 	/**
     * Retrieves the Cloud to which this manager belongs
     */
    public Cloud getCloud() {
        return cloud;
    }

	/**
     * Retrieve the identifying name of the NodeManager
     */
    public String getName() {
        return builder.getTableName();
    }

	/**
     * Retrieve the descriptive name of the NodeManager
     * Note: currently, this method returns the nodetype/builder table name!
     */
    public String getGUIName() {
	    Hashtable singularNames=builder.getSingularNames();
        if (singularNames!=null) {
            String lang=cloud.language;
            String tmp=(String)singularNames.get(lang);
            if (tmp!=null) {
                return tmp;
            }
        }
	    return builder.getTableName();
	}

	/** 
	 * Retrieve the description of the NodeManager.
	 */
	public String getDescription() {
	    Hashtable descriptions=builder.getDescriptions();
        if (descriptions!=null) {
            String lang=cloud.language;
            String tmp=(String)descriptions.get(lang);
            if (tmp!=null) {
                return tmp;
            }
        }
	    return builder.getDescription();
	}

	/**
	 * Retrieve all field types of this NodeManager.
	 * @return a <code>List</code> of <code>FieldType</code> objects
	 */
	public FieldList getFields() {
	    return new BasicFieldList(fieldTypes.values(),cloud,this);
	}

	/**
	 * Retrieve the field type for a given fieldname.
	 * @param fieldName name of the field to retrieve
	 * @return the requested <code>FieldType</code>
	 */
	public Field getField(String fieldName) {
	    Field f= (Field)fieldTypes.get(fieldName);
	    return f;
	}
	
	/**
     * Search nodes beloingin to this NodeManager.
     * @param where The contraint. this is in essence a SQL where clause.
     *      Examples: "email IS NOT NULL", "lastname='admin' OR lastname = 'sa'"
     * @param order the fieldname on which you want to sort.
     *      Examples: 'lastname', 'number'
     * @param direction indicates whether the sort is ascending (true) or descending (false).
     * @return a <code>List</code> of found nodes
     */
    public NodeList getList(String constraints, String orderby,
                            String directions) {
        // "String directions" isn't used yet!!
        String where = constraints;
        String sorted = orderby;
        boolean direction = true;

  		Vector v;
  		if ((where!=null) && (!where.trim().equals(""))) {
		    where="WHERE "+where;
  		}
  		if ((sorted!=null) && (!sorted.trim().equals(""))) {
		    v = builder.searchVector(where,sorted,direction);
	    } else {
		    v = builder.searchVector(where);
	    }
	    // remove all nodes that cannot be accessed
//	    for(int i=(v.size()-1); i>-1; i--) {
//	        if (!cloud.check(Operation.READ, ((MMObjectNode)v.get(i)).getIntValue("number"))) {
//	            v.remove(i);
//	        }
//	    }
	    return new BasicNodeList(v,cloud,this);
    }


	/**
	 * Retrieve info from a node manager based on a command string.
	 * Similar to the $MOD command in SCAN.
	 * @param command the info to obtain, i.e. "USER-OS".
	 */
	public String getInfo(String command) {
	    return getInfo(command, null,null);
	}

	/**
	 * Retrieve info from a node manager based on a command string
	 * Similar to the $MOD command in SCAN.
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param req the Request item to use for obtaining user information. For backward compatibility.
	 * @param resp the Response item to use for redirecting users. For backward compatibility.
	 */
	public String getInfo(String command, ServletRequest req,  ServletResponse resp){
	    StringTokenizer tokens= new StringTokenizer(command,"-");
	    return builder.replace(BasicCloudContext.getScanPage(req, resp),tokens);
	}
	
	/**
	 * Retrieve info (as a list of virtual nodes) from a node manager based on a command string.
	 * Similar to the LIST command in SCAN.
	 * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param parameters a hashtable containing the named parameters of the list.
	 */
	public NodeList getList(String command, Hashtable parameters){
	    return getList(command,parameters,null,null);
	}

	/**
	 * Retrieve info from a node manager based on a command string
	 * Similar to the LIST command in SCAN.
	 * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param parameters a hashtable containing the named parameters of the list.
	 * @param req the Request item to use for obtaining user information. For backward compatibility.
	 * @param resp the Response item to use for redirecting users. For backward compatibility.
	 */
	public NodeList getList(String command, Hashtable parameters, ServletRequest req, ServletResponse resp){
	    StringTagger params= new StringTagger("");
	    if (parameters!=null) {
	        for (Enumeration keys=parameters.keys(); keys.hasMoreElements(); ) {
	            String key=(String)keys.nextElement();
	            Object o = parameters.get(key);
	            if (o instanceof Vector) {
	                params.setValues(key,(Vector)o);
    	        } else {
	                params.setValue(key,""+o);
	            }
	        }
	    }
	    try {
	        StringTokenizer tokens= new StringTokenizer(command,"-");
    	    Vector v=builder.getList(BasicCloudContext.getScanPage(req, resp),params,tokens);
    	    if (v==null) { v=new Vector(); }
            int items=1;
    	    try { items=Integer.parseInt(params.Value("ITEMS")); } catch (Exception e) {}
	        Vector fieldlist=params.Values("FIELDS");
	        Vector res=new Vector(v.size() / items);
    	    for(int i= 0; i<v.size(); i+=items) {
    	        MMObjectNode node = new MMObjectNode(builder);
    	        for(int j= 0; (j<items) && (j<v.size()); j++) {
    	            if ((fieldlist!=null) && (j<fieldlist.size())) {
        	            node.setValue((String)fieldlist.get(j),v.get(i+j));
    	            } else {
        	            node.setValue("item"+(j+1),v.get(i+j));
        	        }
    	        }
    	        res.add(node);
    	    }
   		    NodeManager tempNodeManager = null;
   		    if (res.size()>0) {
  		        tempNodeManager = new VirtualNodeManager((MMObjectNode)res.get(0),cloud);
            }
            return new BasicNodeList(res,cloud,tempNodeManager);
    	} catch (Exception e) {
    	    throw new BridgeException(""+e);
    	}
	}

}
