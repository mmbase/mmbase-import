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

import nl.leocms.util.PaginaHelper;
import nl.leocms.util.PublishUtil;
import nl.leocms.applications.NatMMConfig;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
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

      if(NatMMConfig.hasClosedUserGroup) {
         if (objectNode.getName().equals("posrel")) {
            Cloud cloud = CloudFactory.getCloud();

            String sourceNumber = objectNode.getStringValue("snumber");
            Node sourceNode = cloud.getNode(sourceNumber);
            String sNodeManagerName = sourceNode.getNodeManager().getName();

            String destionationNumber = objectNode.getStringValue("dnumber");
            Node destinationNode = cloud.getNode(destionationNumber);
            String dNodeManagerName = destinationNode.getNodeManager().getName();

            if(sNodeManagerName.equals("dossier") && dNodeManagerName.equals("artikel")) {
               if (objectNode.getStringValue("pos").equals("1")) {
                  // notification is already send, do nothing
               } else {
                  sendMailToCustomers(cloud, sourceNumber, destionationNumber);
                  objectNode.setValue("pos",1);
               }
            }
         }
      }

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

   private void sendMailToCustomers(Cloud cloud, String dossierNumber, String articleNumber) {
      NodeList customersList = cloud.getList(dossierNumber,"dossier,posrel,deelnemers",
            "deelnemers.number,deelnemers.firstname,deelnemers.email", null, null, null, null, true);

      PaginaHelper ph = new PaginaHelper(cloud);
      String relatedPage = cloud.getNodeByAlias("dossiers").getStringValue("number");
      String title = cloud.getNode(articleNumber).getStringValue("titel");

      String link = NatMMConfig.getLiveUrl() + ph.createItemUrl(articleNumber,relatedPage,null, "");

      for(int i=0; i<customersList.size(); i++) {
         Node customer = customersList.getNode(i);
         String toEmailAddress = customer.getStringValue("deelnemers.email");
         String message = "Beste " + customer.getStringValue("deelnemers.firstname")
            + ", Er is een nieuw artikel geplaatst in een door u geselecteerd dossier: ";

         log.info("send email: " + dossierNumber + " to " + toEmailAddress);

         Node emailNode = cloud.getNodeManager("email").createNode();
         emailNode.setValue("from", "demo@mediacompetence.com");
         emailNode.setValue("subject", "About new article");
         emailNode.setValue("replyto", "demo@mediacompetence.com");
         emailNode.setValue("body",
                         "<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\">"
                            + message + "\n\n" + title + " " + link
                         + "</multipart>"
                         + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                            + "<html>"
                               + message
                               + "<br/><br/><a href=\"" + link + "\">" + title + "</a>"
                            + "</html>"
                         + "</multipart>");
         emailNode.commit();

         emailNode.setValue("to", toEmailAddress);
         emailNode.commit();
         emailNode.getValue("mail(oneshotkeep)");
      }
   }
}
