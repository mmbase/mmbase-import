<%
if(!printPage) {
  %>
  <div align="right" style="letter-spacing:1px;">
    <nobr>
      <a href="javascript:history.go(-1);">terug</a> /
      <a target="_blank" href="?<%= request.getQueryString() %>&pst=|action=print">print</a>
    </nobr>
  </div>
  <%
}
%>

