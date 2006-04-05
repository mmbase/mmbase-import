<% 
if(debug) { %><br>Trying to find breadcrums for page <%= pageId %> and category <%= categoryId %>: <% } 
String nodeHref = "";
String nodeUrl = "";
sConstraints = ""; %>
<mm:node number="<%= categoryId %>">
	<mm:aliaslist>
		<mm:write jspvar="alias" vartype="String" write="false">
			<% if (!alias.equals("home")) {
				sConstraints = "rubriek2.number = '" + categoryId + "'";
				}%>
		</mm:write>
	</mm:aliaslist>
</mm:node>
<mm:field name="number" jspvar="node_number" vartype="String" write="false"
>
<% // *** normal page *** 
if (!sConstraints.equals("")){ sConstraints += " AND ";}
sConstraints += "pagina.number='" + pageId + "'";%>
<mm:list nodes="<%= websiteId %>" path="rubriek1,parent,rubriek2,posrel,pagina" constraints="<%= sConstraints %>"
   ><%@include file="../includes/relatednodehref.jsp" %><%
   if(debug) { %>breadcrums for regular page<% } 
%></mm:list><%

// *** subpage *** 
if(nodeHref.equals("")) {
sConstraints += " AND rubriek1.number != rubriek3.number";
%><mm:list nodes="<%= websiteId %>" path="rubriek1,parent1,rubriek2,parent2,rubriek3,posrel,pagina" constraints="<%= sConstraints %>"
       ><%@include file="../includes/relatednodehref.jsp" %><% 
       if(debug) { %>breadcrums for subpage<% } 
    %></mm:list><% 
} %>

<%// *** homepage (nothing there) ***
if(false&&!nodeHref.equals("")) {
    %><mm:list nodes="<%= websiteId %>" path="rubriek,posrel,pagina" constraints="<%= "pagina.number='" + pageId + "'" %>"><% 
       if(debug) { %>breadcrums for homepage (nothing there)<% } 
    %></mm:list><%
}

%></mm:field>