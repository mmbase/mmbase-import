/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import java.io.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Referids;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;


import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;

import org.mmbase.util.Casting;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Tag to produce an URL with parameters. It can use 'context' parameters easily.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UrlTag.java,v 1.59 2004-01-20 23:15:49 michiel Exp $
 */

public class UrlTag extends CloudReferrerTag  implements  ParamHandler {

    private static final Logger log = Logging.getLoggerInstance(UrlTag.class);

    private static final CharTransformer paramEscaper = new Url(Url.PARAM_ESCAPE);

    private   Attribute  referids = Attribute.NULL;
    protected List       extraParameters = null;
    protected Attribute  page = Attribute.NULL;
    private   Attribute  escapeAmps = Attribute.NULL;


    public void setReferids(String r) throws JspTagException {
        referids = getAttribute(r);
    }

    public void setPage(String p) throws JspTagException {
        page = getAttribute(p);
    }

    public void setEscapeamps(String e) throws JspTagException {
        escapeAmps = getAttribute(e);
    }

    public void addParameter(String key, Object value) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("adding parameter " + key + "/" + value);
        }
        extraParameters.add(new Param(key, value));
    }



    public int doStartTag() throws JspTagException {
        log.debug("starttag");
        extraParameters = new ArrayList();
        
        helper.useEscaper(false);
        return EVAL_BODY_BUFFERED;
    }


    protected String getPage() throws JspTagException {
        return page.getString(this);
    }

    /**
     * If it would be nice that an URL starting with '/' would be generated relatively to the current request URL, then this method can do it.
     * If the URL is not used to write to (this) page, then you probably don't want that.
     *
     * The behaviour can be overruled by starting the URL with two '/'s.
     *
     * @since MMBase-1.7
     */
    protected StringBuffer makeRelative(StringBuffer show) {
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
        if (show.charAt(0) == '/') { // absolute on servletcontex
            if (show.length() > 1 && show.charAt(1) == '/') {
                log.debug("'absolute' url, not making relative");
                show.deleteCharAt(0);
                show.insert(0, req.getContextPath());
            } else {
                log.debug("'absolute' url");
                String thisDir = new java.io.File(req.getServletPath()).getParent();
                show.insert(0,  org.mmbase.util.UriParser.makeRelative(thisDir, "/")); // makes a relative path to root.
            }
        }
        return show;
    }

    /**
     * Whether URL must be generatored relatively. This default to false, and can be configured with
     * the servlet context init parameter 'mmbase.taglib.url.makerelative'. It can be useful to be
     * sure that url's are relative, if e.g. the context path is taken away in an URL-rewrite (e.g. by proxy). 
     * This might give problems with redirects, but if you happen to solve that too, or don't do that...
     *
     * @since MMBase-1.7
     */
    protected boolean doMakeRelative() {
        String setting = pageContext.getServletContext().getInitParameter("mmbase.taglib.url.makerelative");
        return "true".equals(setting);
    }

    /**
     * Returns url with the extra parameters (of referids and sub-param-tags).
     */
    protected String getUrl(boolean writeamp, boolean encode) throws JspTagException {
        StringWriter w = new StringWriter();
        StringBuffer show = w.getBuffer();
        show.append(getPage());
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();
        if (show.toString().equals("")) {

            String thisPage = null;
            String requestURI = req.getRequestURI();
            if (requestURI.endsWith("/")) {
                thisPage = ".";
            } else {
                thisPage = new File(requestURI).getName();
            }

            show.append(thisPage);
        }

        if (doMakeRelative()) { 
            makeRelative(show);
        } else {
            if (show.charAt(0) == '/') { // absolute on servletcontex
                show.insert(0, req.getContextPath());
            }
        }

        String amp = (writeamp ? "&amp;" : "&");


        String connector = (show.toString().indexOf('?') == -1 ? "?" : amp);

        if (referids != Attribute.NULL) {
            Iterator refs = Referids.getReferids(referids, this).entrySet().iterator();
            while (refs.hasNext()) {
                Map.Entry entry = (Map.Entry) refs.next();
                show.append(connector).append(entry.getKey()).append("=");
                paramEscaper.transform(new StringReader(Casting.toString(entry.getValue())), w);
                connector = amp;
            }
        }
        Iterator i = extraParameters.iterator();
        while (i.hasNext()) {
            Param param  = (Param) i.next();
            if (param.value == null) continue;
            show.append(connector).append(param.key).append('=');
            paramEscaper.transform(new StringReader(param.value.toString()), w);
            connector = amp;
        }
        if (encode) {
            javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)pageContext.getResponse();
            return response.encodeURL(show.toString());
        } else {
            return show.toString();
        }
    }

    protected String getUrl() throws JspTagException {
        return getUrl(escapeAmps.getBoolean(this, true));
    }

    protected String getUrl(boolean e) throws JspTagException {
        return getUrl(e, true);
    }

    protected void doAfterBodySetValue() throws JspTagException {
        helper.setValue(getUrl());
    }

    public int doAfterBody() throws JspException {
        if (bodyContent != null) bodyContent.clearBody(); // don't show the body.
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        log.debug("endtag of url tag");
        if (helper.getJspvar() == null) {
            helper.overrideWrite(true);
            // because Url tag can have subtags (param), default writing even with body seems sensible
            // unless jspvar is specified, because then, perhaps the user wants that..
        }
        doAfterBodySetValue();

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        bodyContent = null;
        return helper.doEndTag();
    }


    protected static class Param {
        String key;
        Object value;
        Param(String k, Object v) {
            key = k ; value = v;
        }
        public String getKey() { return key; }
        public Object  getValue() { return value; }
    }

}
