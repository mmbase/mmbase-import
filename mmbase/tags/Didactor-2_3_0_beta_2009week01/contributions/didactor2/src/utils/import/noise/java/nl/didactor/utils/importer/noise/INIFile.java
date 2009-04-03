package nl.didactor.utils.importer.noise;

import java.io.*;
import java.util.*;

public class INIFile
{
   Properties iniProperty = new Properties();

// public INIFile(File f) { this( f.getPath() ); }
// public INIFile(String fname) throws IOException { loadFile( fname ); }

   public void loadFile( String fname ) throws IOException
   {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));

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

      finally { br.close(); }
   }

   private void addProperty(String section,String line)
   {
      int equalIndex = line.indexOf("=");

      if( equalIndex > 0 )
      {
         String name = section+'.'+line.substring(0,equalIndex).trim();
         String value = line.substring(equalIndex+1).trim();

         iniProperty.put(name,value);
      }
   }

   public String getProperty(String section,String var,String def)
   {
      return iniProperty.getProperty(section+'.'+var,def);
   }

   public int getProperty(String section,String var,int def)
   {
      String sval = getProperty(section,var,Integer.toString(def));
      return Integer.decode(sval).intValue();
   }

   public boolean getProperty(String section,String var,boolean def)
   {
      String sval = getProperty(section,var,def ? "True":"False");
      return sval.equalsIgnoreCase("Yes") || sval.equalsIgnoreCase("True");
   }

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
}

