<mm:log jspvar="log">
<%

net.sf.mmapps.modules.lucenesearch.LuceneManager lm  = mod.getLuceneManager();
net.sf.mmapps.modules.lucenesearch.SearchConfig cf = lm.getConfig();
if(cf==null) {
   log.error("Cound not read the lucenedatadefinition.xml, is it present in WEB-INF\\config ?");
}

// *** all pages that belong to the selected rubriek: hsetAllowedNodes ***
if((sCategory != null) && (!sCategory.equals(""))) {
   String sConstraints = "naam='" + sCategory + "'";
   %><mm:list nodes="<%= sCategory %>" path="rubriek,posrel,pagina" fields="pagina.number">
      <mm:field name="pagina.number" jspvar="sPagesID" vartype="String" write="false"><%
         hsetAllowedNodes.add(sPagesID);
      %></mm:field>
   </mm:list><%
} 

String qStr = su.queryString(sQuery);

log.info("User searched on '" +  qStr + "'");
boolean searchArchive = sArchive.equals("ja");

// see also PATHS_FROM_PAGE_TO_OBJECTS in nl.leocms.applications.NMIntraConfig.java
if (!sQuery.equals("")){
	
	hsetArticlesNodes = su.addPages(cloud, cf, qStr, 0, "artikel,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);   
   hsetArticlesNodes.addAll(su.addPages(cloud, cf, qStr, 0, "artikel,readmore,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes));
	hsetArticlesNodes.addAll(su.addPages(cloud, cf, qStr, 0, "artikel,pos4rel,images,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes));
	hsetTeaserNodes = su.addPages(cloud, cf, qStr, 5, "teaser,rolerel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetProducttypesNodes = su.addPages(cloud, cf, qStr, 6, "producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetProductsNodes = su.addPages(cloud, cf, qStr, 7, "products,posrel1,producttypes,posrel2,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetItemsNodes = su.addPages(cloud, cf, qStr, 8, "items,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetDocumentsNodes = su.addPages(cloud, cf, qStr, 9, "documents,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetVacatureNodes = su.addPages(cloud, cf, qStr, 10, "vacature,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetContentBlockNodes = su.addPages(cloud, cf, qStr, 11, "contentblocks,readmore,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
  
	hsetPageDescrNodes = su.addPages(cloud, cf, qStr, 12, "pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
   hsetAttachmentsNodes = su.addPages(cloud, cf, qStr, 13, null, sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);

   hsetVraagbaakNodes = su.addPages(cloud, cf, qStr, 14, "vraagbaak,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);   
  
} else if (!sCategory.equals("")||!sPool.equals("")||(fromTime>0)||(toTime>0)){

	hsetArticlesNodes = su.addPages(cloud, "artikel,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetArticlesNodes.addAll(su.addPages(cloud, "artikel,readmore,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes));
	hsetArticlesNodes.addAll(su.addPages(cloud, "artikel,pos4rel,images,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes));
   hsetTeaserNodes = su.addPages(cloud, "teaser,rolerel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetProducttypesNodes = su.addPages(cloud, "producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetProductsNodes = su.addPages(cloud, "products,posrel,producttypes,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetItemsNodes = su.addPages(cloud, "items,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetDocumentsNodes = su.addPages(cloud, "documents,posrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetVacatureNodes = su.addPages(cloud, "vacature,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
	hsetContentBlockNodes = su.addPages(cloud, "contentblocks,readmore,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
  
	hsetPageDescrNodes = su.addPages(cloud, "pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);
   
   hsetVraagbaakNodes = su.addPages(cloud, "vraagbaak,contentrel,pagina", sCategory, sPool, nowSec, fromTime, toTime, searchArchive, hsetPagesNodes);  

}
// *** Create list of categories from list of pages: hsetRubrieken ***
// *** Seems to me it is faster than create another index ***
for (Iterator it = hsetPagesNodes.iterator(); it.hasNext(); ) {
   
   String sPageID = (String) it.next();
   if((hsetAllowedNodes.size() == 0) || hsetAllowedNodes.contains(sPageID)) {
     hsetRubrieken.add(ph.getHoofdRubriek(cloud,sPageID));
   }
}
%>
</mm:log>