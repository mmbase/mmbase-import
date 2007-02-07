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

import nl.leocms.util.ContentTypeHelper;
import nl.leocms.versioning.PublishManager;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationIterator;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.mmbase.util.CloudFactory;

/**
 * Cleans (deletes) nodes in mmbase when they are expired
 * 
 * @author Nico Klasens (Finalist IT Group)
 * @created 9-dec-2003
 * @version $Revision: 1.1 $
 */
public class CleanerModule extends Module implements Runnable {
	/** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(CleanerModule.class.getName());

	/** The mmbase. */
	private MMBase mmb = null;

   private int interval = 3600 * 1000;
   
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
		// Initialize the module.
      String intervalStr = getInitParameter("interval");
		if (intervalStr == null) {
			 throw new IllegalArgumentException("interval");
		}
      else {
         interval = Integer.parseInt(intervalStr) * 1000;
      }

      // Start thread to wait for mmbase to be up and running.
		Thread cleaner = new Thread(this);
      cleaner.setDaemon(true);
      cleaner.start();
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
			} catch (InterruptedException e) {}
		}
      
      while(true) {
         try {
            Thread.sleep(interval);
         } catch (InterruptedException e) {}
         
         cleanNodes();
      }
	}
   
   private void cleanNodes() {
      try {
         Cloud cloud = CloudFactory.getCloud();         
         String constraints = "verloopdatum < " + (System.currentTimeMillis() / 1000); 
         NodeList contentnodes = cloud.getNodeManager("contentelement").getList(constraints, null, null);
         if (!contentnodes.isEmpty()) {
            NodeIterator ni = contentnodes.nodeIterator();
            while (ni.hasNext()) {
               Node element = ni.nextNode();
               log.info("Deleting expired node (" + element.getNodeManager().getName() + "} " + element.getNumber());
               // delete(true) will delete the relations too. 
               // We have to unlink them first to let staging know
               RelationIterator relations = element.getRelations().relationIterator();
               while (relations.hasNext()) {
                  Relation rel = (Relation) relations.next();
                  PublishManager.unLinkNode(rel);
               }

               if (ContentTypeHelper.isPoll(element)) {
                  element.deleteRelations();
               }
               else {
                  PublishManager.unLinkNode(element);
                  element.delete(true);
               }
            }
         }
      }
      catch (Throwable t) {
         log.error("Clean failed " + t.getMessage());
      }
   }
}