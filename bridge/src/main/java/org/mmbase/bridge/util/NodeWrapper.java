/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;

/**
 * Wraps another Node. You can use this if you want to implement Node, and want to base that
 * implementation on an existing <code>Node</code> instance.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public abstract class NodeWrapper implements Node {
    protected final Node node;
    public NodeWrapper(Node node) {
        assert node != null;
        this.node = node;
    }
    @Override
    public Cloud getCloud()             { return node.getCloud(); }
    @Override
    public NodeManager getNodeManager() { return node.getNodeManager(); }
    @Override
    public void setNodeManager(NodeManager nm) { node.setNodeManager(nm); }
    @Override
    public int getNumber()         { return node.getNumber(); }
    @Override
    public boolean isRelation()         { return node.isRelation(); }
    @Override
    public Relation toRelation()        { return node.toRelation(); }
    @Override
    public boolean isNodeManager() { return node.isNodeManager();}
    @Override
    public NodeManager toNodeManager() { return node.toNodeManager(); }
    @Override
    public boolean isRelationManager() { return node.isRelationManager(); }
    @Override
    public RelationManager toRelationManager() { return node.toRelationManager(); }
    @Override
    public void setValue(String fieldName, Object value) { node.setValue(fieldName, value); }
    @Override
    public void setValueWithoutProcess(String fieldName, Object value) { node.setValueWithoutProcess(fieldName, value); }
    @Override
    public void setObjectValue(String fieldName, Object value) { node.setObjectValue(fieldName, value); }
    @Override
    public void setBooleanValue(String fieldName, boolean value) { node.setBooleanValue(fieldName, value); }
    @Override
    public void setNodeValue(String fieldName, Node value) { node.setNodeValue(fieldName, value); }
    @Override
    public void setIntValue(String fieldName, int value) { node.setIntValue(fieldName, value); }
    @Override
    public void setFloatValue(String fieldName, float value) { node.setFloatValue(fieldName, value); }
    @Override
    public void setDoubleValue(String fieldName, double value) { node.setDoubleValue(fieldName, value); }
    @Override
    public void setByteValue(String fieldName, byte[] value) { node.setByteValue(fieldName, value); }
    @Override
    public void setInputStreamValue(String fieldName, java.io.InputStream value, long size) { node.setInputStreamValue(fieldName, value, size); }
    @Override
    public void setLongValue(String fieldName, long value) { node.setLongValue(fieldName, value); }
    @Override
    public void setStringValue(String fieldName, String value) { node.setStringValue(fieldName, value); }
    @Override
    public void setDateValue(String fieldName, Date value) { node.setDateValue(fieldName, value); }
    @Override
    public void setDecimalValue(String fieldName, java.math.BigDecimal value) { node.setDecimalValue(fieldName, value); }
    @Override
    public void setListValue(String fieldName, List<?> value) { node.setListValue(fieldName, value); }
    @Override
    public boolean isNull(String fieldName) { return node.isNull(fieldName); }
    @Override
    public long    getSize(String fieldName) { return node.getSize(fieldName); }
    @Override
    public Object getValue(String fieldName) { return node.getValue(fieldName); }
    @Override
    public Object getValueWithoutProcess(String fieldName) { return node.getValueWithoutProcess(fieldName); }
    @Override
    public Object getObjectValue(String fieldName) { return node.getObjectValue(fieldName); }
    @Override
    public boolean getBooleanValue(String fieldName) { return node.getBooleanValue(fieldName); }
    @Override
    public Node getNodeValue(String fieldName) { return node.getNodeValue(fieldName); }
    @Override
    public int getIntValue(String fieldName)  { return node.getIntValue(fieldName); }
    @Override
    public float getFloatValue(String fieldName)  { return node.getFloatValue(fieldName); }
    @Override
    public long getLongValue(String fieldName)  { return node.getLongValue(fieldName); }
    @Override
    public double getDoubleValue(String fieldName) { return node.getDoubleValue(fieldName); }
    @Override
    public byte[] getByteValue(String fieldName) { return node.getByteValue(fieldName); }
    @Override
    public java.io.InputStream getInputStreamValue(String fieldName) { return node.getInputStreamValue(fieldName); }
    @Override
    public String getStringValue(String fieldName) { return node.getStringValue(fieldName); }
    @Override
    public Date getDateValue(String fieldName) { return node.getDateValue(fieldName); }
    @Override
    public java.math.BigDecimal getDecimalValue(String fieldName) { return node.getDecimalValue(fieldName); }
    @Override
    public List<?> getListValue(String fieldName) { return node.getListValue(fieldName); }
    @Override
    public FieldValue getFieldValue(String fieldName) throws NotFoundException {
        return node.getFieldValue(fieldName);
    }
    @Override
    public FieldValue getFieldValue(Field field) { return node.getFieldValue(field); }
    @Override
    public Collection<String> validate() { return node.validate(); }
    @Override
    public void commit() { node.commit(); }
    @Override
    public void cancel() { node.cancel(); }
    @Override
    public boolean isNew() {  return node.isNew(); }
    @Override
    public boolean isChanged(String fieldName) {  return node.isChanged(fieldName); }
    @Override
    public boolean isChanged() {  return node.isChanged(); }
    @Override
    public Set<String> getChanged() {  return node.getChanged(); }
    @Override
    public void delete() { node.delete(); }
    @Override
    public void delete(boolean deleteRelations) { node.delete(deleteRelations); }
    @Override
    public String toString() { return node.toString(); }
    @Override
    public Document getXMLValue(String fieldName) throws IllegalArgumentException {
        return node.getXMLValue(fieldName);
    }
    @Override
    public Element getXMLValue(String fieldName, Document tree) throws IllegalArgumentException {
        return node.getXMLValue(fieldName, tree);
    }
    @Override
    public void setXMLValue(String fieldName, Document value) { node.setXMLValue(fieldName, value); }
    @Override
    public boolean hasRelations() { return node.hasRelations(); }
    @Override
    public void deleteRelations() { node.deleteRelations(); }

    @Override
    public void deleteRelations(String relationManager) { node.deleteRelations(relationManager); }
    @Override
    public RelationList getRelations() { return node.getRelations(); }
    @Override
    public RelationList getRelations(String role) { return node.getRelations(role); }
    @Override
    public RelationList getRelations(String role, String nodeManager) { return node.getRelations(role, nodeManager); }
    @Override
    public RelationList getRelations(String role, NodeManager nodeManager) { return node.getRelations(role, nodeManager); }
    @Override
    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) { return node.getRelations(role, nodeManager, searchDir); }
    @Override
    public int countRelations() { return node.countRelations(); }
    @Override
    public int countRelations(String relationManager) { return node.countRelations(relationManager); }
    @Override
    public NodeList getRelatedNodes() { return node.getRelatedNodes(); }
    @Override
    public NodeList getRelatedNodes(String nodeManager) { return node.getRelatedNodes(nodeManager); }
    @Override
    public NodeList getRelatedNodes(NodeManager nodeManager) { return node.getRelatedNodes(nodeManager); }
    @Override
    public NodeList getRelatedNodes(String nodeManager, String role, String searchDir) { return node.getRelatedNodes(nodeManager, role, searchDir); }
    @Override
    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) { return node.getRelatedNodes(nodeManager, role, searchDir); }
    @Override
    public int countRelatedNodes(String nodeManager) { return node.countRelatedNodes(nodeManager); }
    @Override
    public int countRelatedNodes(NodeManager otherNodeManager, String role, String searchDir) { return node.countRelatedNodes(otherNodeManager, role, searchDir); }
    @Override
    public StringList getAliases() { return node.getAliases(); }
    @Override
    public void createAlias(String alias) { node.createAlias(alias); }
    @Override
    public void deleteAlias(String alias) { node.deleteAlias(alias); }
    @Override
    public Relation createRelation(Node destinationNode, RelationManager relationManager) { return node.createRelation(destinationNode, relationManager); }
    @Override
    public void setContext(String context) { node.setContext(context); }
    @Override
    public String getContext() { return node.getContext(); }
    @Override
    public StringList getPossibleContexts() { return node.getPossibleContexts(); }
    @Override
    public boolean mayWrite() { return node.mayWrite(); }
    @Override
    public boolean mayDelete() { return node.mayDelete(); }
    @Override
    public boolean mayChangeContext() { return node.mayChangeContext(); }
    @Override
    public Collection<Function<?>> getFunctions() { return node.getFunctions(); }
    @Override
    public Function getFunction(String functionName) { return node.getFunction(functionName); }
    @Override
    public Parameters createParameters(String functionName) { return node.createParameters(functionName); }
    @Override
    public FieldValue getFunctionValue(String functionName, List<?> parameters) { return node.getFunctionValue(functionName, parameters);}


    @Override
    public int hashCode() { return node.hashCode(); }
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) { return node.equals(o); }
    @Override
    public int compareTo(Node o) { return node.compareTo(o); }

    public Node getNode() {
        return node;
    }
}
