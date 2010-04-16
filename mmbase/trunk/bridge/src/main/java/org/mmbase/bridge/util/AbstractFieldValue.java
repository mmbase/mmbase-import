/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.BridgeCaster;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.*;

/**
 * This abstract version of {@link FieldValue} only leaves {@link #get()} to implement. Many other
 * methods ('to&lt;Some type&gt;') are implemented by wrapping methods of {@link Casting}.
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
    @Override
    public boolean canModify() {
        return false;
    }

    @Override
    public boolean isNull() {
        return get() == null;
    }

    @Override
    public abstract Object get();

    @Override
    public Field getField() {
        return null;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public boolean toBoolean() {
        return Casting.toBoolean(get());
    }

    @Override
    public byte[] toByte() {
        return Casting.toByte(get());
    }

    @Override
    public float toFloat() {
        return Casting.toFloat(get());
    }

    @Override
    public double toDouble() {
        return Casting.toDouble(get());
    }

    @Override
    public long toLong() {
        return Casting.toLong(get());
    }

    @Override
    public int toInt() {
        return Casting.toInt(get());
    }

    @Override
    public Node toNode() {
        return BridgeCaster.toNode(get(), cloud);
    }

    @Override
    public String toString() {
        return Casting.toString(get());
    }

    @Override
    public Document toXML() throws IllegalArgumentException {
        return Casting.toXML(get());
    }

    @Override
    public final Element toXML(Document tree) throws IllegalArgumentException {
        Document doc = toXML();
        if(doc == null) return  null;
        return (Element) tree.importNode(doc.getDocumentElement(), true);
    }

    /**
     * @since MMBase-1.8
     */
    @Override
    public Date toDate() {
        return Casting.toDate(get());
    }


    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void set(Object value) {
        throw CANNOTCHANGE;
    }


    @Override
    public void setObject(Object value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setBoolean(boolean value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setFLoat(float value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setDouble(double value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setLong(long value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setInt(int value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setByte(byte[] value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setString(String value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setNode(Node value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @param value set value
     * @throws BridgeException
     */
    @Override
    public void setXML(Document value) {
        throw CANNOTCHANGE;
    }

    /**
     * Function values cannot be changed, and all set-functions throw an exception.
     * @throws BridgeException
     * @since MMBase-1.8
     */
    @Override
    public void setDate(Date value) {
        throw CANNOTCHANGE;
    }

}
