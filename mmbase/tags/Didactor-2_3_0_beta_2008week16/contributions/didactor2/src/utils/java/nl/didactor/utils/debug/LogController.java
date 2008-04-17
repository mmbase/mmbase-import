package nl.didactor.utils.debug;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;

import nl.didactor.utils.ini.INIFile;


public class LogController
{
   private static INIFile iniFile = null;

   private static HashMap hmapIDs = new HashMap();
   private static HashMap hmapNames = new HashMap();



   public static void setContext(ServletContext sc)
   {
      try
      {
//       String sLocalPath = new File("/WEB-INF").getAbsolutePath();
         String sLocalPath = sc.getRealPath("/WEB-INF");
         iniFile = new INIFile();
         iniFile.loadFile(sLocalPath + File.separator + "log_controller.ini");

      }
      catch (IOException e)
      {
      }
   }



/*
   public static LogController getInstance()
   {
      return _logController;
   }
*/




   public static boolean showLogs(String sDeveloperID)
   {
      if(hmapNames.containsKey(sDeveloperID))
      {//Check for cached value
         return ((Boolean) hmapNames.get(sDeveloperID)).booleanValue();
      }

      try
      {
         int f = 0;
         while(true)
         {
            String sLogs = iniFile.getProperty("developer_" + f, "logs", null);
            if(sLogs == null)
            {//if there is no key "logs" this means "no more developers"
               break;
            }
            ArrayList arliAliases = iniFile.getProperties("developer_" + f, "alias", null);

            if(arliAliases.contains(sDeveloperID))
            {//we've found the alias
               if(sLogs.equals("on"))
               {
                  hmapNames.put(sDeveloperID, new Boolean(true));
                  return true;
               }
               else
               {
                  break;
               }
            }

            f++;
         }
      }
      catch (Exception ex)
      {
      }

      hmapNames.put(sDeveloperID, new Boolean(false));
      return false;
   }




   public static boolean showLogs(int iDeveloperID)
   {
      if(hmapIDs.containsKey(new Integer(iDeveloperID)))
      {//Check for cached value
         return ((Boolean) hmapIDs.get(new Integer(iDeveloperID))).booleanValue();
      }

      try
      {
         if(iniFile.getProperty("developer_" + iDeveloperID, "logs", "off").equals("on"))
         {
            hmapIDs.put(new Integer(iDeveloperID), new Boolean(true));
            return true;
         }
      }
      catch (Exception ex)
      {
      }
      hmapIDs.put(new Integer(iDeveloperID), new Boolean(false));
      return false;
   }
}
