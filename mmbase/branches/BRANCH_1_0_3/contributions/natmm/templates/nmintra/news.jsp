<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/header.jsp" %>
<td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
  <td class="transperant" colspan="2">
    <div class="<%= infopageClass %>" id="infopage">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr><td style="padding:10px;padding-top:18px;">
        <%@include file="includes/back_print.jsp" %>
        <%
        if(articleId.equals("-1")) { 
         %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" orderby="contentrel.pos" directions="UP" fields="artikel.number"
             ><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><% 
                articleId = article_number; 
             %></mm:field
          ></mm:list><%
        }
        if(!articleId.equals("-1")) { 
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
</cache:cache>
</mm:cloud>
