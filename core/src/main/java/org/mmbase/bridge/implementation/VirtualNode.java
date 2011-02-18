/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import java.io.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.bridge.util.BridgeCaster;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.DataType;
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
 * @todo  This class has large overlap with {@link BasicNode}. They probably should share an ancestor (AbstractBasicNode or so). (MMB-1870)
 * @since MMBase-1.8
 */
public class VirtualNode extends AbstractNode implements Node, Serializable {
    private static final long serialVersionUID = 0L;

    private static final Logger log = Logging.getLoggerInstance(VirtualNode.class);

    final protected org.mmbase.module.core.VirtualNode noderef;

    /**
     * This is normally, but not always, a VirtualBuilder. It is not for some builders which have
     * besides real nodes also virtual nodes, like typedef (cluster nodes) and typerel (allowed relations because of inheritance).
     */
    protected transient NodeManager nodeManager;
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
     * e.g. RMMCI, but I didn't feel like reimplementing Node completely..
     * See {@link org.mmbase.bridge.util.MapNode}, which <em>is</em> a complete reimplementation (with no core dependencies).
     */
    public VirtualNode(Map<String, ?> values, Cloud cloud) {
        this(getVirtualNode(values), cloud);
    }

    @Override
    public String toString() {
        return "BridgeVirtualNode " + noderef;
    }

    protected static org.mmbase.module.core.VirtualNode getVirtualNode(Map<String, ?> values) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        org.mmbase.module.core.VirtualNode node = new  org.mmbase.module.core.VirtualNode(builder);
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            String name = entry.getKey();
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


    /**
     * @since MMBase-1.9.4
     */
    private static class NodeAndField {
        final Node node;
        final Field field;
        public NodeAndField(Node n, String f) {
            node = n;
            field = node.getNodeManager().getField(f);
        }

    }

    /**
     * @since MMBase-1.9.2
     */
    protected NodeAndField getActualNodeForField(String fieldName) {
        String[] parts = fieldName.split("\\.", 2);
        if (parts.length == 2) {
            if (log.isDebugEnabled()) {
                log.debug("" + fieldName + " --> " + Arrays.asList(parts));
            }
            MMObjectNode mmobjectNode = getNode().getNodeValue(parts[0] + ".number");
            if (mmobjectNode != null) {
                try {
                    return new NodeAndField(cloud.getNode(mmobjectNode.getNumber()), parts[1]);
                } catch (NotFoundException nfe) {
                    // don't know when this happens, perhaps the node was deleted in the mean time?
                    log.debug(nfe.getMessage());
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return new NodeAndField(this, fieldName);
        }
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
        Boolean result = noderef.getBooleanValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            log.debug("" + field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING));
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toBoolean(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BOOLEAN).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public Date getDateValue(String fieldName) {
        Date result =  noderef.getDateValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toDate(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DATETIME).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public List getListValue(String fieldName) {
        List result =  noderef.getListValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toList(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LIST).process(actual.node, actual.field, result));
        }

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
        if (nodeManager.hasField(fieldName)) { // only if this is actually a field of this node-manager, otherwise it might be e.g. a request for an 'element' of a cluster node
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = BridgeCaster.toNode(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_NODE).process(actual.node, actual.field, result), getCloud());
        }
        return result;
    }

    @Override
    public int getIntValue(String fieldName) {
        Integer result = getNode().getIntValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result  = Casting.toInteger(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_INTEGER).process(actual.node, actual.field, result));
            // Casting on this position. Should it not be done in all get<..>Value's?
        }
        return result;

    }

    @Override
    public float getFloatValue(String fieldName) {
        Float result = getNode().getFloatValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toFloat(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_FLOAT).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public long getLongValue(String fieldName) {
        Long result = getNode().getLongValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toLong(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LONG).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public double getDoubleValue(String fieldName) {
        Double result = getNode().getDoubleValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toDouble(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DOUBLE).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public byte[] getByteValue(String fieldName) {
        byte[] result = getNode().getByteValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toByte(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(actual.node, actual.field, result));
        }
        return result;
    }
    @Override
    public java.io.InputStream getInputStreamValue(String fieldName) {
        java.io.InputStream result = getNode().getInputStreamValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toInputStream(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public String getStringValue(String fieldName) {
        String result = getNode().getStringValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toString(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING).process(actual.node, actual.field, result));
        }
        return result;
    }

    @Override
    public Document getXMLValue(String fieldName) {
        Document result = getNode().getXMLValue(fieldName);
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            NodeAndField actual = getActualNodeForField(fieldName);
            result = Casting.toXML(field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_XML).process(actual.node, actual.field, result));
        }
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


   private void writeObject(ObjectOutputStream out) throws IOException {
       // Serialization is not really tested
       out.defaultWriteObject();
       out.writeUTF(nodeManager.getName());
   }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Serialization is not really tested
        in.defaultReadObject();
        nodeManager = cloud.getNodeManager(in.readUTF());
    }
}
