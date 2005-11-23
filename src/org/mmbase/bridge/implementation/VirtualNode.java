/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.io.InputStream;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of Node. Simply wraps virtual node of core into an bridge Node. This class can be
 * used even if you don't know the precise implementation of the Cloud object (in contradiction to {@link BasicNode}, and therefore has a public constructor 
 * {@link VirtualNode(org.mmbase.module.core.VirtualNode, Cloud)}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: VirtualNode.java,v 1.9 2005-11-23 10:22:41 michiel Exp $
 * @see org.mmbase.bridge.Node
 * @see org.mmbase.module.core.VirtualNode
 * @since MMBase-1.8
 */
public class VirtualNode implements Node {

    private static final Logger log = Logging.getLoggerInstance(VirtualNode.class);

    final protected org.mmbase.module.core.VirtualNode noderef;

    /**
     * This is normally, but not always, a VirtualBuilder. It is not for some builders which have
     * besides real nodes also virtual nodes, like typedef (cluster nodes) and typerel (allowed relations because of inheritance).
     */
    final protected NodeManager nodeManager; 
    final protected Cloud cloud;


    protected VirtualNode(Cloud cloud, org.mmbase.module.core.VirtualNode node, NodeManager nm) {
        this.cloud = cloud;
        this.noderef = node;
        nodeManager = nm;
        
    }

    public VirtualNode(org.mmbase.module.core.VirtualNode node, Cloud cloud) {
        this(cloud, node, new VirtualNodeManager(node, cloud));
    }

    /**
     * Returns the MMObjectNode on which the VirtualNode was based
     */
    public org.mmbase.module.core.VirtualNode getNodeRef() {
        return noderef;
    }


    public boolean isRelation() {
        return false;
    }

    public Relation toRelation() {
        return (Relation)this;
    }

    public boolean isNodeManager() {
        return false;
    }

    public NodeManager toNodeManager() {
        return (NodeManager)this;
    }

    public boolean isRelationManager() {
        return false;
    }

    public RelationManager toRelationManager() {
        return (RelationManager)this;
    }


    /**
     * Obtains a reference to the underlying MMObjectNode.
     * If the underlying node was deleted, this returns a virtual node with
     * no info except the (original) node number.
     * @return the underlying MMObjectNode
     * @throws NotFoundException if no node was specified. This generally means the
     */
    protected final MMObjectNode getNode() {
        return noderef;
    }


