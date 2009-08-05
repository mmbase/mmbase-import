<%@page import="
  org.mmbase.module.Module,
	net.sf.mmapps.modules.lucenesearch.LuceneModule,
	nl.leocms.util.tools.SearchUtil" %>
<%@include file="/taglibs.jsp" %>
<%@include file="includes/getactiveaccount.jsp" %>
<mm:content type="text/html" escaper="none">
<mm:cloud logon="<%= account %>" pwd="<%= password %>" jspvar="cloud" method="sessionlogon">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%
if(!articleId.equals("-1")) { 
   String articleTemplate = "/" + ph.getSubDir(cloud,paginaID) + "article.jsp" + templateQueryString;
   %>
   <mm:present referid="newsletter_layout">
      <% articleTemplate = "/" + ph.getSubDir(cloud,paginaID) + "news.jsp" + templateQueryString; %>
   </mm:present>
   <%
   articleTemplate += (articleTemplate.indexOf("?")==-1 ? "?" : "&" ) + "showteaser=false";
   response.sendRedirect(articleTemplate);

} else {
    expireTime = newsExpireTime;
    if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) { 
      expireTime = 5; // this page is also editted by website visitors
    }
    %>
    <cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
    <%
    
      if(termSearchId.equals(defaultSearchText)) { termSearchId = ""; }

      int objectPerPage = 9;
      int thisOffset = 1;
      
      try{
          if(!offsetId.equals("")){
              thisOffset = Integer.parseInt(offsetId);
              offsetId ="";
          }
      } catch(Exception e) {} 
        
      String thisPool = "-1";
      if(!poolId.equals("")){ 
          thisPool = poolId; 
          poolId = ""; 
      }
      
      SearchUtil su = new SearchUtil();
      long [] period = su.getPeriod(periodId);
      long fromTime = period[0];
      long toTime = period[1];
      int fromDay = (int) period[2]; int fromMonth = (int) period[3]; int fromYear = (int) period[4];
      int toDay = (int) period[5]; int toMonth = (int) period[6]; int toYear = (int) period[7];
      int thisYear = (int) period[10];
      int startYear = (int) period[11];
      
      if(toTime==0 || toTime > nowSec) { // do not show articles under embargo
         toTime = nowSec;
      }
      
      String rightBarTitle = "";
      %><mm:node number="<%= paginaID %>" jspvar="thisPage">
          <mm:field name="bron">
              <mm:compare value="0" inverse="true">
              <%
              rightBarTitle = "Zoek in " + thisPage.getStringValue("titel");
              %>
              </mm:compare>
         </mm:field>
      </mm:node
      ><%@include file="includes/info/movetoarchive.jsp" 
      %><%@include file="includes/tickertape_init.jsp" 
      %><%@include file="includes/header.jsp" 
      %><td><%@include file="includes/pagetitle.jsp" %></td>
      <td><%@include file="includes/rightbartitle.jsp" %></td>
      </tr>
      <tr>
      <td class="transperant">
      <div class="<%= infopageClass %>" id="infopage">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><%@include file="includes/relatedteaser.jsp" %><%
              
              TreeMap tmapArticles = new TreeMap();
              String sPool = (thisPool.equals("-1") ? "" : thisPool);
              HashSet hsetPages = new HashSet();
              HashSet hsetArticles = new HashSet();
              
              if(!termSearchId.equals("")) {
                String qStr = su.queryString(termSearchId);
                LuceneModule mod = (LuceneModule) Module.getModule("lucenemodule");
                net.sf.mmapps.modules.lucenesearch.LuceneManager lm  = mod.getLuceneManager();
                net.sf.mmapps.modules.lucenesearch.SearchConfig cf = lm.getConfig();
                hsetArticles = su.addPages(cloud,cf,qStr,0,"artikel,contentrel,pagina","",sPool,nowSec,fromTime,toTime,isArchive,hsetPages);
              } else {
              	 hsetArticles = su.addPages(cloud, "artikel,contentrel,pagina", "", sPool, nowSec, fromTime, toTime, isArchive, hsetPages);
              }
              for (Iterator it = hsetArticles.iterator(); it.hasNext(); ) {
                String article = (String) it.next();
                %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="<%= "artikel.number = '" + article + "'" %>"><%
                  tmapArticles.put(new Long(cloud.getNode(article).getLongValue("embargo")),article);
                %></mm:list><%
              }
              int listSize = tmapArticles.size(); 
              
              String extTemplateQueryString = templateQueryString; 
              if(!periodId.equals("")){ extTemplateQueryString += "&d=" + periodId; }
              %>
              <%@include file="includes/info/offsetlinks.jsp" %>
              <% 
              if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) { 
                %>
                <div style="text-align:right"><a href="<%= editwizard_location 
                       %>/jsp/wizard.jsp?language=nl&wizard=config/artikel/artikel_nieuws_nmintra_simple&objectnumber=new&origin=<%= paginaID 
                       %>&referrer=<%= request.getServletPath().replaceAll("//","/")+"?p=" + paginaID
                       %>">voeg een nieuwsbericht toe</a></div>
                <%
              }
              if(listSize>0) {
                for(int i= 0; i< listSize && i < (thisOffset-1)*objectPerPage; i++) {
                  tmapArticles.remove(tmapArticles.lastKey());
                }
                listSize = tmapArticles.size(); 
                for(int i= 0; i< listSize && i < objectPerPage; i++) {
                  Long nextEmbargoDate = (Long) tmapArticles.lastKey();
                  
                  String article = (String) tmapArticles.get(nextEmbargoDate);
                  %>
                  <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="<%= "artikel.number = '" + article + "'" %>"><%
                    String titleClass = "pageheader"; 
                    String readmoreUrl = "info.jsp";
                    %><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
                       readmoreUrl += "?p=" + paginaID + "&article=" + article_number; 
                    %></mm:field
                    ><mm:field name="pagina.titel_fra" jspvar="showDate" vartype="String" write="false"
                       ><%@include file="includes/info/summaryrow.jsp" 
                    %></mm:field
                  ></mm:list>
                  <%
                  tmapArticles.remove(nextEmbargoDate);
                }
              } else { 
                  %>
                  <mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" max="1">
                     Er zijn geen artikelen gevonden, die voldoen aan uw zoek criteria.
                     <mm:import id="pagehasarticles" />
                  </mm:list>
                  <mm:notpresent referid="pagehasarticles">
                    Dit archief bevat geen artikelen.
                  </mm:notpresent><%
               }
               %><%@include file="includes/pageowner.jsp" 
        %></td>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
      </tr>
      </table>
      </div>
      </td><%
      // *************************************** right bar *******************************
      %>
      <td>
         <mm:node number="<%= paginaID %>">
            <mm:field name="bron">
              <mm:compare value="0" inverse="true">
              <%@include file="includes/info/relatedpools.jsp" %>
              </mm:compare>
            </mm:field>
         </mm:node>
         <br/>
         <% 
         if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) { 
          %><%@include file="includes/birthday.jsp" %><%
         } %>
         <%@include file="includes/tickertape.jsp" %>
         <%@include file="includes/itemurls.jsp" %>
      </td>
      <%@include file="includes/footer.jsp" %>
      </cache:cache><%
} 
%>
</mm:cloud>
</mm:content>