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
   public static String BOEKINGEN_TYPE_INDIVIDUELE_BOEKINGEN = "Individuele boekingen";
   public static String BOEKINGEN_TYPE_GROEPSBOEKINGEN = "Groepsboekingen";
   
   // the fact this String ends with a "z" is good, then it occurs as a last row.
   public static String INSCHRIJVINGS_CATEGORIE_NIET_INGEDEELD = "zonder aanmeldingscategorie";

   /**
     * @description This gets evenementenNumbers for Individuele boekingen as well as Groepsboekingen given a certain startNode.
     * This startNode can for example be an evenementType or an inschrijvingsCategorie.
     * The constraint is usually afdelingenConstraint. Except in the case of "zonder aanmeldingscategorie".
	 * @param cloud
	 * @param startNode
	 * @param sNodepath
	 * @param evenementTimeConstraint
	 * @param constraint
	 * @return
	 */
	private String getEvenementenNumbersForStartNode(Cloud cloud, String startNode, String sNodepath, String evenementTimeConstraint, String constraint){
	   
      StringBuffer events = new StringBuffer();
      
      // *** child events ***
      // First, get the parents with the constraint
      // Second, use the children of those parents with the evenementTimeConstraint
      NodeList nlParentEvenementenAfdeling = cloud.getList(startNode,sNodepath,"evenement.number",constraint,null,null,null,false);
      StringBuffer parents = new StringBuffer();
      
      for (int j = 0; j < nlParentEvenementenAfdeling.size(); j++ ){
         Node nParentEvenementAfdeling = nlParentEvenementenAfdeling.getNode(j);
         parents.append(",").append(nParentEvenementAfdeling.getStringValue("evenement.number"));
      }
      
      if(parents.length()>0) {
    	 // remove the unnecessary "," at the beginning
         parents.deleteCharAt(0);
         
         NodeList nlChildEvenementen = cloud.getList(parents.toString(),"evenement2,partrel,evenement","evenement.number",evenementTimeConstraint,null,null,"destination",false);
         for (int j = 0; j < nlChildEvenementen.size(); j++ ){
            Node nChildEvenement = nlChildEvenementen.getNode(j);
            events.append(",").append(nChildEvenement.getStringValue("evenement.number"));
         }
         
      }
      
      // *** parent events ***
      // Simply use the events with the constraint AND the evenementTimeConstraint
      String parentConstraints = constraint + " AND " + evenementTimeConstraint;
      NodeList nlParentEvenementen = cloud.getList(startNode,sNodepath,"evenement.number",parentConstraints,null,null,null,false);
      
      for (int j = 0; j < nlParentEvenementen.size(); j++ ){
         Node nParentEvenement = nlParentEvenementen.getNode(j);
         events.append(",").append(nParentEvenement.getStringValue("evenement.number"));
      }
      
      if(events.length()>0) {
    	  // remove the unnecessary "," at the beginning
    	  events.deleteCharAt(0);
      }
      
      return events.toString();
      
   }

   private int getCounts(Cloud cloud, String sEvenementenNumbers, String evenementTimeConstraint, String statstype, String boekingenTypeName){

      String sRealNodepath = "evenement,posrel,inschrijvingen,posrel2,deelnemers,related,deelnemers_categorie";
      String sRealConstraints = null;
      
      if (boekingenTypeName.equals(BOEKINGEN_TYPE_INDIVIDUELE_BOEKINGEN)) {
         sRealConstraints = evenementTimeConstraint + " AND deelnemers_categorie.groepsactiviteit != '1'";
      } else if(boekingenTypeName.equals(BOEKINGEN_TYPE_GROEPSBOEKINGEN)) {
         sRealConstraints = evenementTimeConstraint + " AND deelnemers_categorie.groepsactiviteit = '1'";         
      }

      NodeList nl = null;
      int iResult = 0;

      if (statstype.equals("inschrijvingen")){
         nl = cloud.getList(sEvenementenNumbers,sRealNodepath,"inschrijvingen.number",sRealConstraints,null,null,null,true);
         iResult += nl.size();
      }

      else if (statstype.equals("deelnemers")){
    	 if (boekingenTypeName.equals(BOEKINGEN_TYPE_INDIVIDUELE_BOEKINGEN)) {
             nl = cloud.getList(sEvenementenNumbers,sRealNodepath,"inschrijvingen.number,deelnemers.bron",sRealConstraints,null,null,null,false);
             for(int i = 0; i < nl.size(); i++) {
                iResult += nl.getNode(i).getIntValue("deelnemers.bron");
             }
    	 } else if(boekingenTypeName.equals(BOEKINGEN_TYPE_GROEPSBOEKINGEN)) {
    		 // In the case of a groepsboeking the deelnemers.bron field is empty and the deelnemers_categorie.aantal_per_deelnemer should be used. 
             nl = cloud.getList(sEvenementenNumbers,sRealNodepath,"deelnemers_categorie.aantal_per_deelnemer",sRealConstraints,null,null,null,false);
             for(int i = 0; i < nl.size(); i++) {
                iResult += nl.getNode(i).getIntValue("deelnemers_categorie.aantal_per_deelnemer");
             }
    	    }
      }

      else if (statstype.equals("leden")){
         if (boekingenTypeName.equals(BOEKINGEN_TYPE_INDIVIDUELE_BOEKINGEN)) {
            if(!sRealConstraints.equals("")) { sRealConstraints += " AND "; }
            sRealConstraints += " ( UPPER(deelnemers_categorie.naam) NOT like '%NIET%' )";
            nl = cloud.getList(sEvenementenNumbers,sRealNodepath,"inschrijvingen.number,deelnemers.bron",sRealConstraints,null,null,null,false);
            for(int i = 0; i < nl.size(); i++) {
               iResult += nl.getNode(i).getIntValue("deelnemers.bron");             
            }
         } else {
            iResult = 0;
         }
      }

      else if (statstype.equals("opbrengst")){
         nl = cloud.getList(sEvenementenNumbers,sRealNodepath,"inschrijvingen.number,posrel2.pos",sRealConstraints,null,null,null,false);
         for(int i = 0; i < nl.size(); i++) {
            iResult += nl.getNode(i).getIntValue("posrel2.pos");
         }
      }

      else if (statstype.equals("activiteiten")){
         nl = cloud.getList(sEvenementenNumbers,sRealNodepath,"inschrijvingen.number,evenement.number",sRealConstraints,null,null,null,false);
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

   private TreeMap getEvenementenTypes(Cloud cloud, boolean isGroepsBoekingenType) {
     TreeMap tmNames = new TreeMap();
     iTotal = 0;

     String groepsActiviteitConstraintOperator = isGroepsBoekingenType ? "=" : "!=";
     String deelnemersCategorieConstraints = "deelnemers_categorie.groepsactiviteit" + groepsActiviteitConstraintOperator + "1";

     // *** put names on tsNames ***
     String sStatName = "";
     String sNumber = "";
     
     NodeList nlEvenementType = cloud.getList("","evenement_type,posrel,deelnemers_categorie","evenement_type.naam,evenement_type.number",deelnemersCategorieConstraints,"evenement_type.naam",null,null,false);
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
   
   private TreeMap getRegios(Cloud cloud, String constraint) {
	   TreeMap regioMap = new TreeMap();
       String regioConstraint = "afdelingen.naam LIKE '" + constraint + "%'"; // Regio X...
       NodeList nlRegios = cloud.getList("","afdelingen","afdelingen.number,afdelingen.naam",regioConstraint,null,null,null,false);
       for (int jj = 0; jj < nlRegios.size(); jj++ ) {
          Node regio = nlRegios.getNode(jj);
          String afName = regio.getStringValue("afdelingen.naam");
          String afNumber = regio.getStringValue("afdelingen.number");
          if (!regioMap.containsKey(afName)) {
            regioMap.put(afName, afNumber);
          }
       }
       return regioMap;
   }
   
   private String getRegiosNumbers(TreeMap regioMap) {
	   StringBuffer regiosNumbers = new StringBuffer();
       Set regioSet = regioMap.entrySet();
       Iterator regioSetIterator = regioSet.iterator();
       while (regioSetIterator.hasNext()) {
         Map.Entry regio = (Map.Entry)regioSetIterator.next();
         String regioNumber = (String) regio.getValue();
         regiosNumbers.append(",").append(regioNumber);
       }
       if(regiosNumbers.length()>0) {
     	  // remove the unnecessary "," at the beginning
    	   regiosNumbers.deleteCharAt(0);
       }
       return regiosNumbers.toString();
   }
   
   /**
     * @description Get the afdelingen sorted by regio, afdeling.
	 * @param regiosNumbers
	 * @param cloud
	 * @return NodeList nlAfdelingen
	 */
	private NodeList getAfdelingen(String regiosNumbers, Cloud cloud) {
	 NodeList nlAfdelingen = cloud.getList(regiosNumbers,"afdelingen2,readmore,afdelingen","afdelingen.number,afdelingen.naam",null,"afdelingen2.naam,afdelingen.naam",null,null,false);
	 return nlAfdelingen;
   }
   
   private ArrayList getAfdelingenList(NodeList nlAfdelingen) {
	 ArrayList afdelingenList = new ArrayList();
	 for (int jj = 0; jj < nlAfdelingen.size(); jj++ ) {
	    Node afdeling = nlAfdelingen.getNode(jj);
	    String af = afdeling.getStringValue("afdelingen.naam");
	    if (!afdelingenList.contains(af)) {
	      afdelingenList.add(af);
	    }
	 }
	 return afdelingenList;
   }
   
   /**
     * @description This maps afdelingen to their regio names. This map is used to look up the regio of the afdelingen.
	 * @param NodeList nlAfdelingen
	 * @return TreeMap afdelingenMap
	 */
	private TreeMap getAfdelingenMap(NodeList nlAfdelingen) {
	 TreeMap afdelingenMap = new TreeMap();
	 for (int jj = 0; jj < nlAfdelingen.size(); jj++ ) {
	    Node afdeling = nlAfdelingen.getNode(jj);
	    String regio = afdeling.getStringValue("afdelingen2.naam");
	    String af = afdeling.getStringValue("afdelingen.naam");
	    if (!afdelingenMap.containsKey(af)) {
	      afdelingenMap.put(af,regio);
	    }
	 }
	 return afdelingenMap;
   }
	
	private TreeMap getInschrijvingsCategorieen(Cloud cloud) {
	     TreeMap tmNames = new TreeMap();
	     iTotal = 0;

	     // *** put names on tsNames ***
	     String sStatName = "";
	     String sNumber = "";
	     
	     NodeList nlInschrijvingsCategorie = cloud.getList("","inschrijvings_categorie","inschrijvings_categorie.name,inschrijvings_categorie.number",null,"inschrijvings_categorie.name",null,null,false);
	     for (int j = 0; j < nlInschrijvingsCategorie.size(); j++ ){
	       Node nInschrijvingsCategorie = nlInschrijvingsCategorie.getNode(j);
	       sStatName = nInschrijvingsCategorie.getStringValue("inschrijvings_categorie.name");
	       sNumber = nInschrijvingsCategorie.getStringValue("inschrijvings_categorie.number");
	       if (!tmNames.containsKey(sStatName)) {
	         tmNames.put(sStatName, sNumber);
	       }
	     }
	     tmNames.put(INSCHRIJVINGS_CATEGORIE_NIET_INGEDEELD, "");
	     return tmNames;
	}
	
   /**
    * @description Gets stats for all rows (from collection) in this column (statstype).
    * The collection can contain for example evenementTypes of inschrijvingsCategorieen.
    * 
	* @param cloud
	* @param fromTime
	* @param toTime
	* @param period
	* @param afdelingName
	* @param statstype
	* @param collection
	* @param removeZeros
	* @return TreeMap
	*/
   private TreeMap getStatisticsForCollection(Cloud cloud, String pathToEvenement, String evenementTimeConstraint, String afdelingName, String statstype, TreeMap collection, boolean removeZeros, String boekingenTypeName){
      TreeMap tmStatistics = new TreeMap();
      
      String constraintPathToAfdelingen = ",related,natuurgebieden,posrel,afdelingen";
      afdelingName = filterAfdelingName(afdelingName);
      String afdelingenConstraint = "afdelingen.naam='"+afdelingName+"'";

      // *** count total for tmNames ***
      String evenementenNumbers = null;
      String key = null;
      
      // collection = EVENEMENT_TYPES ("evenement_type" objects or "inschrijvings_categorie")
      
      Set set = collection.entrySet();
      Iterator iterator = set.iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        key = (String) entry.getKey();
        if(key.equals(INSCHRIJVINGS_CATEGORIE_NIET_INGEDEELD)){
        	// We are getting in fact totals of all groepsboekingen REGARDLESS inschrijvings_categorie.
        	// When writing the values to the excel sheet we will SUBTRACT all the inschrijvingen WITH inschrijvings_categorie.
        	String pathToEvenementForAllGroepsEvenementenNumbers = "deelnemers_categorie,posrel,evenement_type,related,evenement";
        	pathToEvenementForAllGroepsEvenementenNumbers += constraintPathToAfdelingen;
        	String constraint = afdelingenConstraint;
        	constraint += " AND deelnemers_categorie.groepsactiviteit=1";
        	evenementenNumbers = getEvenementenNumbersForStartNode(cloud, null, pathToEvenementForAllGroepsEvenementenNumbers, evenementTimeConstraint, constraint);
        } else {
	        String number = (String)collection.get(key);
	        String pathToEvenementForEvenementenNumbers = pathToEvenement;
	        pathToEvenementForEvenementenNumbers += constraintPathToAfdelingen;
	        evenementenNumbers = getEvenementenNumbersForStartNode(cloud, number, pathToEvenementForEvenementenNumbers, evenementTimeConstraint, afdelingenConstraint);
        }
        tmStatistics = updateTmStatistics(tmStatistics, cloud, evenementenNumbers, evenementTimeConstraint, statstype, key, removeZeros, boekingenTypeName);
      }

      return tmStatistics;
   }
   
   /**
     * @description Key can be for example inschrijvingsCategorie or evenementType.
	 * @param tmStatistics
	 * @param cloud
	 * @param evenementenNumbers
	 * @param evenementTimeConstraint
	 * @param statstype
	 * @param key
	 * @param removeZeros
	 * @return
	 */
	private TreeMap updateTmStatistics(TreeMap tmStatistics, Cloud cloud, String evenementenNumbers, String evenementTimeConstraint, String statstype, String key, boolean removeZeros, String boekingenTypeName){
      
	    if(!removeZeros||!evenementenNumbers.equals("")) {
	      int iResultCounts = 0;
	      if(!evenementenNumbers.equals("")){
	    	  iResultCounts = getCounts(cloud,evenementenNumbers,evenementTimeConstraint,statstype,boekingenTypeName);
	      }
	      if(!removeZeros||iResultCounts!=0) {
	        iTotal += iResultCounts;
	        tmStatistics.put(key,new Integer(iResultCounts));
	      }
	    }

      return tmStatistics;
      
   }
   
   private String filterAfdelingName(String afdelingName) {
	   
	   // should work well with for example "BC 's Graveland"
       // do the right kind of escaping of an apostrophe:
       // The single quote can be escaped using it twice for every single occurence:
       // "name='aaa''bbb'" (if we want to find the string aaa'bbb)
       // see: http://mmbase.nl/development/api/1.6/org/mmbase/bridge/NodeManager.html#getList(java.lang.String,%20java.lang.String,%20java.lang.String)
       afdelingName = afdelingName.replaceAll("'","''");
       
       return afdelingName;
       
   }
   
   private String getEvenementTimeConstraint(long fromTime, long toTime, int period){

      String evenementTimeConstraint =  "evenement.begindatum > " + fromTime;
      if (period>0) evenementTimeConstraint += " AND evenement.einddatum < " + toTime;

      return evenementTimeConstraint;
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

      String listtype = "Afdelingen";

      ArrayList alStatsTypes = new ArrayList();
      alStatsTypes.add("activiteiten");
      alStatsTypes.add("inschrijvingen");
      alStatsTypes.add("deelnemers");
      alStatsTypes.add("opbrengst");
      alStatsTypes.add("leden");

      String dateString = dateString(fromTime) + "_" + dateString(toTime);
      String fileName = listtype.replaceAll("/","_").replaceAll(" ","") + "_" + dateString + "_stats.xls";

      String sDate  = nowDate();

      String sAttachmentId = "";

      /*NodeList nl = cloud.getList("","events_attachments","events_attachments.number","events_attachments.filename = '" + fileName + "'",null,null,null,false);
      if(nl.isEmpty()) {*/

         WritableWorkbook workbook = Workbook.createWorkbook(new File(NatMMConfig.getTempDir() + fileName));
        
         TreeMap regioMap = getRegios(cloud,"Regio");
         TreeMap extraOrdinaryRegioMap = getRegios(cloud,"Comm., Fondsenw., Ledens.");
         regioMap.putAll(extraOrdinaryRegioMap);
         String regiosNumbers = getRegiosNumbers(regioMap);
         NodeList nlAfdelingen = getAfdelingen(regiosNumbers,cloud);
         ArrayList afdelingen = new ArrayList(); // will be filled with AfdelingBean's to make the totals sheet
         ArrayList afdelingenList = getAfdelingenList(nlAfdelingen); // names
         TreeMap afdelingenMap = getAfdelingenMap(nlAfdelingen); // regionName - afdelingName
         
         ArrayList boekingenTypeList = new ArrayList();
         boekingenTypeList.add(BOEKINGEN_TYPE_INDIVIDUELE_BOEKINGEN);
         boekingenTypeList.add(BOEKINGEN_TYPE_GROEPSBOEKINGEN);
         
         TreeMap rows = new TreeMap();
         TreeMap evenementenTypes = getEvenementenTypes(cloud, false);
         TreeMap inschrijvingsCategorieen = getInschrijvingsCategorieen(cloud);
         String evenementTimeConstraint = getEvenementTimeConstraint(fromTime, toTime, period);
         String pathToEvenement = "";
         String pathToEvenementFromEvenementType = "evenement_type,related,evenement";
         String pathToEvenementFromInschrijvingsCategorie = "inschrijvings_categorie,related,inschrijvingen,posrel,evenement";
         
         WritableCellFormat wrappedText = new WritableCellFormat(WritableWorkbook.ARIAL_10_PT);
         wrappedText.setWrap(true);
         
         /* 
          * 
          * A sheet for each of a set of afdelingingen from all regio's.
          * 
          * */
         Collections.reverse(afdelingenList);
         for (Iterator ait = afdelingenList.iterator();ait.hasNext();) {
        	 
           AfdelingBean afdeling = new AfdelingBean();

           String afdelingName = (String)ait.next();
           // excell does not accept sheetName's with a "/".
           String sheetName = afdelingName.replaceAll("/", " ");
           WritableSheet sheet = workbook.createSheet(sheetName, 0);

           // set columns width
           sheet.setColumnView(0,50);
           sheet.setColumnView(1,20);
           sheet.setColumnView(2,20);
           sheet.setColumnView(3,20);
           sheet.setColumnView(4,20);
           sheet.setColumnView(5,20);

           // add static labels
           int currentExcelRow = 0;
           Label staticLabel = new Label(0,currentExcelRow,"REGIO:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,currentExcelRow,(String)afdelingenMap.get(afdelingName));
           sheet.addCell(staticLabel);
           
           currentExcelRow++;
           staticLabel = new Label(0,currentExcelRow,"");
           sheet.addCell(staticLabel);
           
           currentExcelRow++;
           staticLabel = new Label(0,currentExcelRow,"BEHEEREENHEID/BEZOEKERSCENTRUM:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,currentExcelRow,afdelingName);
           sheet.addCell(staticLabel);
           
           currentExcelRow++;
           staticLabel = new Label(0,currentExcelRow,"DATUM:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,currentExcelRow,sDate);
           sheet.addCell(staticLabel);

           currentExcelRow++;
           staticLabel = new Label(0,currentExcelRow,"PERIODE:");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,currentExcelRow,getStatsperiod(fromTime,toTime,period));
           sheet.addCell(staticLabel);

           currentExcelRow++;
           staticLabel = new Label(0,currentExcelRow,"");
           sheet.addCell(staticLabel);

           currentExcelRow++;
           staticLabel = new Label(0,currentExcelRow,"EXCURSIETYPE");
           sheet.addCell(staticLabel);
           staticLabel = new Label(1,currentExcelRow,"AANTAL");
           sheet.addCell(staticLabel);
           staticLabel = new Label(4,currentExcelRow,"BATEN");
           sheet.addCell(staticLabel);
           staticLabel = new Label(5,currentExcelRow,"Geschat % leden dat deelneemt");
           sheet.addCell(staticLabel);

           currentExcelRow++;
           staticLabel = new Label(1,currentExcelRow,"Excursies");
           sheet.addCell(staticLabel);
           staticLabel = new Label(2,currentExcelRow,"Inschrijvingen");
           sheet.addCell(staticLabel);
           staticLabel = new Label(3,currentExcelRow,"Deelnemers");
           sheet.addCell(staticLabel);

           ExtraStats os = new ExtraStats();

           /*
            * 
            * Loop through boekingenTypes
            * NB:
            * boekingenType "Individuele boekingen" shows rows of evenementen_type
            * boekingenType "Groepsboekingen" shows rows of inschrijvings_categorie
            * 
            * */
           for (Iterator bit = boekingenTypeList.iterator();bit.hasNext();) {

             String boekingenTypeName = (String)bit.next();
             boolean isIndividueleBoekingenType = boekingenTypeName.equals(BOEKINGEN_TYPE_INDIVIDUELE_BOEKINGEN);
             boolean isGroepsBoekingenType = boekingenTypeName.equals(BOEKINGEN_TYPE_GROEPSBOEKINGEN);
             
             // put a label with the boekingenType above the list of evenementenTypes
             currentExcelRow++;
             String boekingenTypeLabel = boekingenTypeName + " ( per ";
             if(isIndividueleBoekingenType){
            	 boekingenTypeLabel += "evenementtype";
             } else if(isGroepsBoekingenType) {
            	 boekingenTypeLabel += "aanmeldingscategorie";
             }
             boekingenTypeLabel += " )";
             staticLabel = new Label(0,currentExcelRow,boekingenTypeLabel);
             sheet.addCell(staticLabel);
             
             currentExcelRow++;
             int maxRow = 0;
             int[] thisBoekingenTypeTotal = new int[] {0,0,0,0,0};
             int totalLedenValue = 0;
             int totalDeelnemersValue = 0;
             Map deelnemers = new HashMap();
             jxl.write.Number nValue = null;

             int columnNo = 1; // *** columns ***
             Iterator columnsIterator = alStatsTypes.iterator();
             while (columnsIterator.hasNext()) {
            	 
               String sStatsType = (String)columnsIterator.next();

               if(isIndividueleBoekingenType){
            	   pathToEvenement = pathToEvenementFromEvenementType;
            	   rows = evenementenTypes;
               } else if(isGroepsBoekingenType) {
            	   pathToEvenement = pathToEvenementFromInschrijvingsCategorie;
            	   rows = inschrijvingsCategorieen;
               }
               // Gets stats for all rows (collection of for example evenementenTypes or inschrijvingenCategorieen) in this column (statstype).
               // Do not remove zero's
               TreeMap tmAllStats = os.getStatisticsForCollection(cloud,pathToEvenement,evenementTimeConstraint,afdelingName,sStatsType,rows,false,boekingenTypeName);
               
               Set tmAllStatsSet = tmAllStats.entrySet();
               Iterator rowIterator = tmAllStatsSet.iterator();
               
               int rowNo = currentExcelRow; // *** rows ***
               while (rowIterator.hasNext()){
            	   
                 Map.Entry row = (Map.Entry)rowIterator.next();
                 
                 String sListTypeName = (String) row.getKey();

                 if (columnNo==1) { // print name of event types
                   Label lListTypeName = new Label(0,rowNo,sListTypeName,wrappedText);
                   sheet.addCell(lListTypeName);
                 }

                 Integer sValue = (Integer) row.getValue();
                 
                 // default 0 in a cell and not empty cell, so NO if statement.
                 //if (sValue.intValue()!=0) {
                	 
	                 int value = sValue.intValue();
	                 
	                 //	If we have "zonder aanmeldingscategorie" we have to subtract the column totals from the values of this row.
	                 // NB: it is assumed this is the last row of "Groepsboekingen". ( The sListTypeName starts with a "z". )
	                 if(sListTypeName.equals(INSCHRIJVINGS_CATEGORIE_NIET_INGEDEELD)){

                      if(sStatsType.equals("opbrengst")) {
                         value = value - (thisBoekingenTypeTotal[columnNo-1] * 100);
                      } 
                      else {
                         value = value - thisBoekingenTypeTotal[columnNo-1];
                      }                     
	                 }
	
	                 if(sStatsType.equals("opbrengst")) {
	                   value = value/100;
	                 }
	
	                 if(sStatsType.equals("deelnemers")) {
	                   deelnemers.put(sListTypeName, sValue);
	                 }
	
	                 if(sStatsType.equals("leden")) {
		                 int deelnemersValue = ((Integer)deelnemers.get(sListTypeName)).intValue();
                       totalLedenValue += value;
                       totalDeelnemersValue += deelnemersValue;
		                 if (deelnemersValue!=0) {
		                    value = value * 100 / deelnemersValue;
                       } else {
		                    value = 0;
                       }
	                 }

	                 thisBoekingenTypeTotal[columnNo-1] += value;
	                 nValue = new jxl.write.Number(columnNo, rowNo, value);
	                 sheet.addCell(nValue);
	             //}
                 rowNo++;
            }
  
               
            // print TOTAL
            if (columnNo==1) {
               staticLabel = new Label(0,rowNo,"TOTAAL");
               sheet.addCell(staticLabel);
             }
            if (columnNo != 5) {
             nValue = new jxl.write.Number(columnNo, rowNo, thisBoekingenTypeTotal[columnNo-1]);
            } else {
               int percentage = (totalDeelnemersValue > 0) ? (100*totalLedenValue/totalDeelnemersValue) : 0;
               thisBoekingenTypeTotal[columnNo-1] = percentage;
               nValue = new jxl.write.Number(columnNo, rowNo, percentage);
            }
             sheet.addCell(nValue);
             maxRow = (rowNo>maxRow) ? rowNo : maxRow;
             
             // for totals sheet
             if(isIndividueleBoekingenType){
            	 afdeling.setIndividueleBoekingenTotal(thisBoekingenTypeTotal);
                if (columnNo == 5) {
                   afdeling.setIndividueleBoekingenLedenTotal(afdeling.getIndividueleBoekingenLedenTotal() + totalLedenValue);
                   afdeling.setIndividueleBoekingenDeelnemersTotal(afdeling.getIndividueleBoekingenDeelnemersTotal() + totalDeelnemersValue);
                }
             } else if(isGroepsBoekingenType) {
            	 afdeling.setGroepsBoekingenTotal(thisBoekingenTypeTotal);
                if (columnNo == 5) {
                   afdeling.setGroepsBoekingenLedenTotal(afdeling.getGroepsBoekingenLedenTotal() + totalLedenValue);
                   afdeling.setGroepsBoekingenDeelnemersTotal(afdeling.getGroepsBoekingenDeelnemersTotal() + totalDeelnemersValue);
                }
             }
             
             columnNo++;
           }
           currentExcelRow = ++maxRow; //  add an empty row between boekingenTypes
           
         }
        
        // for totals sheet
        afdeling.setAfdelingName(afdelingName);
        afdeling.setRegioName((String)afdelingenMap.get(afdelingName));
        afdelingen.add(afdeling);

       }
         
         // CREATE TOTALEN SHEET
	     WritableSheet sheet = workbook.createSheet("Totalen", 0);
	
	     // set columns width
	     int currentExcelColumn = 0;
	     sheet.setColumnView(currentExcelColumn,30);
	     currentExcelColumn++;
	     sheet.setColumnView(currentExcelColumn,30);
	     for (int i = 0; i < 5; i++) {
	    	 currentExcelColumn++;
		     sheet.setColumnView(currentExcelColumn,10);
		 }
	     currentExcelColumn++;
	     sheet.setColumnView(currentExcelColumn,5);
	     for (int i = 0; i < 5; i++) {
	    	 currentExcelColumn++;
		     sheet.setColumnView(currentExcelColumn,10);
		 }
	     
	     // add static labels
         int currentExcelRow = 0;
         Label staticLabel = new Label(0,currentExcelRow,"TOTALEN");
         sheet.addCell(staticLabel);
         
         currentExcelRow++;
         staticLabel = new Label(0,currentExcelRow,"");
         sheet.addCell(staticLabel);
         
         currentExcelRow++;
         staticLabel = new Label(0,currentExcelRow,"DATUM:");
         sheet.addCell(staticLabel);
         staticLabel = new Label(1,currentExcelRow,sDate);
         sheet.addCell(staticLabel);

         currentExcelRow++;
         staticLabel = new Label(0,currentExcelRow,"PERIODE:");
         sheet.addCell(staticLabel);
         staticLabel = new Label(1,currentExcelRow,getStatsperiod(fromTime,toTime,period));
         sheet.addCell(staticLabel);

         currentExcelRow++;
         staticLabel = new Label(0,currentExcelRow,"");
         sheet.addCell(staticLabel);
         
         currentExcelRow++;
         staticLabel = new Label(2,currentExcelRow,"INDIVIDUELE BOEKINGEN");
         sheet.addCell(staticLabel);
         staticLabel = new Label(8,currentExcelRow,"GROEPSBOEKINGEN");
         sheet.addCell(staticLabel);
         
         currentExcelRow++;
         staticLabel = new Label(2,currentExcelRow,"AANTAL");
         sheet.addCell(staticLabel);
         staticLabel = new Label(5,currentExcelRow,"BATEN");
         sheet.addCell(staticLabel);
         staticLabel = new Label(6,currentExcelRow,"% leden");
         sheet.addCell(staticLabel);
         staticLabel = new Label(8,currentExcelRow,"AANTAL");
         sheet.addCell(staticLabel);
         staticLabel = new Label(11,currentExcelRow,"BATEN");
         sheet.addCell(staticLabel);
         staticLabel = new Label(12,currentExcelRow,"% leden");
         sheet.addCell(staticLabel);

         currentExcelRow++;
         staticLabel = new Label(2,currentExcelRow,"Excursies");
         sheet.addCell(staticLabel);
         staticLabel = new Label(3,currentExcelRow,"Inschrijvingen");
         sheet.addCell(staticLabel);
         staticLabel = new Label(4,currentExcelRow,"Deelnemers");
         sheet.addCell(staticLabel);
         staticLabel = new Label(8,currentExcelRow,"Excursies");
         sheet.addCell(staticLabel);
         staticLabel = new Label(9,currentExcelRow,"Inschrijvingen");
         sheet.addCell(staticLabel);
         staticLabel = new Label(10,currentExcelRow,"Deelnemers");
         sheet.addCell(staticLabel);
         
         jxl.write.Number nValue = null;
         int[] individueleBoekingenGrandTotal = new int[] {0,0,0,0,0};
         int[] groepsBoekingenGrandTotal = new int[] {0,0,0,0,0};
         int individueleBoekingenGrandTotalLedenValue = 0;
         int individueleBoekingenGrandTotalDeelnemersValue = 0;
         int groepsBoekingenGrandTotalLedenValue = 0;
         int groepsBoekingenGrandTotalDeelnemersValue = 0;
         Collections.reverse(afdelingen);
         for (Iterator iter = afdelingen.iterator(); iter.hasNext();) {
			AfdelingBean afdeling = (AfdelingBean) iter.next();
			
			currentExcelRow++;
			staticLabel = new Label(0,currentExcelRow,afdeling.getAfdelingName());
	        sheet.addCell(staticLabel);
	        staticLabel = new Label(1,currentExcelRow,afdeling.getRegioName());
	        sheet.addCell(staticLabel);
            
            for (int i = 0; i < 5; i++) {
            	nValue = new jxl.write.Number(i+2, currentExcelRow, afdeling.getIndividueleBoekingenTotal()[i]);
                sheet.addCell(nValue);
                individueleBoekingenGrandTotal[i] += afdeling.getIndividueleBoekingenTotal()[i];
	   		}
            // grand total percentages require tracking of nominator and denominator
            individueleBoekingenGrandTotalLedenValue += afdeling.getIndividueleBoekingenLedenTotal();
            individueleBoekingenGrandTotalDeelnemersValue += afdeling.getIndividueleBoekingenDeelnemersTotal();
            
            for (int i = 0; i < 5; i++) {
            	nValue = new jxl.write.Number(i+8, currentExcelRow, afdeling.getGroepsBoekingenTotal()[i]);
                sheet.addCell(nValue);
                groepsBoekingenGrandTotal[i] += afdeling.getGroepsBoekingenTotal()[i];
	   		}
            groepsBoekingenGrandTotalLedenValue += afdeling.getGroepsBoekingenLedenTotal();
            groepsBoekingenGrandTotalDeelnemersValue += afdeling.getGroepsBoekingenDeelnemersTotal();
		 }
         
         currentExcelRow++;
         staticLabel = new Label(0,currentExcelRow,"");
         sheet.addCell(staticLabel);
         
         currentExcelRow++;
         staticLabel = new Label(0,currentExcelRow,"GRAND TOTAL");
         sheet.addCell(staticLabel);
         
         int grandPercentage = 0;
         for (int i = 0; i < 4; i++) {
         	 nValue = new jxl.write.Number(i+2, currentExcelRow, individueleBoekingenGrandTotal[i]);
             sheet.addCell(nValue);
	   	 }
         // percentage grand totals
         grandPercentage = (individueleBoekingenGrandTotalDeelnemersValue > 0) ? (100*individueleBoekingenGrandTotalLedenValue/individueleBoekingenGrandTotalDeelnemersValue) : 0;
         nValue = new jxl.write.Number(4+2, currentExcelRow, grandPercentage);
         sheet.addCell(nValue);
         
         for (int i = 0; i < 4; i++) {
	     	 nValue = new jxl.write.Number(i+8, currentExcelRow, groepsBoekingenGrandTotal[i]);
	         sheet.addCell(nValue);
   		 }
         // percentage grand totals
         grandPercentage = (groepsBoekingenGrandTotalDeelnemersValue > 0) ? (100*groepsBoekingenGrandTotalLedenValue/groepsBoekingenGrandTotalDeelnemersValue) : 0;
         nValue = new jxl.write.Number(4+8, currentExcelRow, grandPercentage);
         sheet.addCell(nValue);
         
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
