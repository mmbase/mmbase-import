package portlet.wrappers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.portlet.*;

/** Class to wrap a ServletResponse so it can be used as a PortletResponse */
public class PortletResponseWrapper implements PortletResponse {

	protected ServletResponse response;

	public PortletResponseWrapper(ServletResponse response){
		this.response = response;
	}

	public void addProperty(java.lang.String key, java.lang.String value) {
		System.out.println("PortletResponseWrapper: cannot addProperty from a ServletResponse!");
	}

	public java.lang.String encodeURL(java.lang.String path) {
		if (response instanceof HttpServletResponse) {
			return ((HttpServletResponse)response).encodeURL(path);
		}
		System.out.println("PortletResponseWrapper: cannot encodeURL from a ServletResponse!");
		return null;
	}

	public void setProperty(java.lang.String key, java.lang.String value) {
		System.out.println("PortletResponseWrapper: cannot setProperty from a ServletResponse!");
	}

}