/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import org.mmbase.bridge.jsp.taglib.AbstractNodeProviderTag;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
* A NodeProvider which creates a new node, which will be commited after the body. So, you
* can use `setField's in the body.
*
* @author Michiel Meeuwissen
*/
public class CreateNodeTag extends AbstractNodeProviderTag implements BodyTag {
    
    private static Logger log = Logging.getLoggerInstance(CreateNodeTag.class.getName());
    
    private String type=null;
        
    public void setType(String t) {
        type = t;
    }
    
    
    public int doStartTag() throws JspTagException{            
        Node node;
        
        node = getDefaultCloud().getNodeManager(type).createNode();
        setNodeVar(node);        
        log.debug("created node " + node.getValue("gui()"));
        return EVAL_BODY_TAG; // should perhaps give a SKIP_BODY if 'field' is given.
    }
    
    public void doInitBody() throws JspTagException {       
        fillVars();    
    } 
        
    
    /**
    * this method writes the content of the body back to the jsp page
    **/
    public int doAfterBody() throws JspTagException {
        getNodeVar().commit();
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY;
    }
}
