/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.List;
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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Tag to produce an URL with parameters. This meant to live in a
 * Context of type 'parameters'.
 *
 * @author Michiel Meeuwissen
 */

public class UrlTag extends CloudReferrerTag  implements Writer {

    private static Logger log = Logging.getLoggerInstance(UrlTag.class.getName()); 
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
    public void haveBody() { helper.haveBody(); }

    private   List  referids = null;
    protected Map   extraParameters = null;
    protected String  page;
    private   boolean escapeAmps = true;

    public void setReferids(String r) throws JspTagException {
        referids = StringSplitter.split(getAttributeValue(r));
    }

    public void setPage(String p) throws JspTagException {
        page = getAttributeValue(p);
    }

    public void setEscapeamps(String e) throws JspTagException {
        escapeAmps = getAttributeValue(e).equalsIgnoreCase("true");
    }

    protected void addParameter(String key, Object value) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("adding parameter " + key + "/" + value);
        }
        extraParameters.put(key, value);
    }



    public int doStartTag() throws JspTagException {
        log.debug("starttag");
        extraParameters = new HashMap();
        if (page == null || "".equals(page)) {
            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
            page = new java.io.File(req.getRequestURI()).getName();
        }
        return EVAL_BODY_BUFFERED;
    }

    protected String getUrl(boolean writeamp) throws JspTagException {
        StringBuffer show = new StringBuffer(page);
        String amp = (writeamp ? "&amp;" : "&");

        if (show.charAt(0) == '/') { // absolute on servercontex
            log.debug("'absolute' url");
            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
            show.insert(0,  req.getContextPath());
        }

        String connector = (show.toString().indexOf('?') == -1 ? "?" : amp);

        if (referids != null) {
            Iterator i = referids.iterator();
            while (i.hasNext()) {
                String key = (String)i.next();
                String value = getString(key);
                if (log.isDebugEnabled()) {
                    log.debug("adding parameter (with referids) " + key + "/" + value);
                }
                show.append(connector).append(key).append("=").append((value == null ? "" : org.mmbase.util.Encode.encode("ESCAPE_URL_PARAM", value)));
                connector = amp;
            }
        }

        {
            Iterator i = extraParameters.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry map  = (Map.Entry) i.next();
                show.append(connector).append(map.getKey()).append("=").append(org.mmbase.util.Encode.encode("ESCAPE_URL_PARAM", map.getValue().toString()));
                connector = amp;
            }
        }

        {
            javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)pageContext.getResponse();
            return response.encodeURL(show.toString());
        }

    }
    protected String getUrl() throws JspTagException {
        return getUrl(escapeAmps);
    }

    protected void doAfterBodySetValue() throws JspTagException {
        helper.setValue(getUrl());
    }

    public int doEndTag() throws JspTagException {
        log.debug("endtag of url tag");
        if (bodyContent != null) bodyContent.clearBody(); // don't show the body.
        helper.setBodyContent(bodyContent);
        if (helper.getJspvar() == null) {
            helper.overrideWrite(true);
            // because Url tag can have subtags (param), default writing even with body seems sensible
            // unless jspvar is specified, because then, perhaps the user wants that..
        }
        doAfterBodySetValue();
        helper.setJspvar(pageContext);

        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        bodyContent = null;
        return helper.doEndTag();
    }

}
