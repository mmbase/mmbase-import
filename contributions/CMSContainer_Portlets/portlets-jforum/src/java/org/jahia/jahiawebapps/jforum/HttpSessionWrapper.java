package org.jahia.jahiawebapps.jforum;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Http sesson Wrapper in order to be compatible whith jahia session
 *
 * @author Khaled TLILI
 */
public class HttpSessionWrapper implements HttpSession {
   private HttpSession session;


   /**
    * Constructor for the HttpSessionWrapper object
    *
    * @param session Description of Parameter
    */
   public HttpSessionWrapper(HttpSession session) {
      this.session = session;
   }


   /**
    * setAttribute
    *
    * @param name  The new Attribute value
    * @param value The new Attribute value
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public void setAttribute(String name, Object value) {
      if (value == null) {
         session.removeAttribute(name);
      } else {
         session.setAttribute(name, value);
      }
   }


   /**
    * setMaxInactiveInterval
    *
    * @param mii The new MaxInactiveInterval value
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public void setMaxInactiveInterval(int mii) {
      session.setMaxInactiveInterval(mii);
   }


   /**
    * getAttribute
    *
    * @param name Description of Parameter
    * @return Object
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public Object getAttribute(String name) {
      return session.getAttribute(name);
   }


   /**
    * getAttributeNames
    *
    * @return Enumeration
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public Enumeration getAttributeNames() {
      return session.getAttributeNames();
   }


   /**
    * getCreationTime
    *
    * @return long
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public long getCreationTime() {
      return session.getCreationTime();
   }


   /**
    * getId
    *
    * @return String
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public String getId() {
      return session.getId();
   }


   /**
    * getLastAccessedTime
    *
    * @return long
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public long getLastAccessedTime() {
      return session.getLastAccessedTime();
   }


   /**
    * getMaxInactiveInterval
    *
    * @return int
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public int getMaxInactiveInterval() {
      return session.getMaxInactiveInterval();
   }


   /**
    * getServletContext
    *
    * @return ServletContext
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public ServletContext getServletContext() {
      return session.getServletContext();
   }


   /**
    * getSessionContext
    *
    * @return HttpSessionContext
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public HttpSessionContext getSessionContext() {
      return null;
   }


   /**
    * getValue
    *
    * @param name Description of Parameter
    * @return Object
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public Object getValue(String name) {
      return session.getValue(name);
   }


   /**
    * getValueNames
    *
    * @return String[]
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public String[] getValueNames() {
      return session.getValueNames();
   }


   /**
    * isNew
    *
    * @return boolean
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public boolean isNew() {
      return session.isNew();
   }


   /**
    * invalidate
    *
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public void invalidate() {
      session.invalidate();
   }


   /**
    * putValue
    *
    * @param name  Description of Parameter
    * @param value Description of Parameter
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public void putValue(String name, Object value) {
      session.putValue(name, value);
   }


   /**
    * removeAttribute
    *
    * @param name Description of Parameter
    * @todo Implement this javax.servlet.http.HttpSession method
    */
   public void removeAttribute(String name) {
      session.removeAttribute(name);
   }


   /**
    * removeValue
    *
    * @param value Description of Parameter
    * @todo Implement this javax.servlet.http.HttpSession method
    */
	public void removeValue(String value) {
		session.removeValue(value);
	}

}
