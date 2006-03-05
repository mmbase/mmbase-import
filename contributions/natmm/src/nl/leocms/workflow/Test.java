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
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import nl.leocms.authorization.AuthorizationHelper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.RemoteContext;
import org.mmbase.bridge.remote.RemoteCloud;
import org.mmbase.bridge.util.xml.Generator;
import org.w3c.dom.Document;

/**
 * @author Edwin van der Elst
 * Date :Oct 7, 2003
 * 
 */
public class Test {

   public static void main(String[] args) throws ParserConfigurationException, FactoryConfigurationError {
      CloudContext context = RemoteContext.getCloudContext("rmi://localhost:4022/staging");
      HashMap info = new HashMap();
      info.put("username", "admin");
      info.put("password", "admin2k");

      Cloud cloud = context.getCloud("mmbase", "name/password", info);

      //      NodeManager manager = cloud.getNodeManager("rubriek");
      //      
      //      NodeList list = manager.getList(null,null,null);
      //      Iterator iterator = list.iterator();
      //      
      //      while (iterator.hasNext()) {
      //         Node n = (Node) iterator.next();
      //         System.out.println( n.getStringValue("naam"));
      //      }

      //      Node evenement = cloud.getNode(155);
//      WorkflowController controller = new WorkflowController(cloud);
//      //      Node node = controller.createFor(evenement,"In bewerking");
//      Node artikel = cloud.getNode(170);
//      controller.createFor( artikel,"in bewerking...");
//      controller.rejectContent( artikel,"bagger");
      
//      Node evenement = cloud.getNode(155);
//      Node wf = controller.getWorkflowNode(evenement);
//      System.out.println("wf=" + wf);
//      Node cr = controller.getCreatieRubriek(evenement);
//      System.out.println("creatierub=" + cr.getStringValue("naam"));
//
//      controller.finishWriting(evenement);
//
//      controller.acceptContent(evenement);
//
//      Node rub = cloud.getNode(172);
      AuthorizationHelper helper = new AuthorizationHelper(cloud);
      Map map = helper.getRolesForUser( cloud.getNode(139));
      System.out.println("Roles :"+map);

//      List list = helper.getPathToRoot(rub);
//      for (int i = 0; i < list.size(); i++) {
//         Node n = (Node) list.get(i);
//         System.out.println("Node: " + n.getNumber());
//      }
//
//      List list2 = helper.getUsersWithRights(rub, 3);
//      for (int i = 0; i < list2.size(); i++) {
//         Node n = (Node) list2.get(i);
//         System.out.println("Node: " + n.getStringValue("account"));
//      }
   }
}
