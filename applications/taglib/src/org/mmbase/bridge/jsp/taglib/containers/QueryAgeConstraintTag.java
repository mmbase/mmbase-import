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
 * @version $Id: QueryAgeConstraintTag.java,v 1.1 2003-12-18 09:05:45 michiel Exp $
 * @see    org.mmbase.module.builders.DayMarkers
 */
public class QueryAgeConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(QueryAgeConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute field      = Attribute.NULL;
    protected Attribute minAge     = Attribute.NULL;
    protected Attribute maxAge     = Attribute.NULL;
    protected Attribute inverse    = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setField(String f) throws JspTagException { // default to 'number'
        field = getAttribute(f);
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


    protected Integer getDayMark(int age) throws JspTagException {
        log.debug("finding day mark for " + age + " days ago");
        Cloud cloud = getCloud();
        NodeManager dayMarks = cloud.getNodeManager("daymarks");
        NodeQuery query = dayMarks.createQuery();
        StepField step = query.createStepField("daycount");
        int currentDay = (int) (System.currentTimeMillis()/(1000*60*60*24));
        Integer day = new Integer(currentDay  - age);
        Constraint constraint = query.createConstraint(step, FieldCompareConstraint.LESS_EQUAL, day);
        query.setConstraint(constraint);
        query.addSortOrder(query.createStepField("daycount"), SortOrder.ORDER_DESCENDING);
        query.setMaxNumber(1);

        NodeList result = cloud.getList(query);
        if (result.size() == 0) {
            return new Integer(-1);
        } else {
            return new Integer(result.getNode(0).getIntValue("mark"));
        }


    }



    public int doStartTag() throws JspTagException {

        if (minAge == Attribute.NULL && maxAge == Attribute.NULL) {
            throw new JspTagException("Either 'minage' or 'maxage' (or both) attributes must be present");
        }
        QueryContainer c = (QueryContainer) findParentTag(QueryContainer.class, (String) container.getValue(this));
        Query query = c.getQuery();

        String fieldName;
        if (field == Attribute.NULL) {
            fieldName = "number";
        } else {
            fieldName = field.getString(this);
        }

        StepField stepField = query.createStepField(fieldName);

        Constraint newConstraint = null;

        int minAgeInt = minAge.getInt(this, -1);
        int maxAgeInt = maxAge.getInt(this, -1);

        if (maxAgeInt != -1 && minAgeInt > 0) {
            Integer maxMarker = getDayMark(maxAgeInt);
            if (maxMarker.intValue() > 0) {
                newConstraint = query.createConstraint(stepField, maxMarker, getDayMark(minAgeInt));
            } else{
                newConstraint = query.createConstraint(stepField, FieldCompareConstraint.LESS_EQUAL, getDayMark(minAgeInt));
            }
        } else if (maxAgeInt != -1) { // only on max
            Integer maxMarker = getDayMark(maxAgeInt);
            if (maxMarker.intValue() > 0) {
                newConstraint = query.createConstraint(stepField, FieldCompareConstraint.GREATER_EQUAL, maxMarker);
            }
        } else if (minAgeInt > 0) {
            newConstraint = query.createConstraint(stepField, FieldCompareConstraint.LESS_EQUAL, getDayMark(minAgeInt));
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
                newConstraint = Queries.addConstraint(query, newConstraint);
            }
        }

        return SKIP_BODY;
    }

}
