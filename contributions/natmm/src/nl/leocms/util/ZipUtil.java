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
import java.util.zip.*;
import java.util.*;
import nl.leocms.util.tools.HtmlCleaner;
import java.io.*;

public class ZipUtil{
  
   private static final Logger log = Logging.getLoggerInstance(ZipUtil.class);

   public ZipUtil(){

   }

   public Vector unZip(String zipFileName, String zipDestination) {
      int BUFFER = 2048;
      Vector dataFiles = new Vector();
      try {
          ZipFile zFile = new ZipFile(zipFileName);
          Enumeration entries = zFile.entries();
          while(entries.hasMoreElements()) { // *** write the files to disk ***
              try {
                  int count;
                  byte data[] = new byte[BUFFER];
    
                  ZipEntry entry = (ZipEntry) entries.nextElement();
                  InputStream zis = zFile.getInputStream(entry);
    
                  String dataFile = entry.getName();
                  if(dataFile.indexOf("/")>-1) dataFile = dataFile.substring(dataFile.lastIndexOf("/")+1);
                  dataFile = HtmlCleaner.stripText(dataFile);
                  dataFiles.add(dataFile);
    
                  BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(zipDestination + dataFile), BUFFER);
                  while ((count = zis.read(data)) > 0) {
                      dest.write(data, 0, count);
                  }
                  dest.flush();
                  dest.close();
                  zis.close();
              } catch (Exception e) { log.info("UnZip " + e); }
          }
          zFile.close();
      } catch(Exception e) {
          log.info("UnZip " + e);
      }
      return dataFiles;
   }
   
   void createGZFile(String sRootDir, String sFileName) {
      log.info("createGZFile " + sFileName);
      try {
         File f = new File(sRootDir + "/" + sFileName);
         int bytesIn = 0;
         byte[] readBuffer = new byte[4096];
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
            sRootDir + "/" + sFileName + ".gz"));
         FileInputStream fis = new FileInputStream(f);
         ZipEntry anEntry = new ZipEntry(sFileName);
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
   
   public void createArchiveFile(String sFileName, String sArchiveName) {
      //by default archive is created in the folder where archiving file is.
      //sFileName should contain path to the file and it's name, sArchiveName -
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


   public void createArchiveFiles(String sFileName, String sArchiveName, String[] sDirs) {
      //adds to archive file with name sFileName and files from directories
      //listed in sDir

      //by default archive is creating in the folder where archiving file is.
      //sFileName should contain path to the file and it's name, sArchiveName -
      //only archive file name
      int iLastSlashIndex = sFileName.lastIndexOf("/");
      String sPath = sFileName.substring(0,iLastSlashIndex + 1);
      log.info("creating archive file " + sArchiveName + " in " + sPath + " folder");
      try {
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
            sPath + sArchiveName));

         treating(zos, new File(sFileName));

         for (int i = 0; i < sDirs.length; i++){
            File fDir = new File(sDirs[i]);
            File[] files = fDir.listFiles();
            for (int j = 0; j < files.length; j++) {
               treating(zos, files[j]);
            }
            fDir.delete();
         }

         zos.close();

      } catch (Exception e){
         log.info(e.toString());
      }

   }

   void treating(ZipOutputStream zos, File f){
      int bytesIn = 0;
      byte[] readBuffer = new byte[4096];
      try {
         FileInputStream fis = new FileInputStream(f);
         ZipEntry anEntry = new ZipEntry(f.getName());
         zos.putNextEntry(anEntry);
         while ( (bytesIn = fis.read(readBuffer)) != -1) {
            zos.write(readBuffer, 0, bytesIn);
         }
         fis.close();
         f.delete();
      }
      catch (Exception e){
         log.info(e.toString());
      }

   }

}
