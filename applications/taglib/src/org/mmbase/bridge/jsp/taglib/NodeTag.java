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
    
    private String number   = null;
    private String type     = null;
    private String element = null;
    
    public void setNumber(String number){
        this.number=number;    
    }
    
    public void setParameter(String param) throws JspTagException{  
        this.number = pageContext.getRequest().getParameter(param);
        if (this.number == null) { 
            throw new JspTagException("No parameter "  + param);
        }
    }

    public void setElement(String e) {
        element = e;
    }
    
    
    public int doStartTag() throws JspTagException{            
        Node node;
        
        if (number != null) { 
            // explicity indicated which node (by number or alias)
            node = getCloudProviderVar().getNode(number);
        } else { 
            // get the node from a parent element.           
            NodeProvider nodeProvider;
            try {
                nodeProvider = 
                    (NodeProvider) findAncestorWithClass((Tag)this,
                                                         Class.forName("org.mmbase.bridge.jsp.taglib.NodeProvider"));
            } catch (ClassNotFoundException e){
                throw new JspTagException("Could not find NodeProvider class");
            }
            if (nodeProvider == null) {
                throw new JspTagException("Could not find parent NodeProvider");
            }
            if (element != null) {
                node = nodeProvider.getNodeVar().getNodeValue(element);
            } else {
                node = nodeProvider.getNodeVar();
            }

        }
        //keesj
        //FIXME does not make sence
        if(node == null) {
            throw new JspTagException("Cannot find Node with number " + number);
        }         
        setNodeVar(node);        
        log.debug("found node " + node.getValue("gui()"));
        
        return EVAL_BODY_TAG; 
    }
    
    public void doInitBody() throws JspTagException {       
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
