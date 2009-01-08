<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String subsiteID = request.getParameter("r");
   if(subsiteID==null) { subsiteID = "root"; }
   RubriekHelper rh = new RubriekHelper(cloud);
   PaginaHelper ph =  new PaginaHelper(cloud);
   String paginaID = rh.getFirstPage(subsiteID);

   String redirectURL = ph.createPaginaUrl(paginaID,request.getContextPath());
   response.sendRedirect(redirectURL);
%>  
</mm:cloud>
