<%
String sCompetencies = searchResults(competencies); 
if (!sCompetencies.equals("")) {  // set of educations contains one ore more competencies
	%>
	<table style="width:190px;margin-top:5px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="bold"><div align="left" class="light">&nbsp;Competentie</div></td>
		</tr>
	</table>
	<% if(!competenceId.equals("")) { // a competence has been selected %>
		<table width="190" height="18" border="0" cellpadding="0" cellspacing="0"><tr>
			<td  class="light">&nbsp;<mm:node number="<%= competenceId %>" notfound="skipbody"><mm:field name="name"/></mm:node></td></tr>
		</table><% 
	} else { 
		%>
		<select name="menu1" style="width:180px;" onChange="MM_jumpMenu('document',this,0)">
			<option value='educations.jsp?p=<%= paginaID %>&termsearch=<%= termSearchId %>&k=<%= keywordId %>&pool=<%= poolId %>&pr=<%= providerId %>'>Selecteer</option>
			<mm:list nodes="<%= sCompetencies %>" path="competencies" orderby="competencies.name" directions="UP">
				<option value='<%= searchUrl %>&termsearch=<%= termSearchId %>&k=<%= keywordId %>&pool=<%= poolId %>&pr=<%= providerId %>&c=<mm:field name="competencies.number"/>'><mm:field name= "competencies.name" /></option>
			</mm:list>
		</select>
		<br/>
		<%
	}
} %>
