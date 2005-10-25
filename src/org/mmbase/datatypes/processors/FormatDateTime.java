/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * This can be used as getProcessor for String on DateTime fields. Then, the 'getStringValue' will
 * use a localized String as customized in datatypes.xml
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormatDateTime.java,v 1.1 2005-10-25 12:30:26 michiel Exp $
 * @since MMBase-1.8
 */

public class FormatDateTime implements Processor {
    private static final Logger log = Logging.getLoggerInstance(FormatDateTime.class);

    private static final int serialVersionUID = 1;

    public Object process(Node node, Field field, Object value) {
        DateTimeDataType dateType = (DateTimeDataType) field.getDataType();
        Locale locale = node.getCloud().getLocale();
        Object date = node.getValue(field.getName());
        if (date == null) {
            return "";
        } else {
            return dateType.getPattern().getDateFormat(locale).format(node.getDateValue(field.getName()));
        }
    }

    public String toString() {
        return "format_datetime";
    }

}
