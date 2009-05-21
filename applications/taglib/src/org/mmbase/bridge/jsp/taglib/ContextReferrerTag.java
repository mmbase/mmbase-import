/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.*;

import java.io.*;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.jsp.taglib.edit.FormTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.containers.QueryContainer;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;
import org.mmbase.framework.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

import java.util.*;

/**
 * If you want to have attributes which obtain the value from a
 * parameter or other context variable, or if you want to be able to
 * refer to other tags, then your tag can extend from this one.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see ContextTag
 */

public abstract class ContextReferrerTag extends BodyTagSupport implements TryCatchFinally {

    /**
     * EVAL_BODY is EVAL_BODY_INCLUDE or EVAL_BODY_BUFFERED. It wants to be EVAL_BODY_INCLUDE, but
     * because that often not works, it on default is EVAL_BODY_BUFFERED.
     *
     * This configurable constant might become deprecated, because it is a bit experimental.
     *
     */
    public static int EVAL_BODY = -1;
    // should be EVAL_BODY_INCLUDE. But
    // 1. Completely unsupported by orion 1.6.0
    // 2. Buggy supported by tomcat < 4.1.19


    private static final Logger log = Logging.getLoggerInstance(ContextReferrerTag.class);

    private static final Logger pageLog = Logging.getLoggerInstance(Logging.PAGE_CATEGORY);



