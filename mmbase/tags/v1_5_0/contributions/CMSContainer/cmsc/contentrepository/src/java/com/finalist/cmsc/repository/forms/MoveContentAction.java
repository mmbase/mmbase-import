/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.*;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.services.workflow.Workflow;

public class MoveContentAction extends MMBaseAction {

   private static final String PARAMETER_CHANNEL = "parentchannel";

   private static final String PARAMETER_NUMBER = "objectnumber";

   private static final String PARAMETER_DIRECTION = "direction";

   private static final String DIRECTION_DOWN = "down";

   private static final String DIRECTION_UP = "up";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      Locale locale = request.getLocale();

      int number = Integer.parseInt(request.getParameter(PARAMETER_NUMBER));
      String direction = request.getParameter(PARAMETER_DIRECTION);
      int channel = Integer.parseInt(request.getParameter(PARAMETER_CHANNEL));

      int swappedNodenumber = -1;
      if (direction.equals(DIRECTION_UP)) {
         swappedNodenumber = moveUp(number, channel);
      }
      if (direction.equals(DIRECTION_DOWN)) {
         swappedNodenumber = moveDown(number, channel);
      }

      if (swappedNodenumber > 0) {
         MessageResources resources = getResources(request, "REPOSITORY");
         Node channelNode = cloud.getNode(channel);
         Node elementNode = cloud.getNode(number);
         Node swappedNode = cloud.getNode(swappedNodenumber);

         String remark = resources.getMessage(locale, "content.move.workflow.message", elementNode.getNodeManager()
               .getName(), elementNode.getStringValue("title"));
         List<Node> nodes = new ArrayList<Node>();
         nodes.add(elementNode);
         nodes.add(swappedNode);
         Workflow.create(channelNode, remark, nodes);
      }

      String path = mapping.findForward(SUCCESS).getPath() + "?" + PARAMETER_CHANNEL + "=" + channel
            + "&direction=down";
      return new ActionForward(path);
   }


   private int moveDown(int number, int channel) {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList nodeList = cloud.getList("" + channel, "contentchannel,contentrel,contentelement",
            "contentrel.number,contentrel.pos,contentelement.number", null, "contentrel.pos", "down", null, false);

      return swap(nodeList, number);
   }


   private int moveUp(int number, int channel) {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList nodeList = cloud.getList("" + channel, "contentchannel,contentrel,contentelement",
            "contentrel.number,contentrel.pos,contentelement.number", null, "contentrel.pos", "up", null, false);

      return swap(nodeList, number);
   }


   private int swap(NodeList nodeList, int number) {
      for (NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
         Node node = ni.nextNode();
         int nodeNumber = node.getIntValue("contentelement.number");
         if (nodeNumber == number) {
            if (ni.hasNext()) { // it could already be the last item in the
                                 // list
               Node nextNode = ni.nextNode();
               swap(nextNode.getIntValue("contentrel.number"), node.getIntValue("contentrel.number"));
               return nextNode.getIntValue("contentelement.number");
            }
            break; // (out of the for loop)
         }
      }
      return -1;
   }


   private void swap(int posrelNumber1, int posrelNumber2) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node posrelNode1 = cloud.getNode(posrelNumber1);
      Node posrelNode2 = cloud.getNode(posrelNumber2);
      int oldPosrel1Pos = posrelNode1.getIntValue("pos");
      posrelNode1.setIntValue("pos", posrelNode2.getIntValue("pos"));
      posrelNode1.commit();
      posrelNode2.setIntValue("pos", oldPosrel1Pos);
      posrelNode2.commit();
   }

}
