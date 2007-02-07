package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import org.mmbase.util.logging.*;

public class MigrateUtil {


  private static final Logger log = Logging.getLoggerInstance(MigrateUtil.class);
  
  /*
  * reads node numbers from a String
  */
  public static ArrayList getNodes (String sContent) throws Exception {
    
      ArrayList al = new ArrayList();
      int iBegNodeIndex = sContent.indexOf("<node number=\"");
      while (iBegNodeIndex>-1){
        int iBegNodeNumberIndex = iBegNodeIndex + 14;
        int iEndNodeNumberIndex = sContent.indexOf("\"",iBegNodeNumberIndex);
        String sNodeNumber = sContent.substring(iBegNodeNumberIndex,iEndNodeNumberIndex);
        al.add(sNodeNumber);
        iBegNodeIndex = sContent.indexOf("<node number=\"",iEndNodeNumberIndex);
      }

      return al;
   }
   
  /*
  * reads node numbers from a File
  */
  public static ArrayList getNodesFromFile(String sFileName) throws Exception {

      ArrayList al = new ArrayList();

      FileReader fr = new FileReader(sFileName);
      BufferedReader br = new BufferedReader(fr);
      String sOneString;
      while ( (sOneString = br.readLine()) != null) {
         if (sOneString.indexOf("<node number=")>-1) {
            al.add(sOneString.substring(sOneString.indexOf("<node number=\"") + 14,
                   sOneString.indexOf("\" ")));
            }
         }
      fr.close();

      return al;
   }
   
   
   public static String readingFile(String sFileName) throws Exception{

      FileInputStream file = new FileInputStream (sFileName);
      DataInputStream in = new DataInputStream (file);
      byte[] b = new byte[in.available ()];
      in.readFully (b);
      in.close ();
      String sResult = new String (b, 0, b.length, "UTF-8");

      return sResult;
   }

   public static String readingFileRemoveFields(String sFileName, ArrayList alDeletingFields) throws Exception{

      FileInputStream fin = new FileInputStream(sFileName);
      InputStreamReader isr = new InputStreamReader(fin,"UTF-8");
      BufferedReader br = new BufferedReader(isr);
      String sAllContent = "";
      String sOneString;
      while ( (sOneString = br.readLine()) != null) {
         Iterator itDeletingFields = alDeletingFields.iterator();
         boolean bIsRemovingString = false;
         while (itDeletingFields.hasNext()) {
            if (sOneString.indexOf("<" + (String) itDeletingFields.next() + ">") > -1) {
               bIsRemovingString = true;
            }
         }
         if ((sFileName.indexOf("websites.xml")>-1)&&(sOneString.indexOf("<expiredate>") > -1)){
            bIsRemovingString = true;
         }
         if (!bIsRemovingString) {
            sAllContent += sOneString + "\n";
         }
      }
      br.close();
      return sAllContent;

   }
   
   public static String readingFile(String sFileName, ArrayList alRemovingNodes) throws Exception{

      FileInputStream fin = new FileInputStream(sFileName);
      InputStreamReader isr = new InputStreamReader(fin,"UTF-8");
      BufferedReader br = new BufferedReader(isr);

      String sAllContent = "";
      String sOneString;
      while ( (sOneString = br.readLine()) != null) {
            Iterator itRemovingNodes = alRemovingNodes.iterator();
            boolean bIsRemovingString = false;
            while (itRemovingNodes.hasNext()) {
               String sNextNode = (String) itRemovingNodes.next();
               if ( (sOneString.indexOf("snumber=\"" + sNextNode + "\"") > -1) ||
                    (sOneString.indexOf("dnumber=\"" + sNextNode + "\"") > -1) ){
                        sOneString = br.readLine();
                        sOneString = br.readLine();
                        bIsRemovingString = true;
               }
            }
            if (!bIsRemovingString) {
               sAllContent += sOneString + "\n";
            }
         }

      br.close();

      return sAllContent;

   }
   
