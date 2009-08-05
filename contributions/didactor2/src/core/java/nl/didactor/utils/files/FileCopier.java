package nl.didactor.utils.files;


import java.nio.channels.FileChannel;
import java.io.File;
import java.io.RandomAccessFile;

import nl.didactor.utils.files.CommonUtils;


public class FileCopier
{

   public static void fileCopy(String sSourcePath, String sDestinationPath) throws Exception
   {
      fileCopy(new File(sSourcePath), new File(sDestinationPath));
   }


   public static void fileCopy(File fileSource, File fileDestination) throws Exception
   {
       FileChannel c1= new RandomAccessFile(fileSource, "r").getChannel();
       FileChannel c2= new RandomAccessFile(fileDestination, "rw").getChannel();

       long tCount= 0;
       long size= c1.size();
       while( (tCount += c2.transferFrom(c1, 0, size-tCount))<size);

       c1.close();
       c2.force(true);
       c2.close();
   }






   public static void dirCopy(String sSourcePath, String sDestinationPath) throws Exception
   {
      dirCopy(new File(sSourcePath), new File(sDestinationPath));
   }



   public static void dirCopy(File fileSource, File fileDestination) throws Exception
   {
      if(!fileDestination.exists())
      {
         fileDestination.mkdirs();
      }

      String[] arrstrFiles = fileSource.list();
      for(int f = 0; f < arrstrFiles.length; f++)
      {
         File sSubItem = new File(fileSource.getAbsolutePath() + File.separator + arrstrFiles[f]);
         if(sSubItem.isDirectory())
         {
            dirCopy(fileSource.getAbsolutePath() + File.separator + arrstrFiles[f], fileDestination.getAbsolutePath() + File.separator + arrstrFiles[f]);
         }
         else
         {
            fileCopy(fileSource.getAbsolutePath() + File.separator + arrstrFiles[f], fileDestination.getAbsolutePath() + File.separator + arrstrFiles[f]);
         }
      }
   }
}