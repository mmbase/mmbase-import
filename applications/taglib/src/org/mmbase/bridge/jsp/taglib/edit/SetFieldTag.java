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
    protected Node node;
    private String fieldname;   
    
    public void setName(String fieldname) {
        this.fieldname = fieldname;
    }

    protected String getName() {
        return fieldname;
    }
        
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspTagException {
        NodeProvider np;
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
            np = fieldTag.findNodeProvider();
            node = np.getNodeVar();
            fieldname = fieldTag.getField().getName();
        } else {
            // Find the node.
            np = findNodeProvider();
            node = np.getNodeVar();
        }
        np.setModified();
        pageContext.setAttribute("fieldname", fieldname);
    }
    
    protected String convert (String s) throws JspTagException { 
        return s;
    }
            

    /**
     * Set the value of the field.
     */
    public int doAfterBody() throws JspTagException {
        // Get the new value from the body.
        if (node.getNodeManager().getField(fieldname).getType() == Field.TYPE_BYTE) {
            // if the field type is a BYTE  thing, we expect a BASE64 encoded String...
            node.setValue(fieldname, org.mmbase.util.Encode.decode("BASE64", bodyContent.getString()));
	} else {           
            String newValue = bodyContent.getString();
            node.setValue(fieldname, convert(newValue));
        }
        
        return SKIP_BODY;
    }   
}
