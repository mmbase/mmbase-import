package portlet.wrappers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.portlet.*;

/**
 * Class to wrap a PortletSession so it can be used as a HttpSession
 */
public class HttpSessionWrapper implements HttpSession {

   private PortletSession session;
   private int SCOPE = PortletSession.APPLICATION_SCOPE;

   public HttpSessionWrapper(PortletSession session) {
      this.session = session;
   }

   // scope (default APPLICATION_SCOPE) determines what scope
   // getAttribute etc will use. Change here to use PORTLET_SCOPE if necessary.
   public HttpSessionWrapper(PortletSession session, int scope) {
      this.session = session;
      this.SCOPE = scope;
   }

   public java.lang.Object getAttribute(java.lang.String name) {
      return session.getAttribute(name, SCOPE);
   }

   public java.util.Enumeration getAttributeNames() {
      return session.getAttributeNames(SCOPE);
   }

   public long getCreationTime() {
      return session.getCreationTime();
   }

   public java.lang.String getId() {
      return session.getId();
   }

   public long getLastAccessedTime() {
      return session.getLastAccessedTime();
   }

   public int getMaxInactiveInterval() {
      return session.getMaxInactiveInterval();
   }

   public ServletContext getServletContext() {
      return new ServletContextWrapper(session.getPortletContext());
   }

   public HttpSessionContext getSessionContext() {
      System.out.println("HttpSessionWrapper: cannot call getSessionContext on a PortletSession!");
      return null;
   }

   public java.lang.Object getValue(java.lang.String name) {
      System.out.println("HttpSessionWrapper: cannot call getValue on a PortletSession!");
      return null;
   }

   public java.lang.String[] getValueNames() {
      System.out.println("HttpSessionWrapper: cannot call getValueNames on a PortletSession!");
      return null;
   }

   public void invalidate() {
      session.invalidate();
   }

   public boolean isNew() {
      return session.isNew();
   }

   public void putValue(java.lang.String name, java.lang.Object value) {
      System.out.println("HttpSessionWrapper: cannot call putValue on a PortletSession!");
   }

   public void removeAttribute(java.lang.String name) {
      session.removeAttribute(name, SCOPE);
   }

   public void removeValue(java.lang.String name) {
      System.out.println("HttpSessionWrapper: cannot call removeValue on a PortletSession!");
   }

   public void setAttribute(java.lang.String name, java.lang.Object value) {
      session.setAttribute(name, value, SCOPE);
   }

   public void setMaxInactiveInterval(int interval) {
      session.setMaxInactiveInterval(interval);
   }

}