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
import org.mmbase.util.logging.*;
import nl.leocms.connectors.UISconnector.input.products.model.*;
import nl.leocms.connectors.UISconnector.input.products.process.Result;
import nl.leocms.connectors.UISconnector.shared.properties.model.*;
import nl.leocms.connectors.UISconnector.shared.properties.process.PropertyUtil;

public class Updater
{

   private static final Logger log = Logging.getLoggerInstance(Updater.class);


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
            NodeManager objectNodeManager;

            /*
            UIS uses the same object for different types of forms. Something like the following switch
            could be used to fill the corresponding objects in NatMM. Now the distinction is made on
            the basis on evenement_type, see below.

            switch(product.getProductType()){
               case Product.PRODUCT_TYPE_EVENT:{
                  objectNodeManager = cloud.getNodeManager("evenement");
                  break;
               }
               case Product.PRODUCT_TYPE_ITEM:{
                  objectNodeManager = cloud.getNodeManager("items");
                  break;
               }
               case Product.PRODUCT_TYPE_SUBSCRIPTION:{
                  objectNodeManager = cloud.getNodeManager("formulier");
                  break;
               }
               default:{
                  throw new Exception("Unsupported product type");
               }
            }
            */

            objectNodeManager = cloud.getNodeManager("evenement");
            Node nodeObject = getObjectNode(objectNodeManager, sExternID);


            if(nodeObject == null){
               //There is no such node in db
               nodeObject = objectNodeManager.createNode();

               result.setStatus(result.ADDED);
               nodeObject.setStringValue("soort", "parent");
               nodeObject.setStringValue("externid", sExternID);
               nodeObject.setStringValue("begindatum", "" + product.getEmbargoDate().getTime() / 1000);
               nodeObject.setStringValue("einddatum", "" + product.getExpireDate().getTime() / 1000);

               nodeObject.setStringValue("isspare", "false");
               nodeObject.setStringValue("isoninternet", "true");
               nodeObject.setStringValue("iscanceled", "false");
               nodeObject.setStringValue("groepsexcursie", "0");
               nodeObject.setStringValue("aanmelden_vooraf", "1");
               nodeObject.setStringValue("adres_verplicht", "1");
               nodeObject.setStringValue("reageer", "0");
               nodeObject.setStringValue("min_aantal_deelnemers", "0");
               nodeObject.setStringValue("max_aantal_deelnemers", "9999");

               // prevent creation of NULL values (NULL and "" are represented the same way)
               nodeObject.setStringValue("dagomschrijving", "");
               nodeObject.setStringValue("status", "-1");
               nodeObject.setStringValue("use_verloopdatum", "-1");
               nodeObject.setStringValue("achteraf_bevestigen", "-1");

               // these values are not used in the agenda.jsp of natmm and the editors
               nodeObject.setStringValue("begininschrijving", "" + product.getEmbargoDate().getTime() / 1000);
               nodeObject.setStringValue("eindinschrijving", "" + product.getExpireDate().getTime() / 1000);

               // workaround use expiredate from UIS as begin and end date of event
               nodeObject.setStringValue("begindatum", "" + product.getExpireDate().getTime() / 1000);
               nodeObject.setStringValue("einddatum", "" + product.getExpireDate().getTime() / 1000);

               // for events of type parent the begindatum == embargo and verloopdatum == enddatum
               // see nl.leocms.builders.ContenEvenement class
               nodeObject.setStringValue("embargo", "" + product.getExpireDate().getTime() / 1000);
               nodeObject.setStringValue("verloopdatum", "" + product.getExpireDate().getTime() / 1000);
            }
            else{
               //The node is already present
               result.setStatus(result.UPDATED);
            }
            nodeObject.setStringValue("titel", product.getDescription());

            nodeObject.commit();
            result.setEvenementNode(nodeObject);

            //Sets price
            try{
               setPrice(cloud, nodeObject, product);
            }
            catch(Exception e){
               result.setStatus(Result.EXCEPTION);
               result.setException(e);
            }


            //Set payments
            try{
               setPayments(cloud, nodeObject, product);
            }
            catch(Exception e){
               result.setStatus(Result.EXCEPTION);
               result.setException(e);
            }

