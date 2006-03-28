<%! public HashSet addPages(
      Cloud cloud,
      org.mmbase.util.logging.Logger log,
      SearchConfig cf,
      Query luceneQuery,
      int index,
      String path,
      String rootRubriek,
      long nowSec,
      HashSet hsetPagesNodes) {

   HashSet hsetNodes = new HashSet();
   try {
      SearchIndex si = cf.getIndex(index);
      IndexReader ir = IndexReader.open(si.getIndex());
      IndexSearcher searcher = new IndexSearcher(ir);      
      Hits hits = searcher.search(luceneQuery);
      TreeSet includedEvents = new TreeSet();
   
      for (int i = 0; i < hits.length(); i++) {
         Document doc = hits.doc(i);
         String docNumber = doc.get("node");
         if(path!=null) {
            NodeList list = cloud.getList(docNumber,path,"pagina.number",null,null,null,"SOURCE",true);
            for(int j=0; j<list.size(); j++) {
               String paginaNumber = list.getNode(j).getStringValue("pagina.number");
               if(PaginaHelper.getRootRubriek(cloud,paginaNumber).equals(rootRubriek)) {
                  hsetPagesNodes.add(paginaNumber);
                  hsetNodes.add(docNumber);
               }
            }
         } else { // *** no path implies an Evenement ***
            Node e = cloud.getNode(docNumber);
            String sParent =  Evenement.findParentNumber(docNumber);
            if(!includedEvents.contains(sParent) && Evenement.isOnInternet(e,nowSec)) {
               String paginaNumber = cloud.getNode("agenda").getStringValue("number");
               if(PaginaHelper.getRootRubriek(cloud,paginaNumber).equals(rootRubriek)) {
                  hsetNodes.add(docNumber);
                  includedEvents.add(sParent);
               }
            }
         }
      }

      if(searcher!=null) { searcher.close(); }
      if(ir!=null) { ir.close(); }
   } catch (Exception e) { 
      log.error("lucene index " + index + " throws error on query " + luceneQuery); 
   } 
   return hsetNodes;
}

%><%@include file="../../includes/time.jsp" %><%

boolean debug = false;

String DOUBLESPACE = "  ";
String SINGLESPACE = " ";
String qStr = sQuery;
while(qStr.indexOf(DOUBLESPACE)>-1) {
   qStr = qStr.replaceAll(DOUBLESPACE,SINGLESPACE);
}
qStr = qStr.trim().replaceAll(SINGLESPACE,"* AND ")+ "*";
%><!-- searching on <%= qStr %> --><%
Analyzer analyzer = new StopAnalyzer();
String[] fields = {"titel", "omschrijving", "ondertitel", "tekst", "metatags"};
Query luceneQuery = MultiFieldQueryParser.parse(qStr, fields, analyzer);

LuceneManager lm  = mod.getLuceneManager();
SearchConfig cf = lm.getConfig();

// *** all pages that belong to the selected rubriek: hsetAllowedNodes ***
if((sCategory != null) && (!sCategory.equals(""))) {
   String sConstraints = "naam='" + sCategory + "'";
   %><mm:list nodes="<%= sCategory %>" path="rubriek,posrel,pagina" fields="pagina.number">
      <mm:field name="pagina.number" jspvar="sPagesID" vartype="String" write="false"><%
         hsetAllowedNodes.add(sPagesID);
      %></mm:field>
   </mm:list><%
}

%><mm:log jspvar="log"><%

hsetArticlesNodes = addPages(cloud, log, cf, luceneQuery, 0, "artikel,contentrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>articleHits:<br/><%= hsetArticlesNodes %><br/><%= hsetPagesNodes %><% } 

hsetArtDossierNodes = addPages(cloud, log, cf, luceneQuery, 0, "artikel,posrel,dossier,posrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>artByDossierHits:<br/><%= hsetArtDossierNodes %><br/><%= hsetPagesNodes %><% } 

hsetNatuurgebiedenNodes = addPages(cloud, log, cf, luceneQuery, 1, "natuurgebieden,pos4rel,provincies,contentrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>natuurgebiedenHits:<br/><%= hsetNatuurgebiedenNodes %><br/><%= hsetPagesNodes %><% } 

hsetFormulierNodes = addPages(cloud, log, cf, luceneQuery, 2, "formulier,posrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>formulierHits:<br/><%= hsetFormulierNodes %><br/><%= hsetPagesNodes %><% } 

hsetEvenementNodes = addPages(cloud, log, cf, luceneQuery, 3, null, rootID, nowSec, hsetPagesNodes);
if(hsetEvenementNodes.size()>0) { 
   %><mm:node number="agenda">
      <mm:field name="number" jspvar="agenda_number" vartype="String" write="false"><%
         hsetPagesNodes.add(agenda_number); 
      %></mm:field>
   </mm:node><%
}
if(debug) { %><br/>evenementHits:<br/><%= hsetEvenementNodes %><br/><%= hsetPagesNodes %><% } 

%></mm:log
><%--
// *** list of pages that contain metatags: hsetMetaNodes ***
if(debug) { %><br/>substracting for metatags:<br/><%}
SearchIndex metaSearchindex = cf.getIndex(4);
IndexReader mir = IndexReader.open(metaSearchindex.getIndex());
IndexSearcher metaSearcher = new IndexSearcher(mir);
Hits metaHits = null;
if ((sMeta != null) && (!sMeta.equals(""))) {
   metaHits = metaSearcher.search(MultiFieldQueryParser.parse(sMeta, fields, analyzer));

   if (metaHits != null){
   
      HashSet hsetMetaNodes = new HashSet();
      for (int i = 0; i < metaHits.length(); i++) {
   
         Document doc = metaHits.doc(i);
         String docNumber = doc.get("node");
         hsetMetaNodes.add(docNumber);
      }
   
      // *** remove all pages that do not contain the selected metatag ***
      for(Iterator it = hsetPagesNodes.iterator(); it.hasNext(); ) {
   
         String sPageID = (String) it.next();
         if (!hsetMetaNodes.contains(sPageID)) {
            it.remove();
            if(debug) { %><%= sPageID %>, <% }
         }
      }
   } 
}
if(metaSearcher!=null) { metaSearcher.close(); }
if(mir!=null) { mir.close(); }
--%><%

// *** Create list of categories from list of pages: hSetCategories ***
// *** Seems to me it is faster than create another index ***
for (Iterator it = hsetPagesNodes.iterator(); it.hasNext(); ) {
   
   String sPageID = (String) it.next();
   if((hsetAllowedNodes.size() > 0) && (!hsetAllowedNodes.contains(sPageID)))
   {
      continue;
   }
   %><mm:node number="<%=sPageID%>">
      <mm:relatednodes type="rubriek">
         <mm:field name="number" jspvar="sRubriek" vartype="String" write="false"><%
            hsetCategories.add(sRubriek);
         %></mm:field>
      </mm:relatednodes>
   </mm:node><%
}
%>