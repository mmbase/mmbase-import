/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.module.core.VirtualBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;

/**
 * Implementation of Node. Simply wraps virtual node of core into an bridge Node. This class can be
 * used even if you don't know the precise implementation of the Cloud object (in contradiction to {@link BasicNode}, and therefore has a public constructor
 * {@link #VirtualNode(org.mmbase.module.core.VirtualNode, Cloud)}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
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
     * See {@link org.mmbase.bridge.util.MapNode}, which <em>is</em> a complete reimplementation (with no core dependencies).
     */
    public VirtualNode(Map values, Cloud cloud) {
        this(getVirtualNode(values), cloud);
    }

    @Override
    public String toString() {
        return "BridgeVirtualNode " + noderef;
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


    @Override
    public boolean isRelation() {
        return false;
    }

    @Override
    public Relation toRelation() {
        return (Relation)this;
    }

    @Override
    public boolean isNodeManager() {
        return false;
    }

    @Override
    public NodeManager toNodeManager() {
        return (NodeManager)this;
    }

    @Override
    public boolean isRelationManager() {
        return false;
    }

    @Override
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

    @Override
    public int getNumber() {
        return noderef.getNumber();
    }


    protected void edit(int action) {
        throw new UnsupportedOperationException("Cannot edit virtual node");
    }

    @Override
    public boolean isNull(String fieldName) {
        return noderef.isNull(fieldName);
    }

    @Override
    public void setSize(String fieldName, long size) {
        noderef.setSize(fieldName, size);
    }
    public long getSize(String fieldName) {
        return noderef.getSize(fieldName);
    }

    @Override
    protected  void setValueWithoutChecks(String fieldName, Object value) {
        // cannot edit virtual node.
        // should not come here..
        getNode().setValue(fieldName, value);
    }

    public Object getValueWithoutProcess(String fieldName) {
        Object result = getNode().getValue(fieldName);
        return result;
    }

    @Override
    public boolean getBooleanValue(String fieldName) {
        Boolean result = Boolean.valueOf(noderef.getBooleanValue(fieldName));
        return result.booleanValue();
    }

    @Override
    public Date getDateValue(String fieldName) {
        Date result =  noderef.getDateValue(fieldName);
        return result;
    }

    @Override
    public List getListValue(String fieldName) {
        List result =  noderef.getListValue(fieldName);
        return result;
    }


    /**
     * Returns the Node value of a certain field, but in the case of a VirtualNode this can also occasionally be <code>null</code>
     * because the node can have been deleted.
     */
    @Override
    public Node getNodeValue(String fieldName) {
        if (fieldName == null || fieldName.equals("number")) {
            return this;
        }
        Node result = null;
        Object o = getNode().getValue(fieldName);
        if (o instanceof Node) {
            // a Node already
            return (Node) o;
        }
        MMObjectNode mmobjectNode = getNode().getNodeValue(fieldName);
        if (mmobjectNode != null) {
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

    @Override
    public int getIntValue(String fieldName) {
        Integer result = getNode().getIntValue(fieldName);
        return result.intValue();

    }

    @Override
    public float getFloatValue(String fieldName) {
        Float result = getNode().getFloatValue(fieldName);
        return result.floatValue();
    }

    @Override
    public long getLongValue(String fieldName) {
        Long result = getNode().getLongValue(fieldName);
        return result.longValue();
    }

    @Override
    public double getDoubleValue(String fieldName) {
        Double result = getNode().getDoubleValue(fieldName);
        return result.doubleValue();
    }

    @Override
    public byte[] getByteValue(String fieldName) {
        byte[] result = getNode().getByteValue(fieldName);
        return result;
    }
    @Override
    public java.io.InputStream getInputStreamValue(String fieldName) {
        java.io.InputStream result = getNode().getInputStreamValue(fieldName);
        return result;
    }

    @Override
    public String getStringValue(String fieldName) {
        String result = getNode().getStringValue(fieldName);
        return result;
    }

    @Override
    public Document getXMLValue(String fieldName) {
        Document result = getNode().getXMLValue(fieldName);
        return result;
    }


    public Collection<Function<?>>  getFunctions() {
        return  getNode().getFunctions();
    }

    @Override
    protected Function<?> getNodeFunction(String functionName) {
        return getNode().getFunction(functionName);
    }

    @Override
    public Parameters createParameters(String functionName) {
        return getNode().getFunction(functionName).createParameters();
    }

    @Override
    protected FieldValue createFunctionValue(Object result) {
        return new BasicFunctionValue(getCloud(), result);
    }

}
