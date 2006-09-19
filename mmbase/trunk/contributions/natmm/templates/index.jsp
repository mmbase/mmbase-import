<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
  <%
  String subsiteID = request.getParameter("r");
  if(subsiteID==null) { subsiteID = "root"; }
  RubriekHelper rh = new RubriekHelper(cloud);
  PaginaHelper ph =  new PaginaHelper(cloud);
  String paginaID = rh.getFirstPage(subsiteID);
  %>
  <mm:redirect page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" />
</mm:cloud>
