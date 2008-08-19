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
 * @version $Id: SetFieldTag.java,v 1.38 2008-08-19 14:12:53 michiel Exp $
 */

public class SetFieldTag extends FieldTag { // but it is not a writer
    private static final Logger log = Logging.getLoggerInstance(SetFieldTag.class);

    protected String convert (String s) throws JspTagException {
        return s;
    }

    private String body = null;
    private Attribute valueId = Attribute.NULL;

    public int doStartTag() throws JspException {
        initTag();
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
            if (refid.length() != 0) throw new JspTagException("Cannot use both body and referid attribute on setfield tag");
            value = body;
        } else if (refid.length() != 0) {
            value = getObject(refid);
        } else {
            value = "";
        }

        if ((field != null) && (type == Field.TYPE_BINARY)) {
            // if the field type is a byte[] then we expect a BASE64 encoded String, unless value is actualy a byte[].
            if (value instanceof byte[]) {
                node.setByteValue(fieldName, (byte[]) value);
            } else if (value instanceof org.apache.commons.fileupload.FileItem) {
                node.setByteValue(fieldName, ((org.apache.commons.fileupload.FileItem) value).get());
            } else {
                node.setByteValue(fieldName, base64.transformBack(Casting.toString(value)));
            }
        } else {
            value = convert(Casting.toString(value));

            if (log.isDebugEnabled()) {
                log.debug("Setting field " + fieldName + " to " + value);
            }

            node.setValue(fieldName, value);

            if (getId() != null) {
                getContextProvider().getContextContainer().register(getId(), value);
            }
        }
        body = null;

        return EVAL_PAGE;
    }
}
