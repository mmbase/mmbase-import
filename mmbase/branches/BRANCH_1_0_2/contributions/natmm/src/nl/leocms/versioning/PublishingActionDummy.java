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
package nl.leocms.versioning;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This defines a dummy action which is performed when a node 
 * from the publisingbuilder type is published and there is no other
 * action defined 
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 8-aug-2003
 * @version $Revision: 1.1 $
 */
public class PublishingActionDummy implements PublishingAction {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(PublishingActionDummy.class.getName());

   /**
    * @see nl.leocms.versioning.PublishingAction#inserted(int)
    */
   public void inserted(int nodenumber) {
      log.debug("Object inserted with number " + nodenumber);
   }

   /**
    * @see nl.leocms.versioning.PublishingAction#committed(org.mmbase.module.core.MMObjectNode)
    */
   public void committed(MMObjectNode node) {
      log.debug("objcet committed/changed with number " + node.getNumber());
   }

   /**
    * @see nl.leocms.versioning.PublishingAction#removed(org.mmbase.module.core.MMObjectNode)
    */
   public void removed(MMObjectNode node) {
      log.debug("objcet removed with number " + node.getNumber());
   }

}
