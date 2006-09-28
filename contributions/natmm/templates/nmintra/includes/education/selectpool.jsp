<% 
String sPools = searchResults(educationPools);
if (!sPools.equals("")) { // set of educations contains one ore more pools
	%>
	<table style="width:190px;margin-top:5px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="bold"><div align="left" class="light">&nbsp;Categorie</div></td>
		</tr>
	</table>
	<%
	if(!poolId.equals("")) { // an education pool has been selected
		%>
		<table width="190" height="18" border="0" cellpadding="0" cellspacing="0"><tr>
			<td  class="light">&nbsp;<mm:node number="<%= poolId %>" notfound="skipbody"><mm:field name="name" /></mm:node></td></tr>
		</table>
		<% 
	} else {
		%>
		<select name="menu1" style="width:180px;" onChange="MM_jumpMenu('document',this,0)">
		  <option value='educations.jsp?p=<%= paginaID %>&termsearch=<%= termSearchId %>&k=<%= keywordId %>&pr=<%= providerId %>&c=<%= competenceId %>'>Selecteer</option>
			<mm:list nodes="<%= sPools %>" path="pools" orderby="pools.name" directions="UP">
				<option value='<%= searchUrl %>&termsearch=<%= termSearchId %>&k=<%= keywordId %>&pool=<mm:field name="pools.number" />&pr=<%= providerId %>&c=<%= competenceId %>'><mm:field name="pools.name" /></option>
			</mm:list>
		</select>
		<br>
		<% 
	}
} %>
