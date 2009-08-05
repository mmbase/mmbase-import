<%@page   contentType="text/html;charset=utf-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="includes/settings.jsp" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">

<html>
<head>
<title></title>
<link rel="stylesheet" type="text/css" href="css/editors.css">
<script language="JavaScript" src="scripts/launchcenter.js"></script>
</head>
<body bgcolor="#EFEFEF">
<%	session.setAttribute("editor", cloud.getUser().getIdentifier());
	String userconstraint="mmbaseusers.username='"+ cloud.getUser().getIdentifier()+"'";
%>

<table cellpadding=0 cellspacing=0>
<tr><td>Editwizards voor <%= cloud.getUser().getIdentifier() %></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td class="prikbord_text"><a href="<%= websiteRoot %>/index.jsp" target="_blank">Naar de website</a></td></tr>
<%--
<tr><td class="prikbord_text"><a href="#" target="nav" onClick="javascript:launchCenter('<%= websiteRoot %>/dev/index.jsp', 'preview', 600, 800);">Naar de preview</a></td></tr>
<tr><td><a href="stats/stats.jsp" target="wizard">Statistieken</a></td></tr>
--%>
<tr><td><a href="/editors/logout.jsp" target="search">Uitloggen</a></td></tr>
<mm:import id="referrer">/editors/empty.jsp</mm:import>
<mm:list path="mmbaseusers,posrel,groups" constraints=""
	orderby="posrel.pos" directions="UP">
	<tr><td>&nbsp;</td></tr>
	<tr><td class="prikbord_text"><mm:field name="groups.name" /></a>&nbsp;&nbsp;&nbsp;</td></tr>
<mm:field name="groups.number" jspvar="groups_number" vartype="String" write="false">
	<mm:list nodes="<%= groups_number %>" path="groups,posrel,editwizards"
		orderby="posrel.pos" directions="UP">
		<mm:field name="editwizards.url" jspvar="editwizards_url" vartype="String" write="false">
		<% 	editwizards_url = editwizards_url.trim();
			if(editwizards_url.indexOf("list.jsp")==-1) { %>
			<tr><td><a href="<mm:url referids="referrer" page="<%= editwizards_url %>" />" target="wizard"
				><mm:field name="editwizards.title" /></a></td></tr>
		<% } else { // wizard.jsp
			%><mm:node element="editwizards"
				><mm:related path="templates"
					><mm:first><% editwizards_url += "&startnodes="; %></mm:first
					><mm:first inverse="true"><% editwizards_url += ","; %></mm:first
					><mm:field name="templates.number" jspvar="templates_number" vartype="String" write="false"
						><% editwizards_url += templates_number;
					%></mm:field
				></mm:related
			></mm:node>
			<tr><td>
			<a href="<mm:url referids="referrer" page="<%= editwizards_url %>" />" target="wizard"
				><mm:field name="editwizards.title" /></a>&nbsp;&nbsp;&nbsp;
			</td></tr>
		<% } %>
		</mm:field>
	</mm:list>
</mm:field>
</mm:list>
</table>

</body>
</html>
</mm:cloud>

