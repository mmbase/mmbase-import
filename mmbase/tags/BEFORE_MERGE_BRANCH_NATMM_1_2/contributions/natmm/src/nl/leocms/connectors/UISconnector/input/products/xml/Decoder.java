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
import org.mmbase.util.logging.*;

import org.w3c.dom.*;

import nl.leocms.connectors.UISconnector.input.products.model.*;


public class Decoder
{

   private static final Logger log = Logging.getLoggerInstance(Decoder.class);

   private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   public static ArrayList decode(Document document) {

      ArrayList arliResult = new ArrayList();

      if (document == null) {
         log.error("document is null");

      }
      else {

         Element elemRoot = document.getDocumentElement();
         NodeList nlProducts = elemRoot.getChildNodes();

         for (int f = 0; f < nlProducts.getLength(); f++) {
            Node nodeProduct = nlProducts.item(f);

            if ("product".equals(nodeProduct.getNodeName())) {

               Product product = new Product();
               arliResult.add(product);

               NodeList nlProductData = nodeProduct.getChildNodes();

               for (int g = 0; g < nlProductData.getLength(); g++) {
                  Node nodeProductDataItem = nlProductData.item(g);

                  try {
                     if ("id".equals(nodeProductDataItem.getNodeName())) {
                        product.setExternID(nodeProductDataItem.getFirstChild().getNodeValue());
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.externid");
                  }

                  try {
                     if ("description".equals(nodeProductDataItem.getNodeName())) {
                        product.setDescription(nodeProductDataItem.getFirstChild().getNodeValue());
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.description");
                  }

                  try {
                     if ("price".equals(nodeProductDataItem.getNodeName())) {
                        product.setPrice(new Double(nodeProductDataItem.getFirstChild().getNodeValue()).doubleValue());
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.price");
                  }

                  try {
                     if ("embargoDate".equals(nodeProductDataItem.getNodeName())) {
                        product.setEmbargoDate(df.parse(nodeProductDataItem.getFirstChild().getNodeValue()));
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.embargodate");
                  }

                  try {
                     if ("expireDate".equals(nodeProductDataItem.getNodeName())) {
                        product.setExpireDate(df.parse(nodeProductDataItem.getFirstChild().getNodeValue()));
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.expiredate");
                  }

                  try {
                     if ("membershipRequired".equals(nodeProductDataItem.getNodeName())) {
                        product.setMembershipRequired(new Boolean(nodeProductDataItem.getFirstChild().getNodeValue()).booleanValue());
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.membershiprequired");
                  }

                  try {
                     if ("productType".equals(nodeProductDataItem.getNodeName())) {
                        String sProductType = nodeProductDataItem.getFirstChild().getNodeValue();

                        if ("event".equals(sProductType)) {
                           product.setProductType(Product.PRODUCT_TYPE_EVENT);
                        }
                        else if ("product".equals(sProductType)) {
                           product.setProductType(Product.PRODUCT_TYPE_ITEM);
                        }
                        else if ("subscription".equals(sProductType)) {
                           product.setProductType(Product.PRODUCT_TYPE_SUBSCRIPTION);
                        }
                        else {
                           log.error("Unsupported productType=" + sProductType);
                           throw new Exception("Unsupported productType=" + sProductType);
                        }

                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set product.membershiprequired");
                  }

                  try {
                     if ("paymentTypes".equals(nodeProductDataItem.getNodeName())) {
                        ArrayList arliPaymentTypes = new ArrayList();
                        product.setPaymentTypes(arliPaymentTypes);

                        NodeList nlPaymentTypes = nodeProductDataItem.getChildNodes();

                        for (int i = 0; i < nlPaymentTypes.getLength(); i++) {
                           Node nodePaymentType = nlPaymentTypes.item(i);

                           if ("paymentType".equals(nodePaymentType.getNodeName())) {

                              PaymentType paymentType = new PaymentType();
                              arliPaymentTypes.add(paymentType);

                              NodeList nlPaymentTypeSubnodes = nodePaymentType.getChildNodes();
                              for (int j = 0; j < nlPaymentTypeSubnodes.getLength(); j++) {
                                 Node nlPaymentTypeSubnode = nlPaymentTypeSubnodes.item(j);

                                 if ("id".equals(nlPaymentTypeSubnode.getNodeName())) {
                                    paymentType.setId(nlPaymentTypeSubnode.getFirstChild().getNodeValue());
                                 }
                                 if ("desciption".equals(nlPaymentTypeSubnode.getNodeName())) {
                                    paymentType.setDescription(nlPaymentTypeSubnode.getFirstChild().getNodeValue());
                                 }
                              }
                           }
                        }

                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set payment types");
                  }

                  try {
                     if ("propertyList".equals(nodeProductDataItem.getNodeName())) {
                        product.setProperties(nl.leocms.connectors.UISconnector.shared.properties.xml.Decoder.decode(nodeProductDataItem));
                     }
                  }
                  catch (Exception ex) {
                     log.error("could not set properties");
                  }
               }
            }
         }
      }
      return arliResult;
   }
}

