/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;


import org.mmbase.bridge.jsp.taglib.NodeTag;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A NodeProvider which creates a new node, which will be commited after the body. So, you
 * can use `setField's in the body.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CreateNodeTag.java,v 1.16 2003-08-27 21:33:40 michiel Exp $
 */

public class CreateNodeTag extends NodeTag {

    private static final Logger log = Logging.getLoggerInstance(CreateNodeTag.class);

    private Attribute nodeManager = Attribute.NULL;
    private Attribute makeUniques = Attribute.NULL;

    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t);
    }

    public void setMakeuniques(String u) throws JspTagException {
        makeUniques = getAttribute(u);
    }


    public int doStartTag() throws JspTagException{
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager(nodeManager.getString(this));
        if (nm == null) {
            throw new JspTagException("Could not find nodemanager " + nodeManager.getString(this));
        }
        Node node = nm.createNode();
        if (node == null) {
            throw new JspTagException("Could not create node of type " + nm.getName());
        }
        if (makeUniques.getBoolean(this, false)) {
            // smart stuff to avoid unique key constraint violiations
            FieldIterator fields = nm.getFields().fieldIterator();
            while (fields.hasNext()) {
                Field field = fields.nextField();
                log.debug("checking field " + field); 
                if (field.getType() == Field.TYPE_NODE) continue; // never mind, may be null
                // TODO: there is NO field.isNullable(), but NODE fields should be nullable and also 'owner' is a NODE field (which should never be touched)
                if (field.isUnique()) {
                    Object baseValue = node.getValue(field.getName());
                    int seq = 0;
                    if (baseValue == null) {
                        if (field.getType() == Field.TYPE_STRING) {
                            baseValue = field.getGUIName();
                        } else {
                            baseValue = new Integer(seq);
                        }
                    }
                    boolean found = false;
                    while (! found) {
                        NodeQuery query = nm.createQuery();
                        Constraint cons;
                        if (field.getType() == Field.TYPE_STRING) {
                            cons = query.createConstraint(query.getStepField(field), (String) baseValue + seq);
                        } else {
                            cons = query.createConstraint(query.getStepField(field), new Integer(seq));
                        }
                        query.setConstraint(cons);
                        if (cloud.getList(query).size() == 0) {
                            found = true;
                            break;
                        }
                        seq++;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Setting field " + field.getName() + " to unique value " + baseValue + seq);
                    }
                    if (field.getType() == Field.TYPE_STRING) {
                        node.setValue(field.getName(), (String) baseValue + seq);
                    } else {
                        node.setIntValue(field.getName(), seq);
                    }
                }
            }
        }
        setNodeVar(node);
        setModified();
        if (log.isDebugEnabled()) {
            log.debug("created node " + node.getNumber() + ": " + node.getValue("gui()"));
        }
        fillVars();
        return EVAL_BODY_BUFFERED;
    }


}