   public static String readWholeFile(String sFileName,ArrayList alRemovingNodes) throws Exception{

      FileInputStream file = new FileInputStream (sFileName);
      DataInputStream in = new DataInputStream (file);
      byte[] b = new byte[in.available ()];
      in.readFully (b);
      in.close ();
      String sResult = new String (b, 0, b.length, "Cp850");
      Iterator itRemovingNodes = alRemovingNodes.iterator();
      while (itRemovingNodes.hasNext()) {
         String sNextNode = (String) itRemovingNodes.next();
         int iNIndex = sResult.indexOf("number=\"" + sNextNode + "\"");
         while (iNIndex>-1){
            while (sResult.substring(iNIndex-1,iNIndex-1).equals(" ")){
               iNIndex = sResult.indexOf("number=\"" + sNextNode + "\"");
            }
            int iNodeBegIndex = sResult.indexOf("<node",iNIndex-65);
            int iNodeEndIndex = sResult.indexOf("</node>",iNIndex)+7;

            sResult = sResult.substring(0,iNodeBegIndex-1) + sResult.substring(iNodeEndIndex+1);
            iNIndex = sResult.indexOf("number=\"" + sNextNode + "\"");
         }
      }
      while (sResult.indexOf("\n\n\n")>-1){
         sResult = sResult.replaceAll("\n\n\n","\n\n");
      }
      return sResult;
   }
   
   
   public static String deleteFields(String sContent, ArrayList alThisDeletedFields) {
   
    Iterator it1 = alThisDeletedFields.iterator();
    while (it1.hasNext()){
       String sField = (String)it1.next();
       int iBegIndex = sContent.indexOf("<" + sField + ">");
       while (iBegIndex>-1){
         int iEndEndex = sContent.indexOf("</" + sField + ">",iBegIndex);
         iEndEndex = sContent.indexOf("<",iEndEndex + sField.length() + 3);
         sContent = sContent.substring(0,iBegIndex) + sContent.substring(iEndEndex);
         iBegIndex = sContent.indexOf("<" + sField + ">");
       }
    }
    
    return sContent;
   }
   
   public static String deletingRelation(String sContent, String sDelRel){

     int iDelRelIndex = sContent.indexOf(sDelRel);
     while (iDelRelIndex>-1){
       int iBegNodeIndex = sContent.indexOf("<node number=",iDelRelIndex-75) - 1;
       int iEndNodeIndex = sContent.indexOf("</node>",iDelRelIndex) + 9;
       sContent = sContent.substring(0,iBegNodeIndex) + sContent.substring(iEndNodeIndex);
       iDelRelIndex = sContent.indexOf(sDelRel);
     }
     return sContent;
   }

   static void writeToFile(File file, String sNewFile, String sAllContent) throws Exception{
     file.createNewFile();
     FileOutputStream fos = new FileOutputStream(sNewFile);
     OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
     BufferedWriter bw = new BufferedWriter(osw);
     bw.write(sAllContent);
     bw.close();     
   }
   
   public static void writingFile(File file, String sNewFile, String sAllContent) throws Exception{
     file.delete();
     file = new File(sNewFile);
     writeToFile(file, sNewFile, sAllContent);
   }
	
   public static void writingFile(String sNewFile, String sAllContent) throws Exception{

     File file = new File(sNewFile);
     file.delete();
     writeToFile(file, sNewFile, sAllContent);
   }

   public static String renamingFields(String sContent,TreeMap tmRenamingFields){

      Set set = tmRenamingFields.entrySet();
      Iterator it = set.iterator();

      while (it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         if (sContent.indexOf("<" + me.getKey() + ">")>-1){
            sContent = sContent.replaceAll("<" + me.getKey() + ">","<" + me.getValue() + ">");
            sContent = sContent.replaceAll("</" + me.getKey() + ">","</" + me.getValue() + ">");
         }
        // not part or nmintra migration
        if (sContent.indexOf("rtype=\"" + me.getKey() )>-1){
          sContent = sContent.replaceAll("rtype=\"" + me.getKey(),"rtype=\"" + me.getValue());
        }
      }
      return sContent;
   }

