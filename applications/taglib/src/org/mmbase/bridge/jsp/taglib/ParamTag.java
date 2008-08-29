/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.*;
import java.util.*;
import org.mmbase.util.StringSplitter;
import org.mmbase.util.logging.*;

/**
 * Adds an extra parameter to the parent {@link ParamHandler} tag (e.g. an mm:link tag).
 *
 * @author Michiel Meeuwissen
 * @version $Id: ParamTag.java,v 1.19 2008-08-29 12:40:29 michiel Exp $
 */

public class ParamTag extends AbstractParamTag implements ParamHandler {
    private static final Logger log = Logging.getLoggerInstance(ParamTag.class);

    private ParamHandler paramHandler;

    public int doStartTag() throws JspException {

        paramHandler = findParentTag(ParamHandler.class, null, false);
        if (paramHandler == null) {
            paramHandler = (ParamHandler) pageContext.getAttribute(ParamHandler.KEY, ParamHandler.SCOPE);
        }
        if (paramHandler == null) {
            throw new JspTagException ("ould not find parent of type org.mmbase.bridge.jsp.taglib.ParamHandler, not could it be found in an attribute " + KEY);
        }
        return super.doStartTag();
    }

    /**
     * @since MMBase-1.9
     */
    protected void addParameter(Object value) throws JspTagException {
        if (name == Attribute.NULL) {
            if (value instanceof CharSequence) {
                for (Map.Entry<String, String> entry : StringSplitter.map(((CharSequence) value).toString()).entrySet()) {
                    paramHandler.addParameter(entry.getKey(), entry.getValue());
                }
            } else {
                throw new TaglibException("You must specifiy a 'name' attribute if the value is not a comma separated String of <name>=<value> pairs.");
            }
        } else {
            paramHandler.addParameter(name.getString(this), value);
        }
    }

    public int doEndTag() throws JspTagException {
        int r = super.doEndTag();
        paramHandler = null;
        return r;

    }

    public void doFinally() {
        paramHandler = null;
        super.doFinally();
    }
}