    protected static String getTaglibVersion() {
        try {
            ClassLoader cl = ContextReferrerTag.class.getClassLoader();
            InputStream is = cl.getResourceAsStream("org/mmbase/taglib/version");
            if (is == null) {
                return "2.0";
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            return r.readLine();
        } catch (IOException io) {
            // should not happen
            return "1.0";
        }
    }



    protected ContextTag pageContextTag = null;

    protected  Attribute  contextId = Attribute.NULL; // context to which this tagg is referring to.
    protected  Attribute  referid   = Attribute.NULL;

    protected  Attribute  id        = Attribute.NULL; // hides String id of TagSupport

    private String       thisPage = null;

    void setPageContextOnly(final PageContext pc) {
        super.setPageContext(pc);
        // the 'page' Context
        PageContextThreadLocal.setThreadPageContext(pc, this);
    }

    /**
     * Returns the pageContext which is stored on a trhead local for this request
     * @return jsp pageContext
     * @since MMBase-1.8.5
     */
    public static PageContext getThreadPageContext() {
        return PageContextThreadLocal.getThreadPageContext();
    }

    /**
     * Just exposes the (otherwise protected) pageContext member. Needed by some helper classes in
     * the neighbourhood. Lacking concept of friends.
     * @return JSP Page Context
     */
    public PageContext getPageContext() {
        return pageContext;
    }

    /**
     * Returns the ContextTag first on the page
     * @return ContextTag of page
     * @since MMBase-1.7.4
     */
    protected ContextTag getPageContextTag() {
        if (pageContextTag == null) {
            pageContextTag = (ContextTag) pageContext.getAttribute(ContextTag.CONTEXTTAG_KEY);
            if (pageContextTag == null) { // not yet put
                if (log.isDebugEnabled()) {
                    log.debug("No pageContextTag found in pagecontext, creating.. for "+ pageContext);
                }
                if (pageLog.isServiceEnabled()) {
                    HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
                    //thisPage = request.getRequestURI();
                    String queryString = ((HttpServletRequest) pageContext.getRequest()).getQueryString();
                    String includedPage = (String) request.getAttribute("javax.servlet.include.servlet_path");
                    thisPage = (includedPage == null ? "" : includedPage + " for ") + request.getRequestURI();
                    pageLog.service("Parsing JSP page: " + thisPage +
                                    (queryString != null ? "?" + queryString : "") + " for " + pageContext.getPage());
                    if (pageLog.isTraceEnabled()) {
                        logAttributes(request);
                    }
                }
                pageContextTag = new ContextTag();
                pageContextTag.setId(null);

                // page context has no id, this also avoids that it tries
                // registering itself in the parent (which it is itself)
                // so don't change this!

                pageContextTag.setPageContextOnly(pageContext);

                // set the pageContextTag, before fillVars otherwise the page is not set in the fillVars
                // register also the tag itself under __context.
                // _must_ set __context before calling setPageContext otherwise in infinite loop.
                if (log.isDebugEnabled()) {
                    log.debug("Creating page context container for " + pageContext);
                }
                pageContextTag.createContainer(null);

                pageContextTag.pageContextTag = pageContextTag; // the 'parent' of pageContextTag is itself..

                pageContext.setAttribute(ContextTag.CONTEXTTAG_KEY, pageContextTag);

                // there is one implicit ContextTag in every page.
                // its id is null, it is registered in the pageContext as __context.
                //
                // it is called pageContext, because it is similar to the pageContext, but not the same.
            }
        }
        return pageContextTag;
    }

    @SuppressWarnings("unchecked")
    private void logAttributes(HttpServletRequest request) {
        pageLog.trace("req " + Collections.list(request.getAttributeNames()));
        pageLog.trace("page " + Collections.list(pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE)));
        pageLog.trace("app " + Collections.list(pageContext.getAttributeNamesInScope(PageContext.APPLICATION_SCOPE)));
        pageLog.trace("req " + Collections.list(pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE)));
    }

    @Override
    public void setPageContext(PageContext pc) {
        if (EVAL_BODY == -1) { // as yet unset
            EVAL_BODY =  "true".equals(pc.getServletContext().getInitParameter("mmbase.taglib.eval_body_include")) ?
                EVAL_BODY_INCLUDE : EVAL_BODY_BUFFERED;
            log.info("Using " + (EVAL_BODY == EVAL_BODY_BUFFERED ? " EVAL_BODY_BUFFERED (If you use a modern app-server, which supports it, you prefer EVAL_BODY_INCLUDE. See web.xml)" :  "EVAL_BODY_INCLUDE"));
        }

        if (log.isDebugEnabled()) {
            log.debug("setting page context: " + this.getClass().getName());
        }
        setPageContextOnly(pc); // make pageContext availabe
        pageContextTag = null;
        getPageContextTag();

    }

    /**
     * ContextReferrers normally can have the attribute 'referid'. If
     * a ContextReferrer has the 'id' attribute it registers its
     * output in the surrounding Context.  With 'referid' you can 'repeat' a
     * tag which had the 'id' attribute.
     * @param r referid value
     * @throws JspTagException when parsing of attributes fails
     */

    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }

    @Override
    public void setId(String i) {
        try {
            if ("_".equals(i)) {
                throw new RuntimeException("'_' is not a valid id (it is reserved for the 'current writer')");
            }
            id = getAttribute(i, true);
        } catch (JspTagException j) {
            throw new RuntimeException(j);
        }
    }

    @Override
    public String getId() {
        try {
            return (String) id.getValue(this);
        } catch (JspTagException j) {
            throw new RuntimeException(j);
        }
    }

    protected String getReferid() throws JspTagException {
        return (String) referid.getValue(this);
    }

    /*
     * The writerreferrer functionality is here, though not every conterreferrer is a writerreferrer.
     * So contextreferrer does not implement Writerreferrer. It is enough tough to 'implement WriterReferrer'
     * for every ContextReferrer to become a real WriterReferrer;
     */
    /**
     * Which writer to use.
     */
    protected Attribute writerid = Attribute.NULL;


    /**
     * Find the parent writer tag. It also calls haveBody on this
     * parent tag, so that this knows that it has a body. If a
     * write-tag has a body, then on default it will not write itself,
     * but only communicate itself tot the body's tags.
     * @return Writer
     * @throws JspTagException when parsing of attributes fails
     */
    public Writer findWriter() throws JspTagException {
        return findWriter(true);

    }

    /**
     * Find the parent writer tag. It also calls haveBody on this
     * parent tag, so that this knows that it has a body. If a
     * write-tag has a body, then on default it will not write itself,
     * but only communicate itself tot the body's tags.
     * @param th if it has to throw an exception if the parent can not be found (default: yes).
     * @return Writer
     * @throws JspTagException when parsing of attributes fails
     * @since MMBase-1.6.2
     */
    public Writer findWriter(boolean th) throws JspTagException {
        Writer w = findParentTag(Writer.class, (String) writerid.getValue(this), th);
        if (w != null) {
            w.haveBody();
        }
        return w;
    }



    /**
     * Sets the writer attribute.
     * @param w unparsed attribute
     * @throws JspTagException when parsing of attributes fails
     */
    public void setWriter(String w) throws JspTagException {
        writerid = getAttribute(w);
    }

    @Override
    public int doEndTag() throws JspTagException {
        return EVAL_PAGE;
    }

    public void doFinally() {
        PageContextThreadLocal.cleanThreadPageContexts(this);
        helper.doFinally();
        thisPage = null;
        pageContextTag = null;
        writerid = Attribute.NULL;
    }

    public void doCatch(Throwable e) throws Throwable {
        log.debug("Caught throwable: " + e.getMessage());
        throw e;
    }

    /**
     * Release all allocated resources.
     */
    @Override
    public void release() {
        super.release();
        if (log.isDebugEnabled()) {
            log.debug("releasing context-referrer " + this.getClass().getName());
        }
        if (thisPage != null) {
            pageLog.debug("END Parsing JSP page: " + thisPage);
            thisPage = null;
        }
        pageContextTag = null;
        /*
        id = null;
        referid   = Attribute.NULL;
        contextId = Attribute.NULL;
        */
    }


    /**
     * Refer to a specific context. With this you can refer to, and
     * write in, another context then the direct parent (but is must
     * be an ancestor).  This is for analogy with other attributes
     * like this.
     * @param c name of specific context
     * @throws JspTagException when parsing of attributes fails
     */

    public void setContext(String c) throws JspTagException {
        contextId = getAttribute(c);
    }

    /**
     * Call this function in your set-attribute function. It makes it
     * possible for the user of the taglib to include ids of values stored in
     * the context.
     *
     * The method replaces all occurrences of ${x} and $x, where x is a reference to
     * a attribute value, possibly prefixed with context names. The
     * end of the variable if determined by the closing bracket (${x})
     * or by the first non ContextIndentifierChar or end of string ($x).
     *
     * Simple arithmetic is possible with ${+...}, and since you can
     * even use $-vars inside ${}, you can do in this way some
     * arithmetic on variables.
     *
     * @param attribute name of attribute
     * @return Value of attribute
     * @throws JspTagException when parsing of attributes fails
     *
     * @deprecated Call getAttribute in the set-method and 'toString(tag)' when using
     *             it. This is better for performance and makes sure the impl. works in all servlet
     *             containers.
     */
    public String getAttributeValue(String attribute) throws JspTagException {
        if (attribute == null) return null;
        return getAttribute(attribute).getString(this);
    }
    /**
     * @param attribute unparsed attribute
     * @return Attribute
     * @throws JspTagException when parsing of attributes fails
     * @since MMBase-1.6.1
     */
    public Attribute getAttribute(String attribute) throws JspTagException {
        return Attribute.getAttribute(attribute);
    }

    /**
     * @since MMBase-1.9
     */
    public Attribute getAttribute(String attribute, boolean interpretEmptyAsAbsent) throws JspTagException {
        return Attribute.getAttribute(attribute, interpretEmptyAsAbsent);
    }

    /**
     * Like getAttributeValue but converts the result to a Boolean,
     * and throws an exception if this cannot be done.
     * @param b unparsed attribute
     * @return boolean
     * @throws JspTagException when parsing of attributes fails
     **/

    protected Boolean getAttributeBoolean(String b) throws JspTagException {
        String r = getAttribute(b).getString(this).toLowerCase();
        if ("true".equals(r)) {
            return Boolean.TRUE;
        } else if ("false".equals(r)) {
            return Boolean.FALSE;
        } else {
            throw new JspTagException("'" + r + "' cannot be converted to a boolean");
        }
    }
    /**
     * Like getAttributeValue but converts the result to an Integer,
     * and throws an exception if this cannot be done. It the incoming string evaluates to an empty string, then
     * it will return 0, unless the second optional parameter specifies another default value;
     * @param i unparsed attribute
     * @return integer value of attribute
     * @throws JspTagException when parsing of attributes fails
     **/
    protected Integer getAttributeInteger(String i) throws JspTagException {
        return getAttributeInteger(i, 0);
    }
    protected Integer getAttributeInteger(String i, int def) throws JspTagException {
        try {
            i = getAttribute(i).getString(this);
            if (i.length() == 0) return def;
            return Integer.parseInt(i);
        } catch (NumberFormatException e) { // try first if it was a float
            try {
                return new java.math.BigDecimal(i).intValue();
            } catch (NumberFormatException e2) {
                throw new TaglibException(i + " is not an integer value ", e2);
            }
        }
    }


    /**
     * Finds a parent tag by class and id. This is a base function for
     * 'getContext', but it is handy in itself, so also available for
     * extended classes.
     * @param <C>       type of the tag class
     * @param clazz     the class of the Tag to find.
     * @param tagId     the id of the Tag to find.
     * @param exception if it has to throw an exception if the parent can not be found (default: yes).
     * @return Parent tag
     * @throws JspTagException when the parent tag is not found
     * @since MMBase-1.7
     */
    public <C> C  findParentTag(Class<C> clazz, String tagId, boolean exception) throws JspTagException {
        //public Tag  findParentTag(Class clazz, String tagId, boolean exception) throws JspTagException {
        Tag cTag = findAncestorWithClass(this, clazz);
        if (cTag == null) {
            if (exception) {
                throw new JspTagException ("Could not find parent of type " + clazz.getName() + " for " + this);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Could not find parent of type " + clazz.getName());
                }
                return null;
            }
        }

        if (tagId != null) { // search further, if necessary
            if (log.isDebugEnabled()) {
                log.debug(" with id ("  + tagId + ")");
            }
            String id = cTag instanceof TagSupport ? ((TagSupport) cTag).getId() : "";
            while (! tagId.equals(id)) {
                cTag =  findAncestorWithClass(cTag, clazz);
                if (cTag == null) {
                    if (exception) {
                        throw new JspTagException ("Could not find parent Tag of type " + clazz.getName() + " with id " + tagId);
                    } else {
                        return null;
                    }
                }
                id = cTag instanceof TagSupport ? ((TagSupport) cTag).getId() : "";
            }
        }

        return (C) cTag;
    }

    /**
     * Find loop or query tag using an id
     * @param tagId     the id of the Tag to find.
     * @param exception if it has to throw an exception if the parent can not be found (default: yes).
     * @return Tag
     * @throws JspTagException when parent tag is not found
     * @since MMBase-1.8
     */
    public Tag findLoopOrQuery(String tagId, boolean exception) throws JspTagException {
        Tag tag = getParent();
        while (tag != null) {
            if (tag instanceof LoopTag) {
                if (tagId == null) return tag;
            } else if (tag instanceof QueryContainer) {
                if (tagId == null || ((QueryContainer) tag).getId().equals(tagId)) return tag;
            }
            tag = tag.getParent();
        }
        if (exception) {
            throw new JspTagException("Cloud not find parent Tag of LoopTag or QueryContainer type");
        } else {
            return null;
        }
    }

    /**
     * Finds a parent tag by class and id. This is a base function for
     * 'getContext', but it is handy in itself, so also available for
     * extended classes.
     * @param <C>       type of the tag class
     * @param clazz     the class of the Tag to find.
     * @param id        the id of the Tag to find.
     * @return Parent tag
     * @throws JspTagException when parent tag is not found
     * @since MMBase-1.7
     */
    final protected <C> C  findParentTag(Class<C> clazz, String id) throws JspTagException {
        return findParentTag(clazz, id, true);
    }

    /**
     * Finds the parent context provider.
     * @throws JspTagException when context provider is not found
     * @return ContextProvider
     * @since MMBase-1.7
     */
    public ContextProvider getContextProvider() throws JspTagException {
        return getContextProvider((String) contextId.getValue(this), ContextProvider.class);
    }

    /**
     * Finds the parent context tag. In MMBase 1.7 and higher,
     * normally you would like to use getContextProvider in stead.
     * @return ContextTag
     * @throws JspTagException when context provider is not found
     */
    public ContextTag getContextTag() throws JspTagException {
        return getContextProvider((String) contextId.getValue(this), ContextTag.class);
    }

    /**
     * Finds a parent context tag using an id, and a class. The class
     * is ContextProvider or ContextTag.
     * @param <E> SubType of ContextProvider
     * @param contextid the id of the ContextProvider to find.
     * @param cl the class of the ContextProvider to find.
     * @return ContextProvider
     * @throws JspTagException when context provider is not found
     * @since MMBase-1.7
     */
    private <E extends ContextProvider> E getContextProvider(String contextid, Class<E> cl) throws JspTagException {

        if(log.isDebugEnabled()) {
            log.debug("Searching context " + contextid);
        }
        E contextTag =  findParentTag(cl, contextid, false);

        if (contextTag == null ||
            // doesn't count because it is on a different page, (this tag e.g. is in a tag-file)
            // necessary in tomcat > 5.5.20 only.
            // See http://issues.apache.org/bugzilla/show_bug.cgi?id=31804
            contextTag.getContextContainer().getPageContext() != pageContext) {

            contextTag = (E) getPageContextTag();
            if (contextTag == null) {
                throw new RuntimeException("Did not find pageContextTag!");
            }
            if (contextid != null) {
                if(! contextid.equals(contextTag.getId())) {
                    throw new JspTagException("Could not find context tag with id " + contextid + " (page context has id " + contextTag.getId() + ")");
                }
            }
            log.debug("Didn't find real context tag, taking the 'pageContextTag'");
        } else {
            if (log.isDebugEnabled()) {
                log.debug("found a context " + contextTag + " " + contextTag.getContextContainer());
            }
        }

        return contextTag;
    }

    /**
     * Gets an object from the Context.
     * @param key key the object is stored under
     * @return stored object
     * @throws JspTagException when context provider is not found
     */
    public Object getObject(String key) throws JspTagException {
        // does the key contain '.', then start searching on pageContextTag, otherwise in parent.
        if (log.isDebugEnabled()) {
            log.debug("Getting object '" + key + "' from '" + getContextProvider().getId() + "'");
        }
        Object r = getContextProvider().getContextContainer().getObject(key);
        if (r == null) {
            log.debug("Not found, returning empty string");
            return "";
        } else {
            if (log.isDebugEnabled()) log.debug("found: '" + r + "'");
        }
        return r;
    }

    /**
     * Support '[key]?', which returns the object with name [key] if it is present, or simply null otherwise.
     * If not ends with ?, it simply behaves like {@link #getObject(String)}.
     * @param key key the object is stored under
     * @return stored object
     * @throws JspTagException when context provider is not found
     * @since MMBase-1.8.1
     */
    public Object getObjectConditional(String key) throws JspTagException {
        if (key.endsWith("?")) {
            key = key.substring(0, key.length() - 1);
            return getContextProvider().getContextContainer().get(key);
        } else {
            return getObject(key);
        }
    }

    /**
     * Gets an object from the Context, and returns it as a
     * String. This is not always a simple 'toString'. For example a
     * getString on a Node will return the number, which also uniquely
     * identifies it.
     *
     * If the object is 'not present' then it returns an empty string.
     * @param key key the string is stored under
     * @return stored string
     * @throws JspTagException when context provider is not found
     */
    protected String getString(String key) throws JspTagException {
        return Casting.toString(getObject(key));
    }

    /**
     * Returns the content-tag in which this context-referrer is in,
     * or a default (compatible with MMBase 1.6) if there is none.
     * @return ContextTag
     * @throws JspTagException when parent tag is not found
     * @since MMBase-1.7
     */
    public ContentTag getContentTag() throws JspTagException {
        ContentTag ct = findParentTag(ContentTag.class, null, false);
        if (ct == null) {
            return ContentTag.DEFAULT;
        } else {
            return ct;
        }
    }

    /**
     * Get the locale which is defined by surrounding tags or the cloud
     * @return a locale when defined or otherwise the mmbase locale.
     * @throws JspTagException when parent tag is not found
     * @since MMBase-1.7.1
     */
    public Locale getLocale() throws JspTagException {
        Locale locale = getLocaleFromContext();
        if (locale == null) {
            locale = getDefaultLocale();
        }
        return locale;
    }

    /**
     * Get the locale which is defined by surrounding tags or the cloud
     * @return a locale when defined or otherwise <code>null</code>
     * @throws JspTagException when parent tag is not found
     *
     * @since  MMBase-1.8.1
     */
    public Locale getLocaleFromContext() throws JspTagException {
        // is this correct?
        LocaleTag localeTag = findParentTag(LocaleTag.class, null, false);
        if (localeTag != null) {
            Locale locale = localeTag.getLocale();
            if (locale != null) {
                return locale;
            }
        }
        ContextReferrerTag contextReferrerTag = findParentTag(ContextReferrerTag.class, null, false);
        if (contextReferrerTag != null) {
            Locale locale = contextReferrerTag.getLocale();
            if (locale != null) {
                return locale;
            }
        }
        {
            Locale locale = (Locale) pageContext.getAttribute(LocaleTag.KEY, LocaleTag.SCOPE);
            if (locale != null) {
                return locale;
            }
        }
        return null;
    }

    /**
     * Get the default locale which is set in mmbase.
     * @return default locale
     * @since  MMBase-1.8.1
     */
    public Locale getDefaultLocale() {
        return  org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale();
    }

    /**
     * Get the timezone from the context or the timezone set in mmbase
     * @return timezone
     * @since MMBase-1.8
     */
    public TimeZone getTimeZone() {
        TimeZone timeZone = (TimeZone) pageContext.getAttribute(LocaleTag.TZ_KEY, LocaleTag.SCOPE);
        if (timeZone != null) return timeZone;
        return  org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultTimeZone();
    }

    /**
     * Fill standard parameters like request, response, language and locale
     * @param p the parameters
     * @throws JspTagException when parent tag is not found
     * @since MMBase-1.7.4
     */
    public void fillStandardParameters(Parameters p) throws JspTagException {
        log.debug("Filling standard parameters");
        p.setIfDefined(Parameter.RESPONSE, (HttpServletResponse) pageContext.getResponse());
        p.setIfDefined(Parameter.REQUEST,  (HttpServletRequest) pageContext.getRequest());
        // locale parameters
        java.util.Locale locale = getLocale();
        if (locale != null) {
            p.setIfDefined(Parameter.LANGUAGE, locale.getLanguage());
            p.setIfDefined(Parameter.LOCALE, locale);
        }
    }


    /**
     * @since MMBase-1.9
     */
    protected Block getCurrentBlock() throws JspTagException {
        Framework fw = Framework.getInstance();
        Parameters params = fw.createParameters();
        fillStandardParameters(params);
        Block b = fw.getRenderingBlock(params);
        return b;
    }


    // Writer Implementation
    // Not all ContextReferrerTags are actually Writers, but no m.i. in java.

    /**
     * The helper member is only used by 'Writer' extensions.
     */
    final protected WriterHelper helper = new WriterHelper(this);
    // sigh, we would of course prefer to extend, but no multiple inheritance possible in Java..

    public void setVartype(String t) throws JspTagException {
        helper.setVartype(t);
    }
    /**
     * Set list delimiter
     * @param l delimiter
     * @throws JspTagException when parsing of attributes fails
     * @since MMBase-1.8
     */
    final public void setListdelimiter(String l) throws JspTagException {
        helper.setListdelimiter(getAttribute(l));
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    final public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttribute(w));
    }

    final public void setEscape(String e) throws JspTagException {
        helper.setEscape(getAttribute(e));
    }

    final public Object getWriterValue() {
        return helper.getValue();
    }
    final public void haveBody() { helper.haveBody(); }

    /**
     * Returns the escaped value associated with this tag, but only if the escape attribute was set
     * explicitly (so not when only inherited from content-tag).
     * @param value initial value
     * @return escaped value
     * @throws JspTagException when parsing of attributes fails
     * @since MMBase-1.8
     */
    protected Object getEscapedValue(Object value) throws JspTagException {
        if (helper.getEscape() == null) {
//            if(value instanceof NodeList) {
//                return value;
//            }

            // do wrap though, to maintain EL compatibility (also e.g. when written to request)
            return value == null ? null : Casting.wrap(value, null);
        } else {
            return value == null ? null : Casting.wrap(value, ContentTag.getCharTransformer(helper.getEscape(), this));
        }
    }


    /**
     * Get the surrounding form tag
     * @param excpetion throw exception when form tag not found
     * @param form name of form
     * @return FormTag
     * @throws JspTagException when parsing of attributes fails
     * @since MMBase-1.8.5
     */
    public FormTag getFormTag(boolean excpetion, Attribute form) throws JspTagException {
        FormTag formTag;
        if (form == null || form == Attribute.NULL) {
            formTag = (FormTag) pageContext.getAttribute(FormTag.KEY, FormTag.SCOPE);
            if (formTag == null && excpetion) {
              throw new JspTagException("No form-tag found (" + FormTag.KEY + ")");
            }
        } else {
            formTag = findParentTag(FormTag.class, form != null ? (String) form.getValue(this) : null, true);
        }
        return formTag;
    }

    /**
     * Implements a getQuery for QueryContainerReferrers
     * @since MMBase-1.9.0
     */
    protected Query getQuery(Attribute container) throws JspTagException {
        Query query;
        if (container == null || container == Attribute.NULL) {
            query = (Query) pageContext.getAttribute(QueryContainer.KEY, QueryContainer.SCOPE);
            if (query == null) throw new JspTagException("No query found (" + QueryContainer.KEY + ")");
            if (query.isUsed()) {
                query = query.clone();
                assert ! query.isUsed();
            }
        } else {
            QueryContainer c = findParentTag(QueryContainer.class, (String) container.getValue(this));
            query = c.getQuery();
        }
        return query;

    }

    /**
     * Can be overriden in extensions.
     * @return new connector
     * @since MMBase-1.8.5
     * @deprecated
     */
    public  String appendMoreParameters(String connector, String amp, StringBuffer buf) throws JspTagException {
        return connector;
    }
}
