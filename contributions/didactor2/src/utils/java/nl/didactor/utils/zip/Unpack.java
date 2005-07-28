package nl.didactor.utils.zip;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;

public class Unpack
{


   public static Exception unzipFileToFolder(String sZipFilePath, String sDestinationPath)
   {
      int BUFFER = 10240;
      String sDestinationPathReady = new String(sDestinationPath);
      char chLastChar = sDestinationPath.charAt(sDestinationPath.length() - 1);
      if((chLastChar != '/') || (chLastChar != '\\'))
      {
         sDestinationPathReady += File.separator;
      }


      if(File.separator.equals("\\"))
      {//Something like Windows
         sDestinationPathReady = sDestinationPathReady.replaceAll("/",  File.separator + File.separator);
      }
      else
      {//Something like Unix
         sDestinationPathReady = sDestinationPathReady.replaceAll("\\\\",  File.separator);
      }

      try
      {
         ZipFile zipFile = new ZipFile(sZipFilePath);
         Enumeration entries = zipFile.entries();


         while(entries.hasMoreElements())
         {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            InputStream zis = zipFile.getInputStream(entry);
            byte data[] = new byte[BUFFER];


            String dataFile = entry.getName();
            String sPath = sDestinationPathReady + dataFile;
            if(File.separator.equals("\\"))
            {//Something like Windows
               sPath = sPath.replaceAll("/",  File.separator + File.separator);
            }
            else
            {//Something like Unix
               sDestinationPathReady = sDestinationPathReady.replaceAll("\\\\",  File.separator);
            }


            if(entry.isDirectory())
            {//We have to create a directory
               File file = new File(sPath);
               file.mkdirs();
            }
            else
            {//Usual file
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
         return null;
      }
      catch(Exception e)
      {
         return e;
      }
   }
}