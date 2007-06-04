package nl.didactor.utils.files;

import java.io.File;
import nl.didactor.utils.files.CommonUtils;


public class FileEraser
{
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