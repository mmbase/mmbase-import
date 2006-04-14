<%! public HashSet addPages(
      Cloud cloud,
      org.mmbase.util.logging.Logger log,
      net.sf.mmapps.modules.lucenesearch.SearchConfig cf,
      org.apache.lucene.search.Query luceneQuery,
      int index,
      String path,
      String rootRubriek,
      long nowSec,
      HashSet hsetPagesNodes) {

   HashSet hsetNodes = new HashSet();
   try { 
      net.sf.mmapps.modules.lucenesearch.SearchIndex si = cf.getIndex(index);
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
         } 
      }

      if(searcher!=null) { searcher.close(); }
      if(ir!=null) { ir.close(); }
   } catch (Exception e) { 
      log.error("lucene index " + index + " throws error on query " + luceneQuery); 
   } 
   return hsetNodes;
}

%><%

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
org.apache.lucene.search.Query luceneQuery = MultiFieldQueryParser.parse(qStr, fields, analyzer);

net.sf.mmapps.modules.lucenesearch.LuceneManager lm  = mod.getLuceneManager();
net.sf.mmapps.modules.lucenesearch.SearchConfig cf = lm.getConfig();

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

hsetTeaserNodes = addPages(cloud, log, cf, luceneQuery, 1, "teaser,contentrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>natuurgebiedenHits:<br/><%= hsetTeaserNodes %><br/><%= hsetPagesNodes %><% } 

hsetProducctypesNodes = addPages(cloud, log, cf, luceneQuery, 2, "producttypes,posrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>ProducctypesHits:<br/><%= hsetProducctypesNodes %><br/><%= hsetPagesNodes %><% } 

hsetProductsNodes = addPages(cloud, log, cf, luceneQuery, 3, "products,posrel,producttypes,posrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>ProductsHits:<br/><%= hsetProductsNodes %><br/><%= hsetPagesNodes %><% } 

hsetItemsNodes = addPages(cloud, log, cf, luceneQuery, 4, "items,posrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>ItemsHits:<br/><%= hsetItemsNodes %><br/><%= hsetPagesNodes %><% } 

hsetDocumentsNodes = addPages(cloud, log, cf, luceneQuery, 5, "documents,posrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>DocumentsHits:<br/><%= hsetDocumentsNodes %><br/><%= hsetPagesNodes %><% } 

hsetVacatureNodes = addPages(cloud, log, cf, luceneQuery, 6, "vacature,contentrel,pagina", rootID, nowSec, hsetPagesNodes);
if(debug) { %><br/>VacatureHits:<br/><%= hsetVacatureNodes %><br/><%= hsetPagesNodes %><% } 

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