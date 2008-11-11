package nl.leocms.util.tools;

import java.util.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import net.sf.mmapps.modules.lucenesearch.*;
import net.sf.mmapps.modules.lucenesearch.util.*;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.document.Document;

import nl.leocms.evenementen.Evenement;
import nl.leocms.util.*;
import nl.leocms.util.tools.HtmlCleaner;


/**
 * Utilities functions for the search pages
 *
 * @author H. Hangyi
 * @version $Revision: 1.22 $
 */
public class SearchUtil {

   private static final Logger log = Logging.getLoggerInstance(SearchUtil.class);

   
   public SearchUtil() {
   }

   public final static String sEmployeeConstraint = "(( medewerkers.importstatus = 'active' ) OR ( medewerkers.externid LIKE 'extern' ))";
   public final static String sAfdelingenConstraints = "( afdelingen.importstatus != 'inactive' ) OR ( afdelingen.externid LIKE 'extern' )";
   public final static String sNatuurgebiedenConstraint = "natuurgebieden.bron!=''";
   public String articleConstraint(long nowSec, int quarterOfAnHour) {
      return "(artikel.embargo < '" + (nowSec+quarterOfAnHour) + "') AND (artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
   }
   private static boolean articleMatchesConstraint(long embargo, String use_verloopdatum, long verloopdatum, long nowSec, int quarterOfAnHour) {
      if ((embargo < (nowSec+quarterOfAnHour) ) 
         && ("0".equals(use_verloopdatum) || verloopdatum > nowSec )) {
         return true; 
         } else { 
         return false;
         }
   }

   public String getConstraint(String objecttype, long nowSec, int quarterOfAnHour) {
     // *** the assumption is that some contenttypes have their particular constraint
      if("artikel".equals(objecttype)) {
        return articleConstraint(nowSec, quarterOfAnHour); 
     } else if("natuurgebieden".equals(objecttype)) {
        return sNatuurgebiedenConstraint;
     } else if("medewerkers".equals(objecttype)) {
        return sEmployeeConstraint;
     } else if("afdelingen".equals(objecttype)) {
        return sAfdelingenConstraints;
     }
     return "";
   }

   public String searchResults(TreeSet searchResultList) {
      String searchResults = searchResultList.toString();
      return searchResults.substring(1,searchResults.length()-1);
   }

   public Vector createSearchTerms(String searchString) {
      // use TreeSet to delete duplicates
      TreeSet searchTermSet = new TreeSet();
      String searchTerm = "";
      searchString = searchString.toUpperCase() + " ";
      while(!searchString.equals("")) {
         if(searchString.substring(0,1).equals("\"")) {
            // string whithin quotes ?
            searchString = searchString.substring(1);
            try{
               searchTerm = searchString.substring(0,searchString.indexOf("\""));
               searchString = searchString.substring(searchString.indexOf("\"")+1);
            } catch (Exception e) {
               // no closing "
               searchTerm = "";
            }
         } else {
            // take next word
            searchTerm = searchString.substring(0,searchString.indexOf(" "));
            searchString = searchString.substring(searchString.indexOf(" ")+1);
         }
         searchTerm = searchTerm.replace('-',' ');
         // for 'search-fast' search on the string 'search fast'
         if(!searchTerm.equals("")){
            searchTermSet.add(searchTerm);
         }
      }
      TreeMap searchTermMap = new TreeMap();
      // use SortedMap to sort on length
      Iterator searchTermList = searchTermSet.iterator();
      while(searchTermList.hasNext()) {
         searchTerm = (String) searchTermList.next();
         int key = searchTerm.length()*10;
         while (searchTermMap.containsKey(new Integer(key))){
            key++;
         }
         searchTermMap.put(new Integer(key), searchTerm);
      }
      Vector searchTerms = new Vector();
      // create resulting Vector from sortedMap
      while(!searchTermMap.isEmpty()) {
         Integer lastKey = (Integer) searchTermMap.lastKey();
         searchTerm = (String) searchTermMap.get(lastKey);
         searchTermMap.remove(lastKey);
         searchTerms.add(searchTerm);
      }
      return searchTerms;
   }

    /* Find the first occurence of searchTerm in textStr after fromIndex
    */
   public int [] findSearchTerm(String textStr, String searchTerm, int fromIndex) {

   	textStr = textStr.toUpperCase();
   	searchTerm = searchTerm.toUpperCase();
   	int [] fromToIndex = { -1, -1 };

   	log.debug("searchterm: " + searchTerm + "\ntext: " + textStr);

      int sPos = textStr.indexOf(searchTerm,fromIndex);
      if(sPos>-1) {
         int ePos = sPos + searchTerm.length();
         fromToIndex[0] = sPos;
         fromToIndex[1] = ePos;
   	}
      return fromToIndex;
   }

