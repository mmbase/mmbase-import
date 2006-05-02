<%! public HashSet addPages(
      Cloud cloud,
      org.mmbase.util.logging.Logger log,
      net.sf.mmapps.modules.lucenesearch.SearchConfig cf,
		String sQuery,
      int index,
      String path,
      String rootRubriek,
		String sPoolNumber,
      long nowSec,
		long fromTime,
		long toTime,
		String sArchieve,
      HashSet hsetPagesNodes) {

   HashSet hsetNodes = new HashSet();
   try { 
      net.sf.mmapps.modules.lucenesearch.SearchIndex si = cf.getIndex(index);
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
   	      if(path!=null) {
					String sBuiderName = path.substring(0,path.indexOf(","));
					String sConstraints = "";
					if (sBuiderName.equals("producttypes")||(sBuiderName.equals("documents"))){
						if (sArchieve.equals("ja")||(fromTime>0)||(toTime>0))
						   break;
					}
					else{
						if (sArchieve.equals("ja")){
							sConstraints = "(" + sBuiderName + ".verloopdatum < '" + nowSec + "')"; 
						} else {
							sConstraints = "(" + sBuiderName + ".verloopdatum > '" + nowSec + "')"; 
						}
						if ((fromTime>0)||(toTime>0)) {
							sConstraints += " AND ( " + sBuiderName + ".embargo > '" + fromTime + "') AND (" + sBuiderName + ".embargo < '" + toTime + "')";
						}
					}
					NodeList list = cloud.getList(docNumber,path,"pagina.number",sConstraints,null,null,null,true);
            	for(int j=0; j<list.size(); j++) {
               	String paginaNumber = list.getNode(j).getStringValue("pagina.number");
						if((rootRubriek.equals(""))||(PaginaHelper.getRootRubriek(cloud,paginaNumber).equals(rootRubriek))) {
							NodeList nlPools = PoolUtil.getPool(cloud,docNumber);
							if (sPoolNumber.equals("")||(nlPools.contains(cloud.getNode(sPoolNumber)))){
	      	            hsetPagesNodes.add(paginaNumber);
   	      	         hsetNodes.add(docNumber);
							}
	               }
   	         }
      	   } 
      	}
			

	      if(searcher!=null) { searcher.close(); }
   	   if(ir!=null) { ir.close(); }
		}	
   } catch (Exception e) { 
      log.error("lucene index " + index + " throws error on query " + sQuery); 
   } 
   return hsetNodes;
}

%><% 

boolean debug = false;

%><!-- searching on <%= sQuery %> --><% 
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

%><mm:log jspvar="log"><% //debug = true;

hsetArticlesNodes = addPages(cloud, log, cf, sQuery, 0, "artikel,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>articleHits:<br/><%= hsetArticlesNodes %><br/><%= hsetPagesNodes %><% } 

hsetTeaserNodes = addPages(cloud, log, cf, sQuery, 4, "teaser,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>natuurgebiedenHits:<br/><%= hsetTeaserNodes %><br/><%= hsetPagesNodes %><% } 

hsetProductsNodes = addPages(cloud, log, cf, sQuery, 6, "products,posrel,producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>ProductsHits:<br/><%= hsetProductsNodes %><br/><%= hsetPagesNodes %><% } 

hsetProducctypesNodes = addPages(cloud, log, cf, sQuery, 5, "producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>ProducctypesHits:<br/><%= hsetProducctypesNodes %><br/><%= hsetPagesNodes %><% } 

hsetItemsNodes = addPages(cloud, log, cf, sQuery, 7, "items,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>ItemsHits:<br/><%= hsetItemsNodes %><br/><%= hsetPagesNodes %><% } 

hsetDocumentsNodes = addPages(cloud, log, cf, sQuery, 8, "documents,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>DocumentsHits:<br/><%= hsetDocumentsNodes %><br/><%= hsetPagesNodes %><% } 

hsetVacatureNodes = addPages(cloud, log, cf, sQuery, 9, "vacature,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, sArchieve, hsetPagesNodes);
if(debug) { %><br/>VacatureHits:<br/><%= hsetVacatureNodes %><br/><%= hsetPagesNodes %><% } 

%></mm:log
><%

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