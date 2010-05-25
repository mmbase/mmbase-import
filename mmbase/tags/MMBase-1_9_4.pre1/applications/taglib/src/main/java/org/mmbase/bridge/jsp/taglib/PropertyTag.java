/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import  org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

/**
 * A simple tag to provide the properties on cloud, nodemanager, modules.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */

public class PropertyTag extends  CloudReferrerTag implements Writer {

    private Attribute nodeManager = Attribute.NULL;
    private Attribute module      = Attribute.NULL;
    private Attribute name        = Attribute.NULL;

    public void setNodemanager(String nm) throws JspTagException {
        nodeManager = getAttribute(nm);
    }
    public void setModule(String m) throws JspTagException {
        module = getAttribute(m);
    }
    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    public int doStartTag() throws JspTagException {
        Object result;
        if (nodeManager != Attribute.NULL) {
            if (module != Attribute.NULL) {
                throw new JspTagException("Cannot give both module and nodemanager");
            }
            result = getCloudVar().getNodeManager(nodeManager.getString(this)).getProperty(name.getString(this));
        } else if (module != Attribute.NULL) {
            result = getCloudContext().getModule(module.getString(this)).getProperty(name.getString(this));
        } else {
            result = getCloudVar().getProperty(name.getString(this));
        }


        helper.setValue(result);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
    *
    **/
    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();

    }

}
