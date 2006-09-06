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

package nl.leocms.connectors.Exporter;

import org.w3c.dom.Document;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

import org.mmbase.util.logging.*;
import org.mmbase.bridge.Cloud;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.ApplicationHelper;
import nl.leocms.util.ZipUtil;

public class creatingXML{
   private static final Logger log = Logging.getLoggerInstance(creatingXML.class);

   public creatingXML(){

   }

   public void create(Document document, String sBuilderName){
     
     Cloud cloud = CloudFactory.getCloud();
     ApplicationHelper ap = new ApplicationHelper(cloud);
     String tempDir = ap.getTempDir();
     
      log.info("creating " + sBuilderName + ".xml in " + tempDir);
      try
       {
          TransformerFactory tFactory = TransformerFactory.newInstance();

          Transformer transformer = tFactory.newTransformer();

          FileOutputStream fos = new FileOutputStream(tempDir + sBuilderName +
          ".xml");
          transformer.transform(new javax.xml.transform.dom.DOMSource(document),new StreamResult(fos));
          fos.close();

       }

       catch (Exception e)
       {
          log.info(e.toString());
          System.out.println(e.toString());
       }

       ZipUtil zu = new ZipUtil();
       zu.createArchiveFile(tempDir + sBuilderName + ".xml",sBuilderName + ".zip");

   }
}
