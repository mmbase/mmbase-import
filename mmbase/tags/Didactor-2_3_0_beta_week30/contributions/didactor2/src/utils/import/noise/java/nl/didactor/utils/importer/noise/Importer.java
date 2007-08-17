package nl.didactor.utils.importer.noise;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.io.RandomAccessFile;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import nl.didactor.utils.importer.noise.People;
import nl.didactor.utils.importer.noise.INIFile;
import nl.didactor.utils.importer.noise.IFileConstants;


import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;


public class Importer
{
  
   private static Logger log = Logging.getLoggerInstance(Importer.class);

   public static void main(String[] args) throws Exception
   {  
      //.ini file
      String sLocalPath = null;
      if(args==null) {
        sLocalPath = new File (".").getCanonicalPath ();
      } else {
        sLocalPath = args[0];
      }
      INIFile iniFile = new INIFile();
      log.info("reading ini file " + sLocalPath + IFileConstants.PATH_SEPARATOR + "config.ini");
      iniFile.loadFile(sLocalPath + IFileConstants.PATH_SEPARATOR + "config.ini");
      
      //Access to the cloud
      HashMap user = new HashMap();
      user.put("username","admin");
      user.put("password","admin2k");

      Cloud cloud = null;
      try
      {
         log.info("trying remote cloud");
         CloudContext cloudContext = ContextProvider.getCloudContext(iniFile.getProperty("Options", "rmi", null));
         cloud = cloudContext.getCloud("mmbase");
      }
      catch(Exception ex1)
      {
        try {
          
          log.info("trying locale cloud");
          cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase","name/password",user);
          
        } catch(Exception ex2) {
        
          log.error("Could not connect to a local or remote cloud");
          log.error(ex2.toString());
          System.exit(1);
        }
      }


      for(int f = 0; f < 1000; f++)
      {//Main cycle for reading [File_xxx] groups
         String sPath = iniFile.getProperty("File_" + f, "path", null);
         String sType = iniFile.getProperty("File_" + f, "type", null);
         log.info("reading " + sPath + " of type " + sType);
         
         //If there are no any of these parameters
         if((sPath == null) || (sType == null)) break;

         log.info("Task " + f + " started!");

         if(sType.equals("2"))
         {//ROC Zeeland koppelingDidactor
            boolean bFirstLine = true;
            RandomAccessFile rfileInput = new RandomAccessFile(sLocalPath + IFileConstants.PATH_SEPARATOR + sPath, "r");

            log.info("Collecting groups from file. Please wait.");
            HashSet hsGroups = collectGroupsTypeC(rfileInput);

            log.info("Store groups in MMBase.");
            HashMap hmGroups = storeGroupsInMMBase(cloud, hsGroups);

            rfileInput.seek(0);
            log.info("Collecting classes from file. Please wait.");
            HashSet hsClasses = collectClassesTypeC(rfileInput);
            log.info("Store classes in MMBase.");
            HashMap hmClases = storeClassesInMMBase(cloud, hsClasses);



            log.info("Reading people from file.");
            //Rewind file to 0
            rfileInput.seek(0);
            ArrayList arliPeople = new ArrayList();
            long lPrev = 0;
            while (rfileInput.getFilePointer() < rfileInput.length())
            {
               if(lPrev != rfileInput.getFilePointer() * 100 / rfileInput.length())
               {
                  lPrev = rfileInput.getFilePointer() * 100 / rfileInput.length();
                  log.info(lPrev + "%");
               }

               String sFullDataString = new String(rfileInput.readLine().getBytes("cp1252"));
               if(bFirstLine)
               {
                  bFirstLine = false;
                  continue;
               }
               People people = new People();
               String[] arrstrFullDataString = getSectionForC(sFullDataString);
               people.setClasses(arrstrFullDataString[3]);
               people.setFirstname(arrstrFullDataString[5]);
               people.setSuffix(arrstrFullDataString[6]);
               people.setLastname(arrstrFullDataString[7]);
               people.setInitials(arrstrFullDataString[9]);
               people.setPassword(arrstrFullDataString[17]);
               people.setUsername(arrstrFullDataString[16]);
               people.setAddress(arrstrFullDataString[10] + " " +  arrstrFullDataString[11] + " " + arrstrFullDataString[12]);
               people.setZipcode(arrstrFullDataString[13]);
               people.setCity(arrstrFullDataString[14]);
               people.setTelephone(arrstrFullDataString[15]);
               people.setExternid(arrstrFullDataString[4]);
               people.setDescription("Imported from \"" + sPath +  "\" on " + new Date());
               people.setGroup(arrstrFullDataString[8]);

               arliPeople.add(people);
            }
            log.info("Store people in MMbase.");
            storePeopleInMMBase(cloud, arliPeople, hmGroups, hmClases);
         }

         if(sType.equals("1"))
         {//ROC Zeeland
            boolean bFirstLine = true;
            RandomAccessFile rfileInput = new RandomAccessFile(sLocalPath + IFileConstants.PATH_SEPARATOR + sPath, "r");
            HashSet hsGroups = collectGroupsTypeA(rfileInput);
            HashMap hmGroups = storeGroupsInMMBase(cloud, hsGroups);
            rfileInput.seek(0);
            HashSet hsClasses = collectClassesTypeA(rfileInput);
            HashMap hmClases = storeClassesInMMBase(cloud, hsClasses);


            //Rewind file to 0
            rfileInput.seek(0);
            ArrayList arliPeople = new ArrayList();
            while (rfileInput.getFilePointer() < rfileInput.length())
            {
               String sFullDataString = new String(rfileInput.readLine().getBytes("cp1252"));
               if(bFirstLine)
               {
                  bFirstLine = false;
                  continue;
               }
               People people = new People();
               people.setLastname(getSectionForA(sFullDataString, 7));
               people.setFirstname(getSectionForA(sFullDataString, 5));
               people.setClasses(getSectionForA(sFullDataString, 3));
               people.setDescription("RZ" + getSectionForA(sFullDataString, 4));
               people.setGroup(getSectionForA(sFullDataString, 8));
               arliPeople.add(people);
            }
            storePeopleInMMBase(cloud, arliPeople, hmGroups, hmClases);
         }

         if(sType.equals("0"))
         {//BC
            boolean bFirstLine = true;
            RandomAccessFile rfileInput1 = new RandomAccessFile(sLocalPath + IFileConstants.PATH_SEPARATOR + sPath, "r");
            RandomAccessFile rfileInput2 = new RandomAccessFile(sLocalPath + IFileConstants.PATH_SEPARATOR + iniFile.getProperty("File_" + f, "path2", null), "r");

            HashSet hsGroups = collectGroupsTypeB(rfileInput2);
            HashMap hmGroups = storeGroupsInMMBase(cloud, hsGroups);
            rfileInput2.seek(0);
            HashSet hsClasses = collectClassesTypeB(rfileInput2);
            HashMap hmClases = storeClassesInMMBase(cloud, hsClasses);


            //Assign groups to IDs
            rfileInput2.seek(0);
            HashMap hmUserGroups = new HashMap();
            HashMap hmUserClasses = new HashMap();
            while (rfileInput2.getFilePointer() < rfileInput2.length())
            {
               String sFullDataString = new String(rfileInput2.readLine().getBytes("cp1252"));
               String[] arrstrTmp = sFullDataString.split("\\,");
               hmUserGroups.put(arrstrTmp[1], arrstrTmp[0]);
               hmUserClasses.put(arrstrTmp[1], arrstrTmp[6]);
            }

            ArrayList arliPeople = new ArrayList();
            while (rfileInput1.getFilePointer() < rfileInput1.length())
            {
               String sFullDataString = new String(rfileInput1.readLine().getBytes("cp1252"));
               if(bFirstLine)
               {
                  bFirstLine = false;
                  continue;
               }
               People people = new People();
               String[] arrstrTmp = sFullDataString.split("\\,");
               people.setFirstname(arrstrTmp[4]);
               people.setLastname(arrstrTmp[6]);
               people.setAddress(arrstrTmp[8]);
               people.setZipcode(arrstrTmp[9]);
               people.setCity(arrstrTmp[10]);
               people.setInitials(arrstrTmp[3]);
               people.setGroup((String) hmUserGroups.get(arrstrTmp[0]));
               people.setClasses((String) hmUserClasses.get(arrstrTmp[0]));
               people.setDescription("BC" + arrstrTmp[0]);
               arliPeople.add(people);
            }
            storePeopleInMMBase(cloud, arliPeople, hmGroups, hmClases);
         }
      }
      log.info("----------------------------");
      log.info("Done.");
      log.info("All task have been finished.");
   }




