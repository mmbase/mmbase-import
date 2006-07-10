package nl.leocms.builders;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.util.logging.*;
import nl.leocms.evenementen.Evenement;
import nl.leocms.connectors.UISconnector.UISconfig;
import nl.leocms.connectors.UISconnector.output.orders.process.Sender;


public class ContentInschrijvingen extends HtmlBuilder {

   /** MMbase logging system */
   protected static Logger log = Logging.getLoggerInstance(ContentInschrijvingen.class.getName());

   public boolean commit(MMObjectNode node) {

      Cloud cloud = CloudFactory.getCloud();
      // *** setting inschrijvingen.users to list of users numbers ***
      NodeIterator iNodes= cloud.getList(node.getStringValue("number")
              , "inschrijvingen,schrijver,users"
              , "users.number", null, null, null, null, false).nodeIterator();

      String sRelatedUsers = ",";
      if(iNodes.hasNext()) {
         Node nextNode = iNodes.nextNode();
         sRelatedUsers += nextNode.getIntValue("users.number") + ",";
      }

      if(sRelatedUsers.equals(",")) { sRelatedUsers = ",-1,"; }
      node.setValue("users",sRelatedUsers);

      boolean bSuperCommit = super.commit(node);

      if(UISconfig.IS_ACTIVE) {
			Sender sender = new Sender(cloud.getNode(node.getNumber()));
			sender.run();
		}

      return bSuperCommit;
   }
}
