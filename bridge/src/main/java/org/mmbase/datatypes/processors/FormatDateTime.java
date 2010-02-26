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
 * @version $Id$
 * @since MMBase-1.8
 */

public class FormatDateTime implements Processor {
    private static final Logger log = Logging.getLoggerInstance(FormatDateTime.class);

    private static final long serialVersionUID = 1L;

    public Object process(Node node, Field field, Object value) {
        Locale locale = node.getCloud().getLocale();
        DataType dataType = field.getDataType();
        Object date = node.getValue(field.getName());
        if (dataType instanceof DateTimeDataType) {
            DateTimeDataType dateType = (DateTimeDataType) dataType;
            if (date == null || "".equals(date)) {
                return "";
            } else {
                return dateType.getPattern().getDateFormat(locale).format(node.getDateValue(field.getName()));
            }
        } else { // backwards compatibility
            if (date == null) {
                return "";
            } else {
                return org.mmbase.util.Casting.ISO_8601_LOOSE.get().format(node.getDateValue(field.getName()));
            }
        }
    }

    @Override
    public String toString() {
        return "format_datetime";
    }

}
