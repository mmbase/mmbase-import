/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.Casting;

/**
 * The SetFieldTag can be used as a child of a 'NodeProvider' tag or inside a
 * FieldProvider.
 *
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 * @version $Id: SetFieldTag.java,v 1.34 2005-11-23 10:29:39 michiel Exp $ 
 */

public class SetFieldTag extends FieldTag { // but it is not a writer
    private static final Logger log = Logging.getLoggerInstance(SetFieldTag.class);

    protected String convert (String s) throws JspTagException {
        return s;
    }

    private String body = null;
    private Attribute valueId = Attribute.NULL;

    public int doStartTag() throws JspTagException {
        setFieldVar(name.getString(this));
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) body = bodyContent.getString();
        return SKIP_BODY;
    }
    public void setValueid(String v) throws JspTagException {
        valueId = getAttribute(v);
    }

    private static final ByteToCharTransformer base64 = new Base64();
    /**
     * Set the value of the field.
     */
    public int doEndTag() throws JspTagException {
        setFieldVar();
        Node node = getNode();
        if (field == null) {
            throw new JspTagException("Cannot set field '" + name.getString(this) + "' for node '" + node + "' (it does not exist?)");
        }
        int type = field.getType();

        Object value;
        String refid = valueId.getString(this);
        if (body != null) {
            if (! refid.equals("")) throw new JspTagException("Cannot use both body and referid attribute on setfield tag");
            value = body;
        } else if (! refid.equals("")) {
            value = getObject(refid);
        } else {
            value = "";
        }

        if ((field != null) && (type == Field.TYPE_BYTE)) {
            // if the field type is a byte[] then we expect a BASE64 encoded String, unless value is actualy a byte[].
            if (value instanceof byte[]) {
                node.setByteValue(fieldName, (byte[]) value);
            } else if (value instanceof org.apache.commons.fileupload.FileItem) {
                node.setByteValue(fieldName, ((org.apache.commons.fileupload.FileItem) value).get());
            } else {
                node.setByteValue(fieldName, base64.transformBack(Casting.toString(value)));
            }
        } else {
            String newValue = convert(Casting.toString(value));
            // a bit of hackery to make it more likely that actually a right type is fed to the core.
            // E.g. if you use ExprCalc to set an integer field, that would not work otherwise (because always double like '1.0')
            switch(type) {
            case Field.TYPE_NODE:
            case Field.TYPE_INTEGER: 
                value = Casting.toInteger(newValue);
                break;
            case Field.TYPE_LONG: 
                value = Casting.toInteger(newValue);
                break;
            default: // rest should go ok in core
                value = newValue;
            }

            
            if (log.isDebugEnabled()) {
                log.debug("Setting field " + fieldName + " to " + value);
            }
            
            node.setValue(fieldName, value);

            if (getId() != null) {
                getContextProvider().getContextContainer().register(getId(), value);
            }
        }

        return EVAL_PAGE;
    }
}
