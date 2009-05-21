/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.*;
//import org.mmbase.util.logging.*;

/**
 * The most general 'constraint' tag. Constraints are constructed with a bunch of attributes.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 */
public class QueryConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    //private static final Logger log = Logging.getLoggerInstance(NodeListConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute operator   = Attribute.NULL;

    protected Attribute field      = Attribute.NULL;
    protected Attribute value      = Attribute.NULL;
    protected Attribute referid    = Attribute.NULL;

    protected Attribute value2     = Attribute.NULL; // needed for BETWEEN
    protected Attribute referid2   = Attribute.NULL; // needed for BETWEEN

    protected Attribute inverse    = Attribute.NULL;

    protected Attribute field2     = Attribute.NULL;

    protected Attribute caseSensitive = Attribute.NULL;

    protected Attribute part       = Attribute.NULL; // for dates

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setField(String f) throws JspTagException {
        field = getAttribute(f);
    }

    public void setField2(String f) throws JspTagException {
        field2 = getAttribute(f);
    }

    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }

    public void setValue2(String v) throws JspTagException {
        value2 = getAttribute(v);
    }

    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }

    public void setReferid2(String r) throws JspTagException {
        referid2 = getAttribute(r);
    }

    public void setOperator(String o) throws JspTagException {
        operator = getAttribute(o);
    }

    public void setInverse(String i) throws JspTagException {
        inverse = getAttribute(i);
    }

    public void setCasesensitive(String c) throws JspTagException {
        caseSensitive = getAttribute(c);
    }

    public void setPart(String p) throws JspTagException {
        part = getAttribute(p);
    }

    public boolean getCaseSensitive() throws JspTagException {
        String cs = caseSensitive.getString(this).toUpperCase();
        if (cs.length() == 0 || cs.equals("FALSE")) {
            return false;
        }  else if (cs.equals("TRUE")) {
            return true;
        } else {
            throw new JspTagException("Unknown value '" + cs + "' for casesensitive attribute");
        }
    }

    private Constraint addConstraint(Query query) throws JspTagException {
        int op = Queries.getOperator(operator.getString(this));

        Object compareValue;
        if (value != Attribute.NULL) {
            if (referid != Attribute.NULL || field2 != Attribute.NULL) throw new JspTagException("Can specify only one of value, referid and field2 attributes on constraint tag");
            if (op == Queries.OPERATOR_IN) {
                compareValue = value.getList(this);
            } else {
                compareValue = value.getString(this);
            }
        } else if (referid != Attribute.NULL) {
            if (field2 != Attribute.NULL) throw new JspTagException("Can specify only one of value, referid and field2 attributes on constraint tag");
            compareValue = getObject(referid.getString(this));
        } else if (field2 != Attribute.NULL) {
            compareValue = query.createStepField(field2.getString(this));
        } else {
            if (op != Queries.OPERATOR_NULL) {
                throw new JspTagException("Should specify one of value, referid and field2 attributes on constraint tag (unless operator is NULL)");
            } else {
                compareValue = null;
            }
        }

        Object compareValue2 = null;
        if (op == Queries.OPERATOR_BETWEEN) {
            if (value2 != Attribute.NULL) {
                if (referid != Attribute.NULL) throw new JspTagException("Can specify only one of value2, referid2 attributes on constraint tag");
                compareValue2 = value2.getString(this);
            } else if (referid2 != Attribute.NULL) {
                compareValue2 = getObject(referid2.getString(this));
            } else {
                throw new JspTagException("Should specify one of value2, referid2 attributes on constraint tag if operator is 'BETWEEN'");
            }
        }

        Constraint newConstraint = Queries.createConstraint(query, field.getString(this), Queries.getOperator(operator.getString(this)),
                                                            compareValue, compareValue2, getCaseSensitive(), Queries.getDateTimePart(part.getString(this)));


        QueryCompositeConstraintTag cons = findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);

        if (cons == null && newConstraint instanceof FieldValueConstraint && ! inverse.getBoolean(this, false)) {
            // some arrangement for if this happens to be a constraint on a number field
            FieldValueConstraint fv = (FieldValueConstraint) newConstraint;
            if (fv.getOperator() == FieldCompareConstraint.EQUAL && fv.getField().getFieldName().equals("number")) {
                String number = org.mmbase.util.Casting.toString(fv.getValue());
                if (query.getCloud().hasNode(number)) {
                    int n = query.getCloud().getNode(number).getNumber();
                    if (n > 0) { // TODO, should this situation perhaps be handled by the
                                 // Query-Handler?
                        query.addNode(fv.getField().getStep(), n);
                        return null;
                    }
                }
            }

        }


        //buildConstraint(query, field.getString(this), field2.getString(this), getOperator(), value.getString(this), value2.getString(this), getCaseSensitive());

        // if there is a OR or an AND tag, add
        // the constraint to that tag,
        // otherwise add it direct to the query

        if (cons != null) {
            cons.addChildConstraint(newConstraint);
        } else {
            newConstraint = Queries.addConstraint(query, newConstraint);
        }
        return newConstraint;
    }

    public int doStartTag() throws JspTagException {
        Query query = getQuery(container);
        Constraint cons = addConstraint(query);
        if (inverse.getBoolean(this, false)) {
            query.setInverse(cons, true);
        }
        findWriter(false); // just to call haveBody.., because constraint is not officially a
                           // writerreferer (but e.g. _ can be used in attributes)
        return SKIP_BODY;
    }

}
