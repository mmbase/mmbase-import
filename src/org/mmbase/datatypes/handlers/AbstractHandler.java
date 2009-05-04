/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers;

import org.mmbase.util.Casting;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;



/**
 * Handlers can be associated to DataTypes, but different Handler can be associated with different
 * content types. The main implementation will of course be one that produces HTML, like forms, and
 * post and things like that.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public abstract class AbstractHandler<C>  implements Handler<C> {
    private static final Logger log = Logging.getLoggerInstance(AbstractHandler.class);

    /**
     * Puts a prefix 'mm_' before an id in form fields. To be used in ccs etc..
     */
    protected String id(String s) {
        return s;
    }


    protected boolean interpretEmptyAsNull(Field field) {
        return true;
    }

    protected Object cast(Object value, Node node, Field field) {
        return field.getDataType().cast(value, node, field);
    }


    protected Object getValue(Node node, String fieldName) {
        return node.getValue(fieldName);
    }
    /**
     * Returns the field value as specified by the client's post.
     * @param node This parameter could be used if the client does not fully specify the field's value (possible e.g. with Date fields). The existing specification could be used then.
     */
    protected Object getFieldValue(Request request, Node node, Field field) {
        Object found = request.getValue(field);
        if (interpretEmptyAsNull(field) && "".equals(found)) found = null;
        return  found;
    }
    /**
     * Returns the field value to be used in the page.
     */
    protected Object getFieldValue(Request request, Node node, Field field, boolean useDefault) {
        Object  value = getFieldValue(request, node, field);
        if (value == null) {
            String fieldName = field.getName();
            if (node != null) {
                value = node.isNull(fieldName) ? null : getValue(node, fieldName);
            } else if (useDefault) {
                value = field.getDataType().getDefaultValue(request.getLocale(), request.getCloud(), field);
            }
        }
        return value;
    }


    /**
     * The operator to be used by search(request, field, query)
     */
    protected int getOperator(Field field) {
        return FieldCompareConstraint.EQUAL;
    }
    /**
     * Converts the value to the actual value to be searched. (mainly targeted at StringHandler).
     */
    protected String getSearchValue(String string, Field field, int operator) {
        if (operator == FieldCompareConstraint.LIKE) {
            return "%" + string.toUpperCase() + "%";
        } else {
            return string;
        }
    }
    /**
     */
    final protected String findString(Request request, Field field) {
        String fieldName = field.getName();

        String search = Casting.toString(request.getValue(field));
        if (search == null || "".equals(search)) {
            return null;
        }
        return search;
    }


    public  Constraint search(Request request, Field field, Query query) {
        String value = findString(request, field);
        if (value != null) {

            String fieldName = field.getName();
            if (query.getSteps().size() > 1) {
                fieldName = field.getNodeManager().getName() + "." + fieldName;
            }
            int operator = getOperator(field);
            String searchValue = getSearchValue(value, field, operator);
            if (log.isDebugEnabled()) {
                log.debug("Found value " + value + " -> " + searchValue + " for field " + fieldName);
            }
            Constraint con = Queries.createConstraint(query, fieldName, operator, searchValue);
            Queries.addConstraint(query, con);
            return con;
        } else {
            return null;
        }

    }

    protected void setValue(Node node, String fieldName, Object value) {
        node.setValue(fieldName, value);
    }

    abstract public C check(Request request, Node node, Field field, boolean errors);

    public boolean set(Request request, Node node, Field field) {
        String fieldName = field.getName();
        Object fieldValue = getFieldValue(request, node, field, false);
        if (interpretEmptyAsNull(field) && "".equals(fieldValue)) {
            fieldValue = null;
        }
        Object oldValue = node.getValue(fieldName);

        if (fieldValue == null ? oldValue == null : fieldValue.equals(oldValue)) {
            return false;
        }  else {
            if ("".equals(fieldValue) && interpretEmptyAsNull(field)) {
                setValue(node, fieldName,  null);
            } else {
                setValue(node, fieldName,  fieldValue);
            }
            return true;
        }
    }

    abstract public C input(Request request, Node node, Field field, boolean search);



}
