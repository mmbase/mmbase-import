<%@include file="../../includes/templateheader.jsp" %>
<html>
<head><link rel="stylesheet" type="text/css" href="/css/website.css"></head>
<body>

Deleting stats ...<br>
<mm:listnodes type="mmevents">
		<mm:field name="number" jspvar="mmevents_number" vartype="String" write="false">
				<mm:deletenode number="<%= mmevents_number %>" deleterelations="true" />
		</mm:field>
</mm:listnodes>
ready.<br>

</body>
<%@include file="../../includes/templatefooter.jsp" %>
