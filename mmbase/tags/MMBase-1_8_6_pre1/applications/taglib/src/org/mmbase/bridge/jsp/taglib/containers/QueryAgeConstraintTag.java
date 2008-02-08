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
 * @version $Id: QueryAgeConstraintTag.java,v 1.6 2005-05-28 09:10:15 michiel Exp $
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


    protected int getDayMark(int age) throws JspTagException {
        log.debug("finding day mark for " + age + " days ago");
        Cloud cloud = getCloudVar();
        NodeManager dayMarks = cloud.getNodeManager("daymarks");
        NodeQuery query = dayMarks.createQuery();
        StepField step = query.createStepField("daycount");
        int currentDay = (int) (System.currentTimeMillis()/(1000*60*60*24));
        Integer day = new Integer(currentDay  - age);
        if (log.isDebugEnabled()) {
            log.debug("today : " + currentDay + " requested " + day);
        }
        Constraint constraint = query.createConstraint(step, FieldCompareConstraint.LESS_EQUAL, day);
        query.setConstraint(constraint);
        query.addSortOrder(query.createStepField("daycount"), SortOrder.ORDER_DESCENDING);
        query.setMaxNumber(1);

        NodeList result = dayMarks.getList(query);
        if (result.size() == 0) {
            return -1;
        } else {
            return result.getNode(0).getIntValue("mark");
        }


    }



    public int doStartTag() throws JspTagException {

        if (minAge == Attribute.NULL && maxAge == Attribute.NULL) {
            throw new JspTagException("Either 'minage' or 'maxage' (or both) attributes must be present");
        }
        QueryContainer c = (QueryContainer) findParentTag(QueryContainer.class, (String) container.getValue(this));
        Query query = c.getQuery();

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

        Constraint newConstraint = null;

        int minAgeInt = minAge.getInt(this, -1);
        int maxAgeInt = maxAge.getInt(this, -1);
        // if minimal age given:
        // you need the day marker of the day after that (hence -1 in code below inside the getDayMark), node have to have this number or lower
        // if maximal age given:
        // daymarker object of that age must be included, but last object of previous day not, hece the +1 outside the getDayMark

        if (maxAgeInt != -1 && minAgeInt > 0) {
            int maxMarker = getDayMark(maxAgeInt);
            if (maxMarker > 0) {
                // BETWEEN constraint
                newConstraint = query.createConstraint(stepField, new Integer(maxMarker + 1), new Integer(getDayMark(minAgeInt - 1)));
            } else {
                newConstraint = query.createConstraint(stepField, FieldCompareConstraint.LESS_EQUAL, new Integer(getDayMark(minAgeInt - 1)));
            }
        } else if (maxAgeInt != -1) { // only on max
            int maxMarker = getDayMark(maxAgeInt);
            if (maxMarker > 0) {
                newConstraint = query.createConstraint(stepField, FieldCompareConstraint.GREATER_EQUAL, new Integer(maxMarker + 1));
            }
        } else if (minAgeInt > 0) {
            newConstraint = query.createConstraint(stepField, FieldCompareConstraint.LESS_EQUAL, new Integer(getDayMark(minAgeInt - 1)));
        } else {
            // both unspecified
        }

        if (newConstraint != null) {
            if (inverse.getBoolean(this, false)) {
                query.setInverse(newConstraint, true);
            }

            // if there is a OR or an AND tag, add
            // the constraint to that tag,
            // otherwise add it direct to the query
            QueryCompositeConstraintTag cons = (QueryCompositeConstraintTag) findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);
            if (cons!=null) {
                cons.addChildConstraint(newConstraint);
            } else {
                Queries.addConstraint(query, newConstraint);
            }
        }

        return SKIP_BODY;
    }

}
