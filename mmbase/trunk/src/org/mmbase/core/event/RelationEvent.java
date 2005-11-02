/*
 * Created on 21-jun-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.Serializable;
import java.util.Map;

import org.mmbase.bridge.NotFoundException;
import org.mmbase.module.core.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.InsRel;

/**
 * This class reflects a change relation event. it contains information about
 * the kind of event (new, delete, change), and it contains a reference to the
 * appropriate typerel node, which allows you to find out on which relation from
 * which builder to which builder, the event occered. This is usefull for
 * caching optimization.<br/> A relation changed event is called the twoo nodes
 * that the relation links (or used to).
 * 
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id: RelationEvent.java,v 1.7 2005-11-02 19:15:39 ernst Exp $
 */
public class RelationEvent extends NodeEvent implements Serializable {

    private int relationSourceNumber, relationDestinationNumber;

    private String relationSourceType, relationDestinationType;

    private int relationEventType;

    private int role; //the reldef node number
  
    
    /**
     * Constructor for relation event 
     * @param machineName
     * @param builderName
     * @param nodeNumber
     * @param oldValues
     * @param newValues
     * @param relationEventType
     * @param relationSourceNumber the nodenumber of the 'soucre' node
     * @param relationDestinationNumber the nodenumber of the 'destination' node
     * @param relationSourceType the builder name of the 'source' node
     * @param relationDestinationType the builder name of the 'destination' node
     * @param role the nodenumber of the reldef node
     */
    public RelationEvent(String machineName, String builderName, int nodeNumber, Map oldValues, Map newValues, int relationEventType,
        int relationSourceNumber, int relationDestinationNumber, String relationSourceType, String relationDestinationType, int role) {
        super(machineName, builderName, nodeNumber, oldValues, newValues, NodeEvent.EVENT_TYPE_RELATION_CHANGED);
        
        this.relationSourceNumber = relationSourceNumber;
        this.relationDestinationNumber = relationDestinationNumber;
        this.relationSourceType = relationSourceType;
        this.relationDestinationType = relationDestinationType;
        this.role = role;
    }

    public String getName() {
        return "relation event";
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

    public int getRelationEventType() {
        return relationEventType;
    }


    /**
     * @return the role number
     */
    public int getRole() {
        return role;
    }

  

   

    public String toString() {
        return super.toString() + ", relation eventtype: "
            + getEventTypeGuiName(relationEventType) + ", sourcetype: "
            + relationSourceType + ", destinationtype: "
            + relationDestinationType + ", source-node number: "
            + relationSourceNumber + ", destination-node number: "
            + relationDestinationNumber;
    }

}
