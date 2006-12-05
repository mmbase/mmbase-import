<%@page import="nl.leocms.util.tools.SearchUtil" 
%><%@include file="../../includes/time.jsp" %><%

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
SearchUtil su = new SearchUtil();

String qStr = su.queryString(sQuery);

hsetNatuurgebiedenRouteNodes = su.addPages(cloud,cf,qStr,1,"natuurgebieden,pos4rel,provincies,contentrel,pagina",subsiteID,nowSec,hsetPagesNodes);
if(debug) { %><br/>natuurgebiedenRoutesHits:<br/><%= hsetNatuurgebiedenRouteNodes %><br/><%= hsetPagesNodes %><% } 

hsetNatuurgebiedenNatuurgebiedenNodes = su.addPages(cloud,cf,qStr,2,"natuurgebieden,pos4rel,provincies,contentrel,pagina",subsiteID,nowSec,hsetPagesNodes);
if(debug) { %><br/>natuurgebiedenNatuurgebiedenHits:<br/><%= hsetNatuurgebiedenNatuurgebiedenNodes %><br/><%= hsetPagesNodes %><% } 

hsetArticlesNodes = su.addPages(cloud,cf,qStr,0,"artikel,contentrel,pagina",subsiteID,nowSec,hsetPagesNodes);
if(debug) { %><br/>articleHits:<br/><%= hsetArticlesNodes %><br/><%= hsetPagesNodes %><% } 

hsetArtDossierNodes = su.addPages(cloud,cf,qStr,0,"artikel,posrel,dossier,posrel,pagina",subsiteID,nowSec,hsetPagesNodes);
if(debug) { %><br/>artByDossierHits:<br/><%= hsetArtDossierNodes %><br/><%= hsetPagesNodes %><% } 

hsetFormulierNodes = su.addPages(cloud,cf,qStr,3,"formulier,posrel,pagina",subsiteID,nowSec,hsetPagesNodes);
if(debug) { %><br/>formulierHits:<br/><%= hsetFormulierNodes %><br/><%= hsetPagesNodes %><% } 

hsetEvenementNodes = su.addPages(cloud,cf,qStr,4,null,subsiteID,nowSec,hsetPagesNodes);
if(hsetEvenementNodes.size()>0) { 
   %><mm:node number="agenda">
      <mm:field name="number" jspvar="agenda_number" vartype="String" write="false"><%
         hsetPagesNodes.add(agenda_number); 
      %></mm:field>
   </mm:node><%
}
if(debug) { %><br/>evenementHits:<br/><%= hsetEvenementNodes %><br/><%= hsetPagesNodes %><% } 

%><%--
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