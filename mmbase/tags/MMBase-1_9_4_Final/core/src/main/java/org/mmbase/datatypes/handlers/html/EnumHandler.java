/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.datatypes.handlers.*;
import org.mmbase.datatypes.*;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;

import org.mmbase.util.logging.*;



/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public class EnumHandler extends HtmlHandler {
    private static final Logger log = Logging.getLoggerInstance(EnumHandler.class);
    /**
     */
    protected Iterator<Map.Entry<Object, String>> getIterator(Request request, Node node, Field field) {
        DataType<Object> dataType = field.getDataType();
        return dataType.getEnumerationValues(request.getLocale(), request.getCloud(), node, field);
    }

    @Override
    public String input(Request request, Node node, Field field, boolean search) {
        StringBuilder buffer = new StringBuilder();
        String fieldName = field.getName();
        buffer.append("<select class=\"");
        appendClasses(buffer, node, field);
        appendNameId(buffer, request, field);
        //addExtraAttributes(buffer);
        buffer.append(">");
        Object value  = cast(getFieldValue(request, node, field, true), node, field);
        if (log.isDebugEnabled()) {
            log.debug("using value " + (value == null ? "NULL" : value.getClass().getName() + " " + value));
        }
        if (! field.getDataType().isRequired()) {
            buffer.append("<option value=\"\" ");
            if (value == null) buffer.append("selected=\"selected\" ");
            buffer.append(">--</option>");
        }
        Iterator<Map.Entry<Object, String>> iterator = getIterator(request, node, field);

        String valueString = Casting.toString(value);
        while(iterator != null && iterator.hasNext()) {
            Map.Entry<Object, String> entry = iterator.next();
            Object key = entry.getKey();
            if (key == null) {
                log.warn("Found null as enumeration key for " + field.getDataType());
                continue;
            }
            String keyString = Casting.toString(key);
            buffer.append("<option value=\"");
            buffer.append(XML.transform(keyString));
            buffer.append("\"");
            if (keyString.equals(valueString)) {
                buffer.append(" selected=\"selected\"");
            } else if (search) {
                String searchs = Casting.toString(request.getValue(field));
                if (keyString.equals(searchs)) {
                    buffer.append(" selected=\"selected\"");
                }
            }
            buffer.append(">");
            buffer.append(XML.transform(entry.getValue()));
            buffer.append("</option>");
        }
        buffer.append("</select>");
        if (search) {
            String name = id(field.getName()) + "_search";
            String searchi =  (String) request.getValue(field, "search");
            buffer.append("<input type=\"checkbox\" name=\"").append(name).append("\" ");
            buffer.append("id=\"").append(name).append("\" ");
            if (searchi != null) {
                buffer.append(" checked=\"checked\"");
            }
            buffer.append(" />");
        }
        return buffer.toString();
    }



}
