/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.util.functions.*;
import org.mmbase.util.Casting;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.bridge.*;

/**
 * Lives under a nodeprovider, as a condition tag, reporting whether or not the node is of a certain builder.
 *
 * @author Bert Huijgen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public class InstanceOfTag extends NodeReferrerTag implements Condition {

    private Attribute inverse     = Attribute.NULL;
    private Attribute nodemanager = Attribute.NULL;
    private Attribute descendants = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }

    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public void setNodemanager(String nm) throws JspTagException {
        nodemanager = getAttribute(nm);
    }

    public void setDescendants(String d) throws JspTagException {
        descendants = getAttribute(d, true);
    }

    public int doStartTag() throws JspTagException{

        Node node = getNode();
        NodeManager nm = node.getNodeManager();
        NodeManager compareTo = node.getCloud().getNodeManager(nodemanager.getString(this));
        boolean result = nm.equals(compareTo) || (descendants.getBoolean(this, true) && compareTo.getDescendants().contains(nm));

        if (result != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            try{
                if(bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch(java.io.IOException e){
                throw new JspTagException("IO Error: " + e.getMessage());
            }
        }
        return SKIP_BODY;
    }

}
