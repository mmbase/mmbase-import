<%
String titleClass = "pageheader"; 
%><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
   readmoreUrl = "?p=" + paginaID + "&article=" + article_number; 
%></mm:field
><mm:field name="pagina.titel_fra" jspvar="showDate" vartype="String" write="false"
   ><mm:present referid="nodates">
      <% showDate = "-1"; %>
   </mm:present>
   <a class="<mm:write referid="hrefclass" />"
      href="<%= readmoreUrl %><% if(!postingStr.equals("")) { %>&pst=|action=noprint<% } 
   %>"><mm:field name="artikel.titel_zichtbaar"
		   ><mm:compare value="0" inverse="true"
      		><mm:field name="artikel.titel" 
		   /></mm:compare
		></mm:field>
   <%@include file="../includes/poolanddate.jsp" %><% 
   String summary = ""; 
   %><mm:field name="artikel.intro" jspvar="article_introduction" vartype="String" write="false"
       ><mm:isnotempty><%
           summary = article_introduction; 
           summary = HtmlCleaner.cleanText(summary,"<",">");
           int spacePos = summary.indexOf(" ",50); 
           if(spacePos>-1) { 
               summary =summary.substring(0,spacePos) + "&hellip;";
           } 
       %></mm:isnotempty
   ></mm:field
   ><span class="normal"><%= summary   %> >></span></a><br/><br/>
</mm:field>