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

public class OptionedStats {

   private static final Logger log = Logging.getLoggerInstance(OptionedStats.class);

   public int iTotal = 0;

   private String getEvenementen(Cloud cloud, String sRealNumber, String sNodepath, String evenementTimeConstraint){
      StringBuffer events = new StringBuffer();
      // *** child events ***
      NodeList nlEvenementen = cloud.getList(sRealNumber,
         sNodepath,"evenement.number",null,null,null,null,false);
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

   private int getCounts(Cloud cloud, String sNodeNumber, String evenementTimeConstraint, String listtype, String statstype){

      String sRealNodepath = "evenement,posrel,inschrijvingen";
      String sRealConstraints = evenementTimeConstraint;
      if(listtype.equals("Bestelwijze")) {
         sRealConstraints += " AND (inschrijvingen.users LIKE'%," + sNodeNumber + ",%')";
         sNodeNumber = "";
      }
      NodeList nl = null;
      int iResult = 0;
      if (statstype.equals("inschrijvingen")){
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number",sRealConstraints,null,null,null,false);
         iResult += nl.size();
      } else if (statstype.equals("deelnemers")){
         sRealNodepath += ",posrel2,deelnemers";
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,deelnemers.bron",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("deelnemers.bron");
         }
      } else if (statstype.equals("leden")||statstype.equals("niet leden")){
         sRealNodepath += ",posrel2,deelnemers,related,deelnemers_categorie";
         if(!sRealConstraints.equals("")) { sRealConstraints += " AND "; }
         if(statstype.equals("leden")) {
            sRealConstraints += " ( UPPER(deelnemers_categorie.naam) NOT like '%NIET%' )";
         } else {
            sRealConstraints += " ( UPPER(deelnemers_categorie.naam) like '%NIET%' )";
         }
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,deelnemers.bron",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("deelnemers.bron");
         }
      } else if (statstype.equals("opbrengst")){
         sRealNodepath += ",posrel2,deelnemers";
         nl = cloud.getList(sNodeNumber,sRealNodepath,"inschrijvingen.number,posrel2.pos",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("posrel2.pos");
         }
      } else if (statstype.equals("activiteiten") || statstype.equals("geannuleerde activiteiten")){

         if(statstype.equals("geannuleerde activiteiten")) {
            if(!sRealConstraints.equals("")) { sRealConstraints += " AND "; }
             sRealConstraints += " ( evenement.iscanceled = 'true' )";
         }
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

   public TreeMap getStatistics(Cloud cloud, long fromTime, long toTime, int period, String listtype, String statstype, boolean removeZeros){
      TreeMap tmStatistics = new TreeMap();
      TreeSet tsNames = new TreeSet();
      TreeMap tmNames = new TreeMap();
      iTotal = 0;

      String evenementTimeConstraint =  "evenement.begindatum > " + fromTime;
      if (period>0) evenementTimeConstraint += " AND evenement.einddatum < " + toTime;

      if (listtype.equals("Betaalwijze")){ // *** path depends on inschrijvingen ***

         // *** put names on tsNames ***
         String sStatName = "";
         tsNames.add("");
         tsNames.add("Contant");
         NodeList nlPaymentTypes = cloud.getNodeManager("payment_type").getList(null,null,null);
         for(int i = 0; i < nlPaymentTypes.size(); i++) {
            Node nPaymentTypes = nlPaymentTypes.getNode(i);
            sStatName = nPaymentTypes.getStringValue("naam");
            if (!tsNames.contains(sStatName)){
               tsNames.add(sStatName);
            }
         }

         // *** count total for tmNames ***
         Iterator i = tsNames.iterator();
         while (i.hasNext()) {
            sStatName = (String)i.next();
            String sConstraints = evenementTimeConstraint + " AND inschrijvingen.betaalwijze = '" + sStatName + "'";
            int iResultCounts = getCounts(cloud,"",sConstraints,listtype,statstype);
            if(!removeZeros||iResultCounts!=0) {
               iTotal += iResultCounts;
               tmStatistics.put(sStatName,new Integer(iResultCounts));
            }
         }
      } else if (listtype.equals("Bestelwijze")){ // *** path depends on inschrijvingen, e.g. users,schrijver, ***

        // *** put names on tsNames ***
        String sStatName = "";
        String sNumber = "";
        tmNames.put("website", "-1");
        tmNames.put("backoffice", "");

        log.info("Bestelwijze: before nlUsers: "+(new Date()).getTime());

        NodeList nlUsers = cloud.getList("natuurin_rubriek","rubriek,rolerel,users,schrijver,inschrijvingen",
           "users.achternaam,users.voornaam,users.tussenvoegsel,users.account,users.number",null, null, null, null, true);

        log.info("Bestelwijze: after nlUsers: "+(new Date()).getTime());

        for(int i = 0; i < nlUsers.size(); i++) {
           Node nUsers = nlUsers.getNode(i);
           sStatName = nUsers.getStringValue("users.achternaam") + ", " +
              nUsers.getStringValue("users.voornaam") + " " +
              nUsers.getStringValue("users.tussenvoegsel");
           if(sStatName.trim().equals(",")) {
              sStatName = nUsers.getStringValue("users.account");
           }
           sStatName = "backoffice: " + sStatName;
           sNumber = nUsers.getStringValue("users.number");

           if (!tmNames.containsKey(sStatName) && (!sStatName.equals(""))) {
              tmNames.put(sStatName, sNumber);
           }
        }

        log.info("Bestelwijze: before countTotal: "+(new Date()).getTime());

        // *** count total for tmNames ***
        String sRealNumber = "";
        String sConstraints = "";
        Set set = tmNames.entrySet();
        Iterator i = set.iterator();
        tmStatistics.put("backoffice",new Integer(0));
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            sStatName = (String) me.getKey();
            sRealNumber = (String)tmNames.get(sStatName);
            sConstraints = evenementTimeConstraint;
            if(sRealNumber.equals("-1")) {
               sConstraints += " AND inschrijvingen.ticket_office = '" + sStatName + "'";
            } else {
               sConstraints += " AND inschrijvingen.ticket_office = 'backoffice'";
            }

            int iResultCounts = getCounts(cloud,sRealNumber,sConstraints,listtype,statstype);

            if (sStatName.startsWith("backoffice:")&&iResultCounts!=0){
               tmStatistics.put("backoffice",
               new Integer(((Integer)tmStatistics.get("backoffice")).intValue()+ iResultCounts));
            }
            if(!removeZeros||iResultCounts!=0) {
               if(!sStatName.equals("backoffice")) { // *** prevent from counting backoffice twice
                  iTotal += iResultCounts;
                  tmStatistics.put(sStatName,new Integer(iResultCounts));
               }
            }
         }

        log.info("Bestelwijze: after countTotal: "+(new Date()).getTime());

      } else if (listtype.equals("Activiteitstype")) { // *** path is evenement,posrel, ***

         // *** put names on tsNames ***
         String sStatName = "";
         String sNumber = "";
         NodeList nlEvenementType = cloud.getList("","evenement_type","evenement_type.naam,evenement_type.number",null,null,null,null,false);
         for (int j = 0; j < nlEvenementType.size(); j++ ){
            Node nEvenementType = nlEvenementType.getNode(j);
            sStatName = nEvenementType.getStringValue("evenement_type.naam");
            sNumber = nEvenementType.getStringValue("evenement_type.number");
            if (!tmNames.containsKey(sStatName)) {
               tmNames.put(sStatName, sNumber);
            }
         }

         // *** count total for tmNames ***
         String sRealNumber = "";
         Set set = tmNames.entrySet();
         Iterator i = set.iterator();
         while (i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            sStatName = (String) me.getKey();
            sRealNumber = (String)tmNames.get(sStatName);

            sRealNumber = getEvenementen(cloud, sRealNumber, "evenement_type,related,evenement",evenementTimeConstraint);

            if(!sRealNumber.equals("")) {
               int iResultCounts = getCounts(cloud,sRealNumber,evenementTimeConstraint,listtype,statstype);
               if(!removeZeros||iResultCounts!=0) {
                  iTotal += iResultCounts;
                  tmStatistics.put(sStatName,new Integer(iResultCounts));
               }
            }
         }
      } else if (listtype.equals("Provincie / Natuurgebied")){ // *** path is evenement,posrel, ***

         // *** put names on tsNames ***
         String sStatName = "";
         String sNumber = "";
         NodeList nlNatuurgebieden = cloud.getList("","natuurgebieden,pos4rel,provincies",
            "natuurgebieden.naam,natuurgebieden.number,provincies.naam",null,null,null,null,false);
         for (int j = 0; j < nlNatuurgebieden.size(); j++ ){
            Node nNatuurgebieden = nlNatuurgebieden.getNode(j);
            sStatName = nNatuurgebieden.getStringValue("provincies.naam") +
               "/" + nNatuurgebieden.getStringValue("natuurgebieden.naam");
            sNumber = Integer.toString(nNatuurgebieden.getIntValue(
               "natuurgebieden.number"));
            if (!tmNames.containsKey(sStatName)) {
               tmNames.put(sStatName, sNumber);
            }
         }

         // *** count total for tmNames ***
         String sRealNumber = "";
         String sRealNodepath = "evenement,posrel,";
         Set set = tmNames.entrySet();
         Iterator i = set.iterator();
         while (i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            sStatName = (String) me.getKey();
            sRealNumber = (String)tmNames.get(sStatName);

            sRealNumber = getEvenementen(cloud, sRealNumber, "natuurgebieden,related,evenement",evenementTimeConstraint);
            if(!sRealNumber.equals("")) {
               int iResultCounts = getCounts(cloud,sRealNumber,evenementTimeConstraint,listtype,statstype);
               if(!removeZeros||iResultCounts!=0) {
                  iTotal += iResultCounts;
                  tmStatistics.put(sStatName, new Integer(iResultCounts));
               }
            }
         }
      }
      return tmStatistics;
   }

