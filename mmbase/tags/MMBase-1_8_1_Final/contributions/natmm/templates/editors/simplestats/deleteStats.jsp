<%@include file="../../nmintra/includes/templateheader.jsp" %>
<mm:cloud>
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
</mm:cloud>