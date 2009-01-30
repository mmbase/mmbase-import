<% 
String sKeywords = searchResults(keywords); 
if (!sKeywords.equals("")) { // set of educations contains one ore more keywords
	%>
	<table style="width:190px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="bold"><div align="left" class="light">&nbsp;Trefwoord</div></td>
		</tr>
	</table>	
	<%
	if(!keywordId.equals("")) { // a keyword has been selected 
		%>
		<table width="190" height="18" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td  class="light">&nbsp;<mm:node number="<%= keywordId %>" notfound="skipbody"><mm:field name="word" /></mm:node></td>
			</tr>
		</table>
		<% 
	} else {
		%>
		<select name="menu1" style="width:180px;" onChange="MM_jumpMenu('document',this,0)">
			<option value='educations.jsp?p=<%= paginaID %>&termsearch=<%= termSearchId %>&pool=<%= poolId %>&pr=<%= providerId %>&c=<%= competenceId %>'>Selecteer</option>
			<mm:list nodes="<%= sKeywords %>" path="keywords" orderby="keywords.word" directions="UP">
				<option value='<%= searchUrl %>&termsearch=<%= termSearchId %>&k=<mm:field name="keywords.number" />&pool=<%= poolId %>&pr=<%= providerId %>&c=<%= competenceId %>'><mm:field name="keywords.word" /></option>
			</mm:list>
		</select>
		<br>
		<% 
	}
}%>
