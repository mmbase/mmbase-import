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
     * @param s The servlet context. Try {@link MMBaseContext#getServletContext()}
     * @param r The body of the request. Normally an empty string
     * @param path The ServletPath the do the request on
     */
    public LocalHttpServletRequest(ServletContext s, String r, String path) {
        super(s, r);
        this.path = path;
    }

    @Override
    public String  getAuthType() {
        return null;
    }
    @Override
    public String  getContextPath() {
        try {
            java.lang.reflect.Method m = sx.getClass().getMethod("getContextPath");
            return (String) m.invoke(sx);
        } catch (NoSuchMethodException nsme) {
            return "/";
        } catch (Exception e) {
            // Should not happen!
            return "/";
        }
    }
    @Override
    public Cookie[]  getCookies() {
        return null;
    }
    @Override
    public long  getDateHeader(String name) {
        return -1;
    }
    @Override
    public String  getHeader(String name) {
        return headers.get(name);
    }
    @Override
    public Enumeration  getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }
    @Override
    public Enumeration  getHeaders(String name) {
        return Collections.enumeration(headers.keySet());
    }
    @Override
    public int  getIntHeader(String name) {
        return -1;
    }
    @Override
    public String  getMethod() {
        return "GET";
    }
    @Override
    public String  getPathInfo() {
        return null;
    }
    @Override
    public String  getPathTranslated() {
        return null;
    }
    @Override
    public String  getQueryString() {
        return null;
    }
    @Override
    public String  getRemoteUser() {
        return null;
    }
    @Override
    public String  getRequestedSessionId() {
        return null;
    }
    @Override
    public String  getRequestURI() {
        return path;
    }
    @Override
    public StringBuffer  getRequestURL() {
        return new StringBuffer("local://localhost/" + path);
    }
    @Override
    public String  getServletPath() {
        return path;
    }
    @Override
    public HttpSession  getSession() {
        return getSession(true);
    }
    @Override
    public HttpSession  getSession(final boolean create) {
        if (httpSession == null && create) {

            // TODO remember session longer that duration of request.
            httpSession =
                new HttpSession() {
                    private final Map<String, Object> attributes = new HashMap<String, Object>();
                    private final long creationTime = System.currentTimeMillis();
                    private final String id = "" + (sessionId ++);
                    private int interval = 10000;
                    @Override
                    public Object  getAttribute(String name) {
                        return attributes.get(name);
                    }
                    @Override
                    public Enumeration  getAttributeNames() {
                        return Collections.enumeration(attributes.keySet());
                    }
                    @Override
                    public long  getCreationTime() {
                        return creationTime;
                    }
                    @Override
                    public String  getId() {
                        return id;
                    }
                    @Override
                    public long  getLastAccessedTime() {
                        return System.currentTimeMillis();
                    }
                    @Override
                    public int  getMaxInactiveInterval() {
                        return interval;
                    }
                    @Override
                    public ServletContext  getServletContext() {
                        return LocalHttpServletRequest.this.sx;
                    }
                    @Override
                    @Deprecated@SuppressWarnings({"deprecation"})
                    public HttpSessionContext  getSessionContext() {
                        return null;
                    }
                    @Override
                    @Deprecated
                    public Object  getValue(String name) {
                        return getAttribute(name);
                    }
                    @Override
                    @Deprecated
                    public String[]  getValueNames() {
                        return null;
                    }
                    @Override
                    public void  invalidate() {
                        attributes.clear();
                    }
                    @Override
                    public boolean  isNew() {
                        return true;
                    }
                    @Override
                    @Deprecated
                    public void  putValue(String name, Object value) {
                        setAttribute(name, value);
                    }
                    @Override
                    public void  removeAttribute(String name) {
                        attributes.remove(name);
                    }

                    @Override
                    @Deprecated
                    public void  removeValue(String name)  {
                        removeAttribute(name);
                    }
                    @Override
                    public void  setAttribute(String name, Object value) {
                        attributes.put(name, value);
                    }
                    @Override
                    public void  setMaxInactiveInterval(int interval) {
                        this.interval = interval;
                    }
                };
        }
        return httpSession;
    }
    @Override
    public java.security.Principal  getUserPrincipal() {
        return null;
    }
    @Override
    public boolean  isRequestedSessionIdFromCookie() {
        return false;
    }
    @Override
    @Deprecated
    public boolean  isRequestedSessionIdFromUrl() {
        return false;
    }
    @Override
    @Deprecated
    public boolean  isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean  isRequestedSessionIdValid() {
        return true;
    }
    @Override
    public boolean  isUserInRole(String role) {
        return false;
    }


}
