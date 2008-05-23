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
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.util.logging.*;
import nl.leocms.evenementen.Evenement;

/**
 * @todo javadoc
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 21-nov-2003
 * @version $Revision: 1.2 $
 */
public class ContentNatuurgebieden extends ContentOrganisatie {
  
   public boolean commit(MMObjectNode node) {
   
      log.debug("commiting natuurgebied " + node.getStringValue("titel") + "(" + node.getStringValue("number") + ")");

      Cloud cloud = CloudFactory.getCloud();
      // *** setting titel_eng to list of afdelingen numbers ***
      NodeIterator iNodes= cloud.getList(node.getStringValue("number")
               , "natuurgebieden,posrel,afdelingen"
               , "afdelingen.number", null, null, null, null, false).nodeIterator();
      String sRelatedDept = ",";
      if(iNodes.hasNext()) {
          Node nextNode = iNodes.nextNode();
          sRelatedDept += nextNode.getIntValue("afdelingen.number") + ",";
      }
      if(sRelatedDept.equals(",")) { sRelatedDept = ",-1,"; }
      node.setValue("titel_eng",sRelatedDept);
		
      boolean bSuperCommit = super.commit(node);
      
      return bSuperCommit;
   }
   
}
