package nl.leocms.connectors.UISconnector.input.customers.process;

import java.util.*;
import java.text.SimpleDateFormat;
import org.mmbase.bridge.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.connectors.UISconnector.input.customers.model.*;
import nl.leocms.connectors.UISconnector.shared.properties.process.PropertyUtil;

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


	  List listProperties = customerInformation.getPropertyList().getProperty();
	  PropertyUtil.setProperties(cloud, nodeDeelnemers, listProperties);

   }
}
