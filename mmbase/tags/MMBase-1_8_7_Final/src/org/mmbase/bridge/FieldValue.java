/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * This interface represents a value stored in a node.
 *
 * @author Pierre van Rooden
 * @since MMBase 1.6
 * @version $Id$
 */
public interface FieldValue {

    /**
     * Returns whether this value can be changed.
     * Some field values (such as the values of the number, owner, and otype fields) cannot be changed
     * through this interface (but some may be changed through other means).
     *
     * @return  <code>true</code> if the value can be changed
     */
    public boolean canModify();


    public boolean isNull();

    /**
     * Returns the value as an Object.
     * The object type may vary and is dependent on how data was stored in a field.
     * I.e. It may be possible for an Integer field to return it's value as a String
     * if it was stored that way in the first place.
     *
     * @return  the field value as an object
     */
    public Object get();

    /**
     * Returns the Field object belonging to this value.
     *
     * @return  the field object
     */
    public Object getField();

    /**
     * Returns the Node to which this value belongs.
     *
     * @return  the Node object
     */
    public Node getNode();

    /**
     * Returns the value as an boolean (<code>true</code> or  <code>false</code>).
     * If the actual value is a Boolean object, this call returns it's (primitive) boolean value.
     * If the actual value is a Number object, this call returns <code>true</code>
     * if the value is a positive, non-zero, value. In other words, values '0'
     * and '-1' are concidered <code>false</code>.
     * If the value is a string, this call returns <code>true</code> if
     * the value is "true" or "yes" (case-insensitive).
     * In all other cases (including calling byte fields), <code>false</code>
     * is returned.
     *
     * @return  the field value as a boolean
     */
    public boolean toBoolean();

    /**
     * Returns the value as a byte array.
     * This function returns either the value of a byte field, or the byte value of a string
     * (converted using the default encoding, i.e. UTF8)
     * Other types of values return an empty byte-array.
     *
     * @return  the field value as a byte array
     */
    public byte[] toByte();

    /**
     * Returns the value as a float.
     * This function attempts to convert the value to a float.
     * Numeric fields are simply converted.
     * Boolean fields return 0.0 if false, and 1.0 if true.
     * String fields are parsed.
     * If a parsed string contains an error, ot the field value is not of a type that can be converted
     * (i.e. a byte array), this function returns -1.0.
     *
     * @return  the field value as a float
     */
    public float toFloat();

    /**
     * Returns the value as a double.
     * This function attempts to convert the value to a double.
     * Numeric fields are simply converted. Double may be truncated.
     * Boolean fields return 0.0 if false, and 1.0 if true.
     * String fields are parsed.
     * If a parsed string contains an error, ot the field value is not of a type that can be converted
     * (i.e. a byte array), this function returns -1.0.
     *
     * @return  the field value as a double
     */
    public double toDouble();

    /**
     * Returns the value as a long.
     * This function attempts to convert the value to a long.
     * Numeric fields are simply converted. Double and float values may be truncated.
     * Boolean fields return 0 if false, and 1 if true.
     * String fields are parsed.
     * If a parsed string contains an error, ot the field value is not of a type that can be converted
     * (i.e. a byte array), this function returns -1
     *
     * @return  the field value as a long.
     */
    public long toLong();

    /**
     * Returns the value as an int.
     * This function attempts to convert the value to an int.
     * Numeric fields are simply converted. Double and float values may be truncated.
     * For Node values, the numeric key is returned.
     * Long values return -1 of the value is too large.
     * Boolean fields return 0 if false, and 1 if true.
     * String fields are parsed.
     * If a parsed string contains an error, ot the field value is not of a type that can be converted
     * (i.e. a byte array), this function returns -1
     *
     * @return  the field value as an int.
     */
    public int toInt();

    /**
     * Returns the value as a Node.
     * This function attempts to retrieve the node represented by the value.
     * For numeric fields the node is retrieved using the numeric values as the node key.
     * String fields are used as Node aliases, withw hich to retrieve the Node.
     * If the node does not exist, or the value is of anotehr type, the function returns <code>null</code>.
     *
     * @return  the field value as a Node
     */
    public Node toNode();

