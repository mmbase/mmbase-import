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
   int count = 0;
%>
<mm:node number="weblogs">
  <div style="background-color: gray; color:white; padding-left:10px; font-weight:bold; width:100%; height:18px">
    COLUMNS
  </div>
  <mm:related path="posrel,pagina">
  s(48x48!)
    <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
      <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" style="text-decoration:none;">
        <mm:field name="pagina.titel_eng" jspvar="pagina_titel_eng" vartype="String" write="false">
          <%= pagina_titel_eng.toUpperCase() %>
        </mm:field>
     </a>
     <br/>
     <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" style="text-decoration:none;">
        <mm:field name="pagina.titel" />
     </a>
     <br/>
     <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" style="text-decoration:none;">Naar weblog ></a>
    </mm:field>
  </mm:related>
</mm:node>
</mm:cloud>