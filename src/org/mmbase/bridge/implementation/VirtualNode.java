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
import org.mmbase.bridge.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.VirtualBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.core.util.Fields;
import org.mmbase.core.CoreField;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of Node. Simply wraps virtual node of core into an bridge Node. This class can be
 * used even if you don't know the precise implementation of the Cloud object (in contradiction to {@link BasicNode}, and therefore has a public constructor
 * {@link #VirtualNode(org.mmbase.module.core.VirtualNode, Cloud)}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: VirtualNode.java,v 1.17 2005-12-27 22:12:03 michiel Exp $
 * @see org.mmbase.bridge.Node
 * @see org.mmbase.module.core.VirtualNode
 * @since MMBase-1.8
 */
public class VirtualNode extends AbstractNode implements Node {

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
     * Makes a Node from a map of values. Sadly, this uses a local MMBase, so you can't use this with
     * e.g. RMMCI, but I didn't feel like reimplementating Node completely..
     */
    public VirtualNode(Map values, Cloud cloud) {
        this(getVirtualNode(values), cloud);

    }

    protected static org.mmbase.module.core.VirtualNode getVirtualNode(Map values) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        org.mmbase.module.core.VirtualNode node = new  org.mmbase.module.core.VirtualNode(builder);
        Iterator i = values.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String name = entry.getKey().toString();
            Object value = entry.getValue();
            node.storeValue(name, value);
        }
        return node;
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
        return noderef.getNumber();
    }

    public boolean isNew() {
        return false;
    }

    public boolean isChanged(String fieldName) {
        return false;
    }
    public boolean isChanged() {
        return false;
    }
    protected void edit(int action) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public boolean isNull(String fieldName) {
        return noderef.isNull(fieldName);
    }

    public void setSize(String fieldName, long size) {
        noderef.setSize(fieldName, size);
    }
    public long getSize(String fieldName) {
        return noderef.getSize(fieldName);
    }

    protected  void setValueWithoutChecks(String fieldName, Object value) {
        // cannot edit virtual node.
        // should not come here..
        getNode().setValue(fieldName, value);
    }

    public Object getValueWithoutProcess(String fieldName) {
        Object result = getNode().getValue(fieldName);
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

    public Collection validate() {
        // I have no idea..
        return Collections.EMPTY_SET;
    }

    public void commit() {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    public void cancel() {
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

    public void deleteRelations(String type) throws NotFoundException {
    }

    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }
    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }

    public boolean hasRelations() {
        return false;
    }



    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        return 0;
    }

    public int countRelatedNodes(String type) {
        return 0;
    }

    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) {
        return BridgeCollections.EMPTY_NODELIST;
    }


    public StringList getAliases() {
        return BridgeCollections.EMPTY_STRINGLIST;
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
        return BridgeCollections.EMPTY_STRINGLIST;
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
        return new WrappedFunction(function) {
                public final Object getFunctionValue(Parameters params) {
                    params.set(Parameter.NODE, VirtualNode.this);
                    params.set(Parameter.CLOUD, VirtualNode.this.cloud);
                    return super.getFunctionValue(params);
                }
            };
    }

    public Parameters createParameters(String functionName) {
        return getNode().getFunction(functionName).createParameters();
    }

    public FieldValue getFunctionValue(String functionName, List parameters) {
        Function function = getFunction(functionName);
        Parameters params = function.createParameters();
        params.setAll(parameters);
        return new BasicFunctionValue(getCloud(), function.getFunctionValue(params));
    }
}
