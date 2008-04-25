<mm:field name="number" jspvar="pages_number" vartype="String" write="false"
><mm:field name="title" jspvar="pages_title" vartype="String" write="false"
	><% breadCrumPath = "<a href=\"/index.jsp?portal=" + portalId + "&page=" + pages_number + "\">" + pages_title + "</a>"; 
%></mm:field
></mm:field><%

// check if we can find the portal from this page
%><mm:related path="posrel,portals" max="1" constraints="<%= "portals.number=" + portalId %>"
	><% foundPortal= true;
%></mm:related><%
// not found try if without constraint
if(!foundPortal) { 
	%><mm:related path="posrel,portals" max="1" 
		><% foundPortal= true;
	%></mm:related><%
}
%>
