/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * NodeTag provides the fields of a node
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeTag.java 38670 2009-09-17 14:43:09Z michiel $
 */

public class NodeManagerTag extends AbstractNodeProviderTag implements BodyTag {
    private static final Logger log = Logging.getLoggerInstance(NodeManagerTag.class);

    private Attribute name    = Attribute.NULL;
    private Attribute notfound  = Attribute.NULL;


    public void setName(String n) throws JspTagException {
        this.name = getAttribute(n, false);
    }

    public void setNotfound(String i) throws JspTagException {
        notfound = getAttribute(i, true);
    }


    @Override
    public int doStartTag() throws JspTagException{
        NodeManager node = getCloudVar().getNodeManager(name.getString(this));
        setNodeVar(node);
        fillVars();
        //log.debug("found node " + node.getValue("gui()"));
        return EVAL_BODY;
    }


    /**
     * this method writes the content of the body back to the jsp page
     **/
    @Override
    public int doAfterBody() throws JspTagException { // write the body if there was one
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
        }
        return SKIP_BODY;
    }


    @Override
    public int doEndTag() throws JspTagException {
        super.doAfterBody(); // if modified
        return super.doEndTag();
    }

}
