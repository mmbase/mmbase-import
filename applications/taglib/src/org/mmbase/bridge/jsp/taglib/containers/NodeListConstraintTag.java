/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListConstraintTag.java,v 1.1 2003-07-23 17:46:58 michiel Exp $
 */
public class NodeListConstraintTag extends CloudReferrerTag implements NodeListContainerReferrer {

    private static Logger log = Logging.getLoggerInstance(NodeListConstraintTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute field      = Attribute.NULL;
    protected Attribute value      = Attribute.NULL;


    protected Attribute field2     = Attribute.NULL; // not implemented
    protected Attribute operator   = Attribute.NULL; // not implemented



    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setField(String f) throws JspTagException {
        field = getAttribute(f);
    }

    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }


    public int doStartTag() throws JspTagException {        
        NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));

        Query query = c.getQuery();

        Step step = (Step) query.getSteps().get(0); // XXXX need a better idea !

        StepField stepField = null;
        String fieldName = field.getString(this);
        Iterator fields = query.getFields().iterator();
        while (fields.hasNext()) {
            StepField sf = (StepField) fields.next();
            if (sf.getFieldName().equals(fieldName)) {
                stepField = sf;
            }
        }
        if (stepField == null) {
            stepField = query.addField(step, getCloud().getNodeManager(step.getTableName()).getField(fieldName));
        }
        
        Constraint newConstraint = query.createConstraint(stepField, value.getValue(this));
        Constraint constraint = query.getConstraint();
        if (constraint != null) {
            newConstraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
            
        }
        query.setConstraint(newConstraint);
                
        return SKIP_BODY;
    }

}
