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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This class communicates a node event. in case of a change event, it contains
 * a map of changed values, mapped to their field's name, as well as the
 * previous values of the changed fields.
 *
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id: NodeEvent.java,v 1.22 2005-12-23 16:13:49 ernst Exp $
 */
public class NodeEvent extends Event implements Serializable, Cloneable {

  
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logging.getLoggerInstance(NodeEvent.class);

    public static final int EVENT_TYPE_NEW = 0;

    public static final int EVENT_TYPE_CHANGED = 1;

    public static final int EVENT_TYPE_DELETE = 2;

    public static final int EVENT_TYPE_RELATION_CHANGED = 3;


    private int eventType;
    
    private int nodeNumber;
    
    private String builderName;
    
    private Map oldValues = new HashMap();
    private Map newValues = new HashMap();

    // implementation of serializable
    /*
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (node.getNumber() < 0) throw new IOException("Cannot serialize " + node);
        out.writeUTF(node.getBuilder().getTableName());
        out.writeInt(node.getNumber());
        out.writeInt(eventType);
        out.writeUTF(machine);
        out.writeObject(oldValues);
        out.writeObject(newValues);
    }
    // implementation of serializable
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String builderName = in.readUTF();
        MMObjectBuilder builder = MMBase.getMMBase().getBuilder(builderName);
        int nodeNumber = in.readInt();
        eventType = in.readInt();
        machine = in.readUTF();
        oldValues = (Map) in.readObject();
        newValues = (Map) in.readObject();
        node = builder.getNode(nodeNumber);
        if (node == null) {
            // probably the node was deleted. Happily, we know more or less enough to reconstruct it.            
            node = new MMObjectNode(builder, false);            
            Iterator it = oldValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                node.storeValue((String) entry.getKey(), entry.getValue());
            }
        }
    }
    */

    /*
    public NodeEvent(MMObjectNode node, int eventType) {
        this(node, eventType, MMBase.getMMBase().getMachineName());
    }

    public NodeEvent(MMObjectNode node, int eventType, String machine) {
        super(machine);
        this.node = node;
        this.eventType = eventType;

        // at this point the new value for the changed fields is
        // in the node, and the old values are in the oldValues map
        oldValues.putAll(node.getOldValues());
        newValues.putAll(node.getValues());
    }
    */
    
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
        if(oldValues != null)
            this.oldValues.putAll(oldValues);
        if(newValues != null)
            this.newValues.putAll(newValues);
    }
    
    public String getName() {
        return "node event";
    }


    /**
     * @param fieldName the field you want to get the old value of
     * @return an Object containing the old value (in case of change event), or
     *         null if the fieldName was not found in the old value list
     */
    public Object getOldValue(String fieldName) {
        return oldValues.get(fieldName);
    }
    
    /**
     * @return a set containing the names of the fields that have changed
     */
    public Set getChangedFields(){
        if(getType() ==  EVENT_TYPE_NEW){
            return Collections.unmodifiableSet(newValues.keySet());
        }else if(getType() == EVENT_TYPE_CHANGED){
            //for changed both old and new values are good (similar keys)
            return Collections.unmodifiableSet(newValues.keySet());
        }else if(getType() == EVENT_TYPE_DELETE){
            return Collections.unmodifiableSet(oldValues.keySet());
        }else{
            return new HashSet();
        }
    }

    /**
     * @param fieldName the field you want the new value of (in case of change
     *        event), or null if the fieldName was not found in the new value
     *        list
     * @return the new value of the field
     */
    public Object getNewValue(String fieldName) {
        return newValues.get(fieldName);
    }

    public int getType() {
        return eventType;
    }


    /**
     * @return Returns the builderName.
     */
    public String getBuilderName() {
        return builderName;
    }

    /**
     * @param builderName The builderName to set.
     */
    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    /**
     * @return Returns the nodeNumber.
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * @param nodeNumber The nodeNumber to set.
     */
    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
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
        if (eventType.equals("c")) {
            return NodeEvent.EVENT_TYPE_CHANGED;
        } else if (eventType.equals("d")) {
            return NodeEvent.EVENT_TYPE_DELETE;
        } else if (eventType.equals("n")) {
            return NodeEvent.EVENT_TYPE_NEW;
        } else if (eventType.equals("r")) {
            return NodeEvent.EVENT_TYPE_RELATION_CHANGED;
        } else {
            throw new IllegalArgumentException("HELP! event of type " + eventType + " is unknown. This should not happen");
        }
    }

    /**
     * utility method: check if a certain field has changed
     * @param fieldName
     * @return true if the field of given name is among the changed fields 
     */
    public boolean hasChanged(String fieldName){
        if(oldValues.keySet().contains(fieldName) || newValues.keySet().contains(fieldName))return true;
        return false;
    }
    
    public Object clone(){
        Object clone = null;
        clone = super.clone();
        //  deep clone the fields that can be changed
        builderName = new String(builderName);
        return clone;
    }
    
    public static void main(String[] args) {
        //test serializable
        Map  oldv = new HashMap(), newv = new HashMap();
        oldv.put("een","veen");
        oldv.put("twee","vtwee");
        newv.putAll(oldv);
        
        NodeEvent event = new NodeEvent(  "local", "builder", 0, oldv, newv, NodeEvent.EVENT_TYPE_CHANGED);
        System.out.println("event 1: " + event.toString());
        NodeEvent event2 = (NodeEvent) event.clone();
        event2.setBuilderName("otherbuilder");
        System.out.println("clone: " + event2.toString());
        System.out.println("event 1: " + event.toString());
        
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
    public Map getOldValues(){
        return Collections.unmodifiableMap(oldValues);
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
    public Map getNewValues(){
        return Collections.unmodifiableMap(newValues);
    }
}
