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

import nl.leocms.connectors.UISconnector.output.orders.model.*;
import java.util.Date;
import java.util.regex.*;
import java.io.*;


public class OrderMaker
{
   public static Order makeOrder(Node nodeSubscription)
   {

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
         Node nodeEvenement = nodeSubscription.getRelatedNodes("evenement").getNode(0);
         order.setExternId(nodeEvenement.getStringValue("externid"));
      }
      catch (Exception e)
      {
      }

      try
      {
         Node nodeDeelnemers = nodeSubscription.getRelatedNodes("deelnemers").getNode(0);
         order.setQuantity(nodeDeelnemers.getIntValue("bron"));

         personalInformation.setInitials(nodeDeelnemers.getStringValue("initials"));
         personalInformation.setFirstName(nodeDeelnemers.getStringValue("firstname"));
         personalInformation.setSuffix(nodeDeelnemers.getStringValue("suffix"));
         personalInformation.setLastName(nodeDeelnemers.getStringValue("lastname"));
         personalInformation.setBirthDate(new Date(nodeDeelnemers.getLongValue("dayofbirth") * 1000));
         personalInformation.setGender(nodeDeelnemers.getStringValue("gender"));
         personalInformation.setTelephoneNo(nodeDeelnemers.getStringValue("privatephone"));
         personalInformation.setEmailAddress(nodeDeelnemers.getStringValue("email"));

         System.out.println(nodeDeelnemers.getStringValue("dayofbirth"));

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
         }catch(Exception e){
            address.setHouseNumberExtension(nodeDeelnemers.getStringValue("huisnummer"));
         }

         address.setStreetName(nodeDeelnemers.getStringValue("straatnaam"));
         address.setExtraInfo(nodeDeelnemers.getStringValue("lidnummer"));
         address.setZipCode(nodeDeelnemers.getStringValue("postcode"));
         address.setCity(nodeDeelnemers.getStringValue("plaatsnaam"));

         businessInformation.setTelephoneNo("privatphone");
      }
      catch (Exception e)
      {
      }

      return order;
   }
}

