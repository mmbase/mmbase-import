<% 
if(isPreview) {
   %><a href="<%= request.getContextPath() %>/editors/paginamanagement/pagina_flush.jsp?number=<%=paginaID %>&rnd=<%= "" + (int) (Math.random()*100000) %>" title="Publiceer deze pagina">
         <img src="<%= request.getContextPath() %>/editors/img/colors.gif" border="0" style="margin-left:3px;"></a>
   <%
} 
%>