<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 


%><td <% if(cssClassName.equals("bibliotheek")) { %>colspan="2"<% } %>><%@include file="includes/pagetitle.jsp" %></td>
<% if(!cssClassName.equals("bibliotheek")) { 
   %><td><% String rightBarTitle = "";
   %><%@include file="includes/rightbartitle.jsp" 
   %></td><%
} %>
</tr>
<tr>
<td class="transperant" <% if(cssClassName.equals("bibliotheek")) { %>colspan="2"<% } %>>
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
    <% 
      if(!postingStr.equals("|action=print")) {
        %><div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString %>&pst=|action=print">print</a></div><%
      } 
      String startnodeId = articleId;
      String articlePath = "artikel";
      String articleOrderby = "";
      if(articleId.equals("")) { 
      startnodeId = pageId;
      articlePath = "pagina,contentrel,artikel";
      articleOrderby = "contentrel.pos";
      }
      %><mm:list nodes="<%= startnodeId %>"  path="<%= articlePath %>" orderby="<%= articleOrderby %>"
         ><%@include file="includes/relatedarticle.jsp" 
      %></mm:list>
      <mm:node number="<%= pageId %>">
         <%@include file="includes/relatedcompetencies.jsp" %>
      </mm:node>
      <%@include file="includes/pageowner.jsp" 
    %></td>
</tr>
</table>
</div>
</td>
<% if(!cssClassName.equals("bibliotheek")) { 
   %><td><%
   // *********************************** right bar *******************************
   %><img src="media/spacer.gif" width="10" height="1"></td><%
} %><%@include file="includes/footer.jsp" 
%></mm:cloud>
