<%@include file="/taglibs.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%
if(!articleId.equals("-1")) {

    String articleTemplate = "article.jsp" + templateQueryString;
    %><% response.sendRedirect(articleTemplate); %><%

} else {

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
   
   String archiefId = "archief";
   boolean isArchive = false;
   
   int fromDay = 0; int fromMonth = 0; int fromYear = 0;
   int toDay = 0; int toMonth = 0; int toYear = 0;
   int thisDay = 0; int thisMonth = 0; int thisYear = 0; int startYear = 0;
   long fromTime = 0; long toTime = 0;
   boolean checkOnPeriod = false;
   
   boolean hasPools = false; 
   %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel,posrel,pools"
		orderby="artikel.embargo" searchdir="destination" max="1"><%
      hasPools = true;
   %></mm:list
   ><%@include file="includes/info/movetoarchive.jsp" %>
   <%@include file="includes/header.jsp" 
   %><td><%@include file="includes/pagetitle.jsp" %></td>
   <td><% 
      String rightBarTitle = "";
      if(hasPools) { 
         rightBarTitle = "Selecteer&nbsp;categorie"; 
      } else {
         %><mm:list nodes="<%= paginaID %>" path="pagina,lijstcontentrel,linklijst"
            ><mm:field name="linklijst.naam" jspvar="items_name" vartype="String" write="false"><%
               rightBarTitle = items_name;
            %></mm:field
         ></mm:list><% 
      } 
   %><%@include file="includes/rightbartitle.jsp" %></td>
   </tr>
   <tr>
   <td class="transperant">
   <div class="<%= infopageClass %>" id="infopage">
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr><td style="padding:10px;padding-top:18px;width:100%;">
		 <table border="0" cellpadding="0" cellspacing="0" width="100%">
       <tr><td style="padding-bottom:10px;width:100%;">
		 	  <%@include file="includes/relatedteaser.jsp" %>
           <%
             
                String articlePath = "pagina,contentrel,artikel";
                String articleConstraint = "(artikel.embargo < " + nowSec + " )";
                if(!thisPool.equals("-1")) {
                    articlePath += ",posrel,pools";
                    articleConstraint += " AND ( pools.number = '" + thisPool + "' )";
                }
					 String sTemplateUrl = "homepage.jsp";
                String extTemplateQueryString = templateQueryString;
                int listSize = 0; 
                %>
					 <mm:list nodes="<%= paginaID %>" path="<%= articlePath %>" constraints="<%= articleConstraint %>"
					 	orderby="artikel.embargo" searchdir="destination" 
                  ><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
                ></mm:list
                ><%@include file="includes/info/offsetlinks.jsp" %><%
                if(listSize>0) {
                   %><mm:list nodes="<%= paginaID %>" path="<%= articlePath %>" 
                          orderby="artikel.begindatum" directions="DOWN" constraints="<%= articleConstraint %>"
								  offset="<%= "" + (thisOffset-1)*10 %>" max="10"><%
                       String titleClass = "pageheader"; 
                       String readmoreUrl = "homepage.jsp";
                       if(isIPage) readmoreUrl = "ipage.jsp";
                       %><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
                           readmoreUrl += "?p=" + paginaID + "&article=" + article_number; 
                       %></mm:field
                       ><mm:field name="pagina.titel_fra" jspvar="showExpireDate" vartype="String" write="false"
                           ><%@include file="includes/info/summaryrow.jsp" 
                       %></mm:field
                     ></mm:list><%
               } else {
                  %>Er zijn geen artikelen gevonden, die voldoen aan uw selectie criteria.<%
               } 
               %>
       </td></tr>
   </table>
   </td></tr>
   </table>
   </div>
   </td><% 
   
   // *************************************** right bar *******************************
   %><td><%@include file="includes/info/relatedpools.jsp" 
       %><%@include file="includes/itemurls.jsp" 
       %><%@include file="includes/whiteline.jsp" 
       %><%@include file="includes/tickertape.jsp" 
   %></td>
	<%@include file="includes/footer.jsp" %>
	<%
} 
%>
</cache:cache>
</mm:cloud>