/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This interface represents a relation constraint (or contex, if you like).
 * More specifically, it represents a relation type (itself a node type) as it applies between two
 * other node types.
 * Some of the information here is retrieved from the node type used to build the catual relation node
 * (the data as described in the xml builder config file). This node type is also referred to as the parent type.
 * Other data is retrieved from a special (hidden) object that decsribes what relations apply between two nodes.
 * (formerly known as the TypeRel builder).
 * This includes direction and cardinality, and the type of nodes itself. These fields are the only ones that can be changed
 * (node type data cannot be changed except through the use of an administration module).
 * This interface is therefor not a real mmbase 'object' in itself - it exists of two objects joined together.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicRelationType extends BasicNodeType implements RelationType {
	
    private MMObjectNode typeRelNode = null;
    private MMObjectNode relDefNode = null;
    private int snum = 0;
    private int dnum = 0;
    int roleID  = 0;
  	
  	BasicRelationType(MMObjectNode node, Cloud cloud) {
  	    typeRelNode = node;
  	    snum=node.getIntValue("snumber");
  	    dnum=node.getIntValue("dnumber");
  	    roleID=node.getIntValue("rnumber");
  	    RelDef reldef = ((BasicCloudContext)cloud.getCloudContext()).mmb.getRelDef();
  	    relDefNode= reldef.getNode(roleID);
  	    builder=reldef.getBuilder(relDefNode);
  	    init(builder,cloud);
  	}

    /**
     * Creates a new initialized relation node
     * @return a node of type <code>Relation</code>
     */
    public Node createNode() {
        MMObjectNode node= builder.getNewNode("system");
        if (node==null) {
	        return null;
	    } else {
            return new BasicRelation(node, (RelationType)this);
	    }
    }
	
	/**
	 * Retrieves the role of the source to the destination
	 * @return the role as a <code>String</code>
	 */
	public String getForwardRole() {
	    return relDefNode.getStringValue("sname");
	}

	/**
	 * Retrieves the role of the destination to the source
	 * @return the role as a <code>String</code>
	 */
	public String getReciprocalRole() {
	    return relDefNode.getStringValue("dname");
	}

 	/**
     * Retrieves the directionality for this type (the default assigned to a new relation).
     * @return one of the directionality constants
     */
    public int getDirectionality() {
	    return relDefNode.getIntValue("dir");
	}

    /**
     * Retrieves the type of node that can act as the source of a relation of this type.
     * @return the source type
     */
    public NodeType getSourceType() {
	    int nr=typeRelNode.getIntValue("snumber");
	    return cloud.getNodeType(nr);
	}

	/**
     * Retrieves the type of node that can act as the destination of a relation of this type.
     * @return the destination type
     */
    public NodeType getDestinationType() {
	    int nr=typeRelNode.getIntValue("dnumber");
	    return cloud.getNodeType(nr);
	}
	
	/**
     * Adds a relation from this type
     * @param sourceNode the node from which you want to relate
     * @param destinationNode the node to which you want to relate
	 * @return the added relation
     */
    public Relation addRelation(Node sourceNode, Node destinationNode) {
        // check on insert : cannot craete relation is not committed
	   Relation relation = (Relation)createNode();
       relation.setSource(sourceNode);
       relation.setDestination(destinationNode);
       relation.setIntValue("rnumber",roleID);
       relation.commit();
       return relation;
    };
	
}
