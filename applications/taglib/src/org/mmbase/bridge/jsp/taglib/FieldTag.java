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
public class FieldTag extends FieldReferrerTag implements FieldProvider {
    
    private static Logger log = Logging.getLoggerInstance(FieldTag.class.getName()); 
    
    protected Node   node;
    protected NodeProvider nodeProvider;
    protected Field  field;
    protected String fieldName;
    private   String name;   
    private   String head;
       
    public void setName(String n) throws JspTagException {
        name = getAttributeValue(n);
    }

    /**
     * @deprecated Use FieldInfo under FieldTag
     */
    public void setHead(String h) throws JspTagException {
        head = getAttributeValue(h);
    }

    /**
     * A fieldprovider also provides a node.
     */

    public Node getNodeVar() throws JspTagException {
        if (node == null) {
            nodeProvider = findNodeProvider();
            node = nodeProvider.getNodeVar();
        }
        if (node == null) {
            throw new JspTagException ("Did not find node in the parent node provider");
        }
        return node;
    }
    public void setModified() {
        nodeProvider.setModified();
    }

    public Field getFieldVar() {
        return field;
    }

    private void setFieldVar(String n) throws JspTagException {
        if (n != null) {             
            field = getNodeVar().getNodeManager().getField(n);
            fieldName = n;
            if (getReferid() != null) {
                throw new JspTagException ("Could not indicate both  'referid' and 'name/head' attribute");  
            }
        } else { 
            if (getReferid() == null) {
                field = getField(); // get from parent.
                fieldName = field.getName();
            }
        }       
    }
    protected void setFieldVar() throws JspTagException {
        setFieldVar(name);
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

    public int doStartTag() throws JspTagException {
        node= null;
        fieldName = name;
        if (head != null) { // should be deprecated.
            if (name != null) {
                throw new JspTagException ("Could not indicate both  'name' and 'head' attribute");  
            }
            fieldName = head;
        }
        setFieldVar(fieldName);               
        return EVAL_BODY_TAG;
    }

    /**
     * write the value of the field.
     **/
    public int doAfterBody() throws JspTagException {               
        // found the node now. Now we can decide what must be shown:
        String show;
        // now also 'node' is availabe;
        if (head == null) { 
            if (field == null) { // some function, or 'referid' was used.
                if (getReferid() != null) { // referid
                    show = getString(getReferid());
                } else {                    // function
                    show = getNodeVar().getStringValue(fieldName);
                }
            } else { // a field was found!
                if (field.getType() == Field.TYPE_BYTE) {
                    show = "" + org.mmbase.util.Encode.encode("BASE64", node.getByteValue(name));
                } else {    
                    show = getNodeVar().getStringValue(fieldName);
                    show = convert(show);
                }
            }
        } else { // should be deprecated
            if (field == null) {
                throw new JspTagException ("Could not find field " + head);  
            }
            show = "" + field.getGUIName();
        }

        if (getId() != null) {
            getContextTag().register(getId(), show);
        }
        
        try {         
            if ("".equals(bodyContent.getString())) { // only write out if no body.
                // bodyContent.clearBody();
                bodyContent.print(show);
            } else {
                if (getReferid() != null) {
                    throw new JspTagException("Cannot use body in reused field (only the value of the field was stored, because a real 'field' object does not exist in MMBase)");
                    // there is of course Field, but that does not contain the value of a field.
                    // in fact Field == FieldManager
                }
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }
        
        return SKIP_BODY;
    }
}
