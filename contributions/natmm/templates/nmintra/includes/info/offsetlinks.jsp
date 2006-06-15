<% 
if (!termSearchId.equals("")){
	extTemplateQueryString += "&termsearch=" + termSearchId;
}
int pagesCount = listSize/objectPerPage;
if (pagesCount*objectPerPage < listSize) { pagesCount++; }
// show navigation to other pages if there are more than objectPerPage articles
if(listSize>objectPerPage) { 
   %> In archief: <%= listSize %> artikelen [<%= pagesCount %> pagina's]<br/>
   <% 
   if (thisOffset == 1) {
      %><<<%
   } else {
      %>
      <a href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=<%= thisOffset-1 %>"><<</a>
      <a href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=1">1</a>
      <%
   }
   if (thisOffset > 3) {
      %>
      &hellip;
      <% 
   }
   if (thisOffset > 2) {
      %>
      <a href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=<%= thisOffset-1 %>"><%= thisOffset-1 %></a>
      <%
   } 
   %>
   [<%= thisOffset %>]
   <%
   if (thisOffset+1 < pagesCount) {
      %>
      <a href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=<%= thisOffset+1 %>"><%= thisOffset+1 %></a>
      <%
   } 
   if (pagesCount - thisOffset > 2) {
      %>
      &hellip;
      <%
   } 
   if (thisOffset == pagesCount) {
      %>>><%
   } else {
      %>
      <a href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=<%= pagesCount %>"><%= pagesCount %></a>
      <a href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=<%= thisOffset+1 %>">>></a>
      <% 
   }
   if (pagesCount > 5) {
      %>
      <form name="myform" action="<%= requestURL %><%= sTemplateUrl  %><%=  extTemplateQueryString  %>" method="post" style="padding:0px;margin:0px;">
        Ga naar pgn: <input name="offset" style="width:23px;height:17px;font-size:12px;">
        <a href="#" onclick="myform.submit(); return false;">Zoek ></a>
      </form>
      <%
   }
	%>
	<br/>
	<%
}
%>