/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.logging.*;

/**
 * The SetFieldTag can be used as a child of a 'NodeProvider' tag or inside a
 * FieldListTag.
 * 
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 */
public class SetFieldTag extends NodeReferrerTag {
    private static Logger log = Logging.getLoggerInstance(SetFieldTag.class.getName()); 
    private Node node;
    private String fieldname;   
    
    public void setName(String fieldname) {
        this.fieldname = fieldname;
    }
        
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspTagException {
        if (fieldname == null) {
            // Get node and fieldname from the fieldlist tag.
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
            node = fieldTag.findNodeProvider().getNodeVar();
            fieldname = fieldTag.getField().getName();
        } else {
            // Find the node.
            node = findNodeProvider().getNodeVar();
        }
        pageContext.setAttribute("fieldname", fieldname);
    }
    
    /**
     * Set the value of the field.
     */
    public int doAfterBody() throws JspTagException {
        // Get the new value from the body.
        String newValue = bodyContent.getString();
        node.setValue(fieldname, newValue);
        return SKIP_BODY;
    }
}
