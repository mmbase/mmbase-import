package nl.didactor.utils.files;

import java.io.File;

public class CommonUtils
{
   public static String fixPath(String sPath)
   {
      if (File.separator.equals("\\"))
      { //Something like Windows
         return sPath.replaceAll("/", File.separator + File.separator);
      }
      else
      { //Something like Unix
         return sPath.replaceAll("\\\\", File.separator);
      }

   }
}