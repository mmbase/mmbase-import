/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A very simple tag to check if a relation may be created. It needs two nodes.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 * @version $Id: MayCreateRelationTag.java,v 1.13 2006-04-11 22:55:02 michiel Exp $
 */

public class MayCreateRelationTag extends MayWriteTag implements Condition {
    
    private static final Logger log = Logging.getLoggerInstance(MayCreateRelationTag.class);

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
        String roleStr = role.getString(this);
        RelationManager rm   = getCloudVar().getRelationManager(roleStr);
        Node sourceNode      = getNode(source.getString(this));
        Node destinationNode = getNode(destination.getString(this));
        
        boolean hasRelationManager = getCloudVar().hasRelationManager(sourceNode.getNodeManager(), 
                                                            destinationNode.getNodeManager(), roleStr);
        if ((hasRelationManager && rm.mayCreateRelation(sourceNode, destinationNode)) != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }

    }
}
