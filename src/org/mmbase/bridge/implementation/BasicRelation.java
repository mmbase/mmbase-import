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


  	BasicRelation(MMObjectNode node, Cloud cloud) {
  	    super(node,cloud);
  	    snum=node.getIntValue("snumber");
  	    int stypenum=mmb.getTypeRel().getNodeType(snum);
  	    dnum=node.getIntValue("dnumber");
  	    int dtypenum=mmb.getTypeRel().getNodeType(dnum);
  	    rnum=node.getIntValue("rnumber");
  	    Enumeration e = mmb.getTypeRel().search("WHERE ((snumber="+stypenum+" AND dnumber="+dtypenum+
  	                                                  ") OR (dnumber="+stypenum+" AND snumber="+dtypenum+
  	                                                  ")) AND rnumber="+rnum);
  	    if (e.hasMoreElements()) {
  	        relationManager=new BasicRelationManager((MMObjectNode)e.nextElement(), cloud);
  	    } else {
  	        relationManager=null;
  	    }
  	}

  	BasicRelation(MMObjectNode node, RelationManager relationManager) {
        super(node,relationManager.getCloud());
        isnew=true;
   	    this.relationManager=relationManager;
  	}

	/**
	 * Retrieves the source of the relation
	 * @return the source node
	 */
	public Node getSource() {
	    return nodeManager.getCloud().getNode(snum);
	}

	/**
	 * Retrieves the destination of the relation
	 * @return the destination node
	 */
	public Node getDestination() {
	    return nodeManager.getCloud().getNode(dnum);
	}

	/** 
	 * set the source of the relation
	 * @param the source node
	 */
	public void setSource(Node node) {
        Edit(ACTION_EDIT);
	    int source=node.getNodeID();
	    this.node.setValue("snumber",source);
	    snum=source;
	}

	/**
	 * set the destination of the relation
	 * @param the destination node
	 */
	public void setDestination(Node node) {
        Edit(ACTION_EDIT);
	    int dest=node.getNodeID();
	    this.node.setValue("dnumber",dest);
	    dnum=dest;
	}

 	/**
     * Retrieves the RelationManager used
     * @return the RelationManager
     */
    public RelationManager getRelationManager() {
        return relationManager;
    }
}

