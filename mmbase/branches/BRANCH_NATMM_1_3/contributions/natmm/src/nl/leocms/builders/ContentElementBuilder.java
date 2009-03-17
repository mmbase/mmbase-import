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

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Cloud;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.versioning.VersioningController;

/**
 * This builder maintains the create and lastmodified datetime
 * of contentelements
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 23-okt-2003
 * @version $Revision: 1.4 $
 */
public class ContentElementBuilder extends HtmlBuilder {

   /** methods switch */
   public static boolean ADDVERSION_ON_COMMIT = true;

   /** MMbase logging system */
   protected static Logger log = Logging.getLoggerInstance(ContentElementBuilder.class.getName());

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#setDefaults(org.mmbase.module.core.MMObjectNode)
    */
   public void setDefaults(MMObjectNode node) {
      log.debug("DEFAULT create node: " + node.getName() + " "  + node.getNumber());

      int seconds = (int) (System.currentTimeMillis()/1000);
      super.setDefaults(node);
      //mmbase datetime representation is in seconds
      node.setValue("creatiedatum",seconds);
      node.setValue("datumlaatstewijziging",seconds);
      node.setValue("embargo",seconds);
      node.setValue("verloopdatum", seconds + (30*24*60*60) ); // one month
      node.setValue("reageer","0"); // 0 = false
   }

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#preCommit(org.mmbase.module.core.MMObjectNode)
    */
   public MMObjectNode preCommit(MMObjectNode node) {
      log.debug("PRECOMMIT lastmodified node: "  + node.getName() + " " + node.getNumber());

      super.preCommit(node);
      //mmbase datetime representation is in seconds
      int seconds = (int) (System.currentTimeMillis()/1000);
      node.setValue("datumlaatstewijziging",seconds);

      return node;
   }

   public boolean commit(MMObjectNode node) {

      if (ADDVERSION_ON_COMMIT) {
         String builderName = node.getName();
         log.debug("COMMIT new version node for: " + builderName + " " + node.getNumber());
      	if (!builderName.equals("evenement") && !builderName.equals("pagina")) {
            Cloud cloud = CloudFactory.getCloud();
            VersioningController versioningController = new VersioningController(cloud);
            versioningController.addVersion(cloud.getNode(node.getNumber()));
         }
      }
         
      return super.commit(node);
   }
}
