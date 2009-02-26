package com.finalist.cmsc.directreaction.taglib;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;

import com.finalist.cmsc.directreaction.util.Reaction;
import com.finalist.cmsc.services.publish.Publish;

/**
 * The GetReactionTag will retrieve a single reaction node from the live
 * database and then populate and return a Reaction object.
 *
 * @author jderuijter
 */
public class GetReactionTag extends SimpleTagSupport {

   private int number;
   private String var;


   @Override
   public void doTag() {
      Cloud remoteCloud = getLiveCloud();
      Node node = remoteCloud.getNode(number);

      Reaction reaction = new Reaction(node.getIntValue("number"), node.getStringValue("title"), node
            .getStringValue("body"), node.getStringValue("name"), node.getStringValue("email"), node
            .getDateValue("creationdate"), this.getRelatedContentTitle());

      getJspContext().setAttribute(var, reaction);
   }


   public Cloud getLiveCloud() {
      CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = cloudProvider.getCloud();
      Cloud remoteCloud = Publish.getRemoteCloud(cloud);
      return remoteCloud;
   }


   public String getRelatedContentTitle() {
      String contentTitle = null;
      NodeList nodeList = getLiveCloud().getList("" + number, "reaction,contentelement", "contentelement.title", null,
            null, null, null, true);

      for (NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         contentTitle = node.getStringValue("contentelement.title");
      }
      return contentTitle;
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setVar(String var) {
      this.var = var;
   }

}