   public String highLightSearchTerm(String textStr, String searchTerm, String highlight, int maxHighlights) {
      int i = 0;
      int [] fromToIndex = findSearchTerm(textStr,searchTerm, 0);
      while(fromToIndex[0]!=-1 && i<maxHighlights) {
         int sPos = fromToIndex[0];
         int ePos = fromToIndex[1];
         /*
         the highlight should not:
         - fall inside another highlight
         - be part of tag e.g. u
         - be part of a charentity
         */
         if(HtmlCleaner.insideTag(textStr,sPos,ePos,"<" + highlight + ">","</" + highlight + ">")
            || HtmlCleaner.insideTag(textStr,sPos,ePos,"<",">")
            || HtmlCleaner.insideTag(textStr,sPos,ePos,"&",";")) {
            log.debug("Not going to highlight: " + textStr.substring(sPos,ePos));
            fromToIndex = findSearchTerm(textStr,searchTerm,ePos);
         } else {
            log.debug("Going to highlight: " + textStr.substring(sPos,ePos));
            textStr = textStr.substring(0,sPos)
                  + "<" + highlight + ">" + textStr.substring(sPos,ePos) + "</" + highlight + ">"
                  + textStr.substring(ePos);
            fromToIndex = findSearchTerm(textStr,searchTerm,ePos+5+2*highlight.length());
            i++;
         }
      }
      return textStr;
   }

   public int startPos(String textStr, Vector searchTerms) {
      // find the first position of a searchTerm in the textStr
      int maxPos = textStr.length();
      int startPos = maxPos;
      Iterator searchTermList = searchTerms.iterator();
      while(startPos==maxPos&&searchTermList.hasNext()) {
         String searchTerm = (String) searchTermList.next();
         int [] fromToIndex = findSearchTerm(textStr,searchTerm,0);
         if(fromToIndex[0]!=-1) {
            startPos = fromToIndex[0];
         }
      }
      log.debug( "startPos on: " + textStr + " for " + searchTerms + " is " + startPos);
      return startPos;
   }

   public String superSearchString(String searchText) {
      for(int charPos = 0; charPos < searchText.length(); charPos++){
         char c = searchText.charAt(charPos);
         if  (   !(('a'<=c)&&(c<='z'))
             &&  !(('A'<=c)&&(c<='Z'))
             &&  !(('0'<=c)&&(c<='9'))
             &&  !(c=='-')
             &&  !(c=='_')
             &&  !(c=='.')
             &&  !(c==' ')
             ) {
                 searchText = searchText.substring(0,charPos) + "%" + searchText.substring(charPos+1);
             }
      }
      return searchText;
   }
    
   public String highlightSearchTerms(String textStr, Vector searchTerms, String highlight) {

      //  strip textStr from html taggings
      textStr = HtmlCleaner.cleanText(textStr,"<",">");

      // map the &charentities; to their \uFFFF counterparts
      String rawString [] = HtmlCleaner.rawString();
      char translatedChar [] = HtmlCleaner.translatedChar();
      for(int c= 0; c<rawString.length; c++){
         textStr = HtmlCleaner.replace(textStr,rawString[c],"" + translatedChar[c]);
      }

      int startPos = startPos(textStr, searchTerms);

      if(startPos==textStr.length()) {
         startPos = 0;
      }

      // try to find beginning of sentence
      int dotPos = textStr.lastIndexOf(". ",startPos);
      if(dotPos>-1) {
         // found beginning of sentence
         startPos = dotPos+2;
      } else {
         // probably first sentence of paragraph or title
         startPos = 0;
      }
      textStr = textStr.substring(startPos);
      int spacePos = textStr.indexOf(" ",180);
      if(spacePos>-1) {
         textStr = textStr.substring(0,spacePos);
      }
      Iterator searchTermList = searchTerms.iterator();
      while(searchTermList.hasNext()) {
         String searchTerm = (String) searchTermList.next();
         int length = textStr.length();
         textStr = highLightSearchTerm(textStr, searchTerm, highlight, 5);
      }

      // map the \uFFFF to their &charentities; counterparts
      for(int c= 0; c<rawString.length; c++){
         textStr = HtmlCleaner.replace(textStr,"" + translatedChar[c],rawString[c]);
      }
      return textStr;
   }