   public TreeMap getStatistics(Cloud cloud, long fromTime, long toTime, int period, String listtype, String statstype){
      return getStatistics(cloud, fromTime, toTime, period, listtype, statstype, false);
   }

   public int getTotal() {
      return iTotal;
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

   public String dateString(long timeInSec) {
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

   public String timeString(long timeInSec) {
      String timeString = "";
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(timeInSec*1000);
      if(cal.get(Calendar.HOUR_OF_DAY)<10) { timeString += "0"; }
      timeString += cal.get(Calendar.HOUR_OF_DAY);
      if(cal.get(Calendar.MINUTE)<10) { timeString += "0"; }
      timeString += cal.get(Calendar.MINUTE);
      return timeString;
   }

   public String write(Cloud cloud, long fromTime, long toTime, int period, String listtype) throws IOException, WriteException {

      ArrayList alStatstypes = new ArrayList();
      alStatstypes.add("inschrijvingen");
      alStatstypes.add("deelnemers");
      alStatstypes.add("leden");
      alStatstypes.add("niet leden");
      alStatstypes.add("opbrengst");
      alStatstypes.add("activiteiten");
      alStatstypes.add("geannuleerde activiteiten");

      String dateString = dateString(fromTime) + "_" + dateString(toTime);
      String fileName = listtype.replaceAll("/","_").replaceAll(" ","") + "_" + dateString + "_stats.xls";

      String sAttachmentId = "";
      NodeList nl = cloud.getList("","events_attachments","events_attachments.number","events_attachments.filename = '" + fileName + "'",null,null,null,false);
      if(nl.isEmpty()) {
         WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.getTempDir() + fileName));
         WritableSheet sheet = workbook.createSheet("CAD Statistieken", 0);
         WritableCellFormat wrappedText = new WritableCellFormat(WritableWorkbook.ARIAL_10_PT);
         wrappedText.setWrap(true);

         sheet.mergeCells(0,0,3,0);
         sheet.setColumnView(0,25);
         sheet.setColumnView(1,15);
         sheet.setColumnView(2,10);
         sheet.setColumnView(3,10);
         sheet.setColumnView(4,10);
         sheet.setColumnView(5,10);
         sheet.setColumnView(6,10);
         sheet.setColumnView(7,15);

         Label lStatsPeriod = new Label(0,0,getStatsperiod(fromTime,toTime,period));
         sheet.addCell(lStatsPeriod);

         Label lListType = new Label(0,1,listtype);
         sheet.addCell(lListType);

         OptionedStats os = new OptionedStats();

         int j = 1; // *** rows ***

         Iterator i = alStatstypes.iterator();
         while (i.hasNext()){
            String sStatsType = (String)i.next();
            TreeMap tmAllStats = os.getStatistics(cloud,fromTime,toTime,period,listtype,sStatsType);
            Set set = tmAllStats.entrySet();
            Iterator it = set.iterator();
            int k = 2; // *** columns ***
            while (it.hasNext()){
               Map.Entry me = (Map.Entry)it.next();
               String sListTypeName = (String) me.getKey();
               if (j==1){
                  Label lListTypeName = new Label(0,k,sListTypeName,wrappedText);
                  sheet.addCell(lListTypeName);
               }
               if ( !( ((j==6) && (listtype.equals("Betaalwijze"))) ||
                    ((j==7) && (listtype.equals("Betaalwijze"))) ||
                    ((j==6) && (listtype.equals("Bestelwijze"))) ||
                    ((j==7) && (listtype.equals("Bestelwijze"))) ) ){

                  Label lStatsType = new Label(j,1,sStatsType);
                  sheet.addCell(lStatsType);
                  Integer sValue = (Integer) me.getValue();
                  if (sValue.intValue()!=0){
                     jxl.write.Number nValue = null;
                     if(sStatsType.equals("opbrengst")) {
                        nValue = new jxl.write.Number(j, k, (sValue.intValue()/100));
                     } else {
                        nValue = new jxl.write.Number(j, k, sValue.intValue());
                     }
                     sheet.addCell(nValue);
                  }
               }
               k++;
            }
            j++;
         }
         workbook.write();
         workbook.close();

         String sFile = NatMMConfig.getTempDir() + fileName;
         File f = new File(sFile);
         int fsize = (int)f.length();
         byte[] thedata = new byte[fsize];
         try {
            FileInputStream instream = new FileInputStream(f);
            instream.read(thedata);
            NodeManager nmAttachmentsManager = cloud.getNodeManager("events_attachments");
            Node attNode = nmAttachmentsManager.createNode();
            attNode.setValue("titel","CAD Statistics " + listtype + "_" + dateString);
            attNode.setValue("title","CAD Statistics " + listtype + "_" + dateString);
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
      } else {
           sAttachmentId = nl.getNode(0).getStringValue("events_attachments.number");
      }
      return sAttachmentId;
   }
}
