package portlet.wrappers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.portlet.*;

/** Class to wrap a ServletRequest so it can be used as a PortletRequest */
public class PortletRequestWrapper implements PortletRequest {
	protected ServletRequest request;

	public static java.lang.String BASIC_AUTH = PortletRequest.BASIC_AUTH;
	public static java.lang.String CLIENT_CERT_AUTH = PortletRequest.CLIENT_CERT_AUTH;
	public static java.lang.String DIGEST_AUTH = PortletRequest.DIGEST_AUTH;
	public static java.lang.String FORM_AUTH = PortletRequest.FORM_AUTH;
	public static java.lang.String USER_INFO  = PortletRequest.USER_INFO;

	public PortletRequestWrapper(ServletRequest request){
		this.request = request;
	}

	public java.lang.Object getAttribute(java.lang.String name) {
		return request.getAttribute(name);
	}

	public java.util.Enumeration getAttributeNames() {
		return request.getAttributeNames();
	}

	public java.lang.String getAuthType() {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).getAuthType();
		}
		System.out.println("PortletRequestWrapper: cannot getAuthType from a ServletRequest!");
		return null;
	}

	public java.lang.String getContextPath() {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).getContextPath();
		}
		System.out.println("PortletRequestWrapper: cannot getContextPath from a ServletRequest!");
		return null;
	}

	public java.util.Locale getLocale() {
		return request.getLocale();
	}

	public java.util.Enumeration getLocales() {
		return request.getLocales();
	}

	public java.lang.String getParameter(java.lang.String name) {
		return request.getParameter(name);
	}

	public java.util.Map getParameterMap() {
		return request.getParameterMap();
	}

	public java.util.Enumeration getParameterNames() {
		return request.getParameterNames();
	}

	public java.lang.String[] getParameterValues(java.lang.String name) {
		return request.getParameterValues(name);
	}

	public PortalContext getPortalContext() {
		System.out.println("PortletRequestWrapper: cannot getPortalContext from a ServletRequest!");
		return null;
	}

	public PortletMode getPortletMode() {
		System.out.println("PortletRequestWrapper: cannot getPortletMode from a ServletRequest!");
		return null;
	}

	public PortletSession getPortletSession() {
		if (request instanceof HttpServletRequest) {
			HttpSession session = ((HttpServletRequest)request).getSession();
			if (session==null) return null;
			return new PortletSessionWrapper(session);
		}
		System.out.println("PortletRequestWrapper: cannot getPortletSession from a ServletRequest!");
		return null;
	}

	public PortletSession getPortletSession(boolean create) {
		if (request instanceof HttpServletRequest) {
			HttpSession session = ((HttpServletRequest)request).getSession(create);
			if (session==null) return null;
			return new PortletSessionWrapper(session);
		}
		System.out.println("PortletRequestWrapper: cannot getPortletSession from a ServletRequest!");
		return null;
	}

	public PortletPreferences getPreferences() {
		System.out.println("PortletRequestWrapper: cannot getPreferences from a ServletRequest!");
		return null;
	}

	public java.util.Enumeration getProperties(java.lang.String name) {
		System.out.println("PortletRequestWrapper: cannot getProperties from a ServletRequest!");
		return null;
	}

	public java.lang.String getProperty(java.lang.String name) {
		System.out.println("PortletRequestWrapper: cannot getProperty from a ServletRequest!");
		return null;
	}

	public java.util.Enumeration getPropertyNames() {
		System.out.println("PortletRequestWrapper: cannot getPropertyNames from a ServletRequest!");
		return null;
	}

	public java.lang.String getRemoteUser() {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).getRemoteUser();
		}
		System.out.println("PortletRequestWrapper: cannot getRemoteUser from a ServletRequest!");
		return null;
	}

	public java.lang.String getRequestedSessionId() {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).getRequestedSessionId();
		}
		System.out.println("PortletRequestWrapper: cannot getRequestedSessionId from a ServletRequest!");
		return null;
	}

	public java.lang.String getResponseContentType() {
		System.out.println("PortletRequestWrapper: cannot getResponseContentType from a ServletRequest!");
		return null;
	}

	public java.util.Enumeration getResponseContentTypes() {
		System.out.println("PortletRequestWrapper: cannot getResponseContentTypes from a ServletRequest!");
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

	public java.security.Principal getUserPrincipal() {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).getUserPrincipal();
		}
		System.out.println("PortletRequestWrapper: cannot getUserPrincipal from a ServletRequest!");
		return null;
	}

	public WindowState getWindowState() {
		System.out.println("PortletRequestWrapper: cannot getWindowState from a ServletRequest!");
		return null;
	}

	public boolean isPortletModeAllowed(PortletMode mode) {
		System.out.println("PortletRequestWrapper: cannot isPortletModeAllowed from a ServletRequest!");
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).isRequestedSessionIdValid();
		}
		System.out.println("PortletRequestWrapper: cannot isRequestedSessionIdValid from a ServletRequest!");
		return false;
	}

	public boolean isSecure() {
		return request.isSecure();
	}

	public boolean isUserInRole(java.lang.String role) {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest)request).isUserInRole(role);
		}
		System.out.println("PortletRequestWrapper: cannot isUserInRole from a ServletRequest!");
		return false;
	}

	public boolean isWindowStateAllowed(WindowState state) {
		System.out.println("PortletRequestWrapper: cannot isWindowStateAllowed from a ServletRequest!");
		return false;
	}

	public void removeAttribute(java.lang.String name) {
		request.removeAttribute(name);
	}

	public void setAttribute(java.lang.String name, java.lang.Object o) {
		request.setAttribute(name, o);
	}

}