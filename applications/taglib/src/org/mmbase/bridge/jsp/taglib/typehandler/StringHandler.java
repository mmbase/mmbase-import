/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.util.Encode;

/**
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class StringHandler extends AbstractTypeHandler {

    /**
     * Constructor for StringHandler.
     */
    public StringHandler(FieldInfoTag context) {
        super(context);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search)        throws JspTagException {
            
        StringBuffer buffer = new StringBuffer();
        if(! search) {
            if(field.getMaxLength() > 2048)  {
                // the wrap attribute is not valid in XHTML, but it is really needed for netscape < 6
                buffer.append("<textarea wrap=\"soft\" rows=\"10\" cols=\"80\" class=\"big\"  name=\"");
                buffer.append(prefix(field.getName()));
                buffer.append("\">");
                if (node != null) {
                    buffer.append(Encode.encode("ESCAPE_XML", context.decode(node.getStringValue(field.getName()), node)));
                }
                buffer.append("</textarea>");
            } else if(field.getMaxLength() > 255 )  {
                buffer.append("<textarea wrap=\"soft\" rows=\"5\" cols=\"80\" class=\"small\"  name=\"");
                buffer.append(prefix(field.getName()));
                buffer.append("\">");
                if (node != null) {
                    buffer.append(Encode.encode("ESCAPE_XML", context.decode(node.getStringValue(field.getName()), node)));
                }
                buffer.append("</textarea>");
            } else {
                buffer.append("<input type =\"text\" class=\"small\" size=\"80\" name=\"");
                buffer.append(prefix(field.getName()));
                buffer.append("\" value=\"");
                if (node != null) {
                    buffer.append(Encode.encode("ESCAPE_XML_ATTRIBUTE_DOUBLE", context.decode(node.getStringValue(field.getName()), node)));
                }
                buffer.append("\" />");
            }
            return buffer.toString();
        } else {
            return super.htmlInput(node, field, search);
        }
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public String useHtmlInput(Node node, Field field) throws JspTagException {
        // do the xml decoding thing...
        String fieldName = field.getName();
        String fieldValue = context.getContextTag().findAndRegisterString(prefix(fieldName));
        fieldValue = context.encode(fieldValue, field);
        if (fieldValue != null) {
            node.setValue(fieldName,  fieldValue);
        }
        return "";
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String fieldName = field.getName();
        String search = context.getContextTag().findAndRegisterString(prefix(fieldName));
        if (search == null) {
            return null;
        }
        if ("".equals(search)) {
            return null;
        }
        return "( UPPER( [" + fieldName + "] ) LIKE '%" + search.toUpperCase() + "%')";
    }

}
