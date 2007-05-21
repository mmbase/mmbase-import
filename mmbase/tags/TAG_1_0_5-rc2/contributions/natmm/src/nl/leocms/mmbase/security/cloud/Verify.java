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

import java.util.*;
import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.security.Operation;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.security.UserContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.storage.search.implementation.*;


/**
 * This class implements the custom authorization rules for leeuwarden 
  * <p>
 * An <em>editor</em> is a basic user that is a member of the group of editors,
 * e.g. that has a "member" relation to the redactie node with alias 
 * "redactie.editors".
 * <p>
 * The following builders are considered "system builders":
 * <ul>
 * <li>typedef</li>
 * <li>syncnodes</li>
 * <li>mmservers</li>
  * <li>versions</li>
 * <li>typerel</li>
 * <li>reldef</li>
 * <li>daymarks</li>
 * <li>users</li>
  * </ul>
 * This list is configured in {@link #load()}
 *
 * @see Authenticate
 * @see UserBuilder
 * @author  Ronald Kramp
 * @version '$Revision: 1.1 $, $Date: 2006-03-05 21:43:58 $'
 */
public class Verify extends org.mmbase.security.implementation.cloud.Verify {

   /** No idea why this is static. */
   private static MMObjectBuilder builder = null;

   /** Timeout for redacteuren cache (milliseconds). */
   private final static int IS_EDITOR_CACHE_TIMEOUT = 60 * 1000; // One minute.
   
   /** Logger. */
   private static Logger log = Logging.getLoggerInstance(Verify.class.getName());

   /** Set of restricted builders. */
   private Set adminBuilders = new HashSet();

   // javadoc is inherited
   protected void load() {
      // Create list of builders that are consisered "system builders".
      adminBuilders.add("typedef");
      adminBuilders.add("syncnodes");
      adminBuilders.add("mmservers");
      adminBuilders.add("versions");
      adminBuilders.add("typerel");
      adminBuilders.add("reldef");
      adminBuilders.add("daymarks");
      
      Rank.createRank(2000, "chiefeditor");
   }

   // javadoc is inherited
   public boolean check(UserContext user, int nodeId, Operation operation) {

      // Everyone may read everything.
      if (operation == Operation.READ) {
         return true;
      }

      // Anonymous may do nothing further.
      if (user.getRank() == Rank.ANONYMOUS) {
         return false;
      }

      // Link operation may always be done by basic users.
      if (operation == Operation.CHANGE_RELATION) {
         return true;
      }

      // Retrieve the node to be operated upon.
      MMObjectNode node = getMMNode(nodeId);
      log.debug("Checking node #" + nodeId + " for operation " + operation
         + " by user " + user.getIdentifier() + ".");

      String username = user.getIdentifier();
      String builderNodeName = node.getName();
      Rank rank = user.getRank();
      
      // Admin may write user nodes and delete all but his own.
      if (builderNodeName.equals("users")) {
         if (rank == Rank.ADMIN) {

            // Look at our node.
            if (node.getStringValue("account").equals(username)) {
               if (operation == Operation.WRITE) {
                  // User can write his/her own node.
                  return true;
               }
               if (operation == Operation.DELETE) {
                  // User can not delete his/her own node.
                  return false;
               }
            }
            // Further nothing allowed, unless we are the admin.
            return true;
         } 
         else if (rank == Rank.getRank("chiefeditor")) {

            // Look at own node.
            if (node.getStringValue("account").equals(username)) {
               if (operation == Operation.WRITE) {
                  // User can write his/her own node.
                  return true;
               }
               if (operation == Operation.DELETE) {
                  // User can not delete his/her own node.
                  return false;
               }
            }
            // look at admin node
            if (node.getStringValue("rank").equals("administrator")) {
               return false;
            }
            // Further nothing allowed, unless we are the admin.
            return true;
         } 
         else {
            // a non editor or admin may do nothin
            return false;
         }
      }
      
      // Not creating a core builder node.
      else if ((operation != Operation.CREATE)
            && (adminBuilders.contains(builderNodeName))) {
         return rank == Rank.ADMIN;
      }
      else {
         // Admin may do everything else.
         if (rank == Rank.ADMIN) {
            return true;
         }
         
         boolean isEditor = (rank.getInt() >= Rank.getRank("basic user").getInt());
         
         // Basic user may create new nodes, except one of the system builders,
         // if he is an editor.
         if (operation == Operation.CREATE) {
            String builderName = node.getStringValue("name");
            if (adminBuilders.contains(builderName)) {
               return false;
            }
            else {
               return isEditor;
            }
         }

         // Change context and change node itselve only allowed if user is 
         // an editor.
         if (operation == Operation.WRITE
            || operation == Operation.CHANGE_CONTEXT
            || operation == Operation.DELETE) {

            return isEditor;
         }
         
         // basic users may do everything further...
         return true;
      }
   }
   
   /**
    * Returns a <code>Set</code> of  possible contexts.
    *
    * @param user the user for which to retrieve the <code>Set</code> of possible
    *        context
    * @param nodeId node id
    * @return a <code>Set</code> of possible contexts
    * @throws SecurityException if something went wrong
    */
   public Set getPossibleContexts(UserContext user, int nodeId)
      throws SecurityException {

      builder = MMBase.getMMBase().getMMObject("users");
      Iterator userIterator = null;
      try {
         userIterator = builder.getNodes(new NodeSearchQuery(builder)).iterator();
      } catch (Exception e) {
         log.error(e);
      }     

      // Retrieve the possible contexts.
      Set contexts = new HashSet();
      while (userIterator!=null&&userIterator.hasNext()) {
         contexts.add(
            ((MMObjectNode) userIterator.next()).getStringValue("account"));
      }
      return contexts;
   }
   
   private MMObjectNode getMMNode(int n) {

      if (builder == null) {
         MMBase mmb = (MMBase) Module.getModule("mmbaseroot");
         builder = mmb.getMMObject("typedef");
         if (builder == null) {
            String errorMessage = "Builder 'typedef' not found.";
            log.error(errorMessage);
            throw new SecurityException(errorMessage);
         }
      }

      MMObjectNode node = builder.getNode(n);
      if (node == null) {
         String msg = "Node #" + n + " not found.";
         log.error(msg);
         throw new SecurityException(msg);
      }
      return node;
   }
}