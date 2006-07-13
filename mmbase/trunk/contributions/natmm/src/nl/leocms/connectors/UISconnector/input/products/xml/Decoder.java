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
package nl.leocms.connectors.UISconnector.input.products.xml;

import java.util.ArrayList;
import java.text.SimpleDateFormat;

import org.w3c.dom.*;

import nl.leocms.connectors.UISconnector.input.products.model.*;
import nl.leocms.connectors.UISconnector.shared.model.*;



public class Decoder
{
   private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");


   public static ArrayList decode(Document document){
      ArrayList arliResult = new ArrayList();

      Element elemRoot = document.getDocumentElement();
      NodeList nlProducts = elemRoot.getChildNodes();


      for(int f = 0; f < nlProducts.getLength(); f++){
         Node nodeProduct = nlProducts.item(f);

         if("product".equals(nodeProduct.getNodeName())){

            Product product = new Product();
            arliResult.add(product);

            NodeList nlProductData = nodeProduct.getChildNodes();

            for (int g = 0; g < nlProductData.getLength(); g++)
            {
               Node nodeProductDataItem = nlProductData.item(g);

               try{
                  if ("id".equals(nodeProductDataItem.getNodeName()))
                  {
                     product.setExternID(nodeProductDataItem.getFirstChild().getNodeValue());
                  }
               }
               catch(Exception ex){
               }

               try{
                  if ("price".equals(nodeProductDataItem.getNodeName()))
                  {
                     product.setPrice(new Double(nodeProductDataItem.getFirstChild().getNodeValue()).doubleValue());
                  }
               }
               catch(Exception ex){
               }

               try{
                  if ("embargoDate".equals(nodeProductDataItem.getNodeName()))
                  {
                     product.setEmbargoDate(df.parse(nodeProductDataItem.getFirstChild().getNodeValue()));
                  }
               }
               catch(Exception ex){
               }

               try{
                  if ("expireDate".equals(nodeProductDataItem.getNodeName()))
                  {
                     product.setExpireDate(df.parse(nodeProductDataItem.getFirstChild().getNodeValue()));
                  }
               }
               catch(Exception ex){
               }


               try{
                  if ("membershipRequired".equals(nodeProductDataItem.getNodeName()))
                  {
                     product.setMembershipRequired( new Boolean(nodeProductDataItem.getFirstChild().getNodeValue()).booleanValue());
                  }
               }
               catch(Exception ex){
               }


               try{
                  if ("paymentTypes".equals(nodeProductDataItem.getNodeName()))
                  {
                     ArrayList arliPaymentTypes = new ArrayList();
                     product.setPaymentTypes(arliPaymentTypes);

                     NodeList nlPaymentTypes = nodeProductDataItem.getChildNodes();


                     for(int i = 0; i < nlPaymentTypes.getLength(); i++){
                        Node nodePaymentType = nlPaymentTypes.item(i);

                        if ("paymentType".equals(nodePaymentType.getNodeName())){

                           PaymentType paymentType = new PaymentType();
                           arliPaymentTypes.add(paymentType);

                           NodeList nlPaymentTypeSubnodes = nodePaymentType.getChildNodes();
                           for (int j = 0; j < nlPaymentTypeSubnodes.getLength(); j++)
                           {
                              Node nlPaymentTypeSubnode = nlPaymentTypeSubnodes.item(j);

                              if ("id".equals(nlPaymentTypeSubnode.getNodeName()))
                              {
                                 paymentType.setId(nlPaymentTypeSubnode.getFirstChild().getNodeValue());
                              }
                              if ("desciption".equals(nlPaymentTypeSubnode.getNodeName()))
                              {
                                 paymentType.setDescription(nlPaymentTypeSubnode.getFirstChild().getNodeValue());
                              }
                           }
                        }
                     }

                  }
               }
               catch(Exception ex){
               }


               try{
                  if ("propertyList".equals(nodeProductDataItem.getNodeName()))
                  {
                     ArrayList arliProperties = new ArrayList();
                     product.setProperties(arliProperties);

                     NodeList nlPropertiesNodes = nodeProductDataItem.getChildNodes();


                     for(int i = 0; i < nlPropertiesNodes.getLength(); i++){
                        Node nodeProperty = nlPropertiesNodes.item(i);

                        if ("property".equals(nodeProperty.getNodeName())){
                           Property property = new Property();
                           arliProperties.add(property);


                           NodeList nlPropertyInnerNodes = nodeProperty.getChildNodes();
                           for (int j = 0; j < nlPropertyInnerNodes.getLength(); j++)
                           {
                              Node nodePropertyInnerNode = nlPropertyInnerNodes.item(j);

                              ArrayList arliPropertyValues = new ArrayList();
                              property.setPropertyValues(arliPropertyValues);

                              if ("propertyId".equals(nodePropertyInnerNode.getNodeName()))
                              {
                                 property.setPropertyId(nodePropertyInnerNode.getFirstChild().getNodeValue());
                              }
                              if ("propertyDescription".equals(nodePropertyInnerNode.getNodeName()))
                              {
                                 property.setPropertyDescription(nodePropertyInnerNode.getFirstChild().getNodeValue());
                              }
                              if ("propertyValue".equals(nodePropertyInnerNode.getNodeName()))
                              {
                                 PropertyValue propertyValue = new PropertyValue();
                                 arliPropertyValues.add(propertyValue);
                                 System.out.println(arliPropertyValues.size());

                                 NodeList nlPropertyValueInnerNodes = nodePropertyInnerNode.getChildNodes();
                                 for(int k = 0; k < nlPropertyValueInnerNodes.getLength(); k++){
                                    Node nodePropertyValueInner = nlPropertyValueInnerNodes.item(k);

                                    if("propertyId".equals(nodePropertyValueInner.getNodeName())){
                                       propertyValue.setPropertyValueId(nodePropertyValueInner.getFirstChild().getNodeValue());
                                    }
                                    if("propertyDescription".equals(nodePropertyValueInner.getNodeName())){
                                       propertyValue.setPropertyValueDescription(nodePropertyValueInner.getFirstChild().getNodeValue());
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
               catch(Exception ex){
               }
            }
         }
      }

      return arliResult;
   }
}