    /**
     * Returns the value as a String.
     * Byte arrays are converted to string using the default encoding (UTF8).
     * Node values return a string representation of their numeric key.
     * DOM Documents are serialized to a proper strign represnattion fo the xml.
     * For other values the result is calling the toString() method on the actual object.
     *
     * @return  the field value as a String
     */
    public String toString();

    /**
     * Returns the value as a <code>org.w3c.dom.Document</code>
     * If the node value is not itself a Document, the method attempts to
     * attempts to convert the String value into an XML.
     * If the value cannot be converted, this method returns <code>null</code>
     *
     * @return  the field value as a Document
     * @throws  IllegalArgumentException if the Field is not of type TYPE_XML.
     */
    public org.w3c.dom.Document toXML() throws IllegalArgumentException;

    /**
     * Returns the value as a <code>java.util.Date</code>
     * If the value cannot be converted, this method returns <code>null</code>
     * @return the field value as Date
     * @since MMBase-1.8
     */
    public java.util.Date toDate();

    /**
     * Returns the value as a <code>org.w3c.dom.Element</code>
     * If the node value is not itself a Document, the method attempts to
     * attempts to convert the String value into an XML.
     * This method fails (throws a IllegalArgumentException) if the Field is not of type TYPE_XML.
     * If the value cannot be converted, this method returns <code>null</code>
     *
     * @param tree the DOM Document to which the Element is added
     *             (as the document root element)
     * @return  the field value as an Element
     * @throws  IllegalArgumentException if the Field is not of type TYPE_XML.
     */
    public org.w3c.dom.Element toXML(org.w3c.dom.Document tree) throws IllegalArgumentException;

    /**
     * Sets the value, passing any Object
     * The object type may vary and is generally stored in memory as-is, which means that,
     * generally, the get() method returns the same object.
     * Note that for an XML field String values are converted to a XML document, and individual builders
     * may make their own changes.
     * The object is converted to the actual type (using the getXXX() methods detailed above) once the node
     * is stored, though that does not affect the data in-memory until the Node is read anew from the storage.
     * Note that this behavior may change in the future and therefor code should not be dependent on this.
     * By preference, use the more specific methods for setting data (i.e. setString()).
     *
     * @see #get
     * @param value the field value as an Object
     */
    public void set(Object value);

    /*
     * Sets the value of the specified field using an object, but without dispatching to the right
     * type first.
     * @since MMBase-1.8
     */
    public void setObject(Object value);

    /**
     * Sets the value, passing a boolean value.
     * This value is converted to a Boolean object.
     *
     * @see #toBoolean
     * @param value the field value as a boolean
     */
    public void setBoolean(boolean value);

    /**
     * Sets the value, passing a float value.
     * This value is converted to a Float object.
     *
     * @see #toFloat
     * @param value the field value as a float
     */
    public void setFLoat(float value);

    /**
     * Sets the value, passing a double value.
     * This value is converted to a Double object.
     *
     * @see #toDouble
     * @param value the field value as a double
     */
    public void setDouble(double value);

    /**
     * Sets the value, passing a long value.
     * This value is converted to a Long object.
     *
     * @see #toLong
     * @param value the field value as a long
     */
    public void setLong(long value);

    /**
     * Sets the value, passing a int value.
     * This value is converted to a Integer object.
     *
     * @see #toInt
     * @param value the field value as a int
     */
    public void setInt(int value);

    /**
     * Sets the value, passing a byte array.
     *
     * @see #toByte
     * @param value the field value as a byte array
     */
    public void setByte(byte[] value);

    /**
     * Sets the value, passing a String.
     *
     * @see #toString
     * @param value the field value as a String
     */
    public void setString(String value);

    /**
     * Sets the value, passing a Node.
     *
     * @see #toNode
     * @param value the field value as a Node
     */
    public void setNode(Node value);

    /**
     * Sets the value, passing a org.w3c.dom.Document object.
     *
     * @see #toXML(org.w3c.dom.Document)
     * @param value the field value as a XML Document
     */
    public void setXML(org.w3c.dom.Document value);

    /**
     * Sets the value, passing a java.util.Date object.
     * @see #toDate
     * @param value the field value as a java.util.Date Document
     * @since MMBase-1.8
     */
    public void setDate(java.util.Date value);

}
