/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The SetFieldTag can be used as a child of a 'NodeProvider' tag.   
* 
* @author Michiel Meeuwissen
*/
public class SetFieldTag extends NodeReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(SetFieldTag.class.getName()); 
    
    private String parentNodeId = null;
    private String name;   
    private String head;
    
    public void setNode(String node){
        parentNodeId = node;
    }
    
    public void setName(String n) {
        name = n;
    }
        
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }
    
    /**
    * write the value of the field.
    **/
    public int doAfterBody() throws JspTagException {
        
        // firstly, search the node:
        Node node;
        NodeProvider nodeLikeTag = findNodeProvider(parentNodeId);
        node = nodeLikeTag.getNodeVar();
        
        // new value is in the body:
        String newValue = bodyContent.getString();

        node.setValue(name, newValue);
        return SKIP_BODY;
    }
}
