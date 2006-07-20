<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String rubriekID = request.getParameter("r");
   String styleSheet = request.getParameter("rs");
   String paginaID = request.getParameter("s");
   PaginaHelper ph = new PaginaHelper(cloud);

   String articleConstraint = "(artikel.embargo < '" + (nowSec+quarterOfAnHour) + "') AND "
                              + "(artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
%>
<mm:node number="channels">
  <div style="background-color: #BDBDBD; color:black; padding-left:10px; font-weight:bold; width:100%; height:18px">
    NIEUWSBRIEF
  </div>
  dfsf sfsdf sdf sdf sf shd fgd 
</mm:node>
</mm:cloud>