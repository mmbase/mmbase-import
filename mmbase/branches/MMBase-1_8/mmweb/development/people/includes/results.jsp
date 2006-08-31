<% if(!(firstnameId.equals("")&&lastnameId.equals("")&&nicknameId.equals("")
	&&organisationId.equals("default")&&groupsId.equals("default"))){
	String personsConstraint = "";
	boolean first = true;
	String personsPath = "persons";
	if(!firstnameId.equals("")){
		if(!first) personsConstraint += " AND ";
		personsConstraint += "UPPER(persons.firstname) LIKE  '%" + firstnameId.toUpperCase().replace('E','_').replace('O','%') + "%'";
		first = false;
	}
	if(!lastnameId.equals("")){
		if(!first) personsConstraint += " AND ";
		personsConstraint += "UPPER(persons.lastname) LIKE  '%" + lastnameId.toUpperCase().replace('E','_').replace('O','%') + "%'";
		first = false;
	}
	if(!nicknameId.equals("")){
		if(!first) personsConstraint += " AND ";
		personsConstraint += "UPPER(persons.nickname) LIKE  '%" + nicknameId.toUpperCase().replace('E','_').replace('O','%') + "%'";
		first = false;
	}
	if(!organisationId.equals("default")) {
		if(!first) personsConstraint += " AND ";
		personsConstraint += "organisation.number = '" + organisationId + "'";
		personsPath = "persons,organisation";
		first = false;
	}
	if(!groupsId.equals("default")&&organisationId.equals("default")) {
		if(!first) personsConstraint += " AND ";
		personsConstraint += "groups.number = '" + groupsId + "'";
		personsPath = "persons,groups";
		first = false;
	}
%><%-- = personsConstraint 
--%><mm:list path="<%= personsPath %>"
	orderby="persons.firstname,persons.lastname" directions="UP,UP" constraints="<%= personsConstraint %>"
	><mm:field name="persons.number" jspvar="persons_number" vartype="String" write="false"
	><%-- 
	when both an organisation and a group is selected, the listing could not contain the total constraint
	so we add the groups constraint now
	--%><%
	boolean memberOfGroup = true;		
	if(!groupsId.equals("default")&&!organisationId.equals("default")) { 
		memberOfGroup = false;
		%><mm:list nodes="<%= persons_number %>" path="persons,groups"
			constraints="<%= "groups.number = '" + groupsId  + "'" %>" max="1"
				><% memberOfGroup = true;
		%></mm:list><%
	}
	if(memberOfGroup) {
		%><mm:field name="persons.firstname" jspvar="persons_firstname" vartype="String" write="false"
		><mm:field name="persons.suffix" jspvar="persons_suffix" vartype="String" write="false"
		><mm:field name="persons.lastname" jspvar="persons_lastname" vartype="String" write="false"
		><mm:first
			><table cellpadding="0" cellspacing="0" align="left" width="100%">
			<tr><th width="100%">Results:</th></tr>
			<mm:import id="resultsfound" 
		/></mm:first
			><tr><td>
			<a href="<mm:url referids="portal,page" />&organisation=<%= organisationId %>&groups=<%= groupsId 
				%>&firstname=<%= firstnameId %>&lastname=<%= lastnameId %>&person=<%= persons_number %>"
				>&nbsp;-&nbsp;<%= persons_firstname %> <%= persons_suffix %> <%= persons_lastname
				%></a></td></tr>
		<mm:last
			></table></div></mm:last
		></mm:field
		></mm:field
		></mm:field><%
	} %></mm:field
></mm:list
><mm:notpresent referid="resultsfound"
	><table cellpadding="0" cellspacing="0" align="center">
		<tr><th>no matching people found</th></tr>
	</table></mm:notpresent><%
} else if(!submitId.equals("")) {
	 %><table cellpadding="0" cellspacing="0" align="center">
		<tr><th>please use one of the search criteria</th></tr>
	</table><%
} %>
