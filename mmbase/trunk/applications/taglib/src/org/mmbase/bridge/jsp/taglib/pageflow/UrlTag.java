/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import java.io.*;
import java.net.*;
import org.mmbase.framework.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Referids;
import org.mmbase.util.functions.*;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.transformers.CharTransformer;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Tag to produce an URL with parameters. It can use 'context' parameters easily.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UrlTag.java,v 1.91 2006-10-31 15:32:56 michiel Exp $
 */

public class UrlTag extends CloudReferrerTag  implements  ParamHandler {

    private static final Logger log                   = Logging.getLoggerInstance(UrlTag.class);

    private static final CharTransformer paramEscaper = new org.mmbase.util.transformers.Url(org.mmbase.util.transformers.Url.ESCAPE);

    private static Boolean makeRelative      = null;
    private   Attribute  referids             = Attribute.NULL;
    protected final List<Map.Entry<String, Object>> extraParameters = new ArrayList<Map.Entry<String, Object>>();
    protected final UrlParameters parameters = new UrlParameters();
    protected Attribute  page                 = Attribute.NULL;
    protected Attribute  component            = Attribute.NULL;
    protected Attribute  escapeAmps           = Attribute.NULL;
    private   Attribute  absolute             = Attribute.NULL;
    protected Attribute  encode               = Attribute.NULL;

    public void setReferids(String r) throws JspTagException {
        referids = getAttribute(r);
    }

    public void setPage(String p) throws JspTagException {
        page = getAttribute(p);
    }

    /**
     * @since MMBase-1.9
     */
    public void setComponent(String p) throws JspTagException {
        component = getAttribute(p);
    }

    public void setEscapeamps(String e) throws JspTagException {
        escapeAmps = getAttribute(e);
    }

    public void setEncode(String e) throws JspTagException {
       encode = getAttribute(e);
    }
    /**
     * @since MMBase-1.8
     */
    public void setAbsolute(String a) throws JspTagException {
        absolute = getAttribute(a);
    }


