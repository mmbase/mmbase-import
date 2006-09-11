<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
  <%
  RubriekHelper rh = new RubriekHelper(cloud);
  PaginaHelper ph =  new PaginaHelper(cloud);
  String paginaID = rh.getFirstPage("root");
  %>
  <mm:redirect page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" />
</mm:cloud>