   public static String buildingUrlsTitels(String sContent){

     int iBegNameIndex = sContent.indexOf("<name>") ;
     int iEndNameIndex = sContent.indexOf("</name>");
     while ((iBegNameIndex>-1)&&(iEndNameIndex>-1)){
       String sNameValue = sContent.substring(iBegNameIndex + 6,iEndNameIndex);
       if (!sNameValue.equals("")){
         sContent = sContent.substring(0,iBegNameIndex) + "<titel>" + sNameValue +
             "</titel>" + sContent.substring(iEndNameIndex + 7);
       } else {
         sContent = sContent.substring(0,iBegNameIndex) + sContent.substring(iEndNameIndex + 7);
         int iBegDescrIndex = sContent.indexOf("<description>",iEndNameIndex);
         int iEndDescrIndex = sContent.indexOf("</description>",iBegDescrIndex);
         if ((iBegDescrIndex>-1)&&(iEndDescrIndex>-1)){
           String sDecrValue = sContent.substring(iBegDescrIndex + 13,iEndDescrIndex);
           sContent = sContent.substring(0,iBegDescrIndex) + "<titel>" + sDecrValue +
             "</titel>" + sContent.substring(iEndDescrIndex + 14);
         }
       }
       iBegNameIndex = sContent.indexOf("<name>") ;
       iEndNameIndex = sContent.indexOf("</name>");
     }
     sContent = sContent.replaceAll("<description></description>","");
     sContent = sContent.replaceAll("\n\t\t\n\t\t","\n\t\t");

     return sContent;
   }

   public static String deletingRelation(ArrayList alFrom, ArrayList alTo, String sContent) {

     Iterator it = alFrom.iterator();
     while (it.hasNext()){
       String sNextNode = (String)it.next();
       int iSNumberIndex = sContent.indexOf("snumber=\"" + sNextNode + "\"");
       while (iSNumberIndex != -1) {
         int iDNIndex = sContent.indexOf("dnumber", iSNumberIndex);
         int iQuotIndex = sContent.indexOf("\"", iDNIndex + 9);
         String sRelNode = sContent.substring(iDNIndex + 9, iQuotIndex);
         if (alTo.contains(sRelNode)){
           int iBegNodeIndex = sContent.indexOf("<node number=",iSNumberIndex - 60) - 1;
           int iEndNodeIndex = sContent.indexOf("</node>",iSNumberIndex) + 9;
           sContent = sContent.substring(0,iBegNodeIndex) + sContent.substring(iEndNodeIndex);
           iSNumberIndex = sContent.indexOf("snumber=\"" + sNextNode + "\"", iBegNodeIndex);
         } else {
            iSNumberIndex = sContent.indexOf("snumber=\"" + sNextNode + "\"",
                                             iSNumberIndex + 1);
         }
       }
     }

     return sContent;
   }
   
   public static String timeStamp() {
      Calendar cal = Calendar.getInstance();
      String sTimeStamp = "" + cal.get(Calendar.YEAR);
      if ((cal.get(Calendar.MONTH) + 1)<10){
         sTimeStamp += "0";
      }
      sTimeStamp += (cal.get(Calendar.MONTH) + 1);
      if (cal.get(Calendar.DAY_OF_MONTH)<10){
         sTimeStamp += "0";
      }
      sTimeStamp += cal.get(Calendar.DAY_OF_MONTH);
      if (cal.get(Calendar.HOUR_OF_DAY)<10){
          sTimeStamp += "0";
      }
      sTimeStamp += cal.get(Calendar.HOUR_OF_DAY);
      if (cal.get(Calendar.MINUTE)<10){
          sTimeStamp += "0";
      }
      sTimeStamp += cal.get(Calendar.MINUTE);
      if (cal.get(Calendar.SECOND)<10){
          sTimeStamp += "0";
      }
      sTimeStamp += cal.get(Calendar.SECOND);
      return sTimeStamp;
   }

