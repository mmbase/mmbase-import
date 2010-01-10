/*
 * Created on 21-jun-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.Serializable;
import java.util.*;
import org.mmbase.util.HashCodeUtil;
import org.mmbase.module.core.MMBase;

/**
 * This class reflects a ,,change relation event. it contains information about the kind of event (new, delete, change),
 * and it contains a reference to the appropriate typerel node, which allows you to find out on which relation from
 * which builder to which builder, the event occered. This is usefull for caching optimization.<br/> A relation changed
 * event is called the twoo nodes that the relation links (or used to).
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public class RelationEvent extends Event implements Serializable, Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final int relationSourceNumber, relationDestinationNumber;
    // these are not final becouse they can be reset by MMObjectBuilder.notify()
    private String relationSourceType;
    private String relationDestinationType;

    private final int role; // the reldef node number

    private NodeEvent nodeEvent;

    /**
     * Constructor for relation event
     * 
     * @param nodeEvent
     * @param relationSourceNumber the nodenumber of the 'soucre' node
     * @param relationDestinationNumber the nodenumber of the 'destination' node
     * @param relationSourceType the builder name of the 'source' node
     * @param relationDestinationType the builder name of the 'destination' node
     * @param role the nodenumber of the reldef node
     */
    public RelationEvent(NodeEvent nodeEvent, int relationSourceNumber, int relationDestinationNumber,
            String relationSourceType, String relationDestinationType, int role) {
        super(nodeEvent.getMachine(), nodeEvent.getType());
        this.nodeEvent = nodeEvent;
        this.relationSourceNumber = relationSourceNumber;
        this.relationDestinationNumber = relationDestinationNumber;
        this.relationSourceType = relationSourceType;
        this.relationDestinationType = relationDestinationType;
        this.role = role;
    }


    /**
     * @return Returns the relationSourceType.
     */
    public String getRelationSourceType() {
        return relationSourceType;
    }


    /**
     * @return Returns the relationDestinationType.
     */
    public String getRelationDestinationType() {
        return relationDestinationType;
    }

    /**
     * @return Returns the relationSourceNumber.
     */
    public int getRelationSourceNumber() {
        return relationSourceNumber;
    }

    /**
     * @return Returns the relationDestinationNumber.
     */
    public int getRelationDestinationNumber() {
        return relationDestinationNumber;
    }

    public int getType() {
        return eventType;
    }

    /**
     * @return the role number
     */
    public int getRole() {
        return role;
    }

    /**
     * I think this method is not needed.
     * @deprecated
     */
    /*
    public RelationEvent clone(String sourceType, String destType) {
        RelationEvent clone = (RelationEvent) super.clone();
        clone.nodeEvent = (NodeEvent) nodeEvent.clone();
        clone.relationSourceType = sourceType;
        clone.relationDestinationType = destType;
        return clone;
    }
    */

    public int hashCode() {
        int result = nodeEvent.hashCode();
        result = HashCodeUtil.hashCode(result, relationSourceNumber);
        result = HashCodeUtil.hashCode(result, relationDestinationNumber);
        result = HashCodeUtil.hashCode(result, relationSourceType);
        result = HashCodeUtil.hashCode(result, relationDestinationType);
        result = HashCodeUtil.hashCode(result, role);
        result = HashCodeUtil.hashCode(result, eventType);
        return result;
    }

    public boolean equals(Object o) {
        if (o instanceof RelationEvent) {
            RelationEvent re = (RelationEvent) o;
            return 
                nodeEvent.equals(re.nodeEvent) && 
                eventType == re.eventType && 
                role == re.role && 
                relationSourceType.equals(re.relationSourceType) && 
                relationDestinationType.equals(re.relationDestinationType) && 
                relationSourceNumber == re.relationSourceNumber && 
                relationDestinationNumber == re.relationDestinationNumber;
        } else {
            return false;
        }
    }


    public NodeEvent getNodeEvent() {
        return nodeEvent;
    }

    public String toString() {
        return "Relation event. type: " + NodeEvent.getEventTypeGuiName(getType())
                + ", sourcetype: " + relationSourceType + ", destinationtype: " + relationDestinationType
                + ", source-node number: " + relationSourceNumber + ", destination-node number: "
                + relationDestinationNumber + ", role: " + role + " node event: " + nodeEvent;
    }



}
