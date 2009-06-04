/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Referids;
import org.mmbase.bridge.jsp.taglib.util.Notfound;
import org.mmbase.bridge.jsp.taglib.util.Debug;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.jsp.taglib.ContextTag;
import org.mmbase.bridge.NotFoundException;
import java.net.*;

import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like UrlTag, but does not spit out an URL, but the page itself.
 *
 * @author Michiel Meeuwissen
 * @author Johannes Verelst
 * @version $Id$
 */

public class IncludeTag extends UrlTag {

    private static  final Logger log     = Logging.getLoggerInstance(IncludeTag.class);
    private static  final Logger pageLog = Logging.getLoggerInstance(Logging.PAGE_CATEGORY);


    public static final String INCLUDE_PATH_KEY   = "javax.servlet.include.servlet_path";
    public static final String INCLUDE_LEVEL_KEY = "org.mmbase.taglib.includeLevel";

    protected static int MAX_INCLUDE_LEVEL = -1;
    private static final Xml xml = new Xml(Xml.ESCAPE);



    protected Attribute debugType       = Attribute.NULL;

    private Attribute cite              = Attribute.NULL;

    private Attribute encodingAttribute = Attribute.NULL;

    private Attribute attributes        = Attribute.NULL;

    protected Attribute notFound        = Attribute.NULL;

    protected Attribute resource        = Attribute.NULL;

    protected Attribute timeOut        = Attribute.NULL;
    //protected Attribute configuration   = Attribute.NULL;

    protected Attribute omitXmlDeclaration = Attribute.NULL;

    /**
     * Test whether or not the 'cite' parameter is set
     */
    public void setCite(String c) throws JspTagException {
        cite = getAttribute(c);
    }

    public void setEncoding(String e) throws JspTagException {
        encodingAttribute = getAttribute(e);
    }

    public void setNotfound(String n) throws JspTagException {
        notFound = getAttribute(n);
    }

    protected boolean getCite() throws JspTagException {
        return resource != Attribute.NULL || cite.getBoolean(this, false);
    }

    public void setAttributes(String a) throws JspTagException {
        attributes = getAttribute(a);
    }

    public void setResource(String r) throws JspTagException {
        resource = getAttribute(r);
    }
    /**
     * @since MMBase-1.8.5
     */
    public void setTimeout(String t) throws JspTagException {
        timeOut = getAttribute(t);
    }
    /*
    public void setConfiguration(String r) throws JspTagException {
        configuration = getAttribute(r);
    }
    */

    @Override
    protected String getPage() throws JspTagException {
        if (resource != Attribute.NULL) return resource.getString(this);
        return super.getPage();
    }


    /**
     * @since MMBase-1.8.5
     */
    protected void checkAttributes() throws JspTagException {
        if (page == Attribute.NULL && resource == Attribute.NULL && referid == Attribute.NULL) { // for include tags, page attribute is obligatory.
            throw new JspTagException("No attribute 'page', 'resource' or 'referid' was specified");
        }
    }
    @Override
    public int doStartTag() throws JspTagException {
        checkAttributes();
        initTag(true);
        return EVAL_BODY_BUFFERED;
    }


