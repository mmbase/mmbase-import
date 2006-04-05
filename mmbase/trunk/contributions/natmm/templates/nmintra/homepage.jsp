<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" %><%
String sTemplateUrl = "homepage.jsp";
if(!articleId.equals("")) { 
    String articleTemplate = "article.jsp" + templateQueryString;

    %><jsp:include page="<%= articleTemplate %>" /><%

} else {  
   int thisOffset = 0;
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
   
   templateTitle = "home"; 
   String archiefId = "archief";
   boolean isArchive = false;
   
   int fromDay = 0; int fromMonth = 0; int fromYear = 0;
   int toDay = 0; int toMonth = 0; int toYear = 0;
   int thisDay = 0; int thisMonth = 0; int thisYear = 0; int startYear = 0;
   long fromTime = 0; long toTime = 0;
   boolean checkOnPeriod = false;
   boolean periodExceedsMonth = true;
   
   boolean hasPools = false;
   %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel,posrel,pools"
		orderby="artikel.embargo" searchdir="destination" max="1"><%
      hasPools = true;
   %></mm:list
   ><%@include file="includes/movetoarchive.jsp" %>
   <td><%@include file="includes/pagetitle.jsp" %></td>
   <td><% 
      String rightBarTitle = "";
      if(hasPools) { 
         rightBarTitle = "Selecteer&nbsp;categorie"; 
      } else {
         %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,shorty"
            ><mm:field name="shorty.titel" jspvar="items_name" vartype="String" write="false"><%
               rightBarTitle = items_name;
            %></mm:field
         ></mm:list><% 
      } 
   %><%@include file="includes/rightbartitle.jsp" %></td>
   </tr>
   <tr>
   <td class="transperant">
   <div class="<%= infopageClass %>">
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr><td style="padding:10px;padding-top:18px;width:100%;"><%
   
   String pageTitle = "";
   
   %><table border="0" cellpadding="0" cellspacing="0" width="100%">
       <tr><td style="padding-bottom:10px;width:100%;">
           <div class="pageheader"><%= pageTitle %></div><br>
           <%
             
                String articleConstraint = "";
                String articlePath = "pagina,contentrel,artikel";
                if(!thisPool.equals("-1")) {
                    articleConstraint = "( pools.number = '" + thisPool + "' )";
                    articlePath += ",posrel,pools";
                }
                String extTemplateQueryString = templateQueryString; 
                int listSize = 0; 
                %><mm:list nodes="<%= pageId %>" path="<%= articlePath %>" constraints="<%= articleConstraint %>"
				orderby="artikel.embargo" searchdir="destination" 
                  ><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
                ></mm:list
                ><%@include file="includes/offsetlinks.jsp" %><%
                if(listSize>0) {
                   %><mm:list nodes="<%= pageId %>" path="<%= articlePath %>" 
                          orderby="artikel.embargo" directions="DOWN" 
                       offset="<%= "" + thisOffset*10 %>" max="10" constraints="<%= articleConstraint %>"><%
                       String titleClass = "pageheader"; 
                       String readmoreUrl = "homepage.jsp";
                       if(isIPage) readmoreUrl = "ipage.jsp";
                       %><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
                           readmoreUrl += "?p=" + pageId + "&article=" + article_number; 
                       %></mm:field
                       ><mm:field name="pagina.titel_fra" jspvar="showExpireDate" vartype="String" write="false"
                           ><%@include file="includes/summaryrow.jsp" 
                       %></mm:field
                     ></mm:list><%
               } else {
                  %>Er zijn geen artikelen gevonden, die voldoen aan uw selectie criteria.<%
               } 
               %><%@include file="includes/offsetlinks.jsp" %>
       </td></tr>
   </table>
   </td></tr>
   </table>
   </div>
   </td><% 
   
   // *************************************** right bar *******************************
   %><td><%@include file="includes/relatedpools.jsp" 
       %><%@include file="includes/itemurls.jsp" 
       %><%@include file="includes/whiteline.jsp" 
       %><%@include file="includes/tickertape.jsp" 
   %></td><%
} 
%><%@include file="includes/footer.jsp" 
%></mm:cloud>