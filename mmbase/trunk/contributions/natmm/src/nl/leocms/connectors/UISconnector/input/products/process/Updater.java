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
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.connectors.UISconnector.input.products.process;

import java.util.*;
import org.mmbase.bridge.*;
import nl.leocms.connectors.UISconnector.input.products.model.*;
import nl.leocms.connectors.UISconnector.input.products.process.Result;


public class Updater
{
   public static ArrayList update(Cloud cloud, ArrayList arliProducts){
      ArrayList arliResult = new ArrayList();

      for(Iterator it = arliProducts.iterator(); it.hasNext(); ){
         Product product = (Product) it.next();
         Result result = new Result();
         result.setProduct(product);
         arliResult.add(result);


         String sExternID = product.getExternID();
         if (sExternID == null){
            result.setException(new Exception("ExternID can't be null"));
            result.setStatus(Result.EXCEPTION);
            result.setEvenementNode(null);
         }

         try{
            Node nodeEvenement = getEvenementNode(cloud, sExternID);
            if(nodeEvenement == null){
               //There is no such node in db
               nodeEvenement = cloud.getNodeManager("evenement").createNode();

               result.setStatus(result.ADDED);
               nodeEvenement.setStringValue("soort", "parent");
               nodeEvenement.setStringValue("isspare", "false");
               nodeEvenement.setStringValue("isoninternet", "true");
               nodeEvenement.setStringValue("iscanceled", "false");
 			   nodeEvenement.setStringValue("groepsexcursie", "0");
               nodeEvenement.setStringValue("aanmelden_vooraf", "1");
               nodeEvenement.setStringValue("adres_verplicht", "1");
               nodeEvenement.setStringValue("reageer", "0");
 			   nodeEvenement.setStringValue("min_aantal_deelnemers", "0");
 			   nodeEvenement.setStringValue("max_aantal_deelnemers", "9999");

			   // prevent creation of NULL values (NULL and "" are represented the same way)
 			   nodeEvenement.setStringValue("dagomschrijving", "");
 			   nodeEvenement.setStringValue("status", "-1");
 			   nodeEvenement.setStringValue("use_verloopdatum", "-1");
 			   nodeEvenement.setStringValue("aanmelden_vooraf", "-1");
 			   nodeEvenement.setStringValue("achteraf_bevestigen", "-1");
 			   nodeEvenement.setStringValue("begininschrijving", "-1");
 			   nodeEvenement.setStringValue("eindinschrijving", "-1");

               nodeEvenement.setStringValue("externid", sExternID);
               nodeEvenement.setStringValue("begindatum", "" + product.getEmbargoDate().getTime() / 1000);
               nodeEvenement.setStringValue("einddatum", "" + product.getExpireDate().getTime() / 1000);

            }
            else{
               //The node is already present
               result.setStatus(result.UPDATED);
            }
            nodeEvenement.setStringValue("embargo", "" + product.getEmbargoDate().getTime() / 1000);
            nodeEvenement.setStringValue("verloopdatum", "" + product.getExpireDate().getTime() / 1000);
            nodeEvenement.setStringValue("titel", product.getDescription());

            nodeEvenement.commit();
            result.setEvenementNode(nodeEvenement);

            //Sets price
            try{
               setPrice(cloud, nodeEvenement, product);
            }
            catch(Exception e){
               result.setStatus(Result.EXCEPTION);
               result.setException(e);
            }


            //Set payments
            try{
               setPayments(cloud, nodeEvenement, product);
            }
            catch(Exception e){
               result.setStatus(Result.EXCEPTION);
               result.setException(e);
            }
         }
         catch(Exception e){
            result.setException(e);
            result.setStatus(Result.EXCEPTION);
         }
      }


      return arliResult;
   }

   /**
    * Tries to find an existing evenement node,
    * It returns null if there is no such node
    *
    * @param cloud Cloud
    * @param sExternID String
    * @return Node
    */
   private static Node getEvenementNode(Cloud cloud, String sExternID){
      NodeList nl = cloud.getList("",
                                  "evenement",
                                  "evenement.number",
                                  "evenement.externid='" + sExternID + "'",
                                  null, null, null, true);
      if (nl.size() == 0){
         return null;
      }
      else{
         return cloud.getNode(nl.getNode(0).getStringValue("evenement.number"));
      }
   }

   private static void setPrice(Cloud cloud, Node nodeEvenement, Product product) throws Exception{

      //deletes old relations
      for(Iterator it = nodeEvenement.getRelations("posrel", cloud.getNodeManager("deelnemers_categorie")).iterator(); it.hasNext();){
         Node nodeRelation = (Node) it.next();
         nodeRelation.delete(true);
      }

      String sKey;
      if(product.isMembershipRequired()){
         sKey = "Leden";
      }
      else{
         sKey = "Niet leden";
      }

      //Looks for a proper category
      NodeList nl = cloud.getList("",
                                  "deelnemers_categorie",
                                  "deelnemers_categorie.number,deelnemers_categorie.naam",
                                  "deelnemers_categorie.naam='" + sKey + "'",
                                  null, null, null, true);
      try{
         Node nodeDeelnemers_categorie = cloud.getNode(nl.getNode(0).getStringValue("deelnemers_categorie.number"));
         Relation relPosrel =  nodeEvenement.createRelation(nodeDeelnemers_categorie, cloud.getRelationManager("posrel"));
         relPosrel.setIntValue("pos", new Double(product.getPrice() * 100).intValue());
         relPosrel.commit();
      }
      catch(Exception e){
         throw new Exception("Node deelnemers_categorie with name=\"" + sKey + "\" can't be found in db:" + e);
      }

      return;
   }

   private static void setPayments(Cloud cloud, Node nodeEvenement, Product product) throws Exception{
      NodeManager nmPaymentType = cloud.getNodeManager("payment_type");

      //deletes old relations
      for(Iterator it = nodeEvenement.getRelations("related", nmPaymentType).iterator(); it.hasNext();){
         Node nodeRelation = (Node) it.next();
         nodeRelation.delete(true);
      }

      ArrayList arliPaymentTypes = product.getPaymentTypes();
      for(Iterator it = arliPaymentTypes.iterator(); it.hasNext();){
         PaymentType paymentType = (PaymentType) it.next();

         //Looks for an already existing Node
         NodeList nl = cloud.getList("",
                                     "payment_type",
                                     "payment_type.number,payment_type.naam",
                                     "payment_type.naam='" + paymentType.getId() + "'",
                                     null, null, null, true);
         Node nodePaymentType;
         if(nl.size() > 0){
            //the Node already exist
            nodePaymentType = cloud.getNode(nl.getNode(0).getStringValue("payment_type.number"));
         }
         else{
            //There is no such node
            nodePaymentType = nmPaymentType.createNode();
            nodePaymentType.setStringValue("naam", paymentType.getDescription());
            nodePaymentType.setStringValue("externid", paymentType.getId());
            nodePaymentType.commit();
         }
         nodeEvenement.createRelation(nodePaymentType, cloud.getRelationManager("related")).commit();

         nodeEvenement.commit();
      }
   }
}
