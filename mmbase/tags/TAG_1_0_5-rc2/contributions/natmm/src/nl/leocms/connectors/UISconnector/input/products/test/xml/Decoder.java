package nl.leocms.connectors.UISconnector.input.products.test.xml;

import java.net.*;
import java.io.*;
import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.*;
import nl.leocms.connectors.UISconnector.input.products.model.*;
import nl.leocms.connectors.UISconnector.shared.properties.model.*;
import nl.leocms.connectors.UISconnector.UISconfig;


public class Decoder
{
   public Decoder()
   {
   }

   public static void main(String[] args)
   {
      try
      {
         URL url = new URL(UISconfig.getProductUrl());
         URLConnection connection = url.openConnection();

         BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

         DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dbuilder = dfactory.newDocumentBuilder();

         InputStream is = new BufferedInputStream(in);
         Document document = dbuilder.parse(is);

         ArrayList arliPOJOs = nl.leocms.connectors.UISconnector.input.products.xml.Decoder.decode(document);

         for (Iterator it = arliPOJOs.iterator(); it.hasNext(); ){
            Product product = (Product) it.next();

            System.out.println("externid=" + product.getExternID());
            System.out.println("price=" + product.getPrice());
            System.out.println("embargo=" + product.getEmbargoDate());
            System.out.println("expire=" + product.getExpireDate());
            System.out.println("membership=" + product.isMembershipRequired());

            for(Iterator it2 = product.getPaymentTypes().iterator(); it2.hasNext();){
               PaymentType paymentType = (PaymentType) it2.next();
               System.out.println("payment_type=" + paymentType.getId() + " - " + "payment_description=" + paymentType.getDescription());
            }

            for(Iterator it3 = product.getProperties().iterator(); it3.hasNext();){
               Property property = (Property) it3.next();
               System.out.println("=========");
               System.out.println("property_id=" + property.getPropertyId());
               System.out.println("property_description=" + property.getPropertyDescription());

               for(Iterator it4 = property.getPropertyValues().iterator(); it4.hasNext();){
                  PropertyValue propertyValue = (PropertyValue) it4.next();

                  System.out.println("property_value_id=" + propertyValue.getPropertyValueId());
                  System.out.println("property_value_description=" + propertyValue.getPropertyValueDescription());
               }
            }

         }

      }
      catch (Exception e)
      {
         System.out.println(e);
      }
   }
}
