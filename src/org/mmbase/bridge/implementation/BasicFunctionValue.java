/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.module.core.MMObjectNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.*;

/**
 * This implementation of the Field Value interface is used by getFunctionValue of Node. This
 * represents the result of a `function' on a node and it (therefore) is a unmodifiable.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: BasicFunctionValue.java,v 1.16 2005-10-12 09:28:26 michiel Exp $
 * @since   MMBase-1.6
 */
public class BasicFunctionValue implements FieldValue {

    static private BridgeException CANNOTCHANGE =  new BridgeException("Cannot change function value");

    private Node node = null;
    private Object value = null;
    private Cloud cloud = null;

    /**
     * Constructor for a function value returned by a Node.
     * @since MMBase-1.8
     * @param node the node that called the function
     * @param value the function value
     */
    BasicFunctionValue(Node node, Object value) {
        this(node.getCloud(), value);
        this.node  = node;
    }

    /**
     * Constructor for a function value returned by a Module or NodeManager.
     * @since MMBase-1.8
     * @param cloud the cloud under which the call was run, used to instantiate NodeList values
     * @param value the function value
     */
    BasicFunctionValue(Cloud cloud, Object value) {
        this.value = value;
        this.cloud = cloud;
        if (this.value instanceof List) { // might be a collection of MMObjectNodes
            List list  = (List) this.value;
            if (list.size() > 0) {
                if (list.get(0) instanceof MMObjectNode) { // if List of MMObjectNodes, make NodeList
                    if (cloud == null) {
                        throw new IllegalStateException("Cloud is unknown, cannot convert MMObjectNode to Node");
                    }
                    NodeList l = cloud.createNodeList();
                    this.value = l;
                    l.addAll(list);
                }
            }
        }
    }

    /**
     * Function values cannot be changed
     * @return false
     */
    public boolean canModify() {
        return false;
    }

    public boolean isNull() {
        return value == null;
    }

    public Object get() {
        return value;
    }

    public Object getField() {
        return null;
    }

    public Node getNode() {
        return node;
    }

    public boolean toBoolean() {
        return Casting.toBoolean(value);
    }

    public byte[] toByte() {
        return Casting.toByte(value);
    }

    public float toFloat() {
        return Casting.toFloat(value);
    }

    public double toDouble() {
        return Casting.toDouble(value);
    }

    public long toLong() {
        return Casting.toLong(value);
    }

    public int toInt() {
        return Casting.toInt(value);
    }

    public Node toNode() {
        if (cloud == null) {
            throw new IllegalStateException("Cloud is unknown, cannot convert MMObjectNode to Node");
        }
        if (value instanceof MMObjectNode) {
            return cloud.getNode(((MMObjectNode) value).getNumber());
        }
        return Casting.toNode(value, cloud);
    }

    public String toString() {
        return Casting.toString(value);
    }

    public Document toXML() throws IllegalArgumentException {
        return Casting.toXML(value);
    }

    public Element toXML(Document tree) throws IllegalArgumentException {
        Document doc = toXML();
        if(doc == null) return  null;
        return (Element) tree.importNode(doc.getDocumentElement(), true);
    }

    /**
     * @since MMBase-1.8
     */
    public Date toDate() {
        return Casting.toDate(value);
    }


    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void set(Object value) {
        throw CANNOTCHANGE;
    }


    public void setObject(Object value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setBoolean(boolean value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setFLoat(float value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setDouble(double value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setLong(long value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setInt(int value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setByte(byte[] value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setString(String value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setNode(Node value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    public void setXML(Document value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @throws BridgeException
     * @since MMBase-1.8
     */
    public void setDate(Date value) {
        throw CANNOTCHANGE;
    }

}
