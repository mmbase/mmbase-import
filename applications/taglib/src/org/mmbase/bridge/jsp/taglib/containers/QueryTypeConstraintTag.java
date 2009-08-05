/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import java.util.*;

/**
 * Type as constraint.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: QueryTypeConstraintTag.java,v 1.9 2008-06-27 09:07:10 michiel Exp $
 */
public class QueryTypeConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    // private static final Logger log = Logging.getLoggerInstance(NodeListAliasConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute element    = Attribute.NULL;
    protected Attribute name       = Attribute.NULL;

    protected Attribute inverse    = Attribute.NULL;
    protected Attribute descendants  = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setElement(String e) throws JspTagException {
        element = getAttribute(e);
    }

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }


    public void setInverse(String i) throws JspTagException {
        inverse = getAttribute(i);
    }

    public void setDescendants(String d) throws JspTagException {
        descendants = getAttribute(d);
    }


    protected SortedSet<Integer> getOTypes(List<String> names) throws JspTagException {
        Cloud cloud = getCloudVar();
        SortedSet<Integer> set = new TreeSet<Integer>();
        Iterator<String> i = names.iterator();
        boolean desc = descendants.getBoolean(this, true);
        while (i.hasNext()) {
            NodeManager nm = cloud.getNodeManager(i.next());
            set.add(nm.getNumber());
            if (desc) {
                NodeManagerIterator j = nm.getDescendants().nodeManagerIterator();
                while (j.hasNext()) {
                    set.add(j.nextNodeManager().getNumber());
                }
            }
        }
        return set;
    }



    public int doStartTag() throws JspTagException {
        Query query = getQuery(container);
        String elementString = element.getString(this);
        Step step;
        if (elementString.length() == 0) {
            if (query instanceof NodeQuery) {
                step = ((NodeQuery) query).getNodeStep();
            } else {
                throw new JspTagException("Don't know on what path element the type constraint must be applied. Use the 'element' attribute");
            }
        } else {
            step = query.getStep(elementString);
        }
        if (step == null) {
            throw new JspTagException("No element '" + element.getString(this) + "' in path '" + query.getSteps() + "'");
        }
        StepField stepField = query.createStepField(step, "otype");

        Constraint newConstraint = query.createConstraint(stepField, getOTypes(name.getList(this)));

        if (newConstraint != null) {
            if (inverse.getBoolean(this, false)) {
                query.setInverse(newConstraint, true);
            }

            // if there is a OR or an AND tag, add
            // the constraint to that tag,
            // otherwise add it direct to the query
            QueryCompositeConstraintTag cons = findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);
            if (cons != null) {
                cons.addChildConstraint(newConstraint);
            } else {
                Queries.addConstraint(query, newConstraint);
            }
        }

        return SKIP_BODY;
    }

}
