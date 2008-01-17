package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/** Class to wrap a ServletContext so it can be used as a PortletContext */
public class PortletContextWrapper implements PortletContext {

	private ServletContext context;

	public PortletContextWrapper(ServletContext context){
		this.context = context;
	}

	public java.lang.Object getAttribute(java.lang.String name) {
		return context.getAttribute(name);
	}

	public java.util.Enumeration getAttributeNames() {
		return context.getAttributeNames();
	}

	public java.lang.String getInitParameter(java.lang.String name) {
		return context.getInitParameter(name);
	}

	public java.util.Enumeration getInitParameterNames() {
		return context.getInitParameterNames();
	}

	public int getMajorVersion() {
		return context.getMajorVersion();
	}

	public java.lang.String getMimeType(java.lang.String file) {
		return context.getMimeType(file);
	}

	public int getMinorVersion() {
		return context.getMinorVersion();
	}

	public PortletRequestDispatcher getNamedDispatcher(java.lang.String name) {
		return new PortletRequestDispatcherWrapper(context.getNamedDispatcher(name));
	}

	public java.lang.String getPortletContextName() {
		System.out.println("PortletContextWrapper: cannot call getPortletContextName on a ServletContext!");
		return null;
	}

	public java.lang.String getRealPath(java.lang.String path) {
		return context.getRealPath(path);
	}

	public PortletRequestDispatcher getRequestDispatcher(java.lang.String path) {
		return new PortletRequestDispatcherWrapper(context.getRequestDispatcher(path));
	}

	public java.net.URL getResource(java.lang.String path) throws java.net.MalformedURLException {
		return context.getResource(path);
	}

	public java.io.InputStream getResourceAsStream(java.lang.String path) {
		return context.getResourceAsStream(path);
	}

	public java.util.Set getResourcePaths(java.lang.String path) {
		return context.getResourcePaths(path);
	}

	public java.lang.String getServerInfo() {
		return context.getServerInfo();
	}

	public void log(java.lang.String msg) {
		context.log(msg);
	}

	public void log(java.lang.String message, java.lang.Throwable throwable) {
		context.log(message, throwable);
	}

	public void removeAttribute(java.lang.String name) {
		context.removeAttribute(name);
	}

	public void setAttribute(java.lang.String name, java.lang.Object object) {
		context.setAttribute(name, object);
	}

}