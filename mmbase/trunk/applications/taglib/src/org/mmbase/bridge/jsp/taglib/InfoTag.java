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
 * Calls 'doInfo' from NodeManager or from Module.
 *
 * @author Michiel Meeuwissen
 * @deprecated
 */
public class InfoTag extends  CloudReferrerTag implements Writer {

    private Attribute nodeManager = Attribute.NULL;
    private Attribute module      = Attribute.NULL;
    private Attribute command     = Attribute.NULL;

    protected WriterHelper helper = new WriterHelper();
    // sigh, we would of course prefer to extend, but no multiple inheritance possible in Java..

    public void setVartype(String t) throws JspTagException {
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getWriterValue() {
        return helper.getValue();
    }
    public void haveBody() { helper.haveBody(); }

    public void setNodemanager(String nm) throws JspTagException {
        nodeManager = getAttribute(nm);
    }
    public void setModule(String m) throws JspTagException {
        module = getAttribute(m);
    }
    public void setCommand(String c) throws JspTagException {
        command = getAttribute(c);
    }

    public int doStartTag() throws JspTagException {
        String result;
        if (nodeManager != Attribute.NULL) {
            if (module != Attribute.NULL) {
                throw new JspTagException("Cannot give both module and nodemanager");
            }
            result = getCloud().getNodeManager(nodeManager.getString(this)).getInfo(command.getString(this),
                                                                                    pageContext.getRequest(),
                                                                                    pageContext.getResponse());
        } else if (module != Attribute.NULL) {
            result = getCloudContext().getModule(module.getString(this)).getInfo(command.getString(this),
                                                                                 pageContext.getRequest(),
                                                                                 pageContext.getResponse());
        } else {
            throw new JspTagException("Must give module or nodemanager");
        }

        helper.setValue(result);
        helper.setJspvar(pageContext);
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        helper.setBodyContent(getBodyContent());
        return super.doAfterBody();
    }


    /**
    *
    **/
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }

}
