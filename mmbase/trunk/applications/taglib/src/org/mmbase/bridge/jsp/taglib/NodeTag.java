/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

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
*/
public class NodeTag extends AbstractNodeProviderTag implements BodyTag {
    
    private static Logger log = Logging.getLoggerInstance(NodeTag.class.getName());
    
    private String number=null;
    private String type=null;
    
    public void setNumber(String number){
        this.number=number;    
    }
    
    public void setParameter(String param) {
        this.number = pageContext.getRequest().getParameter(param);
    }
    
    
    public int doStartTag() throws JspException{            
        Node node;
        
        if (number != null) { 
            // explicity indicated which node (by number or alias)
            node = getDefaultCloud().getNode(number);
        } else { 
            // get the node from a parent element.           
            try {
                NodeProvider nodeProvider = 
                    (NodeProvider) findAncestorWithClass((Tag)this,
                                                         Class.forName("org.mmbase.bridge.jsp.taglib.NodeProvider"));
                node = nodeProvider.getNodeVar();
            } catch (ClassNotFoundException e){
                throw new JspException("Could not find NodeProvider class");
            }
        }
        //keesj
        //FIXME does not make sence
        if(node == null) {
            throw new JspException("Cannot find Node with number " + number);
        }         
        setNodeVar(node);        
        log.debug("found node " + node.getValue("gui()"));
        
        //System.out.println("doStartTag");
        return EVAL_BODY_TAG; // should perhaps give a SKIP_BODY if 'field' is given.
    }
    
    public void doInitBody() throws JspException {       
        fillVars();    
    } 
    
    
    
    /**
    * this method writes the content of the body back to the jsp page
    **/
    public int doAfterBody() throws JspException {
        
        try {
            BodyContent bodyOut = getBodyContent();
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspException(ioe.toString());
        }
        return SKIP_BODY;
    }
}
