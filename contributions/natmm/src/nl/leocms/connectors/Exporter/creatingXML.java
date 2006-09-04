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
import nl.leocms.applications.*;
import nl.leocms.util.ZipUtil;

public class creatingXML{
   private static final Logger log = Logging.getLoggerInstance(creatingXML.class);

   public creatingXML(){

   }

   public void create(Document document, String sBuilderName){
      log.info("creating " + sBuilderName + ".xml in " + NMIntraConfig.tempDir);
      try
       {
          TransformerFactory tFactory = TransformerFactory.newInstance();

          Transformer transformer = tFactory.newTransformer();

          FileOutputStream fos = new FileOutputStream(NMIntraConfig.tempDir + sBuilderName +
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
       zu.createArchiveFile(NMIntraConfig.tempDir + sBuilderName + ".xml",sBuilderName + ".zip");

   }
}
