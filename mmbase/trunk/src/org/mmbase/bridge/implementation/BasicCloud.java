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

    // node types
    private HashMap nodeTypes = new HashMap();


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
        for(Enumeration builders = cloudContext.mmb.mmobjs.elements(); builders.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)builders.nextElement();
            NodeType nodeType=new BasicNodeType(bul, this);
            nodeTypes.put(nodeType.getName(),nodeType);
        }
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
     * Retrieves all node types (aka builders) available in this cloud
     * @return an <code>List</code> containing all node types
     */
    public List getNodeTypes() {
       Vector v = new Vector(nodeTypes.values());
       return v;
    }

	/**
     * Retrieves a node type (aka builder)
     * @param nodeTypeName name of the node type to retrieve
     * @return the requested node type
     */
    public NodeType getNodeType(String nodeTypeName) {
        return (NodeType)nodeTypes.get(nodeTypeName);
    }

	/**
     * Retrieves a node type (aka builder)
     * @param nodeTypeID number of the node type to retrieve
     * @return the requested node type
     */
    public NodeType getNodeType(int nodeTypeID) {
        return (NodeType)nodeTypes.get(typedef.getValue(nodeTypeID));
    }

 	/**
     * Retrieves a relation type
     * @param sourceTypeName name of the type of the source node
     * @param destinationTypeName name of the type of the destination node
     * @param roleName name of the role
     * @return the requested node type
     */
    public RelationType getRelationType(String sourceTypeName, String destinationTypeName, String roleName) {
        int r=cloudContext.mmb.getRelDef().getGuessedNumber(roleName);
        int n1=typedef.getIntValue(sourceTypeName);
        int n2=typedef.getIntValue(destinationTypeName);
        Enumeration e =cloudContext.mmb.getTypeRel().search("WHERE snumber="+n1+" AND dnumber="+n2+" AND rnumber="+r);
        if (e.hasMoreElements()) {
            MMObjectNode node=(MMObjectNode)e.nextElement();
            RelationType reltype= new BasicRelationType(node,this);
            return reltype;
        } else {
            return null;
        }
    };

	/**
     * Creates a node of a specific type
     * @param nodeTypeName name of the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(String nodeTypeName){
        NodeType nodeType = getNodeType(nodeTypeName);
	    if (nodeType==null) {
	        return null;
	    } else {
            return nodeType.createNode();
        }
    }

	/**
     * Creates a node of a specific type
     * @param nodeTypeID number of the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(int nodeTypeID) {
        NodeType nodeType = getNodeType(nodeTypeID);
	    if (nodeType==null) {
	        return null;
	    } else {
            return nodeType.createNode();
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
     * @param nodes The numbers of the nodes to start the search with. These have to be a member of the first node type
     *      listed in the nodetypes parameter. The syntax is a comma-seperated lists of node ids.
     *      Example : '112' or '1,2,14'
     * @param nodetypes The nodetype chain. The syntax is a comma-seperated lists of node type names.
     *      The search is formed by following the relations between successive nodetypes in the list. It is possible to explicitly supply
     *      a relation type by placing the name of the type between two nodetypes to search.
     *      Example: 'company,people' or 'typedef,authrel,people'.
     * @param fields The fieldnames to return (comma seperated). This can include the name of the nodetype in case of fieldnames that are used by more than one type (i.e number).
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with typeindication) as they are specified in this parameter.
     *      Examples: 'people.lastname', 'typedef.number,authrel.creat,people.number'
     * @param where The contraint. this is in essence a SQL where clause, using the type names from the typenodes as tablenames.
     *      Examples: "people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param order the fieldnames on which you want to sort. Identical in syntax to the fields parameter.
     * @param direction A list of values containing, for each field in the order parameter, a value inidcating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      The first value in the list is used for teh remainig fields. Default value is <code>'UP'</code>.
     *      Examples: 'UP,DOWN,DOWN'
     * @param distinct <code>True> indicates the records returned need to be distinct. <code>False</code> indicates double values can be returned.
     * @return a <code>List</code> of found nodes
     */
     public List search(String nodes, String nodeTypes, String fields, String where, String sorted, String direction, boolean distinct) {
  		StringTagger tagger= new StringTagger(
  		                    "NODES='"+nodes+"' TYPES='"+nodeTypes+"' FIELDS='"+fields+
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
  		    for(Enumeration nodeEnum = v.elements(); nodeEnum.hasMoreElements(); ){
  		        MMObjectNode node = (MMObjectNode)nodeEnum.nextElement();
                retval.addElement(new BasicNode(node,this));
  		    }
		}
  		return retval;
    }

}
