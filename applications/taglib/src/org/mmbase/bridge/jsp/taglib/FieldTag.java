/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.*;

import org.mmbase.bridge.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The FieldTag can be used as a child of a 'NodeProvider' tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldTag.java,v 1.52 2005-08-18 14:40:00 michiel Exp $
 */
public class FieldTag extends FieldReferrerTag implements FieldProvider, Writer {

    private static final Logger log = Logging.getLoggerInstance(FieldTag.class);

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
        return getNode();
    }


    public void setModified() {
        try {
            findNodeProvider().setModified();
        } catch (JspTagException jte) {
            log.error(jte);
        }
    }


    public Field getFieldVar() {
        return field;
    }

    protected void setFieldVar(String n) throws JspTagException {
        if (n != null) {
            try {
                field = getNode().getNodeManager().getField(n);
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
                getNode();       // be sure to set the nodevar too, though.
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


    /**
     * @todo  EXPERIMENTAL
     * @since MMBase-1.8
     */
    protected void handleEditTag() {
        // Andre is busy with this.
    }

    public int doStartTag() throws JspTagException {
        Node node = getNode();
        fieldName = (String) name.getValue(this);
        if ("number".equals(fieldName)) {
            if (findNodeProvider() instanceof org.mmbase.bridge.jsp.taglib.edit.CreateNodeTag) {
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
                value = node.getValue(fieldName);
            }
        } else {        // a field was found!
            // if direct parent is a Formatter Tag, then communicate
            FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class, null, false);
            if (f != null && f.wantXML()) {
                if (log.isDebugEnabled()) log.debug("field " + field.getName() + " is in a formatter tag, creating objects Element. ");
                f.getGenerator().add(node, field); // add the field
                value = "";
            } else { // do the rest as well.

                // if a value is really null, should it be past as null or cast?
                // I am leaning to the latter but it would break backward compatibility.
                // currently implemented this behavior for DateTime values (new fieldtype)
                // Maybe better is an attribute on fieldtag that determines this?
                // I.e. ifempty = "skip|asis|default"
                // where:
                //   skip: skips the field tag
                //   asis: returns null as a value
                //   default: returns a default value

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
                    case Field.TYPE_BINARY:
                        value = node.getByteValue(fieldName);
                        break;
                    case Field.TYPE_INTEGER:
                    case Field.TYPE_NODE:
                        value = new Integer(node.getIntValue(fieldName));
                        break;
                    case Field.TYPE_DOUBLE:
                        value = new Double(node.getDoubleValue(fieldName));
                        break;
                    case Field.TYPE_LONG:
                        value = new Long(node.getLongValue(fieldName));
                        break;
                    case Field.TYPE_FLOAT:
                        value = new Float(node.getFloatValue(fieldName));
                        break;
                    case Field.TYPE_DATETIME:
                        value = node.getValue(fieldName);
                        if (value != null) {
                            value = node.getDateValue(fieldName);
                        }
                        break;
                    case Field.TYPE_BOOLEAN:
                        value = Boolean.valueOf(node.getBooleanValue(fieldName));
                        break;
                    case Field.TYPE_LIST:
                        value = node.getListValue(fieldName);
                        break;
                    default:
                        value = convert(node.getStringValue(fieldName));
                    }
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("value of " + fieldName + ": " + value);


        handleEditTag();

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
        field = null;
        fieldName = null;
        helper.doEndTag();
        return super.doEndTag();
    }
}
