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

    public boolean isNull() {
        return node.isNull(field.getName());
    }

    public Object get() {
        return node.getValue(field.getName());
    }

    public Object getField() {
        return field;
    }

    public Node getNode() {
        return node;
    }

    public boolean toBoolean() {
        return node.getBooleanValue(field.getName());
    }

    public byte[] toByte() {
        return node.getByteValue(field.getName());
    }

    public float toFloat() {
        return node.getFloatValue(field.getName());
    }

    public double toDouble() {
        return node.getDoubleValue(field.getName());
    }

    public long toLong() {
        return node.getLongValue(field.getName());
    }

    public int toInt() {
        return node.getIntValue(field.getName());
    }

    public Node toNode() {
        return node.getNodeValue(field.getName());
    }

    public String toString() {
        return node.getStringValue(field.getName());
    }

    public org.w3c.dom.Document toXML() throws IllegalArgumentException {
        return node.getXMLValue(field.getName());
    }

    public org.w3c.dom.Element toXML(org.w3c.dom.Document tree) throws IllegalArgumentException {
        return node.getXMLValue(field.getName(), tree);
    }

    public java.util.Date toDate() {
        return node.getDateValue(field.getName());
    }

    public void set(Object value) {
        node.setValue(field.getName(), value);
    }

    public void setObject(Object value) {
        node.setObjectValue(field.getName(), value);
    }

    public void setBoolean(boolean value) {
        node.setBooleanValue(field.getName(),value);
    }

    public void setFLoat(float value) {
        node.setFloatValue(field.getName(),value);
    }

    public void setDouble(double value) {
        node.setDoubleValue(field.getName(),value);
    }

    public void setLong(long value) {
        node.setLongValue(field.getName(),value);
    }

    public void setInt(int value) {
        node.setIntValue(field.getName(),value);
    }

    public void setByte(byte[] value) {
        node.setByteValue(field.getName(),value);
    }

    public void setString(String value) {
        node.setStringValue(field.getName(),value);
    }

    public void setNode(Node value) {
        node.setNodeValue(field.getName(),value);
    }

    public void setXML(org.w3c.dom.Document value) {
        node.setXMLValue(field.getName(),value);
    }


    public void setDate(java.util.Date value) {
        node.setDateValue(field.getName(), value);
    }

}
