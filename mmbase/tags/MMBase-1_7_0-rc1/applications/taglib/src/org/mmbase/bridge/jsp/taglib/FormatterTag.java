/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.util.StringSplitter;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;

import org.mmbase.bridge.util.xml.Generator;

import java.io.File;
import java.util.*;
import javax.servlet.jsp.PageContext;

import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.cache.xslt.*;


/**
 * The formatter can reformat its body. It usually uses XSL for this.
 *
 * @since  MMBase-1.6
 * @author Michiel Meeuwissen
 * @version $Id: FormatterTag.java,v 1.36 2003-11-19 16:57:41 michiel Exp $ 
 */
public class FormatterTag extends ContextReferrerTag  implements Writer {

    private static final Logger log = Logging.getLoggerInstance(FormatterTag.class);

    protected Attribute xslt    = Attribute.NULL;
    protected Attribute format  = Attribute.NULL; 
    protected Attribute options = Attribute.NULL;
    protected Attribute wants   = Attribute.NULL; 

    protected Source   xsltSource = null;

    private static javax.xml.parsers.DocumentBuilder        documentBuilder;
    private File cwd;

    private static final class Counter {
        private int i = 0;
        public int inc() { return ++i;}
        public String toString() {
            return "" + i;
        }
    }
    private Counter  counter; // times that formatter was called in this page, can be usefull for some transformations to know.


    // formats that needs XML input, when setting these 'wantXML' will be true, and a DOM Document will be created.

    // XSLT transformations:
    private static final int FORMAT_XHTML      = 1;
    private static final int FORMAT_PRESENTXML = 2;
    private static final int FORMAT_CODE       = 3;
    private static final int FORMAT_TEXTONLY   = 4;
    private static final int FORMAT_RICH       = 5;

    // wants XML, but uses Encode to output (no XSLT is used).
    private static final int FORMAT_ESCAPEXMLPRETTY = 500;


    private static final int FORMAT_LIMIT_WANTXML         = 1000; // if smaller then this, then need XML.

    // These formats take the body as a string. So the body doesn't need to be valid XML.
    private static final int FORMAT_ESCAPEXML       = 1001;
    private static final int FORMAT_DATE            = 1002;
    private static final int FORMAT_LOWERCASE       = 1003;
    private static final int FORMAT_UPPERCASE       = 1004;


    private static final int FORMAT_UNSET           = -1;


    private static final int WANTS_DEFAULT         = -1;
    private static final int WANTS_DOM             = 1;
    private static final int WANTS_STRING          = 2;

    private static final String PAGECONTEXT_COUNTER = "formatter__counter";

