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
import org.mmbase.util.logging.*;

/**
 * The SetFieldTag can be used as a child of a 'NodeProvider' tag or inside a
 * FieldProvider.
 *
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 * @version $Id: SetFieldTag.java,v 1.28 2003-12-21 18:06:42 michiel Exp $ 
 */

public class SetFieldTag extends FieldTag { // but it is not a writer
    private static final Logger log = Logging.getLoggerInstance(SetFieldTag.class);

    protected String convert (String s) throws JspTagException {
        return s;
    }

    public int doStartTag() throws JspTagException {
        node= null;
        setFieldVar(name.getString(this));
        return EVAL_BODY_BUFFERED;
    }


    public int doEndTag() throws JspTagException {
        return EVAL_PAGE;
    }

    /**
     * Set the value of the field.
     */
    public int doAfterBody() throws JspTagException {
        setFieldVar();
        String body = "";
        if (bodyContent != null) body = bodyContent.getString();
        // Get the new value from the body.

        if (field == null) {
            throw new JspTagException("Cannot set field '" + name.getString(this) + "' (it does not exist?)");
        }
        Node node = getNodeVar();
        int type = field.getType();
        if ((field != null) && (type == Field.TYPE_BYTE)) {
            // if the field type is a BYTE  thing, we expect a BASE64 encoded String...
            node.setByteValue(fieldName, org.mmbase.util.Encode.decodeBytes("BASE64", body));
        } else {
            String newValue = convert(body);
            Object value;
            // a bit of hackery to make it more likely that actually a right type is fed to the core.
            // E.g. if you use ExprCalc to set an integer field, that would not work otherwise (because always double like '1.0')
            switch(type) {
            case Field.TYPE_NODE:
            case Field.TYPE_INTEGER: {
                try {
                    value = new Integer(newValue);
                } catch (NumberFormatException e) {
                    try {
                        log.debug("Values does not look like a integer, trying to round");
                        value = new Integer(new Float(newValue).intValue());
                    } catch (NumberFormatException e2) {
                        // don't know any more, leave error-handling to core.
                        value = newValue;
                    }
                }
                break;
            }
            case Field.TYPE_LONG: {
                try {
                    value = new Long(newValue);
                } catch (NumberFormatException e) {
                    try {
                        log.debug("Values does not look like a long, trying to round");
                        value = new Long(new Float(newValue).longValue());
                    } catch (NumberFormatException e2) {
                        // don't know any more, leave error-handling to core.
                        value = newValue;
                    }
                }
                break;
            }
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
        findNodeProvider().setModified();

        return SKIP_BODY;
    }
}