    public Cloud getCloud() {
        return cloud;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public int getNumber() {
        return -1;
    }

    public boolean isNew() {
        return true;
    }

    public boolean isChanged(String fieldName) {
        return false;
    }
    public boolean isChanged() {
        return false;
    }
    

    public void setValue(String fieldName, Object value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }
    public void setValueWithoutProcess(String fieldName, Object value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setObjectValue(String fieldName, Object value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setBooleanValue(String fieldName,final  boolean value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setDateValue(String fieldName, final Date value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setListValue(String fieldName, final List value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setNodeValue(String fieldName, final Node value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setIntValue(String fieldName, final int value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setLongValue(String fieldName, final long value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setFloatValue(String fieldName, final float value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setDoubleValue(String fieldName, final double value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setByteValue(String fieldName, final byte[] value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setInputStreamValue(String fieldName, final InputStream value, long size) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setStringValue(String fieldName, final String value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void setXMLValue(String fieldName, final Document value) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public boolean isNull(String fieldName) {
        return noderef.isNull(fieldName);
    }

    public Object getValue(String fieldName) {
        Object value = noderef.getValue(fieldName);
        if (value == null) return null;
        if (noderef.getBuilder().hasField(fieldName)) {
            int type = noderef.getBuilder().getField(fieldName).getType();
            switch(type) {
                case Field.TYPE_STRING:  return getStringValue(fieldName);
                case Field.TYPE_BINARY:    return getByteValue(fieldName);
                case Field.TYPE_INTEGER: return new Integer(getIntValue(fieldName));
                case Field.TYPE_FLOAT:   return new Float(getFloatValue(fieldName));
                case Field.TYPE_DOUBLE:  return new Double(getDoubleValue(fieldName));
                case Field.TYPE_LONG:    return new Long(getLongValue(fieldName));
                case Field.TYPE_XML:     return getXMLValue(fieldName);
                case Field.TYPE_NODE:    return getNodeValue(fieldName);
                case Field.TYPE_BOOLEAN: return Boolean.valueOf(getBooleanValue(fieldName));
                case Field.TYPE_DATETIME:return getDateValue(fieldName);
                case Field.TYPE_LIST:    return getListValue(fieldName);
                default:
                    log.error("Unknown fieldtype '" + type + "'");
                    return value;
            }
        } else {
            //log.warn("Requesting value of unknown field '" + fieldName + "')");
            return value;
        }

    }

    public Object getValueWithoutProcess(String fieldName) {
        Object result = getNode().getValue(fieldName);
        return result;
    }

    public Object getObjectValue(String fieldName) {
        Object result = getValueWithoutProcess(fieldName);
        return result;
    }

    public boolean getBooleanValue(String fieldName) {
        Boolean result = Boolean.valueOf(noderef.getBooleanValue(fieldName));
        return result.booleanValue();
    }

    public Date getDateValue(String fieldName) {
        Date result =  noderef.getDateValue(fieldName);
        return result;
    }

    public List getListValue(String fieldName) {
        List result =  noderef.getListValue(fieldName);
        return result;
    }


    /**
     * Returns the Node value of a certain field, but in the case of a VirtualNode this can also occasionally be <code>null</code>
     * because the node can have been deleted.
     */
    public Node getNodeValue(String fieldName) {
        if (fieldName == null || fieldName.equals("number")) {
            return this;
        }        
        Node result = null;
        MMObjectNode mmobjectNode = getNode().getNodeValue(fieldName);
        if (mmobjectNode != null) {
            MMObjectBuilder builder = mmobjectNode.getBuilder();
            try {
                result = cloud.getNode(mmobjectNode.getNumber());
            } catch (NotFoundException nfe) {
                // don't know when this happens, perhaps the node was deleted in the mean time?
                log.debug(nfe.getMessage());
                return null;
            }
        }
        return result;
    }

    public int getIntValue(String fieldName) {
        Integer result = new Integer(getNode().getIntValue(fieldName));
        return result.intValue();

    }

    public float getFloatValue(String fieldName) {
        Float result = new Float(getNode().getFloatValue(fieldName));
        return result.floatValue();
    }

    public long getLongValue(String fieldName) {
        Long result = new Long(getNode().getLongValue(fieldName));
        return result.longValue();
    }

    public double getDoubleValue(String fieldName) {
        Double result = new Double(getNode().getDoubleValue(fieldName));
        return result.doubleValue();
    }

    public byte[] getByteValue(String fieldName) {
        byte[] result = getNode().getByteValue(fieldName);
        return result;
    }
    public java.io.InputStream getInputStreamValue(String fieldName) {
        java.io.InputStream result = getNode().getInputStreamValue(fieldName);
        return result;
    }

    public String getStringValue(String fieldName) {
        String result = getNode().getStringValue(fieldName);
        return result;
    }

    public Document getXMLValue(String fieldName) {
        Document result = getNode().getXMLValue(fieldName);
        return result;
    }

    public FieldValue getFieldValue(String fieldName) throws NotFoundException {
        return new BasicFieldValue(this, getNodeManager().getField(fieldName));
    }

    public FieldValue getFieldValue(Field field) {
        return new BasicFieldValue(this, field);
    }



    public Element getXMLValue(String fieldName, Document tree) {
        Document doc = getXMLValue(fieldName);
        if (doc == null) {
            return null;
        }
        return (Element)tree.importNode(doc.getDocumentElement(), true);
    }

    public Collection validate() {
        // I have no idea..
        return Collections.EMPTY_SET;
    }

    public void commit() {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void cancel() {
    }

    public void delete() {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void delete(boolean deleteRelations) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public String toString() {
        /*
        Map values = getNode().getValues();
        List res = new ArrayList();
        Iterator i = values.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            if (((String) entry.getKey()).endsWith("number")) {
                res.add(entry.getValue());
            }
        }
        
        return "VIRTUAL" + res + values.keySet();
        */
        return "VIRTUAL" + getNode();
    }

    public void deleteRelations() {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public void deleteRelations(String type) throws NotFoundException {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public RelationList getRelations() {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public RelationList getRelations(String role) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public RelationList getRelations(String role, NodeManager nodeManager) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public boolean hasRelations() {
        return false;
    }

    public int countRelations() {
        return 0;
    }

    public int countRelations(String type) {
        return 0;

    }


    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        return 0;
    }


    public NodeList getRelatedNodes() {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public NodeList getRelatedNodes(String type) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public NodeList getRelatedNodes(NodeManager nodeManager) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }
    public NodeList getRelatedNodes(String type, String role, String searchDir) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }

    public int countRelatedNodes(String type) {
        return 0;
    }

    public StringList getAliases() {
        throw new UnsupportedOperationException("Virtual nodes have no aliases");
    }

    public void createAlias(String aliasName) {
        throw new UnsupportedOperationException("Virtual nodes have no aliases");
    }

    public void deleteAlias(String aliasName) {
        throw new UnsupportedOperationException("Virtual nodes have no aliases");
    }

    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        throw new UnsupportedOperationException("Virtual nodes have no relations");
    }


    public void setContext(String context) {
        throw new UnsupportedOperationException("Virtual nodes have no security context");
    }

    // javadoc inherited (from Node)
    public String getContext() {
        throw new UnsupportedOperationException("Virtual nodes have no security context");
    }


    // javadoc inherited (from Node)
    public StringList getPossibleContexts() {
        throw new UnsupportedOperationException("Virtual nodes have no security context");
    }

    public boolean mayWrite() {
        return false;
    }

    public boolean mayDelete() {
        return false;
    }

    public boolean mayChangeContext() {
        return false;
    }

    public Collection  getFunctions() {
        return  getNode().getFunctions();
    }

    public Function getFunction(String functionName) {
        Function function = getNode().getFunction(functionName);
        if (function == null) {
            throw new NotFoundException("Function with name " + functionName + " does not exist on node " + getNode().getNumber() + " of type " + getNodeManager().getName());
        }
        return function;
    }

    public Parameters createParameters(String functionName) {
        Parameters params =  getFunction(functionName).createParameters();
        params.setIfDefined(Parameter.NODE, this);
        params.setIfDefined(Parameter.CLOUD, getCloud());
        return params;
    }

    public FieldValue getFunctionValue(String functionName, List parameters) {
        Function function = getFunction(functionName);
        Parameters params = function.createParameters();
        params.setIfDefined(Parameter.NODE,  this);
        params.setIfDefined(Parameter.CLOUD, getCloud());
        params.setAll(parameters);
        return new BasicFunctionValue(getCloud(), function.getFunctionValue(params));
    }
    public int compareTo(Object o) {
        return 1;
    }
}
