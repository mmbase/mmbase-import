<script>
 function gotoURL(targ,link){ //v3.0
	eval(targ+".location='"+link+"'");
 }
</script>
<%
PaginaHelper ph = new PaginaHelper(cloud);
%>
<tr>
	<%= getTableCells("VAN","vh",ph.createPaginaUrl("home",request.getContextPath())+thisLanguage) %>
	<%= getTableCells(otherLanguageName.toUpperCase(),otherLanguageName,ph.createPaginaUrl(paginaID,request.getContextPath())+otherLanguage+"&"+queryString) %>
	<%= getTableCells("STATEMENT","doc",ph.createPaginaUrl("statement",request.getContextPath())+thisLanguage) %>
	<%= getTableCells("CV","cv",ph.createPaginaUrl("cv",request.getContextPath())+thisLanguage) %>
	<%= getTableCells("WEBWORK","webwork",ph.createPaginaUrl("webwork",request.getContextPath())+thisLanguage) %>
	<%= getTableCells("@","contact",ph.createPaginaUrl("contact",request.getContextPath())+thisLanguage) %>
</tr>
<tr>
	<%= getTableCells("HAM","vh",ph.createPaginaUrl("home",request.getContextPath())+thisLanguage) %>
  <td colspan="21"></td>
</tr>
<tr style="height:1px;">
  <% for(int i=0; i<24; i++) { %><td style="height:1px;"><img src="media/spacer.gif" alt="" border="" width="32px" height="1px"/></td><% } %>
</tr>
