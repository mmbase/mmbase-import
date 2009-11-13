/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;


/**
 * This class (and {@link LocalHttpServletResponse}, and super classes) can be used to do requests on
 * the current MMBase without having an actual client, which can be useful in cronjobs or so.
 *
 * This is possible because the {@link javax.servlet.ServetContext} is known via {@link
 * org.mmbase.module.core.MMBaseContext#getServletContext()}.
 * For example
 <pre>
     ServletContext sx = MMBaseContext.getServletContext();
     HttpServletRequest req = new LocalHttpServletRequest(sx, "", "/test.jspx");
     StringBuilderWriter w = new StringBuilderWriter(new StringBuilder());
     HttpServletResponse res = new LocalHttpServletResponse(w);
     RequestDispatcher rd = sx.getRequestDispatcher(req.getServletPath());
     rd.include(req, res);
     res.flushBuffer();
     log.info("Got " + w.getBuffer());
 </pre>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class LocalHttpServletRequest extends LocalServletRequest implements HttpServletRequest {

    //private static final Map<String, HttpSession> session = new HashMap<String, HttpSession>(); TODO
    private static int sessionId = 0;

    private final Map<String, String> headers = new HashMap<String, String>(); // TODO headers cannot be presented in a map.

    private final String path;

    private HttpSession httpSession;

    /**
     * @param s The servlet context. Try {@link org.mmbase.module.core.MMBaseContext#getServletContext()}
     * @param r The body of the request. Normally an empty string
     * @param path The ServletPath the do the request on
     */
    public LocalHttpServletRequest(ServletContext s, String r, String path) {
        super(s, r);
        this.path = path;
    }

    public String  getAuthType() {
        return null;
    }
    public String  getContextPath() {
        try {
            java.lang.reflect.Method m = sx.getClass().getMethod("getContextPath");
            String cp = (String) m.invoke(sx);
            return cp;
        } catch (NoSuchMethodException nsme) {
            return "/";
        } catch (Exception e) {
            // Should not happen!
            return "/";
        }
    }
    public Cookie[]  getCookies() {
        return null;
    }
    public long  getDateHeader(String name) {
        return -1;
    }
    public String  getHeader(String name) {
        return headers.get(name);
    }
    public Enumeration  getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }
    public Enumeration  getHeaders(String name) {
        return Collections.enumeration(headers.keySet());
    }
    public int  getIntHeader(String name) {
        return -1;
    }
    public String  getMethod() {
        return "GET";
    }
    public String  getPathInfo() {
        return null;
    }
    public String  getPathTranslated() {
        return null;
    }
    public String  getQueryString() {
        return null;
    }
    public String  getRemoteUser() {
        return null;
    }
    public String  getRequestedSessionId() {
        return null;
    }
    public String  getRequestURI() {
        return path;
    }
    public StringBuffer  getRequestURL() {
        return new StringBuffer("local://localhost/" + path);
    }
    public String  getServletPath() {
        return path;
    }
    public HttpSession  getSession() {
        return getSession(true);
    }
    public HttpSession  getSession(final boolean create) {
        if (httpSession == null && create) {

            // TODO remember session longer that duration of request.
            httpSession =
                new HttpSession() {
                    private final Map<String, Object> attributes = new HashMap<String, Object>();
                    private final long creationTime = System.currentTimeMillis();
                    private final String id = "" + (sessionId ++);
                    private int interval = 10000;
                    public Object  getAttribute(String name) {
                        return attributes.get(name);
                    }
                    public Enumeration  getAttributeNames() {
                        return Collections.enumeration(attributes.keySet());
                    }
                    public long  getCreationTime() {
                        return creationTime;
                    }
                    public String  getId() {
                        return id;
                    }
                    public long  getLastAccessedTime() {
                        return System.currentTimeMillis();
                    }
                    public int  getMaxInactiveInterval() {
                        return interval;
                    }
                    public ServletContext  getServletContext() {
                        return LocalHttpServletRequest.this.sx;
                    }
                    @Deprecated@SuppressWarnings({"deprecation"})
                    public HttpSessionContext  getSessionContext() {
                        return null;
                    }
                    @Deprecated public Object  getValue(String name) {
                        return getAttribute(name);
                    }
                    @Deprecated public String[]  getValueNames() {
                        return null;
                    }
                    public void  invalidate() {
                        attributes.clear();
                    }
                    public boolean  isNew() {
                        return true;
                    }
                    @Deprecated public void  putValue(String name, Object value) {
                        setAttribute(name, value);
                    }
                    public void  removeAttribute(String name) {
                        attributes.remove(name);
                    }

                    @Deprecated public void  removeValue(String name)  {
                        removeAttribute(name);
                    }
                    public void  setAttribute(String name, Object value) {
                        attributes.put(name, value);
                    }
                    public void  setMaxInactiveInterval(int interval) {
                        this.interval = interval;
                    }
                };
        }
        return httpSession;
    }
    public java.security.Principal  getUserPrincipal() {
        return null;
    }
    public boolean  isRequestedSessionIdFromCookie() {
        return false;
    }
    @Deprecated public boolean  isRequestedSessionIdFromUrl() {
        return false;
    }
    @Deprecated public boolean  isRequestedSessionIdFromURL() {
        return false;
    }

    public boolean  isRequestedSessionIdValid() {
        return true;
    }
    public boolean  isUserInRole(String role) {
        return false;
    }


}
