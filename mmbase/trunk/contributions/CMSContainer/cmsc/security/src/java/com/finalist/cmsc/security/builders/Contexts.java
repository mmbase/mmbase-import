/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.security.builders;

import org.mmbase.bridge.Query;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.security.Authorization;
import org.mmbase.security.Operation;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.security.implementation.cloudcontext.User;

public class Contexts extends org.mmbase.security.implementation.cloudcontext.builders.Contexts {

   public final MMObjectNode getDefaultContextNode() {
      return getContextNode("default");
   }

   /**
    * CMSC-1174 Removal of inline links fails for non administrators
    * Workaround for http://www.mmbase.org/jira/browse/MMB-1713
    * 
    * MMBase code to handle transactions is changed and sometimes nodes which are not modified are committed
    * CMSc deletes relations in the RichtextBuilder. These deleted relations are stilled loaded in the
    * transaction are committed, The transaction code checks if the node exists which produces and exception.
    * Read actions are always allowed. 
    * 
    * @see org.mmbase.security.implementation.cloudcontext.builders.Contexts#mayDo(org.mmbase.security.implementation.cloudcontext.User, int, org.mmbase.security.Operation)
    */
   public boolean mayDo(User user, int nodeId, Operation operation) throws SecurityException {
      if (operation == Operation.READ) {
          return true;
      }
      return super.mayDo(user, nodeId, operation);
   }

   /**
    * @see org.mmbase.security.implementation.cloudcontext.builders.Contexts#mayDo(org.mmbase.security.implementation.cloudcontext.User,
    *      org.mmbase.module.core.MMObjectNode, org.mmbase.security.Operation)
    *      Disabled right check per group and user.
    */
   @Override
   protected boolean mayDo(User user, MMObjectNode contextNode, Operation operation) {
      // if (contextNode != null &&
      // "system".equalsIgnoreCase(contextNode.getStringValue("name"))) {
      // return user.getRank() == Rank.ADMIN;
      // }
      return true;
   }


   /**
    * @see org.mmbase.security.implementation.cloudcontext.builders.Contexts#mayDo(org.mmbase.module.core.MMObjectNode,
    *      org.mmbase.module.core.MMObjectNode, org.mmbase.security.Operation,
    *      boolean) Disabled right check per group and user.
    */
   @Override
   protected boolean mayDo(MMObjectNode user, MMObjectNode contextNode, Operation operation, boolean checkOwnRights) {
      return true;
   }


   @Override
   public Authorization.QueryCheck check(User user, Query query, Operation operation) {
      if (user.getRank().getInt() >= Rank.ADMIN.getInt()) {
         return Authorization.COMPLETE_CHECK;
      }
      if (operation == Operation.READ) {
         return Authorization.COMPLETE_CHECK;
      }
      else {
         // Queries are read operations
         // not checking for READ: never mind, this method is only used for read
         // checks any way
         return Authorization.NO_CHECK;
      }
   }

}
