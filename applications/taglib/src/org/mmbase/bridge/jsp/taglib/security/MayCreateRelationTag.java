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


/**
 * A very simple tag to check if a relation may be created. It needs two nodes.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 * @version $Id: MayCreateRelationTag.java,v 1.5.2.1 2004-07-10 12:13:05 nico Exp $
 */

public class MayCreateRelationTag extends MayWriteTag implements Condition {
    private String role;
    private String source;
    private String destination;

    public void setRole(String r) throws JspTagException {
        role = getAttributeValue(r);
    }

    public void setSource(String s) throws JspTagException {
        source = getAttributeValue(s);
    }

    public void setDestination(String d) throws JspTagException {
        destination = getAttributeValue(d);
    }

    public int doStartTag() throws JspTagException {
        RelationManager rm   = getProviderCloudVar().getRelationManager(role);
        Node sourceNode      = getNode(source);
        Node destinationNode = getNode(destination);

        if (rm.mayCreateRelation(sourceNode, destinationNode) != getInverse()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

}
