/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import javax.servlet.jsp.tagext.Tag;
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
public class FieldInfoTag extends NodeReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(FieldInfoTag.class.getName()); 
    
    private String name;   
       
    public void setName(String n) {
        name = n;
    }
    
    
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }

    private String prefix(String s) {
        String id = getId();
        if (id == null) id = "";
        if (id.equals("") ) {
            return s;
        } else {
            return id + "_" + s;
        }
    }


    /**
    * write the value of the fieldinfo.
    **/
    public int doAfterBody() throws JspTagException {
        
        // firstly, search the field:

        Class fieldClass;
        try {
            fieldClass = Class.forName("org.mmbase.bridge.jsp.taglib.FieldListTag");

        } catch (java.lang.ClassNotFoundException e) {
            throw new JspTagException ("Could not find FieldListTag class");  
        }

        FieldListTag fieldTag = (FieldListTag) findAncestorWithClass((Tag)this, fieldClass); 
        if (fieldTag == null) {
            throw new JspTagException ("Could not find parent FieldListTag");  
        }

        if (getId() == null) { // inherit id..
            setId(fieldTag.getId());
        }


        Field field = fieldTag.getField();
        
        // found the node now. Now we can decide what must be shown:
        String show = "";
        
        if ("name".equals(name)) {
            show = field.getName();
        } else if ("guiname".equals(name)) {
            show = field.getGUIName();
        } else if ("value".equals(name)) {
            Node node = fieldTag.findNodeProvider().getNodeVar();
            show = node.getStringValue(field.getName());
        } else if ("input".equals(name)) {
            // not yet complete...
            Node node = fieldTag.findNodeProvider().getNodeVar();
            int type = field.getType();
            switch(type) {
            case Field.TYPE_STRING:
                if(field.getMaxLength() > 2048)  {
                    show = "<textarea wrap=\"on\" rows=\"10\" cols=\"80\" class=\"big\"  name=\"" + prefix(field.getName()) + "\">" + 
                        node.getStringValue(field.getName()) +
                        "</textarea>";
                    
                    break;                    
                }
                if(field.getMaxLength() > 255 )  {                
                    show = "<textarea wrap=\"on\" rows=\"5\" cols=\"80\" class=\"small\"  name=\"" + prefix(field.getName()) + "\">" + 
                        node.getStringValue(field.getName()) + "</textarea>";
                    break;
                }
            case Field.TYPE_BYTE:
            case Field.TYPE_INTEGER:        
            case Field.TYPE_FLOAT:
            case Field.TYPE_DOUBLE:
            case Field.TYPE_LONG:
                show =  "<input type =\"text\" size=\"80\" name=\"" + prefix(field.getName()) + "\" " + 
                                         "value=\"" + node.getStringValue(field.getName()) + "\" />";
                break;
            }
        } else if ("useinput".equals(name)) {
            
        } else {
            throw new JspTagException("Unknown value for name  attribute " + name);
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
