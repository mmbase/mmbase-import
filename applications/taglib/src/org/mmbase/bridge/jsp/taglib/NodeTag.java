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
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * NodeTag provides the fields of a node
 *
 * @author Rob Vermeulen
 * @author Michiel Meeuwissen
 */
public class NodeTag extends AbstractNodeProviderTag implements BodyTag {

    private static Logger log = Logging.getLoggerInstance(NodeTag.class.getName());

    private String number    = null;
    private String type      = null;
    private String element   = null;

    private final static int NOT_FOUND_THROW = 0;
    private final static int NOT_FOUND_SKIP  = 1;
    

    private int notfound = NOT_FOUND_THROW;

    /**
     * Release all allocated resources.
     */
    public void release() {
        log.debug("releasing");
        super.release();
        number = null;
        type = null ;
        element = null;
    }

    
    public void setNumber(String number) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("setting number to " + number);
        }
        this.number = getAttributeValue(number);
    }


    public void setNotfound(String i) throws JspTagException {
        String is = getAttributeValue(i);        
        if ("skip".equalsIgnoreCase(is)) {
            notfound = NOT_FOUND_SKIP;
        } else if ("skipbody".equalsIgnoreCase(is)) {
            notfound = NOT_FOUND_SKIP;
        } else if ("throw".equalsIgnoreCase(is)) {
            notfound = NOT_FOUND_THROW;
        } else if ("exception".equalsIgnoreCase(is)) {
            notfound = NOT_FOUND_THROW;
        } else if ("throwexception".equalsIgnoreCase(is)) {
            notfound = NOT_FOUND_THROW;
        } else {
            throw new JspTagException("Invalid value for attribute 'ifnotfound' " + is + "(" + i + ")");
        }
    }
    /**
     * This function cannot be added because of Orion.
     * It will call this function even if you use an attribute without <%= %>, stupidly.
     
     public void setNumber(int number) throws JspTagException {
     this.number = new Integer(number).toString();
     }
     
    */


    /**
     * The element attribute is used to access elements of
     * clusternodes.
     */
    public void setElement(String e) throws JspTagException {
        element = getAttributeValue(e);
    }


    public int doStartTag() throws JspTagException{
        Node node = null;

        try {
            if (referid != null) {
                // try to find if already in context.
                if (log.isDebugEnabled()) {
                    log.debug("looking up Node with " + referid + " in context");
                }            
                node = getNode(referid);
                if(referid.equals(id)) {
                    getContextTag().unRegister(referid); 
                    // register it again, but as node
                    // (often referid would have been a string).
                    
                }
            }
            
            if (node == null) {
                log.debug("node is null");
                if (number != null) {
                    // explicity indicated which node (by number or alias)
                    node = getCloud().getNode(number);             
                } else {
                    // get the node from a parent element.
                    NodeProvider nodeProvider = (NodeProvider) findParentTag("org.mmbase.bridge.jsp.taglib.NodeProvider", null);
                    if (element != null) {
                        node = nodeProvider.getNodeVar().getNodeValue(element);
                    } else {
                        node = nodeProvider.getNodeVar();
                    }
                    
                }
            }
        } catch (org.mmbase.bridge.NotFoundException e) {
            log.warn(e.toString());
            switch(notfound) {
            case NOT_FOUND_SKIP:  return SKIP_BODY;
            default: throw e;
            }                        
        }

        setNodeVar(node);        

        // if direct parent is a Formatter Tag, then communicate 
        FormatterTag f = (FormatterTag) findParentTag("org.mmbase.bridge.jsp.taglib.FormatterTag", null, false);
        if (f!= null && f.wantXML()) {
            f.getGenerator().addNode(node);
        }
        //log.debug("found node " + node.getValue("gui()"));
        return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspTagException {
        log.debug("fillvars");
        fillVars();
        if (id != null) {
            getContextTag().registerNode(id, getNodeVar());
        }

    }

    /**
     * this method writes the content of the body back to the jsp page
     **/
    public int doAfterBody() throws JspTagException {
        super.doAfterBody();
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY;
    }

}
