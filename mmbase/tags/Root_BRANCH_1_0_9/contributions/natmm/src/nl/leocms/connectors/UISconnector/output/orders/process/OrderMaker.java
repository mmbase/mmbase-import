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
package nl.leocms.connectors.UISconnector.output.orders.process;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.connectors.UISconnector.output.orders.model.*;
import java.util.*;
import java.io.*;


public class OrderMaker
{


   private static final Logger log = Logging.getLoggerInstance(OrderMaker.class);

   public static Order makeOrder(Node nodeSubscription)
   {

      log.info("Creating new order for inschrijving " + nodeSubscription.getNumber());

      Order order = new Order();

      CustomerInformation customerInformation = new CustomerInformation();
      order.setCustomerInformation(customerInformation);

      PersonalInformation personalInformation = new PersonalInformation();
      BusinessInformation businessInformation = new BusinessInformation();
      CommonInformation commonInformation = new CommonInformation();
      Address address = new Address();

      customerInformation.setPersonalInformation(personalInformation);
      customerInformation.setBusinessInformation(businessInformation);
      customerInformation.setCommonInformation(commonInformation);
      customerInformation.setAddress(address);

      int bankAccount = nodeSubscription.getIntValue("bank_of_gironummer");
      commonInformation.setBankAccount( (bankAccount!=-1 ? "" + bankAccount : "") );

      order.setOrderId(nodeSubscription.getNumber());
      order.setExtraInformation(nodeSubscription.getStringValue("description"));

      try
      {

        NodeList nlEvenement = nodeSubscription.getRelatedNodes("evenement","posrel",null);

        if(nlEvenement.size()==0) {
          log.error("There are no related evenement object for inschrijving "  + nodeSubscription.getNumber());

        } else if(nlEvenement.size()>1) {
          log.error("There is more than one evenement object for inschrijving "  + nodeSubscription.getNumber());

        } else {

           Node nodeEvenement = nlEvenement.getNode(0);
           order.setExternId(nodeEvenement.getStringValue("externid"));
           if(nodeEvenement.getStringValue("externid").equals("")) {
              log.error("There is no externid for " + nodeEvenement.getStringValue("number") + ". Probably this isn't an event imported from UIS.");
           }

           // get the payment_type.externid for inschrijvingen.betaalwijze
           Cloud cloud = CloudFactory.getCloud();
           NodeManager nmPaymentTypes = cloud.getNodeManager("payment_type");
           NodeList nlPaymentTypes = nmPaymentTypes.getList("naam='" + nodeSubscription.getStringValue("betaalwijze") + "'",null,null);

           if(nlPaymentTypes.size()==0) {
               log.error("There is no payment_type that matches '" + nodeSubscription.getStringValue("betaalwijze")
                  + "' for subscription "  + nodeSubscription.getNumber());

            } else {
               Node nodePaymentType = nlPaymentTypes.getNode(0);
               order.setPaymentType(nodePaymentType.getStringValue("externid"));
               log.info("Setting payment type to "  + nodePaymentType.getStringValue("externid"));
            }

            // get the media.externid for inschrijvingen.bron
            NodeManager nmMedia = cloud.getNodeManager("media");
            NodeList nlMedia = nmMedia.getList("naam='" + nodeSubscription.getStringValue("source") + "'",null,null);

            if(nlMedia.size()==0) {
               log.error("There is no acquisition id that matches '" + nodeSubscription.getStringValue("source")
                 + "' for subscription "  + nodeSubscription.getNumber());

            } else {
               Node nodeMedia = nlMedia.getNode(0);
               order.setAcquisitionId(nodeMedia.getStringValue("externid"));
               log.info("Setting acquisition id to "  + nodeMedia.getStringValue("externid"));
            }

         }
      }
      catch (Exception e)
      {
        log.error("Could not set externid for inschrijving " + nodeSubscription.getNumber());
      }

      try
      {
        NodeList nlDeelnemers = nodeSubscription.getRelatedNodes("deelnemers","posrel",null);

        if(nlDeelnemers.size()==0) {

          log.error("There are no related deelnemer object for inschrijving "  + nodeSubscription.getNumber());

        } else if(nlDeelnemers.size()>1) {

          log.error("There is more than one deelnemer object for inschrijving "  + nodeSubscription.getNumber());

        } else {

          Node nodeDeelnemers = nlDeelnemers.getNode(0);

          order.setQuantity(nodeDeelnemers.getIntValue("bron"));

          personalInformation.setInitials(nodeDeelnemers.getStringValue("initials"));
          personalInformation.setFirstName(nodeDeelnemers.getStringValue("firstname"));
          personalInformation.setSuffix(nodeDeelnemers.getStringValue("suffix"));
          personalInformation.setLastName(nodeDeelnemers.getStringValue("lastname"));
          // TODO check if dayofbirth is meaningfull
          Calendar cal = Calendar.getInstance();
          cal.set(1900,0,1);
          personalInformation.setBirthDate(cal.getTime());
          // personalInformation.setBirthDate(new Date(nodeDeelnemers.getLongValue("dayofbirth") * 1000));
          String sGender = nodeDeelnemers.getStringValue("gender");
          personalInformation.setGender(sGender.substring(0,1).toUpperCase());
          personalInformation.setTelephoneNo(nodeDeelnemers.getStringValue("privatephone"));
          personalInformation.setEmailAddress(nodeDeelnemers.getStringValue("email"));

          address.setAddressType("P");

           String sCompositeNumber = nodeDeelnemers.getStringValue("huisnummer");
           int i = 0;
           while (   i < sCompositeNumber.length()
                    && ('0'<=sCompositeNumber.charAt(i))&&(sCompositeNumber.charAt(i)<='9')) {
             i++;
           }
           if(i>0) {
              address.setHouseNumber(new Integer(sCompositeNumber.substring(0, i)).intValue());
           }
           if(i<sCompositeNumber.length()) {
             address.setHouseNumberExtension(sCompositeNumber.substring(i).trim());
           }

          address.setStreetName(nodeDeelnemers.getStringValue("straatnaam"));
          address.setExtraInfo(nodeDeelnemers.getStringValue("lidnummer"));
          address.setZipCode(nodeDeelnemers.getStringValue("postcode"));
          address.setCity(nodeDeelnemers.getStringValue("plaatsnaam"));

          businessInformation.setTelephoneNo("");
        }
      }
      catch (Exception e)
      {
          log.error("Could not set customerInformation for inschrijving " + nodeSubscription.getNumber());
      }

      return order;
   }
}

