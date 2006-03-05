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
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import com.finalist.mmbase.util.CloudFactory;

/**
 * @author Ronald Kramp
 *  
 */
public class PosrelBuilder extends InsRel {
   
   private static final String FORMULIERVELD = "formulierveld";
   private static final String FORMULIERVELD_ANTWOORD = "formulierveldantwoord";
   private static Logger log = Logging.getLoggerInstance(PosrelBuilder.class.getName());

   public boolean commit(MMObjectNode objectNode) {
      boolean retval = super.commit(objectNode);
      
      // hh: we do not use publishqueue in NatMM
      // if (isFormulierRelated(objectNode)) {
      //   PublishUtil.PublishOrUpdateNode(objectNode);
      // }
      return retval;
   }

   public void removeNode(MMObjectNode objectNode) {
      // hh: we do not use publishqueue in NatMM
      // if (isFormulierRelated(objectNode)) {
      //   PublishUtil.removeNode(objectNode);
      // }
      super.removeNode(objectNode);
   }
   
   private boolean isFormulierRelated(MMObjectNode objectNode) {
      Cloud cloud = CloudFactory.getCloud();
      String destionationNumber = objectNode.getStringValue("dnumber");
      Node destinationNode = cloud.getNode(destionationNumber);
      String destinatioNodeNodeManagerName = destinationNode.getNodeManager().getName();
      return ((destinationNode.getNodeManager().getName().equals(FORMULIERVELD)) || (destinationNode.getNodeManager().getName().equals(FORMULIERVELD_ANTWOORD)));
   }
}
