/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import java.io.*;

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

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A Tag to produce an URL with parameters. It can use 'context' parameters easily.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UrlTag.java,v 1.95 2006-11-11 15:46:47 michiel Exp $
 */

public class UrlTag extends CloudReferrerTag  implements  ParamHandler {

    private static final Logger log                   = Logging.getLoggerInstance(UrlTag.class);

    private static Boolean makeRelative      = null;
    private   Attribute  referids             = Attribute.NULL;
    protected List<Map.Entry<String, Object>> extraParameters  = null;
    protected UrlParameters parameters;
    protected Attribute  page                 = Attribute.NULL;
    protected Attribute  component            = Attribute.NULL;
    protected Attribute  escapeAmps           = Attribute.NULL;
    protected Attribute  absolute             = Attribute.NULL;
    protected Attribute  encode               = Attribute.NULL;
    protected Url        url;

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
        extraParameters = new ArrayList<Map.Entry<String, Object>>();
        parameters = new UrlParameters(this);
        helper.useEscaper(false);
        if (referid != Attribute.NULL) {
            if (page != Attribute.NULL || component != Attribute.NULL) throw new TaglibException("Cannot specify both 'referid' and 'page' attributes");

            Object o = getObject(getReferid());
            if (o instanceof Url) {
                Url u = (Url) getObject(getReferid());
                extraParameters.addAll(u.params);
                url = new Url(this, u, parameters);
            } else {
                url = new Url(this, Casting.toString(o), getComponent(), parameters);
            }
        } else {
            url = new Url(this, getPage(), getComponent(), parameters);

        }
        if (getId() != null) {
            parameters.getWrapped(); // dereference this
            getContextProvider().getContextContainer().register(getId(), url); 
        }

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
        if (makeRelative == null) {
            String setting = pageContext.getServletContext().getInitParameter("mmbase.taglib.url.makerelative");
            makeRelative = "true".equals(setting) ? Boolean.TRUE : Boolean.FALSE;
        }
        return makeRelative.booleanValue();
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
     * Url-generation without use of the framework. Only used in tree/leaf extensions.
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

    protected void doAfterBodySetValue() throws JspTagException {
        helper.setValue(url.get());
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
        initDoEndTag();
        doAfterBodySetValue();
        helper.doEndTag();
        extraParameters = null;
        parameters = null;
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
    protected static class UrlParameters extends AbstractList<Map.Entry<String, Object>> {
        List<Map.Entry<String, Object>> wrapped = null;
        private UrlTag tag;
        UrlParameters(UrlTag tag) {
            this.tag = tag;
        }
        protected void getWrapped() {
            if (wrapped == null) {
                try {
                    List<Map.Entry<String, Object>> refs = Referids.getList(tag.referids, tag);
                    wrapped = tag.extraParameters == null ? refs : 
                        new ChainedList<Map.Entry<String, Object>>(refs, tag.extraParameters);
                    tag = null; // no need any more. dereference.
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
