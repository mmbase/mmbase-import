/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

/**
 * Collects MMBase specific 'cast' information, as static
 * to... functions. This is used (and used to be implemented) in
 * MMObjectNode. But this functionality is more generic to MMbase.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */

import org.mmbase.module.core.*;
import org.w3c.dom.*;
import org.mmbase.util.logging.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import java.util.*;
import java.io.Writer;

public class Casting {
    private static final Logger log = Logging.getLoggerInstance(Casting.class);

    /**
     * Get a value of a certain field.  The value is returned as a
     * String. Non-string values are automatically converted to
     * String. 'null' is converted to an empty string.
     * @param o the object which must be presented as a string
     * @return the field's value as a <code>String</code>
     */
    public static String toString(Object o) {
        if (o instanceof String) {
            return (String)o;
        }
        if (o == null) {
            return "";
        }
        return toStringBuffer(new StringBuffer(), o).toString();
    }

    /**
     * @since MMBase-1.7
     */
    public static StringBuffer toStringBuffer(StringBuffer buffer, Object o) {
        try {
            toWriter(new StringBufferWriter(buffer), o);
        } catch (java.io.IOException e) {}
        return buffer;
    }

    /**
     * @since MMBase-1.7
     */
    public static Writer toWriter(Writer writer, Object o) throws java.io.IOException {
        if (o == null || o instanceof Writer) {
            return writer;
        }

        if (o instanceof String) {
            writer.write((String)o);
        } else if (o instanceof byte[]) {
            writer.write(new String((byte[])o));
        } else if (o instanceof MMObjectNode) {
            writer.write("" + ((MMObjectNode)o).getNumber());
        } else if (o instanceof Node) {
            writer.write("" + ((Node)o).getNumber());
        } else if (o instanceof Document) {
            // doctype unknown.
            writer.write(convertXmlToString(null, (Document)o));
        } else if (o instanceof List) {
            Iterator i = ((List)o).iterator();
            boolean hasNext = i.hasNext();
            while (i.hasNext()) {
                toWriter(writer, i.next());
                hasNext = i.hasNext();
                if (hasNext) {
                    writer.write(",");
                }
            }
        } else {
            writer.write("" + o);
        }
        return writer;

    }

    /**
     * @since MMBase-1.7
     */
    public static List toList(Object o) {
        if (o == null)
            return new ArrayList();
        if (o instanceof List)
            return (List)o;
        return StringSplitter.split(toString(o));
    }

    /**
     * Returns the value of the specified field as a <code>dom.Document</code>
     * If the node value is not itself a Document, the method attempts to
     * attempts to convert the String value into an XML.
     * If the value cannot be converted, this method returns <code>null</code>
     *
     * @param o the object to be converted to an XML document
     * @return  the value of the specified field as a DOM Element or <code>null</code>
     * @throws  IllegalArgumentException if the Field is not of type TYPE_XML.
     * @since MMBase-1.6
     */

    static public Document toXML(Object o, String documentType, String conversion) {
        if (!(o instanceof Document)) {
            //do conversion from String to Document...
            // This is a laborous action, so we log it on service.
            // It will happen often if the nodes are not cached and so on.
            String xmltext = toString(o);
            if (log.isServiceEnabled()) {
                String msg = xmltext;
                if (msg.length() > 20)
                    msg = msg.substring(0, 20);
                log.service("Object " + msg + "... is not a Document, but " + (o == null ? "NULL" : "a " + o.getClass().getName()));
            }
            return convertStringToXML(xmltext, documentType, conversion);
        }
        return (Document)o;
    }

