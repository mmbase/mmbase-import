/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.community;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.bridge.jsp.taglib.*;

/**
 * GetInfo tag obtains information from the multipurpose INFO field.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 */
public class GetInfoTag extends NodeReferrerTag implements Writer {

    private static Logger log = Logging.getLoggerInstance(FieldTag.class.getName());

    protected WriterHelper helper = new WriterHelper();

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

    private String key=null;
    public void setKey(String k) throws JspTagException {
        key = getAttributeValue(k);
    }


    public int doStartTag() throws JspTagException{
        // firstly, search the node:
        Node node = getNode();

        // found the node now. Now we can decide what must be shown:
        if (key == null) key = "name";
        String value=node.getStringValue("getinfovalue("+key+")");
        if (value == null) value="";            
        helper.setValue(value);
        helper.setJspvar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_TAG;
    }


    /**
     * write the value of the field.
     **/
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }
}
