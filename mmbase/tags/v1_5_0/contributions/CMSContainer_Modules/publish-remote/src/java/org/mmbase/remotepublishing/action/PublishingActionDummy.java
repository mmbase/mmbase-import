/*
 * MMBase Remote Publishing
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.remotepublishing.action;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This defines a dummy action which is performed when a node 
 * from the publisingbuilder type is published and there is no other
 * action defined 
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @version $Revision: 1.1 $
 */
public class PublishingActionDummy implements PublishingAction {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(PublishingActionDummy.class.getName());

   /**
    * @see org.mmbase.remotepublishing.action.PublishingAction#inserted(int)
    */
   public void inserted(int nodenumber) {
      log.debug("Object inserted with number " + nodenumber);
   }

   /**
    * @see org.mmbase.remotepublishing.action.PublishingAction#committed(org.mmbase.module.core.MMObjectNode)
    */
   public void committed(MMObjectNode node) {
      log.debug("objcet committed/changed with number " + node.getNumber());
   }

   /**
    * @see org.mmbase.remotepublishing.action.PublishingAction#removed(org.mmbase.module.core.MMObjectNode)
    */
   public void removed(MMObjectNode node) {
      log.debug("objcet removed with number " + node.getNumber());
   }

}
