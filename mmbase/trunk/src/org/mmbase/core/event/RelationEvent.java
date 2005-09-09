/*
 * Created on 21-jun-2005
 *
 */
package org.mmbase.core.event;

import java.io.Serializable;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.InsRel;

/**
 * This class reflects a change relation event. it contains information
 * about the kind of event (new, delete, change), and it contains
 * a reference to the appropriate typerel node, which allows you to find
 * out on which relation from which builder to which builder, the event
 * occered. This is usefull for caching optimization.<br/>
 * A relation changed event is called the twoo nodes that the relation 
 * links (or used to).
 * 
 *  @author Ernst Bunders
 * @since MMBase-1.8
 */
public class RelationEvent extends NodeEvent implements Serializable{

	private String relationSourceNumber, relationDestinationNumber;
	private String relationSourceType, relationDestinationType;
	private String sourceRoleName, destinationRoleName;
	private int relationEventType;
	private int role;
	/**
	 * @param node the changed relation node
	 * @param eventType the type of change
	 * @throws IllegalArgumentException if the node is not of type InsRel
	 */
	public RelationEvent(MMObjectNode node, int eventType) {
		super(node, NodeEvent.EVENT_TYPE_RELATION_CHANGED);
		name = "relation event";
		if(!(node.parent instanceof InsRel))
			throw new IllegalArgumentException("you can not create a relation changed event with this node");
		
		relationEventType = eventType;
		eventType = NodeEvent.EVENT_TYPE_RELATION_CHANGED;
		MMObjectNode reldef = node.getNodeValue("rnumber");
		MMObjectBuilder relationSourceBuilder = node.getNodeValue("snumber").parent;
		MMObjectBuilder relationDestinationBuilder = node.getNodeValue("dnumber").parent;
		
		relationSourceNumber = node.getStringValue("snumber");
		relationDestinationNumber = node.getStringValue("dnumber");
		relationSourceType = relationSourceBuilder.getTableName();
		relationDestinationType = relationDestinationBuilder.getTableName();
		
		role=reldef.getNumber();
		sourceRoleName = reldef.getStringValue("sname");
		destinationRoleName = reldef.getStringValue("dname");
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
	public String getRelationSourceNumber() {
		return relationSourceNumber;
	}
	/**
	 * @return Returns the relationDestinationNumber.
	 */
	public String getRelationDestinationNumber() {
		return relationDestinationNumber;
	}
	public int getRelationEventType() {
		return relationEventType;
	}
	
	/**
	 *
	 * @return rolename of changed relation
	 * @throws UnsupportedOperationException if source and destination role names are
	 * not similar
	 */
	public String getRoleName() throws UnsupportedOperationException{
		if (! getDestinationRoleName().equals(getSourceRoleName()) ){
			throw new UnsupportedOperationException("the source and destination rolenames are not the same. Please specify");
		}
		return getDestinationRoleName();
	}
	
	/**
	 * @return the role number 
	 */
	public int getRole(){
		return role;
	}
	
	public String getDestinationRoleName(){
		return destinationRoleName;
	}
	
	public String getSourceRoleName(){
		return sourceRoleName;
	}
	
	 public String toString(){
		return super.toString() + ", relation eventtype: " + getEventName(relationEventType) +
			", sourcetype: " + relationSourceType + ", destinationtype: " +
			relationDestinationType + ", source-node number: " + relationSourceNumber +
			", destination-node number: " + relationDestinationNumber; 
	}
	
}
