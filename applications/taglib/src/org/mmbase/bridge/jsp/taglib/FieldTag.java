/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The FieldTag can be used as a child of a 'NodeProvider' tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldTag.java,v 1.42 2003-11-19 16:57:41 michiel Exp $ 
 */
public class FieldTag extends FieldReferrerTag implements FieldProvider, Writer {

    private static final Logger log = Logging.getLoggerInstance(FieldTag.class);

    protected Node   node;
    protected NodeProvider nodeProvider;
    protected Field  field;
    protected String fieldName;
    protected Attribute name = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    // NodeProvider Implementation
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

    protected void setFieldVar(String n) throws JspTagException {
        if (n != null) {
            try {
                field = getNodeVar().getNodeManager().getField(n);
            } catch (NotFoundException e) {
                field = null;
            }
            fieldName = n;
            if (getReferid() != null) {
                throw new JspTagException ("Could not indicate both  'referid' and 'name' attribute");
            }
        } else {
            if (getReferid() == null) {
                field = getField(); // get from parent.
                getNodeVar();       // be sure to set the nodevar too, though.
                fieldName = field.getName();
            }
        }
    }
    protected void setFieldVar() throws JspTagException {
        setFieldVar((String) name.getValue(this));
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
        node = null;
        fieldName = (String) name.getValue(this);
        if ("number".equals(fieldName)) {
            if (nodeProvider instanceof org.mmbase.bridge.jsp.taglib.edit.CreateNodeTag) {
                // WHY can't it simply return the number it _will_ get?
                throw new JspTagException("It does not make sense to ask 'number' field on uncommited node");
            }
        }
        setFieldVar(fieldName); // set field and node
        if (log.isDebugEnabled()) {
            log.debug("Field.doStartTag(); '"  + fieldName + "'");
        }

        // found the node now. Now we can decide what must be shown:
        Object value;
        // now also 'node' is availabe;
        if (field == null) { // some function, or 'referid' was used.
            if (getReferid() != null) { // referid
                value = getObject(getReferid());
            } else {         // function
                value = getNodeVar().getValue(fieldName);
            }
        } else {        // a field was found!
            // if direct parent is a Formatter Tag, then communicate
            FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class, null, false);
            if (f != null && f.wantXML()) {
                if (log.isDebugEnabled()) log.debug("field " + field.getName() + " is in a formatter tag, creating objects Element. ");
                f.getGenerator().add(node, field); // add the field
                value = "";
            } else { // do the rest as well.
                switch(helper.getVartype()) {
                case WriterHelper.TYPE_NODE:
                    value = node.getNodeValue(fieldName);
                    break;
                case WriterHelper.TYPE_FIELDVALUE:
                    value = node.getFieldValue(fieldName);
                    break;
                case WriterHelper.TYPE_FIELD:
                    value = node.getFieldValue(fieldName).getField();
                    break;
                default: 
                    switch(field.getType()) {
                    case Field.TYPE_BYTE:
                        value = node.getByteValue(fieldName);
                        break;
                    case Field.TYPE_INTEGER:
                    case Field.TYPE_NODE:
                        value = new Integer(node.getIntValue(fieldName));
                        break;
                    case Field.TYPE_DOUBLE:
                        value = new Double(node.getStringValue(fieldName));
                        break;
                    case Field.TYPE_LONG:
                        value = new Long(node.getLongValue(fieldName));
                        break;
                    case Field.TYPE_FLOAT:
                        value = new Float(node.getFloatValue(fieldName));
                        break;
                    default:
                        value = convert(node.getStringValue(fieldName));
                    }
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("value of " + fieldName + ": " + value);
        
        helper.setValue(value);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        log.debug("end of doStartTag");
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }
       
    /**
     * write the value of the field.
     **/
    public int doEndTag() throws JspTagException {
        log.debug("doEndTag van FieldTag");
        if ((! "".equals(helper.getString()) && getReferid() != null)) {
            throw new JspTagException("Cannot use body in reused field (only the value of the field was stored, because a real 'field' object does not exist in MMBase)");
        }

        return helper.doEndTag();
    }
}
