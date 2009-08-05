package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/**
 * Class to wrap a PortletContext so it can be used as a ServletContext
 */
public class ServletContextWrapper implements ServletContext {

   private PortletContext context;

   public ServletContextWrapper(PortletContext context) {
      this.context = context;
   }

   public java.lang.Object getAttribute(java.lang.String name) {
      return context.getAttribute(name);
   }

   public java.util.Enumeration getAttributeNames() {
      return context.getAttributeNames();
   }

   public String getContextPath() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public ServletContext getContext(java.lang.String uripath) {
      System.out.println("ServletContextWrapper: cannot call getContext on a PortletContext!");
      return null;
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

   public RequestDispatcher getNamedDispatcher(java.lang.String name) {
      return new RequestDispatcherWrapper(context.getNamedDispatcher(name));
   }

   public java.lang.String getRealPath(java.lang.String path) {
      return context.getRealPath(path);
   }

   public RequestDispatcher getRequestDispatcher(java.lang.String path) {
      return new RequestDispatcherWrapper(context.getRequestDispatcher(path));
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

   public Servlet getServlet(java.lang.String name) throws ServletException {
      System.out.println("ServletContextWrapper: cannot call getServlet on a PortletContext!");
      return null;
   }

   public java.lang.String getServletContextName() {
      return context.getPortletContextName();
   }

   public java.util.Enumeration getServletNames() {
      System.out.println("ServletContextWrapper: cannot call getServletNames on a PortletContext!");
      return null;
   }

   public java.util.Enumeration getServlets() {
      System.out.println("ServletContextWrapper: cannot call getServlets on a PortletContext!");
      return null;
   }

   public void log(java.lang.Exception exception, java.lang.String msg) {
      context.log(msg, exception);
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