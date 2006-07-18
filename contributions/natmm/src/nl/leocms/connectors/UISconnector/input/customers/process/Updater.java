package nl.leocms.connectors.UISconnector.input.customers.process;

import java.util.*;
import java.text.SimpleDateFormat;


import org.mmbase.bridge.*;
import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.connectors.UISconnector.input.customers.model.*;



public class Updater
{
   private static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

   public static void update(CustomerInformation customerInformation) throws Exception
   {
      String sExternID = customerInformation.getCommonInformation().getCustomerId();

      if (sExternID == null)
      {
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
      if (nl.size() > 0)
      {
         nodeDeelnemers = cloud.getNode(nl.getNode(0).getStringValue("deelnemers.number"));
      }
      else
      {
         nodeDeelnemers = cloud.getNodeManager("deelnemers").createNode();
         nodeDeelnemers.setStringValue("externid", sExternID);
         nodeDeelnemers.commit();
      }

      nodeDeelnemers.setStringValue("initials", customerInformation.getPersonalInformation().getInitials());
      nodeDeelnemers.setStringValue("firstname", customerInformation.getPersonalInformation().getFirstName());
      nodeDeelnemers.setStringValue("suffix", customerInformation.getPersonalInformation().getSuffix());
      nodeDeelnemers.setStringValue("lastname", customerInformation.getPersonalInformation().getLastName());
      nodeDeelnemers.setLongValue("dayofbirth", df.parse(customerInformation.getPersonalInformation().getBirthDate()).getTime() / 1000);
      nodeDeelnemers.setStringValue("gender", customerInformation.getPersonalInformation().getGender());
      nodeDeelnemers.setStringValue("privatephone", customerInformation.getPersonalInformation().getTelephoneNo());
      nodeDeelnemers.setStringValue("email", customerInformation.getPersonalInformation().getEmailAddress());

      nodeDeelnemers.setStringValue("companyphone", customerInformation.getBusinessInformation().getTelephoneNo());

      nodeDeelnemers.setStringValue("huisnummer", customerInformation.getAddress().getHouseNumber() + "-" + customerInformation.getAddress().getHouseNumberExtension());
      nodeDeelnemers.setStringValue("straatnaam", customerInformation.getAddress().getStreetName());
      nodeDeelnemers.setStringValue("lidnummer", customerInformation.getAddress().getExtraInfo());
      nodeDeelnemers.setStringValue("postcode", customerInformation.getAddress().getZipCode());
      nodeDeelnemers.setStringValue("plaatsnaam", customerInformation.getAddress().getCity());
      nodeDeelnemers.commit();





      //Delete old values
      nl = cloud.getList("",
                         "deelnemers,pools,topics",
                         "deelnemers.number,topics.number",
                         "deelnemers.externid='" + sExternID + "'",
                         null, null, null, true);

      for (int f = 0; f < nl.size(); f++)
      {
         Node nodeTopic = cloud.getNode(nl.getNode(f).getStringValue("topics.number"));

         NodeList nl2 = cloud.getList("" + nodeTopic.getNumber(),
                                      "topics,pools,contentelement",
                                      "topics.number,contentelement.number",
                                      "contentelement.number!='" + nodeDeelnemers.getNumber() + "'",
                                      null, null, null, true);
         if (nl2.size() == 0)
         {
            nodeTopic.delete(true);
         }
      }

      //Put new values
      List listProperties = customerInformation.getPropertyList().getProperty();
      for (Iterator it = listProperties.iterator(); it.hasNext(); )
      {
         Property property = (Property) it.next();

         Node nodeTopic = cloud.getNodeManager("topics").createNode();
         nodeTopic.setStringValue("externid", property.getPropertyId());
         nodeTopic.setStringValue("title", property.getPropertyDescription());
         nodeTopic.commit();

         //Create pools
         for (Iterator it2 = property.getPropertyValue().iterator(); it2.hasNext(); )
         {
            PropertyValue propertyValue = (PropertyValue) it2.next();

            NodeList nl2 = cloud.getList("",
                                         "pools",
                                         "pools.number,pools.externid",
                                         "pools.externid='" + propertyValue.getPropertyValueId() + "'",
                                         null, null, null, true);
            Node nodePool;
            if (nl2.size() == 0)
            {
               nodePool = cloud.getNodeManager("pools").createNode();
               nodePool.setStringValue("externid", propertyValue.getPropertyValueId());
               nodePool.setStringValue("name", propertyValue.getPropertyValueDescription());
               nodePool.commit();

               nodeDeelnemers.createRelation(nodePool, cloud.getRelationManager("posrel")).commit();
            }
            else
            {
               nodePool = cloud.getNode(nl2.getNode(0).getStringValue("pools.number"));
            }

            nodeTopic.createRelation(nodePool, cloud.getRelationManager("posrel")).commit();
         }
      }
   }
}
