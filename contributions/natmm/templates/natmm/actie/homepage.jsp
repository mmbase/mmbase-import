<%@include file="../includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="../includes/top1_params.jsp" %>
<% 
if(rubriekExists&&pageExists) { 
   %>
   <%@include file="../includes/top2_cacheparams.jsp" %>
   <!-- cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
   <mm:import id="subdir" />
   <%@include file="../includes/top4_head.jsp" %>
   <table cellSpacing=0 cellPadding=0 width="100%" align="center" border=0 valign="top">
      <%@include file="top5b_pano.jsp" %>
   </table>
   <table cellSpacing=0 cellPadding=0 width=744 align=center border=0 valign="top">
      <tr>
      <mm:node number="<%= rubriekID %>" notfound="skip">
        <%
          int rubriekNum = 0;
        %>
        <mm:related path="parent,rubriek" orderby="parent.pos" searchdir="destination" max="4">
          <mm:node element="rubriek" jspvar="thisRubriek">
            <%
              rubriekNum++;
              styleSheet = thisRubriek.getStringValue("style");
              for(int s = 0; s< style1.length; s++) {
                if(styleSheet.indexOf(style1[s])>-1) { iRubriekStyle = s; } 
              }
            %>
            <mm:relatednodes type="pagina" path="posrel,pagina" orderby="posrel.pos" max="1">
              <td style="padding-right:5px;padding-left:5px;padding-bottom:10px;vertical-align:top;padding-top:10px">
                <table cellSpacing="0" cellPadding="0" style="vertical-align:top;width:170px;border-color:828282;border-width:1px;border-style:solid">
                  <tr>
                    <td>
                      <mm:relatednodes type="artikel" path="contentrel,artikel" orderby="contentrel.pos"  max="1">
                        <mm:relatednodes type="images" path="posrel,images" orderby="posrel.pos"  max="1">
                          <img src="<mm:image template="s(170)+part(0,0,170,98)" />">
                        </mm:relatednodes>
                      </mm:relatednodes>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:10px 10px 10px 10px">  
                      <div style="font-weight:bold;color:#<%=color1[iRubriekStyle]%>"><mm:field name="titel"/></div>
                      <div style="padding-bottom: 5px;"><b><mm:field name="kortetitel"/></b></div>
                      <mm:field name="omschrijving"/>
                      <mm:relatednodes type="artikel" path="contentrel,artikel" orderby="contentrel.pos" max="1">
                        <% if (rubriekNum == 1) { %>
                             <span style="color:red">></span> 
                        <% } else { %>
                             <span style="color:<%=color2[iRubriekStyle]%>">></span> 
                        <% } %>
                        <span style="font-weight: bold; color:#<%=color1[iRubriekStyle]%>"><mm:field name="titel"/></span
                        ><mm:field name="intro"
                          ><mm:compare value="" inverse="true">: <mm:write/></mm:compare>
                        </mm:field>
                      </mm:relatednodes>
                      <br><a href="index.jsp?p=<mm:field name="number"/>" style="color:#<%=color1[iRubriekStyle]%>">Lees verder</a> <span style="color:#<%=color1[iRubriekStyle]%>">></span>
                    </td>
                  </tr>
                </table>
              </td>
            </mm:relatednodes>
          </mm:node>
        </mm:related>
      </mm:node>
      </tr>
   </table>
   </body>
   <%@include file="../includes/sitestatscript.jsp" %>
   </html>
   <!-- /cache:cache -->
   <% 
} %>
</mm:cloud>
