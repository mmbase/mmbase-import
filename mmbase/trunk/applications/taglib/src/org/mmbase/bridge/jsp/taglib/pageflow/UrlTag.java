/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
           
    private Vector  referids = null;
    private HashMap extraParameters = null;
    protected String  page;
    private String  jspvar;

    public void setReferids(String r) throws JspTagException {
        referids = stringSplitter(getAttributeValue(r));
    }

    public void setPage(String p) throws JspTagException {
        page = getAttributeValue(p); 
    }

    public void setJspvar(String jv) {
        jspvar = jv;
    }

    void addParameter(String key, Object value) {
        extraParameters.put(key, value);
    }

   
    
    public int doStartTag() throws JspTagException {  
        extraParameters = new HashMap();
        return EVAL_BODY_TAG;
    }

    protected String getUrl(boolean writeamp) throws JspTagException {        
        String show = page;
        String amp = (writeamp ? "&amp;" : "&");
        
        String connector = (show.indexOf('?') == -1 ? "?" : amp);

        if (referids != null) {
            Iterator i = referids.iterator();
            while (i.hasNext()) {
                String key = (String)i.next();           
                String value = getString(key);                
                show += connector + key + "=" + (value == null ? "" : org.mmbase.util.Encode.encode("ESCAPE_URL_PARAM", value));
                connector = amp;
            }
        }
        
        {
            Iterator i = extraParameters.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry map  = (Map.Entry) i.next();
                show += connector + (String) map.getKey() + "=" + org.mmbase.util.Encode.encode("ESCAPE_URL_PARAM", map.getValue().toString());
                connector = amp;
            }
        }
        
        {
            javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)pageContext.getResponse();
            show = response.encodeURL(show);
        }
        return show;

    }
    protected String getUrl() throws JspTagException {
        return getUrl(true);
    }

    public int doAfterBody() throws JspTagException {

        if (page == null) {
            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
            page = req.getRequestURI();
        }

        String show = getUrl();

        if (getId() != null) {
            getContextTag().register(getId(), show);
        }
            
        if (jspvar != null) {
            pageContext.setAttribute(jspvar, show);
        } else {
            try {                
                bodyContent.clear();
                bodyContent.print(show);                
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException e) {
                throw new JspTagException (e.toString());            
            }
            
        }
        return SKIP_BODY;
    }

}
