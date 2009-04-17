/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.datatypes.handlers.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import java.util.*;

/**
 * Handlers can be associated to DataTypes, but different Handler can be associated with different
 * content types. The main implementation will of course be one that produces HTML, like forms, and
 * post and things like that.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractHtmlHandler.java,v 1.2 2009-04-17 15:43:39 michiel Exp $
 * @since MMBase-1.9.1
 */

public abstract class AbstractHtmlHandler  extends AbstractHandler<String> {
    private static final Logger log = Logging.getLoggerInstance(AbstractHtmlHandler.class);

    protected static final CharTransformer XML = new Xml(Xml.ESCAPE);

    protected void appendClasses(StringBuilder buf, Node node, Field field) {
        buf.append("mm_validate");
        if (field instanceof org.mmbase.bridge.util.DataTypeField) {
            buf.append(" mm_dthandler mm_dt_").append(field.getDataType().getName());
        } else {
            buf.append(" mm_dthandler mm_f_").append(field.getName()).append(" mm_nm_").append(field.getNodeManager().getName());
        }
        if (node != null) buf.append(" mm_n_").append(node.getNumber());
    }

    protected void appendNameId(StringBuilder buf, Request request, Field field) {
        buf.append("name=\"").append(request.getName(field)).append("\" ");
        buf.append("id=\"").append(id(request.getName(field))).append("\" ");
    }

    protected String prefixError(String s)  {
        String prefix = "_";
        return "mm_check_" + prefix + (prefix.length() != 0 ? "_" : "") + s;
    }

    public String check(Request request, Node node, Field field, boolean errors) {
        Object fieldValue = request.getValue(field);
        final DataType<Object> dt = field.getDataType();
        if (fieldValue == null) {
            log.debug("Field value not found in context, using existing value ");
            fieldValue = getFieldValue(request, node, field, node == null);
        } else if (fieldValue.equals("") && ! field.isRequired()) {
            log.debug("Field value found in context is empty, interpreting as null");
            fieldValue = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Value for field " + field + ": " + fieldValue + " and node " + node);
        }
        Collection<LocalizedString> col = dt.validate(fieldValue, node, field);
        if (col.size() == 0) {
            // do actually set the field, because some datatypes need cross-field checking
            // also in an mm:form, you can simply commit.
            if (node != null && ! field.isReadOnly()) {
                String fieldName = field.getName();
                Object oldValue = node.getValue(fieldName);
                if (fieldValue == null ? oldValue != null : ! fieldValue.equals(oldValue)) {
                    try {
                        if(log.isDebugEnabled()) {
                            log.debug("Setting " + fieldName + " to " + (fieldValue == null ? "" : fieldValue.getClass().getName()) + " " + fieldValue);
                        }
                        if ("".equals(fieldValue) && interpretEmptyAsNull(field)) {
                            setValue(node, fieldName,  null);
                        } else {
                            setValue(node, fieldName,  fieldValue);
                        }
                    } catch (Throwable t) {
                        // may throw exception like 'You cannot change the field"
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("not Setting " + fieldName + " to " + fieldValue + " because already has that value");
                    }
                }
            }
            if (errors && ! field.isReadOnly()) {
                return "<div id=\"" + prefixError(field.getName()) + "\" class=\"mm_check_noerror\"> </div>";
            } else {
                return "";
            }
        } else {
            request.invalidate();
            if (errors && ! field.isReadOnly()) {
                StringBuilder show = new StringBuilder("<div id=\"");
                show.append(request.getName(field));
                show.append("\" class=\"mm_check_error\">");
                Locale locale = request.getLocale();
                for (LocalizedString error : col) {
                    show.append("<span class='" + error.getKey() + "'>");
                    Xml.XMLEscape(error.get(locale), show);
                    show.append("</span>");
                }
                show.append("</div>");
                return show.toString();
            } else {
                return "";
            }
        }
    }

}
