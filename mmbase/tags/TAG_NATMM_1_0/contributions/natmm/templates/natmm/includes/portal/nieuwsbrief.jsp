<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String rubriekID = request.getParameter("r");
   String styleSheet = request.getParameter("rs");
   String paginaID = request.getParameter("s");
   PaginaHelper ph = new PaginaHelper(cloud);
%>
<div class="headerBar" style="width:100%;">NIEUWSBRIEF</div>
<div style="padding-left:3px;">
  <span class="colortitle">NIEUWSBRIEF</span> Periodiek |
  <span class="colortitle">NIEUWSBRIEF</span> Attendering
</div>
</mm:cloud>