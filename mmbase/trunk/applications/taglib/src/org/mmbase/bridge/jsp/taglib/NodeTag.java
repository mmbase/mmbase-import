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
    private String contextid = null;
    private Node   node      = null;

    
    /**
     * Release all allocated resources.
     */
    public void release() {   
        log.debug("releasing");
        super.release();       
        number = null;
	type = null ;
	element = null;
	contextid = null;
        node = null;

    }
    
    public void setNumber(String number) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("setting number to " + number);
        }
        node = null;
        this.number = getAttributeValue(number);
    }
    
    public void setId(String id) {
        super.setId(id);
        node = null;
        try {
            // try to find if already in context.
            node = getContextTag().getNode(id);
        } catch (JspTagException e) {
            //could not be found. No problem.
        }
    }


    public void setContextid(String ci) {
        this.contextid = ci;
    }
    /**
     * The element attribute is used to access elements of
     * clusternodes. 
     */
    public void setElement(String e) {
        node = null;
        element = e;
    }
    
    
    public int doStartTag() throws JspTagException{            
        if (node == null) {
            log.debug("node is null");
            if (number != null) { 
                // explicity indicated which node (by number or alias)
                node = getCloudProviderVar().getNode(number);
                if(node == null) {
                    throw new JspTagException("Cannot find Node with number " + number);
                }         
            } else { 
                // get the node from a parent element.           
                NodeProvider nodeProvider;
                try {
                    log.debug("getting node from parent");
                    nodeProvider = 
                        (NodeProvider) findAncestorWithClass((Tag)this,
                                                             Class.forName("org.mmbase.bridge.jsp.taglib.NodeProvider"));
                } catch (ClassNotFoundException e){
                    throw new JspTagException("Could not find NodeProvider class");
                }
                if (nodeProvider == null) {
                    throw new JspTagException("Could not find parent NodeProvider (id = " + getId() + ")");
                }
                if (element != null) {
                    node = nodeProvider.getNodeVar().getNodeValue(element);
                } else {
                    node = nodeProvider.getNodeVar();
                }
                
            }
        }

        setNodeVar(node);        
        //log.debug("found node " + node.getValue("gui()"));        
        return EVAL_BODY_TAG; 
    }
    
    public void doInitBody() throws JspTagException {       
        log.debug("fillvars");
        fillVars();    
    } 
    
    
    
    /**
    * this method writes the content of the body back to the jsp page
    **/
    public int doAfterBody() throws JspTagException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY;
    }
}
