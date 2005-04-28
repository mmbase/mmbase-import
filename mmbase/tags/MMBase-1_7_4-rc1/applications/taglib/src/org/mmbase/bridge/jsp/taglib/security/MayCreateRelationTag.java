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

/**
 * A very simple tag to check if a relation may be created. It needs two nodes.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 * @version $Id: MayCreateRelationTag.java,v 1.5.2.3 2005-03-14 18:33:24 michiel Exp $
 */

public class MayCreateRelationTag extends MayWriteTag implements Condition {
    private Attribute role    = Attribute.NULL;    
    private Attribute source  = Attribute.NULL;
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
        RelationManager rm   = getCloudVar().getRelationManager(role.getString(this));
        Node sourceNode      = getNode(source.getString(this));
        Node destinationNode = getNode(destination.getString(this));

        if (rm.mayCreateRelation(sourceNode, destinationNode) != getInverse()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

}
