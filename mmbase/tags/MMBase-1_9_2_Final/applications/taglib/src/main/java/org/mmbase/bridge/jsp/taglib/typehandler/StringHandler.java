/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.datatypes.StringDataType;
import org.mmbase.datatypes.DataType;
import org.mmbase.storage.search.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.util.transformers.Sql;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A TypeHandler for strings, textareas and text-input.
 * Search values are SQL escaped.
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id$
 */

public class StringHandler extends AbstractTypeHandler {

    private static final Logger log = Logging.getLoggerInstance(StringHandler.class);

    /**
     * Constructor for StringHandler.
     */
    public StringHandler(FieldInfoTag tag) {
        super(tag);
    }


    /**
     * @since MMBase-1.9.1
     */
    protected int getCols(Field field) {
        int cols = field.getMaxLength();
        if (cols > 80) cols = 80;
        return cols;
    }


    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    @Override public String htmlInput(Node node, Field field, boolean search)        throws JspTagException {
        eh = getEnumHandler(node, field);
        if (eh != null) {
            return eh.htmlInput(node, field, search);
        }

        if(! search) {
            StringBuilder buffer = new StringBuilder();
            try {
                Object v = getFieldValue(node, field, true);
                String value = org.mmbase.util.Casting.toString(v);
                value = tag.decode(value, node);
                StringDataType dataType = (StringDataType) field.getDataType();

                if (dataType.getPattern().matcher("a\na").matches()) {
                    if(field.getMaxLength() > 2048)  {
                        // the wrap attribute is not valid in XHTML, but it is really needed for netscape < 6
                        // wrap attribute removed, we want to produce valid XHTML, and who is still using netscape < 6?
                        buffer.append("<textarea class=\"big " + getClasses(node, field) + "\" rows=\"10\" cols=\"80\" ");
                    } else {
                        buffer.append("<textarea class=\"small " + getClasses(node, field) + "\" rows=\"5\" cols=\"" + getCols(field) + "\" ");
                    }
                    addExtraAttributes(buffer);
                    buffer.append("name=\"").append(prefix(field.getName())).append("\" ");
                    buffer.append("id=\"").append(prefixID(field.getName())).append("\">");
                    if ("".equals(value)) {
                        String opt = tag.getOptions();
                        if (opt != null && opt.indexOf("noempty") > -1) {
                            // This can be needed because:
                            // If included, e.g. with xmlhttprequest,
                            // the textarea can collaps: <textarea />
                            // This does not work in either FF or IE if the contenttype is text/html
                            // The more logical contenttype application/xml or text/xml would make it behave normally in FF,
                            // but that is absolutely not supported by IE. IE sucks. FF too, but less so.
                            //
                            // Any how, in short, sometimes you _must_ output one space here if empty otherwise.
                            // I _reall_ cannot think of anything more sane then this.
                            // e.g. <!-- empty --> would simply produce a textarea containing that...
                            // also <![CDATA[]]> produces a textarea containing that...
                            //
                            // HTML is broken.

                            buffer.append(' ');
                        }
                    } else {
                        Xml.XMLEscape(value, buffer);
                    }
                    buffer.append("</textarea>");
                } else { // not 'field' perhaps it's 'string'.
                    buffer.append("<input class=\"small " + getClasses(node, field) + "\" type=\"").append(dataType.isPassword() ? "password" : "text").append("\"  size=\"" + getCols(field) + "\" ");
                    buffer.append("name=\"").append(prefix(field.getName())).append("\" ");
                    buffer.append("id=\"").append(prefixID(field.getName())).append("\" ");
                    String opt = tag.getOptions();
                    if (opt != null && opt.indexOf("noautocomplete") > -1) {
                        buffer.append("autocomplete=\"off\" ");
                    }
                    addExtraAttributes(buffer);
                    buffer.append("value=\"");
                    Xml.XMLEscape(value, buffer);
                    buffer.append("\" />");
                }
            } catch (ClassCastException cce) {
                DataType<?> dt = field.getDataType();
                log.error("Expected StringDataType for field " + field + " but found " + dt.getClass().getName() + ":"+ dt);
                throw cce;
            }
            return buffer.toString();
        } else { // in case of search
            return super.htmlInput(node, field, search);
        }
    }


    @Override protected void setValue(Node node, String fieldName, Object value) {
        String string = value == null ? null :  org.mmbase.util.Casting.toString(value);
        node.setStringValue(fieldName, string);
    }

    @Override protected Object getValue(Node node, String fieldName) {
        return node.getStringValue(fieldName);
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        @SuppressWarnings("deprecation")
        String guiType = field.getGUIType();

        if (guiType.indexOf('.') > 0) {
            EnumHandler e = new EnumHandler(tag, node, field);
            if (e.isAvailable()) {
                return e.useHtmlInput(node, field);
            }
        }
        String fieldValue = (String) getFieldValue(node, field);

        if (fieldValue != null) {
            String fieldName = field.getName();
            if (! fieldValue.equals(node.getValue(fieldName))) {
                if (fieldValue.length() == 0 && node.getValue(fieldName) == null) return false;
                setValue(node, fieldName,  fieldValue);
                return true;
            }
        }

        return false;
    }
    @Override protected Object getFieldValue(Node node, Field field) throws JspTagException {
        String fieldName = field.getName();
        String fieldValue =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));

        fieldValue = tag.encode(fieldValue, field);
        if (fieldValue != null) {
            String opt = tag.getOptions();
            if (opt != null && opt.indexOf("trim") > -1) {
                fieldValue = fieldValue.trim();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Received '" + fieldValue + "' for " + field + " " + tag.getOptions());
        }
        return fieldValue;
    }

    @Override protected boolean interpretEmptyAsNull(Field field) {
        return ! field.getDataType().isRequired();
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    @Override public String whereHtmlInput(Field field) throws JspTagException {
        EnumHandler e = getEnumHandler(null, field);
        if (e != null) {
            return e.whereHtmlInput(field);
        }
        String search =  findString(field);
        if (search == null) return null;

        Sql sql = new Sql(Sql.ESCAPE_QUOTES);
        return "( UPPER( [" + field.getName() + "] ) LIKE '%" + sql.transform(search.toUpperCase()) + "%')";
        // cannot call getSearvhValue, because sql excaping is done twice then :-(
    }

    @Override protected int getOperator(Field field) {
        if (field.getType() == Field.TYPE_STRING) {
            return FieldCompareConstraint.LIKE;
        } else {
            return FieldCompareConstraint.EQUAL;
        }
    }


    @Override public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        EnumHandler e = getEnumHandler(null, field);
        if (e != null) {
            return e.whereHtmlInput(field, query);
        }
       Constraint cons =  super.whereHtmlInput(field, query);

       if (cons != null) {
           if (! (cons instanceof FieldConstraint)) {
               throw new JspTagException("Found an instance of " + cons.getClass().getName() + " while expected a field-constraint");
           }
           query.setCaseSensitive((FieldConstraint) cons, false);
       }

       return cons;
   }

}
