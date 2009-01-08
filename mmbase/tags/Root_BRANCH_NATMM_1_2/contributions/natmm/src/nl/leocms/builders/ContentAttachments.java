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

import org.mmbase.module.builders.Attachments;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;

/**
 * @todo javadoc
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 18-nov-2003
 * @version $Revision: 1.1 $
 */
public class ContentAttachments extends Attachments {

   ContentElementBuilder ceb = null;

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#init()
    */
   public boolean init() {
      if (super.init()) {
         ceb = (ContentElementBuilder) MMBase.getMMBase().getBuilder("contentelement");
      }
      return true;
   }

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#setDefaults(org.mmbase.module.core.MMObjectNode)
    */
   public void setDefaults(MMObjectNode node) {
      super.setDefaults(node);
      ceb.setDefaults(node);
      node.setValue("embargo", (System.currentTimeMillis() / 1000)); //current time
      node.setValue("verloopdatum", "2145913209");//last MMbase year
   }

   /**
    * @see org.mmbase.module.core.MMObjectBuilder#preCommit(org.mmbase.module.core.MMObjectNode)
    */
   public MMObjectNode preCommit(MMObjectNode node) {
      super.preCommit(node);
      ceb.preCommit(node);
      
      node.setValue("title", node.getStringValue("titel"));
      node.setValue("description", node.getStringValue("omschrijving"));
      return node;
   }

}
