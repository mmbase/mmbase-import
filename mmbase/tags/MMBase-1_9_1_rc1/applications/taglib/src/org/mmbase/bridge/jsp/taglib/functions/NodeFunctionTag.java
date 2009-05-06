/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Notfound;
import org.mmbase.util.Casting;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: NodeFunctionTag.java,v 1.15 2008-08-14 13:58:49 michiel Exp $
 */
public class NodeFunctionTag extends AbstractFunctionTag implements NodeProvider, FunctionContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(NodeFunctionTag.class);

    protected  NodeProviderHelper nodeHelper = new NodeProviderHelper(this); // no m.i. and there are more nodeprovider which cannot extend this, they can use the same trick.

    public void setJspvar(String jv) {
        nodeHelper.setJspvar(jv);
    }


    public Node getNodeVar() {
        return nodeHelper.getNodeVar();
    }


    protected void setNodeVar(Node node) {
        nodeHelper.setNodeVar(node);
    }

    private Attribute notfound = Attribute.NULL;

    public void setNotfound(String i) throws JspTagException {
        notfound = getAttribute(i);
    }

    protected void fillVars() throws JspTagException {
        nodeHelper.fillVars();
    }

    public Query getGeneratingQuery() throws JspTagException {
        return nodeHelper.getGeneratingQuery();
    }
    /**
     * @since MMBase-1.8
     */
    public void setCommitonclose(String c) throws JspTagException {
        nodeHelper.setCommitonclose(c);
    }


    public int doEndTag() throws JspTagException {
        nodeHelper.doEndTag();
        return super.doEndTag();
    }

    public int doStartTag() throws JspTagException {
        initTag();
        Object value = getFunctionValue(false); // don't register, 'fillVars' will do.
        if (value == null || "".equals(value)) {
            switch(Notfound.get(notfound, this)) {
            case Notfound.MESSAGE:
                try {
                    getPageContext().getOut().write("Function value was null");
                    } catch (java.io.IOException ioe) {
                        log.warn(ioe);
                    }
                    return SKIP_BODY;
            case Notfound.LOG:
                log.warn("Function value was null");
            case Notfound.SKIP:         {
                return SKIP_BODY;
            }
            case Notfound.THROW:  {
              throw new TaglibException("Function value was null");
            }
            default:
            }
        }
        Node node = Casting.toNode(value, getCloudVar());
        setNodeVar(node);
        fillVars();
        return  EVAL_BODY;
    }

    /**
     * this method writes the content of the body back to the jsp page
     **/
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
        return nodeHelper.doAfterBody();
    }

    public void doFinally() {
        nodeHelper.doFinally();
    }
}
