package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;
import java.util.Enumeration;

/**
 * Class to wrap a PortletRequest so it can be used as a ServletRequest
 */
public class ServletRequestWrapper implements ServletRequest {
    protected PortletRequest request;

    public ServletRequestWrapper(PortletRequest request) {
        this.request = request;
    }

    public java.lang.Object getAttribute(java.lang.String name) {
        return request.getAttribute(name);
    }

    public java.util.Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }

    public java.lang.String getCharacterEncoding() {
        if (request instanceof ActionRequest) {
            return ((ActionRequest) request).getCharacterEncoding();
        }
        System.out.println("ServletRequestWrapper: cannot getCharacterEncoding on a PortletRequest!");
        return null;
    }

    public int getContentLength() {
        if (request instanceof ActionRequest) {
            return ((ActionRequest) request).getContentLength();
        }
        System.out.println("ServletRequestWrapper: cannot getContentLength on a PortletRequest!");
        return 0;
    }

    public java.lang.String getContentType() {
        if (request instanceof ActionRequest) {
            return ((ActionRequest) request).getContentType();
        }
        System.out.println("ServletRequestWrapper: cannot getContentType on a PortletRequest!");
        return null;
    }

    public ServletInputStream getInputStream() throws java.io.IOException {
        System.out.println("ServletRequestWrapper: cannot getInputStream on a PortletRequest!");
        return null;
    }

    public java.lang.String getLocalAddr() {
        System.out.println("ServletRequestWrapper: cannot getLocalAddr on a PortletRequest!");
        return null;
    }

    public java.util.Locale getLocale() {
        return request.getLocale();
    }

    public java.util.Enumeration getLocales() {
        return request.getLocales();
    }

    public java.lang.String getLocalName() {
        System.out.println("ServletRequestWrapper: cannot getLocalName on a PortletRequest!");
        return null;
    }

    public int getLocalPort() {
        System.out.println("ServletRequestWrapper: cannot getLocalPort on a PortletRequest!");
        return 80;
    }

    public java.lang.String getParameter(java.lang.String name) {
        return request.getParameter(name);
    }

    public java.util.Map getParameterMap() {
        return request.getParameterMap();
    }

    public java.util.Enumeration getParameterNames() {
        Enumeration e = request.getParameterNames();
        return e;
    }

    public java.lang.String[] getParameterValues(java.lang.String name) {
        return request.getParameterValues(name);
    }

    public java.lang.String getProtocol() {
        System.out.println("ServletRequestWrapper: cannot getProtocol on a PortletRequest!");
        return null;
    }

    public java.io.BufferedReader getReader() throws java.io.IOException {
        if (request instanceof ActionRequest) {
            return ((ActionRequest) request).getReader();
        }
        System.out.println("ServletRequestWrapper: cannot getReader on a PortletRequest!");
        return null;
    }

    public java.lang.String getRealPath(java.lang.String path) {
        System.out.println("ServletRequestWrapper: cannot getRealPath on a PortletRequest!");
        return null;
    }

    public java.lang.String getRemoteAddr() {
        System.out.println("ServletRequestWrapper: cannot getRemoteAddr on a PortletRequest!");
        return null;
    }

    public java.lang.String getRemoteHost() {
        System.out.println("ServletRequestWrapper: cannot getRemoteHost on a PortletRequest!");
        return null;
    }

    public int getRemotePort() {
        System.out.println("ServletRequestWrapper: cannot getRemotePort on a PortletRequest!");
        return 0;
    }

    public RequestDispatcher getRequestDispatcher(java.lang.String path) {
        System.out.println("ServletRequestWrapper: cannot getRequestDispatcher on a PortletRequest!");
        return null;
    }

    public java.lang.String getScheme() {
        return request.getScheme();
    }

    public java.lang.String getServerName() {
        return request.getServerName();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public void removeAttribute(java.lang.String name) {
        request.removeAttribute(name);
    }

    public void setAttribute(java.lang.String name, java.lang.Object o) {
        request.setAttribute(name, o);
    }

    public void setCharacterEncoding(java.lang.String env) throws java.io.UnsupportedEncodingException {
        if (request instanceof ActionRequest) {
            ((ActionRequest) request).setCharacterEncoding(env);
        }
        System.out.println("ServletRequestWrapper: cannot setCharacterEncoding on a PortletRequest!");
    }


}