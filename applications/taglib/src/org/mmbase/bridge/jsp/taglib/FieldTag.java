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
import org.mmbase.bridge.Field;

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
    private String head;
    
    public void setNode(String node){
        parentNodeId = node;
    }
    
    public void setName(String n) {
        name = n;
    }

    public void setHead(String h) {
        head = h;
    }

    public int doStartTag() throws JspException{
        return EVAL_BODY_TAG;
    }
    
    /**
     * write the value of the field.
     **/
    public int doAfterBody() throws JspException {
        
        // firstly, search the node:
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

        node = nodeLikeTag.getNodeVar();
        if (node == null) {
            throw new JspException ("Parent of field did not  set node");  
        }

        // found the node now. Now we can decide what must be shown:

        String show;
       
        if (name != null) { // name not null, head perhaps.
            log.debug("using name " + name );
            show = "" + node.getValue(name);
            if (head != null) {
                throw new JspException ("Could not indicate both  'name' and 'head' attribute");  
            }
        } else if (head !=null) { // name null, head isn't.
            log.debug("using head " + head);
            Field f = node.getNodeManager().getField(head);
            if (f == null) {
                throw new JspException ("Could not find field " + head);  
            }
            show = "" + f.getGUIName();
        } else { // both null
            throw new JspException ("Should use  'name' or 'head' attribute");  
        }

        if (show == null) {
            throw new JspException ("Could not find field " + name + " /"  +  head);  
        }

        try {         
            BodyContent bodyOut = getBodyContent();
            bodyOut.clearBody();
            bodyOut.print(show);
            bodyOut.writeOut(bodyOut.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspException (e.toString());            
        }

        return SKIP_BODY;
    }
}
