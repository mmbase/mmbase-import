<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 


   %><td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
   <td class="transperant" colspan="2">
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
    <% if(!postingStr.equals("|action=print")) {
        %><div align="right" style="letter-spacing:1px;"><a href="javascript:history.go(-1);">terug</a>&nbsp/&nbsp;<a target="_blank" href="ipage.jsp<%= 
                    templateQueryString %>&pst=|action=print">print</a></div><%
    } 
    if(articleId.equals("")) { 
     %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel" orderby="posrel.pos" directions="UP" fields="artikel.number"
         ><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><% 
            articleId = article_number; 
         %></mm:field
      ></mm:list><%
    }
    if(!articleId.equals("")) { 
        %><mm:list nodes="<%= articleId %>" path="artikel"
            ><%@include file="includes/relatednews.jsp" 
        %></mm:list><%
    } 
    %><%@include file="includes/pageowner.jsp" 
    %></td>
</tr>
</table>
</div>
</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
