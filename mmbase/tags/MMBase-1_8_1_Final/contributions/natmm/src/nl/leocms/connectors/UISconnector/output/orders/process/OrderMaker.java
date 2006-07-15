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

import nl.leocms.connectors.UISconnector.output.orders.model.*;
import java.util.Date;
import java.util.regex.*;
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

             NodeList nlPaymentTypes = nodeEvenement.getRelatedNodes("payment_type","related",null);

  			 if(nlPaymentTypes.size()==0) {
				 log.error("There are no related payment_types object for evenement "  + nodeEvenement.getNumber());

			 } else if(nlPaymentTypes.size()>1) {
				 log.error("There is more than one related payment_types for evenement "  + nodeEvenement.getNumber());

			 } else {
				 Node nodePaymentType = nlPaymentTypes.getNode(0);
	             order.setPaymentType(nodePaymentType.getStringValue("naam"));
	             log.info("Setting payment type to "  + nodePaymentType.getStringValue("naam"));
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
			 personalInformation.setBirthDate(new Date(nodeDeelnemers.getLongValue("dayofbirth") * 1000));
			 String sGender = nodeDeelnemers.getStringValue("gender");
			 personalInformation.setGender(sGender.substring(0,1).toUpperCase());
			 personalInformation.setTelephoneNo(nodeDeelnemers.getStringValue("privatephone"));
			 personalInformation.setEmailAddress(nodeDeelnemers.getStringValue("email"));

			 address.setAddressType("P");

			 try{
				String sCompositeHouse = nodeDeelnemers.getStringValue("huisnummer");
				Pattern p = Pattern.compile("[\\D]*$");
				Matcher m = p.matcher(sCompositeHouse);

				if (m.matches())
				{
				   int iHouseEnd = m.start();
				   address.setHouseNumber(new Integer(sCompositeHouse.substring(0, iHouseEnd)).intValue());
				   address.setHouseNumberExtension(sCompositeHouse.substring(iHouseEnd));
				}
				else
				{
				   address.setHouseNumber(new Integer(sCompositeHouse).intValue());
				}
			 } catch(Exception e){
				address.setHouseNumberExtension(nodeDeelnemers.getStringValue("huisnummer"));
			 }

			 address.setStreetName(nodeDeelnemers.getStringValue("straatnaam"));
			 address.setExtraInfo(nodeDeelnemers.getStringValue("lidnummer"));
			 address.setZipCode(nodeDeelnemers.getStringValue("postcode"));
			 address.setCity(nodeDeelnemers.getStringValue("plaatsnaam"));

			 businessInformation.setTelephoneNo("privatphone");
	     }
      }
      catch (Exception e)
      {
          log.error("Could not set customerInformation for inschrijving " + nodeSubscription.getNumber());
      }

      return order;
   }
}

