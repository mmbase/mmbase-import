<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String rubriekID = request.getParameter("r");
   String styleSheet = request.getParameter("rs");
   String paginaID = request.getParameter("s");
   PaginaHelper ph = new PaginaHelper(cloud);
   int count = 0;
%>
<mm:node number="weblogs">
  <div style="background-color: gray; color:white; padding-left:10px; font-weight:bold; width:100%; height:18px">COLUMNS</div>
  <table>
    <mm:related path="posrel,pagina">
      <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
<%
        String linkToPagina = ph.createPaginaUrl(pagina_number,request.getContextPath());

        if (count%2==0 && count>1) { %><tr><td colspan=2>&nbsp;<div class="rule"></div></td></tr><% }

        count++;
        if (count%2==1) { %><tr><% }
%>
        <td width="50%">
          <table>
          <tr>
            <td>
              <mm:list nodes="<%=pagina_number%>" path="pagina,posrel,images">
                <mm:node element="images"><img src="<mm:image  template="s(48)+part(0,0,48,48)" />" alt="" border="0" /></mm:node>
              </mm:list>
            </td>
            <td>
              <a href="<%= linkToPagina %>" style="text-decoration:none;">
                <mm:field name="pagina.titel_eng" jspvar="pagina_titel_eng" vartype="String" write="false">
                  <span class="colortitle"><%= pagina_titel_eng.toUpperCase() %></span>
                </mm:field>
              </a>
              <br/>
              <a href="<%= linkToPagina %>" style="text-decoration:none;"><mm:field name="pagina.titel" /></a>
            </td>
          </tr>
          </table>
          <a href="<%= linkToPagina %>" style="text-decoration:none;">Naar&nbsp;weblog&nbsp;></a>
        </td>
<%
        if (count%2==0) { %></tr><% }
%> 
      </mm:field>
    </mm:related>
<%
    if (count%2==1) { %><td>&nbsp;</td></tr><% }
%> 
  </table>
</mm:node>
</mm:cloud>
