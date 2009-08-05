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

import nl.leocms.util.tools.HtmlCleaner;
import nl.leocms.evenementen.*;
import nl.leocms.evenementen.forms.*;
import nl.leocms.util.DoubleDateNode;
import nl.leocms.applications.NatMMConfig;

public class ExcelWriter {
   private static final Logger log = Logging.getLoggerInstance(ExcelWriter.class);
   
   public String createEventSubscribeAttachment(Cloud cloud, String sEvenementNumber) throws IOException, WriteException {
      // writes all the subscriptions for this date (used in subscribe.jsp)

      DoubleDateNode ddn = new DoubleDateNode(cloud.getNode(sEvenementNumber));
      String fileName = "aanmeldingen_voor_activiteit_" + sEvenementNumber + " " + ".xls";
      WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.getTempDir() + fileName));

      Evenement ev = new Evenement();
      String sParentEvent = Evenement.findParentNumber(sEvenementNumber);
      Node nParentNode = cloud.getNode(sParentEvent);

      createEventSubscribeSheet(cloud,nParentNode,sEvenementNumber,workbook,ddn,0);

      workbook.write();
      workbook.close();

      return createAttachmentNode(cloud,fileName,"Event subscribe " + sParentEvent);
   }

   public String createEventDatesAttachment(Cloud cloud, String sEvenementNumber) throws IOException, WriteException {
      // writes all the dates for this event (used in subscribe.jsp)
      Evenement ev = new Evenement();
      String sParentEvent = Evenement.findParentNumber(sEvenementNumber);
      String fileName = "alle_data_voor_activiteit_" + sParentEvent + ".xls";
      WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.getTempDir() + fileName));

      HtmlCleaner hc = new HtmlCleaner();
      Node nParentNode = cloud.getNode(sParentEvent);
      String sSheetTitle = HtmlCleaner.stripText(nParentNode.getStringValue("titel"));
      if (sSheetTitle.length()>30) {
         sSheetTitle = sSheetTitle.substring(0,30);
      }

      createEventDatesSheet(cloud,hc,nParentNode,workbook,0,sSheetTitle, null);

      workbook.write();
      workbook.close();

      return createAttachmentNode(cloud,fileName,"Event dates " + sParentEvent);
   }

   public String createAllEventSubsribeAttachment(Cloud cloud, String sEvenementNumber) throws IOException, WriteException {
      // writes all subscriptions of the selected event (used in evenementen.jsp)
      log.debug("createAllEventSubsribeAttachment for data " + sEvenementNumber);
      Evenement ev = new Evenement();
      String sParentEvent = Evenement.findParentNumber(sEvenementNumber);
      Node nParentNode = cloud.getNode(sParentEvent);
      DoubleDateNode ddnParent = new DoubleDateNode(nParentNode);

      String fileName = "alle_data_met_aanmeldingen_voor_activiteit_" + sParentEvent + ".xls";
      WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.getTempDir() + fileName));

      NodeList nl = ev.getSortedList(cloud, sParentEvent);
//     ** Don't use very database intensive code that is not needed, if-statement will always be executed.
//      NodeList nls = cloud.getList(sParentEvent,"evenement,partrel,evenement1,posrel,inschrijvingen","evenement1.number,evenement1.begindatum",null,"evenement1.begindatum","UP",null,true);
//      boolean bOneDateWithoutSubscriptions = (nl.size()!= nls.size());
      int j = 0;
//      if(bOneDateWithoutSubscriptions) {
//         String sThisEvenementNumber = (String)nl.getNode(0).getStringValue("evenement1.number");
         HtmlCleaner hc = new HtmlCleaner();
         String sSheetTitle = HtmlCleaner.stripText(nParentNode.getStringValue("titel"));
         if (sSheetTitle.length()>28) {
            sSheetTitle = sSheetTitle.substring(0,28);
         }
         createEventDatesSheet(cloud,hc,nParentNode,workbook,j,sSheetTitle, null);
         j++;
