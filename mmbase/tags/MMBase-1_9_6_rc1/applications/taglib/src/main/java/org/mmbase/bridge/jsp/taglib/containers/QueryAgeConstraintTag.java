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
import org.mmbase.util.logging.*;

/**
 * Cognate of daymarkers.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 * @see    org.mmbase.module.builders.DayMarkers
 */
public class QueryAgeConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(QueryAgeConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute field      = Attribute.NULL;
    protected Attribute element    = Attribute.NULL;
    protected Attribute minAge     = Attribute.NULL;
    protected Attribute maxAge     = Attribute.NULL;
    protected Attribute inverse    = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * @deprecated Use {@link #setElement}
     */
    public void setField(String f) throws JspTagException { // default to 'number'
        field = getAttribute(f);
    }

    public void setElement(String e) throws JspTagException {
        element = getAttribute(e);
    }

    public void setMinage(String a) throws JspTagException {
        minAge = getAttribute(a);
    }

    public void setMaxage(String a) throws JspTagException {
        maxAge = getAttribute(a);
    }

    public void setInverse(String i) throws JspTagException {
        inverse = getAttribute(i);
    }



    public int doStartTag() throws JspTagException {

        if (minAge == Attribute.NULL && maxAge == Attribute.NULL) {
            throw new JspTagException("Either 'minage' or 'maxage' (or both) attributes must be present");
        }
        Query query = getQuery(container);


        String fieldName;
        if (field == Attribute.NULL && element == Attribute.NULL) {
            fieldName = "number";
        } else if (field != Attribute.NULL) {
            if(element != Attribute.NULL) {
                throw new JspTagException("Could not specify both 'field' and 'element' attributes on ageconstraint");
            }
            fieldName = field.getString(this);
        } else {
            fieldName = element.getString(this) + ".number";
        }

        StepField stepField = query.createStepField(fieldName);


        int minAgeInt = minAge.getInt(this, -1);
        int maxAgeInt = maxAge.getInt(this, -1);

        Constraint newConstraint = Queries.createAgeConstraint(query, stepField.getStep(), minAgeInt, maxAgeInt);

        // if minimal age given:
        // you need the day marker of the day after that (hence -1 in code below inside the getDayMark), node have to have this number or lower
        // if maximal age given:
        // daymarker object of that age must be included, but last object of previous day not, hece the +1 outside the getDayMark


        if (newConstraint != null) {
            if (inverse.getBoolean(this, false)) {
                query.setInverse(newConstraint, true);
            }

            // if there is a OR or an AND tag, add
            // the constraint to that tag,
            // otherwise add it direct to the query
            QueryCompositeConstraintTag cons = findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);
            if (cons!=null) {
                cons.addChildConstraint(newConstraint);
            } else {
                Queries.addConstraint(query, newConstraint);
            }
        }

        return SKIP_BODY;
    }

}
