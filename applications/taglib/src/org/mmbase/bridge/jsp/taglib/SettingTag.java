/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.*;
import org.mmbase.framework.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;


/**
 * A very simple tag to check if node may be changed.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SettingTag.java,v 1.1 2007-07-26 20:55:45 michiel Exp $
 */

public class SettingTag extends CloudReferrerTag implements Writer {

    protected Attribute name = Attribute.NULL;
    protected Attribute component = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }
    public void setComponent(String c) throws JspTagException {
        component = getAttribute(c);
    }

    protected Component getComponent() throws JspTagException {
        String c = component.getString(this);
        if (c.length() == 0) {
            State state = (State) pageContext.getRequest().getAttribute(State.KEY);
            Component component = state == null ? null : state.getBlock().getComponent();
            if (component == null) {
                throw new JspTagException("No current exception found");
            }
            return component;
        } else {
            return ComponentRepository.getInstance().getComponent(c);
        }
    }


    public int doStartTag() throws JspTagException {
        Setting<?> setting = getComponent().getSetting(name.getString(this));
        if (setting == null) throw new JspTagException("No setting '" + name.getString(this) + "' in component '" + getComponent() + "'");
        Framework fw = getCloudContext().getFramework();
        Parameters parameters = fw.createSettingValueParameters(); 
        fillStandardParameters(parameters);
        helper.setValue(fw.getSettingValue(setting, parameters));
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     * write the value of the field.
     **/
    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }

}
