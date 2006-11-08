/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import java.net.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mmbase.module.core.MMBase; // TODO
import org.mmbase.bridge.jsp.taglib.util.Referids;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.util.Casting;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * <p>
 * A lazy 'URL' creator. A container object that contains all necessary to construct an URL, but
 * will only do it on actual request (by the {@link #get}) method. This is also what is stored by an
 * url-tag with an id attribute.
 * </p>
 * <p>
 * The creation of the URL is delegated to the MMBase framework.
 * </p>
 * @version $Id: Url.java,v 1.7 2006-11-08 10:35:01 johannes Exp $;
 * @since MMBase-1.9
 */
public class Url implements Comparable {
    private static final Logger log = Logging.getLoggerInstance(Url.class);
    private final UrlTag tag;
    private final String page;
    private final Component component;
    protected final List<Map.Entry<String, Object>> params;
    private final String abs;
    private final boolean encodeUrl;
    private final boolean escapeAmps;

    private String cacheAmp = null;
    private String cacheNoAmp = null;
    private String string = null;


    public Url(UrlTag t, String p, Component comp, List<Map.Entry<String, Object>> pars) throws JspTagException {
        tag = t;
        abs = tag.absolute.getString(tag);
        encodeUrl = tag.encode.getBoolean(tag, true);
        escapeAmps = tag.escapeAmps.getBoolean(tag, true);
        page = p;
        params = pars;
        component = comp;
    }
    public Url(UrlTag t, Url u, List<Map.Entry<String, Object>> pars) throws JspTagException {
        tag = t;
        abs = tag.absolute.getString(tag);
        encodeUrl = tag.encode.getBoolean(tag, true);
        escapeAmps = tag.escapeAmps.getBoolean(tag, true);
        page = u.page;
        component = u.component;
        params = pars;
    }

    public String get(boolean writeamp) throws JspTagException {

        String result = writeamp ? cacheAmp : cacheNoAmp;
        if (result != null) return result;

        // TODO we should not use core.
        Framework framework = MMBase.getMMBase().getFramework();

        // perhaps this?
        //Framework is of course always only relevant for local cloud context.
        //Framework framework = LocalContext.getCloudContext().getFramework();

        Parameters frameworkParams = framework.createFrameworkParameters();
        tag.fillStandardParameters(frameworkParams);
        Parameters blockParams;
        if (component != null) {
            Block block = "".equals(page) ? component.getDefaultBlock() : component.getBlock(page);
            if (block == null) throw new TaglibException("There is no block " + page + " in component " + component);
            blockParams = block.createParameters(); 
            tag.fillStandardParameters(blockParams);
            blockParams.setAutoCasting(true);
            for (Map.Entry<String, ?> entry : params) {
                blockParams.set(entry.getKey(), entry.getValue());
            }

        } else {
            // no component, this is a normal 'link'. no compoments, so no block can be guessed.
            blockParams = new Parameters(params); // sad that this will make it needed to copy the  entire list again.
        }
        result = framework.getUrl(page, component, blockParams, frameworkParams, writeamp).toString();
        if (writeamp) {
            cacheAmp = result;
        } else {
            cacheNoAmp = result;
        }
        return result;
    }

    protected boolean useAbsoluteAttribute(StringBuilder show, String p) throws JspTagException {

        if ("".equals(abs) || "false".equals(abs)) {
            return false;
        }

        HttpServletRequest req = (HttpServletRequest) tag.getPageContext().getRequest();

        if (abs.equals("true")) {
            show.append(req.getScheme()).append("://");
            show.append(req.getServerName());
            int port = req.getServerPort();
            String scheme = req.getScheme();
            show.append((port == 80 && "http".equals(scheme)) ||
                        (port == 443 && "https".equals(scheme)) 
                        ? "" : ":" + port);
        } else if (abs.equals("server")) {
            //show.append("/");
        } else if (abs.equals("context")) {

        } else {
            throw new JspTagException("Unknown value for 'absolute' attribute '" + abs + "' (must be either 'true', 'false', 'server' or 'context')");
        }
        if (! abs.equals("context")) {
            show.append(req.getContextPath());
        }
        char firstChar = p.charAt(0);
        try {
            URI uri;
            if (firstChar != '/') {
                uri = new URI("servlet", req.getServletPath() + "/../" + p, null);
            } else {
                uri = new URI("servlet", p, null);
            }
            uri = uri.normalize(); // resolves .. and so one
            show.append(uri.getSchemeSpecificPart());
        } catch (URISyntaxException  use) {
            throw new TaglibException(use.getMessage(), use);
        }
        return true;
    }

    public String get() {
        if (string != null) return string;
        try { 
            String u = get(escapeAmps);
            StringBuilder show = new StringBuilder();
            if (! useAbsoluteAttribute(show, u)) {
                if (u.charAt(0) == '/') {
                    HttpServletRequest req =  (HttpServletRequest) tag.getPageContext().getRequest();
                    show.insert(0, req.getContextPath());
                }
                show.append(u);
            }
            if (encodeUrl) {
                HttpServletResponse response = (HttpServletResponse)tag.getPageContext().getResponse();
                string = response.encodeURL(show.toString());
            } else {
                string = show.toString();
            }
        } catch (Throwable e){
            string =  e.toString();
        }
        return string;
    }

    public String toString() {
        // this means that it is written to page by ${_} and that consequently there _must_ be a body.
        // this is needed when body is not buffered.
        tag.haveBody();
        return get();
    }
    public int compareTo(Object o) {
        return toString().compareTo(Casting.toString(o));
    }
}
