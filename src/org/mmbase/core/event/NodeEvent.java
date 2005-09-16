/*
 * Created on 21-jun-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;

/**
 * This class communicates a node event. in case of a change event, it contains
 * a map of changed values, mapped to their field's name, as well as the
 * preveaus values of the changed fields.
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public class NodeEvent extends Event implements Serializable {

    public static final int EVENT_TYPE_NEW = 0;

    public static final int EVENT_TYPE_CHANGED = 1;

    public static final int EVENT_TYPE_DELETE = 2;

    public static final int EVENT_TYPE_RELATION_CHANGED = 3;

    private String nodeNumber;

    private String eventSource;

    private int eventType;

    private String machine;

    // ernst:what object types can the 'old values' be, and what happens if they
    // can't be serialized??
    private Map oldValues = null, newValues = null;

    // private static final String noFieldsMessage = "an event of this type has
    // no changed fields";

    /**
     * @param nodeNumber
     * @param eventType
     * @param source
     * @param oldValues
     */

    public NodeEvent(MMObjectNode node, int eventType) {
        init(node, eventType, MMBase.getMMBase().getMachineName());
    }

    public NodeEvent(MMObjectNode node, int eventType, String machine) {
        init(node, eventType, machine);
    }

    /**
     * @param node
     * @param eventType
     */
    private void init(MMObjectNode node, int eventType, String machine) {
        this.nodeNumber = node.getStringValue("number");
        this.eventSource = node.parent.getTableName();
        this.eventType = eventType;
        this.machine = machine;
        name = "node event";

        // at this point the new value for the changed fields is
        // in the node, and the old values are in the oldValues map
        oldValues = new HashMap(10);
        newValues = new HashMap(10);
        for (Iterator i = node.oldValues.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            oldValues.put(key, node.oldValues.get(key));
            newValues.put(key, node.getValue(key));
        }
    }

    /**
     * Adds the name and old value of a changed field, But only if this event
     * type supports changed fields (i.e. node changed, relation changed)
     * 
     * @param fieldName
     * @param oldValue
     */
    public void addChangedField(String fieldName, Object oldValue) {
        if (canHaveChangedFields()) {
            oldValues.put(fieldName, oldValue);
        }
    }

    /**
     * @return an iterator of the names of the changed fields.
     */
    public Iterator changedFieldIterator() {
        return oldValues.keySet().iterator();
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
     * @param fieldName the field you want the new value of (in case of change
     *        event), or null if the fieldName was not found in the new value
     *        list
     * @return
     */
    public Object getNewValue(String fieldName) {
        return newValues.get(fieldName);
    }

    public int getType() {
        return eventType;
    }

    public String getBuilderName() {
        return eventSource;
    }

    protected boolean canHaveChangedFields() {
        return eventType == NodeEvent.EVENT_TYPE_CHANGED
            || eventType == NodeEvent.EVENT_TYPE_RELATION_CHANGED;
    }

    public String toString() {
        String changedFields = "";
        for (Iterator i = changedFieldIterator(); i.hasNext();) {
            changedFields = changedFields + (String) i.next() + ",";
        }
        return "eventtype: '" + getEventTypeGuiName(eventType) + "', node: "
            + nodeNumber + ", nodetype: " + eventSource + ", changedfields: "
            + changedFields;
    }

    protected String getEventTypeGuiName(int eventType) {
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
                throw new RuntimeException("HELP! event of type " + eventType
                    + " is unknown. This should not happen");
        }
    }

    /**
     * For conveneance: conversion of the new event type indication to the old
     * style
     * 
     * @param eventType must be c,d,n or r
     * @return
     */
    public static String newTypeToOldType(int eventType) {
        switch (eventType) {
            case NodeEvent.EVENT_TYPE_CHANGED:
                return "c";
            case NodeEvent.EVENT_TYPE_DELETE:
                return "d";
            case NodeEvent.EVENT_TYPE_NEW:
                return "n";
            case NodeEvent.EVENT_TYPE_RELATION_CHANGED:
                return "r";
            default:
                throw new RuntimeException("HELP! event of type " + eventType
                    + " is unknown. This should not happen");
        }
    }

    /**
     * For conveneance: conversion of the old event type indication to the new
     * style
     * 
     * @param eventType
     * @return
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
            throw new RuntimeException("HELP! event of type " + eventType
                + " is unknown. This should not happen");
        }
    }

    /**
     * @return Returns the nodeNumber.
     */
    public String getNodeNumber() {
        return nodeNumber;
    }

    /**
     * @return Returns the machine name.
     */
    public String getMachine() {
        return machine;
    }

}