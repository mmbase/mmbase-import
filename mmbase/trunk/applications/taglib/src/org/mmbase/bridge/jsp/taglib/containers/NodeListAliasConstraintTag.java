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
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Alias as constraint.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListAliasConstraintTag.java,v 1.1 2003-12-02 10:12:27 michiel Exp $
 */
public class NodeListAliasConstraintTag extends CloudReferrerTag implements NodeListContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(NodeListAliasConstraintTag.class);

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
        Cloud cloud = getCloud();
        Node node = cloud.getNode(name);
        return new Integer(node.getNumber());
    }



    public int doStartTag() throws JspTagException {
        NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));
        Query query = c.getQuery();

        Step step = query.getStep(element.getString(this));
        
        StepField stepField = query.createStepField(step, "number");

        Constraint newConstraint = null;
        newConstraint = query.createConstraint(stepField, getAlias(name.getString(this)));

        if (newConstraint != null) {
            if (inverse.getBoolean(this, false)) {
                query.setInverse(newConstraint, true);
            }

            // if there is a OR or an AND tag, add
            // the constraint to that tag,
            // otherwise add it direct to the query
            NodeListCompositeConstraintTag cons = (NodeListCompositeConstraintTag) findParentTag(NodeListCompositeConstraintTag.class, (String) container.getValue(this), false);
            if (cons != null) {
                cons.addChildConstraint(newConstraint);
            } else {
                newConstraint = NodeListConstraintTag.addConstraintToQuery(query, newConstraint);
            }
        }

        return SKIP_BODY;
    }

}