    static {
        log.service("static init of FormatterTag.");

        try {            
            javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            documentBuilder = dfactory.newDocumentBuilder();
            org.xml.sax.ErrorHandler handler = new org.mmbase.util.XMLErrorHandler();
            documentBuilder.setErrorHandler(handler);
            documentBuilder.setEntityResolver( new org.mmbase.util.XMLEntityResolver());
        }  catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * Must store the XML somewhere. This will contain the central DOM Document.
     *
     */
    private   Generator xmlGenerator  = null;

    /**
     * A handle necessary when using the Timer Tag;
     */
    protected int timerHandle;

    /**
     * You can give the path the the XSLT-file by this attribute.
     */
    public void setXslt(String x) throws JspTagException {
        xslt = getAttribute(x);
    }

    /**
     * Predefined formattings.
     */
    public void setFormat(String f) throws JspTagException {
        format = getAttribute(f);
    }

    protected int getFormat() throws JspTagException {
        if (format == Attribute.NULL) return FORMAT_UNSET;
        String fs = format.getString(this).toLowerCase();
        if ("xhtml".equals(fs)) {
            return FORMAT_XHTML;
        } else if ("presentxml".equals(fs)) {
            return  FORMAT_PRESENTXML;
        } else if ("code".equals(fs)) {
            return FORMAT_CODE;
        } else if ("escapexml".equals(fs)) {
            return FORMAT_ESCAPEXML;
        } else if ("escapexmlpretty".equals(fs)) {
            return FORMAT_ESCAPEXMLPRETTY;
        } else if ("date".equals(fs)) {
            return FORMAT_DATE;
        } else if ("textonly".equals(fs)) {
            return FORMAT_TEXTONLY;
        } else if ("rich".equals(fs)) {
            return FORMAT_RICH;
        } else {
            throw new JspTagException("Unknown format " + fs);
        }
    }

    /**
     * The 'options' attribute can be used to provide option to the transformation to be done
     */
    public void setOptions(String o) throws JspTagException {
        options = getAttribute(o);
    }

    public void setWants(String w) throws JspTagException {
        wants = getAttribute(w);
    }
    protected int getWants() throws JspTagException {
        if (wants == Attribute.NULL) return WANTS_DEFAULT;
        String ww = wants.getString(this).toLowerCase();
        if ("default".equals(ww)) {
            return WANTS_DEFAULT;            
        } else if ("DOM".equals(ww)) {
            return  WANTS_DOM;            
        } else if ( "string".equals(ww)) {
            return WANTS_STRING;
        } else {
            throw new JspTagException("Unknown value '" + ww + "' for wants attribute.");
        }         
    }

    /**
     * The  Xslt tag will call this, to inform this tag about the XSLT which must be done.
     */
    public void setXsltSource(Source xs) {
        xsltSource = xs;
    }

    /**
     * Subtags can write themselves as XML to the DOM document of this
     * tag. This functions returns this document.
     */
    public Generator getGenerator() {
        return xmlGenerator;
    }

    /**
     * Subtags need to know how they must communicate there content to
     * this tag. If wantXML evaluates false, they must simply write to
     * the page, and formatter will pick it up.
     */
    public final boolean wantXML() {   
        return xmlGenerator != null;
    }


    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
        cwd = new File(pageContext.getServletContext().getRealPath(request.getServletPath())).getParentFile();        
    }


