<%
if(!providerId.equals("")) { // a provider has been selected 
   %>
   <mm:list nodes="<%= providerId %>" path="providers,related,educations"  constraints="<%= providerConstraint %>" max="1">
   <table style="width:190px;margin-top:5px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
      <tr>
   	   <td class="bold"><div align="left" class="light_<%= cssClassName %>">&nbsp;<%= providerTitle %></div></td>
      </tr>
   </table>	
   <table width="190" height="18" border="0" cellpadding="0" cellspacing="0">
      <tr>
      	<td  class="light_<%= cssClassName %>">&nbsp;<mm:node number="<%= providerId %>" notfound="skipbody"><mm:field name="naam" /></mm:node></td>
   	</tr>
   </table>
   </mm:list>
   <% 
} else {
   %>
   <table style="width:190px;margin-top:5px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
      <tr>
   	   <td class="bold"><div align="left" class="light_<%= cssClassName %>">&nbsp;<%= providerTitle %></div></td>
      </tr>
   </table>	
	<select name="menu1" class="<%= cssClassName %>" style="width:180px;" onChange="MM_jumpMenu('parent',this,0)">
      <option value='educations.jsp?p=<%= pageId %>&k=<%= keywordId %>&pool=<%= poolId %>&c=<%= competenceId %>'>Selecteer</option>
      <% String sProviders = searchResults(providers); %>
      <mm:list nodes="<%= sProviders %>" path="providers,related,educations" orderby="providers.naam" directions="UP"
         constraints="<%= providerConstraint %>" fields="providers.number" distinct="true">
         <option value='<%= localPath %>educations.jsp?p=<%= pageId %>&k=<%= keywordId %>&pool=<%= poolId %>&pr=<mm:field name="providers.number" />&c=<%= competenceId %>'><mm:field name="providers.naam" /></option>
      </mm:list>
	</select>
	<br/>
   <% 
} %>
