<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Verwijder statistieken</title>
</head>
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