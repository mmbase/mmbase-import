/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.mmbase.security.cloud;

import java.util.Map;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.security.Authentication;
import org.mmbase.security.Rank;
import org.mmbase.bridge.*;
import org.mmbase.security.SecurityException;
import org.mmbase.security.UserContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.mmbase.util.CloudFactory;

/**
 * This class implements the custom authentication rules for the leocms.nl
 * Quiz Engine
 * <ul>
 * <li>Users are authenticated using {@link UserBuilder#getRankAsString(String,String)}.
 * <li>An authenticated user with username "admin" is an admin user.
 * <li>An authenticated user with rank editor is an editor user.
 * <li>All other authenticated users are basic users.
 * </ul>
 *
 * @see UserBuilder
 * @author  Ronald Kramp
 * @version '$Revision: 1.2 $, $Date: 2006-07-18 18:22:38 $'
 */
public class Authenticate extends Authentication {


   /** Logger. */
   private static Logger log = Logging.getLoggerInstance(Authenticate.class.getName());

   /** The builder implementation that is used for authentication. */
   private UserBuilder builder;

   private long validKey;


   /**
    * Constructor
    */
   public Authenticate() {
      validKey = System.currentTimeMillis();
   }


   /**
    * Checks whether the given user context is valid.
    * @param userContext the user context to check
    * @return <code>true</code> when the user context is valid, <code>false</code>
    *         otherwise
    */
   public boolean isValid(UserContext userContext) {
      return ((User) userContext).getKey() == validKey;
   }



   /**
    * Loads any additional settings for this class.
    */
   protected void load() {
      Rank.createRank(2000, "chiefeditor");
   }



   /**
    * This method will verify the login, and give a UserContext back if
    * everything is valid.
    *
    * @param moduleName name of the login module
    * @param loginInfo <code>Map</code> of login information (username/password)
    * @param parameters extra login parameters
    * @return a user context if the login succeeds or <code>null</code> if the
    *         user does not exist
    * @throws SecurityException if invalid parameters are given
    */
   public UserContext login(String moduleName, Map loginInfo, Object[] parameters)
      throws SecurityException {

      log.debug("[Authenticate]: Using login module: " + moduleName);

      if (moduleName.equals("anonymous")) {

         return new User("anonymous", Rank.ANONYMOUS, validKey);
      }
      else if (moduleName.equals("name/password")) {

         builder = getBuilder();

         String username = (String) loginInfo.get("username");
         String password = (String) loginInfo.get("password");


         

         // Check properties in loginInfo.
         if (username == null) {
            throw new SecurityException("Property 'username' not provided in login.");
         }
         if (password == null) {
            throw new SecurityException("Property 'password' not provided in login.");
         }
         if (username.equals("anonymous")) {
            String errorMessage = "User '" + username
               + "' is not allowed to do a login.";
            log.error(errorMessage);
            throw new SecurityException(errorMessage);
         }

         String rankAsString = builder.getRankAsString(username, password);

         if (rankAsString == null) { // username is treated as case insensitive
            Cloud cloud = CloudFactory.getCloud();
            NodeList nl = cloud.getNodeManager("users").getList("UPPER(account) = '" + username.toUpperCase() + "'", null, null);
            if(nl.size()>0) {
               username = nl.getNode(0).getStringValue("account");
               rankAsString = builder.getRankAsString(username, password);
            }
         }

         if (rankAsString == null) {
            return null;
         }
         Rank rank = Rank.getRank(rankAsString);
         if (rank == null) {
             return null;
         }
         return new User(username, rank, validKey);
      }
      else {
         // Unknown login module.
         String errorMessage = "Attempt to login using module '" + moduleName
            + "' (only 'anonymous' and 'name/password' exist).";
         log.error(errorMessage);
         throw new SecurityException(errorMessage);
      }
   }



   /**
    * Returns the builder if it exists or throws a
    * <code>SecurityException</code> if it does not.
    */
   private UserBuilder getBuilder()
      throws SecurityException {

      if (builder == null) {
         MMBase mmb = (MMBase) Module.getModule("mmbaseroot");
         builder = (UserBuilder) mmb.getMMObject("users");
         if (builder == null) {
            String errorMessage = "Builder 'users' does not exist.";
            log.error(errorMessage);
            throw new SecurityException(errorMessage);
         }
      }
      return builder;
   }



   /**
    *
    */
   class User extends UserContext {

      private String username;
      private Rank rank;
      private long key;

      User(String username, Rank rank, long key) {
         this.username = username;
         this.rank = rank;
         this.key = key;
      }

      public String getIdentifier() {
         return username;
      }

      public Rank getRank() throws SecurityException {
         return rank;
      }

      long getKey() {
         return key;
      }

      public String toString() {
         return username + "[" + rank + "]";
      }
   }
}