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

import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.util.PropertiesUtil;
import nl.leocms.util.PublishUtil;

/**
 * Module that publish all contentpaginas
 *
 * @author Ronald Kramp
 * @version $Revision  $
 */
public class DefaultContentPagesModule extends Module implements Runnable {
   /** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(DefaultContentPagesModule.class.getName());

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
	 * Also read for the config parameters expiretime and signaleringperiod from de module.xml
	 */
	public void init() {
		mmb = (MMBase) Module.getModule("MMBASEROOT");
		// Initialize the module.
		// Start thread to wait for mmbase to be up and running.
		thread = new Thread(this);
      thread.setDaemon(true);
      thread.start();
	}

   /*
    * The thread in which the singaleringen will be added and removed
    */
   private void publishDefaultContentPages() {
      // first wait for one minute
      try {
			Thread.sleep(60000);
         while (!mmb.hasStarted()) {
            // not started, sleep another minute
            Thread.sleep(60000);
         }
		}
		catch (InterruptedException e) {}
      log.info("defaultContentPages started");
      Cloud cloud = CloudFactory.getCloud();

      String publishdefaultcontent = PropertiesUtil.getProperty("publishdefaultcontent");
      if ("".equals(publishdefaultcontent) || "false".equals(publishdefaultcontent)) {

         log.info("defaultContentPages starts publishing pages");

         NodeManager paginaManager = cloud.getNodeManager("pagina");
         NodeList nodeList = paginaManager.getList(null,null,null);
         NodeIterator nodeIterator = nodeList.nodeIterator();
         while (nodeIterator.hasNext()) {
            Node pagina = nodeIterator.nextNode();
            PublishUtil.PublishOrUpdateNode( pagina);
            // Relaties naar rubriek publiceren
            RelationList list = pagina.getRelations("posrel","rubriek");
            publishRelations(list);
            list = pagina.getRelations("gebruikt","paginatemplate");
            publishRelations(list);
            // TODO: Deze werkt nog niet???
            list = pagina.getRelations("related","menutemplate");
            publishRelations(list);
         }

         PublishUtil.PublishOrUpdateNode(cloud.getNode("leocms.colofon"));
         PublishUtil.PublishOrUpdateNode(cloud.getNode("leocms.disclaimer"));
//         PublishUtil.PublishOrUpdateNode(cloud.getNode("leeuwarden.contact"));

//         ArrayList contentTypeList = ApplicationHelper.getContentTypes();
//         for (int i = 0; i < contentTypeList.size(); i++) {
//            String contentType = (String) contentTypeList.get(i);
//            try {
//               Node paginaNode = cloud.getNodeByAlias("contentpagina." + contentType);
//               PublishUtil.PublishOrUpdateNode(paginaNode);
//            }
//            catch (NotFoundException nfe) {
//               // just skip
//            }
//         }
         PropertiesUtil.setProperty("publishdefaultcontent", "true");
      }
   }

   private static void publishRelations(RelationList list) {
      RelationIterator relIter = list.relationIterator();
      while (relIter.hasNext()) {
         Relation relation = relIter.nextRelation();
         PublishUtil.PublishOrUpdateNode(relation);
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
		publishDefaultContentPages();
	}
}