/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.edit.FormTag;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.datatypes.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;


/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8 (was named ByteHandler previously)
 * @version $Id$
 */

public class BinaryHandler extends AbstractTypeHandler {
    private static final Logger log = Logging.getLoggerInstance(BinaryHandler.class);
    /**
     * Constructor
     * @param tag
     */
    public BinaryHandler(FieldInfoTag tag) {
        super(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    @Override public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        StringBuilder show = new StringBuilder();
        if (node != null) {
            Function<?> gui = node.getFunction("gui");
            Parameters args = gui.createParameters();
            args.set("field", field.getName());
            args.set(Parameter.LANGUAGE, tag.getLocale().getLanguage());
            args.set("session",  tag.getSessionName());
            PageContext pc = tag.getContextTag().getPageContext();
            args.set(Parameter.RESPONSE, (HttpServletResponse) pc.getResponse());
            args.set(Parameter.REQUEST,  (HttpServletRequest) pc.getRequest());
            args.set(Parameter.LOCALE, tag.getLocale());
            show.append("" + gui.getFunctionValue(args));
        }
        show.append("<input class=\"" + getClasses(node, field) + "\" type=\"").append(search ? "text" : "file").append("\" name=\"").append(prefix(field.getName())).append("\" id=\"").append(prefixID(field.getName())).append("\" ");
        addExtraAttributes(show);
        show.append("/>");
        return show.toString();

    }


    /**
     * Returns the field value as specified by the client's post.
     */
    @Override
    protected Object getFieldValue(Node node, Field field) throws JspTagException {
        if (MultiPart.isMultipart(tag.getPageContext())) {
            ContextTag ct = tag.getContextTag();
            return ct.getInputStream(prefix(field.getName()));
        } else {
            return null;
        }
    }

    @Override public String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException {
        Object fieldValue = getFieldValue(node, field);

        if (fieldValue != null) {
            DataType<Object> dt = field.getDataType();
            Collection<LocalizedString> col = dt.validate(fieldValue, node, field);
            if (col.size() == 0) {
                // do actually set the field, because some datatypes need cross-field checking
                // also in an mm:form, you can simply commit.
                if (node != null && ! field.isReadOnly()) {
                    setValue(node, field.getName(), (SerializableInputStream) fieldValue);
                }
                if (errors) {
                    return "<div id=\"" + prefixError(field.getName()) + "\" class=\"mm_check_noerror\"> </div>";
                } else {
                    return "";
                }
            } else {
                FormTag form =  tag.getFormTag(false, null);
                if (form != null) {
                    form.setValid(false);
                }
                if (errors) {
                    StringBuilder show = new StringBuilder("<div id=\"");
                    show.append(prefixError(field.getName()));
                    show.append("\" class=\"mm_check_error\">");
                    Locale locale =  tag.getLocale();
                    Iterator<LocalizedString> i = col.iterator();
                    while (i.hasNext()) {
                        LocalizedString error = i.next();
                        show.append("<span>");
                        Xml.XMLEscape(error.get(locale), show);
                        show.append("</span>");
                    }
                    show.append("</div>");
                    return show.toString();
                } else {
                    return "";
                }
            }
        } else {
            return "";
        }
    }


    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        SerializableInputStream bytes = (SerializableInputStream) getFieldValue(node, field);
        if (bytes == null) {
            throw new BridgeException("getBytes(" + prefix(field.getName()) + ") returned null (node= " +  node.getNumber() +") field=(" + field + ") (Was your form  enctype='multipart/form-data' ?");
        }
        if ("".equals(bytes.getName())) {
            return false;
        } else {
            node.setValue(field.getName(), bytes);
            return true;
        }
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    @Override public String whereHtmlInput(Field field) throws JspTagException {
        log.error("Don't know what to do with byte[]");
        return super.whereHtmlInput(field);
    }

}
