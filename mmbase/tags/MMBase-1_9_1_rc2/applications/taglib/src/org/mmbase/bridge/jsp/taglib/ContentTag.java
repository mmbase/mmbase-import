/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;

import java.util.*;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.mmbase.util.transformers.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.functions.Parameters;

import org.mmbase.util.*;
import org.mmbase.security.UserContext;

import org.xml.sax.InputSource;
import org.w3c.dom.Element;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provided environmental information to its body's tags.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 **/

public class ContentTag extends LocaleTag  {
    private static  Logger log;


    static final CharTransformer COPY = CopyCharTransformer.INSTANCE;

    private static final long DEFAULT_EXPIRE_TIME = 60; // one minute

    public static final String ESCAPER_KEY = "org.mmbase.bridge.jsp.taglib.escaper";

    static final ContentTag DEFAULT = new ContentTag() {
            public CharTransformer getWriteEscaper() { return COPY; }
            public String  getType()    { return "text/html"; }
            public String  getEncoding(){ return "ISO-8859-1"; }
        };

    private static final Map<String, String> defaultEscapers       = new HashMap<String, String>(); // contenttype id -> chartransformer id
    private static final Map<String, String> defaultPostProcessors = new HashMap<String, String>(); // contenttype id -> chartransformer id
    private static final Map<String, String> defaultEncodings      = new HashMap<String, String>(); // contenttype id -> charset to be used in content-type header (defaults to UTF-8)

    private static final Map<String, String> contentTypes          = new HashMap<String, String>(); // contenttype id  -> contenttype

    private static final Map<String, CharTransformer> charTransformers        = new HashMap<String, CharTransformer>(); // chartransformer id -> chartransformer instance.
    private static final Map<String, ParameterizedTransformerFactory> parameterizedCharTransformerFactories  = new HashMap<String, ParameterizedTransformerFactory>(); // chartransformer id -> chartransformer factories.

