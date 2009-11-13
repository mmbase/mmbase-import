/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.handlers.Handler;
import org.mmbase.datatypes.handlers.Request;
import org.mmbase.datatypes.handlers.AbstractRequest;
import org.mmbase.datatypes.handlers.html.HtmlHandler;
import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.storage.search.Constraint;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.edit.FormTag;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.Parameter;

/**
 * The DataType of the field can also itself specify how the input widgets must look like. This
 * TypeHandler wraps this {@link org.mmbase.datatypes.handlers.Handler} object into a {@link
 * TypeHandler}.
 *
 * Actually, as soon as all TypeHandler implementations are migrated to Handlers, this can become
 * the only way to do it.
 *
 * @author Michiel Meeuwisssen
 * @since  MMBase-1.9.1
 * @version $Id$
 */

public class DataTypeHandler implements TypeHandler {

    private static final Logger log = Logging.getLoggerInstance(DataTypeHandler.class);

    protected final Handler<String> handler;
    protected final Request request;

    public DataTypeHandler(Handler<String> h, final FieldInfoTag tag) {
        handler = h;
        request = new Request() {
                {
                    setProperty(HtmlHandler.SESSIONNAME, tag.getSessionName());
                    try {
                        javax.servlet.jsp.PageContext pc = tag.getContextTag().getPageContext();
                        setProperty(Parameter.REQUEST, (javax.servlet.http.HttpServletRequest) pc.getRequest());
                        setProperty(Parameter.RESPONSE, (javax.servlet.http.HttpServletResponse) pc.getResponse());
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }


                public Cloud getCloud() {
                    try {
                        return tag.getCloudVar();
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }
                public java.util.Locale getLocale() {
                    try {
                        return tag.getLocale();
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }

                public void invalidate() {
                    try {
                        FormTag form = tag.getFormTag(false, null);
                        if (form != null) {
                            form.setValid(false);
                        }
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }

                public boolean isValid() {
                    try {
                        FormTag form = tag.getFormTag(false, null);
                        if (form != null) {
                            return form.isValid();
                        } else {
                            return true;
                        }
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }
                protected String prefix(String s) throws JspTagException {
                    return tag.getPrefix() + "_" + s;
                }
                public Object getValue(Field field) {
                    try {
                        Object found = tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()));
                        log.debug("found fv " + found);
                        return found;
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }
                public Object getValue(Field field, String part) {
                    try {
                        Object found = tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()) + "_" + part);
                        log.debug("found fv " + found);
                        return found;
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }
                public String getName(Field field) {
                    try {
                        return prefix(field.getName());
                    } catch (JspTagException te) {
                        throw new RuntimeException(te);
                    }
                }

                public <C> C setProperty(Parameter<C> name, C value) {
                    C prev = getProperty(name);
                    tag.getPageContext().setAttribute(name.getName(), value);
                    return prev;
                }
                public <C> C getProperty(Parameter<C> name) {
                    return (C) tag.getPageContext().getAttribute(name.getName());
                }

            };

    }


    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        return handler.input(request, node, field, search);
    }


    public String htmlInputId(Node node, Field field) throws JspTagException {
        return handler.id(request, field);
    }


    public String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException {
        return handler.check(request, node, field, errors);
    }


    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        return handler.set(request, node, field);
    }


    public String whereHtmlInput(Field field) throws JspTagException {
        throw new UnsupportedOperationException();
    }

    final protected String findString(Field field) throws JspTagException {
        String search = org.mmbase.util.Casting.toString(request.getValue(field));
        if (search == null || "".equals(search)) {
            return null;
        }
        return search;
    }


    public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException {
        handler.addParameter(request.getName(field), findString(field));
    }

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        return handler.search(request, field, query);
    }

    public void init() {
    }

}