    /**
     * Get a binary value of a object.
     * @param obj The object to be converted to a byte[]
     * @return the field's value as an <code>byte []</code> (binary/blob field)
     */
    static public byte[] toByte(Object obj) {
        if (obj instanceof byte[]) {
            // was allready unmapped so return the value
            return (byte[])obj;
        } else {
            return toString(obj).getBytes();
        }
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an MMObjectNode.
     * If the field contains an Numeric value, the method
     * tries to obtrain the object with that number.
     * If it is a String, the method tries to obtain the object with
     * that alias. The only other possible values are those created by
     * certain virtual fields.
     * All remaining situations return <code>null</code>.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>int</code>
     */
    public static MMObjectNode toNode(Object i, MMObjectBuilder parent) {
        MMObjectNode res = null;
        if (i instanceof MMObjectNode) {
            res = (MMObjectNode)i;
        } else if (i instanceof Node) {
            res = parent.getNode(((Node)i).getNumber());
        } else if (i instanceof Number) {
            int nodenumber = ((Number)i).intValue();
            if (nodenumber != -1) {
                res = parent.getNode(nodenumber);
            }
        } else if (i != null && !i.equals("")) {
            res = parent.getNode(i.toString());
        }
        return res;
    }

    /**
     * @since MMBase-1.7
     */
    public static Node toNode(Object i, Cloud cloud) {
        Node res = null;
        if (i instanceof Node) {
            res = (Node)i;
        } else if (i instanceof MMObjectNode) {
            res = cloud.getNode(((MMObjectNode)i).getNumber());
        } else if (i instanceof Number) {
            int nodenumber = ((Number)i).intValue();
            if (nodenumber != -1) {
                res = cloud.getNode(nodenumber);
            }
        } else if (i != null && !i.equals("")) {
            res = cloud.getNode(i.toString());
        }
        return res;
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an int value. Values of non-int, numeric fields are converted if possible.
     * Booelan fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * If a value is an MMObjectNode, it's numberfield is returned.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>int</code>
     */
    static public int toInt(Object i) {
        return toInt(i, -1);
    }

    /**
     * as toInt, but with configurable fallback-value
     * @since MMBase-1.7
     */

    static public int toInt(Object i, int def) {
        int res = def;
        if (i instanceof MMObjectNode) {
            res = ((MMObjectNode)i).getNumber();
        } else if (i instanceof Node) {
            res = ((Node)i).getNumber();
        } else if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).intValue();
        } else if (i != null) {
            try {
                res = Integer.parseInt("" + i);
            } catch (NumberFormatException e) {
                // not an integer? perhaps it is a fload or double represented as String.
                try {
                    res = Double.valueOf("" + i).intValue();
                } catch (NumberFormatException ex) {
                    // give up, fall back to default.
                }
            }
        }
        return res;
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an boolean value.
     * If the actual value is numeric, this call returns <code>true</code>
     * if the value is a positive, non-zero, value. In other words, values '0'
     * and '-1' are concidered <code>false</code>.
     * If the value is a string, this call returns <code>true</code> if
     * the value is "true" or "yes" (case-insensitive).
     * In all other cases (including calling byte fields), <code>false</code>
     * is returned.
     * Note that there is currently no basic MMBase boolean type, but some
     * <code>excecuteFunction</code> calls may return a Boolean result.
     *
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>int</code>
     */
    static public boolean toBoolean(Object b) {
        if (b instanceof Boolean) {
            return ((Boolean)b).booleanValue();
        } else if (b instanceof Number) {
            return ((Number)b).intValue() > 0;
        } else if (b instanceof String) {
            // note: we don't use Boolean.valueOf() because that only captures
            // the value "true"
            String s = ((String)b).toLowerCase();
            if (s.equals("true") || s.equals("yes")) {
                return true;
            } else if (s.equals("false") || s.equals("no")) {
                return false;
            } else {
                // still not yet!
                // Call MMLanguage, and compare to
                // the 'localized' values of true or yes.
                org.mmbase.module.gui.html.MMLanguage languages = (org.mmbase.module.gui.html.MMLanguage)org.mmbase.module.Module.getModule("mmlanguage");
                if (languages != null) {
                    return s.equals(languages.getFromCoreEnglish("true")) || s.equals(languages.getFromCoreEnglish("yes"));
                }
            }
        }
        return false;
    }

    /**
     * Get a value of a certain field.
     * The value is returned as an Integer value. Values of non-Integer, numeric fields are converted if possible.
     * Boolean fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>Integer</code>
     */
    static public Integer toInteger(Object i) {
        int res = -1;
        if (i instanceof Integer) {
            return (Integer)i;
        }
        return new Integer(toInt(i));
    }

    /**
     * Get a value of a certain field.
     * The value is returned as a long value. Values of non-long, numeric fields are converted if possible.
     * Boolean fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>long</code>
     * @since MMBase-1.7
     */
    static public long toLong(Object i, long def) {
        long res = def;
        if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).longValue();
        } else if (i != null) {
            //keesj:
            //TODO:add Node and MMObjectNode  
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

    static public long toLong(Object i) {
        return toLong(i, -1);
    }

    /**
     * Get a value of a certain field.
     * The value is returned as a float value. Values of non-float, numeric fields are converted if possible.
     * Boolean fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>float</code>
     */
    static public float toFloat(Object i) {
        float res = -1;
        if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).floatValue();
        } else if (i != null) {
            try {
                res = Float.parseFloat("" + i);
            } catch (NumberFormatException e) {}
        }
        return res;
    }

    /**
     * How to convert mmbase object to a Date object
     * @since MMBase-1.7
     */

    static public java.util.Date toDate(Object i) {
        long date = -1;
        if (i instanceof Integer) {
            date = ((Integer)i).longValue();
        } else if (i instanceof Number) {
            date = ((Number)i).longValue();
        } else if (i != null) {
            try {
                date = Long.parseLong("" + i);
            } catch (NumberFormatException e) {}
        }
        if (date == -1) {
            return new java.util.Date(-1);
        } else {
            return new java.util.Date(date * 1000);
        }
    }

    /**
     * Get a value of a certain field.
     * The value is returned as a double value. Values of non-double, numeric fields are converted if possible.
     * Boolean fields return 0 for false, 1 for true.
     * String fields are parsed to a number, if possible.
     * All remaining field values return -1.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as a <code>double</code>
     */
    static public double toDouble(Object i) {
        double res = -1;
        if (i instanceof Boolean) {
            res = ((Boolean)i).booleanValue() ? 1 : 0;
        } else if (i instanceof Number) {
            res = ((Number)i).doubleValue();
        } else if (i != null) {
            try {
                res = Double.parseDouble("" + i);
            } catch (NumberFormatException e) {}
        }
        return res;
    }

    /**
     * Convert a String value of a field to a Document
     * @param value     The current value of the field, (can be null)
     * @return A DOM <code>Document</code> or <code>null</code> if there was no value and builder allowed  to be null
     * @throws RuntimeException When value was null and not allowed by builer, and xml failures.
     */
    static private Document convertStringToXML(String value, String documentType, String conversion) {
        if (value == null)
            return null;
        if (value.startsWith("<")) { // _is_ already XML, only presented as a string.
            // removing doc-headers if nessecary

            // remove all the <?xml stuff from beginning if there....
            //  <?xml version="1.0" encoding="utf-8"?>
            if (value.startsWith("<?xml")) {
                // strip till next ?>
                int stop = value.indexOf("?>");
                if (stop > 0) {
                    value = value.substring(stop + 2).trim();
                    log.debug("removed <?xml part");
                } else {
                    throw new RuntimeException("no ending ?> found in xml:\n" + value);
                }
            } else {
                log.debug("no <?xml header found");
            }

            // remove all the <!DOCTYPE stuff from beginning if there....
            // <!DOCTYPE builder PUBLIC "-//MMBase/builder config 1.0 //EN" "http://www.mmbase.org/dtd/builder_1_1.dtd">
            if (value.startsWith("<!DOCTYPE")) {
                // strip till next >
                int stop = value.indexOf(">");
                if (stop > 0) {
                    value = value.substring(stop + 1).trim();
                    log.debug("removed <!DOCTYPE part");
                } else {
                    throw new RuntimeException("no ending > found in xml:\n" + value);
                }
            } else {
                log.debug("no <!DOCTYPE header found");
            }
        } else {
            // not XML, make it XML, when conversion specified, use it...
            if (conversion == null) {
                conversion = "MMXF_POOR";
                log.warn("Using default for XML conversion: '" + conversion + "'.");
            }
            if (log.isDebugEnabled()) {
                log.debug("converting the string to something else using conversion: " + conversion);
            }
            value = org.mmbase.util.Encode.decode(conversion, (String)value);
        }

        if (log.isDebugEnabled()) {
            log.trace("using xml string:\n" + value);
        }
        // add the header stuff...
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
        if (documentType != null) {
            xmlHeader += "\n" + documentType;
        }
        value = xmlHeader + "\n" + value;

        try {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            if (documentType != null) {
                if (log.isDebugEnabled()) {
                    log.debug("validating with doctype: " + documentType);
                }
                dfactory.setValidating(true);
            }
            DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

            // dont log errors, and try to process as much as possible...
            XMLErrorHandler errorHandler = new XMLErrorHandler(false, org.mmbase.util.XMLErrorHandler.NEVER);
            documentBuilder.setErrorHandler(errorHandler);
            documentBuilder.setEntityResolver(new XMLEntityResolver());
            // ByteArrayInputStream?
            // Yes, in contradiction to what one would think, XML are bytes, rather then characters.
            Document doc = documentBuilder.parse(new java.io.ByteArrayInputStream(value.getBytes("UTF-8")));
            if (log.isDebugEnabled()) {
                log.trace("parsed: " + convertXmlToString(null, doc));
            }
            if (!errorHandler.foundNothing()) {
                throw new RuntimeException("xml invalid:\n" + errorHandler.getMessageBuffer() + "for xml:\n" + value);
            }
            return doc;
        } catch (ParserConfigurationException pce) {
            String msg = "[sax parser] not well formed xml: " + pce.toString() + "\n" + Logging.stackTrace(pce);
            log.error(msg);
            throw new RuntimeException(msg);
        } catch (org.xml.sax.SAXException se) {
            String msg = "[sax] not well formed xml: " + se.toString() + "(" + se.getMessage() + ")\n" + Logging.stackTrace(se);
            log.error(msg);
            throw new RuntimeException(msg);
        } catch (java.io.IOException ioe) {
            String msg = "[io] not well formed xml: " + ioe.toString() + "\n" + Logging.stackTrace(ioe);
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    static private String convertXmlToString(String doctype, Document xml) {
        log.debug("converting from xml to string");

        // check for null values
        if (xml == null) {
            log.debug("field was empty");
            // string with null isnt allowed in mmbase...
            return "";
        }
        // check if we are using the right DOC-type for this field....
        //String doctype = parent.getField(fieldName).getDBDocType();
        if (doctype != null) {
            // we have a doctype... the doctype of the document has to mach the doctype of the doctype which is needed..
            org.w3c.dom.DocumentType type = xml.getDoctype();
            String publicId = type.getPublicId();
            if (doctype.indexOf(publicId) == -1) {
                //throw new RuntimeException("doctype('"+doctype+"') required by field '"+fieldName+"' and public id was NOT in it : '"+publicId+"'");
            }
            log.warn("doctype check can not completely be trusted");
        }

        try {
            //make a string from the XML
            TransformerFactory tfactory = org.mmbase.cache.xslt.FactoryCache.getCache().getDefaultFactory();
            Transformer serializer = tfactory.newTransformer();

            // for now, we save everything in ident form, this since it makes debugging a little bit more handy
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");

            // store as less as possible, otherthings should be resolved from gui-type
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            java.io.StringWriter str = new java.io.StringWriter();
            serializer.transform(new javax.xml.transform.dom.DOMSource(xml), new javax.xml.transform.stream.StreamResult(str));
            if (log.isDebugEnabled()) {
                log.debug("xml -> string:\n" + str.toString());
            }
            return str.toString();
        } catch (TransformerConfigurationException tce) {
            String message = tce.toString() + " " + Logging.stackTrace(tce);
            log.error(message);
            throw new RuntimeException(message);
        } catch (TransformerException te) {
            String message = te.toString() + " " + Logging.stackTrace(te);
            log.error(message);
            throw new RuntimeException(message);
        }
    }

}