    static {
        try {
            log = Logging.getLoggerInstance(ContentTag.class);
            org.mmbase.util.xml.EntityResolver.registerPublicID("-//MMBase//DTD taglibcontent 1.0//EN", "taglibcontent_1_0.dtd", ContentTag.class);
            ResourceWatcher watcher = new ResourceWatcher(ResourceLoader.getConfigurationRoot().getChildResourceLoader("taglib")) {
                    public void onChange(String resource) {
                        defaultEscapers.clear();
                        defaultPostProcessors.clear();
                        defaultEncodings.clear();
                        charTransformers.clear();
                        parameterizedCharTransformerFactories.clear();
                        contentTypes.clear();
                        initialize(getResourceLoader(), resource);
                    }
                };
            watcher.add("content.xml");
            watcher.start();
            watcher.onChange("content.xml");
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    private static CharTransformer readCharTransformer(DocumentReader reader, Element parentElement, String id) {
        List<CharTransformer> result = new ArrayList<CharTransformer>();
        for (Element element: reader.getChildElements(parentElement, "class")) {
            String claz = reader.getElementValue(element);

            String config = element.getAttribute("config");
            boolean back = "true".equalsIgnoreCase(element.getAttribute("back"));

            CharTransformer ct = Transformers.getCharTransformer(claz, config, " escaper " + id, back);
            if (ct == null) continue;
            result.add(ct);
        }
        if (result.size() == 0) {
            return COPY;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            ChainedCharTransformer cct = new ChainedCharTransformer();
            cct.addAll(result);
            return cct;
        }
    }

    private static ParameterizedTransformerFactory readTransformerFactory(final DocumentReader reader, final Element parentElement, final String id) {
        final String claz = reader.getElementValue(reader.getChildElements(parentElement, "class").get(0));
        final Map configuredParams = new HashMap();
        for (Element param: reader.getChildElements(parentElement, "param")) {
            String name = param.getAttribute("name");
            String value = param.getAttribute("value");
            if (value.length() != 0) {
                 configuredParams.put(name, value);
            }
        }
        if (configuredParams.size() == 0) {
            return Transformers.getTransformerFactory(claz, " parameterizedescaper " + id);
        } else {
            return new ParameterizedTransformerFactory() {
                ParameterizedTransformerFactory wrapped = Transformers.getTransformerFactory(claz, " parameterizedescaper " + id);
                public Transformer createTransformer(Parameters parameters) {
                    return wrapped.createTransformer(parameters);
                }
                public Parameters createParameters() {
                    Parameters params = wrapped.createParameters();
                    params.setAutoCasting(true);
                    params.setAll(configuredParams);
                    return params;
                }
            };
        }
    }

    /**
     * Initialize the write-escapers for MMBase taglib.
     */
    private static void initialize(ResourceLoader taglibLoader, String resource) {
        log.service("Reading taglib write-escapers");
        InputStream stream = ContentTag.class.getResourceAsStream("resources/taglibcontent.xml");
        if (stream != null) {
            log.service("Reading backwards compatible resource " + ContentTag.class.getName() + "/resources/taglibcontent.xml");
            InputSource escapersSource = new InputSource(stream);
            readXML(escapersSource);
        }
        List resources = taglibLoader.getResourceList(resource);
        log.service("Using " + resources);
        ListIterator i = resources.listIterator();
        while (i.hasNext()) i.next();
        while (i.hasPrevious()) {
            try {
                URL u = (URL) i.previous();
                log.debug("Reading " + u);
                URLConnection con = u.openConnection();
                if (con.getDoInput()) {
                    InputSource source = new InputSource(con.getInputStream());
                    readXML(source);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        {
            List<String> l = new ArrayList<String>(charTransformers.keySet());
            Collections.sort(l);
            log.service("Found escapers: " + l);
            l = new ArrayList<String>(parameterizedCharTransformerFactories.keySet());
            Collections.sort(l);
            log.service("Found parameterized escapers: " + l);
            l = new ArrayList<String>(contentTypes.keySet());
            Collections.sort(l);
            log.service("Recognized content-types: " + l);
        }

    }

    protected static void readXML(InputSource escapersSource) {

        DocumentReader reader  = new DocumentReader(escapersSource, ContentTag.class);
        Element root = reader.getElementByPath("taglibcontent");

        for (Element element: reader.getChildElements(root, "escaper")) {
            String id   = element.getAttribute("id");
            CharTransformer ct = readCharTransformer(reader, element, id);
            CharTransformer prev = charTransformers.put(id, ct);
            if (prev != null) {
                log.warn("Replaced an escaper '" + id + "' : " + ct + "(was " + prev + ")");
            } else {
                log.debug("Found an escaper '" + id + "' : " + ct);
            }

        }

        log.debug("Reading content tag parameterizedescaperss");
        for (Element element: reader.getChildElements(root, "parameterizedescaper")) {
            String id   = element.getAttribute("id");
            ParameterizedTransformerFactory fact = readTransformerFactory(reader, element, id);
            ParameterizedTransformerFactory prev = parameterizedCharTransformerFactories.put(id, fact);
            if (prev != null) {
                log.warn("Replaced an parameterized escaper '" + id + "' : " + fact + " (was " + prev + ")");
            } else {
                log.debug("Found an parameterized escaper '" + id + "' : " + fact);
            }

            try {
                CharTransformer ct = (CharTransformer) fact.createTransformer(fact.createParameters());
                if (! charTransformers.containsKey("id")) {
                    log.debug("Could be instantiated with default parameters too");
                    charTransformers.put(id, ct);
                } else {
                    log.service("Already a chartransformer with id " + id);
                }
            } catch (Exception ex) {
                log.debug("Could not be instantiated with default parameters only: " + ex.getMessage());
            }

        }


        Set<String> postProcessors = new HashSet<String>();
        log.debug("Reading content tag post-processors");
        for (Element element: reader.getChildElements(root, "postprocessor")) {
            String id   = element.getAttribute("id");
            CharTransformer ct = readCharTransformer(reader, element, id);
            CharTransformer prev = charTransformers.put(id, ct);
            if (prev != null) {
                log.warn("Replaced an postprocessor '" + id + "' : " + ct + " (was " + prev + ")");
            } else {
                log.debug("Found an postprocessor '" + id + "' : " + ct);
            }
            postProcessors.add(id);
        }
        if (postProcessors.size() > 0) {
            log.service("Found post-processors: " + postProcessors);
        }

        for (Element element: reader.getChildElements(root, "content")) {
            String type           = element.getAttribute("type");
            String id             = element.getAttribute("id");
            if (id.length() == 0) {
                id = type;
            }
            contentTypes.put(id, type);
            String defaultEscaper = element.getAttribute("defaultescaper");
            if (defaultEscaper.length() != 0) {
                if (charTransformers.containsKey(defaultEscaper)) {
                    defaultEscapers.put(id, defaultEscaper);
                } else {
                    log.warn("Default escaper '" + defaultEscaper + "' for type + '"+ type + "' is not known");
                }
            }
            String defaultPostprocessor = element.getAttribute("defaultpostprocessor");
            if (defaultPostprocessor.length() != 0) {
                if (charTransformers.containsKey(defaultPostprocessor)) {
                    defaultPostProcessors.put(id, defaultPostprocessor);
                } else {
                    log.warn("Default postprocessor '" + defaultPostprocessor + "' for type + '"+ type + "' is not known");
                }
            }
            String defaultEncoding = element.getAttribute("defaultencoding");
            if (! defaultEncoding.equals("NOTSPECIFIED")) {
                defaultEncodings.put(id, defaultEncoding);
            }
        }

    }


    private Attribute type           = Attribute.NULL;
    private Attribute encoding       = Attribute.NULL;
    private Attribute escaper        = Attribute.NULL;
    private Attribute postprocessor  = Attribute.NULL;
    private Attribute expires        = Attribute.NULL;
    private Attribute unacceptable   = Attribute.NULL;
    private Attribute disposition    = Attribute.NULL;


    public void setType(String ct) throws JspTagException {
        type = getAttribute(ct);
    }

    public void setEncoding(String e) throws JspTagException {
        encoding = getAttribute(e);
    }

    public void setEscaper(String e) throws JspTagException {
        escaper = getAttribute(e);
    }

    public void setPostprocessor(String e) throws JspTagException {
        postprocessor = getAttribute(e);
    }

    public void setExpires(String e) throws JspTagException {
        expires = getAttribute(e);
    }
    /**
     * @since MMBase-1.8.5
     */
    public void setDisposition(String d) throws JspTagException {
        disposition = getAttribute(d);
    }


    public String getType() throws JspTagException {
        if (type == Attribute.NULL) {
            return "text/html"; // implicit
        } else {
            String ct = type.getString(this);
            String c = contentTypes.get(ct);
            if (c != null) return c;
            return ct;
        }
    }

    /**
     * @since MMBase-1.8.5
     */
    public void setUnacceptable(String u) throws JspTagException {
        unacceptable = getAttribute(u);
    }

    /*
    protected int getVersion() {
        return 1;
    }
    */


    /**
     * @return A CharTransformer or null if no postprocessing needed
     * @throws JspTagException can occur if taglibcontent.xml is misconfigured
     */
    protected CharTransformer getPostProcessor() throws JspTagException {
        if (postprocessor.getString(this).length() != 0) {
            return getCharTransformer(postprocessor.getString(this), this);
        } else {
            if (type != Attribute.NULL) {
                String defaultPostProcessor = defaultPostProcessors.get(type.getString(this));
                if (defaultPostProcessor != null) {
                    return getCharTransformer(defaultPostProcessor, this);
                }
            }
            return null;
        }
    }


    public String getEncoding() throws JspTagException {
        if (encoding == Attribute.NULL) {
            String defaultEncoding = defaultEncodings.get(getType());
            if (defaultEncoding == null) {
                return "UTF-8"; // implicit
            } else {
                return defaultEncoding;
            }
        } else {
            return encoding.getString(this);
        }
    }

    /**
     * Gets a CharTransformer identified by <code>id<code>, withouth trying to create chains of
     * them.
     */
    protected static CharTransformer getSimpleCharTransformer(String id, ContextReferrerTag tag) throws JspTagException {
        CharTransformer c = charTransformers.get(id);
        if (c == null && tag != null) c = (CharTransformer) tag.getContextProvider().getContextContainer().get(id);
        if (c == null) {
            int paramsPos = id.indexOf('(');
            if (paramsPos > 0 && id.charAt(id.length() - 1) == ')') { // inline parameterized
                                                                           // like substring(2,3)
                String parameterized = id.substring(0, paramsPos);
                ParameterizedTransformerFactory factory = getTransformerFactory(parameterized);
                Parameters parameters = factory.createParameters();
                parameters.setAutoCasting(true);
                if (tag != null) {
                    tag.fillStandardParameters(parameters);
                }
                parameters.setAll(StringSplitter.split(id.substring(paramsPos + 1, id.length() - 1)));
                c = (CharTransformer) factory.createTransformer(parameters);
            } else {
                // try if there is a factory with this name, which would work with only 'standard' parameters.
                ParameterizedTransformerFactory factory = getTransformerFactory(id);
                log.debug("Found factory for " + id + " " + factory);
                if (factory != null) {
                    Parameters parameters = factory.createParameters();
                    parameters.setAutoCasting(true);
                    if (tag != null) {
                        tag.fillStandardParameters(parameters);
                    }
                    c = (CharTransformer) factory.createTransformer(parameters);
                }
            }
        }
        if (c == null) throw new JspTagException("The chartransformer " + id + " is unknown");
        return c;
    }

    /**
     * Gets a CharTransformer identified by <code>id</code>, which possibly can also be list of id's
     * in which case a chain of chartransformers will be returned.
     * @return A CharTransformer
     * @throws JspTagException if not transformer with given id was configured
     */

    public static CharTransformer getCharTransformer(String id,  ContextReferrerTag tag) throws JspTagException {

        List transs = org.mmbase.util.StringSplitter.splitFunctions(id);

        if (transs.size() > 1) {
            ChainedCharTransformer ct = new ChainedCharTransformer();
            // Iterator ids = StringSplitter.split(id).iterator();
            Iterator ids = transs.iterator();
            while (ids.hasNext()) {
                String i = (String) ids.next();
                CharTransformer c = getSimpleCharTransformer(i, tag);
                if (ct != COPY) {
                    ct.add(c);
                }
            }
            return ct;
        } else {
            CharTransformer ct =  getSimpleCharTransformer(id, tag);
            if (ct != COPY) {
                return ct;
            } else {
                return null;
            }

        }
    }

    /**
     * Returns transformer factory with given id or throws exception if there is none
     */
    public static ParameterizedTransformerFactory getTransformerFactory(String id) throws JspTagException {
        ParameterizedTransformerFactory fact = parameterizedCharTransformerFactories.get(id);
        if (fact == null) throw new JspTagException("The chartransformerfactory " + id + " is unknown");
        return fact;
    }

    /**
     * Called by children
     * @return A CharTransformer (not null)
     */

    public CharTransformer getWriteEscaper() {
        return (CharTransformer) pageContext.findAttribute(ESCAPER_KEY);
    }
    private CharTransformer prevEscaper = null;

    protected void setWriteEscaper() throws JspTagException {
        prevEscaper = getWriteEscaper();
        CharTransformer esc;
        if (escaper.getString(this).length() != 0) {
            esc =  getCharTransformer(escaper.getString(this), this);
        }  else {
            String defaultEscaper = defaultEscapers.get(getType());
            if (defaultEscaper != null) {
                esc = getCharTransformer(defaultEscaper, this);
            } else {
                esc = COPY;
            }
        }
        pageContext.setAttribute(ESCAPER_KEY, esc, PageContext.REQUEST_SCOPE);
    }
    protected void unsetWriteEscaper() {
        if (prevEscaper == null) {
            pageContext.removeAttribute(ESCAPER_KEY, PageContext.REQUEST_SCOPE);
        } else {
            pageContext.setAttribute(ESCAPER_KEY, prevEscaper, PageContext.REQUEST_SCOPE);
        }
    }

    /**
     * @see org.mmbase.bridge.jsp.taglib.LocaleTag#determineLocale()
     */
    protected void determineLocale() throws JspTagException {
        // only set the locale when attributes are present or inside CloudProviderTag
        determineLocaleFromAttributes();
        if (locale == null) {
            determineFromCloudProvider();
        }
    }

    public int doStartTag() throws JspTagException {
        super.doStartTag();
        setWriteEscaper();
        String type = getType();

        addedCacheHeaders = false;
        if (type.length() != 0) {
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            String a = unacceptable.getString(this);
            if (! "".equals(a)) {
                addVary("Accept");
                String acceptHeader = request.getHeader("Accept");
                log.debug("a: " + acceptHeader);
                boolean acceptable = acceptHeader == null ? true : acceptHeader.indexOf(type) != -1;
                if (! acceptable) {
                    if (a.startsWith("CRIPPLE")) {
                        log.debug("browser doesn't accept " + type + " crippling now");
                        if (type.equals("text/html")) {
                            acceptable = true;
                        } else if (type.equals("application/xhtml+xml")) {
                            type = "text/html";
                            acceptable = true; //request.getHeader("Accept").indexOf(type) != -1;
                        }
                        if (a.length() > 7) {
                            a = a.substring(8);
                        } else {
                            a = "_";
                        }
                    }
                    if (! acceptable) {
                        try {
                            response.sendError(javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE,
                                               "_".equals(a) ? null : a);
                            return SKIP_BODY;
                        } catch (java.io.IOException ioe) {
                            throw new JspTagException(ioe.getMessage());
                        }
                    }
                }
            }
            if (locale != null) {
                response.setLocale(locale);
            }
            String enc  = getEncoding();
            log.debug("Found encoding " + enc);
            if (enc.length() == 0) {
                response.setContentType(type); // sadly, tomcat does not allow for not setting the charset, it will simply do it always
            } else {
                response.setContentType(type + ";charset=" + enc);
            }

            if (expires == Attribute.NULL && request.getSession(false) == null) { // if no session, can as well cache in proxy
                addNoCacheHeaders(request, response, DEFAULT_EXPIRE_TIME);
            } else {
                // there is a session, or 'expires' was set explicitely
                // perhaps default cache behaviour should be no-cache if there is a session?
                long exp = expires.getLong(this, DEFAULT_EXPIRE_TIME);
                addNoCacheHeaders(request, response, exp);
            }
            if (disposition != Attribute.NULL) {
                String dis = disposition.getString(this);
                if (! dis.equals("")) {
                    response.setHeader("Content-Disposition", "attachment; filename=\""  + dis + "\"");
                }
            }
        }
        CharTransformer post = getPostProcessor();
        if (post == null || post.equals(COPY)) {
            log.debug("no postprocessor " + (EVAL_BODY == EVAL_BODY_INCLUDE));
            return EVAL_BODY;
        } else {
            return EVAL_BODY_BUFFERED;
        }
    }
    private boolean addedCacheHeaders = false;

    /**
     * The code first sets the Expires header to a date in the
     * past. This indicates to the recipient that the page's content
     * have already expired, as a hint that it's contents should not be
     * cached. The no-cache value for the Pragma header is provided by
     * version 1.0 of the HTTP protocol to further indicate that
     * browsers and proxy servers should not cache a page. Version 1.1
     * of HTTP replaces this header with a more specific Cache-Control
     * header, but recommends including the Pragma header as well for
     * backward compatibility.
     *
     * @param response - http response
     * @param expire - seconds before content should expire
     * @since MMBase 1.8.1
     */
    protected void addNoCacheHeaders(HttpServletRequest request, HttpServletResponse response, long expire) {
        if (request.getAttribute(org.mmbase.bridge.jsp.taglib.pageflow.IncludeTag.INCLUDE_PATH_KEY) == null) {
            if (expire <= 0) {
                // Add some header to make sure these pages are not cached anywhere.
                // Set standard HTTP/1.1 no-cache headers.
                response.setHeader("Cache-Control","no-cache, no-store, must-revalidate, proxy-revalidate");
                // Set IE extended HTTP/1.1 no-cache headers
                response.addHeader("Cache-Control", "post-check=0, pre-check=0");
                // Set standard HTTP/1.0 no-cache header.
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader ("Expires", -1);

                // long now = System.currentTimeMillis();
                // according to  rfc2616 sec14 'already expires' means that date-header is expires header
                // sadly, this does not work:
                // perhaps because tomcat overrides the date header later, so a difference of a second can occur
                // response.setDateHeader("Date",     now);
            } else {
                // calc the string in GMT not localtime and add the offset
                response.setDateHeader ("Expires", System.currentTimeMillis() + (expire * 1000));
                response.setHeader("Cache-Control", "public");
                if (varyHeaders != null) {
                    StringBuilder buf = new StringBuilder();
                    for (String h : varyHeaders) {
                        if (buf.length() > 0) buf.append(',');
                        buf.append(h);
                    }
                    response.setHeader("Vary", buf.toString());
                }
            }
            addedCacheHeaders = true;
        } else {
            addedCacheHeaders = false;
        }
    }

    public int doEndTag() throws JspTagException {
        unsetWriteEscaper();
        return super.doEndTag();
    }


    /**
     * Sets a user. This is used by cloud-tag.
     */

    void setUser(UserContext newUser) throws JspTagException {
        if (addedCacheHeaders) {
            if (newUser != null) {
                long exp = expires.getLong(this, DEFAULT_EXPIRE_TIME);
                if (exp > 0) {
                    HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
                    if (! response.containsHeader("Cache-Control")) {
                        // This page is using the non-anonymous cloud. Cache control must be private.
                        response.setHeader("Cache-Control", "private");
                    }
                }
            }
        }
    }

    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) {
            CharTransformer post = getPostProcessor();
            if (post != null) {
                if (log.isDebugEnabled()) {
                    log.debug("A postprocessor was defined " + post);
                    // log.trace("processing  " + bodyContent.getString());
                }

                post.transform(bodyContent.getReader(), bodyContent.getEnclosingWriter());

            } else {
                if (EVAL_BODY == EVAL_BODY_BUFFERED) {
                    // only needed for lousy app-servers
                    try {
                        if (bodyContent != null) {
                            bodyContent.writeOut(bodyContent.getEnclosingWriter());
                        }
                    } catch (java.io.IOException ioe){
                        throw new TaglibException(ioe);
                    }
                }
            }
        }
        return SKIP_BODY;
    }



}

