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
package nl.leocms.util;

import org.mmbase.util.logging.*;
import nl.leocms.util.*;
import java.util.zip.*;
import java.io.*;

public class ZipUtil{
   private static final Logger log = Logging.getLoggerInstance(ZipUtil.class);

   public ZipUtil(){

   }

   public void createArchiveFile(String sFileName, String sArchiveName) {
      //by default archive is creating in the folder where archiving file is.
      //sFileName should containt path to the file and it's name, sArchiveName -
      //only archive file name
      int iLastSlashIndex = sFileName.lastIndexOf("/");
      String sPath = sFileName.substring(0,iLastSlashIndex + 1);
      String sRealFileName = sFileName.substring(iLastSlashIndex + 1);
      log.info("creating archive file " + sArchiveName + " in " + sPath + " folder");
      try {
         File f = new File(sFileName);
         int bytesIn = 0;
         byte[] readBuffer = new byte[4096];
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
            sPath + sArchiveName));
         FileInputStream fis = new FileInputStream(f);
         ZipEntry anEntry = new ZipEntry(sRealFileName);
         zos.putNextEntry(anEntry);
         while ( (bytesIn = fis.read(readBuffer)) != -1) {
            zos.write(readBuffer, 0, bytesIn);
         }
         fis.close();
         zos.close();
         f.delete();
      } catch (Exception e){
         log.info(e.toString());
      }

   }



}
