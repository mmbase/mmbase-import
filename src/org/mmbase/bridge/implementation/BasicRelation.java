/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: BasicRelation.java,v 1.17.2.1 2002-02-20 10:43:37 michiel Exp $
 */
public class BasicRelation extends BasicNode implements Relation {
    private static Logger log = Logging.getLoggerInstance(BasicRelation.class.getName());

    private RelationManager relationManager = null;
    protected int snum = 0;
    protected int dnum = 0;

    private int snumtype = 0;
    private int dnumtype = 0;

    protected boolean relationChanged = false; // Indicates a change in snum or dnum


    /**
     * @javadoc
     */
    BasicRelation(MMObjectNode node, NodeManager nodeManager) {
        super(node,nodeManager);
        if (nodeManager instanceof RelationManager) {
            this.relationManager=relationManager;
        }
        snum = node.getIntValue("snumber");
        dnum = node.getIntValue("dnumber");

        snumtype = mmb.getTypeDef().getNodeType(snum);
        dnumtype = mmb.getTypeDef().getNodeType(dnum);
    }

    /**
     * @javadoc
     */
    BasicRelation(MMObjectNode node, NodeManager nodeManager, int id) {
        super(node,nodeManager,id);
        isnew=true;
        if (nodeManager instanceof RelationManager) {
            this.relationManager=relationManager;

        }
        temporaryNodeId=id;
    }


    public Node getSource() {
        // Note that this will not return an accurate value when the field is
        // edited during a transaction.
        return nodeManager.getCloud().getNode(snum);
    }

    public Node getDestination() {
        // Note that this will not return an accurate value when the field is
        // edited during a transaction.
        return nodeManager.getCloud().getNode(dnum);
    }

    public void setSource(Node node) {
        if (node.getCloud() != cloud) {
            String message;
            message = "Source and relation are not in the same transaction or "
                      + "from different clouds.";
            log.error(message);
            throw new BridgeException(message);
        }
        relationChanged = true;
        int source=node.getIntValue("number");
        if (source==-1) {
            // set a temporary field, transactionmanager resolves this
            getNode().setValue("_snumber",node.getValue("_number"));
        } else {
          getNode().setValue("snumber",source);
        }
        snum = node.getNumber();
        snumtype = node.getIntValue("otype");
    }

    public void setDestination(Node node) {
        if (node.getCloud() != cloud) {
            String message;
            message = "Destination and relation are not in the same "
                      + "transaction or from different clouds.";
            log.error(message);
            throw new BridgeException(message);
        }
        relationChanged = true;
        int dest=node.getIntValue("number");
        if (dest==-1) {
            // set a temporary field, transactionmanager resolves this
            getNode().setValue("_dnumber",node.getValue("_number"));
        } else {
          getNode().setValue("dnumber",dest);
        }
       dnum = node.getNumber();
       dnumtype = node.getIntValue("otype");
    }

    public RelationManager getRelationManager() {
        if (relationManager==null) {
            int stypenum=mmb.getTypeRel().getNodeType(snum);
            int dtypenum=mmb.getTypeRel().getNodeType(dnum);
            relationManager=cloud.getRelationManager(stypenum,dtypenum,
                                getNode().getIntValue("rnumber"));
        }
        return relationManager;
    }

    /**
     * 
     * @javadoc
     */
    void checkValid() {
        if (log.isDebugEnabled()) {
            log.debug("s : " + snum + " d: " + dnum);
        }
        //int snumber = snumtype.getNumber();
        //int dnumber = dnumtype.getNumber();
        int rnumber = getNode().getIntValue("rnumber");
        if (!mmb.getTypeRel().reldefCorrect(snumtype, dnumtype, rnumber)) {
            if (!mmb.getTypeRel().reldefCorrect(dnumtype,snumtype,rnumber)) {
                String message;
                message = "Source and/or Destination node are not of the "
                          + "correct type. ("
                          + cloud.getNode(snumtype).getValue("name") + ","
                          + cloud.getNode(dnumtype).getValue("name") + ","
                          + cloud.getNode(rnumber).getValue("sname") + ").";
                log.error(message);
                throw new BridgeException(message);
            }
        }

    }

    public void commit() {
        // Check types of source and destination
        // Normally, this check would be run in the core.
        // However, the current system mdoes not throw an exception when a wrong
        // node is created, we want to do this so for them moment
        // we perform this check here.
        // Note that we do not realign the node since InsRel does it for us,
        // but we DO update snum and dnum.
        //
        // XXX:This check should ultimately be removed, once we have an agreement
        // on throwing the exception in the core

        checkValid();
        if (! (BasicCloud.isTemporaryId(snum) || BasicCloud.isTemporaryId(dnum))) {
            if (isnew) {
                cloud.assert(Operation.CREATE, mmb.getTypeDef().getIntValue(getNodeManager().getName()), snum, dnum);
                relationChanged = false;            
            } else {
                if (relationChanged) {
                    cloud.assert(Operation.CHANGE_RELATION, mmb.getTypeDef().getIntValue(getNodeManager().getName()), snum, dnum);
                    relationChanged = false;
                }        
            }
        } 
        super.commit();
        if (!(cloud instanceof Transaction)) {
            snum = getNode().getIntValue("snumber");
            dnum = getNode().getIntValue("dnumber");
        }
    }

    /**
     * Compares two relations, and returns true if they are equal.
     * This effectively means that both objects are relations, and they both refer to the same objectnode
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof Relation) && (o.hashCode()==hashCode());
    }

    /**
     * Returns the object's hashCode.
     * This effectively returns th objectnode's number
     */
    public int hashCode() {
        return getNumber();
    }

}
