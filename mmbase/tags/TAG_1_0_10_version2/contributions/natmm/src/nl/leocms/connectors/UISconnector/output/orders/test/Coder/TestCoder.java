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
package nl.leocms.connectors.UISconnector.output.orders.test.Coder;

import org.w3c.dom.*;
import javax.xml.transform.*;
import java.io.*;
import javax.xml.parsers.*;
import java.net.*;

import nl.leocms.connectors.UISconnector.output.orders.model.Order;
import java.util.Date;
import nl.leocms.connectors.UISconnector.output.orders.model.*;




/**
 * Test class for XML encoder
 * It contains fake data
 */


public class TestCoder
{

   public static void main(String[] args) throws Exception
   {
      Order order = new Order();
      order.setOrderId(0);
      order.setQuantity(1);
      order.setPaymentType("you can't buy it!!!");
      order.setOrderDate(new Date());
      order.setExternId("there is no any");
      order.setAcquisitionId("what is it?");

      CustomerInformation customerInformation = new CustomerInformation();
      order.setCustomerInformation(customerInformation);

      PersonalInformation personalInformation = new PersonalInformation();
      personalInformation.setInitials("A");
      personalInformation.setFirstName("B");
      personalInformation.setSuffix("C");
      personalInformation.setLastName("D");
      personalInformation.setBirthDate(new Date());
      personalInformation.setTelephoneNo("E");
      personalInformation.setEmailAddress("F");
      personalInformation.setGender("G");
      customerInformation.setPersonalInformation(personalInformation);



      BusinessInformation businessInformation = new BusinessInformation();
      businessInformation.setCompanyName("AA");
      businessInformation.setEmailAddress("BB");
      businessInformation.setTelephoneNo("CC");
      businessInformation.setVatNo("DD");
      customerInformation.setBusinessInformation(businessInformation);


      CommonInformation commonInformation = new CommonInformation();
      commonInformation.setBankAccount("384573985");
      customerInformation.setCommonInformation(commonInformation);


      Address address = new Address();
      address.setAddressType("AAA");
      address.setCity("BBB");
      address.setCountryId("CCC");
      address.setExtraInfo("DDD");
      address.setHouseNumber(99);
      address.setHouseNumberExtension("EEE");
      address.setStreetName("FFF");
      address.setZipCode("GGG");
      customerInformation.setAddress(address);


      Document document = null;

      try
      {
         document = nl.leocms.connectors.UISconnector.output.orders.xml.Coder.code(order);
      }
      catch (Exception e)
      {
         System.out.println(e);
      }

//      System.out.println(document.getDocumentElement());




/*
            URL url = new URL("file:///Z:/getProducts.jsp.xml");
            URLConnection connection = url.openConnection();

            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

            try
            {
               DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
               DocumentBuilder dbuilder = dfactory.newDocumentBuilder();

               InputStream is = new BufferedInputStream(in);
               document = dbuilder.parse(is);

            }
            catch (Exception e){
               System.out.println(e);
            }
      System.out.println(document.getDocumentElement());
*/



      try
      {
         TransformerFactory tFactory = TransformerFactory.newInstance();

         // Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource("z:/doc.xsl"));
         Transformer transformer = tFactory.newTransformer();

         StringWriter result = new StringWriter();
         transformer.transform(new javax.xml.transform.dom.DOMSource(document),
                               new javax.xml.transform.stream.StreamResult(result));
         System.out.println(result);

         //         transformer.transform(new javax.xml.transform.dom.DOMSource(document),
         //            new javax.xml.transform.stream.StreamResult( new FileOutputStream("z:/doc.html")));

         //         transformer.transform(new javax.xml.transform.stream.StreamSource("z:/doc.xml"),
         //                               new javax.xml.transform.stream.StreamResult( new FileOutputStream("z:/doc.html")));
      }

      catch (Exception e)
      {
         System.out.println(e);
      }
   }
}