   public long [] getPeriod(String sPeriod) {

      Calendar cal = Calendar.getInstance();
      int fromDay = 0; int fromMonth = 0; int fromYear = 0;
      int toDay = 0; int toMonth = 0; int toYear = 0;
      int thisDay = cal.get(Calendar.DAY_OF_MONTH);
      int thisMonth = cal.get(Calendar.MONTH)+1;
      int thisYear = cal.get(Calendar.YEAR);
      int startYear = 2004;
      long fromTime = 0;
      long toTime = 0;

      if(!sPeriod.equals("")) {
          try{
              fromDay = new Integer(sPeriod.substring(0,2)).intValue();
              fromMonth = new Integer(sPeriod.substring(2,4)).intValue();
              fromYear = new Integer(sPeriod.substring(4,8)).intValue();

              toDay = new Integer(sPeriod.substring(8,10)).intValue();
              toMonth = new Integer(sPeriod.substring(10,12)).intValue();
              toYear = new Integer(sPeriod.substring(12)).intValue();
              if((fromDay+fromMonth+fromYear+toDay+toMonth+toYear)>0) {
                  // if not set use defaults for day, month and year
                  if(fromDay==0) fromDay = 1;
                  if(fromMonth==0) fromMonth = 1;
                  if(fromYear==0) fromYear = startYear;
                  if(toDay==0) toDay = thisDay;
                  if(toMonth==0) toMonth = thisMonth;
                  if(toYear==0) toYear = thisYear;

                  cal.set(fromYear,fromMonth-1,fromDay,0,0,0);
                  fromTime = (cal.getTime().getTime()/1000);

                  cal.set(toYear,toMonth-1,toDay,23,60,0);
                  toTime = (cal.getTime().getTime()/1000);
              }
          } catch (Exception e) { }
      }

      long [] period = { fromTime, toTime, fromDay, fromMonth, fromYear, toDay, toMonth, toYear, thisDay, thisMonth, thisYear, startYear };

      return period;
   }
   
