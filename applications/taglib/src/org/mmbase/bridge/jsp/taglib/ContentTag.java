/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.*;
import java.util.*;

import org.mmbase.util.transformers.*;

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
 * @version $Id: ContentTag.java,v 1.3 2003-05-08 13:04:33 michiel Exp $
 **/

public class ContentTag extends LocaleTag  {
    private static Logger log;


    private static final Escaper COPY = new Escaper(new CopyCharTransformer(), "");

    static final ContentTag DEFAULT = new ContentTag() {
            public Escaper getEscaper() { return COPY; } 
            public String  getType()    { return "text/html"; } 
            public String  getEncoding(){ return "iso-8859-1"; } 
        };

    private static Map escapers       = new HashMap();
    private static Map postProcessors = new HashMap();

    static {
        try {
            log = Logging.getLoggerInstance(ContentTag.class.getName());
            initializeEscapersAndPostProcessors();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    private static CharTransformer getCharTransformer(XMLBasicReader reader, Element parentElement, String id) {
        List result = new ArrayList();
        Enumeration e = reader.getChildElements(parentElement, "class");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String claz = reader.getElementValue(element);
            Class clazz;
             try {
                clazz = Class.forName(claz);
            } catch (ClassNotFoundException ex) {
                log.error("Class " + claz + " for '" + id + "' could not be found");
                continue;
            }
            if (! CharTransformer.class.isAssignableFrom(clazz)) {
                log.error("The class " + clazz + " specified as an escaper for " +id + " is not a CharTransformer");
                continue;
            }
            CharTransformer ct;
            try {
                ct = (CharTransformer) clazz.newInstance();
            } catch (Exception ex) {
                log.error("Error instantiating a " + clazz + ": " + ex.toString());
                continue;
            }

            String config = element.getAttribute("config");
            if (ct instanceof ConfigurableCharTransformer) {
                log.debug("Trying to configure with '" + config + "'");
                if (! config.equals("")) {
                    int conf;
                    try {
                        log.debug("With int");
                        conf = Integer.parseInt(config);
                    } catch (NumberFormatException nfe) {
                        try {
                            log.debug("With static field");
                            conf = clazz.getDeclaredField(config).getInt(null);
                        } catch (Exception nsfe) {
                            log.error("Type " + id + " is not well configured : " + nfe.toString() + " and " + nsfe.toString());
                            continue;
                        }                
                    }
                    ((ConfigurableTransformer) ct).configure(conf);
                }
            } else {
                if (! config.equals("")) {
                    log.warn("Tried to configure non-configurable transformer " + claz);
                }
            }
            boolean back = "true".equalsIgnoreCase(element.getAttribute("back"));
            if (back) {
                result.add(new InverseCharTransformer(ct));
            } else {
                result.add(ct);
            }            
        }
        if (result.size() ==0 ) {
            return COPY.charTransformer;
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
    private static void initializeEscapersAndPostProcessors() {
        log.service("Reading taglib write-escapers");

        Class thisClass = ContentTag.class;
        InputSource escapersSource = new InputSource(thisClass.getResourceAsStream("resources/taglibcontent.xml"));
        XMLBasicReader reader  = new XMLBasicReader(escapersSource, thisClass);
        Element root = reader.getElementByPath("taglibcontent");
        Enumeration e = reader.getChildElements(root, "escaper");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String id   = element.getAttribute("id");
            try { 
                CharTransformer ct = getCharTransformer(reader, element, id);
                String type   = element.getAttribute("type");
                if ("".equals(type)) type = id;
                log.service("Found an escaper '" + id + "' for type " + type + ": " + ct);
                escapers.put(id, new Escaper(ct, type));
            } catch (Exception nfe) {
                log.error("Exception in escaper '" + id + "': " + nfe.toString());
            }
        }
        log.service("Reading content tag post-processors");
        e = reader.getChildElements(root, "postprocessor");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String id   = element.getAttribute("id");
            CharTransformer ct = getCharTransformer(reader, element, id);
            log.service("Found an postprocessor '" + id + "' : " + ct);
            postProcessors.put(id, ct);
        }
    }
    
    /**
     * Returns the CharTransformer associated with the escaping described by type 'type'.
     */
    public static Escaper getEscaper(String type) {
        Escaper esc = (Escaper) escapers.get(type);
        if (esc == null) return COPY;
        return esc;
    }
    

    private Attribute type           = Attribute.NULL;
    private Attribute encoding       = Attribute.NULL;
    private Attribute postprocessor  = Attribute.NULL;

    public void setType(String ct) throws JspTagException {
        type = getAttribute(ct);
    }

    public void setEncoding(String e) throws JspTagException {
        encoding = getAttribute(e);
    }

    public void setPostprocessor(String e) throws JspTagException {
        postprocessor = getAttribute(e);
    }


    public String getType() throws JspTagException {
        if (type == Attribute.NULL) {
            return "text/html"; // implicit
        } else {
            return type.getString(this);
        }
    }

    protected CharTransformer getPostprocessor() throws JspTagException {
        if (postprocessor != Attribute.NULL) {
            CharTransformer result =  (CharTransformer) postProcessors.get(postprocessor.getString(this));
            if (result == null) throw new JspTagException("The postprocessor " + postprocessor.getString(this) + " is not defined");
            return result;
        } else {
            return null;
        }
    }


    public String getContentType() throws JspTagException {
        String type = getType();
        String contentType = ((Escaper) escapers.get(type)).contentType;
        if (contentType == null) return "text/html";
        return contentType;
    }

    public String getEncoding() throws JspTagException {
        if (encoding == Attribute.NULL) {
            return "UTF-8"; // implicit
        } else {
            return encoding.getString(this);
        }
    }

    public Escaper getEscaper() throws JspTagException {
        return getEscaper(getType());
    }


    public int doStartTag() throws JspTagException {
        super.doStartTag();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        response.setLocale(locale);
        response.setContentType(getContentType() + "; charset=" + getEncoding());
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) {
            try {
                CharTransformer post = getPostprocessor();
                if (post == null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } else {
                    post.transform(bodyContent.getReader(), bodyContent.getEnclosingWriter());
                }
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }        
        }
        return SKIP_BODY;
    }

    /**
     * Wraps a CharTranformer together with content-type in one
     * object. Just to subtain easy storage in a Map.
     */
    public static class Escaper {
        private CharTransformer charTransformer;
        private String contentType;

        
        Escaper(CharTransformer c, String ct) {
            charTransformer = c;
            contentType = ct;
        }
        public java.io.Writer transform(String string, java.io.Writer w) {
            return charTransformer.transform(new StringReader(string), w);            
        }
    }



}

