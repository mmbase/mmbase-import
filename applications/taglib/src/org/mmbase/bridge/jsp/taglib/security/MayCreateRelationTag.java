/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.jsp.taglib.Condition;


/**
* A very simple tag to check if a relation may be created.
* 
* @author Jaco de Groot
*/
public class MayCreateRelationTag extends MayWriteTag implements Condition {
    private String type;
    private String source;
    private String destination;

    public void setType(String type) {
        this.type = type;
    }

    public void setSourceNode(String source) {
        this.source = source;
    }

    public void setDestinationNode(String destination) {
        this.destination = destination;
    }
               
    public int doStartTag() throws JspTagException {
        Cloud cloud = getCloud();
        RelationManager rm = cloud.getRelationManager(type);
        Node src = cloud.getNode(source);
        Node dst = cloud.getNode(destination);
        if (rm.mayCreateRelation(src, dst) != inverse) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

}
