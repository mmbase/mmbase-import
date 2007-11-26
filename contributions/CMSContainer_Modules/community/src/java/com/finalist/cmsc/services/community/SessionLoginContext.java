package com.finalist.cmsc.services.community;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.Subject;
import javax.security.auth.login.*;
import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.*;
import java.util.Map;

/**
 * The SessionLoginContext extends the JAAS LoginContext, so that, when bound to
 * an HttpSession, it will execute login(), and when the session times out, it
 * will execute logout(). One can bind the SessionLoginContext to the session,
 * by calling<br>
 * 
 * <pre>
 * session.setAttribute(&quot;loginContext&quot;, myLoginContext);
 * </pre>
 * 
 * @author Paul Feuer and John Musser
 * @version 1.0
 */

public class SessionLoginContext extends LoginContext implements HttpSessionBindingListener {

   /**
    * Default constructor. See javax.security.auth.login.LoginContext for
    * details.
    */
   public SessionLoginContext(String name) throws LoginException {
      super(name);
   }


   /**
    * Default constructor. See javax.security.auth.login.LoginContext for
    * details.
    */
   public SessionLoginContext(String name, CallbackHandler callbackHandler) throws LoginException {
      super(name, callbackHandler);
   }


   /**
    * Default constructor. See javax.security.auth.login.LoginContext for
    * details.
    */
   public SessionLoginContext(String name, Subject subject) throws LoginException {
      super(name, subject);
   }


   /**
    * Default constructor. See javax.security.auth.login.LoginContext for
    * details.
    */
   public SessionLoginContext(String name, Subject subject, CallbackHandler callbackHandler) throws LoginException {
      super(name, subject, callbackHandler);
   }


   /**
    * Notifies the object that it is being bound to a session and identifies the
    * session.
    * 
    * @param event
    *           the event that identifies the session
    */
   public void valueBound(HttpSessionBindingEvent event) {
      try {
         login();
      }
      catch (LoginException ex) {
         throw new java.lang.RuntimeException(ex.getMessage());
      }
   }


   /**
    * Notifies the object that it is being unbound from a session and identifies
    * the session.
    * 
    * @param event
    *           the event that identifies the session
    */
   public void valueUnbound(HttpSessionBindingEvent event) {
      try {
         logout();
      }
      catch (LoginException ex) {
         throw new java.lang.RuntimeException(ex.getMessage());
      }
   }
}
