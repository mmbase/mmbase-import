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
<mm:node number="channels">
  <div style="background-color: #BDBDBD; color:black; padding-left:10px; font-weight:bold; width:100%; height:18px">CHANNELS</div>
  <table>
    <mm:related path="rubriek,parent,rubriek2,posrel,pagina" orderby="parent.pos, posrel.pos" max="4">
    <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
<%
      count++;
      if (count==1 || count==3) { %><tr><% }
%>
      <td width="50%">
        <table>
        <tr>
          <td>
            <mm:node element="rubriek2">
              <mm:relatednodes type="images" max="1">
                <img src="<mm:image  template="s(68)" />" alt="" border="0" /><br>
              </mm:relatednodes>
            </mm:node>
          </td>
          <td>
            <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" style="text-decoration:none;">
              <span class="colortitle">
                <mm:field name="pagina.titel" jspvar="pagina_titel" vartype="String" write="false">
                  <%= pagina_titel.toUpperCase() %>
                </mm:field>
              </span>
            </a>
            <br/>
            <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" 
               class="hover"><mm:field name="pagina.omschrijving"/></a>
          </td>
        </tr>
        </table>
      </td>
<%
      if (count==2 || count==4) { %></tr><% }
      if (count==2) { %><tr><td colspan=2>&nbsp;<div class="rule"></div></td></tr><% }
%>
    </mm:field>
    </mm:related>
  </table>
</mm:node>
</mm:cloud>