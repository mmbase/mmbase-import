/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;
import org.mmbase.bridge.util.fields.Processor;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

/**
 * This class implements the `get' for XML field with specialization 'field'. They look like ASCII,
 * if accessed by setString, getString.
 * 
 * This can be used if the XML features of a certain field are only used in certain extensions only,
 * but must look like String in the base class. So, it allows you to have the db-type XML without
 * noticing it too much.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldSetString.java,v 1.1 2005-06-28 08:15:36 michiel Exp $
 * @since MMBase-1.8
 */

public class FieldSetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(FieldSetString.class);

    protected static final String PREF = "<field><![CDATA[";
    protected static final String POST = "]]></field>";
    public Object process(Node node, Field field, Object value) {
        log.debug("Getting " + field + " from " + node + " as a String");
        return Casting.toXML(PREF + Casting.toString(value) + POST);
    }
  
}
