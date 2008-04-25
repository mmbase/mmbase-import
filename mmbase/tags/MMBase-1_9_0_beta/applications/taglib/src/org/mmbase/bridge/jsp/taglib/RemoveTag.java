/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;
import java.util.Collection;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;
import org.mmbase.bridge.jsp.taglib.functions.Functions;
import org.mmbase.bridge.jsp.taglib.util.*;

/**
 * Remove an object from the Context.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RemoveTag.java,v 1.13 2007-07-18 07:50:47 michiel Exp $
 */

public class RemoveTag extends ContextReferrerTag {

    private Attribute value = Attribute.NULL;
    private Attribute from = Attribute.NULL;

    /**
     * @since MMBase-1.8
     */
    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }

    /**
     * @since MMBase-1.8
     */
    public void setFrom(String f) throws JspTagException {
        from = getAttribute(f);
    }

    /**
     * @since MMBase-1.8
     */
    protected void remove(ContextContainer cc, String referid) throws JspTagException {
        if (value.getString(this).length() == 0) {
            cc.unRegister(referid);
        } else {
            Collection col = (Collection) cc.get(referid);
            Functions.remove(col, value.getValue(this));
        }
    }

    public int doEndTag() throws JspTagException {
        super.doEndTag();
        String fromString = from.getString(this);
        if (! "".equals(fromString)) {
            boolean useCollection = value.getString(this).length() != 0;
            Collection col = null;
            int from = ContextContainer.stringToLocation(fromString);
            switch(from) {
            case ContextContainer.LOCATION_PARENT:
                if (useCollection) {
                    col = (Collection) getContextProvider().getContextContainer().getParent().get(getReferid());
                    break;
                } else {
                    remove(getContextProvider().getContextContainer().getParent(), getReferid());
                    return EVAL_PAGE;
                }
            case ContextContainer.LOCATION_PAGE:
                if (useCollection) {
                    col = (Collection) pageContext.getAttribute(getReferid());
                    break;
                } else {
                    pageContext.removeAttribute(getReferid());
                    return EVAL_PAGE;
                }
            case ContextContainer.LOCATION_REQUEST:
                if (useCollection) {
                    col = (Collection) pageContext.getAttribute(getReferid(), PageContext.REQUEST_SCOPE);
                    break;
                } else {
                    pageContext.removeAttribute(getReferid(), PageContext.REQUEST_SCOPE);
                    return EVAL_PAGE;
                }
            case ContextContainer.LOCATION_APPLICATION:
                if (useCollection) {
                    col = (Collection) pageContext.getAttribute(getReferid(), PageContext.APPLICATION_SCOPE);
                    break;
                } else {
                    pageContext.removeAttribute(getReferid(), PageContext.APPLICATION_SCOPE);
                    return EVAL_PAGE;
                }
            case ContextContainer.LOCATION_SESSION:
                HttpSession session = ((HttpServletRequest) pageContext.getRequest()).getSession(false);
                if (session != null) {
                    if (useCollection) {
                        col = (Collection) session.getAttribute(getReferid());
                        break;
                    } else {
                        session.removeAttribute(getReferid());
                    }
                }
                return EVAL_PAGE;
            case ContextContainer.LOCATION_THIS:
                break;
            default: {
                throw new JspTagException("Unknown value for 'from' attribute " + fromString);
            }
            }
            if (col != null) {
                Functions.remove(col, value.getValue(this));
                return EVAL_PAGE;
            }
        }

        remove(getContextProvider().getContextContainer(), getReferid());
        return EVAL_PAGE;
    }

}
