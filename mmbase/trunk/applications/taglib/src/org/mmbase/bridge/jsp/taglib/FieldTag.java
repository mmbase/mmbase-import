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
        log.info("hoi");
        return EVAL_BODY_TAG;
    }
    
    /**
     * write the value of the field.
     **/
    public int doAfterBody() throws JspException {
        
        Node node;

        try {
            if (parentNodeId == null) { // take it from the tag directly above
                NodeLikeTag nodeLike = 
                    (NodeLikeTag) findAncestorWithClass((Tag)this,
                                                        Class.forName("org.mmbase.bridge.jsp.taglib.NodeLikeTag"));
                if (nodeLike == null) {
                    throw new JspException ("Could nog find parent node");
                }
                node = nodeLike.getNode();
            } else {
                // search the node with this id...                
                node = (Node) pageContext.getAttribute(parentNodeId + type);
            }
        
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspException (e.toString());
        }
        log.debug("tadaaam " + node.getValue("gui()"));
        try {
            BodyContent bodyOut = getBodyContent();
            bodyOut.clearBody();
            bodyOut.print(node.getValue(name));
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspException (e.toString());            
        }

        return SKIP_BODY;
    }
}
