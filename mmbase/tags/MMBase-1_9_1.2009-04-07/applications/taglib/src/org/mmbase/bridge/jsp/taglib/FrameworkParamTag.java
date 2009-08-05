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
 * Adds an extra parameter to the parent {@link FrameworkParamHandler} tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FrameworkParamTag.java,v 1.3 2008-08-29 12:39:04 michiel Exp $
 * @since MMBase-1.9
 */

public class FrameworkParamTag extends AbstractParamTag implements FrameworkParamHandler {
    private static final Logger log = Logging.getLoggerInstance(FrameworkParamTag.class);

    private FrameworkParamHandler paramHandler;

    public int doStartTag() throws JspException {
        paramHandler = findParentTag(FrameworkParamHandler.class, null);
        return super.doStartTag();
    }

    public void addFrameworkParameter(String key, Object value) throws JspTagException {
        super.addParameter(key, value);
    }
    /**
     */
    protected void addParameter(Object value) throws JspTagException {
        if (name == Attribute.NULL) {
            if (value instanceof CharSequence) {
                for (Map.Entry<String, String> entry : StringSplitter.map(((CharSequence) value).toString()).entrySet()) {
                    paramHandler.addFrameworkParameter(entry.getKey(), entry.getValue());
                }
            } else {
                throw new TaglibException("You must specifiy a 'name' attribute if the value is not a comma separated String of <name>=<value> pairs.");
            }
        } else {
            paramHandler.addFrameworkParameter(name.getString(this), value);
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
