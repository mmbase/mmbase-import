/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import java.io.*;
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
    @Override
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
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
            show.append(gui.getFunctionValue(args));
        }
        show.append("<input class=\"").append(getClasses(node, field)).append("\" type=\"").append(search ? "text" : "file").append("\" name=\"").append(prefix(field.getName())).append("\" id=\"").append(prefixID(field.getName())).append("\" ");
        addExtraAttributes(show);
        show.append("/>");
        return show.toString();

    }



    @Override
    protected InputStream getValue(Node node, String fieldName) {
        InputStream v = node.getInputStreamValue(fieldName);
        if (log.isDebugEnabled()) {
            log.debug("Value for " + node.getNumber() + ":" + fieldName + ": " + v.getClass() + " " + v + " of " + node.getClass());
        }
        return v;
    }


    /**
     * Returns the field value as specified by the client's post.
     */
    @Override
    protected SerializableInputStream getFieldValue(Node node, Field field) throws JspTagException {
        if (MultiPart.isMultipart(tag.getPageContext())) {
            ContextTag ct = tag.getContextTag();
            log.debug("Field value '" + field.getName() + "' not found in context, using existing value ");
            SerializableInputStream si = ct.getInputStream(prefix(field.getName()));
            if (si == null || si.getName().length() == 0) {
                return null;
            } else {
                return si;
            }
        } else {
            return null;
        }
    }

    @Override
    public String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException {
        final Object fieldValue;
        boolean fromUser = true;
        {
            SerializableInputStream si = getFieldValue(node, field);
            if (si == null) {
                fieldValue = getFieldValue(node, field, node == null);
                log.debug("Field value '" + field.getName() + "' not found in context, using existing value " + fieldValue);
                fromUser = false;
            } else {
                fieldValue = si;
            }
        }

        DataType<Object> dt = field.getDataType();
        if (log.isDebugEnabled()) {
            log.debug("Validating " + fieldValue + " with " + dt);
        }
        Collection<LocalizedString> col = dt.validate(fieldValue, node, field);
        if (col.isEmpty()) {
            // do actually set the field, because some datatypes need cross-field checking
            // also in an mm:form, you can simply commit.
            if (fromUser) {
                SerializableInputStream bytes = Casting.toSerializableInputStream(fieldValue);
                if (node != null && ! field.isReadOnly() && ! bytes.getName().equals("")) {
                    setValue(node, field.getName(), bytes);
                }
            }
            if (errors) {
                return "<div id=\"" + prefixError(field.getName()) + "\" class=\"mm_check_noerror\"> </div>";
            } else {
                return "";
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Found errors " + errors);
            }
            FormTag form =  tag.getFormTag(false, null);
            if (form != null) {
                form.setValid(false);
            }
            if (errors) {
                StringBuilder show = new StringBuilder("<div id=\"");
                show.append(prefixError(field.getName()));
                show.append("\" class=\"mm_check_error\">");
                Locale locale =  tag.getLocale();
                for (LocalizedString error : col) {
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
    }


    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    @Override
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        SerializableInputStream bytes = getFieldValue(node, field);
        if (bytes == null) {
            if (! MultiPart.isMultipart(tag.getPageContext())) {
                throw new BridgeException("getBytes(" + prefix(field.getName()) + ") returned null (node= " +  node.getNumber() +") field=(" + field + ") (Was your form  enctype='multipart/form-data' ?");
            } else {
                return false;
            }
        }
        log.debug("Found " + bytes.getName());
        if ("".equals(bytes.getName())) {
            log.debug("Nothing uploaded for " + field + ", hence not changed");
            return false;
        } else {
            log.debug("Uploaded for " + field + ": " + bytes);
            node.setValue(field.getName(), bytes);
            return true;
        }
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    @Override
    public String whereHtmlInput(Field field) throws JspTagException {
        log.error("Don't know what to do with byte[]");
        return super.whereHtmlInput(field);
    }

}
