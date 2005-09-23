/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.File;
import java.util.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.images.*;
import org.mmbase.util.UriParser;
import org.mmbase.module.builders.AbstractServletBuilder;
import org.mmbase.module.builders.Images;

import org.mmbase.security.Rank;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Produces an url to the image servlet mapping. Using this tag makes
 * your pages more portable to other system, and hopefully less
 * sensitive for future changes in how the image servlet works.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ImageTag.java,v 1.61 2005-09-23 09:21:51 michiel Exp $
 */

public class ImageTag extends FieldTag {

    private static final Logger log = Logging.getLoggerInstance(ImageTag.class);

    private static final int MODE_URL = 0;
    private static final int MODE_HTML_ATTRIBUTES = 1;
    private static final int MODE_HTML_IMG = 2;
        

    private static Boolean makeRelative = null;
    private Attribute template = Attribute.NULL;
    private Attribute mode     = Attribute.NULL;


    private Object prevDimension;

    /**
     * The transformation template
     */
    public void setTemplate(String t) throws JspTagException {
        template = getAttribute(t);
    }

    public void setMode(String m) throws JspTagException {
        mode = getAttribute(m);
    }

    private int getMode() throws JspTagException {
        String m = mode.getString(this).toLowerCase();
        if (m.equals("") || m.equals("url")) {
            return MODE_URL;
        } else if (m.equals("attributes")) {
            return MODE_HTML_ATTRIBUTES;
        } else if (m.equals("img")) {
            return MODE_HTML_IMG;
        } else {
            throw new JspTagException("Value '" + m + "' not known for 'mode' attribute");
        }
    }


    public int doStartTag() throws JspTagException {
        Node node = getNode();
        if (!node.getNodeManager().hasField("handle")) {
            throw new JspTagException("Found parent node '" + node.getNumber() + "' of type " + node.getNodeManager().getName() + " does not have 'handle' field, therefore cannot be a image. Perhaps you have the wrong node, perhaps you'd have to use the 'node' attribute?");
        }

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        String servletArgument; // can be the node-number or a template (if that is configured to be allowed).

        String t = template.getString(this);        
        if ("true".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.image.format.asis"))) {
            if (t.length() > 0) {
                t = t + "+f(asis)";
            } else {
                t = "f(asis)";
            }
        }
        if ("".equals(t)) {
            // the node/image itself
            servletArgument = node.getStringValue("number");
        } else {
            if ("true".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.image.urlconvert"))) {
                try {                    
                    servletArgument = "" + node.getNumber() + "+" + java.net.URLEncoder.encode(t, "UTF-8");
                } catch (java.io.UnsupportedEncodingException uee) {
                    // cannot happen 'UTF-8' is supported.
                    servletArgument = "" + node.getNumber() + "+" + t;
                }
            } else {
                // the cached image
                servletArgument = node.getFunctionValue("cache", new ParametersImpl(Images.CACHE_PARAMETERS).set("template", t)).toString();
            }
        }

        if (makeRelative == null) {            
            String setting = pageContext.getServletContext().getInitParameter("mmbase.taglib.url.makerelative");
            makeRelative = "true".equals(setting) ? Boolean.TRUE : Boolean.FALSE;
        }


        String servletPath;
        Function servletPathFunction = node.getFunction("servletpath");        
        Parameters args = servletPathFunction.createParameters();
        args.set("context",  makeRelative.booleanValue() ? UriParser.makeRelative(new File(req.getServletPath()).getParent(), "/") : req.getContextPath())
            .set("argument", servletArgument)
            ;
        fillStandardParameters(args);
        servletPath = servletPathFunction.getFunctionValue( args).toString();

        helper.useEscaper(false);
        prevDimension = pageContext.getAttribute("dimension");
        switch(getMode()) {
        case MODE_URL: 
            helper.setValue(((HttpServletResponse) pageContext.getResponse()).encodeURL(servletPath));
            pageContext.setAttribute("dimension", new LazyDimension(getNodeVar(), t));
            break;
        case MODE_HTML_ATTRIBUTES: {
            List a = new ArrayList();
            a.add(t);
            Dimension dim = (Dimension) getNodeVar().getFunctionValue("dimension", a).get();
            String url = ((HttpServletResponse) pageContext.getResponse()).encodeURL(servletPath);
            if (dim.getHeight() > 0 && dim.getWidth() > 0) {
                helper.setValue("src=\"" + url + "\" height=\"" + dim.getHeight() + "\" width=\"" + dim.getWidth() + "\"");
            } else {
                log.warn("Found odd dimension " + dim);
                helper.setValue("src=\"" + url + "\"");
            }
            pageContext.setAttribute("dimension", dim);
            break;
        }
        case MODE_HTML_IMG: {
            List a = new ArrayList();
            a.add(t);
            Dimension dim = (Dimension) node.getFunctionValue("dimension", a).get();
            String url = ((HttpServletResponse) pageContext.getResponse()).encodeURL(servletPath);
            String alt;
            if (node.getNodeManager().hasField("title")) {
                alt = org.mmbase.util.transformers.Xml.XMLAttributeEscape(node.getStringValue("title"));
            } else if (node.getNodeManager().hasField("name")) {
                alt = org.mmbase.util.transformers.Xml.XMLAttributeEscape(node.getStringValue("name"));
            } else {
                alt = null;
            }
            if (dim.getHeight() > 0 && dim.getWidth() > 0) {
                helper.setValue("<img src=\"" + url + "\" height=\"" + dim.getHeight() + "\" width=\"" + dim.getWidth() + "\" " +
                                (alt == null ? "" : " alt=\"" + alt + "\"") + " />"
                                );
            } else {
                log.warn("Found odd dimension " + dim);
                helper.setValue("<img src=\"" + url + "\" " + 
                                (alt == null ? "" : " alt=\"" + alt + "\"") + " />"
                                );
            }
            pageContext.setAttribute("dimension", dim);
        }
        }
        
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }


        
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspTagException {
        if (prevDimension == null) {
            pageContext.removeAttribute("dimension");
        } else {
            pageContext.setAttribute("dimension", prevDimension);
        }
        helper.doEndTag();
        return super.doEndTag();
        
    }
}

