/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.bridge.jsp.taglib.ContextTag;

import javax.servlet.jsp.JspTagException;


/**
* A Tag to produce an URL with parameters. This meant to live in a
* Context of type 'parameters'.
* 
* @author Michiel Meeuwissen
*/
public class UrlTag extends ContextReferrerTag {
           
    private Vector keys = null;
    private Vector extraParameters = null;
    private String file;
    private String jspvar;

    public void setKeys(String k) {
        keys = stringSplitter(k);
    }

    public void setExtraparameters(String p) {
        extraParameters = stringSplitter(p);
    }

    public void setFile(String f) {
        file = f;
    }

    public void setJspvar(String jv) {
        jspvar = jv;
    }
    
    public int doStartTag() throws JspTagException {  
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspTagException {

        if (file == null) {
            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
            file = req.getRequestURI();
        }

        String show = file;
        
        if (keys == null) { // all keys not in session
            keys = getContextTag().getKeys(ContextTag.TYPE_POSTPARAMETERS);
        }
        String connector = (show.indexOf('?') == -1 ? "?" : "&amp;");

        Iterator i = keys.iterator();
        while (i.hasNext()) {
            String key = (String)i.next();
            String value = getContextTag().getObjectAsString(key);
            if (value != null) {
                show += connector + key + "=" + org.mmbase.util.Encode.encode("ESCAPE_URL_PARAM", getContextTag().getObjectAsString(key));
                connector = "&amp;";
            }
        }
        
        if (extraParameters != null) {            
            i = extraParameters.iterator();
            while (i.hasNext()) {
                String keyvalue = (String) i.next();
                show += connector + keyvalue;
                connector = "&amp;";
            }
        }
        
        {
            javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)pageContext.getResponse();
            show = response.encodeURL(show);
        }
    
        if (jspvar != null) {
            pageContext.setAttribute(jspvar, show);
        } else {
            try {                
                bodyContent.print(show);
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (java.io.IOException e) {
                throw new JspTagException (e.toString());            
            }
            
        }
        return SKIP_BODY;
    }

}
