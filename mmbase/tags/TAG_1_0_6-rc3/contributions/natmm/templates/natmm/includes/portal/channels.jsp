<%@page import="org.mmbase.bridge.*" %>
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
  <div class="headerBar" style="width:396px;">
      <mm:field name="naam" jspvar="name" vartype="String" write="false"><%= name.toUpperCase() %></mm:field>
  </div>
  <table cellspacing="0" style="width:396px;">
  <mm:field name="number" jspvar="channels_number" vartype="String" write="false">
    <%@include file="/editors/mailer/util/memberid_get.jsp" %>
<%
    NodeList nlChannels = PoolUtil.getByPools(cloud,channels_number,"rubriek,parent,rubriek2,posrel","pagina",
                                              ",rubriek2.number","parent.pos,posrel.pos",memberid);
    int maxChannels = nlChannels.size();
    if (maxChannels > 4) {
      maxChannels = 4;
    }
    for(int i=0; i<maxChannels; i++) {
      Node node = nlChannels.getNode(i);
%>
      <mm:node number="<%= node.getStringValue("pagina.number") %>">
      <mm:field name="number" jspvar="pagina_number" vartype="String" write="false">

<%
      if (count%2==0 && count>1) { %><tr><td colspan="2" style="height:3px;"><div class="rule" style="margin:0px;"></div></td></tr><% }

      count++;
      if (count%2==1) { %><tr><% }
%>
      <td style="vertical-align:top;width:50%;">
        <table cellspacing="0">
        <tr>
          <td style="vertical-align:top;">
            <mm:node number="<%= node.getStringValue("rubriek2.number") %>">
              <mm:relatednodes type="images" role="contentrel" max="1">
                <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>">
                  <img src="<mm:image template="s(68)+part(0,0,68,49)" />" alt="<mm:field name="alt_tekst" />" border="0" />
                </a><br/>
              </mm:relatednodes>
            </mm:node>
          </td>
          <td style="vertical-align:top;">
            <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" class="maincolor_link_shorty">
              <span class="colortitle">
                <mm:field name="titel" jspvar="pagina_titel" vartype="String" write="false">
                  <%= pagina_titel.toUpperCase() %>
                </mm:field>
              </span>
            </a>
            <br/>
            <a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>" 
               class="hover"><mm:field name="omschrijving"/></a>
          </td>
        </tr>
        </table>
      </td>
<%
      if (count%2==0) { %></tr><% }
%> 
      </mm:field>
      </mm:node>
<%
    }
    if (count%2==1) { %><td>&nbsp;</td></tr><% }
%> 
  </mm:field>
  </table>
</mm:node>
</mm:cloud>
