/*
 * Created on 21-jun-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

import org.mmbase.cache.Cache;
import org.mmbase.module.core.MMBase;
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
 * @version $Id$
 */
public class NodeEvent extends Event {


    private static final long serialVersionUID = 1L;

    private static final Logger log = Logging.getLoggerInstance(NodeEvent.class);

    /**
     * Event type specific for MMBase nodes.
     */
    public static final int TYPE_RELATION_CHANGE = 3;

    private final int nodeNumber;
    private String builderName;

    private final Map oldValues;
    private final Map newValues;

    /**
     * @param machineName (MMBase) name of the server
     * @param builderName name of builder of node event is about
     * @param oldValues map with fields and their values that have been changed by the event
     * @param newValues map with new values of changed fields
     * @param eventType the type of event
     **/
    public NodeEvent(String machineName, String builderName, int nodeNumber, Map oldValues, Map newValues, int eventType ){
        super(machineName, eventType);
        this.builderName = builderName;
        this.nodeNumber = nodeNumber;
        this.oldValues = oldValues == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(new HashMap(oldValues));
        this.newValues = newValues == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(new HashMap(newValues));
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
        case TYPE_NEW:
            return newValues.keySet();
        case TYPE_CHANGE:
            //for changed both old and new values are good (similar keys)
            return newValues.keySet();
        case  TYPE_DELETE:
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
        return "Node event: '" + getEventTypeGuiName(eventType) + "', node: " + nodeNumber + ", nodetype: " + builderName + ", oldValues: " + oldValues + ", newValues: " + newValues + "changedFields: " + getChangedFields();
    }

    protected static String getEventTypeGuiName(int eventType) {
        switch (eventType) {
        case Event.TYPE_CHANGE:
            return "node changed";
        case Event.TYPE_DELETE:
            return "node deleted";
        case Event.TYPE_NEW:
            return "new node";
        case NodeEvent.TYPE_RELATION_CHANGE:
            return "relation changed";
        default:
            throw new IllegalArgumentException("HELP! event of type " + eventType + " is unknown. This should not happen");
        }
    }



    /**
     * I think this method is not needed.
     * @deprecated
     */
    /*
    public NodeEvent clone(String builderName) {
        NodeEvent clone = (NodeEvent) super.clone();
        clone.builderName = builderName;
        return clone;
    }
    */

    /**
     * For conveneance: conversion of the new event type indication to the old
     * style
     *
     * @param eventType must be c,d,n or r
     * @return A String describing the type of an event. (like "c" (change), "d" (delete), "n" (new), or "r" (relation change))
     */
    public static String newTypeToOldType(int eventType) {
        switch (eventType) {
        case Event.TYPE_CHANGE:           return "c";
        case Event.TYPE_DELETE:           return "d";
        case Event.TYPE_NEW:              return "n";
        case NodeEvent.TYPE_RELATION_CHANGE: return "r";
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
            throw new IllegalArgumentException("HELP! event of type '" + eventType + "' is unknown. This should not happen. (length = " + eventType.length() + ")");
        }
        switch(eventType.charAt(0)) {
        case 'c': return Event.TYPE_CHANGE;
        case 'd': return Event.TYPE_DELETE;
        case 'n': return Event.TYPE_NEW;
        case 'r': return NodeEvent.TYPE_RELATION_CHANGE;
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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        log.debug("deserialized node event for " + nodeNumber);
        try {
            int otype = MMBase.getMMBase().getTypeDef().getIntValue(builderName);
            if (otype != -1) {
                Cache typeCache = Cache.getCache("TypeCache");
                if (typeCache != null) {
                    Integer node = new Integer(nodeNumber);
                    Integer type = new Integer(otype);
                    Integer cachedType = (Integer) typeCache.get(node);
                    if (cachedType == null) {
                        log.debug("Putting in type cache " + node + " -> " + type);
                        typeCache.put(node, type);
                    } else {
                        if (type.equals(cachedType)) {
                            log.debug("Type already cached");
                        } else {
                            log.warn("Type in event not the same as in cache " + type + " != " + cachedType);
                        }
                    }
                } else {
                    log.service("No typecache?");
                }
            } else {
                log.service("Builder '" + builderName + "' from " + nodeNumber + " not found. Originating from " + getMachine());
            }
        } catch (Exception e) {
             log.error(e);
        }

    }


    public static void main(String[] args) {
        //test serializable
        Map  oldv = new HashMap(), newv = new HashMap();
        oldv.put("een","veen");
        oldv.put("twee","vtwee");
        newv.putAll(oldv);

        NodeEvent event = new NodeEvent(  "local", "builder", 0, oldv, newv, NodeEvent.TYPE_CHANGE);
        System.out.println("event 1: " + event.toString());

    }

}
