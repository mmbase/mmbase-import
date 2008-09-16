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
import org.mmbase.bridge.NodeList;
import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.util.logging.*;
import nl.leocms.evenementen.*;

/**
 * @todo javadoc
 *
 * @author Nico Klasens (Finalist IT Group)
 * @created 21-nov-2003
 * @version $Revision: 1.9 $
 */
public class ContentEvenement extends ContentElementBuilder {
   private static final Logger log = Logging.getLoggerInstance(ContentEvenement.class);
   /**
    * @see org.mmbase.module.core.MMObjectBuilder#preCommit(org.mmbase.module.core.MMObjectNode)
    */
   public boolean commit(MMObjectNode node) {
      // *** setting default costs ***
      Cloud cloud = CloudFactory.getCloud();
      NodeIterator iNodes= cloud.getList(node.getStringValue("number")
               , "evenement,posrel1,deelnemers_categorie,posrel2,evenement_type"
               , "posrel1.number,posrel1.pos,posrel2.pos", null, null, null, null, false).nodeIterator();
      if(iNodes.hasNext()) {
          Node nextNode = iNodes.nextNode();
          int costs = nextNode.getIntValue("posrel1.pos");
          if(costs==-1) {
            int defaultCosts = nextNode.getIntValue("posrel2.pos");
            if(defaultCosts!=-1) {
               String costNodeNumber =  nextNode.getStringValue("posrel1.number");
               Node costNode = cloud.getNode(costNodeNumber);
               costNode.setIntValue("pos",defaultCosts);
               costNode.commit();
            }
          }
      }
      // *** setting lokatie to list of provincie numbers ***
      String sParent = Evenement.findParentNumber(node.getStringValue("number"));
      iNodes= cloud.getList(sParent
               , "evenement,related,natuurgebieden,pos4rel,provincies"
               , "provincies.number", null, null, null, null, false).nodeIterator();
      String relatedProv = ",";
      if(iNodes.hasNext()) {
          Node nextNode = iNodes.nextNode();
          relatedProv += nextNode.getIntValue("provincies.number") + ",";
      }
      if(relatedProv.equals(",")) { relatedProv = ",-1,"; }
      node.setValue("lokatie",relatedProv);
      // *** setting embargo / verloopdatum of parent to min begindatum and max einddatum of childs ***
      if(node.getStringValue("soort").equals("parent")) {
         long lMinBeginDatum = node.getLongValue("begindatum");
         long lMaxEindDatum =  node.getLongValue("einddatum");
         iNodes= cloud.getList(node.getStringValue("number")
               , "evenement1,partrel,evenement2"
               , "evenement2.begindatum,evenement2.einddatum", null, null, null, "destination", false).nodeIterator();
         while(iNodes.hasNext()) {
            Node nextNode = iNodes.nextNode();
            long lBeginDatum = nextNode.getLongValue("evenement2.begindatum");
            if(lBeginDatum<lMinBeginDatum) { lMinBeginDatum = lBeginDatum; }
            long lEindDatum = nextNode.getLongValue("evenement2.einddatum");
            if(lEindDatum>lMaxEindDatum) { lMaxEindDatum =lEindDatum; }
         }
         node.setValue("embargo",lMinBeginDatum);
         node.setValue("verloopdatum",lMaxEindDatum);
      }
      // *** update cur_aantal_deelnemers ***
      boolean isGroupExcursion = Evenement.isGroupExcursion(cloud,sParent);
      int iCurPart = 0;
      iNodes= cloud.getList(node.getStringValue("number")
               , "evenement,posrel,inschrijvingen,posrel,deelnemers"
               , "deelnemers.bron,deelnemers.number"
               , null, null, null, null, false).nodeIterator();
      while(iNodes.hasNext()) {
         Node nextNode = iNodes.nextNode();
         NodeList nl = cloud.getList(nextNode.getStringValue("deelnemers.number"),"deelnemers,related,deelnemers_categorie"
                                     ,"deelnemers_categorie.aantal_per_deelnemer,deelnemers_categorie.groepsactiviteit"
                                     ,null, null, null, null, false);
         if (nl.size()==0){
            if (!isGroupExcursion) {
               iCurPart += nextNode.getIntValue("deelnemers.bron");
            }
         } else {
            NodeIterator ni = nl.nodeIterator();
            while (ni.hasNext()) {
               Node n = ni.nextNode();
               boolean isGroupBooking = n.getStringValue(
                  "deelnemers_categorie.groepsactiviteit").equals("1");
               // for group excursions only count the group booking
               if (!isGroupExcursion || isGroupBooking) {
                  iCurPart += n.getIntValue("deelnemers.bron") *
                     n.getIntValue("deelnemers_categorie.aantal_per_deelnemer");
               }
            }
         }

      }
      node.setValue("cur_aantal_deelnemers",iCurPart);
      // *** send email, if the event is canceled ***
       if(node.getChanged().contains("iscanceled")
            && node.getStringValue("iscanceled").equals("true")) {
            (new EventNotifier()).isCanceledNotification(cloud, node.getStringValue("number"));
         }

      boolean bSuperCommit = super.commit(node);

      return bSuperCommit;
   }

}
