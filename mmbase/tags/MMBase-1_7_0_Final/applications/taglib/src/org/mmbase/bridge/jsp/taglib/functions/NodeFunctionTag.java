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
import org.mmbase.util.logging.*;


/**
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: NodeFunctionTag.java,v 1.2 2004-02-11 20:40:12 keesj Exp $
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
    

    protected void fillVars() throws JspTagException {    
        nodeHelper.fillVars();
    }
               
    public void setModified() {
        nodeHelper.setModified();
    }

    protected boolean getModified() {
        return nodeHelper.getModified();
    }
    
    public int doEndTag() throws JspTagException {
        return nodeHelper.doEndTag();
    }

    public int doStartTag() throws JspTagException {
        Object value =  getFunctionValue();
        Node node;
        if (value instanceof Node) {
            node = (Node) value;
        } else {
            // depend on 'convert' of BasicNodeList, ugly ugly
            NodeList list = getCloud().getCloudContext().createNodeList();
            list.add(value);
            node = list.getNode(0);
            
        }
        setNodeVar(node);
        fillVars();
        return  EVAL_BODY_BUFFERED;
    }

    /**
     * this method writes the content of the body back to the jsp page
     **/
    public int doAfterBody() throws JspTagException { // write the body if there was one
        if (bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new TaglibException(ioe);
            }
        }
        return nodeHelper.doAfterBody();
    }



}
