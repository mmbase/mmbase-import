/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.*;

/**
 * This implementation of the Field Value interface is used by getFunctionValue of Node. This
 * represents the result of a `function' on a node and it (therefore) is an unmodifiable.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */
public abstract class AbstractFieldValue implements FieldValue {

    static protected BridgeException CANNOTCHANGE =  new BridgeException("Cannot change function value");

    protected final Node node;
    protected final Cloud cloud;

    protected AbstractFieldValue(Node n, Cloud c) {
        node = n;
        cloud = c != null ? c : (n != null ? n.getCloud() : null);
    }

    /**
     * Function values cannot be changed
     * @return false
     */
    public boolean canModify() {
        return false;
    }

    public boolean isNull() {
        return get() == null;
    }

    public abstract Object get();

    public Object getField() {
        return null;
    }

    public Node getNode() {
        return node;
    }

    public boolean toBoolean() {
        return Casting.toBoolean(get());
    }

    public byte[] toByte() {
        return Casting.toByte(get());
    }

    public float toFloat() {
        return Casting.toFloat(get());
    }

    public double toDouble() {
        return Casting.toDouble(get());
    }

    public long toLong() {
        return Casting.toLong(get());
    }

    public int toInt() {
        return Casting.toInt(get());
    }

    public Node toNode() {
        return Casting.toNode(get(), cloud);
    }

    public String toString() {
        return Casting.toString(get());
    }

    public Document toXML() throws IllegalArgumentException {
        return Casting.toXML(get());
    }

    public final Element toXML(Document tree) throws IllegalArgumentException {
        Document doc = toXML();
        if(doc == null) return  null;
        return (Element) tree.importNode(doc.getDocumentElement(), true);
    }

    /**
     * @since MMBase-1.8
     */
    public Date toDate() {
        return Casting.toDate(get());
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
