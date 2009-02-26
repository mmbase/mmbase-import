package portlet.wrappers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.portlet.*;

/**
 * Class to wrap a HttpSession so it can be used as a PortletSession
 */
public class PortletSessionWrapper implements PortletSession {

   public final static int APPLICATION_SCOPE = PortletSession.APPLICATION_SCOPE;
   public final static int PORTLET_SCOPE = PortletSession.PORTLET_SCOPE;

   private HttpSession session;
   private int SCOPE = APPLICATION_SCOPE;

   public PortletSessionWrapper(HttpSession session) {
      this.session = session;
   }

   // scope (default APPLICATION_SCOPE) determines what scope
   // the attributes in the contained HttpSession are 'in'.
   // Change here to use PORTLET_SCOPE if necessary.
   public PortletSessionWrapper(HttpSession session, int scope) {
      this.session = session;
      this.SCOPE = scope;
   }

   public java.lang.Object getAttribute(java.lang.String name) {
      if (this.SCOPE == PORTLET_SCOPE) {
         return session.getAttribute(name);
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with portlet-scoped attributes on this HttpSession!");
         return null;
      }
   }

   public java.lang.Object getAttribute(java.lang.String name, int scope) {
      if (this.SCOPE == scope) {
         return session.getAttribute(name);
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with scoped attributes on this HttpSession!");
         return null;
      }
   }

   public java.util.Enumeration getAttributeNames() {
      if (this.SCOPE == PORTLET_SCOPE) {
         return session.getAttributeNames();
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with portlet-scoped attributes on this HttpSession!");
         return null;
      }
   }

   public java.util.Enumeration getAttributeNames(int scope) {
      if (this.SCOPE == scope) {
         return session.getAttributeNames();
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with scoped attributes on this HttpSession!");
         return null;
      }
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

   public PortletContext getPortletContext() {
      return new PortletContextWrapper(session.getServletContext());
   }

   public void invalidate() {
      session.invalidate();
   }

   public boolean isNew() {
      return session.isNew();
   }

   public void removeAttribute(java.lang.String name) {
      if (this.SCOPE == PORTLET_SCOPE) {
         session.removeAttribute(name);
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with portlet-scoped attributes on this HttpSession!");
      }
   }

   public void removeAttribute(java.lang.String name, int scope) {
      if (this.SCOPE == scope) {
         session.removeAttribute(name);
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with scoped attributes on this HttpSession!");
      }
   }

   public void setAttribute(java.lang.String name, java.lang.Object value) {
      if (this.SCOPE == PORTLET_SCOPE) {
         session.setAttribute(name, value);
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with portlet-scoped attributes on this HttpSession!");
      }
   }

   public void setAttribute(java.lang.String name, java.lang.Object value, int scope) {
      if (this.SCOPE == scope) {
         session.setAttribute(name, value);
      } else {
         System.out.println("PortletSessionWrapper: cannot deal with scoped attributes on this HttpSession!");
      }
   }

   public void setMaxInactiveInterval(int interval) {
      session.setMaxInactiveInterval(interval);
   }


}