/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import java.io.IOException;
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
 * @version $Id: ContentTag.java,v 1.1 2003-05-06 20:32:08 michiel Exp $
 **/

public class ContentTag extends LocaleTag  {
    private static Logger log;


    private static final Escaper NO = new Escaper(new EmptyCharTransformer(), "", false);

    static final ContentTag DEFAULT = new ContentTag() {
            public Escaper getEscaper() { return NO; } 
            public String          getType()    { return "text/html"; } 
            public String          getEncoding(){ return "iso-8859-1"; } 
        };

    private static Map escapers     = new HashMap();

    static {
        try {
            log = Logging.getLoggerInstance(ContentTag.class.getName());
            initializeEscapers();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * Initialize the write-escapers for MMBase taglib.
     */
    private static void initializeEscapers() {
        log.service("Reading taglib write-escapers");

        Class thisClass = ContentTag.class;
        InputSource escapersSource = new InputSource(thisClass.getResourceAsStream("resources/escapers.xml"));
        XMLBasicReader reader  = new XMLBasicReader(escapersSource, thisClass);
        Element fieldtypesElement = reader.getElementByPath("escapers");
        Enumeration e = reader.getChildElements(fieldtypesElement, "escaper");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String id   = element.getAttribute("id");
            String claz = reader.getElementValue(reader.getElementByPath(element, "escaper.class"));
            Class clazz;
            try {
                clazz = Class.forName(claz);
            } catch (ClassNotFoundException ex) {
                log.error("Class " + claz + " could not be found for id " + id);
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
            String config = reader.getElementValue(reader.getElementByPath(element, "escaper.config"));
            if (! "".equals(config)) {
                try {
                    int conf = Integer.parseInt(config);
                    ct.configure(conf);
                } catch (NumberFormatException nfe) {
                    log.error("Type " + id + " is not well configured : " + nfe);
                    continue;
                }                
            }
            String type   = element.getAttribute("type");
            if ("".equals(type)) type = id;
            log.service("Found an escaper '" + id + "' for type " + type + ": " + ct);
            String back= reader.getElementValue(reader.getElementByPath(element, "escaper.backTransform"));
            escapers.put(id, new Escaper(ct, type, back.equalsIgnoreCase("true")));
        }
    }
    
    /**
     * Returns the CharTransformer associated with the escaping described by type 'type'.
     */
    public static Escaper getEscaper(String type) {
        Escaper esc = (Escaper) escapers.get(type);
        if (esc == null) return NO;
        return esc;
    }
    

    private Attribute type        = Attribute.NULL;
    private Attribute encoding    = Attribute.NULL;

    public void setType(String ct) throws JspTagException {
        type = getAttribute(ct);
    }

    public void setEncoding(String e) throws JspTagException {
        encoding = getAttribute(e);
    }


    public String getType() throws JspTagException {
        if (type == Attribute.NULL) {
            return "text/html"; // implicit
        } else {
            return type.getString(this);
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
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }        
        }
        return SKIP_BODY;
    }

    public static class Escaper {
        private CharTransformer charTransformer;
        private String contentType;
        private boolean backTransform;
        
        Escaper(CharTransformer c, String ct, boolean b) {
            charTransformer = c;
            contentType = ct;
            backTransform = b;
        }
        public String transform(String string) {
            if (backTransform) {
                return charTransformer.transformBack(string);
            } else {
                return charTransformer.transform(string);
            }
            
        }
    }



}

