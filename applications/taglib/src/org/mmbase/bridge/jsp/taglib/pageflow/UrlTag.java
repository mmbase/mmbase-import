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
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.ContextTag;

import org.mmbase.bridge.jsp.taglib.Writer;
import org.mmbase.bridge.jsp.taglib.WriterHelper;

import org.mmbase.bridge.jsp.taglib.util.StringSplitter;

import javax.servlet.jsp.JspTagException;


/**
 * A Tag to produce an URL with parameters. This meant to live in a
 * Context of type 'parameters'.
 * 
 * @author Michiel Meeuwissen
 */
public class UrlTag extends CloudReferrerTag  implements Writer {

    protected WriterHelper helper = new WriterHelper(); 
    // sigh, we would of course prefer to extend, but no multiple inheritance possible in Java..

    public void setVartype(String t) throws JspTagException { 
        throw new JspTagException("Url tag can only produces Strings");
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getWriterValue() throws JspTagException {
        return getUrl();
    }
           
    private   Vector  referids = null;
    private   HashMap extraParameters = null;
    protected String  page;

    public void setReferids(String r) throws JspTagException {
        referids = StringSplitter.split(getAttributeValue(r));
    }

    public void setPage(String p) throws JspTagException {
        page = getAttributeValue(p); 
    }

    void addParameter(String key, Object value) throws JspTagException {
        extraParameters.put(key, value);
    }

   
    
    public int doStartTag() throws JspTagException {  
        extraParameters = new HashMap();
        if (page == null) {
            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
            page = new java.io.File(req.getRequestURI()).getName();
        }
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
        bodyContent.clearBody(); // don't show the body.
        helper.setBodyContent(bodyContent);
        if (helper.getJspvar() == null) {
            helper.overrideWrite(true); // because Url tag can have subtags (param), default writing even with body seems sensible
        }
        helper.setValue(getUrl());
        helper.setJspvar(pageContext);  
        
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return helper.doAfterBody();
    }

}
