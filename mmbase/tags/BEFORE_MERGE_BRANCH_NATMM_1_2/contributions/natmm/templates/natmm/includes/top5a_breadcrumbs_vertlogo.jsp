<tr>
	<td style="width:48%" class="maincolor" style="<%= (iRubriekLayout!=NatMMConfig.DEFAULT_LAYOUT ? "background-color: #FFFFFF;" : "" ) %>"><%@include file="/editors/paginamanagement/flushlink.jsp" %></td>
	<td style="width:744px;height:21px:" class="maincolor" style="<%= (iRubriekLayout==NatMMConfig.SUBSITE2_LAYOUT ? "background-color: #10086B;" : "" ) %>"><img src="media/trans.gif" width="744px" height="21" border="0" alt=""></td>
	<td style="width:48%;text-align:center;" class="maincolor" style="<%= (iRubriekLayout!=NatMMConfig.DEFAULT_LAYOUT ? "background-color: #FFFFFF;" : "" ) %>"></td>
</tr>
<% 
if(iRubriekLayout==NatMMConfig.DEFAULT_LAYOUT) { 
%><tr>
	<td style="width:48%;background-color:#0076b9"></td>
	<td style="width:744px;height:68px;background-color: #c6d5ec;"><div style="background-color: #c6d5ec;">		
	<table width="744" border="0" cellspacing="0" cellpadding="0" background="media/bgtab_ho_light.gif" style="background-color: #c6d5ec;"> 
		<tr>
			<td style="width:523;height:68;vertical-align:bottom;padding-bottom:6px;">
			<% 
      if(breadcrumbs.size()==2){
        %>
				<span style="font-size:22px;color:#FFFFFF;margin:0px 0px 5px 0px; font-weight: bold;">Natuurmonumenten.</span>
				<span style="font-size:16px;color:#FFFFFF;margin:0px 0px 5px 0px;">Als je van Nederland houdt.</span>
				
        <% 
      } else { 
        for(int r=breadcrumbs.size()-2; r>=0; r--) {
           %>
           <mm:list nodes="<%= (String) breadcrumbs.get(r) %>" path="rubriek,posrel,pagina,gebruikt,paginatemplate" 
             fields="rubriek.naam,pagina.number,paginatemplate.url" orderby="posrel.pos" max="1" directions="UP">
            <mm:field name="paginatemplate.url" write="false" jspvar="template_url" vartype="String">
            <mm:field name="pagina.number" write="false" jspvar="pagina_number" vartype="String">
            <mm:field name="rubriek.naam" write="false" jspvar="rubriek_naam" vartype="String">
                <% if(r!=breadcrumbs.size()-2) { %><a class="klikpad">&gt;</a><% } 
                %> <a href="<%=template_url +"?id="+pagina_number %>" class="klikpad"><% 
                   if(r==breadcrumbs.size()-2) { 
                      %>Home<% 
                   } else {
                            %><%=  rubriek_naam.substring(0,1).toUpperCase() + rubriek_naam.substring(1).toLowerCase() %><%
                   } %></a>
            </mm:field>
            </mm:field>
            </mm:field>
          </mm:list>
          <%
        } 
        String pUrl = "";
        %>
        <mm:node number="<%=paginaID%>">
          <% 
          if(!natuurgebiedID.equals("-1")){
              %>
              <mm:relatednodes type="paginatemplate" max="1">
                <mm:field name="url" write="false" jspvar="template_url" vartype="String">
                   <% pUrl = template_url +"?id="+paginaID; %>
                </mm:field>
              </mm:relatednodes>
              <% 
          } %>
          <mm:field name="titel" write="false" jspvar="titel" vartype="String">
             <% if(!pUrl.equals("")) { %>
               <a href="<%= pUrl %>" class="klikpad"> &gt; </a>
               <a href="<%= pUrl %>" class="klikpad"><%= titel %></a>
             <% }  else { %>
               <span style="color:#FFFFFF;"> &gt; <%= titel %></span>
             <% } %>
          </mm:field>
        </mm:node>
        <% 
        if(!natuurgebiedID.equals("-1")){%>
				   <mm:node number="<%=natuurgebiedID%>">
							<span style="color:#FFFFFF;">&gt; <mm:field name="naam" /></span>
				   </mm:node>
					<%
         }
      } %>
			</td>
			<td width="220" align="right" valign="top" >
		</td></tr>
		</table></div>
	</td>
	<td style="width:48%;background-color:#c6d5ec;vertical-align:bottom;">
	<%--
      <script type="text/javascript" src="scripts/styleswitcher.js">
      </script>
      <input type="button" value="0" onclick="setActiveStyleSheet('default')">
      <input type="button" value="+" onclick="setActiveStyleSheet('groot')">
      <input type="button" value="++" onclick="setActiveStyleSheet('groter')">
   --%>
   </td>
</tr>
<% 
} 
%>

