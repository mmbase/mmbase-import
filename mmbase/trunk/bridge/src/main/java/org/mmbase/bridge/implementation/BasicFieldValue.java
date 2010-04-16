/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;

/**
 * This is the basic implementation of the Field Value interface.
 *
 * @author   Pierre van Rooden
 * @version $Id$
 * @since    MMBase-1.6
 */
public class BasicFieldValue implements FieldValue {

    private Node node = null;
    private Field field = null;

    public BasicFieldValue (Node node, Field field) {
        this.node = node;
        this.field = field;
    }

    @Override
    public boolean canModify() {
        // rather simple... should maybe ask Node
        return (field!=null) ||
               "number".equals(field.getName()) ||
               "otype".equals(field.getName()) ||
               "owner".equals(field.getName()) ||
               "snumber".equals(field.getName()) ||
               "dnumber".equals(field.getName()) ||
               "rnumber".equals(field.getName());
    }

    @Override
    public boolean isNull() {
        return node.isNull(field.getName());
    }

    @Override
    public Object get() {
        return node.getValue(field.getName());
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public boolean toBoolean() {
        return node.getBooleanValue(field.getName());
    }

    @Override
    public byte[] toByte() {
        return node.getByteValue(field.getName());
    }

    @Override
    public float toFloat() {
        return node.getFloatValue(field.getName());
    }

    @Override
    public double toDouble() {
        return node.getDoubleValue(field.getName());
    }

    @Override
    public long toLong() {
        return node.getLongValue(field.getName());
    }

    @Override
    public int toInt() {
        return node.getIntValue(field.getName());
    }

    @Override
    public Node toNode() {
        return node.getNodeValue(field.getName());
    }

    @Override
    public String toString() {
        return node.getStringValue(field.getName());
    }

    @Override
    public org.w3c.dom.Document toXML() throws IllegalArgumentException {
        return node.getXMLValue(field.getName());
    }

    @Override
    public org.w3c.dom.Element toXML(org.w3c.dom.Document tree) throws IllegalArgumentException {
        return node.getXMLValue(field.getName(), tree);
    }

    @Override
    public java.util.Date toDate() {
        return node.getDateValue(field.getName());
    }

    @Override
    public void set(Object value) {
        node.setValue(field.getName(), value);
    }

    @Override
    public void setObject(Object value) {
        node.setObjectValue(field.getName(), value);
    }

    @Override
    public void setBoolean(boolean value) {
        node.setBooleanValue(field.getName(),value);
    }

    @Override
    public void setFLoat(float value) {
        node.setFloatValue(field.getName(),value);
    }

    @Override
    public void setDouble(double value) {
        node.setDoubleValue(field.getName(),value);
    }

    @Override
    public void setLong(long value) {
        node.setLongValue(field.getName(),value);
    }

    @Override
    public void setInt(int value) {
        node.setIntValue(field.getName(),value);
    }

    @Override
    public void setByte(byte[] value) {
        node.setByteValue(field.getName(),value);
    }

    @Override
    public void setString(String value) {
        node.setStringValue(field.getName(),value);
    }

    @Override
    public void setNode(Node value) {
        node.setNodeValue(field.getName(),value);
    }

    @Override
    public void setXML(org.w3c.dom.Document value) {
        node.setXMLValue(field.getName(),value);
    }


    @Override
    public void setDate(java.util.Date value) {
        node.setDateValue(field.getName(), value);
    }

}
