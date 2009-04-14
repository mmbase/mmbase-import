package com.finalist.cmsc.security;

import org.mmbase.security.implementation.cloudcontext.Authenticate;

import org.mmbase.security.implementation.cloudcontext.User;
import org.mmbase.security.implementation.cloudcontext.builders.Users;
import org.mmbase.security.SecurityException;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.Map;

/**
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class ThreadLocalAuthenticate extends Authenticate {
   private static final Logger log = Logging.getLoggerInstance(ThreadLocalAuthenticate.class);

   public static ThreadLocal<String> context = new ThreadLocal<String>();


   @Override
   public User login(String moduleName, Map loginInfo, Object[] parameters)
         throws org.mmbase.security.SecurityException {
      MMObjectNode node = null;
      Users users = Users.getBuilder();

      if ("threadlocal".equals(moduleName)) {

         String userName = context.get();
         if (userName == null) {
            throw new SecurityException("Expected the property 'username' on the threadlocal");
         }
         node = users.getUser(userName);
         if (node != null && !users.isValid(node)) {
            throw new SecurityException("Logged in an invalid user");
         }
         return node == null ? null : new User(node, getKey(), moduleName);
      }

      return (User) super.login(moduleName, loginInfo, parameters);
   }


   /**
    * Associates the given username with the current thread of execution.
    * 
    * @param username
    *           The username to bind.
    */
   public static void bind(String username) {
      cleanupAnyOrphanedCloud();
      doBind(username);
   }


   /**
    * Unassociate a previously bound username from the current thread of
    * execution.
    * 
    * @return The username which was unbound.
    */
   public static String unbind() {
      return doUnbind();
   }


   private static void cleanupAnyOrphanedCloud() {
      String orphan = doUnbind();
      if (orphan != null) {
         log.warn("Already username bound on call to bind(); make sure you clean up your username!");
      }
   }


   private static void doBind(String username) {
      if (username != null) {
         context.set(username);
      }
   }


   private static String doUnbind() {
      String username = context.get();
      if (username != null) {
         context.set(null);
      }
      return username;
   }
}
