package nl.didactor.utils.ini;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class INIFile
{
   private HashMap hmapGroups = new HashMap();


   public void loadFile(String sFileName) throws IOException
   {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sFileName)));

      try
      {
         String section = "";
         String line;

         while( (line = br.readLine())!=null )
         {
            if( line.startsWith(";") ) continue;

            if( line.startsWith("[") )
            {
               section = line.substring(1,line.lastIndexOf("]")).trim();
               continue;
            }

            addProperty(section,line);
         }
      }

      finally
      {
         br.close();
      }
   }





   private void addProperty(String section,String line)
   {
      int equalIndex = line.indexOf("=");

      if( equalIndex > 0 )
      {
         String sKeyName = line.substring(0,equalIndex).trim();
         String sKeyValue = line.substring(equalIndex+1).trim();

         HashMap hmapKeys;
         if(hmapGroups.get(section) == null)
         {//no such group yet
            hmapKeys = new HashMap();
            hmapGroups.put(section, hmapKeys);
         }
         else
         {//adding to existing group
            hmapKeys = (HashMap) hmapGroups.get(section);
         }


         ArrayList arliKeyValues;
         if(hmapKeys.get(sKeyName) == null)
         {//no such key yet
            arliKeyValues = new ArrayList();
            hmapKeys.put(sKeyName, arliKeyValues);
         }
         else
         {//adding to existing key
            arliKeyValues = (ArrayList) hmapKeys.get(sKeyName);
         }

         arliKeyValues.add(sKeyValue);
      }
   }





   public String getProperty(String sSection, String sKey, String sDefault)
   {
      try
      {
         HashMap hmapKeys = (HashMap) hmapGroups.get(sSection);
         ArrayList arliValues = (ArrayList) hmapKeys.get(sKey);
         return (String) arliValues.get(0);
      }
      catch(Exception e)
      {
         return sDefault;
      }
   }

   public int getProperty(String sSection, String sKey, int iDefault)
   {
      try
      {
         HashMap hmapKeys = (HashMap) hmapGroups.get(sSection);
         ArrayList arliValues = (ArrayList) hmapKeys.get(sKey);
         return (new Integer((String) arliValues.get(0))).intValue();
      }
      catch(Exception e)
      {
         return iDefault;
      }
   }

   public boolean getProperty(String sSection, String sKey, boolean bDefault)
   {
      try
      {
         HashMap hmapKeys = (HashMap) hmapGroups.get(sSection);
         ArrayList arliValues = (ArrayList) hmapKeys.get(sKey);
         return (new Boolean((String) arliValues.get(0))).booleanValue();
      }
      catch(Exception e)
      {
         return bDefault;
      }
   }

   public ArrayList getProperties(String sSection, String sKey, ArrayList arliDefault)
   {
      try
      {
         HashMap hmapKeys = (HashMap) hmapGroups.get(sSection);
         ArrayList arliValues = (ArrayList) hmapKeys.get(sKey);
         return arliValues;
      }
      catch(Exception e)
      {
         return arliDefault;
      }
   }






/*
   public ArrayList getAllKeysNames_for_SelectedGroup(String sGroup)
   {
      ArrayList arliKeysNames = new ArrayList();

      for( Enumeration e = iniProperty.propertyNames(); e.hasMoreElements(); )
      {
         String sKey = (String) e.nextElement();
         String[] arstrKey = sKey.split("\\.");
         if (arstrKey[0].equals(sGroup))
         {
            arliKeysNames.add(arstrKey[1]);
         }
      }

      return arliKeysNames;
   }






   public HashMap getAllKeysValues_for_SelectedGroup(String sGroup)
   {
      HashMap hsmapAllKeysValues = new HashMap();
      ArrayList arliKeysNames = this.getAllKeysNames_for_SelectedGroup(sGroup);
      for(Iterator it = arliKeysNames.iterator(); it.hasNext();)
      {
         String sKeyName = (String) it.next();
         hsmapAllKeysValues.put(sKeyName, iniProperty.getProperty(sGroup + "." + sKeyName));
      }
      return hsmapAllKeysValues;
   }
*/



}

