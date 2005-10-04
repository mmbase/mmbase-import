/*
 * Created on 21-jun-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;


/**
 * This class communicates a node event. in case of a change event, it contains
 * a map of changed values, mapped to their field's name, as well as the
 * preveaus values of the changed fields.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public class NodeEvent extends Event implements Serializable {

    private static final Logger log = Logging.getLoggerInstance(NodeEvent.class);

    public static final int EVENT_TYPE_NEW = 0;

    public static final int EVENT_TYPE_CHANGED = 1;

    public static final int EVENT_TYPE_DELETE = 2;

    public static final int EVENT_TYPE_RELATION_CHANGED = 3;

    private MMObjectNode node;

    private int eventType;

    private String machine;

    // ernst:what object types can the 'old values' be, and what happens if they
    // can't be serialized??
    private Map oldValues = new HashMap();
    private Map newValues = new HashMap();

    // implementation of serializable
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
            node = new MMObjectNode(builder);
            Iterator it = oldValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                node.storeValue((String) entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * @param node
     * @param eventType
     */

    public NodeEvent(MMObjectNode node, int eventType) {
        this(node, eventType, MMBase.getMMBase().getMachineName());
    }

    public NodeEvent(MMObjectNode node, int eventType, String machine) {
        this.node = node;
        this.eventType = eventType;
        this.machine = machine;
        name = "node event";

        // at this point the new value for the changed fields is
        // in the node, and the old values are in the oldValues map
        oldValues.putAll(node.getOldValues());
        newValues.putAll(node.getValues());
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
     * @return the new value of the field
     */
    public Object getNewValue(String fieldName) {
        return newValues.get(fieldName);
    }

    public int getType() {
        return eventType;
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
        return getName() + " : '" + getEventTypeGuiName(eventType) + "', node: " + node.getNumber() + ", nodetype: " + node.getBuilder() + ", changedfields: " + changedFields;
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
     * @return Returns the node.
     */
    public MMObjectNode getNode() {
        return node;
    }

    /**
     * @return Returns the machine name.
     */
    public String getMachine() {
        return machine;
    }

}
