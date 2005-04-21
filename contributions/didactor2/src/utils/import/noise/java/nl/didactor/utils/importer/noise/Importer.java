package nl.didactor.utils.importer.noise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.io.RandomAccessFile;

import nl.didactor.utils.importer.noise.People;
import nl.didactor.utils.importer.noise.INIFile;
import nl.didactor.utils.importer.noise.IFileConstants;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;


public class Importer
{
   public static void main(String[] args) throws Exception
   {  //.ini file
      String sLocalPath = new File (".").getCanonicalPath ();
      INIFile iniFile = new INIFile();
      iniFile.loadFile(sLocalPath + IFileConstants.PATH_SEPARATOR + "config.ini");
      //Access to the cloud
      HashMap user = new HashMap();
      user.put("username",iniFile.getProperty("Options", "username", null));
      user.put("password",iniFile.getProperty("Options", "password", null));

      CloudContext cloudContext = null;
      Cloud cloud = null;
      try
      {
         cloudContext = ContextProvider.getCloudContext(iniFile.getProperty("Options", "rmi", null));
         cloud = cloudContext.getCloud("mmbase");
      }
      catch(Exception ex)
      {
         System.out.println("Failed to connect to the cloud: " + iniFile.getProperty("Options", "rmi", null) 
            + " with (" + iniFile.getProperty("Options", "username", null) + "," + iniFile.getProperty("Options", "password", null) + ")");
         System.out.println(ex.toString());
         System.exit(1);
      }

/*
      NodeManager nmPeople = cloud.getNodeManager("people");
      NodeList nlPeople = nmPeople.getList(null, null, null);
      System.out.println(nlPeople.size());
      Node node = (Node) nlPeople.get(0);
      System.out.print(node.getNumber());
      System.out.print(node.getStringValue("firstname"));
      node.setStringValue("firstname", "44444444444");
      node.commit();

      Node nodPeople = nmPeople.createNode();
      nodPeople.setValue("firstname", "333333");
      nodPeople.commit();
*/

      for(int f = 0; f < 1000; f++)
      {//Main cycle for reading [File_xxx] groups
         String sPath = iniFile.getProperty("File_" + f, "path", null);
         String sType = iniFile.getProperty("File_" + f, "type", null);

         //If there are no any of these parameters
         if((sPath == null) || (sType == null)) break;

         System.out.println("Task " + f + " started!");

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
      System.out.println("");
      System.out.println("Done");
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


   private static HashMap storeGroupsInMMBase(Cloud cloud, HashSet hsGroups)
   {
      HashMap hmGroups = new HashMap();
      NodeManager nmWorkgroup = cloud.getNodeManager("workgroups");
      for(Iterator it = hsGroups.iterator(); it.hasNext(); )
      {
         String sGroupName = (String) it.next();
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
      for(Iterator it = hsClasses.iterator(); it.hasNext(); )
      {
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


   private static void storePeopleInMMBase(Cloud cloud, ArrayList arliPeople, HashMap hmGroups, HashMap hmClasses)
   {
      NodeManager nmWorkgroup = cloud.getNodeManager("workgroups");
      NodeManager nmClass = cloud.getNodeManager("classes");
      NodeManager nmPeople = cloud.getNodeManager("people");
      for(Iterator it = arliPeople.iterator(); it.hasNext(); )
      {
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


