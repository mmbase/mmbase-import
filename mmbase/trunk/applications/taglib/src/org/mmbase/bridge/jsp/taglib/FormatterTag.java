/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;


import org.mmbase.bridge.jsp.taglib.Writer;
import org.mmbase.bridge.jsp.taglib.WriterHelper;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;

import org.mmbase.bridge.util.xml.Generator;


import java.io.File;
import org.mmbase.module.core.MMBaseContext; 
import javax.servlet.jsp.PageContext;

import org.mmbase.util.xml.URIResolver;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The formatter can reformat its body. It usually uses XSL for this.
 *
 *
 * @since  MMBase-1.6 
 * @author Michiel Meeuwissen
 */
public class FormatterTag extends ContextReferrerTag  implements Writer {

    private static Logger log = Logging.getLoggerInstance(FormatterTag.class.getName());

    private static int cacheSize = 50;
    private static org.mmbase.util.LRUHashtable xsltCache;

    // standard Writer properties:
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
        return helper.getValue();
    }
    public void haveBody() {
        helper.haveBody();
    }
           
    protected String   xslt    = null;
    protected int      format  = FORMAT_UNSET;
    protected String   options = null;

    protected Source   xsltSource = null;
    protected String   xsltSourceSubKey;
  
    private static javax.xml.parsers.DocumentBuilder        documentBuilder;
    private static org.mmbase.util.LRUHashtable             tfactoryCache;
    private File cwd;
    

    // formats that needs XML input, when setting these 'wantXML' will be true, and a DOM Document will be created.
    
    // XSLT transformations:
    private static final int FORMAT_XHTML      = 1;
    private static final int FORMAT_PRESENTXML = 2;
    private static final int FORMAT_CODE       = 3;
    private static final int FORMAT_TEXTONLY   = 4;
    private static final int FORMAT_RICH       = 5;

    // wants XML, but uses Encode to output (no XSLT is used).
    private static final int FORMAT_ESCAPEXMLPRETTY = 500;
    

    // These formats take the body as a string. So the body doesn't need to be valid XML.
    private static final int FORMAT_ESCAPEXML       = 1001;
    private static final int FORMAT_DATE            = 1002;
    private static final int FORMAT_LOWERCASE       = 1003;
    private static final int FORMAT_UPPERCASE       = 1004;

    
    private static final int FORMAT_UNSET           = -1;


    static {
        log.service("static init of FormatterTag.");    
        //readConfiguration();
        xsltCache = new org.mmbase.util.LRUHashtable(cacheSize, 0);
        tfactoryCache = new org.mmbase.util.LRUHashtable(cacheSize, 0);

        try {
            javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            documentBuilder = dfactory.newDocumentBuilder();
            org.xml.sax.ErrorHandler handler = new org.mmbase.util.XMLErrorHandler();
            documentBuilder.setErrorHandler(handler);
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
     * A handle necessary when using the Time Tag;
     */
    protected int timerHandle;

    /**
     * You can give the path the the XSLT-file by this attribute.
     */
    public void setXslt(String x) throws JspTagException {
        xslt = getAttributeValue(x);
    }

    /**
     * Predefined formattings.
     */
    public void setFormat(String f) throws JspTagException {
        String fs = getAttributeValue(f);
        if ("xhtml".equalsIgnoreCase(fs)) {
            format = FORMAT_XHTML;            
        } else if ("presentxml".equalsIgnoreCase(fs)) {
            format = FORMAT_PRESENTXML;            
        } else if ("code".equalsIgnoreCase(fs)) {
            format = FORMAT_CODE;
        } else if ("escapexml".equalsIgnoreCase(fs)) {
            format = FORMAT_ESCAPEXML;
        } else if ("escapexmlpretty".equalsIgnoreCase(fs)) {
            format = FORMAT_ESCAPEXMLPRETTY;
        } else if ("date".equalsIgnoreCase(fs)) {
            format = FORMAT_DATE;
        } else if ("textonly".equalsIgnoreCase(fs)) {
            format = FORMAT_TEXTONLY;
        } else if ("rich".equalsIgnoreCase(fs)) {
            format = FORMAT_RICH;
        } else {
            throw new JspTagException("Unknown format " + fs + "(" + f + ")");
        }         
    }

    public void setOptions(String o) throws JspTagException {
        options = getAttributeValue(o);
    }

    /**
     * The  Xslt tag will call this, to inform this tag about the XSLT which must be done.
     */
    public void setXsltSource(Source xs, String subKey) {
        xsltSource = xs;
        xsltSourceSubKey = subKey;
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
        // what would evaluate quicker?
        // return format < 1000;
        return xmlGenerator != null;
    }
   

    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        // get a documentBuilder.
        // the uriResolver must be set per page, because it can be the same instance for all formatters on this page.
        javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
        cwd = new File(pageContext.getServletContext().getRealPath(request.getServletPath())).getParentFile(); 

    }            
    
    public int doStartTag() throws JspTagException {  
        // serve parent timer tag:
        TagSupport t = findParentTag("org.mmbase.bridge.jsp.taglib.debug.TimerTag", null, false);
        if (t != null) {
            timerHandle = ((org.mmbase.bridge.jsp.taglib.debug.TimerTag)t).startTimer(getId(), getClass().getName());
        } else {
            timerHandle = -1;
        }

        xsltSource = null;
        if (format > FORMAT_UNSET  && xslt != null) {
            throw new JspTagException ("One of the attributes xslt and format must be specified, or none (then you have to use an mm:xslt subtag.");
        }
    
        if (format < 1000) {  // also if format is unset.
            xmlGenerator = new Generator(documentBuilder.newDocument());
        } else {
            xmlGenerator = null; // my childen will know, that this formatter doesn't want them.
        }

        return EVAL_BODY_TAG;
    }

    public int doEndTag() throws JspTagException {
        helper.setBodyContent(bodyContent);
        if (helper.getJspvar() == null) {
            helper.overrideWrite(true); 
        }        

        // If there is some bodycontent, then we can add also that to
        // the Document. This is of course mainly useful for debugging purposes.
        String body = bodyContent.getString().trim();
        bodyContent.clearBody(); // should not be shown itself.

        if(wantXML() && xmlGenerator.getDocument().getDocumentElement() != null && body.length() > 0) {
            throw new JspTagException ("It is not possible to have tags which produce DOM-XML and  text in the body.");
        }
/*        if (wantXML()) {
            if (log.isDebugEnabled()) log.debug("bodycontent:>" + body + "<");
            Document bodyContentDocument;
            try {
                bodyContentDocument = documentBuilder.parse(new java.io.ByteArrayInputStream(body.getBytes("UTF-8"))); 
            } catch (Exception e) {
                throw new JspTagException(body + ":" +  e.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug("created an element: " + bodyContentDocument.getDocumentElement().getTagName());            
            }
            org.w3c.dom.Node field = doc.importNode(bodyContentDocument.getDocumentElement(), true);
            doc.appendChild(field);
        } */


        if (log.isDebugEnabled()) {
            if (wantXML()) {
                log.debug("XSL converting document: " + xmlGenerator.toStringFormatted());
            } else {
                log.debug("Converting: " + body);
            }
        }


        // Determin which XSL to use.
        // useXsl is the pathless name.
        // First it is searced for in <current directory>/xslt
        // if it cannot be found there, then the default in mmbase.config/xslt will be used.
        File useXslt;

        if (format > FORMAT_UNSET) {
            if (xslt != null) {
                throw new JspTagException("Cannot specify both the 'xslt' attribute and the 'format' attribute");
            }
            if (xsltSource != null) {
                throw new JspTagException("Cannot use  'xslt' subtag and the 'format' attribute");
            }
            switch(format) {
            case FORMAT_XHTML:
                helper.setValue(XSLTransform("xslt/2xhtml.xslt"));
                break;
            case FORMAT_PRESENTXML:
                helper.setValue(XSLTransform("xslt/2xml.xslt"));
                break;
            case FORMAT_CODE:
                helper.setValue(XSLTransform("xslt/code2xml.xslt"));
                break;
            case FORMAT_TEXTONLY:
                helper.setValue(XSLTransform("xslt/2ascii.xslt"));
                break;
            case FORMAT_RICH:
                helper.setValue(XSLTransform("xslt/mmxf2rich.xslt"));
                break;
            case FORMAT_ESCAPEXMLPRETTY:
                helper.setValue(Encode.encode("ESCAPE_XML", xmlGenerator.toStringFormatted()));
                break;
            case FORMAT_ESCAPEXML:
                helper.setValue(Encode.encode("ESCAPE_XML", body));
                break;
            case FORMAT_DATE:
                java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat) java.text.SimpleDateFormat.getDateInstance();
                if (options == null) {
                    options = "yyyy-MM-dd HH:mm:ss";
                }
                // iso 8601 for date/time
    	    	dateFormat.applyPattern(options);
	    	java.util.Date datum = new java.util.Date((new Long(body)).longValue() * 1000);
                helper.setValue(dateFormat.format(datum));
                break;
            }
        } else {
            
            if (xslt != null) {
                if (xsltSource != null) {
                    throw new JspTagException("Cannot use  'xslt' subtag and the 'xslt' attribute");
                }
                if (log.isDebugEnabled()) log.debug("Transforming with " + xslt);
                                                                                 
                helper.setValue(XSLTransform(xslt));
            } else {
                if (xsltSource == null) {
                    throw new JspTagException("No 'format' attribute, no 'xslt' attribute and no 'xslt' subtag. Don't know what to do.");
                }
                helper.setValue(XSLTransform(xsltSource, xsltSourceSubKey));
            }
        }


        helper.setJspvar(pageContext);  
        
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }

        if (timerHandle != -1) {
            ((org.mmbase.bridge.jsp.taglib.debug.TimerTag)findParentTag("org.mmbase.bridge.jsp.taglib.debug.TimerTag", null, false)).haltTimer(timerHandle);
        }
        return helper.doAfterBody();
    }


    /**
     * Base function for XSL conversions, which this Tag does.
     *
     * It returns a String, even if it goes wrong, in which case the string contains the error message.
     *
     */
    private String XSLTransform(String xsl) {
        try {
            return XSLTransform(getFactory().getURIResolver().resolve(xsl, null), xsl);
        } catch (Exception e) {
            String msg =  "XSL transformation did not succeed: " + e.toString();
            log.service(msg); // don't log this as warning or error, because web site builders can generate their own XSLT, which can contain errors.
            log.error(Logging.stackTrace(e));
            return msg + "\n";
        }
    }

    private String xsltCacheKey(String subKey) {
        return cwd.toString() + "." + subKey;
    }

    private TransformerFactory getFactory() {
        TransformerFactory tfactory = (TransformerFactory) tfactoryCache.get(cwd);
        if (tfactory == null) {
            URIResolver uriResolver = new URIResolver(cwd);
            tfactory = TransformerFactory.newInstance();
            tfactory.setURIResolver(uriResolver);
            // you must set the URIResolver in the tfactory, because it will not be called everytime, when you uses Templates-caching.
            tfactoryCache.put(cwd, tfactory);
        }
        return tfactory;
    }

    private String XSLTransform(Source xsl, String subKey) {
        log.debug("transforming");
        try {
            String key = xsltCacheKey(subKey);
            //Transformer trans = cachedXSLT.newTransformer();
            Templates cachedXslt = (Templates) xsltCache.get(key);
            if (cachedXslt == null) {                
                log.service("Put xslt in cache with key " + key);
                cachedXslt = getFactory().newTemplates(xsl);
                xsltCache.put(key, cachedXslt);
            } else {
                log.service("Used xslt from cache with " + key);
            }
            javax.xml.transform.Transformer transformer = cachedXslt.newTransformer();
          
            // set some parameters to the XSLT style sheet.
            // transformer.setParameter("formatter_xsltpath", baseXsltPath);        
            // necessary when extending XSLT's (this will problably not be used yet)
            // no idea if I get that working.
           
            transformer.setParameter("formatter_requestcontext",  
                                     ((javax.servlet.http.HttpServletRequest)pageContext.getRequest()).getContextPath()); 
            // necessary to generate Image urls.

            // getting the language from the locale, this is perhaps not a very good idea, 
            // but for the moment, I don't know a sensible other place to get it from.
            transformer.setParameter("formatter_language",  
                                     java.util.Locale.getDefault().getLanguage());
            
            java.io.StringWriter res = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(xmlGenerator.getDocument()),
                                  new javax.xml.transform.stream.StreamResult(res));
            return res.toString();
        } catch (Exception e) {
            String msg =  "XSL transformation did not succeed: " + e.toString();
            log.service(msg); // don't log this as warning or error, because web site builders can generate their own XSLT, which can contain errors.
            log.error(Logging.stackTrace(e));
            return msg + "\n";
        }
    }

}


