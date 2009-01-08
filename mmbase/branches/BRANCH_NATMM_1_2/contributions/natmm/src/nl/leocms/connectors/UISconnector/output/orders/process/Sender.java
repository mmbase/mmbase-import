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


import org.w3c.dom.Document;
import org.mmbase.util.logging.*;
import javax.xml.transform.*;
import java.net.*;
import java.io.*;

import org.mmbase.bridge.*;

import nl.leocms.connectors.UISconnector.output.orders.xml.Coder;
import nl.leocms.connectors.UISconnector.output.orders.process.OrderMaker;
import nl.leocms.connectors.UISconnector.output.orders.model.*;
import nl.leocms.connectors.UISconnector.UISconfig;


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

//         System.out.println(result);



         log.info("Trying to post the following XML to WSS on " + UISconfig.postOrderUrl());
         log.info(result.toString());

         String sEncodedXML = URLEncoder.encode(result.toString(), "windows-1252");

         URL url = new URL(UISconfig.postOrderUrl());
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setDoOutput(true);
         connection.setRequestMethod("POST");
         connection.connect();

         PrintWriter out = new PrintWriter(connection.getOutputStream());
         out.print("xml=" + sEncodedXML);
         out.flush();
         out.close();

         log.info("Respons on POST by WSS:");
         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String inputLine;
         while ( (inputLine = in.readLine()) != null) {
            log.info(inputLine);
	     }
         in.close();

         log.info("WSS report for Subscription=" + nodeSubscription.getNumber() + " has been sent successfully.");
      }
      catch(Exception e){
         StringWriter writer = new StringWriter();
         PrintWriter pwriter = new PrintWriter(writer);
         e.printStackTrace(pwriter);
         log.error("Tried to compose an WSS report for Subscription=" + nodeSubscription.getNumber() + ". An error has occured:\n" + writer);
      }
   }
}
