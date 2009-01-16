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
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import com.finalist.mmbase.util.CloudFactory;

public class VraagbaakBuilder extends ContentElementBuilder {

   protected static Logger log = Logging.getLoggerInstance(VraagbaakBuilder.class.getName());

   public boolean commit(MMObjectNode node){

      String sNodeNumber = node.getStringValue("number");
      Cloud cloud = CloudFactory.getCloud();
      NodeList nl = cloud.getList(sNodeNumber,"vraagbaak,posrel,paragraaf",
         "posrel.number",null,"posrel.pos,paragraaf.number","up,down",null,false);
      for (int i = 0; i < nl.size(); i++){
         Node nPosrel = cloud.getNode(nl.getNode(i).getStringValue("posrel.number"));
         nPosrel.setIntValue("pos",i+1);
         nPosrel.commit();
      }
      return super.commit(node);
   }

}
