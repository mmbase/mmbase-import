/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.*;
import java.util.*;

import org.mmbase.util.transformers.*;
import org.mmbase.util.StringSplitter;

import org.mmbase.util.XMLBasicReader;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provided environmental information to its body's tags.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: ContentTag.java,v 1.19 2003-12-21 14:57:28 michiel Exp $
 **/

public class ContentTag extends LocaleTag  {
    private static  Logger log;


    private static final CharTransformer COPY = new CopyCharTransformer();

    private static final long DEFAULT_EXPIRE_TIME = 60; // one minute

    static final ContentTag DEFAULT = new ContentTag() {
            public CharTransformer getWriteEscaper() { return COPY; } 
            public String  getType()    { return "text/html"; } 
            public String  getEncoding(){ return "iso-8859-1"; } 
        };

    private static Map defaultEscapers       = new HashMap(); // contenttype -> chartransformer id
    private static Map defaultPostProcessors = new HashMap(); // contenttype -> chartransformer id
    private static Map defaultEncodings      = new HashMap(); // contenttype -> charset to be used in content-type header (defaults to UTF-8)
    private static Map charTransformers      = new HashMap(); // chartransformer id ->
                                                              // chartransformer instance.

    static {
        try {
            log = Logging.getLoggerInstance(ContentTag.class);
            org.mmbase.util.XMLEntityResolver.registerPublicID("-//MMBase//DTD taglibcontent 1.0//EN", "taglibcontent_1_0.dtd", ContentTag.class);
            initialize();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    private static CharTransformer readCharTransformer(XMLBasicReader reader, Element parentElement, String id) {
        List result = new ArrayList();
        Enumeration e = reader.getChildElements(parentElement, "class");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
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
            return (CharTransformer) result.get(0);
        } else {
            ChainedCharTransformer cct = new ChainedCharTransformer();
            cct.addAll(result);
            return cct;
        }
    }
    /**
     * Initialize the write-escapers for MMBase taglib.
     */
    private static void initialize() {
        log.service("Reading taglib write-escapers");
        InputSource escapersSource = new InputSource(ContentTag.class.getResourceAsStream("resources/taglibcontent.xml"));
        XMLBasicReader reader  = new XMLBasicReader(escapersSource, ContentTag.class);
        Element root = reader.getElementByPath("taglibcontent");

        Enumeration e = reader.getChildElements(root, "escaper");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String id   = element.getAttribute("id");
            CharTransformer ct = readCharTransformer(reader, element, id);
            log.service("Found an escaper '" + id + "' : " + ct);
            charTransformers.put(id, ct);
        }
        log.service("Reading content tag post-processors");
        e = reader.getChildElements(root, "postprocessor");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String id   = element.getAttribute("id");
            CharTransformer ct = readCharTransformer(reader, element, id);
            log.service("Found an postprocessor '" + id + "' : " + ct);
            charTransformers.put(id, ct);
        }

