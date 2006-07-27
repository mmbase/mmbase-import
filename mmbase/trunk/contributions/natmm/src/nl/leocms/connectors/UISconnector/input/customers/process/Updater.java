package nl.leocms.connectors.UISconnector.input.customers.process;

import java.util.*;
import java.text.SimpleDateFormat;
import org.mmbase.bridge.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.connectors.UISconnector.input.customers.model.*;
import nl.leocms.connectors.UISconnector.shared.properties.process.PropertyUtil;
import org.mmbase.util.logging.*;

public class Updater
{
   private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   private static final Logger log = Logging.getLoggerInstance(Updater.class);


   public static String update(CustomerInformation customerInformation) throws Exception {
      String sExternID = customerInformation.getCommonInformation().getCustomerId();

      if (sExternID == null) {
         throw new Exception("Can't parse customer without externId");
      }

      Cloud cloud = CloudFactory.getCloud();

      //Looks for a deelnemers node
      NodeList nl = cloud.getList("",
                                  "deelnemers",
                                  "deelnemers.number",
                                  "deelnemers.externid='" + sExternID + "'",
                                  null, null, null, true);

      Node nodeDeelnemers;
      if (nl.size() > 0) {
         nodeDeelnemers = cloud.getNode(nl.getNode(0).getStringValue("deelnemers.number"));
      }
      else {
         nodeDeelnemers = cloud.getNodeManager("deelnemers").createNode();
         nodeDeelnemers.setStringValue("externid", sExternID);
         nodeDeelnemers.commit();
      }

      nodeDeelnemers.setStringValue("initials", customerInformation.getPersonalInformation().getInitials());
      nodeDeelnemers.setStringValue("firstname", customerInformation.getPersonalInformation().getFirstName());
      nodeDeelnemers.setStringValue("suffix", customerInformation.getPersonalInformation().getSuffix());
      nodeDeelnemers.setStringValue("lastname", customerInformation.getPersonalInformation().getLastName());
      try{
         nodeDeelnemers.setLongValue("dayofbirth", df.parse(customerInformation.getPersonalInformation().getBirthDate()).getTime() / 1000);
      }
      catch(Exception e){
         log.warn("The date in the element <customerInformation> -> <personalInformation> -> <dayofbirth> has got wrong format(" + customerInformation.getPersonalInformation().getBirthDate() + ")");
      }
      nodeDeelnemers.setStringValue("gender", customerInformation.getPersonalInformation().getGender());
      nodeDeelnemers.setStringValue("privatephone", customerInformation.getPersonalInformation().getTelephoneNo());
      nodeDeelnemers.setStringValue("email", customerInformation.getPersonalInformation().getEmailAddress());
      nodeDeelnemers.setStringValue("fax", customerInformation.getPersonalInformation().getFaxNo());

      nodeDeelnemers.setStringValue("companyphone", customerInformation.getBusinessInformation().getTelephoneNo());

      nodeDeelnemers.setStringValue("huisnummer", customerInformation.getAddress().getHouseNumber() + "-" + customerInformation.getAddress().getHouseNumberExtension());
      nodeDeelnemers.setStringValue("straatnaam", customerInformation.getAddress().getStreetName());
      nodeDeelnemers.setStringValue("lidnummer", customerInformation.getAddress().getExtraInfo());
      nodeDeelnemers.setStringValue("postcode", customerInformation.getAddress().getZipCode());
      nodeDeelnemers.setStringValue("plaatsnaam", customerInformation.getAddress().getCity());
      nodeDeelnemers.setStringValue("lidnummer", customerInformation.getCommonInformation().getIsMember());
      nodeDeelnemers.commit();


      List listProperties = customerInformation.getPropertyList().getProperty();
      ArrayList arliStandartPropertis = new ArrayList();


      for (Iterator it = listProperties.iterator(); it.hasNext(); ) {
         nl.leocms.connectors.UISconnector.input.customers.model.Property property = (nl.leocms.connectors.UISconnector.input.customers.model.Property) it.next();
         nl.leocms.connectors.UISconnector.shared.properties.model.Property standartProperty = new nl.leocms.connectors.UISconnector.shared.properties.model.Property();
         standartProperty.setPropertyId(property.getPropertyId());
         standartProperty.setPropertyDescription(property.getPropertyDescription());

         ArrayList arliStandartPropertyValues = new ArrayList();
         standartProperty.setPropertyValues(arliStandartPropertyValues);

         for(Iterator it2 = property.getPropertyValue().iterator(); it2.hasNext();){
            nl.leocms.connectors.UISconnector.input.customers.model.PropertyValue propertyValue = (nl.leocms.connectors.UISconnector.input.customers.model.PropertyValue) it2.next();
            nl.leocms.connectors.UISconnector.shared.properties.model.PropertyValue standartPropertyValue = new nl.leocms.connectors.UISconnector.shared.properties.model.PropertyValue();

            standartPropertyValue.setPropertyValueId(propertyValue.getPropertyValueId());
            standartPropertyValue.setPropertyValueDescription(propertyValue.getPropertyValueDescription());
            arliStandartPropertyValues.add(standartPropertyValue);
         }
         arliStandartPropertis.add(standartProperty);
      }
      PropertyUtil.setProperties(cloud, nodeDeelnemers, arliStandartPropertis);

      return "" + nodeDeelnemers.getNumber();
   }
}

