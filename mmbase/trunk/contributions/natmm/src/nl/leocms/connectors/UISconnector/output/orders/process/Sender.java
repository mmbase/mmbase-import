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
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.connectors.UISconnector.output.orders.process;

import org.mmbase.bridge.*;

import nl.leocms.connectors.UISconnector.output.orders.process.OrderMaker;
import nl.leocms.connectors.UISconnector.output.orders.model.*;
import org.w3c.dom.Document;
import nl.leocms.connectors.UISconnector.output.orders.xml.Coder;
import org.mmbase.util.logging.*;
import javax.xml.transform.Transformer;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;


public class Sender extends Thread
{
   private Node nodeSubscription;

   private static final Logger log = Logging.getLoggerInstance(Sender.class);

   public Sender(Node nodeSubscription)
   {
      this.nodeSubscription = nodeSubscription;
   }

   public void run(){
      try{
         Order order = OrderMaker.makeOrder(nodeSubscription);
         Document document = Coder.code(order);

         TransformerFactory tFactory = TransformerFactory.newInstance();

//         Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource("z:/doc.xsl"));
			Transformer transformer = tFactory.newTransformer();

         StringWriter result = new StringWriter();
         transformer.transform(new javax.xml.transform.dom.DOMSource(document),
                               new javax.xml.transform.stream.StreamResult(result));
         System.out.println(result);

         log.info("WSS report for Subscription=" + nodeSubscription.getNumber() + " has been sent seccessfully.");
      }
      catch(Exception e){
         log.error("We has tried to compose an WSS report for Subscription=" + nodeSubscription.getNumber() + ". An error has occured:" + e);
      }
   }
}
