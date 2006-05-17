<tr>
	<td style="width:48%" class="maincolor" style="<%= (iRubriekLayout!=DEFAULT_LAYOUT ? "background-color: #FFFFFF;" : "" ) %>"><%@include file="/editors/paginamanagement/flushlink.jsp" %></td>
	<td style="width:744px;height:21px:" class="maincolor" style="<%= (iRubriekLayout==SUBSITE2_LAYOUT ? "background-color: #10086B;" : "" ) %>"><img src="media/trans.gif" width="744px" height="21" border="0" alt=""></td>
	<td style="width:48%;text-align:center;" class="maincolor" style="<%= (iRubriekLayout!=DEFAULT_LAYOUT ? "background-color: #FFFFFF;" : "" ) %>"></td>
</tr>
<% 
if(iRubriekLayout==DEFAULT_LAYOUT) { 
%><tr>
	<td style="width:48%;background-color:#000039"></td>
	<td style="width:744px;height:68px"><div style="background-color: #000098;">		
	<table width="744" border="0" cellspacing="0" cellpadding="0" background="media/bgtab_ho.gif">
		<tr>
			<td style="width:523;height:68;vertical-align:bottom;padding-bottom:2px;">
			<% if(breadcrumbs.size()==2){ %>
				<div style="font-size:19px;color:#FFFFFF;margin:0px 0px 5px 0px;">Kijk eens om je heen</div>
			<% } else { %>
				   <% for(int r=breadcrumbs.size()-2; r>=0; r--) { %>
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
				   <% } 
               String pageUrl = "";
               %><mm:node number="<%=paginaID%>">
					   <% if(!natuurgebiedID.equals("-1")){ %>
                     <mm:relatednodes type="paginatemplate" max="1">
								<mm:field name="url" write="false" jspvar="template_url" vartype="String">
                           <% pageUrl = template_url +"?id="+paginaID; %>
								</mm:field>
							</mm:relatednodes>
                  <% } %>
						<mm:field name="titel" write="false" jspvar="titel" vartype="String">
						   <% if(!pageUrl.equals("")) { %>
							   <a href="<%= pageUrl %>" class="klikpad"> &gt; </a>
                        <a href="<%= pageUrl %>" class="klikpad"><%= titel %></a>
                     <% }  else { %>
                        <span style="color:#FFFFFF;"> &gt; <%= titel %></span>
                     <% } %>
						</mm:field>
					</mm:node>
					<% if(!natuurgebiedID.equals("-1")){%>
					   <mm:node number="<%=natuurgebiedID%>">
							<span style="color:#FFFFFF;">&gt; <mm:field name="naam" /></span>
					   </mm:node>
					<% } %>
				<% } %>			
			</td>
			<td width="220" align="right" valign="top"><img src="media/nmlogo.gif" alt="Vereniging Natuurmonumenten" width="209" height="68" hspace="2" vspace="2" border="0">
		</td></tr></table></div>
	</td>
	<td style="width:48%;background-color:#000098;vertical-align:bottom;">
	<%--
      <script language="JavaScript1.1" src="scripts/styleswitcher.js">
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

