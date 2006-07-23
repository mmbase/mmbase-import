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
<mm:node number="channels" notfound="skipbody">
  <div class="headerBar" style="width:100%;">
  		<mm:field name="naam" jspvar="name" vartype="String" write="false"><%= name.toUpperCase() %></mm:field>
  </div>
  <table style="width:398">
    <mm:related path="parent,rubriek2,posrel,pagina" fields="pagina.number,pagina.titel"
	 	orderby="parent.pos,posrel.pos" max="4" searchdir="destination">
    <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
<%
      if (count%2==0 && count>1) { %><tr><td colspan=2>&nbsp;<div class="rule"></div></td></tr><% }

      count++;
      if (count%2==1) { %><tr><% }
%>
      <td style="vertical-align:top;width:50%;">
        <table>
        <tr>
          <td style="vertical-align:top;">
            <mm:node element="rubriek2">
              <mm:relatednodes type="images" max="1">
                <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>">
					 	<img src="<mm:image template="s(68)" />" alt="<mm:field name="alt_tekst" />" border="0" />
					</a><br/>
              </mm:relatednodes>
            </mm:node>
          </td>
          <td style="vertical-align:top;">
            <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" class="maincolor_link_shorty">
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
