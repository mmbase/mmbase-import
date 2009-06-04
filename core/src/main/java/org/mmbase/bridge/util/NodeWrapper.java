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
 * implementation on a existing <code>Node</code> instance.
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
    public Cloud getCloud()             { return node.getCloud(); }
    public NodeManager getNodeManager() { return node.getNodeManager(); }
    public void setNodeManager(NodeManager nm) { node.setNodeManager(nm); }
    public int getNumber()         { return node.getNumber(); }
    public boolean isRelation()         { return node.isRelation(); }
    public Relation toRelation()        { return node.toRelation(); }
    public boolean isNodeManager() { return node.isNodeManager();}
    public NodeManager toNodeManager() { return node.toNodeManager(); }
    public boolean isRelationManager() { return node.isRelationManager(); }
    public RelationManager toRelationManager() { return node.toRelationManager(); }
    public void setValue(String fieldName, Object value) { node.setValue(fieldName, value); }
    public void setValueWithoutProcess(String fieldName, Object value) { node.setValueWithoutProcess(fieldName, value); }
    public void setObjectValue(String fieldName, Object value) { node.setObjectValue(fieldName, value); }
    public void setBooleanValue(String fieldName, boolean value) { node.setBooleanValue(fieldName, value); }
    public void setNodeValue(String fieldName, Node value) { node.setNodeValue(fieldName, value); }
    public void setIntValue(String fieldName, int value) { node.setIntValue(fieldName, value); }
    public void setFloatValue(String fieldName, float value) { node.setFloatValue(fieldName, value); }
    public void setDoubleValue(String fieldName, double value) { node.setDoubleValue(fieldName, value); }
    public void setByteValue(String fieldName, byte[] value) { node.setByteValue(fieldName, value); }
    public void setInputStreamValue(String fieldName, java.io.InputStream value, long size) { node.setInputStreamValue(fieldName, value, size); }
    public void setLongValue(String fieldName, long value) { node.setLongValue(fieldName, value); }
    public void setStringValue(String fieldName, String value) { node.setStringValue(fieldName, value); }
    public void setDateValue(String fieldName, Date value) { node.setDateValue(fieldName, value); }
    public void setDecimalValue(String fieldName, java.math.BigDecimal value) { node.setDecimalValue(fieldName, value); }
    public void setListValue(String fieldName, List<?> value) { node.setListValue(fieldName, value); }
    public boolean isNull(String fieldName) { return node.isNull(fieldName); }
    public long    getSize(String fieldName) { return node.getSize(fieldName); }
    public Object getValue(String fieldName) { return node.getValue(fieldName); }
    public Object getValueWithoutProcess(String fieldName) { return node.getValueWithoutProcess(fieldName); }
    public Object getObjectValue(String fieldName) { return node.getObjectValue(fieldName); }
    public boolean getBooleanValue(String fieldName) { return node.getBooleanValue(fieldName); }
    public Node getNodeValue(String fieldName) { return node.getNodeValue(fieldName); }
    public int getIntValue(String fieldName)  { return node.getIntValue(fieldName); }
    public float getFloatValue(String fieldName)  { return node.getFloatValue(fieldName); }
    public long getLongValue(String fieldName)  { return node.getLongValue(fieldName); }
    public double getDoubleValue(String fieldName) { return node.getDoubleValue(fieldName); }
    public byte[] getByteValue(String fieldName) { return node.getByteValue(fieldName); }
    public java.io.InputStream getInputStreamValue(String fieldName) { return node.getInputStreamValue(fieldName); }
    public String getStringValue(String fieldName) { return node.getStringValue(fieldName); }
    public Date getDateValue(String fieldName) { return node.getDateValue(fieldName); }
    public java.math.BigDecimal getDecimalValue(String fieldName) { return node.getDecimalValue(fieldName); }
    public List<?> getListValue(String fieldName) { return node.getListValue(fieldName); }
    public FieldValue getFieldValue(String fieldName) throws NotFoundException {
        return node.getFieldValue(fieldName);
    }
    public FieldValue getFieldValue(Field field) { return node.getFieldValue(field); }
    public Collection<String> validate() { return node.validate(); }
    public void commit() { node.commit(); }
    public void cancel() { node.cancel(); }
    public boolean isNew() {  return node.isNew(); }
    public boolean isChanged(String fieldName) {  return node.isChanged(fieldName); }
    public boolean isChanged() {  return node.isChanged(); }
    public Set<String> getChanged() {  return node.getChanged(); }
    public void delete() { node.delete(); }
    public void delete(boolean deleteRelations) { node.delete(deleteRelations); }
    @Override
    public String toString() { return node.toString(); }
    public Document getXMLValue(String fieldName) throws IllegalArgumentException {
        return node.getXMLValue(fieldName);
    }
    public Element getXMLValue(String fieldName, Document tree) throws IllegalArgumentException {
        return node.getXMLValue(fieldName, tree);
    }
    public void setXMLValue(String fieldName, Document value) { node.setXMLValue(fieldName, value); }
    public boolean hasRelations() { return node.hasRelations(); }
    public void deleteRelations() { node.deleteRelations(); };
    public void deleteRelations(String relationManager) { node.deleteRelations(relationManager); }
    public RelationList getRelations() { return node.getRelations(); }
    public RelationList getRelations(String role) { return node.getRelations(role); }
    public RelationList getRelations(String role, String nodeManager) { return node.getRelations(role, nodeManager); }
    public RelationList getRelations(String role, NodeManager nodeManager) { return node.getRelations(role, nodeManager); }
    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) { return node.getRelations(role, nodeManager, searchDir); }
    public int countRelations() { return node.countRelations(); }
    public int countRelations(String relationManager) { return node.countRelations(relationManager); }
    public NodeList getRelatedNodes() { return node.getRelatedNodes(); }
    public NodeList getRelatedNodes(String nodeManager) { return node.getRelatedNodes(nodeManager); }
    public NodeList getRelatedNodes(NodeManager nodeManager) { return node.getRelatedNodes(nodeManager); }
    public NodeList getRelatedNodes(String nodeManager, String role, String searchDir) { return node.getRelatedNodes(nodeManager, role, searchDir); }
    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) { return node.getRelatedNodes(nodeManager, role, searchDir); }
    public int countRelatedNodes(String nodeManager) { return node.countRelatedNodes(nodeManager); }
    public int countRelatedNodes(NodeManager otherNodeManager, String role, String searchDir) { return node.countRelatedNodes(otherNodeManager, role, searchDir); }
    public StringList getAliases() { return node.getAliases(); }
    public void createAlias(String alias) { node.createAlias(alias); }
    public void deleteAlias(String alias) { node.deleteAlias(alias); }
    public Relation createRelation(Node destinationNode, RelationManager relationManager) { return node.createRelation(destinationNode, relationManager); }
    public void setContext(String context) { node.setContext(context); }
    public String getContext() { return node.getContext(); }
    public StringList getPossibleContexts() { return node.getPossibleContexts(); }
    public boolean mayWrite() { return node.mayWrite(); }
    public boolean mayDelete() { return node.mayDelete(); }
    public boolean mayChangeContext() { return node.mayChangeContext(); }
    public Collection<Function<?>> getFunctions() { return node.getFunctions(); }
    public Function getFunction(String functionName) { return node.getFunction(functionName); }
    public Parameters createParameters(String functionName) { return node.createParameters(functionName); }
    public FieldValue getFunctionValue(String functionName, List<?> parameters) { return node.getFunctionValue(functionName, parameters);}


    @Override
    public int hashCode() { return node.hashCode(); }
    @Override
    public boolean equals(Object o) { return node.equals(o); }
    public int compareTo(Node o) { return node.compareTo(o); }

    public Node getNode() {
        return node;
    }
}
