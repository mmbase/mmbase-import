/*
 * Created on 2-nov-2005
 *
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;

/**
 * @author Ernst Bunders
 *this is a utility class to create node event and relation event instances. the reason for it is
 *that we want to references to core classes in the NodeEvent and RelationEvent classes, to keep them bridge-friendly,
 *but we need a little help for easy instantiation.
 */
public class NodeEventHelper {

    public static NodeEvent createNodeEventInstance(Node node, int eventType, String machineName){
        if(machineName == null)machineName = MMBase.getMMBase().getMachineName();
        MMObjectNode coreNode = MMBase.getMMBase().getBuilder(node.getNodeManager().getName()).getNode(node.getNumber());
        return createNodeEventInstance(coreNode, eventType, machineName);
    }

    /**
     * create an NodeEvent instance with an MMObjectNode
     * @param node
     * @param eventType
     * @param machineName or null to create a node event with local machine name
     * @return new instance of NodeEvent
     */
    public static NodeEvent createNodeEventInstance(MMObjectNode node, int eventType, String machineName){
        if(machineName == null) machineName = MMBase.getMMBase().getMachineName();
        Map oldEventValues;
        Map newEventValues;

        //fill the old and new values maps for the event
        switch(eventType) {
        case Event.TYPE_NEW:
            newEventValues = removeBinaryValues(node.getValues());
            oldEventValues = Collections.EMPTY_MAP;
            break;
        case Event.TYPE_CHANGE:
            oldEventValues = removeBinaryValues(node.getOldValues());
            newEventValues = new HashMap();
            Map values = node.getValues();
            for(Iterator i = oldEventValues.keySet().iterator(); i.hasNext(); ) {
                Object key = i.next();
                newEventValues.put(key, values.get(key));
            }
            newEventValues = removeBinaryValues(newEventValues);
            break;
        case Event.TYPE_DELETE:
            newEventValues = Collections.EMPTY_MAP;
            oldEventValues = removeBinaryValues(node.getValues());
            break;
        default: {
            oldEventValues = Collections.EMPTY_MAP;
            newEventValues = Collections.EMPTY_MAP;
            // err.
        }
        }

        return new NodeEvent(machineName, node.getBuilder().getTableName(), node.getNumber(), oldEventValues, newEventValues, eventType);
    }

    private static Map removeBinaryValues(Map oldEventValues) {
        Set toremove = null;
        synchronized(oldEventValues) {
            for (Iterator iterator = oldEventValues.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getValue() != null && (entry.getValue() instanceof byte[])) {
                    if (toremove == null) toremove = new HashSet();
                    toremove.add(entry.getKey());
                }
            }
        }
        if (toremove != null) {
            Map newMap = new HashMap();
            newMap.putAll(oldEventValues);
            for (Iterator iterator = toremove.iterator(); iterator.hasNext();) {
                newMap.remove(iterator.next());
            }
            return Collections.unmodifiableMap(newMap);
        } else {
            return oldEventValues;
        }
    }

    public static RelationEvent createRelationEventInstance(Relation node, int eventType, String machineName){
        MMObjectNode coreNode = MMBase.getMMBase().getBuilder(node.getNodeManager().getName()).getNode(node.getNumber());
        return createRelationEventInstance(coreNode, eventType, machineName);
    }

    /**
     * create an RelationEvent instnce with an MMObjectNode (builder should be specialization of insrel)
     * @param node
     * @param eventType
     * @param machineName
     * @return a new RelationEvetn instance
     * @throws IllegalArgumentException when given node's builder is not a specialization of insrel
     */
    public static RelationEvent createRelationEventInstance(MMObjectNode node, int eventType, String machineName){
        if (!(node.getBuilder() instanceof InsRel)) {
            throw new IllegalArgumentException( "you can not create a relation changed event with this node");
        }
        if(machineName == null) machineName = MMBase.getMMBase().getMachineName();
        MMObjectNode reldef = node.getNodeValue("rnumber");

        int relationSourceNumber = node.getIntValue("snumber");
        int relationDestinationNumber = node.getIntValue("dnumber");

        String relationSourceType = MMBase.getMMBase().getBuilderNameForNode(relationSourceNumber);
        String relationDestinationType =MMBase.getMMBase().getBuilderNameForNode(relationDestinationNumber);
        NodeEvent nodeEvent = createNodeEventInstance(node, eventType, machineName);
        int role = reldef.getNumber();
        return new RelationEvent(nodeEvent, relationSourceNumber, relationDestinationNumber, relationSourceType, relationDestinationType, role);
    }
}
