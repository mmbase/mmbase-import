/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class HttpUtil {

    private static final String IDENT = "   ";
    private static final String ENDL = "\n";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(HttpUtil.class.getName());
    
    private HttpUtil() {
        //Utility
    }

    /**
     * The code first sets the Expires header to a date in the
     * past. This indicates to the recipient that the page's content
     * have already expired, as a hint that it's contents should not be
     * cached. The no-cache value for the Pragma header is provided by
     * version 1.0 of the HTTP protocol to further indicate that
     * browsers and proxy servers should not cache a page. Version 1.1
     * of HTTP replaces this header with a more specific Cache-Control
     * header, but recommends including the Pragma header as well for
     * backward compatibility.
     * 
     * @param response - http response
     * @param expire - millisecond before content should expire 
     */
    public static void addNoCacheHeaders(HttpServletResponse response, int expire) {
         if (expire <= 0) {
             // Add some header to make sure these pages are not cached anywhere.
             // Set standard HTTP/1.1 no-cache headers.
             response.setHeader("Cache-Control","no-cache, no-store, must-revalidate, proxy-revalidate");
             // Set IE extended HTTP/1.1 no-cache headers
             response.addHeader("Cache-Control", "post-check=0, pre-check=0");
             // Set standard HTTP/1.0 no-cache header.
             response.setHeader("Pragma", "no-cache");
             response.setDateHeader ("Expires", -1);
             
             // long now = System.currentTimeMillis();                        
             // according to  rfc2616 sec14 'already expires' means that date-header is expires header
             // sadly, this does not work:
             // perhaps because tomcat overrides the date header later, so a difference of a second can occur
             // response.setDateHeader("Date",     now);                         
         }
         else {
             // calc the string in GMT not localtime and add the offset
             response.setDateHeader ("Expires", System.currentTimeMillis() + expire);
             response.setHeader("Cache-Control", "public");
         }
    }
    
    /** get WebappUri.
     * 
     * @param request HttpServletRequest
     * @return WebappUri
     */
    public static String getWebappUri(HttpServletRequest request) {
        return getWebappUri(request, request.getServerName(), false);
    }

    /** get WebappUri.
     * 
     * @param request HttpServletRequest
     * @param serverName replacement servername
     * @param forceSecure force url to a secure one 
     * @return WebappUri
     */
    public static String getWebappUri(HttpServletRequest request, boolean forceSecure) {
        return getWebappUri(request, request.getServerName(), forceSecure);
    }

    /** get WebappUri.
     * 
     * @param request HttpServletRequest
     * @param serverName replacement servername
     * @param forceSecure force url to a secure one 
     * @return WebappUri
     */
    public static String getWebappUri(HttpServletRequest request, String serverName, boolean forceSecure) {
        String scheme = forceSecure ? "https" : request.getScheme();
        StringBuffer s = new StringBuffer();
        s.append(scheme).append("://").append(serverName);
        
        int serverPort = request.getServerPort();
        if (serverPort != 80 && serverPort != 443 ) {
            s.append(':').append(Integer.toString(serverPort));
        }

        s.append(request.getContextPath()).append('/');
        return s.toString();
    }

    /** get Secure WebappUri. 
     * 
     * @param request HttpServletRequest
     * @return String
     */
    public static String getSecureWebappUri(HttpServletRequest request) {
       StringBuffer s = new StringBuffer();
       s.append("https://")
       .append(request.getServerName())
       .append(request.getContextPath())
       .append('/');
       return s.toString();
    }

    /**  get Server Document Root
     *
     * @param request HttpServletRequest
     * @return String
     */
    public static String getServerDocRoot(HttpServletRequest request) {
       StringBuffer s = new StringBuffer();
        s.append(request.getScheme()).append("://").append(request.getServerName());
        
        int serverPort = request.getServerPort();
        if (serverPort != 80 && serverPort != 443 ) {
            s.append(':').append(Integer.toString(serverPort));
        }
        s.append('/');
       return s.toString();
    }

    /** get Secure Server Document Root
     *
     * @param request HttpServletRequest
     * @return String
     */
    public static String getSecureServerDocRoot(HttpServletRequest request) {
       StringBuffer s = new StringBuffer();
       s.append("https://")
       .append(request.getServerName())
       .append('/');
       return s.toString();
    }
    
    /**
     * Determines the relative path to the root (e.g. ../../..) for the given request. Does not end 
     * with '/'!
     * 
     * @param request 
     * @param uri the uri to use or request.getRequestURL() if null
     * @return relative path
     */
    public static String determinePathToRoot(HttpServletRequest request, String uri) {
       if (StringUtils.isEmpty(uri)) {
          uri = request.getRequestURL().toString();
       }
       StringBuffer ret = new StringBuffer();
       String appName = request.getContextPath();
       String stepUrl = uri.substring(uri.indexOf(appName) + appName.length() + 1);
       
       int subdirs = stepUrl.split("/").length;
       for (int i = 1; i < subdirs; i++) {
           ret.append("../");
       }
       return StringUtils.isEmpty(ret.toString())?".":ret.toString();
    }

    public static ServletOutputStream getOutputStreamForXml(HttpServletResponse response) throws IOException {
        return getOutputStreamForXml(response, "UTF-8");
    }

    public static ServletOutputStream getOutputStreamForXml(HttpServletResponse response, String encoding) throws IOException {
        setResponseForXml(response, encoding);
        ServletOutputStream out = response.getOutputStream();
        return out;
    }
    
    public static PrintWriter getWriterForXml(HttpServletResponse response) throws IOException {
        return getWriterForXml(response, "UTF-8");
    }

    public static PrintWriter getWriterForXml(HttpServletResponse response, String encoding) throws IOException {
        setResponseForXml(response, encoding);
        PrintWriter out = response.getWriter();
        return out;
    }

    public static void setResponseForXml(HttpServletResponse response, String encoding) {
        response.setCharacterEncoding(encoding);
        response.setContentType("text/xml");
    }

    /**
     * Add session information to url
     * @param request add session information from this request
     * @param url url which should get the session information
     * @return url with session
     */
    public static String addSessionId(HttpServletRequest request, String url) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            url += ";jsessionid=" + session.getId();
        }
        return url;
    }

    
    /**
     * Sends out the xml to the HttpServletResponse.
     *
     * @param xml      The xml to send.
     * @param response The HttpServletResponse to write to.
     */
    public static void sendXml(String xml, HttpServletResponse response) {
        if (log.isDebugEnabled()) {
          log.debug("******************* RESULT");
          log.debug(xml);
          log.debug("*******************");
       }
       ServletOutputStream out = null;
       try {
          out = HttpUtil.getOutputStreamForXml(response);
          response.setStatus(HttpServletResponse.SC_OK);
          out.println(xml);
          out.flush();
       }
       catch (IOException e) {
          log.error("IOException while sending xml to HttpServletResponse: " + e.getMessage());
       }
       finally {
          try {
             if (out != null) {
                out.close();
             }
          }
          catch (IOException e1) {
             log.error("IOException while closing outputStream: " + e1.getMessage());
          }
       }
    }

    
    /**
     * get Remote Content
     *  
     * @param urlPath - path to resource
     * @return content of resource
     */
    public static String getURLContent(String urlPath) {
       try {
          return getURLContent(new URL(urlPath));
       }
       catch (MalformedURLException e) {
          return "";
       }
    }

    /**
     * get Remote Content
     *  
     * @param url - url to resource
     * @return content of resource
     */
    public static String getURLContent(URL url) {
       Writer sb = new StringWriter();
       getURLContent(url, sb);
       return sb.toString();
    }
    
    /**
     * get Remote Content
     *  
     * @param urlPath - url to resource
     * @param out - writer for the content
     */
    public static void getURLContent(String urlPath, Writer out) {
       try {
          getURLContent(new URL(urlPath), out);
       }
       catch (MalformedURLException e) {
          log.warn("MalformedURL: " + e.getMessage());
       }
    }
    
    /**
     * get Remote Content
     *  
     * @param url - url to resource
     * @param out - writer for the content
     */
    public static void getURLContent(URL url, Writer out) {
       try {
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setConnectTimeout(5000); // five second connect timeout
          conn.setReadTimeout(5000); // five second read timeout 
          int responseCode = conn.getResponseCode();
          if (responseCode == HttpURLConnection.HTTP_OK) {
             getStreamContent(conn.getInputStream(), out);
          }
       }
       catch (IOException e) {
          log.error("Exception " + e.toString(), e);
       }
    }

    /**
     * get Remote Content
     *  
     * @param urlStream - stream of url
     * @param out - writer for the content
     */
   private static void getStreamContent(InputStream urlStream, Writer out) {
       // find the newline character(s) on the current system
       String newline = null;
       try {
          newline = System.getProperty("line.separator");
       }
       catch (Exception e) {
          newline = "\n";
       }
       BufferedReader in = null;
       try {
          InputStreamReader input = new InputStreamReader(urlStream);
          in = new BufferedReader(input);
          String line;
          while ((line = in.readLine()) != null) {
             out.append(line);
             out.append(newline);
          }
       }
       catch (IOException e) {
          log.error("Exception " + e.toString(), e);
       }
       finally {
          try {
             in.close();
          } catch (IOException e) {
              log.debug("Exception " + e.toString(), e);
          }
       }
   }
    
    /**
     * Post a string to an URL and get the reply as a string. 
     * Returns an empty string if things didn't work out.
     *
     * @param url - url to resource
     * @param body - content to post
     * @return copntent of resource
     */
    public static String getURLContentPost(URL url, String body) {
       try {
          // URL must use the http protocol!
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("POST");
          conn.setAllowUserInteraction(false); // you may not ask the user
          conn.setDoOutput(true); // we want to send things
          conn.setDoInput(true); // we want to receive things
          // the Content-type should be default, but we set it anyway
          conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
          // the content-length should not be necessary, but we're cautious
          conn.setRequestProperty("Content-length", Integer.toString(body.length()));
          // No caching
          conn.setUseCaches(false);
          
          conn.setConnectTimeout(5000); // five second connect timeout
          conn.setReadTimeout(5000); // five second read timeout 

          
          // Post data to url
          OutputStreamWriter printout = null;
          try {
              printout = new OutputStreamWriter(conn.getOutputStream());
              printout.write(body);
              printout.flush();
          }
          catch (IOException e) {
              log.debug("" + e.getMessage(), e);
          }
          finally {
              if (printout != null) {
                  printout.close();
              }
          }
          
          StringWriter out = new StringWriter();
          int responseCode = conn.getResponseCode();
          if (responseCode == HttpURLConnection.HTTP_OK) {
             // get the input stream for reading the reply
             // IMPORTANT! Your body will not get transmitted if you get the
             // InputStream before completely writing out your output first!
             InputStream rawInStream = conn.getInputStream();
             getStreamContent(rawInStream, out);
          }
          return out.toString();
       }
       catch (Exception e) {
          log.error("Exception " + e.toString(), e);
          return "";
       }
    }

    public static String getErrorInfo(HttpServletRequest request, Throwable exception, long ticket, String version) {
        // prepare error details
        String msg = "";

        msg += "TIME: " + DATE_TIME_FORMAT.format(new Date(ticket)) + ENDL;
        msg += "VERSION: " + version + ENDL;
        msg += ENDL;
        msg += getAllRequestInfo(request);

        if (exception != null) {
            msg += ENDL;
            msg += getExceptionInfo(exception);
        }
        return msg;
    }

    public static String getExceptionInfo(Throwable exception) {
        // add stack stacktrace
        StringWriter wr = new StringWriter();
        PrintWriter pw = new PrintWriter(wr);

        pw.println("EXCEPTION:");
        exception.printStackTrace(new PrintWriter(wr));

        Throwable rootEx = exception;
        while (true) {
            if (rootEx instanceof ServletException) {
                rootEx = ((ServletException) rootEx).getRootCause();
            }
            else {
                rootEx = rootEx.getCause();
            }
            if (rootEx != null) {
                pw.println("\nCause:");
                rootEx.printStackTrace(new PrintWriter(wr));
            }
            else {
                break;
            }
        }
        String errString = wr.toString();
        return errString;
    }

    public static String getAllRequestInfo(HttpServletRequest request) {
        String msg = getRequestInfo(request);
        msg += getCookieInfo(request);
        msg += getSessionInfo(request);
        return msg;
    }
    
    public static String getRequestInfo(HttpServletRequest request) {
        String msg = "REQUEST:" + ENDL;

        // request properties
        msg += IDENT + "requesturl: " + request.getRequestURL() + ENDL;
        msg += IDENT + "querystring: " + request.getQueryString() + ENDL;
        msg += IDENT + "method: " + request.getMethod() + ENDL;
        msg += IDENT + "client: " + request.getRemoteAddr()  + ENDL;
        msg += IDENT + "user: " + request.getRemoteUser()  + ENDL;

        // request headers
        msg += IDENT + "headers: " + ENDL;
        Enumeration<String> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            msg += IDENT + IDENT + name + ": " + request.getHeader(name) + ENDL;
        }

        // request parameters
        msg += IDENT + "parameters: " + ENDL;
        en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            msg += IDENT + IDENT + name+": " + request.getParameter(name) + ENDL;
        }

        // request attributes
        msg += IDENT + "attributes: " + ENDL;
        en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            if (!name.startsWith("javax.servlet")) {
                msg += IDENT + IDENT + name+": " + request.getAttribute(name) + ENDL;
            }
        }
        return msg;
    }
    
    public static String getCookieInfo(HttpServletRequest request) {
        String msg = "";
        // request cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            msg += IDENT + "COOKIES: " + ENDL;
            for (Cookie cookie : cookies) {
                msg += IDENT + IDENT + "name: " + cookie.getName();
                msg += IDENT + "domain: " + cookie.getDomain();
                msg += IDENT + "path: " + cookie.getPath();
                msg += IDENT + "version: " + cookie.getVersion();
                msg += IDENT + "maxage: " + cookie.getMaxAge();
                msg += IDENT + "comment: " + cookie.getComment();
                msg += IDENT + "value: " + cookie.getValue() + ENDL;
            }
        }
        return msg;
    }

    public static String getSessionInfo(HttpServletRequest request) {
        String msg = "";
        HttpSession session = request.getSession(false);
        if (session != null && !session.isNew()) {
            msg += ENDL;
            msg += "SESSION:" + ENDL;

            // session properties
            msg += IDENT + "id: " + session.getId() + ENDL;
            msg += IDENT + "CreationTime: " + DATE_TIME_FORMAT.format(new Date(  session.getCreationTime() )) + ENDL;
            msg += IDENT + "LastAccessedTime: " + DATE_TIME_FORMAT.format(new Date( session.getLastAccessedTime() )) + ENDL;
            msg += IDENT + "MaxInactiveInterval: " + session.getMaxInactiveInterval() + ENDL;

            // session attributes
            msg += IDENT + "attributes: " + ENDL;
            Enumeration<String> en = session.getAttributeNames();
            while (en.hasMoreElements()) {
                String name = en.nextElement();
                msg += IDENT + IDENT + name+": " + session.getAttribute(name) + ENDL;
            }
        }
        return msg;
    }
    
}
