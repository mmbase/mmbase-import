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
<mm:node number="fun" notfound="skipbody">
  <div class="rightSideBar" style="width:100%;">
    <mm:field name="naam" />
  </div>
  <div style="padding-left:3px;">
    <mm:related path="posrel,pagina">
      <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
        <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>"  class="maincolor_link_shorty">
          Life <span style="color:black;">Line</span>
          <mm:field name="pagina.titel" jspvar="pagina_titel" vartype="String" write="false">
            <%= pagina_titel.toUpperCase() %>
          </mm:field>
       </a>
       <br/>
       <mm:last inverse="true">
         <div style="border-top-width: 1px; border-top-style: solid; border-color: gray; margin: 0px 0px 0px 0px;"></div>
       </mm:last>
      </mm:field>
    </mm:related>
  </div>
</mm:node>
</mm:cloud>