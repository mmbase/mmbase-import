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
package nl.leocms.module;

import org.mmbase.bridge.*;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;

import java.net.URL;
import java.io.IOException;
import java.net.URLConnection;

import com.finalist.mmbase.util.CloudFactory;

/**
 * 
 * @author Ronald Kramp
 * @version $Revision  $
 */
public class LinkValidationModule extends Module implements Runnable {
   /** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(LinkValidationModule.class.getName());
	
   //Object sync;
   private static Thread thread = null;
   
	/** The mmbase instance */
	private MMBase mmb = null;
   
   /**
	 * @see org.mmbase.module.Module#onload()
	 */
	public void onload() {
	}
   
   /**
	 * @see org.mmbase.module.Module#init()
	 */
	public void init() {
	   mmb = (MMBase) Module.getModule("MMBASEROOT");
		// Start thread to wait for mmbase to be up and running.
		thread = new Thread(this);
      thread.setDaemon(true);
      thread.start();
	}
   
   /*
    * The thread in which the external links will be checked
    */
   private void checkExternalLinks() {
      log.info("LinkValidation thread started");
      
      while (true) {
         try {
            // wait for mmbase to put in default data
            Thread.sleep(60 * 60 * 1000); // om het uur kijken of er links bij zijn die niet meer goed zijn
         } 
         catch (InterruptedException e) {
            log.warn("Interupted while sleeping , continuing");
         }
         
         Cloud cloud = CloudFactory.getCloud();

	      NodeList linkElements = cloud.getNodeManager("link").getList("[type]='extern'", "titel", null);
  	   
   	   // only 1 emailtempalte maybe active
   	   if (linkElements.size() > 0) {
   	      for (int i = 0; i < linkElements.size(); i++) {
   	         Node linkNode = linkElements.getNode(i);
   	         String url = linkNode.getStringValue("url");
               
               try {
                  URLConnection urlConnection = (new URL(url)).openConnection();
                  urlConnection.connect();
                  linkNode.setBooleanValue("valid", true);
               }
               catch (IOException ioe) {
                  log.debug("Invalid url: " + url + ", " + ioe);
                  linkNode.setBooleanValue("valid", false);
               }
               linkNode.commit();
            }
         }
      }
   }
   
  
   /**
	 * Wait for mmbase to be up and running,
	 * then execute the tests.
	 */
	public void run() {
		// Wait for mmbase to be up & running.
		while (!mmb.getState()) {
			try {
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) {}
		}
		checkExternalLinks();
	}
}