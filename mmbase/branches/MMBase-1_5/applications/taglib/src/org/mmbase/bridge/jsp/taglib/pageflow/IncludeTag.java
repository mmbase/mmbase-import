/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like UrlTag, but does not spit out an URL, but the page itself.
 * 
 * @author Michiel Meeuwissen
 */
public class IncludeTag extends UrlTag {

    private static Logger log = Logging.getLoggerInstance(IncludeTag.class.getName()); 

    private static final int DEBUG_NONE = 0;
    private static final int DEBUG_HTML = 1;
    private static final int DEBUG_CSS  = 2;
    
    protected int debugtype = DEBUG_NONE;
    

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
        if (log.isDebugEnabled()) log.debug("External: found url: >" + absoluteUrl + "<");
        debugStart(absoluteUrl);     
        try {
            URL includeURL = new URL(absoluteUrl); 

            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
            
            if (request != null) {
                // Also propagate the cookies (like the jsession...)
                // Then these, and the session,  also can be used in the include-d page
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    String koekjes = "";
                    for (int i=0; i < cookies.length; i++) {
                        if (log.isDebugEnabled()) {
                            log.debug("setting cookie " + i + ":" + cookies[i].getName() + "=" + cookies[i].getValue());
                        }
                        koekjes += (i > 0 ? ";" : "") + cookies[i].getName() + "=" + cookies[i].getValue();
                    }
                    connection.setRequestProperty("Cookie", koekjes);
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
                in = new BufferedReader(new InputStreamReader (connection.getInputStream(), coding));
            }

            int buffersize = 10240;
            char[] buffer = new char[buffersize];
            StringBuffer string = new StringBuffer();
            int len;
            while ((len = in.read(buffer, 0, buffersize)) != -1) {
                string.append(buffer, 0, len);
            }            
            helper.setValue(string.toString());
            
            if (log.isDebugEnabled()) log.debug("found string: " + bodyContent.getString());
            debugEnd(absoluteUrl);

        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }         
    }

    /**
     *
     */
    private void externalRelative(BodyContent bodyContent, String relativeUrl, HttpServletRequest request, HttpServletResponse response) throws JspTagException {
        external(bodyContent,
                 request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + relativeUrl, 
                 request, response);
    }

    /**
     * When staying on the same server (relative URL's) in principle we could do something smarter.
     * For the moment we don't use it, because e.g. I can't get codings working.

     */
    private void internal(BodyContent bodyContent, String relativeUrl, HttpServletRequest request, HttpServletResponse resp) throws JspTagException {
        try {
            if (log.isDebugEnabled()) log.debug("Internal: found url: >" + relativeUrl + "<");
            debugStart(relativeUrl);
            bodyContent.getEnclosingWriter().flush();
            ResponseWrapper response = new ResponseWrapper(resp);
            try {
                javax.servlet.ServletContext sc = pageContext.getServletContext();
                if (sc == null) log.error("sc is null");                                
                javax.servlet.RequestDispatcher rd = sc.getRequestDispatcher(relativeUrl);
                if (rd == null) log.error("rd is null");
                rd.include(request, response);
                helper.setValue(response.toString());
            } catch (Exception e) {
                log.debug(Logging.stackTrace(e));
                throw new JspTagException(e.toString());
            }
            debugEnd(relativeUrl);
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        } 
    }
    
    /**
     * Includes another page in the current page.
     */

    protected void includePage() throws JspTagException {
        try {
            String gotUrl = getUrl(false);// false: don't write &amp; tags but real &.
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
	    if (log.isDebugEnabled()) log.debug("Found nude url " + nudeUrl);
            javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
            javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse)pageContext.getResponse();
               
            if (nudeUrl.indexOf(':') == -1) { // relative
                if (nudeUrl.charAt(0) == '/') {
                    log.debug("URL was absolute on servletcontext");
                    urlString = gotUrl;
                } else {
                    log.debug("URL was relative");
                    urlString =
                        // find the parent directory of the relativily given URL:
                        // parent-file: the directory in which the current file is.
                        // nude-Url   : is the relative path to this dir
                        // canonicalPath: to get rid of /../../ etc
                        // replace:  windows uses \ as path seperator..., but they may not be in URL's.. 
                        new java.io.File(new java.io.File(request.getRequestURI()).getParentFile(), nudeUrl).getCanonicalPath().toString().replace('\\', '/');
                    
                    // getCanonicalPath gives also gives c: in windows:
                    // take it off again if necessary:
                    int colIndex = urlString.indexOf(':');
                    if (colIndex == -1) {
                        urlString += params;
                    } else {
                        urlString = urlString.substring(colIndex + 1) + params;
                    }
                }
                externalRelative(bodyContent, urlString, request, response);
                //internal(bodyContent, urlString, request, response);
            } else { // really absolute
                external(bodyContent, gotUrl, null, response); // null: no need to give cookies to external url
            }
          
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        } 
    }

    /**
     * With debug attribute you can write the urls in comments to the page.
     */
    
    public void setDebug(String p) throws JspTagException {
        String dtype = getAttributeValue(p); 
        if (dtype.toLowerCase().equals("none")) { 
            debugtype = DEBUG_NONE; // also implement the default, then people can use a variable
                                    // to select this property in their jsp pages.
        } else if (dtype.toLowerCase().equals("html")) {
            debugtype = DEBUG_HTML;
        } else if (dtype.toLowerCase().equals("css")) {
            debugtype = DEBUG_CSS;
        } else {
            throw new JspTagException("Unknow value for debug attribute " + dtype + " (" + p + ")");
        }
    }

    /** 
     * Returns a name for this tag, that must appear in the debug message (in the comments)
     */

    protected String getThisName() {
        String clazz = this.getClass().getName();
        return clazz.substring(clazz.lastIndexOf(".") + 1);
    }
    
  
    private void debugStart(String url) {
        try {
            switch(debugtype) {
            case DEBUG_NONE: return; // do this one first, to avoid losing time.
            case DEBUG_HTML: 
                bodyContent.write("\n<!-- " + getThisName() + " page = '" + url + "' -->\n"); break;
            case DEBUG_CSS:
                bodyContent.write("\n/* " + getThisName() +  " page  = '" + url + "' */\n"); break;
            }
        }  catch (java.io.IOException e) {
            log.error(e.toString());
        }
    }
     
    private void debugEnd(String url) {
        try {
            switch(debugtype) {
            case DEBUG_NONE: return;
            case DEBUG_HTML: 
                bodyContent.write("\n<!-- END " + getThisName() + " page = '" + url + "' -->\n"); break;
            case DEBUG_CSS:
                bodyContent.write("\n/* END " + getThisName() + " page = '" + url + "' */\n"); break;
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
    }

}

/**
 * These classes are used by the 'internal' function. It is still
 * experiment, though it seems to work.
 *
 * Should perhaps be moved to an util dir. 
 *
 */

class Bytes extends java.io.ByteArrayOutputStream {
    private static Logger log = Logging.getLoggerInstance(IncludeTag.class.getName()); 
    Bytes() {
        super();
        log.debug("making bytes");
    }
    
    public void write(byte[] i) throws java.io.IOException {
        log.debug("writebyte[] ");
        super.write(i);
    }
    public void write(int i)  {
        log.debug("write ");
        super.write(i);
    }
    public void write(byte[] i, int of, int len) {
        log.debug("writebyte[] 2");
        super.write(i, of, len);
    }    
    public void writeTo(java.io.OutputStream o) throws java.io.IOException {
        log.debug("outputstream");
        super.writeTo(o);
    }    

    
}

class StreamWrapper extends ServletOutputStream {
    private static Logger log = Logging.getLoggerInstance(IncludeTag.class.getName()); 
    
    private Bytes buffer;
    private String coding;
    StreamWrapper(HttpServletResponse resp) {
        log.debug("making streamwrapper");
        coding = resp.getCharacterEncoding();
        this.buffer = new Bytes();
    }
    
    public void write(int i) {
        log.debug("x");
        buffer.write(i);
    }
    public void write(byte[] i) throws java.io.IOException  {

        log.debug("x");
        buffer.write(i);
        
    }
    public void write(byte[] i, int of, int len) {
        log.debug("x");
        buffer.write(i, of, len);
    }    

    public String toString() {
        try {
            return new String(buffer.toByteArray(), coding);
        } catch (Exception e) {
            return e.toString();
        }
    }
    java.io.PrintWriter getWriter() {
        return new java.io.PrintWriter(buffer);
    }
    
 }

/**
 * Wrapper around the response. It collects the part which is written to it, so you can get it with 'toString'.
 */

class ResponseWrapper extends HttpServletResponseWrapper {
    private static Logger log = Logging.getLoggerInstance(IncludeTag.class.getName());

    private StreamWrapper stream;
    public ResponseWrapper(HttpServletResponse resp) {
        super(resp);
        stream = new StreamWrapper(resp);
        log.debug("getting " + resp.getCharacterEncoding());
    }
    
    public ServletOutputStream getOutputStream() {
        log.debug("getting outputstream");
        return stream;
    }

    public void setContentType(String s) {
        log.debug("settting contenttype to" + s);
    }
    
   
    public String toString() {
        log.debug("getting string");
        String test = stream.toString();
        return test;
    }
    
    public java.io.PrintWriter getWriter() {
        log.debug("Getting the writer");
        return stream.getWriter();
    }

}


