/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.Relation;

import org.mmbase.bridge.jsp.taglib.NodeTag;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A tag lib to create relations.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateRelationTag extends NodeTag {

    private static final Logger log = Logging.getLoggerInstance(CreateRelationTag.class);

    private Attribute role = Attribute.NULL;
    private Attribute source = Attribute.NULL;
    private Attribute destination = Attribute.NULL;

    public void setRole(String r) throws JspTagException {
        role = getAttribute(r);
    }

    public void setSource(String s) throws JspTagException {
        source = getAttribute(s);
    }
    public void setDestination(String d) throws JspTagException {
        destination = getAttribute(d);
    }

    public int doStartTag() throws JspTagException {
        RelationManager rm = getCloudVar().getRelationManager(role.getString(this));
        Node sourceNode      = getNode(source.getString(this));
        Node destinationNode = getNode(destination.getString(this));

        if (log.isDebugEnabled()) {
            log.debug("cloud from relationmanager " + rm.getCloud().getName());
            log.debug("cloud from source node " + sourceNode.getCloud().getName());
            log.debug("cloud from dest node " + destinationNode.getCloud().getName());
        }

        Relation r = rm.createRelation(sourceNode, destinationNode);
        r.commit();

        setNodeVar(r);
        fillVars();
        return EVAL_BODY;
    }

}
