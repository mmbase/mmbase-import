/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * 
 * @author Michiel Meeuwissen
 */
public class FieldTag extends BodyTagSupport {

    private static Logger log = Logging.getLoggerInstance(FieldTag.class.getName()); 

    private String parentNodeId = null;
    private String name;   
    private String type = "NODE"; // could be handy to specifiy this.
    
    public void setNode(String node){
        parentNodeId = node;
    }
    
    public void setName(String n) {
        name = n;
    }
    public void setType(String t) {
        type = t;
    }

    public int doStartTag() throws JspException{
        return EVAL_BODY_TAG;
    }
    
    /**
     * write the value of the field.
     **/
    public int doAfterBody() throws JspException {
        
        Node node;
        NodeLikeTag nodeLikeTag;
        Class nodeLikeTagClass;
        try {
            nodeLikeTagClass = Class.forName("org.mmbase.bridge.jsp.taglib.NodeLikeTag");
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspException ("Could not found NodeLikeTag class");  
        }
        
        nodeLikeTag = (NodeLikeTag) findAncestorWithClass((Tag)this, nodeLikeTagClass); 
        if (nodeLikeTag == null) {
            throw new JspException ("Could not find parent node");  
        }

        if (parentNodeId != null) { // search further, if necessary
            while (nodeLikeTag.getId() != parentNodeId) {
                nodeLikeTag = (NodeLikeTag) findAncestorWithClass((Tag)nodeLikeTag, nodeLikeTagClass);            
                if (nodeLikeTag == null) {
                    throw new JspException ("Could not find parent with id " + parentNodeId);  
                }
            }
        }

        node = nodeLikeTag.getNode();
        if (node == null) {
            throw new JspException ("Parent of field did not  set node");  
        }

        String field = "" + node.getValue(name);

        if (field == null) {
            throw new JspException ("Could not find field " + name);  
        }
        log.debug("tadaaam " + node.getValue("gui()"));
        try {
            BodyContent bodyOut = getBodyContent();
            bodyOut.clearBody();
            bodyOut.print(field);
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspException (e.toString());            
        }

        return SKIP_BODY;
    }
}
