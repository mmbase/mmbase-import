<mm:log jspvar="log">
<% 
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

String DOUBLESPACE = "  ";
String SINGLESPACE = " ";
String qStr = sQuery;
while(qStr.indexOf(DOUBLESPACE)>-1) {
   qStr = qStr.replaceAll(DOUBLESPACE,SINGLESPACE);
}
qStr = qStr.trim().replaceAll(SINGLESPACE,"* AND ") + "*";


log.info("******* Starting search on '" +  qStr + "' *******");
boolean searchArchive = sArchive.equals("ja");
hsetArticlesNodes = su.addPages(cloud, cf, qStr, 0, "artikel,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
hsetTeaserNodes = su.addPages(cloud, cf, qStr, 4, "teaser,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
hsetProducctypesNodes = su.addPages(cloud, cf, qStr, 5, "producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
hsetProductsNodes = su.addPages(cloud, cf, qStr, 6, "products,posrel,producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
hsetItemsNodes = su.addPages(cloud, cf, qStr, 7, "items,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
hsetDocumentsNodes = su.addPages(cloud, cf, qStr, 8, "documents,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
hsetVacatureNodes = su.addPages(cloud, cf, qStr, 9, "vacature,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);

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
</mm:log>