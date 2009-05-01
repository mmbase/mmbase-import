/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.*;
import java.util.*;
import java.io.*;


/**
 * @see LocalHttpServletRequest
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class LocalServletRequest implements ServletRequest {


    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private final Map<String, String> parameters = new HashMap<String, String>();
    protected String charEncoding = "UTF-8";
    protected final String request;

    protected final ServletContext sx;

    public LocalServletRequest(ServletContext s, String r) {
        sx = s;
        request = r;
    }


    public Object getAttribute(String name) {
        return attributes.get(name);

    }
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public String getCharacterEncoding() {
        return charEncoding;
    }
    public int  getContentLength() {
        return request.length();
    }
    public String getContentType() {
        return "text/html";
    }
    public ServletInputStream  getInputStream() {
        return new ServletInputStream() {
            private InputStream is;
            {
                try {
                    is = new ByteArrayInputStream(request.getBytes(charEncoding));
                } catch ( java.io.UnsupportedEncodingException uee) {
                    // should not happen
                }
            }
            public int read() throws IOException {
                return is.read();
            }
        };
    }
    public String getLocalAddr() {
        return "localhost";
    }
    public Locale getLocale() {
        return Locale.US;
    }
    public Enumeration getLocales() {
        return Collections.enumeration(Collections.singletonList(Locale.US));
    }
    public String getLocalName() {
        return "localhost";
    }
    public int getLocalPort() {
        return -1;
    }
    public String getParameter(String name) {
        return parameters.get(name);
    }
    public Map getParameterMap() {
        return parameters;
    }
    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }
    public String[] getParameterValues(String name) {
        return null;

    }
    public String getProtocol() {
        return "LOCAL/1";
    }
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(request));
    }

    @Deprecated public String getRealPath(String path) {
        return sx.getRealPath(path);
    }
    public String getRemoteAddr() {
        return "localhost";
    }
    public String  getRemoteHost() {
        return "localhost";
    }
    public int  getRemotePort() {
        return 0;
    }
    public RequestDispatcher  getRequestDispatcher(String path) {
        return sx.getRequestDispatcher(path);
    }
    public String  getScheme() {
        return "local";
    }
    public String  getServerName() {
        return  "localhost";
    }
    public int  getServerPort() {
        return 0;
    }
    public boolean  isSecure() {
        return false;
    }
    public void  removeAttribute(String name) {
        attributes.remove(name);
    }
    public void  setAttribute(String name, Object o) {
        attributes.put(name, o);
    }
    public void  setCharacterEncoding(String env) {
        charEncoding = env;
    }


}