   private static HashSet collectGroupsTypeA(RandomAccessFile rfInput) throws Exception
   {//Collect goups for ROC Zeeland
      boolean bFirstLine = true;
      HashSet hsGroups = new HashSet();

      while (rfInput.getFilePointer() < rfInput.length())
      {
         String sFullDataString = new String(rfInput.readLine().getBytes("cp1252"));
         if(bFirstLine)
         {
            bFirstLine = false;
            continue;
         }
         hsGroups.add(getSectionForA(sFullDataString, 8));
      }

      return hsGroups;
   }




   private static HashSet collectGroupsTypeB(RandomAccessFile rfInput) throws Exception
   {//Collect goups for ROC Zeeland
      boolean bFirstLine = true;
      HashSet hsGroups = new HashSet();

      while (rfInput.getFilePointer() < rfInput.length())
      {
         String sFullDataString = new String(rfInput.readLine().getBytes("cp1252"));
         if(bFirstLine)
         {
            bFirstLine = false;
            continue;
         }
         hsGroups.add(sFullDataString.split("\\,")[0]);
      }
      return hsGroups;
   }




   private static HashSet collectGroupsTypeC(RandomAccessFile rfInput) throws Exception
   {//Collect goups for koppelingDidactor
      boolean bFirstLine = true;
      HashSet hsGroups = new HashSet();
      long lPrev = -1;

      while (rfInput.getFilePointer() < rfInput.length())
      {
         if(lPrev != rfInput.getFilePointer() * 100 / rfInput.length())
         {
            lPrev = rfInput.getFilePointer() * 100 / rfInput.length();
            log.info(lPrev + "%");
         }

         String sFullDataString = new String(rfInput.readLine().getBytes("cp1252"));

         if(bFirstLine)
         {
            bFirstLine = false;
            continue;
         }
         hsGroups.add(getSectionForC(sFullDataString)[8]);
      }

      return hsGroups;
   }


