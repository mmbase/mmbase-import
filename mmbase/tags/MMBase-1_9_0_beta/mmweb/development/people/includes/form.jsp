<table cellpadding="0" cellspacing="0" class="relationcontainer" align="right">
<form method="POST" name="peoplefinder" action="<mm:url page="/index.jsp" referids="portal,page" />">
	<tr><th>Firstname:</th>
		<td><input type="text"  name="firstname" size="13" value="<%= firstnameId %>">
		&nbsp;and</td></tr>
	<tr><th>Lastname:</th>
		<td><input type="text" name="lastname" size="13" value="<%= lastnameId %>">
		&nbsp;and</td></tr>
	<tr><th>Nickname:</th>
			<td><input type="text" name="nickname" size="13" value="<%= nicknameId %>">
		&nbsp;and</td></tr>
	<tr><td colspan=3><select name="organisation" style="width:176px;">
		<option value="default" <% if(organisationId.equals("default")) { %>SELECTED<% } 
			%>>all organisations
	<mm:list path="organisation" orderby="organisation.name" directions="UP"
		><mm:field name="organisation.number" jspvar="organisation_number" vartype="String" write="false"
		><mm:field name="organisation.name" jspvar="organisation_name" vartype="String" write="false"
		><option value="<%= organisation_number %>" <%	if(organisation_number.equals(organisationId))  { %>SELECTED<% } 
				%>><%= organisation_name 
		%></mm:field
		></mm:field
	></mm:list
	></select> </td></tr>
	<tr><td colspan=3><select name="groups"  style="width:176px;">
		<option value="default" <% if(groupsId.equals("default")) { %>SELECTED<% } 
			%>>all members
	<mm:list path="groups" orderby="groups.name" directions="UP"
		><mm:node element="groups"><mm:related path="related,persons" max="1" 
			><mm:remove referid="foundrelatedperson" /><mm:import id="foundrelatedperson"
		/></mm:related
		><mm:present referid="foundrelatedperson"
			><mm:field name="number" jspvar="groups_number" vartype="String" write="false"
			><mm:field name="name" jspvar="groups_name" vartype="String" write="false"
			><option value="<%= groups_number %>" <% if(groups_number.equals(groupsId))  { %>SELECTED<% } 
				%>><%= groups_name 
			%></mm:field
			></mm:field
		></mm:present
		><mm:remove referid="foundrelatedperson"
		/></mm:node
	></mm:list
	></select> </td></tr>
	<tr><td colspan=3><div align="right"><input type="submit" name="submit" value="Search" style="text-align:center;font-weight:bold;">&nbsp;</div>
	</td></tr>
	<tr><td colspan=3><%@include file="results.jsp" %>
	</td></tr>
</form>
</table>

