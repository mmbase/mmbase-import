/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.NotFoundException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like UrlTag, but does not spit out an URL, but the page itself.
 *
 * @author Michiel Meeuwissen
 * @author Johannes Verelst
 * @version $Id: IncludeTag.java,v 1.48 2004-03-23 22:50:54 michiel Exp $
 */

public class IncludeTag extends UrlTag {

    private static  final Logger log = Logging.getLoggerInstance(IncludeTag.class);
    private static  final Logger pageLog = Logging.getLoggerInstance(Logging.PAGE_CATEGORY);

    private static final int DEBUG_NONE = 0;
    private static final int DEBUG_HTML = 1;
    private static final int DEBUG_CSS  = 2;

    private static  Set invalidIncludingAppServerRequestClasses = new HashSet();

    static {
    }

    protected Attribute debugType = Attribute.NULL;

    private Attribute cite = Attribute.NULL;

    /**
     * Test whether or not the 'cite' parameter is set
     */
    public void setCite(String c) throws JspTagException {
        cite = getAttribute(c);
    }

    protected boolean getCite() throws JspTagException {
        return cite.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException {
        if (page == null) { // for include tags, page attribute is obligatory.
            throw new JspTagException("Attribute 'page' was not specified");
        }
        return super.doStartTag();
    }

    protected void doAfterBodySetValue() throws JspTagException {
        includePage();
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

            if (request != null) {
                // Also propagate the cookies (like the jsession...)
                // Then these, and the session,  also can be used in the include-d page
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    StringBuffer koekjes = new StringBuffer();
                    for (int i=0; i < cookies.length; i++) {
                        if (log.isDebugEnabled()) {
                            log.debug("setting cookie " + i + ":" + cookies[i].getName() + "=" + cookies[i].getValue());
                        }
                        koekjes.append((i > 0 ? ";" : "")).append(cookies[i].getName()).append("=").append(cookies[i].getValue());
                    }
                    connection.setRequestProperty("Cookie", koekjes.toString());
                }
            }

            BufferedReader in;
            String coding = connection.getContentEncoding();

            // I don't understand really why this explicit treatment of the encoding is necessary,
            // but anyhow it didn't work without this in sun jdk 1.3.1/orion 1.5.3
            // it almost seems a hack like this.
            if (log.isDebugEnabled()) log.debug("found content encoding " + coding);
            if (coding == null) {
                // default, steal from current response.
                coding = response.getCharacterEncoding();
            }
            if (coding == null) { // stil null?
                in = new BufferedReader(new InputStreamReader (connection.getInputStream()));
            } else {
                try {
                    in = new BufferedReader(new InputStreamReader (connection.getInputStream(), coding));
                }  catch (UnsupportedEncodingException e) { // sometimes there are strange things there...
                    log.debug("Found a strange encoding in connection: " + coding);
                    in = new BufferedReader(new InputStreamReader (connection.getInputStream()));
                } catch (Exception e) {
                    throw new TaglibException(e.getMessage(), e);
                }
            }

            int buffersize = 10240;
            char[] buffer = new char[buffersize];
            StringBuffer string = new StringBuffer();
            int len;
            while ((len = in.read(buffer, 0, buffersize)) != -1) {
                string.append(buffer, 0, len);
            }
            helper.setValue(debugStart(absoluteUrl) + string.toString() + debugEnd(absoluteUrl));

            if (log.isDebugEnabled()) {
                log.debug("found string: " + helper.getValue());
            }

        } catch (IOException e) {
            throw new TaglibException (e);
        }
    }

    /**
     * Include a local file by doing a new HTTP request
     * Do not use this method, but use the 'internal()' method instead
     */
    private void externalRelative(BodyContent bodyContent, String relativeUrl, HttpServletRequest request, HttpServletResponse response) throws JspTagException {
        external(bodyContent,
                 request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + relativeUrl,
                 request, response);
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
         
        // Orion bug fix.
        req.getParameterMap();

        HttpServletRequestWrapper requestWrapper   = new HttpServletRequestWrapper(req);

        try {
            ServletContext sc = pageContext.getServletContext();
            if (sc == null) log.error("Cannot retrieve ServletContext from PageContext");
            RequestDispatcher requestDispatcher = sc.getRequestDispatcher(relativeUrl);
            if (requestDispatcher == null) {
                throw new NotFoundException("Page \"" + relativeUrl + "\" does not exist (No request-dispatcher could be created)");
            }

            GenericResponseWrapper responseWrapper = new GenericResponseWrapper(resp);

            requestDispatcher.include(requestWrapper, responseWrapper);                
            String page = responseWrapper.toString();
            helper.setValue(debugStart(relativeUrl) + page + debugEnd(relativeUrl));                


        } catch (Throwable e) {
            log.error(relativeUrl + " " +  Logging.stackTrace(e));
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
     */
    private void cite(BodyContent bodyContent, String relativeUrl, HttpServletRequest request) throws JspTagException {
        try {
            if (log.isDebugEnabled()) log.debug("Citing " + relativeUrl);
            if (relativeUrl.indexOf("..") > -1 || relativeUrl.toUpperCase().indexOf("WEB-INF") > -1)  { // toUpperCase: just for windows, of course
                throw new JspTagException("Not allowed to cite " + relativeUrl);
            }
            String urlFile = pageContext.getServletContext().getRealPath(relativeUrl.substring(request.getContextPath().length()));

            // take of the sessionid if it is present
            //HttpSession session = request.getSession(false);
            //if (session != null && session.isNew())
                { // means there is a ;jsession argument
                int j = urlFile.lastIndexOf(';');
                if (j != -1) {
                    urlFile = urlFile.substring(0, j);
                }

            }
            File file = new File(urlFile);

            if (file.isDirectory()) {
                throw new JspTagException("Cannot cite a directory");
            }

            if (! file.toURL().getProtocol().equals("file")) {
                throw new JspTagException("Cannot only cite local files");
            }


            if (log.isDebugEnabled()) log.debug("Citing " + file.toString());
            FileReader reader = new FileReader(file);
            StringWriter string = new StringWriter();
            int c = reader.read();
            while (c != -1) {
                string.write(c);
                c = reader.read();
            }
            helper.setValue(debugStart(urlFile) + string.toString() + debugEnd(urlFile));
        } catch (IOException e) {
            throw new TaglibException (e);
        }
    }

    /**
     * Includes another page in the current page.
     */
    protected void includePage() throws JspTagException {
        try {
            // Variables to keep track of which level we are at and what URI is current
            int includeLevel;
            String includeURI="";
            String previncludeURI="";

            String gotUrl = getUrl(false, false); // false, false: don't write &amp; tags but real & and don't urlEncode

            if (pageLog.isServiceEnabled()) {
                pageLog.service("Parsing mm:include JSP page: " + gotUrl);
            }
            // if not absolute, make it absolute:
            // (how does one check something like that?)
            String urlString;

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
                // This code, which passes the RequestURI through Attributes is necessary
                // Because Orion doesn't adapt the RequestURI when using the RequestDispatcher
                // This breaks relative includes of more than 1 level.

                // Fetch includelevel en reqeust URI from Attributes.
                Integer level = (Integer) request.getAttribute("includeTagLevel");
                if (level == null) {
                    includeLevel = 0;
                } else {
                    includeLevel = level.intValue();
                }
                includeURI = (String) request.getAttribute("includeTagURI");
                if (includeLevel == 0 || includeURI==null) {
                    includeURI = request.getRequestURI();
                    paramsIndex = includeURI.indexOf('?');
                    if (paramsIndex != -1) {
                        includeURI = includeURI.substring(0, paramsIndex);
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("Include: Level=" + includeLevel + " URI=" + includeURI);
                }

                if (nudeUrl.charAt(0) == '/') {
                    log.debug("URL was absolute on servletcontext");
                    urlString = gotUrl;
                } else {
                    log.debug("URL was relative");
                    File currentDir = new File(includeURI + "includetagpostfix"); // to make sure that it is not a directory (tomcat 5 does not redirect then)
                    
                    urlString =
                        // find the parent directory of the relativily given URL:
                        // parent-file: the directory in which the current file is.
                        // nude-Url   : is the relative path to this dir
                        // canonicalPath: to get rid of /../../ etc
                        // replace:  windows uses \ as path seperator..., but they may not be in URL's..
                        new File(currentDir.getParentFile(), nudeUrl).getCanonicalPath().toString().replace('\\', '/');

                    // getCanonicalPath gives also gives c: in windows:
                    // take it off again if necessary:
                    int colIndex = urlString.indexOf(':');
                    if (colIndex == -1) {
                        urlString += params;
                    } else {
                        urlString = urlString.substring(colIndex + 1) + params;
                    }
                }

                // Increase level and put it together with the new URI in the Attributes of the request
                includeLevel++;
                request.setAttribute("includeTagLevel", new Integer(includeLevel));
                // keep current URI so we can retrieve it after the include
                previncludeURI = includeURI;
                includeURI = urlString;
                paramsIndex = includeURI.indexOf('?');
                if (paramsIndex != -1) {
                    includeURI = includeURI.substring(0, paramsIndex);
                }
                request.setAttribute("includeTagURI", includeURI);
                if (log.isDebugEnabled()) {
                    log.debug("Next Include: Level=" + includeLevel + " URI=" + includeURI);
                }

                if (getCite()) {
                    cite(bodyContent, urlString, request);
                } else {
                    if (invalidIncludingAppServerRequestClasses.contains(request.getClass().getName())) {
                        externalRelative(bodyContent, response.encodeURL(urlString), request, response);
                    } else {
                        internal(bodyContent, urlString.substring(request.getContextPath().length()), request, response);
                    }
                }
                // Reset include level and URI to previous state
                includeLevel--;
                request.setAttribute("includeTagLevel",new Integer(includeLevel));
                request.setAttribute("includeTagURI",previncludeURI);
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

    protected int getDebug() throws JspTagException {

        if (debugType == Attribute.NULL) return DEBUG_NONE;

        String dtype = debugType.getString(this).toLowerCase();
        if (dtype.equals("none")) {
            return  DEBUG_NONE; // also implement the default, then people can use a variable
                               // to select this property in their jsp pages.
        } else if (dtype.equals("html")) {
            return DEBUG_HTML;
        } else if (dtype.equals("css")) {
            return DEBUG_CSS;
        } else {
            throw new JspTagException("Unknow value for debug attribute " + dtype);
        }
    }

    /**
     * Returns a name for this tag, that must appear in the debug message (in the comments)
     */
    protected String getThisName() {
        String clazz = this.getClass().getName();
        return clazz.substring(clazz.lastIndexOf(".") + 1);
    }

    /**
     * Write the comment that is just above the include page.
     */
    private String debugStart(String url) throws JspTagException {
        switch(getDebug()) {
        case DEBUG_NONE: return "";
        case DEBUG_HTML: return "\n<!-- " + getThisName() + " page = '" + url + "' -->\n";
        case DEBUG_CSS:  return "\n/* " + getThisName() +  " page  = '" + url + "' */\n";
        default: return "";
        }
    }

    /**
     * Write the comment that is just below the include page.
     */
    private String debugEnd(String url) throws JspTagException {
        switch(getDebug()) {
        case DEBUG_NONE: return "";
        case DEBUG_HTML: return "\n<!-- END " + getThisName() + " page = '" + url + "' -->\n";
        case DEBUG_CSS:  return "\n/* END " + getThisName() + " page = '" + url + "' */\n";
        default: return "";
        }
    }
}
