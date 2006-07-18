package nl.leocms.connectors.UISconnector.input.customers.process;

import nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformation;
import javax.xml.bind.*;
import java.io.*;

public class JaxBUnmarshaller
{
   public static CustomerInformation Unmarshaller(InputStream is) throws Exception{
      // create a JAXBContext capable of handling classes generated into
      // the primer.po package

      JAXBContext jc = JAXBContext.newInstance("nl.leocms.connectors.UISconnector.input.customers.model");

      // create an Unmarshaller
      Unmarshaller u = jc.createUnmarshaller();

      // unmarshal a po instance document into a tree of Java content
      // objects composed of classes from the primer.po package.
      CustomerInformation customerInformation = (CustomerInformation) u.unmarshal(is);

      return customerInformation;
   }
}
