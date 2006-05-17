<table style="width:190px;margin-top:5px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
   <tr>
	   <td class="bold"><div align="left" class="light_<%= cssClassName %>">&nbsp;Categorie</div></td>
	</tr>
</table>
<%
if(!poolId.equals("")) { // an education pool has been selected
   %>
   <table width="190" height="18" border="0" cellpadding="0" cellspacing="0"><tr>
		<td  class="light_<%= cssClassName %>">&nbsp;<mm:node number="<%= poolId %>" notfound="skipbody"><mm:field name="name" /></mm:node></td></tr>
	</table>
   <% 
} else { 
   %>
	<select name="menu1" class="<%= cssClassName %>" style="width:180px;" onChange="MM_jumpMenu('parent',this,0)">
	  <option value='educations.jsp?p=<%= paginaID %>&k=<%= keywordId %>&pr=<%= providerId %>&c=<%= competenceId %>'>Selecteer</option>
	   <% String sPools = searchResults(educationPools); %>
      <mm:list nodes="<%= sPools %>" path="pools" orderby="pools.name" directions="UP">
         <option value='<%= localPath %>educations.jsp?&p=<%= paginaID %>&k=<%= keywordId %>&pool=<mm:field name="pools.number" />&pr=<%= providerId %>&c=<%= competenceId %>'><mm:field name="pools.name" /></option>
      </mm:list>
	</select>
	<br>
	<% 
} %>
				
				
				
				
	
	

				