/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.*;

/**
 * A relation within the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicRelation extends BasicNode implements Relation {

    private RelationManager relationManager = null;
    private int snum = 0;
    private int dnum = 0;
    private int rnum = 0;


  	BasicRelation(MMObjectNode node, NodeManager nodeManager) {
  	    super(node,nodeManager);
  	    snum=node.getIntValue("snumber");
  	    dnum=node.getIntValue("dnumber");
  	    rnum=node.getIntValue("rnumber");
  	}

  	BasicRelation(MMObjectNode node, RelationManager relationManager, int id) {
        super(node,relationManager,id);
        isnew=true;
   	    this.relationManager=relationManager;
  	    temporaryNodeId=id;
  	}

	/**
	 * Retrieves the source of the relation
	 * Note that this will not return an accurate value when the field is edited during a transaction.
	 * @return the source node
	 */
	public Node getSource() {
	    return nodeManager.getCloud().getNode(snum);
	}

	/**
	 * Retrieves the destination of the relation.
	 * Note that this will not return an accurate value when the field is edited during a transaction.
	 * @return the destination node
	 */
	public Node getDestination() {
	    return nodeManager.getCloud().getNode(dnum);
	}

	/** 
	 * Set the source of the relation.
	 * @param the source node
	 */
	public void setSource(Node node) {
        if (node.getCloud() != cloud) {
            throw new BasicBridgeException("Source and relation are not in the same transaction or from different clouds");
        }
        Edit(ACTION_EDIT);
        ((BasicNode)node).Edit(ACTION_LINK);
	    int source=node.getNumber();
        if (source==-1) {
            // set a temporary field, transactionmanager resolves this
            getNode().setValue("_snumber",node.getValue("_number"));
        } else {
    	    getNode().setValue("snumber",source);
        }
	    snum=node.getNumber();
	}

	/**
	 * Set the destination of the relation
	 * @param the destination node
	 */
	public void setDestination(Node node) {
        if (node.getCloud() != cloud) {
            throw new BasicBridgeException("Destination and relation are not in the same transaction or from different clouds");
        }
        Edit(ACTION_EDIT);
        ((BasicNode)node).Edit(ACTION_LINK);
	    int dest=node.getNumber();
        if (dest==-1) {
            // set a temporary field, transactionmanager resolves this
            getNode().setValue("_dnumber",node.getValue("_number"));
        } else {
    	    getNode().setValue("dnumber",dest);
        }
	    dnum=node.getNumber();
	}

 	/**
     * Retrieves the RelationManager used.
     * @return the RelationManager
     */
    public RelationManager getRelationManager() {
        if (relationManager==null) {
  	        int stypenum=mmb.getTypeRel().getNodeType(snum);
      	    int dtypenum=mmb.getTypeRel().getNodeType(dnum);
      	    relationManager=cloud.getRelationManager(stypenum,dtypenum,rnum);
        }
        return relationManager;
    }

    /**
    * Compares two relations, and returns true if they are equal.
    * This effectively means that both objects are relations, and they both refer to the same objectnode
    * @param o the object to compare it with
    */
    public boolean equals(Object o) {
        return (o instanceof Relation) && (o.hashCode()==hashCode());
    };

    /**
    * Returns the object's hashCode.
    * This effectively returns th objectnode's number
    */
    public int hashCode() {
        return getNumber();
    };
}