    /* 
    * version used by natmm search page
    */
    public HashSet addPages(
      Cloud cloud,
      net.sf.mmapps.modules.lucenesearch.SearchConfig cf,
      String sQuery,
      int index,
      String path,
      String rootRubriek,
      long nowSec,
      HashSet hsetPagesNodes) {

      HashSet hsetNodes = new HashSet();
      try { 
        net.sf.mmapps.modules.lucenesearch.SearchIndex si = cf.getIndex(index);
        Analyzer analyzer = si.getAnalyzer();
        
        QueryParser qp = new QueryParser("indexed.text", analyzer);
        qp.setDefaultOperator(QueryParser.AND_OPERATOR);
        org.apache.lucene.search.Query result = null;
        //SearchValidator sv = new SearchValidator();
        String value = SearchValidator.validate(sQuery);
        try {
          result = qp.parse(value);
        } catch (Exception e) {
          log.error("Error parsing field 'indexed.text' with value '" + value + "'");
        }
        if (result != null) {
        int quarterOfAnHour = 60*15;
        BooleanQuery constructedQuery = new BooleanQuery();
        constructedQuery.add(result, BooleanClause.Occur.MUST);
        
        IndexReader ir = IndexReader.open(si.getIndex());
        IndexSearcher searcher = new IndexSearcher(ir); 
        Hits hits = searcher.search(constructedQuery);

        TreeSet includedEvents = new TreeSet();
      
        for (int i = 0; i < hits.length(); i++) {
            Document doc = hits.doc(i);
            String docNumber = doc.get("node");
            if(path!=null) {
                NodeList list = cloud.getList(docNumber,path,"pagina.number",null,null,null,"SOURCE",true);
                for(int j=0; j<list.size(); j++) {
                  String paginaNumber = list.getNode(j).getStringValue("pagina.number");
                  if(PaginaHelper.getSubsiteRubriek(cloud,paginaNumber).equals(rootRubriek)) {
                    if (index==1) {
                      PaginaHelper ph = new PaginaHelper(cloud);
                      String sConstraints = (new nl.leocms.util.tools.SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
                      NodeList nl = cloud.getList(docNumber,"natuurgebieden,rolerel,artikel","artikel.number",sConstraints,null,null,null,true);
                      if (ph.getPaginaTemplate(paginaNumber).getStringValue("url").equals("routes.jsp")
                        &&(nl.size()>0)){
                        hsetPagesNodes.add(paginaNumber);
                        hsetNodes.add(docNumber);
                      }
                    } else if (index==2) {
                      PaginaHelper ph = new PaginaHelper(cloud);
                      if (ph.getPaginaTemplate(paginaNumber).getStringValue("url").equals("natuurgebieden.jsp")){
                        hsetPagesNodes.add(paginaNumber);
                        hsetNodes.add(docNumber);
                      }
                    } else {
                       //FIX FOR NMCMS-230
                       //verloopdatum filter for articles
                       PaginaHelper ph = new PaginaHelper(cloud);
                       String sConstraints = (new nl.leocms.util.tools.SearchUtil()).articleConstraint(nowSec, quarterOfAnHour);
                       Node foundNode = cloud.getNode(docNumber);
                       //boolean test = articleMatchesConstraint(foundNode.getLongValue("embargo"), foundNode.getStringValue("use_verloopdatum"), foundNode.getLongValue("verloopdatum"), nowSec, quarterOfAnHour);
                       
                       if (true) {
                          hsetPagesNodes.add(paginaNumber);
                          hsetNodes.add(docNumber);
                       }
                       
                    }
                  }
                }
            } else { // *** no path implies an Evenement ***
                Node e = cloud.getNode(docNumber);
               String sParent =  Evenement.findParentNumber(docNumber);
                if(!includedEvents.contains(sParent) && Evenement.isOnInternet(e,nowSec)) {
                  String paginaNumber = cloud.getNode("agenda").getStringValue("number");
                   if(PaginaHelper.getSubsiteRubriek(cloud,paginaNumber).equals(rootRubriek)) {
                     hsetNodes.add(docNumber);
                      includedEvents.add(sParent);
                  }
                }
             }
         }
        
        //Close resources when they are not needed anymore.
        if(searcher!=null) { searcher.close(); }
        if(ir!=null) { ir.close(); }
        
        }
      } catch (Exception e) { 
        log.error("lucene index " + index + " throws error on query " + sQuery + " - " + e); 
        //Also log the specific error
      } 
      return hsetNodes;
   }

   /* 
    * version used by nmintra search page
    */
    public HashSet addPages(
      Cloud cloud,
      SearchConfig cf,
		  String sQuery,
      int index,
      String path,
      String sRubriekNumber,
		  String sPoolNumber,
      long nowSec,
		  long fromTime,
		  long toTime,
		  boolean searchArchive,
      HashSet hsetPagesNodes) {
      HashSet hsetNodes = new HashSet();
      try {
         SearchIndex si = cf.getIndex(index);
         Analyzer analyzer = si.getAnalyzer();
         IndexReader ir = IndexReader.open(si.getIndex());
         QueryParser qp = new QueryParser("indexed.text", analyzer);
         qp.setDefaultOperator(QueryParser.AND_OPERATOR);
         org.apache.lucene.search.Query result = null;
         SearchValidator sv = new SearchValidator();
         String value = sv.validate(sQuery);
         try {
           result = qp.parse(value);
         } catch (Exception e) {
           log.error("Error parsing field 'indexed.text' with value '" + value + "'");
         }
         if (result != null) {
            BooleanQuery constructedQuery = new BooleanQuery();
            constructedQuery.add(result, BooleanClause.Occur.MUST);

            IndexSearcher searcher = new IndexSearcher(ir);
            Hits hits = searcher.search(constructedQuery);
            TreeSet includedEvents = new TreeSet();

            for (int i = 0; i < hits.length(); i++) {
               Document doc = hits.doc(i);
               String docNumber = doc.get("node");
               if (path != null) {
                  hsetNodes.addAll(calculate(cloud, path, sRubriekNumber, sPoolNumber,
                               docNumber, nowSec, fromTime, toTime,
                               searchArchive, hsetPagesNodes)) ;
               } else { // add all hits for null path
                 hsetNodes.add(docNumber);
               }
            }
            if (searcher != null) { searcher.close(); }
            if (ir != null) { ir.close(); }
         }
         log.debug("Searching for " + sQuery + " on " + path + " results in nodes " + hsetNodes + " and pages " + hsetPagesNodes);
      } catch (Exception e) {
         log.error("Lucene index " + index + " on query " + sQuery + " throws error " + e);
      }
      return hsetNodes;
   }

   /* 
    * version used by nmintra search page, if the query is empty
    * see for a good example of usage: templates/nmintra/info.jsp
    */
   public HashSet addPages(
      Cloud cloud,
      String path,
      String sRubriekNumber,
      String sPoolNumber,
      long nowSec,
      long fromTime,
      long toTime,
      boolean searchArchive,
      HashSet hsetPagesNodes) {

      HashSet hsetNodes = new HashSet();
      String sBuiderName = getBuilderName(path);
      NodeList nl = cloud.getList(null, path, sBuiderName + ".number", null, null, null, null, true);
      for (int i = 0; i < nl.size(); i++) {
         String docNumber = nl.getNode(i).getStringValue(sBuiderName + ".number");
         hsetNodes.addAll(calculate(cloud, path, sRubriekNumber, sPoolNumber,
                               docNumber, nowSec, fromTime, toTime,
                               searchArchive, hsetPagesNodes)) ;
      }

      return hsetNodes;

   }

   public static HashSet calculate(
      Cloud cloud,
      String path,
      String sRubriekNumber,
      String sPoolNumber,
      String docNumber,
      long nowSec,
      long fromTime,
      long toTime,
      boolean searchArchive,
      HashSet hsetPagesNodes) {

      log.debug("calculate(path="+path
        +" sRubriekNumber="+sRubriekNumber
        +" sPoolNumber="+sPoolNumber
        +" docNumber="+docNumber
        +" nowSec="+nowSec
        +" fromTime="+fromTime
        +" toTime="+toTime
        +" searchArchive="+searchArchive
        +" hsetPagesNodes="+hsetPagesNodes+")");
      
      HashSet hsetNodes = new HashSet();
      String sBuiderName = getBuilderName(path);
      String sConstraints = "";
      if(fromTime < toTime) {
         // exclude builders that do not have embargo and verloopdatum
         if(!sBuiderName.equals("producttypes") && !sBuiderName.equals("documents")) {
            sConstraints += "( " + sBuiderName + ".embargo > '" + fromTime +
               "') AND (" + sBuiderName + ".embargo < '" + toTime + "')";
         }
      }
      Node doc = null;
      try {
        doc = cloud.getNode(docNumber);
      } catch (Exception e) {
        log.error("Node " + docNumber + " does not exist, maybe it was deleted.");
      }
      if(doc!=null) {
        NodeList list = cloud.getList(docNumber, path, "pagina.number," +
                                      sBuiderName + ".number",
                                      sConstraints, null, null, null, true);
        for (int j = 0; j < list.size(); j++) {
           String paginaNumber = list.getNode(j).getStringValue("pagina.number");
           if (docNumber.equals("")){
              docNumber = list.getNode(j).getStringValue(sBuiderName + ".number");
           }
           Vector breadcrumbs = PaginaHelper.getBreadCrumbs(cloud, paginaNumber);
           boolean inRubriek = sRubriekNumber.equals("") || breadcrumbs.contains(sRubriekNumber);
           // exclude builders that do not have a relation to pools
           boolean inPool = sPoolNumber.equals("")
                    || ( !sBuiderName.equals("producttypes") 
                          && !sBuiderName.equals("documents")
                          && PoolUtil.containsPool(cloud, docNumber, sPoolNumber));
           boolean inArchive = breadcrumbs.contains(cloud.getNode("archive").getStringValue("number"));
           // when not searching the archive, exclude contentelements that are in the archive
           if(inRubriek && inPool && !(!searchArchive && inArchive) ) {
              hsetPagesNodes.add(paginaNumber);
              hsetNodes.add(docNumber);
           }
        }
      }
      log.debug("found " + hsetNodes);
      return hsetNodes;
   }

   public static String getBuilderName(String path) {
      String builderName = path;
      if(path.indexOf(",")>0) {
         builderName = path.substring(0, path.indexOf(","));
      }
      return builderName;   
   }
   
   public NodeList ArtikelsRelatedToPagina (Cloud cloud, String sPaginaID, String sConstraints){
      return cloud.getList(sPaginaID,"pagina,contentrel,artikel","artikel.number",
      sConstraints,"artikel.embargo",null,"destination",true);
   }
   
   
   public String queryString(String sQuery) {
     String DOUBLESPACE = "  ";
     String SINGLESPACE = " ";
     String qStr = sQuery;
     while(qStr.indexOf(DOUBLESPACE)>-1) {
       qStr = qStr.replaceAll(DOUBLESPACE,SINGLESPACE);
     }
     qStr = qStr.trim().replaceAll(SINGLESPACE,"* AND ") + "*";
     return  qStr;
   }

}
