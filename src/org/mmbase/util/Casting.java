/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

/**
 * Collects MMBase specific 'cast' information, as static to... functions. This is used (and used to
 * be implemented) in MMObjectNode. But this functionality is more generic to MMbase.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id$
 */

import java.util.*;
import java.text.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.NodeWrapper;
import org.mmbase.bridge.util.NodeMap;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.XMLWriter;

import org.w3c.dom.*;

public class Casting {

    private static final Logger log = Logging.getLoggerInstance(Casting.class);


    /**
     * A Date formatter that creates a date based on a ISO 8601 date and a ISO 8601 time.
     * I.e. 2004-12-01 14:30:00.
     * It is NOT 100% ISO 8601, as opposed to {@link #ISO_8601_UTC}, as the standard actually requires
     * a 'T' to be placed between the date and the time.
     * The date given is the date for the local (server) time. Use this formatter if you want to display
     * user-friendly dates in local time.

     * XXX: According to http://en.wikipedia.org/wiki/ISO_8601, the standard allows ' ' in stead of
     * 'T' if no misunderstanding arises, which is the case here. So I don't think this is 'loose'.
     */
    public final static DateFormat ISO_8601_LOOSE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * A Date formatter that creates a ISO 8601 datetime according to UTC/GMT.
     * I.e. 2004-12-01T14:30:00Z.
     * This is 100% ISO 8601, as opposed to {@link #ISO_8601_LOOSE}.
     * Use this formatter if you want to export dates.
     *
     * XXX: Hmm, we parse with UTC now, while we don't store them as such.
     */
    public final static DateFormat ISO_8601_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    static {
        try {
            ISO_8601_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
        }
    }

    public final static DateFormat ISO_8601_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public final static DateFormat ISO_8601_TIME = new SimpleDateFormat("HH:mm:ss", Locale.US);




    /**
     * Returns whether the passed object is of the given class.
     * Unlike Class instanceof this also includes Object Types that
     * are representative for primitive types (i.e. Integer for int).
     * @param type the type (class) to check
     * @param value the value whose type to check
     * @return <code>true</code> if compatible
     * @since MMBase-1.8
     */
    public static boolean isType(Class type, Object value) {
        if (type.isPrimitive()) {
            return (type.equals(Boolean.TYPE) && value instanceof Boolean) ||
                   (type.equals(Byte.TYPE) && value instanceof Byte) ||
                   (type.equals(Character.TYPE) && value instanceof Character) ||
                   (type.equals(Short.TYPE) && value instanceof Short) ||
                   (type.equals(Integer.TYPE) && value instanceof Integer) ||
                   (type.equals(Long.TYPE) && value instanceof Long) ||
                   (type.equals(Float.TYPE) && value instanceof Float) ||
                   (type.equals(Double.TYPE) && value instanceof Double);
        } else {
            return value == null || type.isInstance(value);
        }
    }

    /**
     * Tries to 'cast' an object for use with the provided class. E.g. if value is a String, but the
     * type passed is Integer, then the string is act to an Integer.
     * If the type passed is a primitive type, the object is cast to an Object Types that is representative
     * for that type (i.e. Integer for int).
     * @param type the type (class)
     * @param value The value to be converted
     * @return value the converted value
     * @since MMBase-1.8
     */
    public static Object toType(Class type, Object value) {
        return toType(type, null, value);
    }

    private static Cloud anonymousCloud = null;

    /**
     * Tries to 'cast' an object for use with the provided class. E.g. if value is a String, but the
     * type passed is Integer, then the string is act to an Integer.
     * If the type passed is a primitive type, the object is cast to an Object Types that is representative
     * for that type (i.e. Integer for int).
     * @param type the type (class)
     * @param cloud When casting to Node, a cloud may be needed. May be <code>null</code>, for an anonymous cloud to be tried.
     * @param value The value to be converted
     * @return value the converted value
     * @since MMBase-1.8
     */
    public static Object toType(Class type, Cloud cloud, Object value) {
        if (value != null && isType(type, value))  {
            return value;
        } else {
            if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
                return Boolean.valueOf(toBoolean(value));
            } else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
                return new Byte(toInteger(value).byteValue());
            } else if (type.equals(Character.TYPE) || type.equals(Character.class)) {
                String chars = toString(value);
                if (chars.length() > 0) {
                    return new Character(chars.charAt(0));
                } else {
                    return new Character(Character.MIN_VALUE);
                }
            } else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
                return new Short(toInteger(value).shortValue());
            } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
                return toInteger(value);
            } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
                return new Long(toLong(value));
            } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
                return new Float(toFloat(value));
            } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
                return new Double(toDouble(value));
            } else if (type.equals(Number.class)) {
                Number res;
                try {
                    res = new Long("" + value);
                } catch (NumberFormatException nfe) {
                    try {
                        res = new Double("" + value);
                    } catch (NumberFormatException nfe1) {
                        res = new Integer(-1);
                    }
                }
                return res;
            } else if (type.equals(byte[].class)) {
                return toByte(value);
            } else if (type.equals(String.class)) {
                return toString(value);
            } else if (type.equals(Date.class)) {
                return toDate(value);
            } else if (type.equals(Node.class)) {
                try {
                    if (cloud == null) {
                        if (anonymousCloud == null) anonymousCloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                        cloud = anonymousCloud;
                    }
                    return toNode(value, cloud);
                } catch (Exception e) {
                    // suppose that that was because mmbase not running
                    return value instanceof Node ? value : null;
                }
            } else if (type.equals(Document.class)) {
                return toXML(value);
            } else if (type.equals(List.class)) {
                return toList(value);
            } else if (type.equals(Map.class)) {
                return toMap(value);
            } else if (type.equals(Collection.class)) {
                return toCollection(value);
            } else {
                if (value == null || "".equals(value)) {
                    // just to avoid the error
                    return null;
                }
                log.error("Dont now how to convert '" + value.getClass() + " to " + type);
                if (log.isDebugEnabled()) {
                    log.debug("cause: ", new Exception());
                }
                // don't know
                return value;
            }
        }
    }

    /**
     * Whether or not Casting can more or less reliably cast a certain type to String and back.
     * For collection types also the entries of the collection must be string representable.
     * @since MMBase-1.8
     */
    public static boolean isStringRepresentable(Class type) {
        return
            CharSequence.class.isAssignableFrom(type) ||
            Number.class.isAssignableFrom(type) ||
            Boolean.TYPE.isAssignableFrom(type) ||
            Boolean.class.isAssignableFrom(type) ||
            Character.class.isAssignableFrom(type) ||
            Node.class.isAssignableFrom(type) ||
            Document.class.isAssignableFrom(type) ||
            Collection.class.isAssignableFrom(type) ||
            Map.class.isAssignableFrom(type);
    }

    /**
     * Convert an object to a String.
     * 'null' is converted to an empty string.
     * @param o the object to convert
     * @return the converted value as a <code>String</code>
     */
    public static String toString(Object o) {
        if (o instanceof String) {
            return (String)o;
        }
        if (o == null || "".equals(o)) {
            return "";
        }

        return toStringBuffer(new StringBuffer(), o).toString();
    }

    /**
     * Convert an object to a string, using a StringBuffer.
     * @param buffer The StringBuffer with which to create the string
     * @param o the object to convert
     * @return the StringBuffer used for conversion (same as the buffer parameter)
     * @since MMBase-1.7
     */
    public static StringBuffer toStringBuffer(StringBuffer buffer, Object o) {
        if (o == null) {
            return buffer;
        }
        try {
            toWriter(new StringBufferWriter(buffer), o);
        } catch (java.io.IOException e) {}
        return buffer;
    }

    /**
     * Convert an object to a string, using a Writer.
     * @param writer The Writer with which to create (write) the string
     * @param o the object to convert
     * @return the Writer used for conversion (same as the writer parameter)
     * @since MMBase-1.7
     */
    public static Writer toWriter(Writer writer, Object o) throws java.io.IOException {
        if (o instanceof Writer) {
            return writer;
        }
        Object s = wrap(o, null);
        writer.write(s.toString());
        return writer;
    }

    /**
     * Wraps it in an object with a toString as we desire. Casting can now be done with
     * toString() on the resulting object.
     *
     * This is used to make JSTL en EL behave similarly as mmbase taglib when writing objects to the
     * page (taglib calls Casting, but they of course don't).
     *
     * @todo  Not everything is wrapped (and can be unwrapped) already.
     * @param o        The object to be wrapped
     * @param escaper  <code>null</code> or a CharTransformer to pipe the strings through
     * @since MMBase-1.8
     */

    public static Object wrap(final Object o, final CharTransformer escaper) {
        if (o == null) {
            return escape(escaper, "");
        } else if (o instanceof Node) {
            return new NodeMap((Node)o) {
                    public Object getValue(String fieldName) {
                        NodeManager nm = getNodeManager();
                        if (nm.hasField(fieldName)) {
                            switch(nm.getField(fieldName).getType()) {
                            case org.mmbase.bridge.Field.TYPE_NODE:
                                // I don't understand why, but the 'number' field is of type NODE,
                                // which makes no sense whatsoever.
                                if (! "number".equals(fieldName)) {
                                    return wrap(getNodeValue(fieldName), escaper);
                                } else {
                                    return super.getStringValue(fieldName);
                                }
                            case org.mmbase.bridge.Field.TYPE_DATETIME: return wrap(getDateValue(fieldName), escaper);
                            case org.mmbase.bridge.Field.TYPE_XML:      return wrap(getXMLValue(fieldName), escaper);
                            case org.mmbase.bridge.Field.TYPE_UNKNOWN:  return wrap(super.getValue(fieldName), escaper);
                            default: return escape(escaper, super.getStringValue(fieldName));
                            }
                        } else {
                            return escape(escaper, super.getStringValue(fieldName));
                        }
                    }
                    public String toString() {
                        return escape(escaper, "" + node.getNumber());
                    }
                };
        } else if (o instanceof Date) {
            return new java.util.Date(((Date)o).getTime()) {
                    private static final long serialVersionUID = 1L; // increase this if object chages.
                    public String toString() {
                        long time = getTime();
                        return time == -1 ? ("" + time) : ("" + time / 1000);
                    }
                };
        } else if (o instanceof org.w3c.dom.Node) {
            // don't know how to wrap
            return escape(escaper, XMLWriter.write((org.w3c.dom.Node) o, false, true));
        } else if (o instanceof List) {
            return new ListWrapper((List) o, escaper);
        } else if (o instanceof byte[]) {
            return escape(escaper, new String((byte[])o));
        } else if (o instanceof String) {
            // sad this is that this cannont be unwrapped because string cannot be reimplemnented or extended.
            // But many methods, want a String, not a CharSequence.
            // 22.
            return escape(escaper, (String) o);
        } else if (o instanceof CharSequence) {
            return new StringWrapper((CharSequence) o, escaper);
        } else {
            return o;
        }


    }

    private static String escape(CharTransformer escaper, CharSequence string) {
        if (escaper != null) {
            return escaper.transform(string.toString());
        } else {
            return string.toString();
        }
    }
    /**
     * When you want to undo the wrapping, this method can be used.
     * @since MMBase-1.8
     */
    public static Object unWrap(final Object o) {
        if (o instanceof NodeWrapper) {
            return ((NodeWrapper)o).getNode();
        } else if (o instanceof ListWrapper) {
            return ((ListWrapper)o).getList();
        } else if (o instanceof StringWrapper) {
            return ((StringWrapper)o).getString();
        } else {
            return o;
        }
    }

    /**
     * Convert an object to a List.
     * A String is split up (as if it was a comma-separated String).
     * Individual objects are wrapped and returned as Lists with one item.
     * <code>null</code> and the empty string are  returned as an empty list.
     * @param o the object to convert
     * @return the converted value as a <code>List</code>
     * @since MMBase-1.7
     */
    public static List toList(Object o) {
        return toList(o, ",");
    }

    /**
     * As {@link #toList(Object)} but with one extra argument.
     *
     * @param delimiter Regexp to use when splitting up the string if the object is a String. <code>null</code> or the empty string mean the default, which is a comma.
     * @since MMBase-1.8
     */
    public static List toList(Object o, String delimiter) {
        if (o instanceof List) {
            return (List)o;
        } else if (o instanceof Collection) {
            return new ArrayList((Collection) o);
        } else if (o instanceof String) {
            if ("".equals(delimiter) || delimiter == null) delimiter = ",";
            return StringSplitter.split((String)o, delimiter);
        } else if (o instanceof Map) {
            return new ArrayList(((Map)o).entrySet());
        } else {
            List l = new ArrayList();
            if (o != null) {
                l.add(o);
            }
            return l;
        }
    }


    /**
     * @since MMBase-1.8
     */
    public static Map toMap(Object o) {
        if (o instanceof Map) {
            return (Map) o;
        } else if (o instanceof org.mmbase.util.functions.Parameters) {
            return ((org.mmbase.util.functions.Parameters) o).toMap();
        } else if (o instanceof Collection) {
            Map result = new HashMap();
            Iterator i = ((Collection)o).iterator();
            while (i.hasNext()) {
                Object n = i.next();
                if (n instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry) n;
                    result.put(entry.getKey(), entry.getValue());
                } else {
                    result.put(n, n);
                }
            }
            return result;
        } else if (o instanceof Node) {
            return new NodeMap((Node)o);
        } else {
            Map m = new HashMap();
            m.put(o, o);
            return m;
        }
    }


    /**
     * Transforms an object to a collection. If the object is a collection already, then nothing
     * happens. If it is a Map, then the 'entry set' is returned. A string is interpreted as a
     * comma-separated list of strings. Other objects are wrapped in an ArrayList with one element.
     *
     * @since MMBase-1.8.5
     */
    public static Collection toCollection(Object o, String delimiter) {
        if (o instanceof Collection) {
            return (Collection)o;
        } else if (o instanceof Map) {
            return ((Map)o).entrySet();
        } else if (o instanceof String) {
            if ("".equals(delimiter) || delimiter == null) delimiter = ",";
            return StringSplitter.split((String)o, delimiter);
        } else if (o instanceof Object[]) {
            return Arrays.asList((Object[]) o);
        } else {
            return Collections.singletonList(o);
        }
    }
    /**
     * @since MMBase-1.8
     */
    public static Collection toCollection(Object o) {
        return toCollection(o, ",");
    }

    /**
     * Convert the value to a <code>Document</code> object.
     * If the value is not itself a Document, the method attempts to
     * attempts to convert the String value into an XML.
     * A <code>null</code> value is returned as <code>null</code>.
     * If the value cannot be converted, this method throws an IllegalArgumentException.
     * @param o the object to be converted to an XML document
     * @return  the value as a DOM Element or <code>null</code>
     * @throws  IllegalArgumentException if the value could not be converted
     * @since MMBase-1.6
     */
    static public Document toXML(Object o) {
        if (o == null) return null;
        if (!(o instanceof Document)) {
            //do conversion from String to Document...
            // This is a laborous action, so we log it on debug.
            // It will happen often if the nodes are not cached and so on.
            String xmltext = toString(o);
            if (log.isDebugEnabled()) {
                String msg = xmltext;
                if (msg.length() > 84) {
                    msg = msg.substring(0, 80) + "...";
                }
                log.debug("Object '" + msg + "' is not a Document, but a " + o.getClass().getName() + "");
            }
            return convertStringToXML(xmltext);
        }
        return (Document)o;
    }

    /**
     * Convert an object to a byte array.
     * @param obj The object to be converted
     * @return the value as an <code>byte[]</code> (binary/blob field)
     */
    static public byte[] toByte(Object obj) {
        if (obj == null) {
            return new byte[] {};
        } else if (obj instanceof byte[]) {
            // was allready unmapped so return the value
            return (byte[])obj;
        } else if (obj instanceof org.apache.commons.fileupload.FileItem) {
            return ((org.apache.commons.fileupload.FileItem) obj).get();
        } else {
            return toString(obj).getBytes();
        }
    }

    static public InputStream toInputStream(Object obj) {
        if (obj instanceof InputStream) {
            return (InputStream) obj;
        } else {
            return new ByteArrayInputStream(toByte(obj));
        }
    }


    /**
     * Convert an object to an Node.
     * If the value is Numeric, the method
     * tries to obtrain the object with that number.
     * If it is a String, the method tries to obtain the object with
     * that alias.
     * All remaining situations return <code>null</code>.
     * @param i the object to convert
     * @param cloud the Cloud to use for loading a node
     * @return the value as a <code>Node</code>
     * @since MMBase-1.7
     */
    public static Node toNode(Object i, Cloud cloud) {
        if (log.isDebugEnabled()) {
            log.debug("Convering to node " + i + " using " + cloud);
        }
        Node res = null;
        if (i instanceof Node) {
            res = (Node)i;
        } else if (i instanceof Number) {
            int nodenumber = ((Number)i).intValue();
            if (nodenumber != -1) {
                res = cloud.getNode(nodenumber);
            }
        } else if (i instanceof Map) {
            res = new MapNode((Map)i, cloud);
        } else if (i != null && !i.equals("")) {
            res = cloud.getNode(i.toString());
        }
        return res;
    }

    /**
     * Convert an object to an <code>int</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * If a value is an Node, it's number field is returned.
     * All remaining values return the provided default value.
     * @param i the object to convert
     * @param def the default value if conversion is impossible
     * @return the converted value as an <code>int</code>
     * @since MMBase-1.7
     */
    static public int toInt(Object i, int def) {
        int res = def;
        if (i instanceof Node) {
            res = ((Node)i).getNumber();
        } else if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Date) {
            long timeValue = ((Date)i).getTime();
            if (timeValue !=-1) timeValue = timeValue / 1000;
            if (timeValue > Integer.MAX_VALUE) {
                timeValue = Integer.MAX_VALUE;
            }
            if (timeValue < Integer.MIN_VALUE) {
                timeValue = Integer.MIN_VALUE;
            }
            res = (int) timeValue;
        } else if (i instanceof Number) {
            long l = ((Number)i).longValue();
            if (l > Integer.MAX_VALUE) {
                res = Integer.MAX_VALUE;
            } else if (l < Integer.MIN_VALUE) {
                res = Integer.MIN_VALUE;
            } else {
                res = (int) l;
            }
        } else if (i != null) {
            try {
                res = Integer.parseInt("" + i);
            } catch (NumberFormatException e) {
                // not an integer? perhaps it is a fload or double represented as String.
                try {
                    res = Double.valueOf("" + i).intValue();
                } catch (NumberFormatException ex) {

                    // try if the value is a string representing a boolean.
                    if(i instanceof String){
                        if(((String)i).toLowerCase().equals(("true"))){
                            res = 1;
                        }else if(((String)i).toLowerCase().equals(("false"))){
                            res = 0;
                        }
                    }
                }
            }
        }
        return res;
    }

    /**
     * Convert an object to an <code>int</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * If a value is a Node, it's number field is returned.
     * All remaining values return -1.
     * @param i the object to convert
     * @return the converted value as an <code>int</code>
     */
    static public int toInt(Object i) {
        return toInt(i, -1);
    }



    /**
     * Convert an object to a <code>boolean</code>.
     * If the value is numeric, this call returns <code>true</code>
     * if the value is a positive, non-zero, value. In other words, values '0'
     * and '-1' are considered <code>false</code>.
     * If the value is a string, this call returns <code>true</code> if
     * the value is "true" or "yes" (case-insensitive).
     * In all other cases (including calling byte fields), <code>false</code>
     * is returned.
     * @param b the object to convert
     * @return the converted value as a <code>boolean</code>
     */
    static public boolean toBoolean(Object b) {
        if (b == null) {
            return false;
        } else if (b instanceof Boolean) {
            return ((Boolean)b).booleanValue();
        } else if (b instanceof Number) {
            return ((Number)b).doubleValue() > 0;
        } else if (b instanceof Node) {
            return true; // return true if a NODE is filled
        } else if (b instanceof Date) {
            return ((Date)b).getTime() != -1;
        } else if (b instanceof Document) {
            return false; // undefined
        } else if (b instanceof String) {
            // note: we don't use Boolean.valueOf() because that only captures
            // the value "true"
            String s = ((String)b).toLowerCase();
            return s.equals("true") || s.equals("yes") || s.equals("1");
        } else {
            return false;
        }
    }

    /**
     * Convert an object to an Integer.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return -1.
     * @param i the object to convert
     * @return the converted value as a <code>Integer</code>
     */
    static public Integer toInteger(Object i) {
        if (i instanceof Integer) {
            return (Integer)i;
        } else {
            return new Integer(toInt(i));
        }
    }

    /**
     * Convert an object to a <code>long</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return the provided default value.
     * @param i the object to convert
     * @param def the default value if conversion is impossible
     * @return the converted value as a <code>long</code>
     * @since MMBase-1.7
     */
    static public long toLong(Object i, long def) {
        long res = def;
        if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).longValue();
        } else if (i instanceof Date) {
            res = ((Date)i).getTime();
            if (res !=- 1) res /= 1000;
        } else if (i instanceof Node) {
            res = ((Node)i).getNumber();
        } else if (i != null) {
            if(i instanceof String){
                if(((String)i).toLowerCase().equals("true")) return 1;
                if(((String)i).toLowerCase().equals("false")) return 0;
            }
            try {
                res = Long.parseLong("" + i);
            } catch (NumberFormatException e) {
                // not an integer? perhaps it is a float or double represented as String.
                try {
                    res = Double.valueOf("" + i).longValue();
                } catch (NumberFormatException ex) {
                    // give up, fall back to default.
                }
            }
        }
        return res;
    }

    /**
     * Convert an object to a <code>long</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return -1.
     * @param i the object to convert
     * @return the converted value as a <code>long</code>
     * @since MMBase-1.7
     */
    static public long toLong(Object i) {
        return toLong(i, -1);
    }

    /**
     * Convert an object to an <code>float</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return the default value.
     * @param i the object to convert
     * @param def the default value if conversion is impossible
     * @return the converted value as a <code>float</code>
     */
    static public float toFloat(Object i, float def) {
        float res = def;
        if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).floatValue();
        } else if (i instanceof Date) {
            res = ((Date)i).getTime();
            if (res!=-1) res = res / 1000;
        } else if (i instanceof Node) {
            res = ((Node)i).getNumber();
        } else if (i != null) {
            if(i instanceof String){
                if(((String)i).toLowerCase().equals("true")) {
                    res = 1;
                } else if(((String)i).toLowerCase().equals("false")) {
                    res = 0;
                }
            }
            try {
                res = Float.parseFloat("" + i);
            } catch (NumberFormatException e) {
//              try if the value is a string representing a boolean.
                if(i instanceof String){
                    if(((String)i).toLowerCase().equals(("true"))){
                        res = 1;
                    } else if(((String)i).toLowerCase().equals(("false"))){
                        res = 0;
                    }
                }
            }
        }
        return res;
    }

    /**
     * Convert an object to an <code>float</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return -1.
     * @param i the object to convert
     * @return the converted value as a <code>float</code>
     */
    static public float toFloat(Object i) {
        return toFloat(i, -1);
    }

    /**
     * Convert an object to an <code>double</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return the default value.
     * @param i the object to convert
     * @param def the default value if conversion is impossible
     * @return the converted value as a <code>double</code>
     */
    static public double toDouble(Object i, double def) {
        double res = def;
        if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).doubleValue();
        } else if (i instanceof Date) {
            res = ((Date)i).getTime();
            if (res != -1) res = res / 1000;
        } else if (i instanceof Node) {
            res = ((Node)i).getNumber();
        } else if (i != null) {
            try {
                res = Double.parseDouble("" + i);
            } catch (NumberFormatException e) {
//              try if the value is a string representing a boolean.
                if(i instanceof String){
                    if(((String)i).toLowerCase().equals(("true"))){
                        res = 1;
                    }else if(((String)i).toLowerCase().equals(("false"))){
                        res = 0;
                    }
                }
            }
        }
        return res;
    }

    /**
     * Convert an object to an <code>double</code>.
     * Boolean values return 0 for false, 1 for true.
     * String values are parsed to a number, if possible.
     * All remaining values return -1.
     * @param i the object to convert
     * @return the converted value as a <code>double</code>
     */
    static public double toDouble(Object i) {
        return toDouble(i, -1);
    }



    /**
     * Convert an object to a <code>Date</code>.
     * String values are parsed to a date, if possible.
     * Numeric values are assumed to represent number of seconds since 1970.
     * All remaining values return 1969-12-31 23:59 GMT.
     * @param d the object to convert
     * @return the converted value as a <code>Date</code>, never <code>null</code>
     * @since MMBase-1.7
     */
    static public Date toDate(Object d) {
        if (d == null) return new Date(-1);
        Date date = null;

        if (d instanceof Date) {
            date = (Date) d;
        } else {
            try {
                long dateInSeconds = -1;
                if (d instanceof Number) {
                    dateInSeconds = ((Number)d).longValue();
                } else if (d instanceof Document) {
                    // impossible
                    dateInSeconds = -1;
                } else if (d instanceof Boolean) {
                    dateInSeconds = -1;
                } else if (d instanceof Collection) {
                    // impossible
                    dateInSeconds = -1;
                } else if (d instanceof Node) {
                    // impossible
                    dateInSeconds = -1;
                } else if (d != null) {
                    d = toString(d);
                    if (d.equals("")) {
                        return new Date(-1);
                    }
                    dateInSeconds = Long.parseLong((String) d);
                } else {
                    dateInSeconds = -1;
                }
                if (dateInSeconds == -1) {
                    date = new Date(-1);
                } else if (dateInSeconds > Long.MAX_VALUE / 1000) {
                    date = new Date(Long.MAX_VALUE); // or should this throw an exception?
                } else if (dateInSeconds < Long.MIN_VALUE / 1000) {
                    date = new Date(Long.MIN_VALUE); // or should this throw an exception?
                } else {
                    date = new Date(dateInSeconds * 1000);
                }
            } catch (NumberFormatException e) {
                try {
                    date =  DynamicDate.getInstance((String) d);
                } catch (org.mmbase.util.dateparser.ParseException pe) {
                    log.error("Parser exception in " + d, pe);
                    return new Date(-1);
                } catch (Error per) {
                    throw new Error("Parser error in " + d, per);
                }
            }
        }
        return date;
    }

    static DocumentBuilder DOCUMENTBUILDER;
    static {
        try {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setValidating(false);
            dfactory.setNamespaceAware(true);
            DOCUMENTBUILDER = dfactory.newDocumentBuilder();
            DOCUMENTBUILDER.setEntityResolver(new XMLEntityResolver(false));
        } catch (ParserConfigurationException pce) {
            log.error("[sax parser]: " + pce.toString() + "\n" + Logging.stackTrace(pce));
        }
    }
    /**
     * Convert a String value to a Document
     * @param value The current value (can be null)
     * @return  the value as a DOM Element or <code>null</code>
     * @throws  IllegalArgumentException if the value could not be converted
     */
    static private Document convertStringToXML(String value) {
        if (value == null) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.trace("using xml string:\n" + value);
        }
        try {
            Document doc;
            final XMLErrorHandler errorHandler = new XMLErrorHandler(false, org.mmbase.util.XMLErrorHandler.NEVER);
            synchronized(DOCUMENTBUILDER) {
                // dont log errors, and try to process as much as possible...
                DOCUMENTBUILDER.setErrorHandler(errorHandler);
                // ByteArrayInputStream?
                // Yes, in contradiction to what one would think, XML are bytes, rather then characters.
                doc = DOCUMENTBUILDER.parse(new java.io.ByteArrayInputStream(value.getBytes("UTF-8")));
            }
            if (log.isDebugEnabled()) {
                log.trace("parsed: " + XMLWriter.write(doc, false, true));
            }
            if (!errorHandler.foundNothing()) {
                throw new IllegalArgumentException("xml invalid:\n" + errorHandler.getMessageBuffer() + "for xml:\n" + value);
            }
            return doc;
        } catch (org.xml.sax.SAXException se) {
            log.debug("[sax] not well formed xml: " + se.toString() + "(" + se.getMessage() + ")\n" + Logging.stackTrace(se));
            return convertStringToXML("<p>" + Encode.encode("ESCAPE_XML", value) + "</p>"); // Should _always_ be sax-compliant.
        } catch (java.io.IOException ioe) {
            String msg = "[io] not well formed xml: " + ioe.toString() + "\n" + Logging.stackTrace(ioe);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }


    /*
     * Wraps a List with an 'Escaper'.
     * @since MMBase-1.8
     */
    public static class ListWrapper extends AbstractList{
        private final List list;
        private final CharTransformer escaper;
        ListWrapper (List l, CharTransformer e) {
            list = l;
            escaper = e;
        }
        public Object get(int index) { return Casting.wrap(list.get(index), escaper); }
        public int size() { return list.size(); }
        public Object set(int index, Object value) { return list.set(index, value); }
        public void add(int index, Object value) { list.add(index, value); }
        public Object remove(int index) { return list.remove(index); }
        public boolean isEmpty() 	    {return list.isEmpty();}
        public boolean contains(Object o)   {return list.contains(o);}
        public Object[] toArray() 	    {return list.toArray();}
        public Object[] toArray(Object[] a) {return list.toArray(a);}
        public Iterator iterator() { return list.iterator(); }
        public ListIterator listIterator() { return list.listIterator(); }
        public String toString() {
            StringBuffer buf = new StringBuffer();
            Iterator i = list.iterator();
            boolean hasNext = i.hasNext();
            while (hasNext) {
                Casting.toStringBuffer(buf, i.next());
                hasNext = i.hasNext();
                if (hasNext) {
                    buf.append(',');
                }
            }
            return buf.toString();
        }
        public List getList() {
            return list;
        }
    }

    /**
     * Wraps a String with an 'Escaper'.
     * @since MMBase-1.8
     */
    public static class StringWrapper implements CharSequence {
        private final CharTransformer escaper;
        private final CharSequence string;
        private  String escaped = null;
        StringWrapper(CharSequence s, CharTransformer e) {
            escaper = e;
            string  = s;

        }

        public char charAt(int index) {
            toString();
            return escaped.charAt(index);
        }
        public int length() {
            toString();
            return escaped.length();
        }

        public CharSequence subSequence(int start, int end) {
            toString();
            return escaped.subSequence(start, end);
        }

        public String toString() {
            if (escaped == null) escaped = escape(escaper, string);
            return escaped;
        }
        public CharSequence getString() {
            return string;
        }
    }

}


