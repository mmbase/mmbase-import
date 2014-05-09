/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.security.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicRelation extends BasicNode implements Relation {
    private static final Logger log = Logging.getLoggerInstance(BasicRelation.class);

    private RelationManager relationManager = null;
    protected int snum;
    protected int dnum;

    private static final int UNSET = -999;

    private int sourceNodeType      = UNSET;
    private int destinationNodeType = UNSET;

    protected boolean relationChanged = false; // Indicates a change in snum or dnum

    /**
     * @javadoc
     */
    BasicRelation(MMObjectNode node, BasicCloud cloud) {
        super(node, cloud);
    }

    /**
     * @javadoc
     */
    BasicRelation(MMObjectNode node, BasicRelationManager nodeManager) {
        super(node, nodeManager);
    }

    /**
     * @javadoc
     */
    BasicRelation(MMObjectNode node, BasicCloud cloud, int id) {
        super(node, cloud, id);
    }

    public final boolean isRelation() {
        return true;
    }
    public Relation toRelation() {
        return this;
    }

    /**
     * Initializes the node.
     * Determines nodemanager and cloud (depending on information available),
     * Sets references to MMBase modules and initializes state in case of a transaction.
     */
    protected void init() {
        super.init();
        if (nodeManager instanceof RelationManager) {
            relationManager = (RelationManager)nodeManager;
        }
        snum = getIntValue("snumber");
        dnum = getIntValue("dnumber");

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
            throw new BridgeException("Source and relation are not in the same transaction or from different clouds.");
        }
        edit(ACTION_EDIT);
        relationChanged = true;
        int source = node.getIntValue("number");
        if (source == -1) {
            // set a temporary field, transactionmanager resolves this
            getNode().setValue("_snumber", node.getValue(MMObjectBuilder.TMP_FIELD_NUMBER));
        } else {
            getNode().setValue("snumber", source);
        }
        snum = node.getNumber();
        sourceNodeType = node.getIntValue("otype");
    }

    public void setDestination(Node node) {
        if (node.getCloud() != cloud) {
            throw new BridgeException("Destination and relation are not in the same transaction or from different clouds.");
        }
        edit(ACTION_EDIT);
        relationChanged = true;
        int dest = node.getIntValue("number");
        if (dest == -1) {
            // set a temporary field, transactionmanager resolves this
            getNode().setValue("_dnumber", node.getValue(MMObjectBuilder.TMP_FIELD_NUMBER));
        } else {
          getNode().setValue("dnumber",dest);
        }
       dnum = node.getNumber();
       destinationNodeType = node.getIntValue("otype");
    }

    public RelationManager getRelationManager() {
        if (relationManager == null) {
            int stypenum = BasicCloudContext.mmb.getTypeRel().getNodeType(snum);
            int dtypenum = BasicCloudContext.mmb.getTypeRel().getNodeType(dnum);
            if (log.isDebugEnabled()) {
                log.debug(stypenum + ", " + dtypenum + ", " + getNode().getIntValue("rnumber"));
            }

            relationManager=cloud.getRelationManager(stypenum,dtypenum, getNode().getIntValue("rnumber"));
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

        if (sourceNodeType == UNSET) {
            sourceNodeType = -1;
            if (snum != -1) sourceNodeType = BasicCloudContext.mmb.getTypeDef().getNodeType(snum);
        }
        if (destinationNodeType == UNSET) {
            destinationNodeType = -1;
            if (dnum!=-1) destinationNodeType = BasicCloudContext.mmb.getTypeDef().getNodeType(dnum);
        }

        int rnumber = getNode().getIntValue("rnumber");
        if (!BasicCloudContext.mmb.getTypeRel().contains(sourceNodeType, destinationNodeType, rnumber)) {
            if (!BasicCloudContext.mmb.getTypeRel().contains(destinationNodeType, sourceNodeType, rnumber)) {
                if (! cloud.hasNode(sourceNodeType)) {
                    throw new BridgeException("Source type of relation " + this + ": " + sourceNodeType + " does not point to a valid node.");
                }
                if (! cloud.hasNode(destinationNodeType)) {
                    throw new BridgeException("Destination type of relation " + this + ": " + destinationNodeType + " does not point to a valid node.");
                }
                if (! cloud.hasNode(rnumber)) {
                    throw new BridgeException("Rnumber of relation " + this + ": " + rnumber + " does not point to a valid node.");
                }
                throw new BridgeException("Source and/or Destination node are not of the correct type, or relation not allowed ("
                                          + cloud.getNode(sourceNodeType).getValue("name") + ","
                                          + cloud.getNode(destinationNodeType).getValue("name") + ","
                                          + cloud.getNode(rnumber).getValue("sname") + ")");
            }
        }

    }

    public void setValueWithoutProcess(String fieldName, Object value) {
        edit(ACTION_EDIT);
        if ("rnumber".equals(fieldName)) {
            throw new BridgeException("Not allowed to change field '" + fieldName + "'.");
        } else if ("snumber".equals(fieldName) || "dnumber".equals(fieldName)) {
            relationChanged = true;
        }
        super.setValueWithoutProcess(fieldName, value);
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
            if (isNew()) {
                cloud.verify(Operation.CREATE, BasicCloudContext.mmb.getTypeDef().getIntValue(getNodeManager().getName()), snum, dnum);
                relationChanged = false;
            } else {
                if (relationChanged) {
                    cloud.verify(Operation.CHANGE_RELATION, noderef.getNumber(), snum, dnum);
                    relationChanged = false;
                }
            }
        }
        log.debug("Committing " + getChanged());
        super.commit();
        if (!(cloud instanceof Transaction)) {
            snum = getNode().getIntValue("snumber");
            dnum = getNode().getIntValue("dnumber");
        }
    }

}