    public int doStartTag() throws JspTagException {
        
        counter = (Counter) pageContext.getAttribute(PAGECONTEXT_COUNTER);
        if (counter == null) {
            log.debug("counter not found");
            counter = new Counter();
            pageContext.setAttribute(PAGECONTEXT_COUNTER, counter);
        } 
        counter.inc();

        if (log.isDebugEnabled()) log.debug("startag of formatter tag " + counter);

        // serve parent timer tag:
        TagSupport t = findParentTag(org.mmbase.bridge.jsp.taglib.debug.TimerTag.class, null, false);
        if (t != null) {
            timerHandle = ((org.mmbase.bridge.jsp.taglib.debug.TimerTag)t).startTimer(getId(), getClass().getName());
        } else {
            timerHandle = -1;
        }

        xsltSource = null;

        if (format != Attribute.NULL  && xslt != Attribute.NULL) {
            throw new JspTagException ("One of the attributes xslt and format must be specified, or none (then you have to use an mm:xslt subtag.");
        }

        int w = getWants();
        if ((w == WANTS_DEFAULT && getFormat() < FORMAT_LIMIT_WANTXML) || w == WANTS_DOM) { // also if format is unset, that means: use xslt
            xmlGenerator = new Generator(documentBuilder);
        } else {
            xmlGenerator = null; // my childen will know, that this formatter doesn't want them.
        }

        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


    public int doEndTag() throws JspTagException {
        if (helper.getJspvar() == null) {
            helper.overrideWrite(true);
        }

        Document doc;

        String body = bodyContent.getString().trim();
        bodyContent.clearBody(); // should not be shown itself.

        if(getFormat() < FORMAT_LIMIT_WANTXML) { // determin the Document 
            if (xmlGenerator != null && xmlGenerator.getDocument().getDocumentElement().getFirstChild() != null) {
                if (body.length() > 0) {
                    throw new JspTagException ("It is not possible to have tags which produce DOM-XML and  text in the body. Perhaps you want to use the attribute wants='string'?");
                } else {
                    doc = xmlGenerator.getDocument();
                }                
            } else {
                if (log.isDebugEnabled()) log.debug("Using bodycontent as input:>" + body + "<");                               
                try {
                    // TODO, we cannot get the default encoding from the bridge yet, so UTF-8 is assumed now.
                    doc = documentBuilder.parse(new java.io.ByteArrayInputStream(body.getBytes("UTF-8")));
                } catch (Exception e) {
                    throw new TaglibException(body, e);
                }
                if (log.isDebugEnabled()) {
                    log.debug("created an element: " + doc.getDocumentElement().getTagName());
                }
            }      
            
        } else {
            doc = null;
        }
        

        if (log.isDebugEnabled()) {
            if (doc != null) {
                log.trace("XSL converting document: " + prettyXML(doc));
            } else {
                log.trace("Converting: " + body);
            }
        }


        // Determin which XSL to use.
        // useXsl is the pathless name.
        // First it is searced for in <current directory>/xslt
        // if it cannot be found there, then the default in mmbase.config/xslt will be used.
        File useXslt;

        if (format != Attribute.NULL) {
            if (xslt != Attribute.NULL) {
                throw new JspTagException("Cannot specify both the 'xslt' attribute and the 'format' attribute");
            }
            if (xsltSource != null) {
                throw new JspTagException("Cannot use  'xslt' subtag and the 'format' attribute");
            }
            switch(getFormat()) {
            case FORMAT_XHTML:
                helper.useEscaper(false); // fieldinfo typicaly produces xhtml
                helper.setValue(xslTransform(doc, "xslt/2xhtml.xslt"));
                break;
            case FORMAT_PRESENTXML:
                helper.useEscaper(false); // fieldinfo typicaly produces xhtml
                helper.setValue(xslTransform(doc, "xslt/2xml.xslt"));
                break;
            case FORMAT_CODE:
                helper.setValue(xslTransform(doc, "xslt/code2xml.xslt"));
                break;
            case FORMAT_TEXTONLY:
                helper.useEscaper(true); 
                helper.setValue(xslTransform(doc, "xslt/2ascii.xslt"));
                break;
            case FORMAT_RICH:
                helper.useEscaper(false); // fieldinfo typicaly produces xhtml
                helper.setValue(xslTransform(doc, "xslt/mmxf2rich.xslt"));
                break;
            case FORMAT_ESCAPEXMLPRETTY:
                helper.useEscaper(false); // fieldinfo typicaly produces xhtml
                helper.setValue(Encode.encode("ESCAPE_XML", prettyXML(doc)));
                // helper.setValue(prettyXML(doc));
                break;
            // -- FORMAT_LIMIT_XML
            case FORMAT_ESCAPEXML:
                helper.useEscaper(false); // fieldinfo typicaly produces xhtml
                helper.setValue(Encode.encode("ESCAPE_XML", body));
                break;
            case FORMAT_DATE:
                java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat) java.text.SimpleDateFormat.getDateInstance();
                String pattern;
                if (options == Attribute.NULL) {
                    pattern = "yyyy-MM-dd HH:mm:ss";
                } else {
                    pattern = options.getString(this);
                }
                // iso 8601 for date/time
                dateFormat.applyPattern(pattern);
                Date datum = new Date((new Long(body)).longValue() * 1000);
                helper.setValue(dateFormat.format(datum));
                break;
            }
        } else {

            if (xslt != Attribute.NULL) {                
                if (xsltSource != null) {
                    throw new JspTagException("Cannot use  'xslt' subtag and the 'xslt' attribute");
                }
                if (log.isDebugEnabled()) log.debug("Transforming with " + xslt.getString(this));

                helper.setValue(xslTransform(doc, xslt.getString(this)));
            } else {
                if (xsltSource == null) {
                    throw new JspTagException("No 'format' attribute, no 'xslt' attribute and no 'xslt' subtag. Don't know what to do.");
                }
                helper.setValue(xslTransform(doc, xsltSource));
            }
        }

        if (log.isDebugEnabled()) {
            log.trace("found result " + helper.getValue());
            
        }
        

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }

