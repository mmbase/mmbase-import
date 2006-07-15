package nl.leocms.evenementen.stats;

import java.util.*;
import java.io.*;
import java.text.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WritableCellFormat;
import jxl.write.Label;
import jxl.write.WriteException;


import nl.leocms.applications.NatMMConfig;

public class ExtraStats {

   private static final Logger log = Logging.getLoggerInstance(ExtraStats.class);

   public int iTotal = 0;

   private String getEvenementen(Cloud cloud, String sRealNumber, String sNodepath, String evenementTimeConstraint, String afdelingenConstraint){
      StringBuffer events = new StringBuffer();
      // *** child events ***
      NodeList nlEvenementen = cloud.getList(sRealNumber,
         sNodepath,"evenement.number",afdelingenConstraint,null,null,null,false);
      StringBuffer parents = new StringBuffer();
      for (int j = 0; j < nlEvenementen.size(); j++ ){
         Node nEvenement = nlEvenementen.getNode(j);
         parents.append(",").append(nEvenement.getStringValue("evenement.number"));
      }
      if(parents.length()>0) {
         parents.deleteCharAt(0);
         nlEvenementen = cloud.getList(parents.toString(),
            "evenement2,partrel,evenement","evenement.number",evenementTimeConstraint,null,null,"destination",false);
         for (int j = 0; j < nlEvenementen.size(); j++ ){
            Node nEvenement = nlEvenementen.getNode(j);
            events.append(",").append(nEvenement.getStringValue("evenement.number"));
         }
      }
      // *** parent events ***
      nlEvenementen = cloud.getList(sRealNumber,
            sNodepath,"evenement.number",evenementTimeConstraint,null,null,null,false);
      for (int j = 0; j < nlEvenementen.size(); j++ ){
         Node nEvenement = nlEvenementen.getNode(j);
         events.append(",").append(nEvenement.getStringValue("evenement.number"));
      }
      if(events.length()>0) { events.deleteCharAt(0); }
      return events.toString();
   }

   private int getCounts(Cloud cloud, String sNodeNumber, String evenementTimeConstraint, String statstype){

      String sRealNodepath = "evenement,posrel,inschrijvingen";
      String sRealConstraints = evenementTimeConstraint;

      NodeList nl = null;
      int iResult = 0;

      if (statstype.equals("inschrijvingen")){
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number",sRealConstraints,null,null,null,false);
         iResult += nl.size();
      }

      else if (statstype.equals("deelnemers")){
         sRealNodepath += ",posrel2,deelnemers";
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,deelnemers.bron",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("deelnemers.bron");
         }
      }

