/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;

/**
 * mm:composite makes it possible to connect constraints by 'OR'.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: QueryCompositeConstraintTag.java,v 1.13 2008-09-16 11:25:11 michiel Exp $
 */
public class QueryCompositeConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    // private static final Logger log = Logging.getLoggerInstance(NodeListCompositeConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute operator  = Attribute.NULL;

    protected Attribute inverse  = Attribute.NULL;

    private List<Constraint> constraints;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setOperator(String o) throws JspTagException {
        operator = getAttribute(o);
    }

    protected int getOperator() throws JspTagException {
        String op = operator.getString(this).toUpperCase();
        if (op.equals("OR")) {
            return CompositeConstraint.LOGICAL_OR;
        } else if (op.equals("AND") || op.length() == 0) {
            return CompositeConstraint.LOGICAL_AND;
        } else {
            throw new JspTagException("Unknown Field Compare Operator '" + op + "'");
        }
    }

    public void setInverse(String i) throws JspTagException {
        inverse = getAttribute(i, true);
    }

    public void addChildConstraint(Constraint cons) throws JspTagException {
        constraints.add(cons);
    }

    private Constraint addConstraint(Query query, int op, List<Constraint> constraints) throws JspTagException {
        Constraint newConstraint = null;
        for (Constraint constraint : constraints) {
            if (newConstraint == null) {
                newConstraint = constraint;
            } else {
                newConstraint = query.createConstraint(newConstraint, op, constraint);
            }
        }
        boolean not = inverse.getBoolean(this, false);
        if (newConstraint == null) {
            // Nothing added. Very interesting, what must happen now?
            switch(op) {
            case CompositeConstraint.LOGICAL_OR:
                if (not) {
                    return null;
                } else {
                    newConstraint = Queries.createMakeEmptyConstraint(query);
                    break;
                }
            case CompositeConstraint.LOGICAL_AND:
                if (not) {
                    newConstraint = Queries.createMakeEmptyConstraint(query);
                    not = false;
                    break;
                } else {
                    return null;
                }
            }
        }
        // if there is a OR or an AND tag, add
        // the constraint to that tag,
        // otherwise add it direct to the query
        QueryCompositeConstraintTag cons = findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);
        if (cons != null) {
            cons.addChildConstraint(newConstraint);
        } else {
            newConstraint = Queries.addConstraint(query, newConstraint);
        }
        if (not) {
            query.setInverse(newConstraint, true);
        }
        return newConstraint;
    }

    public int doStartTag() throws JspTagException {
        constraints = new ArrayList<Constraint>();
        return EVAL_BODY;
    }

    public int doAfterBody() throws JspTagException {
        Query query = getQuery(container);

        addConstraint(query, getOperator(), constraints);
        if(EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (java.io.IOException ioe){
                throw new JspTagException(ioe.toString());
            }
        }
        return SKIP_BODY;
    }
    public int doEndTag() throws JspTagException {
        constraints = null;
        return super.doEndTag();
    }



}