    @Override
    protected void doAfterBodySetValue() throws JspTagException {
        try {
            includePage();
        } catch (org.mmbase.framework.FrameworkException fw) {
            throw new TaglibException(fw);
        }
    }
    /**
     * Opens an Http Connection, retrieves the page, and returns the result.
     **/
    private void external(BodyContent bodyContent, String absoluteUrl, HttpServletRequest request, HttpServletResponse response) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("External: found url: >" + absoluteUrl + "<");
        }
        try {
            URL includeURL = new URL(absoluteUrl);

            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
            int to = timeOut.getInt(this, 10000);
            connection.setConnectTimeout(to);
            connection.setReadTimeout(to);

            if (request != null) {
                // Also propagate the cookies (like the jsession...)
                // Then these, and the session,  also can be used in the include-d page
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    StringBuilder koekjes = new StringBuilder();
                    String sep = "";
                    for (Cookie cookie : cookies) {
                        if (log.isDebugEnabled()) {
                            log.debug("setting cookie:" + cookie.getName() + "=" + cookie.getValue());
                        }
                        koekjes.append(sep).append(cookie.getName()).append("=").append(cookie.getValue());
                        sep = ";";
                    }
                    connection.setRequestProperty("Cookie", koekjes.toString());
                }
            }

            String result;
            int responseCode;
            try {
                responseCode = connection.getResponseCode();
                // how about responsecodes < 200?
                if (responseCode < 300) {

                    String encoding = encodingAttribute.getString(this);
                    if (encoding.length() == 0) {
                        encoding = connection.getContentEncoding();
                    }
                    log.debug("Found content encoding " + encoding);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    InputStream inputStream = connection.getInputStream();
                    int c = inputStream.read();
                    while (c != -1) {
                        bytes.write(c);
                        c = inputStream.read();
                    }
                    byte[] allBytes = bytes.toByteArray();
                    if (encoding == null || encoding.length() == 0) {
                        String contentType = connection.getContentType();
                        if (contentType != null) {
                            // according to http://www.w3.org/TR/2002/NOTE-xhtml-media-types-20020801/, 'higher level' charset indication should prevail
                            encoding = GenericResponseWrapper.getEncoding(contentType);
                        }

                        if (encoding == null && contentType != null) { // take a default based on the content type
                            encoding = GenericResponseWrapper.getDefaultEncoding(contentType);
                        }
                        if (encoding.equals(GenericResponseWrapper.TEXT_XML_DEFAULT_CHARSET)) { // if content-type is text/xml the body should be US-ASCII, which we will ignore and evalute the body. See comments in GenericResponseWrapper#getDefaultEncoding.
                            encoding = GenericResponseWrapper.getXMLEncoding(allBytes); // Will detect if body is XML, and set encoding to something if it is, otherwise, remains null.
                        }

                    }
                    log.debug("Using " + encoding);
                    result = new String(allBytes, encoding) + getDebug().end(getThisName(), absoluteUrl);

                } else {
                    if (responseCode >= 500) {
                        result = "Server error " + responseCode + " during mm:include of " + includeURL + " " + connection.getResponseMessage();
                    } else if (responseCode >= 400) {
                        result = "Client error " + responseCode + " during mm:include of " + includeURL + " " + connection.getResponseMessage();
                    } else { // >= 300 < 400
                        result = "Redirect " + responseCode + " during mm:include of " + includeURL + " " + connection.getResponseMessage() + " " + connection.getInstanceFollowRedirects();
                    }
                }
            } catch (java.net.ConnectException ce) {
                result = "For " + includeURL + ": " + ce.getMessage();
                responseCode = -1;
            } catch (java.net.SocketTimeoutException ste) {
                result = "";
                responseCode = -2;
            }


            handleResponse(responseCode, result, absoluteUrl);

            if (log.isDebugEnabled()) {
                log.debug("found string: " + helper.getValue());
            }
        } catch (IOException e) {
            throw new TaglibException (e.getMessage(), e);
        }
    }

    /**
     * @since MMBase-1.8
     */
    protected void handleResponse(int code, String result, String url) throws JspTagException {
        pageContext.setAttribute("_responseCode", code);
        log.debug("" + code);
        String output;
        switch(code) {
        case -2:
        case 200:
            output = result;
            break;
        default:
        case 404:
            switch(Notfound.get(notFound, this)) {
            case Notfound.LOG:
                pageLog.warn("The requested resource '" + url + "' is not available", new Exception());
                output = "";
                break;
            case Notfound.SKIP:
            case Notfound.PROVIDENULL:
                output = "";
                break;
            case Notfound.THROW:
                if ("".equals(result)) result = "The requested resource '" + url + "' is not available";
                throw new JspTagException(result);
            default:
            case Notfound.DEFAULT:
            case Notfound.MESSAGE:
                if ("".equals(result)) {
                    result = "The requested resource '" + xml.transform(url) + "' is not available";
                }
                output = result;
            }
            break;
        }
        Debug debug = getDebug();
        helper.setValue(debug.start(getThisName(), url) + output + debug.end(getThisName(), url));
    }
    /**
     * Use the RequestDispatcher to include a page without doing a request.
     * Encoding apparently work, but why they do isn't very clear.
     */
    private void internal(BodyContent bodyContent, String relativeUrl, HttpServletRequest req, HttpServletResponse resp) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Internal: found url: >" + relativeUrl + "<");
            String targetEncoding   = resp.getCharacterEncoding();
            log.debug("encoding: " + targetEncoding);
            log.debug("req Parameters");
            Map params = req.getParameterMap();
            Iterator i = params.entrySet().iterator();
            Object o;
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                o = e.getValue();
                if (o instanceof String[]) {
                    log.debug("key '" + e.getKey() + "' value '" + Arrays.asList((String[]) o) + "'");
                } else {
                    log.debug("key '" + e.getKey() + "' value '" + o.toString() + "'");
                }
            }
        }

        req.removeAttribute(ContextTag.CONTEXTTAG_KEY);

        if (attributes != Attribute.NULL) {
            Iterator<Map.Entry<String, Object>> i = Referids.getReferids(attributes, this).entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, Object> entry = i.next();
                req.setAttribute(entry.getKey(), entry.getValue());
            }

        }
        // Orion bug fix.
        req.getParameterMap();

        try {
            ServletContext sc = pageContext.getServletContext();
            if (sc == null) log.error("Cannot retrieve ServletContext from PageContext");

            if (! ResourceLoader.getWebRoot().getResource(relativeUrl).openConnection().getDoInput()) {
                handleResponse(404, "No such resource " + xml.transform(relativeUrl), relativeUrl);
            } else {
                HttpServletRequestWrapper requestWrapper   = new HttpServletRequestWrapper(req);

                RequestDispatcher requestDispatcher = sc.getRequestDispatcher(relativeUrl);
                if (requestDispatcher == null) {
                    throw new NotFoundException("Page \"" + relativeUrl + "\" does not exist (No request-dispatcher could be created)");
                }

                IncludeWrapper responseWrapper;
                String encoding = encodingAttribute.getString(this);
                if (encoding.length() == 0) {
                    responseWrapper = new IncludeWrapper(resp);
                } else {
                    responseWrapper = new IncludeWrapper(resp, encoding);
                }
                requestDispatcher.include(requestWrapper, responseWrapper);
                handleResponse(responseWrapper.getStatus(), responseWrapper.toString(), relativeUrl);
            }

            getThreadPageContext();
        } catch (Throwable e) {
            log.error(relativeUrl, e);
            throw new TaglibException(relativeUrl + " " + e.getMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("req Parameters");
            Map params = req.getParameterMap();
            Iterator i = params.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                log.debug("key '" + e.getKey() + "' value '" + e.getValue() + "'");
            }
        }
    }



    /**
     * When staying in the same web-application, then the file also can be found on the file system,
     * and the possibility arises simply citing it (passing the web-server). It is in no way
     * interpreted then. This can be useful when creating example pages.
     * @param bodyContent unused
     * @param relativeUrl URL to cite relative to root of web-app.
     * @param request     unused
     */
    private void cite(BodyContent bodyContent, String relativeUrl, HttpServletRequest request) throws JspTagException {
        try {
            if (log.isDebugEnabled()) log.trace("Citing " + relativeUrl);
            if (resource == Attribute.NULL) {
                if (relativeUrl.indexOf("..") > -1 || relativeUrl.toUpperCase().indexOf("WEB-INF") > -1)  { // toUpperCase: just for windows, of course
                    throw new JspTagException("Not allowed to cite " + relativeUrl);
                }
            }

            // take of the sessionid if it is present
            //HttpSession session = request.getSession(false);
            //if (session != null && session.isNew())
            { // means there is a ;jsession argument
                int j = relativeUrl.lastIndexOf(';');
                if (j != -1) {
                    relativeUrl = relativeUrl.substring(0, j);
                }

            }


            String resource = relativeUrl;
            if (log.isDebugEnabled()) log.debug("Citing " + resource);


            Reader reader = ResourceLoader.getWebRoot().getReader(resource);
            if (reader == null) {
                handleResponse(404, "No such resource to cite " + resource, resource);
            } else {
                StringWriter writer = new StringWriter();
                IOUtil.copy(reader, writer);
                handleResponse(200, writer.toString(), resource);
            }

        } catch (IOException e) {
            throw new TaglibException (e);
        }
    }

    /**
     * Includes another page in the current page.
     */
    protected void includePage() throws JspTagException, org.mmbase.framework.FrameworkException {
        if (MAX_INCLUDE_LEVEL == -1) {
            String s =  pageContext.getServletContext().getInitParameter("mmbase.taglib.max_include_level");
            if (s != null && ! "".equals(s)) {
                MAX_INCLUDE_LEVEL = Integer.parseInt(s);
            }
            if (MAX_INCLUDE_LEVEL == -1) {
                MAX_INCLUDE_LEVEL = 50;
            }
        }

        try {
            String gotUrl = url == null ? null : url.get(false);
            if (gotUrl == null) {
                gotUrl = page.getString(this);
                pageLog.service("No URL object found (" + url + "), using: " + gotUrl);
            }

            if (gotUrl == null || "".equals(gotUrl)) {
                return; //if there is no url, we cannot include
            }

            if (pageLog.isServiceEnabled()) {
                pageLog.service("Parsing mm:include JSP page: " + gotUrl);
            }

            // Do some things to make the URL absolute.
            String nudeUrl; // url withouth the params
            String params;  // only the params.
            int paramsIndex = gotUrl.indexOf('?');
            if (paramsIndex != -1) {
                nudeUrl = gotUrl.substring(0, paramsIndex);
                params  = gotUrl.substring(paramsIndex);
            } else {
                nudeUrl = gotUrl;
                params  = "";
            }
            if (log.isDebugEnabled()) {
                log.debug("Found nude url " + nudeUrl);
            }
            HttpServletRequest request   = (HttpServletRequest) pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();


            if (nudeUrl.indexOf(':') == -1) { // relative
                // Fetch include level from Attributes, mainly for debugging
                Integer level = (Integer) request.getAttribute(INCLUDE_LEVEL_KEY);
                int includeLevel;
                if (level == null) {
                    includeLevel = 0;
                } else {
                    includeLevel = level.intValue();
                }

                // Fetch the current servlet from request attribute.
                // This is needed when we are resolving relatively.
                String includingServlet = (String) request.getAttribute(INCLUDE_PATH_KEY);
                if (includingServlet == null) {
                    includingServlet = request.getServletPath();
                }
                if (log.isDebugEnabled()) {
                    log.debug("Including from : Level=" + includeLevel + " URI=" + includingServlet);
                }

                String includedServlet;
                if (nudeUrl.startsWith("/")) {
                    log.debug("URL was absolute on servletcontext");
                    includedServlet = gotUrl;
                } else {
                    log.debug("URL was relative");
                    // Using url-objects only because they know how to resolve relativity
                    URL u = new URL("http", "localhost", includingServlet);
                    URL dir = new URL(u, "."); // directory

                    File currentDir = new File(includingServlet + "includetagpostfix"); // to make sure that it is not a directory (tomcat 5 does not redirect then)
                    nudeUrl = new URL(dir, nudeUrl).getFile();
                    includedServlet = nudeUrl + params;
                }

                // Increase level and put it together with the new URI in the Attributes of the request
                includeLevel++;
                request.setAttribute(INCLUDE_LEVEL_KEY, Integer.valueOf(includeLevel));

                if (log.isDebugEnabled()) {
                    log.debug("Next Include: Level=" + includeLevel + " URI=" + includedServlet);
                }

                if (includeLevel < MAX_INCLUDE_LEVEL) {
                    if (getCite()) {
                        cite(bodyContent, includedServlet, request);
                    } else {
                        internal(bodyContent, includedServlet, request, response);
                    }
                } else {
                    log.warn("TOO DEEP mm:include recursion (" + includedServlet + "), " + includeLevel + ">=" + MAX_INCLUDE_LEVEL);
                    handleResponse(200,
                                   "TOO DEEP mm:include recursion (" + xml.transform(includedServlet) + "), " + includeLevel + ">=" + MAX_INCLUDE_LEVEL, includedServlet);

                }
                // Reset include level and URI to previous state
                includeLevel--;
                if (includeLevel == 0) {
                    request.removeAttribute(INCLUDE_LEVEL_KEY);
                } else {
                    request.setAttribute(INCLUDE_LEVEL_KEY, Integer.valueOf(includeLevel));
                }

            } else { // really absolute
                if (getCite()) {
                    cite(bodyContent, gotUrl, request);
                } else {
                    external(bodyContent, gotUrl, null, response); // null: no need to give cookies to external url
                                                                   // also no need to encode the URL.
                }
            }

        } catch (IOException e) {
            throw new TaglibException (e);
        }
        if (pageLog.isDebugEnabled()) {
            pageLog.debug("END Parsing mm:include JSP page");
        }
    }

    /**
     * With debug attribute you can write the urls in comments to the page, just before and after
     * the included page.
     */
    public void setDebug(String p) throws JspTagException {
        debugType = getAttribute(p);
    }

    protected Debug getDebug() throws JspTagException {

        if (debugType == Attribute.NULL) return Debug.NONE;
        String dtype = debugType.getString(this).toUpperCase();
        if (dtype.length() == 0) return Debug.NONE;
        return Debug.valueOf(dtype);
    }

    /**
     * Returns a name for this tag, that must appear in the debug message (in the comments)
     */
    protected String getThisName() {
        String clazz = this.getClass().getName();
        return clazz.substring(clazz.lastIndexOf(".") + 1);
    }


    private static class IncludeWrapper extends GenericResponseWrapper {
        int includeStatus = 200;
        public IncludeWrapper(HttpServletResponse resp) {
            super(resp);
        }
        public IncludeWrapper(HttpServletResponse resp, String encoding) {
            super(resp, encoding);
        }

        // don't wrap status to including request.
        public void setStatus(int status) {
            includeStatus = status;
        }
        public void sendError(int sc, String mes) {
            includeStatus = sc;
        }
        public void sendError(int sc) {
            includeStatus = sc;
        }
        public int getStatus() {
            return includeStatus;
        }

    }
}
