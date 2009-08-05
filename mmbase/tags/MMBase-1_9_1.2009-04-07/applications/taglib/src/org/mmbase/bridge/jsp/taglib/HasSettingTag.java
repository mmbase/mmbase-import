/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.framework.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import javax.servlet.jsp.JspTagException;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: HasSettingTag.java,v 1.1 2008-10-22 09:31:12 michiel Exp $
 * @since MMBase-1.9
 */

public class HasSettingTag extends CloudReferrerTag implements Condition {


    protected Attribute name = Attribute.NULL;
    protected Attribute component = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }
    public void setComponent(String c) throws JspTagException {
        component = getAttribute(c, true);
    }

    protected Component getComponent() throws JspTagException {
        if (component == Attribute.NULL) {
            Block b = getCurrentBlock();
            if (b != null) {
                return b.getComponent();
            } else {
                throw new JspTagException("No current component found");
            }
        } else {
            String c = component.getString(this);
            return ComponentRepository.getInstance().getComponent(c);
        }
    }

    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }


    public int doStartTag() throws JspTagException {
        Component comp = getComponent();
        Setting<?> setting = comp.getSetting(name.getString(this));
        if ((setting != null) != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            try{
                if(bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch(java.io.IOException e){
                throw new JspTagException("IO Error: " + e.getMessage());
            }
        }
        return SKIP_BODY;
    }
}
