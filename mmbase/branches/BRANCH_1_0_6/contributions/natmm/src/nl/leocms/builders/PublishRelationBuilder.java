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
package nl.leocms.builders;

import nl.leocms.util.PublishUtil;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Edwin van der Elst
 * Date :Oct 22, 2003
 *  
 */
public class PublishRelationBuilder extends InsRel {

   private static Logger log = Logging.getLoggerInstance(PublishRelationBuilder.class.getName());
   private boolean publish = false;

   public boolean init() {
      if ("true".equals(getInitParameter("publish"))) {
         publish = true;
         log.info(tableName + " is active");
      } else {
         log.info(tableName + " is inactive");
      }
      return super.init();
   }

   public int insert(String owner, MMObjectNode node) {
      int number = super.insert(owner, node);
      if (number != -1) {
         if (publish) {
            PublishUtil.PublishOrUpdateNode(number);
         }
      }
      return number;
   }

   public boolean commit(MMObjectNode objectNode) {
      boolean retval = super.commit(objectNode);

      if (publish) {
         PublishUtil.PublishOrUpdateNode(objectNode);
      }
      return retval;
   }

   public void removeNode(MMObjectNode objectNode) {
      if (publish) {
         PublishUtil.removeNode(objectNode);
      }
      super.removeNode(objectNode);
   }

}
