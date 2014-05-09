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
 * Alias as constraint.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: QueryAliasConstraintTag.java,v 1.7 2005-01-30 16:46:34 nico Exp $
 */
public class QueryAliasConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    // private static final Logger log = Logging.getLoggerInstance(NodeListAliasConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute element    = Attribute.NULL;
    protected Attribute name       = Attribute.NULL;

    protected Attribute inverse    = Attribute.NULL;

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


    protected Integer getAlias(String name) throws JspTagException {
        Cloud cloud = getCloudVar();
        Node node = cloud.getNode(name);
        return new Integer(node.getNumber());
    }

    protected SortedSet getAliases(List names) throws JspTagException {
        SortedSet set = new TreeSet();
        Iterator i = names.iterator();
        while (i.hasNext()) {
            set.add(getAlias((String) i.next()));
        }
        return set;
    }



    public int doStartTag() throws JspTagException {
        QueryContainer c = (QueryContainer) findParentTag(QueryContainer.class, (String) container.getValue(this));
        Query query = c.getQuery();
        String elementString = element.getString(this);
        Step step;
        if (elementString.equals("")) {
            if (query instanceof NodeQuery) {
                step = ((NodeQuery) query).getNodeStep();
            } else {
                throw new JspTagException("Don't know on what path element the alias constraint must be applied. Use the 'element' attribute");
            }
        } else {
            step = query.getStep(elementString);
        }

        if (step == null) {
            throw new JspTagException("No element '" + element.getString(this) + "' in path '" + query.getSteps() + "'");
        }
        StepField stepField = query.createStepField(step, "number");

        Constraint newConstraint = query.createConstraint(stepField, getAliases(name.getList(this)));

        if (newConstraint != null) {
            if (inverse.getBoolean(this, false)) {
                query.setInverse(newConstraint, true);
            }

            // if there is a OR or an AND tag, add
            // the constraint to that tag,
            // otherwise add it direct to the query
            QueryCompositeConstraintTag cons = (QueryCompositeConstraintTag) findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);
            if (cons != null) {
                cons.addChildConstraint(newConstraint);
            } else {
                newConstraint = Queries.addConstraint(query, newConstraint);
            }
        }

        return SKIP_BODY;
    }

}
