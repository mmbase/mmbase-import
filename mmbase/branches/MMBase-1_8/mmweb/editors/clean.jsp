<%@page language="java" contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud name="mmbase" logon method="http" rank="basic user" jspvar="cloud">

<%
String actieId = request.getParameter("actie"); 
if(actieId==null){ actieId = ""; }
String nextId = request.getParameter("next"); 
if(nextId==null){ nextId = ""; }
String parId = request.getParameter("paragraph"); 
if(parId==null){ %>
	<mm:list path="paragraph" fields="paragraph.number" orderby="paragraph.number" directions="DOWN" max="1">
		<mm:field name="paragraph.number" jspvar="paragraph_number" vartype="String" write="false">
			<% parId = paragraph_number; %>
		</mm:field>
	</mm:list>
<% } %>



<html>
<head><title>Paragraaf cleaner (ingelogd als <%= cloud.getUser().getIdentifier() %>)</title></head>
<frameset border=0 framespacing=0 frameborder=1 rows=20,500,200>
	<frame name="header" src="header.jsp?paragraph=<%= parId %>&actie=<%= actieId %>&next=<%= nextId %>" scrolling=no border=0 framespacing=0 frameborder=0 marginwidth=0 marginheight=0 noresize>
	<frameset border=0 framespacing=0 frameborder=1 cols=300,300>
		<frame name="org_html" src="parcleaner/original_html.jsp?paragraph=<%= parId %>" border=0 framespacing=0 frameborder=1 marginwidth=0 marginheight=0>
		<frame name="new_html" src="parcleaner/new_html.jsp?paragraph=<%= parId %>" border=0 framespacing=0 frameborder=1 marginwidth=0 marginheight=0>
	</frameset>
	<frameset border=0 framespacing=0 frameborder=1 cols=300,300>
		<frame name="org_text" src="parcleaner/original_text.jsp?paragraph=<%= parId %>" border=0 framespacing=0 frameborder=1 marginwidth=0 marginheight=0>
		<frame name="new_text" src="parcleaner/new_text.jsp?paragraph=<%= parId %>" border=0 framespacing=0 frameborder=1 marginwidth=0 marginheight=0>
	</frameset>
</frameset>
</html>

</mm:cloud>

