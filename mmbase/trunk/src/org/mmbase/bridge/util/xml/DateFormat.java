/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.xpath.XPathAPI;
import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.LocalContext;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Dates are stored as integer in mmbase. If you want to show these dates
 * in a nice format in the XSL transformation.
 * it is necessary to use this Xalan extension.
 * The XSLT looks like this then:
 *
 * <pre>
 *  &lt;xsl:stylesheet  version = "1.0"
 *    xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
 *    xmlns:date ="org.mmbase.bridge.util.xml.DateFormat"
 *  &gt;
 * </pre>
 *
 * @author Nico Klasens
 * @version $Id: DateFormat.java,v 1.4 2004-01-21 12:50:42 michiel Exp $
 * @since   MMBase-1.7
 */
public class DateFormat {

    private static final Logger log = Logging.getLoggerInstance(DateFormat.class);


    /**
     * Formats a node's field value with the date pattern
     * @param node the number or alias of the node containing the field
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @return the formatted string
     */
    public static String format(String node, String fieldName, String pattern) {
        if (log.isDebugEnabled()) {
            log.debug("calling with string '" + node + "' fieldname: " + fieldName + "' pattern: " + pattern);
        }
        return format("mmbase", node, fieldName, pattern);
    }

    /**
     * Formats a node's field value with the date pattern
     * @param cloudName the name of the cloud in which to find the node
     * @param number the number or alias of the node containing the field
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @return the formatted string
     */
    public static String format(String cloudName, String number, String fieldName, String pattern) {
        log.debug("calling base");
        try {
            Cloud cloud = LocalContext.getCloudContext().getCloud(cloudName);
            return format(cloud, number, fieldName, pattern);
        } catch (BridgeException e) {
            return "could not find '" + fieldName + "' on node '" + number + "' (" + e.toString() + ")";
        }
    }

    /**
     * Formats a node's field value with the date pattern
     * @param cloudName the cloud in which to find the node
     * @param number the number or alias of the node containing the field
     * @param fieldName the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @return the formatted string
     */
    public static String format(Cloud cloud, String number, String fieldName, String pattern) {
        log.debug("calling base");
        try {
            Node node = cloud.getNode(number);
            String fieldValue = node.getStringValue(fieldName);
            return format(fieldValue, pattern);
        } catch (BridgeException e) {
            if (log.isDebugEnabled()) {
                log.debug("could not find '" + fieldName + "' on node '" + number + "'");
                log.trace(Logging.stackTrace(e));
            }
            return "could not find " + fieldName + " on node " + number + "(" + e.toString() + ")";
        }
    }

    
    public static String format(String fieldValue, String pattern) {
        return format(fieldValue, pattern, 1000);
    }

    /** Formats the fieldvalue to a date pattern
     * 
     * @param fieldvalue  time-stamp
     * @param pattern   the date pattern (i.e. 'dd-MM-yyyy')
     * @param factor    Factor to multiply fieldvalue to make milliseconds. Should be 1000 normally (so field in seconds)
     * @return the formatted string
     */
    public static String format(String fieldValue, String pattern, int factor) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (fieldValue == null || "".equals(fieldValue)) {
           return "";
        }
        long seconds = Long.valueOf(fieldValue).longValue();
        return sdf.format(new Date(seconds * factor));
    }

    /**
     * Formats a node's field value with the date pattern.
     * This version requires you to supply a DOM node. It will search for a tag of the form
     * &lt;field name='number' &gt; and uses it's contents to retrieve the node.
     * @deprecated not sure where this is used?
     * @param cloudName the cloud in which to find the node
     * @param node A DOM node (xml) containing the node's fields as subtags
     * @param fieldname the name of the field to format
     * @param pattern the date pattern (i.e. 'dd-MM-yyyy')
     * @return the formatted string
     * @throws javax.xml.transform.TransformerException if something went wrong while searching the DOM Node
     */
    public static String format(Cloud cloud, org.w3c.dom.Node node, String fieldName, String pattern) throws javax.xml.transform.TransformerException {
        log.debug("calling with dom node");
        // bit of a waste to use an xpath here?
        String number = XPathAPI.eval(node, "./field[@name='number']").toString();
        return format(cloud, number, fieldName, pattern);
    }

}
