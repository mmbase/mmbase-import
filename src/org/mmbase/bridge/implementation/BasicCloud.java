/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.builders.MultiRelations;
import org.mmbase.util.StringTagger;
import java.util.*;

/**
 * A Cloud is a collection of Nodes (and relations that are also nodes).
 * A Cloud is tied to one or more CLoudContexts (which reside on various VMs).
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicCloud implements Cloud {

    // link to cloud context
    private BasicCloudContext cloudContext = null;

    // link to typedef object for retrieving type info (builders, etc)
    private TypeDef typedef = null;

    // name
    private String name = null;

    // description
    // note: in future, this is dependend on language settings!
    private String description = null;

    /**
     *  constructor to call from the CloudContext class
     *  (package only, so cannot be reached from a script)
     */
    BasicCloud(String cloudName, CloudContext cloudcontext) {
        cloudContext=(BasicCloudContext)cloudcontext;
        typedef = cloudContext.mmb.getTypeDef();

        // normally, we want the cloud to read it's context from an xml file.
        // the current system does not support multiple clouds yet,
        // so as a temporary hack we set default values

        name = cloudName;
        description = cloudName;
    }

	/**
	 * Retrieve the node from the cloud
	 * @param nodenumber the number of the node
	 * @return the requested node
	 */
	public Node getNode(int nodenumber) {

	    MMObjectNode node = typedef.getNode(nodenumber);
	    if (node==null) {
	        return null;
	    } else {
	        return new BasicNode(node, this);
	    }
	}

	/**
	 * Retrieves the node with given aliasname
	 * @param aliasname the aliasname of the node
	 * @return the requested node
	 */
	public Node getNode(String aliasname) {
	    MMObjectNode node = typedef.getNode(aliasname);
	    if (node==null) {
	        return null;
	    } else {
	        return new BasicNode(node, this);
	    }
	}

 	/**
     * Retrieves all node managers (aka builders) available in this cloud
     * @return an <code>Iterator</code> containing all node managers
     */
    public List getNodeManagers() {
       Vector nodeManagers = new Vector();
        for(Enumeration builders = cloudContext.mmb.mmobjs.elements(); builders.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)builders.nextElement();
            NodeManager nodeManager=new BasicNodeManager(bul, this);
            nodeManagers.add(nodeManager);
        }
       return nodeManagers;
    }

	/**
     * Retrieves a node manager (aka builder)
     * @param nodeManagerName name of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    public NodeManager getNodeManager(String nodeManagerName) {
        MMObjectBuilder bul=cloudContext.mmb.getMMObject(nodeManagerName);
        NodeManager nodeManager=new BasicNodeManager(bul, this);
        return nodeManager;
    }

	/**
     * Retrieves a node manager (aka builder)
     * @param nodeManagerID number of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    public NodeManager getNodeManager(int nodeManagerID) {
        return getNodeManager(typedef.getValue(nodeManagerID));
    }

 	/**
     * Retrieves a RelationManager
     * @param sourceManagerName name of the NodeManager of the source node
     * @param destinationManagerName name of the NodeManager of the destination node
     * @param roleName name of the role
     * @return the requested RelationManager
     */
    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
        int r=cloudContext.mmb.getRelDef().getGuessedNumber(roleName);
        int n1=typedef.getIntValue(sourceManagerName);
        int n2=typedef.getIntValue(destinationManagerName);
        Enumeration e =cloudContext.mmb.getTypeRel().search("WHERE snumber="+n1+" AND dnumber="+n2+" AND rnumber="+r);
        if (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            RelationManager relManager = new BasicRelationManager(node,this);
            return relManager;
        } else {
            return null;
        }
    };

	/**
     * Creates a node using a specified NodeManager
     * @param nodeManagerName name of the NodeManager defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(String nodeManagerName) {
        NodeManager nodeManager = getNodeManager(nodeManagerName);
	    if (nodeManager==null) {
	        return null;
	    } else {
            return nodeManager.createNode();
        }
    }

	/**
     * Creates a node using a specified NodeManager
     * @param nodeManagerID number of the NodeManager defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(int nodeManagerID) {
        NodeManager nodeManager = getNodeManager(nodeManagerID);
	    if (nodeManager==null) {
	        return null;
	    } else {
            return nodeManager.createNode();
        }
    }
	
	/**
     * Retrieves the context for this cloud
     * @return the cloud's context
     */
    public CloudContext getCloudContext() {
        return cloudContext;
    }

  	/**
     * Retrieves the cloud's name (this is an unique identifier).
     * @return the cloud's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the description of the cloud
     * @return return a description of the cloud
     */
    public String getDescription(){
        return description;
    }

	/**
     * Search nodes in a cloud accoridng to a specified filter.
     * @param nodes The numbers of the nodes to start the search with. These have to be a member of the first NodeManager
     *      listed in the nodeManagers parameter. The syntax is a comma-seperated lists of node ids.
     *      Example : '112' or '1,2,14'
     * @param nodeManagers The NodeManager chain. The syntax is a comma-seperated lists of NodeManager names.
     *      The search is formed by following the relations between successive NodeManagers in the list. It is possible to explicitly supply
     *      a RelationManager by placing the name of the manager between two NodeManagers to search.
     *      Example: 'company,people' or 'typedef,authrel,people'.
     * @param fields The fieldnames to return (comma seperated). This can include the name of the NodeManager in case of fieldnames that are used by
     *      more than one manager (i.e number).
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname', 'typedef.number,authrel.creat,people.number'
     * @param where The contraint. this is in essence a SQL where clause, using the NodeManager names from the nodes as tablenames.
     *      Examples: "people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param order the fieldnames on which you want to sort. Identical in syntax to the fields parameter.
     * @param direction A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      The first value in the list is used for teh remainig fields. Default value is <code>'UP'</code>.
     *      Examples: 'UP,DOWN,DOWN'
     * @param distinct <code>True> indicates the records returned need to be distinct. <code>False</code> indicates double values can be returned.
     * @return a <code>List</code> of found (virtual) nodes
     */
    public List search(String nodes, String nodeManagers, String fields, String where, String sorted, String direction, boolean distinct) {
  		StringTagger tagger= new StringTagger(
  		                    "NODES='"+nodes+"' TYPES='"+nodeManagers+"' FIELDS='"+fields+
  		                  "' SORTED='"+sorted+"' DIR='"+direction+"'",
  		                    ' ','=',',','\'');
  		
  		String sdistinct="";
        if (distinct) sdistinct="YES";

        Vector snodes = tagger.Values("NODES");
  		Vector sfields = tagger.Values("FIELDS");
  		Vector tables = tagger.Values("TYPES");
  		Vector orderVec = tagger.Values("SORTED");
  		Vector sdirection =tagger.Values("DIR"); // minstens een : UP
		if (direction==null) {
		    sdirection=new Vector();
		    sdirection.addElement("UP"); // UP == ASC , DOWN =DESC
		}
	  		
  		MultiRelations multirel = (MultiRelations)cloudContext.mmb.getMMObject("multirelations");
  		int nrfields = sfields.size();
  		Vector retval = new Vector();
  		if (nrfields==0) { return retval; }
  		if (where!=null) {
  		    if (where.trim().equals("")) {
  		        where = null;
  		    } else {
  		        where="WHERE "+where;
  		    }
  		}	
  		Vector v = multirel.searchMultiLevelVector(snodes,sfields,sdistinct,tables,where,orderVec,sdirection);
  		if (v!=null) {
  		    NodeManager tempNodeManager=null;
  		    for(Enumeration nodeEnum = v.elements(); nodeEnum.hasMoreElements(); ){
  		        MMObjectNode node = (MMObjectNode)nodeEnum.nextElement();
  		        if (tempNodeManager==null) {
  		            tempNodeManager = new TemporaryNodeManager(node,this);
  		        }
                retval.addElement(new BasicNode(node,tempNodeManager));
  		    }
		}
  		return retval;
    }

}
