<%@page import="nl.leocms.util.tools.SearchUtil" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
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
    %>
    <% expireTime = newsExpireTime; %>
    <cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
    <%
      int objectPerPage = 10;
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
      boolean checkOnPeriod = (fromTime<toTime);
      
      String rightBarTitle = "";
      %><mm:node number="<%= paginaID %>" jspvar="thisPage"><%
         rightBarTitle = "Zoek in " + thisPage.getStringValue("titel");
      %></mm:node
      ><%@include file="includes/info/movetoarchive.jsp" 
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
             
                String articleConstraint = su.articleConstraint(nowSec,quarterOfAnHour); 
                String articlePath = "pagina,contentrel,artikel";
                if(!thisPool.equals("-1")) {
                    articleConstraint += " AND ( pools.number = '" + thisPool + "' )";
                    articlePath += ",posrel,pools";
                }
                if(checkOnPeriod) {
                  articleConstraint += " AND (( artikel.begindatum > '" + fromTime + "') AND (artikel.begindatum < '" + toTime + "'))";
                }
                if(!termSearchId.equals("")) {
                  articleConstraint += " AND (( UPPER(artikel.titel) LIKE '%" + termSearchId.toUpperCase() + "%') OR ( UPPER(artikel.intro) LIKE '%" + termSearchId.toUpperCase() + "%') ";
                  if(!thisPool.equals("-1")) {
                     articleConstraint += " OR ( UPPER(pools.name) LIKE '%" + termSearchId.toUpperCase() + "%' )";
                  }
                  articleConstraint += ")";
                }
                String sTemplateUrl = "info.jsp";
                String extTemplateQueryString = templateQueryString; 
                if(!periodId.equals("")){ extTemplateQueryString += "&d=" + periodId; }
                int listSize = 0; 
                %>
                <%--
                <%= articlePath %><br/>
                <%= articleConstraint %><br/>
                --%>
                <mm:list nodes="<%= paginaID %>" path="<%= articlePath %>" constraints="<%= articleConstraint %>"
				         orderby="artikel.embargo" searchdir="destination"
                  ><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
                ></mm:list>
					 <%@include file="includes/info/offsetlinks.jsp" %><%
                if(listSize>0) {
                   %><mm:list nodes="<%= paginaID %>" path="<%= articlePath %>" orderby="artikel.embargo" searchdir="destination" directions="DOWN" 
                       offset="<%= "" + (thisOffset-1)*10 %>" max="<%= "" + objectPerPage %>" constraints="<%= articleConstraint %>"><%
                       String titleClass = "pageheader"; 
                       String readmoreUrl = "info.jsp";
                       %><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
                           readmoreUrl += "?p=" + paginaID + "&article=" + article_number; 
                       %></mm:field
                       ><mm:field name="pagina.titel_fra" jspvar="showDate" vartype="String" write="false"
                           ><%@include file="includes/info/summaryrow.jsp" 
                       %></mm:field
                     ></mm:list><%
               } else { 
                  %><mm:list nodes="<%= paginaID %>" path="<%= articlePath %>" max="1">
								Er zijn geen artikelen gevonden, die voldoen aan uw selectie criteria.
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
         <%@include file="includes/info/relatedpools.jsp" %>
         <br/>
         <%@include file="includes/tickertape.jsp" %>
         <%@include file="includes/itemurls.jsp" %>
      </td>
      <%@include file="includes/footer.jsp" %>
      </cache:cache><%
} 
%>
</mm:cloud>