            //Set Properties
            try{
              List listProperties = product.getProperties();
              PropertyUtil.setProperties(cloud, nodeObject, listProperties);
            }
            catch(Exception e){
               result.setStatus(Result.EXCEPTION);
               result.setException(e);
            }

            //Set evenement_type
            try {
               setEvenementType(cloud, nodeObject, product);
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
   private static Node getObjectNode(NodeManager nodeManager, String sExternID){
      NodeList nl = nodeManager.getCloud().getList("",
                                  nodeManager.getName(),
                                  nodeManager.getName() + ".number",
                                  nodeManager.getName() + ".externid='" + sExternID + "'",
                                  null, null, null, true);
      if (nl.size() == 0){
         return null;
      }
      else{
         return nodeManager.getCloud().getNode(nl.getNode(0).getStringValue(nodeManager.getName() + ".number"));
      }
   }

   private static void setPrice(Cloud cloud, Node nodeObject, Product product) throws Exception{

      //deletes old relations
      for(Iterator it = nodeObject.getRelations("posrel", cloud.getNodeManager("deelnemers_categorie")).iterator(); it.hasNext();){
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
         Relation relPosrel =  nodeObject.createRelation(nodeDeelnemers_categorie, cloud.getRelationManager("posrel"));
         relPosrel.setIntValue("pos", new Double(product.getPrice() * 100).intValue());
         relPosrel.commit();
      }
      catch(Exception e){
         throw new Exception("Node deelnemers_categorie with name=\"" + sKey + "\" can't be found in db:" + e);
      }

      return;
   }



   private static void setPayments(Cloud cloud, Node nodeObject, Product product) throws Exception{
      NodeManager nmPaymentType = cloud.getNodeManager("payment_type");

      //deletes old relations
      for(Iterator it = nodeObject.getRelations("related", nmPaymentType).iterator(); it.hasNext();){
         Node nodeRelation = (Node) it.next();
         nodeRelation.delete(true);
      }


      ArrayList arliPaymentTypes = product.getPaymentTypes();
      for(Iterator it = arliPaymentTypes.iterator(); it.hasNext();){
         PaymentType paymentType = (PaymentType) it.next();

         //Looks for an already existing Node
         NodeList nl = cloud.getList("",
                                     "payment_type",
                                     "payment_type.number,payment_type.externid",
                                     "payment_type.externid='" + paymentType.getId() + "'",
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
         nodeObject.createRelation(nodePaymentType, cloud.getRelationManager("related")).commit();

         nodeObject.commit();
      }
   }


   private static void setEvenementType(Cloud cloud, Node nodeObject, Product product) throws Exception{

      NodeManager nmEvenementType = cloud.getNodeManager("evenement_type");

      // deletes old relations
      // for(Iterator it = nodeObject.getRelations("related", nmEvenementType).iterator(); it.hasNext();){
      //   Node nodeRelation = (Node) it.next();
      //   nodeRelation.delete(true);
      // }

      //Look for an already existing Node
      NodeList nl = cloud.getList("",
                                  "evenement_type",
                                  "evenement_type.number,evenement_type.naam",
                                  "evenement_type.naam='" + product.getProductTypeName() + "'",
                                  null, null, null, true);
      Node nodeEvenementType;
      if(nl.size() > 0){
         //the Node already exist
         nodeEvenementType = cloud.getNode(nl.getNode(0).getStringValue("evenement_type.number"));
      }
      else{
         //There is no such node
         nodeEvenementType = nmEvenementType.createNode();
         switch(product.getProductType()){
            case Product.PRODUCT_TYPE_EVENT:{
               nodeEvenementType.setStringValue("naam", product.getProductTypeName());
               nodeEvenementType.setStringValue("isoninternet", "1");
               break;
            }
            case Product.PRODUCT_TYPE_ITEM:{
               nodeEvenementType.setStringValue("naam", product.getProductTypeName());
               nodeEvenementType.setStringValue("isoninternet", "0");
               break;
            }
            case Product.PRODUCT_TYPE_SUBSCRIPTION:{
               nodeEvenementType.setStringValue("naam", product.getProductTypeName());
               nodeEvenementType.setStringValue("isoninternet", "0");
               break;
            }
            default:{
               throw new Exception("Unsupported product type");
            }
         }
         nodeEvenementType.commit();
      }
      nodeObject.createRelation(nodeEvenementType, cloud.getRelationManager("related")).commit();

   }
}
