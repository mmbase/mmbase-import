package nl.didactor.utils.zip;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;

import nl.didactor.utils.files.CommonUtils;

public class Unpack
{


   public static void unzipFileToFolder(String sZipFilePath, String sDestinationPath) throws Exception
   {
      int BUFFER = 10240;
      String sDestinationPathReady = new String(sDestinationPath);
      char chLastChar = sDestinationPath.charAt(sDestinationPath.length() - 1);
      if ( (chLastChar != '/') || (chLastChar != '\\'))
      {
         sDestinationPathReady += File.separator;
      }

      sDestinationPathReady = CommonUtils.fixPath(sDestinationPathReady);
      ZipFile zipFile = new ZipFile(sZipFilePath);


      //At the begining the have to create all directories
      Enumeration entries = zipFile.entries();
      while (entries.hasMoreElements())
      {
         ZipEntry entry = (ZipEntry) entries.nextElement();

         String dataFile = entry.getName();
         String sPath = sDestinationPathReady + dataFile;
         sPath = CommonUtils.fixPath(sPath);

         if (entry.isDirectory())
         { //We have to create a directory
            File file = new File(sPath);
            file.mkdirs();
         }
      }

      //Now we can extract files, all subdirs have been extracted already
      entries = zipFile.entries();
      while (entries.hasMoreElements())
      {
         ZipEntry entry = (ZipEntry) entries.nextElement();
         InputStream zis = zipFile.getInputStream(entry);
         byte data[] = new byte[BUFFER];

         String dataFile = entry.getName();
         String sPath = sDestinationPathReady + dataFile;
         sPath = CommonUtils.fixPath(sPath);

         if (!entry.isDirectory())
         { //Usual file

            String[] arrstrPathSegments;
            if (File.separator.equals("\\"))
            { //Something like Windows
               arrstrPathSegments =  sPath.split("\\\\");
            }
            else
            { //Something like Unix
               arrstrPathSegments =  sPath.split("/");
            }
            String sPathOnlyDir = sPath.substring(0, sPath.length() - arrstrPathSegments[arrstrPathSegments.length - 1].length());
            File fileOnlyDir = new File(sPathOnlyDir);
            if(!fileOnlyDir.exists())
            {
               fileOnlyDir.mkdirs();
            }

            int count;
            BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(sDestinationPathReady + dataFile), BUFFER);
            while ( (count = zis.read(data)) > 0)
            {
               dest.write(data, 0, count);
            }
            zis.close();
            dest.flush();
            dest.close();
         }
      }
   }






   public static void deleteFolderIncludeSubfolders(String sPath, boolean bDeleteOnExit)
   {
      File fileFolder = new File(CommonUtils.fixPath(sPath));
      String[] arrstrFiles = fileFolder.list();
      for(int f = 0; f < arrstrFiles.length; f++)
      {
         File sSubItem = new File(CommonUtils.fixPath(sPath) + File.separator + arrstrFiles[f]);
         if(sSubItem.isDirectory())
         {
            deleteFolderIncludeSubfolders(sSubItem.getAbsolutePath(), bDeleteOnExit);
            if(bDeleteOnExit) sSubItem.deleteOnExit();
               else sSubItem.delete();
         }
         else
         {
            if(bDeleteOnExit) sSubItem.deleteOnExit();
               else sSubItem.delete();
         }
      }
      if(bDeleteOnExit) fileFolder.deleteOnExit();
         else fileFolder.delete();
   }
}