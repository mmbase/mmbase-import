/*
 * Created on 21-jun-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.*;
import java.util.*;

import org.mmbase.util.HashCodeUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This class communicates a node event. in case of a change event, it contains
 * a map of changed values, mapped to their field's name, as well as the
 * previous values of the changed fields.
 *
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id: NodeEvent.java,v 1.24 2006-01-24 17:56:48 michiel Exp $
 */
public class NodeEvent extends Event implements Serializable, Cloneable {

  
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logging.getLoggerInstance(NodeEvent.class);

    public static final int EVENT_TYPE_NEW = 0;

    public static final int EVENT_TYPE_CHANGED = 1;

    public static final int EVENT_TYPE_DELETE = 2;

    public static final int EVENT_TYPE_RELATION_CHANGED = 3;


    private final int eventType;    
    private final int nodeNumber;    
    private String builderName;
    
    private final Map oldValues;
    private final Map newValues;

    
    /**
    *@param machineName (MMBase) name of the server
    *@param builderName name of builder of node event is about
    *@param oldValues map with fields and their values that have been changed by the event
    *@param newValues map with new values of changed fields
    *@param eventType the type of event
    **/
    public NodeEvent(String machineName, String builderName, int nodeNumber, Map oldValues, Map newValues, int eventType ){
        super(machineName);
        this.builderName = builderName;
        this.nodeNumber = nodeNumber;
        this.eventType = eventType;
        this.oldValues = oldValues == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(new HashMap(oldValues));
        this.newValues = newValues == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(new HashMap(newValues));
    }
    
    public String getName() {
        return "node event";
    }


    /**
     * @param fieldName the field you want to get the old value of
     * @return an Object containing the old value (in case of change event), or
     *         null if the fieldName was not found in the old value list
     */
    public final Object getOldValue(String fieldName) {
        return oldValues.get(fieldName);
    }
    
    /**
     * @return a set containing the names of the fields that have changed
     */
    public final Set getChangedFields() {
        switch(getType()) {
        case EVENT_TYPE_NEW:
            return newValues.keySet();
        case EVENT_TYPE_CHANGED:
            //for changed both old and new values are good (similar keys)
            return newValues.keySet();
        case  EVENT_TYPE_DELETE:
            return oldValues.keySet();
        default: 
            return Collections.EMPTY_SET;
        }
    }

    /**
     * @param fieldName the field you want the new value of (in case of change
     *        event), or null if the fieldName was not found in the new value
     *        list
     * @return the new value of the field
     */
    public final  Object getNewValue(String fieldName) {
        return newValues.get(fieldName);
    }

    public final int getType() {
        return eventType;
    }


    /**
     * @return Returns the builderName.
     */
    public final String getBuilderName() {
        return builderName;
    }
    /**
     * @return Returns the nodeNumber.
     */
    public final int getNodeNumber() {
        return nodeNumber;
    }


    public String toString() {
        String changedFields = "";
        for (Iterator i = getChangedFields().iterator(); i.hasNext();) {
            changedFields = changedFields + (String) i.next() + ",";
        }
        return getName() + " : '" + getEventTypeGuiName(eventType) + "', node: " + nodeNumber + ", nodetype: " + builderName + ", oldValues: " + oldValues + ", newValues: " + newValues + "changedFields: " + getChangedFields();
    }

    protected static String getEventTypeGuiName(int eventType) {
        switch (eventType) {
        case NodeEvent.EVENT_TYPE_CHANGED:
            return "node changed";
        case NodeEvent.EVENT_TYPE_DELETE:
            return "node deleted";
        case NodeEvent.EVENT_TYPE_NEW:
            return "new node";
        case NodeEvent.EVENT_TYPE_RELATION_CHANGED:
            return "relation changed";
        default:
            throw new IllegalArgumentException("HELP! event of type " + eventType + " is unknown. This should not happen");
        }
    }