      else if (statstype.equals("leden")){
         sRealNodepath += ",posrel2,deelnemers,related,deelnemers_categorie";
         if(!sRealConstraints.equals("")) { sRealConstraints += " AND "; }
         sRealConstraints += " ( UPPER(deelnemers_categorie.naam) NOT like '%NIET%' )";
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,deelnemers.bron",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("deelnemers.bron");
         }
      }

      else if (statstype.equals("opbrengst")){
         sRealNodepath += ",posrel2,deelnemers";
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,posrel2.pos",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("posrel2.pos");
         }
      }

      else if (statstype.equals("activiteiten")){
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,evenement.number",sRealConstraints,null,null,null,false);
         TreeSet tsEvenementNumbers = new TreeSet();
         for(int i = 0; i < nl.size(); i++) {
            String eventNumber = nl.getNode(i).getStringValue("evenement.number");
            if(!tsEvenementNumbers.contains(eventNumber)){
               tsEvenementNumbers.add(eventNumber);
               iResult++;
            }
         }
      }

      return iResult;
   }

   private TreeMap getEvenementenTypes(Cloud cloud, boolean groupsCategorie) {
     TreeMap tmNames = new TreeMap();
     iTotal = 0;

     String notGroups = (groupsCategorie) ? "" : "NOT ";
     String deelnemersCategorieConstraints = "deelnemers_categorie.naam "+notGroups+"LIKE '%groups excursion%'";

     // *** put names on tsNames ***
     String sStatName = "";
     String sNumber = "";
     NodeList nlEvenementType = cloud.getList("","evenement_type,posrel,deelnemers_categorie","evenement_type.naam,evenement_type.number",deelnemersCategorieConstraints,null,null,null,false);
     for (int j = 0; j < nlEvenementType.size(); j++ ){
       Node nEvenementType = nlEvenementType.getNode(j);
       sStatName = nEvenementType.getStringValue("evenement_type.naam");
       sNumber = nEvenementType.getStringValue("evenement_type.number");
       if (!tmNames.containsKey(sStatName)) {
         tmNames.put(sStatName, sNumber);
       }
     }
     return tmNames;
   }


   private TreeMap getStatistics(Cloud cloud, long fromTime, long toTime, int period, String afdelingName, String statstype, TreeMap tmNames){
      return getStatistics(cloud, fromTime, toTime, period, afdelingName, statstype, tmNames, true); // zero's rows not showed
   }

   private TreeMap getStatistics(Cloud cloud, long fromTime, long toTime, int period, String afdelingName, String statstype, TreeMap tmNames, boolean removeZeros){
      TreeMap tmStatistics = new TreeMap();

      String evenementTimeConstraint =  "evenement.begindatum > " + fromTime;
      if (period>0) evenementTimeConstraint += " AND evenement.einddatum < " + toTime;

      // *** count total for tmNames ***
      String sRealNumber = null;
      String sStatName = null;
      Set set = tmNames.entrySet();
      Iterator i = set.iterator();
      while (i.hasNext()) {
        Map.Entry me = (Map.Entry)i.next();
        sStatName = (String) me.getKey();
        sRealNumber = (String)tmNames.get(sStatName);

        String pathToEvenement = "evenement_type,related,evenement";

        afdelingName = afdelingName.replace('\'','"');

        String afdelingenConstraint = "afdelingen.naam LIKE '%"+afdelingName+"%'";


        // CANNOT EXECUTE
        pathToEvenement += ",related,natuurgebieden,posrel,afdelingen";

        sRealNumber = getEvenementen(cloud, sRealNumber, pathToEvenement, evenementTimeConstraint,afdelingenConstraint);

        if(!sRealNumber.equals("")) {
          int iResultCounts = getCounts(cloud,sRealNumber,evenementTimeConstraint,statstype);
          if(!removeZeros||iResultCounts!=0) {
            iTotal += iResultCounts;
            tmStatistics.put(sStatName,new Integer(iResultCounts));
          }
        }
      }

      return tmStatistics;
   }


   private String nowDate() {
      SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy");
      return formatter.format(new Date());
   }

   public String getStatsperiod(long fromTime, long toTime, int period) {
      SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy");
      String fromStr = formatter.format(new Date(fromTime*1000));
      String untillAndIncludingStr = formatter.format(new Date((toTime-24*60*60)*1000));
      String sStatsperiod = "Statistieken ";
      if(period==-1){
         sStatsperiod += "vanaf "  + fromStr;
      } else if(period==1){
   	   sStatsperiod += "voor " + fromStr;
	   } else {
         sStatsperiod += "van " +  fromStr + " tot en met " + untillAndIncludingStr;
      }
      return sStatsperiod;
   }

   private String dateString(long timeInSec) {
      String dateString = "";
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(timeInSec*1000);
      dateString += cal.get(Calendar.YEAR);
      if(cal.get(Calendar.MONTH)+1<10) { dateString += "0"; }
      dateString += cal.get(Calendar.MONTH)+1;
      if(cal.get(Calendar.DAY_OF_MONTH)<10) { dateString += "0"; }
      dateString += cal.get(Calendar.DAY_OF_MONTH);
      return dateString;
   }


   public String write(Cloud cloud, long fromTime, long toTime, int period) throws IOException, WriteException {

      String listtype = "Afdelingen (BCs)";

      ArrayList alStatstypes = new ArrayList();
      alStatstypes.add("activiteiten");
      alStatstypes.add("inschrijvingen");
      alStatstypes.add("deelnemers");
      alStatstypes.add("opbrengst");
      alStatstypes.add("leden");

      String dateString = dateString(fromTime) + "_" + dateString(toTime);
      String fileName = listtype.replaceAll("/","_").replaceAll(" ","") + "_" + dateString + "_stats.xls";

      String sDate  = nowDate();

      String sAttachmentId = "";

      /*NodeList nl = cloud.getList("","events_attachments","events_attachments.number","events_attachments.filename = '" + fileName + "'",null,null,null,false);
      if(nl.isEmpty()) {*/

         WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.tempDir + fileName));


         ArrayList afdelingenList = new ArrayList();
         String afdelingenConstraint = "afdelingen.naam LIKE '%BC%'";
         NodeList nlAfdelingen = cloud.getList("","afdelingen","afdelingen.number,afdelingen.naam",afdelingenConstraint,null,null,null,false);
         for (int jj = 0; jj < nlAfdelingen.size(); jj++ ) {
            Node afdeling = nlAfdelingen.getNode(jj);
            String af = afdeling.getStringValue("afdelingen.naam");
            if (!afdelingenList.contains(af)) {
              afdelingenList.add(af);
            }
         }

         for (Iterator ait = afdelingenList.iterator();ait.hasNext();) {

           String afdelingName = (String)ait.next();
           WritableSheet sheet = workbook.createSheet(afdelingName, 0);
           WritableCellFormat wrappedText = new WritableCellFormat(WritableWorkbook.ARIAL_10_PT);
           wrappedText.setWrap(true);

           // set columns width
           sheet.setColumnView(0,50);
           sheet.setColumnView(1,20);
           sheet.setColumnView(2,20);
           sheet.setColumnView(3,20);
           sheet.setColumnView(4,20);
           sheet.setColumnView(5,20);

           // add static labels
           Label staticLabel = new Label(0,0,"BEHEEREENHEID/BEZOEKERSCENTRUM:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,0,afdelingName);
           sheet.addCell(staticLabel);
           staticLabel = new Label(0,1,"DATUM:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,1,sDate);
           sheet.addCell(staticLabel);
           staticLabel = new Label(0,2,"PERIODE:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,2,getStatsperiod(fromTime,toTime,period));
           sheet.addCell(staticLabel);
           staticLabel = new Label(0,3,"");
           sheet.addCell(staticLabel);
           staticLabel = new Label(0,4,"EXCURSIETYPE");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,4,"AANTAL");
           sheet.addCell(staticLabel);
           staticLabel = new Label(4,4,"BATEN");
           sheet.addCell(staticLabel);
           staticLabel = new Label(5,4,"Geschat % leden dat deelneemt");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,5,"Excursies");
           sheet.addCell(staticLabel);
           staticLabel = new Label(2,5,"Inschrijvingen");
           sheet.addCell(staticLabel);
           staticLabel = new Label(3,5,"Deelnemers");
           sheet.addCell(staticLabel);

           ExtraStats os = new ExtraStats();

           int currentRow = 6;

           ArrayList boekingenList = new ArrayList();
           boekingenList.add("Individuele boekingen");
           boekingenList.add("Groepsboekingen");

           for (Iterator bit = boekingenList.iterator();bit.hasNext();) {

             String boekingenName = (String)bit.next();
             boolean isGroups = boekingenName.equals("Groepsboekingen");
             staticLabel = new Label(0,currentRow,boekingenName);
             sheet.addCell(staticLabel);
             currentRow++;

             TreeMap evTypes = getEvenementenTypes(cloud, isGroups);

             int maxRow = 0;
             int[] total = new int[] {0,0,0,0,0};
             Map deelnemers = new HashMap();
             jxl.write.Number nValue = null;

             int j = 1; // *** columns ***
             Iterator i = alStatstypes.iterator();
             while (i.hasNext()) {
               String sStatsType = (String)i.next();
               TreeMap tmAllStats = os.getStatistics(cloud,fromTime,toTime,period,afdelingName,sStatsType,evTypes);
               Set set = tmAllStats.entrySet();
               Iterator it = set.iterator();
               int k = currentRow; // *** rows ***
               while (it.hasNext()){
                 Map.Entry me = (Map.Entry)it.next();
                 String sListTypeName = (String) me.getKey();

                 if (j==1) { // print name of event types
                   Label lListTypeName = new Label(0,k,sListTypeName,wrappedText);
                   sheet.addCell(lListTypeName);
                 }

                 Integer sValue = (Integer) me.getValue();
                 if (sValue.intValue()!=0) {
                 int value = sValue.intValue();

                 if(sStatsType.equals("opbrengst")) {
                   value = value/100;
                 }

                 if(sStatsType.equals("deelnemers")) {
                   deelnemers.put(sListTypeName, sValue);
                 }

                 if(sStatsType.equals("leden")) {
                  int deelnemersValue = ((Integer)deelnemers.get(sListTypeName)).intValue();
                  if (deelnemersValue!=0)
                    value = value * 100 / deelnemersValue;
                  else
                    value = 0;
                }

                total[j-1] += value;
                nValue = new jxl.write.Number(j, k, value);
                sheet.addCell(nValue);
              }

              k++;
            }
            // print TOTAL
            if (j==1) {
               staticLabel = new Label(0,k,"TOTAAL");
               sheet.addCell(staticLabel);
             }
             nValue = new jxl.write.Number(j, k, total[j-1]);
             sheet.addCell(nValue);
             maxRow = (k>maxRow) ? k : maxRow;
             j++;
           }
           currentRow =  maxRow + 2; //  add an extra empty row between categories
         }

       }

       workbook.write();
       workbook.close();

         String sFile = NatMMConfig.tempDir + fileName;
         File f = new File(sFile);
         int fsize = (int)f.length();
         byte[] thedata = new byte[fsize];
         try {
            FileInputStream instream = new FileInputStream(f);
            instream.read(thedata);
            NodeManager nmAttachmentsManager = cloud.getNodeManager("events_attachments");
            Node attNode = nmAttachmentsManager.createNode();
            attNode.setValue("titel","Extra Statistics " + listtype + "_" + dateString);
            attNode.setValue("title","Extra Statistics " + listtype + "_" + dateString);
            attNode.setValue("handle",thedata);
            attNode.setValue("filename",fileName);
            attNode.setValue("mimetype","application/msexcel");
            attNode.setIntValue("size",fsize);
            attNode.commit();
            sAttachmentId = attNode.getStringValue("number");
            instream.close();
         } catch (Exception e) {
            log.error("Exception: " + e);
         }
      /*} else {
           sAttachmentId = nl.getNode(0).getStringValue("events_attachments.number");
      }*/
      return sAttachmentId;
   }
}