   private static HashSet collectClassesTypeA(RandomAccessFile rfInput) throws Exception
   {//Collect classes for ROC Zeeland
      boolean bFirstLine = true;
      HashSet hsClasses = new HashSet();

      while (rfInput.getFilePointer() < rfInput.length())
      {
         String sFullDataString = new String(rfInput.readLine().getBytes("cp1252"));
         if(bFirstLine)
         {
            bFirstLine = false;
            continue;
         }
         hsClasses.add(getSectionForA(sFullDataString, 3));
      }

      return hsClasses;
   }


   private static HashSet collectClassesTypeB(RandomAccessFile rfInput) throws Exception
   {//Collect classes for ROC Zeeland
      boolean bFirstLine = true;
      HashSet hsClasses = new HashSet();

      while (rfInput.getFilePointer() < rfInput.length())
      {
         String sFullDataString = new String(rfInput.readLine().getBytes("cp1252"));
         if(bFirstLine)
         {
            bFirstLine = false;
            continue;
         }
         hsClasses.add(sFullDataString.split("\\,")[6]);
      }

      return hsClasses;
   }


   private static HashSet collectClassesTypeC(RandomAccessFile rfInput) throws Exception
   {//Collect classes for koppelingDidactor
      boolean bFirstLine = true;
      HashSet hsClasses = new HashSet();
      long lPrev = -1;

      while (rfInput.getFilePointer() < rfInput.length())
      {
         if(lPrev != rfInput.getFilePointer() * 100 / rfInput.length())
         {
            lPrev = rfInput.getFilePointer() * 100 / rfInput.length();
            log.info(lPrev + "%");
         }

         String sFullDataString = new String(rfInput.readLine().getBytes("cp1252"));
         if(bFirstLine)
         {
            bFirstLine = false;
            continue;
         }
         hsClasses.add(getSectionForC(sFullDataString)[3]);
      }

      return hsClasses;
   }