    public NodeEvent clone(String builderName) {
        NodeEvent clone = (NodeEvent) super.clone();
        clone.builderName = builderName;
        return clone;
    }

    /**
     * For conveneance: conversion of the new event type indication to the old
     * style
     *
     * @param eventType must be c,d,n or r
     * @return A String describing the type of an event. (like "c" (change), "d" (delete), "n" (new), or "r" (relation change))
     */
    public static String newTypeToOldType(int eventType) {
        switch (eventType) {
        case NodeEvent.EVENT_TYPE_CHANGED:          return "c";
        case NodeEvent.EVENT_TYPE_DELETE:           return "d";
        case NodeEvent.EVENT_TYPE_NEW:              return "n";
        case NodeEvent.EVENT_TYPE_RELATION_CHANGED: return "r";
        default: throw new IllegalArgumentException("HELP! event of type " + eventType + " is unknown. This should not happen");
        }
    }

    /**
     * For conveneance: conversion of the old event type indication to the new
     * style
     *
     * @param eventType
     */
    public static int oldTypeToNewType(String eventType) {
        if (eventType.length() > 1) {
            throw new IllegalArgumentException("HELP! event of type " + eventType + " is unknown. This should not happen");
        }
        switch(eventType.charAt(0)) {
        case 'c': return NodeEvent.EVENT_TYPE_CHANGED;
        case 'd': return NodeEvent.EVENT_TYPE_DELETE;
        case 'n': return NodeEvent.EVENT_TYPE_NEW;
        case 'r': return NodeEvent.EVENT_TYPE_RELATION_CHANGED;
        default: throw new IllegalArgumentException("HELP! event of type " + eventType + " is unknown. This should not happen");
        }
    }

    /**
     * utility method: check if a certain field has changed
     * @param fieldName
     * @return true if the field of given name is among the changed fields 
     */
    public boolean hasChanged(String fieldName){
        return oldValues.keySet().contains(fieldName) || newValues.keySet().contains(fieldName);
    }
    

    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, eventType);
        result = HashCodeUtil.hashCode(result, nodeNumber);
        result = HashCodeUtil.hashCode(result, builderName);
        return result;
        
    }
    public boolean equals(Object o) {
        if (o instanceof NodeEvent) {
            NodeEvent ne = (NodeEvent) o;
            return eventType == ne.eventType && nodeNumber == ne.nodeNumber && builderName.equals(ne.builderName);
        } else {
            return false;
        }
    }
    
    /**
     * old values can be different things.
     * <ul>
     * <li>if the event type is 'new' this collection is empty.
     * <li>if the event type is 'changed' this collection contains the old values of the changed fields.
     * <li>if the event type is 'delete' this collection contains all the values of the node to be deleted.
     * </ul>
     * @return a map where the key is a fieldname and the value the field's value
     */
    public final Map getOldValues(){
        return oldValues;
    }
    
    /**
     * new values can be different things.
     * <ul>
     * <li>if the event type is 'new' this collection contains all the fields of the node.
     * <li>if the event type is 'changed' this collection contains the new values of the changed fields.
     * <li>if the event type is 'delete' this collection is empty.
     * </ul>
     * @return a map where the key is a fieldname and the value the field's value
     */
    public final Map getNewValues(){
        return newValues;
    }

    
    public static void main(String[] args) {
        //test serializable
        Map  oldv = new HashMap(), newv = new HashMap();
        oldv.put("een","veen");
        oldv.put("twee","vtwee");
        newv.putAll(oldv);
        
        NodeEvent event = new NodeEvent(  "local", "builder", 0, oldv, newv, NodeEvent.EVENT_TYPE_CHANGED);
        System.out.println("event 1: " + event.toString());
        NodeEvent event2 = (NodeEvent) event.clone("otherbuilder");
        System.out.println("clone: " + event2.toString());
        System.out.println("event 1: " + event.toString());
        
    }

}
