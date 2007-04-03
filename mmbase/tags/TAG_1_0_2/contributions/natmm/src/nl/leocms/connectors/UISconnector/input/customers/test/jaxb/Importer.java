package nl.leocms.connectors.UISconnector.input.customers.test.jaxb;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nl.leocms.connectors.UISconnector.input.customers.model.*;


public class Importer
{

   // This sample application demonstrates how to unmarshal an instance
   // document into a Java content tree and access data contained within it.

   public static void main(String[] args)
   {
      try
      {
         // create a JAXBContext capable of handling classes generated into
         JAXBContext jc = JAXBContext.newInstance("nl.leocms.connectors.UISconnector.input.customers.model");

         // create an Unmarshaller
         Unmarshaller u = jc.createUnmarshaller();

         // unmarshal a po instance document into a tree of Java content
         // objects composed of classes from the primer.po package.
         CustomerInformation customerInformation = (CustomerInformation) u.unmarshal(new FileInputStream("z:/test.xml"));

         System.out.println(customerInformation.getPersonalInformation().getEmailAddress());
         System.out.println(customerInformation.getAddress().getCity());

      }
      catch (JAXBException je)
      {
         je.printStackTrace();
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
   }
}
