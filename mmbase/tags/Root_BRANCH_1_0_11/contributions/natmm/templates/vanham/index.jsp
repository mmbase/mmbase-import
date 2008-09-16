<%@include file="includes/templateheader.jsp" %>
<mm:cloud jspvar="cloud">
  <%
  PaginaHelper ph = new PaginaHelper(cloud);
  String title = "";
  %>
  <mm:list nodes="<%= paginaID %>" path="pagina,posrel,images">
    <mm:node element="pagina" jspvar="dummy">
      <% title = LocaleUtil.getField(dummy,"omschrijving",language, ""); %>
    </mm:node>
    <mm:node element="images">
        <table style="width:100%;height:100%;">
          <tr><td style="height:23%;"></td></tr>
          <tr><td style="height:33%;width:100%;text-align:center;">
            <a href="<%= ph.createPaginaUrl("statement",request.getContextPath())+thisLanguage %>">
              <img src="<mm:image template="s(600)" />" alt="<%= title %>"  border="0" />
            </a>
          </td></tr>
          <tr><td style="height:43%;"></td></tr>
        </table>
    </mm:node>
  </mm:list>
</mm:cloud>
<%@include file="includes/templatefooter.jsp" %>