        e = reader.getChildElements(root, "content");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String type           = element.getAttribute("type");
            String defaultEscaper = element.getAttribute("defaultescaper");
            if (! defaultEscaper.equals("")) {
                if (charTransformers.containsKey(defaultEscaper)) {
                    defaultEscapers.put(type, defaultEscaper);
                } else {
                    log.warn("Default escaper '" + defaultEscaper + "' for type + '"+ type + "' is not known");
                }
            }
            String defaultPostprocessor = element.getAttribute("defaultpostprocessor");
            if (! defaultPostprocessor.equals("")) {
                if (charTransformers.containsKey(defaultPostprocessor)) {
                    defaultPostProcessors.put(type, defaultPostprocessor);
                } else {
                    log.warn("Default postprocessor '" + defaultPostprocessor + "' for type + '"+ type + "' is not known");
                }
            }
            String defaultEncoding = element.getAttribute("defaultencoding");
            if (! defaultEncoding.equals("NOTSPECIFIED")) {
                defaultEncodings.put(type, defaultEncoding);
            }
        }


    }
    

    private Attribute type           = Attribute.NULL;
    private Attribute encoding       = Attribute.NULL;
    private Attribute escaper        = Attribute.NULL;
    private Attribute postprocessor  = Attribute.NULL;
    private Attribute expires        = Attribute.NULL;

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


    public String getType() throws JspTagException {
        if (type == Attribute.NULL) {
            return "text/html"; // implicit
        } else {
            return type.getString(this);
        }
    }

    

    /**
     * @return A CharTranformer or null if no postprocessing needed
     * @throws JspTagException can occur if taglibcontent.xml is misconfigured
     */
    protected CharTransformer getPostProcessor() throws JspTagException {
        if (! postprocessor.getString(this).equals("")) {
            return getCharTransformer(postprocessor.getString(this));
        } else {
            if (type != Attribute.NULL) {
                String defaultPostProcessor = (String) defaultPostProcessors.get(type.getString(this));
                if (defaultPostProcessor != null) {
                    return getCharTransformer(defaultPostProcessor);                    
                }
            }
            return null;
        }
    }


    public String getEncoding() throws JspTagException {
        if (encoding == Attribute.NULL) {
            String defaultEncoding = (String) defaultEncodings.get(getType());
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
     * @return A CharTransformer
     * @throws JspTagException if not transformer with given id was configured
     */

    protected static CharTransformer getCharTransformer(String id) throws JspTagException {
        if (id.indexOf(',') > 0) {
            ChainedCharTransformer ct = new ChainedCharTransformer();
            Iterator ids = StringSplitter.split(id).iterator();
            while (ids.hasNext()) {
                String i = (String) ids.next();
                CharTransformer c = (CharTransformer) charTransformers.get(i);
                if (c == null) throw new JspTagException("The chartransformer " + i + " is unknown");
                ct.add(c);                
            }
            return ct;
        } else {
            CharTransformer c = (CharTransformer) charTransformers.get(id);
            if (c == null) throw new JspTagException("The chartransformer " + id + " is unknown");
            return c;
        }
    }
    
    /** 
     * Called by children
     * @return A CharTransformer (not null)
     */

    public CharTransformer getWriteEscaper() throws JspTagException {
        if (! escaper.getString(this).equals("")) { 
            return getCharTransformer(escaper.getString(this));
        } 
        String defaultEscaper = (String) defaultEscapers.get(getType());
        if (defaultEscaper != null) {                
            return getCharTransformer(defaultEscaper);
        } else {
            return COPY;
        }
    }


    public int doStartTag() throws JspTagException {
        super.doStartTag();
        String type = getType();

        if (! type.equals("")) {
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            response.setLocale(locale);
            String enc  = getEncoding();
            log.debug("Found encoding " + enc);
            if (enc.equals("")) {
                response.setContentType(getType());
            } else {
                response.setContentType(getType() + ";charset=" + enc);
            }
        }
        if (getPostProcessor() == null) {
            log.debug("no postprocessor");
            //return EVAL_BODY_INCLUDE; // some appserver don't support this (orion)
            return EVAL_BODY_BUFFERED;
        } else {
            return EVAL_BODY_BUFFERED; 
            // don't really need buffering, but cant figure out to avoid
        }
    }

    public int doAfterBody() throws JspTagException {       
        if (bodyContent != null) {
            if (! getType().equals("")) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
                if (expires == Attribute.NULL && request.getSession(false) == null) { // if no session, can as well cache in proxy
                    long later = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME * 1000;
                    response.setDateHeader("Expires", later);
                    response.setHeader("Cache-Control", "public");
                } else {
                    // perhaps default cache behaviour should be no-cache if there is a session?
                    long exp = expires.getLong(this, DEFAULT_EXPIRE_TIME);
                    if (exp == 0) { // means : cannot be cached!
                        response.setHeader("Cache-Control", "no-cache");
                        response.setHeader("Pragma","no-cache");
                        response.setDateHeader("Expires",  0); //System.currentTimeMillis());
                    } else {
                        long later = System.currentTimeMillis() + exp * 1000;
                        response.setDateHeader("Expires", later);
                        response.setHeader("Cache-Control", "public");
                    }
                    
                }
            }
            CharTransformer post = getPostProcessor();
            if (post != null) {
                if (log.isDebugEnabled()) {
                    log.debug("A postprocessor was defined " + post);
                }
                post.transform(bodyContent.getReader(), bodyContent.getEnclosingWriter());
            } else {
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
        return SKIP_BODY;
    }

}