    public void addParameter(String key, Object value) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("adding parameter " + key + "/" + value);
        }
        extraParameters.add(new Entry<String, Object>(key, value));
    }



    public int doStartTag() throws JspTagException {
        log.debug("starttag");
        extraParameters.clear();
        parameters.wrapped = null;
        helper.useEscaper(false);
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Return the page. This is delegated to the underlying framework if it is available. 
     * Otherwise, if the component is set, the return value is 'component/page'.
     */
    protected String getPage() throws JspTagException {
        String origPage = page.getString(this);
        if (component != Attribute.NULL) {
            return "/" + component.getString(this) + "/" + origPage;
        } else {
            return origPage;
        }
    }

    /**
     * If it would be nice that an URL starting with '/' would be generated relatively to the current request URL, then this method can do it.
     * If the URL is not used to write to (this) page, then you probably don't want that.
     *
     * The behaviour can be overruled by starting the URL with two '/'s.
     *
     * @since MMBase-1.7
     */
    protected StringBuilder makeRelative(StringBuilder show) {
        HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
        if (show.charAt(0) == '/') { // absolute on servletcontex
            if (show.length() > 1 && show.charAt(1) == '/') {
                log.debug("'absolute' url, not making relative");
                if (addContext()) {
                    show.deleteCharAt(0);
                    show.insert(0, req.getContextPath());
                }
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
        if (makeRelative == null) {
            String setting = pageContext.getServletContext().getInitParameter("mmbase.taglib.url.makerelative");
            makeRelative = "true".equals(setting) ? Boolean.TRUE : Boolean.FALSE;
        }
        return makeRelative.booleanValue();
    }

    protected boolean addContext() {
        return true;
    }

    /**
     * Checks the 'absolute' attribute, and uses it.
     * @return whether show was changed.
     * @since MMBase-1.8.1
     */
    protected boolean useAbsoluteAttribute(StringBuilder show, String page) throws JspTagException {

        String abs = absolute.getString(this);
        if ("".equals(abs) || "false".equals(abs)) {
            return false;
        }

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        if (abs.equals("true")) {
            show.append(req.getScheme()).append("://");
            show.append(req.getServerName());
            int port = req.getServerPort();
            show.append(port == 80 ? "" : ":" + port);
        } else if (abs.equals("server")) {
            //show.append("/");
        } else if (abs.equals("context")) {

        } else {
            throw new JspTagException("Unknown value for 'absolute' attribute '" + abs + "' (must be either 'true', 'false', 'server' or 'context')");
        }
        if (! abs.equals("context")) {
            show.append(req.getContextPath());
        }
        char firstChar = page.charAt(0);
        try {
            URI uri;
            if (firstChar != '/') {
                uri = new URI("servlet", req.getServletPath() + "/../" + page, null);
            } else {
                uri = new URI("servlet", page, null);
            }
            uri = uri.normalize(); // resolves .. and so one
            show.append(uri.getSchemeSpecificPart());
        } catch (URISyntaxException  use) {
            throw new TaglibException(use.getMessage(), use);
        }
        return true;
    }

    /**
     * Returns the component assiociated with this url. This is either the 'current' component, the
     * explicit component specified with the 'component' attribute, or <code>null</code>
     * @since MMBase-1.9
     */
    protected Component getComponent() throws JspTagException {
        if (component == Attribute.NULL) {
            HttpServletRequest req = (HttpServletRequest) getPageContext().getRequest();
            Renderer renderer = (Renderer) req.getAttribute(Renderer.KEY);
            if (renderer != null) {
                return renderer.getBlock().getComponent();
            } else {
                return null;
            }
        } else {
            ComponentRepository rep = ComponentRepository.getInstance();
            return rep.getComponent(component.getString(this));
        }
    }

    /**
     * The specified parameters, by the referids attirbute and by sub-param-tags.
     * @since MMBase-1.9
     */
    protected List<Map.Entry<String, Object>> getParameters() {
        return parameters;
    }

    /**
     * Returns url with the extra parameters (of referids and sub-param-tags).
     */
    protected String getUrl(boolean writeamp, boolean encodeUrl) throws JspTagException {

        StringBuilder show = new StringBuilder();
        if (referid != Attribute.NULL) {
            if (page != Attribute.NULL || component != Attribute.NULL) throw new TaglibException("Cannot specify both 'referid' and 'page' attributes");

            // TODO anticipate also String here, for backwards compatibility.
            Url url = (Url) getObject(getReferid());
            String u = url.get(writeamp);
            if (! useAbsoluteAttribute(show, u)) {
                show.append(u);
            }
        } else {
            Url url = new Url(this, page.getString(this), getComponent(), getParameters());
            String u = url.get(writeamp);
            if (! useAbsoluteAttribute(show, u)) {
                if (u.charAt(0) == '/') {
                    HttpServletRequest req =  (HttpServletRequest) getPageContext().getRequest();
                    show.insert(0, req.getContextPath());
                }
                show.append(u);
            }
        }

        if (encodeUrl) {
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
            return response.encodeURL(show.toString());
        } else {
            return show.toString();
        }
    }
    /**
     * Url-generation without use of the framework
     */
    protected String getLegacyUrl(boolean writeamp, boolean encodeUrl) throws JspTagException {
        Parameters p = new Parameters(getParameters());
        StringBuilder show = BasicFramework.getUrl(getPage(), p.toMap(), (HttpServletRequest) pageContext.getRequest(), writeamp);
        if (encodeUrl) {
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            return response.encodeURL(show.toString());
        } else {
            return show.toString();
        }
    }

    protected String getUrl() throws JspTagException {
        return getUrl(escapeAmps.getBoolean(this, true));
    }

    protected String getUrl(boolean escapeAmps) throws JspTagException {
        return getUrl(escapeAmps, encode.getBoolean(this, true));
    }

    protected void doAfterBodySetValue() throws JspTagException {
        helper.setValue(getUrl());
    }

    public int doAfterBody() throws JspException {
        if (bodyContent != null) bodyContent.clearBody(); // don't show the body.
        return helper.doAfterBody();
    }


    protected void initDoEndTag() throws JspTagException {
        log.debug("endtag of url tag");
        if (helper.getJspvar() == null) {
            helper.overrideWrite(true);
            // because Url tag can have subtags (param), default writing even with body seems sensible
            // unless jspvar is specified, because then, perhaps the user wants that..
        }

    }
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), getUrl(false, false));  // write it as cleanly as possible.
        }
        initDoEndTag();
        doAfterBodySetValue();
        helper.doEndTag();
        extraParameters.clear();
        parameters.wrapped = null;
        return super.doEndTag();
    }

    protected void fillStandardParameters(Parameters p) throws JspTagException { // makes it ]accessible to Url
        super.fillStandardParameters(p);
    }
    /**
     * Combines the parameters from the 'referids' attribute with the explicit mm:param's
     * subtags. This happens 'lazily'. So, the referids are evaluated only when used.
     * @since MMBase-1.9.
     */
    protected class UrlParameters extends AbstractList<Map.Entry<String, Object>> {
        public ChainedList<Map.Entry<String, Object>> wrapped = null;
        protected void getWrapped() {
            if (wrapped == null) {
                try {
                    wrapped = new ChainedList<Map.Entry<String, Object>>(Referids.getList(UrlTag.this.referids, UrlTag.this), 
                                                                         UrlTag.this.extraParameters);
                } catch (JspTagException je) {
                    throw new RuntimeException(je);
                }
            }
        }
        public int size() {
            getWrapped();
            return wrapped.size();
        }
        public Map.Entry<String, Object> get(int i) {
            getWrapped();
            return wrapped.get(i);
        }
    }
}
