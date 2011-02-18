/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;

import java.text.Collator;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.BasicFieldValue;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Abstract implementation of Node.
 * All methods which are based on other methods are implemented
 * here, to minimalize the implementation effort of fully implemented Nodes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.bridge.Node
 * @since MMBase-1.8
 */
public abstract class AbstractNode implements Node {
    private static final Logger log = Logging.getLoggerInstance(AbstractNode.class);


    @Override
    public boolean isRelation() {
        return false;
    }

    @Override
    public Relation toRelation() {
        throw new ClassCastException("The node " + this + " is not a relation, (but a " + getClass() + ")");
    }

    @Override
    public boolean isNodeManager() {
        return false;
    }

    @Override
    public NodeManager toNodeManager() {
        throw new ClassCastException("The node " + this + " is not a node manager , (but a " + getClass() + ")");
    }

    @Override
    public boolean isRelationManager() {
        return false;
    }

    @Override
    public RelationManager toRelationManager() {
        throw new ClassCastException("The node " + this + " is not a relation manager , (but a " + getClass() + ")");
    }

    @Override
    public boolean isNull(String fieldName) {
        return getValueWithoutProcess(fieldName) == null;
    }

    @Override
    public int getNumber() {
        return Casting.toInt(getValueWithoutProcess("number"));
    }

    /**
     * Setting value with default method (depending on field's type)
     * @param fieldName name of the field
     * @param value set value
     */
    @Override
    public final void setValue(String fieldName, Object value) {
        Field field = getNodeManager().getField(fieldName);
        if (value == null) {
            setValueWithoutProcess(fieldName, value);
        } else {
            DataType dt = field.getDataType();


            value = dt.cast(value, this, field);

            // All this stuff with setSize is pretty horrible
            // we need to come up with something clearer than this.
            if (value instanceof org.apache.commons.fileupload.FileItem) {
                org.apache.commons.fileupload.FileItem fi = (org.apache.commons.fileupload.FileItem) value;
                setSize(fieldName, fi.getSize());
            } else if (value instanceof SerializableInputStream) {
                SerializableInputStream si = (SerializableInputStream) value;
                setSize(fieldName, si.getSize());
                log.debug("Setting size to " + si.getSize());
            }

            if (value == null && dt instanceof org.mmbase.datatypes.NumberDataType) {
                // null would otherwise be converted to -1, which makes little sense.
                // but must happen because set<Numeric>Value methods cannot accept null.
                setValueWithoutProcess(fieldName, value);
                return;
            }
            switch(dt.getBaseType()) {
            case Field.TYPE_STRING:  setStringValue(fieldName, (String) value); break;
            case Field.TYPE_INTEGER:
                setIntValue(fieldName, Casting.toInt(value));
                break;
            case Field.TYPE_BINARY:    {
                long length = getSize(fieldName);
                setInputStreamValue(fieldName, Casting.toSerializableInputStream(value), length);
                break;
            }
            case Field.TYPE_FLOAT:
                setFloatValue(fieldName, Casting.toFloat(value));
                break;
            case Field.TYPE_DOUBLE:
                setDoubleValue(fieldName, Casting.toDouble(value));
                break;
            case Field.TYPE_LONG:
                setLongValue(fieldName, Casting.toLong(value));
                break;
            case Field.TYPE_XML:     setXMLValue(fieldName, (Document) value); break;
            case Field.TYPE_NODE:    setNodeValue(fieldName, BridgeCaster.toNode(value, getCloud())); break;
            case Field.TYPE_DATETIME: setDateValue(fieldName, (Date) value); break;
            case Field.TYPE_BOOLEAN: setBooleanValue(fieldName, Casting.toBoolean(value)); break;
            case Field.TYPE_DECIMAL: setDecimalValue(fieldName, Casting.toDecimal(value)); break;
            case Field.TYPE_LIST:    setListValue(fieldName, (List) value); break;
            default:                 setObjectValue(fieldName, value);
            }
        }
    }
    /**
     * Throws exception if may not write current node
     * @since MMBase-1.9
     */
    protected void checkWrite() {
    }

