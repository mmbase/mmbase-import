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


    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);

    }
    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String getCharacterEncoding() {
        return charEncoding;
    }
    @Override
    public int  getContentLength() {
        return request.length();
    }
    @Override
    public String getContentType() {
        return "text/html";
    }
    @Override
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
            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
    }
    @Override
    public String getLocalAddr() {
        return "localhost";
    }
    @Override
    public Locale getLocale() {
        return Locale.US;
    }
    @Override
    public Enumeration getLocales() {
        return Collections.enumeration(Collections.singletonList(Locale.US));
    }
    @Override
    public String getLocalName() {
        return "localhost";
    }
    @Override
    public int getLocalPort() {
        return -1;
    }
    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }
    @Override
    public Map getParameterMap() {
        return parameters;
    }
    @Override
    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }
    @Override
    public String[] getParameterValues(String name) {
        return null;

    }
    @Override
    public String getProtocol() {
        return "LOCAL/1";
    }
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(request));
    }

    @Override
    @Deprecated public String getRealPath(String path) {
        return sx.getRealPath(path);
    }
    @Override
    public String getRemoteAddr() {
        return "localhost";
    }
    @Override
    public String  getRemoteHost() {
        return "localhost";
    }
    @Override
    public int  getRemotePort() {
        return 0;
    }
    @Override
    public RequestDispatcher  getRequestDispatcher(String path) {
        return sx.getRequestDispatcher(path);
    }
    @Override
    public String  getScheme() {
        return "local";
    }
    @Override
    public String  getServerName() {
        return  "localhost";
    }
    @Override
    public int  getServerPort() {
        return 0;
    }
    @Override
    public boolean  isSecure() {
        return false;
    }
    @Override
    public void  removeAttribute(String name) {
        attributes.remove(name);
    }
    @Override
    public void  setAttribute(String name, Object o) {
        attributes.put(name, o);
    }
    @Override
    public void  setCharacterEncoding(String env) {
        charEncoding = env;
    }


}
