<table style="width:190px;margin-top:5px;margin-bottom:3px;" border="0" cellpadding="0" cellspacing="0">
   <tr>
	   <td class="bold"><div align="left" class="light_<%= cssClassName %>">&nbsp;Competentie</div></td>
	</tr>
</table>
<% if(!competenceId.equals("")) { // a competence has been selected %>
	<table width="190" height="18" border="0" cellpadding="0" cellspacing="0"><tr>
    	<td  class="light_<%= cssClassName %>">&nbsp;<mm:node number="<%= competenceId %>" notfound="skipbody"><mm:field name="name"/></mm:node></td></tr>
	</table><% 
} else { %>
	<select name="menu1" class="<%= cssClassName %>" style="width:180px;" onChange="MM_jumpMenu('parent',this,0)">
      <option value='educations.jsp?p=<%= pageId %>&k=<%= keywordId %>&pool=<%= poolId %>&pr=<%= providerId %>'>Selecteer</option>
      <% String sCompetencies = searchResults(competencies); %>
      <mm:list nodes="<%= sCompetencies %>" path="competencies" orderby="competencies.name" directions="UP">
         <option value='<%= localPath %>educations.jsp?p=<%= pageId %>&k=<%= keywordId %>&pool=<%= poolId %>&pr=<%= providerId %>&c=<mm:field name="competencies.number"/>'><mm:field name= "competencies.name" /></option>
      </mm:list>
	</select>
   <br/>							   
<% } %> 