//      } ** //End of database intensive code.
      for (int i = 0; i < nl.size(); i++){
         Node event = cloud.getNode(nl.getNode(i).getStringValue("evenement.number"));
         if(event.getRelatedNodes("inschrijvingen").size()!=0) {
            DoubleDateNode ddn = new DoubleDateNode(event);
            createEventSubscribeSheet(cloud,nParentNode,nl.getNode(i).getStringValue("evenement.number"),workbook,ddn,j);
            j++;
         }
      }

      workbook.write();
      workbook.close();

      return createAttachmentNode(cloud,fileName,"All Event Subscribe " + sParentEvent);
   }


   public String createAllEventDatesAttachment(Cloud cloud, String allEvents) throws IOException, WriteException{
      // writes all dates of all selected events (used in evenementen.jsp)
      log.debug("createAllEventDatesAttachment for events " + allEvents);
      LinkedList ll = new LinkedList();
      while (allEvents.indexOf(",")>-1){
         ll.add(allEvents.substring(0,allEvents.indexOf(",")));
         allEvents = allEvents.substring(allEvents.indexOf(",")+1);
      }
      ll.add(allEvents);

      Calendar cal = Calendar.getInstance();
      int iYear = cal.get(Calendar.YEAR)%100;
      String sDate = "";
      if (iYear<10){ sDate += "0"; }
      sDate += iYear + ".";
      int iMonth = cal.get(Calendar.MONTH)+1;
      if (iMonth<10) {sDate += "0";}
      sDate += iMonth + ".";
      int iDay = cal.get(Calendar.DAY_OF_MONTH);
      if (iDay<10) {sDate += "0";}
      sDate += iDay + "_";
      int iHour = cal.get(Calendar.HOUR_OF_DAY);
      if (iHour<10) {sDate +="0";}
      sDate += iHour + ".";
      int iMinute = cal.get(Calendar.MINUTE);
      if (iMinute<10) {sDate +="0";}
      sDate += iMinute + ".";
      int iSecond = cal.get(Calendar.SECOND);
      if (iSecond<10) {sDate +="0";}
      sDate += iSecond;

      String fileName = "alle_geselecteerde_activiteiten_" + sDate + ".xls";
      WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.getTempDir() + fileName));

      // create flyersheet
      WritableSheet sheetFlyer = workbook.createSheet("flyer", 9999);
      sheetFlyer.setColumnView(0,40);
      sheetFlyer.setColumnView(1,40);
      sheetFlyer.setColumnView(2,20);
      sheetFlyer.setColumnView(3,20);
      sheetFlyer.setColumnView(4,20);
      
      ArrayList alXlsListTitles = new ArrayList();
      
      ArrayList eventFlyerList = new ArrayList();
      int i = 0;
      Iterator it = ll.iterator();
      while (it.hasNext()){
         String sEvenementNumber = (String)it.next();
         String sParentEvent = Evenement.findParentNumber(sEvenementNumber);
         Node nParentNode = cloud.getNode(sParentEvent);
         HtmlCleaner hc = new HtmlCleaner();
         String sSheetTitle = HtmlCleaner.stripText(nParentNode.getStringValue("titel"));
         if (sSheetTitle.length()>28) {
            sSheetTitle = sSheetTitle.substring(0,28);
         }
         String sSheetRealTitle = sSheetTitle;
         int j = 1;
         while (alXlsListTitles.contains(sSheetRealTitle)){
            sSheetRealTitle = sSheetTitle + "." + j;
            j++;
         }
         alXlsListTitles.add(sSheetRealTitle);
         createEventDatesSheet(cloud, hc, nParentNode, workbook, i, sSheetRealTitle, eventFlyerList);         
         i++;
      }
 
      // create events flyer
      int j = 0;
      // sort events by DoubleDateNode
      Collections.sort(eventFlyerList);
      
      Iterator itEventList = eventFlyerList.iterator();    
      while (itEventList.hasNext()){        
         EventData event = (EventData) itEventList.next();
         
         // get location of the event
         NodeList nl1 = cloud.getList(event.getEventNumber(),"evenement,related,natuurgebieden","natuurgebieden.naam",null,null,null,null,false);
         for(int k = 0; k < nl1.size(); k++){
            sheetFlyer.addCell(new Label(0, j, nl1.getNode(k).getStringValue("natuurgebieden.naam")));
         }         
         
         // retrieve event details
         String eventName = event.getEventName();
         String eventDate = event.getEventDate().getReadableDate(" ", true);
         String eventYear = event.getEventDate().getReadableYear();
         String eventTime = event.getEventDate().getReadableTime(false);
         
         // add details to flyer sheer
         sheetFlyer.addCell(new Label(1, j, eventName));
         sheetFlyer.addCell(new Label(2, j, eventDate));
         sheetFlyer.addCell(new Label(3, j, eventYear));
         sheetFlyer.addCell(new Label(4, j, eventTime));
         j++;
      }
      
      workbook.write();
      workbook.close();

      return createAttachmentNode(cloud,fileName,"All Event dates " + sDate);
   }
   
   public int writeNameString(LinkedList llXlsData, int counter, Node nParentNode){
      llXlsData.add(new Label(0,counter,"Naam"));
      llXlsData.add(new Label(1,counter,nParentNode.getStringValue("titel")));

      return ++counter;
   }

   public int writeStrings1(Cloud cloud, HtmlCleaner hc, LinkedList llXlsData, int counter, Node nParentNode) {
      // Bijzonderheden, Activiteitstype, Natuurgebied, Provincie, Extra info
      String sParentNumber = nParentNode.getStringValue("number");
      llXlsData.add(new Label(0,counter,"Bijzonderheden"));
      String sText = hc.cleanText(nParentNode.getStringValue("tekst"),"<",">","");
      sText = hc.filterTextEntities(sText);
      sText = hc.replace(sText, "\n", "");
      llXlsData.add(new Label(1,counter,sText));

      counter++;
      NodeList nl1 = cloud.getList(sParentNumber,"evenement,related,evenement_type","evenement_type.naam",null,null,null,null,false);
      for(int i = 0; i < nl1.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Activiteitstype"));
         }
         llXlsData.add(new Label(1,counter+i,nl1.getNode(i).getStringValue("evenement_type.naam")));
      }
      counter += nl1.size();

      NodeList nl2 = cloud.getList(sParentNumber,"evenement,related,natuurgebieden","natuurgebieden.naam",null,null,null,null,false);
      for(int i = 0; i < nl2.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Natuurgebied"));
         }
         llXlsData.add(new Label(1,counter+i,nl2.getNode(i).getStringValue("natuurgebieden.naam")));
      }
      counter += nl2.size();

      NodeList nl3 = cloud.getList(sParentNumber,"evenement,related,natuurgebieden,pos4rel,provincies","provincies.naam",null,null,null,null,false);
      llXlsData.add(new Label(0,counter,"Provincie"));
      if (nl3.size()>0){
         llXlsData.add(new Label(1,counter,nl3.getNode(0).getStringValue("provincies.naam")));
      }
      counter++;

      NodeList nl4 = cloud.getList(sParentNumber,"evenement,readmore,extra_info","extra_info.omschrijving",null,null,null,null,false);
      for(int i = 0; i < nl4.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Extra info"));
         }
         String sDescr = hc.cleanText(nl4.getNode(i).getStringValue("extra_info.omschrijving"),"<",">","");
         sDescr = hc.filterTextEntities(sDescr);
         sDescr = hc.replace(sDescr, "\n", "");
         llXlsData.add(new Label(1,counter+i,sDescr));
      }
      counter += nl4.size();

      return counter;
   }

   public int writeVertrekpuntString(Cloud cloud, HtmlCleaner hc, LinkedList llXlsData, int counter, Node nParentNode) {
      String sParentNumber = nParentNode.getStringValue("number");
      NodeList nl6 = cloud.getList(sParentNumber,"evenement,posrel,vertrekpunten","vertrekpunten.titel,vertrekpunten.tekst",null,null,null,null,false);
      for(int i = 0; i < nl6.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Vertrekpunt"));
         }
         llXlsData.add(new Label(1,counter+i*2,nl6.getNode(i).getStringValue("vertrekpunten.titel")));
         String sText = hc.cleanText(nl6.getNode(i).getStringValue("vertrekpunten.tekst"),"<",">","");
         sText = hc.filterTextEntities(sText);
         sText = hc.replace(sText, "\n", "");
         llXlsData.add(new Label(1,counter+i*2+1,sText));
      }
      counter += nl6.size()*2;
      return counter;
   }

   public int writeStrings2(Cloud cloud, HtmlCleaner hc, LinkedList llXlsData, int counter, Node nParentNode){
      // Max aantal deelnemers, Min aantal deelnemers, Interne notitie, Betrokken afdelingen, Betrokken medewerkers, Bevestiging tekst
      String sParentNumber = nParentNode.getStringValue("number");
      llXlsData.add(new Label(0,counter,"Max aantal deelnemers"));
      int iMax = nParentNode.getIntValue("max_aantal_deelnemers");
      if (iMax==-1) {iMax = 9999;}
      llXlsData.add(new Label(1,counter,new Integer(iMax).toString()));
      counter++;

      llXlsData.add(new Label(0,counter,"Min aantal deelnemers"));
      int iMin = nParentNode.getIntValue("min_aantal_deelnemers");
      if (iMin==-1) {iMin = 0;}
      llXlsData.add(new Label(1,counter,new Integer(iMin).toString()));
      counter++;

      llXlsData.add(new Label(0,counter,"Interne notitie"));
      String sDescr = hc.cleanText(nParentNode.getStringValue("omschrijving"),"<",">","");
      sDescr = hc.filterTextEntities(sDescr);
      sDescr = hc.replace(sDescr, "\n", "");
      llXlsData.add(new Label(1,counter,sDescr));
      counter++;

      NodeList nl8 = cloud.getList(sParentNumber,"evenement,readmore,afdelingen","readmore.readmore,afdelingen.naam,afdelingen.telefoonnummer",null,null,null,null,false);
      for(int i = 0; i < nl8.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Betrokken afdelingen"));
         }
         String sReadmore = nl8.getNode(i).getStringValue("readmore.readmore");
         if (sReadmore.equals("1")) {sReadmore = "organisator";}
         else if (sReadmore.equals("2")) {sReadmore = "boekende afdeling";}
         else {sReadmore = "overig";}
         llXlsData.add(new Label(1,counter+i,sReadmore));
         String sInfo = nl8.getNode(i).getStringValue("afdelingen.naam");
         if (!nl8.getNode(i).getStringValue("afdelingen.naam").equals("")){
            sInfo += ", " + nl8.getNode(i).getStringValue("afdelingen.telefoonnummer");
         }
         llXlsData.add(new Label(2,counter+i,sInfo));
      }
      counter += nl8.size();

      NodeList nl9 = cloud.getList(sParentNumber,"evenement,readmore,medewerkers","readmore.readmore,medewerkers.titel,medewerkers.companyphone,medewerkers.email",null,null,null,null,false);
      for(int i = 0; i < nl9.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Betrokken medewerkers"));
         }
         String sInfo = nl9.getNode(i).getStringValue("medewerkers.titel");
         if (!nl9.getNode(i).getStringValue("medewerkers.companyphone").equals("")){
            sInfo += ", " + nl9.getNode(i).getStringValue("medewerkers.companyphone");
         }
         if (!nl9.getNode(i).getStringValue("medewerkers.email").equals("")){
            sInfo += ", " + nl9.getNode(i).getStringValue("medewerkers.email");
         }

         String sReadmore = nl9.getNode(i).getStringValue("readmore.readmore");
         if (sReadmore.equals("1")) {sReadmore = "aanspreekpunt";}
         else if (sReadmore.equals("2")) {sReadmore = "excursieleider";}
         else {sReadmore = "overig";}
         llXlsData.add(new Label(1,counter+i,sReadmore));
         llXlsData.add(new Label(2,counter+i,sInfo));
      }
      counter += nl9.size();

      NodeList nl10 = cloud.getList(sParentNumber,"evenement,posrel,bevestigings_teksten","bevestigings_teksten.titel,bevestigings_teksten.intro",null,null,null,null,false);
      for(int i = 0; i < nl10.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Bevestiging tekst"));
         }
         String sText = hc.cleanText(nl10.getNode(i).getStringValue("bevestigings_teksten.titel") + " " + nl10.getNode(i).getStringValue("bevestigings_teksten.intro"),"<",">","");
         sText = hc.filterTextEntities(sText);
         sText = hc.replace(sText, "\n", "");
         llXlsData.add(new Label(1,counter+i,sText));
      }
      counter += nl10.size();
      return counter;
   }

   public int writeKosten(Cloud cloud, LinkedList llXlsData, int counter, Node nParentNode) {
      String sParentNumber = nParentNode.getStringValue("number");
      NodeList nl5 = cloud.getList(sParentNumber,"evenement,posrel,deelnemers_categorie","posrel.pos,deelnemers_categorie.naam ",null,null,null,null,false);
      for(int i = 0; i < nl5.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Kosten"));
         }
         llXlsData.add(new Label(1,counter+i,nl5.getNode(i).getStringValue("deelnemers_categorie.naam")));
         llXlsData.add(new Label(2,counter+i,priceFormating(nl5.getNode(i).getIntValue("posrel.pos"))));
      }
      counter += nl5.size();
      return counter;

   }

   public int writeKostenEventSubscribe(Cloud cloud, LinkedList llXlsData, int counter, String sEvenementNumber){
      int iTotalParticipants = 0;
      NodeList nl5 = cloud.getList(sEvenementNumber,"evenement,posrel,deelnemers_categorie","posrel.pos,deelnemers_categorie.naam,deelnemers_categorie.aantal_per_deelnemer",null,null,null,null,false);
      for(int i = 0; i < nl5.size(); i++){
         if (i==0) {
            llXlsData.add(new Label(0,counter,"Kosten"));
         }
         llXlsData.add(new Label(1,counter+i,nl5.getNode(i).getStringValue("deelnemers_categorie.naam")));
         llXlsData.add(new Label(2,counter+i,priceFormating(nl5.getNode(i).getIntValue("posrel.pos"))));
         int iNumberPerParticipant = 1;
         int iParticipantsInCat = 0;
         iNumberPerParticipant = nl5.getNode(i).getIntValue("deelnemers_categorie.aantal_per_deelnemer");
         NodeList nl51 = cloud.getList(nl5.getNode(i).getStringValue("deelnemers_categorie.number"),"deelnemers_categorie,related,deelnemers,posrel,inschrijvingen,posrel,evenement","deelnemers.bron","evenement.number LIKE '" + sEvenementNumber + "'",null,null,null,false);
         for (int j = 0; j < nl51.size(); j++){
            iParticipantsInCat += nl51.getNode(j).getIntValue("deelnemers.bron");
         }
         iTotalParticipants += iParticipantsInCat*iNumberPerParticipant;
         llXlsData.add(new Label(3,counter+i,new Integer(iParticipantsInCat*iNumberPerParticipant).toString()));
      }
      counter += nl5.size();
      llXlsData.add(new Label(1,counter,"TOTAAL"));
      llXlsData.add(new Label(3,counter,new Integer(iTotalParticipants).toString()));

      return ++counter;

   }

   public int writeDataEventDates(Cloud cloud, LinkedList llXlsData, int counter, Node nParentNode, ArrayList eventFlyerList){

      String sParentNumber = nParentNode.getStringValue("number");
      Evenement ev = new Evenement();
      NodeList nl7 = ev.getSortedList(cloud, sParentNumber);
      llXlsData.add(new Label(0,counter,"Data"));
      for(int i = 0; i < nl7.size(); i++){
         DoubleDateNode ddn = new DoubleDateNode(cloud.getNode(nl7.getNode(i).getStringValue("evenement.number")));
         llXlsData.add(new Label(1,counter,ddn.getReadableDate()));
         llXlsData.add(new Label(2,counter,ddn.getReadableTime()));
         
         // add data tot eventFlyerList
         if(eventFlyerList != null) {
            eventFlyerList.add(new EventData(sParentNumber, nParentNode.getStringValue("titel"), ddn));
         }
         
         Node childEvent = cloud.getNode(nl7.getNode(i).getStringValue("evenement.number"));
         int aanmeldingSize = childEvent.getRelatedNodes("inschrijvingen").size();
         
         if (aanmeldingSize==0) {
            llXlsData.add(new Label(3,counter,"geen aanmeldingen"));
         } else {
        	llXlsData.add(new Label(3,counter,String.valueOf(aanmeldingSize)));
         }
         counter++;
      }
      return counter;
   }

   public void createEventDatesSheet(Cloud cloud,  HtmlCleaner hc, Node nParentNode, WritableWorkbook workbook, int sheetNumber, String sSheetTitle, ArrayList eventFlyerList) throws WriteException{

      log.debug("createEventDatesSheet for parent event " + nParentNode.getStringValue("number"));
      WritableSheet sheet = workbook.createSheet(sSheetTitle, sheetNumber);
      sheet.setColumnView(0,23);
      sheet.setColumnView(1,23);

      WritableCellFormat wrappedText = new WritableCellFormat(WritableWorkbook.ARIAL_10_PT);
      wrappedText.setWrap(true);

      LinkedList llXlsData = new LinkedList();

      int counter = writeNameString(llXlsData,0,nParentNode);
      counter = writeStrings1(cloud,hc, llXlsData,counter,nParentNode);
      counter = writeKosten(cloud,llXlsData,counter,nParentNode);
      counter = writeVertrekpuntString(cloud,hc,llXlsData,counter,nParentNode);
      counter = writeDataEventDates(cloud,llXlsData,counter,nParentNode,eventFlyerList);
      counter = writeStrings2(cloud,hc,llXlsData,counter,nParentNode);
      counter = writeDownloadDate(llXlsData,counter);

      Iterator it = llXlsData.iterator();
      while (it.hasNext()){
         sheet.addCell((Label)it.next());
      }

   }

   public int writeSubscriptions(Cloud cloud, LinkedList llXlsData, int counter, String sEvenementNumber){
      llXlsData.add(new Label(0,counter,"nummer"));
      llXlsData.add(new Label(1,counter,"m/v"));
      llXlsData.add(new Label(2,counter,"voornaam"));
      llXlsData.add(new Label(3,counter,"voorl."));
      llXlsData.add(new Label(4,counter,"tussenv."));
      llXlsData.add(new Label(5,counter,"achternaam"));
      llXlsData.add(new Label(6,counter,"straatnaam"));
      llXlsData.add(new Label(7,counter,"huisnummer"));
      llXlsData.add(new Label(8,counter,"postcode"));
      llXlsData.add(new Label(9,counter,"plaats"));
      llXlsData.add(new Label(10,counter,"land"));
      llXlsData.add(new Label(11,counter,"aantal"));
      llXlsData.add(new Label(12,counter,"categorie"));
      llXlsData.add(new Label(13,counter,"kosten"));
      llXlsData.add(new Label(14,counter,"betalingswijze"));
      llXlsData.add(new Label(15,counter,"telefoon"));
      llXlsData.add(new Label(16,counter,"email"));
      llXlsData.add(new Label(17,counter,"lidnummer"));
      llXlsData.add(new Label(18,counter,"bron"));
      llXlsData.add(new Label(19,counter,"bijzonderheden"));
      llXlsData.add(new Label(20,counter,"website"));
      llXlsData.add(new Label(21,counter,"status"));
      counter++;

      TreeMap subscriptions = new TreeMap();
      NodeList nl11 = cloud.getList(sEvenementNumber,"evenement,posrel,inschrijvingen,posrel,deelnemers","inschrijvingen.number,deelnemers.lastname",null,"deelnemers.number","UP",null,false);
      for(int i = 0; i < nl11.size(); i++){
         String key = nl11.getNode(i).getStringValue("deelnemers.lastname");
         String tmpKey = key;
         int j =0;
         while(subscriptions.containsKey(key)) { key = tmpKey + j; j++; }
         subscriptions.put(key,nl11.getNode(i).getStringValue("inschrijvingen.number"));
      }
      int totalSubscriptions = 0;
      int totalCosts = 0;
      while(subscriptions.size()>0) {
         String sThisKey = (String) subscriptions.firstKey();
         String sThisSubscription = (String) subscriptions.get(sThisKey);
         Node nThisSubscription = cloud.getNode(sThisSubscription);
         NodeList nl12 = cloud.getList(sThisSubscription,"inschrijvingen,posrel,deelnemers","",null,null,null,null,false);
         for(int i = 0; i < nl12.size(); i++){
            llXlsData.add(new Label(0,counter,sThisSubscription));
            String sGender = nl12.getNode(i).getStringValue("deelnemers.gender");
            if (sGender.equals("female")) {sGender = "Mevr";}
            else if (sGender.equals("male")) {sGender = "Dhr";}
            llXlsData.add(new Label(1,counter,sGender));
            llXlsData.add(new Label(2,counter,nl12.getNode(i).getStringValue("deelnemers.firstname")));
            llXlsData.add(new Label(3,counter,nl12.getNode(i).getStringValue("deelnemers.initials")));
            llXlsData.add(new Label(4,counter,nl12.getNode(i).getStringValue("deelnemers.suffix")));
            llXlsData.add(new Label(5,counter,nl12.getNode(i).getStringValue("deelnemers.lastname")));
            llXlsData.add(new Label(6,counter,nl12.getNode(i).getStringValue("deelnemers.straatnaam")));
            llXlsData.add(new Label(7,counter,nl12.getNode(i).getStringValue("deelnemers.huisnummer")));
            llXlsData.add(new Label(8,counter,nl12.getNode(i).getStringValue("deelnemers.postcode")));
            llXlsData.add(new Label(9,counter,nl12.getNode(i).getStringValue("deelnemers.plaatsnaam")));
            llXlsData.add(new Label(10,counter,nl12.getNode(i).getStringValue("deelnemers.land")));
            llXlsData.add(new Label(11,counter,nl12.getNode(i).getStringValue("deelnemers.bron")));
            totalSubscriptions += nl12.getNode(i).getIntValue("deelnemers.bron");
            NodeList nl13 = cloud.getList(nl12.getNode(i).getStringValue("deelnemers.number"),"deelnemers,related,deelnemers_categorie","deelnemers_categorie.naam",null,null,null,null,false);
            if (nl13.size()>0){
               llXlsData.add(new Label(12, counter,nl13.getNode(0).getStringValue("deelnemers_categorie.naam")));
            }
            NodeList nl14 = cloud.getList(nl12.getNode(i).getStringValue("deelnemers.number"),"deelnemers,posrel,inschrijvingen","inschrijvingen.betaalwijze,posrel.pos",null,null,null,null,false);
            if (nl14.size()>0){
               String sPaymentType = nl14.getNode(0).getStringValue("inschrijvingen.betaalwijze");
               if (sPaymentType == null || sPaymentType.equals("Contant")) {
                  llXlsData.add(new Label(13,counter,priceFormating(nl14.getNode(0).getIntValue("posrel.pos"))));
                  totalCosts += nl14.getNode(0).getIntValue("posrel.pos");
               }
               llXlsData.add(new Label(14,counter,sPaymentType));
            }
            llXlsData.add(new Label(15,counter,nl12.getNode(i).getStringValue("deelnemers.privatephone")));
            llXlsData.add(new Label(16,counter,nl12.getNode(i).getStringValue("deelnemers.email")));
            llXlsData.add(new Label(17,counter,nl12.getNode(i).getStringValue("deelnemers.lidnummer")));
            llXlsData.add(new Label(18,counter,nThisSubscription.getStringValue("source")));
            llXlsData.add(new Label(19,counter,nThisSubscription.getStringValue("description")));
            String sIsWebsite = nThisSubscription.getStringValue("ticket_office");
            if (sIsWebsite.equals("website")) {sIsWebsite = "j";} else {sIsWebsite = "n";}
            llXlsData.add(new Label(20,counter,sIsWebsite));
            NodeList nl15 = cloud.getList(sThisSubscription,"inschrijvingen,related,inschrijvings_status","inschrijvings_status.naam",null,null,null,null,false);
            if (nl15.size()>0){
               llXlsData.add(new Label(21, counter,nl15.getNode(0).getStringValue("inschrijvings_status.naam")));
            }
            counter++;
         }
         subscriptions.remove(sThisKey);
      }
      llXlsData.add(new Label(11,counter,"" + totalSubscriptions));
      llXlsData.add(new Label(13,counter,priceFormating(totalCosts)));
      counter++;
      return counter;
   }

   public void createEventSubscribeSheet(Cloud cloud, Node nParentNode, String sEvenementNumber, WritableWorkbook workbook, DoubleDateNode ddn, int sheetNumber) throws WriteException {
      log.debug("createEventSubscribeSheet for data " + sEvenementNumber + " of parent event " + nParentNode.getStringValue("number"));
      WritableSheet sheet = workbook.createSheet(ddn.getSeparatedBeginTime(), sheetNumber);

      sheet.setColumnView(0,23);
      sheet.setColumnView(1,23);

      LinkedList llXlsData = new LinkedList();

      HtmlCleaner hc = new HtmlCleaner();

      int counter = writeNameString(llXlsData,0,nParentNode);
      llXlsData.add(new Label(0,counter,"Datum")); llXlsData.add(new Label(1,counter,ddn.getReadableDate()));
      counter++;
      llXlsData.add(new Label(0,counter,"Tijd")); llXlsData.add(new Label(1,counter,ddn.getReadableTime()));
      counter++;
      counter = writeStrings1(cloud,hc,llXlsData,counter,nParentNode);
      counter = writeKostenEventSubscribe(cloud,llXlsData,counter,sEvenementNumber);
      counter = writeVertrekpuntString(cloud,hc,llXlsData,counter,nParentNode);
      counter = writeStrings2(cloud,hc,llXlsData,counter,nParentNode);
      counter++;
      counter = writeSubscriptions(cloud,llXlsData,counter,sEvenementNumber);
      counter = writeDownloadDate(llXlsData,counter);

      Iterator it = llXlsData.iterator();
      while (it.hasNext()){
         sheet.addCell((Label)it.next());
      }

   }

   public String priceFormating(int iPrice) {
      String sResult = iPrice/100 + ",";
      if (iPrice%100 < 9) sResult += "0";
      sResult += iPrice%100;

      return "€ " + sResult;
   }

   public int writeDownloadDate(LinkedList llXlsData, int counter) {

      llXlsData.add(new Label(0,counter+1,"File gedownload op"));

      Calendar cal = Calendar.getInstance();
      String sDate =  cal.get(Calendar.YEAR) + ".";
      int iMonth = cal.get(Calendar.MONTH)+1;
      if (iMonth<10) {sDate += "0";}
      sDate += iMonth + ".";
      int iDay = cal.get(Calendar.DAY_OF_MONTH);
      if (iDay<10) {sDate += "0";}
      sDate += iDay + "_";
      int iHour = cal.get(Calendar.HOUR_OF_DAY);
      if (iHour<10) {sDate +="0";}
      sDate += iHour + ".";
      int iMinute = cal.get(Calendar.MINUTE);
      if (iMinute<10) {sDate +="0";}
      sDate += iMinute + ".";
      int iSecond = cal.get(Calendar.SECOND);
      if (iSecond<10) {sDate +="0";}
      sDate += iSecond;

      llXlsData.add(new Label(1,counter+1,sDate));

      return ++counter;
   }

   public String createAttachmentNode(Cloud cloud, String fileName, String sTitle) {
      String sAttachmentId = "";
      String sFile = NatMMConfig.getTempDir() + fileName;
      File f = new File(sFile);
      int fsize = (int)f.length();
      byte[] thedata = new byte[fsize];
      try {
         FileInputStream instream = new FileInputStream(f);
         instream.read(thedata);
         NodeManager nmAttachmentsManager = cloud.getNodeManager("events_attachments");
         Node attNode = nmAttachmentsManager.createNode();
         attNode.setValue("titel",sTitle);
         attNode.setValue("title",sTitle);
         attNode.setValue("handle",thedata);
         attNode.setValue("filename",fileName);
         attNode.setValue("mimetype","application/msexcel");
         attNode.setIntValue("size",fsize);
         attNode.commit();
         instream.close();
         sAttachmentId = attNode.getStringValue("number");
      } catch (Exception e) {
          log.error("Exception: " + e);
      }

      return sAttachmentId;

   }
   
   // inner class to store event data in
   class EventData implements Comparable {

      final String eventNumber;
      final String eventName;
      final DoubleDateNode eventDate;
      
      public EventData(final String eventNumber, final String eventName, final DoubleDateNode eventDate) {
         super();
         this.eventNumber = eventNumber;         
         this.eventName = eventName;
         this.eventDate = eventDate;
      }

      public int compareTo(Object o) {
         if (o instanceof EventData) {
            EventData that = (EventData) o;
            DoubleDateNode ddn1 = this.getEventDate();
            DoubleDateNode ddn2 = that.getEventDate();

            return ddn1.compareTo(ddn2);
         }
         return toString().compareTo(o.toString());         
      }

      public DoubleDateNode getEventDate() {
         return eventDate;
      }

      public String getEventName() {
         return eventName;
      }

      public String getEventNumber() {
         return eventNumber;
      }
      
   }
   
}
