/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Field;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The FieldTag can be used as a child of a 'NodeProvider' tag.   
* 
* @author Michiel Meeuwissen
*/
public class FieldTag extends NodeReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(FieldTag.class.getName()); 
    
    private String parentNodeId = null;
    protected Node node;
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
    
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }

    /**
     * Does something with the generated output. This default
     * implementation does nothing, but extending classes could
     * override this function.
     * 
     **/
    protected String convert (String s) throws JspTagException { // virtual
        return s;
    }
    
    /**
    * write the value of the field.
    **/
    public int doAfterBody() throws JspTagException {
        
        // firstly, search the node:
        NodeProvider nodeLikeTag = findNodeProvider(parentNodeId);
        node = nodeLikeTag.getNodeVar();
        
        // found the node now. Now we can decide what must be shown:
        String show;
        
        if (name != null) { // name not null, head perhaps.
            log.trace("using name " + name );
            show = "" + node.getValue(name);
            show = convert(show);
            if (head != null) {
                throw new JspTagException ("Could not indicate both  'name' and 'head' attribute");  
            }
        } else if (head !=null) { // name null, head isn't.
            log.trace("using head " + head);
            Field f = node.getNodeManager().getField(head);
            if (f == null) {
                throw new JspTagException ("Could not find field " + head);  
            }
            show = "" + f.getGUIName();
        } else { // both null
            throw new JspTagException ("Should use  'name' or 'head' attribute");  
        }
        
        if (show == null) {
            throw new JspTagException ("Could not find field " + name + " /"  +  head);  
        }
        
        try {         
            // bodyContent.clearBody();
            bodyContent.print(show);
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }
        
        return SKIP_BODY;
    }
}
