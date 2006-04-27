/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import java.util.*;

import org.mmbase.bridge.jsp.taglib.edit.FormTag;
import org.mmbase.util.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.storage.search.*;
import org.mmbase.datatypes.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: AbstractTypeHandler.java,v 1.46 2006-04-27 09:31:59 michiel Exp $
 */

public abstract class AbstractTypeHandler implements TypeHandler {
    private static final Logger log = Logging.getLoggerInstance(AbstractTypeHandler.class);

    protected FieldInfoTag tag;
    protected EnumHandler eh;
    protected boolean gotEnumHandler = false;

    /**
     * Constructor for AbstractTypeHandler.
     */
    public AbstractTypeHandler(FieldInfoTag tag) {
        super();
        this.tag = tag;

    }
    public void init() {
        eh = null;
        gotEnumHandler = false;
    }


    protected EnumHandler getEnumHandler(Node node, Field field) throws JspTagException {
        if (gotEnumHandler) return eh;
        gotEnumHandler = true;
        DataType dt = field.getDataType();

        if (dt.getEnumerationValues(tag.getLocale(), tag.getCloudVar(), node, field) != null) {
            return new EnumHandler(tag, node, field);
        }

        // XXX: todo the following stuff may peraps be somehow wrapped to IntegerDataType itself;
        // but what to do with 200L??
        if (dt instanceof IntegerDataType) {
            IntegerDataType idt = (IntegerDataType) dt;
            final int min = idt.getMin() + (idt.isMinInclusive() ? 0 : 1);
            final int max = idt.getMax() - (idt.isMaxInclusive() ? 0 : 1);
            if ((long) max - min < 200L) {
                return new EnumHandler(tag, node, field) {
                        int i = min;
                        protected Iterator getIterator(Node node, Field field) {
                            return new Iterator() {
                                    public boolean hasNext() {
                                        return i <= max;
                                    }
                                    public Object next() {
                                        Integer value = new Integer(i++);
                                        return new Entry(value, value);
                                    }
                                    public void remove() {
                                        throw new UnsupportedOperationException();
                                    }
                                };
                        }
                    };
            }
        }
        if (dt instanceof LongDataType) {
            LongDataType ldt = (LongDataType) dt;
            final long min = ldt.getMin() + (ldt.isMinInclusive() ? 0 : 1);
            final long max = ldt.getMax() - (ldt.isMaxInclusive() ? 0 : 1);
            if ((double) max - min < 200.0) {
                return new EnumHandler(tag, node, field) {
                        long i = min;
                        protected Iterator getIterator(Node node, Field field) {
                            return new Iterator() {
                                    public boolean hasNext() {
                                        return i <= max;
                                    }
                                    public Object next() {
                                        Long value = new Long(i++);
                                        return new Entry(value, value);
                                    }
                                    public void remove() {
                                        throw new UnsupportedOperationException();
                                    }
                                };
                        }
                    };
            }
        }

        return null;
    }

    protected StringBuffer addExtraAttributes(StringBuffer buf) throws JspTagException {
        String options = tag.getOptions();
        if (options != null) {
            int i = options.indexOf("extra:");
            if (i > -1) {
                buf.append(" " + options.substring(i + 6) + " ");
            }
        }
        return buf;
    }

    /**
     * @since MMBase-1.8
     */
    protected String getClasses(Field field) {
        return "mm_validate mm_f_" + field.getName() + " mm_nm_" + field.getNodeManager().getName();
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        eh = getEnumHandler(node, field);
        if (eh != null) {
            return eh.htmlInput(node, field, search);
        }
        // default implementation.
        StringBuffer show =  new StringBuffer("<input type=\"text\" class=\"small " + getClasses(field) + "\" size=\"80\" ");
        addExtraAttributes(show);
        Object value = getFieldValue(node, field, ! search);
        show.append("name=\"").append(prefix(field.getName())).append("\" ");
        show.append("id=\"").append(prefixID(field.getName())).append("\" ");
        show.append("value=\"");
        show.append((value == null ? "" : Casting.toString(value)));
        show.append("\" />");
        return show.toString();
    }

