/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import  org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

/**
 * A simple tag to provide the properties on cloud, nodemanager, modules.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.2
 */

public class SetPropertyTag extends  CloudReferrerTag  {

    private String body = null;
    private Attribute nodeManager = Attribute.NULL;
    private Attribute module      = Attribute.NULL;
    private Attribute name        = Attribute.NULL;
    private Attribute valueId     = Attribute.NULL;

    public void setNodemanager(String nm) throws JspTagException {
        nodeManager = getAttribute(nm);
    }
    public void setModule(String m) throws JspTagException {
        module = getAttribute(m);
    }
    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    /*
    public void setValue(Object v) throws JspTagException {
        value = v;
    }
    */

    public void setValueid(String v) throws JspTagException {
        valueId = getAttribute(v);
    }

    @Override
    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) body = bodyContent.getString();
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspTagException {
        Object v;
        String refid = valueId.getString(this);
        if (body != null) {
            if (refid.length() != 0) throw new JspTagException("Cannot use both body and referid attribute on setfield tag");
            v = body;
        } else if (refid.length() != 0) {
            v = getObject(refid);
        } else {
            v = "";
        }
        if (nodeManager != Attribute.NULL) {
            if (module != Attribute.NULL) {
                throw new JspTagException("Cannot give both module and nodemanager");
            }
            throw new UnsupportedOperationException("Setting properties on nodemanagers not (yet) supported");
        } else if (module != Attribute.NULL) {
            throw new UnsupportedOperationException("Setting properties on modues not (yet) supported");
        } else {
            getCloudVar().setProperty(name.getString(this), v);
        }
        return EVAL_PAGE;
    }

}
