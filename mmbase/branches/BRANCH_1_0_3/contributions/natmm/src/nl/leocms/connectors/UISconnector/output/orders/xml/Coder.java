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
package nl.leocms.connectors.UISconnector.output.orders.xml;

import nl.leocms.connectors.UISconnector.output.orders.model.Order;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.text.SimpleDateFormat;
import nl.leocms.connectors.UISconnector.output.orders.model.*;

public class Coder
{

   private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
   private static SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");

   public Coder()
   {
   }

   public static Document code(Order order) throws Exception
   {

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
/*
      domFactory.setValidating(false);
      domFactory.setCoalescing(true);
      domFactory.setExpandEntityReferences(true);
      domFactory.setIgnoringElementContentWhitespace(true);
*/
      DocumentBuilder domBuilder = domFactory.newDocumentBuilder();


      Document document = domBuilder.newDocument();
      Element elemRoot = document.createElement("order");
      document.appendChild(elemRoot);

      Element elemProductID = document.createElement("productId");
      elemProductID.appendChild(document.createTextNode(order.getExternId()));
      elemRoot.appendChild(elemProductID);

      Element elemCustomerInformation1 = document.createElement("customerInformation1");
      Element elemCustomerInformation2 = document.createElement("customerInformation2");
      elemRoot.appendChild(elemCustomerInformation1);
      elemRoot.appendChild(elemCustomerInformation2);


      Element elemQuantity = document.createElement("quantity");
      elemQuantity.appendChild(document.createTextNode("" + order.getQuantity()));
      elemRoot.appendChild(elemQuantity);


      Element elemAcquisitionId = document.createElement("acquisitionId");
      elemAcquisitionId.appendChild(document.createTextNode(order.getAcquisitionId()));
      elemRoot.appendChild(elemAcquisitionId);


      Element elemOrderDate = document.createElement("orderDate");
      elemOrderDate.appendChild(document.createTextNode(df.format(order.getOrderDate())));
      elemRoot.appendChild(elemOrderDate);


      Element elemOrderId = document.createElement("orderId");
      elemOrderId.appendChild(document.createTextNode("" + order.getOrderId()));
      elemRoot.appendChild(elemOrderId);


      Element elemPaymentType = document.createElement("paymentType");
      elemPaymentType.appendChild(document.createTextNode(order.getPaymentType()));
      elemRoot.appendChild(elemPaymentType);


      Element elemExtraInformation = document.createElement("extraInformation");
      elemExtraInformation.appendChild(document.createTextNode(order.getExtraInformation()));
      elemRoot.appendChild(elemExtraInformation);


      Element elemPersonalInformation = document.createElement("personalInformation");
      elemCustomerInformation1.appendChild(elemPersonalInformation);

      Element elemBusinessInformation = document.createElement("businessInformation");
      elemCustomerInformation1.appendChild(elemBusinessInformation);

      Element elemCommonInformation = document.createElement("commonInformation");
      elemCustomerInformation1.appendChild(elemCommonInformation);

      Element elemAddress = document.createElement("address");
      elemCustomerInformation1.appendChild(elemAddress);




      CustomerInformation custumerInformation = order.getCustomerInformation();
      PersonalInformation personalInformation = custumerInformation.getPersonalInformation();
      {
         Element elemInitials = document.createElement("initials");
         elemInitials.appendChild(document.createTextNode(personalInformation.getInitials()));
         elemPersonalInformation.appendChild(elemInitials);

         Element elemFirstName = document.createElement("firstName");
         elemFirstName.appendChild(document.createTextNode(personalInformation.getFirstName()));
         elemPersonalInformation.appendChild(elemFirstName);

         Element elemSuffix = document.createElement("suffix");
         elemSuffix.appendChild(document.createTextNode(personalInformation.getSuffix()));
         elemPersonalInformation.appendChild(elemSuffix);

         Element elemLastName = document.createElement("lastName");
         elemLastName.appendChild(document.createTextNode(personalInformation.getLastName()));
         elemPersonalInformation.appendChild(elemLastName);

         Element elemBirthDate = document.createElement("birthDate");
         elemBirthDate.appendChild(document.createTextNode(df2.format(personalInformation.getBirthDate())));
         elemPersonalInformation.appendChild(elemBirthDate);

         Element elemGender = document.createElement("gender");
         elemGender.appendChild(document.createTextNode(personalInformation.getGender()));
         elemPersonalInformation.appendChild(elemGender);

         Element elemTelephoneNo = document.createElement("telephoneNo");
         elemTelephoneNo.appendChild(document.createTextNode(personalInformation.getTelephoneNo()));
         elemPersonalInformation.appendChild(elemTelephoneNo);

         Element elemEmailAddress = document.createElement("emailAddress");
         elemEmailAddress.appendChild(document.createTextNode(personalInformation.getEmailAddress()));
         elemPersonalInformation.appendChild(elemEmailAddress);
      }



      BusinessInformation businessInformation = custumerInformation.getBusinessInformation();
      {
         Element elemCompanyName = document.createElement("companyName");
         elemCompanyName.appendChild(document.createTextNode(businessInformation.getCompanyName()));
         elemBusinessInformation.appendChild(elemCompanyName);

         Element elemVatNo = document.createElement("vatNo");
         elemVatNo.appendChild(document.createTextNode(businessInformation.getVatNo()));
         elemBusinessInformation.appendChild(elemVatNo);

         Element elemTelephoneNo = document.createElement("telephoneNo");
         elemTelephoneNo.appendChild(document.createTextNode(businessInformation.getTelephoneNo()));
         elemBusinessInformation.appendChild(elemTelephoneNo);

         Element elemEmailAddress = document.createElement("emailAddress");
         elemEmailAddress.appendChild(document.createTextNode(businessInformation.getEmailAddress()));
         elemBusinessInformation.appendChild(elemEmailAddress);
      }


      CommonInformation commonInformation = custumerInformation.getCommonInformation();
      {
         Element elemBankAccount = document.createElement("bankAccount");
         elemBankAccount.appendChild(document.createTextNode("" + commonInformation.getBankAccount()));
         elemCommonInformation.appendChild(elemBankAccount);

         Element elemGiroAccount = document.createElement("giroAccount");
         elemGiroAccount.appendChild(document.createTextNode(""));
         elemCommonInformation.appendChild(elemGiroAccount);

      }

      Address address = custumerInformation.getAddress();
      {
         Element elemAddressType = document.createElement("addressType");
         elemAddressType.appendChild(document.createTextNode(address.getAddressType()));
         elemAddress.appendChild(elemAddressType);

         Element elemCountryId = document.createElement("countryId");
         elemCountryId.appendChild(document.createTextNode(address.getCountryId()));
         elemAddress.appendChild(elemCountryId);

         Element elemHouseNumber = document.createElement("houseNumber");
         elemHouseNumber.appendChild(document.createTextNode("" + address.getHouseNumber()));
         elemAddress.appendChild(elemHouseNumber);

         Element elemHouseNumberExtension = document.createElement("houseNumberExtension");
         elemHouseNumberExtension.appendChild(document.createTextNode(address.getHouseNumberExtension()));
         elemAddress.appendChild(elemHouseNumberExtension);

         Element elemStreetName = document.createElement("streetName");
         elemStreetName.appendChild(document.createTextNode(address.getStreetName()));
         elemAddress.appendChild(elemStreetName);

         Element elemExtraInfo = document.createElement("extraInfo");
         elemExtraInfo.appendChild(document.createTextNode(address.getExtraInfo()));
         elemAddress.appendChild(elemExtraInfo);

         Element elemZipCode = document.createElement("zipCode");
         elemZipCode.appendChild(document.createTextNode(address.getZipCode()));
         elemAddress.appendChild(elemZipCode);

         Element elemCity = document.createElement("city");
         elemCity.appendChild(document.createTextNode(address.getCity()));
         elemAddress.appendChild(elemCity);
      }


      return document;
   }
}