    protected final Object getFieldValue(String fieldName) throws JspTagException {
        Object found = tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));
        return found;
    }
    protected boolean interpretEmptyAsNull(Field field) {
        return true;
    }

    protected Object cast(Object value, Node node, Field field) {
        return field.getDataType().cast(value, node, field);
    }
    protected Object getFieldValue(Node node, Field field, boolean useDefault) throws JspTagException {
        String fieldName = field.getName();
        Object value = getFieldValue(fieldName);
        if (value == null) {
            if (node != null) {
                value = node.isNull(fieldName) ? null : node.getValue(fieldName);
            } else if (useDefault) {
                value = field.getDataType().getDefaultValue();
            }
        }
        return value;
    }

    public String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException {
        eh = getEnumHandler(node, field);
        if (eh != null) {
            return eh.checkHtmlInput(node, field, errors);
        }
        String fieldName = field.getName();
        Object fieldValue = getFieldValue(fieldName);
        DataType dt = field.getDataType();
        if (fieldValue == null) {
            log.debug("Field value not found in context, using existing value ");
            fieldValue = getFieldValue(node, field, node == null);
        } else if (fieldValue.equals("") && ! field.isRequired()) {
            log.debug("Field value found in context is empty, interpreting as null");
            fieldValue = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Value for field " + fieldName + ": " + fieldValue);
        }
        Collection col = dt.validate(fieldValue, node, field);
        if (col.size() == 0) {
            // do actually set the field, because some datatypes need cross-field checking
            // also in an mm:form, you can simply commit.
            if (node != null && ! field.isReadOnly()) {
                Object oldValue = node.getValue(fieldName);
                if (fieldValue == null ? oldValue != null : ! fieldValue.equals(oldValue)) {
                    try {
                        if(log.isDebugEnabled()) {
                            log.debug("Setting " + fieldName + " to " + fieldValue);
                        }
                        if ("".equals(fieldValue) && interpretEmptyAsNull(field)) {
                            node.setValue(fieldName,  null);
                        } else {
                            node.setValue(fieldName,  fieldValue);
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
            return "";
        } else {
            FormTag form =  (FormTag) tag.findParentTag(FormTag.class, null, false);
            if (form != null) {
                form.setValid(false);
            }
            if (errors) {
                StringBuffer show = new StringBuffer("<div class=\"mm_check_error\">");
                Locale locale =  tag.getLocale();
                Iterator i = col.iterator();
                while (i.hasNext()) {
                    LocalizedString error = (LocalizedString) i.next();
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
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        String fieldName = field.getName();
        Object fieldValue = getFieldValue(node, field, false);
        Object oldValue = node.getValue(fieldName);
        if (fieldValue == null ? oldValue == null : fieldValue.equals(oldValue)) {
            return false;
        }  else {
            if ("".equals(fieldValue) && interpretEmptyAsNull(field)) {
                node.setValue(fieldName,  null);
            } else {
                node.setValue(fieldName,  fieldValue);
            }
            return true;
        }
    }



    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        eh = getEnumHandler(null, field);
        if (eh != null) {
            return eh.whereHtmlInput(field);
        }
        String string = findString(field);
        if (string == null) return null;
        return "( [" + field.getName() + "] =" + getSearchValue(string) + ")";
    }

    /**
     * The operator to be used by whereHtmlInput(field, query)
     * @since MMBase-1.7
     */
    protected int getOperator() {
        return FieldCompareConstraint.EQUAL;
    }
    /**
     * Converts the value to the actual value to be searched. (mainly targeted at StringHandler).
     * @since MMBase-1.7
     */
    protected String getSearchValue(String string) {
        return string;
    }

    /**
     * @since MMBase-1.7
     */
    final protected String findString(Field field) throws JspTagException {
        String fieldName = field.getName();
        String search = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(fieldName));
        if (search == null || "".equals(search)) {
            return null;
        }
        return search;
    }


    public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException  {
        eh = getEnumHandler(null, field);
        if (eh != null) {
            eh.paramHtmlInput(handler, field);
            return;
        }
        handler.addParameter(prefix(field.getName()), findString(field));
    }


    /**
     * Adds search constraint to Query object.
     * @return null if nothing to be searched, the constraint if constraint added
     */

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        eh = getEnumHandler(null, field);
        if (eh != null) {
            return eh.whereHtmlInput(field, query);
        }
        String value = findString(field);
        if (value != null) {
            String fieldName = field.getName();
            if (query.getSteps().size() > 1) {
                fieldName = field.getNodeManager().getName()+"."+fieldName;
            }
            Constraint con = Queries.createConstraint(query, fieldName, getOperator(), getSearchValue(value));
            Queries.addConstraint(query, con);
            return con;
        } else {
            return null;
        }
    }

    /**
     * Puts a prefix before a name. This is used in htmlInput and
     * useHtmlInput, they need it to get a reasonably unique value for
     * the name attribute of form elements.
     *
     */
    protected String prefix(String s) throws JspTagException {
        return tag.getPrefix() + "_" + s;
    }

    /**
     * Puts a prefix 'mm_' before an id in form fields. To be used in ccs etc..
     *
     * @param   s   Fieldname
     * @return  String with the id, like f.e. 'mm_title'
     */
    protected String prefixID(String s) throws JspTagException {
        String prefix = tag.getPrefix();
        if (! prefix.equals("")) prefix += "_";
        return "mm_" + prefix + s;
    }



}