        if (timerHandle != -1) {
            ((org.mmbase.bridge.jsp.taglib.debug.TimerTag)findParentTag(org.mmbase.bridge.jsp.taglib.debug.TimerTag.class, null, false)).haltTimer(timerHandle);
        }
        return helper.doEndTag();
    } // doEndTag




    /**
     * @return the Factory which must be used for XSL transformations in this directory.
     */

    private TransformerFactory getFactory() {
        return FactoryCache.getCache().getFactory(cwd);
    }

    /**
     * Base function for XSL conversions, which this Tag does.
     *
     * It returns a String, even if it goes wrong, in which case the string contains the error
     * message.
     * @param A Source (representing the XSLT).
     * @return The result ot the transformation.
     */
    private String xslTransform(Document doc, Source xsl) throws JspTagException {
        log.debug("transforming");

        TemplateCache cache= TemplateCache.getCache();
        Templates cachedXslt = cache.getTemplates(xsl);
        if (cachedXslt == null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("getting for " + cwd);
                }
                cachedXslt = getFactory().newTemplates(xsl);
                cache.put(xsl, cachedXslt);
            } catch (javax.xml.transform.TransformerConfigurationException e) {
                // throw new JspTagException(e.toString())
                return e.toString() + ": " + Logging.stackTrace(e);
            }
        } else {
            if (log.isDebugEnabled()) log.debug("Used xslt from cache with " + xsl.getSystemId());
        }

        // set some parameters to the XSLT style sheet.
        Map params = new HashMap();
        String context =  ((javax.servlet.http.HttpServletRequest)pageContext.getRequest()).getContextPath();
        params.put("formatter_requestcontext",  context);
        //params.put("formatter_imgdb", org.mmbase.module.builders.AbstractImages.getImageServletPath(context)); 
        // use node function
        LocaleTag localeTag = (LocaleTag) findParentTag(org.mmbase.bridge.jsp.taglib.LocaleTag.class, null, false);
        Locale locale;
        if (localeTag != null) {
            locale = localeTag.getLocale();
        } else {
            locale = Locale.getDefault(); // should perhaps somehow find the MMBase default language setting.
        }          
        params.put("formatter_language", locale.getLanguage());

        params.put("formatter_counter", counter.toString());

        //other options
        // a=b,c=d,e=f
        if (options != Attribute.NULL) {
            Iterator i = options.getList(this).iterator();
            while (i.hasNext()) {
                String option = (String) i.next();
                List   o = StringSplitter.split(option, "=");
                if (o.size() != 2) {
                    throw  new JspTagException("Option '" + option + "' is not in the format key=value (required for XSL transformations)");
                    
                } else {
                    if (log.isDebugEnabled()) log.debug("Setting XSLT option " + option);
                    params.put(o.get(0), o.get(1));
                }
            }
        }
       
        return ResultCache.getCache().get(cachedXslt, xsl,  params, null, doc);

    }
    /**
     * @see   xslTransform
     * @param A name of an XSLT file.
     */
    private String xslTransform(Document doc, String xsl) throws JspTagException {
        try {
            return xslTransform(doc, getFactory().getURIResolver().resolve(xsl, null));
         } catch (javax.xml.transform.TransformerException e) {
             throw new TaglibException(e); // probably the file could not be found.
         }
    }

    private String prettyXML(Document doc) throws JspTagException  {
        if ( log.isDebugEnabled() ) {
            log.trace("pretty XML " + doc);   
        }
        return xslTransform(doc, "xslt/indentxml.xslt");
    }
}