   private static HashMap storeGroupsInMMBase(Cloud cloud, HashSet hsGroups)
   {
      HashMap hmGroups = new HashMap();
      NodeManager nmWorkgroup = cloud.getNodeManager("workgroups");
      int iCounter = 0;
      int iPrev = -1;

      for(Iterator it = hsGroups.iterator(); it.hasNext(); )
      {
         if( iCounter * 100 / hsGroups.size() != iPrev)
         {
            iPrev = iCounter * 100 / hsGroups.size();
            log.info(iPrev + "%");
         }
         iCounter++;

         String sGroupName = (String) it.next();
         sGroupName = sGroupName.replaceAll("\\\'", "`");
         NodeList nlWorkgroup = nmWorkgroup.getList("name='" + sGroupName + "'", null, null);
         if(nlWorkgroup.size() == 0)
         {//There aren't such group yet
            Node nodeWorkgroup = nmWorkgroup.createNode();
            nodeWorkgroup.setValue("name", sGroupName);
            nodeWorkgroup.commit();
            hmGroups.put(sGroupName, new Integer(nodeWorkgroup.getNumber()));
         }
         else
         {
            hmGroups.put(sGroupName, new Integer(((Node) nlWorkgroup.get(0)).getNumber()));
         }
      }
      return hmGroups;
   }


   private static HashMap storeClassesInMMBase(Cloud cloud, HashSet hsClasses)
   {
      HashMap hmClasses = new HashMap();
      NodeManager nmClass = cloud.getNodeManager("classes");
      int iCounter = 0;
      int iPrev = -1;

      for(Iterator it = hsClasses.iterator(); it.hasNext(); )
      {
         if( iCounter * 100 / hsClasses.size() != iPrev)
         {
            iPrev = iCounter * 100 / hsClasses.size();
            log.info(iPrev + "%");
         }
         iCounter++;

         String sClassName = (String) it.next();
         sClassName = sClassName.replaceAll("\\\'", "`");
         NodeList nlClass = nmClass.getList("name='" + sClassName + "'", null, null);
         if(nlClass.size() == 0)
         {//There aren't such group yet
            Node nodeClass = nmClass.createNode();
            nodeClass.setValue("name", sClassName);
            nodeClass.commit();
            hmClasses.put(sClassName, new Integer(nodeClass.getNumber()));
         }
         else
         {
            hmClasses.put(sClassName, new Integer(((Node) nlClass.get(0)).getNumber()));
         }
      }
      return hmClasses;
   }

   private static String getSectionForA(String sWholeString, int iTargetSection)
   {
      boolean bInsideQuotes = false;
      int iPreviousQuote = 0;
      int iCurrentSection = 0;
      String sResult = null;
      for(int f = 0; f < sWholeString.length(); f++)
      {
         if(sWholeString.charAt(f) == '\"')
         {
            if(bInsideQuotes)
            {
               bInsideQuotes = false;
            }
            else bInsideQuotes = true;
         }
         if(sWholeString.charAt(f) == ';')
         {
            if(!bInsideQuotes)
            {
               if(iCurrentSection == iTargetSection)
               {
                  sResult = sWholeString.substring(iPreviousQuote, f);
                  break;
               }
               iCurrentSection++;
               iPreviousQuote = f + 1;
            }
         }
      }
      if (sResult == null) sResult = sWholeString.substring(iPreviousQuote, sWholeString.length());
      if(sResult.charAt(0) == '"')
      {
         sResult = sResult.substring(1, sResult.length() - 1);
      }

      return sResult;
   }







