/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;

/**
 * Calls 'doInfo' from NodeManager or from Module.
 *
 * @author Michiel Meeuwissen
 * @deprecated
 */
public class InfoTag extends  CloudReferrerTag implements Writer {
    
    private String nodeManager = null;
    private String module      = null;
    private String command     = null;
    
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
        nodeManager = getAttributeValue(nm);
    }
    public void setModule(String m) throws JspTagException {
        module = getAttributeValue(m);
    }
    public void setCommand(String c) throws JspTagException {
        command = getAttributeValue(c);
    }

    
    public int doStartTag() throws JspTagException {
        String result;
        if (nodeManager != null) {
            if (module != null) {
                throw new JspTagException("Cannot give both module and nodemanager");
            }
            result = getCloud().getNodeManager(nodeManager).getInfo(command,
                                                                    pageContext.getRequest(),
                                                                    pageContext.getResponse());
        } else if (module != null) {
            result = getCloudContext().getModule(module).getInfo(command,
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
        return EVAL_BODY_TAG;
    }
    
    /**
    *
    **/
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }

}
