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
 <pre>
  &lt;xsl:stylesheet  version = "1.0"
    xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
    xmlns:date ="org.mmbase.bridge.util.xml.DateFormat"
  &gt;
 * 
 * @author Nico Klasens
 * @created 4-nov-2003
 * @version $Id: DateFormat.java,v 1.2 2003-12-30 16:00:43 nico Exp $
 */
public class DateFormat {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(DateFormat.class.getName());

    /** Formats the field value with the date pattern
     * 
     * @param node
     * @param fieldname
     * @param pattern
     * @return
     */
    public static String format(String node, String fieldname, String pattern) {
        if (log.isDebugEnabled()) {
            log.debug("calling with string '" + node + "' fieldname: " + fieldname + "' pattern: " + pattern);
        }
        return format("mmbase", node, fieldname, pattern);
    }

    /** Formats the field value with the date pattern
     * 
     * @param cloudName
     * @param number
     * @param fieldname
     * @param pattern
     * @return
     */
    public static String format(String cloudName, String number, String fieldname, String pattern) {
        log.debug("calling base");
        try {
            Cloud cloud = LocalContext.getCloudContext().getCloud(cloudName);
            return format(cloud, number, fieldname, pattern);
        } catch (BridgeException e) {
            return "could not find '" + fieldname + "' on node '" + number + "' (" + e.toString() + ")";
        }
    }

    /** Formats the field value with the date pattern
     * 
     * @param cloud
     * @param number
     * @param fieldname
     * @param pattern
     * @return
     */
    public static String format(Cloud cloud, String number, String fieldname, String pattern) {
        log.debug("calling base");
        try {
            Node node = cloud.getNode(number);
            String fieldvalue = node.getStringValue(fieldname);
            return format(fieldvalue, pattern);
        } catch (BridgeException e) {
            if (log.isDebugEnabled()) {
                log.debug("could not find '" + fieldname + "' on node '" + number + "'");
                log.trace(Logging.stackTrace(e));
            }
            return "could not find " + fieldname + " on node " + number + "(" + e.toString() + ")";
        }
    }
    
    /** Formats the fieldvalue to a date pattern
     * 
     * @param fieldvalue number of seconds
     * @param pattern pattern to format the second
     * @return
     */
    public static String format(String fieldvalue, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (fieldvalue == null || "".equals(fieldvalue)) {
           return "";
        }
        long seconds = Long.valueOf(fieldvalue).longValue();
        return sdf.format(new Date(seconds * 1000));
    }

    /** It can be handy to supply a whole node, it will search for the field 
     * 'number' and 'name' itself.
     * 
     * @param cloud
     * @param node
     * @param fieldname
     * @param pattern
     * @return
     * @throws javax.xml.transform.TransformerException
     */
    public static String format(Cloud cloud, org.w3c.dom.Node node, String fieldname, String pattern) throws javax.xml.transform.TransformerException {
        log.debug("calling with dom node");
        String number = XPathAPI.eval(node, "./field[@name='number']").toString();
        return format(cloud, number, fieldname, pattern);
    }

}