   public static void creatingNewXML(String sFolder, String sBuilderName, String sApplication, String sContent) throws Exception{
      
      String sRealContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
         "\n" + "<" + sBuilderName + " exportsource=\"mmbase://127.0.0.1/" + sApplication + "/install\"" +
         " timestamp=\"" + timeStamp() + "\"" + ">" + "\n";
      sRealContent += sContent + "</" + sBuilderName + ">";

      String sFileName = sFolder + sBuilderName + ".xml";
      File file = new File(sFileName);

      writingFile(file,sFileName,sRealContent);

   }

   public static String[] movingRelations(ArrayList alFrom, ArrayList alTo,
     String sOldBuilderContent, String sOldBuilderName, String sNewBuilderName){

     String sNewContent = "";
     Iterator it = alFrom.iterator();
     while (it.hasNext()){
       String sFromNode = (String)it.next();
       int iSNIndex = sOldBuilderContent.indexOf("snumber=\"" + sFromNode + "\"");
       while (iSNIndex>-1){
         int iDNBegIndex = sOldBuilderContent.indexOf("dnumber=\"", iSNIndex) + 9;
         int iSNEndIndex = sOldBuilderContent.indexOf("\"", iDNBegIndex + 1);
         String sDNNodeNumber = sOldBuilderContent.substring(iDNBegIndex, iSNEndIndex);
         if (alTo.contains(sDNNodeNumber)) {
           int iBegNodeIndex = sOldBuilderContent.indexOf("<node number",iSNIndex - 60) - 1;
           int iEndNodeIndex = sOldBuilderContent.indexOf("</node>",iBegNodeIndex) + 9;
           sNewContent += sOldBuilderContent.substring(iBegNodeIndex, iEndNodeIndex);
           sOldBuilderContent = sOldBuilderContent.substring(0, iBegNodeIndex) +
               sOldBuilderContent.substring(iEndNodeIndex);
           iSNIndex = sOldBuilderContent.indexOf("snumber=\"" + sFromNode + "\"", iBegNodeIndex);
         } else {
            iSNIndex = sOldBuilderContent.indexOf("snumber=\"" + sFromNode +
               "\"", iSNIndex + 1);
         }
       }
      }
      sNewContent = sNewContent.replaceAll(sOldBuilderName,sNewBuilderName);
      String [] sRel = new String[2];
      sRel[0] = sOldBuilderContent;
      sRel[1] = sNewContent;
     return sRel;
   }


   public static String movingRelations(ArrayList alFrom, ArrayList alTo,
     String sContent, String sNewBuilderName){

     Iterator it = alFrom.iterator();
     while (it.hasNext()){
       String sFromNode = (String)it.next();
       int iSNIndex = sContent.indexOf("snumber=\"" + sFromNode + "\"");
       while (iSNIndex>-1){
         int iDNBegIndex = sContent.indexOf("dnumber=\"", iSNIndex) + 9;
         int iSNEndIndex = sContent.indexOf("\"", iDNBegIndex + 1);
         String sDNNodeNumber = sContent.substring(iDNBegIndex, iSNEndIndex);
         if (alTo.contains(sDNNodeNumber)) {
           int iRTypeBegIndex = sContent.indexOf("rtype=\"", iSNEndIndex) + 7;
           int iRTypeEndIndex = sContent.indexOf("\"", iRTypeBegIndex + 1);
           sContent = sContent.substring(0, iRTypeBegIndex) + sNewBuilderName +
               sContent.substring(iRTypeEndIndex);

         }
         iSNIndex = sContent.indexOf("snumber=\"" + sFromNode + "\"",iSNIndex + 1);
       }
      }
     return sContent;
   }

   public static String addingContent (String sContent, String sBuilderName, String sAdd){
     int iEntryPointIndex = sContent.lastIndexOf("</" + sBuilderName + ">");
     sContent = sContent.substring(0,iEntryPointIndex) + sAdd +
     sContent.substring(iEntryPointIndex);

     return sContent;
   }

}
