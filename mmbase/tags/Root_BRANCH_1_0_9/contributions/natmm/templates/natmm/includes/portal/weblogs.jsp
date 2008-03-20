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
<mm:node number="weblogs" notfound="skipbody">
  <div class="rightSideBar" style="width:100%;">
    <mm:field name="naam" />
  </div>
  <table>
    <mm:related path="posrel,pagina">
      <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
<%
        String linkToPagina = ph.createPaginaUrl(pagina_number,request.getContextPath());

        if (count%2==0 && count>1) { %><tr><td colspan="2" style="height:3px;"><div class="rule" style="margin:0px;"></div></td></tr><% }

        count++;
        if (count%2==1) { %><tr><% }
%>
        <td width="50%" style="vertical-align:top;">
          <table cellspacing="0">
          <tr>
            <mm:list nodes="<%=pagina_number%>" path="pagina,posrel,images">
              <td style="vertical-align:top;">
                <mm:node element="images"><img src="<mm:image  template="s(48)+part(0,0,48,48)" />" alt="" border="0" /></mm:node>
              </td>
            </mm:list>
            <td style="vertical-align:top;">
                <mm:field name="pagina.titel_eng" jspvar="pagina_titel_eng" vartype="String" write="false">
                  <mm:isnotempty>
                    <a href="<%= linkToPagina %>" class="maincolor_link_shorty">
                      <span class="colortitle"><%= pagina_titel_eng.toUpperCase() %></span>
                    </a>
                  <br/>
                  </mm:isnotempty>
                </mm:field>
                <a href="<%= linkToPagina %>" class="hover"><mm:field name="pagina.titel" /></a>
            </td>
          </tr>
          </table>
          <a href="<%= linkToPagina %>" class="hover">Naar&nbsp;weblog&nbsp;></a>
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