   private static String[] getSectionForC(String sWholeString)
   {
      boolean bInsideQuotes = false;
      int iBeginOfString = 0;
      int iCurrentSection = 0;
      String arrstrResult[] = new String[18];
      for(int f = 0; f < sWholeString.length(); f++)
      {
         if(sWholeString.charAt(f) == '\"')
         {
            if(bInsideQuotes)
            {
               bInsideQuotes = false;
            }
            else bInsideQuotes = true;
         }
         if(sWholeString.charAt(f) == ',')
         {
            if(!bInsideQuotes)
            {
               arrstrResult[iCurrentSection] = sWholeString.substring(iBeginOfString, f);
               if(sWholeString.charAt(iBeginOfString) == '"')
               {
                  arrstrResult[iCurrentSection] = sWholeString.substring(iBeginOfString + 1, f - 1);
               }
               else
               {
                  arrstrResult[iCurrentSection] = sWholeString.substring(iBeginOfString, f);
               }
               iCurrentSection++;
               iBeginOfString = f + 1;
            }
         }
      }
      arrstrResult[17] = sWholeString.substring(iBeginOfString, sWholeString.length());
      return arrstrResult;
   }









   private static void storePeopleInMMBase(Cloud cloud, ArrayList arliPeople, HashMap hmGroups, HashMap hmClasses)
   {
      NodeManager nmWorkgroup = cloud.getNodeManager("workgroups");
      NodeManager nmClass = cloud.getNodeManager("classes");
      NodeManager nmPeople = cloud.getNodeManager("people");
      int iPrev = -1;
      int iCounter = 0;

      for(Iterator it = arliPeople.iterator(); it.hasNext(); )
      {
         if(iPrev != iCounter / arliPeople.size())
         {
            iPrev = iCounter / arliPeople.size();
            log.info(iPrev + "%");
         }
         iCounter++;

         People people = (People) it.next();
/*
         NodeList nlWorkgroup = nmWorkgroup.getList("name='" + people.getGroup() + "'", null, null);
         Node nodeWorkgroup = (Node) nlWorkgroup.get(0);
         String sClassName = people.getClasses().replaceAll("\\\'", "`");
         NodeList nlClass = nmClass.getList("name='" + sClassName + "'", null, null);
         Node nodeClass = (Node) nlClass.get(0);
*/
         Node nodPeople = nmPeople.createNode();
         nodPeople.setValue("firstname", people.getFirstname());
         nodPeople.setValue("lastname", people.getLastname());
         nodPeople.setValue("email", people.getEmail());
         nodPeople.setValue("password", people.getPassword());
         nodPeople.setValue("username", people.getUsername());
         nodPeople.setValue("address", people.getAddress());
         nodPeople.setValue("zipcode", people.getZipcode());
         nodPeople.setValue("city", people.getCity());
         nodPeople.setValue("telephone", people.getTelephone());
         nodPeople.setValue("description", people.getDescription());
         nodPeople.setValue("initials", people.getInitials());

         nodPeople.setValue("suffix", people.getSuffix());
         nodPeople.setValue("country", people.getCountry());
         nodPeople.setValue("mobile", people.getMobile());
         nodPeople.setValue("website", people.getWebsite());
         nodPeople.setValue("dayofbirth", people.getDayOfBirth());
         nodPeople.setValue("externid", people.getExternid());
         nodPeople.commit();

         if(hmGroups.get(people.getGroup()) != null)
         {
            Node nodeWorkgroup = cloud.getNode( ( (Integer) hmGroups.get(people.getGroup())).intValue());
            nodeWorkgroup.createRelation(nodPeople, cloud.getRelationManager("related")).commit();
         }

         if(hmClasses.get(people.getClasses()) != null)
         {
            Node nodeClass = cloud.getNode( ( (Integer) hmClasses.get(people.getClasses().replaceAll("\\\'", "`"))).intValue());
            nodeClass.createRelation(nodPeople, cloud.getRelationManager("classrel")).commit();
         }
      }
   }
}


