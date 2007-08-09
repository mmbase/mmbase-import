<%@page import="
   org.mmbase.module.Module,
	net.sf.mmapps.modules.lucenesearch.LuceneModule,
	net.sf.mmapps.modules.lucenesearch.util.*,
	org.apache.lucene.index.IndexReader,
	org.apache.lucene.analysis.*,
   org.apache.lucene.search.*,
	org.apache.lucene.queryParser.QueryParser,
	org.apache.lucene.document.Document,
   nl.leocms.util.tools.SearchUtil" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<mm:log jspvar="log">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%@include file="includes/calendar.jsp" %>
<% 
   
  String sQuery = request.getParameter("search");
  if(sQuery==null) { sQuery = ""; }
  String sMeta = request.getParameter("trefwoord");
  String sCategory = request.getParameter("categorie");
  if(sCategory==null) { sCategory = ""; }
  String sPool = request.getParameter("pool");
  if (sPool==null) { sPool = ""; }
  String sArchive = request.getParameter("archive");
  if (sArchive==null) { sArchive = "nee"; }
  String sAdv = request.getParameter("adv");
  if (sAdv==null) { sAdv = ""; }
	
  RubriekHelper rh = new RubriekHelper(cloud);
  SearchUtil su = new SearchUtil();
  
  long [] period = su.getPeriod(periodId);
  long fromTime = period[0];
  long toTime = period[1];
  int fromDay = (int) period[2]; int fromMonth = (int) period[3]; int fromYear = (int) period[4];
  int toDay = (int) period[5]; int toMonth = (int) period[6]; int toYear = (int) period[7];
  int thisYear = (int) period[10];
  int startYear = (int) period[11];
  
  boolean searchIsOn = (searchId.equals("")||searchId.equals(defaultSearchText))&&sCategory.equals("")&&sPool.equals("")&&(fromTime==0)&&(toTime==0);
  
  boolean categorieExists = false;
  %><mm:node number="<%=  sCategory %>" notfound="skipbody"
    ><mm:nodeinfo type="type" write="false" jspvar="nType" vartype="String"><%
       categorieExists = nType.equals("rubriek");
    %></mm:nodeinfo
  ></mm:node><%
  if(!categorieExists) { sCategory = ""; }
  
  HashSet hsetAllowedNodes = new HashSet();
  HashSet hsetPagesNodes = new HashSet();
  HashSet hsetRubrieken = new HashSet();
  
  HashSet hsetArticlesNodes = new HashSet();

  HashSet hsetVraagbaakNodes = new HashSet();  
  
  HashSet hsetTeaserNodes = new HashSet();
  HashSet hsetProducttypesNodes = new HashSet();
  HashSet hsetProductsNodes = new HashSet();
  HashSet hsetItemsNodes = new HashSet();
  HashSet hsetDocumentsNodes = new HashSet();
  HashSet hsetVacatureNodes = new HashSet();
  HashSet hsetContentBlockNodes = new HashSet();
  HashSet hsetPageDescrNodes = new HashSet();
  
  HashSet hsetAttachmentsNodes = new HashSet();

  LuceneModule mod = (LuceneModule) Module.getModule("lucenemodule");
  if(mod!= null) {
    %><%@include file="includes/search/hashsets.jsp" %><%
  }
  %><%@include file="includes/header.jsp" %>
    <td><%@include file="includes/pagetitle.jsp" %></td>
    <td><% 
       String rightBarTitle = "Uitgebreid Zoeken";
       if(actionId.equals("adv_search")) {
          %><%@include file="includes/rightbartitle.jsp" %><%
       } 
       %>
    </td>
	</tr>
	<tr>
	<td class="transperant">
	<div class="<%= infopageClass %>" id="infopage">
   <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td><a name="top"/>
        <% 
        if (searchIsOn){
          %>
          Vul een zoekterm in bij '<%= defaultSearchText %>' en klik op de 'Zoek'-knop om in het Intranet te zoeken."						
          <% 
        } else {
            if(hsetRubrieken.size()==0) {
              %>Er zijn geen zoekresultaten gevonden, die voldoen aan uw zoekopdracht.<%
            } else { 
              %>De volgende zoekresultaten zijn gevonden in:<br/><% 
            }
            NodeList rubriekenList = (NodeList) rh.getTreeNodes(subsiteID);
            
            // *** show anchors to rubrieken
            boolean bFirst = true;
            for (Iterator it = rubriekenList.iterator(); it.hasNext();) {
              String sRubriek = ((Node) it.next()).getStringValue("rubriek.number");
              if(hsetRubrieken.contains(sRubriek)) {
                if(!bFirst) { %> | <% }
                %><mm:node number="<%=sRubriek%>">
                  <mm:field name="naam" jspvar="name" vartype="String" write="false">
                      <a href="search.jsp?<%= request.getQueryString() %>#<mm:field name="number" />">
                         <span class="colortitle" style="text-decoration:underline;"><%= name.toUpperCase() %></span></a>
                  </mm:field>	
                </mm:node><%
                bFirst = false;
              }
			   }
              
			   // *** show rubrieken
		      if (hsetRubrieken.size() > 0) { 
              Vector defaultSearchTerms = new Vector(); 
              defaultSearchTerms = su.createSearchTerms(searchId);
              %>
              <br/><br/>
              <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
              <% 
              for (Iterator it = rubriekenList.iterator(); it.hasNext(); ) {
                String sRubriek = ((Node) it.next()).getStringValue("rubriek.number");
                if(hsetRubrieken.contains(sRubriek)) {
                  HashSet hsetPagesForThisRubriek = rh.getAllPages(sRubriek);
                  %>
                  <mm:node number="<%= sRubriek %>">
                    <a name="<mm:field name="number" />" />
                    <span class="colortitle"><b>
                    <mm:field name="naam" jspvar="name" vartype="String" write="false">
                      <%= name.toUpperCase() %>
                    </mm:field></b>
                    </span>
                    <br/>
                    <%
                    bFirst = true;
                    for (Iterator itp = hsetPagesNodes.iterator(); itp.hasNext(); ) {
                      String sPageID = (String) itp.next();
                      %><%@include file="includes/search/page.jsp" %><%
                      bFirst = false;
                    }
                    %>
                  </mm:node>
                  <div align="right">
                    <a href="?<%= request.getQueryString() %>#top"><img src="media/<%= NMIntraConfig.style1[iRubriekStyle] %>_up.gif" border="0" /></a>
                  </div>
                  <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
                  <%
                }
              }
            }
          }
			%><br/>
			</td>
	      <td><img src="media/spacer.gif" width="10" height="1"></td>
   	   </tr>
      </table>
</div>
</td>
<td><% 

// *********************************** right bar *******************************
	if(actionId.equals("adv_search")) {

		%><%@include file="includes/search/form.jsp"
		%><%@include file="includes/whiteline.jsp" %><%

  } else if(!"".equals(sQuery)) {
    
    String sPageRefMinOne = (String) session.getAttribute("pagerefminone");
		if(sPageRefMinOne!=null) {
      %>
      <mm:list nodes="<%= sPageRefMinOne %>" path="pagina,gebruikt,paginatemplate">
        <a href="<mm:field name="paginatemplate.url"/>?p=<%= sPageRefMinOne %>" style="color:#FFFFFF;margin-left:10px;">
          Terug naar vorige pagina
        </a>
      </mm:list>
      <%
    }
  }
	%></td>
<%@include file="includes/footer.jsp" %>
</mm:log>
</mm:cloud>