    /**
     * Like setObjectValue, but without processing, this is called by the other set-values.
     * @param fieldName name of field
     * @param value new value of the field
     * @todo setting certain specific fields (i.e. snumber) should be directed to a dedicated
     *       method such as setSource(), where applicable.
     * @since MMBase-1.7
     */
    @Override
    public void setValueWithoutProcess(String fieldName, Object value) {
        checkWrite();
        if ("owner".equals(fieldName)) {
            // System.out.println("Setting owner, so setting context to " + Casting.toString(value));
            setContext(Casting.toString(value));
            return;
        }
        if ("number".equals(fieldName) || "otype".equals(fieldName)) {
            throw new BridgeException("Not allowed to change field '" + fieldName + "'.");
        }
        setValueWithoutChecks(fieldName, value);
    }

    protected abstract void setValueWithoutChecks(String fieldName, Object value);

    @Override
    public final void setObjectValue(String fieldName, Object value) {
        Field field = getNodeManager().getField(fieldName);
        value = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_UNKNOWN).process(this, field, value);
        setValueWithoutProcess(fieldName, value);
    }

    @Override
    public final void setBooleanValue(String fieldName,final  boolean value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BOOLEAN).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setDateValue(String fieldName, final Date value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_DATETIME).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }
    @Override
    public final void setDecimalValue(String fieldName, final BigDecimal value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_DECIMAL).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setListValue(String fieldName, final List value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_LIST).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    /**
     * A method to convert an object to an node number.
     * Default impelmentation is reasonable, but does not support core objects.
     */
    protected Integer toNodeNumber(Object v) {
        if (v == null) {
            return null;
        } else if (v instanceof Node) {
            return ((Node) v).getNumber();
        } else {
            // giving up
            return getCloud().getNode(v.toString()).getNumber();
        }
    }

    @Override
    public final void setNodeValue(String fieldName, final Node value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_NODE).process(this, field, value);
        setValueWithoutProcess(fieldName, toNodeNumber(v));
    }

    @Override
    public final void setIntValue(String fieldName, final int value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_INTEGER).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setLongValue(String fieldName, final long value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_LONG).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setFloatValue(String fieldName, final float value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_FLOAT).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setDoubleValue(String fieldName, final double value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_DOUBLE).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setByteValue(String fieldName, final byte[] value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BINARY).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    protected abstract void setSize(String fieldName, long size);

    private static final int readLimit = 10 * 1024 * 1024;

    @Override
    public final void setInputStreamValue(String fieldName, final InputStream value, long size) {
        if (log.isDebugEnabled()) {
            log.debug("Setting " + size + " + bytes (" + value + ")");
        }
        setSize(fieldName, size);
        Field field = getNodeManager().getField(fieldName);
        if (log.isDebugEnabled()) {
            log.debug("Setting binary value for " + field);
        }
        Object v = value;
        try {
            if (field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BINARY) != null) {
                if (value.markSupported() && size < readLimit) {
                    if (log.isDebugEnabled()) {
                        log.debug("Mark supported and using " + field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BINARY));
                    }
                    value.reset();
                    value.mark(readLimit);
                    v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BINARY).process(this, field, value);
                    value.reset();
                } else {

                    if (log.isDebugEnabled()) {
                        log.debug("Mark not supported but using " + field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BINARY));
                    }
                    org.mmbase.util.SerializableInputStream si = Casting.toSerializableInputStream(value);
                    v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_BINARY).process(this, field, si);
                }
            } else {
                log.debug("No need for processing");
                v = value;
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
        log.debug("Setting " + v);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setStringValue(final String fieldName, final String value) {
        Field field = getNodeManager().getField(fieldName);
        Object setValue = field.getDataType().preCast(value, this, field); // to resolve enumerations
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_STRING).process(this, field, setValue);
        setValueWithoutProcess(fieldName, v);
    }

    @Override
    public final void setXMLValue(String fieldName, final Document value) {
        Field field = getNodeManager().getField(fieldName);
        Object v = field.getDataType().getProcessor(DataType.PROCESS_SET, Field.TYPE_XML).process(this, field, value);
        setValueWithoutProcess(fieldName, v);
    }

    /**
     * @since MMBase-1.8.5
     */
    protected Object processNull(int type, Field field) {
        return field.getDataType().getProcessor(DataType.PROCESS_GET, type).process(this, field, null);
    }

    @Override
    public final Object getValue(String fieldName) {
        Object value = getValueWithoutProcess(fieldName);
        NodeManager nm = getNodeManager();
        if (nm.hasField(fieldName)) {
            int type = nm.getField(fieldName).getType();
            if (value == null) {
                return processNull(type, nm.getField(fieldName));
            }
            switch(type) {
            case Field.TYPE_STRING:  return getStringValue(fieldName);
            case Field.TYPE_BINARY:    return getByteValue(fieldName);
            case Field.TYPE_INTEGER: return getIntValue(fieldName);
            case Field.TYPE_FLOAT:   return getFloatValue(fieldName);
            case Field.TYPE_DOUBLE:  return getDoubleValue(fieldName);
            case Field.TYPE_LONG:    return getLongValue(fieldName);
            case Field.TYPE_XML:     return getXMLValue(fieldName);
            case Field.TYPE_NODE:   {
                // number is a NODE field, but should be returned as
                // a number (in this case, a long)
                // in the future, we may change the basic MMBase type for the number field to ID
                if ("number".equals(fieldName)) {
                    return getLongValue(fieldName);
                } else {
                    return getNodeValue(fieldName);
                }
            }
            case Field.TYPE_BOOLEAN: return getBooleanValue(fieldName);
            case Field.TYPE_DATETIME:return getDateValue(fieldName);
            case Field.TYPE_DECIMAL: return getDecimalValue(fieldName);
            case Field.TYPE_LIST:    return getListValue(fieldName);
            default:
                log.error("Unknown fieldtype '" + type + "' " +  Logging.stackTrace());
                return value;
            }
        } else {
            //log.warn("Requesting value of unknown field '" + fieldName + "')");
            return value;
        }

    }

    @Override
    public final Object getObjectValue(String fieldName) {
        Object result = getValueWithoutProcess(fieldName);
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            Object r = field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_UNKNOWN).process(this, field, result);
            if ((result != null && (! result.equals(r)))) {
                log.debug("getObjectvalue was processed! " + result + " != " + r);
                result = r;
            }
        }
        return result;
    }

    @Override
    public boolean getBooleanValue(String fieldName) {
        Boolean result = Casting.toBoolean(getValueWithoutProcess(fieldName)) ? Boolean.TRUE : Boolean.FALSE; // odd.
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = getNodeManager().getField(fieldName);
            result = (Boolean) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BOOLEAN).process(this, field, result);
        }
        return result.booleanValue();
    }

    @Override
    public Date getDateValue(String fieldName) {
        Date result = Casting.toDate(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Date) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DATETIME).process(this, field, result);
        }
        return result;
    }
    @Override
    public BigDecimal getDecimalValue(String fieldName) {
        BigDecimal result = Casting.toDecimal(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (BigDecimal) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DECIMAL).process(this, field, result);
        }
        return result;
    }

    @Override
    public List getListValue(String fieldName) {
        List result =  Casting.toList(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (List) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LIST).process(this, field, result);
        }
        return result;
    }

    @Override
    public int getIntValue(String fieldName) {
        Integer result = Casting.toInteger(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Integer) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_INTEGER).process(this, field, result);
        }
        return result.intValue();
    }

    @Override
    public float getFloatValue(String fieldName) {
        Float result = Casting.toFloat(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Float) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_FLOAT).process(this, field, result);
        }
        return result.floatValue();
    }

    @Override
    public long getLongValue(String fieldName) {
        Long result = Casting.toLong(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Long) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_LONG).process(this, field, result);
        }
        return result.longValue();
    }

    @Override
    public double getDoubleValue(String fieldName) {
        Double result = Casting.toDouble(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Double) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_DOUBLE).process(this, field, result);
        }
        return result.doubleValue();
    }

    @Override
    public byte[] getByteValue(String fieldName) {
        byte[] result = Casting.toByte(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (byte[]) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(this, field, result);
        }
        return result;
    }

    @Override
    public java.io.InputStream getInputStreamValue(String fieldName) {
        java.io.InputStream result = Casting.toInputStream(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (java.io.InputStream) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_BINARY).process(this, field, result);
        }
        return result;
    }

    @Override
    public String getStringValue(String fieldName) {
        String result = Casting.toString(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (String) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING).process(this, field, result);
        }
        return result;
    }

    @Override
    public Document getXMLValue(String fieldName) {
        Document result = Casting.toXML(getValueWithoutProcess(fieldName));
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Document) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_XML).process(this, field, result);
        }
        return result;
    }

    @Override
    public Node getNodeValue(String fieldName) {
        Node result = BridgeCaster.toNode(getValueWithoutProcess(fieldName), getCloud());
        NodeManager nodeManager = getNodeManager();
        if (nodeManager.hasField(fieldName)) { // gui(..) stuff could not work.
            Field field = nodeManager.getField(fieldName);
            result = (Node) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_NODE).process(this, field, result);
        }
        return result;
    }

    @Override
    public final FieldValue getFieldValue(String fieldName) throws NotFoundException {
        return new BasicFieldValue(this, getNodeManager().getField(fieldName));
    }

    @Override
    public final FieldValue getFieldValue(Field field) {
        return new BasicFieldValue(this, field);
    }

    @Override
    public final Element getXMLValue(String fieldName, Document tree) {
        Document doc = getXMLValue(fieldName);
        if (doc == null) {
            return null;
        }
        return (Element)tree.importNode(doc.getDocumentElement(), true);
    }



    @Override
    public Collection<String> validate() {
        List<String> errors = new ArrayList<String>();
        Locale locale = getCloud().getLocale();
        for (Field field : getNodeManager().getFields()) {
            // don't validate read-only (cannot be changed) or virtual fields (are not stored).
            // Specifically, the 'number' field must not be validated, because for new nodes it does not yet
            // point to an existing node...
	    // TODO: the number field should not be a NODE field
	    // TODO: possibly virtual fields DO need validation? How about temporary fields?
            if (! field.isReadOnly() && !field.isVirtual()) {
		// Only change a field if the enforcestrength of the restrictions is
		// applicable to the change.
                DataType dataType = field.getDataType();
                int enforceStrength = dataType.getEnforceStrength();
                if ((enforceStrength > DataType.ENFORCE_ONCHANGE) ||
		    (isChanged(field.getName()) && (enforceStrength >= DataType.ENFORCE_ONCREATE)) ||
	            (isNew() && (enforceStrength >= DataType.ENFORCE_NEVER))) {
                    Object value = getValueWithoutProcess(field.getName());
                    Collection<LocalizedString> fieldErrors = dataType.validate(value, this, field);
                    for (LocalizedString error : fieldErrors) {
                        errors.add("field '" + field.getName() + "' with value '" + value + "': " + // TODO need to i18n this intro too
                                   error.get(locale));
                    }
                }
            }
        }
        return errors;
    }

    @Override
    public final void delete() {
        delete(false);
    }

    @Override
    public final void deleteRelations() {
        deleteRelations("object");
    }

    @Override
    public final RelationList getRelations() {
        return getRelations(null, (String) null);
    }

    @Override
    public final RelationList getRelations(String role) {
        return getRelations(role, (String) null);
    }

    @Override
    public final RelationList getRelations(String role, NodeManager nodeManager) {
        if (nodeManager == null) {
            return getRelations(role);
        } else {
            return getRelations(role, nodeManager.getName());
        }
    }

    @Override
    public final int countRelations() {
        return countRelatedNodes(getCloud().getNodeManager("object"), null, "BOTH");
    }

    @Override
    public final int countRelations(String type) {
        //err
        return countRelatedNodes(getCloud().getNodeManager("object"), type, "BOTH");
    }

    @Override
    public final NodeList getRelatedNodes() {
        return getRelatedNodes("object", null, null);
    }

    @Override
    public final NodeList getRelatedNodes(String type) {
        return getRelatedNodes(type, null, null);
    }

    @Override
    public final NodeList getRelatedNodes(NodeManager nodeManager) {
        return getRelatedNodes(nodeManager, null, null);
    }

    @Override
    public final NodeList getRelatedNodes(String type, String role, String searchDir) {
        return getRelatedNodes(getCloud().getNodeManager(type), role, searchDir);
    }

    @Override
    public Relation createRelation(Node destinationNode, RelationManager relationManager) {
        Relation relation = relationManager.createRelation(this, destinationNode);
        return relation;
    }

    /**
     * Compares this node to the passed object.
     * Returns 0 if they are equal, -1 if the object passed is a NodeManager and larger than this manager,
     * and +1 if the object passed is a NodeManager and smaller than this manager.
     * This is used to sort Nodes.
     * A node is 'larger' than another node if its GUI() result is larger (alphabetically, case sensitive)
     * than that of the other node. If the GUI() results are the same, the nodes are compared on number,
     * and (if needed) on their owning clouds.
     *
     * @param o the object to compare it with
     * @return 0 if they are equal, -1 if the object passed is a NodeManager and larger than this manager,
     * and +1 if the object passed is a NodeManager and smaller than this manager.
     */
    @Override
    public final int compareTo(Node o) {
        Node n = o;
        String s1 = "";
        if (this instanceof NodeManager) {
            s1 = ((NodeManager)this).getGUIName();
        } else {
            s1 = getFunctionValue("gui", null).toString();
        }
        String s2 = "";
        if (n instanceof NodeManager) {
            s2 = ((NodeManager)n).getGUIName();
        } else {
            s2 = n.getFunctionValue("gui", null).toString();
        }
        Collator col = Collator.getInstance(getCloud().getLocale());
        col.setStrength(Collator.PRIMARY);
        int res = col.compare(s1, s2);
        if (res != 0) {
            return res;
        } else {
            res = s1.compareTo(s2);
            if (res != 0) return res;

            int n1 = getNumber();
            int n2 = n.getNumber();
            if (n2 > n1) {
                return -1;
            } else if (n2 < n1) {
                return -1;
            } else {
                Cloud c = getCloud();
                if (c instanceof Comparable) {
                    return ((Comparable<Cloud>) c).compareTo(n.getCloud());
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public boolean isChanged(String fieldName) {
        return false;
    }

    @Override
    public boolean isChanged() {
        return false;
    }
    @Override
    public Set<String> getChanged() {
        return Collections.emptySet();
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Cannot edit node of type " + getClass().getName() + " " + this);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void delete(boolean deleteRelations) {
        throw new UnsupportedOperationException("Cannot edit node of type " + getClass().getName() + " " + this);
    }

    @Override
    public void deleteRelations(String type) throws NotFoundException {
    }

    @Override
    public RelationList getRelations(String role, NodeManager nodeManager, String searchDir) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }
    @Override
    public RelationList getRelations(String role, String nodeManager) throws NotFoundException {
        return BridgeCollections.EMPTY_RELATIONLIST;
    }

    @Override
    public boolean hasRelations() {
        return false;
    }

    @Override
    public int countRelatedNodes(NodeManager otherNodeManager, String role, String direction) {
        return 0;
    }

    @Override
    public int countRelatedNodes(String type) {
        return 0;
    }

    @Override
    public NodeList getRelatedNodes(NodeManager nodeManager, String role, String searchDir) {
        return BridgeCollections.EMPTY_NODELIST;
    }

    @Override
    public StringList getAliases() {
        return BridgeCollections.EMPTY_STRINGLIST;
    }

    @Override
    public void createAlias(String aliasName) {
        throw new UnsupportedOperationException(this.getClass().getName() + "s have no aliases");
    }

    @Override
    public void deleteAlias(String aliasName) {
        throw new UnsupportedOperationException(this.getClass().getName() + "s  have no aliases");
    }

    @Override
    public void setContext(String context) {
        throw new UnsupportedOperationException(this.getClass().getName() + "s have no security context");
    }

    @Override
    public String getContext() {
        throw new UnsupportedOperationException(this.getClass().getName() + "s have no security context");
    }

    // javadoc inherited (from Node)
    @Override
    public StringList getPossibleContexts() {
        return BridgeCollections.EMPTY_STRINGLIST;
    }

    @Override
    public boolean mayWrite() {
        return false;
    }

    @Override
    public boolean mayDelete() {
        return false;
    }

    @Override
    public boolean mayChangeContext() {
        return false;
    }

    /**
     * Compares two nodes, and returns true if they are equal.
     * This effectively means that both objects are nodes, and they both have the same number and cloud
     * @param o the object to compare it with
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object o) {
        return (o instanceof Node) && getNumber() == ((Node)o).getNumber() && getCloud().equals(((Node)o).getCloud());
    }
    @Override
    public final int hashCode() {
        return 127 * getNumber();
    }


    @Override
    public Parameters createParameters(String functionName) {
        return getNodeFunction(functionName).createParameters();
    }

    protected FieldValue createFunctionValue(final Object result) {
        return new AbstractFieldValue(this, getCloud()) {
            @Override
            public Object get() {
                return result;
            }
        };
    }

    @Override
    public FieldValue getFunctionValue(String functionName, List<?> parameters) {
        Function function = getFunction(functionName);
        Parameters params = function.createParameters();
        // Always create a new parameters object, which makes it possible to use a Parameters object
        // not created with createParameters too.

        params.setAll(parameters);
        return createFunctionValue(function.getFunctionValue(params));
    }


    /**
     * Wraps {@link #getFunctions} in a Map
     *
     * @since MMBase-1.9.2
     */
    protected final Map<String, Function<?>>  getFunctionMap() {
        return new AbstractMap<String, Function<?>>() {
            @Override
            public Set<Map.Entry<String, Function<?>>> entrySet() {
                return new AbstractSet<Map.Entry<String, Function<?>>>() {
                    @Override
                    public int size() {
                        return AbstractNode.this.getFunctions().size();
                    }
                    @Override
                    public Iterator<Map.Entry<String, Function<?>>> iterator() {
                        final Iterator<Function<?>> i = AbstractNode.this.getFunctions().iterator();
                        return new Iterator<Map.Entry<String, Function<?>>>() {
                            @Override
                            public boolean hasNext() {
                                return i.hasNext();
                            }
                            @Override
                            public Map.Entry<String, Function<?>> next() {
                                Function<?> n = i.next();
                                return new org.mmbase.util.Entry<String, Function<?>>(n.getName(), n);
                            }
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                };
            }
        };
    }

    /**
     * This default implementation is based on {@link #getNodeManager}.{@link org.mmbase.bridge.NodeManager#getFunctions}.
     */
    @Override
    public Collection<Function<?>> getFunctions() {
        return AbstractNode.this.getNodeManager().getFunctions();
    }


    /**
     * Based on {@link getFunctions}.
     */
    protected Function<?> getNodeFunction(String functionName) {
        Function<?> fun = getFunctionMap().get(functionName);
        if (fun instanceof NodeFunction) {
            return fun;
        } else {
            return null;
        }
    }

    @Override
    public Function<?> getFunction(String functionName) {
        Function<?> function = getNodeFunction(functionName);
        if (function == null) {
            throw new NotFoundException("Function with name " + functionName + " does not exist on node " + getNumber() + " of type " + getNodeManager().getName() + "(known are " + getFunctionMap() + ")");
        }
        return new WrappedFunction(function) {
            @Override
            public final Object getFunctionValue(Parameters params) {
                if (params == null) params = createParameters();
                params.setIfDefined(Parameter.NODE, AbstractNode.this);
                params.setIfDefined(Parameter.CLOUD, AbstractNode.this.getCloud());
                return AbstractNode.this.createFunctionValue(super.getFunctionValue(params)).get();
            }
        };
    }

    @Override
    public void setNodeManager(NodeManager nm) {
        throw new UnsupportedOperationException();
    }

}
